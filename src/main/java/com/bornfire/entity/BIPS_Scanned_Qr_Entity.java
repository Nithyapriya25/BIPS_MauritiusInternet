package com.bornfire.entity;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class BIPS_Scanned_Qr_Entity {
	@Id
	private String	scan_val;

	public String getScan_val() {
		return scan_val;
	}

	public void setScan_val(String scan_val) {
		this.scan_val = scan_val;
	}

	public BIPS_Scanned_Qr_Entity(String scan_val) {
		super();
		this.scan_val = scan_val;
	}

	public BIPS_Scanned_Qr_Entity() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
