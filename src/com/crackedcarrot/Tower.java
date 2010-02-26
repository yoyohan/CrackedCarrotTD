package com.crackedcarrot;

import java.util.Random;

/**
*
* Class defining a tower in the game
*/
public class Tower extends Sprite{
	
	// The damage factor when calculating the damage
	public static final int DAMFACTOR = 10;
	// The current level of a tower
	public int level;
	// The current range of a tower
	public float range;
	// The type of Shot for a tower
	public Shot shot;
	// The current minimum damage for a tower
	public int minDamage;
	// The current maximum damage for a tower
	public int maxDamage;

	
	public Tower(int resourceId){
		super(resourceId);
	}
	
	/**
	 * Method that calculates the damage for a specific tower
	 * depending on the upgrade level and a random integer
	 * so the damage wont be predictable during game play
	 * @return a random integer between a towers min- and max damage
	 */
	public int createDamage(){
		Random rand = new Random();
		int randomInt = rand.nextInt((this.maxDamage-this.minDamage)) + this.minDamage;
		return randomInt;
	}
	
}