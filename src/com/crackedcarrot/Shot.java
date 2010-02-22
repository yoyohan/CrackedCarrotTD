package com.crackedcarrot;


/**
*
* Class defining a shot in the game
*/
public class Shot extends Sprite{
	
	// The next way point for a given shot aka the target point
    public int nextWayPoint;
	// The speed of the shot
    public float velocity;
    // The creature target
    public Creature creature;
    // The tower object which the shot belongs to
    public Tower tower;
    // The length in pixels between a creature and its next way point
    public float creLength;
    // The length in pixels between a shot (same pos. as the tower) and its target point
    public float shotLength;
    
	
	public Shot(int resourceId){
		super(resourceId);
	}
	
	/**
	 * Method that tracks a creature. It iterates over a list of creatures and picks
	 * the first creature in the list that is within the range of the tower 
	 * @param null 
	 */
	public void trackEnemy(Creature[] cres){
		int listLength = cres.length;
		for(int i = 0;i < listLength; i++ ){
			if(cres[i].draw == true){ // Is the creature still alive?
				float distance = Math.abs(this.x - cres[i].x) + Math.abs(this.y - cres[i].y);
				if(distance < tower.range){ // Is the creature within tower range?
					this.creature = cres[i];
					break;
				} else {
					i++;
				}
			}	
		}
	}
	
	/**
	 * Method that calculates and returns the destination of the shot
	 * which depends on the current coordinates and velocity of the creature
	 * and the starting position and velocity of the shot.
	 * @param cre
	 * @param wayPointList
	 */
	public Coords calcWayPoint(Creature cre, Coords[] wayPointList){
		float creDistx = Math.abs(cre.x - wayPointList[cre.nextWayPoint].x);
		float creDisty = Math.abs(cre.y - wayPointList[cre.nextWayPoint].y);
		this.creLength = creDistx + creDisty;
		float shotDistx = this.x - wayPointList[cre.nextWayPoint].x;
		float shotDisty = this.y - wayPointList[cre.nextWayPoint].y;
		this.shotLength = Math.abs(Math.abs(shotDistx) + Math.abs(shotDisty));
		float creTime = this.creLength / cre.velocity;
		float shotTime = this.shotLength / this.velocity;
		if(creTime < shotTime){ // Will the creature reach next way point before the shot will?
			//Ta hänsyn till sträcka till nästa way point också
			return wayPointList[cre.nextWayPoint]; //Skjut mot way point sålänge
		}else { //Utgå från första way point
			switch(cre.direction){
				case Creature.LEFT:
					for(float i = (cre.x - creDistx); i < cre.x; i++){ // iterate to right
						shotDistx = this.x - i;
						shotDisty = this.y - wayPointList[cre.nextWayPoint].y;
						this.shotLength = Math.abs(Math.abs(Math.abs(shotDistx) + Math.abs(shotDisty)));
						if((i/cre.velocity) == this.shotLength/this.velocity){ // cre time = shot time?
							Coords co = new Coords((int)i, (int)cre.y);
							return co;
						}
					}
				case Creature.RIGHT:
					for(float i = (cre.x + creDistx); i > cre.x; i--){ // iterate to left
						shotDistx = this.x - i;
						shotDisty = this.y - wayPointList[cre.nextWayPoint].y;
						this.shotLength = Math.abs(Math.abs(Math.abs(shotDistx) + Math.abs(shotDisty)));
						if((i/cre.velocity) == this.shotLength/this.velocity){ // cre time = shot time?
							Coords co = new Coords((int)i, (int)cre.y);
							return co;
						}
					}
				case Creature.UP:
					for(float i = (cre.y + creDisty); i > cre.y; i--){ // iterate down
						shotDistx = this.x - i;
						shotDisty = this.y - wayPointList[cre.nextWayPoint].y;
						this.shotLength = Math.abs(Math.abs(Math.abs(shotDistx) + Math.abs(shotDisty)));
						if((i/cre.velocity) == this.shotLength/this.velocity){ // cre time = shot time?
							Coords co = new Coords((int)i, (int)cre.y);
							return co;
						}
					}
				case Creature.DOWN:
					for(float i = (cre.y - creDisty); i < cre.y; i++){ // iterate up
						shotDistx = this.x - i;
						shotDisty = this.y - wayPointList[cre.nextWayPoint].y;
						this.shotLength = Math.abs(Math.abs(Math.abs(shotDistx) + Math.abs(shotDisty)));
						if((i/cre.velocity) == this.shotLength/this.velocity){ // cre time = shot time?
							Coords co = new Coords((int)i, (int)cre.y);
							return co;
						}
					}
			}//switch
			return null;
		}
		
	}
	
}