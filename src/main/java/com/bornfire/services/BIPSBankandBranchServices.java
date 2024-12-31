
package com.bornfire.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.CIMMerchantQRAddlInfo;
import com.bornfire.entity.CIMMerchantQRcodeAcctInfo;
import com.bornfire.entity.CIMMerchantQRcodeRequest;
import com.bornfire.entity.CimDynamicMaucasRequest;
import com.bornfire.entity.MerchantQRRegistration;
import com.bornfire.entity.cimMerchantQRcodeResponse;
import com.google.gson.Gson;

@Service
@ConfigurationProperties("output")
public class BIPSBankandBranchServices {

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	Environment env;

	public ResponseEntity<cimMerchantQRcodeResponse> getMerchantQrCode(MerchantQRRegistration merchantQRgenerator)
			throws UnknownHostException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("P-ID", sequence.generateMerchantQRPID());
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-Channel", "BIPS");
		httpHeaders.set("Merchant_ID", merchantQRgenerator.getMerchant_id());

		CIMMerchantQRcodeRequest cimMerchantQRcodeRequest = new CIMMerchantQRcodeRequest();
		System.out.println(merchantQRgenerator.getPayload_format_indicator().toString());
		cimMerchantQRcodeRequest.setPayloadFormatIndiator(merchantQRgenerator.getPayload_format_indicator().toString());
		cimMerchantQRcodeRequest.setPointOfInitiationFormat(merchantQRgenerator.getPoi_method().toString());

		CIMMerchantQRcodeAcctInfo merchantQRAcctInfo = new CIMMerchantQRcodeAcctInfo();
		merchantQRAcctInfo.setGlobalID(merchantQRgenerator.getGlobal_unique_id());
		merchantQRAcctInfo.setPayeeParticipantCode(merchantQRgenerator.getPayee_participant_code());
		merchantQRAcctInfo.setMerchantAcctNumber(merchantQRgenerator.getMerchant_id());
		merchantQRAcctInfo.setMerchantID(merchantQRgenerator.getMerchant_id());
		cimMerchantQRcodeRequest.setMerchantAcctInformation(merchantQRAcctInfo);

		cimMerchantQRcodeRequest.setMCC(merchantQRgenerator.getMerchant_category_code().toString());
		cimMerchantQRcodeRequest.setCurrency(merchantQRgenerator.getTransaction_crncy().toString());

		if (!String.valueOf(merchantQRgenerator.getTransaction_amt()).equals("null")
				&& !String.valueOf(merchantQRgenerator.getTransaction_amt()).equals("")) {
			cimMerchantQRcodeRequest.setTrAmt(merchantQRgenerator.getTransaction_amt().toString());
		}

		if (merchantQRgenerator.getTip_or_conv_indicator() != null) {
			if (!merchantQRgenerator.getTip_or_conv_indicator().toString().equals("")) {

				if (merchantQRgenerator.getTip_or_conv_indicator().toString().equals("01")) {
					cimMerchantQRcodeRequest.setTipOrConvenienceIndicator("01");
				} else if (merchantQRgenerator.getTip_or_conv_indicator().toString().equals("02")) {
					if (merchantQRgenerator.getConv_fees_type().equals("Fixed")) {
						cimMerchantQRcodeRequest.setTipOrConvenienceIndicator("02");
						cimMerchantQRcodeRequest.setConvenienceIndicatorFee(merchantQRgenerator.getValue_conv_fees());
					} else if (merchantQRgenerator.getConv_fees_type().equals("Percentage")) {
						cimMerchantQRcodeRequest.setTipOrConvenienceIndicator("03");
						cimMerchantQRcodeRequest.setConvenienceIndicatorFee(merchantQRgenerator.getValue_conv_fees());

					}
				}

			}
		}

		cimMerchantQRcodeRequest.setCountryCode(merchantQRgenerator.getCountry().toString());
		cimMerchantQRcodeRequest.setMerchantName(merchantQRgenerator.getMerchant_name().toString());
		cimMerchantQRcodeRequest.setCity(merchantQRgenerator.getCity().toString());

		if (!String.valueOf(merchantQRgenerator.getZip_code()).equals("null")
				&& !String.valueOf(merchantQRgenerator.getZip_code()).equals("")) {
			cimMerchantQRcodeRequest.setPostalCode(merchantQRgenerator.getZip_code().toString());
		}

