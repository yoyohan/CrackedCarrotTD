package com.crackedcarrot;

import java.util.Enumeration;
import java.util.Random;

import android.util.Log;

/**
* Class defining a tower in the game
*/
public class Tower extends Sprite {
	private SoundManager soundManager;
	private Creature[] mCreatures;
	private Tracker creepTracker;
	
	//different towertypes
	private final int PROJECTILEAOE = 1;
	private final int PUREAOE = 2;
	private final int PROJECTILE = 3;
	//towertype
	private int towerType;
	// The current range of a tower
	private float range;
	// The current AOE range of a tower
	private float rangeAOE;
	// Tower title
	private String title;
	// Price for the tower
	private int price;
	// Resell value if the tower is sold
	private int resellPrice;
	// Minimum damage that this tower can inflict
	private int minDamage;
	// Maximum damage that this tower can inflict
	private int maxDamage;
	// AOE damage
	private int aoeDamage;
	// Speed of the shots
	private int velocity;
	// If the tower have frost damage
	private boolean hasFrostDamage;
	// If the tower have frost damage. How long?
	private int frostTime;
	// If the tower have fire damage
	private boolean hasFireDamage;
	// If the tower have poison damage
	private boolean hasPoisonDamage;
	// If the tower have poison damage, how mutch?
	private int poisonDamage;
	// If the tower have poison damage, how long?
	private int poisonTime;
	// The first linked update for this tower
	private int upgrade1;
	// The second linked update for this tower
	private int upgrade2;
	// The type of shot related to this tower
	public Shot relatedShot;
    // The time existing between each fired shot
    private float coolDown;
    // The temporary variable representing the time existing between each fired shot
    private float tmpCoolDown;
    // The current target creature
    private Creature targetCreature;
	// Random used to calculate damage
    private Random rand;
    // DATA used by tracker
    private int left_column;
    private int right_column;
    private int upper_row;
    private int bottom_row;
    
    	// used by resume to uniquely identify this tower-type.
    private int towerTypeId;

    private boolean esplodeGIB = false;
    
	public Tower(int resourceId, int type, Creature[] mCreatures, SoundManager soundManager, Tracker creepTracker){
		super(resourceId, TOWER, type);
		this.soundManager = soundManager;
		this.mCreatures = mCreatures;
		this.creepTracker = creepTracker;
		rand = new Random();
	}
	public Tower(int resourceId, int type){
		super(resourceId,TOWER, type);
		rand = new Random();
	}
	
	/**
	 * Calculates special damage. Used by all towers that have frost,posion or fire damage
	 */
	private float specialDamage(Creature tmpCreature, boolean aoeTower) {
		//If tower has frostdamage
		if (this.hasFrostDamage) {
			if (aoeTower)
				tmpCreature.affectWithFrost(this.frostTime/2);
			else
				tmpCreature.affectWithFrost(this.frostTime);
		}		
		// If tower has poison damage
		if (this.hasPoisonDamage) {
			if (aoeTower) 
				tmpCreature.affectWithPoison(poisonTime,this.poisonDamage);
			else
				tmpCreature.affectWithPoison(poisonTime/2,this.poisonDamage);
		}				
		// If tower has firedamage
		if (tmpCreature.creatureFireResistant && this.hasFireDamage)
			return 0.4f;
		else 
			return 1;
	}
	
	/**
	 * Method that tracks a creature. It iterates over a list of creatures and picks
	 * the first creature in the list that is within the range of the tower 
	 * @param null 
	 */
	private Creature trackNearestEnemy(int nbrCreatures) {
		Creature targetCreature = null;
		double lastCreatureDistance = Double.MAX_VALUE;
		
		for(int i = 0;i < nbrCreatures; i++ ){
			if(mCreatures[i].draw == true && mCreatures[i].health > 0){ // Is the creature still alive?
				double distance = Math.sqrt(
									Math.pow((this.relatedShot.x - (mCreatures[i].x + mCreatures[i].getWidth()/2))  , 2) + 
									Math.pow((this.relatedShot.y - (mCreatures[i].y + mCreatures[i].getHeight()/2)) , 2)  );
				if(distance < range){ // Is the creature within tower range?
					if (targetCreature == null) 
						targetCreature = mCreatures[i];
					else if (lastCreatureDistance > distance) {
						targetCreature = mCreatures[i];
						lastCreatureDistance = distance;
					}
				}
			}
		}
		return targetCreature;
	}

