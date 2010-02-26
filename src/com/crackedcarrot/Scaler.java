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
	
	
	public Scaler(int x, int y) {
		this.res_x = x;
		this.res_y = y;
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
	public int getX() {
		return res_x;
	}
	public int getY() {
		return res_y;
	}

}
