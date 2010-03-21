package com.crackedcarrot;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;

import com.crackedcarrot.fileloader.Level;
import com.crackedcarrot.fileloader.Map;
import com.crackedcarrot.fileloader.TowerGrid;
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
    private int remainingCreatures;
    private int startNrCreatures;
    private int percentageCreatures = 100;

    private Level[] mLvl;
    private int lvlNbr;
    
    private Tower[] mTower;
    private TowerGrid[][] mTowerGrid;
    private Tower[] mTTypes;
    private int totalNumberOfTowers = 0;
    private Shot[] mShots;
    
    private long mLastTime;
    private boolean run = true;

    private int gameSpeed;
    
    private SoundManager soundManager;
    private Scaler mScaler;
    private NativeRender renderHandle;
    
    private Handler updateCreatureHandler = new Handler();
    private Handler updateHealthHandler = new Handler();
    
    public GameLoop(NativeRender renderHandle, Map gameMap, Level[] waveList, Tower[] tTypes,
			Player p, SoundManager sm){
    	this.renderHandle = renderHandle;
		this.mGameMap = gameMap;
   		this.mTowerGrid = gameMap.getTowerGrid();
   		this.mScaler = gameMap.getScaler();
		this.mTTypes = tTypes;
        this.mLvl = waveList;
    	this.soundManager = sm;
    	this.player = p;
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
	    	mTower[i].relatedShot = mShots[i];
	    	mTower[i].draw = false;
	    	mShots[i].draw = false;
	    } 

	    //same as for the towers and shots.
	    for (int i = 0; i < mCreatures.length; i++) {
	    	mCreatures[i] = new Creature(R.drawable.bunny_pink_alive, player, soundManager, mGameMap.getWaypoints().getCoords());
	    	mCreatures[i].draw = false;
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
    	final long starttime = SystemClock.uptimeMillis();
    	
    	//Set the creatures texture size and other atributes.
    	remainingCreatures = mLvl[lvlNbr].nbrCreatures;
    	startNrCreatures = remainingCreatures;
    	//Need to reverse the list for to draw correctly.
    	int reverse = remainingCreatures; 
		for (int z = 0; z < remainingCreatures; z++) {
			reverse--;
			// The following line is used to add the following wave of creatures to the list of creatures.
			mCreatures[z].cloneCreature(mLvl[lvlNbr]);
    		// In some way we have to determine when to spawn the creature. Since we dont want to spawn them all at once.
			int special = 1;
    		if (mCreatures[z].isCreatureFast())
    			special = 2;
    		mCreatures[z].setSpawndelay((long)(starttime + (player.timeBetweenLevels + (reverse * (500/special)))/gameSpeed));
		}
		try {
			
			//Finally send of the sprites to the render to be allocated
			//And after that drawn.
			renderHandle.finalizeSprites();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

    public void run() {
    	
    	Looper.prepare();
    	
	    initializeDataStructures();
    	lvlNbr = 0;
	    gameSpeed = 1;
	    Log.d("GAMELOOP","INIT GAMELOOP");

	    while(run){
	    	// Tries to create a test tower
	    	Coords tmp = mScaler.getPosFromGrid(2, 9);
	    	createTower(tmp,0);
	    	tmp = mScaler.getPosFromGrid(4, 6);
	    	createTower(tmp,0);
	    	
	    	//It is important that ALL SIZES OF SPRITES ARE SET BEFORE! THIS!
    		//OR they will be infinitely small.
    		initializeLvl();
    		
    		// Initialize the status, displaying how many creatures still alive
    		updateCreatureHandler.post(new Runnable(){
				public void run(){
					NrCreTextView.listener.creatureUpdate(remainingCreatures);
				}
			});
    		updateHealthHandler.post(new Runnable(){
				public void run(){
					HealthProgressBar.proChangeListener.progressUpdate(100);
				}
			});
    		
            // The LEVEL loop. Will run until all creatures are dead or done or player are dead.
    		while(remainingCreatures > 0 && run){

    			//Systemclock. Used to help determine speed of the game. 
				final long time = SystemClock.uptimeMillis();
				
	            // Used to calculate creature movement.
				final long timeDelta = time - mLastTime;
	            final float timeDeltaSeconds = 
	                mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
	            mLastTime = time;
	            
	            // Shows how long it is left until next level
	            player.timeUntilNextLevel = (int)(player.timeUntilNextLevel - mLastTime);	            

	            //Calls the method that moves the creature.
	        	for (int x = 0; x < mLvl[lvlNbr].nbrCreatures; x++) {
	        		this.remainingCreatures -= mCreatures[x].move(timeDeltaSeconds, time, gameSpeed);
	        	}
	            //Calls the method that handles the monsterkilling.
	        	for (int x = 0; x <= totalNumberOfTowers; x++) {
	        		mTower[x].towerKillCreature(timeDeltaSeconds,gameSpeed, mLvl[lvlNbr].nbrCreatures);
	        	}	            
	            // Check if the GameLoop are to run the level loop one more time.
	            if (player.health < 1) {
            		//If you have lost all your lives then the game ends.
	            	run = false;
            	}
	        }
    		player.calculateInterest();

    		// Check if the GameLoop are to run the level loop one more time.
            if (player.health < 1) {
        		//If you have lost all your lives then the game ends.
            	Log.d("GAMETHREAD", "You are dead");
            	run = false;
        	} 
        	else if (remainingCreatures < 1) {
        		//If you have survied the entire wave without dying. Proceed to next next level.
            	Log.d("GAMETHREAD", "Wave complete");
        		lvlNbr++;
        		if (lvlNbr >= mLvl.length) {
        			// You have completed this map
                	Log.d("GAMETHREAD", "You have completed this map");
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
			Coords tmpC = mScaler.getGridXandY(TowerPos.x,TowerPos.y);
			int tmpx = tmpC.x;
			int tmpy = tmpC.y;
			
			if (mTowerGrid[tmpx][tmpy].empty) {
				mTower[totalNumberOfTowers].cloneTower(mTTypes[towerType]);
				Coords tmp = mScaler.getPosFromGrid(tmpx, tmpy);
				mTower[totalNumberOfTowers].x = tmp.x;
				mTower[totalNumberOfTowers].y = tmp.y;
				mTower[totalNumberOfTowers].resetShotCordinates();//Same location of Shot as midpoint of Tower
				mTowerGrid[tmpx][tmpy].empty = false;
				mTowerGrid[tmpx][tmpy].tower = totalNumberOfTowers;
				totalNumberOfTowers++;
				return true;
			}
		}
		return false;
    }
    
    public void stopGameLoop(){
    	run = false;
    }
    
}