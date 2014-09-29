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
import java.util.Map;

import android.util.Log;

import com.oakley.fon.util.FONUtils;
import com.oakley.fon.util.HttpUtils;
import com.oakley.fon.util.WISPrConstants;

public abstract class SimpleHTTPLogger extends HTTPLogger {
	protected static String TAG = SimpleHTTPLogger.class.getName();

	protected SimpleHTTPLogger(String targetUrl) {
		this.targetURL = targetUrl;
	}

	@Override
	public LoggerResult login(String user, String password) {
		String res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		try {
			if (!FONUtils.haveConnection()) {
				if (FONUtils.isSafeUrl(targetURL)) {
					Map<String, String> postParams = getPostParameters(user, password);
					Log.d(TAG, "Posting username & password");
					HttpUtils.getUrlByPost(targetURL, postParams);

					Log.d(TAG, "Verifying if now we have connection");

					if (FONUtils.haveConnection()) {
						res = WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED;
					}
				} else {
					Log.e(TAG, "Not safe URL:" + targetURL);
				}
			} else {
				res = WISPrConstants.ALREADY_CONNECTED;
			}
		} catch (IOException e) {
			Log.e(TAG, "Error trying to log", e);
			Log.v(TAG, "Failed login URL: " + targetURL);
			res = WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR;
		}

		return new LoggerResult(res, getLogOffUrl());
	}

	abstract protected Map<String, String> getPostParameters(String user, String password);
}
