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
    private int health;
    // The next way point for a given creature
    private int nextWayPoint;
    // SPRITE DEAD RESOURCE
    private int mDeadResourceId;
    // SPRITE DEAD 
	private int mDeadTextureName;
    // The speed of the creature
    private float velocity;
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
    private int goldValue;
    // Creature special abilty
    private boolean creatureFast;
    private boolean creatureFrostResistant;
    private boolean creatureFireResistant;
    private boolean creaturePoisonResistant;
    // Creature affected by some kind of tower
    public float creatureFrozenTime;
    public float creaturePoisonTime;
    public int creaturePoisonDamage;
    
	public Creature(int resourceId, Player player, SoundManager soundMan, Coords[] wayP){
		super(resourceId);
		this.draw = false;
		this.player = player;
		this.setNextWayPoint(0);
		this.soundManager = soundMan;
		this.wayP = wayP;
	}
	
	//This is only used by the level constructor.
	public Creature(int resourceId){
		super(resourceId);
		this.draw = false;
	}

	public void updateWayPoint (){
		setNextWayPoint(getNextWayPoint() + 1);
	}

	public float getVelocity() {
		return velocity;
	}

	public void setVelocity(float velocity) {
		this.velocity = velocity;
	}

	public void setHealth(int health) {
		this.health = health;
	}

	public int getHealth() {
		return health;
	}

	public long getSpawndelay() {
		return spawndelay;
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

	public int getDeadTextureName() {
		return mDeadTextureName;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}

	public int getDirection() {
		return direction;
	}

	public int getGoldValue() {
		return goldValue;
	}

	public void setGoldValue(int goldValue) {
		this.goldValue = goldValue;
	}

	public void setCreatureFast(boolean creatureFast) {
		this.creatureFast = creatureFast;
	}

	public void setCreatureFrostResistant(boolean creatureFrostResistant) {
		this.creatureFrostResistant = creatureFrostResistant;
	}

	public boolean isCreatureFrostResistant() {
		return creatureFrostResistant;
	}

	public boolean isCreatureFast() {
		return creatureFast;
	}

	public void setCreatureFireResistant(boolean creatureFireResistant) {
		this.creatureFireResistant = creatureFireResistant;
	}

	public boolean isCreatureFireResistant() {
		return creatureFireResistant;
	}

	public void setCreaturePoisonResistant(boolean creaturePoisonResistant) {
		this.creaturePoisonResistant = creaturePoisonResistant;
	}

	public boolean isCreaturePoisonResistant() {
		return creaturePoisonResistant;
	}

	public int move(float timeDeltaSeconds, long time, int gameSpeed){
		//Time to spawn.
		if (time > spawndelay && wayP[0].x == x && wayP[0].y == y) {
			draw = true;
		}	            	
		// If the creature is living start movement calculations.
		if (draw && opacity == 1.0f) {
    		Coords co = wayP[getNextWayPoint()];
    		// If creature has been shot by an frost tower we will force it to walk slower
    		int slowAffected = 1;
    		if (creatureFrozenTime > 0) {
	    		slowAffected = 2;
	    		creatureFrozenTime = creatureFrozenTime - timeDeltaSeconds;
    		}
    		// If creature has been shot by a poison tower we slowly reduce creature health
    		if (creaturePoisonTime > 0) {
    			creaturePoisonTime = creaturePoisonTime - timeDeltaSeconds;
    			setHealth((int)(getHealth() - (timeDeltaSeconds * creaturePoisonDamage)));	    		
		    	// Have the creature died?
	    		if (getHealth() <= 0) {
	    			creatureDied();
	    		}
    		}
    		float movement = (velocity * timeDeltaSeconds * gameSpeed) / slowAffected;
    		
    		// Creature is moving left.
			if(x > co.x){
				setDirection(Creature.LEFT);
				x = x - movement;
	    		if(!(x > co.x)){
	    			x = co.x;
	    		}
	    	}
    		// Creature is moving right.
			else if (x < co.x) {
				setDirection(Creature.RIGHT);
				x = x + movement;
	    		if(!(x < co.x)){
	    			x = co.x;
	    		}
	    	}
    		// Creature is moving down.
			else if(y > co.y){
				setDirection(Creature.DOWN);
				y = y - movement;
	    		if(!(y > co.y)){
	    			y = co.y;
	    		}
	    	}
    		// Creature is moving up.
	    	else if (y < co.y) {
	    		setDirection(Creature.UP);
	    		y = y + movement;
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
	    		draw = false;
	    		player.health --;
	    		//The creature exited the screen, return 1.
	    		return 1;
	    	}
	    	
	    	// Creature is dead and fading...
		} else if (draw && opacity > 0.0f) {
				// If we divide by 10 the creature stays on the screen a while longer...
			opacity = opacity - (timeDeltaSeconds/10 * gameSpeed);
			if (opacity <= 0.0f) {
				draw = false;
	    		//The creature died, return 1.
				return 1;
			}
		}
		//Still alive and on the screen.
		return 0;
	}
	
	public void creatureDied() {
		setTextureName(getDeadTextureName());
		opacity = opacity - 0.1f;
		player.money = player.money + goldValue;
		// play died1.mp3
		soundManager.playSound(10);
	}
	
	public void SetWayPoint(Coords[] waypoints){
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

