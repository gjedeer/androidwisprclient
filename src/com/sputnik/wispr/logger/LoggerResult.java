package com.sputnik.wispr.logger;

public class LoggerResult {
	protected String result;

	protected String logOffUrl;

	public LoggerResult(String result, String logOffUrl) {
		this.result = result;
		this.logOffUrl = logOffUrl;
	}

	public String getResult() {
		return result;
	}

	public String getLogOffUrl() {
		return logOffUrl;
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{result: " + result + ", logOffUrl:" + logOffUrl + "}";
	}
}
