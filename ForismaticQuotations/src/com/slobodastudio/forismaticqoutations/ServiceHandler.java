package com.slobodastudio.forismaticqoutations;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class ServiceHandler extends Handler {
	private final static String TAG = ServiceHandler.class.getSimpleName();	
	private final static String TEXT = "text";
	private final static String AUTHOR = "author";
	private TextView handlerTextHolder, handlerAthorHolder;
	
	public ServiceHandler(TextView textHolder, TextView authorHolder) {
		handlerTextHolder = textHolder;
		handlerAthorHolder = authorHolder;
	}
	
	@Override
	public void handleMessage(Message message) {
		super.handleMessage(message);
		Bundle data = (Bundle) message.obj;
		if (message.what == 0 && data != null) {
			handlerTextHolder.setText(data.getString(TEXT));
			if (!data.getString(AUTHOR).isEmpty()) {
				handlerAthorHolder.setVisibility(View.VISIBLE);
				handlerAthorHolder.setText(data.getString(AUTHOR) + ".");
			} else {
				handlerAthorHolder.setVisibility(View.GONE);
			}
			Log.d(TAG, " Message Sent Success!");								//For testing purposes
		} else {
			Log.d(TAG, " Message Sent Failed!");								//For testing purposes
		}
	}

}