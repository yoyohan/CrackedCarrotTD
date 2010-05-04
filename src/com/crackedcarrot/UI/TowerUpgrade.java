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
	private boolean runShow;
	private Coords coords;
	
	public TowerUpgrade(Scaler s){
		this.s = s;		
		Coords co = this.s.scale(120, 120);
		
		currentBackground  = initSprite(R.drawable.upgrade_ui_background, 0, co, 0.5f, 0.5f, 0.5f);
		upgradeABackground = initSprite(R.drawable.upgrade_ui_background, 0, co, 0.5f, 0.5f, 0.5f);
		upgradeBBackground = initSprite(R.drawable.upgrade_ui_background, 0, co, 0.5f, 0.5f, 0.5f);
		destroyBackground  = initSprite(R.drawable.upgrade_ui_background, 0, co, 0.5f, 0.5f, 0.5f);
		
		co = this.s.scale(60, 60);
		currentTower = initSprite(R.drawable.tower1, 1, co, 1.0f, 1.0f, 1.0f);
		upgradeA	 = initSprite(R.drawable.tower1, 1, co, 1.0f, 1.0f, 1.0f);
		upgradeB 	 = initSprite(R.drawable.tower1, 1, co, 1.0f, 1.0f, 1.0f);
		destroyTower = initSprite(R.drawable.destroy_tower, 1, co, 1.0f, 1.0f, 1.0f);
		
		showRunner = new ShowUI();		
	}
	
	private Sprite initSprite(int resource, int subtype, Coords size, float r, float g, float b){
		Sprite rSprite   = new Sprite(resource, Sprite.UI, subtype);
		rSprite.x = 0; rSprite.y = 0; rSprite.z = 0;		
		
		rSprite.setWidth(size.getX());
        rSprite.setHeight(size.getY());
        rSprite.draw = false;
        
        rSprite.r = r;
        rSprite.g = g;
        rSprite.b = b;
        rSprite.opacity = 0.0f;
        
        return rSprite;
	}
	
	public ShowUI getShowRunner(){
		return showRunner;
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
		
		this.currentBackground.x = currentTower.x;
		this.currentBackground.y = currentTower.y;
		this.upgradeABackground.x = upgradeA.x;
		this.upgradeABackground.y = upgradeA.y;
		this.upgradeBBackground.x = upgradeB.x;
		this.upgradeABackground.y = upgradeB.y;
		this.destroyBackground.x = destroyTower.x;
		this.destroyBackground.y = destroyTower.y;
		
	}
	
	private class ShowUI implements Runnable{
		//@Override
		public void run() {
			
			if(currentTower.draw == true)
				return;
			
			runShow = true;
			
			currentTower.draw = true;
			currentTower.opacity = 0.0f;
			
			upgradeA.draw = true;
			upgradeA.opacity = 0.0f;
			
			upgradeB.draw = true;
			upgradeB.opacity = 0.0f;
					
			destroyTower.draw = true;
			destroyTower.opacity = 0.0f;
			
			currentBackground.draw = true;
			currentBackground.opacity = 0.0f;
			
			upgradeABackground.draw = true;
			upgradeABackground.opacity = 0.0f;
			
			upgradeBBackground.draw = true;
			upgradeBBackground.opacity = 0.0f;
			
			destroyBackground.draw = true;
			destroyBackground.opacity = 0.0f;
			
			SystemClock.sleep(500);
			
			startTime = SystemClock.uptimeMillis();
			currentTime = SystemClock.uptimeMillis();
			lastUpdateTime = currentTime;
			while((currentTime - startTime) < 500){
				if(runShow == false)
					return;
				
				if((currentTime - lastUpdateTime) > 50){
					currentTower.opacity += 0.1f;
					upgradeA.opacity += 0.1f;
					upgradeB.opacity += 0.1f;
					destroyTower.opacity += 0.1f;
					
					currentBackground.opacity += 0.1f;
					upgradeABackground.opacity += 0.1f;
					upgradeBBackground.opacity += 0.1f;
					destroyBackground.opacity += 0.1f;
					
					lastUpdateTime = currentTime;
				}
				
				SystemClock.sleep(10);
				currentTime = SystemClock.uptimeMillis();
			}
			
			currentTower.opacity = 1.0f;
			upgradeA.opacity = 1.0f;
			upgradeB.opacity = 1.0f;
			destroyTower.opacity = 1.0f;
			
			currentBackground.opacity = 1.0f;
			upgradeABackground.opacity = 1.0f;
			upgradeBBackground.opacity = 1.0f;
			destroyBackground.opacity = 1.0f;
		}
	}

	public boolean onUpgradeA(int x, int y) {
		
		return x >= upgradeA.x && x <= upgradeA.x + upgradeA.getWidth() &&
			   y >= upgradeA.y && y <= upgradeA.y + upgradeA.getHeight();
	}

	public boolean onUpgradeB(int x, int y) {
		
		return x >= upgradeB.x && x <= upgradeB.x + upgradeB.getWidth() &&
		       y >= upgradeB.y && y <= upgradeB.y + upgradeB.getHeight();
	}

	public boolean onDestroy(int x, int y) {
		return x >= destroyTower.x && x <= destroyTower.x + destroyTower.getWidth() &&
		       y >= destroyTower.y && y <= destroyTower.y + destroyTower.getHeight();
	}

	public void hideUI() {
		this.runShow = false;
		
		currentTower.draw = false;
		upgradeA.draw = false;
		upgradeB.draw = false;
		destroyTower.draw = false;
	}
}