	/**
	 * TRACKER BETA
	 * The tower is safe to asume that all creatures that will be returned by the tracker is alive 
	 */
	private Creature trackNearestEnemyBETA() {
		Creature targetCreature = null;
		double lastCreatureDistance = Double.MAX_VALUE;
		Enumeration<Creature> tmpCreep = creepTracker.getCreaturesInRange(left_column, right_column, upper_row, bottom_row);
		while (tmpCreep.hasMoreElements()) {
			Creature currCreep = tmpCreep.nextElement();
			double distance = Math.sqrt(
					Math.pow((this.relatedShot.x - currCreep.x + currCreep.getWidth()/2),2) + 
					Math.pow((this.relatedShot.y - currCreep.y + currCreep.getHeight()/2),2));
			if(distance < range){ // Is the creature within tower range?
				if (lastCreatureDistance > distance) {
					targetCreature = currCreep;
					lastCreatureDistance = distance;
				}
			}
		}
		return targetCreature;
	}	
	
	
	/**
	 * Method that tracks all creatures that are in range of tower. It iterates over a list of creatures and 
	 * picks all creatures in range.
	 */
	private int trackAllNearbyEnemies(int nbrCreatures, boolean doFullDamage) {
		int nbrOfHits = 0;
		float range;
		if (doFullDamage)
			range = this.range;
		else 
			range = this.rangeAOE;
		
		for(int i = 0;i < nbrCreatures; i++ ){
			if(mCreatures[i].draw == true && mCreatures[i].health > 0){ // Is the creature still alive?
				double distance = Math.sqrt(
						Math.pow((this.relatedShot.x - (mCreatures[i].x + mCreatures[i].getWidth()/2))  , 2) + 
						Math.pow((this.relatedShot.y - (mCreatures[i].y + mCreatures[i].getHeight()/2)) , 2)  );
				if(distance <= range){ 
					float randomInt;
					if (doFullDamage) {
						float damageFactor = specialDamage(mCreatures[i],false);
						randomInt = (rand.nextInt(this.maxDamage-this.minDamage) + this.minDamage) * damageFactor;
					} else {
						float damageFactor = specialDamage(mCreatures[i],true);
						randomInt = this.aoeDamage * damageFactor;
					}
					mCreatures[i].damage(randomInt);
					nbrOfHits++;
				}
			}
		}
		return nbrOfHits;
	}
	
	/**
	 * Method that calculates the damage for a specific tower
	 * depending on the upgrade level and a random integer
	 * so the damage wont be predictable during game play
	 */
	private void createProjectileDamage(float timeDeltaSeconds, int nbrCreatures){
		// AOE tower uses this to create animate
		if (esplodeGIB) {
			if (this.tmpCoolDown <= this.coolDown/2) {
				this.relatedShot.draw = false;
				this.esplodeGIB = false;
				this.resetShotCordinates();
				this.relatedShot.scale = 1;
				this.relatedShot.cFrame = 0;
			}
			else {
				//this.relatedShot.cFrame = (int)
				//	(2*(this.relatedShot.getNbrOfFrames()-1)
				//			* (1-tmpCoolDown/coolDown)) + 1;
				this.relatedShot.cFrame = (int)
					(
							(this.relatedShot.getNbrOfFrames() - 1)*((coolDown-tmpCoolDown)/(coolDown/2))
					);
				this.relatedShot.cFrame++;
				
			}
		}
		//First we have to check if the tower is ready to fire
		else if (!this.relatedShot.draw && (this.tmpCoolDown <= 0)) {
			// This is happens when a tower with projectile damage is ready to fire.
			//this.targetCreature = trackNearestEnemyBETA();
			this.targetCreature = trackNearestEnemy(nbrCreatures);
			towerStartFireSequence(this.targetCreature);
		}
		// if the tower is currently in use:
		else if (this.relatedShot.draw) {
			updateProjectile(timeDeltaSeconds,nbrCreatures);
		}
	}

