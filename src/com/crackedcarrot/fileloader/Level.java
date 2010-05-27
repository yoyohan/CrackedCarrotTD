package com.crackedcarrot.fileloader;

import com.crackedcarrot.Creature;

// Class contains level information. Number of creatures creature type etc.
public class Level extends Creature {
	public int nbrCreatures;
	public String creepTitle;
	
	//Constructor
    public Level(int resourceId){
    	//Change subtype and numer of frames during load
		super(resourceId, 0, null, null, null, null, 0, null);
	}
    public float getHealth() {
		return health;
	}

	// If a creature is have a special ability we also want him to change color
	public void setCreatureSpecials(boolean fast, boolean fireResistant,boolean frostResistant,boolean poisonResistant) {
		this.creatureFrostResistant = frostResistant;
		this.creatureFireResistant = fireResistant;
		this.creaturePoisonResistant = poisonResistant;
		this.creatureFast = fast;
		
		this.rDefault = 1;
		this.gDefault = 1;
		this.bDefault = 1;

		if (poisonResistant && !frostResistant && !fireResistant) {
			this.rDefault = 0.7f;
			this.bDefault = 0.7f;
		}
		if (!poisonResistant && frostResistant && !fireResistant) {
			this.rDefault = 0.7f;
			this.gDefault = 0.7f;
		}
		if (!poisonResistant && !frostResistant && fireResistant) {
			this.gDefault = 0.7f;
			this.bDefault = 0.7f;
		}
		if (poisonResistant && frostResistant && !fireResistant) {
			this.rDefault = 0.7f;
		}
		if (poisonResistant && !frostResistant && fireResistant) {
			this.bDefault = 0.7f;
		}
		if (!poisonResistant && frostResistant && fireResistant) {
			this.gDefault = 0.7f;
		}
	}
	
	public void cloneCreature(Creature clone) {
		clone.setResourceId(this.getResourceId());
		clone.setDeadResourceId(this.getDeadResourceId());
		clone.setDeadTexture(this.getDeadTexture());
		clone.creatureFast = this.creatureFast;
		clone.creatureFireResistant = this.creatureFireResistant;
		clone.creatureFrostResistant = this.creatureFrostResistant;
		clone.creaturePoisonResistant = this.creaturePoisonResistant;
		clone.setHealth(this.health);
		clone.setVelocity(this.velocity);
		clone.setWidth(this.getWidth());
		clone.setHeight(this.getHeight());
		clone.setGoldValue(this.goldValue);
		clone.draw = false;
		clone.opacity = 1;
		clone.creatureFrozenTime = 0;
		clone.creaturePoisonTime = 0;
		clone.setRGB(this.rDefault,this.gDefault,this.bDefault);
		clone.setDead(false);
		clone.setAllDead(false);
		clone.scale = this.scale;
		clone.setDisplayResourceId(this.getDisplayResourceId());
		clone.setAnimationTime(clone.creatureFast);
		clone.moveToWaypoint(0);
		clone.setSpawnPoint();
		clone.setNextWayPoint(1);
	}
}