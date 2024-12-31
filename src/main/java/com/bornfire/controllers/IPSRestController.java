package com.bornfire.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bornfire.entity.BIPS_Charge_Back_Entity;
import com.bornfire.entity.BIPS_Outward_Trans_Monitoring_Entity;
import com.bornfire.entity.MerchantMaster;
import com.bornfire.entity.MerchantQRRegistration;
import com.bornfire.entity.cimMerchantQRcodeResponse;
import com.bornfire.services.BIPSBankandBranchServices;
import com.google.zxing.WriterException;

@RestController
public class IPSRestController {

	@Autowired
	BIPSBankandBranchServices ipsServices;

	@Autowired
	Environment env;
	
	@Autowired
	private RestTemplate restTemplate;
	
	@RequestMapping(value = "getdynamicqrcode", method = RequestMethod.GET)
	public String getdynamicqrcode(@RequestParam(required = false) String acct_num,
			@RequestParam(required = false) String tran_amt, @RequestParam(required = false) String mob_num,
			@RequestParam(required = false) String loy_num, @RequestParam(required = false) String sto_label,
			@RequestParam(required = false) String cust_label, @RequestParam(required = false) String ref_label,
			@RequestParam(required = false) String ter_label, @RequestParam(required = false) String pur_tran,
			@RequestParam(required = false) String add_det, @RequestParam(required = false) String bill_num, Model md)
			throws UnknownHostException {
		System.out.println();
		System.out.println(tran_amt);
		// MerchantQRRegistration merchantQRgenerator =
		// merchantQrCodeRegRep.findByIdCustom(acct_num);
		MerchantQRRegistration merchantQRgenerator = null;
		merchantQRgenerator.setTransaction_amt(tran_amt);
		merchantQRgenerator.setBill_number(bill_num);
		merchantQRgenerator.setMobile(mob_num);
		merchantQRgenerator.setLoyalty_number(loy_num);
		merchantQRgenerator.setStore_label(sto_label);
		merchantQRgenerator.setCustomer_label(cust_label);
		merchantQRgenerator.setReference_label(ref_label);
		merchantQRgenerator.setTerminal_label(ter_label);
		merchantQRgenerator.setPurpose_of_tran(pur_tran);
		merchantQRgenerator.setAdditional_details(add_det);
		String paycode = env.getProperty("ipsx.qr.payeecode");
		String globalUnique = env.getProperty("ipsx.qr.globalUnique");
		String payload = env.getProperty("ipsx.qr.payload");
		String poiMethod_dynamic = env.getProperty("ipsx.qr.poiMethod_dynamic");
		merchantQRgenerator.setPoi_method(poiMethod_dynamic);
		merchantQRgenerator.setPayee_participant_code(paycode);
		merchantQRgenerator.setGlobal_unique_id(globalUnique);
		merchantQRgenerator.setPayload_format_indicator(payload);
		ResponseEntity<cimMerchantQRcodeResponse> merchantQRResponse = ipsServices
				.getMerchantQrCode(merchantQRgenerator);
		String QrImg;
		String imageAsBase64;
		if (merchantQRResponse.getStatusCode() == HttpStatus.OK) {
			QrImg = merchantQRResponse.getBody().getBase64QR();
			imageAsBase64 = "data:image/png;base64," + QrImg;

		} else {
			if (merchantQRResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
				imageAsBase64 = merchantQRResponse.getBody().getError().toString() + ":"
						+ merchantQRResponse.getBody().getError_Desc().get(0);
			} else {
				imageAsBase64 = "Something went wrong at server end";
			}
		}
		return imageAsBase64;
	}

