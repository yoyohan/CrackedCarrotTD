package com.crackedcarrot;

/**
* Class defining a shot in the game
*/
public class Shot extends Sprite{
    // The tower object which the shot belongs to
    public Tower tower;
    // Varibles used to calculate animation time for a shot
    private float animationTime;
    private float tmpAnimationTime;
    
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
	
	public boolean animateShot(float timeDeltaSeconds, float size, Creature targetCreature) {
		tmpAnimationTime -= timeDeltaSeconds;
		
		if (tmpAnimationTime <= 0) {
			this.draw = false;
			this.resetShotCordinates();
			this.scale = 1;
			this.cFrame = 0;
			tmpAnimationTime = this.animationTime;
			return false;
		}
		else {
			this.x = targetCreature.getScaledX();
			this.y = targetCreature.getScaledY();
			scaleSprite(size);
    		cFrame = (int)(((1-(tmpAnimationTime/animationTime))*(this.getNbrOfFrames()-1)))+1;
    		
			return true;
		}
	}

	public float getAnimationTime() {
		return animationTime;
	}

	public void setAnimationTime(float animationTime) {
		this.animationTime = animationTime;
		this.tmpAnimationTime = animationTime;
	}

}