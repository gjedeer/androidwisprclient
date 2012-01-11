package com.sputnik.wispr.logger;

import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

import android.util.Log;

import com.sputnik.wispr.handler.WISPrResponseHandler;
import com.sputnik.wispr.util.HttpUtils;
import com.sputnik.wispr.util.WISPrConstants;
import com.sputnik.wispr.util.WISPrUtil;

public class NeufLogger extends HTTPLogger {
	private static String TAG = NeufLogger.class.getName();

	public NeufLogger() {
		targetURL = "https://hotspot.neuf.fr/nb4_crypt.php";
	}

	@Override
	public LoggerResult login(String user, String password) {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		try {
			String blockedUrlText = HttpUtils.getUrl(BLOCKED_URL);
			if (!blockedUrlText.equals(CONNECTED)) {

				Map<String, String> refererParams = parseForm(blockedUrlText);
				if (!refererParams.isEmpty()) {
					Log.v(TAG, "refererParams:" + refererParams);

					Map<String, String> loginParams = new HashMap<String, String>();
					loginParams.put("lang", "fr");
					loginParams.put("challenge", refererParams.get("challenge"));
					loginParams.put("accessType", "fon");
					loginParams.put("userurl", refererParams.get("userurl") + ";fon;fr;4;"
							+ refererParams.get("userurl"));
					loginParams.put("nb4", refererParams.get("nb4") + "&challenge" + refererParams.get("challenge"));
					loginParams.put("ARCHI", refererParams.get("ARCHI"));

					Log.v(TAG, "loginParams:" + loginParams);

					loginParams.put(userParam, user);
					loginParams.put(passwordParam, password);
					String result = HttpUtils.getUrlByPost(targetURL, loginParams);
					Log.v(TAG, "Login result:" + result);

					String metaRefresh = HttpUtils.getMetaRefresh(result);
					if (metaRefresh != null) {
						Log.v(TAG, "meta refresh:" + metaRefresh);
						result = HttpUtils.getUrl(metaRefresh);
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
				.indexOf("<form id=\"portal\" name=\"portal\" action=\"https://hotspot.neuf.fr/nb4_crypt.php\" method=\"post\">");

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
