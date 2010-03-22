package com.crackedcarrot.fileloader;

import com.crackedcarrot.Creature;

// Class contains level information. Number of creatures creature type etc.
public class Level extends Creature {
	public int nbrCreatures;
	
	//Constructor
    public Level(int resourceId){
		super(resourceId);
	}
    public int getHealth() {
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
		clone.setDeadResourceId(this.mDeadResourceId);
		clone.setDeadTextureName(this.mDeadTextureName);
		clone.creatureFast = this.creatureFast;
		clone.creatureFireResistant = this.creatureFireResistant;
		clone.creatureFrostResistant = this.creatureFrostResistant;
		clone.creaturePoisonResistant = this.creaturePoisonResistant;
		clone.moveToWaypoint(0);
		clone.setHealth(this.health);
		clone.setNextWayPoint(this.getNextWayPoint());
		clone.setVelocity(this.velocity);
		clone.setWidth(this.width);
		clone.setHeight(this.height);
		clone.setGoldValue(this.goldValue);
		clone.draw = false;
		clone.opacity = 1;
	}	
}