package com.slobodastudio.forismaticqoutations;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Timer;

public class PreferenceChangeReceiver extends BroadcastReceiver {
	private final static String TAG = "Logs: ";	
	public final static String ACTION_PREFERENCE_CHANGE = "PREFERENCE CHANGE";
	private Timer receiverTimer;
	private DownloadTimerTask receiverTimerTask;
	private Messenger receiverMessenger;
	
	public PreferenceChangeReceiver(Messenger messenger, DownloadTimerTask timerTask, Timer timer) {
		receiverTimer = timer;
		receiverTimerTask = timerTask;
		receiverMessenger = messenger;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {	
		if (receiverTimer != null) {
			receiverTimer.cancel();
			receiverTimerTask.cancel();
			receiverTimer = new Timer();
			SharedPreferences receiverSharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
			long receiverRefreshTime = Long.parseLong(receiverSharedPrefs.getString(context.getString(R.string.pref_refresh_time_key), "30")) * 1000;
			receiverTimerTask = new DownloadTimerTask(context, receiverMessenger);
			receiverTimer.scheduleAtFixedRate(receiverTimerTask, 0,receiverRefreshTime);
			Log.d(TAG, "Quotation download refresh time " + receiverRefreshTime);										//For testing purposes
		}
	}

}