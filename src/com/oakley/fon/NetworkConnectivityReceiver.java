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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BadParcelableException;
import android.util.Log;

import com.oakley.fon.util.FONUtils;
import com.oakley.fon.util.Utils;

public class NetworkConnectivityReceiver extends BroadcastReceiver {
	private static String TAG = NetworkConnectivityReceiver.class.getName();

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
			Log.v(TAG,
					"Connected. SSID:" + ssid + ", bssid:" + bssid + ", supplicantState:"
							+ connectionInfo.getSupplicantState());

			// We look if it's a FON Access Point
			if (FONUtils.isSupportedNetwork(ssid, bssid)) {

				boolean active = Utils.getBooleanPreference(context, R.string.pref_active, true);

				if (active) {
					String username = Utils.getStringPreference(context, R.string.pref_username, "");
					String password = Utils.getStringPreference(context, R.string.pref_password, "");
					if (username.trim().length() > 0 && password.trim().length() > 0) {
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

	private boolean isConnectedIntent(Intent intent) {
		NetworkInfo networkInfo = null;
		try {
			networkInfo = (NetworkInfo) intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
		} catch (BadParcelableException ignored) {}

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
}
