package com.slobodastudio.forismaticqoutations;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;

public class ForismaticPreferences extends PreferenceActivity {
	private final static String TAG = "Logs: ";	
	private final static String TIME = "time";	
	private SharedPreferences preferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(preferencesChanged);
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
		editor.putBoolean(getString(R.string.is_shown), state);
		Log.d(TAG, getLocalClassName() + " " + state);																						//For testing purposes
		editor.commit();
	}
	
	OnSharedPreferenceChangeListener preferencesChanged = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			Log.d(TAG, getLocalClassName() + " " + key);																					//For testing purposes
			if (key.equals(getString(R.string.pref_refresh_time_key))) {
				int refreshTimeValue = Integer.parseInt(preferences.getString(getString(R.string.pref_refresh_time_key), "30")) *1000;
				Intent broadcastIntent = new Intent();
				broadcastIntent.setAction(PreferenceChangeReceiver.ACTION_PREFERENCE_CHANGE);
				broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
				broadcastIntent.putExtra(TIME, refreshTimeValue);
				sendBroadcast(broadcastIntent);
			}
		}
	};

}