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
        
        // Create Levels;// Will probebly be catched from main menu or something
        //////////////////////////////////        
        Scaler res= new Scaler(dm.widthPixels, dm.heightPixels);
        WayPoints w = new WayPoints(7,res);
        int nbrOfLevels = 20;
        Coords recalc;
    	int nrCrLvl = 20; //We will start with 20 creatures on every level
    	Level[] LevelList = new Level[20];
    	recalc = res.scale(w.getFirstWP().x,w.getFirstWP().y);
        
        for (int i = 0; i < nbrOfLevels; i++) {
        	Creature tmpCr = new Creature(R.drawable.skate3);
            tmpCr.x = (float)recalc.getX();
            tmpCr.y = (float)recalc.getY();
            recalc = res.scale(64,64); //Creature size
        	tmpCr.width = recalc.getX();
            tmpCr.height = recalc.getY();
            tmpCr.velocity = 50f;
        	Level lvl = new Level(tmpCr,nrCrLvl);
        	LevelList[i] = lvl;
        }
        
      
        
        Sprite[] spriteArray = new Sprite[2];
        spriteArray[0] = background;
        spriteArray[1] = robot;
        
        
        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
        Runtime r = Runtime.getRuntime();
        r.gc();
        
        
        simulationRuntime = new GameLoop();
        RenderThread = new Thread(simulationRuntime);
        
        simulationRuntime.setRenderables(spriteArray);

        
        simulationRuntime.setWP(w);
        //simulationRuntime.setViewSize(dm.widthPixels, dm.heightPixels);

        nativeRenderer.setSprites(spriteArray);
    	
        
        mGLSurfaceView.setRenderer(nativeRenderer);        
    	
        setContentView(mGLSurfaceView);
        
        RenderThread.start();
    }
}

