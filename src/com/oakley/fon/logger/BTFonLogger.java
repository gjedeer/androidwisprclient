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

import android.util.Log;

public class BTFonLogger extends WISPrLogger {
	protected static String TAG = BTFonLogger.class.getName();

	private static final String NETWORK_PREFIX = "BTFON/";

	@Override
	public LoggerResult login(String user, String password) {
		Log.d(TAG, "Login with " + NETWORK_PREFIX + user);
		return super.login(NETWORK_PREFIX + user, password);
	}
}
