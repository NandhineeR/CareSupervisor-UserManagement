package com.qdm.cs.usermanagement.response;

import lombok.Data;

@Data
public class ResponseInfo {

	private String status;
	private int status_code;
	private String message;
	private Object data;

	
	public ResponseInfo() {
		super();
	}


	public ResponseInfo(String status, int status_code, String message, Object data) {
		super();
		this.status = status;
		this.status_code = status_code;
		this.message = message;
		this.data = data;
	}

	
}