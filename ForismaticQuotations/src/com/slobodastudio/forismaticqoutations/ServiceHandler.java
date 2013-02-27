package com.slobodastudio.forismaticqoutations;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import com.j256.ormlite.dao.Dao;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class ServiceHandler extends Handler {
	private final static String TAG = ServiceHandler.class.getSimpleName();	
	private final static String TEXT = "text";
	private final static String AUTHOR = "author";
	private String handlerText = null; 
	private String handlerAuthor = null;
	private Context handlerContext;
	private Dao<QuotationData, Integer> handlerDao;
	private ListView handlerAllListHolder;
	private TextView handlerTextHolder, handlerAthorHolder;
	
	public ServiceHandler(Context context, Dao<QuotationData,Integer> dao, ListView allListHolder, TextView textHolder, TextView authorHolder) {
		handlerContext = context;
		handlerDao = dao;
		handlerAllListHolder = allListHolder;
		handlerTextHolder = textHolder;
		handlerAthorHolder = authorHolder;
	}
	
	@Override
	public void handleMessage(Message message) {
		super.handleMessage(message);		
		Bundle data = (Bundle) message.obj;
		if (data != null) {
			Log.d(TAG, " Message Sent Success! Tab is All? " + message.what);								//For testing purposes
			handlerTextHolder.setText(data.getString(TEXT));
			if (!data.getString(AUTHOR).isEmpty()) {
				handlerAthorHolder.setVisibility(View.VISIBLE);
				handlerAthorHolder.setText(data.getString(AUTHOR) + ".");
			} else {
				handlerAthorHolder.setVisibility(View.GONE);
			}
			if (handlerText != null && handlerAuthor != null) {
				try {
					handlerDao.create(databaseEntry(handlerText, handlerAuthor));
					if (message.what == 1) {
						List<QuotationData> handlerList = handlerDao.queryForAll();
						Collections.reverse(handlerList);
						Log.d(TAG, " Quotation list all size " + handlerList.size());														//For testing purposes
						handlerAllListHolder.setAdapter(new QuotationsAdapter(handlerContext, R.layout.listview_row, handlerList));
					}
				} catch (SQLException e) {
					Log.d(TAG, " Database exception " + e);														//For testing purposes
				}
			}
			handlerText = data.getString(TEXT);
			handlerAuthor = data.getString(AUTHOR);
		} else {
			Log.d(TAG, " Message Sent Failed!");								//For testing purposes
		}
	}
	
	private QuotationData databaseEntry(String quotation, String author) {
		QuotationData entry = new QuotationData();
		entry.setQuotation(quotation);
		entry.setAuthor(author);
		entry.setFavourite(false);
		return entry;
	}	
}