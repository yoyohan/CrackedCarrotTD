package com.crackedcarrot;

import java.util.Random;

/**
* Class defining a tower in the game
*/
public class Tower extends Sprite {
	private SoundManager soundManager;
	private Creature[] mCreatures;
	
	//different towertypes
	private final int PROJECTILEAOE = 1;
	private final int PUREAOE = 2;
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
    
	public Tower(int resourceId, Creature[] mCreatures, SoundManager soundManager){
		super(resourceId);
		this.soundManager = soundManager;
		this.mCreatures = mCreatures;
		rand = new Random();
	}
	public Tower(int resourceId){
		super(resourceId);
		rand = new Random();
	}
	
	/**
	 * Calculates special damage
	 */
	private float specialDamage(Creature tmpCreature) {
		// if this is not the first tower that is hit. we dont 
		// want to make maximum damage
		boolean aoeTower = false;
		float damageFactor = 1;
		
		if (tmpCreature == null) {
			tmpCreature = targetCreature;
			aoeTower = true;
		}
		
		// Target is frost resistant?
		if (!tmpCreature.creatureFrostResistant && this.hasFrostDamage) {
			if (aoeTower)
				tmpCreature.creatureFrozenTime = (this.frostTime/2);
			else
				tmpCreature.creatureFrozenTime = this.frostTime;
		}
		
		// Target is fire resistant?
		if (tmpCreature.creatureFireResistant && this.hasFireDamage)
			damageFactor = 0.4f;
		else damageFactor = 1;
		
		// Target is poison resistant?
		if (!tmpCreature.creaturePoisonResistant && this.hasPoisonDamage) {
			// If target is already affected by poison damage we dont want to remove the previous buff
			float tmpED = 0;
			int tmpPD = tmpCreature.creaturePoisonDamage;
			float tmpPT = tmpCreature.creaturePoisonTime;
		
			if (aoeTower) {
				if (tmpPD > 0 && tmpPT > 0)
					tmpED = (tmpPD * tmpPT) / (this.poisonTime/2);
				tmpCreature.creaturePoisonTime = (this.poisonTime/2);
				tmpCreature.creaturePoisonDamage = (int)(this.poisonDamage + tmpED);
			} else {
				if (tmpPD > 0 && tmpPT > 0)
					tmpED = (tmpPD * tmpPT) / this.poisonTime;
				tmpCreature.creaturePoisonTime = this.poisonTime;
				tmpCreature.creaturePoisonDamage = (int)(this.poisonDamage + tmpED);
			}
		}
		
		return damageFactor;
	}
	
	
	/**
	 * Method that calculates the damage for a specific tower
	 * depending on the upgrade level and a random integer
	 * so the damage wont be predictable during game play
	 */
	private void createProjectileDamage(){
		float damageFactor = specialDamage(null);
		float randomInt = (rand.nextInt(this.maxDamage-this.minDamage) + this.minDamage) * damageFactor;
		targetCreature.damage(randomInt);
	}

	/**
	 * Method that calculates the damage for a specific tower
	 * depending on the upgrade level and a random integer
	 * so the damage wont be predictable during game play
	 */
	private void createProjectileAOEDamage(int nbrCreatures) {
		for(int i = 0;i < nbrCreatures; i++ ){
			if (mCreatures[i] != targetCreature) {

				if(mCreatures[i].draw == true && mCreatures[i].health > 0){ // Is the creature still alive?
					double distance = Math.sqrt(Math.pow((targetCreature.x - mCreatures[i].x),2) + Math.pow((targetCreature.y - mCreatures[i].y),2));
					if(distance < this.rangeAOE){ // Is the creature within tower range?
						float damageFactor = specialDamage(mCreatures[i]);
						mCreatures[i].damage(this.aoeDamage*damageFactor);
					}
				}
			}
		}
	}
	
	/**
	 * Method that calculates AOE damage for a specific tower
	 * depending on the upgrade level and a random integer
	 * so the damage wont be predictable during game play
	 * This method is only used by towers with direct aoe damage.
	 * Not to be confused with towers that have projectiledamage
	 */
	private boolean createPureAOEDamage(int nbrCreatures){
		int nbrOfHits = 0;
		for(int i = 0;i < nbrCreatures; i++ ){
			if(mCreatures[i].draw == true && mCreatures[i].health > 0){ // Is the creature still alive?
				double distance = Math.sqrt(Math.pow((this.x - mCreatures[i].x),2) + Math.pow((this.y - mCreatures[i].y),2));
				if(distance < this.range){ 
					float damageFactor = specialDamage(mCreatures[i]);
					float randomInt = (rand.nextInt(this.maxDamage-this.minDamage) + this.minDamage) * damageFactor;
					mCreatures[i].damage(randomInt);
					nbrOfHits++;
				}
			}
		}
		if (nbrOfHits > 0) return true;
		else return false;
	}
	
