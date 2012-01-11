package com.sputnik.wispr.logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.util.Log;

import com.sputnik.wispr.handler.WISPrInfoHandler;
import com.sputnik.wispr.handler.WISPrResponseHandler;
import com.sputnik.wispr.util.FONUtil;
import com.sputnik.wispr.util.HttpUtils;
import com.sputnik.wispr.util.WISPrConstants;
import com.sputnik.wispr.util.WISPrUtil;

public class WISPrLogger implements WebLogger {

	private static final String TAG = WISPrLogger.class.getName();

	private static final String DEFAULT_LOGOFF_URL = "http://192.168.182.1:3990/logoff";

	protected String userParam = "UserName";

	protected String passwordParam = "Password";

	public LoggerResult login(String user, String password) {
		LoggerResult res = new LoggerResult(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR, null);
		try {
			String blockedUrlText = HttpUtils.getUrl(BLOCKED_URL);
			if (!blockedUrlText.equalsIgnoreCase(CONNECTED)) {
				String WISPrXML = WISPrUtil.getWISPrXML(blockedUrlText);
				if (WISPrXML != null) {
					// Log.d(TAG, "XML Found:" + WISPrXML);
					WISPrInfoHandler wisprInfo = new WISPrInfoHandler();
					android.util.Xml.parse(WISPrXML, wisprInfo);

					if (wisprInfo.getMessageType().equals(WISPrConstants.WISPR_MESSAGE_TYPE_INITIAL)
							&& wisprInfo.getResponseCode().equals(WISPrConstants.WISPR_RESPONSE_CODE_NO_ERROR)) {
						res = tryToLogin(user, password, wisprInfo);
					}
				} else {
					// Log.d(TAG, "XML NOT FOUND : " + blockedUrlText);
					res = new LoggerResult(WISPrConstants.WISPR_NOT_PRESENT, null);
				}
			} else {
				res = new LoggerResult(WISPrConstants.ALREADY_CONNECTED, DEFAULT_LOGOFF_URL);
			}
		} catch (Exception e) {
			Log.e(TAG, "Error trying to log", e);
			res = new LoggerResult(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR, null);
		}
		// Log.d(TAG, "WISPR Login Result: " + res);

		return res;
	}

	private LoggerResult tryToLogin(String user, String password, WISPrInfoHandler wisprInfo) throws IOException,
			ParserConfigurationException, FactoryConfigurationError {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		String logOffUrl = null;
		String targetURL = wisprInfo.getLoginURL();
		Log.d(TAG, "Trying to Log " + targetURL);
		Map<String, String> data = new HashMap<String, String>();
		data.put(userParam, user);
		data.put(passwordParam, password);

		String htmlResponse = HttpUtils.getUrlByPost(targetURL, data);
		// Log.d(TAG, "WISPR Reponse:" + htmlResponse);
		if (htmlResponse != null) {
			String response = WISPrUtil.getWISPrXML(htmlResponse);
			if (response != null) {
				// Log.d(TAG, "WISPr response:" + response);
				WISPrResponseHandler wrh = new WISPrResponseHandler();
				try {
					android.util.Xml.parse(response, wrh);
					res = wrh.getResponseCode();
					logOffUrl = wrh.getLogoffURL();
				} catch (SAXException saxe) {
					res = WISPrConstants.WISPR_NOT_PRESENT;
				}
			} else {
				res = WISPrConstants.WISPR_NOT_PRESENT;
			}
		}

		// If we dont find the WISPR Response or we cannot parse it, we check if we have connection
		if (res.equals(WISPrConstants.WISPR_NOT_PRESENT)) {
			if (FONUtil.haveConnection()) {
				res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED;
			}
		}

		return new LoggerResult(res, logOffUrl);
	}
}