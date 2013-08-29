package com.zoki.podsetnik.logika;

import java.util.Date;

import com.zoki.podsetnik.gui.MainGui;

public class Countdown 
{
	long time_ms;
	MainGui gui;
	Thread thread;
	Date start_time;
	boolean is_running;
	public static Countdown start(long time_ms , MainGui gui)
	{
		return new Countdown(time_ms,gui);
	}
	
	private Countdown(long time_ms,MainGui gui)
	{
		this.time_ms = time_ms;
		this.gui=gui;
		startCountdown();
	}
	private void startCountdown()
	{
		start_time= new Date();
		is_running=true;
		thread = new Thread(){
			public void run()
			{				
				try {
					sleep(time_ms);
					gui.show_message();
					is_running=false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		
		thread.start();
	}

	public void notify_thread()
	{
		synchronized (thread) 
		{
			thread.notifyAll();
			time_ms = gui.sp.period;
		}
		startCountdown();
	}
	
	public long get_time_remaining()
	{
		long ret=0;
		Date curent=new Date();
		
		ret =time_ms - (curent.getTime() - start_time.getTime()); 
	//	System.out.println(curent);
	//	System.out.println(start_time);
	//	System.out.println((curent.getTime() - start_time.getTime()));
		
		return ret;
	}

	public boolean isIs_running() {
		return is_running;
	}
}
