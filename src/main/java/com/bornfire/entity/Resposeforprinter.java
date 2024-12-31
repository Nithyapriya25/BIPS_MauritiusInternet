package com.bornfire.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Resposeforprinter {

	

	private String TranID;
	private String TranDateTime;

		// TODO Auto-generated constructor stub

	private String Status;
	private String TrAmt;
	
	@JsonProperty("Error")
	private String Error;
	@JsonProperty("Error_Desc")
	private List<String> Error_Desc;
	public String getTranID() {
		return TranID;
	}
	public void setTranID(String tranID) {
		TranID = tranID;
	}
	public String getTranDateTime() {
		return TranDateTime;
	}
	public void setTranDateTime(String tranDateTime) {
		TranDateTime = tranDateTime;
	}
	public String getStatus() {
		return Status;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public String getTrAmt() {
		return TrAmt;
	}
	public void setTrAmt(String trAmt) {
		TrAmt = trAmt;
	}
	public String getError() {
		return Error;
	}
	public void setError(String error) {
		Error = error;
	}
	public List<String> getError_Desc() {
		return Error_Desc;
	}
	public void setError_Desc(List<String> error_Desc) {
		Error_Desc = error_Desc;
	}
	public Resposeforprinter(String tranID, String tranDateTime, String status, String trAmt, String error,
			List<String> error_Desc) {
		super();
		TranID = tranID;
		TranDateTime = tranDateTime;
		Status = status;
		TrAmt = trAmt;
		Error = error;
		Error_Desc = error_Desc;
	}
	public Resposeforprinter() {
		super();
	}
	
	
}
