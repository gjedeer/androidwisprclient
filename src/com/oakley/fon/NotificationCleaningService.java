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
package com.oakley.fon;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.oakley.fon.util.Utils;

public class NotificationCleaningService extends IntentService {

	private static String TAG = NotificationCleaningService.class.getName();

	public static final String ACTION_CLEAN = "CLEAN";

	public NotificationCleaningService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.v(TAG, "Handling disconnection intent: " + intent);
		if (intent.getAction().equals(ACTION_CLEAN)) {
			cleanNotification(this, intent);
			cleanLogOffUrl(this, intent);
		}
	}

	private void cleanLogOffUrl(Context context, Intent intent) {
		Utils.removePreference(context, R.string.pref_logOffUrl);
	}

	private void cleanNotification(Context context, Intent intent) {
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		notificationManager.cancel(1);
	}
}
