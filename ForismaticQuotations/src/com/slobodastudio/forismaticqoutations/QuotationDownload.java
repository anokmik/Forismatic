package com.slobodastudio.forismaticqoutations;

import android.app.NotificationManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
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
	private final static String TAG = "Logs: ";																//For testing purposes
	
	SharedPreferences sharedPrefs;
	NotificationManager notifMng;
	Messenger messenger;
	Timer refreshTimer;
	TimerTask downloadTimerTask;
	long refreshTime;
	boolean showNotifs, applicationInFront;
	
	static int i = 0;																								//For testing purposes
	
	@Override
	public void onCreate() {
		super.onCreate();
		Log.d(TAG, "QuotationLog Download Created!");
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		notifMng = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "QuotationLog Download Command Start!");													//For testing purposes
		refreshTime = Long.parseLong(sharedPrefs.getString("refreshTime", "30")) * 1000;
		Log.d(TAG, "QuotationLog Download Refresh Time: " + refreshTime);									//For testing purposes
		
		//For testing purposes
		if (intent.getExtras() != null) {
			messenger = (Messenger) intent.getExtras().get("MESSENGER");
		}
		//For testing purposes
		
		refreshTimer = new Timer();
		downloadTimerTask = new TimerTask() {
			@Override
			public void run() {
//				i++;																						//For testing purposes
				Log.d(TAG, "QuotationLog Task scheduled " + i + ".");										//For testing purposes
				showNotifs = sharedPrefs.getBoolean("notifDisplay", false);
				applicationInFront = sharedPrefs.getBoolean("isShown", true);
				Log.d(TAG, "QuotationLog Download Show Notififcations: " + showNotifs);						//For testing purposes
				
				//For testing purposes				
				String[] serviceData = quotationRequest();				
				//String[] serviceData = {"Text " + i, "Author " + i};
				Message msg = Message.obtain();
				msg.arg1 = 0;
				msg.obj = serviceData;
				try {
					messenger.send(msg);
				} catch (Exception e) {
					Log.d(TAG, "QuotationLog Exception: " + e.toString());									//For testing purposes
				}
				if (!applicationInFront && showNotifs) {
					downloadNotif("Download task completed", "Task scheduled " + i + ".");					//Change title and text
				}
				
//				new DownloadTask(getBaseContext()).execute();
				//For testing purposes
			}
		};
		refreshTimer.scheduleAtFixedRate(downloadTimerTask, refreshTime, refreshTime);
		return super.onStartCommand(intent, flags, startId);
	}
	
	private static String[] quotationRequest() {
		DefaultHttpClient quotationHttpClient = new DefaultHttpClient();
		HttpResponse quotationResponse;
		JSONObject quotationJSON;
		String url = "http://api.forismatic.com/api/1.0/?method=getQuote&format=json";
		String[] quotationData = {"",""};
		
		try {
			quotationResponse = quotationHttpClient.execute(new HttpGet(url));
			quotationJSON = new JSONObject(EntityUtils.toString(quotationResponse.getEntity())); 			//Problem: JSONObject equals null
			if (quotationJSON != null) {
				quotationData[0] = quotationJSON.getString("quoteText");
				quotationData[1] = quotationJSON.getString("quoteAuthor");				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return quotationData;	
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		refreshTimer.cancel();
		Log.d(TAG, "QuotationLog Download Destroyed!");														//For testing purposes
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "QuotationLog Download Binded!");														//For testing purposes		
		return null;
	}
	
	private void downloadNotif(String notifTitle, String notifText) {
		Notification notif = new Notification(R.drawable.ic_launcher, notifTitle, 0);
		Intent intent = new Intent(this, ForismaticQuotation.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
		notif.setLatestEventInfo(this, notifTitle, notifText, pendingIntent);
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notifMng.notify(23, notif);
	}

}