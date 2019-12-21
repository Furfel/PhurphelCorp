package com.furfel.phurphel;

import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.util.Log;

public class SoundControl {
	
	public static Context scontext;
	
	public static int[] soundStreams;
	public static final int[] soundsList={
		R.raw.s_can,
		R.raw.s_elevator,
		R.raw.s_wrong,
		R.raw.s_mchit,
		R.raw.s_click,
	};
	
	public static final int[] heavySoundList = {
		R.raw.s_welcome,
		R.raw.s_msgfirst,
	};
	
	public static final int[] musicList = {
		R.raw.s_bg1,
	};
	
	private static SoundPool pool;
	private static HashMap<Integer,Integer> soundMap;
	
	public static int bgStream, bgSample, heavySample;
	
	public static boolean loaded=false;
	
	public static void initSounds(Context context) {
		pool = new SoundPool(4,AudioManager.STREAM_MUSIC,100);
		scontext = context;
		soundStreams = new int[soundsList.length];
		pool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
			public void onLoadComplete(SoundPool paramSoundPool, int sample,
					int status) {
				if(sample==soundStreams[soundStreams.length-1])
					if(status==0)
						loaded=true;
			}
		});
		for(int i=0;i<soundsList.length;i++) {
			soundStreams[i]=pool.load(context, soundsList[i], 1);
		}
	}
	
	
	public static void playSound(int sound) {
		AudioManager mgr = (AudioManager)scontext.getSystemService(Context.AUDIO_SERVICE);
	    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
	    float volume = streamVolumeCurrent / streamVolumeMax;
	    pool.play(soundStreams[sound], volume, volume, 1, 0, 1f); 
	}
	
	public static void playHeavySound(int sound) {
		AudioManager mgr = (AudioManager)scontext.getSystemService(Context.AUDIO_SERVICE);
	    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	    final float volume = streamVolumeCurrent / streamVolumeMax;
	    pool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
	    	public void onLoadComplete(SoundPool paramSoundPool, int sample, int status) {
	    		if(sample==heavySample && status==0){
	    		    pool.play(heavySample, volume, volume, 1, 0, 1f);
	    		}
	    	}
	    });
	    heavySample = pool.load(scontext, heavySoundList[sound], 2);
	}
	
	public static void playBackgroundSound(int music) {
		AudioManager mgr = (AudioManager)scontext.getSystemService(Context.AUDIO_SERVICE);
	    float streamVolumeCurrent = mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
	    float streamVolumeMax = mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);    
	    final float volume = streamVolumeCurrent / streamVolumeMax;
	    pool.setOnLoadCompleteListener(new OnLoadCompleteListener(){
	    	public void onLoadComplete(SoundPool paramSoundPool, int sample, int status) {
	    		if(sample==bgSample && status==0){
	    		    bgStream = pool.play(bgSample, volume, volume, 1, -1, 1f);
	    		}
	    	}
	    });
	    bgSample = pool.load(scontext, musicList[music], 3);
	}
	
	public static void stopBackgroundSound() {
		pool.stop(bgStream);
	}
	
	public static void stopSounds() {
		if(pool!=null)
		pool.autoPause();
	}
	
	public static void resumeSounds() {
		if(pool!=null)
		pool.autoResume();
	}
}
