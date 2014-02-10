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

import java.io.IOException;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.oakley.fon.util.HttpUtils;

public class LogOffService extends IntentService {
	private static String TAG = LogOffService.class.getName();

	public LogOffService() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String logOffUrl = intent.getStringExtra(this.getString(R.string.pref_logOffUrl));
		try {
			if (logOffUrl != null && logOffUrl.trim().length() > 0) {
				HttpUtils.getUrl(logOffUrl);
				Intent cleaningIntent = new Intent(this, NotificationCleaningService.class);
				cleaningIntent.setAction(NotificationCleaningService.ACTION_CLEAN);
				this.startService(cleaningIntent);
			}
		} catch (IOException e) {
			Log.e(TAG, "Error login off", e);
		}
	}
}
