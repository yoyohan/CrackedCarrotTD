package com.crackedcarrot.UI;

import android.os.SystemClock;
import android.util.Log;

import com.crackedcarrot.Coords;
import com.crackedcarrot.Scaler;
import com.crackedcarrot.Sprite;
import com.crackedcarrot.menu.R;

public class TowerUpgrade{
	
	private Sprite currentBackground;
	private Sprite upgradeABackground;
	private Sprite upgradeBBackground;
	private Sprite destroyBackground;
	
	private Sprite currentTower;
	private Sprite upgradeA;
	private Sprite upgradeB;
	private Sprite destroyTower;
	
	private long startTime;
	private long currentTime;
	private long lastUpdateTime;
	
	private Scaler s;
	private Coords g;
	
	private ShowUI showRunner;
	private HideUI hideRunner;
	private Coords coords;
	
	public TowerUpgrade(Scaler s){
		this.s = s;

		
		currentTower   = new Sprite(R.drawable.tower1, Sprite.UI, 1);
		currentTower.x = 0; currentTower.y = 0; currentTower.z = 0;		
		Coords co = this.s.scale(60, 60);
		
		currentTower.setWidth(co.getX());
        currentTower.setHeight(co.getY());
        currentTower.draw = false;
        
        currentTower.r = 0.0f;
        currentTower.g = 0.0f;
        currentTower.b = 0.0f;
        currentTower.opacity = 0.0f;
		
		upgradeA  = new Sprite(R.drawable.tower1, Sprite.UI, 1);
		upgradeA.x = 0; upgradeA.y = 0; upgradeA.z = 0;
		co = this.s.scale(60, 60);
		upgradeA.setWidth(co.getX());
		upgradeA.setHeight(co.getY());
		upgradeA.draw = false;
        
		upgradeA.r = 1.0f;
		upgradeA.g = 1.0f;
		upgradeA.b = 1.0f;
		upgradeA.opacity = 0.0f;
        
		
		upgradeB = new Sprite(R.drawable.tower1, Sprite.UI, 1);
		upgradeB.x = 0; upgradeB.y = 0; upgradeB.z = 0;
		co = this.s.scale(60, 60);
		upgradeB.setWidth(co.getX());
		upgradeB.setHeight(co.getY());
		upgradeB.draw = false;
        
		upgradeB.r = 1.0f;
		upgradeB.g = 1.0f;
		upgradeB.b = 1.0f;
		upgradeB.opacity = 0.0f;
		
		
		destroyTower = new Sprite(R.drawable.destroy_tower, Sprite.UI, 1);
		destroyTower.x = 0; destroyTower.y = 0; destroyTower.z = 0;
		co = this.s.scale(60, 60);
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
	
	public Sprite[] getUISpritesToRender(){
		Sprite[] rArray = new Sprite[4];
		rArray[0] = currentTower;
		rArray[1] = upgradeA;
		rArray[2] = upgradeB;
		rArray[3] = destroyTower;

		return rArray;
	}
	
	public void moveUI(int CenterX, int CenterY){
		
		
		//What grid pos are we on ?
		this.g = this.s.getGridXandY(CenterX, CenterY);
		//what are the coordinates of this grid pos ?
		this.coords = s.getPosFromGrid(g.x, g.y); 
		
		currentTower.x = coords.x;
		currentTower.y = coords.y;
		
		//Grid 7x10 is max
		Log.d("TOWER UPGRADE", "GridX: " + g.x + "GridY: " + g.y);
		
		if(g.x == 0){
			
			//Left top corner, use cake-slice-fan layout.
			if(g.y == 10){
				Log.d("TOWER UPGRADE","Top, left corner");
				this.coords = s.getPosFromGrid(g.x+1, g.y);
				this.upgradeA.x = this.coords.x;
				this.upgradeA.y = this.coords.y;
				
				this.coords = s.getPosFromGrid(g.x, g.y-1);
				this.upgradeB.x = this.coords.x;
				this.upgradeB.y = this.coords.y;
				
				this.coords = s.getPosFromGrid(g.x+1, g.y-1);

				this.destroyTower.x = this.coords.x;
				this.destroyTower.y = this.coords.y;
			}
			//Bottom left corner, use cake-slice-fan layout.
			else if(g.y == 0){
				Log.d("TOWER UPGRADE","Bottom, left corner");
				this.coords = s.getPosFromGrid(g.x, g.y+1);
				this.upgradeA.x = this.coords.x;
				this.upgradeA.y = this.coords.y;
				
				this.coords = s.getPosFromGrid(g.x+1, g.y);
				this.upgradeB.x = this.coords.x;
				this.upgradeB.y = this.coords.y;
				
				this.coords = s.getPosFromGrid(g.x+1, g.y+1);

				this.destroyTower.x = this.coords.x;
				this.destroyTower.y = this.coords.y;
				
			}
			//Use wide fan layout.
			else{
				Log.d("TOWER UPGRADE","Center, left side");
				this.coords = s.getPosFromGrid(g.x+1, g.y+1);
				this.upgradeA.x = this.coords.x;
				this.upgradeA.y = this.coords.y;
				
				this.coords = s.getPosFromGrid(g.x+1, g.y-1);
				this.upgradeB.x = this.coords.x;
				this.upgradeB.y = this.coords.y;
				
				this.destroyTower.x = this.coords.x;
				this.destroyTower.y = this.currentTower.y;
			}
			
		}
		//close to right side, use fan layout
		else if(g.x == 7){
			//Right top corner, use cake-slice-fan layout.
			if(g.y == 10){
				Log.d("TOWER UPGRADE","Top, Right corner");
				this.coords = s.getPosFromGrid(g.x, g.y-1);
				this.upgradeA.x = this.coords.x;
				this.upgradeA.y = this.coords.y;
				
				this.coords = s.getPosFromGrid(g.x-1, g.y);
				this.upgradeB.x = this.coords.x;
				this.upgradeB.y = this.coords.y;
				
				this.coords = s.getPosFromGrid(g.x-1, g.y-1);

				this.destroyTower.x = this.coords.x;
				this.destroyTower.y = this.coords.y;
			}
			//Bottom right corner, use cake-slice-fan layout.
			else if(g.y == 0){
				Log.d("TOWER UPGRADE","Bottom, Right courner");
				this.coords = s.getPosFromGrid(g.x-1, g.y);
				this.upgradeA.x = this.coords.x;
				this.upgradeA.y = this.coords.y;
				
				this.coords = s.getPosFromGrid(g.x, g.y+1);
				this.upgradeB.x = this.coords.x;
				this.upgradeB.y = this.coords.y;
				
				this.coords = s.getPosFromGrid(g.x-1, g.y+1);
				this.destroyTower.x = this.coords.x;
				this.destroyTower.y = this.coords.y;
				
			}
			//Use wide fan layout.
			else{
				Log.d("TOWER UPGRADE","Center, Right side");
				this.coords = s.getPosFromGrid(g.x-1, g.y+1);
				this.upgradeA.x = this.coords.x;
				this.upgradeA.y = this.coords.y;
				
				this.coords = s.getPosFromGrid(g.x-1, g.y-1);
				this.upgradeB.x = this.coords.x;
				this.upgradeB.y = this.coords.y;
				
				this.destroyTower.x = this.coords.x;
				this.destroyTower.y = this.currentTower.y;
			}
		}
		
		//Close to top of screen, use fan layout. No need to check corners here.
		else if(g.y == 10){
			Log.d("TOWER UPGRADE", "Center, Top");
			this.coords = s.getPosFromGrid(g.x -1, g.y-1);
			this.upgradeA.x = this.coords.x;
			this.upgradeA.y = this.coords.y;
			
			this.coords = s.getPosFromGrid(g.x +1, g.y-1);
			this.upgradeB.x = this.coords.x;
			this.upgradeB.y = this.coords.y;
			
			this.destroyTower.x = this.currentTower.x;
			this.destroyTower.y = coords.y;
		}
		
		//Close to bottom of screen, use fan layout. No need to check corners here.
		//Or in the middle of the map, use same layout.
		else{
			this.coords = s.getPosFromGrid(g.x -1, g.y+1);
			this.upgradeA.x = this.coords.x;
			this.upgradeA.y = this.coords.y;
			
			this.coords = s.getPosFromGrid(g.x +1, g.y+1);
			this.upgradeB.x = this.coords.x;
			this.upgradeB.y = this.coords.y;
			
			this.destroyTower.x = this.currentTower.x;
			this.destroyTower.y = coords.y;
			Log.d("TOWER UPGRADE","Centre, Centre, Away from the edges of the map");
		}
	}
	
	private class ShowUI implements Runnable{
		//@Override
		public void run() {
			
			if(currentTower.draw == true)
				return;
			
			int moveInc = (int) (currentTower.getWidth() / 10);
			
			currentTower.draw = true;
			currentTower.opacity = 0.0f;
			
			upgradeA.draw = true;
			upgradeA.opacity = 0.0f;
			
			upgradeB.draw = true;
			upgradeB.opacity = 0.0f;
					
			destroyTower.draw = true;
			destroyTower.opacity = 0.0f;

			startTime = SystemClock.uptimeMillis();
			currentTime = SystemClock.uptimeMillis();
			lastUpdateTime = currentTime;
			while((currentTime - startTime) < 500){
				if((currentTime - lastUpdateTime) > 50){
					currentTower.opacity += 0.1f;
					upgradeA.opacity += 0.1f;
					upgradeB.opacity += 0.1f;
					destroyTower.opacity += 0.1f;

					lastUpdateTime = currentTime;
				}
				
				SystemClock.sleep(10);
				currentTime = SystemClock.uptimeMillis();
			}
			
			currentTower.opacity = 1.0f;
			upgradeA.opacity = 1.0f;
			upgradeB.opacity = 1.0f;
			destroyTower.opacity = 1.0f;
		}
	}
	private class HideUI implements Runnable{
		//@Override
		public void run() {
			
			if(currentTower.draw == false)
				return;

			startTime = SystemClock.uptimeMillis();
			currentTime = SystemClock.uptimeMillis();
			lastUpdateTime = currentTime;
			while((currentTime - startTime) < 500){
				if((currentTime - lastUpdateTime) > 50){
					currentTower.opacity -= 0.1f;
					upgradeA.opacity -= 0.1f;
					upgradeB.opacity -= 0.1f;
					destroyTower.opacity -= 0.1f;

					lastUpdateTime = currentTime;
				}
				SystemClock.sleep(10);
				currentTime = SystemClock.uptimeMillis();
			}
			
			currentTower.opacity = 0.0f;
			upgradeA.opacity = 0.0f;
			upgradeB.opacity = 0.0f;
			destroyTower.opacity = 0.0f;
			
			currentTower.draw = false;
			upgradeA.draw = false;
			upgradeB.draw = false;
			destroyTower.draw = false;

		}
	}

	public boolean upgradeAClicked(int x, int y) {
		
		return x >= upgradeA.x && x <= upgradeA.x + upgradeA.getWidth() &&
			   y >= upgradeA.y && y <= upgradeA.y + upgradeA.getHeight();
	}

	public boolean upgradeBClicked(int x, int y) {
		
		return x >= upgradeB.x && x <= upgradeB.x + upgradeB.getWidth() &&
		       y >= upgradeB.y && y <= upgradeB.y + upgradeB.getHeight();
	}

	public boolean destroyClicked(int x, int y) {
		return x >= destroyTower.x && x <= destroyTower.x + destroyTower.getWidth() &&
		       y >= destroyTower.y && y <= destroyTower.y + destroyTower.getHeight();
	}
}
