package com.bornfire.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
//@JsonPropertyOrder({"trAmt","mCCreditTransferResponse"})
public class tranamtclassresponse {
	private String TrAmt;
	
	private MCCreditTransferResponse MCCreditTransferResponse;

	public String getTrAmt() {
		return TrAmt;
	}

	public void setTrAmt(String trAmt) {
		TrAmt = trAmt;
	}

	public MCCreditTransferResponse getMCCreditTransferResponse() {
		return MCCreditTransferResponse;
	}

	public void setMCCreditTransferResponse(MCCreditTransferResponse mCCreditTransferResponse) {
		MCCreditTransferResponse = mCCreditTransferResponse;
	}

	@Override
	public String toString() {
		return "tranamtclassresponse [TrAmt=" + TrAmt + ", MCCreditTransferResponse=" + MCCreditTransferResponse
				+ ", getTrAmt()=" + getTrAmt() + ", getMCCreditTransferResponse()=" + getMCCreditTransferResponse()
				+ ", getClass()=" + getClass() + ", hashCode()=" + hashCode() + ", toString()=" + super.toString()
				+ "]";
	}
	

}
