package com.slobodastudio.forismaticqoutations;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.util.Log;
import java.util.TimerTask;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

public class DownloadTimerTask extends TimerTask {
	private final static String TAG = DownloadTimerTask.class.getSimpleName();
	private final static String URL = "http://api.forismatic.com/api/1.0/?method=getQuote&format=json&lang=ru";
	private final static String TEXT = "text";
	private final static String AUTHOR = "author";
	private final static String QUOTE_TEXT = "quoteText";
	private final static String QUOTE_AUTHOR = "quoteAuthor";
	private static SharedPreferences sharedPrefs;
	private static Messenger downloadMessenger;
	private Context downloadContext;
	
	public DownloadTimerTask(Context context, Messenger messenger) {
		downloadContext = context;
		downloadMessenger = messenger;
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	@Override
	public void run() {
		boolean showNotifs = sharedPrefs.getBoolean(downloadContext.getString(R.string.pref_notif_key), true);
		boolean appInFront = sharedPrefs.getBoolean(downloadContext.getString(R.string.is_shown), true);
		Bundle serviceData = requestQuotation(downloadContext.getString(R.string.quotation_text_default));
		Message servMsg = Message.obtain(null, 0, serviceData);
		try {
			downloadMessenger.send(servMsg);
			if (!appInFront && showNotifs) {
				downloadNotif(downloadContext, serviceData.getString(TEXT), serviceData.getString(AUTHOR));
			}
		} catch (Exception e) {
			Log.d(TAG, "Schedule download exception " + e.toString());														//For testing purposes
		}
	}
	
	private Bundle requestQuotation(String checkInternetConn) {
		Bundle quotationData = new Bundle();
		DefaultHttpClient quotationHttpClient = new DefaultHttpClient();
		HttpResponse quotationResponse;
		try {
			quotationResponse = quotationHttpClient.execute(new HttpGet(URL));
			JSONObject quotationJSON = new JSONObject(EntityUtils.toString(quotationResponse.getEntity(), "UTF-8"));
			if (quotationJSON != null) {
				quotationData.putString(TEXT, quotationJSON.getString(QUOTE_TEXT));
				quotationData.putString(AUTHOR, quotationJSON.getString(QUOTE_AUTHOR));
			}
		} catch (Exception e) {
			Log.d(TAG, "Quotation Download Failed!" + e.toString());														//For testing purposes
			quotationData.putString(TEXT, checkInternetConn);																//Change to string from activity
			quotationData.putString(AUTHOR, "");																			//Change to string from activity
		}
		return quotationData;	
	}
	
	private void downloadNotif(Context ctxt, String notifTitle, String notifText) {
		NotificationManager notifMng = (NotificationManager) ctxt.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notif = new Notification(R.drawable.ic_launcher, notifTitle, 0);
		Intent intent = new Intent(ctxt, ForismaticQuotation.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(ctxt, 0, intent, 0);
		notif.setLatestEventInfo(ctxt, notifTitle, notifText, pendingIntent);
		notif.flags |= Notification.FLAG_AUTO_CANCEL;
		notifMng.notify(23, notif);
	}

}