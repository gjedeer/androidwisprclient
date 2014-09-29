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
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

/**
 * Make all redirects to go to http in stead of http<b>s</b><br/>
 * Only used for testing purposes
 */
public class RemoveHttpsRedirectHandler extends DefaultRedirectHandler {
	private static String TAG = RemoveHttpsRedirectHandler.class.getName();

	@Override
	public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
		String uri = super.getLocationURI(response, context).toString();
		if (uri.startsWith("https")) {
			uri = uri.replaceFirst("https", "http");
			Log.d(TAG, "Removing https from redirect:" + uri);
		}
		URI notSafeUri = null;
		try {
			notSafeUri = new URI(uri);
		} catch (URISyntaxException e) {
			Log.e(TAG, "error change URI", e);
		}
		return notSafeUri;
	}
}