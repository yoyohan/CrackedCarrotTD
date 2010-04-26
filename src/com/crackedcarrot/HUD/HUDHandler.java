package com.crackedcarrot.HUD;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;

public class HUDHandler extends Thread{
	
	private Grid g;
	private RangeIndicator range;
	private TowerUpgrade   upgrade;
	
	private Handler mHandler;
	
	public HUDHandler(Scaler s){
		g = new Grid(s);
		range = new RangeIndicator(s);
		upgrade = new TowerUpgrade(s);
	}
	
	public void run(){
		Looper.prepare();
        
		mHandler = new Handler();
        
        Looper.loop();

	}
	
	public void showGrid(){
		Log.d("HUD","Showing grid.");
		this.mHandler.post(g.getShowRunner());
	}
	
	public void hideGrid(){
		Log.d("HUD","Showing grid.");
		this.mHandler.post(g.getHideRunner());

	}

	public void blinkRedGrid(){
		this.mHandler.removeCallbacks(g.getBlinRedRunner());
		this.mHandler.post(g.getBlinRedRunner());
	}
	
	public void showRangeIndicator(int towerX, int towerY, int towerRange, int width, int height){
		this.mHandler.removeCallbacks(range.getShowRunner());
		range.scaleSprite( towerX, towerY, towerRange, width, height);
		this.mHandler.post(range.getShowRunner());
	}
	
	public void hideRangeIndicator(){
		this.mHandler.removeCallbacks(range.getShowRunner());
		this.mHandler.post(range.getShowRunner());
	}
	
	public void showTowerUpgrade(){
		this.mHandler.post(upgrade);
	}
	
	public Sprite[] getOverlayObjectsToRender(){
		Sprite [] rArray = new Sprite[2];
		rArray[0] = g;
		rArray[1] = range;
		return rArray;
	}
	public Sprite[] getUIObjectsToRender(){
		return upgrade.getUISpritesToRender();
	}

}
