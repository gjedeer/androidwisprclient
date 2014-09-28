package com.sputnik.wispr.logger;

public interface WebLogger {
	public static final String CONNECTED = "CONNECTED";

	public static final String BLOCKED_URL = "https://raw.githubusercontent.com/gjedeer/androidwisprclient/master/connected.txt";

	public LoggerResult login(String user, String password);
}
