package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BIPS_CustTrans_Repo extends JpaRepository<BIPS_Cust_Trans_Entity, String>{

	@Query(value = "SELECT DISTINCT * FROM bips_custtrans", nativeQuery = true)
	List<BIPS_Cust_Trans_Entity> getlst();
	
}
