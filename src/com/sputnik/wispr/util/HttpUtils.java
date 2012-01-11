package com.sputnik.wispr.util;

import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpUtils {
	private static final int DEFAULT_MAX_RETRIES = 3;

	private static String TAG = HttpUtils.class.getName();

	private static final String UTF8 = "UTF-8";

	private static HttpParams defaultHttpParams = new BasicHttpParams();

	static {
		defaultHttpParams.setParameter(CoreProtocolPNames.USER_AGENT, "FONAccess; wispr; (Linux; U; Android)");
	}

	public static String getUrl(String url) throws IOException {
		return getUrl(url, DEFAULT_MAX_RETRIES);
	}

	public static String getUrl(String url, int maxRetries) throws IOException {
		String result = null;
		int retries = 0;
		DefaultHttpClient httpclient = new DefaultHttpClient(defaultHttpParams);
		httpclient.setCookieStore(null);
		HttpGet httpget = new HttpGet(url);
		while (retries <= maxRetries && result == null) {
			try {
				retries++;
				HttpEntity entity = httpclient.execute(httpget).getEntity();

				if (entity != null) {
					result = EntityUtils.toString(entity).trim();
				}
			} catch (SocketException se) {
				if (retries > maxRetries) {
					throw se;
				} else {
					Log.v(TAG, "SocketException, retrying " + retries);
				}
			}
		}

		return result;
	}

	public static String getUrlByPost(String url, Map<String, String> params, int maxRetries) throws IOException {
		return getUrlByPost(url, params, null, maxRetries);
	}

	public static String getUrlByPost(String url, Map<String, String> params) throws IOException {
		return getUrlByPost(url, params, DEFAULT_MAX_RETRIES);
	}

	public static String getUrlByPost(String url, Map<String, String> params, Map<String, String> headers,
			int maxRetries) throws IOException {
		String result = null;
		int retries = 0;
		DefaultHttpClient httpclient = new DefaultHttpClient(defaultHttpParams);
		httpclient.setCookieStore(null);

		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		if (params != null) {
			Set<Entry<String, String>> paramsSet = params.entrySet();
			for (Entry<String, String> entry : paramsSet) {
				formParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}

		UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(formParams, UTF8);
		HttpPost httppost = new HttpPost(url);
		httppost.setEntity(postEntity);

		if (headers != null) {
			Set<Entry<String, String>> headersSet = headers.entrySet();
			for (Entry<String, String> entry : headersSet) {
				httppost.setHeader(entry.getKey(), entry.getValue());
			}
		}

		while (retries < maxRetries && result == null) {
			try {
				retries++;
				HttpEntity responseEntity = httpclient.execute(httppost).getEntity();
				if (responseEntity != null) {
					result = EntityUtils.toString(responseEntity).trim();
				}
			} catch (SocketException se) {
				if (retries > maxRetries) {
					throw se;
				} else {
					Log.v(TAG, "SocketException, retrying " + retries, se);
				}
			}
		}

		return result;
	}

	public static String getMetaRefresh(String html) {
		String meta = null;
		int start = html.toLowerCase().indexOf("<meta http-equiv=\"refresh\" content=\"");
		if (start > -1) {
			start += 36;

			int end = html.indexOf('"', start);
			if (end > -1) {
				meta = html.substring(start, end);
				start = meta.toLowerCase().indexOf("url=");
				if (start > -1) {
					start += 4;
					meta = new String(meta.substring(start));
				}
			}
		}

		return meta;
	}
}