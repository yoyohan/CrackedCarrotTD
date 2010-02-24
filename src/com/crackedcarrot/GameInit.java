package com.crackedcarrot;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.fileloader.MapLoader;
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
        
        // Create Levels;// Will probebly be taken from main menu or something
        //////////////////////////////////        
        Scaler res= new Scaler(dm.widthPixels, dm.heightPixels);
        mapLoad = new MapLoader(this,res);
        Map m = mapLoad.readLevel("level1");
        Waypoints w = m.getWaypoints();
        
        WaveLoader waveLoad = new WaveLoader(this,res);
        Level[] waveList  = waveLoad.readWave("wave1");
              
        Coords recalc;
    	int nrCrLvl = 20; //We will start with 20 creatures on every level
        int maxNbrTowers = 1;
    	Creature[] creatureList = new Creature[nrCrLvl]; // Maximum of creatures
    	Tower[] towerList = new Tower[maxNbrTowers];
    	Shot[] shotList = new Shot[maxNbrTowers];
        
        //Creature init. We dont want to send an empty list to addSprite(). This can probably be done in a better way
        for (int i = 0; i < nrCrLvl; i++) {
        	Creature tmpCr = new Creature(R.drawable.skate1);
            creatureList[i] = tmpCr;
        }
        
        //Tower init. This for can probably be better
        for (int i = 0; i < maxNbrTowers; i++) {
        	Tower tmpTw = new Tower(R.drawable.skate2);
        	Shot tmpSh = new Shot(R.drawable.skate3, tmpTw);
        	tmpTw.draw = true; //Tower drawable
        	tmpSh.draw = false; //Shot not drawable until launch

        	tmpTw.damage = 10;
        	
            recalc = res.scale(96,96); //Tower size
        	tmpTw.width = recalc.getX(); //Tower width
            tmpTw.height = recalc.getY(); //Tower height

            recalc = res.scale(16,16); //Shot size
        	tmpSh.width = recalc.getX(); //Shot width
            tmpSh.height = recalc.getY(); //Shot height

        	recalc = res.scale(220,300);
        	tmpTw.x = recalc.getX();//Tower location x
            tmpTw.y = recalc.getY();//Tower location y
            tmpSh.resetShotCordinates();//Same location as midpoint of Tower

            recalc = res.scale(200,0);
            tmpSh.velocity = recalc.getX();
            recalc = res.scale(300,0);
            tmpTw.range = recalc.getX();
	
            shotList[i] = tmpSh;
            towerList[i] = tmpTw;
        }
        
        // Sending data to GAME LOOP
        simulationRuntime = new GameLoop();
        simulationRuntime.setCreatures(creatureList);
        simulationRuntime.setLevels(waveList);
        simulationRuntime.setWP(w);
        simulationRuntime.setShots(shotList);
        RenderThread = new Thread(simulationRuntime);

        // Sends an array with sprites to the renderer
        nativeRenderer.setSprites(m.getBackground(), NativeRender.BACKGROUND);
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
        RenderThread.start();
    }
    
    protected void onStop() {
    	simulationRuntime.run = false;
    	super.onStop();
    }
}