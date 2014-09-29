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
package com.oakley.fon.logger;

import com.oakley.fon.util.WISPrConstants;

public class LoggerResult {
	protected String result;

	protected String logOffUrl;

	public LoggerResult(String result, String logOffUrl) {
		this.result = result;
		this.logOffUrl = logOffUrl;
	}

	public String getResult() {
		return result;
	}

	public String getLogOffUrl() {
		return logOffUrl;
	}

	public boolean hasSucceded() {
		return result.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_SUCCEEDED)
				|| result.equals(WISPrConstants.ALREADY_CONNECTED);
	}

	public boolean hasFailed() {
		return result.equals(WISPrConstants.WISPR_RESPONSE_CODE_INTERNAL_ERROR)
				|| result.equals(WISPrConstants.WISPR_RESPONSE_CODE_LOGIN_FAILED)
				|| result.equals(WISPrConstants.WISPR_NOT_PRESENT);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + "{result: " + result + ", logOffUrl:" + logOffUrl + "}";
	}
}
