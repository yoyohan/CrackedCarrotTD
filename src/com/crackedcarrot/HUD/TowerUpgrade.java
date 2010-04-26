package com.crackedcarrot.HUD;

import com.crackedcarrot.Coords;
import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;
import com.crackedcarrot.menu.R;

public class TowerUpgrade implements Runnable{
	private UIBackground background;
	private UIButton	 upgradeLeft;
	private UIButton	 upgradeRight;
	private UIButton	 info;
	private UIButton     destroyTower;

	
	public TowerUpgrade(Scaler s){
		background = new UIBackground(s);
		upgradeLeft = new UIButton(s,R.drawable.tower1);
		upgradeRight = new UIButton(s, R.drawable.tower1);
		info = new UIButton(s, R.drawable.bunny_pink_dead);
		destroyTower = new UIButton(s, R.drawable.destroy_tower);
	}
	
	private class UIBackground extends Sprite{
		public UIBackground(Scaler s){
			super(R.drawable.upgrade_ui_background, UI, 0);
			this.x = 0; this.y = 0; this.z = 0;
			Coords co = s.scale(128, 128);
			this.setWidth(co.getX());
	        this.setHeight(co.getY());
	        this.draw = false;
	        
	        this.r = 0.0f;
	        this.g = 0.0f;
	        this.b = 0.0f;
	        this.opacity = 0.0f;
		}
	}
	
	private class UIButton extends Sprite{
		public UIButton(Scaler s, int ResId){
			super(ResId, UI, 1);
			this.x = 0; this.y = 0; this.z = 0;
			Coords co = s.scale(60, 60);
			this.setWidth(co.getX());
	        this.setHeight(co.getY());
	        this.draw = false;
	        
	        this.r = 0.0f;
	        this.g = 0.0f;
	        this.b = 0.0f;
	        this.opacity = 0.0f;
		}
	}
	
	public Sprite[] getUISpritesToRender(){
		Sprite[] rArray = new Sprite[5];
		rArray[0] = background;
		rArray[1] = upgradeLeft;
		rArray[2] = upgradeRight;
		rArray[3] = info;
		rArray[4] = destroyTower;

		return rArray;
	}
	
	public void moveUI(int CenterX, int CenterY){
		background.x = CenterX - background.getWidth() / 2;
		background.y = CenterY - background.getHeight() / 2;
	}
	
	private void showUI(){
		background.draw = true;
		background.opacity = 1.0f;
	}
	
	//@Override
	public void run() {
		showUI();
	}
}
