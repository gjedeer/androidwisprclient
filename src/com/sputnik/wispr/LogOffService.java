package com.sputnik.wispr;

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.sputnik.wispr.util.HttpUtils;

public class LogOffService extends IntentService {
	private static String TAG = LogOffService.class.getName();

	public LogOffService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String logOffUrl = intent.getStringExtra(this.getString(R.string.pref_logOffUrl));
		try {
			if (logOffUrl != null && logOffUrl.trim().length() > 0) {
				HttpUtils.getUrl(logOffUrl);
				Intent cleaningIntent = new Intent(this, NotificationCleaningService.class);
				cleaningIntent.setAction(NotificationCleaningService.ACTION_CLEAN);
				this.startService(cleaningIntent);
			}
		} catch (IOException e) {
			Log.e(TAG, "Error login off", e);
		}
	}

}
