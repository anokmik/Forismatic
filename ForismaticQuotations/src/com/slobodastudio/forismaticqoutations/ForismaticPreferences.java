package com.slobodastudio.forismaticqoutations;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class ForismaticPreferences extends PreferenceActivity {
	private final static String TAG = "Logs: ";	
	private SharedPreferences preferences;
	private Messenger preferencesMessenger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(preferencesChanged);		
		if (getIntent().getExtras() != null) {
			preferencesMessenger = (Messenger) getIntent().getExtras().get("MESSENGER");
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		preferencesIsShown(true);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		preferencesIsShown(false);
	}
	
	private void preferencesIsShown(boolean state) {
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean("isShown", state);
		Log.d(TAG, getLocalClassName() + " " + state);									//For testing purposes
		editor.commit();
	}
	
	OnSharedPreferenceChangeListener preferencesChanged = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			//Send intent to broadcast receiver with refresh time!
			/*
			//For testing purposes
			Log.d(TAG, "Preferences changed " + key);											//For testing purposes
			int refreshTimeValue = Integer.parseInt(preferences.getString("refreshTime", "30")) *1000;
			Message prefMsg = Message.obtain(null, refreshTimeValue);
			try {
				preferencesMessenger.send(prefMsg);
			} catch (Exception e) {
				Log.d(TAG, "Preferences exception " + e.toString());											//For testing purposes
			}
			//For testing purposes
			*/
		}
	};
	
}