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

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

import android.util.Log;

import com.oakley.fon.handler.WISPrResponseHandler;
import com.oakley.fon.util.FONUtils;
import com.oakley.fon.util.HttpResult;
import com.oakley.fon.util.HttpUtils;
import com.oakley.fon.util.WISPrConstants;
import com.oakley.fon.util.WISPrUtil;

public class NeufLogger extends HTTPLogger {
	private static String TAG = NeufLogger.class.getName();

	public NeufLogger() {
		targetURL = "https://hotspot.wifi.sfr.fr/nb4_crypt.php";
	}

	@Override
	public LoggerResult login(String user, String password) {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		try {
			HttpResult httpResult = HttpUtils.getUrl(BLOCKED_URL);
			if (FONUtils.isSafeUrl(httpResult.getTargetHost())) {
				String blockedUrlText = httpResult.getContent();
				if (!blockedUrlText.equals(CONNECTED)) {

					Map<String, String> refererParams = parseForm(blockedUrlText);
					if (!refererParams.isEmpty()) {
						Log.v(TAG, "refererParams:" + refererParams);

						Map<String, String> loginParams = new HashMap<String, String>();
						loginParams.put("lang", "fr");
						loginParams.put("challenge", refererParams.get("challenge"));
						loginParams.put("accessType", "fon");
						loginParams.put("userurl",
								refererParams.get("userurl") + ";fon;fr;4;" + refererParams.get("userurl"));
						loginParams
								.put("nb4", refererParams.get("nb4") + "&challenge" + refererParams.get("challenge"));
						loginParams.put("ARCHI", refererParams.get("ARCHI"));

						Log.v(TAG, "loginParams:" + loginParams);

						loginParams.put(userParam, user);
						loginParams.put(passwordParam, password);
						String result = HttpUtils.getUrlByPost(targetURL, loginParams).getContent();
						Log.v(TAG, "Login result:" + result);

						String metaRefresh = HttpUtils.getMetaRefresh(result);
						if (metaRefresh != null) {
							Log.v(TAG, "meta refresh:" + metaRefresh);
							result = HttpUtils.getUrl(metaRefresh).getContent();
							Log.v(TAG, "Login result after refresh:" + result);
							if (hasLoginSuceeded(result)) {
								res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED;
							} else {
								res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_FAILED;
							}
						}
					} else {
						Log.v(TAG, "Form NOT FOUND : " + blockedUrlText);
						res = WISPrConstants.WISPR_NOT_PRESENT;
					}
				} else {
					res = WISPrConstants.ALREADY_CONNECTED;
				}
			} else {
				Log.e(TAG, "Not safe URL:" + httpResult.getTargetHost());
			}
		} catch (Exception e) {
			Log.e(TAG, "Error trying to log", e);
			res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		}

		return new LoggerResult(res, getLogOffUrl());
	}

	private boolean hasLoginSuceeded(String html) throws SAXException {
		html = html.replace("<!--", "<");
		html = html.replace("-->", ">");
		String xml = WISPrUtil.getWISPrXML(html);

		WISPrResponseHandler wrh = new WISPrResponseHandler();
		android.util.Xml.parse(xml, wrh);

		return (wrh.getReplyMessage().equals("Authentication Success"));
	}

	private Map<String, String> parseForm(String html) {
		Map<String, String> data = new HashMap<String, String>();
		int start = html
				.indexOf("<form id=\"portal\" name=\"portal\" action=\"https://hotspot.wifi.sfr.fr/nb4_crypt.php\" method=\"post\">");

		int end = html.indexOf("</form>", start) + 7;
		if (start > -1 && end > -1) {
			String htmlForm = new String(html.substring(start, end));
			data.put("challenge", getValue(htmlForm, "challenge"));
			data.put("nb4", getValue(htmlForm, "nb4"));
			data.put("userurl", getValue(htmlForm, "userurl"));
			data.put("ARCHI", getValue(htmlForm, "ARCHI"));
		}

		return data;
	}

	private String getValue(String htmlForm, String name) {
		String res = null;
		int start = htmlForm.indexOf("name=\"" + name + "\" value=\"");
		if (start > -1)
			start += 15 + name.length();

		int end = htmlForm.indexOf('"', start);
		if (end > -1) {
			res = htmlForm.substring(start, end);
		}

		return res;
	}

	@Override
	protected String getLogOffUrl() {
		return "http://192.168.2.1:3990/logoff";
	}
}
