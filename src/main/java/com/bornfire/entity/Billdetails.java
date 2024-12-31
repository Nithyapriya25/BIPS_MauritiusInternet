package com.bornfire.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Billdetails {
	
	@Id
	private String billNumber;
	private String billDate;
	private String billAmount;

	// Default constructor (required for deserialization)
	public void BillDetails() {
	}

	// Parameterized constructor
	public void BillDetails(String billNumber, String billDate, String billAmount) {
		this.billNumber = billNumber;
		this.billDate = billDate;
		this.billAmount = billAmount;
	}

	// Getter and setter methods
	public String getBillNumber() {
		return billNumber;
	}

	public void setBillNumber(String billNumber) {
		this.billNumber = billNumber;
	}

	public String getBillDate() {
		return billDate;
	}

	public void setBillDate(String billDate) {
		this.billDate = billDate;
	}

	public String getBillAmount() {
		return billAmount;
	}

	public void setBillAmount(String billAmount) {
		this.billAmount = billAmount;
	}

}
