package com.sputnik.wispr.logger;

import java.util.HashMap;
import java.util.Map;

public class BTFonLogger extends SimpleHTTPLogger {
	protected static String TAG = BTFonLogger.class.getName();

	private static final String NETWORK_PREFIX = "BTFON/";

	public BTFonLogger() {
		super("https://www.btopenzone.com:8443/ante");
	}

	@Override
	public Map<String, String> getPostParameters(String user, String password) {
		Map<String, String> postParams = new HashMap<String, String>();
		postParams.put(userParam, NETWORK_PREFIX + user);
		postParams.put(passwordParam, password);

		return postParams;
	}

	@Override
	protected String getLogOffUrl() {
		return "https://www.btopenzone.com:8443/accountLogoff/home?confirmed=true";
	}
}