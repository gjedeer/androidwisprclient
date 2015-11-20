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
package com.oakley.fon.logger;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import android.util.Log;

import com.oakley.fon.handler.WISPrInfoHandler;
import com.oakley.fon.handler.WISPrResponseHandler;
import com.oakley.fon.util.FONUtils;
import com.oakley.fon.util.HttpResult;
import com.oakley.fon.util.HttpUtils;
import com.oakley.fon.util.WISPrConstants;
import com.oakley.fon.util.WISPrUtil;

public class WISPrLogger implements WebLogger {

	private static final String TAG = WISPrLogger.class.getName();

	private static final String DEFAULT_LOGOFF_URL = "http://192.168.182.1:3990/logoff";

	protected String userParam = "UserName";

	protected String passwordParam = "Password";

	public LoggerResult login(String user, String password) {
		LoggerResult res = new LoggerResult(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR, null);
		try {
			HttpResult httpResult = HttpUtils.getUrl(BLOCKED_URL);
			String blockedUrlText = httpResult.getContent();
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
//			res = new LoggerResult(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR, null);
			res = new LoggerResult(e.getMessage(), null);
		}
		// Log.d(TAG, "WISPR Login Result: " + res);

		return res;
	}

	private LoggerResult tryToLogin(String user, String password, WISPrInfoHandler wisprInfo) throws IOException,
			ParserConfigurationException, FactoryConfigurationError {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		String logOffUrl = null;
		String targetURL = wisprInfo.getLoginURL();
		if (FONUtils.isSafeUrl(targetURL)) {
			Log.d(TAG, "Trying to Log " + targetURL);
			Map<String, String> data = new HashMap<String, String>();
			data.put(userParam, user);
			data.put(passwordParam, password);

			String htmlResponse = HttpUtils.getUrlByPost(targetURL, data).getContent();
			// Log.d(TAG, "HTML Reponse:" + htmlResponse);
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
						Log.e(TAG, saxe.getMessage());
						res = WISPrConstants.WISPR_NOT_PRESENT;
					}
				} else {
					// Log.d(TAG, "Response is null");
					res = WISPrConstants.WISPR_NOT_PRESENT;
				}
			}
		} else {
			Log.e(TAG, "Not safe URL:" + targetURL);
		}

		// If we dont find the WISPR Response or we cannot parse it, we check if we have connection
		// (some times happens)
		if (res.equals(WISPrConstants.WISPR_NOT_PRESENT)) {
			if (FONUtils.haveConnection()) {
				res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED;
				logOffUrl = DEFAULT_LOGOFF_URL;
			}
		}

		return new LoggerResult(res, logOffUrl);
	}
}
