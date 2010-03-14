package com.crackedcarrot;

import java.util.Random;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
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
    
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo){
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuItem restart = menu.add(Menu.NONE, Menu.NONE, Menu.NONE, "Restart");
		// Apparently Android doesnt support icons in a Context-menu.
		//restart.setIcon(R.drawable.restart_key_button);

		SubMenu quitMenu = menu.addSubMenu("Quit");
		quitMenu.setHeaderTitle("Save?");
		quitMenu.add(Menu.NONE,Menu.NONE,Menu.NONE, "Yes");
		quitMenu.add(Menu.NONE,Menu.NONE,Menu.NONE, "No");
	}
	
	@Override
	public boolean onContextItemSelected (MenuItem item) {
		String title = (String) item.getTitle();
		
		if (title.matches("Restart")) {
			return true;
			
		} else if (title.matches("Quit")) {
			return true;
			
		} else if (title.matches("Yes")) {
			return true;
			
		} else if (title.matches("No")) {
			return true;
			
		} else {
			// This should never happen.
			Log.d("GameInit", "ContextMenu: " + item.getTitle());
			return super.onContextItemSelected(item);
		}
		
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
        Button inMenu2 = (Button)findViewById(R.id.inmenu2);
        inMenu2.setOnClickListener(new OnClickListener() {
        	
        	public void onClick(View v) {
        		// A tower of type 1 has been chosen, where to put it?
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
        Waypoints gameWaypoints = gameMap.getWaypoints();

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
        Tower[] allTowers  = towerLoad.readTowers("towers");
        
        // When all data needed for the game is loaded we allocate memory for towers and
        // creatures. If we do this right we will hopefully not activate the garbage collector during
        // the game
        int maxNbrCreaturs = 20; // Maximum number of creatures on the map on the same time
        int maxNbrTowers = 40; // Maximum number of towers on the map on the same time
        Creature[] creatureList = new Creature[maxNbrCreaturs];
    	Tower[] towerList = new Tower[maxNbrTowers];
    	Shot[] shotList = new Shot[maxNbrTowers]; //Will always be the same number of shots as the number of towers
        
        //We dont want to send an empty list of creatures to native renderer.
        for (int i = 0; i < creatureList.length; i++) {
        	Creature tmpCr = new Creature(R.drawable.bjoern);
            creatureList[i] = tmpCr;
        }        
        //We dont want to send an empty list of towers or shots  to then native renderer.        
        for (int i = 0; i < maxNbrTowers; i++) {
    		Tower tmpTw = new Tower(R.drawable.skate2);
    		tmpTw.relatedShot = new Shot(R.drawable.skate3,tmpTw);
        	shotList[i] = tmpTw.relatedShot;
        	towerList[i] = tmpTw;
        }

    	// Sending data to GAME LOOP
        simulationRuntime = new GameLoop(nativeRenderer);
        simulationRuntime.setCreatures(creatureList);
        simulationRuntime.setLevels(waveList);
        simulationRuntime.setWP(gameWaypoints);
        simulationRuntime.setTowers(towerList);
        simulationRuntime.setAllTowers(allTowers);
        simulationRuntime.setScaler(res);
        simulationRuntime.setPlayer(p);
        simulationRuntime.setSoundManager(new SoundManager(getBaseContext()));
        simulationRuntime.setTowerGrid(gameMap.getTowerGrid());
        
        RenderThread = new Thread(simulationRuntime);
        
        //Will try to create 50 different towers of type 0        
    	Random rand = new Random();
    	
        for (int i = 0; i < 50; i++) {
        	int randomInt1 = rand.nextInt((res.getScreenResolutionX()));
        	int randomInt2 = rand.nextInt((res.getScreenResolutionY()));
        	Coords tmp = new Coords(randomInt1,randomInt2);//Tower location
        	boolean test = simulationRuntime.createTower(tmp, 0);
        	Log.d("Towercreate status:","" + test);
        }
        
        // Sends an array with sprites to the renderer
        nativeRenderer.setSprites(gameMap.getBackground(), NativeRender.BACKGROUND);
        nativeRenderer.setSprites(creatureList, NativeRender.CREATURE);
        nativeRenderer.setSprites(towerList, NativeRender.TOWER);
        nativeRenderer.setSprites(shotList, NativeRender.SHOT);
                
        // Nåt sånt här skulle jag vilja att renderaren hanterar. Denna lista behöver aldig
        // ritas men vi behöver texturen som ligger i varje "lvl"
        // nativeRenderer.setSprites(waveList, NativeRender.WAVE);
        
        mGLSurfaceView.setRenderer(nativeRenderer);        
        registerForContextMenu(mGLSurfaceView);
        

        // Sends an array with sprites to the renderer

        // Nï¿½t sï¿½nt hï¿½r skulle jag vilja att renderaren hanterar. Denna lista behï¿½ver aldig
        // ritas men vi behï¿½ver texturen som ligger i varje "lvl"
        // nativeRenderer.setSprites(waveList, NativeRender.WAVE);
        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
        Runtime r = Runtime.getRuntime();
        r.gc();
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