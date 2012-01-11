package com.sputnik.wispr.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class WISPrResponseHandler extends DefaultHandler {
	enum Tag {
		wispaccessgatewayparam, redirect, responsecode, fonresponsecode, logoffurl, replymessage, authenticationpollreply, messagetype, authenticationreply
	}

	private Tag actualTag;

	private String responseCode = "";

	private String fonResponseCode = "";

	private String logoffURL = "";

	private String replyMessage = "";

	private String messageType = "";

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		actualTag = Tag.valueOf(name.trim().toLowerCase());
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if (actualTag.equals(Tag.responsecode)) {
			responseCode += chars;
		} else if (actualTag.equals(Tag.fonresponsecode)) {
			fonResponseCode += chars;
		} else if (actualTag.equals(Tag.logoffurl)) {
			logoffURL += chars;
		} else if (actualTag.equals(Tag.replymessage)) {
			replyMessage += chars;
		} else if (actualTag.equals(Tag.messagetype)) {
			messageType += chars;
		}
	}

	public String getResponseCode() {
		return responseCode.trim();
	}

	public String getFonResponseCode() {
		return fonResponseCode.trim();
	}

	public String getLogoffURL() {
		return logoffURL.trim();
	}

	public String getReplyMessage() {
		return replyMessage.trim();
	}

	public String getMessageType() {
		return messageType.trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WISPrResponseHandler{");
		sb.append("responseCode: ").append(responseCode).append(", ");
		sb.append("fonResponseCode: ").append(fonResponseCode).append(", ");
		sb.append("logoffURL: ").append(logoffURL).append(", ");
		sb.append("replyMessage: ").append(replyMessage).append(", ");
		sb.append("messageType: ").append(messageType);
		sb.append("}");

		return sb.toString();
	}
}
