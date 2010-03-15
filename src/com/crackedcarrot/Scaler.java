package com.crackedcarrot;

import android.util.Log;
/** 
 * Class defining the relation between different screen resolutions
 */
public class Scaler {
	private int res_x;
	private int res_y;
	private final int FX = 480;
	private final int FY = 800;
	private Coords tmpGridSize;
	private Coords tmpPosStatusBar;
	private Coords tmpTowerMenu;
	
	public Scaler(int x, int y) {
		this.res_x = x;
		this.res_y = y;
		tmpGridSize = scale(60,60);
		tmpPosStatusBar = scale(0,740);
		tmpTowerMenu = scale(0,80);
		
	}

	public Coords scale(int currX, int currY) { 
		if (res_x == FX && res_y == FY) {
			return new Coords(currX,currY);
		}
		if (res_x > FX || res_y > FY) {
			Log.e("Scaler", "Resolution not supported");
		}
		
		float rX = 0;
		float rY = 0;
		if (currX != 0)
			rX = (float)currX/(float)FX;
		if (currY != 0)
			rY = (float)currY/(float)FY;
		
		float fRX = res_x * rX;
		float fRY = res_y * rY;

		return new Coords((int)fRX,(int)fRY);
	}
	
	// Returns resolution of the used phone
	public int getScreenResolutionX() {
		return res_x;
	}
	// Returns resolution of the used phone
	public int getScreenResolutionY() {
		return res_y;
	}

	// Returns position in the grid
	public Coords getGridXandY(int x,int y) {
		int tmpX = ((x / tmpGridSize.x));
		int tmpY = (((y-tmpTowerMenu.y) / tmpGridSize.y));
		return new Coords(tmpX,tmpY);
	}

	// Return pixel position from a grid position
	public Coords getPosFromGrid(int gridX,int gridY) {
		int tmpPosX = tmpGridSize.x * gridX;
		int tmpPosY = (tmpGridSize.y * gridY) + tmpTowerMenu.y;
		return new Coords(tmpPosX,tmpPosY);
	}
	
	// Check if the the position is inside the grid
	public boolean insideGrid(int x,int y) {
		if (x > res_x || y > res_y || x < 0 || y < 0)
			return false;
		
		//Are we belove statusbar?
		if (!(y < tmpPosStatusBar.y))
			return false;
	
		//Are we above menu?
		if (!(y > tmpTowerMenu.y))
			return false;
		
		return true;
	}
	
	
}
