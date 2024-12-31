package com.bornfire.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name ="BIPS_TRANSACTIONDETAILS")
public class BIPS_TransactionDetails_entity 
{
	@Id
	private String	bill_number;
	private String	name;
	private BigDecimal	device_id;
	private BigDecimal	user_id;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date	bill_date;
	private BigDecimal	bill_amount;
	@DateTimeFormat(pattern = "dd-MM-yyyy")
	private Date	transaction_date;
	private BigDecimal	transaction_amount;
	private String	status;
	public String getBill_number() {
		return bill_number;
	}
	public void setBill_number(String bill_number) {
		this.bill_number = bill_number;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public BigDecimal getDevice_id() {
		return device_id;
	}
	public void setDevice_id(BigDecimal device_id) {
		this.device_id = device_id;
	}
	public BigDecimal getUser_id() {
		return user_id;
	}
	public void setUser_id(BigDecimal user_id) {
		this.user_id = user_id;
	}
	public Date getBill_date() {
		return bill_date;
	}
	public void setBill_date(Date bill_date) {
		this.bill_date = bill_date;
	}
	public BigDecimal getBill_amount() {
		return bill_amount;
	}
	public void setBill_amount(BigDecimal bill_amount) {
		this.bill_amount = bill_amount;
	}
	public Date getTransaction_date() {
		return transaction_date;
	}
	public void setTransaction_date(Date transaction_date) {
		this.transaction_date = transaction_date;
	}
	public BigDecimal getTransaction_amount() {
		return transaction_amount;
	}
	public void setTransaction_amount(BigDecimal transaction_amount) {
		this.transaction_amount = transaction_amount;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public BIPS_TransactionDetails_entity(String bill_number, String name, BigDecimal device_id, BigDecimal user_id,
			Date bill_date, BigDecimal bill_amount, Date transaction_date, BigDecimal transaction_amount,
			String status) {
		super();
		this.bill_number = bill_number;
		this.name = name;
		this.device_id = device_id;
		this.user_id = user_id;
		this.bill_date = bill_date;
		this.bill_amount = bill_amount;
		this.transaction_date = transaction_date;
		this.transaction_amount = transaction_amount;
		this.status = status;
	}
	public BIPS_TransactionDetails_entity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
