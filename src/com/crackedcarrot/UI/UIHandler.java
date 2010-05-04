package com.crackedcarrot.UI;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;

public class UIHandler extends Thread{
	
	private Grid g;
	private RangeIndicator range;
	private TowerUpgrade   upgrade;
	
	private Handler mHandler;
	
	public UIHandler(Scaler s){
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
	
	public void showTowerUpgrade(int x, int y){
		upgrade.moveUI(x, y);
		this.mHandler.post(upgrade.getShowRunner());
	}
	
	public void hideTowerUpgrade(){
		upgrade.hideUI();
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

	public boolean upgradeAClicked(int x, int y) {
		return upgrade.onUpgradeA(x, y);
	}

	public boolean upgradeBClicked(int x, int y) {
		return upgrade.onUpgradeB(x, y);
	}

	public boolean destroyClicked(int x, int y) {
		return upgrade.onDestroy(x, y);
	}

}
