package com.zoki.podsetnik.logika;

public class SettingsPodsetnik {
	
	public static String TIMEOUT_TAG = "timeout";
	public static String PLAY_SOUND_TAG = "play_sound";
	
	public long period;
	public boolean play_sound;
	
	public Status status;

	public SettingsPodsetnik()
	{
		period = 0;
		play_sound = false;
		status = Status.PAUSE;
	}
}
