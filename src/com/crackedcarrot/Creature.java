package com.crackedcarrot;

/**
*
* Class defining creature in the game
*/
public class Creature extends Sprite{
	
    
    // A creatures health
    int health;
    // The next way point for a given creature
    public int nextWayPoint;
    // The speed of the creature
    public float velocity;
	
    
	public Creature(int resourceId){
		super(resourceId);
		nextWayPoint = 0;
	}
	
	public void updateWayPoint (){
		nextWayPoint++;
	}
	
}

