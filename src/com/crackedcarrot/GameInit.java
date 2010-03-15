package com.crackedcarrot;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import android.view.MotionEvent;
import android.view.SubMenu;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;

import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.fileloader.MapLoader;
import com.crackedcarrot.fileloader.TowerLoader;
import com.crackedcarrot.fileloader.WaveLoader;
import com.crackedcarrot.menu.R;

public class GameInit extends Activity {

    public GLSurfaceView mGLSurfaceView;
    private GameLoop simulationRuntime;
    private Thread RenderThread;
    private MapLoader mapLoad;
    private ExpandMenu expandMenu = null;
    
    private int highlightIcon = R.drawable.map_choose;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem restart = menu.add(0, Menu.NONE, 0, "Restart");
        restart.setIcon(R.drawable.restart_key_button);
        MenuItem quit = menu.add(0, Menu.NONE, 0, "Quit");
        quit.setIcon(R.drawable.quit_key_button);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return false;
    }
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
    	/** Ensures that the activity is displayed only in the portrait orientation */
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    	
    	/** Use the xml layout file. The GLSufaceView is declared in this */
    	setContentView(R.layout.gameinit);
    	
    	/** Create objects of GLSurfaceView, NativeRender and the two objects
    	 *  that are used for define the pixel resolution of current display;
    	 *  DisplayMetrics & Scaler */
        mGLSurfaceView = (GLSurfaceView) findViewById(R.id.surface_view);
        NativeRender nativeRenderer = new NativeRender(this, mGLSurfaceView);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Scaler res= new Scaler(dm.widthPixels, dm.heightPixels);
        
        /** Create the expandable menu */
        expandMenu =(ExpandMenu)findViewById(R.id.expand_menu);
        
        /** Listeners for the five icons in the in-game menu.
         *  When clicked on, it's possible to place a tower
         *  on an empty space on the map. The first button
         *  expands the menu. */
        Button inMenu1 = (Button)findViewById(R.id.inmenu1);
        inMenu1.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		expandMenu.switchMenu();
        	}
        });
        /**final OnTouchListener o = new View.OnTouchListener() {
			
			public boolean onTouch(View v1, MotionEvent event){
				v1.setBackgroundResource(R.drawable.inmenu2_button);
				o = null;
				return true;
			}
		}; */
        final Button inMenu2 = (Button)findViewById(R.id.inmenu2);
        inMenu2.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 1 has been chosen, where to put it?
        		/**inMenu2.setBackgroundResource(R.drawable.icon_selected);
        		v.setOnTouchListener(o); */
        	}
        });
        Button inMenu3 = (Button)findViewById(R.id.inmenu3);
        inMenu3.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 2 has been chosen, where to put it?
        	}
        });
        Button inMenu4 = (Button)findViewById(R.id.inmenu4);
        inMenu4.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 3 has been chosen, where to put it?
        	}
        });
        Button inMenu5 = (Button)findViewById(R.id.inmenu5);
        inMenu5.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 4 has been chosen, where to put it?
        	}
        });
        Button inMenu6 = (Button)findViewById(R.id.inmenu6);
        inMenu6.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		expandMenu.switchMenu();
        	}
        });
        
        // Fetch information from previous intent. The information will contain the
        // map and difficulty decided by the player.
        Bundle extras  = getIntent().getExtras();
        int levelChoice = 0;
        int difficulty = 0;
        if(extras != null) {
        	levelChoice = extras.getInt("com.crackedcarrot.menu.levelVal");
        	difficulty = extras.getInt("com.crackedcarrot.menu.dificultVal");
        }        
        
        // Create the map requested by the player
        mapLoad = new MapLoader(this,res);
        Map gameMap = null;
        if (levelChoice == 0) 
        	gameMap = mapLoad.readLevel("level1");
        else if (levelChoice == 1)
        	gameMap = mapLoad.readLevel("level2");
        else
        	gameMap = mapLoad.readLevel("level3");

        //Load the creature waves and apply the correct difficulty
        WaveLoader waveLoad = new WaveLoader(this,res);
        Level[] waveList  = waveLoad.readWave("wave1",difficulty);

        //Define player specific variables depending on difficulty.
        Player p = new Player();
        p.difficulty = difficulty +1;
        if (difficulty == 2)
        	p.health = 40;
        if (difficulty == 1)
        	p.health = 20;
        else
        	p.health = 60;
        p.money      = 100;
        
        // Load all available towers and the shots related to the tower
        TowerLoader towerLoad = new TowerLoader(this,res);
        Tower[] tTypes  = towerLoad.readTowers("towers");
        
    	// Sending data to GAMELOOP
        simulationRuntime = new GameLoop(nativeRenderer,gameMap,waveList,tTypes,p,new SoundManager(getBaseContext()));
        RenderThread = new Thread(simulationRuntime);
        
        mGLSurfaceView.setRenderer(nativeRenderer);        
        registerForContextMenu(mGLSurfaceView);

        // Start GameLoop
        RenderThread.start();
    }
    
    public void onConfigurationChanged(Configuration newConfig) {
    	super.onConfigurationChanged(newConfig);
    }
    
    protected void onStop() {
    	simulationRuntime.run = false;
    	super.onStop();
    }
}