	@RequestMapping(value = "/getstaticqrcode/{acct_num}", method = RequestMethod.GET)
	public String getstaticqrcode(@PathVariable("acct_num") String acct_num, Model md) throws UnknownHostException {

		// MerchantQRRegistration merchantQRgenerator =
		// merchantQrCodeRegRep.findByIdCustom(acct_num);
		MerchantQRRegistration merchantQRgenerator = null;
		String paycode = env.getProperty("ipsx.qr.payeecode");
		String globalUnique = env.getProperty("ipsx.qr.globalUnique");
		String payload = env.getProperty("ipsx.qr.payload");
		String poiMethod_static = env.getProperty("ipsx.qr.poiMethod_static");
		merchantQRgenerator.setPoi_method(poiMethod_static);
		merchantQRgenerator.setPayee_participant_code(paycode);
		merchantQRgenerator.setGlobal_unique_id(globalUnique);
		merchantQRgenerator.setPayload_format_indicator(payload);

		ResponseEntity<cimMerchantQRcodeResponse> merchantQRResponse = ipsServices
				.getMerchantQrCode(merchantQRgenerator);
		String QrImg;
		String imageAsBase64;
		if (merchantQRResponse.getStatusCode() == HttpStatus.OK) {
			QrImg = merchantQRResponse.getBody().getBase64QR();
			imageAsBase64 = "data:image/png;base64," + QrImg;

		} else {
			if (merchantQRResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
				imageAsBase64 = merchantQRResponse.getBody().getError().toString() + ":"
						+ merchantQRResponse.getBody().getError_Desc().get(0);
			} else {
				imageAsBase64 = "Something went wrong at server end";
			}
		}
		return imageAsBase64;
	}

	@GetMapping(path = "/getstaticqrcodeMerchantMaucas/{acct_num}")
//	@RequestMapping(value = "/getstaticqrcodeMerchantMaucas/{acct_num}", method = RequestMethod.GET)
	public String getstaticqrcodeMerchant(@PathVariable("acct_num") String acct_num, Model md)
			throws UnknownHostException {

		// MerchantMaster ms = merchantmasterRep.findByIdCustom(acct_num);
		MerchantMaster ms = null;
		MerchantQRRegistration merchantQRgenerator = new MerchantQRRegistration();
		String paycode = env.getProperty("ipsx.qr.payeecode");
		String globalUnique = env.getProperty("ipsx.qr.globalUnique");
		String payload = env.getProperty("ipsx.qr.payload");
		String poiMethod_static = env.getProperty("ipsx.qr.poiMethod_static");
		merchantQRgenerator.setPoi_method(poiMethod_static);
		merchantQRgenerator.setPayee_participant_code(paycode);
		merchantQRgenerator.setGlobal_unique_id(globalUnique);
		merchantQRgenerator.setPayload_format_indicator(payload);
		merchantQRgenerator.setMerchant_acct_no(ms.getMerchant_id());
		merchantQRgenerator.setMerchant_id(ms.getMerchant_id());
		merchantQRgenerator.setMerchant_name(ms.getMerchant_name());
		merchantQRgenerator.setMerchant_category_code(ms.getMerchant_cat_code());
		merchantQRgenerator.setTransaction_crncy(ms.getCurr());
		merchantQRgenerator.setTip_or_conv_indicator(ms.getTip_or_conv_indicator());
		merchantQRgenerator.setConv_fees_type(ms.getConv_fees_type());
		merchantQRgenerator.setValue_conv_fees(ms.getValue_conv_fees());
		merchantQRgenerator.setCity(ms.getMerchant_city());
		merchantQRgenerator.setCountry("MU");
		merchantQRgenerator.setZip_code(ms.getPincode());
		merchantQRgenerator.setBill_number(ms.getTr());
		merchantQRgenerator.setMobile(ms.getMerchant_mob_no());
		merchantQRgenerator.setLoyalty_number(ms.getLoyalty_number());
		merchantQRgenerator.setCustomer_label(ms.getCustomer_label());
		merchantQRgenerator.setStore_label(ms.getStore_label());
		merchantQRgenerator.setTerminal_label(ms.getTerminal_label());
		merchantQRgenerator.setReference_label(ms.getReference_label());
		merchantQRgenerator.setPurpose_of_tran(ms.getPurpose_of_tran());
		merchantQRgenerator.setAdditional_details(ms.getAdd_details_req());

		merchantQRgenerator.setBill_number(ms.getTr());
		merchantQRgenerator.setCustomer_label(ms.getCustomer_label());

		ResponseEntity<cimMerchantQRcodeResponse> merchantQRResponse = ipsServices
				.getMerchantQrCode(merchantQRgenerator);
		String QrImg;
		String imageAsBase64;
		if (merchantQRResponse.getStatusCode() == HttpStatus.OK) {
			QrImg = merchantQRResponse.getBody().getBase64QR();
			imageAsBase64 = "data:image/png;base64," + QrImg;

		} else {
			if (merchantQRResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
				imageAsBase64 = merchantQRResponse.getBody().getError().toString() + ":"
						+ merchantQRResponse.getBody().getError_Desc().get(0);
			} else {
				imageAsBase64 = "Something went wrong at server end";
			}
		}
		return imageAsBase64;
	}

