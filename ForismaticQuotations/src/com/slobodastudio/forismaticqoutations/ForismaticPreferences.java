package com.slobodastudio.forismaticqoutations;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class ForismaticPreferences extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}