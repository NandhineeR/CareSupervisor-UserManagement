package com.qdm.cs.usermanagement.response;

public enum ResponseType {

	SUCCESS(200, "Success"), ERROR(500, "Error"), BAD_REQUEST(400, "Bad Request"), NOT_FOUND(404, "Not Found");

	private final int responseCode;

	private final String responseMessage;

	private ResponseType(int responseCode, String responseMessage) {
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

}