	/**
	 * Method that updates the shot for the current tower
	 */
	private void updateProjectile(float timeDeltaSeconds, int nbrCreatures) {

		float yDistance = (targetCreature.y+(targetCreature.getHeight()/2)) - this.relatedShot.y+(this.relatedShot.getHeight()/2);
		float xDistance = (targetCreature.x+(targetCreature.getWidth()/2)) - this.relatedShot.x+(this.relatedShot.getWidth()/2);
		double xyMovement = (this.velocity * timeDeltaSeconds);
		
		// This will only happen if we have reached our destination creature
		if ((Math.abs(yDistance) <= xyMovement) && (Math.abs(xDistance) <= xyMovement)) 
			projectileHitsTarget(nbrCreatures);		
		else {
			// Travel until we reach target
			double radian = Math.atan2(yDistance, xDistance);
			this.relatedShot.x += Math.cos(radian) * xyMovement;
			this.relatedShot.y += Math.sin(radian) * xyMovement;
		}
	}

	private void projectileHitsTarget(int nbrCreatures) {
		this.tmpCoolDown = this.coolDown;

		float damageFactor = specialDamage(this.targetCreature,false);
		float randomInt = (rand.nextInt(this.maxDamage-this.minDamage) + this.minDamage) * damageFactor;
		targetCreature.damage(randomInt);
		//IF A PROJECTILEAOE tower fires a shot we also have to damage surrounding creatures
		if (this.towerType == this.PROJECTILEAOE){
	    	this.trackAllNearbyEnemies(nbrCreatures,false);
	    	relatedShot.scale(this.rangeAOE);
	    	esplodeGIB = true;
		}
		else {
			this.resetShotCordinates();
			this.relatedShot.draw = false;
		}
	}
	
	/**
	 * Method that calculates AOE damage for a specific tower
	 * depending on the upgrade level and a random integer
	 * so the damage wont be predictable during game play
	 * This method is only used by towers with direct aoe damage.
	 * Not to be confused with towers that have projectiledamage
	 */
	private void createPureAOEDamage(int nbrCreatures){
		if (this.tmpCoolDown <= this.coolDown/2) {
			this.relatedShot.draw = false;
			this.resetShotCordinates();
		}
		else if (this.relatedShot.draw && this.tmpCoolDown-this.coolDown/2 <= 1f) {
	    	this.relatedShot.opacity = this.tmpCoolDown-this.coolDown/2;
		}
		if (this.tmpCoolDown <= 0) {
			if (trackAllNearbyEnemies(nbrCreatures,true) > 0) {
				// TODO: 
				// TODO:
				soundManager.playSound(0);
				this.tmpCoolDown = this.coolDown;
				this.relatedShot.draw = true;
				this.resetShotCordinates();
				relatedShot.scale(this.range);
			}
		}
	}	
	

	public void attackCreatures(float timeDeltaSeconds, int nbrCreatures) {
		// Decrease the coolDown variable
		this.tmpCoolDown = this.tmpCoolDown - timeDeltaSeconds;

		// This code is used by towers firing pure aoe damage.
		if (this.towerType == this.PUREAOE) {
			createPureAOEDamage(nbrCreatures);
		}
		else if (this.towerType == this.PROJECTILE || this.towerType == this.PROJECTILEAOE) {
			createProjectileDamage(timeDeltaSeconds, nbrCreatures);
		}
	}
	
	/**
	 * Make sound when projectile leaves tower
	 */		
	private void towerStartFireSequence(Creature targetCreature) {
		if (targetCreature != null) {
			// play shot1.mp3
			soundManager.playSound(0);
			//this.tmpCoolDown = this.coolDown;
			this.relatedShot.draw = true;
		}
	}
	
	/**
	 * Returns the cost of this tower
	 */	
	public int getPrice() {
		return price;
	}
	
	/**
	 * Method that places the tower related shot back to 
	 * the start position
	 */
	public void resetShotCordinates() {
		relatedShot.x = this.x + getWidth()/2;
		relatedShot.y = this.y + getHeight()/2;	
	}

