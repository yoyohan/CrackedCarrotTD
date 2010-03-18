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
    public int health;
    // The next way point for a given creature
    public int nextWayPoint;
    // SPRITE DEAD RESOURCE
    private int mDeadResourceId;
    // SPRITE DEAD 
	private int mDeadTextureName;
    // The speed of the creature
    public float velocity;
    // The different directions for a creature
    public static final int LEFT = 0;
    public static final int RIGHT = 1;
    public static final int UP = 2;
    public static final int DOWN = 3;
    // The current direction of the creature
    public int direction;
    // Delay before spawning the creature to the map
    public long spawndelay;
    // How much gold this creature gives when it's killed.
    public int goldValue;
    // Creature special abilty
    public boolean creatureFast;
    public boolean creatureFrostResistant;
    public boolean creatureFireResistant;
    public boolean creaturePoisonResistant;
    // Creature affected by some kind of tower
    public float creatureFrozenTime;
    public float creaturePoisonTime;
    public int creaturePoisonDamage;
    
	public Creature(int resourceId, Player player, SoundManager soundMan, Coords[] wayP){
		super(resourceId);
		this.draw = false;
		this.player = player;
		this.nextWayPoint = 0;
		this.soundManager = soundMan;
		this.wayP = wayP;
	}
	
	//This is only used by the level constructor.
	public Creature(int resourceId){
		super(resourceId);
		this.draw = false;
	}

	public void updateWayPoint (){
		nextWayPoint++;
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
	
	public int move(float timeDeltaSeconds, long time, int gameSpeed){
		//Time to spawn.
		if (time > spawndelay && wayP[0].x == x && wayP[0].y == y) {
			draw = true;
		}	            	
		// If the creature is living start movement calculations.
		if (draw && opacity == 1.0f) {
    		Coords co = wayP[nextWayPoint];
    		// If creature has been shot by an frost tower we will force it to walk slower
    		int slowAffected = 1;
    		if (creatureFrozenTime > 0) {
	    		slowAffected = 2;
	    		creatureFrozenTime = creatureFrozenTime - timeDeltaSeconds;
    		}
    		// If creature has been shot by a poison tower we slowly reduce creature health
    		if (creaturePoisonTime > 0) {
    			creaturePoisonTime = creaturePoisonTime - timeDeltaSeconds;
    			health = (int)(health - (timeDeltaSeconds * creaturePoisonDamage));	    		
		    	// Have the creature died?
	    		if (health <= 0) {
	    			creatureDied();
	    		}
    		}
    		float movement = (velocity * timeDeltaSeconds * gameSpeed) / slowAffected;
    		
    		// Creature is moving left.
			if(x > co.x){
				direction = Creature.LEFT;
				x = x - movement;
	    		if(!(x > co.x)){
	    			x = co.x;
	    		}
	    	}
    		// Creature is moving right.
			else if (x < co.x) {
				direction = Creature.RIGHT;
				x = x + movement;
	    		if(!(x < co.x)){
	    			x = co.x;
	    		}
	    	}
    		// Creature is moving down.
			else if(y > co.y){
				direction = Creature.DOWN;
				y = y - movement;
	    		if(!(y > co.y)){
	    			y = co.y;
	    		}
	    	}
    		// Creature is moving up.
	    	else if (y < co.y) {
	    		direction = Creature.UP;
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
	    	if (nextWayPoint >= wayP.length){
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
}

