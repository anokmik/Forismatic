package com.slobodastudio.forismaticqoutations;

import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class QuotationDownload extends Service {
	private final static String TAG = "Logs: ";																			//For testing purposes	
	private static SharedPreferences sharedPrefs;
	private static Messenger serviceMessenger;
	public static Timer refreshTimer;
	public static TimerTask downloadTimerTask;
	
	@Override
	public void onCreate() {
		super.onCreate();
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		refreshTimer = new Timer();
		//Register  receiver or handler for preferences change!
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent.getExtras() != null) {
			serviceMessenger = (Messenger) intent.getExtras().get("MESSENGER");
		}
		long refreshTime = Long.parseLong(sharedPrefs.getString("refreshTime", "30")) * 1000;
		Log.d(TAG, "Quotation download refresh time " + refreshTime);												//For testing purposes
		scheduleDownload(refreshTimer, refreshTime);
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
	}
	
	private void scheduleDownload(Timer timer, long time) {
		downloadTimerTask = new TimerTask() {
			@Override
			public void run() {
				boolean showNotifs = sharedPrefs.getBoolean("notifDisplay", false);
				boolean appInFront = sharedPrefs.getBoolean("isShown", true);
				Bundle serviceData = requestQuotation();
				Message servMsg = Message.obtain(null, 0, 0, 0, serviceData);
				try {
					serviceMessenger.send(servMsg);
					if (!appInFront && showNotifs) {
						downloadNotif(serviceData.getString("text"), serviceData.getString("author"));
					}
				} catch (Exception e) {
					Log.d(TAG, "Schedule download exception " + e.toString());											//For testing purposes
				}
			}
		};
		timer.scheduleAtFixedRate(downloadTimerTask, 0, time);
	}
	
	private Bundle requestQuotation() {
		String url = "http://api.forismatic.com/api/1.0/?method=getQuote&format=json&lang=ru";
		Bundle quotationData = new Bundle();
		DefaultHttpClient quotationHttpClient = new DefaultHttpClient();
		HttpResponse quotationResponse;
		try {
			quotationResponse = quotationHttpClient.execute(new HttpGet(url));
			JSONObject quotationJSON = new JSONObject(EntityUtils.toString(quotationResponse.getEntity(), "UTF-8"));
			if (quotationJSON != null) {
				quotationData.putString("text", quotationJSON.getString("quoteText"));
				quotationData.putString("author", quotationJSON.getString("quoteAuthor"));
			}
		} catch (Exception e) {
			Log.d(TAG, "Quotation Download Failed!");
		}
		return quotationData;	
	}
	
	private void downloadNotif(String notifTitle, String notifText) {
		NotificationManager notifMng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notif = new Notification(R.drawable.ic_launcher, notifTitle, 0);
		Intent intent = new Intent(this, ForismaticQuotation.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		notif.setLatestEventInfo(this, notifTitle, notifText, pendingIntent);
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notifMng.notify(23, notif);
	}

}