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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.oakley.fon.util.Utils;

public class AndroidWISPr extends PreferenceActivity {
	private static String TAG = AndroidWISPr.class.getSimpleName();

	public static final int CLOSE_ID = Menu.FIRST;

	public static final int LOGOFF_ID = CLOSE_ID + 1;

	public static final int ADVANCED_ID = LOGOFF_ID + 1;

	public static final int HELP_ID = ADVANCED_ID + 1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences_main);
		setContentView(R.layout.preferences_header);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		String logOffUrl = getLogOffUrl();
		MenuItem menuItem = menu.findItem(LOGOFF_ID);
		if (menuItem != null) {
			if (logOffUrl != null) {
				menuItem.setEnabled(true);
			} else {
				menuItem.setEnabled(false);
			}
		}

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, CLOSE_ID, 0, R.string.menu_close).setIcon(android.R.drawable.ic_menu_save);
		menu.add(Menu.NONE, HELP_ID, 1, R.string.menu_help).setIcon(android.R.drawable.ic_menu_help);
		menu.add(Menu.NONE, LOGOFF_ID, 1, R.string.menu_logOff).setIcon(android.R.drawable.ic_menu_revert);
		menu.add(Menu.NONE, ADVANCED_ID, 2, R.string.menu_advanced).setIcon(android.R.drawable.ic_menu_preferences);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean res = true;
		switch (item.getItemId()) {
			case CLOSE_ID:
				finish();
				break;
			case LOGOFF_ID:
				logOff_clicked(item);
				break;
			case ADVANCED_ID:
				startActivity(new Intent(this, PreferencesAdvanced.class));
				break;
			case HELP_ID:
				showHelpWindow();
				break;
			default:
				res = super.onOptionsItemSelected(item);
		}

		return res;
	}

	private void showHelpWindow() {
		LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.help_window, (ViewGroup) findViewById(R.id.layout_help));

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setTitle(R.string.help_title);
		builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}

	private void logOff_clicked(MenuItem item) {
		String logOffUrl = getLogOffUrl();
		Log.d(TAG, "Logoff Button Clicked:" + logOffUrl);
		if (logOffUrl != null) {
			Intent logOffIntent = new Intent(this, LogOffService.class);
			logOffIntent.putExtra(this.getString(R.string.pref_logOffUrl), logOffUrl);
			this.startService(logOffIntent);
		}
		item.setEnabled(false);
	}

	private String getLogOffUrl() {
		String logOffUrl = Utils.getStringPreference(this, R.string.pref_logOffUrl, "");
		if (logOffUrl != null) {
			logOffUrl = logOffUrl.trim();
			if (logOffUrl.length() == 0) {
				logOffUrl = null;
			}
		}

		return logOffUrl;
	}
}