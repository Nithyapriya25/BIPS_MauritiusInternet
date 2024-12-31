package com.bornfire.services;

import java.util.*;

import javax.servlet.http.HttpServletRequest;

import com.bornfire.entity.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.bornfire.configuration.Encryption;

@Service
@ConfigurationProperties("output")
//@Transactional

public class BankAndBranchMasterServices {

	private static final org.jboss.logging.Logger logger = LoggerFactory.logger(BankAndBranchMasterServices.class);

	@Autowired
	Encryption encryption;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Environment env;

	public String VerifyUserMer(@RequestParam String user_ids, @RequestParam String userID) {
		String msg = "";
		try {
			if (user_ids != null) {
				String url3 = env.getProperty("ipsx.url") + "/api/InternetVerifyUserdetails?user_id="+user_ids+"&USERID="+userID;
				String response = restTemplate.getForObject(url3,String.class);
				return response;
			} else {
				msg = "Error occurred while verifying user";
				return msg;
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "Exception occurred while verifying user";
			return msg;
		}
	}

	public ResponseEntity<String> VerifyDeviceMer(String user_ids, String userID,
			BIPS_Mer_Device_Management_Entity roleEntity) {
		String msg = "";

		try {
			System.out.println("In the second stage of verification " + user_ids);
			if (roleEntity == null) {
				msg = "Role entity not provided or invalid";
				return ResponseEntity.badRequest().body(msg);
			}

			System.out.println("In the second stage of verification " + roleEntity.getDevice_id());
			String updateUserUrl = env.getProperty("ipsx.url") + "api/UpdateDeviceData";
			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);

			System.out.println("Service in the second stage");
			roleEntity.setVerify_user(userID);
			roleEntity.setVerify_time(new Date());
			roleEntity.setEntry_flag("Y");
			roleEntity.setModify_flag("N");

			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(roleEntity);
			System.out.println(requestBody);
			// Encrypt the JSON string
			String encryptedBody = encryption.encrypt(requestBody, "123123");
			EncryptionEntity o = new EncryptionEntity();
			o.setEncryptedstring(encryptedBody);

			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);
			ResponseEntity<String> response = restTemplate.exchange(updateUserUrl, HttpMethod.PUT,
					encryptedRequestEntity, String.class);

			// Handle the response from Application B
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Device verified successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error verifying user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "Exception occurred while verifying user";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
		}
	}

	public ResponseEntity<String> VerifyPassMer(String user_ids, String userID, HttpServletRequest req,
			LoginEntity roleEntity) {
		String msg = "";
		String USERID = (String) req.getSession().getAttribute("USERID");
		try {
			System.out.println("In the second stage of verification " + user_ids);
			if (roleEntity == null) {
				msg = "Role entity not provided or invalid";
				return ResponseEntity.badRequest().body(msg);
			}

			System.out.println("In the second stage of verification " + roleEntity.getMerchant_rep_id());
			String updateUserUrl = env.getProperty("ipsx.url") + "api/VerifyRepresentativedetails";
			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);

			System.out.println("Service in the second stage");
			roleEntity.setVerify_time(new Date());
			roleEntity.setEntry_flag("Y");
			roleEntity.setVerify_user(USERID);
			roleEntity.setEntry_user(USERID);
			roleEntity.setModify_flag("N");
			System.out.println("VERIFY USER NEW  " + roleEntity.getVerify_user());
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(roleEntity);
			System.out.println(requestBody);
			// Encrypt the JSON string
			String encryptedBody = encryption.encrypt(requestBody, "123123");
			EncryptionEntity o = new EncryptionEntity();
			o.setEncryptedstring(encryptedBody);

			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);
			ResponseEntity<String> response = restTemplate.exchange(updateUserUrl, HttpMethod.PUT,
					encryptedRequestEntity, String.class);

			// Handle the response from Application B

			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Password verified successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error verifying user.");
			}

		} catch (Exception e) {
			e.printStackTrace();
			msg = "Exception occurred while verifying user";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
		}
	}

	public String RepLogin(BIPS_Password_Management_Entity repLogin) throws Exception {

		String url = env.getProperty("ipsx.url") + "api/LoginForTab";
		HttpHeaders headers = new HttpHeaders();
		headers.set("PSU_Device_ID", "123123");
		headers.setContentType(MediaType.APPLICATION_JSON);
		if (repLogin == null) {
			logger.warn("BIPS_Password_Management_Entity is null");
			return "Error: BIPS_Password_Management_Entity is null";
		}
		ObjectMapper objectMapper = new ObjectMapper();
		String requestBody = objectMapper.writeValueAsString(repLogin);
		System.out.println(requestBody);
		// Encrypt the JSON string
		String encryptedBody = encryption.encrypt(requestBody, "123123");
		EncryptionEntity o = new EncryptionEntity();
		o.setEncryptedstring(encryptedBody);
		System.out.println(encryptedBody);
		// Create a new HttpEntity with the encrypted body and the same headers
		HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, encryptedRequestEntity,
					String.class);
			System.out.println("Res" + response);

			// Check HTTP status code
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody();
			} else {
			}
		} catch (HttpClientErrorException e) {
			logger.error("HTTP client error during Merchant Representative Login", e);
			return "Error Merchant Representative Login: " + e.getRawStatusCode();
		} catch (HttpServerErrorException e) {
			logger.error("HTTP server error during Merchant Representative Login", e);
			return "Error Merchant Repres(entative Login: " + e.getRawStatusCode();
		} catch (Exception e) {
			logger.error("Error during Merchant Representative Login", e);
			return "Error Merchant Representative Login";
		}
		return encryptedBody;
	}

}