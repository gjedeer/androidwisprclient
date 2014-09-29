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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.oakley.fon.util.FONUtils;
import com.oakley.fon.util.Utils;

public class NetworkScanReceiver extends BroadcastReceiver {
	private static String TAG = NetworkScanReceiver.class.getName();

	private static long lastCalled = -1;

	private static final int MIN_PERIOD_BTW_CALLS = 10 * 1000;// 10 Seconds

	@Override
	public void onReceive(Context context, Intent intent) {
		long now = System.currentTimeMillis();

		// Log.d(TAG, "Action Received: " + intent.getAction() + " From intent: " + intent);

		if (lastCalled == -1 || (now - lastCalled > MIN_PERIOD_BTW_CALLS)) {
			lastCalled = now;
			if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
				boolean activeEnabled = Utils.getBooleanPreference(context, R.string.pref_active, true);
				boolean autoConnectEnabled = Utils.getBooleanPreference(context, R.string.pref_connectionAutoEnable,
						true);
				boolean areCredentialsConfigured = FONUtils.areCredentialsConfigured(context);

				if (autoConnectEnabled && activeEnabled && areCredentialsConfigured) {
					WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
					if (!isAlreadyConnected(wm)) {
						if (!isAnyPreferedNetworkAvailable(wm)) {
							ScanResult fonScanResult = getFonNetwork(wm);
							if (fonScanResult != null) {
								// Log.d(TAG, "Scan result found:" + fonScanResult);
								WifiConfiguration fonNetwork = lookupConfigurationByScanResult(
										wm.getConfiguredNetworks(), fonScanResult);
								// Log.d(TAG, "FON Network found:" + fonNetwork);
								if (fonNetwork == null) {
									fonNetwork = new WifiConfiguration();
									fonNetwork.SSID = '"' + fonScanResult.SSID + '"';
									fonNetwork.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
									fonNetwork.status = WifiConfiguration.Status.ENABLED;

									fonNetwork.networkId = wm.addNetwork(fonNetwork);
									wm.saveConfiguration();
								}
								// Log.v(TAG, "Selected network:" + fonNetwork);
								wm.enableNetwork(fonNetwork.networkId, false);
								Log.d(TAG, "Trying to connect");
							}// No FON Signal Available
						} else {
							Log.d(TAG, "Not connecting because a prefered network is available");
						}
					} else {
						Log.d(TAG, "Not connecting because it's already connected");
					}
					lastCalled = System.currentTimeMillis();

				} else {
					// Not Active in preferences
					Log.d(TAG, "App not active or not configured");
					Log.d(TAG, "activeEnabled=" + activeEnabled + ", autoConnectEnabled=" + autoConnectEnabled
							+ ", areCredentialsConfigured=" + areCredentialsConfigured);
				}
			}// Not Scanning State
		} else {
			Log.v(TAG, "Events to close, ignoring.");
		}
	}

	private boolean isAlreadyConnected(WifiManager wm) {
		boolean alreadyConnected = false;
		WifiInfo connectionInfo = wm.getConnectionInfo();
		if (connectionInfo != null) {
			SupplicantState supplicantState = connectionInfo.getSupplicantState();
			if (supplicantState != null) {
				alreadyConnected = supplicantState.equals(SupplicantState.ASSOCIATING)
						|| supplicantState.equals(SupplicantState.ASSOCIATED)
						|| supplicantState.equals(SupplicantState.COMPLETED)
						|| supplicantState.equals(SupplicantState.FOUR_WAY_HANDSHAKE)
						|| supplicantState.equals(SupplicantState.GROUP_HANDSHAKE);
			}
		}

		return alreadyConnected;
	}

	private WifiConfiguration lookupConfigurationByScanResult(List<WifiConfiguration> configuredNetworks,
			ScanResult scanResult) {
		boolean found = false;
		WifiConfiguration wifiConfiguration = null;
		Iterator<WifiConfiguration> it = configuredNetworks.iterator();
		while (!found && it.hasNext()) {
			wifiConfiguration = it.next();
			// Log.v(TAG, FONUtils.cleanSSID(wifiConfiguration.SSID) + " equals " +
			// FONUtils.cleanSSID(scanResult.SSID));
			if (wifiConfiguration.SSID != null) {
				found = FONUtils.cleanSSID(wifiConfiguration.SSID).equals(FONUtils.cleanSSID(scanResult.SSID));
			}
		}

		if (!found) {
			wifiConfiguration = null;
		}

		return wifiConfiguration;
	}

	private ScanResult getFonNetwork(WifiManager wm) {
		ScanResult scanResult = null;
		boolean found = false;

		List<ScanResult> scanResults = wm.getScanResults();
		if (scanResults != null) {
			Iterator<ScanResult> it = scanResults.iterator();
			while (!found && it.hasNext()) {
				scanResult = it.next();
				found = FONUtils.isSupportedNetwork(scanResult.SSID, scanResult.BSSID);
			}
			if (!found) {
				scanResult = null;
			}
		}

		return scanResult;
	}

	private boolean isAnyPreferedNetworkAvailable(WifiManager wm) {
		Set<String> scanResultsKeys = new HashSet<String>();
		boolean found = false;

		List<WifiConfiguration> configuredNetworks = wm.getConfiguredNetworks();
		if (configuredNetworks != null && !configuredNetworks.isEmpty()) {
			List<ScanResult> scanResults = wm.getScanResults();
			if (scanResults != null && !scanResults.isEmpty()) {
				// We load the SSIDs of the available networks
				for (ScanResult scanResult : scanResults) {
					scanResultsKeys.add(FONUtils.cleanSSID(scanResult.SSID));
					// Log.v(TAG, "Adding scanResultKey:" + FONUtils.cleanSSID(scanResult.SSID));
				}

				Iterator<WifiConfiguration> it = configuredNetworks.iterator();

				// We look for the Known networks
				while (!found && it.hasNext()) {
					WifiConfiguration wifiConfiguration = it.next();
					if (wifiConfiguration.SSID == null) {
						Log.v(TAG, "Removing null wifiConfiguration:" + wifiConfiguration);
						wm.removeNetwork(wifiConfiguration.networkId);
					} else if (!FONUtils.isSupportedNetwork(wifiConfiguration.SSID, wifiConfiguration.BSSID)) {
						found = scanResultsKeys.contains(FONUtils.cleanSSID(wifiConfiguration.SSID));
						// Log.v(TAG, "looking for: " + FONUtils.cleanSSID(wifiConfiguration.SSID) +
						// (found ? " match" : " NO match"));
					}
				}
			}
		}

		return found;
	}
}