package com.bornfire.controllers;

import java.io.IOException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.BIPS_allclassentityqrtransaction;
import com.bornfire.entity.CIMCustomerDecodeQRFormatResponse;
import com.bornfire.entity.CIMMerchantQRRequestFormat;
import com.bornfire.entity.CustomerPayResponse;
import com.bornfire.entity.MerchantMaster;
import com.bornfire.entity.MerchantQRRegistration;
import com.bornfire.entity.cimMerchantQRcodeResponse;
import com.bornfire.services.BIPSBankandBranchServices;
import com.bornfire.services.QRCODEServices;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;

@Controller
@ConfigurationProperties("default")
public class QRController {

	@Autowired
	BIPSBankandBranchServices ipsServices;

	@Autowired
	Environment env;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	QRCODEServices qrCODEServices;

	@Autowired
	SequenceGenerator sequence;

	// Static QR Code

	@RequestMapping(value = "staticQRCodeGenarate", method = RequestMethod.GET)
	public String staticQRCodeGenarate(@RequestParam(required = false) String formmode,
			@RequestParam(required = false) String merchantId, Model md, HttpServletRequest req)
			throws SQLException, UnknownHostException {
		String userid = (String) req.getSession().getAttribute("USERID");
		String UNITID = (String) req.getSession().getAttribute("UNITID");
		System.out.println("merchant id" + merchantId);
		String refNumber = sequence.generateMerchantQRPID() + sequence.getRandom4Digit();
		if (formmode.equals("StaticQR")) {
			md.addAttribute("formmode", "StaticQR");
			ResponseEntity<cimMerchantQRcodeResponse> merchantQRResponse = qrCODEServices.getStaticQRCode(merchantId,
					userid, UNITID, refNumber);
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

			md.addAttribute("merchantId", merchantId);
			md.addAttribute("deviceId", "PC");
			md.addAttribute("refNumber", refNumber);
			md.addAttribute("qrCodeImage", imageAsBase64);
			md.addAttribute("formmode", "StaticQR");
		}
		return "BIPS_MerchantOperation";
	}

	// Dynamic QR Code

