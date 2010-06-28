package com.crackedcarrot;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;

import com.crackedcarrot.UI.UIHandler;
import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.fileloader.MapLoader;
import com.crackedcarrot.fileloader.TowerLoader;
import com.crackedcarrot.fileloader.WaveLoader;
import com.crackedcarrot.menu.R;
import com.crackedcarrot.multiplayer.MultiplayerGameLoop;
import com.crackedcarrot.multiplayer.MultiplayerService;
import com.crackedcarrot.textures.TextureLibraryLoader;
import com.scoreninja.adapter.ScoreNinjaAdapter;

public class GameInit extends Activity {

	public GameLoop     gameLoop;
	public MultiplayerGameLoop gLoop = null;
    public SurfaceView  mGLSurfaceView;

	private GameLoopGUI gameLoopGui;
    private Thread      gameLoopThread;
    public  UIHandler   hudHandler;
    private MapLoader   mapLoader;
    private SoundManager soundManager;
    

    ///////////////// Multiplayer ////////////////////////////
    private MultiplayerService mMultiplayerService;
    private static BluetoothSocket multiplayerSocket = null;
   
    /** Makes the Bluetooth socket available from GameInit */
    public static void setMultiplayer(BluetoothSocket socket){
        multiplayerSocket = socket;
    }
    
