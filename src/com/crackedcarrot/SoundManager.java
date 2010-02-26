package com.crackedcarrot;

/*
 * 
 * Simple class for playing sounds.
 * 
 *   All sounds needs to be added to res/raw/sound.mp3 with
 *   their extension intact. They're loaded and cached within
 *   the constructor of the class.
 * 
 *   To initialize:
 * 
 *     SoundManager sm = new SoundManager(getBaseContext());
 *   
 *   from GameInit.java or similar.
 *
 *
 *   TODO:
 *   
 *     Annat system för ljud-id? Int är lite sådär...
 *     
 *
 */

//import com.crackedcarrot.menu.R;

import android.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;


public class SoundManager {

	private  SoundPool     mSoundPool;
	private  int[]         mSoundArray = new int[32];   // max # of sounds.
													    // set to 32 for no reason.
	private  float[]       mSoundPitch = new float[32];
	private  AudioManager  mAudioManager;
	private  Context       mContext;


	public SoundManager(Context baseContext) {
        this.initSounds(baseContext);
        // Here goes the mp3/wav/ogg-files we want to use.
        // These need to be added to the res/raw/NameOfSound.mp3 folder,
        // WITH the extension on the file.
        this.addSound( 0, 1.0f, R.raw.shot1);
        this.addSound(10, 1.0f, R.raw.died1);
        //this.addSound(20, 1.0f, R.raw.creaturehappy);
        //this.addSound(30, 1.0f, R.raw.victory);
        //this.addSound(31, 1.0f, R.raw.defeat);
	}

	public void initSounds(Context context) { 
		 mContext = context;
		 // number 4 is the total number of concurrently playing sounds. if 4 are already
		 // playing the oldest one will be replaced. we can change if necessary.
	     mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
	     mAudioManager = (AudioManager)mContext.getSystemService(Context.AUDIO_SERVICE); 	     
	} 

    /**
	 * Loads a sound-file into our cache and prepares it for being played
	 * by the playSound or playLoopedSound-functions.
	 *
	 * @param  index		The desired id for this sound, value: int.
	 * @param  pitch		The pitch for the sound, value: 0.5 - 2.0 float.
	 * @param  soundId		Location of the R.raw.soundfile.mp3 resource.
	 * @return     			void
	 */ 
	public void addSound(int index, float pitch, int soundId) {
		mSoundArray[index] = mSoundPool.load(mContext, soundId, 1);
		mSoundPitch[index] = pitch;
	}

	public void playSound(int index) {
		//Log.d("SOUNDMANAGER", "Playing sound " + index);
		
	    int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
	    if (mSoundPool.play(mSoundArray[index], streamVolume, streamVolume, 1, 0, mSoundPitch[index]) == 0) {
	    	Log.d("SOUNDMANAGER", "Failed to play " + index);
	    }
	}

	public void playLoopedSound(int index) {
	     int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
	     mSoundPool.play(mSoundArray[index], streamVolume, streamVolume, 1, -1, 1f); 
	}

}

