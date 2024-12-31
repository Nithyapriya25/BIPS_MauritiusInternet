package com.bornfire.entity;

public class AlertEntity {
	    private int alertId;
	    private String alertType;
	    private String alertMessage;
	    private String screenName;

	
	    public AlertEntity() {
			super();
			// TODO Auto-generated constructor stub
		}

		public AlertEntity(int alertId, String alertType, String alertMessage, String screenName) {
			super();
			this.alertId = alertId;
			this.alertType = alertType;
			this.alertMessage = alertMessage;
			this.screenName = screenName;
		}

		// Getters and Setters
	    public int getAlertId() {
	        return alertId;
	    }

	    public void setAlertId(int alertId) {
	        this.alertId = alertId;
	    }

	    public String getAlertType() {
	        return alertType;
	    }

	    public void setAlertType(String alertType) {
	        this.alertType = alertType;
	    }

	    public String getAlertMessage() {
	        return alertMessage;
	    }

	    public void setAlertMessage(String alertMessage) {
	        this.alertMessage = alertMessage;
	    }

	    public String getScreenName() {
	        return screenName;
	    }

	    public void setScreenName(String screenName) {
	        this.screenName = screenName;
	    }

	    @Override
	    public String toString() {
	        return "BipsAlert{" +
	                "alertId=" + alertId +
	                ", alertType='" + alertType + '\'' +
	                ", alertMessage='" + alertMessage + '\'' +
	                ", screenName='" + screenName + '\'' +
	                '}';
	    }
}
