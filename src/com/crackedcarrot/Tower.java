package com.crackedcarrot;

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
	// The current damage for a tower
	public int damage;

	
	public Tower(int resourceId){
		super(resourceId);
	}
	
	public void createDamage(){
		this.damage = DAMFACTOR^(this.level);
	}
	
}