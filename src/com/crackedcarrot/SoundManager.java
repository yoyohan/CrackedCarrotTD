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
 */

//import com.crackedcarrot.menu.R;

import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.SystemClock;
import android.util.Log;

import com.crackedcarrot.menu.R;


public class SoundManager {

	private  SoundPool     mSoundPool;
	private  int[]         mSoundArray     = new int[32];   // max # of sounds.
											     		    // set to 32 for no reason.
	private  float[]       mSoundPitch     = new float[32];
	private  long[]        mSoundDelay     = new long[32];
	private  long[]        mSoundDelayLast = new long[32];
	private  AudioManager  mAudioManager;
	private  Context       mContext;
	
	private  Random        random;

    public   boolean       playSound = false; // play sounds?
    
    	// TODO: The sounds need to be better declared to make sense, ala GameLoopGUI.
    final int    		   SOUND_TOWER_SHOT1   = 0;
    final int              SOUND_TOWER_SHOT2   = 1;
    
    final int	           SOUND_CREATURE_DEAD = 10;
    
    final int              SOUND_TOWER_BUILD   = 20;

	public SoundManager(Context baseContext) {
        this.initSounds(baseContext);
        // Here goes the mp3/wav/ogg-files we want to use.
        // These need to be added to the res/raw/NameOfSound.mp3 folder,
        // WITH the extension on the file.
        this.addSound( 0, 1.0f, 250, R.raw.shot1);
        this.addSound(10, 1.0f, 1000, R.raw.died1);
        this.addSound(20, 1.0f, 1000, R.raw.build1);
        //this.addSound(20, 1.0f, R.raw.creaturehappy);
        //this.addSound(30, 1.0f, R.raw.victory);
        //this.addSound(31, 1.0f, R.raw.defeat);
        
        random = new Random();
        
        // Load default sound on/off settings.
        SharedPreferences settings = baseContext.getSharedPreferences("Options", 0);
        playSound = settings.getBoolean("optionsSound", false);
	}

	/**
	 * Called by the constructor to pre-cache all sound files and save in an array.
	 *
	 * @param  context	The context of our activity.
	 * @return			void
	 */
	private void initSounds(Context context) { 
		 mContext = context;
		 // number 4 is the total number of concurrently playing sounds. if 4 are already
		 // playing the oldest one will be replaced. we can change if necessary.
	     mSoundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
	     mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE); 	     
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
	private void addSound(int index, float pitch, long delay, int soundId) {
		mSoundArray[index] = mSoundPool.load(mContext, soundId, 1);
		mSoundPitch[index] = pitch;
		mSoundDelay[index] = delay;
	}

	/**
	 * Plays a sound from the array of sounds cached.
	 *
	 * @param  index	Numerical int-value for the sound to play.
	 * @return			void
	 */
	public void playSound(int index) {
		//Log.d("SOUNDMANAGER", "Playing sound " + index);
		
		final long time = SystemClock.uptimeMillis();
		
		if (playSound && mSoundDelay[index] + mSoundDelayLast[index] < time) {
			mSoundDelayLast[index] = time;
			int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (mSoundPool.play(mSoundArray[index], streamVolume, streamVolume, 1, 0, mSoundPitch[index]) == 0) {
				Log.d("SOUNDMANAGER", "Failed to play " + index);
			}
		}
	}

    /**
	 * Plays a sound in a loop, e.g. background music or similar.
	 *
	 * @param  index	Numerical int-value for the sound.
	 * @return			void
	 */
	public void playLoopedSound(int index) {
	     int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC); 
	     mSoundPool.play(mSoundArray[index], streamVolume, streamVolume, 1, -1, 1f); 
	}
	
	/**
	 * Stops playing a sound or looped sound.
	 * 
	 * @param index		Index of sound to stop playing.
	 * @return			void
	 */
	public void stopSound(int index) {
		mSoundPool.stop(mSoundArray[index]);
	}
	
    /**
	 * Plays a random sound from the array of sounds cached.
	 *
	 * @param  i		Random minimum sound to play.
	 * @param  j		Random maximum sound to play.
	 * @return			void
	 */
	public void playSoundRandom(int i, int j) {
		playSound(i + random.nextInt(j));
	}
	
	/**
	 * Releases all cached soundfiles and returns their resources.
	 * It's a good idea to call this when done using SoundManager
	 * to preserve memory/CPU.
	 * 
	 * @return		void
	 */
	public void release() {
		mSoundPool.release();
	}

}
