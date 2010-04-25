package com.crackedcarrot;

/**
* Class defining a shot in the game
*/
public class Shot extends Sprite{
    // The tower object which the shot belongs to
    public Tower tower;
    
	public Shot(int resourceId,  int type, Tower tower){
		super(resourceId, SHOT, type);
		this.tower = tower;
		super.draw = false;
	}

	/**
	 * Method that places a shot back to 
	 * the start position
	 */
	public void resetShotCordinates() {
		x = tower.x + tower.getWidth()/2;
		y = tower.y + tower.getHeight()/2;
	}
}