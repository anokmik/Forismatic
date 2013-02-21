package com.slobodastudio.forismaticqoutations;

import android.content.Context;
import android.os.AsyncTask;

public class DownloadTask extends AsyncTask<Void, Void, Integer> {
	
	DownloadTaskListener downloadTaskListener;
	
	public interface DownloadTaskListener {
		public void onDataReceived(String[] receivedData);
	}
	
	public DownloadTask(Context context) {
		downloadTaskListener = (DownloadTaskListener) context.getApplicationContext();
	}

	@Override
	protected Integer doInBackground(Void... arg0) {
		return QuotationDownload.i++;
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		String[] data = {"Text " + result, "Author " + result};
		downloadTaskListener.onDataReceived(data);
	}

}
