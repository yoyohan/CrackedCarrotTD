package com.crackedcarrot;

import java.util.Random;

import com.crackedcarrot.textures.TextureData;
/**
* Class defining a creature in the game
*/
public class Creature extends Sprite{
	//Player Ref
	private Player player;
	//SoundManager ref
	private SoundManager soundManager;
	// Creatures needs to know gridlocation
	//Waypoints for this creature
	private Coords[] wayP;
	//Variable keeping all creatures from going in a straight line
	private int xoffset;
	//Variable to move creature up and down
	private int yoffset;
	// A creatures health
    protected float health;
    // The next way point for a given creature
    private int nextWayPoint;
    // SPRITE DEAD RESOURCE
    private int mDeadResourceId;
    // SPRITE DEAD 
	private TextureData mDeadTextureData;
	// Display image
	private int mDisplayResourceId;
    // The speed of the creature
    protected float velocity;
    // Delay before spawning the creature to the map
    private float spawndelay;
    // How much gold this creature gives when it's killed.
    protected int goldValue;
    //Ref to gameloop that runs this creature.
    private GameLoop GL;
    //Is this creature dead USED BY ALBIN
    private boolean dead;
    // All creatures are dead:
    private boolean allDead = false;
    // Creature special abilty
    public boolean creatureFast;
    public boolean creatureFrostResistant;
    public boolean creatureFireResistant;
    public boolean creaturePoisonResistant;
    // Creature affected by some kind of tower
    public float creatureFrozenTime;
    public float creatureFrozenAmount = 1;
    public float creaturePoisonTime;
    public float creaturePoisonDamage;
	// This is the base rgb colors for this creature
	protected float rDefault = 1;
	protected float gDefault = 1;
	protected float bDefault = 1;
	// Creature id
	protected int creatureIndex;
	// Variables used for animation
    private Random rand;
	protected float animationTime;
	protected float tmpAnimationTime;
	// Next waypoint location
	private float wayPointX;
	private float wayPointY;
	private float spawnWayPointX;
	private float spawnWayPointY;
	//Shows how many laps the creature has currentle completed
	public int mapLap;
	
	//Tracker
	// Each creature is a double list component 
	public Creature nextCreature;
	public Creature previousCreauture;
	private int trackerX = 0;
	private int trackerY = 0;
	private Tracker tracker;
	
	//How mutch a creature will damage the player
	private int damagePerCreep;
	
	
    /**
     * Constructor. When a new creature is definced we will also
     * send texturenumber, creaturesubtype, playerdata, soundmanager,
     * waypoints, gameloop(for message parsing), creature index.
     * @param resourceId
     * @param type
     * @param player
     * @param soundMan
     * @param wayP
     * @param loop
     * @param creatureIndex
     */
	public Creature(int resourceId, 
					int type, 
					Player player, 
					SoundManager soundMan, 
					Coords[] wayP, 
					GameLoop loop, 
					int creatureIndex,
					Tracker tracker
					){
		
		super(resourceId, CREATURE, type);
		this.draw = false;
		this.dead = false;
		this.player = player;
		this.soundManager = soundMan;
		this.wayP = wayP;
		this.GL = loop;
		this.creatureIndex = creatureIndex;
		rand = new Random();
		double randomDouble = (rand.nextDouble());
		tmpAnimationTime = (float)randomDouble/2;
		this.tracker = tracker;
	}

	/**
	 * Method used to applie damage to this creature.
	 * Will also send the amount of damage inflicted to 
	 * the gameloop.
	 * @param dmg
	 */
	public void damage(float dmg, int sound){
		if (health > 0) {
			dmg = health >= dmg ? dmg : health; 
			health -= dmg;
			GL.updateCreatureProgress(dmg);
			if(health <= 0){
				die();
				soundManager.playSoundRandomDIE();
			}
			else if (sound != -1)
				soundManager.playSound(sound);
		}
	}
	
	/**
	 * Calculate movement animation
	 * @param timeDeltaSeconds
	 */
	private void animate(float timeDeltaSeconds) {
		this.tmpAnimationTime -= timeDeltaSeconds;
		if (tmpAnimationTime <= 0f) {
			this.tmpAnimationTime = this.animationTime;
			this.animate();
		}
	}

