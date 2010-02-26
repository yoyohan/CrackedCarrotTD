package com.crackedcarrot;

import android.os.SystemClock;
import android.util.Log;

/**
 * A runnable that updates the position of each creature and projectile
 * every frame by applying a simple movement simulation. Also keeps
 * track of player life, level count etc.
 */
public class GameLoop implements Runnable {
    private Creature[] mCreatures;
    private Level[] mLvl;
    private Shot[] mShot;
    private long mLastTime;
    private int lvlNbr;
    private int playerHealth;
    private int remainingCreatures;
    private Coords[] wayP;
    public volatile boolean run = true;
    private long gameSpeed;
    //private long difficulty;
    
    public void run() { 
    	final long starttime = SystemClock.uptimeMillis();
    	lvlNbr = 0;
		playerHealth = 60;
	    gameSpeed = 1;
	    //difficulty = 1;

    	while(run){
    		Log.d("GAMELOOP","INIT GAMELOOP");
    		
    		//The following line contains the code for initiating every level
    		/////////////////////////////////////////////////////////////////
    		remainingCreatures = mLvl[lvlNbr].nrCr;
    		for (int z = 0; z < remainingCreatures; z++) {
    			// The following line is used to add the following wave of creatures to the list of creatures.
    			mCreatures[z].cloneCreature(mLvl[lvlNbr].cr);
    			// In some way we have to determine when to spawn the creature. Since we dont want to spawn them all at once.
    			mCreatures[z].spawndelay = z * (int)(mCreatures[z].velocity * mCreatures[z].height * gameSpeed);
    		}
    		
			// The LEVEL loop. Will run until all creatures are dead or done or player are dead.
    		while(remainingCreatures > 0 && run){
	    		//Systemclock. Used to help determine speed of the game. 
				final long time = SystemClock.uptimeMillis();
				
	            // Used to calculate creature movement.
				final long timeDelta = time - mLastTime;
	            final float timeDeltaSeconds = 
	                mLastTime > 0.0f ? timeDelta / 1000.0f : 0.0f;
	            mLastTime = time;
	            
	            //Calls the method that moves the creature.
	            moveCreature(timeDeltaSeconds,time-starttime);
	            //Calls the method that handles the monsterkilling.
	            killCreature(timeDeltaSeconds);

	            
	            // Check if the GameLoop are to run the level loop one more time.
	            if (playerHealth < 1) {
            		//If you have lost all your lives then the game ends.
	            	run = false;
            	} 
	        }

    		// Check if the GameLoop are to run the level loop one more time.
            if (playerHealth < 1) {
        		//If you have lost all your lives then the game ends.
            	Log.d("GAMETHREAD", "You are dead");
            	run = false;
        	} 
        	else if (remainingCreatures < 1) {
        		//If you have survied the entire wave without dying. Proceed to next next level.
            	Log.d("GAMETHREAD", "Level " + lvlNbr + "complete");
        		lvlNbr++;
        		if (lvlNbr > mLvl.length) {
        			// You have completed this map
                	Log.d("GAMETHREAD", "You have completed this map");
        			run = false;
        		}
        	}

	    }
    	Log.d("GAMETHREAD", "dead thread");
    }

    
	/**
	 * Will go through all of the creatures from this level and
	 * calculate the movement. 
	 * <p>
	 * This method runs every loop the gameLoop takes. 
	 *
	 * @param  timeDeltaSeconds  	Time since last GameLoop lap 
	 * @param  time	 				Time since this level started
	 * @return      				void
	 */
    public void moveCreature(float timeDeltaSeconds, long time) {
    	// If the list of creatures is empty we will end this method
    	if (mCreatures == null) {
    		return;
    	}
    	for (int x = 0; x < mLvl[lvlNbr].nrCr; x++) {
    		Creature object = mCreatures[x];
    		
    		// Check to see if a not existing creature is supposed to spawn on to the map
			if (time > object.spawndelay && wayP[0].x == object.x && wayP[0].y == object.y) {
				object.draw = true;
			}	            	
			
			// If the creature is living start movement calculations.
			if (object.draw) {
	    		Coords co = wayP[object.nextWayPoint];

	    		// Creature is moving left.
				if(object.x > co.x){
		    		object.direction = Creature.LEFT;
		    		object.x = object.x - (object.velocity * timeDeltaSeconds * gameSpeed);
		    		if(!(object.x > co.x)){
		    			object.x = co.x;
		    		}
		    	}
	    		// Creature is moving right.
				else if (object.x < co.x) {
		    		object.direction = Creature.RIGHT;
		    		object.x = object.x + (object.velocity * timeDeltaSeconds * gameSpeed);
		    		if(!(object.x < co.x)){
		    			object.x = co.x;
		    		}
		    	}
	    		// Creature is moving down.
				else if(object.y > co.y){
		    		object.direction = Creature.DOWN;
		    		object.y = object.y - (object.velocity * timeDeltaSeconds * gameSpeed);
		    		if(!(object.y > co.y)){
		    			object.y = co.y;
		    		}
		    	}
	    		// Creature is moving up.
		    	else if (object.y < co.y) {
		    		object.direction = Creature.UP;
		    		object.y = object.y + (object.velocity * timeDeltaSeconds * gameSpeed);
		    		if(!(object.y < co.y)){
		    			object.y = co.y;
		    		}
		    	}
				// Creature has reached a WayPoint. Update
		    	if (object.y == co.y && object.x == co.x){
		    		object.updateWayPoint();
		    	}
		    	// Creature has reached is destination without being killed
		    	if (object.nextWayPoint >= wayP.length){
		    		object.draw = false;
		    		playerHealth--;
		    		remainingCreatures--;
		    	}
			}
    	}
    }

    
	/**
	 * Will go through all of the towews and try to find targets
	 * <p>
	 * This method runs every loop the gameLoop takes. 
	 *
	 * @param  timeDeltaSeconds  	Time since last GameLoop lap 
	 * @return      				void
	 */
    public void killCreature(float timeDeltaSeconds) {
    	// If the list of shots is empty we will end this method
    	if (mShot == null) {
    		return;
    	}
    	for (int x = 0; x < mShot.length; x++) {
    		Shot object = mShot[x];
    		
    		if (!object.draw) {
    			// If the tower/shot is existing start calculations.
    			object.trackEnemy(mCreatures);
    			if (object.cre != null) {
    				//object.calcWayPoint(wayP);
    				//if (object.crTarget != null) {
    					object.draw = true;
    				//}
    			}
    		}
    		// if the creature is still alive or have not reached the goal
    		if (object.draw && object.cre.draw) {
    			Creature cro = object.cre;

    			float yDistance = (cro.y+(cro.height/2)) - object.y;
    			float xDistance = (cro.x+(cro.width/2)) - object.x;
    			double xyMovement = (object.velocity * timeDeltaSeconds * gameSpeed);
    			
    			if ((Math.abs(yDistance) <= xyMovement) && (Math.abs(xDistance) <= xyMovement)) {
		    		object.draw = false;
		    		object.resetShotCordinates();
		    		//Basic way of implementing damage
		    		object.cre.health = object.cre.health - object.tower.damage;
		    		if (object.cre.health <= 0) {
		    			object.cre.draw = false;		    		
		    			remainingCreatures--;
		    			Log.d("LOOP","Creature killed");
		    		}
    			}
    			else {
        			double radian = Math.atan2(yDistance, xDistance);
    				object.x += Math.cos(radian) * xyMovement;
    				object.y += Math.sin(radian) * xyMovement;
    			}
			}
    		else {
	    		object.draw = false;
	    		object.resetShotCordinates();
    		}
		}
	}
    
    
	/**
	 * Will set the list of creatures moving over the map.
	 * <p>
	 * This method is called before GameLoop is started. 
	 *
	 * @param  creature  	List of Creature. 
	 * @return      		void
	 */    
    public void setCreatures(Creature[] creatures) {
        this.mCreatures = creatures;
    }
    
	/**
	 * Will give level information to the GameLoop.
	 * calculate the movement. 
	 * <p>
	 * This method is called before GameLoop is started. 
	 *
	 * @param  lvl			List of type Levels
	 * @return      		void
	 */    
    public void setLevels(Level[] lvl) {
        this.mLvl = lvl;
    }
    
	/**
	 * Will set the WayPoints for this map to  the GameLoop.
	 * <p>
	 * This method is called before GameLoop is started. 
	 *
	 * @param  wp			Object of type WayPoints
	 * @return     			void
	 */    
    public void setWP(WayPoints wayP){
    	this.wayP = wayP.getCoords();
    }

    /**
	 * Will set the Shots(Tower projectiles) for this map to  the GameLoop.
	 * <p>
	 * This method is called before GameLoop is started. 
	 *
	 * @param  sh			List of type Shot
	 * @return     			void
	 */    
    public void setShots(Shot[] sh){
    	this.mShot = sh;
    }
    
}