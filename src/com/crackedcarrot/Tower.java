package com.crackedcarrot;

/**
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
	// Cooldown between shots
	public int cooldown;
	// If the tower have any special abilitys
	public int specialAbility;
	// The first linked update for this tower
	public int upgrade1;
	// The second linked update for this tower
	public int upgrade2;
	
	public Tower(int resourceId){
		super(resourceId);
	}
}