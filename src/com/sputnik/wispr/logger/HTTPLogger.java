package com.sputnik.wispr.logger;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public abstract class HTTPLogger implements WebLogger {
	protected static String TAG = HTTPLogger.class.getName();

	protected String targetURL = null;

	protected String userParam = "username";

	protected String passwordParam = "password";

	public abstract LoggerResult login(String user, String password);

	protected Map<String, String> parseUrl(String url) throws MalformedURLException {
		Log.d(TAG, "url to parse:" + url);
		Map<String, String> res = new HashMap<String, String>();
		String query = new URL(url).getQuery();
		String[] params = query.split("&");
		for (String param : params) {
			String[] split = param.split("=");
			res.put(split[0], URLDecoder.decode(split[1]));
		}
		return res;
	}

	abstract protected String getLogOffUrl();
}