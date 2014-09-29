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

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

public abstract class HTTPLogger implements WebLogger {
	protected static String TAG = HTTPLogger.class.getName();

	protected String targetURL = null;

	protected String userParam = "username";

	protected String passwordParam = "password";

	public abstract LoggerResult login(String user, String password);

	protected Map<String, String> parseUrl(String url) throws MalformedURLException {
		Log.d(TAG, "url to parse:" + url);
		Map<String, String> res = new HashMap<String, String>();
		String query = new URL(url).getQuery();
		String[] params = query.split("&");
		for (String param : params) {
			String[] split = param.split("=");
			res.put(split[0], URLDecoder.decode(split[1]));
		}
		return res;
	}

	abstract protected String getLogOffUrl();
}