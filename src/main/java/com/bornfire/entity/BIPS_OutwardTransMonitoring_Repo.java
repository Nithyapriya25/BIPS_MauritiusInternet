package com.bornfire.entity;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BIPS_OutwardTransMonitoring_Repo extends JpaRepository<BIPS_Outward_Trans_Monitoring_Entity, String>{

	@Query(value = "select * from bips_outward_transaction_monitoring_table where merchant_id=?1", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTranlst(String merchant_id);
	
	@Query(value = "select * from bips_outward_transaction_monitoring_table where user_id=?1", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getmainTranlst(String user_id);
	
	@Query(value = "select * from bips_outward_transaction_monitoring_table where sequence_unique_id=?1 ", nativeQuery = true)
	BIPS_Outward_Trans_Monitoring_Entity getTranfees(String sequence_unique_id);
	
	@Query(value = "select * from bips_outward_transaction_monitoring_table", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTranDevlst();
	
	@Query(value = "select * from bips_outward_transaction_monitoring_table where merchant_id=?1", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getTranDevlst1(String merchant_id);
	
	@Query(value = "select * from bips_outward_transaction_monitoring_table where device_id=?1 and merchant_id=?2", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> findByDeviceIdAndMerchantId( String device_id, String merchant_id);

	@Query(value = "select count(*) from bips_outward_transaction_monitoring_table where merchant_id=?1", nativeQuery = true)
	Integer getTranscount(String merchant_id);
	
	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND tran_status = 'SUCCESS'", nativeQuery = true)
	Integer getTranssuccess(String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND tran_status = 'FAILED'", nativeQuery = true)
	Integer getTransfailure(String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND tran_status = 'INITIATED'", nativeQuery = true)
	Integer getTransinitiated(String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE tran_status = 'SUCCESS'", nativeQuery = true)
	Integer getTranssuccess1(String merchant_id);
	
	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE tran_status = 'FAILED'", nativeQuery = true)
	Integer getTransfailure1(String merchant_id);
	
	@Query(value = "SELECT COUNT(*) FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE  tran_status = 'INITIATED'", nativeQuery = true)
	Integer getTransinitiated1(String merchant_id);
	
	@Query(value = "select * from bips_outward_transaction_monitoring_table where TRAN_DATE=?1", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getAllRecord(Date tran_date);
	
	@Query(value = "select * from bips_outward_transaction_monitoring_table where TRAN_DATE=?1 and merchant_id=?2", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getAllRecord1(Date tran_date, String merchant_id);

	@Query(value = "SELECT COUNT(*) FROM bips_outward_transaction_monitoring_table WHERE TO_DATE(TRAN_DATE, 'DD-MM-YYYY') = TO_DATE(SYSDATE, 'DD-MM-YYYY') ", nativeQuery = true)
	Integer getDailyTrans(Date tran_date);
	
	@Query(value = "SELECT COUNT(*) FROM bips_outward_transaction_monitoring_table WHERE TO_DATE(TRAN_DATE, 'DD-MM-YYYY') = TO_DATE(SYSDATE, 'DD-MM-YYYY') AND merchant_id =?1 ", nativeQuery = true)
	Integer getDailyTransMer(String merchant_id);
	
	@Query(value = "SELECT SUM(tran_amount) FROM bips_outward_transaction_monitoring_table WHERE TO_DATE(TRAN_DATE, 'DD-MM-YYYY') = TO_DATE(SYSDATE, 'DD-MM-YYYY') ", nativeQuery = true)
	Integer getTransactionCounts(Date tran_date);

	@Query(value = "SELECT SUM(tran_amount) FROM bips_outward_transaction_monitoring_table WHERE TO_DATE(TRAN_DATE, 'DD-MM-YYYY') = TO_DATE(SYSDATE, 'DD-MM-YYYY') AND merchant_id =?1 ", nativeQuery = true)
	Integer getTransactionCountMer(String merchant_id);
	
	@Query(value = "select  DISTINCT device_id FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id =?1", nativeQuery = true)
	List<String> getdeviceId(String merchant_id1);
	
	@Query(value = "select  DISTINCT unit_id FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id =?1", nativeQuery = true)
	List<String> getpartUnitId(String merchant_id1);
	
	@Query(value = "select  DISTINCT user_id FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id =?1", nativeQuery = true)
	List<String> getuserid(String merchant_id1);
	
	@Query(value = "SELECT * FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND tran_date BETWEEN ?2 AND ?3  AND unit_id = ?4", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getFromToDate(String merUserId ,Date from_date, Date to_date, String unit_id);

	@Query(value = "SELECT * FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND tran_date BETWEEN ?2 AND ?3  AND device_id = ?4", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getFromToDateDev(String merUserId ,Date from_date, Date to_date, String device_id);
	
	@Query(value = "SELECT * FROM BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id = ?1 AND tran_date BETWEEN ?2 AND ?3  AND user_id = ?4", nativeQuery = true)
	List<BIPS_Outward_Trans_Monitoring_Entity> getFromToDateUser(String merUserId ,Date from_date, Date to_date, String user_id);
	
	@Query(value = "SELECT cim_account from BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE WHERE merchant_id =?1", nativeQuery = true)
	List<String> getAccNum(String merchant_id1);
}
