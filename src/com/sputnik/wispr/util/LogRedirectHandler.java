package com.sputnik.wispr.util;

import java.net.URI;

import org.apache.http.HttpResponse;
import org.apache.http.ProtocolException;
import org.apache.http.impl.client.DefaultRedirectHandler;
import org.apache.http.protocol.HttpContext;

import android.util.Log;

public class LogRedirectHandler extends DefaultRedirectHandler {
	private static String TAG = LogRedirectHandler.class.getName();

	@Override
	public URI getLocationURI(HttpResponse response, HttpContext context) throws ProtocolException {
		URI uri = super.getLocationURI(response, context);
		Log.d(TAG, "Redirecting to:" + uri.toString());
		return uri;
	}
}
