package com.crackedcarrot;

public class Animation extends Sprite {
	private int nFrames;
	private int frameNo;
	
	public Animation(int resourceId, int type, int nFrames) {
		super(resourceId);
		this.nFrames = nFrames;
		this.frameNo = 0;
		setType(NativeRender.ANIMATION, type);

	}
	
	public void animate(){
		frameNo = (frameNo +1) % nFrames;
	}

}
