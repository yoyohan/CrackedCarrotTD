package com.crackedcarrot;

import java.util.Random;

/**
* Class defining a tower in the game
*/
public class Tower extends Sprite{
	// The current level of a tower
	public int level;
	// The current range of a tower
	public float range;
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
	// Speed of the shots
	public int velocity;
	// If the tower have any special abilitys
	public int specialAbility;
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
	 * Method that calculates the damage for a specific tower
	 * depending on the upgrade level and a random integer
	 * so the damage wont be predictable during game play
	 * @return a random integer between a towers min- and max damage
	 */
	public int createDamage(){
		int randomInt = rand.nextInt((this.maxDamage-this.minDamage)) + this.minDamage;
		return randomInt;
	}

	/**
	 * Method that tracks a creature. It iterates over a list of creatures and picks
	 * the first creature in the list that is within the range of the tower 
	 * @param null 
	 */
	public void trackEnemy(Creature[] cres, int nbrCreatures){
		targetCreature = null;
		
		for(int i = 0;i < nbrCreatures; i++ ){
			if(cres[i].draw == true && cres[i].opacity == 1.0f){ // Is the creature still alive?
				double distance = Math.sqrt(Math.pow((this.x - cres[i].x),2) + Math.pow((this.y - cres[i].y),2));
				if(distance < range){ // Is the creature within tower range?
					targetCreature = cres[i];
					return;
				}
			}
		}
	}
	
	public void resetShotCordinates() {
		relatedShot.x = x + width/2;
		relatedShot.y = y + height/2;	
	}

}