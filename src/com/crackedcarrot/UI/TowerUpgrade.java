package com.crackedcarrot.UI;

import android.os.SystemClock;

import com.crackedcarrot.Coords;
import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;
import com.crackedcarrot.menu.R;

public class TowerUpgrade{
	private Sprite background;
	private Sprite upgradeLeft;
	private Sprite upgradeRight;
	private Sprite destroyTower;

	private long startTime;
	private long currentTime;
	private long lastUpdateTime;
	
	private ShowUI showRunner;
	private HideUI hideRunner;
	
	public TowerUpgrade(Scaler s){
		background   = new Sprite(R.drawable.upgrade_ui_background, Sprite.UI, 0);
		background.x = 0; background.y = 0; background.z = 0;
		Coords co = s.scale(256, 256);
		background.setWidth(co.getX());
        background.setHeight(co.getY());
        background.draw = false;
        
        background.r = 0.0f;
        background.g = 0.0f;
        background.b = 0.0f;
        background.opacity = 0.0f;
		
		upgradeLeft  = new Sprite(R.drawable.tower1, Sprite.UI, 1);
		upgradeLeft.x = 0; upgradeLeft.y = 0; upgradeLeft.z = 0;
		co = s.scale(60, 60);
		upgradeLeft.setWidth(co.getX());
		upgradeLeft.setHeight(co.getY());
		upgradeLeft.draw = false;
        
		upgradeLeft.r = 1.0f;
		upgradeLeft.g = 1.0f;
		upgradeLeft.b = 1.0f;
		upgradeLeft.opacity = 0.0f;
        
		
		upgradeRight = new Sprite(R.drawable.tower1, Sprite.UI, 1);
		upgradeRight.x = 0; upgradeRight.y = 0; upgradeRight.z = 0;
		co = s.scale(60, 60);
		upgradeRight.setWidth(co.getX());
		upgradeRight.setHeight(co.getY());
		upgradeRight.draw = false;
        
		upgradeRight.r = 1.0f;
		upgradeRight.g = 1.0f;
		upgradeRight.b = 1.0f;
		upgradeRight.opacity = 0.0f;
		
		
		destroyTower = new Sprite(R.drawable.bunny_pink_dead, Sprite.UI, 1);
		destroyTower.x = 0; destroyTower.y = 0; destroyTower.z = 0;
		co = s.scale(60, 60);
		destroyTower.setWidth(co.getX());
		destroyTower.setHeight(co.getY());
		destroyTower.draw = false;
        
		destroyTower.r = 1.0f;
		destroyTower.g = 1.0f;
		destroyTower.b = 1.0f;
		destroyTower.opacity = 0.0f;
				
		showRunner = new ShowUI();
		hideRunner = new HideUI();
		
	}
	
	public ShowUI getShowRunner(){
		return showRunner;
	}
	
	public HideUI getHideRunner(){
		return hideRunner;
	}
	
	private class UIButton extends Sprite{
		public UIButton(Scaler s, int ResId){
			super(ResId, UI, 1);
			this.x = 0; this.y = 0; this.z = 0;
			Coords co = s.scale(60, 60);
			this.setWidth(co.getX());
	        this.setHeight(co.getY());
	        this.draw = false;
	        
	        this.r = 1.0f;
	        this.g = 1.0f;
	        this.b = 1.0f;
	        this.opacity = 0.0f;
		}
	}
	
	public Sprite[] getUISpritesToRender(){
		Sprite[] rArray = new Sprite[4];
		rArray[0] = background;
		rArray[1] = upgradeLeft;
		rArray[2] = upgradeRight;
		rArray[3] = destroyTower;

		return rArray;
	}
	
	public void moveUI(int CenterX, int CenterY){
		background.x = CenterX - background.getWidth() / 2;
		background.y = CenterY - background.getHeight() / 2;
		
		upgradeLeft.x = background.x;
		upgradeLeft.y = background.y + background.getHeight() / 2;
		
		upgradeRight.x = background.x + background.getWidth() / 2;
		upgradeRight.y = background.y + background.getHeight() / 2;
		
		destroyTower.x = background.x + background.getWidth() / 2;
		destroyTower.y = background.y;
		
	}
	
	private class ShowUI implements Runnable{
		//@Override
		public void run() {
			
			if(background.draw == true)
				return;
			
			background.draw = true;
			background.opacity = 0.0f;
			
			upgradeLeft.draw = true;
			upgradeLeft.opacity = 0.0f;
			
			upgradeRight.draw = true;
			upgradeRight.opacity = 0.0f;
					
			destroyTower.draw = true;
			destroyTower.opacity = 0.0f;

			startTime = SystemClock.uptimeMillis();
			currentTime = SystemClock.uptimeMillis();
			lastUpdateTime = currentTime;
			while((currentTime - startTime) < 500){
				if((currentTime - lastUpdateTime) > 50){
					background.opacity += 0.1f;
					upgradeLeft.opacity += 0.1f;
					upgradeRight.opacity += 0.1f;
					destroyTower.opacity += 0.1f;

					lastUpdateTime = currentTime;
				}
				SystemClock.sleep(10);
				currentTime = SystemClock.uptimeMillis();
			}
			
			background.opacity = 1.0f;
			upgradeLeft.opacity = 1.0f;
			upgradeRight.opacity = 1.0f;
			destroyTower.opacity = 1.0f;
		}
	}
	private class HideUI implements Runnable{
		//@Override
		public void run() {
			
			if(background.draw == false)
				return;

			startTime = SystemClock.uptimeMillis();
			currentTime = SystemClock.uptimeMillis();
			lastUpdateTime = currentTime;
			while((currentTime - startTime) < 500){
				if((currentTime - lastUpdateTime) > 50){
					background.opacity -= 0.1f;
					upgradeLeft.opacity -= 0.1f;
					upgradeRight.opacity -= 0.1f;
					destroyTower.opacity -= 0.1f;

					lastUpdateTime = currentTime;
				}
				SystemClock.sleep(10);
				currentTime = SystemClock.uptimeMillis();
			}
			
			background.opacity = 0.0f;
			upgradeLeft.opacity = 0.0f;
			upgradeRight.opacity = 0.0f;
			destroyTower.opacity = 0.0f;
			
			background.draw = false;
			upgradeLeft.draw = false;
			upgradeRight.draw = false;			
			destroyTower.draw = false;

		}
	}

	public boolean upgradeAClicked(int x, int y) {
		
		return x >= upgradeLeft.x && x <= upgradeLeft.x + upgradeLeft.getWidth() &&
			   y >= upgradeLeft.y && y <= upgradeLeft.y + upgradeLeft.getHeight();
	}

	public boolean upgradeBClicked(int x, int y) {
		
		return x >= upgradeRight.x && x <= upgradeRight.x + upgradeRight.getWidth() &&
		       y >= upgradeRight.y && y <= upgradeRight.y + upgradeRight.getHeight();
	}

	public boolean destroyClicked(int x, int y) {
		return x >= destroyTower.x && x <= destroyTower.x + destroyTower.getWidth() &&
		       y >= destroyTower.y && y <= destroyTower.y + destroyTower.getHeight();
	}
}
