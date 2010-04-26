package com.crackedcarrot;

import java.util.Random;

import com.crackedcarrot.textures.TextureData;
/**
* Class defining creature in the game
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
    public float creaturePoisonTime;
    public int creaturePoisonDamage;
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
	
    
	public Creature(int resourceId, 
					int type, 
					int frames, 
					Player player, 
					SoundManager soundMan, 
					Coords[] wayP, 
					GameLoop loop, 
					int creatureIndex
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
	
	}
	
	//This is only used by the level constructor.
	public Creature(int resourceId, int type){
		super(resourceId, CREATURE, type);
		this.draw = false;
		this.dead = false;

	}

	public void updateWayPoint (){
    	// Creature has reached is destination without being killed
    	if (getNextWayPoint() +1 >= wayP.length){
    		score();
    	}
    	else 		
    		setNextWayPoint(getNextWayPoint() + 1);
	}

	public void damage(float dmg){
		if (health > 0) {
			dmg = health >= dmg ? dmg : health; 
			health -= dmg;
			GL.updateCreatureProgress(dmg);
			if(health <= 0){
				die();
			}
		}
	}
	
	public void setHealth(float health){
		this.health = health;
	}
	
	public void setSpawndelay(float f) {
		this.spawndelay = f;
	}
	
	public void setDeadResourceId(int mDeadResourceId) {
		this.mDeadResourceId = mDeadResourceId;
	}
    
	public int getDeadResourceId() {
		return mDeadResourceId;
	}

	public void setGoldValue(int goldValue) {
		this.goldValue = goldValue;
	}

	public void setVelocity(float velocity){
		this.velocity = velocity;
	}
	
	public void setDeadTexture(TextureData mDeadTexture) {
		this.mDeadTextureData = mDeadTexture;
	}

	public boolean isCreatureFast() {
		return creatureFast;
	}

	public void animate(float timeDeltaSeconds) {
		this.tmpAnimationTime -= timeDeltaSeconds;
		if (tmpAnimationTime <= 0f) {
			this.tmpAnimationTime = this.animationTime;
			this.animate();
		}
	}

	private float getWayX(int i) {
		float newsize = (this.getWidth()/2 - this.scale*this.getWidth()/2);
		float cen_x = wayP[i].getX() + newsize;
		return (cen_x/this.scale);
	}
	private float getWayY(int i) {
		float newsize = (this.getHeight() - this.scale*this.getHeight());
		float cen_y = wayP[i].getY() + newsize;
		return (cen_y/this.scale);
	}
	
	public float getScaledX() {
		float cen_x  = x + this.getWidth()/2;
		return (cen_x*this.scale);
	}
	public float getScaledY() {
		float cen_y  = y + this.getHeight()/2;
		return (cen_y*this.scale);
	}

	public void update(float timeDeltaSeconds){
	
		//Time to spawn.
		if (spawnWayPointX == this.x && spawnWayPointY == this.y) {
			spawndelay -= timeDeltaSeconds;
			if (spawndelay <= 0) {
				draw = true;
				spawnWayPointX = 0;
				spawnWayPointY = 0;
			}
		}
		
		//If still alive move the creature.
		float movement = 0;
		if (draw && health > 0) {
			// If the creature is living calculate tower effects.
			movement = applyEffects(timeDeltaSeconds);
			move(movement);
			animate(timeDeltaSeconds);
		}
	    // Creature is dead and fading...
		else if (allDead) {
			fade(timeDeltaSeconds/5);
		}
	}
	
	private void die() {
		this.dead = true;
		setCurrentTexture(this.mDeadTextureData);
		resetRGB();
		player.moneyFunction(this.goldValue);
		GL.updateCurrency();
		// play died1.mp3
		soundManager.playSound(10);
		//we dont remove the creature from the gameloop just yet
		//that is done when it has faded completely, see the fade method.
		GL.creatureDiesOnMap(1);
	}
	
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
		}

	}
	
	private float applyEffects(float timeDeltaSeconds){
		float tmpR = this.rDefault;
		float tmpG = this.gDefault;
		float tmpB = this.bDefault;
		float tmpRGB = 1;
		
		int slowAffected = 1;
		if (creatureFrozenTime > 0) {
			slowAffected = 2;
    		creatureFrozenTime = creatureFrozenTime - (timeDeltaSeconds);
    		tmpRGB = creatureFrozenTime <= 1f ? 1-0.3f*creatureFrozenTime : 0.7f;
    		tmpR = tmpR*tmpRGB;
    		tmpG = tmpG*tmpRGB;
		}
		// If creature has been shot by a poison tower we slowly reduce creature health
		if (creaturePoisonTime > 0) {
			creaturePoisonTime = creaturePoisonTime - (timeDeltaSeconds);
			damage(timeDeltaSeconds * creaturePoisonDamage);
	  		tmpRGB = creaturePoisonTime <= 1f ? 1-0.3f*creaturePoisonTime : 0.7f;
	  		tmpR = tmpR*tmpRGB;
	  		tmpB = tmpG*tmpRGB;
		}
  
		if (creatureFrozenTime > 0 && creaturePoisonTime > 0) {
			this.r = 0;
		}
		
		float movement = (velocity * (timeDeltaSeconds/this.scale)) / slowAffected;
		
		this.r = tmpR;
		this.g = tmpG;
		this.b = tmpB;		
		return movement;
	}
	
	private void fade(float reduceOpacity){
		this.opacity -= reduceOpacity;
		if (opacity <= 0.0f) {
			draw = false;
			//The creature is now completely gone from the map, tell the loop.
			GL.creatureLeavesMAP(1);
		}
	}
	
	private void score(){
		//draw = false;
		player.damage(1);
		GL.updatePlayerHealth();
		//GL.subtractCreature(1);
		// Testar hur spelet blir om en creature som inte har d�tt b�rjar om l�ngst upp
		moveToWaypoint(0);
		setNextWayPoint(1);
	}
	
	public void SetWayPoints(Coords[] waypoints){
		this.wayP = waypoints;
	}
	
	public void moveToWaypoint(int p){
		this.x = this.getWayX(p);
		this.y = this.getWayY(p);
	}

	public void setNextWayPoint(int nextWayPoint) {
		this.nextWayPoint = nextWayPoint;
		this.wayPointX = this.getWayX(nextWayPoint);
		this.wayPointY = this.getWayY(nextWayPoint);
	}

	public int getNextWayPoint() {
		return nextWayPoint;
	}

	public TextureData getDeadTexture() {
		return mDeadTextureData;
	}

	public void setXOffset(int xoffset) {
		this.xoffset = xoffset;
	}

	public void setYOffset(int yoffset) {
		this.yoffset = yoffset;
	}

	public void setAllDead(boolean allDead) {
		this.allDead = allDead;
	}

	public void affectWithFrost(int time) {
		if (!this.creatureFrostResistant)
			this.creatureFrozenTime = time;
	}

	public void affectWithPoison(int poisonTime, int poisonDamage) {
		if (!this.creaturePoisonResistant) {
			if ( this.creaturePoisonDamage > 0 && this.creaturePoisonTime > 0 )
				this.creaturePoisonDamage = (int)(poisonDamage + (this.creaturePoisonDamage * this.creaturePoisonTime) / poisonTime);
			else
				this.creaturePoisonDamage = (int)(poisonDamage);
			this.creaturePoisonTime = poisonTime;
		}
	}

	// Set rgb to new value
	public void setRGB(float rDefault, float gDefault, float bDefault) {
		this.rDefault = rDefault;
		this.gDefault = gDefault;
		this.bDefault = bDefault;
	
	}
	
	// Needed to reset rgb to default value
	private void resetRGB() {
		this.r = this.rDefault;
		this.g = this.gDefault;
		this.b = this.bDefault;
	}

	public void setDead(boolean b) {
		this.dead = b;
	}

	// This setters is used by the waveloader. Needed to show correct creature
	public void setDisplayResourceId(int resID) {
		this.mDisplayResourceId = resID;
	}
	public int getDisplayResourceId() {
		return this.mDisplayResourceId;
	}

	// Defines animationtime for the walk of a creature
	public void setAnimationTime(boolean fast) {
		if (fast)
			this.animationTime = 0.15f;
		else
			this.animationTime = 0.3f;
	}

	//This is the spawnpoint of a creature
	public void setSpawnPoint() {
		this.spawnWayPointX = this.getWayX(0);
		this.spawnWayPointY = this.getWayY(0);
	}

}