package com.crackedcarrot.fileloader;

import com.crackedcarrot.Creature;

// Class contains level information. Number of creatures creature type etc.
public class Level extends Creature {
	public int nbrCreatures;
	public String creepTitle;
	
	//Constructor
    public Level(int resourceId){
		super(resourceId);
	}
    public float getHealth() {
		return health;
	}
	
	public void setCreatureFast(boolean creatureFast) {
		this.creatureFast = creatureFast;
	}

	// If a creature is frostresistan we also want him to change color
	public void setCreatureResistant(boolean fireResistant,boolean frostResistant,boolean poisonResistant) {
		this.creatureFrostResistant = frostResistant;
		this.creatureFireResistant = fireResistant;
		this.creaturePoisonResistant = poisonResistant;
		
		this.rDefault = 1;
		this.bDefault = 1;
		this.gDefault = 1;
		
		if (poisonResistant) {
			this.rDefault = 0.7f;
			this.bDefault = 0.7f;
		}
		if (frostResistant) {
			this.rDefault = 0.7f;
			this.gDefault = 0.7f;
		}
		if (fireResistant) {
			this.gDefault = 0.7f;
			this.bDefault = 0.7f;
		}		

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
		clone.setRGB(this.rDefault,this.gDefault,this.bDefault);
		clone.setAllDead(false);
	}	
}