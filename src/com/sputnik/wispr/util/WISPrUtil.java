package com.sputnik.wispr.util;

public class WISPrUtil {
	public static final String WISPR_TAG_NAME = "WISPAccessGatewayParam";

	public static String getWISPrXML(String source) {
		String res = null;
		int start = source.indexOf("<" + WISPR_TAG_NAME);
		int end = source.indexOf("</" + WISPR_TAG_NAME + ">", start) + WISPR_TAG_NAME.length() + 3;
		if (start > -1 && end > -1) {
			res = new String(source.substring(start, end));
			if (!res.contains("&amp;")) {
				res = res.replace("&", "&amp;");
			}
		}

		return res;
	}
}