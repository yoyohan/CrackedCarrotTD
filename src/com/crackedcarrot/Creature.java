package com.crackedcarrot;

/**
*
* Class defining creature in the game
*/
public class Creature extends Sprite{
	
    
    // A creatures health
    public int health;
    // The next way point for a given creature
    public int nextWayPoint;
    // The speed of the creature
    public float velocity;
    // The different directions for a creature
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    // The current direction of the creature
    public int direction;
	
    
	public Creature(int resourceId){
		super(resourceId);
		nextWayPoint = 0;
	}
	
	public void updateWayPoint (){
		nextWayPoint++;
	}
	
}

