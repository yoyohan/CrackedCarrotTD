package com.crackedcarrot;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.crackedcarrot.menu.R;

public class GameInit extends Activity {

    public GLSurfaceView mGLSurfaceView;
    private GameLoop simulationRuntime;
    private Thread RenderThread;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {

    	super.onCreate(savedInstanceState);
    	
        mGLSurfaceView = new GLSurfaceView(this);
        NativeRender nativeRenderer = new NativeRender(this);
        
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        
        // Gamemap
        Sprite background = new Sprite(R.drawable.background2);
        BitmapDrawable backgroundImage = (BitmapDrawable)getResources().getDrawable(R.drawable.background2);
        Bitmap backgoundBitmap = backgroundImage.getBitmap();
        background.width = backgoundBitmap.getWidth();
        background.height = backgoundBitmap.getHeight();
        Sprite[] bckgrd = new Sprite[1];
        bckgrd[0] = background;
        
        // Create Levels;// Will probebly be taken from main menu or something
        //////////////////////////////////        
        Scaler res= new Scaler(dm.widthPixels, dm.heightPixels);
        WayPoints w = new WayPoints(8,res);
        int nbrOfLevels = 20;
        Coords recalc;
    	int nrCrLvl = 20; //We will start with 20 creatures on every level
        int maxNbrTowers = 1;
    	Creature[] creatureList = new Creature[nrCrLvl]; // Maximum of creatures
    	Level[] levelList = new Level[nrCrLvl];
    	Tower[] towerList = new Tower[maxNbrTowers];
    	Shot[] shotList = new Shot[maxNbrTowers];
        
        for (int i = 0; i < nbrOfLevels; i++) {
        	Creature tmpCr = new Creature(R.drawable.skate1);
        	tmpCr.draw = false;
        	tmpCr.x = (float)w.getFirstWP().x;
            tmpCr.y = (float)w.getFirstWP().y;
            recalc = res.scale(64,64); //Creature size
        	tmpCr.width = recalc.getX();
            tmpCr.height = recalc.getY();
            tmpCr.health = 40;
            recalc = res.scale(50,0);
            tmpCr.velocity = recalc.getX();
        	Level lvl = new Level(tmpCr,nrCrLvl);
        	levelList[i] = lvl;
        }
        
        //Creature init. This for can probably be better
        for (int i = 0; i < nrCrLvl; i++) {
        	Creature tmpCr = new Creature(R.drawable.skate1);
        	tmpCr.draw = false;
        	tmpCr.x = (float)w.getFirstWP().x;
            tmpCr.y = (float)w.getFirstWP().y;
            recalc = res.scale(64,64); //Creature size
        	tmpCr.width = recalc.getX();
            tmpCr.height = recalc.getY();
            tmpCr.health = 20;
            recalc = res.scale(50,0);
            tmpCr.velocity = recalc.getX();
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
        simulationRuntime.setLevels(levelList);
        simulationRuntime.setWP(w);
        simulationRuntime.setShots(shotList);
        simulationRuntime.setSoundManager(new SoundManager(getBaseContext()));
        RenderThread = new Thread(simulationRuntime);

        // Sends an array with sprites to the renderer
        nativeRenderer.setSprites(bckgrd, NativeRender.BACKGROUND);
        nativeRenderer.setSprites(creatureList, NativeRender.CREATURE);
        nativeRenderer.setSprites(towerList, NativeRender.TOWER);
        nativeRenderer.setSprites(shotList, NativeRender.SHOT);
        nativeRenderer.finalizeSprites();

        mGLSurfaceView.setRenderer(nativeRenderer);        
   	
        setContentView(mGLSurfaceView);
        
        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
        Runtime r = Runtime.getRuntime();
        r.gc();
        RenderThread.start();
    }
    
    protected void onStop() {
    	simulationRuntime.run = false;
    	super.onStop();
    }
}