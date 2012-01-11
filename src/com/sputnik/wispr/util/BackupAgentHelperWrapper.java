package com.sputnik.wispr.util;

import java.io.IOException;

import android.app.backup.BackupAgentHelper;
import android.app.backup.BackupDataInput;
import android.app.backup.BackupDataOutput;
import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;
import android.os.ParcelFileDescriptor;
import android.util.Log;

/**
 * A wrapper class for the Android 2.2+ BackupHelper class. It marshalls all calls to the helper to
 * the original class, but only if it exists. Inspired on the official doc at
 * http://developer.android.com/resources/articles/backward-compatibility.html
 * 
 * @author erickok
 */
public class BackupAgentHelperWrapper extends BackupAgentHelper {
	private static String TAG = BackupAgentHelperWrapper.class.getName();

	private BackupAgentHelper instance = null;

	private static boolean checkAvailability = true;

	private static boolean isAvailable = false;

	/**
	 * Class initialization which will fail when the helper class doesn't exist
	 */
	static {
		if (checkAvailability) {
			try {
				Class.forName("android.app.backup.BackupAgentHelper");
				isAvailable = true;
			} catch (Exception e) {
				isAvailable = false;
			} finally {
				checkAvailability = false;
			}
			Log.d(TAG, "isAvailable:" + isAvailable);
		}
	}

	public BackupAgentHelperWrapper() {
		if (isAvailable) {
			instance = new BackupAgentHelper();
			Log.d(TAG, "instance:" + instance);
		}
	}

	protected Context getBackupAgentInstance() {
		Context ctx = null;
		if (isAvailable) {
			ctx = instance;
		}
		return ctx;
	}

	@Override
	public void onCreate() {
		if (isAvailable) {
			instance.onCreate();
		}
	}

	@Override
	public void onDestroy() {
		if (isAvailable) {
			instance.onDestroy();
		}
	}

	@Override
	public void onBackup(ParcelFileDescriptor oldState, BackupDataOutput data, ParcelFileDescriptor newState)
			throws IOException {
		if (isAvailable) {
			instance.onBackup(oldState, data, newState);
		}
	}

	@Override
	public void onRestore(BackupDataInput data, int appVersionCode, ParcelFileDescriptor newState) throws IOException {
		if (isAvailable) {
			instance.onRestore(data, appVersionCode, newState);
		}
	}

	public void addHelper(String keyPrefix, SharedPreferencesBackupHelper helper) {
		if (isAvailable) {
			instance.addHelper(keyPrefix, helper);
		}
	}

	@Override
	public String getPackageName() {
		String pkgName = null;
		if (isAvailable) {
			Log.d(TAG, "getPackageName@instance:" + instance);
			pkgName = instance.getPackageName();
		}
		return pkgName;
	}

}