	/**
	 * Main function of the creature. Used by the gameloop 
	 * @param timeDeltaSeconds
	 */
	public void update(float timeDeltaSeconds){
		//Time to spawn.
		if (spawnWayPointX == this.x && spawnWayPointY == this.y) {
			spawndelay -= timeDeltaSeconds;
			if (spawndelay <= 0) {
				draw = true;
				spawnWayPointX = 0;
				spawnWayPointY = 0;

				//Prepare tracker for game launch
				Coords tmp = this.GL.mScaler.getGridXandY((int)(x*this.scale),(int)(y*this.scale));
	    		tmp.x++;
	    		tmp.y++;
				TrackerData tmpTrac = tracker.getTrackerData(tmp.x, tmp.y);
    			tmpTrac.addCreatureToList(this);
    			trackerX = tmp.x;
    			trackerY = tmp.y;
			}
		}
		//If still alive move the creature.
		float movement = 0;
		if (draw && health > 0) {
			// If the creature is living calculate tower effects.
			movement = applyEffects(timeDeltaSeconds);
			if (health > 0) {
				move(movement);
				animate(timeDeltaSeconds);
			}
		}
	    // Creature is dead and fading...
		else if (allDead) {
			fade(timeDeltaSeconds/3);
		}
	}
	
	/**
	 * Calculate movement depending on
	 * speed and other effects.
	 * @param timeDeltaSeconds
	 */
	private void move(float movement){
		float yDistance = this.wayPointY - this.y+yoffset;
		float xDistance = this.wayPointX - this.x+xoffset;
			
		if ((Math.abs(yDistance) <= movement) && (Math.abs(xDistance) <= movement)) {
			// We have reached our destination!!!
    		updateWayPoint();
		}
		else {
    		double radian = Math.atan2(yDistance, xDistance);
    		this.x += (Math.cos(radian) * movement);
    		this.y += (Math.sin(radian) * movement);
    		
    		// Update tracker
    		Coords tmp = this.GL.mScaler.getGridXandY((int)(x*this.scale),(int)(y*this.scale));
    		tmp.x++;
    		tmp.y++;
    		if (trackerX != tmp.x || trackerY != tmp.y) {
    			TrackerData tmpTrac = tracker.getTrackerData(trackerX, trackerY);
    			tmpTrac.removeCreatureFromList(this);
    			tmpTrac = tracker.getTrackerData(tmp.x, tmp.y);
    			tmpTrac.addCreatureToList(this);
    			trackerX = tmp.x;
    			trackerY = tmp.y;
    		}
    			
    		
		}
	}

	/**
	 * This method i used when a creature has reached a waypoint
	 * either the next avaible waypoint will be selected or the
	 * player will loose a life.
	 */
	private void updateWayPoint (){
    	// Creature has reached is destination without being killed
    	if (getNextWayPoint() +1 >= wayP.length){
    		score();
    		moveToWaypoint(0);
    		setNextWayPoint(1);
    		this.mapLap++;
    	}
    	else { 		
    		setNextWayPoint(getNextWayPoint() + 1);
    	}
    }

	/**
	 * When a creature is killed by a tower this function
	 * will be called.
	 */
	private void die() {
		this.dead = true;
		setCurrentTexture(this.mDeadTextureData);
		this.resetRGB();
		player.moneyFunction(this.goldValue);
		player.scoreFunction(this.goldValue);
		GL.updateCurrency();
		//we dont remove the creature from the gameloop just yet
		//that is done when it has faded completely, see the fade method.
		GL.creatureDiesOnMap(1);
		// Remove creature from tracker
		TrackerData tmpTrac = tracker.getTrackerData(trackerX, trackerY);
		tmpTrac.removeCreatureFromList(this);
	}
	
	/**
	 * Method to check if creature is affected by any tower effects
	 * For example if creature is hit by a frost bolt we want to make
	 * the creature walk slower and turn blue.
	 * @param timeDeltaSeconds
	 * @return movement speed
	 */
	private float applyEffects(float timeDeltaSeconds){
		float damage = 0;
		float tmpR = this.rDefault;
		float tmpG = this.gDefault;
		float tmpB = this.bDefault;
		float tmpRGB = 1;
		
		float slowAffected = 1;
		if (creatureFrozenTime > 0) {
			slowAffected = creatureFrozenAmount;
    		creatureFrozenTime = creatureFrozenTime - timeDeltaSeconds;
    		tmpRGB = creatureFrozenTime <= 1f ? 1-0.3f*creatureFrozenTime : 0.7f;
    		tmpR = tmpR*tmpRGB;
    		tmpG = tmpG*tmpRGB;
		} else {
			creatureFrozenAmount = 1;
		}
		// If creature has been shot by a poison tower we slowly reduce creature health
		if (creaturePoisonTime > 0) {
			creaturePoisonTime = creaturePoisonTime - (timeDeltaSeconds);
			damage = timeDeltaSeconds * creaturePoisonDamage;
			tmpRGB = creaturePoisonTime <= 1f ? 1-0.3f*creaturePoisonTime : 0.7f;
	  		tmpR = tmpR*tmpRGB;
	  		tmpB = tmpG*tmpRGB;
		}
  
		if (creatureFrozenTime > 0 && creaturePoisonTime > 0) {
			this.r = 0;
		}
		
		float movement = (velocity * (timeDeltaSeconds/this.scale)) * slowAffected;
		
		this.r = tmpR;
		this.g = tmpG;
		this.b = tmpB;		

		if (damage > 0)
			damage(damage,-1);

		return movement;
	}
	
