package com.crackedcarrot;

import java.util.Random;
import java.util.concurrent.Semaphore;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.menu.R;

/**
 * A runnable that updates the position of each creature and projectile
 * every frame by applying a simple movement simulation. Also keeps
 * track of player life, level count etc.
 */
public class GameLoop implements Runnable {
    private Player player;
    private Map mGameMap;
    private Sprite[] mGrid;

    private Creature[] mCreatures;
    private int remainingCreaturesALIVE;
    private int remainingCreaturesALL;
    
    private float startCreatureHealth;
    float currentCreatureHealth;
    private Level[] mLvl;
    private int lvlNbr;
    
    private Tower[] mTower;
    private Tower[][] mTowerGrid;
    private Tower[] mTTypes;
    private int totalNumberOfTowers = 0;
    private Shot[] mShots;
    
    private long mLastTime;
    private boolean run = true;

    private int gameSpeed;
    
    	// We need to reach this to be able to turn off sound.
    public  SoundManager soundManager;
    private Scaler mScaler;
    private NativeRender renderHandle;
    
    private Handler updateCreatureHandler = new Handler();
    private Handler updateHealthHandler = new Handler();
    private Handler nextLevelHandler;
    private CreatureUpdate cUpdate = new CreatureUpdate();
    private ProgressUpdate pUpdate = new ProgressUpdate();
    
    private Semaphore nextLevelSemaphore = new Semaphore(1);
    
    private class CreatureUpdate implements Runnable{
		public void run(){
			NrCreTextView.listener.creatureUpdate(remainingCreaturesALIVE);
		}
	}
    
    private class ProgressUpdate implements Runnable{
    	public void run(){
			HealthProgressBar.proChangeListener.progressUpdate((int)(100*(currentCreatureHealth/startCreatureHealth)));
		}
    }
    
    public GameLoop(NativeRender renderHandle, Map gameMap, Level[] waveList, Tower[] tTypes,
			Player p, Handler nlh, SoundManager sm){
    	this.renderHandle = renderHandle;
		this.mGameMap = gameMap;
   		this.mTowerGrid = gameMap.getTowerGrid();
   		this.mScaler = gameMap.getScaler();
		this.mTTypes = tTypes;
        this.mLvl = waveList;
    	this.soundManager = sm;
    	this.player = p;
    	this.nextLevelHandler = nlh;
    }
    
