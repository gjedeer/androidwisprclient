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

public class WISPrResponseHandler extends DefaultHandler {
	enum Tag {
		wispaccessgatewayparam, redirect, responsecode, fonresponsecode, logoffurl, replymessage, authenticationpollreply, messagetype, authenticationreply
	}

	private Tag actualTag;

	private final StringBuilder responseCode = new StringBuilder();

	private final StringBuilder fonResponseCode = new StringBuilder();

	private final StringBuilder logoffURL = new StringBuilder();

	private final StringBuilder replyMessage = new StringBuilder();

	private final StringBuilder messageType = new StringBuilder();

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		actualTag = Tag.valueOf(name.trim().toLowerCase());
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if (actualTag.equals(Tag.responsecode)) {
			responseCode.append(chars);
		} else if (actualTag.equals(Tag.fonresponsecode)) {
			fonResponseCode.append(chars);
		} else if (actualTag.equals(Tag.logoffurl)) {
			logoffURL.append(chars);
		} else if (actualTag.equals(Tag.replymessage)) {
			replyMessage.append(chars);
		} else if (actualTag.equals(Tag.messagetype)) {
			messageType.append(chars);
		}
	}

	public String getResponseCode() {
		return responseCode.toString().trim();
	}

	public String getFonResponseCode() {
		return fonResponseCode.toString().trim();
	}

	public String getLogoffURL() {
		return logoffURL.toString().trim();
	}

	public String getReplyMessage() {
		return replyMessage.toString().trim();
	}

	public String getMessageType() {
		return messageType.toString().trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getSimpleName()).append("{");
		sb.append("responseCode: ").append(responseCode).append(", ");
		sb.append("fonResponseCode: ").append(fonResponseCode).append(", ");
		sb.append("logoffURL: ").append(logoffURL).append(", ");
		sb.append("replyMessage: ").append(replyMessage).append(", ");
		sb.append("messageType: ").append(messageType);
		sb.append("}");

		return sb.toString();
	}
}