	/**
	 * Reduce opacity until creature is invisible and
	 * then notify gameloop.
	 * @param reduceOpacity
	 */
	private void fade(float reduceOpacity){
		this.opacity -= reduceOpacity;
		if (opacity <= 0.0f) {
			draw = false;
			//The creature is now completely gone from the map, tell the loop.
			GL.creatureLeavesMAP(1);
		}
	}
	
	/**
	 * A creature has reached the last waypoint and therefore we
	 * will remove one life from player
	 */
	private void score(){
		player.damage(damagePerCreep);
		soundManager.playSoundRandomScore();
		GL.updatePlayerHealth();
	}

	/**
	 * When we want to teleport a creature to a specific waypoint. Used when creature needs 
	 * to restart
	 * @param p
	 */
	public void moveToWaypoint(int p){
		this.x = this.getWayX(p);
		this.y = this.getWayY(p);
	}

	/**
	 * Affect this creature if possible with frost for the submitted time
	 * @param time
	 * @param amount of frost
	 */
	public void affectWithFrost(int time, float frostAmount) {
		if (!this.creatureFrostResistant) {
			this.creatureFrozenTime = time;
			if (this.creatureFrozenAmount > frostAmount)
				this.creatureFrozenAmount = frostAmount;
		}
	}

	/**
	 * Affect this creature if possible with poisondamage for the submitted time
	 * @param poisontime
	 * @param poisonDamage
	 */
	public void affectWithPoison(int poisonTime, float poisonDamage) {
		if (!this.creaturePoisonResistant) {
			if ( this.creaturePoisonDamage > 0 && this.creaturePoisonTime > 0 )
				this.creaturePoisonDamage = poisonDamage + ((this.creaturePoisonDamage * this.creaturePoisonTime) / poisonTime);
			else
				this.creaturePoisonDamage = poisonDamage;
			this.creaturePoisonTime = poisonTime;
		}
	}
	
	//////////////////////////////////////////////
	//Below are all setters for the creature class
	//////////////////////////////////////////////

	/**
	 * Setter for health variable
	 * @param health
	 */
	public void setHealth(float health){ this.health = health; }
	
	/**
	 * Setter for spawndelay. Time between each creature enters the map
	 * @param delay
	 */
	public void setSpawndelay(float delay) { this.spawndelay = delay; }
	
	/**
	 * Setter for texture of a dead creature
	 * @param mDeadResourceId
	 */
	public void setDeadResourceId(int mDeadResourceId) { this.mDeadResourceId = mDeadResourceId; }

	/**
	 * Setter for TextureData object for an creature.
	 * @param mDeadTexture
	 */
	public void setDeadTexture(TextureData mDeadTexture) { this.mDeadTextureData = mDeadTexture; }

	/**
	 * Setter for the gained gold when this creaute dies. 
	 * @param goldValue
	 */
	public void setGoldValue(int goldValue) { this.goldValue = goldValue; }

	/**
	 * Function to set speed for this creature
	 * @param velocity
	 */
	public void setVelocity(float velocity){ this.velocity = velocity; }

	/**
	 * This setters is needed to show creature between levels
	 * @param resID
	 */
	public void setDisplayResourceId(int resID) { this.mDisplayResourceId = resID; }

	/**
	 * Defines time for walkanimation of a creature depending on speed.
	 * @param fast
	 */
	public void setAnimationTime(boolean fast) {
		if (fast)
			this.animationTime = 0.22f;
		else
			this.animationTime = 0.3f;
	}

