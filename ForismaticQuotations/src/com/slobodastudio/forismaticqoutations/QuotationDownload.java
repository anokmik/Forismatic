package com.slobodastudio.forismaticqoutations;

import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;

public class QuotationDownload extends Service {
	private final static String TAG = "Logs: ";																//for testing purposes
	
	SharedPreferences sharedPrefs;
	NotificationManager notifManager;
	String refreshTime;
	Timer refreshTimer;
	TimerTask downloadTimerTask;
	
	int i = 0;																								//For testing purposes
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "Quotation Download Created!");
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		notifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "Quotation Download Command Start!");													//For testing purposes
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "Quotation Download Started!");															//For testing purposes
		refreshTime = sharedPrefs.getString("refreshTime", "30");	
		Log.d(TAG, "Quotation Download Refresh Time: " + refreshTime);										//For testing purposes
		refreshTimer = new Timer();
		downloadTimerTask = new TimerTask() {
			@Override
			public void run() {
				i++;																						//For testing purposes
				Log.d(TAG, "Task scheduled " + i + ".");													//For testing purposes
				downloadNotif("Download task completed", "Task scheduled " + i + ".");						//Change title and text
			}
		};
		refreshTimer.scheduleAtFixedRate(downloadTimerTask, 0, Long.parseLong(refreshTime) * 1000);
		super.onStart(intent, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		refreshTimer.cancel();
		Log.d(TAG, "Quotation Download Destroyed!");														//For testing purposes
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "Quotation Download Binded!");															//For testing purposes		
		return null;
	}
	
	private void downloadNotif(String notifTitle, String notifText) {
		Notification notif = new Notification(R.drawable.ic_launcher, notifTitle, 0);
		Intent intent = new Intent(this, ForismaticQuotation.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		notif.setLatestEventInfo(this, notifTitle, notifText, pendingIntent);
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notifManager.notify(23, notif);
	}

}