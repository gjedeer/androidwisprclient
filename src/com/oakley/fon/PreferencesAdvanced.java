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

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuItem;

public class PreferencesAdvanced extends PreferenceActivity {

	public static final int BACK_ID = Menu.FIRST;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.preferences_advanced);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		boolean result = super.onCreateOptionsMenu(menu);
		menu.add(Menu.NONE, BACK_ID, 0, R.string.menu_back).setIcon(android.R.drawable.ic_menu_revert);

		return result;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		boolean res = true;
		switch (item.getItemId()) {
			case BACK_ID:
				setResult(RESULT_OK);
				finish();
				break;
			default:
				res = super.onOptionsItemSelected(item);
		}

		return res;
	}
}