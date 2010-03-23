package com.crackedcarrot;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class SurfaceView extends GLSurfaceView {
	
	public GameLoop gameLoop = null;
	
	public int towerType = 0;

	@Override
	public boolean onTouchEvent(MotionEvent me) {
		Log.d("SURFACEVIEW", "onTouchEvent: X " + me.getX() + "  Y " + me.getY());
		// we build where the user last touched the screen.
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			Log.d("SURFACEVIEW", "onTouchEvent: X " + me.getX() + "  Y " + me.getY());

			// We need to do this because Java and our grid counts backwards.
			// 480 - clickedYValue = the correct Y-value, for example.
			boolean test = gameLoop.createTower(new Coords((int) me.getX(), 480 - (int) me.getY()), towerType);
		
			Log.d("SURFACEVIEW", "Create tower: " + test);
			return true;
		}
		return false;
	}

	public SurfaceView(Context context) {
		super(context);
	}
	
	public SurfaceView(Context context, AttributeSet attrs){
		super(context, attrs);
	}
	
	public void setSimulationRuntime(GameLoop simulationRuntime) {
		this.gameLoop = simulationRuntime;
	}

}
