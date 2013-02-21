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
	SharedPreferences preferences;

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
		editor.putBoolean("isShown", state);
		Log.d(TAG, "QuotationLog Application Is Shown State " + state);														//For testing purposes
		editor.commit();
	}
	
	OnSharedPreferenceChangeListener preferencesChanged = new OnSharedPreferenceChangeListener() {
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			if (key.equals("refreshTime")) {
				restartService();
			}
		}
	};
	
	private void restartService() {
		stopService(new Intent(getApplicationContext(), QuotationDownload.class));
		startService(new Intent(getApplicationContext(), QuotationDownload.class));
	}
	
}