    /** Used by next level dialog to check if it should be shown */
    public static boolean multiplayerMode(){
    	if(multiplayerSocket != null){
    		return true;
    	} else{
    		return false;
    	}
    }
    //////////////////////////////////////////////////////////

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
       	} else if (keyCode == KeyEvent.KEYCODE_MENU) {
       		Log.d("GAMEINIT", "onKeyDown KEYCODE_MENU");
       		GameLoop.pause();
       		showDialog(gameLoopGui.DIALOG_PAUSE_ID);
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
        
        mGLSurfaceView.setScreenHeight(dm.heightPixels);
        
        
    	// We need this to communicate with our GUI.
        if(multiplayerMode()){
        	gameLoopGui = new GameLoopGUI(this, hudHandler, true);
        } else {
        	gameLoopGui = new GameLoopGUI(this, hudHandler, false);
        }
        
        

        // Fetch information from previous intent. The information will contain the
        // map and difficulty decided by the player.
        Bundle extras  = getIntent().getExtras();
        int mapChoice = 0;
        int difficulty = 0;
        int wave = 0;
        
        if (extras != null) {
        	Log.d("GAMEINIT", "Extras != null, fetching intents...");
        	mapChoice = extras.getInt("com.crackedcarrot.menu.map");
        	difficulty =  extras.getInt("com.crackedcarrot.menu.difficulty");
        	wave =  extras.getInt("com.crackedcarrot.menu.wave");
        } else {
        	Log.d("GAMEINIT", "WTF?! Extras == null, please tell fredrik how you did this?!");
        }
        
        	// Are we resuming an old saved game?
        SharedPreferences resume = getSharedPreferences("resume", 0);
        int               resumes = 0;
        if (mapChoice == 0) {
        		// Increase the resumes-counter, keep people from cheating.
    		SharedPreferences.Editor editor = resume.edit();
    		editor.putInt("resumes", resume.getInt("resumes", 0) + 1);
    		editor.commit();
    		resumes = resume.getInt("resumes", 0);
    		difficulty = -1;		// load saved health/money-values as well.
        } else {
        		// We are not resuming anything, clear the old flag(s) and
        		// prepare for a new save. Saves the chosen map directly.
    		SharedPreferences.Editor editor = resume.edit();
    		editor.putInt("map", mapChoice);
    		editor.putInt("resumes", 0);
    		editor.commit();
        }
        
        
        // Create the map requested by the player

       	// resume needs to load the correct map as well.
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
        	gameMap = mapLoader.readLevel("level4");
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerothree", "41F4C7AEF5A4DEF7BDC050AEB3EA37FC");
        } else if (mapChoice == 4) {
        	gameMap = mapLoader.readLevel("level4");
        	scoreNinjaAdapter = new ScoreNinjaAdapter(this, "mapzerothree", "41F4C7AEF5A4DEF7BDC050AEB3EA37FC");
        }
        
        NativeRender nativeRenderer = new NativeRender(this, 
        		mGLSurfaceView,TextureLibraryLoader.loadTextures(gameMap.getTextureFile(),this),
        		hudHandler.getOverlayObjectsToRender());
        mGLSurfaceView.setRenderer(nativeRenderer);
        
        // We will init soundmanager here insteed
        soundManager = new SoundManager(getBaseContext());
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        
        //Define player specific variables depending on difficulty.
        Player p;
        if (difficulty == 0) {
        	p = new Player(difficulty, 60, 5000, 10);
        }
        else if (difficulty == 1) {
        	p = new Player(difficulty, 50, 50, 10);
        }
        else if (difficulty == 2) {
        	p = new Player(difficulty, 40, 50, 10);
        }
        else { // resume.
        	p = new Player(resume.getInt("difficulty", 0), resume.getInt("health", 0), resume.getInt("money", 0), 10);
        }
        
        //Load the creature waves and apply the correct difficulty
        WaveLoader waveLoad = new WaveLoader(this, scaler);
        Level[] waveList;
        // INcase 1 this game is not a multiplayer game and we will launch the ordinary wavefile
        if (wave == 1) {
            waveList  = waveLoad.readWave("wave1",difficulty);
        }
        else 
        	//Multiplayer game
            waveList  = waveLoad.readWave("wave2",difficulty);

       	// Load all available towers and the shots related to the tower
        TowerLoader towerLoad = new TowerLoader(this, scaler, soundManager);
        Tower[] tTypes  = towerLoad.readTowers("towers");
        
        if(multiplayerSocket != null) {
        	Log.d("GAMEINIT", "Create multiplayerGameLoop");
        	mMultiplayerService = new MultiplayerService(multiplayerSocket, gameLoopGui);
        	mMultiplayerService.start();
        	
        	gLoop = new MultiplayerGameLoop(nativeRenderer,gameMap,waveList,tTypes,p,
    				gameLoopGui,soundManager, mMultiplayerService);
    		gameLoop = gLoop;
    		
    		mMultiplayerService.setGameLoop(gLoop);

    	} else {
    		// Sending data to GAMELOOP
        	Log.d("GAMEINIT", "Create ordinary GameLoop");
            gameLoop = new GameLoop(nativeRenderer,gameMap,waveList,tTypes,p,
            		gameLoopGui,soundManager);
    	}

        	// Resuming old game? Prepare GameLoop for this...
        if (resumes > 0) {
        	gameLoop.resume(resume.getInt("level", 0), resume.getString("towers", null));
        }
        
        gameLoopThread = new Thread(gameLoop);
                
        mGLSurfaceView.setSimulationRuntime(gameLoop);
        mGLSurfaceView.setHUDHandler(hudHandler);
        
        //Uncomment this to start cpu profileing (IT KICKS ROYAL ASS!)
        //You also need to uncomment the stopMethodTraceing() further down.
        //Debug.startMethodTracing();
        
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
    	
    	Log.d("GAMEINIT", "OnPause: (we lost focus!) calling finish() on gameinit to kill everything.");
    	this.finish();
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
    	gameLoop.soundManager.release();
    	if(multiplayerMode()){
    		this.mMultiplayerService.endBluetooth();
    		this.mMultiplayerService = null;
    		Log.d("GAMEINIT", "End Bluetooth");
    	}
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
    		Log.d("GAMEINIT", "Saving game status...");
    			// Save everything.
    		SharedPreferences resume = getSharedPreferences("resume", 0);
    		SharedPreferences.Editor editor = resume.edit();
    		//editor.putInt("map",... <- this is saved above, at the if (mapChoice == 0) check.
    		editor.putInt("difficulty", gameLoop.getPlayerData().getDifficulty());
    		editor.putInt("health", gameLoop.getPlayerData().getHealth());
    		editor.putInt("level", gameLoop.getLevelNumber());
    		editor.putInt("money", gameLoop.getPlayerData().getMoney());
    		editor.putInt("resumes", resume.getInt("resumes", 0));
    		editor.putString("towers", gameLoop.resumeGetTowers());
    		editor.commit();
    		Log.d("GAMEINIT","resumes: " + resume.getInt("resumes", 0));
    	} else {
    		Log.d("GAMEINIT", "Erasing saved game status.");
    			// Dont allow resume. Clears the main resume flag!
    		SharedPreferences resume = getSharedPreferences("resume", 0);
    		SharedPreferences.Editor editor = resume.edit();
    		editor.putInt("resumes", -1);
    		editor.commit();
    		Log.d("GAMEINIT","resumes: " + -1);
    	}
    }
}