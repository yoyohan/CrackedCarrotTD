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
    // A creatures health
    protected int health;
    // The next way point for a given creature
    private int nextWayPoint;
    // SPRITE DEAD RESOURCE
    private int mDeadResourceId;
    // SPRITE DEAD 
	private int mDeadTextureName;
    // The speed of the creature
    protected float velocity;
    // The different directions for a creature
    private static final int LEFT = 0;
    private static final int RIGHT = 1;
    private static final int UP = 2;
    private static final int DOWN = 3;
    // The current direction of the creature
    private int direction;
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
    
	public Creature(int resourceId, Player player, SoundManager soundMan, Coords[] wayP, GameLoop loop){
		super(resourceId);
		this.draw = false;
		this.player = player;
		this.setNextWayPoint(0);
		this.soundManager = soundMan;
		this.wayP = wayP;
		this.GL = loop;
	}
	
	//This is only used by the level constructor.
	public Creature(int resourceId){
		super(resourceId);
		this.draw = false;
	}

	public void updateWayPoint (){
		setNextWayPoint(getNextWayPoint() + 1);
	}

	public void cloneCreature(Creature clone) {
		this.setResourceId(clone.getResourceId());
		this.mDeadResourceId = clone.mDeadResourceId;
		this.mDeadTextureName = clone.mDeadTextureName;
		this.creatureFast = clone.creatureFast;
		this.creatureFireResistant = clone.creatureFireResistant;
		this.creatureFrostResistant = clone.creatureFrostResistant;
		this.creaturePoisonResistant = clone.creaturePoisonResistant;
		this.moveToWaypoint(0);
		this.health = clone.health;
		this.nextWayPoint = clone.getNextWayPoint();
		this.velocity = clone.velocity;
		this.width = clone.width;
		this.height = clone.height;
		this.goldValue = clone.goldValue;
		this.draw = false;
		this.opacity = 1;
	}

	public void damage(int dmg){
		health -= dmg;
		if(health <= 0){
			die();
		}
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
		if (draw && health > 0) {
			// If the creature is living calculate tower effects.
			movement = applyEffects(timeDeltaSeconds, gameSpeed);
		}
		
		if(health > 0){
			move(movement);
		}
	    // Creature is dead and fading...
		else if (draw && health <= 0) {
			fade(timeDeltaSeconds/10 * gameSpeed);
		}
	}
	
	private void die() {
		setTextureName(this.mDeadTextureName);
		this.opacity -= 0.1f;
		player.addMoney(this.goldValue);
		GL.subtractCreature(1);
		// play died1.mp3
		soundManager.playSound(10);
	}
	
	private void move(float movement){
		Coords co = wayP[getNextWayPoint()];
		
		// Creature is moving left.
		if(x > co.x){
			this.direction = Creature.LEFT;
			x -= movement;
    		if(!(x > co.x)){
    			x = co.x;
    		}
    	}
		// Creature is moving right.
		else if (x < co.x) {
			this.direction = Creature.RIGHT;
			x += movement;
    		if(!(x < co.x)){
    			x = co.x;
    		}
    	}
		// Creature is moving down.
		else if(y > co.y){
			this.direction =  Creature.DOWN;
			y -= movement;
    		if(!(y > co.y)){
    			y = co.y;
    		}
    	}
		// Creature is moving up.
    	else if (y < co.y) {
    		this.direction = Creature.UP;
    		y += movement;
    		if(!(y < co.y)){
    			y = co.y;
    		}
    	}
		// Creature has reached a WayPoint. Update
    	if (y == co.y && x == co.x){
    		updateWayPoint();
    	}
    	// Creature has reached is destination without being killed
    	if (getNextWayPoint() >= wayP.length){
    		score();
    	}
	}
	
	private float applyEffects(float timeDeltaSeconds, float gameSpeed){
		int slowAffected = 1;
		if (creatureFrozenTime > 0) {
    		slowAffected = 2;
    		creatureFrozenTime = creatureFrozenTime - timeDeltaSeconds;
		}
		// If creature has been shot by a poison tower we slowly reduce creature health
		if (creaturePoisonTime > 0) {
			creaturePoisonTime = creaturePoisonTime - timeDeltaSeconds;
			health = ((int)(this.health - (timeDeltaSeconds * creaturePoisonDamage)));	    		
	    	// Have the creature died?
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
		}
	}
	
	private void score(){
		draw = false;
		player.damage(1);
		GL.subtractCreature(1);
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
}

