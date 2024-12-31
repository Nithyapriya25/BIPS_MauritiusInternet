package com.bornfire.entity;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.Id;

@Embeddable
public class MerchantContactID implements Serializable {
	@Id
	private String srl_no;
	@Id
	private String merchant_id;
	public String getSrl_no() {
		return srl_no;
	}
	public void setSrl_no(String srl_no) {
		this.srl_no = srl_no;
	}
	public String getMerchant_id() {
		return merchant_id;
	}
	public void setMerchant_id(String merchant_id) {
		this.merchant_id = merchant_id;
	}


	
}