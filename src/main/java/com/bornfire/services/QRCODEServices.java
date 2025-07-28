package com.bornfire.services;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.BIPS_allclassentityqrtransaction;
import com.bornfire.entity.CIMCustomerDecodeQRFormatResponse;
import com.bornfire.entity.CIMMerchantQRRequestFormat;
import com.bornfire.entity.CIMMerchantQRcodeRequest;
import com.bornfire.entity.CimDynamicMaucasRequest;
import com.bornfire.entity.MerchantMaster;
import com.bornfire.entity.MerchantQRRegistration;
import com.bornfire.entity.Resposeforprinter;
import com.bornfire.entity.cimMerchantQRcodeResponse;
import com.google.gson.Gson;

@Service
@ConfigurationProperties("output")
public class QRCODEServices {

	// Static QR Generation

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Environment env;

	public String deviceid() {
		String device = "PC";
		return device;
	}

	public ResponseEntity<cimMerchantQRcodeResponse> getStaticQRCode(String merchantId, String merUserId, String userid,
			String UNITID, String refNumber) throws UnknownHostException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("P-ID", sequence.generateMerchantQRPID());
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-Channel", "BIPS");
		httpHeaders.set("Merchant_ID", merchantId);
		// httpHeaders.set("Device_ID", deviceid());
		// httpHeaders.set("Reference_Number", refNumber);
		// httpHeaders.set("User-ID", userid);
		// httpHeaders.set("Unit-ID", UNITID);
		httpHeaders.set("PSU-Resv-Field2", null);