	/**
	 * Method that tracks a creature. It iterates over a list of creatures and picks
	 * the first creature in the list that is within the range of the tower 
	 * @param null 
	 */
	private void trackEnemy(int nbrCreatures){
		targetCreature = null;
		double lastCreatureDistance = Double.MAX_VALUE;
		
		for(int i = 0;i < nbrCreatures; i++ ){
			if(mCreatures[i].draw == true && mCreatures[i].health > 0){ // Is the creature still alive?
				double distance = Math.sqrt(Math.pow((this.x - mCreatures[i].x),2) + Math.pow((this.y - mCreatures[i].y),2));
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
	}

	/**
	 * Method that places the tower related shot back to 
	 * the start position
	 */
	public void resetShotCordinates() {
		relatedShot.x = x + getWidth()/2;
		relatedShot.y = y + getHeight()/2;	
	}

	/**
	 * Given all variable this method will create a exaxt copy of
	 * another tower
	 */
	public void cloneTower(	
				int resourceId,
				int towerType,
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
				float relatedShotWidth,
				float relatedShotHeight
				){

			this.setResourceId(resourceId);
			this.towerType = towerType;
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
			this.relatedShot.setWidth(relatedShotWidth);
			this.relatedShot.setHeight(relatedShotHeight);

	}

	/**
	 * Given a tower this method will create a new tower with the same
	 * variables as the given one
	 */
	public void createTower(Tower clone, Coords towerPlacement) {
		//Use the textureNames that we preloaded into the towerTypes at startup
		this.cloneTower(
				clone.getResourceId(),
				clone.towerType,
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
		    	clone.relatedShot.getWidth(),
		    	clone.relatedShot.getHeight()
			);
		
			// TODO: This cannot be in the cloneTower function because
			// then it breaks with TowerLoader.java
		this.setTextureName(clone.getTextureName());
		
		this.draw = true;
		this.x = towerPlacement.x;
		this.y = towerPlacement.y;
		this.resetShotCordinates();//Same location of Shot as midpoint of Tower
		this.relatedShot.draw = false;
	}

	public void towerKillCreature(float timeDeltaSeconds, int gameSpeed, int nbrCreatures) {
		// Decrease the coolDown variable and check if it has reached zero
		this.tmpCoolDown = this.tmpCoolDown - (timeDeltaSeconds * gameSpeed);

		// This code is used to display the AOE for a pure AOE shot. This is for testing.
		if (this.towerType == this.PUREAOE) {
			
			if (this.tmpCoolDown <= this.coolDown/2) {
				this.relatedShot.draw = false;
			}
			else {
				this.relatedShot.scale = (coolDown/2)/tmpCoolDown;
			}
			
			if (this.tmpCoolDown <= 0) {
				if (this.createPureAOEDamage(nbrCreatures)) {
					soundManager.playSound(0);
					this.tmpCoolDown = this.coolDown;
					this.relatedShot.draw = true;
		    		this.relatedShot.x = this.x + this.getWidth()/2 - this.relatedShot.getWidth()/2;
		    		this.relatedShot.y = this.y + this.getHeight()/2 - this.relatedShot.getHeight()/2;
				}
			}
		}
		// This code is for towers that use projectile damage.
		else {
			if (!this.relatedShot.draw && (this.tmpCoolDown <= 0)) {
    			// If the tower/shot is existing start calculations.
    			this.trackEnemy(nbrCreatures);
    			if (this.targetCreature != null) {
    					// play shot1.mp3
    					soundManager.playSound(0);
    					this.tmpCoolDown = this.coolDown;
    					this.relatedShot.draw = true;
    			}
			}
			// if the creature is still alive or have not reached the goal
    		if (this.towerType != this.PUREAOE && this.relatedShot.draw && this.targetCreature.draw && this.targetCreature.health > 0) {
    			Creature targetCreature = this.targetCreature;

    			float yDistance = (targetCreature.y+(targetCreature.getHeight()/2)) - this.relatedShot.y;
    			float xDistance = (targetCreature.x+(targetCreature.getWidth()/2)) - this.relatedShot.x;
    			double xyMovement = (this.velocity * timeDeltaSeconds * gameSpeed);
    			
    			if ((Math.abs(yDistance) <= xyMovement) && (Math.abs(xDistance) <= xyMovement)) {
		    		this.relatedShot.draw = false;
		    		this.resetShotCordinates();
		    		//More advanced way of implementing damage
		    		this.createProjectileDamage();
		    		//IF A CANNONTOWER FIRES A SHOT we also have to damage surrounding creatures
		    		if (this.towerType == this.PROJECTILEAOE){
				    	this.createProjectileAOEDamage(nbrCreatures);
		    		}
/*		    		if (targetCreature.getHealth() <= 0) {
		    			targetCreature.die();
		    		}*/
    			}
    			else {
        			double radian = Math.atan2(yDistance, xDistance);
        			this.relatedShot.x += Math.cos(radian) * xyMovement;
        			this.relatedShot.y += Math.sin(radian) * xyMovement;
    			}
			}
    		else if (this.towerType != this.PUREAOE) {
    			this.relatedShot.draw = false;
	    		this.resetShotCordinates();
    		}
		}
	}
	
	public int getPrice() {
		return price;
	}
	
}