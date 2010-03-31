package com.crackedcarrot.fileloader;

import com.crackedcarrot.Creature;

// Class contains level information. Number of creatures creature type etc.
public class Level extends Creature {
	public int nbrCreatures;
	public String creepTitle;
	
	//Constructor
    public Level(int resourceId){
    	//Change subtype and numer of frames during load
		super(resourceId, 0,1);
	}
    public float getHealth() {
		return health;
	}
	
	public void setCreatureFast(boolean creatureFast) {
		this.creatureFast = creatureFast;
	}

	public void setCreatureFrostResistant(boolean creatureFrostResistant) {
		this.creatureFrostResistant = creatureFrostResistant;
	}
	
	public void setCreatureFireResistant(boolean creatureFireResistant) {
		this.creatureFireResistant = creatureFireResistant;
	}

	public void setCreaturePoisonResistant(boolean creaturePoisonResistant) {
		this.creaturePoisonResistant = creaturePoisonResistant;
	}

	public void cloneCreature(Creature clone) {
		clone.setResourceId(this.getResourceId());
		clone.setDeadResourceId(this.getDeadResourceId());
		clone.setDeadTextureName(this.getDeadTextureName());
		clone.creatureFast = this.creatureFast;
		clone.creatureFireResistant = this.creatureFireResistant;
		clone.creatureFrostResistant = this.creatureFrostResistant;
		clone.creaturePoisonResistant = this.creaturePoisonResistant;
		clone.moveToWaypoint(0);
		clone.setHealth(this.health);
		clone.setNextWayPoint(1);
		clone.setVelocity(this.velocity);
		clone.setWidth(this.getWidth());
		clone.setHeight(this.getHeight());
		clone.setGoldValue(this.goldValue);
		clone.draw = false;
		clone.opacity = 1;
		clone.creatureFrozenTime = 0;
		clone.creaturePoisonTime = 0;
	}	
}