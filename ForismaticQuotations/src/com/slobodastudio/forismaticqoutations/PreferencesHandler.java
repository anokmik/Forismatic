package com.slobodastudio.forismaticqoutations;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class PreferencesHandler extends Handler {
	private final static String TAG = "Logs: ";	
	private Timer timerHandler;
	private TimerTask timerTaskHandler;
	
	public PreferencesHandler(Timer timerHandler, TimerTask timerTaskHandler) {
		this.timerHandler = timerHandler;
		this.timerTaskHandler = timerTaskHandler;
	}
	
	@Override
	public void handleMessage(Message message) {
		super.handleMessage(message);
		int time = message.what;
		timerHandler.cancel();
		timerHandler.scheduleAtFixedRate(timerTaskHandler, time, time);
		Log.d(TAG, "Tiemr restarted!");
	}
	
}