	@GetMapping(value = "getdynamicqrcodeMerchantMaucas")
	@RequestMapping(value = "getdynamicqrcodeMerchantMaucas", method = RequestMethod.GET)
	public String getdynamicqrcodeMerchantMaucas(@RequestParam(required = false) String acct_num,
			@RequestParam(required = false) String tran_amt, @RequestParam(required = false) String mob_num,
			@RequestParam(required = false) String loy_num, @RequestParam(required = false) String sto_label,
			@RequestParam(required = false) String cust_label, @RequestParam(required = false) String ref_label,
			@RequestParam(required = false) String ter_label, @RequestParam(required = false) String pur_tran,
			@RequestParam(required = false) String add_det, @RequestParam(required = false) String bill_num, Model md)
			throws UnknownHostException {
		System.out.println();
		System.out.println(tran_amt);
		// MerchantMaster ms = merchantmasterRep.findByIdCustom(acct_num);
		MerchantMaster ms = null;
		MerchantQRRegistration merchantQRgenerator = new MerchantQRRegistration();
		String paycode = env.getProperty("ipsx.qr.payeecode");
		String globalUnique = env.getProperty("ipsx.qr.globalUnique");
		String payload = env.getProperty("ipsx.qr.payload");
		merchantQRgenerator.setPayee_participant_code(paycode);
		merchantQRgenerator.setGlobal_unique_id(globalUnique);
		merchantQRgenerator.setPayload_format_indicator(payload);
		merchantQRgenerator.setMerchant_acct_no(ms.getMerchant_id());
		merchantQRgenerator.setMerchant_id(ms.getMerchant_id());
		merchantQRgenerator.setMerchant_name(ms.getMerchant_name());
		merchantQRgenerator.setMerchant_category_code(ms.getMerchant_cat_code());
		merchantQRgenerator.setTransaction_crncy(ms.getCurr());
		merchantQRgenerator.setTip_or_conv_indicator(ms.getTip_or_conv_indicator());
		merchantQRgenerator.setConv_fees_type(ms.getConv_fees_type());
		merchantQRgenerator.setValue_conv_fees(ms.getValue_conv_fees());
		merchantQRgenerator.setCity(ms.getMerchant_city());
		merchantQRgenerator.setCountry("MU");
		merchantQRgenerator.setZip_code(ms.getPincode());
		merchantQRgenerator.setTransaction_amt(tran_amt);
		merchantQRgenerator.setBill_number(bill_num);
		merchantQRgenerator.setMobile(mob_num);
		merchantQRgenerator.setLoyalty_number(loy_num);
		merchantQRgenerator.setStore_label(sto_label);
		merchantQRgenerator.setCustomer_label(cust_label);
		merchantQRgenerator.setReference_label(ref_label);
		merchantQRgenerator.setTerminal_label(ter_label);
		merchantQRgenerator.setPurpose_of_tran(pur_tran);
		merchantQRgenerator.setAdditional_details(add_det);

		merchantQRgenerator.setBill_number(ms.getTr());
		merchantQRgenerator.setCustomer_label(ms.getCustomer_label());

		String poiMethod_dynamic = env.getProperty("ipsx.qr.poiMethod_dynamic");
		merchantQRgenerator.setPoi_method(poiMethod_dynamic);
		merchantQRgenerator.setPayee_participant_code(paycode);
		merchantQRgenerator.setGlobal_unique_id(globalUnique);
		merchantQRgenerator.setPayload_format_indicator(payload);
		ResponseEntity<cimMerchantQRcodeResponse> merchantQRResponse = ipsServices
				.getMerchantQrCode(merchantQRgenerator);
		String QrImg;
		String imageAsBase64;
		if (merchantQRResponse.getStatusCode() == HttpStatus.OK) {
			QrImg = merchantQRResponse.getBody().getBase64QR();
			imageAsBase64 = "data:image/png;base64," + QrImg;

		} else {
			if (merchantQRResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
				imageAsBase64 = merchantQRResponse.getBody().getError().toString() + ":"
						+ merchantQRResponse.getBody().getError_Desc().get(0);
			} else {
				imageAsBase64 = "Something went wrong at server end";
			}
		}
		return imageAsBase64;
	}

