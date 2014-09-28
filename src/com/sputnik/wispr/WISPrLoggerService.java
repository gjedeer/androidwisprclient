package com.sputnik.wispr;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sputnik.wispr.logger.BTFonLogger;
import com.sputnik.wispr.logger.LivedoorLogger;
import com.sputnik.wispr.logger.LoggerResult;
import com.sputnik.wispr.logger.NeufLogger;
import com.sputnik.wispr.logger.NetiaLogger;
import com.sputnik.wispr.logger.WISPrLogger;
import com.sputnik.wispr.logger.WebLogger;
import com.sputnik.wispr.util.FONUtil;
import com.sputnik.wispr.util.WISPrConstants;

public class WISPrLoggerService extends IntentService {
	private static String TAG = WISPrLoggerService.class.getName();

	public WISPrLoggerService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(TAG, "starting service, received intent:" + intent);
		String password = intent.getStringExtra(this.getString(R.string.pref_password));
		String username = intent.getStringExtra(this.getString(R.string.pref_username));
		String ssid = intent.getStringExtra(this.getString(R.string.pref_ssid));
		String bssid = intent.getStringExtra(this.getString(R.string.pref_bssid));

		WebLogger logger = null;
		if (FONUtil.isNeufBox(ssid, bssid)) {
			logger = new NeufLogger();
		} else if (FONUtil.isBtHub(ssid, bssid)) {
			logger = new BTFonLogger();
		} else if (FONUtil.isLivedoor(ssid, bssid)) {
			logger = new LivedoorLogger();
		} else if (FONUtil.isNetiaFonera(ssid, bssid)) {
			logger = new NetiaLogger();
		} else {
			logger = new WISPrLogger();
		}

		LoggerResult result = logger.login(username, password);
		Log.d(TAG, "LoggerResult:" + result);
		notifyConnectionResult(this, result, ssid);
	}

	private void notifyConnectionResult(Context context, LoggerResult result, String ssid) {
		int icon_ok = R.drawable.f;
		int icon_ko = R.drawable.f_error;

		long[] vibratePattern = null;
		String resultDesc = result.getResult();
		SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean notificationsActive = mPreferences.getBoolean(context
				.getString(R.string.pref_connectionNotificationsEnable), false);
		if (notificationsActive) {
			String notificationTitle = null;
			String notificationText = null;
			NotificationManager notificationManager = (NotificationManager) context
					.getSystemService(Context.NOTIFICATION_SERVICE);

			if (ssid == null) {
				ssid = context.getString(R.string.notif_default_ssid);
			}

			Intent appIntent = null;
			Notification notification = null;
			PendingIntent pendingIntent = null;
			boolean useRingtone = true;
			boolean useVibration = true;
			Log.d(TAG, "Result=" + resultDesc);
			if (resultDesc.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED)
					|| resultDesc.equals(WISPrConstants.ALREADY_CONNECTED)) {
				notificationTitle = context.getString(R.string.notif_title_ok);
				notificationText = context.getString(R.string.notif_text_ok, ssid);
				appIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
				vibratePattern = new long[] { 100, 250 };

				pendingIntent = PendingIntent.getActivity(context, 1, appIntent, 0);
				notification = new Notification(icon_ok, notificationTitle, System.currentTimeMillis());
				notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
				if (result.getLogOffUrl() != null && result.getLogOffUrl().length() > 0) {
					Editor editor = mPreferences.edit();
					editor.putString(context.getString(R.string.pref_logOffUrl), result.getLogOffUrl());
					editor.commit();
				}
			} else if (!resultDesc.equals(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR)) {
				if (resultDesc.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_FAILED)) {
					resultDesc = context.getString(R.string.notif_error_100);
				} else if (resultDesc.equals(WISPrConstants.WISPR_NOT_PRESENT)) {
					resultDesc = context.getString(R.string.notif_error_1024);
				} else if (resultDesc.equals(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR)) {
					resultDesc = context.getString(R.string.notif_error_255);
				}

				notificationTitle = context.getString(R.string.notif_title_ko);
				notificationText = context.getString(R.string.notif_text_ko, resultDesc);
				appIntent = new Intent(context, AndroidWISPr.class);
				vibratePattern = new long[] { 100, 250, 100, 500 };

				pendingIntent = PendingIntent.getActivity(context, 1, appIntent, 0);
				notification = new Notification(icon_ko, notificationTitle, System.currentTimeMillis());
				notification.flags = notification.flags | Notification.FLAG_AUTO_CANCEL;
			}

			if (appIntent != null) {
				notification.setLatestEventInfo(context, notificationTitle, notificationText, pendingIntent);

				if (useVibration) {
					boolean vibrate = mPreferences
							.getBoolean(context.getString(R.string.pref_connectionVibrate), false);
					if (vibrate) {
						notification.vibrate = vibratePattern;
					}
				}

				if (useRingtone) {
					String ringtone = mPreferences.getString(context.getString(R.string.pref_connectionRingtone), "");
					if (!ringtone.equals("")) {
						notification.sound = Uri.parse(ringtone);
					}
				}

				notificationManager.notify(1, notification);
			}
		}
	}
}
