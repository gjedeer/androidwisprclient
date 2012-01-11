package com.sputnik.wispr.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class WISPrInfoHandler extends DefaultHandler {
	enum Tag {
		wispaccessgatewayparam, redirect, accessprocedure, loginurl, abortloginurl, messagetype, responsecode, accesslocation, locationname
	}

	private Tag actualTag;

	private String accessProcedure = "";

	private String accessLocation = "";

	private String loginURL = "";

	private String abortLoginURL = "";

	private String messageType = "";

	private String responseCode = "";

	private String locationName = "";

	@Override
	public void startElement(String uri, String name, String qName, Attributes atts) {
		actualTag = Tag.valueOf(name.trim().toLowerCase());
	}

	@Override
	public void characters(char ch[], int start, int length) {
		String chars = (new String(ch).substring(start, start + length));
		if (actualTag.equals(Tag.accessprocedure)) {
			accessProcedure += chars;
		} else if (actualTag.equals(Tag.loginurl)) {
			loginURL += chars;
		} else if (actualTag.equals(Tag.abortloginurl)) {
			abortLoginURL += chars;
		} else if (actualTag.equals(Tag.messagetype)) {
			messageType += chars;
		} else if (actualTag.equals(Tag.responsecode)) {
			responseCode += chars;
		} else if (actualTag.equals(Tag.accesslocation)) {
			accessLocation += chars;
		} else if (actualTag.equals(Tag.locationname)) {
			locationName += chars;
		}
	}

	public String getAccessProcedure() {
		return accessProcedure.trim();
	}

	public String getLoginURL() {
		return loginURL.trim();
	}

	public String getAbortLoginURL() {
		return abortLoginURL.trim();
	}

	public String getMessageType() {
		return messageType.trim();
	}

	public String getResponseCode() {
		return responseCode.trim();
	}

	public String getAccessLocation() {
		return accessLocation.trim();
	}

	public String getLocationName() {
		return locationName.trim();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("WISPrInfoHandler{");
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
