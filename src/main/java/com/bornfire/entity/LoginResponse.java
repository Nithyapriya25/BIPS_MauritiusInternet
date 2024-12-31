package com.bornfire.entity;

public class LoginResponse {
    private String status;
    private String message;

    public LoginResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    
    @Override
	public String toString() {
		return "LoginResponse [status=" + status + ", message=" + message + "]";
	}


	public LoginResponse() {
		super();
		// TODO Auto-generated constructor stub
	}


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
}