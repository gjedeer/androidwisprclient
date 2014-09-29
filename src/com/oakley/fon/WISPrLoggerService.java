/*
 * This file is part of FONAccess.
 * 
 * FONAccess is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * FONAccess is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with FONAccess. If not, see <http://www.gnu.org/licenses/>.
 */
package com.oakley.fon;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.util.Log;

import com.oakley.fon.logger.BTFonLogger;
import com.oakley.fon.logger.LivedoorLogger;
import com.oakley.fon.logger.LoggerResult;
import com.oakley.fon.logger.NetiaLogger;
import com.oakley.fon.logger.NeufLogger;
import com.oakley.fon.logger.WISPrLogger;
import com.oakley.fon.logger.WebLogger;
import com.oakley.fon.util.FONUtils;
import com.oakley.fon.util.Utils;
import com.oakley.fon.util.WISPrConstants;

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
		if (FONUtils.isNeufBox(ssid, bssid)) {
			logger = new NeufLogger();
		} else if (FONUtils.isBtHub(ssid, bssid)) {
			logger = new BTFonLogger();
		} else if (FONUtils.isLivedoor(ssid, bssid)) {
			logger = new LivedoorLogger();
        } else if (FONUtil.isNetiaFonera(ssid, bssid)) {
            logger = new NetiaLogger();
		} else {
			logger = new WISPrLogger();
		}

		LoggerResult result = logger.login(username, password);
		Log.d(TAG, "LoggerResult:" + result);
		notifyConnectionResult(this, result, ssid);
		saveLogOffUrl(this, result);
		if (result.hasSucceded()) {
			FONUtils.cleanNetworks(this);
		}
	}

	private void notifyConnectionResult(Context context, LoggerResult result, String ssid) {
		int icon_ok = R.drawable.f;
		int icon_ko = R.drawable.f_error;

		long[] vibratePattern = null;
		String resultDesc = result.getResult();
		boolean notificationsActive = Utils.getBooleanPreference(context, R.string.pref_connectionNotificationsEnable,
				true);

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
			if (result.hasSucceded()) {
				notificationTitle = context.getString(R.string.notif_title_ok);
				notificationText = context.getString(R.string.notif_text_ok, ssid);
				appIntent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
				vibratePattern = new long[] { 100, 250 };

				pendingIntent = PendingIntent.getActivity(context, 1, appIntent, 0);
				notification = new Notification(icon_ok, notificationTitle, System.currentTimeMillis());
				notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT | Notification.FLAG_NO_CLEAR;
			} else if (result.hasFailed()) {
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
					boolean vibrate = Utils.getBooleanPreference(context, R.string.pref_connectionVibrate, false);
					if (vibrate) {
						notification.vibrate = vibratePattern;
					}
				}

				if (useRingtone) {
					String ringtone = Utils.getStringPreference(context, R.string.pref_connectionRingtone, null);
					if (ringtone == null) {
						notification.defaults |= Notification.DEFAULT_SOUND;
					} else {
						notification.sound = Uri.parse(ringtone);
					}
				}

				notificationManager.notify(1, notification);
			}
		}
	}

	private void saveLogOffUrl(Context context, LoggerResult result) {
		if (result != null && result.hasSucceded() && result.getLogOffUrl() != null
				&& result.getLogOffUrl().length() > 0) {
			Editor editor = Utils.getSharedPreferences(context).edit();
			editor.putString(context.getString(R.string.pref_logOffUrl), result.getLogOffUrl());
			editor.commit();
		}
	}
}
