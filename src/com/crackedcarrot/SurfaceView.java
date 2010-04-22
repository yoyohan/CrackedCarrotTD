package com.crackedcarrot;

import com.crackedcarrot.HUD.HUDHandler;

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
	private boolean buildTower = false;
	private HUDHandler hud;
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		
		// we build where the user last touched the screen.
		if (me.getAction() == MotionEvent.ACTION_DOWN) {
			
			boolean test = false;
			Log.d("SURFACEVIEW", "onTouchEvent: X " + me.getX() + "  Y " + me.getY());
			
			// We need to do this because Java and our grid counts backwards.
			// 480 - clickedYValue = the correct Y-value, for example, on a 
			// screen with a 480 Y-resolution.
			if(buildTower){
				test = gameLoop.createTower(new Coords((int) me.getX(), magicValue - (int) me.getY()), towerType);
				//Log.d("SURFACEVIEW", "Create tower: " + test);
			}
			
			if(hud != null && gameLoop.gridOcupied((int)me.getX(), magicValue - (int) me.getY())){
				
				int[] data = gameLoop.getTowerCoordsAndRange((int)me.getX(), magicValue - (int) me.getY());
				if(data != null){
					hud.showRangeIndicator(data[0], data[1], data[2]);
					test = true;
				}
				else{
					Log.d("SURFACEVIEW","Guru Meditation: Cant get towerdata");
				}
			}
			
			else{
				Log.d("SURFACEVIEW", "The edge of the map, here be dragons!");
			}
			
			return test;
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
		if (i == -1) {
			this.buildTower = false;
		}
		else 
			this.buildTower = true;
		this.towerType = i;
	}

	public void setHUDHandler(HUDHandler hudHandler) {
		hud = hudHandler;
	}

}