	/**
	 * Given all variable this method will create a exaxt copy of
	 * another tower
	 */
	public void cloneTower(	
				int resourceId,
				int towerType,
				int towerTypeId,
				float range,
				float rangeAOE,
				String title,
				int price,
				int resellPrice,
				int minDamage,
				int maxDamage,
				int aoeDamage,
				int velocity,
				boolean hasFrostDamage,
				int frostTime,
				boolean hasFireDamage,
				boolean hasPoisonDamage,
				int poisonDamage,
				int poisonTime,
				int upgrade1,
				int upgrade2,
				float coolDown,
				float width,
				float height,
				Shot copyShot
			){

			this.setResourceId(resourceId);
			this.towerType = towerType;
			this.towerTypeId = towerTypeId;
			this.range = range;
			this.rangeAOE = rangeAOE;
			this.title = title;
			this.price = price;
			this.resellPrice = resellPrice;
			this.minDamage = minDamage;
			this.maxDamage = maxDamage;
			this.aoeDamage = aoeDamage;
			this.velocity = velocity;
			this.hasFrostDamage = hasFrostDamage;
			this.frostTime = frostTime;
			this.hasFireDamage = hasFireDamage;
			this.hasPoisonDamage = hasPoisonDamage;
			this.poisonDamage = poisonDamage;
			this.poisonTime = poisonTime;
			this.upgrade1 = upgrade1;
			this.upgrade2 = upgrade2;
			this.coolDown = coolDown;
			this.setWidth(width);
			this.setHeight(height);
			this.relatedShot.setWidth(copyShot.getWidth());
			this.relatedShot.setHeight(copyShot.getHeight());
			this.relatedShot.setResourceId(copyShot.getResourceId());
	}

	/**
	 * Given a tower this method will create a new tower with the same
	 * variables as the given one
	 */
	public void createTower(Tower clone, Coords towerPlacement, Scaler mScaler) {
		//Use the textureNames that we preloaded into the towerTypes at startup
		this.cloneTower(
				clone.getResourceId(),
				clone.towerType,
				clone.towerTypeId,
				clone.range,
				clone.rangeAOE,
				clone.title,
				clone.price,
				clone.resellPrice,
				clone.minDamage,
				clone.maxDamage,
				clone.aoeDamage,
				clone.velocity,
				clone.hasFrostDamage,
				clone.frostTime,
				clone.hasFireDamage,
				clone.hasPoisonDamage,
				clone.poisonDamage,
				clone.poisonTime,
				clone.upgrade1,
				clone.upgrade2,
				clone.coolDown,
				clone.getWidth(),
				clone.getHeight(),
		    	clone.relatedShot
			);
		
		// This cannot be in the cloneTower function because
		// then it breaks with TowerLoader.java

		//this.setCurrentTexture(clone.getCurrentTexture());
		//this.relatedShot.setCurrentTexture(clone.relatedShot.getCurrentTexture());
		
		this.draw = true;
		this.x = towerPlacement.x;
		this.y = towerPlacement.y;
		this.resetShotCordinates();//Same location of Shot as midpoint of Tower
		this.relatedShot.draw = false;
		
		////////////////////////////////////
		// Code used by the tracker:
		////////////////////////////////////
		Coords tmp = mScaler.getGridXandY((int)this.x, (int)this.y);
		int column = tmp.x;
		int row = tmp.y;
		
		Coords range = mScaler.getGridXandY((int)this.range, 0);
		int size = range.x;

		
		
		
		// TODO USED BY tracker to define tower position
		//�versta raden:
		this.upper_row = row+size;
		if (this.upper_row > mScaler.getGridHeight())
			this.upper_row = mScaler.getGridHeight();
		//Nedersta raden
		this.bottom_row = row-size;
		if (this.bottom_row < 0)
			this.bottom_row = 0;
		//V�nstra
		this.left_column = column - size;
		if (this.left_column < 0)
			this.left_column = 0;
		//H�gra
		this.right_column = column + size;
		if (this.right_column > mScaler.getGridWidth())
			this.right_column = mScaler.getGridWidth();
	}
	
	public int getTowerTypeId() {
		Log.d("TOWER", "arg returned: " + towerTypeId);
		return towerTypeId;
	}

}