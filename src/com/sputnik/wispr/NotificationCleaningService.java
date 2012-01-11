package com.sputnik.wispr;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.util.Log;

public class NotificationCleaningService extends IntentService {

	private static String TAG = NotificationCleaningService.class.getName();

	public static final String ACTION_CLEAN = "CLEAN";

	public NotificationCleaningService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(TAG, "Handling disconection intent: " + intent);
		if (intent.getAction().equals(ACTION_CLEAN)) {
			cleanNotification(this, intent);
			cleanLogOffUrl(this, intent);
		}
	}

	private void cleanLogOffUrl(Context context, Intent intent) {
		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		Editor editor = mPreferences.edit();
		editor.remove(context.getString(R.string.pref_logOffUrl));
		editor.commit();
	}

	private void cleanNotification(Context context, Intent intent) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.cancel(1);
	}
}
