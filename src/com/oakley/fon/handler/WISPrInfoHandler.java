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
package com.oakley.fon.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class WISPrInfoHandler extends DefaultHandler {
	enum Tag {
		wispaccessgatewayparam, redirect, accessprocedure, loginurl, abortloginurl, messagetype, responsecode, accesslocation, locationname
	}

	private Tag actualTag;

	private final StringBuilder accessProcedure = new StringBuilder();

	private final StringBuilder accessLocation = new StringBuilder();

	private final StringBuilder loginURL = new StringBuilder();

	private final StringBuilder abortLoginURL = new StringBuilder();

	private final StringBuilder messageType = new StringBuilder();

	private final StringBuilder responseCode = new StringBuilder();

	private final StringBuilder locationName = new StringBuilder();

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		actualTag = Tag.valueOf(name.trim().toLowerCase());
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if (actualTag.equals(Tag.accessprocedure)) {
			accessProcedure.append(chars);
		} else if (actualTag.equals(Tag.loginurl)) {
			loginURL.append(chars);
		} else if (actualTag.equals(Tag.abortloginurl)) {
			abortLoginURL.append(chars);
		} else if (actualTag.equals(Tag.messagetype)) {
			messageType.append(chars);
		} else if (actualTag.equals(Tag.responsecode)) {
			responseCode.append(chars);
		} else if (actualTag.equals(Tag.accesslocation)) {
			accessLocation.append(chars);
		} else if (actualTag.equals(Tag.locationname)) {
			locationName.append(chars);
		}
	}

	public String getAccessProcedure() {
		return accessProcedure.toString().trim();
	}

	public String getLoginURL() {
		return loginURL.toString().trim();
	}

	public String getAbortLoginURL() {
		return abortLoginURL.toString().trim();
	}

	public String getMessageType() {
		return messageType.toString().trim();
	}

	public String getResponseCode() {
		return responseCode.toString().trim();
	}

	public String getAccessLocation() {
		return accessLocation.toString().trim();
	}

	public String getLocationName() {
		return locationName.toString().trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName()).append("{");
		sb.append("accessProcedure: ").append(accessProcedure).append(", ");
		sb.append("accessLocation: ").append(accessLocation).append(", ");
		sb.append("locationName: ").append(locationName).append(", ");
		sb.append("loginURL: ").append(loginURL).append(", ");
		sb.append("abortLoginURL: ").append(abortLoginURL).append(", ");
		sb.append("messageType: ").append(messageType).append(", ");
		sb.append("responseCode: ").append(responseCode);
		sb.append("}");

		return sb.toString();
	}
}
