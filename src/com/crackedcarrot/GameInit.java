package com.crackedcarrot;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.crackedcarrot.UI.UIHandler;
import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.fileloader.MapLoader;
import com.crackedcarrot.fileloader.TowerLoader;
import com.crackedcarrot.fileloader.WaveLoader;
import com.crackedcarrot.menu.R;
import com.crackedcarrot.multiplayer.MultiplayerService;
import com.crackedcarrot.textures.TextureLibraryLoader;
import com.scoreninja.adapter.ScoreNinjaAdapter;

public class GameInit extends Activity {

	public GameLoop     gameLoop;
    public SurfaceView  mGLSurfaceView;

	private GameLoopGUI gameLoopGui;
    private Thread      gameLoopThread;
    private UIHandler   hudHandler;
    private MapLoader   mapLoader;
    
    public ScoreNinjaAdapter scoreNinjaAdapter;

    
    /*
     *  DONT CHANGE THESE @Override FUNCTIONS UNLESS YOU KNOW WHAT YOU'RE DOING.
     *  
     *  If you want to change how the GUI works in the gameloop you probably
     *  want to edit GameLoopGUI.java instead?
     */
    
    @Override
	protected Dialog onCreateDialog(int id) { return gameLoopGui.onCreateDialog(id); }
    @Override
	protected void onPrepareDialog(int id, Dialog dialog) { gameLoopGui.onPrepareDialog(id, dialog); }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return gameLoopGui.onCreateOptionsMenu(menu); }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) { return gameLoopGui.onPrepareOptionsMenu(menu); }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) { return gameLoopGui.onOptionsItemSelected(item); }
    
    /*
     * This will have to live in GameInit.java for now - havent figured out how to move it correctly yet,
     * there might be problems with the key-input to the application/gameLoop... /Fredrik
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK) {
    		Log.d("GAMEINIT", "onKeyDown KEYCODE_BACK");
    		showDialog(gameLoopGui.DIALOG_QUIT_ID);
    		return true;
       	}
    	return super.onKeyDown(keyCode, event);
    } 
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	Log.d("GAMEINIT", "onCreate");
    	
    	/** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
    	/** Prevent the screen from sleeping while the game is active */
    	getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    	
    	/** Use the xml layout file. The GLSufaceView is declared in this */
    	setContentView(R.layout.gameinit);
    	
    	/** Create objects of GLSurfaceView, NativeRender and the two objects
    	 *  that are used for define the pixel resolution of current display;
    	 *  DisplayMetrics & Scaler */
        mGLSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Scaler scaler = new Scaler(dm.widthPixels, dm.heightPixels);
        
        hudHandler = new UIHandler(scaler);
        hudHandler.start();
        
        NativeRender nativeRenderer = new NativeRender(this, 
        		mGLSurfaceView,TextureLibraryLoader.loadTextures(R.raw.all_textures,this),
        		hudHandler.getOverlayObjectsToRender(), hudHandler.getUIObjectsToRender());

        mGLSurfaceView.setScreenHeight(dm.heightPixels);
        
        
    	// We need this to communicate with our GUI.
        gameLoopGui = new GameLoopGUI(this, hudHandler);
        

        // Fetch information from previous intent. The information will contain the
        // map and difficulty decided by the player.
        Bundle extras  = getIntent().getExtras();
        int mapChoice = 0;
        int difficulty = 0;
        if(extras != null) {
        	mapChoice = extras.getInt("com.crackedcarrot.menu.map");
        	difficulty =  extras.getInt("com.crackedcarrot.menu.difficulty");
        }

        
        	// Are we resuming an old saved game?
        SharedPreferences resume = getSharedPreferences("resume", 0);
        int               resumes = 0;
        if (mapChoice == 0) {
        		// Increase the resumes-counter, keep people from cheating.
            resumes = resume.getInt("resumes", 0) + 1;
        } else {
        		// We are not resuming anything, clear the old flag(s) and
        		// prepare for a new save. Saves the chosen map directly.
    		SharedPreferences.Editor editor = resume.edit();
    		editor.putInt("map", mapChoice);
    		editor.putInt("resumes", -1);
    		editor.commit();
        }
        
        // Create the map requested by the player

       	// resume needs to load the correct map aswell.
       	if (resumes > 0)
       		mapChoice = resume.getInt("map", 0);
        
        mapLoader = new MapLoader(this, scaler);
        Map gameMap = null;
        if (mapChoice == 1) {
        	gameMap = mapLoader.readLevel("level1");
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzeroone", "E70411F009D4EDFBAD53DB7BE528BFE2");
        } else if (mapChoice == 2) {
        	gameMap = mapLoader.readLevel("level2");
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerotwo", "26CCAFB5B609DEB078F18D52778FA70B");
        } else if (mapChoice == 3) {
        	gameMap = mapLoader.readLevel("level3");
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerothree", "41F4C7AEF5A4DEF7BDC050AEB3EA37FC");
        }
        

        //Define player specific variables depending on difficulty.
        Player p;
        if (difficulty == 0) {
        	p = new Player(difficulty, 60, 100, 10);
        }
        else if (difficulty == 1) {
        	p = new Player(difficulty, 50, 100, 10);
        }
        else if (difficulty == 2) {
        	p = new Player(difficulty, 40, 100, 10);
        }
        else { // resume.
        	p = new Player(resume.getInt("difficulty", 0), resume.getInt("health", 0), resume.getInt("money", 0), 10);
        }
        
        //Load the creature waves and apply the correct difficulty
        WaveLoader waveLoad = new WaveLoader(this, scaler);
        Level[] waveList  = waveLoad.readWave("wave1",difficulty);
        
        // Load all available towers and the shots related to the tower
        TowerLoader towerLoad = new TowerLoader(this, scaler);
        Tower[] tTypes  = towerLoad.readTowers("towers");
        
    	// Sending data to GAMELOOP
        gameLoop = new GameLoop(nativeRenderer,gameMap,waveList,tTypes,p,gameLoopGui,new SoundManager(getBaseContext()));
        
        	// Resuming old game? Prepare GameLoop for this...
        if (resumes > 0) {
        	gameLoop.resume(resumes, resume.getInt("level", 0), resume.getString("towers", null));
        }
        
        gameLoopThread = new Thread(gameLoop);
        
        mGLSurfaceView.setRenderer(nativeRenderer);        
        
        mGLSurfaceView.setSimulationRuntime(gameLoop);
        mGLSurfaceView.setHUDHandler(hudHandler);
        
        //Uncomment this to start cpu profileing (IT KICKS ROYAL ASS!)
        //You also need to uncomment the stopMethodTraceing() further down.
        
        // Start GameLoop
        gameLoopThread.start();
    }
    
    
    	// According to ScoreNinja we need this here, so I left it in:
    // Unfortunate API, but you must notify ScoreNinja onActivityResult.
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      super.onActivityResult(requestCode, resultCode, data);
      scoreNinjaAdapter.onActivityResult(
          requestCode, resultCode, data);
    }

    
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }

    protected void onDestroy() {
    	super.onDestroy();
    	Log.d("GAMEINIT", "onDestroy");
    }
    
    protected void onPause() {
    	super.onPause();
    	Log.d("GAMEINIT", "onPause");
    }
    
    protected void onRestart() {
    	super.onRestart();
    	Log.d("GAMEINIT", "onRestart");
    }
    
    protected void onResume() {
    	super.onResume();
    	Log.d("GAMEINIT", "onResume");
    }
    
    protected void onStart() {
    	super.onStart();
    	Log.d("GAMEINIT", "onStart");
    }
    
    protected void onStop() {
    	super.onStop();
    	gameLoop.stopGameLoop();
    	Log.d("GAMEINIT", "onStop");

    	//You also need to stop the trace when you are done!
    	//Debug.stopMethodTracing();
    }

    
    public void saveGame(int i) {
    	
    	// This uses Android's own internal storage-system to save all
    	// currently relevent information to restore the game to the
    	// beginning of the next wave of creatures.
    	// This is probably (read: only meant to work with) best called
    	// in between levels when the NextLevel-dialog is shown.
    	
    	if (i == 1) {
    			// Save everything.
    		SharedPreferences resume = getSharedPreferences("resume", 0);
    		SharedPreferences.Editor editor = resume.edit();
    		//editor.putInt("map",... <- this is saved above, at the if (mapChoice == 0) check.
    		editor.putInt("difficulty", gameLoop.getPlayerData().getDifficulty());
    		editor.putInt("health", gameLoop.getPlayerData().getHealth());
    		editor.putInt("level", gameLoop.getLevelNumber());
    		editor.putInt("money", gameLoop.getPlayerData().getMoney());
    			// Increase the counter of # of resumes the player has used.
    		editor.putInt("resumes", resume.getInt("resumes", 0) + 1);
    		editor.putString("towers", gameLoop.resumeGetTowers());
    		editor.commit();
    	} else {
    			// Dont allow resume. Clears the main resume flag!
    		SharedPreferences resume = getSharedPreferences("resume", 0);
    		SharedPreferences.Editor editor = resume.edit();
    		editor.putInt("resumes", -1);
    		editor.commit();
    	}
    }
    
    
    /** This is the multiplayer part, will move this to the GameLoopGUI as soon as it works */
    /////////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////
    
    private MultiplayerService mMultiplayerService;
    
    // Message types sent from the MultiplayerService Handler
    public static final int MESSAGE_READ = 1;
    public static final int MESSAGE_WRITE = 2;
    public static final int MESSAGE_DEVICE_NAME = 3;
    public static final int MESSAGE_TOAST = 4;
    
    // The Handler that gets information back from the MultiplayerService
    private final Handler mMultiPlayerHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MESSAGE_WRITE:
                byte[] writeBuf = (byte[]) msg.obj;
                // construct a string from the buffer
                String writeMessage = new String(writeBuf);
                //mConversationArrayAdapter.add("Me:  " + writeMessage);
                break;
            case MESSAGE_READ:
                byte[] readBuf = (byte[]) msg.obj;
                // construct a string from the valid bytes in the buffer
                String readMessage = new String(readBuf, 0, msg.arg1);
                //mConversationArrayAdapter.add(mConnectedDeviceName+":  " + readMessage);
                break;
            case MESSAGE_DEVICE_NAME:
                // save the connected device's name
                //mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                //Toast.makeText(getApplicationContext(), "Connected to "
                //               + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                break;
            case MESSAGE_TOAST:
                Toast.makeText(getApplicationContext(), msg.getData().getString("toast"),
                               Toast.LENGTH_SHORT).show();
                break;
            }
        }
    };
    
}