package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BIPS_TransactionDetails_Repo extends JpaRepository<BIPS_TransactionDetails_entity,String>{
	
	@Query(value = "select * from BIPS_TRANSACTIONDETAILS ", nativeQuery = true)
	 List<BIPS_TransactionDetails_entity> getTransDetails();

}
