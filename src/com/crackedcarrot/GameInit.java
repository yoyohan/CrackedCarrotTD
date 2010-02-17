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
 
        Sprite background = new Sprite(R.drawable.background2);
        BitmapDrawable backgroundImage = (BitmapDrawable)getResources().getDrawable(R.drawable.background);
        Bitmap backgoundBitmap = backgroundImage.getBitmap();
        background.width = backgoundBitmap.getWidth();
        background.height = backgoundBitmap.getHeight();
        
        
        Sprite robot;
        robot = new Sprite(R.drawable.skate3);
        Scaler res= new Scaler(dm.widthPixels, dm.heightPixels);
        Coords recalc = res.scale(64,64);
        robot.width = recalc.getX();
        robot.height = recalc.getY();
        recalc = res.scale(200,400);
        robot.x = (float)recalc.getX();
        robot.y = (float)recalc.getY();
        robot.velocityX = 50f;

        //robot.setGrid(spriteGrid);
        
        // Add this robot to the spriteArray so it gets drawn and to the
        // renderableArray so that it gets moved.
        //spriteArray[x + 1] = robot;
        //renderableArray[x] = robot;
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
        simulationRuntime.setViewSize(dm.widthPixels, dm.heightPixels);

        nativeRenderer.setSprites(spriteArray);
    	mGLSurfaceView.setRenderer(nativeRenderer);        
    	
        setContentView(mGLSurfaceView);
        
        RenderThread.start();
    }
}

