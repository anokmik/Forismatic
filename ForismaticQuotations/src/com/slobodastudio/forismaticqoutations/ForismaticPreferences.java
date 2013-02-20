package com.slobodastudio.forismaticqoutations;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class ForismaticPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		preferences.registerOnSharedPreferenceChangeListener(preferencesChanged);
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