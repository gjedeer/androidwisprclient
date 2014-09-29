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
package com.oakley.fon.util;

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