package com.slobodastudio.forismaticqoutations;

import android.os.Bundle;
import android.os.Messenger;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ForismaticQuotation extends Activity {
	public final static String TAG = "Logs: ";
	private static TextView quotationText, quotationAuthor;
	private static Intent service;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forismatic_quotation);
		initVIew();
		
		final Messenger messenger = new Messenger(new ServiceHandler(quotationText, quotationAuthor));
		service = new Intent(this, QuotationDownloadService.class);
		service.putExtra(getString(R.string.messenger), messenger);
		startService(service);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		activityIsShown(true);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		activityIsShown(false);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(service);
		Log.d(TAG, getLocalClassName() + " Service stopped!");											//For testing purposes
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_forismatic_quotation, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.share:
			checkAndShare(quotationText, quotationAuthor);
			break;
		case R.id.settings:
			openSettings();
			break;
		case R.id.exit:
			finish();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public void shareBtnClick(View v) {
		checkAndShare(quotationText, quotationAuthor);
	}
	
	public void settingsBtnClick(View v) {
		openSettings();
	}
	
	private void initVIew() {
		quotationText = (TextView) findViewById(R.id.quotationText);
		quotationAuthor = (TextView) findViewById(R.id.quotationAuthor);
		
		TabHost forismaticTabs = (TabHost) findViewById(android.R.id.tabhost);
		forismaticTabs.setup();		
		addTab(forismaticTabs, getString(R.string.tab_current), R.id.currentQuotation);
		addTab(forismaticTabs, getString(R.string.tab_all), R.id.allQuotations);
		addTab(forismaticTabs, getString(R.string.tab_favourite), R.id.favQuotations);
		forismaticTabs.setCurrentTabByTag(getString(R.string.tab_current));
		forismaticTabs.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				Log.d(TAG, "QuotationLog Tab Clicked: " + tabId);										//For testing purposes
			}
		});
	}
	
	private void addTab(TabHost host, String text, int view) {
		TabHost.TabSpec spec = host.newTabSpec(getString(R.string.tab_current));
		spec.setIndicator(text);
		spec.setContent(view);
		host.addTab(spec);
	}
	
	private void activityIsShown(boolean state) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(getString(R.string.is_shown), state);
		Log.d(TAG, getLocalClassName() + " " + state);													//For testing purposes
		editor.commit();
	}
	
	private void checkAndShare(TextView textToCheck, TextView authorToCheck) {
		if (textToCheck != null && !textToCheck.getText().toString().isEmpty()) {
			String shareText = textToCheck.getText().toString();
			if (authorToCheck != null) {
				String author = authorToCheck.getText().toString();
				if (!author.isEmpty()) {
					shareText += " " + author + ".";
				}
			}
			Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
			shareIntent.setType("text/plain");
			shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getString(R.string.share_subject));
			shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
			startActivity(Intent.createChooser(shareIntent, getText(R.string.share_chooser_title)));
		} else {
			Toast.makeText(getBaseContext(), getString(R.string.quotation_text_not_found), Toast.LENGTH_SHORT).show();
		}
	}
	
	private void openSettings() {
		startActivity(new Intent(this, ForismaticPreferences.class));
	}

}