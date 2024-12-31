package com.bornfire.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "MERCHANT_CONTACT_DETAILS")
@IdClass(MerchantContactID.class)
public class Merchant_Contact_entity {

	@Id
	private String srl_no;
	@Id
	private String merchant_id;
	private String mer_contact_person;
	private String mer_email;
	private String mer_mobile_number;
	private String merchant_address_detail;
	private String send_notify;
	private String notify_mode;
	private String mer_office_number;

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

	public String getMer_contact_person() {
		return mer_contact_person;
	}

	public void setMer_contact_person(String mer_contact_person) {
		this.mer_contact_person = mer_contact_person;
	}

	public String getMer_email() {
		return mer_email;
	}

	public void setMer_email(String mer_email) {
		this.mer_email = mer_email;
	}

	public String getMer_mobile_number() {
		return mer_mobile_number;
	}

	public void setMer_mobile_number(String mer_mobile_number) {
		this.mer_mobile_number = mer_mobile_number;
	}

	public String getMerchant_address_detail() {
		return merchant_address_detail;
	}

	public void setMerchant_address_detail(String merchant_address_detail) {
		this.merchant_address_detail = merchant_address_detail;
	}

	public String getSend_notify() {
		return send_notify;
	}

	public void setSend_notify(String send_notify) {
		this.send_notify = send_notify;
	}
	public String getnotify_mode() {
		return notify_mode;
	}

	public void setnotify_mode(String notify_mode) {
		this.notify_mode = notify_mode;
	}

	public String getNotify_mode() {
		return notify_mode;
	}

	public void setNotify_mode(String notify_mode) {
		this.notify_mode = notify_mode;
	}
	
	

	public String getMer_office_number() {
		return mer_office_number;
	}

	public void setMer_office_number(String mer_office_number) {
		this.mer_office_number = mer_office_number;
	}

	public Merchant_Contact_entity(String srl_no, String merchant_id, String mer_contact_person, String mer_email, String mer_mobile_number, 
			String merchant_address_detail, String send_notify, String notify_mode, String mer_office_number) {
		this.srl_no = srl_no;
		this.merchant_id = merchant_id;
		this.mer_contact_person = mer_contact_person;
		this.mer_email = mer_email;
		this.mer_mobile_number = mer_mobile_number;
		this.merchant_address_detail = merchant_address_detail;
		this.send_notify = send_notify;
		this.notify_mode = notify_mode;
		this.mer_office_number = mer_office_number;
	}

	public Merchant_Contact_entity() {
		super();
	}
	
	
	public Merchant_Contact_entity(Merchant_Contact_entity_Mod up ) {
		super();
		this.srl_no = up.getSrl_no();
		this.merchant_id = up.getMerchant_id();
		this.mer_contact_person = up.getMer_contact_person();
		this.mer_email = up.getMer_email();
		this.mer_mobile_number = up.getMer_mobile_number();
		this.merchant_address_detail = up.getMerchant_address_detail();
		this.send_notify = up.getSend_notify();
		this.notify_mode =up.getNotify_mode();
		this.mer_office_number = up.getMer_office_number();
	}
	
}
