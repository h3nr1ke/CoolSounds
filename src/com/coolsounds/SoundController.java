package com.coolsounds;

import android.content.Context;
import android.media.MediaPlayer;


public class SoundController {
	private MediaPlayer mp;
	private static SoundController sc;
	private Context c;
	public int status = 0;
	private boolean firstLoad = true;
	
	public SoundController(Context c){
		this.c = c;
	}

    public static SoundController getSoundController(Context c)
    {
      if (sc == null)
          // it's ok, we can call this constructor
          sc = new SoundController(c);
      return sc;
    }

	/**
	 * Load the sound in the soundsList variable
	 * */
	public void loadSound(int sound) {
		if (!this.firstLoad) {
			this.mp.release();
		}
		
		this.status = 1;
		
		this.mp = MediaPlayer.create(this.c, sound);
	}

	/**
	 * Stop any sound before start a new one
	 * */
	public void stopSounds() {
		if (!this.mp.equals(null) && this.mp.isPlaying()) {
			this.mp.stop();
			firstLoad = true;
		}
	}

	/**
	 * Start the sound previously loaded
	 * */
	public void startSound() {
		this.mp.start();
	}

	/**
	 * Initialize the passed sound
	 * */
	public void initSound(int sound) {

		this.loadSound(sound);
		this.startSound();
		if (firstLoad) {
			firstLoad = false;
		}
	}
}






