package com.crackedcarrot;

import java.util.Random;

/**
* Class defining a tower in the game
*/
public class Tower extends Sprite{
	//different towertypes
	public final int PROJECTILE = 0;
	public final int PROJECTILEAOE = 1;
	public final int PUREAOE = 2;
	//towertype
	public int towerType;
	// The current level of a tower
	public int level;
	// The current range of a tower
	public float range;
	// The current AOE range of a tower
	public float rangeAOE;
	// Tower title
	public String title;
	// Price for the tower
	public int price;
	// Resell value if the tower is sold
	public int resellPrice;
	// Minimum damage that this tower can inflict
	public int minDamage;
	// Maximum damage that this tower can inflict
	public int maxDamage;
	// AOE damage
	public int aoeDamage;
	// Speed of the shots
	public int velocity;
	// If the tower have frost damage
	public boolean hasFrostDamage;
	// If the tower have frost damage. How long?
	public int frostTime;
	// If the tower have fire damage
	public boolean hasFireDamage;
	// If the tower have poison damage
	public boolean hasPoisonDamage;
	// If the tower have poison damage, how mutch?
	public int poisonDamage;
	// If the tower have poison damage, how long?
	public int poisonTime;
	// The first linked update for this tower
	public int upgrade1;
	// The second linked update for this tower
	public int upgrade2;
	// The type of shot related to this tower
	public Shot relatedShot;
    // The time existing between each fired shot
    public float coolDown;
    // The temporary variable representing the time existing between each fired shot
    public float tmpCoolDown;
    // The current target creature
    public Creature targetCreature;
	// Random used to calculate damage
    private Random rand;
    
	public Tower(int resourceId){
		super(resourceId);
		rand = new Random();
	}

	/**
	 * Calculates special damage
	 */
	public double specialDamage(Creature tmpCreature) {
		// if this is not the first tower that is hit. we dont 
		// want to make maximum damage
		boolean aoeTower = false;
		double damageFactor = 1;
		
		if (tmpCreature == null) {
			tmpCreature = targetCreature;
			aoeTower = true;
		}
		
		// Target is frost resistant?
		if (!tmpCreature.creatureFrostResistant && this.hasFrostDamage) {
			if (aoeTower)
				tmpCreature.creatureFrozenTime = this.frostTime/2;
			else 
				tmpCreature.creatureFrozenTime = this.frostTime;
		}
		
		// Target is fire resistant?
		if (tmpCreature.creatureFireResistant && this.hasFireDamage)
			damageFactor = 0.4;
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
				tmpCreature.creaturePoisonTime = this.poisonTime/2;
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
	public void createProjectileDamage(){
		double damageFactor = specialDamage(null);
		int randomInt = (int)((rand.nextInt(this.maxDamage-this.minDamage) + this.minDamage) * damageFactor);
		targetCreature.health = targetCreature.health - randomInt;
	}

	/**
	 * Method that calculates the damage for a specific tower
	 * depending on the upgrade level and a random integer
	 * so the damage wont be predictable during game play
	 */
	public void createProjectileAOEDamage(Creature[] mCreatures,int nbrCreatures) {
		for(int i = 0;i < nbrCreatures; i++ ){
			if (mCreatures[i] != targetCreature) {

				if(mCreatures[i].draw == true && mCreatures[i].opacity == 1.0f){ // Is the creature still alive?
					double distance = Math.sqrt(Math.pow((targetCreature.x - mCreatures[i].x),2) + Math.pow((targetCreature.y - mCreatures[i].y),2));
					if(distance < this.rangeAOE){ // Is the creature within tower range?
						double damageFactor = specialDamage(mCreatures[i]);
						int thisDamage = (int)(this.aoeDamage*damageFactor);
						mCreatures[i].health = mCreatures[i].health - thisDamage;
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
	public boolean createPureAOEDamage(Creature[] cres, int nbrCreatures){
		int nbrOfHits = 0;
		for(int i = 0;i < nbrCreatures; i++ ){
			if(cres[i].draw == true && cres[i].opacity == 1.0f){ // Is the creature still alive?
				double distance = Math.sqrt(Math.pow((this.x - cres[i].x),2) + Math.pow((this.y - cres[i].y),2));
				if(distance < this.range){ 
					double damageFactor = specialDamage(cres[i]);
					int randomInt = (int)((rand.nextInt(this.maxDamage-this.minDamage) + this.minDamage) * damageFactor);
					cres[i].health = cres[i].health - randomInt;
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
	public void trackEnemy(Creature[] cres, int nbrCreatures){
		targetCreature = null;
		double lastCreatureDistance = Double.MAX_VALUE;
		
		for(int i = 0;i < nbrCreatures; i++ ){
			if(cres[i].draw == true && cres[i].opacity == 1.0f){ // Is the creature still alive?
				double distance = Math.sqrt(Math.pow((this.x - cres[i].x),2) + Math.pow((this.y - cres[i].y),2));
				if(distance < range){ // Is the creature within tower range?
					if (targetCreature == null) 
						targetCreature = cres[i];
					else if (lastCreatureDistance > distance) {
						targetCreature = cres[i];
						lastCreatureDistance = distance;
					}
				}
			}
		}
	}
	
	public void resetShotCordinates() {
		relatedShot.x = x + width/2;
		relatedShot.y = y + height/2;	
	}


}