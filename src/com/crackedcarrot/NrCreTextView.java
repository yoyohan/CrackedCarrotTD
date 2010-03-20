package com.crackedcarrot;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class NrCreTextView extends TextView {

	public static CreatureUpdateListener listener = null;
	
	public NrCreTextView(Context ctx){
		super(ctx);
	}
	
	public NrCreTextView(Context ctx, AttributeSet attrs){
		super(ctx, attrs);
	}
	
	/** A method that sets the listener for NrCreTextView object */
	public void setCreatureUpdateListener(CreatureUpdateListener l) {
		listener = l;
	}
	
	/** Create an interface to notify an activity when number of enemies is updated */
	public interface CreatureUpdateListener {
		public abstract void creatureUpdate(int number);
	}
	
}