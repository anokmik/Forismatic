package com.slobodastudio.forismaticqoutations;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ServiceHandler extends Handler {
	private final static String TAG = "Logs: ";	
	private TextView textHolder, authorHolder;
	
	public ServiceHandler(TextView textHolder, TextView authorHolder) {
		this.textHolder = textHolder;
		this.authorHolder = authorHolder;
	}
	
	@Override
	public void handleMessage(Message message) {
		super.handleMessage(message);
		Bundle data = (Bundle) message.obj;
		if (message.arg1 == 0 && data != null) {
			textHolder.setText(data.getString("text"));
			if (!data.getString("author").isEmpty()) {
				authorHolder.setVisibility(View.VISIBLE);
				authorHolder.setText(data.getString("author") + ".");
			} else {
				authorHolder.setVisibility(View.GONE);
			}
			Log.d(TAG, "QuotationLog Message Sent Success!");										//For testing purposes
		} else {
			Log.d(TAG, "QuotationLog Message Sent Failed!");										//For testing purposes
		}
	}
	
}