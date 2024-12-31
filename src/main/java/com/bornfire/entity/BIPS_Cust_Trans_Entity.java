package com.bornfire.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BIPS_CUSTTRANS")
public class BIPS_Cust_Trans_Entity {

	private Date	transaction_date;
	@Id
	private String	message_ref;
	private String	audit_ref;
	private String	bank;
	private String	remitter_account;
	private String	beneficiary_account;
	private String	currency;
	private BigDecimal	amount;
	private String	particulars;
	private String	remarks;
	private String	status;
	
	public Date getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(Date transaction_date) {
		this.transaction_date = transaction_date;
	}
	public String getMessage_ref() {
		return message_ref;
	}
	public void setMessage_ref(String message_ref) {
		this.message_ref = message_ref;
	}
	public String getAudit_ref() {
		return audit_ref;
	}
	public void setAudit_ref(String audit_ref) {
		this.audit_ref = audit_ref;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getRemitter_account() {
		return remitter_account;
	}
	public void setRemitter_account(String remitter_account) {
		this.remitter_account = remitter_account;
	}
	public String getBeneficiary_account() {
		return beneficiary_account;
	}
	public void setBeneficiary_account(String beneficiary_account) {
		this.beneficiary_account = beneficiary_account;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getParticulars() {
		return particulars;
	}
	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BIPS_Cust_Trans_Entity(Date transaction_date, String message_ref, String audit_ref, String bank,
			String remitter_account, String beneficiary_account, String currency, BigDecimal amount, String particulars,
			String remarks, String status) {
		super();
		this.transaction_date = transaction_date;
		this.message_ref = message_ref;
		this.audit_ref = audit_ref;
		this.bank = bank;
		this.remitter_account = remitter_account;
		this.beneficiary_account = beneficiary_account;
		this.currency = currency;
		this.amount = amount;
		this.particulars = particulars;
		this.remarks = remarks;
		this.status = status;
	}
	public BIPS_Cust_Trans_Entity() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
