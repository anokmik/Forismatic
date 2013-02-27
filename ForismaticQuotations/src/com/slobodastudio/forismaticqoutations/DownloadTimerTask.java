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
	private static String prefNotifKey, appIsShown, tabIsShown, tabAll, defQuotationText;
	private static SharedPreferences sharedPrefs;
	private static Messenger downloadMessenger;
	private Context downloadContext;
	
	public DownloadTimerTask(Context context, Messenger messenger) {
		downloadContext = context;
		downloadMessenger = messenger;
		sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		prefNotifKey = context.getString(R.string.pref_notif_key);
		appIsShown = context.getString(R.string.app_is_shown);
		tabIsShown = context.getString(R.string.tab_is_shown);
		tabAll = context.getString(R.string.tab_all);
		defQuotationText = context.getString(R.string.quotation_text_default);
	}
	
	@Override
	public void run() {
		boolean showNotifs = sharedPrefs.getBoolean(prefNotifKey, true);
		boolean appInFront = sharedPrefs.getBoolean(appIsShown, true);
		int allTabIsShown = sharedPrefs.getString(tabIsShown, null).equals(tabAll) ? 1 : 0;
		Bundle serviceData = requestQuotation(defQuotationText);
		Message servMsg = Message.obtain(null, allTabIsShown, serviceData);
		try {
			downloadMessenger.send(servMsg);
			if (!appInFront && showNotifs) {
				downloadNotif(downloadContext, serviceData.getString(TEXT), serviceData.getString(AUTHOR));
			}
		} catch (Exception e) {
			Log.d(TAG, "Schedule download exception " + e);														//For testing purposes
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
			quotationData.putString(TEXT, checkInternetConn);
			quotationData.putString(AUTHOR, "");
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