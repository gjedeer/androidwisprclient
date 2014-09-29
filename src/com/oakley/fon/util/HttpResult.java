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

import org.apache.http.message.BasicHttpResponse;

public class HttpResult {
	private String content;

	private BasicHttpResponse response;

	private String targetHost;

	public HttpResult(String content, BasicHttpResponse response, String targetHost) {
		this.content = content;
		this.response = response;
		this.targetHost = targetHost;
	}

	public String getContent() {
		return content;
	}

	public BasicHttpResponse getResponse() {
		return response;
	}

	public String getTargetHost() {
		return targetHost;
	}
}