		CIMMerchantQRAddlInfo cimMercbantQRAddlInfo = new CIMMerchantQRAddlInfo();
		cimMercbantQRAddlInfo.setBillNumber(merchantQRgenerator.getBill_number());
		cimMercbantQRAddlInfo.setMobileNumber(merchantQRgenerator.getMobile());
		cimMercbantQRAddlInfo.setStoreLabel(merchantQRgenerator.getStore_label());
		cimMercbantQRAddlInfo.setLoyaltyNumber(merchantQRgenerator.getLoyalty_number());
		cimMercbantQRAddlInfo.setCustomerLabel(merchantQRgenerator.getCustomer_label());
		cimMercbantQRAddlInfo.setTerminalLabel(merchantQRgenerator.getTerminal_label());
		cimMercbantQRAddlInfo.setReferenceLabel(merchantQRgenerator.getReference_label());
		cimMercbantQRAddlInfo.setPurposeOfTransaction(merchantQRgenerator.getPurpose_of_tran());
		cimMercbantQRAddlInfo.setAddlDataRequest(merchantQRgenerator.getAdditional_details());

		cimMerchantQRcodeRequest.setAdditionalDataInformation(cimMercbantQRAddlInfo);

		HttpEntity<CIMMerchantQRcodeRequest> entity = new HttpEntity<>(cimMerchantQRcodeRequest, httpHeaders);
		ResponseEntity<cimMerchantQRcodeResponse> response = null;
		try {

			response = restTemplate.postForEntity(env.getProperty("ipsx.url") + "api/ws/generateMerchantQRcode", entity,
					cimMerchantQRcodeResponse.class);

			System.out.println("data-->");

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

	public static String getDeviceId() {
		String serialNumber = "";
		try {
			Process process = Runtime.getRuntime().exec(new String[] { "wmic", "bios", "get", "serialnumber" });
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				if (!line.trim().isEmpty() && !line.contains("SerialNumber")) {
					serialNumber = line.trim();
					break;
				}
			}
			reader.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return serialNumber;
	}
	
	public ResponseEntity<cimMerchantQRcodeResponse> getMerchantQrCodeStr(MerchantQRRegistration merchantQRgenerator)
			throws UnknownHostException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("P-ID", sequence.generateMerchantQRPID());
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-Channel", "BIPS");
		httpHeaders.set("Device-ID","f14a3245e5446a6f");
		//getDeviceId()
		httpHeaders.set("Merchant_ID", merchantQRgenerator.getMerchant_id());

		System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy" + merchantQRgenerator.getBill_number());
		System.out.println(
				"YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYyy" + merchantQRgenerator.getTransaction_amt());
		CIMMerchantQRcodeRequest cimMerchantQRcodeRequest = new CIMMerchantQRcodeRequest();
		System.out.println(merchantQRgenerator.getPayload_format_indicator().toString());
		cimMerchantQRcodeRequest.setPayloadFormatIndiator(merchantQRgenerator.getPayload_format_indicator().toString());
		cimMerchantQRcodeRequest.setPointOfInitiationFormat(merchantQRgenerator.getPoi_method().toString());

		CIMMerchantQRcodeAcctInfo merchantQRAcctInfo = new CIMMerchantQRcodeAcctInfo();
		merchantQRAcctInfo.setGlobalID(merchantQRgenerator.getGlobal_unique_id());
		merchantQRAcctInfo.setPayeeParticipantCode(merchantQRgenerator.getPayee_participant_code());
		merchantQRAcctInfo.setMerchantAcctNumber(merchantQRgenerator.getMerchant_id());
		merchantQRAcctInfo.setMerchantID(merchantQRgenerator.getMerchant_id());
		cimMerchantQRcodeRequest.setMerchantAcctInformation(merchantQRAcctInfo);

		cimMerchantQRcodeRequest.setMCC(merchantQRgenerator.getMerchant_category_code().toString());
		cimMerchantQRcodeRequest.setCurrency(merchantQRgenerator.getTransaction_crncy().toString());

		if (!String.valueOf(merchantQRgenerator.getTransaction_amt()).equals("null")
				&& !String.valueOf(merchantQRgenerator.getTransaction_amt()).equals("")) {
			cimMerchantQRcodeRequest.setTrAmt(merchantQRgenerator.getTransaction_amt().toString());
		}

		/*
		 * if(merchantQRgenerator.getTip_or_conv_indicator().toString().equals("0")){
		 * cimMerchantQRcodeRequest.setConvenienceIndicator(false); }else {
		 * cimMerchantQRcodeRequest.setConvenienceIndicator(true);
		 * cimMerchantQRcodeRequest.setConvenienceIndicatorFeeType(merchantQRgenerator.
		 * getConv_fees_type().toString());
		 * cimMerchantQRcodeRequest.setConvenienceIndicatorFee(merchantQRgenerator.
		 * getValue_conv_fees()); }
		 */

		if (merchantQRgenerator.getTip_or_conv_indicator() != null) {
			if (!merchantQRgenerator.getTip_or_conv_indicator().toString().equals("")) {

				if (merchantQRgenerator.getTip_or_conv_indicator().toString().equals("01")) {
					cimMerchantQRcodeRequest.setTipOrConvenienceIndicator("01");
				} else if (merchantQRgenerator.getTip_or_conv_indicator().toString().equals("02")) {
					if (merchantQRgenerator.getConv_fees_type().equals("Fixed")) {
						cimMerchantQRcodeRequest.setTipOrConvenienceIndicator("02");
						cimMerchantQRcodeRequest.setConvenienceIndicatorFee(merchantQRgenerator.getValue_conv_fees());
					} else if (merchantQRgenerator.getConv_fees_type().equals("Percentage")) {
						cimMerchantQRcodeRequest.setTipOrConvenienceIndicator("03");
						cimMerchantQRcodeRequest.setConvenienceIndicatorFee(merchantQRgenerator.getValue_conv_fees());

					}
				}

			}
		}

		cimMerchantQRcodeRequest.setCountryCode(merchantQRgenerator.getCountry().toString());
		cimMerchantQRcodeRequest.setMerchantName(merchantQRgenerator.getMerchant_name().toString());
		cimMerchantQRcodeRequest.setCity(merchantQRgenerator.getCity().toString());

		if (!String.valueOf(merchantQRgenerator.getZip_code()).equals("null")
				&& !String.valueOf(merchantQRgenerator.getZip_code()).equals("")) {
			cimMerchantQRcodeRequest.setPostalCode(merchantQRgenerator.getZip_code().toString());
		}

		CIMMerchantQRAddlInfo cimMercbantQRAddlInfo = new CIMMerchantQRAddlInfo();
		cimMercbantQRAddlInfo.setBillNumber(merchantQRgenerator.getBill_number());
		cimMercbantQRAddlInfo.setMobileNumber(merchantQRgenerator.getMobile());
		cimMercbantQRAddlInfo.setStoreLabel(merchantQRgenerator.getStore_label());
		cimMercbantQRAddlInfo.setLoyaltyNumber(merchantQRgenerator.getLoyalty_number());
		cimMercbantQRAddlInfo.setCustomerLabel(merchantQRgenerator.getCustomer_label());
		cimMercbantQRAddlInfo.setTerminalLabel(merchantQRgenerator.getTerminal_label());
		cimMercbantQRAddlInfo.setReferenceLabel(merchantQRgenerator.getReference_label());
		cimMercbantQRAddlInfo.setPurposeOfTransaction(merchantQRgenerator.getPurpose_of_tran());
		cimMercbantQRAddlInfo.setAddlDataRequest(merchantQRgenerator.getAdditional_details());

		cimMerchantQRcodeRequest.setAdditionalDataInformation(cimMercbantQRAddlInfo);

		HttpEntity<CIMMerchantQRcodeRequest> entity = new HttpEntity<>(cimMerchantQRcodeRequest, httpHeaders);
		ResponseEntity<cimMerchantQRcodeResponse> response = null;
		try {

			response = restTemplate.postForEntity(env.getProperty("ipsx.url") + "/api/ws/StaticMaucas", entity,
					cimMerchantQRcodeResponse.class);

			System.out.println("data-->");

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

	/*
	 * public ResponseEntity<MCCreditTransferResponse>
	 * getmerchantreaderinitiate(CIMMerchantDirectFndRequest Qrcode) throws
	 * UnknownHostException {
	 * System.out.println("IPSSERVICE REQUAT------------>"+Qrcode); HttpHeaders
	 * httpHeaders = new HttpHeaders();
	 * httpHeaders.setContentType(MediaType.APPLICATION_JSON);
	 * httpHeaders.set("P-ID", sequence.generateMerchantQRPID());
	 * httpHeaders.set("PSU-Device-ID",
	 * InetAddress.getLocalHost().getHostAddress());
	 * httpHeaders.set("PSU-IP-Address",
	 * InetAddress.getLocalHost().getHostAddress()); httpHeaders.set("PSU-Channel",
	 * "web"); httpHeaders.set("User-ID" ,"HI");
	 * HttpEntity<CIMMerchantDirectFndRequest> entity = new HttpEntity<>(Qrcode,
	 * httpHeaders); ResponseEntity<MCCreditTransferResponse> response = null; try {
	 * 
	 * response = restTemplate.postForEntity(env.getProperty("ipsx.url")+
	 * "/api/ws/directMerchantFndTransfer", entity, MCCreditTransferResponse.class);
	 * 
	 * System.out.println("data-->");
	 * 
	 * if (response.getStatusCode() == HttpStatus.OK) {
	 * System.out.println("data-->OK"); return new
	 * ResponseEntity<MCCreditTransferResponse>(response.getBody(), HttpStatus.OK);
	 * 
	 * } else { return response; }
	 * 
	 * }catch (HttpClientErrorException ex) {
	 * 
	 * } return response;
	 * 
	 * }
	 */
	public ResponseEntity<cimMerchantQRcodeResponse> getMerchantQrCodeStrs(MerchantQRRegistration merchantQRgenerator)
			throws UnknownHostException {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		httpHeaders.set("P-ID", sequence.generateMerchantQRPID());
		httpHeaders.set("PSU-Device-ID", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-IP-Address", InetAddress.getLocalHost().getHostAddress());
		httpHeaders.set("PSU-Channel", "BIPS");
		httpHeaders.set("Device-ID","f14a3245e5446a6f");
		//getDeviceId()
		httpHeaders.set("Merchant_ID", merchantQRgenerator.getMerchant_id());

		System.out.println("yyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyyy" + merchantQRgenerator.getBill_number());
		System.out.println(
				"YYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYYyy" + merchantQRgenerator.getTransaction_amt());
		// CIMMerchantQRcodeRequest cimMerchantQRcodeRequest=new
		// CIMMerchantQRcodeRequest();
		CimDynamicMaucasRequest cimDynamicMaucasRequest = new CimDynamicMaucasRequest();
		System.out.println(merchantQRgenerator.getPayload_format_indicator().toString());
		// cimMerchantQRcodeRequest.setAdditionalDataInformation(cimMercbantQRAddlInfo);
		cimDynamicMaucasRequest.setTran_amt(merchantQRgenerator.getTransaction_amt());
		cimDynamicMaucasRequest.setBill_num(merchantQRgenerator.getBill_number());
		System.out.println("hhhhhhhhhhhhhhhh" + cimDynamicMaucasRequest.getBill_num());

		cimDynamicMaucasRequest.setSto_label("uuu");
		cimDynamicMaucasRequest.setCust_label("hh");
		cimDynamicMaucasRequest.setRef_label("hh");
		cimDynamicMaucasRequest.setTer_label("hhh");
		cimDynamicMaucasRequest.setPur_tran("hhh");
		cimDynamicMaucasRequest.setAdd_det("hhhh");

		cimDynamicMaucasRequest.setMob_num("666");
		cimDynamicMaucasRequest.setLoy_num("hi");
		cimDynamicMaucasRequest.setMerchant_ID("MER0101");
		/*
		 * cimDynamicMaucasRequest.setMob_num(merchantQRgenerator.getMobile());
		 * cimDynamicMaucasRequest.setLoy_num(merchantQRgenerator.getLoyalty_number());
		 * 
		 * cimDynamicMaucasRequest.setSto_label(merchantQRgenerator.getStore_label());
		 * cimDynamicMaucasRequest.setCust_label(merchantQRgenerator.getCustomer_label()
		 * );
		 * cimDynamicMaucasRequest.setRef_label(merchantQRgenerator.getReference_label()
		 * );
		 * cimDynamicMaucasRequest.setTer_label(merchantQRgenerator.getTerminal_label())
		 * ;
		 * cimDynamicMaucasRequest.setPur_tran(merchantQRgenerator.getPurpose_of_tran())
		 * ;
		 * 
		 */

		HttpEntity<CimDynamicMaucasRequest> entity = new HttpEntity<>(cimDynamicMaucasRequest, httpHeaders);
		ResponseEntity<cimMerchantQRcodeResponse> response = null;
		try {

			response = restTemplate.postForEntity(env.getProperty("ipsx.url") + "/api/ws/DynamicMaucas", entity,
					cimMerchantQRcodeResponse.class);

			System.out.println("data-->");

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

}