	private void initializeDataStructures() {
		//this allocates the space we need for shots towers and creatures.
	    this.mTower = new Tower[60];
	    this.mShots = new Shot[60];
	    this.mCreatures = new Creature[50];
		this.mGrid = new Grid[1]; 
		mGrid[0]  = new Grid(R.drawable.grid4px, mScaler);
		
	    //Initialize the all the elements in the arrays with garbage data
	    for (int i = 0; i < mTower.length; i++) {
	    	mTower[i] = new Tower(R.drawable.tower1, mCreatures, soundManager);
	    	mShots[i] = new Shot(R.drawable.cannonball, mTower[i]);
	    	mTower[i].setHeight(this.mTTypes[0].getHeight());
	    	mTower[i].setWidth(this.mTTypes[0].getWidth());
	    	mTower[i].relatedShot = mShots[i];
	    	mTower[i].relatedShot.setHeight(this.mTTypes[0].relatedShot.getHeight());
	    	mTower[i].relatedShot.setWidth(this.mTTypes[0].relatedShot.getWidth());
	    	mTower[i].draw = false;
	    	mShots[i].draw = false;
	    } 

	    Random rand = new Random();	    
	    //same as for the towers and shots.
	    for (int i = 0; i < mCreatures.length; i++) {
	    	mCreatures[i] = new Creature(R.drawable.bunny_pink_alive, player, soundManager, mGameMap.getWaypoints().getCoords(), this);
	    	mCreatures[i].draw = false;
	    	int tmpOffset = rand.nextInt(10) - 5;
	    	Coords tmpCoord = mScaler.scale(tmpOffset,0);
	    	mCreatures[i].setOffset(tmpCoord.getX());
	    }
	    //Set grid attributes.
	    //Free all allocated data in the render
	    //Not needed really.. but now we know for sure that
	    //we don't have any garbage anywhere.
		try {
			renderHandle.freeSprites();
			renderHandle.freeAllTextures();		
			
			//Load textures for towers.

			for (int i = 0; i < mTTypes.length; i++) {
				renderHandle.loadTexture(mTTypes[i].getResourceId());
				renderHandle.loadTexture(mTTypes[i].relatedShot.getResourceId());
			}

			//Load textures for all creature types.
			for(int i = 0; i < mLvl.length; i++){
				renderHandle.loadTexture(mLvl[i].getResourceId());
				renderHandle.loadTexture(mLvl[i].getDeadResourceId());
			}
			renderHandle.loadTexture(mGrid[0].getResourceId());
			//Ok, here comes something superduper mega important.
			//The folowing looks up what names the render assigned
			//To every texture from their resource ids 
			//And assigns that id to the template objects for
			//Towers shots and creatures.
			
			for(int i = 0; i < mTTypes.length; i++){
				mTTypes[i].setTextureName(
						renderHandle.getTextureName(mTTypes[i].getResourceId()));
				
				mTTypes[i].relatedShot.setTextureName(
						renderHandle.getTextureName(mTTypes[i].relatedShot.getResourceId()));
				
			}
			
			for(int i = 0; i < mLvl.length; i++){
				mLvl[i].setTextureName(renderHandle.getTextureName(mLvl[i].getResourceId()));
				mLvl[i].setDeadTextureName(renderHandle.getTextureName(mLvl[i].getDeadResourceId()));
			}
			
						
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// Sends an array with sprites to the renderer
		renderHandle.setSprites(mGameMap.getBackground(), NativeRender.BACKGROUND);
		renderHandle.setSprites(mCreatures, NativeRender.CREATURE);
		renderHandle.setSprites(mTower, NativeRender.TOWER);
		renderHandle.setSprites(mShots, NativeRender.SHOT);
		renderHandle.setSprites(mGrid, NativeRender.GRID);
		
        // Now's a good time to run the GC.  Since we won't do any explicit
        // allocation during the test, the GC should stay dormant and not
        // influence our results.
		Runtime r = Runtime.getRuntime();
        r.gc();
		
	}
    
	private void initializeLvl() {
		try {
			//Free last levels sprites to clear the video mem and ram from
			//Unused creatures and settings that are no longer valid.
			renderHandle.freeSprites();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	
    	//Set the creatures texture size and other atributes.
    	remainingCreaturesALL = mLvl[lvlNbr].nbrCreatures;
    	remainingCreaturesALIVE = mLvl[lvlNbr].nbrCreatures;
    	currentCreatureHealth = mLvl[lvlNbr].getHealth() * remainingCreaturesALL;
    	startCreatureHealth = mLvl[lvlNbr].getHealth() * remainingCreaturesALL;
    	
    	//Need to reverse the list for to draw correctly.
    	for (int z = 0; z < remainingCreaturesALL; z++) {
			// The following line is used to add the following wave of creatures to the list of creatures.
			mLvl[lvlNbr].cloneCreature(mCreatures[z]);
		}
		try {
			
			//Finally send of the sprites to the render to be allocated
			//And after that drawn.
			renderHandle.finalizeSprites();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Show the NextLevel-dialog and waits for user to click ok
		// via the semaphore.
    	try {
			nextLevelSemaphore.acquire();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Message msg = new Message();
		msg.what = 1; // 1 means to show NextLevel-box.
		msg.arg1 = mLvl[lvlNbr].nbrCreatures;
		msg.arg2 = mLvl[lvlNbr].getResourceId();
    	nextLevelHandler.sendMessage(msg);

		// Code to wait for the user to click ok on NextLevel-dialog.
		try {
			nextLevelSemaphore.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		nextLevelSemaphore.release();

    	final long starttime = SystemClock.uptimeMillis();
    	int reverse = remainingCreaturesALL; 
		for (int z = 0; z < remainingCreaturesALL; z++) {
			reverse--;
			int special = 1;
    		if (mCreatures[z].isCreatureFast())
    			special = 2;
    		mCreatures[z].setSpawndelay((long)(starttime + (player.getTimeBetweenLevels() + (reverse * (1000/special)))/gameSpeed));
		}
		
	}

    public void run() {
    	
    	Looper.prepare();
    	
	    initializeDataStructures();
    	lvlNbr = 0;
	    gameSpeed = 1;

	    Log.d("GAMELOOP","INIT GAMELOOP");

	    while(run){
	    	//It is important that ALL SIZES OF SPRITES ARE SET BEFORE! THIS!
    		//OR they will be infinitely small.
    		initializeLvl();
    		
    		// Initialize the status, displaying how many creatures still alive
    		updateCreatureHandler.post(new Runnable(){
				public void run(){
					NrCreTextView.listener.creatureUpdate(remainingCreaturesALL);
				}
			});
    		updateHealthHandler.post(new Runnable(){
				public void run(){
					HealthProgressBar.proChangeListener.progressUpdate(100);
				}
			});
    		
            // The LEVEL loop. Will run until all creatures are dead or done or player are dead.
    		while(remainingCreaturesALL > 0 && run){

    			//Systemclock. Used to help determine speed of the game. 
				final long time = SystemClock.uptimeMillis();
				
	            // Used to calculate creature movement.
				final long timeDelta = time - mLastTime;
	            final float timeDeltaSeconds = 
	                mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
	            mLastTime = time;
	            
	            // Shows how long it is left until next level
	            player.setTimeUntilNextLevel((int)(player.getTimeUntilNextLevel() - mLastTime));	            

	            //Calls the method that moves the creature.
	        	for (int x = 0; x < mLvl[lvlNbr].nbrCreatures; x++) {
	        		mCreatures[x].update(timeDeltaSeconds, time, gameSpeed);
	        	}
	            //Calls the method that handles the monsterkilling.
	        	for (int x = 0; x <= totalNumberOfTowers; x++) {
	        		mTower[x].towerKillCreature(timeDeltaSeconds,gameSpeed, mLvl[lvlNbr].nbrCreatures);
	        	}	            
	            // Check if the GameLoop are to run the level loop one more time.
	            if (player.getHealth() < 1) {
            		//If you have lost all your lives then the game ends.
	            	run = false;
            	}
	        }
    		player.calculateInterest();

    		// Check if the GameLoop are to run the level loop one more time.
            if (player.getHealth() < 1) {
        		//If you have lost all your lives then the game ends.
            	Log.d("GAMETHREAD", "You are dead");

            		// Show the You Lost-dialog.
        		Message msg = new Message();
        		msg.what = 3; // YouLost-box.
            	nextLevelHandler.sendMessage(msg);
            	
            	run = false;
        	} 
        	else if (remainingCreaturesALL < 1) {
        		//If you have survied the entire wave without dying. Proceed to next next level.
            	Log.d("GAMETHREAD", "Wave complete");
        		lvlNbr++;
        		if (lvlNbr >= mLvl.length) {
        			// You have completed this map
                	Log.d("GAMETHREAD", "You have completed this map");
                	
            		// Show the You Won-dialog.
            		Message msg = new Message();
            		msg.what = 2; // YouWon
                	nextLevelHandler.sendMessage(msg);
                	
        			run = false;
        		}
        	}
	    }
    	Log.d("GAMETHREAD", "dead thread");
    }

    public boolean createTower(Coords TowerPos, int towerType) {
		if (mTTypes.length > towerType && totalNumberOfTowers < mTower.length) {
			if (!mScaler.insideGrid(TowerPos.x,TowerPos.y)) {
				//You are trying to place a tower on a spot outside the grid
				return false;
			}
			if (player.getMoney() < mTower[totalNumberOfTowers].getPrice()) {
				// Not enough money to build this tower.
				return false;
			}
			Coords tmpC = mScaler.getGridXandY(TowerPos.x,TowerPos.y);
			int tmpx = tmpC.x;
			int tmpy = tmpC.y;
			
			if (mTowerGrid[tmpx][tmpy] != null && !mTowerGrid[tmpx][tmpy].draw) {
				Coords towerPlacement = mScaler.getPosFromGrid(tmpx, tmpy);
				mTower[totalNumberOfTowers].createTower(mTTypes[towerType], towerPlacement);
				mTowerGrid[tmpx][tmpy] = mTower[totalNumberOfTowers];
				player.moneyFunction(-mTower[totalNumberOfTowers].getPrice());
				totalNumberOfTowers++;
				
				soundManager.playSound(20);
				
				return true;
			} else {
				// User clicked on an existing tower. Show upgrade window.
				Message msg = new Message();
				msg.what = 4;
				nextLevelHandler.sendMessage(msg);
			}
		}
		return false;
    }
    
    // When a creature is dead and have faded away we will remove it from the gameloop
    public void creatureLeavesMAP(int n){
    	this.remainingCreaturesALL -= n;
    }
    // When a creature is dead we will notify the statusbar
    public void creaturDiesOnMap(int n){
    	this.remainingCreaturesALIVE -= n;
		// Update the status, displaying how many creatures that are still alive
		updateCreatureHandler.post(cUpdate);
    }
    
    public void updateCreatureProgress(float dmg){
    	// Update the status, displaying total health of all creatures
    	this.currentCreatureHealth -= dmg;
    	updateHealthHandler.post(pUpdate);
    }
    
    public void stopGameLoop(){
    	run = false;
    }
    
    public void nextLevelClick() {
    	nextLevelSemaphore.release();
    }
    
    public void upgradeTower(int i) {
    	Log.d("GAMELOOP", "upgradeTower: " + i);
    }
    
}