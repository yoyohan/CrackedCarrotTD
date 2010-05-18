package com.crackedcarrot;

import java.util.Random;

/**
* Class defining a tower in the game
*/
public class Tower extends Sprite {
	private SoundManager soundManager;
	private Creature[] mCreatures;
	//different towertypes
	protected final static int CANNON = 1;
	protected final static int AOE = 2;
	protected final static int BUNKER = 3;
	protected final static int TELSA = 4;
	//towertype
	public int towerType;
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
   // used by resume to uniquely identify this tower-type.
    private int towerTypeId;
    private boolean ImpactdAnimate = false;
    //To determine when a creature has been teleported to spawnpoint we will check his nbr of laps
	private int targetCreatureMapLap;
    
    /**
     * Constructor used when defining a new tower in the game. Needs the texture resource,
     * subtype, list of creatures and soundmanager.
     * @param resourceId
     * @param type
     * @param mCreatures
     * @param soundManager
     */
	public Tower(int resourceId, int type, Creature[] mCreatures, SoundManager soundManager){
		super(resourceId, TOWER, type);
		this.soundManager = soundManager;
		this.mCreatures = mCreatures;
		rand = new Random();
	}
	
	public void initTower(int resourceId, int type, Creature[] mCreatures, SoundManager soundManager){
		this.setResourceId(resourceId);
		this.setType(TOWER, type);
		this.soundManager = soundManager;
		this.mCreatures = mCreatures;
		rand = new Random();
	}
	
