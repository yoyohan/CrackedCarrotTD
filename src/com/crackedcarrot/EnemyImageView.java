package com.crackedcarrot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

public class EnemyImageView extends ImageView {

	public static EnemyUpdateListener listener = null;
	
	public EnemyImageView(Context ctx){
		super(ctx);
	}
	
	public EnemyImageView(Context ctx, AttributeSet attrs){
		super(ctx, attrs);
	}
	
	/** A method that sets the listener for NrCreTextView object */
	public void setEnemyUpdateListener(EnemyUpdateListener l) {
		listener = l;
	}
	
	/** Create an interface to notify an activity when number of enemies is updated */
	public interface EnemyUpdateListener {
		public abstract void enemyUpdate(int imageId);
	}
	
}