	/**
	 * This setter is used by the renderer when a creature dies
	 * @param dead
	 */
	public void setDead(boolean dead) { this.dead = dead; }
	
	/**
	 * Define the default rgb. When a creature is frost or poisonresistant
	 * the color of the creature will be different.
	 * @param rDefault
	 * @param gDefault
	 * @param bDefault
	 */
	public void setRGB(float rDefault, float gDefault, float bDefault) {
		this.rDefault = rDefault;
		this.gDefault = gDefault;
		this.bDefault = bDefault;
	}

	/**
	 * Needed to reset rgb to default value
	 */
	private void resetRGB() {
		this.r = this.rDefault;
		this.g = this.gDefault;
		this.b = this.bDefault;
	}

	/**
	 * To make the walking of creatures more intresting
	 * we have a this setter to change standard position
	 * of a creature
	 * @param xoffset
	 */
	public void setXOffset(int xoffset) { this.xoffset = xoffset; }

	/**
	 * To make the walking of creatures more intresting
	 * we have a this setter to change standard position
	 * of a creature
	 * @param yoffset
	 */
	public void setYOffset(int yoffset) { this.yoffset = yoffset; }
	
	/**
	 * When all creatures are dead we will set this
	 * varibale and then start fading the corpses away.
	 * @param allDead
	 */
	public void setAllDead(boolean allDead) { this.allDead = allDead; }
	
	/**
	 * Given a waypoint number we will set next target for this creature
	 * @param nextWayPoint
	 */
	public void setNextWayPoint(int nextWayPoint) {
		this.nextWayPoint = nextWayPoint;
		this.wayPointX = this.getWayX(nextWayPoint);
		this.wayPointY = this.getWayY(nextWayPoint);
	}

	/**
	 * This is the spawnpoint of a creature 
	 */
	public void setSpawnPoint() {
		this.spawnWayPointX = this.getWayX(0);
		this.spawnWayPointY = this.getWayY(0);
	}
	
	/**
	 * Set all waypoints this creature will travel through
	 * @param waypoints
	 */
	public void SetWayPoints(Coords[] waypoints){
		this.wayP = waypoints;
	}
	
	/**
	 * Set how mutch a creature will damage the player
	 * @param int
	 * 
	 */
	public void setDamagePerCreep(int damagePerCreep) {
		this.damagePerCreep = damagePerCreep;
	}

	////////////////////////////////
	// All getters
	////////////////////////////////
	
	/**
	 * Get resource id of dead texture
	 * @return resource id
	 */
	public int getDeadResourceId() { return mDeadResourceId; }

	/**
	 * Get resource id of display texture
	 * @return resource id
	 */
	public int getDisplayResourceId() {	return this.mDisplayResourceId;	}

	/**
	 * Get resource id of display texture
	 * @return waypoint number
	 */
	public int getNextWayPoint() { return nextWayPoint;	}

	/**
	 * @return TextureData object of dead creauture
	 */
	public TextureData getDeadTexture() { return mDeadTextureData; }

	/**
	 * Returns the x coordinat for the requested waypoint
	 * @param i
	 * @return x pos
	 */
	private float getWayX(int i) {
		float newsize = (this.getWidth()/2 - this.scale*this.getWidth()/2);
		float cen_x = wayP[i].getX() + newsize;
		return (cen_x/this.scale);
	}

	/**
	 * Returns the y coordinat for the requested waypoint
	 * @param i
	 * @return y pos
	 */
	private float getWayY(int i) {
		float newsize = (this.getHeight() - this.scale*this.getHeight());
		float cen_y = wayP[i].getY() + newsize;
		return (cen_y/this.scale);
	}

	/**
	 * When a tower asks for creature position this method will return the position
	 * of this creature in a way that the tower can use
	 * @return scaled x pos
	 */
	public float getScaledX() {
		float cen_x  = x + this.getWidth()/2;
		return (cen_x*this.scale);
	}

	/**
	 * When a tower asks for creature position this method will return the position
	 * of this creature in a way that the tower can use
	 * @return scaled y pos
	 */
	public float getScaledY() {
		float cen_y  = y + this.getHeight()/2;
		return (cen_y*this.scale);
	}
	/**
	 * Return how mutch this creature will do to a player
	 * @return int
	 */
	public int getDamagePerCreep() {
		return this.damagePerCreep;
	}
	/**
	 * Return if this creature is fast or not
	 * @return true if fast
	 */
	public boolean isCreatureFast() {
		return creatureFast;
	}
}