	@GetMapping(path = "/getstaticqrcodeMerchantMaucasNewFormat/{acct_num}")
//	@RequestMapping(value = "/getstaticqrcodeMerchantMaucas/{acct_num}", method = RequestMethod.GET)
	public String getstaticqrcodeMerchantMaucasNewFormat(@PathVariable("acct_num") String acct_num, Model md)
			throws IOException, WriterException {

		// MerchantMaster ms = merchantmasterRep.findByIdCustom(acct_num);
		MerchantMaster ms = null;
		MerchantQRRegistration merchantQRgenerator = new MerchantQRRegistration();
		String paycode = env.getProperty("ipsx.qr.payeecode");
		String globalUnique = env.getProperty("ipsx.qr.globalUnique");
		String payload = env.getProperty("ipsx.qr.payload");
		String poiMethod_static = env.getProperty("ipsx.qr.poiMethod_static");
		merchantQRgenerator.setPoi_method(poiMethod_static);
		merchantQRgenerator.setPayee_participant_code(paycode);
		merchantQRgenerator.setGlobal_unique_id(globalUnique);
		merchantQRgenerator.setPayload_format_indicator(payload);
		merchantQRgenerator.setMerchant_acct_no(ms.getMerchant_id());
		merchantQRgenerator.setMerchant_id(ms.getMerchant_id());
		merchantQRgenerator.setMerchant_name(ms.getMerchant_name());
		merchantQRgenerator.setMerchant_category_code(ms.getMerchant_cat_code());
		merchantQRgenerator.setTransaction_crncy(ms.getCurr());
		merchantQRgenerator.setTip_or_conv_indicator(ms.getTip_or_conv_indicator());
		merchantQRgenerator.setConv_fees_type(ms.getConv_fees_type());
		merchantQRgenerator.setValue_conv_fees(ms.getValue_conv_fees());
		merchantQRgenerator.setCity(ms.getMerchant_city());
		merchantQRgenerator.setCountry("MU");
		merchantQRgenerator.setZip_code(ms.getPincode());
		merchantQRgenerator.setBill_number(ms.getTr());
		merchantQRgenerator.setMobile(ms.getMerchant_mob_no());
		merchantQRgenerator.setLoyalty_number(ms.getLoyalty_number());
		merchantQRgenerator.setCustomer_label(ms.getCustomer_label());
		merchantQRgenerator.setStore_label(ms.getStore_label());
		merchantQRgenerator.setTerminal_label(ms.getTerminal_label());
		merchantQRgenerator.setReference_label(ms.getReference_label());
		merchantQRgenerator.setPurpose_of_tran(ms.getPurpose_of_tran());
		merchantQRgenerator.setAdditional_details(ms.getAdd_details_req());

		merchantQRgenerator.setBill_number(ms.getTr());
		merchantQRgenerator.setCustomer_label(ms.getCustomer_label());

		ResponseEntity<cimMerchantQRcodeResponse> merchantQRResponse = ipsServices
				.getMerchantQrCodeStr(merchantQRgenerator);
		String QrImg;
		String imageAsBase64;
		if (merchantQRResponse.getStatusCode() == HttpStatus.OK) {
			QrImg = merchantQRResponse.getBody().getBase64QR();
			imageAsBase64 = "data:image/png;base64," + QrImg;

		} else {
			if (merchantQRResponse.getStatusCode() == HttpStatus.BAD_REQUEST) {
				imageAsBase64 = merchantQRResponse.getBody().getError().toString() + ":"
						+ merchantQRResponse.getBody().getError_Desc().get(0);
			} else {
				imageAsBase64 = "Something went wrong at server end";
			}
		}
		return imageAsBase64;
	}

	@RequestMapping(value = "DownloadTxnReport", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> TransactionDownload(
	        @RequestParam(value = "from_date", required = false) String from_date,
	        @RequestParam(value = "to_date", required = false) String to_date,
	        @RequestParam(value = "unit_id", required = false) String unit_id,
	        @RequestParam(value = "tran_type", required = false) String tran_type,
	        @RequestParam(value = "filetype", required = false) String filetype,
	        HttpServletRequest req) throws IOException, SQLException {

	    String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
	    String completeUrl = env.getProperty("ipsx.url") + "/api/TransactionReportDownload?merchant_id=" + merUserId +
	            "&filetype=" + filetype + "&unit_id=" + unit_id + "&tran_type=" + tran_type + 
	            "&from_date=" + from_date + "&to_date=" + to_date;

	    // Fetch the file from the API
	    ResponseEntity<byte[]> response = restTemplate.getForEntity(completeUrl, byte[].class);

	    // Create the InputStreamResource
	    InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(response.getBody()));

	    // Get the content disposition header from the response
	    String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);

