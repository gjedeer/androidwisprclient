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

public class WISPrConstants {
	public static String WISPR_MESSAGE_TYPE_INITIAL = "100";

	public static String WISPR_MESSAGE_TYPE_PROXY_NOTIFICATION = "110";

	public static String WISPR_MESSAGE_TYPE_AUTH_NOTIFICATION = "120";

	public static String WISPR_MESSAGE_TYPE_LOGOFF_NOTIFICATION = "130";

	public static String WISPR_MESSAGE_TYPE_RESPONSE_AUTH_POLL = "140";

	public static String WISPR_MESSAGE_TYPE_RESPONSE_ABORT_LOGIN = "150";

	public static String WISPR_RESPONSE_CODE_NO_ERROR = "0";

	public static String WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED = "50";

	public static String WISPR_RESPONSE_CODE_LOGIN_FAILED = "100";

	public static String WISPR_RESPONSE_CODE_RADIUS_ERROR = "102";

	public static String WISPR_RESPONSE_CODE_NETWORK_ADMIN_ERROR = "105";

	public static String WISPR_RESPONSE_CODE_LOGOFF_SUCCEEDED = "150";

	public static String WISPR_RESPONSE_CODE_LOGING_ABORTED = "151";

	public static String WISPR_RESPONSE_CODE_PROXY_DETECTION = "200";

	public static String WISPR_RESPONSE_CODE_AUTH_PENDING = "201";

	public static String WISPR_RESPONSE_CODE_INTERNAL_ERROR = "255";

	// Custom Response Codes
	public static String WISPR_NOT_PRESENT = "1024";

	public static String ALREADY_CONNECTED = "1025";
}
