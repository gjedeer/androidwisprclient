package com.sputnik.wispr.logger;

import java.io.IOException;
import java.util.Map;

import android.util.Log;

import com.sputnik.wispr.util.FONUtil;
import com.sputnik.wispr.util.HttpUtils;
import com.sputnik.wispr.util.WISPrConstants;

public abstract class SimpleHTTPLogger extends HTTPLogger {
	protected static String TAG = SimpleHTTPLogger.class.getName();

	protected SimpleHTTPLogger(String targetUrl) {
		this.targetURL = targetUrl;
	}

	@Override
	public LoggerResult login(String user, String password) {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		try {
			if (!FONUtil.haveConnection()) {
				Map<String, String> postParams = getPostParameters(user, password);
				Log.d(TAG, "Posting username & password");
				HttpUtils.getUrlByPost(targetURL, postParams);

				Log.d(TAG, "Verifying if now we have connection");

				if (FONUtil.haveConnection()) {
					res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED;
				}
			} else {
				res = WISPrConstants.ALREADY_CONNECTED;
			}
		} catch (IOException e) {
			Log.e(TAG, "Error trying to log", e);
			res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		}

		return new LoggerResult(res, getLogOffUrl());
	}

	abstract protected Map<String, String> getPostParameters(String user, String password);
}
