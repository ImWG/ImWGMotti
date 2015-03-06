package com.imwg.imwgmotti;

import java.util.HashMap;
import java.util.Map;

import com.example.imwgmotti.R;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class SoundSpeaker extends SoundPool {
	
	Map<String, int[]> soundmap;
	
	Context context;
	
	static boolean enabled;
	
	
	public SoundSpeaker(Context context) {
		super(5, AudioManager.STREAM_SYSTEM, 5);
		this.context = context;
		this.soundmap = new HashMap<String, int[]>();
		
		loadSound("move", R.raw.move);
		loadSound("swap", R.raw.swap);
		loadSound("win", R.raw.youwin);
	}
	
	private void loadSound(String name, int id){
		int newid = load(context, id, 1);
		soundmap.put(name, new int[]{newid});
	}
	
	public void play(String name){
		if (!enabled)
			return;
		
		this.play(soundmap.get(name)[0], 1, 1, 0, 0, 1);
	}

}
