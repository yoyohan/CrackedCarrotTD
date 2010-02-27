package com.crackedcarrot;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;

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
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	
        mGLSurfaceView = new GLSurfaceView(this);
        NativeRender nativeRenderer = new NativeRender(this);
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Scaler res= new Scaler(dm.widthPixels, dm.heightPixels);
        
        // Fetch information from previous intent. The information will contain the
        // map and difficulty decided by the player.
        Bundle extras  = getIntent().getExtras();
        int levelChoice = 0;
        int difficulty = 0;
        if(extras != null) {
        	levelChoice = extras.getInt("com.crackedcarrot.menu.levelVal");
        	difficulty = extras.getInt("com.crackedcarrot.menu.difficulty");
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
        p.difficulty = difficulty;
        if (difficulty == 2)
        	p.health = 40;
        if (difficulty == 3)
        	p.health = 20;
        else
        	p.health = 20;
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
        	Creature tmpCr = new Creature(R.drawable.skate1);
            creatureList[i] = tmpCr;
        }        
        //We dont want to send an empty list of towers or shots  to then native renderer.        
        for (int i = 0; i < maxNbrTowers; i++) {
        	Tower tmpTw = new Tower(R.drawable.skate2);
        	Shot tmpSh = new Shot(R.drawable.skate3,tmpTw);
            shotList[i] = tmpSh;
        	towerList[i] = tmpTw;
        }
        
        // TODO: Only needed until we have real way to place towers on the map
        Coords recalc;
    	Tower tmpTw = allTowers[0];
    	tmpTw.draw = true; //Tower drawable
    	Shot tmpSh = tmpTw.relatedShot;
    	recalc = res.scale(220,300);
    	tmpTw.x = recalc.getX();//Tower location x
        tmpTw.y = recalc.getY();//Tower location y
        tmpTw.resetShotCordinates();//Same location of Shot as midpoint of Tower
        shotList[0] = tmpSh;
    	towerList[0] = tmpTw;

    	// Sending data to GAME LOOP
        simulationRuntime = new GameLoop();
        simulationRuntime.setCreatures(creatureList);
        simulationRuntime.setLevels(waveList);
        simulationRuntime.setWP(gameWaypoints);
        simulationRuntime.setTowers(towerList);
        simulationRuntime.setPlayer(p);
        simulationRuntime.setSoundManager(new SoundManager(getBaseContext()));
        RenderThread = new Thread(simulationRuntime);

        // Sends an array with sprites to the renderer
        nativeRenderer.setSprites(gameMap.getBackground(), NativeRender.BACKGROUND);
        nativeRenderer.setSprites(creatureList, NativeRender.CREATURE);
        nativeRenderer.setSprites(towerList, NativeRender.TOWER);
        nativeRenderer.setSprites(shotList, NativeRender.SHOT);
        // Nåt sånt här skulle jag vilja att renderaren hanterar. Denna lista behöver aldig
        // ritas men vi behöver texturen som ligger i varje "lvl"
        // nativeRenderer.setSprites(waveList, NativeRender.WAVE);
        nativeRenderer.finalizeSprites();
        mGLSurfaceView.setRenderer(nativeRenderer);        

        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
        Runtime r = Runtime.getRuntime();
        r.gc();
        
        setContentView(mGLSurfaceView);
        // Start GameLoop
        RenderThread.start();
    }
    
    protected void onStop() {
    	simulationRuntime.run = false;
    	super.onStop();
    }
}