	    return ResponseEntity.ok()
	            .headers(response.getHeaders()) // Use headers from the second method
	            .body(resource);
	}

	@RequestMapping(value = "DownloadChargeTxnReport", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> DownloadChargeTxnReport(
	        @RequestParam(value = "from_date", required = false) String from_date,
	        @RequestParam(value = "to_date", required = false) String to_date,
	        @RequestParam(value = "unit_id", required = false) String unit_id,
	        @RequestParam(value = "tran_type", required = false) String tran_type,
	        @RequestParam(value = "filetype", required = false) String filetype,
	        HttpServletRequest req) throws IOException, SQLException {

	    String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
	    String completeUrl = env.getProperty("ipsx.url") + "/api/ChargebackTransactionReportDownload?merchant_id=" + merUserId +
	            "&filetype=" + filetype + "&unit_id=" + unit_id + "&tran_type=" + tran_type + 
	            "&from_date=" + from_date + "&to_date=" + to_date;

	    // Fetch the file from the API
	    ResponseEntity<byte[]> response = restTemplate.getForEntity(completeUrl, byte[].class);

	    // Create the InputStreamResource
	    InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(response.getBody()));

	    // Get the content disposition header from the response
	    String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);

	    return ResponseEntity.ok()
	            .headers(response.getHeaders()) // Use headers from the second method
	            .body(resource);
	}

	
	@RequestMapping(value = "getTransactionRecords", method = RequestMethod.GET)
	public List<BIPS_Outward_Trans_Monitoring_Entity> getTransactionRecords(
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate, HttpServletRequest req) {

		SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
		String valueDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String UNITID = (String) req.getSession().getAttribute("UNITID");
		List<BIPS_Outward_Trans_Monitoring_Entity> records = new ArrayList<>();
		String url = env.getProperty("ipsx.url") + "/api/CustomerTransactionListSingleDate?merchant_id=" + merUserId+"&Date="+valueDateRef+"&unit_id="+UNITID;
			BIPS_Outward_Trans_Monitoring_Entity[] responseArray = restTemplate.getForObject(url,
					BIPS_Outward_Trans_Monitoring_Entity[].class);
			List<BIPS_Outward_Trans_Monitoring_Entity> response = Arrays.asList(responseArray);
			records = response;

		return records;
	}
	
	@RequestMapping(value = "getFeesAndChargesRecords", method = RequestMethod.GET)
	public List<BIPS_Outward_Trans_Monitoring_Entity> getFeesAndChargesRecords(
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate, HttpServletRequest req) {

		SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
		String valueDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String UNITID = (String) req.getSession().getAttribute("UNITID");
		List<BIPS_Outward_Trans_Monitoring_Entity> records = new ArrayList<>();
		String url = env.getProperty("ipsx.url") + "/api/FeesAndChargesListSingleDate?merchant_id=" + merUserId+"&Date="+valueDateRef+"&unit_id="+UNITID;
			BIPS_Outward_Trans_Monitoring_Entity[] responseArray = restTemplate.getForObject(url,
					BIPS_Outward_Trans_Monitoring_Entity[].class);
			List<BIPS_Outward_Trans_Monitoring_Entity> response = Arrays.asList(responseArray);
			records = response;

		return records;
	}
	
	
	@RequestMapping(value = "getChargeBackRecords", method = RequestMethod.GET)
	public List<BIPS_Charge_Back_Entity> getChargeBackRecords(
	        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate,
	        @RequestParam String tranRecord,
	        HttpServletRequest req) {

	    SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
	    String valueDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();
	    
	    String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
	    String UNITID = (String) req.getSession().getAttribute("UNITID");
	    
	    List<BIPS_Charge_Back_Entity> records = new ArrayList<>();
	    
	    // Assuming your REST endpoint expects a date format like 'dd-MMM-yyyy'
	    String url = env.getProperty("ipsx.url") 
	                 + "/api/ChargeBackListSingleDate?merchant_id=" + merUserId 
	                 + "&date=" + valueDateRef 
	                 + "&unit_id=" + UNITID 
	                 + "&remarks=" + tranRecord;
	    
	    BIPS_Charge_Back_Entity[] responseArray = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
	    if (responseArray != null) {
	        records = Arrays.asList(responseArray);
	    }

	    return records;
	}
	
	@RequestMapping(value = "getInitiateRecords", method = RequestMethod.GET)
	public List<BIPS_Outward_Trans_Monitoring_Entity> getInitiateRecords(
	        @RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate,
	        @RequestParam String tranRecord,
	        HttpServletRequest req) {

	    SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
	    String valueDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();
	    
	    String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
	    String UNITID = (String) req.getSession().getAttribute("UNITID");
	    
	    List<BIPS_Outward_Trans_Monitoring_Entity> records = new ArrayList<>();
	    
	    // Assuming your REST endpoint expects a date format like 'dd-MMM-yyyy'
	    String url = env.getProperty("ipsx.url") 
	                 + "/api/InitiateSingleDate?merchant_id=" + merUserId 
	                 + "&date=" + valueDateRef 
	                 + "&unit_id=" + UNITID 
	                 + "&remarks=" + tranRecord;
	    
	    BIPS_Outward_Trans_Monitoring_Entity[] responseArray = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity[].class);
	    if (responseArray != null) {
	        records = Arrays.asList(responseArray);
	    }

	    return records;
	}
	
	
	@RequestMapping(value = "DownloadUnitList", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> DownloadUnitList(
	        @RequestParam(value = "merchant_user_id", required = false) String merchant_user_id,
	        @RequestParam(value = "filetype", required = false) String filetype,
	        HttpServletRequest req) throws IOException, SQLException {

	    String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
	    //TransactionReportDownload
	    String completeUrl = env.getProperty("ipsx.url") + "/api/UnitListReportDownload?merchant_user_id=" + merUserId +
	            "&filetype=" + filetype;

	    // Fetch the file from the API
	    ResponseEntity<byte[]> response = restTemplate.getForEntity(completeUrl, byte[].class);

	    // Create the InputStreamResource
	    InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(response.getBody()));

	    // Get the content disposition header from the response
	    String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);

	    return ResponseEntity.ok()
	            .headers(response.getHeaders()) // Use headers from the second method
	            .body(resource);
	}
	
	
	@RequestMapping(value = "DownloadUserList", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> DownloadUserList(
	        @RequestParam(value = "merchant_user_id", required = false) String merchant_user_id,
	        @RequestParam(value = "filetype", required = false) String filetype,
	        HttpServletRequest req) throws IOException, SQLException {

	    String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
	    //TransactionReportDownload
	    String completeUrl = env.getProperty("ipsx.url") + "/api/UserListReportDownload?merchant_user_id=" + merUserId +
	            "&filetype=" + filetype;

	    // Fetch the file from the API
	    ResponseEntity<byte[]> response = restTemplate.getForEntity(completeUrl, byte[].class);

	    // Create the InputStreamResource
	    InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(response.getBody()));

	    // Get the content disposition header from the response
	    String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);

	    return ResponseEntity.ok()
	            .headers(response.getHeaders()) // Use headers from the second method
	            .body(resource);
	}
	
	@RequestMapping(value = "DownloadDeviceList", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<InputStreamResource> DownloadDeviceList(
	        @RequestParam(value = "merchant_user_id", required = false) String merchant_user_id,
	        @RequestParam(value = "filetype", required = false) String filetype,
	        HttpServletRequest req) throws IOException, SQLException {

	    String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
	    //TransactionReportDownload
	    String completeUrl = env.getProperty("ipsx.url") + "/api/DeviceListReportDownload?merchant_user_id=" + merUserId +
	            "&filetype=" + filetype;

	    // Fetch the file from the API
	    ResponseEntity<byte[]> response = restTemplate.getForEntity(completeUrl, byte[].class);

	    // Create the InputStreamResource
	    InputStreamResource resource = new InputStreamResource(new ByteArrayInputStream(response.getBody()));

	    // Get the content disposition header from the response
	    String contentDisposition = response.getHeaders().getFirst(HttpHeaders.CONTENT_DISPOSITION);

	    return ResponseEntity.ok()
	            .headers(response.getHeaders()) // Use headers from the second method
	            .body(resource);
	}
	
}