	/**
	 * Calculates special damage. Used by all towers that have frost,posion or fire damage
     * @param tmpCreature
     * @param aoeTower
 	 * @return damage factor
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
	 * @param nbrCreatures
	 * @return nearest creature
	 */
	private Creature trackNearestEnemy(int nbrCreatures) {
		Creature targetCreature = null;
		double lastCreatureDistance = Double.MAX_VALUE;
		
		for(int i = 0;i < nbrCreatures; i++ ){
			if(mCreatures[i].draw == true && mCreatures[i].health > 0){ // Is the creature still alive?
				double distance = Math.sqrt(
									Math.pow((this.relatedShot.x - (mCreatures[i].getScaledX())) , 2) + 
									Math.pow((this.relatedShot.y - (mCreatures[i].getScaledY())) , 2)  );
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
	 * Method that tracks all creatures that are in range of tower. It iterates over a list of creatures and 
	 * picks all creatures in range.
	 * @param nbrCreatures
	 * @param doFullDamage
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
						Math.pow((this.relatedShot.x - (mCreatures[i].getScaledX())) , 2) + 
						Math.pow((this.relatedShot.y - (mCreatures[i].getScaledY())) , 2)  );
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
	 * @param timeDeltaSeconds
	 * @param nbrCreatures
	 */
	private void createProjectileDamage(float timeDeltaSeconds, int nbrCreatures){
		//First we have to check if the tower is ready to fire
		if (!this.relatedShot.draw && (this.tmpCoolDown <= 0)) {
			// This is happens when a tower with projectile damage is ready to fire.
			//this.targetCreature = trackNearestEnemyBETA();
			this.targetCreature = trackNearestEnemy(nbrCreatures);
			towerStartFireSequence(this.targetCreature);
		}
		//Creature has been teleported away. stop firing
		else if (this.relatedShot.draw && targetCreatureMapLap != targetCreature.mapLap) {
			this.tmpCoolDown = this.coolDown;			
			this.relatedShot.draw = false;
			this.relatedShot.resetShotCordinates();			
		}
		else if (this.relatedShot.draw && ImpactdAnimate) {
			float size = this.relatedShot.getWidth()/2;
			if (this.towerType == Tower.CANNON)
				size = this.rangeAOE;
			if (this.towerType == Tower.TELSA)
				size = this.targetCreature.getWidth()/2*this.targetCreature.scale;
			
			ImpactdAnimate = relatedShot.animateShot(timeDeltaSeconds, size, targetCreature);
		}
		// if the tower is currently in use:
		else if (this.relatedShot.draw) {
			updateProjectile(timeDeltaSeconds,nbrCreatures);
		}
	}

	/**
	 * Method that updates the shot for the current tower
	 * @param timeDeltaSeconds
	 * @param nbrCreatures
	 */
	private void updateProjectile(float timeDeltaSeconds, int nbrCreatures) {

		float yDistance = targetCreature.getScaledY() - this.relatedShot.y-(this.relatedShot.getHeight()/2);
		float xDistance = targetCreature.getScaledX() - this.relatedShot.x-(this.relatedShot.getWidth()/2);
		double xyMovement = (this.velocity * timeDeltaSeconds);
		
		if (this.towerType == Tower.TELSA || this.towerType == Tower.BUNKER) {
			relatedShot.animateMovingShot(timeDeltaSeconds);
		}
		
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

	/**
	 * This runs when a tower hits a creature
	 * @param nbrCreatures
	 */
	private void projectileHitsTarget(int nbrCreatures) {
		this.tmpCoolDown = this.coolDown;
		float damageFactor = specialDamage(this.targetCreature,false);
		float randomInt = (rand.nextInt(this.maxDamage-this.minDamage) + this.minDamage) * damageFactor;
		targetCreature.damage(randomInt);
		
		this.ImpactdAnimate = true;
		relatedShot.tmpAnimationTime = relatedShot.animationTime;
		
    	if (this.towerType == Tower.CANNON){
	    	this.trackAllNearbyEnemies(nbrCreatures,false);
		}
	}
	
	/**
	 * Method that calculates AOE damage for a specific tower
	 * depending on the upgrade level and a random integer
	 * so the damage wont be predictable during game play
	 * This method is only used by towers with direct aoe damage.
	 * Not to be confused with towers that have projectiledamage
	 */
	private void createPureAOEDamage(int nbrCreatures, float timeDeltaSeconds){
		if (ImpactdAnimate) {
			float size = this.range;
			ImpactdAnimate = relatedShot.animateShot(timeDeltaSeconds, size, null);
		}
		else if (this.tmpCoolDown <= 0) {
			if (trackAllNearbyEnemies(nbrCreatures,true) > 0) {
				soundManager.playSound(0);
				this.tmpCoolDown = this.coolDown;
				this.relatedShot.draw = true;
				//relatedShot.scaleSprite(this.range);
				ImpactdAnimate = true;
			}
		}
	}	
	

	/**
	 * Main function of the tower class. Find and kill creatures
	 * @param timeDeltaSeconds
	 * @param nbrCreatures
	 */
	public void attackCreatures(float timeDeltaSeconds, int nbrCreatures) {
		// Decrease the coolDown variable
		this.tmpCoolDown = this.tmpCoolDown - timeDeltaSeconds;

		// This code is used by towers firing pure aoe damage.
		if (this.towerType == Tower.AOE) {
			createPureAOEDamage(nbrCreatures,timeDeltaSeconds);
		}
		// If bunker, cannon or tesla
		else {
			createProjectileDamage(timeDeltaSeconds, nbrCreatures);
		}
	}
	
	/**
	 * Init fire sequence. Show projectile and play sound
	 * @param targetCreature
	 */
	private void towerStartFireSequence(Creature targetCreature) {
		if (targetCreature != null) {
			// play shot1.mp3
			soundManager.playSound(0);
			//this.tmpCoolDown = this.coolDown;
			this.relatedShot.draw = true;
			this.targetCreatureMapLap = targetCreature.mapLap;
		}
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
			this.relatedShot.setAnimationTime(copyShot.getAnimationTime());
	}
	
	/**
	 * Create new tower and place on map
	 * @param clone
	 * @param towerPlacement
	 * @param mScaler
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
		this.draw = true;
		this.x = towerPlacement.x;
		this.y = towerPlacement.y;
		this.relatedShot.resetShotCordinates();//Same location of Shot as midpoint of Tower
		this.relatedShot.draw = false;
	}

	//////////////////////////////////////////////
	// Getter for tower
	//////////////////////////////////////////////
	
	/**
	 * Given a tower this method will create a new tower with the same
	 * variables as the given one
	 * @return
	 */
	public int getTowerTypeId() { return towerTypeId; }
	
	/**
	 * Return range of this tower
	 * @return
	 */
	public float getRange() { return this.range; }
	
	/**
	 * Return minimum damage
	 * @return
	 */
	public float getMinDamage() { return this.minDamage; }
	
	/**
	 * Return maximum damage
	 * @return
	 */
	public float getMaxDamage() { return this.maxDamage; }
	
	/**
	 * Return title of tower
	 * @return
	 */
	public String getTitle() { return this.title; }

	/**
	 * Returns the cost of this tower
	 * @return price
	 */	
	public int getPrice() { return price; }

	public int upgradeCost(int i) {
		// TODO Auto-generated method stub
		return 0;
	}

	public void upgrade(int i) {
		// TODO Auto-generated method stub
		
	}

	public int getResellPrice() {
		return this.resellPrice;
	}
	
}