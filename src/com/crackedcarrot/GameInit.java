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
        BitmapDrawable backgroundImage = (BitmapDrawable)getResources().getDrawable(R.drawable.background);
        Bitmap backgoundBitmap = backgroundImage.getBitmap();
        background.width = backgoundBitmap.getWidth();
        background.height = backgoundBitmap.getHeight();
        Sprite[] bckgrd = new Sprite[1];
        bckgrd[0] = background;
        
        // Create Levels;// Will probebly be taken from main menu or something
        //////////////////////////////////        
        Scaler res= new Scaler(dm.widthPixels, dm.heightPixels);
        WayPoints w = new WayPoints(7,res);
        int nbrOfLevels = 20;
        Coords recalc;
    	int nrCrLvl = 20; //We will start with 20 creatures on every level
        Creature[] creatureList = new Creature[nrCrLvl]; // Maximum of creatures
    	Level[] LevelList = new Level[nrCrLvl];
        
        for (int i = 0; i < nbrOfLevels; i++) {
        	Creature tmpCr = new Creature(R.drawable.skate1);
        	tmpCr.draw = false;
        	tmpCr.x = (float)w.getFirstWP().x;
            tmpCr.y = (float)w.getFirstWP().y;
            recalc = res.scale(64,64); //Creature size
        	tmpCr.width = recalc.getX();
            tmpCr.height = recalc.getY();
            recalc = res.scale(50,0);
            tmpCr.velocity = recalc.getX();
        	Level lvl = new Level(tmpCr,nrCrLvl);
        	LevelList[i] = lvl;
        }
        
        //This for can probebly be better
        for (int i = 0; i < nrCrLvl; i++) {
        	Creature tmpCr = new Creature(R.drawable.skate1);
        	tmpCr.draw = false;
        	tmpCr.x = (float)w.getFirstWP().x;
            tmpCr.y = (float)w.getFirstWP().y;
            recalc = res.scale(64,64); //Creature size
        	tmpCr.width = recalc.getX();
            tmpCr.height = recalc.getY();
            recalc = res.scale(50,0);
            tmpCr.velocity = recalc.getX();
            creatureList[i] = tmpCr;
        }
        
        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
        Runtime r = Runtime.getRuntime();
        r.gc();
        
        // Sending data to GAME LOOP
        simulationRuntime = new GameLoop();
        simulationRuntime.setCreatures(creatureList);
        simulationRuntime.setLevels(LevelList);
        simulationRuntime.setWP(w);
        //simulationRuntime.setViewSize(dm.widthPixels, dm.heightPixels);
        RenderThread = new Thread(simulationRuntime);
        
        
        ////////////////////////////////////////////
        // Nåt enligt nedan va?
        //nativeRenderer.setSprites(bckgrd, 0);
        //nativeRenderer.setSprites(creatureList,3);
        nativeRenderer.setSprites(creatureList);
        
        
        mGLSurfaceView.setRenderer(nativeRenderer);        
   	
        setContentView(mGLSurfaceView);
        
        RenderThread.start();
    }
}

