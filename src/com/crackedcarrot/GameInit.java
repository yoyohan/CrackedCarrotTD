
package com.crackedcarrot;

import java.util.concurrent.Semaphore;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.crackedcarrot.HUD.HUDHandler;
import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.fileloader.MapLoader;
import com.crackedcarrot.fileloader.TowerLoader;
import com.crackedcarrot.fileloader.WaveLoader;
import com.crackedcarrot.menu.R;
import com.crackedcarrot.textures.TextureLibraryLoader;

public class GameInit extends Activity {

	public GameLoop    gameLoop;
	private GameLoopGUI gameLoopGui;
    public SurfaceView mGLSurfaceView;
    private HUDHandler  hudHandler;
    
    private Thread     gameLoopThread;
    private MapLoader  mapLoad;
    
    public static Semaphore pauseSemaphore = new Semaphore(1);
    public static boolean pause = false;
    
    
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
    
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
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
        Scaler res= new Scaler(dm.widthPixels, dm.heightPixels);
        
        hudHandler = new HUDHandler(R.drawable.grid4px, res);
        hudHandler.start();
        
        NativeRender nativeRenderer = new NativeRender(this, 
        		mGLSurfaceView,TextureLibraryLoader.loadTextures(R.raw.all_textures,this),
        		hudHandler.getObjectsToRender());

        mGLSurfaceView.setScreenHeight(dm.heightPixels);

        
    	// We need this to communicate with our GUI.
        gameLoopGui = new GameLoopGUI(this, hudHandler);
        
        
        // Fetch information from previous intent. The information will contain the
        // map and difficulty decided by the player.
        Bundle extras  = getIntent().getExtras();
        int levelChoice = 0;
        int difficulty = 0;
        if(extras != null) {
        	levelChoice = extras.getInt("com.crackedcarrot.menu.map");
        	difficulty =  extras.getInt("com.crackedcarrot.menu.difficulty");
        }
        
        	// Are we resuming an old saved game?
        int resume = 0;
        int resumeLevelNumber = 0;
        int resumePlayerDifficulty = 0;
        int resumePlayerHealth = 0;
        int resumePlayerMoney = 0;
        String resumeTowers = null;
        if (levelChoice == 0) {
            // Restore preferences
            SharedPreferences settings = getSharedPreferences("Resume", 0);
            resume                 = settings.getInt("Resume", 0) + 1;
            resumeLevelNumber      = settings.getInt("LevelNumber", 0);
            resumePlayerDifficulty = settings.getInt("PlayerDifficulty", 0);
            resumePlayerHealth     = settings.getInt("PlayerHealth", 0);
            resumePlayerMoney      = settings.getInt("PlayerMoney", 0);
            resumeTowers           = settings.getString("Towers", "");
        }
        // TODO:
        // �r detta n�dv�ndigt?
        //   difficulty = resumePlayerDIfficulty;
        // Det anv�nds i WaveLoadern?
        
        // Create the map requested by the player
        // TODO: resume needs to load the correct map aswell.
        mapLoad = new MapLoader(this,res);
        Map gameMap = null;
        if (levelChoice == 1) 
        	gameMap = mapLoad.readLevel("level1");
        else if (levelChoice == 2)
        	gameMap = mapLoad.readLevel("level2");
        else
        	gameMap = mapLoad.readLevel("level3");

        //Define player specific variables depending on difficulty.
        Player p;
        if (difficulty == 0) {
        	p = new Player(difficulty, 60, 100, 13);
        }
        else if (difficulty == 1) {
        	p = new Player(difficulty, 50, 100, 13);
        }
        else if (difficulty == 2) {
        	p = new Player(difficulty, 40, 100, 13);
        }
        else { // resume.
        	// TODO: set difficulty variable to this as well, so we load the correct
        	//       difficulty level on resume.
        	p = new Player(resumePlayerDifficulty, resumePlayerHealth, resumePlayerMoney, 1);
        }
        
        //Load the creature waves and apply the correct difficulty
        WaveLoader waveLoad = new WaveLoader(this,res);
        Level[] waveList  = waveLoad.readWave("wave1",difficulty);
        
        // Load all available towers and the shots related to the tower
        TowerLoader towerLoad = new TowerLoader(this,res);
        Tower[] tTypes  = towerLoad.readTowers("towers");
        
    	// Sending data to GAMELOOP
        gameLoop = new GameLoop(nativeRenderer,gameMap,waveList,tTypes,p,gameLoopGui,new SoundManager(getBaseContext()));
        
        	// Resuming old game. Prepare GameLoop for this...
        if (resume > 0) {
        	gameLoop.resumeSetLevelNumber(resumeLevelNumber);
        	gameLoop.resumeSetTowers(resumeTowers);
        }
        
        gameLoopThread = new Thread(gameLoop);
        
        mGLSurfaceView.setRenderer(nativeRenderer);        
        
        mGLSurfaceView.setSimulationRuntime(gameLoop);
        
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
      gameLoopGui.getScoreNinjaAdapter().onActivityResult(
          requestCode, resultCode, data);
    }

    
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
    
    protected void onStop() {
    	gameLoop.stopGameLoop();
    	
    	//You also need to stop the trace when you are done!
    	
    	//Debug.stopMethodTracing();
    	super.onStop();
    }
    
    protected void onPause() {
    	super.onPause();
    	Log.d("ONPAUSE NOW", "onPause");
    }
    
    protected void onResume() {
    	super.onResume();
    	Log.d("ONPAUSE NOW", "onPause");
    }

    
    public void saveGame(int i) {
    	
    	// This uses Android's own internal storage-system to save all
    	// currently relevent information to restore the game to the
    	// beginning of the next wave of creatures.
    	// This is probably (read: only meant to work with) best called
    	// in between levels when the NextLevel-dialog is shown.
    	
    	if (i == 1) {
    			// Save everything.
    		SharedPreferences settings = getSharedPreferences("Resume", 0);
    		SharedPreferences.Editor editor = settings.edit();
    		editor.putInt("Resume", settings.getInt("Resume", 0) + 1);
    		editor.putInt("LevelNumber", gameLoop.getLevelNumber());
    		editor.putInt("PlayerDifficulty", gameLoop.getPlayerData().getDifficulty());
    		editor.putInt("PlayerHealth", gameLoop.getPlayerData().getHealth());
    		editor.putInt("PlayerMoney", gameLoop.getPlayerData().getMoney());
    		editor.putString("Towers", gameLoop.resumeGetTowers());
    		editor.commit();
    	} else {
    			// Dont allow resume. Clears the main resume flag!
    		SharedPreferences settings = getSharedPreferences("Resume", 0);
    		SharedPreferences.Editor editor = settings.edit();
    		editor.putInt("Resume", -1);
    		editor.commit();
    	}
    }
    
}