	@RequestMapping(value = "dynamicQRCodeGenerate", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> dynamicQRCodeGenerate(@RequestParam(required = false) String tran_amt,
			@RequestParam(required = false) String bill_num, HttpServletRequest req)
			throws IOException, WriterException {

		String USERID = (String) req.getSession().getAttribute("USERID");
		String MER_USER_ID = (String) req.getSession().getAttribute("MER_USER_ID");
		String UNITID = (String) req.getSession().getAttribute("unitidacess");

		Map<String, Object> responseMap = new HashMap<>();

		responseMap.put("qrValidTiming", env.getProperty("ipsx.qr.dynamic_valid_timer"));
		responseMap.put("merchantId", MER_USER_ID);
		responseMap.put("deviceId", "PC");
		responseMap.put("formmode", "dynamicQRCode");

		String url1 = env.getProperty("ipsx.url") + "/api/getDynamicQrMerMaucasFormat?merchant_id=" + MER_USER_ID;
		MerchantMaster response = restTemplate.getForObject(url1, MerchantMaster.class);

		if (response == null) {
			responseMap.put("success", false);
			responseMap.put("message", "Failed to retrieve merchant details");
			return responseMap;
		}

		MerchantMaster ms = response;
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
		merchantQRgenerator.setMobile(null);
		merchantQRgenerator.setLoyalty_number(null);
		merchantQRgenerator.setStore_label(null);
		merchantQRgenerator.setCustomer_label(null);
		merchantQRgenerator.setReference_label(sequence.generateMerchantQRPID() + sequence.getRandom4Digit());
		merchantQRgenerator.setTerminal_label(null);
		merchantQRgenerator.setPurpose_of_tran(null);
		merchantQRgenerator.setAdditional_details(null);
		merchantQRgenerator.setTransaction_amt(tran_amt);
		merchantQRgenerator.setBill_number(bill_num);
		merchantQRgenerator.setCustomer_label(ms.getCustomer_label());
		String poiMethod_dynamic = env.getProperty("ipsx.qr.poiMethod_dynamic");
		merchantQRgenerator.setPoi_method(poiMethod_dynamic);

		merchantQRgenerator.setTransaction_amt(tran_amt);
		merchantQRgenerator.setBill_number(bill_num);

		ResponseEntity<cimMerchantQRcodeResponse> merchantQRResponse = qrCODEServices
				.getDynamicQRCode(merchantQRgenerator, USERID, MER_USER_ID, UNITID);

		String imageAsBase64;

		if (merchantQRResponse.getStatusCode() == HttpStatus.OK) {
			if (merchantQRResponse.getBody().getBase64QR().contains("Unable to generate QR code")) {
				responseMap.put("success", false);
				responseMap.put("message", "Unable to generate QR code");
			} else {
				imageAsBase64 = merchantQRResponse.getBody().getBase64QR();
				responseMap.put("success", true);
				responseMap.put("qrCodeUrl", "data:image/png;base64," + imageAsBase64);
			}
		} else {
			responseMap.put("success", false);
			responseMap.put("message", "Something went wrong at server end");
		}
		
		responseMap.put("refNumber", merchantQRgenerator.getReference_label());

		return responseMap;
	}

	// Customer QR Code

	@RequestMapping(value = "getCustomerDataUsingSacnValue", method = RequestMethod.POST)
	public ResponseEntity<CIMCustomerDecodeQRFormatResponse> getdynamicqrcodeMerchantMaucasNewFormatScan(Model md,
			HttpServletRequest rq, @ModelAttribute String tran_id, @RequestBody CIMMerchantQRRequestFormat qrCode)
			throws UnknownHostException {

		String merUserId = (String) rq.getSession().getAttribute("MER_USER_ID");
		System.out.println("Sacn Value : " + qrCode);
		System.out.println("Merchant User Id : " + merUserId);
		ResponseEntity<CIMCustomerDecodeQRFormatResponse> merchantQRResponse = qrCODEServices
				.getMerchantscanQrCodeStr(qrCode);
		md.addAttribute("merchantQRResponse", merchantQRResponse);
		return merchantQRResponse;
	}

	@RequestMapping(value = "customerPaymentInitiate", method = RequestMethod.POST)
	@ResponseBody
	public String merchantreaderinitiate(@RequestBody BIPS_allclassentityqrtransaction formData, Model md,
			HttpServletRequest req) throws UnknownHostException {
		
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String tranamtclassresponse = qrCODEServices.getmerchantreaderinitiate(merUserId, formData, md, req);
		return tranamtclassresponse;
	}

	@Autowired
	com.bornfire.configuration.Encryption Encryption;

	@RequestMapping(value = "getCustomerPayDetails", method = RequestMethod.POST)
	@ResponseBody
	public String getCustomerPayDetails(@RequestParam String merchantId, @RequestParam String deviceId,
			@RequestParam String refNumber, Model md, HttpServletRequest req) throws Exception {
		String url1 = env.getProperty("ipsx.url") + "/api/getCustomerPaydetails?merchant_id=" + merchantId
				+ "&device_id=" + deviceId + "&reference_number=" + refNumber;

		HttpHeaders headers = new HttpHeaders();
		headers.set("PSU_Device_ID", "123123");
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(url1, HttpMethod.GET, entity, String.class);
		String responseBody = response.getBody();
		String decryptedResponse = Encryption.decrypt(responseBody, "123123");
		System.out.println(decryptedResponse);
		if (decryptedResponse.equals("null") || decryptedResponse.equals("") || decryptedResponse.equals(null)) {
			return null;
		} else {
			ObjectMapper objectMapper = new ObjectMapper();
			CustomerPayResponse customerPayResponse = objectMapper.readValue(decryptedResponse,
					CustomerPayResponse.class);
			if (customerPayResponse.getTran_status().equals("SUCCESS")) {
				return "Payment Recieved Successfully";
			} else {
				return null;
			}
		}
	}

}
