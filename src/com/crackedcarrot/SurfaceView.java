package com.crackedcarrot;

import com.crackedcarrot.HUD.HUDHandler;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;

public class SurfaceView extends GLSurfaceView {
	
	public GameLoop gameLoop = null;

		// Towertype to build, set by the GUI.
	public int towerType = 0;
	
		// Not very magic, read the comment below for explanation.
	private int magicValue;
	private boolean longClick = false;
	private boolean buildTower = false;
	private HUDHandler hud;
	
	@Override
	public boolean onTouchEvent(MotionEvent me) {
		int action = me.getAction();
		// we build where the user last touched the screen.
		if (action == MotionEvent.ACTION_UP) {
			int x = (int)me.getX();
			int y = (int)me.getY();
			Log.d("SURFACEVIEW", "UP_EVENT: X " + x + "  Y " + y);
			boolean test = false;
			//Log.d("SURFACEVIEW", "onTouchEvent: X " + me.getX() + "  Y " + me.getY());
			
			// We need to do this because Java and our grid counts backwards.
			// 480 - clickedYValue = the correct Y-value, for example, on a 
			// screen with a 480 Y-resolution.
			if(buildTower){
				test = gameLoop.createTower(new Coords(x, magicValue - y), towerType);
			}
			
			if(hud != null && gameLoop.gridOcupied(x, magicValue - y)){
				
				int[] data = gameLoop.getTowerCoordsAndRange(x, magicValue - y);
				if(data != null){
					hud.showRangeIndicator(data[0], data[1], data[2], data[3], data[4]);
					test = true;
				}
				else{
					Log.d("SURFACEVIEW","Guru Meditation: Cant get towerdata");
				}
			} if (buildTower &&	!test && hud != null) {
				//You are not allowed to place tower here
				hud.blinkRedGrid();
			}
			else Log.d("SURFACEVIEW", "The edge of the map, here be dragons! Or maybe road or a snowman, maybe a bush to =)");
			
			longClick = false;
			return false;
		}
		
		else if(!longClick && action == MotionEvent.ACTION_MOVE && (me.getEventTime() - me.getDownTime()) > 1000){
			Log.d("SURFACEVIEW", "Long touch event: Down: "+ me.getDownTime() + "Current: " + me.getEventTime());
			longClick = true;
			
			//SHOW TOWER UPGRADE!
			hud.showTowerUpgrade((int)me.getX(), magicValue - (int)me.getY());
			
		}
		
		return true;
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
