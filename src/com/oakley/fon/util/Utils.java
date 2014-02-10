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

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

public class Utils {
	private static Map<Context, SharedPreferences> preferences = new HashMap<Context, SharedPreferences>();

	public static void logIntent(String TAG, Intent intent) {
		if (Log.isLoggable(TAG, Log.DEBUG)) {
			Log.d(TAG, "intent.getAction():" + intent.getAction());
			Log.d(TAG, "intent.getData():" + intent.getData());
			Log.d(TAG, "intent.getDataString():" + intent.getDataString());
			Log.d(TAG, "intent.getScheme():" + intent.getScheme());
			Log.d(TAG, "intent.getType():" + intent.getType());
			Bundle extras = intent.getExtras();
			if (extras != null && !extras.isEmpty()) {
				for (String key : extras.keySet()) {
					Object value = extras.get(key);
					Log.d(TAG, "EXTRA: {" + key + "::" + value + "}");
				}
			} else {
				Log.d(TAG, "NO EXTRAS");
			}
		}
	}

	public static SharedPreferences getSharedPreferences(Context context) {
		SharedPreferences pref = preferences.get(context);
		if (pref == null) {
			pref = PreferenceManager.getDefaultSharedPreferences(context);
			preferences.put(context, pref);
		}

		return pref;
	}

	public static boolean getBooleanPreference(Context context, int resId, boolean defaultValue) {
		return getSharedPreferences(context).getBoolean(context.getString(resId), defaultValue);
	}

	public static int getIntPreference(Context context, int resId, int defaultValue) {
		return getSharedPreferences(context).getInt(context.getString(resId), defaultValue);
	}

	public static String getStringPreference(Context context, int resId, String defaultValue) {
		return getSharedPreferences(context).getString(context.getString(resId), defaultValue);
	}

	public static void savePreference(Context context, int resId, int newValue) {
		SharedPreferences mPreferences = Utils.getSharedPreferences(context);
		Editor editor = mPreferences.edit();
		editor.putInt(context.getString(resId), newValue);
		editor.commit();
	}

	public static void savePreference(Context context, int resId, String newValue) {
		SharedPreferences mPreferences = Utils.getSharedPreferences(context);
		Editor editor = mPreferences.edit();
		editor.putString(context.getString(resId), newValue);
		editor.commit();
	}

	public static void removePreference(Context context, int resId) {
		SharedPreferences mPreferences = Utils.getSharedPreferences(context);
		Editor editor = mPreferences.edit();
		editor.remove(context.getString(resId));
		editor.commit();
	}
}