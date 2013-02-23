package com.slobodastudio.forismaticqoutations;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Timer;

public class QuotationDownloadService extends Service {
	private final static String TAG = "Logs: ";
	private static Timer refreshTimer;
	private static DownloadTimerTask downloadTimerTask;
	private static Messenger serviceMessenger;
	private PreferenceChangeReceiver receiver;
	
	@Override
	public void onCreate() {
		super.onCreate();
		refreshTimer = new Timer();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getExtras() != null) {
			serviceMessenger = (Messenger) intent.getExtras().get(getString(R.string.messenger));
		}
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		long refreshTime = Long.parseLong(sharedPrefs.getString(getString(R.string.pref_refresh_time_key), "30")) * 1000;
		downloadTimerTask = new DownloadTimerTask(this, serviceMessenger);
		refreshTimer.scheduleAtFixedRate(downloadTimerTask, 0, refreshTime);
		Log.d(TAG, "Quotation download refresh time " + refreshTime);														//For testing purposes
		
		IntentFilter filter = new IntentFilter(PreferenceChangeReceiver.ACTION_PREFERENCE_CHANGE);
		filter.addCategory(Intent.CATEGORY_DEFAULT);
		receiver = new PreferenceChangeReceiver(serviceMessenger, downloadTimerTask, refreshTimer);
		registerReceiver(receiver, filter);
		
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		refreshTimer.cancel();
		downloadTimerTask.cancel();
		unregisterReceiver(receiver);
	}

}