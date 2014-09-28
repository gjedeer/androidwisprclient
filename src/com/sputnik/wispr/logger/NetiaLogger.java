package com.sputnik.wispr.logger;

import java.util.HashMap;
import java.util.Map;

public class NetiaLogger extends SimpleHTTPLogger {
	protected static String TAG = NetiaLogger.class.getName();

	public NetiaLogger() {
		super("https://netia.portal.fon.com/en/login/processLogin");
	}

	@Override
	public Map<String, String> getPostParameters(String user, String password) {
		Map<String, String> postParams = new HashMap<String, String>();
		postParams.put("login[user]", user);
		postParams.put("login[pass]", password);
		postParams.put("login[provider]", "netia_others");
		postParams.put("commit", "Login");

		return postParams;
	}

	@Override
	protected String getLogOffUrl() { // TODO figure it out
		return null;
	}
}
