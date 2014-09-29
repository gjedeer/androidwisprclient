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

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

/**
 * Used for debugging purposes
 */
public class LogRedirectHandler extends DefaultRedirectHandler {
	private static String TAG = LogRedirectHandler.class.getName();

	@Override
	public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
		URI uri = super.getLocationURI(response, context);
		Log.d(TAG, "Redirecting to:" + uri.toString());
		return uri;
	}
}