		System.out.println("MMEERR ID");
		HttpEntity<CIMMerchantQRcodeRequest> entity = new HttpEntity<>(httpHeaders);
		ResponseEntity<cimMerchantQRcodeResponse> response = null;
		try {
			response = restTemplate.postForEntity(env.getProperty("ipsx.url") + "/api/ws/StaticMaucasMur", entity,
					cimMerchantQRcodeResponse.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				return new ResponseEntity<>(response.getBody(), HttpStatus.OK);
			} else {
				return response;
			}
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode() == HttpStatus.BAD_REQUEST) {
				cimMerchantQRcodeResponse errorRestResponse = new Gson().fromJson(ex.getResponseBodyAsString(),
						cimMerchantQRcodeResponse.class);
				return new ResponseEntity<>(errorRestResponse, HttpStatus.BAD_REQUEST);
			} else {
				cimMerchantQRcodeResponse c24ftResponse = new cimMerchantQRcodeResponse("500",
						Arrays.asList("Something went wrong at server end"));
				return new ResponseEntity<>(c24ftResponse, ex.getStatusCode());
			}
		} catch (HttpServerErrorException ex) {
			cimMerchantQRcodeResponse c24ftResponse = new cimMerchantQRcodeResponse("500",
					Arrays.asList("Something went wrong at server end"));
			return new ResponseEntity<>(c24ftResponse, ex.getStatusCode());
		}
	}

	public ResponseEntity<cimMerchantQRcodeResponse> getDynamicQRCode(MerchantQRRegistration merchantQRgenerator,
			String USERID, String MER_USER_ID, String UNITID) throws UnknownHostException {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("P-ID", sequence.generateMerchantQRPID());
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-Channel", "BIPS");
		httpHeaders.set("Merchant_ID", merchantQRgenerator.getMerchant_id());
		httpHeaders.set("PSU-Resv-Field2", "RESVFIELD");
		httpHeaders.set("Transaction_Amt", "100");
		httpHeaders.set("Mobile_Number", "9028909890");
		httpHeaders.set("Loyality_Number", "01");
		httpHeaders.set("Store_Label", "storelabel");
		httpHeaders.set("Customer_Label", "10");
		httpHeaders.set("Reference_Label", "20");
		httpHeaders.set("Terminal_Label", "10");
		httpHeaders.set("Purpose_Of_Tran", "newtran");
		httpHeaders.set("Additonal_Detail", "01");
		httpHeaders.set("Bill_Number", "01");

		// httpHeaders.set("Reference_Number", sequence.generateMerchantQRPID() +
		// sequence.getRandom4Digit());

		CimDynamicMaucasRequest cimDynamicMaucasRequest = new CimDynamicMaucasRequest();
		cimDynamicMaucasRequest.setRef_label(merchantQRgenerator.getReference_label());
		System.out.println(cimDynamicMaucasRequest.getRef_label());
		cimDynamicMaucasRequest.setTran_amt(merchantQRgenerator.getTransaction_amt());
		cimDynamicMaucasRequest.setBill_num(merchantQRgenerator.getBill_number());
		cimDynamicMaucasRequest.setSto_label(merchantQRgenerator.getStore_label());
		cimDynamicMaucasRequest.setCust_label(merchantQRgenerator.getCustomer_label());
		// cimDynamicMaucasRequest.setRef_label(merchantQRgenerator.getReference_label());
		cimDynamicMaucasRequest.setTer_label(merchantQRgenerator.getTerminal_label());
		cimDynamicMaucasRequest.setPur_tran(merchantQRgenerator.getPurpose_of_tran());
		cimDynamicMaucasRequest.setAdd_det(merchantQRgenerator.getAdditional_details());

		cimDynamicMaucasRequest.setMob_num(merchantQRgenerator.getMobile());
		cimDynamicMaucasRequest.setLoy_num(merchantQRgenerator.getLoyalty_number());
		cimDynamicMaucasRequest.setMerchant_ID(merchantQRgenerator.getMerchant_id());

		HttpEntity<CimDynamicMaucasRequest> entity = new HttpEntity<>(cimDynamicMaucasRequest, httpHeaders);
		ResponseEntity<cimMerchantQRcodeResponse> response = null;
		try {
			response = restTemplate.postForEntity(env.getProperty("ipsx.url") + "/api/ws/DynamicMaucasMur", entity,
					cimMerchantQRcodeResponse.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				System.out.println("data-->OK");
				return new ResponseEntity<cimMerchantQRcodeResponse>(response.getBody(), HttpStatus.OK);
			} else {
				return response;
			}
		} catch (HttpClientErrorException ex) {
			if (ex.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
				cimMerchantQRcodeResponse errorRestResponse = new Gson()
						.fromJson(ex.getResponseBodyAsString().toString(), cimMerchantQRcodeResponse.class);
				return new ResponseEntity<cimMerchantQRcodeResponse>(errorRestResponse, HttpStatus.BAD_REQUEST);
			} else {
				cimMerchantQRcodeResponse c24ftResponse = new cimMerchantQRcodeResponse("500",
						Arrays.asList("Something went wrong at server end"));
				return new ResponseEntity<cimMerchantQRcodeResponse>(c24ftResponse, ex.getStatusCode());
			}
		} catch (HttpServerErrorException ex) {
			cimMerchantQRcodeResponse c24ftResponse = new cimMerchantQRcodeResponse("500",
					Arrays.asList("Something went wrong at server end"));
			return new ResponseEntity<cimMerchantQRcodeResponse>(c24ftResponse, ex.getStatusCode());
		}
	}

	public ResponseEntity<CIMCustomerDecodeQRFormatResponse> getMerchantscanQrCodeStr(CIMMerchantQRRequestFormat Qrcode)
			throws UnknownHostException {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("P-ID", sequence.generateMerchantQRPID());
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-Channel", "BIPS");
		HttpEntity<CIMMerchantQRRequestFormat> entity = new HttpEntity<>(Qrcode, httpHeaders);
		ResponseEntity<CIMCustomerDecodeQRFormatResponse> response = null;
		try {
			response = restTemplate.postForEntity(env.getProperty("ipsx.url") + "/api/ws/scanCustomerQRcode", entity,
					CIMCustomerDecodeQRFormatResponse.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				System.out.println("data-->OK");
				return new ResponseEntity<CIMCustomerDecodeQRFormatResponse>(response.getBody(), HttpStatus.OK);
			} else {
				return response;
			}
		} catch (HttpClientErrorException ex) {
			System.out.println(ex.getMessage());
		}
		return response;
	}

	public String getmerchantreaderinitiate(String merUserId, BIPS_allclassentityqrtransaction Qrcode, Model md,
			HttpServletRequest req) throws UnknownHostException {

		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("P-ID", sequence.generateMerchantQRPID());
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-ID", "web");
		httpHeaders.set("PSU-Channel", "web");
		httpHeaders.set("PSU-Resv-Field1", "");
		httpHeaders.set("PSU-Resv-Field2", "");

		String urli = env.getProperty("ipsx.url") + "/api/Merchantmasterviewdetail?merchant_user_id=" + merUserId;
		MerchantMaster merchantResponse = restTemplate.getForObject(urli, MerchantMaster.class);
		Qrcode.setMerchant_id(merchantResponse.getMerchant_id());
		Qrcode.setMerchant_name(merchantResponse.getMerchant_name());
		Qrcode.setMerchant_mob_no(merchantResponse.getMerchant_mob_no());
		Qrcode.setMerchant_deviceid("PC");
		LocalDateTime now = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
		Qrcode.setMerchant_reference_label(merUserId + now.format(formatter));
		Qrcode.setMerchant_status(merchantResponse.getStatus());
		Qrcode.setCustomer_reference_label(sequence.generateMerchantQRPID() + sequence.getRandom4Digit());
		Qrcode.setPurpose_of_tran(Qrcode.getPurpose_of_tran());
		Qrcode.setGlobal_unique_id(env.getProperty("ipsx.qr.globalUnique"));
		HttpEntity<BIPS_allclassentityqrtransaction> entity = new HttpEntity<>(Qrcode, httpHeaders);
		String response = null;
		try {
			ResponseEntity<String> responseEntity = restTemplate.postForEntity(
					env.getProperty("ipsx.url") + "/api/ws/InitiateCustomerTransaction", entity, String.class);
			if (responseEntity.getStatusCode() == HttpStatus.OK) {
				return responseEntity.getBody();
			} else {
				return responseEntity.getBody();
			}
		} catch (HttpClientErrorException ex) {
			System.err.println("HttpClientErrorException: " + ex.getMessage());
			return ex.getMessage();
		} catch (Exception ex) {
			System.err.println("Exception: " + ex.getMessage());
			return ex.getMessage();
		}
	}

}
