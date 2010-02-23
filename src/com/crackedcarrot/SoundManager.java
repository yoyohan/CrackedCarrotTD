package com.crackedcarrot;

import java.util.HashMap;

import com.crackedcarrot.menu.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;


public class SoundManager {
	
	private  SoundPool mSoundPool; 
	private  HashMap<Integer, Integer> mSoundPoolMap; 
	private  AudioManager  mAudioManager;
	private  Context mContext;
	
	
	public SoundManager(Context baseContext) {
        this.initSounds(baseContext);
        // list of sounds to add.
        // these need to be added to the res/raw/NameOfSound.mp3 folder
        this.addSound(1, R.raw.shot1);
        this.addSound(2, R.raw.died1);
	}
		
	public void initSounds(Context theContext) { 
		 mContext = theContext;
		 // number 4 is the total number of concurrently playing sounds. if 4 are already
		 // playing the oldest one will be replaced. we can change if necessary.
	     mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0); 
	     mSoundPoolMap = new HashMap<Integer, Integer>(); 
	     mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE); 	     
	} 
	
	public void addSound(int Index,int SoundID) {
		mSoundPoolMap.put(Index, mSoundPool.load(mContext, SoundID, 1));
	}
	
	public void playSound(int index) {
		//Log.d("SOUNDMANAGER", "Playing sound " + index);
	     int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	     if (mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1.0f) == 0) {
	    	 Log.d("SOUNDMANAGER", "Failed to play " + index);
	     }
	}
	
	public void playLoopedSound(int index) {
	     int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
	     mSoundPool.play(mSoundPoolMap.get(index), streamVolume, streamVolume, 1, -1, 1f); 
	}
	
}
