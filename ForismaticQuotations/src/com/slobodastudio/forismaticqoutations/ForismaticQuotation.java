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
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import java.util.List;

public class ForismaticQuotation extends Activity {
	public final static String TAG = ForismaticQuotation.class.getSimpleName();
	private static TextView quotationText, quotationAuthor;
	public static ListView allList, favList;
	private static TabHost forismaticTabs;
	private static Intent service;
	private ForismaticDatabaseHelper forismaticDatabaseHelper = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forismatic_quotation);
		initView();
		
		forismaticTabs.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				Log.d(TAG, " Tab Clicked: " + tabId);														//For testing purposes
				List<QuotationData> quotationList;
				try {
					Dao<QuotationData, Integer> forismaticDao = getHelper().getQuotationDataDao();
					if (tabId.equals(getString(R.string.tab_all))) {
						quotationList = forismaticDao.queryForAll();
						Log.d(TAG, " Quotation list all size " + quotationList.size());														//For testing purposes
						allList.setAdapter(new QuotationsAdapter(getApplicationContext(), R.layout.listview_row, quotationList));
					} else if(tabId.equals(getString(R.string.tab_favourite))) {
						quotationList = forismaticDao.queryBuilder().where().eq(QuotationData.FAVOURITE, true).query();
						Log.d(TAG, " Quotation list fav size " + quotationList.size());	
						favList.setAdapter(new QuotationsAdapter(getApplicationContext(), R.layout.listview_row_fav, quotationList));
					}
				} catch (SQLException e) {
					Log.d(TAG, " Database exception " + e);														//For testing purposes
				}
			}
		});
		
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
		Log.d(TAG, " Service stopped!");																	//For testing purposes
		if (forismaticDatabaseHelper != null) {
			OpenHelperManager.releaseHelper();
			forismaticDatabaseHelper = null;
		}
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
	
	public void favQuotationClick(View v) {
		Log.d(TAG, " Checkbox click!");																	//For testing purposes
		if (((CheckBox) v).isChecked()) {
			//Do something when quotation is checked
		} else {
			//Do something when quotation is unchecked
		}
	}
	
	private void initView() {
		quotationText = (TextView) findViewById(R.id.quotationText);
		quotationAuthor = (TextView) findViewById(R.id.quotationAuthor);
		allList = (ListView) findViewById(R.id.allQuotationsList);
		favList = (ListView) findViewById(R.id.favQuotationsList);
		
		forismaticTabs = (TabHost) findViewById(android.R.id.tabhost);
		forismaticTabs.setup();		
		addTab(forismaticTabs, getString(R.string.tab_current), R.id.currentQuotation);
		addTab(forismaticTabs, getString(R.string.tab_all), R.id.allQuotations);
		addTab(forismaticTabs, getString(R.string.tab_favourite), R.id.favQuotations);
		forismaticTabs.setCurrentTabByTag(getString(R.string.tab_current));
	}

	private void addTab(TabHost host, String text, int view) {
		TabHost.TabSpec spec = host.newTabSpec(text);
		spec.setIndicator(text);
		spec.setContent(view);
		host.addTab(spec);
	}
	
	private ForismaticDatabaseHelper getHelper() {
		if (forismaticDatabaseHelper == null) {
			forismaticDatabaseHelper = OpenHelperManager.getHelper(this, ForismaticDatabaseHelper.class);
		}
		return forismaticDatabaseHelper;
	}
	
	private void activityIsShown(boolean state) {
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putBoolean(getString(R.string.is_shown), state);
		Log.d(TAG, " Activity state " + state);																//For testing purposes
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