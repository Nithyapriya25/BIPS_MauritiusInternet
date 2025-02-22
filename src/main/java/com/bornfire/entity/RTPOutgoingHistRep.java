package com.bornfire.entity;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RTPOutgoingHistRep extends JpaRepository<RTP_Outgoing_hist_entity, String> {

	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where (msg_type='OUTGOING') and  trunc(tran_date)=?1 and tran_date is not null order by tran_date asc ", nativeQuery = true)
	List<RTP_Outgoing_hist_entity> findAllCustomoutward(String tranDate);
	
	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where (msg_type='OUTWARD_BULK_RTP') and  trunc(tran_date)=?1 and tran_date is not null order by tran_date asc ", nativeQuery = true)
	List<RTP_Outgoing_hist_entity> findAllCustomoutwardrtp(String tranDate);
	
	@Query(value = "select cim_account,cim_account_name,ipsx_account,ipsx_account_name,tran_date,value_date,sequence_unique_id,tran_audit_number,msg_type,tran_amount,entry_user,master_ref_id,(select a.bank_name from bips_other_bank_agent_table a where a.bank_agent=cdtr_agt)cdtr_agt,(select a.bank_name from bips_other_bank_agent_table a where a.bank_agent=dbtr_agt)dbtr_agt,init_channel_id,tran_currency,tran_status,cbs_status,cbs_status_error,response_status,ipsx_status_error,resv_field1,tran_type_code from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where (msg_type='OUTWARD_BULK_RTP') and  trunc(tran_date)=?1 and tran_date is not null order by tran_date asc ", nativeQuery = true)
	List<Object[]> findAllCustomoutwardrtp1(String tranDate);

	////MC
	@Query(value = "select nvl(count(sequence_unique_id),0) from bips_outward_transaction_hist_monitoring_table where trunc(tran_date)=?1 and (msg_type='OUTGOING')", nativeQuery = true)
	String outwardMCListTotTxs(String tranDate);
	
	@Query(value = "select nvl(count(sequence_unique_id),0) from bips_outward_transaction_hist_monitoring_table where trunc(tran_date)=?1 and tran_status='SUCCESS' and (msg_type='OUTGOING')", nativeQuery = true)
	String outwardMCLisSucTxs(String tranDate);

	@Query(value = "select nvl(count(sequence_unique_id),0) from bips_outward_transaction_hist_monitoring_table where trunc(tran_date)=?1 and tran_status!='SUCCESS' and (msg_type='OUTGOING')", nativeQuery = true)
	String outwardMCListFailTxs(String tranDate);
	
	@Query(value = "select nvl(sum(tran_amount),0) from bips_outward_transaction_hist_monitoring_table where trunc(tran_date)=?1 and (msg_type='OUTGOING')", nativeQuery = true)
	String outwardMCListTotAmt(String tranDate);
	
	@Query(value = "select nvl(sum(tran_amount),0) from bips_outward_transaction_hist_monitoring_table where trunc(tran_date)=?1 and tran_status='SUCCESS' and (msg_type='OUTGOING')", nativeQuery = true)
	String outwardMCLisSucAmt(String tranDate);

	@Query(value = "select nvl(sum(tran_amount),0) from bips_outward_transaction_hist_monitoring_table where trunc(tran_date)=?1 and tran_status!='SUCCESS' and (msg_type='OUTGOING')", nativeQuery = true)
	String outwardMCListFailAmt(String tranDate);

	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE where master_ref_id=?1 UNION ALL select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where master_ref_id=?1", nativeQuery = true)
	List<RTP_Outgoing_hist_entity> findAllCustomBulkManual(String seqUniqueID,String tranDate);
	
	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE where master_ref_id=?1 UNION ALL select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where master_ref_id=?1", nativeQuery = true)
	List<RTP_Outgoing_hist_entity> findAllCustomBulkRTP(String seqUniqueID,String tranDate);

////RTP Outward
	@Query(value = "select nvl(count(sequence_unique_id),0) from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and (msg_type='OUTWARD_BULK_RTP')", nativeQuery = true)
	String outwardOUTListTotTxs(String tranDate);
	
	@Query(value = "select nvl(count(sequence_unique_id),0) from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and tran_status='SUCCESS' and (msg_type='OUTWARD_BULK_RTP')", nativeQuery = true)
	String outwardOUTLisSucTxs(String tranDate);

	@Query(value = "select nvl(count(sequence_unique_id),0) from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and tran_status!='SUCCESS' and (msg_type='OUTWARD_BULK_RTP')", nativeQuery = true)
	String outwardOUTListFailTxs(String tranDate);
	
	@Query(value = "select nvl(sum(tran_amount),0) from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and (msg_type='OUTWARD_BULK_RTP')", nativeQuery = true)
	String outwardOUTListTotAmt(String tranDate);
	
	@Query(value = "select nvl(sum(tran_amount),0) from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and tran_status='SUCCESS' and (msg_type='OUTWARD_BULK_RTP')", nativeQuery = true)
	String outwardOUTLisSucAmt(String tranDate);

	@Query(value = "select nvl(sum(tran_amount),0) from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and tran_status!='SUCCESS' and (msg_type='OUTWARD_BULK_RTP')", nativeQuery = true)
	String outwardOUTListFailAmt(String tranDate);

	////Reconcillation Count
	@Query(value = "select nvl(sum(tran_amount),0) from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and tran_status='SUCCESS' and msg_type='OUTGOING'", nativeQuery = true)
	String countOutwardReconcillation(String tranDate);

	@Query(value = "select nvl(sum(tran_amount),0) from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and msg_type='OUTGOING' and( (tran_status='SUCCESS' and (cbs_status='CBS_DEBIT_ERROR' or cbs_status='CBS_CREDIT_ERROR'))or (tran_status='FAILURE' and (cbs_status='CBS_DEBIT_OK' or cbs_status='CBS_CREDIT_OK'))or(tran_status!='SUCCESS' and tran_status!='FAILURE'))", nativeQuery = true)
	String countUnmatchReconcillation(String tranDate);

	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and tran_status='SUCCESS' and  msg_type='OUTGOING'", nativeQuery = true)
	List<RTP_Outgoing_hist_entity> outwardReconcillationList(String tranDate);

	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and msg_type='OUTGOING' and( (tran_status='SUCCESS' and (cbs_status='CBS_DEBIT_ERROR' or cbs_status='CBS_CREDIT_ERROR'))or (tran_status='FAILURE' and (cbs_status='CBS_DEBIT_OK' or cbs_status='CBS_CREDIT_OK'))or(tran_status!='SUCCESS' and tran_status!='FAILURE'))", nativeQuery = true)
	List<RTP_Outgoing_hist_entity> unmatchReconcillationList(String tranDate);

	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and (cbs_status='CBS_DEBIT_REVERSE_OK' or cbs_status='CBS_CREDIT_REVERSE_OK')", nativeQuery = true)
	List<RTP_Outgoing_hist_entity> offsetReconcillationList(String tranDate);

	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where trunc(tran_date)=?1 and tran_status='FAILURE' and msg_type='OUTGOING' and (cbs_status!='CBS_DEBIT_OK' and cbs_status!='CBS_DEBIT_REVERSE_OK')", nativeQuery = true)
	List<RTP_Outgoing_hist_entity> failedReconcillationList(String tranDate);

	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE where master_ref_id=?1 UNION ALL select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where master_ref_id=?1", nativeQuery = true)
	List<RTP_Outgoing_hist_entity> findAllCustomBulkCredit(String seqUniqueID,String tranDate);
	
	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_MONITORING_TABLE where master_ref_id=?1 UNION ALL select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where master_ref_id=?1", nativeQuery = true)
	List<RTP_Outgoing_hist_entity> findAllCustomBulkDebit(String seqUniqueID,String tran_date);

	@Query(value = "select * from BIPS_OUTWARD_TRANSACTION_HIST_MONITORING_TABLE where sequence_unique_id=?1", nativeQuery = true)
	RTP_Outgoing_hist_entity findBySequenceUniqueID(String seqUniqueID);
	}
