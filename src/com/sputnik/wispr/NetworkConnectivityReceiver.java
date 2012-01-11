package com.sputnik.wispr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.sputnik.wispr.util.FONUtil;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
	private static String TAG = NetworkConnectivityReceiver.class.getName();

	private SharedPreferences mPreferences;

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(TAG, "Action Received: " + action + " From intent: " + intent);

		// We look if we are connected
		if (isConnectedIntent(intent)) {
			WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo connectionInfo = wm.getConnectionInfo();
			String ssid = connectionInfo.getSSID();
			String bssid = connectionInfo.getBSSID();
			Log.v(TAG, "Conected. SSID:" + ssid + ", bssid:" + bssid + ", supplicantState:"
					+ connectionInfo.getSupplicantState());

			// We look if it's a FON Access Point
			if (FONUtil.isSupportedNetwork(ssid, bssid)) {
				boolean active = getPreferences(context).getBoolean(context.getString(R.string.pref_active), false);
				if (active) {
					String username = getPreferences(context).getString(context.getString(R.string.pref_username), "");
					String password = getPreferences(context).getString(context.getString(R.string.pref_password), "");
					if (username.length() > 0 && password.length() > 0) {
						// If the application is active and we have username and password we launch
						// the login intent
						Intent logIntent = new Intent(context, WISPrLoggerService.class);
						logIntent.setAction("LOG");
						logIntent.putExtra(context.getString(R.string.pref_username), username);
						logIntent.putExtra(context.getString(R.string.pref_password), password);
						logIntent.putExtra(context.getString(R.string.pref_ssid), ssid);
						logIntent.putExtra(context.getString(R.string.pref_bssid), bssid);
						context.startService(logIntent);
					} else {
						Log.v(TAG, "Username & Password not available");
						cleanNotification(context);
					}
				} else {
					Log.v(TAG, "Application inactive");
					cleanNotification(context);
				}
			} else {
				Log.v(TAG, "Not a FON Access Point");
				cleanNotification(context);
			}
		} else if (isDisconnectedIntent(intent)) {
			Log.v(TAG, "Disconnected");
			cleanNotification(context);
		}
	}

	private void cleanNotification(Context context) {
		Log.v(TAG, "Cleaning Notificacion Icon");
		Intent cleaningIntent = new Intent(context, NotificationCleaningService.class);
		cleaningIntent.setAction(NotificationCleaningService.ACTION_CLEAN);
		context.startService(cleaningIntent);
	}

	private SharedPreferences getPreferences(Context context) {
		if (mPreferences == null) {
			mPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		}

		return mPreferences;
	}

	private boolean isConnectedIntent(Intent intent) {
		NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);

		return (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
	}

	private boolean isDisconnectedIntent(Intent intent) {
		boolean res = false;
		NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		// Log.d(TAG, "NetworkInfo:" + networkInfo);

		if (networkInfo != null) {
			State state = networkInfo.getState();
			res = (state.equals(NetworkInfo.State.DISCONNECTING) || state.equals(NetworkInfo.State.DISCONNECTED))
					&& (networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
		} else {
			int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
			// Log.d(TAG, "wifiState:" + wifiState);
			if (wifiState == WifiManager.WIFI_STATE_DISABLED || wifiState == WifiManager.WIFI_STATE_DISABLING) {
				res = true;
			}
		}

		return res;
	}

	protected void logIntent(Intent intent) {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "intent.getAction():" + intent.getAction());
			Log.d(TAG, "intent.getData():" + intent.getData());
			Log.d(TAG, "intent.getDataString():" + intent.getDataString());
			Log.d(TAG, "intent.getScheme():" + intent.getScheme());
			Log.d(TAG, "intent.getType():" + intent.getType());
			Bundle extras = intent.getExtras();
			if (extras != null && !extras.isEmpty()) {
				for (String key : extras.keySet()) {
					Object value = extras.get(key);
					Log.d(TAG, "EXTRA: {" + key + "::" + value + "}");
				}
			} else {
				Log.d(TAG, "NO EXTRAS");
			}
		}
	}
}