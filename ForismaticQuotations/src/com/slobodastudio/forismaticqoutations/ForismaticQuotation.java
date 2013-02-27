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
import com.j256.ormlite.stmt.UpdateBuilder;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ForismaticQuotation extends Activity {
	public final static String TAG = ForismaticQuotation.class.getSimpleName();
	private static TextView quotationText, quotationAuthor;
	private static ListView allList, favList;
	private static TabHost forismaticTabs;
	private static Intent service;
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor sharedPrefEditor;
	private ForismaticDatabaseHelper forismaticDatabaseHelper = null;
	private Dao<QuotationData, Integer> forismaticDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forismatic_quotation);
		initView();

		try {
			forismaticDao = getHelper().getQuotationDataDao();
		} catch (SQLException e) {
			Log.d(TAG, " Database exception " + e);														//For testing purposes
		}
		
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
		sharedPrefEditor = sharedPreferences.edit();
		activityIsShown(sharedPrefEditor, true);
		tabIsShown(sharedPrefEditor, getString(R.string.tab_current));
		
		final Messenger messenger = new Messenger(new ServiceHandler(getBaseContext(), forismaticDao, allList, quotationText, quotationAuthor));
		service = new Intent(this, QuotationDownloadService.class);
		service.putExtra(getString(R.string.messenger), messenger);
		startService(service);

		forismaticTabs.setCurrentTabByTag(getString(R.string.tab_current));
		forismaticTabs.setOnTabChangedListener(new OnTabChangeListener() {
			@Override
			public void onTabChanged(String tabId) {
				tabIsShown(sharedPrefEditor, tabId);
				List<QuotationData> quotationList;
				try {
					if (tabId.equals(getString(R.string.tab_all))) {
						quotationList = forismaticDao.queryForAll();
						Collections.reverse(quotationList);
						Log.d(TAG, " Quotation list all size " + quotationList.size());														//For testing purposes
						allList.setAdapter(new QuotationsAdapter(getBaseContext(), R.layout.listview_row, quotationList));
					} else if(tabId.equals(getString(R.string.tab_favourite))) {
						quotationList = forismaticDao.queryBuilder().where().eq(QuotationData.FAVOURITE, true).query();
						Collections.reverse(quotationList);
						Log.d(TAG, " Quotation list fav size " + quotationList.size());														//For testing purposes
						favList.setAdapter(new QuotationsAdapter(getBaseContext(), R.layout.listview_row_fav, quotationList));
					}
				} catch (SQLException e) {
					Log.d(TAG, " Database exception " + e);														//For testing purposes
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		activityIsShown(sharedPrefEditor, true);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		activityIsShown(sharedPrefEditor, false);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopService(service);
		Log.d(TAG, " Service " + service + " stopped!");																	//For testing purposes
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
		Log.d(TAG, " Checkbox " + v.getTag() + " click!");																	//For testing purposes
		try {
			UpdateBuilder<QuotationData, Integer> updateBuilder = forismaticDao.updateBuilder();
			updateBuilder.where().eq("id", v.getTag());
			updateBuilder.updateColumnValue(QuotationData.FAVOURITE, ((CheckBox) v).isChecked());
			updateBuilder.update();
		} catch (SQLException e) {
			Log.d(TAG, " Database exception " + e);														//For testing purposes
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
	
	private void activityIsShown(SharedPreferences.Editor activityEditor, boolean state) {
		activityEditor.putBoolean(getString(R.string.app_is_shown), state);
		activityEditor.commit();
		Log.d(TAG, "Application in front " + state);																//For testing purposes
	}

	private void tabIsShown(SharedPreferences.Editor tabEditor, String string) {
		tabEditor.putString(getString(R.string.tab_is_shown), string);
		tabEditor.commit();
		Log.d(TAG, "Tab is shown " + string);																//For testing purposes
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