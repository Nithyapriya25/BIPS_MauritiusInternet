package com.bornfire.entity;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface UPIQrRep extends JpaRepository<QRUrlGobalEntity, String> {
	Optional<QRUrlGobalEntity> findById(String directorId);

	@Query(value = "select * from BIPS_QR_UPI_GLOBAL ", nativeQuery = true)
	List<QRUrlGobalEntity> findAllCustom();
	
	@Query(value = "select * from BIPS_QR_UPI_GLOBAL where del_flg<>'Y' ", nativeQuery = true)
	List<QRUrlGobalEntity> findAllData();

	@Query(value = "select * from BIPS_QR_UPI_GLOBAL where pa=?1", nativeQuery = true)
	QRUrlGobalEntity findByIdCustom(String Id);
	
}
