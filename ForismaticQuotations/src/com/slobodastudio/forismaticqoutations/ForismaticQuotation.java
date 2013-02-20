package com.slobodastudio.forismaticqoutations;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class ForismaticQuotation extends Activity {
	private final static String CURRENT = "CURRENT QUOTATION";
	private final static String ALL = "ALL QUOTATIONS";
	private final static String FAVOURITE = "FAVOURITE QUOTATIONS";
	private final static String SHARE_SUBJECT = "Forismatic quotation";
	private final static String SHARE_CHOOSER_TITLE = "Choose place to share:";
	private final static String TAG = "Logs: ";
	
	TextView quotationText, quotationAuthor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forismatic_quotation);
		quotationText = (TextView) findViewById(R.id.quotationText);
		quotationAuthor = (TextView) findViewById(R.id.quotationAuthor);
		
		TabHost forismaticTabs = (TabHost) findViewById(android.R.id.tabhost);
		forismaticTabs.setup();	
		TabHost.TabSpec forismaticTabSpec;
		
		forismaticTabSpec = forismaticTabs.newTabSpec(CURRENT);
		forismaticTabSpec.setIndicator("Current");
		forismaticTabSpec.setContent(R.id.currentQuotation);
		forismaticTabs.addTab(forismaticTabSpec);
		
		forismaticTabSpec = forismaticTabs.newTabSpec(ALL);
		forismaticTabSpec.setIndicator("All");
		forismaticTabSpec.setContent(R.id.allQuotations);
		forismaticTabs.addTab(forismaticTabSpec);
		
		forismaticTabSpec = forismaticTabs.newTabSpec(FAVOURITE);
		forismaticTabSpec.setIndicator("Favourite");
		forismaticTabSpec.setContent(R.id.favQuotations);
		forismaticTabs.addTab(forismaticTabSpec);
		
		forismaticTabs.setCurrentTabByTag(CURRENT);
		forismaticTabs.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				Log.d(TAG, "Quotation Tab Clicked: " + tabId);												//For testing purposes
			}
		});
		
		startService(new Intent(this, QuotationDownload.class));
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
	
	private void checkAndShare(TextView textToCheck, TextView authorToCheck) {
		if (textToCheck != null && !textToCheck.getText().toString().equals("")) {
			String shareText = textToCheck.getText().toString();
			if (!shareText.equals("")) {
				if (authorToCheck != null) {
					String author = authorToCheck.getText().toString();
					if (!author.equals("")) {
						shareText += " Author: " + author;
					}
				}			
				Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
				shareIntent.setType("text/plain");
				shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, SHARE_SUBJECT);
				shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareText);
				startActivity(Intent.createChooser(shareIntent, SHARE_CHOOSER_TITLE));
			}
		} else {
			Toast.makeText(getBaseContext(), "No text found for sharing!", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void openSettings() {
		Intent settings = new Intent(this, ForismaticPreferences.class);
		startActivity(settings);
	} 
	
}