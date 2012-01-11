package com.sputnik.wispr.logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.sputnik.wispr.util.HttpUtils;

public class LivedoorLogger extends SimpleHTTPLogger {
	protected static String TAG = LivedoorLogger.class.getName();

	private static final String NETWORK_SUFIX = "@fon";

	public LivedoorLogger() {
		super("https://vauth.lw.livedoor.com/fauth/index");
	}

	@Override
	protected Map<String, String> getPostParameters(String user, String password) {
		Map<String, String> loginParams = new HashMap<String, String>();
		loginParams.put("sn", getSNParameter());
		loginParams.put("original_url", BLOCKED_URL);
		loginParams.put("name", user + NETWORK_SUFIX);
		loginParams.put("password", password);
		// Click coordinates on image button, really not needed
		loginParams.put("x", "66");
		loginParams.put("y", "15");

		return loginParams;
	}

	private String getSNParameter() {
		String sn = "001";
		try {
			String blockedUrlText = HttpUtils.getUrl(BLOCKED_URL);
			int start = blockedUrlText.indexOf("name=\"sn\" value=\"") + 17;
			int end = blockedUrlText.indexOf("\"", start);
			sn = blockedUrlText.substring(start, end);
		} catch (IOException e) {
			Log.e(TAG, "Error getting SN", e);
		}

		return sn;
	}

	@Override
	protected String getLogOffUrl() {
		return null;
	}
}