package com.crackedcarrot;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class SurfaceView extends GLSurfaceView {
	
	public GameLoop gameLoop = null;

		// Towertype to build, set by the GUI.
	public int towerType = 0;
	
		// Not very magic, read the comment below for explanation.
	public int magicValue;
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		Log.d("SURFACEVIEW", "onTouchEvent: X " + me.getX() + "  Y " + me.getY());
		// we build where the user last touched the screen.
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			// We need to do this because Java and our grid counts backwards.
			// 480 - clickedYValue = the correct Y-value, for example, on a 
			// screen with a 480 Y-resolution.
			boolean test = gameLoop.createTower(new Coords((int) me.getX(), magicValue - (int) me.getY()), towerType);
		
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

	public void setScreenHeight(int i) {
		this.magicValue = i;
	}
	
	public void setSimulationRuntime(GameLoop simulationRuntime) {
		this.gameLoop = simulationRuntime;
	}
	
	public void setTowerType(int i) {
		this.towerType = i;
	}

}
