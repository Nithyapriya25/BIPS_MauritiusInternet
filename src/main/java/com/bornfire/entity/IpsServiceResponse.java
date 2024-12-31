package com.bornfire.entity;

public class IpsServiceResponse {
	
	private String status;
	private String message;
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public IpsServiceResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	public IpsServiceResponse(String status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

	
}
