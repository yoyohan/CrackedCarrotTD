package com.crackedcarrot;


/**
* Class defining creature in the game
*/
public class Creature extends Sprite{
	//Player Ref
	private Player player;
	//SoundManager ref
	private SoundManager soundManager;
	//Waypoints for this creature
	private Coords[] wayP;
	//Variable keeping all creatures from going in a straight line
	private int offset;
    // A creatures health
    protected float health;
    // The next way point for a given creature
    private int nextWayPoint;
    // SPRITE DEAD RESOURCE
    private int mDeadResourceId;
    // SPRITE DEAD 
	private int mDeadTextureName;
    // The speed of the creature
    protected float velocity;
    // Delay before spawning the creature to the map
    private long spawndelay;
    // How much gold this creature gives when it's killed.
    protected int goldValue;
    //Ref to gameloop that runs this creature.
    private GameLoop GL;
    // Creature special abilty
    public boolean creatureFast;
    public boolean creatureFrostResistant;
    public boolean creatureFireResistant;
    public boolean creaturePoisonResistant;
    // Creature affected by some kind of tower
    public float creatureFrozenTime;
    public float creaturePoisonTime;
    public int creaturePoisonDamage;
    
	public Creature(int resourceId, int type, int frames, Player player, SoundManager soundMan, Coords[] wayP, GameLoop loop){
		super(resourceId, NativeRender.CREATURE, type, frames);
		this.draw = false;
		this.player = player;
		this.setNextWayPoint(0);
		this.soundManager = soundMan;
		this.wayP = wayP;
		this.GL = loop;
	}
	
	//This is only used by the level constructor.
	public Creature(int resourceId, int type , int frames){
		super(resourceId, NativeRender.CREATURE, type, frames);
		this.draw = false;

	}

	public void updateWayPoint (){
		setNextWayPoint(getNextWayPoint() + 1);
	}

	public void damage(float dmg){
		dmg = health >= dmg ? dmg : health; 
		health -= dmg;
		GL.updateCreatureProgress(dmg);
		if(health <= 0){
			die();
		} 
	}
	
	public void setHealth(float health){
		this.health = health;
	}
	
	public void setSpawndelay(long spawndelay) {
		this.spawndelay = spawndelay;
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
	
	public void setDeadTextureName(int mDeadTextureName) {
		this.mDeadTextureName = mDeadTextureName;
	}

	public boolean isCreatureFast() {
		return creatureFast;
	}

	public void update(float timeDeltaSeconds, long time, int gameSpeed){
	
		//Time to spawn.
		if (time > spawndelay && wayP[0].x == x && wayP[0].y == y)
			draw = true;
		
		//If still alive move the creature.
		float movement = 0;
		if (draw && health > 0 && nextWayPoint < wayP.length) {
			// If the creature is living calculate tower effects.
			movement = applyEffects(timeDeltaSeconds, gameSpeed);
			move(movement);
		}
		
	    // Creature is dead and fading...
		else if (draw && health <= 0) {
			fade(timeDeltaSeconds/10 * gameSpeed);
		}
	}
	
	private void die() {
		setTextureName(this.mDeadTextureName);
		this.opacity -= 0.5;
		player.moneyFunction(this.goldValue);
		// play died1.mp3
		soundManager.playSound(10);
		//we dont remove the creature from the gameloop just yet
		//that is done when it has faded completely, see the fade method.
		GL.creaturDiesOnMap(1);
	}
	
	private void move(float movement){
		Coords co = wayP[getNextWayPoint()];
		
		float yDistance = co.y - this.y;
		float xDistance = co.x - this.x+offset;
			
		if ((Math.abs(yDistance) <= movement) && (Math.abs(xDistance) <= movement)) {
			// We have reached our destination!!!
    		updateWayPoint();
		}
		else {
    		double radian = Math.atan2(yDistance, xDistance);
    		this.x += Math.cos(radian) * movement;
    		this.y += Math.sin(radian) * movement;
		}
    	// Creature has reached is destination without being killed
    	if (getNextWayPoint() >= wayP.length){
    		score();
    	}
	}
	
	private float applyEffects(float timeDeltaSeconds, float gameSpeed){
		this.r = 1;
		this.g = 1;
		this.b = 1;
		float tmpRGB = 1;
		
		int slowAffected = 1;
		if (creatureFrozenTime > 0) {
			slowAffected = 2;
    		creatureFrozenTime = creatureFrozenTime - timeDeltaSeconds;
    		tmpRGB = creatureFrozenTime <= 1f ? 1-0.3f*creatureFrozenTime : 0.7f;
    		this.r = tmpRGB;
    		this.g = tmpRGB;
		}
		// If creature has been shot by a poison tower we slowly reduce creature health
		if (creaturePoisonTime > 0) {
			creaturePoisonTime = creaturePoisonTime - timeDeltaSeconds;
			damage(timeDeltaSeconds * creaturePoisonDamage);
	  		tmpRGB = creaturePoisonTime <= 1f ? 1-0.3f*creaturePoisonTime : 0.7f;
			this.r = tmpRGB;
			this.b = tmpRGB;
		}
  
		if (creatureFrozenTime > 0 && creaturePoisonTime > 0) {
			this.r = 0;
		}

		
		float movement = (velocity * timeDeltaSeconds * gameSpeed) / slowAffected;
		
		if (health <= 0) {
   			die();
   			movement = 0;
   		}
		
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
		//GL.subtractCreature(1);
		// Testar hur spelet blir om en creature som inte har dött börjar om längst upp
		moveToWaypoint(0);
		nextWayPoint = 1;
	}
	
	public void SetWayPoints(Coords[] waypoints){
		this.wayP = waypoints;
	}
	
	public void moveToWaypoint(int p){
		this.x = wayP[p].getX();
		this.y = wayP[p].getY();
	}

	public void setNextWayPoint(int nextWayPoint) {
		this.nextWayPoint = nextWayPoint;
	}

	public int getNextWayPoint() {
		return nextWayPoint;
	}

	public int getDeadTextureName() {
		return mDeadTextureName;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}