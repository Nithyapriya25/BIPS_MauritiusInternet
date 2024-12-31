package com.bornfire.controllers;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.bornfire.configuration.Encryption;
import com.bornfire.entity.AlertEntity;
import com.bornfire.entity.ApiResponse;
import com.bornfire.entity.BIPS_Charge_Back_Entity;
import com.bornfire.entity.BIPS_Mer_Device_Management_Entity;
import com.bornfire.entity.BIPS_Mer_User_Management_Entity;
import com.bornfire.entity.BIPS_Outward_Trans_Monitoring_Entity;
import com.bornfire.entity.BIPS_Password_Management_Entity;
import com.bornfire.entity.BIPS_Rate_Maint_Entity;
import com.bornfire.entity.BIPS_Scanned_Qr_Entity;
import com.bornfire.entity.BIPS_Service_Req_Entity;
import com.bornfire.entity.BIPS_Unit_Mangement_Entity;
import com.bornfire.entity.ChangePasswordEntity;
import com.bornfire.entity.EncryptionEntity;
import com.bornfire.entity.LoginEntity;
import com.bornfire.entity.LoginSecurity;
import com.bornfire.entity.MCCreditTransferResponse;
import com.bornfire.entity.ManualTransaction;
import com.bornfire.entity.MerchantFeesServiceCharges;
import com.bornfire.entity.MerchantMaster;
import com.bornfire.entity.NotificationEntity;
import com.bornfire.entity.Twofactorauth;
import com.bornfire.entity.UserProfile;
import com.bornfire.services.BIPSBankandBranchServices;
import com.bornfire.services.BankAndBranchMasterServices;
import com.bornfire.services.IPSAccessRoleService;
import com.bornfire.services.Passwordsendingmail;
import com.bornfire.services.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;

@Controller
@ConfigurationProperties("default")

public class MainController {

	@Autowired
	UserProfileService userProfileService;

	@Autowired
	IPSAccessRoleService AccessRoleService;

	@Autowired
	Environment env;

	@Autowired
	BankAndBranchMasterServices bankAndBranchMasterServices;

	@Autowired
	Passwordsendingmail Passwordsendingmail;

	@Autowired
	BIPSBankandBranchServices ipsServices;

	@Autowired
	private RestTemplate restTemplate;

	private String pagesize;

	public String getPagesize() {
		return pagesize;
	}

	public void setPagesize(String pagesize) {
		this.pagesize = pagesize;
	}

	/************************ RESET PASSword *********************/

	@RequestMapping("/ResetPassWord")
	public String ResetPassWord() {

		return "ResetPassWord.html";

	}

	/************************ RESET PASSword2 *********************/

	@RequestMapping("/ResetPassWord2")
	public String ResetPassWord1() {

		return "ResetPassWord2.html";

	}

	@RequestMapping("/")
	public ModelAndView firstPage() {
		return new ModelAndView("AStartpage");
	}

	@RequestMapping("")
	public ModelAndView firstPage1() {
		return new ModelAndView("AStartpage");
	}

	@RequestMapping("/logout")
	public String logout() {
		return "AStartpage.html";

	}

	/************************** IPSDashboard **************************/

	@RequestMapping("/IPSDashboard")
	public String login() {
		return "IPSDashboard.html";
	}

	@SuppressWarnings("null")
	@RequestMapping(value = "/IPSDashboard", method = { RequestMethod.GET, RequestMethod.POST })
	public String dashboard(Model md, HttpServletRequest req, String merchant_id, Date tran_date, LoginEntity LoginEntity) throws ParseException {
		
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String USERID = (String) req.getSession().getAttribute("USERID");
		String USERNAME = (String) req.getSession().getAttribute("USERNAME");
		String urli = env.getProperty("ipsx.url") + "/api/CreateAuthFlg?USERID=" + USERID;
		LoginEntity createauthflag = restTemplate.getForObject(urli, LoginEntity.class);
		if ("Y".equals(LoginEntity.getAuthentication_flg())) {
			md.addAttribute("authenticationValue", createauthflag.getAuthentication_flg());
		} else {
			md.addAttribute("authenticationValue", createauthflag.getAuthentication_flg());
		}

		md.addAttribute("USERID", USERID);
		md.addAttribute("USERNAME", USERNAME);
		int totalTranMC = 0;
		int totalTranINC = 0;
		int totalTranRTP = 0;
		int MCTran = 0;
		int INCTran = 0;
		int RTPTran = 0;

		int MCTranF = 0;
		int INCTranF = 0;
		int RTPTranF = 0;

		int MCTranIP = 0;
		int INCTranIP = 0;
		int RTPTranIP = 0;

		int MCTranR = 0;
		int INCTranR = 0;
		int RTPTranR = 0;

		int CountZero = 1;
		int CountSuccess = 0; // bIPS_OutwardTransMonitoring_Repo.getTranssuccess(merUserId);
		int CountFailure = 0; // bIPS_OutwardTransMonitoring_Repo.getTransfailure(merUserId);
		int CountInitiated = 0; // bIPS_OutwardTransMonitoring_Repo.getTransinitiated(merUserId);
		int CountUsers = 0; // bIPS_MerUserManagement_Repo.getUserscount(merUserId);
		int CountDevice = 0; // bIPS_MerDeviceManagement_Repo.getDevicecount(merUserId);
		int TotalSum = CountSuccess + CountFailure + CountInitiated;
		md.addAttribute("countsuccess", CountSuccess);
		md.addAttribute("countfailure", CountFailure);
		md.addAttribute("countinitiated", CountInitiated);
		md.addAttribute("totalsum", TotalSum);
		md.addAttribute("countusers", CountUsers);
		md.addAttribute("countdevice", CountDevice);
		md.addAttribute("countzero", CountZero);
		md.addAttribute("counttrans", null);// bIPS_OutwardTransMonitoring_Repo.getTransactionCountMer(merUserId)
		md.addAttribute("countDailytrans", null); // bIPS_OutwardTransMonitoring_Repo.getDailyTransMer(merUserId));

		md.addAttribute("totalTranMC", totalTranMC);
		md.addAttribute("totalTranINC", totalTranINC);
		md.addAttribute("totalTranRTP", totalTranRTP);
		md.addAttribute("MCTran", MCTran);
		md.addAttribute("INCTran", INCTran);
		md.addAttribute("RTPTran", RTPTran);
		md.addAttribute("MCTranF", MCTranF);
		md.addAttribute("INCTranF", INCTranF);
		md.addAttribute("RTPTranF", RTPTranF);
		md.addAttribute("MCTranIP", MCTranIP);
		md.addAttribute("INCTranIP", INCTranIP);
		md.addAttribute("RTPTranIP", RTPTranIP);
		md.addAttribute("MCTranR", MCTranR);
		md.addAttribute("INCTranR", INCTranR);
		md.addAttribute("RTPTranR", RTPTranR);

		md.addAttribute("menu", "Dashboard");
		return "IPSDashboard";
	}

	@RequestMapping(value = "/submitSecurityQuestions", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public Map<String, String> submitSecurityQuestions(Model md, HttpServletRequest req, @ModelAttribute Twofactorauth up) throws ParseException {
		
		Map<String, String> returnResponse = new HashMap<>();
		try {
			String urlforGetMerchantDatas = env.getProperty("ipsx.url") + "api/bipsUserForAuthentication?" + "userid=" + up.getUser_id();
			BIPS_Password_Management_Entity createauthflag = restTemplate.getForObject(urlforGetMerchantDatas, BIPS_Password_Management_Entity.class);
			up.setPassword_hash(createauthflag.getPassword());
			up.setPhone_number(createauthflag.getMobile_no().toString());
			up.setEmail(createauthflag.getEmail_address());
			up.setTwo_fa_enabled("Y");
			String url = env.getProperty("ipsx.url") + "api/AuthenticationForRep";
			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);
			EncryptionEntity o = new EncryptionEntity();
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(up);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, encryptedRequestEntity, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				returnResponse.put("authenticate_flg", "Y");
				return returnResponse;
			} else {
				returnResponse.put("authenticate_flg", "N");
				return returnResponse;
			}
		} catch (Exception e) {
			e.printStackTrace();
			returnResponse.put("error", "Merchant not found");
			return returnResponse;
		}
	}

	@RequestMapping(value = "/changePasswordwithoutoldpassword", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String changePasswordwithoutoldpassword(Model md, HttpServletRequest req, @RequestParam(required = false) String userid,
			@RequestParam(required = false) String newpass) throws ParseException {
		System.out.println("USERID " + userid);
		System.out.println("NEWPASS " + newpass);

		try {
			String urli = env.getProperty("ipsx.url") + "/api/CreateAuthFlg?USERID=" + userid;
			LoginEntity createauthflag = restTemplate.getForObject(urli, LoginEntity.class);
			createauthflag.setPassword(newpass);
			String url = env.getProperty("ipsx.url") + "api/ResetMerchantPassword";
			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);
			EncryptionEntity o = new EncryptionEntity();
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(createauthflag);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);
			RestTemplate restTemplateforResponse = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			ResponseEntity<String> response = restTemplateforResponse.exchange(url, HttpMethod.POST, encryptedRequestEntity, String.class);
			// Handle the response from Application A
			if (response.getStatusCode().is2xxSuccessful()) {
				return "Success";
			} else {
				return "Failure";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "User not Found";
		}
	}

	@RequestMapping(value = "/submitTwofactorCheck", method = { RequestMethod.POST })
	@ResponseBody
	public String submitTwofactorCheck(@RequestParam(required = false) String userid, @RequestParam(required = false) String securityValue,
			@RequestParam(required = false) String securityAnswer, Model md) {
		try {
			String url = env.getProperty("ipsx.url") + "api/CheckTwoFactorAnswer?userId=" + userid + "&answerNumber=" + securityValue + "&answer="
					+ securityAnswer;
			Boolean response = restTemplate.getForObject(url, Boolean.class);
			return response.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return "Failure";
		}
	}

	/************************ Merchant Master *********************/

	@RequestMapping(value = "merchantReg2")
	public String MerchantMaster(@RequestParam(required = false) String formmode, @RequestParam(required = false) String merchant_acct_no,
			@RequestParam(required = false) String userid, @RequestParam(required = false) Optional<Integer> page,
			@RequestParam(required = false) String mlid, @RequestParam(required = false) String mpcn, @RequestParam(required = false) String mn,
			@RequestParam(required = false) String mer_id, @RequestParam(required = false) String device_id,
			@RequestParam(required = false) String user_id, @RequestParam(required = false) String merchant_name,
			@RequestParam(required = false) String merchant_user_id, @RequestParam(required = false) String merchant_nam,
			@RequestParam(required = false) String merchant_id, @RequestParam(required = false) String merchant_id1,
			@RequestParam(value = "size", required = false) Optional<Integer> size, @ModelAttribute MerchantMaster bankAgentTable, Model md,
			HttpServletRequest req, @RequestParam(required = false) String acctcode, @RequestParam(value = "tranDate", required = false) String date,
			String Id, String mer_user_id_r1, String merchant_rep_id, String unit_id)
			throws FileNotFoundException, SQLException, IOException, ParseException {

		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String unitidacess = (String) req.getSession().getAttribute("unitidacess");
		// md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String BUSER = (String) req.getSession().getAttribute("BUSER");
		String acces = (String) req.getSession().getAttribute("acces");
		md.addAttribute("LoginUserId", userID);
		md.addAttribute("bank", BUSER);
		int count = this.count;
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PowerLog", acces);
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("formmode", "list");
			String url = env.getProperty("ipsx.url") + "/api/Merchantmasterdetail?merchant_user_id=" + merUserId;
			List<MerchantMaster> res = new ArrayList<>();
			ResponseEntity<List<MerchantMaster>> response = restTemplate.exchange(url, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<MerchantMaster>>() {
					});
			res.addAll(response.getBody());
			md.addAttribute("bankDetail", response.getBody());
		} else if (formmode.equals("view")) {
			md.addAttribute("formmode", formmode);
			md.addAttribute("MerchantIdUse", merchant_acct_no);
			md.addAttribute("MerchantNaUse", merchant_nam);

			// Merchant Master View
			String urli = env.getProperty("ipsx.url") + "/api/Merchantmasterviewdetail?merchant_user_id=" + merUserId;
			MerchantMaster merchantResponse = restTemplate.getForObject(urli, MerchantMaster.class);
			md.addAttribute("branchDet", merchantResponse);

			// Fees Details
			String urlf = env.getProperty("ipsx.url") + "/api/MerchantmasterviewFeedetail?merchant_user_id=" + merchant_acct_no;
			ResponseEntity<List<MerchantFeesServiceCharges>> response = restTemplate.exchange(urlf, HttpMethod.GET, null,
					new ParameterizedTypeReference<List<MerchantFeesServiceCharges>>() {
					});
			md.addAttribute("merchantFeeDetails", response.getBody());

			// Unit Details
			String url = env.getProperty("ipsx.url") + "/api/MerchantUnit?" + "merchant_acct_no=" + merchant_acct_no + "&unitidacess=" + unitidacess
					+ "&merchant_user_id=" + merchant_user_id;
			BIPS_Unit_Mangement_Entity[] responseEntity = restTemplate.getForObject(url, BIPS_Unit_Mangement_Entity[].class);
			if (responseEntity != null && responseEntity.length > 0) {
				md.addAttribute("MerchantUnit", responseEntity);
				md.addAttribute("MerchantUnitCount", responseEntity.length);
			} else {
				System.out.println("Empty or null response received from the API.");
			}

			// User Details
			String url1 = env.getProperty("ipsx.url") + "/api/Merchantuser?" + "merchant_acct_no=" + merchant_acct_no + "&unitidacess=" + unitidacess
					+ "&merchant_user_id=" + merchant_user_id;
			BIPS_Mer_User_Management_Entity[] responseEntity1 = restTemplate.getForObject(url1, BIPS_Mer_User_Management_Entity[].class);
			if (responseEntity1 != null && responseEntity1.length > 0) {
				md.addAttribute("pro", responseEntity1);
				md.addAttribute("proCount", responseEntity1.length);
			} else {
				System.out.println("Empty or null response received from the API.");
			}

			// Device Details
			String url2 = env.getProperty("ipsx.url") + "/api/Merchantdevice?" + "merchant_acct_no=" + merchant_acct_no + "&unitidacess="
					+ unitidacess + "&merchant_user_id=" + merchant_user_id;
			BIPS_Mer_Device_Management_Entity[] responseEntity2 = restTemplate.getForObject(url2, BIPS_Mer_Device_Management_Entity[].class);
			if (responseEntity2 != null && responseEntity2.length > 0) {
				md.addAttribute("MerchantDevi", responseEntity2);
				md.addAttribute("MerchantDeviCount", responseEntity2.length);
			} else {
				System.out.println("Empty or null response received from the API.");
			}

			// Password Details
			String url3 = env.getProperty("ipsx.url") + "/api/Merchantpassword?" + "merchant_acct_no=" + merchant_acct_no + "&unitidacess="
					+ unitidacess + "&merchant_user_id=" + merchant_user_id;
			BIPS_Password_Management_Entity[] responseEntity3 = restTemplate.getForObject(url3, BIPS_Password_Management_Entity[].class);
			if (responseEntity3 != null && responseEntity3.length > 0) {
				md.addAttribute("propass", responseEntity3);
			} else {
				System.out.println("Empty or null response received from the API.");
			}

		} else if (formmode.equals("viewnew")) {
			md.addAttribute("formmode", formmode);
			String urli = env.getProperty("ipsx.url") + "/api/Merchantmasterviewdetail?merchant_user_id=" + merUserId;
			MerchantMaster response = restTemplate.getForObject(urli, MerchantMaster.class);
			md.addAttribute("branchDet", response);
			md.addAttribute("MerchantIdUse", merchant_acct_no);
			md.addAttribute("MerchantNaUse", merchant_nam);
			String url = env.getProperty("ipsx.url") + "/api/MerchantUnit?" + "merchant_acct_no=" + merchant_acct_no + "&unitidacess=" + unitidacess
					+ "&merchant_user_id=" + merchant_user_id;
			BIPS_Unit_Mangement_Entity[] responseEntity = restTemplate.getForObject(url, BIPS_Unit_Mangement_Entity[].class);
			if (responseEntity != null && responseEntity.length > 0) {
				md.addAttribute("MerchantUnit", responseEntity);
			} else {
				System.out.println("Empty or null response received from the API.");
			}
			String url1 = env.getProperty("ipsx.url") + "/api/Merchantuser?" + "merchant_acct_no=" + merchant_acct_no + "&unitidacess=" + unitidacess
					+ "&merchant_user_id=" + merchant_user_id;
			BIPS_Mer_User_Management_Entity[] responseEntity1 = restTemplate.getForObject(url1, BIPS_Mer_User_Management_Entity[].class);
			if (responseEntity1 != null && responseEntity1.length > 0) {
				md.addAttribute("pro", responseEntity1);
			} else {
				System.out.println("Empty or null response received from the API.");
			}
			String url2 = env.getProperty("ipsx.url") + "/api/Merchantdevice?" + "merchant_acct_no=" + merchant_acct_no + "&unitidacess="
					+ unitidacess + "&merchant_user_id=" + merchant_user_id;
			BIPS_Mer_Device_Management_Entity[] responseEntity2 = restTemplate.getForObject(url2, BIPS_Mer_Device_Management_Entity[].class);
			if (responseEntity2 != null && responseEntity2.length > 0) {
				md.addAttribute("MerchantDevi", responseEntity2);
			} else {
				System.out.println("Empty or null response received from the API.");
			}
			String url3 = env.getProperty("ipsx.url") + "/api/Merchantpassword?" + "merchant_acct_no=" + merchant_acct_no + "&unitidacess="
					+ unitidacess + "&merchant_user_id=" + merchant_user_id;
			BIPS_Password_Management_Entity[] responseEntity3 = restTemplate.getForObject(url3, BIPS_Password_Management_Entity[].class);
			if (responseEntity3 != null && responseEntity3.length > 0) {
				md.addAttribute("propass", responseEntity3);
			} else {
				System.out.println("Empty or null response received from the API.");
			}
		} else if (formmode.equals("MerMastadd")) {
			md.addAttribute("formmode", "MerMastadd");
			md.addAttribute("MerchantlegId", merchant_acct_no);
			md.addAttribute("MerchantNa", merchant_nam);
			md.addAttribute("count", count);
		} else if (formmode.equals("MerMastvi")) {
			md.addAttribute("formmode", "MerMastvi");
			md.addAttribute("user_id", userID);
			String urli = env.getProperty("ipsx.url") + "/api/uereditdetail?user_id=" + user_id;
			BIPS_Mer_User_Management_Entity response = restTemplate.getForObject(urli, BIPS_Mer_User_Management_Entity.class);
			md.addAttribute("Meruserlist", response);
		} else if (formmode.equals("MerMastvie")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "MerMastvie");
			String urli = env.getProperty("ipsx.url") + "/api/deviceviewdetailWeb?device_id=" + device_id;
			BIPS_Mer_Device_Management_Entity response = restTemplate.getForObject(urli, BIPS_Mer_Device_Management_Entity.class);
			md.addAttribute("Merdevicelist", response);
		} else if (formmode.equals("UsermAdd")) {
			md.addAttribute("formmode", "UsermAdd");
			String urlref = env.getProperty("ipsx.url") + "/api/referenceMasterForDropdown?ref_type=MM12";
			String[] responseArray = restTemplate.getForObject(urlref, String[].class);
			List<String> responseref = Arrays.asList(responseArray);
			md.addAttribute("MakerorChecker", responseref);
			String urlrefstatus = env.getProperty("ipsx.url") + "/api/referenceMasterForDropdown?ref_type=MM13";
			String[] responseArrayuser = restTemplate.getForObject(urlrefstatus, String[].class);
			List<String> responserefuser = Arrays.asList(responseArrayuser);
			md.addAttribute("UserStatus", responserefuser);
			String urlrefcode = env.getProperty("ipsx.url") + "/api/referenceMasterForDropdown?ref_type=CC01";
			String[] responseArraycode = restTemplate.getForObject(urlrefcode, String[].class);
			List<String> responserefcode = Arrays.asList(responseArraycode);
			md.addAttribute("CountryCode", responserefcode);
			String urlrefunit = env.getProperty("ipsx.url") + "/api/unitidDetails?merchant_id=" + merchant_id;
			String[] responseArrayunit = restTemplate.getForObject(urlrefunit, String[].class);
			List<String> responserefunit = Arrays.asList(responseArrayunit);
			md.addAttribute("MerchantUnitId", responserefunit);
			String urlrefdefaultdevice = env.getProperty("ipsx.url") + "/api/defaultDeviceid?merchant_id=" + merchant_id;
			String[] responseArraydefaultdevice = restTemplate.getForObject(urlrefdefaultdevice, String[].class);
			List<String> responserefdefaultdevice = Arrays.asList(responseArraydefaultdevice);
			md.addAttribute("MerdeviceId", responserefdefaultdevice);
			String useridurl = env.getProperty("ipsx.url") + "/api/uniqueUserId?merchant_id=" + merchant_id;
			String uniquserid = restTemplate.getForObject(useridurl, String.class);
			md.addAttribute("uniquserid", uniquserid);
			String urli = env.getProperty("ipsx.url") + "/api/devicemanagementidlist?merchant_id=" + merchant_id;
			try {
				BIPS_Mer_Device_Management_Entity[] response = restTemplate.getForObject(urli, BIPS_Mer_Device_Management_Entity[].class);
				System.out.println("Response: " + Arrays.toString(response));
				List<String> deviceInfoList = new ArrayList<>();
				for (BIPS_Mer_Device_Management_Entity entity : response) {
					String deviceInfo = entity.getDevice_id();
					deviceInfoList.add(deviceInfo);
				}
				md.addAttribute("deviceInfoList", deviceInfoList);
				System.out.println("Device info list added to model: " + deviceInfoList);
			} catch (HttpClientErrorException | HttpServerErrorException e) {
				System.err.println("HTTP error occurred: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
			} catch (Exception e) {
				System.err.println("An error occurred: " + e.getMessage());
			}
			md.addAttribute("MerchantIdUse", merchant_id);
			md.addAttribute("MerchantNaUse", merchant_nam);
		} else if (formmode.equals("UsermEdit")) {
			String urlrefcode = env.getProperty("ipsx.url") + "/api/referenceMasterForDropdown?ref_type=CC01";
			String[] responseArraycode = restTemplate.getForObject(urlrefcode, String[].class);
			List<String> responserefcode = Arrays.asList(responseArraycode);
			md.addAttribute("CountryCode", responserefcode);
			md.addAttribute("formmode", "UsermEdit");
			String urli = env.getProperty("ipsx.url") + "/api/uereditdetail?user_id=" + user_id;
			BIPS_Mer_User_Management_Entity response = restTemplate.getForObject(urli, BIPS_Mer_User_Management_Entity.class);
			md.addAttribute("Meruserlist", response);
		} else if (formmode.equals("UsermVerify")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "UsermVerify");
			String urli = env.getProperty("ipsx.url") + "/api/uerviewdetail?user_id=" + user_id;
			BIPS_Mer_User_Management_Entity response = restTemplate.getForObject(urli, BIPS_Mer_User_Management_Entity.class);
			md.addAttribute("Meruserlist", response);
		} else if (formmode.equals("DevicemAdd")) {
			md.addAttribute("formmode", "DevicemAdd");
			String deviceidurl = env.getProperty("ipsx.url") + "/api/uniqueDeviceId?merchant_id=" + merchant_id;
			String uniqdeviceid = restTemplate.getForObject(deviceidurl, String.class);
			md.addAttribute("uniqdeviceid", uniqdeviceid);
			String urlrefstatus = env.getProperty("ipsx.url") + "/api/referenceMasterForDropdown?ref_type=MM14";
			String[] responseArrayuser = restTemplate.getForObject(urlrefstatus, String[].class);
			List<String> responserefuser = Arrays.asList(responseArrayuser);
			md.addAttribute("DeviceStatus", responserefuser);
			String urlrefunit = env.getProperty("ipsx.url") + "/api/unitidDetails?merchant_id=" + merchant_id;
			String[] responseArrayunit = restTemplate.getForObject(urlrefunit, String[].class);
			List<String> responserefunit = Arrays.asList(responseArrayunit);
			md.addAttribute("MerchantUnitId", responserefunit);
			String urlrefdefaultuser = env.getProperty("ipsx.url") + "/api/defaultUserid?merchant_id=" + merchant_id;
			String[] responseArraydefaultuser = restTemplate.getForObject(urlrefdefaultuser, String[].class);
			List<String> responserefdefaultuser = Arrays.asList(responseArraydefaultuser);
			md.addAttribute("Meruserid", responserefdefaultuser);
			md.addAttribute("user_id", userID);
			md.addAttribute("MerchantIdDev", merchant_id);
			md.addAttribute("MerchantNaDev", merchant_nam);
		} else if (formmode.equals("DevicemEdit")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "DevicemEdit");
			String urli = env.getProperty("ipsx.url") + "/api/deviceeditdetailWeb?device_id=" + device_id;
			BIPS_Mer_Device_Management_Entity response = restTemplate.getForObject(urli, BIPS_Mer_Device_Management_Entity.class);
			md.addAttribute("Merdevicelist", response);
		} else if (formmode.equals("DevicemVerify")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "DevicemVerify");
			String urli = env.getProperty("ipsx.url") + "/api/devicverifydetailWeb?device_id=" + device_id;
			BIPS_Mer_Device_Management_Entity response = restTemplate.getForObject(urli, BIPS_Mer_Device_Management_Entity.class);
			md.addAttribute("Merdevicelist", response);
		} else if (formmode.equals("PassView")) {
			md.addAttribute("formmode", "PassView");
			String urli = env.getProperty("ipsx.url") + "/api/passwordviewdetail?merchant_rep_id=" + merchant_rep_id;
			BIPS_Password_Management_Entity response = restTemplate.getForObject(urli, BIPS_Password_Management_Entity.class);
			md.addAttribute("MerchantPass", response);
		} else if (formmode.equals("PassEdit")) {
			String urlrefcode = env.getProperty("ipsx.url") + "/api/referenceMasterForDropdown?ref_type=CC01";
			String[] responseArraycode = restTemplate.getForObject(urlrefcode, String[].class);
			List<String> responserefcode = Arrays.asList(responseArraycode);
			md.addAttribute("CountryCode", responserefcode);
			md.addAttribute("formmode", "PassEdit");
			String urli = env.getProperty("ipsx.url") + "/api/passwordeditdetail?merchant_rep_id=" + merchant_rep_id;
			BIPS_Password_Management_Entity response = restTemplate.getForObject(urli, BIPS_Password_Management_Entity.class);
			md.addAttribute("MerchantPass", response);
		} else if (formmode.equals("PassVerify")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "PassVerify");
			String urli = env.getProperty("ipsx.url") + "/api/passwordverifydetail?merchant_rep_id=" + merchant_rep_id;
			BIPS_Password_Management_Entity response = restTemplate.getForObject(urli, BIPS_Password_Management_Entity.class);
			md.addAttribute("MerchantPass", response);
		} else if (formmode.equals("UnitView")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "UnitView");
			md.addAttribute("user_id", userID);
			String urli = env.getProperty("ipsx.url") + "/api/unitviewdetail?unit_id=" + unit_id;
			BIPS_Unit_Mangement_Entity response = restTemplate.getForObject(urli, BIPS_Unit_Mangement_Entity.class);
			md.addAttribute("MerUnit", response);
		} else if (formmode.equals("UnitEdit")) {
			String urlrefcode = env.getProperty("ipsx.url") + "/api/referenceMasterForDropdown?ref_type=CC01";
			String[] responseArraycode = restTemplate.getForObject(urlrefcode, String[].class);
			List<String> responserefcode = Arrays.asList(responseArraycode);
			md.addAttribute("CountryCode", responserefcode);
			String urlrefstatus = env.getProperty("ipsx.url") + "/api/referenceMasterForDropdown?ref_type=MM11";
			String[] responseArrayuser = restTemplate.getForObject(urlrefstatus, String[].class);
			List<String> responserefuser = Arrays.asList(responseArrayuser);
			md.addAttribute("UnitType", responserefuser);
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "UnitEdit");
			md.addAttribute("user_id", userID);
			String urli = env.getProperty("ipsx.url") + "/api/uniteditdetail?unit_id=" + unit_id;
			BIPS_Unit_Mangement_Entity response = restTemplate.getForObject(urli, BIPS_Unit_Mangement_Entity.class);
			md.addAttribute("MerUnit", response);
		} else if (formmode.equals("UnitmAdd")) {
			String urlrefcode = env.getProperty("ipsx.url") + "/api/referenceMasterForDropdown?ref_type=CC01";
			String[] responseArraycode = restTemplate.getForObject(urlrefcode, String[].class);
			List<String> responserefcode = Arrays.asList(responseArraycode);
			md.addAttribute("CountryCode", responserefcode);
			String uniqunitid = env.getProperty("ipsx.url") + "/api/uniqueUnitId?merchant_id=" + merUserId;
			String response = restTemplate.getForObject(uniqunitid, String.class);
			md.addAttribute("uniqunitid", response);
			String urlrefstatus = env.getProperty("ipsx.url") + "/api/referenceMasterForDropdown?ref_type=MM11";
			String[] responseArrayuser = restTemplate.getForObject(urlrefstatus, String[].class);
			List<String> responserefuser = Arrays.asList(responseArrayuser);
			md.addAttribute("UnitType", responserefuser);
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "UnitmAdd");
			md.addAttribute("MerchantIdDev", merchant_id);
			md.addAttribute("MerchantNaDev", merchant_nam);
		} else if (formmode.equals("UnitVe" + "rify")) {
			md.addAttribute("user_id", userID);
			md.addAttribute("formmode", "UnitVerify");
			md.addAttribute("user_id", userID);
			String urli = env.getProperty("ipsx.url") + "/api/unitverifydetail?unit_id=" + unit_id;
			BIPS_Unit_Mangement_Entity response = restTemplate.getForObject(urli, BIPS_Unit_Mangement_Entity.class);
			md.addAttribute("MerUnit", response);
		}
		return "IPSMerchantMaster";
	}

	@GetMapping("getUnitDetailsMM")
	@ResponseBody
	public Object[] getUnitDetails(@RequestParam(required = false) String unitId) throws IOException, SQLException {
		System.out.println("Unit Id" + unitId);
		String unitidfetch = env.getProperty("ipsx.url") + "/api/getUnitDetails?unitId=" + unitId;
		Object[] responseArrayfetch = restTemplate.getForObject(unitidfetch, Object[].class);

		if (responseArrayfetch != null) {
			System.out.println(Arrays.toString(responseArrayfetch));
		} else {
			System.out.println("No data found");
		}

		return responseArrayfetch;
	}

	/************************ Merchant Operation *********************/

	int count = 0;
	String scan_val;

	@RequestMapping(value = "merchantOperation")
	public String merchantOperation(@RequestParam(required = false) String formmode, @RequestParam(required = false) String deviceid,
			@RequestParam(required = false) String userid, @RequestParam(required = false) String PayloadFormatIndiatorValue,
			@RequestParam(required = false) String message_ref, @RequestParam(required = false) String message_id,
			@RequestParam(required = false) String srl, @RequestParam(required = false) String device_id,
			@RequestParam(required = false) Optional<Integer> page, @RequestParam(value = "size", required = false) Optional<Integer> size,
			@RequestParam(required = false) String merchant_id, @RequestParam(required = false) String stringunit,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date from_date,
			@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date to_date, @RequestParam(required = false) String unit_id,
			Model md, HttpServletRequest req, @RequestParam(required = false) String user_id,
			@ModelAttribute BIPS_Scanned_Qr_Entity bIPS_Scanned_Qr_Entity, String merchant_user_id, String staticQRID, Object MER_USER_NAME,
			char[] StringUnit) throws SQLException {

		String userID = (String) req.getSession().getAttribute("USERID");
		System.out.println(userID);
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String UNITID = (String) req.getSession().getAttribute("UNITID");
		String UNITNAME = (String) req.getSession().getAttribute("UNITNAME");
		String acces = (String) req.getSession().getAttribute("acces");
		String BUSER = (String) req.getSession().getAttribute("BUSER");
		md.addAttribute("acces", acces);
		md.addAttribute("bank", BUSER);
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		// String scan_val = this.scan_val;

		if (formmode == null || formmode.equals("add")) {
			md.addAttribute("formmode", "add");
		} else if (formmode.equals("Admin")) {
			md.addAttribute("formmode", "Admin");
		}

		/* User Management */
		else if (formmode.equals("UserManClick")) {
			md.addAttribute("formmode", "UserManClick");
			String url = env.getProperty("ipsx.url") + "/api/UserManagementList?merchant_user_id=" + merUserId;
			BIPS_Mer_User_Management_Entity[] response = restTemplate.getForObject(url, BIPS_Mer_User_Management_Entity[].class);
			md.addAttribute("pro", response);
		} else if (formmode.equals("viewuser")) {
			md.addAttribute("formmode", "viewuser");
			String url = env.getProperty("ipsx.url") + "/api/UserManagementView?merchant_user_id=" + user_id;
			BIPS_Mer_User_Management_Entity response = restTemplate.getForObject(url, BIPS_Mer_User_Management_Entity.class);
			md.addAttribute("MerchantView", response);
		} else if (formmode.equals("edituser")) {
			md.addAttribute("formmode", "edituser");
			String url = env.getProperty("ipsx.url") + "/api/UserManagementView?merchant_user_id=" + user_id;
			BIPS_Mer_User_Management_Entity response = restTemplate.getForObject(url, BIPS_Mer_User_Management_Entity.class);
			md.addAttribute("MerchantView", response);
		}

		else if (formmode.equals("view")) {
			md.addAttribute("formmode", "view");
		} else if (formmode.equals("ScanPay")) {
			md.addAttribute("formmode", "ScanPay");
		} else if (formmode.equals("StaticQR")) {
			md.addAttribute("formmode", "StaticQR");
		} else if (formmode.equals("TransDetails")) {
			md.addAttribute("formmode", "TransDetails");
			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			Date date = new Date();
			String valueDateRef = dateFormatWithMonthName.format(date).toUpperCase();
			String url = env.getProperty("ipsx.url") + "/api/CustomerTransactionListSingleDate?merchant_id=" + merUserId + "&Date=" + valueDateRef
					+ "&unit_id=" + UNITID;
			BIPS_Outward_Trans_Monitoring_Entity[] responseArray = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity[].class);
			md.addAttribute("Trans", responseArray);
		} else if (formmode.equals("DynamicQR")) {
			md.addAttribute("formmode", "DynamicQR");
		} /* Device Management */
		else if (formmode.equals("DeviceManClick")) {
			md.addAttribute("formmode", "DeviceManClick");

			String url = env.getProperty("ipsx.url") + "/api/DeviceManagementList?merchant_user_id=" + merUserId;
			BIPS_Mer_Device_Management_Entity[] response = restTemplate.getForObject(url, BIPS_Mer_Device_Management_Entity[].class);
			md.addAttribute("Man", response);
			// md.addAttribute("Man",
			// bIPS_MerDeviceManagement_Repo.getaddDevice(merUserId));
		} else if (formmode.equals("viewdevice")) {
			md.addAttribute("formmode", "viewdevice");
			String url = env.getProperty("ipsx.url") + "/api/AdminViewDevice?device_id=" + device_id;
			BIPS_Mer_Device_Management_Entity response = restTemplate.getForObject(url, BIPS_Mer_Device_Management_Entity.class);
			md.addAttribute("DeviceManage", response); // bIPS_MerDeviceManagement_Repo.getdevice(device_id));
		} else if (formmode.equals("editdevice")) {
			md.addAttribute("formmode", "editdevice");
			String url = env.getProperty("ipsx.url") + "/api/AdminViewDevice?device_id=" + device_id;
			BIPS_Mer_Device_Management_Entity response = restTemplate.getForObject(url, BIPS_Mer_Device_Management_Entity.class);
			md.addAttribute("DeviceManage", response); // bIPS_MerDeviceManagement_Repo.getdevice(device_id));
		} /* Rate Maintance */
		else if (formmode.equals("RateClick")) {
			md.addAttribute("formmode", "RateClick");

			String url = env.getProperty("ipsx.url") + "/api/RateMaintenanceList";
			BIPS_Rate_Maint_Entity[] response = restTemplate.getForObject(url, BIPS_Rate_Maint_Entity[].class);
			md.addAttribute("Rate", response);
			// md.addAttribute("Rate", bIPS_RateMaint_Repo.getlst());
		} else if (formmode.equals("addRate")) {
			md.addAttribute("formmode", "addRate");
		} else if (formmode.equals("viewRate")) {
			md.addAttribute("formmode", "viewRate");
			String url = env.getProperty("ipsx.url") + "/api/UserManagementViewRate?srl=" + srl;
			BIPS_Rate_Maint_Entity response = restTemplate.getForObject(url, BIPS_Rate_Maint_Entity.class);
			md.addAttribute("Rate", response); // bIPS_RateMaint_Repo.getDevlst(srl));
		} else if (formmode.equals("editRate")) {
			md.addAttribute("formmode", "editRate");
			String url = env.getProperty("ipsx.url") + "/api/UserManagementViewRate?srl=" + srl;
			BIPS_Rate_Maint_Entity response = restTemplate.getForObject(url, BIPS_Rate_Maint_Entity.class);
			md.addAttribute("Rate", response); // bIPS_RateMaint_Repo.getDevlst(srl));
		} /* Customer transcation */
		else if (formmode.equals("CustClick")) {
			md.addAttribute("formmode", "CustClick");
			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			Date date = new Date();
			String valueDateRef = dateFormatWithMonthName.format(date).toUpperCase();
			String url = env.getProperty("ipsx.url") + "/api/CustomerTransactionListSingleDate?merchant_id=" + merUserId + "&Date=" + valueDateRef
					+ "&unit_id=" + UNITID;
			BIPS_Outward_Trans_Monitoring_Entity[] responseArray = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity[].class);
			md.addAttribute("click", responseArray);
			// md.addAttribute("click",
			// bIPS_OutwardTransMonitoring_Repo.getTranlst(merUserId));
		} /* Fees and Charges */
		else if (formmode.equals("FeesClick")) {
			md.addAttribute("formmode", "FeesClick");
			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			Date date = new Date();
			String valueDateRef = dateFormatWithMonthName.format(date).toUpperCase();
			String url = env.getProperty("ipsx.url") + "/api/FeesAndChargesListSingleDate?merchant_id=" + merUserId + "&Date=" + valueDateRef
					+ "&unit_id=" + UNITID;
			BIPS_Outward_Trans_Monitoring_Entity[] responseArray = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity[].class);
			md.addAttribute("fees", responseArray);
			// md.addAttribute("fees",
			// bIPS_OutwardTransMonitoring_Repo.getTranlst(merUserId));
		} else if (formmode.equals("viewfees")) {
			md.addAttribute("formmode", "viewfees");
			String url = env.getProperty("ipsx.url") + "/api/FeesAndChargeForOne?message_ref=" + message_ref;
			BIPS_Outward_Trans_Monitoring_Entity response = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity.class);
			md.addAttribute("FeesCharge", response); // bIPS_OutwardTransMonitoring_Repo.getTranfees(message_ref));
		} else if (formmode.equals("editfees")) {
			md.addAttribute("formmode", "editfees");
			md.addAttribute("FeesCharge", null); // bIPS_OutwardTransMonitoring_Repo.getTranfees(message_ref));
		} /* Charge back */
		else if (formmode.equals("ChargeClick")) {
			md.addAttribute("formmode", "ChargeClick");
			if (acces.equals("UNIT")) {
				String url = env.getProperty("ipsx.url") + "/api/UnitChargeBackList?merchant_id=" + merUserId + "&unit_id=" + UNITID;
				BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
				md.addAttribute("Back", response);
			} else {
				String url = env.getProperty("ipsx.url") + "/api/ChargeBackList?merchant_id=" + merUserId;
				BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
				md.addAttribute("Back", response);
			}
		} else if (formmode.equals("addCharge")) {
			md.addAttribute("formmode", "addCharge");
			String url = env.getProperty("ipsx.url") + "/api/ChargebackForOne?message_ref=" + message_ref;
			BIPS_Outward_Trans_Monitoring_Entity response = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity.class);
			md.addAttribute("viewcharge", response); // bIPS_OutwardTransMonitoring_Repo.getTranfees(message_ref));
		} else if (formmode.equals("customerView")) {
			md.addAttribute("formmode", "customerView");
			String url = env.getProperty("ipsx.url") + "/api/CustomerTransactionView?message_ref=" + message_ref;
			BIPS_Outward_Trans_Monitoring_Entity response = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity.class);
			md.addAttribute("viewcustomer", response); // bIPS_OutwardTransMonitoring_Repo.getTranfees(message_ref));
		} /* Service request */
		else if (formmode.equals("ServClick")) {
			md.addAttribute("formmode", "ServClick");
			if (acces.equals("UNIT")) {
				String url = env.getProperty("ipsx.url") + "/api/ServiceRequestUnitList?merchant_id=" + merUserId + "&unit_id=" + UNITID;
				BIPS_Service_Req_Entity[] response = restTemplate.getForObject(url, BIPS_Service_Req_Entity[].class);
				System.out.println(response);
				md.addAttribute("Service", response);
			} else {
				String url = env.getProperty("ipsx.url") + "/api/ServiceRequestAllList?merchant_id=" + merUserId;
				BIPS_Service_Req_Entity[] response = restTemplate.getForObject(url, BIPS_Service_Req_Entity[].class);
				md.addAttribute("Service", response);
			}
			// md.addAttribute("Service", bIPS_ServiceReq_Repo.getlst());
		} else if (formmode.equals("addService")) {
			md.addAttribute("formmode", "addService");
			String url = env.getProperty("ipsx.url") + "/api/ServiceRequestId";
			String response = restTemplate.getForObject(url, String.class);
			md.addAttribute("SRID", response);
			// md.addAttribute("merchant_id",merchant_id);
			md.addAttribute("UNIT_ID", UNITID);
			md.addAttribute("merUserId", merUserId);
		} /* Alert */
		else if (formmode.equals("AlertClick")) {
			md.addAttribute("formmode", "AlertClick");
		} else if (formmode.equals("alert")) {
			md.addAttribute("formmode", "alert");
			String url = env.getProperty("ipsx.url") + "/api/AlertListForAdmin";
			AlertEntity[] response = restTemplate.getForObject(url, AlertEntity[].class);
			md.addAttribute("alt", response);// bIPS_Alert_Repo.getlst());
		} /* Notification */
		else if (formmode.equals("notification")) {
			md.addAttribute("formmode", "notification");
			System.out.println(merUserId + merchant_id + UNITID);
			if (acces.equals("UNIT")) {
				String url = env.getProperty("ipsx.url") + "/api/NotificationListforUnit?merchant_id=" + merUserId + "&unit_id=" + UNITID;
				NotificationEntity[] response = restTemplate.getForObject(url, NotificationEntity[].class);
				md.addAttribute("noti", response); // bIPS_Notification_Repo.getlst());
			} else {
				String url = env.getProperty("ipsx.url") + "/api/NotificationListforMerchant?merchant_id=" + merUserId;
				NotificationEntity[] response = restTemplate.getForObject(url, NotificationEntity[].class);
				md.addAttribute("noti", response); // bIPS_Notification_Repo.getlst());
			}

		} /* QR formmode */
		else if (formmode.equals("MerchantQR")) {
			md.addAttribute("formmode", "MerchantQR");
			String url = env.getProperty("ipsx.url") + "/api/MerchantQrList?user_id=" + user_id;
			BIPS_Mer_User_Management_Entity response = restTemplate.getForObject(url, BIPS_Mer_User_Management_Entity.class);
			md.addAttribute("MerchantView", response); // bIPS_MerUserManagement_Repo.getuser(user_id));
		} else if (formmode.equals("CustomerQR")) {
			md.addAttribute("formmode", "CustomerQR");
		} else if (formmode.equals("CustomerQRStat")) {
			md.addAttribute("UNITID", UNITID);
			md.addAttribute("UNITNAME", UNITNAME);
			md.addAttribute("formmode", "CustomerQRStat");
		} else if (formmode.equals("Receipt")) {
			md.addAttribute("formmode", "Receipt");
		} else if (formmode.equals("StatementView")) {
			md.addAttribute("formmode", "StatementView");
			md.addAttribute("MerdeviceId", null); // bIPS_OutwardTransMonitoring_Repo.getdeviceId(merUserId));
			md.addAttribute("Meruserid", null); // bIPS_OutwardTransMonitoring_Repo.getuserid(merUserId));
			md.addAttribute("MerchantUnit", null); // bIPS_OutwardTransMonitoring_Repo.getpartUnitId(merUserId));
			md.addAttribute("TranAccNum", null); // bIPS_OutwardTransMonitoring_Repo.getAccNum(merUserId));
		} else if (formmode.equals("DownloadStatement")) {
			md.addAttribute("formmode", "DownloadStatement");
			md.addAttribute("MerdeviceId", null); // bIPS_OutwardTransMonitoring_Repo.getdeviceId(merUserId));
			md.addAttribute("Meruserid", null); // bIPS_OutwardTransMonitoring_Repo.getuserid(merUserId));
			md.addAttribute("MerchantUnit", null); // bIPS_OutwardTransMonitoring_Repo.getpartUnitId(merUserId));
			md.addAttribute("Trans", null); // bIPS_OutwardTransMonitoring_Repo.getFromToDate(merUserId, from_date,
											// to_date, unit_id));
			md.addAttribute("FromDate0", from_date);
			md.addAttribute("ToDate0", to_date);
			md.addAttribute("StringUnit", stringunit);
		} else if (formmode.equals("DownloadStatement1")) {
			md.addAttribute("formmode", "DownloadStatement");
			md.addAttribute("MerdeviceId", null); // bIPS_OutwardTransMonitoring_Repo.getdeviceId(merUserId));
			md.addAttribute("Meruserid", null); // bIPS_OutwardTransMonitoring_Repo.getuserid(merUserId));
			md.addAttribute("MerchantUnit", null); // bIPS_OutwardTransMonitoring_Repo.getpartUnitId(merUserId));
			md.addAttribute("Trans", null); // bIPS_OutwardTransMonitoring_Repo.getFromToDateDev(merUserId, from_date,
											// to_date, device_id));
			md.addAttribute("FromDate0", from_date);
			md.addAttribute("ToDate0", to_date);
			md.addAttribute("StringUnit", stringunit);
		} else if (formmode.equals("DownloadStatement2")) {
			md.addAttribute("formmode", "DownloadStatement");
			md.addAttribute("MerdeviceId", null); // bIPS_OutwardTransMonitoring_Repo.getdeviceId(merUserId));
			md.addAttribute("Meruserid", null); // bIPS_OutwardTransMonitoring_Repo.getuserid(merUserId));
			md.addAttribute("MerchantUnit", null); // bIPS_OutwardTransMonitoring_Repo.getpartUnitId(merUserId));
			md.addAttribute("Trans", null); // bIPS_OutwardTransMonitoring_Repo.getFromToDateUser(merUserId, from_date,
											// to_date, user_id));
			md.addAttribute("FromDate0", from_date);
			md.addAttribute("ToDate0", to_date);
			md.addAttribute("StringUnit", stringunit);
		} else if (formmode.equals("NotifiAdd")) {
			md.addAttribute("formmode", "NotifiAdd");
			String url = env.getProperty("ipsx.url") + "/api/NotifiParamId";
			String response = restTemplate.getForObject(url, String.class);
			md.addAttribute("NPID", response);
			md.addAttribute("merUserId", merUserId);
			md.addAttribute("unitid", UNITID);
		}
		return "BIPS_MerchantOperation";
	}

	/*************************************
	 * Admin ---> UserProfile ---> User creation starts
	 * 
	 * @throws Exception
	 * 
	 * @throws SQLException
	 ****************************************/

	@RequestMapping(value = "changePassword", method = RequestMethod.POST)
	@ResponseBody
	public String changePassword(@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass, Model md, HttpServletRequest rq)
			throws Exception {
		String userid = (String) rq.getSession().getAttribute("USERID");
		try {
			String msg = userProfileService.changePasswords(oldpass, newpass, userid);
			return msg;
		} catch (Exception e) {
			return "Error changing password: " + e.getMessage();
		}
	}

	@RequestMapping(value = "changePasswordDashboard", method = RequestMethod.POST)
	@ResponseBody
	public String changePasswordDashboard(@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass, Model md,
			HttpServletRequest rq) throws Exception {
		String userid = (String) rq.getSession().getAttribute("USERID");
		try {
			String msg = userProfileService.changePasswordsDashboard(oldpass, newpass, userid);
			return msg;
		} catch (Exception e) {
			return "Error changing password: " + e.getMessage();
		}
	}

	@RequestMapping(value = "changePasswordloginscrren", method = RequestMethod.POST)
	@ResponseBody
	public String changePasswordlogin(@RequestParam("oldpass") String oldpass, @RequestParam("newpass") String newpass,
			@RequestParam("userid") String userid, @ModelAttribute UserProfile userProfile, Model md, HttpServletRequest rq) throws Exception {
		rq.getSession().getAttribute("USERID");
		String roleId = (String) rq.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String msg = userProfileService.changePasswords(oldpass, newpass, userid);
		return msg;
	}

	@RequestMapping(value = "changePasswordotp", method = RequestMethod.POST)
	@ResponseBody
	public String changePasswordotp(@RequestParam("newpass") String newpass, @RequestParam("userid") String userid, Model md, HttpServletRequest rq)
			throws Exception {

		ChangePasswordEntity cp = new ChangePasswordEntity();
		cp.setPassword(newpass);
		cp.setUser_id(userid);
		if (userid != null && newpass != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);
			EncryptionEntity o = new EncryptionEntity();
			String url = env.getProperty("ipsx.url") + "/api/ResetMerchantPasswordForInternet";
			ObjectMapper objectMapper = new ObjectMapper();
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			objectMapper.setDateFormat(dateFormat);
			String requestBody = objectMapper.writeValueAsString(cp);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, encryptedRequestEntity, String.class);
			String response1 = response.getBody();
			String decryptedData = Encryption.decrypt(response1, "123123");
			return decryptedData;
		} else {
			return "Error occurred while changing password.";
		}
	}

	@RequestMapping(value = "snapShot1")
	public String snapShot1(@RequestParam(required = false) String formmode, @RequestParam(required = false) String userid,
			@RequestParam(value = "tranDate", required = false) String date, @ModelAttribute ManualTransaction manualTransaction, Model md,
			HttpServletRequest req) throws FileNotFoundException, SQLException, IOException, ParseException {

		String loginuserid = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));

		String tranDate = "";
		if (date == null) {
			DateFormat dateFormat = new SimpleDateFormat("dd-MMM-yy");
			tranDate = dateFormat.format(new Date());

		} else {
			tranDate = date;
		}

		Gson gson = new Gson();

		// md.addAttribute("chartDataOut",
		// gson.toJson(monitorService.getSnapShotChartOut(tranDate)));
		// md.addAttribute("chartDataIn",
		// gson.toJson(monitorService.getSnapShotChartIn(tranDate)));

		SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
		Date currentDate = new Date();
		String currentDateRef = dateFormatWithMonthName.format(currentDate).toUpperCase();/* 01-JUN-2024 */

		if (merUserId == null) {
			md.addAttribute("click", null); // bIPS_OutwardTransMonitoring_Repo.getTranDevlst());
		} else {
			md.addAttribute("click", null); // bIPS_OutwardTransMonitoring_Repo.getTranDevlst1(merUserId));
		}

		// md.addAttribute("snapList", monitorService.getSnapShotDetails(tranDate));
		return "IPSSnapShot";
	}

	@RequestMapping(value = "snapShot")
	public String snapShot(@RequestParam(required = false) String formmode, @RequestParam(required = false) String tran_type,
			@RequestParam(required = false) String from_date, @RequestParam(required = false) String to_date,
			@RequestParam(required = false) String unit_id, Model md, HttpServletRequest req, @RequestParam(required = false) String message_ref)
			throws FileNotFoundException, SQLException, IOException, ParseException {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String acces = (String) req.getSession().getAttribute("acces");
		String UNITID = (String) req.getSession().getAttribute("UNITID");

		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PowerLog", acces);

		if (formmode == null || formmode.equals("TransDetails")) {
			md.addAttribute("formmode", "TransDetails");
			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			Date date = new Date();
			String valueDateRef = dateFormatWithMonthName.format(date).toUpperCase();
			String url = env.getProperty("ipsx.url") + "/api/CustomerTransactionListSingleDate?merchant_id=" + merUserId + "&Date=" + valueDateRef
					+ "&unit_id=" + UNITID;
			BIPS_Outward_Trans_Monitoring_Entity[] responseArray = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity[].class);
			md.addAttribute("Trans", responseArray);
		} else if (formmode.equals("customerViewSnap")) {
			md.addAttribute("formmode", "customerViewSnap");
			String url = env.getProperty("ipsx.url") + "/api/CustomerTransactionView?message_ref=" + message_ref;
			BIPS_Outward_Trans_Monitoring_Entity response = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity.class);
			md.addAttribute("viewcustomer", response);
		}

		return "IPSSnapShot";
	}

	/*************************************
	 * Admin ---> Branch --->Bank&Branch Starts
	 ****************************************/

	/*********************************************
	 * MERCHANT QR CODE STARTS
	 **********************************************/

	/*********************************************
	 * MERCHANT QR CODE STARTS
	 **********************************************/

	/*********************************************
	 * MERCHANT QR CODE STARTS
	 **********************************************/

	@RequestMapping("verifyUserSubmit")
	@ResponseBody
	public String verifyUserSubmit(HttpServletRequest req, @RequestParam(required = false) String user_ids,
			@ModelAttribute BIPS_Mer_User_Management_Entity bIPS_Mer_User_Management_Entity) {
		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String response = bankAndBranchMasterServices.VerifyUserMer(user_ids, userID);
		return response;
	}

	@RequestMapping(value = "verifyDevice", method = { RequestMethod.POST, RequestMethod.PUT })
	@ResponseBody
	public ResponseEntity<String> verifyDevice(HttpServletRequest req, @RequestParam(required = false) String user_ids,
			@ModelAttribute BIPS_Mer_Device_Management_Entity bIPS_Mer_Device_Management_Entity) {
		System.out.println("inside verifyDevice");
		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		ResponseEntity<String> response = bankAndBranchMasterServices.VerifyDeviceMer(user_ids, userID, bIPS_Mer_Device_Management_Entity);
		return response;
	}

	@RequestMapping(value = "verifyPass", method = { RequestMethod.POST, RequestMethod.PUT })
	@ResponseBody
	public ResponseEntity<String> verifyPass(HttpServletRequest req, @RequestParam(required = false) String user_ids,
			@ModelAttribute LoginEntity LoginEntity) {
		System.out.println("inside verifyDevice");

		String userID = (String) req.getSession().getAttribute("USERID");
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		ResponseEntity<String> response = bankAndBranchMasterServices.VerifyPassMer(user_ids, userID, req, LoginEntity);
		return response;
	}

	/*
	 * @RequestMapping("verifyService")
	 * 
	 * @ResponseBody public String verifyService(HttpServletRequest
	 * req, @RequestParam(required = false) String user_ids) {
	 * 
	 * System.out.println(
	 * "ggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggggg"
	 * + user_ids); String userID = (String)
	 * req.getSession().getAttribute("USERID"); String roleId = (String)
	 * req.getSession().getAttribute("ROLEID"); String response =
	 * bankAndBranchMasterServices.VerifyServiceMer(userID, user_ids); return
	 * response; }
	 */

	// ****************************
	// Service Charges And Fees
	// *************************************//

	@Autowired
	Encryption encryption;

	@RequestMapping(value = "ChargeBacks", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> ChargeBacks(HttpServletRequest req, @RequestParam(required = false) String message_id, Model md,
			HttpServletRequest rq, @ModelAttribute BIPS_Outward_Trans_Monitoring_Entity bipsEntity) throws Exception {
		String userid = (String) req.getSession().getAttribute("USERID");
		System.out.println("buiikn");
		String url = env.getProperty("ipsx.url") + "/api/ChargebackForOne?message_ref=" + message_id;
		BIPS_Outward_Trans_Monitoring_Entity cbval = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity.class);
		if (Objects.nonNull(cbval)) {
			System.out.println("njniknoo");
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);
			String url1 = env.getProperty("ipsx.url") + "/api/ws/revertMerchantFndTransfer?seqUniqueID=" + message_id + "&userid=" + userid;

			ResponseEntity<MCCreditTransferResponse> response = restTemplate.postForEntity(url1, // URL to send the
																									// request to
					null, // The request body to be sent (replace with your actual request body object)
					MCCreditTransferResponse.class // The response type to convert to
			);

			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Chargeback Approved successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error registering user.");
			}
		} else {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
		}
	}

	@RequestMapping(value = "CustomerRevert", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> CustomerRevert(@RequestParam("message_ref") String message_ref, Model md, HttpServletRequest rq) {
		String userid = (String) rq.getSession().getAttribute("USERID");

		if (userid == null) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in.");
		}

		System.out.println("Came Inside");

		HttpHeaders headers = new HttpHeaders();
		headers.set("PSU_Device_ID", "123123");
		headers.setContentType(MediaType.APPLICATION_JSON);

		EncryptionEntity o = new EncryptionEntity();

		String url1 = env.getProperty("ipsx.url") + "/api/InititedChargeBack?seqUniqueID=" + message_ref + "&userid=" + userid;
		ObjectMapper objectMapper = new ObjectMapper();
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		objectMapper.setDateFormat(dateFormat);

		String requestBody = "";

		try {
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

			ResponseEntity<String> response = restTemplate.exchange(url1, HttpMethod.POST, encryptedRequestEntity, String.class);

			// Handle the response from Application A
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Charge Back Initiated Successfully");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error Initiating Charge Back");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred: " + e.getMessage());
		}
	}

	@PostMapping(value = "RateMaintenance")
	@ResponseBody
	public ResponseEntity<String> RateMaintenance(Model md, HttpServletRequest req, @ModelAttribute BIPS_Rate_Maint_Entity user) {
		try {
			String url = env.getProperty("ipsx.url") + "/api/AddNewRate";
			String userID = (String) req.getSession().getAttribute("USERID");
			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);
			EncryptionEntity o = new EncryptionEntity();

			// Forward the data to Application A
			/*
			 * RestTemplate restTemplate = new RestTemplate(); ResponseEntity<Map> response
			 * = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(user),
			 * Map.class);
			 */
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(user);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, encryptedRequestEntity, String.class);

			// Handle the response from Application A
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("User registered successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error registering user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
		}
	}

	@PostMapping(value = "SubmitRate")
	@ResponseBody
	public ResponseEntity<String> SubmitRate(Model md, HttpServletRequest req, @ModelAttribute BIPS_Rate_Maint_Entity user) {
		try {
			System.out.println("First step in API add");

			System.out.println("First step in API add" + user.getSrl());
			String url = env.getProperty("ipsx.url") + "api/UpdateExistRate";
			String userID = (String) req.getSession().getAttribute("USERID");
			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);
			EncryptionEntity o = new EncryptionEntity();

			// Forward the data to Application A
			/*
			 * RestTemplate restTemplate = new RestTemplate(); ResponseEntity<Map> response
			 * = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(user),
			 * Map.class);
			 */
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(user);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.PUT, encryptedRequestEntity, String.class);

			// Handle the response from Application A
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("User registered successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error registering user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
		}
	}

	@RequestMapping(value = "changePasswordLogin", method = { RequestMethod.GET, RequestMethod.POST })
	public String changePasswordLogin(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) throws ParseException {
		return "ChangePasswordLogin";
	}

	@RequestMapping(value = "forgetpassword", method = { RequestMethod.GET, RequestMethod.POST })
	public String forgetpassword(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req) throws ParseException {
		md.addAttribute("message", "succes");
		return "forgetpassword";
	}

	// Define a class-level attribute to store the OTP
	public String otpValue;

	@RequestMapping(value = "sendingmail_forget", method = { RequestMethod.GET, RequestMethod.POST })
	public ResponseEntity<String> sendingmail_forget(@RequestParam(required = false) String userid, @RequestParam(required = false) String Emailid,
			@RequestParam(required = false) List<String> t, Model md) throws SQLException, ParseException {
		System.out.println("Hiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii");
		// Generate OTP
		String otp = generateOTP();
		// Save the generated OTP to the class attribute
		otpValue = otp;

		String to = Emailid;
		String from = "barath.p@bornfire.in";
		String username = "barath.p@bornfire.in"; // change accordingly
		String password = "Bornfire@123"; // change accordingly
		String host = "sg2plzcpnl491716.prod.sin2.secureserver.net";

		System.out.println("sdfghjkl;");

		// Call sendMail method with correct parameters
		Passwordsendingmail.sendingmailones(from, host, to, username, password, otp); // pass from, host,
		// password, and to

		// Return success response
		return ResponseEntity.status(HttpStatus.OK).body(otp);
	}

	// Your other methods...

	// Define a method to access the OTP value
	public String getOtpValue() {
		return otpValue;
	}

	@RequestMapping(value = "sendingmail_otp", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody // Indicates that the return value should be written directly to the HTTP
					// response body
	public String sendingmail_otp(@RequestParam(required = false) String data, @RequestParam(required = false) String otp, Model md,
			HttpServletRequest req) throws ParseException {
		String otps = getOtpValue();
		System.out.println(otps);
		System.out.println(otp);
		md.addAttribute("message", "success");
		String msg = null;
		if (otps.equals(otp)) { // Correctly comparing strings using equals()
			msg = "OTP confirmed";
		} else {
			msg = "OTP not correct";
		}
		return msg;
	}

	public String otpsms;

	public String getsmsvalidationnmsg() {
		return otpsms;
	}

	@RequestMapping(value = "validtingsms_otp", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody // Indicates that the return value should be written directly to the HTTP
					// response body
	public String validtingsms_otp(@RequestParam(required = false) String data, @RequestParam(required = false) String otp, Model md,
			HttpServletRequest req) throws ParseException {
		System.out.println("hhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhsmd validainf message");
		String otps = getsmsvalidationnmsg();
		System.out.println(otps);
		System.out.println(otp);
		md.addAttribute("message", "success");
		String msg = null;
		if (otps.equals(otp)) { // Correctly comparing strings using equals()
			System.out.println("sucesssssssssssssssssssssssssssssssssss");
			msg = "OTP confirmed";
		} else {
			System.out.println("not sucessssssssssssssssssssssss");
			msg = "OTP not correct";
		}
		return msg;
	}

	public String getotphere() {
		return otpValue;
	}

	@RequestMapping(value = "sendingsms_otp", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String sendingsms_otp(@RequestParam(required = false) String data, @RequestParam(required = false) String sender,
			@RequestParam(required = false) String i, @RequestParam(required = false) String v, @RequestParam(required = false) String s,
			@RequestParam(required = false) String k, @RequestParam(required = false) String apikey, @RequestParam(required = false) String smsinput,
			Model md, HttpServletRequest req) throws ParseException, UnsupportedEncodingException {

		try {
			// Generate OTP
			System.out.println("phoneeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeee" + smsinput);
			String otpss = generateOTP();
			otpsms = otpss;
			System.out.println("jjjjjjjjjjjjhhbhjbhjbjhbhjbhjbhjbhjbhjbhjbhjbhjbhj" + otpss);
			System.out.println(v);
			String pl = "siddhaiyan%40bornfire.in";
			String sendmesaage = "Dear%20Customer%2C%20%7B%23" + otpss
					+ "%23%7D%20is%20your%20OTP%20to%20authenticate%20your%20login.%20Do%20not%20share%20it%20with%20anyone%20else.%20Thanks%2C%20BORNFIRE%20INNOVATION%20PRIVATE%20LIMITED";
			String encodedApiKey = URLEncoder.encode(sender, "UTF-8");
			String encodedSender = URLEncoder.encode(i, "UTF-8");
			String encodedMobileNumbers = URLEncoder.encode(s, "UTF-8");
			String encodedTemplateId = URLEncoder.encode(k, "UTF-8");
			String encodedMessage = URLEncoder.encode(v, "UTF-8");
			String encodedapimail = URLEncoder.encode(apikey, "UTF-8");
			System.out.println("jjjjjjjjjjjjjjjjjjjjjjjjjjj" + encodedMessage);

			// Construct API URL
			String apiUrl = "https://api.smslane.com/api/v2/SendSMS";
			String constructedUrl = apiUrl + "?SenderId=" + encodedApiKey + "&Message=" + sendmesaage + "&MobileNumbers=" + "91" + smsinput
					+ "&ApiKey=" + encodedSender + "&ClientId=" + pl;
			System.out.println(constructedUrl);

			// Make HTTP GET request to send SMS
			HttpURLConnection connection = null;
			try {
				URL url = new URL(constructedUrl);
				connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");

				// Reading the response
				BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String inputLine;
				StringBuilder response = new StringBuilder();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				System.out.println("Response: " + response.toString());
				return otpss;
			} catch (IOException e) {
				e.printStackTrace();
				return "Error occurred while sending SMS";
			} finally {
				if (connection != null) {
					connection.disconnect();
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "Error constructing URL";
		}
	}

	public String generateOTP() {
		// Generate a 6-digit OTP
		Random random = new Random();
		int otpNumber = 100000 + random.nextInt(900000); // Generate a random 6-digit number
		return String.valueOf(otpNumber);
	}

	public String getPSUDeviceID() {
		SystemInfo si = new SystemInfo();
		HardwareAbstractionLayer hal = si.getHardware();
		List<PowerSource> powerSources = hal.getPowerSources();
		String response = "";
		if (powerSources.isEmpty()) {
			response = "No power sources found.";
		} else {
			for (PowerSource powerSource : powerSources) {
				response = powerSource.getDeviceName();
			}
		}
		return response;
	}

	@Autowired
	com.bornfire.configuration.Encryption Encryption;

	@PostMapping(value = "addUserManagement")
	@ResponseBody
	public ResponseEntity<String> userManagementUser(@RequestParam(required = false) String password1, @RequestParam(required = false) String user,
			HttpServletRequest req, @RequestParam(value = "file", required = false) MultipartFile file,
			@ModelAttribute BIPS_Mer_User_Management_Entity bIPS_Mer_User_Management_Entity) throws IOException {

		System.out.println("UserId" + bIPS_Mer_User_Management_Entity.getUser_id());

		String url = env.getProperty("ipsx.url") + "api/AddUserData";
		String userID = (String) req.getSession().getAttribute("USERID");

		ObjectMapper objectMapper = new ObjectMapper();
		HttpHeaders headers = new HttpHeaders();
		headers.set("PSU_Device_ID", "123123");
		headers.setContentType(MediaType.APPLICATION_JSON);
		EncryptionEntity o = new EncryptionEntity();

		try {
			bIPS_Mer_User_Management_Entity.setEntry_user(userID);
			if (file != null) {
				byte[] byteArr = file.getBytes();
				bIPS_Mer_User_Management_Entity.setPhoto(byteArr);
			}
			bIPS_Mer_User_Management_Entity.setUser_status1("ACTIVE");
			bIPS_Mer_User_Management_Entity.setLogin_channel1("WEB");
			String requestBody = objectMapper.writeValueAsString(bIPS_Mer_User_Management_Entity);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, encryptedRequestEntity, String.class);
			String response1 = response.getBody();

			// Handle the response from Application A
			String decryptedData = Encryption.decrypt(response1, "123123");
			System.out.println("Decrypted data: " + decryptedData);
			ObjectMapper objectMapper1 = new ObjectMapper();
			String message = null;
			try {
				ApiResponse apiResponse = objectMapper1.readValue(decryptedData, ApiResponse.class);
				message = apiResponse.getMessage();
				System.out.println("Message: " + message);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok(message);
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error registering user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
		}
	}

	@PostMapping("/UserProfiles")
	@ResponseBody
	public ResponseEntity<String> VerifyUserMer(@ModelAttribute BIPS_Mer_User_Management_Entity up, HttpServletRequest req) {
		String msg = "";
		String userid = (String) req.getSession().getAttribute("USERID");
		Date currentTime = new Date();

		System.out.println("usss" + up.getLogin_status1() + up.getUser_disable_from_date1() + up.getUser_disable_to_date1());
		System.out.println("In the second stage of verification " + up);

		// Construct the URL for UpdateUser endpoint in Application B
		String updateUserUrl = env.getProperty("ipsx.url") + "api/UpdateUser";
		// String userID = (String) req.getSession().getAttribute("USERID");
		HttpHeaders headers = new HttpHeaders();
		headers.set("PSU_Device_ID", "123123");
		headers.setContentType(MediaType.APPLICATION_JSON);
		EncryptionEntity o = new EncryptionEntity();

		try {
			// Ensure roleEntity is not null (you may adjust this check based on your logic)
			if (Objects.nonNull(up)) {
				System.out.println("Service in the third stage");

				// Set fields for verification
				up.setModify_user(userid);
				up.setModify_time(currentTime);
				up.setPassword_life1("30");
				up.setModify_flag("Y");
				up.setEntry_flag("N");
				// Send PUT request to Application B (UpdateUser endpoint)
				/*
				 * ResponseEntity<String> response = restTemplate.exchange(updateUserUrl,
				 * HttpMethod.PUT, new HttpEntity<>(up), String.class);
				 */

				ObjectMapper objectMapper = new ObjectMapper();
				String requestBody = objectMapper.writeValueAsString(up);
				String encryptedBody = Encryption.encrypt(requestBody, "123123");
				o.setEncryptedstring(encryptedBody);
				HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);

				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

				ResponseEntity<String> response = restTemplate.exchange(updateUserUrl, HttpMethod.POST, encryptedRequestEntity, String.class);

				// Handle the response from Application B
				if (response.getStatusCode().is2xxSuccessful()) {
					return ResponseEntity.ok("User Modified successfully!");
				} else {
					return ResponseEntity.status(response.getStatusCode()).body("Error verifying user.");
				}
			} else {
				msg = "Role entity not provided or invalid";
				return ResponseEntity.badRequest().body(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "Exception occurred while verifying user";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
		}
	}

	@PostMapping(value = "deviceManagementsub")
	@ResponseBody
	public ResponseEntity<String> deviceManagementsub(Model md, HttpServletRequest req,
			@ModelAttribute BIPS_Mer_Device_Management_Entity bIPS_Mer_Device_Management_Entity) {
		try {
			String url = env.getProperty("ipsx.url") + "api/AddDevice";
			String userID = (String) req.getSession().getAttribute("USERID");
			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);
			EncryptionEntity o = new EncryptionEntity();

			if (bIPS_Mer_Device_Management_Entity.getDevice_status().equals("ACTIVE")) {
				bIPS_Mer_Device_Management_Entity.setDisable_flag("N");
			} else {
				bIPS_Mer_Device_Management_Entity.setDisable_flag("Y");
			}
			bIPS_Mer_Device_Management_Entity.setModify_flag("N");
			bIPS_Mer_Device_Management_Entity.setEntry_flag("N");
			bIPS_Mer_Device_Management_Entity.setEntry_time(new Date());
			bIPS_Mer_Device_Management_Entity.setEntry_user(userID);
			bIPS_Mer_Device_Management_Entity.setMerchant_legal_user_id(bIPS_Mer_Device_Management_Entity.getMerchant_user_id());
			bIPS_Mer_Device_Management_Entity.setMerchant_corporate_name(bIPS_Mer_Device_Management_Entity.getMerchant_name());
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(bIPS_Mer_Device_Management_Entity);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, encryptedRequestEntity, String.class);
			// Handle the response from Application A
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Device registered successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error registering user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
		}
	}

	@RequestMapping(value = "DeviceMan", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> DeviceMan(String user_ids, String userID, @RequestParam(required = false) String entryuser,
			BIPS_Mer_Device_Management_Entity up, Model md, HttpServletRequest req) {
		String msg = "";

		try {
			System.out.println("In the second stage of verification " + entryuser);
			if (up == null) {
				msg = "Role entity not provided or invalid";
				return ResponseEntity.badRequest().body(msg);
			}

			System.out.println("In the second stage of verification " + up.getDevice_id());
			String updateUserUrl = env.getProperty("ipsx.url") + "api/UpdateDeviceData";

			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);
			EncryptionEntity o = new EncryptionEntity();

			System.out.println("Service in the second stage");
			String userid = (String) req.getSession().getAttribute("USERID");
			Date currentTime = new Date();

			up.setModify_user(userid);
			up.setModify_time(currentTime);
			up.setEntry_user(entryuser);
			up.setModify_flag("Y");
			up.setEntry_flag("N");

			/*
			 * ResponseEntity<String> response = restTemplate.exchange(updateUserUrl,
			 * HttpMethod.PUT, new HttpEntity<>(up), String.class);
			 */

			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(up);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

			ResponseEntity<String> response = restTemplate.exchange(updateUserUrl, HttpMethod.PUT, encryptedRequestEntity, String.class);
			// Handle the response from Application B
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Device modified successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error verifying user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "Exception occurred while verifying user";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
		}
	}

	@RequestMapping(value = "UnitSubmitverify", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> UnitSubmitverify(@RequestParam(required = false) String userId, @RequestParam(required = false) String merchantId,
			@RequestParam(required = false) String unitId, HttpServletRequest req) throws SQLException {

		try {
			String updateUserUrl = env.getProperty("ipsx.url") + "api/UnitSubmitverifyapi?userId=" + userId + "&merchantId=" + merchantId + "&unitId="
					+ unitId;
			ResponseEntity<String> response = restTemplate.exchange(updateUserUrl, HttpMethod.POST, null, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok(response.getBody());
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error verifying user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred while verifying user");
		}
	}

	@RequestMapping(value = "UnitManagementModify", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> UnitManage(@RequestParam(required = false) String entryuser, @ModelAttribute BIPS_Unit_Mangement_Entity up,
			Model md, HttpServletRequest req) {
		String msg = "";
		System.out.println("Entered");
		try {
			if (up == null) {
				msg = "Role entity not provided or invalid";
				return ResponseEntity.badRequest().body(msg);
			}

			String updateUserUrl = env.getProperty("ipsx.url") + "api/Unitedit";
			String userid = (String) req.getSession().getAttribute("USERID");
			Date currentTime = new Date();

			up.setModify_user(userid);
			up.setModify_time(currentTime);
			up.setModify_flag("Y");
			up.setEntry_flag("N");
			up.setEntry_user(entryuser);
			up.setDel_flg("N");

			ResponseEntity<String> response = restTemplate.exchange(updateUserUrl, HttpMethod.POST, new HttpEntity<>(up), String.class);
			System.out.println(response);
			if (response.getStatusCode().is2xxSuccessful()) {
				System.out.println("Response : " + response.getBody());
				return ResponseEntity.ok(response.getBody());
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error verifying user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "Exception occurred while verifying user";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
		}
	}

	/**
	 * @param md
	 * @param req
	 * @param LoginEntity
	 * @param merchant_Rep_id
	 * @return
	 */
	@RequestMapping(value = "passwordManagement", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> registerUser(Model md, HttpServletRequest req, @ModelAttribute LoginEntity LoginEntity,
			@RequestParam(required = false) String merchant_Rep_id) {
		System.out.println(LoginEntity.getMerchant_user_id());
		System.out.println("rep Id " + merchant_Rep_id);

		String updateUserUrl = env.getProperty("ipsx.url") + "api/passwordManagement";
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		System.out.println(LoginEntity.getCountrycode() + " COUNTRY CODE");
		System.out.println(LoginEntity.getAlt_countrycode() + " ALT COUNTRY CODE");
		try {
			// Check if user status is "Active" and set disable flag accordingly
			if ("Active".equals(LoginEntity.getUser_status())) {
				LoginEntity.setUser_disable_flag("N");
			} else {
				LoginEntity.setUser_disable_flag("Y");
			}

			// Set delete flag to "N"
			LoginEntity.setDel_flag("N");
			LoginEntity.setEntry_flag("N");
			LoginEntity.setModify_flag("Y");
			LoginEntity.setModify_user(merUserId);
			LoginEntity.setModify_time(new Date());

			// Set login_status to 'Y'
			LoginEntity.setLogin_status("Y");

			// Make the REST call to the external service
			ResponseEntity<String> response = restTemplate.exchange(updateUserUrl, HttpMethod.POST, new HttpEntity<>(LoginEntity), String.class);

			// Handle the response from the external service
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Password modified successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error verifying user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred while verifying user");
		}
	}

	@RequestMapping(value = "passSubmitverify", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> passSubmitverify(Model md, HttpServletRequest req, @ModelAttribute LoginEntity LoginEntity) {
		System.out.println(LoginEntity.getMerchant_user_id());
		String updateUserUrl = env.getProperty("ipsx.url") + "api/passwordManagement";
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		try {
			// Check if user status is "Active" and set disable flag accordingly
			if ("Active".equals(LoginEntity.getUser_status())) {
				LoginEntity.setUser_disable_flag("N");
			} else {
				LoginEntity.setUser_disable_flag("Y");
			}

			// Set delete flag to "N"
			LoginEntity.setDel_flag("N");
			LoginEntity.setEntry_flag("Y");
			LoginEntity.setModify_flag("N");
			LoginEntity.setVerify_user(merUserId);

			// Set login_status to 'Y'
			LoginEntity.setLogin_status("Y");

			// Make the REST call to the external service
			ResponseEntity<String> response = restTemplate.exchange(updateUserUrl, HttpMethod.POST, new HttpEntity<>(LoginEntity), String.class);

			// Handle the response from the external service
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("User modified successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error verifying user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred while verifying user");
		}
	}

	@PostMapping(value = "addunitform")
	@ResponseBody
	public ResponseEntity<String> addunitform(Model md, HttpServletRequest req, @ModelAttribute BIPS_Unit_Mangement_Entity user) {
		try {
			System.out.println("First step in API add");

			String url = env.getProperty("ipsx.url") + "api/Addunit?psuDeviceID=" + "123123";
			String userID = (String) req.getSession().getAttribute("USERID");

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			EncryptionEntity o = new EncryptionEntity();
			user.setEntry_user(userID);
			user.setEntry_time(new Date());
			user.setModify_flag("N");
			user.setEntry_flag("N");
			user.setDel_flg("N");
			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(user);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);

			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, encryptedRequestEntity, String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Unit registered successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error registering user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
		}
	}

	@PostMapping(value = "ServiceRequest")
	@ResponseBody
	public ResponseEntity<String> ServiceRequest(Model md, HttpServletRequest req, @ModelAttribute BIPS_Service_Req_Entity service_request) {
		try {

			String url = env.getProperty("ipsx.url") + "api/AddServiceReq";
			String userID = (String) req.getSession().getAttribute("USERID");
			service_request.setEntry_user(userID);
			service_request.setRequest_date(new Date());
			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);
			EncryptionEntity o = new EncryptionEntity();

			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(service_request);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, encryptedRequestEntity, String.class);

			String response1 = response.getBody();

			String decryptedData = Encryption.decrypt(response1, "123123");
			System.out.println("Decrypted data: " + decryptedData);

			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok(decryptedData);
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error Occured During Service Request.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Occured During Service Request.");
		}
	}

	@PostMapping(value = "submitnotification")
	@ResponseBody
	public ResponseEntity<String> submitnotification(Model md, HttpServletRequest req, @ModelAttribute NotificationEntity NotificationEntity) {
		try {
			String url = env.getProperty("ipsx.url") + "api/AddNotification";
			HttpHeaders headers = new HttpHeaders();
			headers.set("PSU_Device_ID", "123123");
			headers.setContentType(MediaType.APPLICATION_JSON);
			EncryptionEntity o = new EncryptionEntity();

			ObjectMapper objectMapper = new ObjectMapper();
			String requestBody = objectMapper.writeValueAsString(NotificationEntity);
			String encryptedBody = Encryption.encrypt(requestBody, "123123");
			o.setEncryptedstring(encryptedBody);
			HttpEntity<EncryptionEntity> encryptedRequestEntity = new HttpEntity<>(o, headers);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, encryptedRequestEntity, String.class);

			// Handle the response from Application A
			String decryptedData = Encryption.decrypt(response.getBody(), "123123");
			System.out.println("Decrypted data: " + decryptedData);

			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok(decryptedData);
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error registering user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error registering user.");
		}
	}

	@GetMapping(value = "sendOtp")
	@ResponseBody
	public String sendOtp(Model md, HttpServletRequest req, @RequestParam(required = false) String userId) throws Exception {

		String sendOtpUrl = env.getProperty("ipsx.url") + "/api/OtpForMerchant?merchant_rep_id=" + userId;
		HttpHeaders headers = new HttpHeaders();
		headers.set("PSU_Device_ID", "123123");
		headers.setContentType(MediaType.APPLICATION_JSON);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
		HttpEntity<String> entity = new HttpEntity<>(headers);
		ResponseEntity<String> responseEntity = restTemplate.exchange(sendOtpUrl, HttpMethod.GET, entity, String.class);
		String response = responseEntity.getBody();
		String decryptedData = Encryption.decrypt(response, "123123");
		return decryptedData;
	}

	@RequestMapping(value = "transaction")
	public String transaction(@RequestParam(required = false) String formmode, @RequestParam(required = false) String tran_type,
			@RequestParam(required = false) String from_date, @RequestParam(required = false) String to_date,
			@RequestParam(required = false) String unit_id, Model md, HttpServletRequest req)
			throws FileNotFoundException, SQLException, IOException, ParseException {

		String roleId = (String) req.getSession().getAttribute("ROLEID");
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String merchant_name = (String) req.getSession().getAttribute("MER_USER_NAME");
		String acces = (String) req.getSession().getAttribute("acces");

		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		md.addAttribute("PowerLog", acces);
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchant_id", merUserId);
			md.addAttribute("merchant_name", merchant_name);
			md.addAttribute("formmode", "list");
			String url = env.getProperty("ipsx.url") + "/api/UnitDistinctList?merchant_id=" + merUserId;
			String[] response = restTemplate.getForObject(url, String[].class);
			List<String> responseref = Arrays.asList(response);
			md.addAttribute("unit_ids", responseref);
		} else if (formmode.equals("tranList")) {
			md.addAttribute("merchant_id", merUserId);
			md.addAttribute("merchant_name", merchant_name);
			md.addAttribute("from_date", from_date);
			md.addAttribute("to_date", to_date);
			md.addAttribute("tran_type", tran_type);
			md.addAttribute("unit_id", unit_id);
			md.addAttribute("formmode", formmode);
			if (Objects.nonNull(unit_id)) {
				String url = env.getProperty("ipsx.url") + "/api/transactionListReports?merchant_id=" + merUserId + "&unit_id=" + unit_id
						+ "&tran_type=" + tran_type + "&from_date=" + from_date + "&to_date=" + to_date;
				BIPS_Outward_Trans_Monitoring_Entity[] response = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity[].class);
				md.addAttribute("txnCount", response.length);
				md.addAttribute("txns", response);
			} else {
				String url = env.getProperty("ipsx.url") + "/api/transactionListReports?merchant_id=" + merUserId + "&tran_type=" + tran_type
						+ "&from_date=" + from_date + "&to_date=" + to_date;
				BIPS_Outward_Trans_Monitoring_Entity[] response = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity[].class);
				md.addAttribute("txnCount", response.length);
				md.addAttribute("txns", response);
			}
		}
		return "TransactionReports";

	}

	@RequestMapping(value = "chargebacktxns")
	public String chargebacktxns(@RequestParam(required = false) String formmode, @RequestParam(required = false) String tran_type,
			@RequestParam(required = false) String from_date, @RequestParam(required = false) String to_date,
			@RequestParam(required = false) String unit_id, Model md, HttpServletRequest req)
			throws FileNotFoundException, SQLException, IOException, ParseException {

		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String merchant_name = (String) req.getSession().getAttribute("MER_USER_NAME");
		String acces = (String) req.getSession().getAttribute("acces");

		md.addAttribute("PowerLog", acces);
		if (formmode == null || formmode.equals("list")) {
			md.addAttribute("merchant_id", merUserId);
			md.addAttribute("merchant_name", merchant_name);
			md.addAttribute("formmode", "list");
			String url = env.getProperty("ipsx.url") + "/api/UnitDistinctList?merchant_id=" + merUserId;
			String[] response = restTemplate.getForObject(url, String[].class);
			List<String> responseref = Arrays.asList(response);
			md.addAttribute("unit_ids", responseref);
		} else if (formmode.equals("tranList")) {
			md.addAttribute("merchant_id", merUserId);
			md.addAttribute("merchant_name", merchant_name);
			md.addAttribute("from_date", from_date);
			md.addAttribute("to_date", to_date);
			md.addAttribute("tran_type", tran_type);
			md.addAttribute("unit_id", unit_id);
			md.addAttribute("formmode", formmode);
			if (Objects.nonNull(unit_id)) {
				String url = env.getProperty("ipsx.url") + "/api/chargebackTransactionListReports?merchant_id=" + merUserId + "&unit_id=" + unit_id
						+ "&tran_type=" + tran_type + "&from_date=" + from_date + "&to_date=" + to_date;
				BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
				md.addAttribute("txnCount", response.length);
				md.addAttribute("txns", response);
			} else {
				String url = env.getProperty("ipsx.url") + "/api/chargebackTransactionListReports?merchant_id=" + merUserId + "&tran_type="
						+ tran_type + "&from_date=" + from_date + "&to_date=" + to_date;
				BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
				md.addAttribute("txnCount", response.length);
				md.addAttribute("txns", response);
			}
		}
		return "ChargebackReports";

	}

	@RequestMapping(value = "getLoginSecurityData", method = RequestMethod.GET)
	@ResponseBody
	public LoginSecurity getLoginSecurityData(Model md, HttpServletRequest rq) {
		String url = env.getProperty("ipsx.url") + "/api/LoginSecurityData";
		LoginSecurity response = restTemplate.getForObject(url, LoginSecurity.class);
		System.out.println("----------" + response);
		return response;
	}

	@RequestMapping(value = "deleteUnit", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteUnit(Model md, HttpServletRequest req, @RequestParam String unit_id, @RequestParam String reason) {
		String userID = (String) req.getSession().getAttribute("USERID");
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		// Construct the URL properly and encode parameters
		String baseUrl = env.getProperty("ipsx.url") + "api/DeleteUnitData";
		String url = baseUrl + "?unit_id=" + unit_id + "&remark=" + reason + "&delete_user=" + userID + "&merchantId=" + merUserId;

		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(new HttpHeaders()), String.class);

			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Unit deleted successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error deleting unit.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred while deleting unit.");
		}
	}

	@RequestMapping(value = "RevertedChargeBackTransaction")
	public String RevertedChargeBackTransaction(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			@RequestParam(required = false) String sql_unic_id, @RequestParam(required = false) String message_ref,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String UNITID = (String) req.getSession().getAttribute("UNITID");
		String UNITNAME = (String) req.getSession().getAttribute("UNITNAME");
		String acces = (String) req.getSession().getAttribute("acces");
		String BUSER = (String) req.getSession().getAttribute("BUSER");

		if (formmode == null || formmode.equals("list")) {
			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			Date currentDate = new Date();
			String currentDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();
			md.addAttribute("formmode", "Revert");
			md.addAttribute("currentDate", valueDate);
			// md.addAttribute("Back",
			// bips_ChargeBack_Repo.getRevertedTransactionList(currentDateRef));

			if (acces.equals("UNIT")) {
				System.out.println("unit login");
				String url = env.getProperty("ipsx.url") + "/api/UnitChargeBackListRevert?merchant_id=" + merUserId + "&unit_id=" + UNITID;
				BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
				md.addAttribute("Back", response);
			} else {
				String url = env.getProperty("ipsx.url") + "/api/ChargeBackListRevert?merchant_id=" + merUserId;
				BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
				md.addAttribute("Back", response);
			}
		} else if (formmode.equals("addCharge")) {
			md.addAttribute("formmode", "addCharge");
			// md.addAttribute("viewcharge", bips_ChargeBack_Repo.getTranfees(sql_unic_id));
			String url = env.getProperty("ipsx.url") + "/api/ChargebackForOne?message_ref=" + message_ref;
			BIPS_Outward_Trans_Monitoring_Entity response = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity.class);
			md.addAttribute("viewcharge", response);
		}
		return "IPSChargeback.html";
	}

	@RequestMapping(value = "InitiateTransaction")
	public String InitiateTransaction(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			@RequestParam(required = false) String sql_unic_id, @RequestParam(required = false) String message_ref,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String UNITID = (String) req.getSession().getAttribute("UNITID");
		String UNITNAME = (String) req.getSession().getAttribute("UNITNAME");
		String acces = (String) req.getSession().getAttribute("acces");
		String BUSER = (String) req.getSession().getAttribute("BUSER");
		String userID = (String) req.getSession().getAttribute("USERID");
		if (formmode == null || formmode.equals("list")) {
			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			Date currentDate = new Date();
			String currentDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();
			md.addAttribute("formmode", "Initiate");
			md.addAttribute("currentDate", valueDate);
			// md.addAttribute("Back",
			// bips_ChargeBack_Repo.getRevertedTransactionList(currentDateRef));

			SimpleDateFormat dateFormatWithMonthNames = new SimpleDateFormat("dd-MMM-yyyy");
			Date date = new Date();
			String valueDateRef = dateFormatWithMonthNames.format(date).toUpperCase();
			String url = env.getProperty("ipsx.url") + "/api/UserTransactionListSingleDate?user_id=" + userID + "&Date=" + valueDateRef;

			List<BIPS_Outward_Trans_Monitoring_Entity> responseList = Arrays
					.asList(restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity[].class));
			md.addAttribute("Back", responseList);

		} else if (formmode.equals("addCharge")) {
			md.addAttribute("formmode", "addCharge");
			// md.addAttribute("viewcharge", bips_ChargeBack_Repo.getTranfees(sql_unic_id));

			String url = env.getProperty("ipsx.url") + "/api/ChargebackForOne?message_ref=" + message_ref;
			BIPS_Outward_Trans_Monitoring_Entity response = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity.class);
			md.addAttribute("viewcharge", response);
		}
		return "IPSChargeback.html";
	}

	@RequestMapping(value = "chargeBackRecord")
	public String chargeBackRecord(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			@RequestParam(required = false) String sql_unic_id, @RequestParam(required = false) String message_ref,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate) throws SQLException {
		String roleId = (String) req.getSession().getAttribute("ROLEID");
		md.addAttribute("IPSRoleMenu", AccessRoleService.getRoleMenu(roleId));
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String UNITID = (String) req.getSession().getAttribute("UNITID");
		String acces = (String) req.getSession().getAttribute("acces");

		String LoginUserId = (String) req.getSession().getAttribute("USERID");

		if (formmode == null || formmode.equals("list")) {
			SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
			Date currentDate = new Date();
			String currentDateRef = dateFormatWithMonthName.format(valueDate).toUpperCase();
			md.addAttribute("formmode", "list");
			md.addAttribute("currentDate", valueDate);
			// md.addAttribute("Back", bips_ChargeBack_Repo.getAllList(currentDateRef));
			if (acces.equals("UNIT")) {
				String url = env.getProperty("ipsx.url") + "/api/UnitChargeBackList?merchant_id=" + merUserId + "&unit_id=" + UNITID;
				BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
				md.addAttribute("Back", response);
			} else {
				String url = env.getProperty("ipsx.url") + "/api/ChargeBackList?merchant_id=" + merUserId;
				BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
				md.addAttribute("Back", response);
			}

		} else if (formmode.equals("addCharge")) {
			md.addAttribute("formmode", "addCharge");
			md.addAttribute("LoginUserId", LoginUserId);
			String url = env.getProperty("ipsx.url") + "/api/ChargebackForOne?message_ref=" + sql_unic_id;
			BIPS_Charge_Back_Entity response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity.class);
			System.out.println(response);
			md.addAttribute("viewcharge", response);
			md.addAttribute("InitiateEntryUser", response.getEntry_user());
		} else if (formmode.equals("CustTxn")) {
			md.addAttribute("formmode", "CustTxn");
			String url = env.getProperty("ipsx.url") + "/api/TransactionForOne?message_ref=" + sql_unic_id;
			BIPS_Outward_Trans_Monitoring_Entity response = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity.class);
			System.out.println(response);
			md.addAttribute("viewcharge", response);
		}
		return "IPSChargeback.html";
	}

	@RequestMapping(value = "deleteUser", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteUser(Model md, HttpServletRequest req, @RequestParam String user_id, @RequestParam String reason) {
		String userID = (String) req.getSession().getAttribute("USERID");
		String baseUrl = env.getProperty("ipsx.url") + "api/DeleteUserData";
		String url = baseUrl + "?userid=" + user_id + "&remark=" + reason + "&verifyuser=" + userID;
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(new HttpHeaders()), String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("User deleted successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error deleting unit.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred while deleting unit.");
		}
	}

	@RequestMapping(value = "deleteDevice", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<String> deleteDevice(Model md, HttpServletRequest req, @RequestParam String device_id, @RequestParam String reason) {
		String userID = (String) req.getSession().getAttribute("USERID");
		String baseUrl = env.getProperty("ipsx.url") + "api/DeleteDeviceData";
		String url = baseUrl + "?deviceid=" + device_id + "&remark=" + reason + "&verifyuser=" + userID;
		try {
			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, new HttpEntity<>(new HttpHeaders()), String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Device deleted successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error deleting unit.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Exception occurred while deleting unit.");
		}
	}

	@RequestMapping(value = "resetUser", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String resetUser(Model md, HttpServletRequest req, @RequestParam String user_id) {
		// Retrieve current session user ID
		String sessionUserID = (String) req.getSession().getAttribute("USERID");
		String logoutUrl = env.getProperty("ipsx.url") + "api/LogoutforMobileUsingWeb?user_id=" + user_id;
		String response = restTemplate.getForObject(logoutUrl, String.class);
		return response;
	}

	@RequestMapping(value = "resetPassMerId", method = { RequestMethod.GET, RequestMethod.POST })
	@ResponseBody
	public String resetPassword(Model md, HttpServletRequest req, @RequestParam String merchantRepId) {
		// Retrieve current session user ID
		String sessionUserID = (String) req.getSession().getAttribute("USERID");
		String logoutUrl = env.getProperty("ipsx.url") + "api/LogoutforTabUsingWeb?MerchantRepId=" + merchantRepId;
		String response = restTemplate.getForObject(logoutUrl, String.class);
		return response;
	}

	@RequestMapping(value = "pendingChargeBackTransaction")
	public String pendingChargeBackTransaction(@RequestParam(required = false) String formmode, Model md, HttpServletRequest req,
			@RequestParam(required = false) String sql_unic_id, @RequestParam(required = false) String message_ref,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate) throws SQLException {

		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String UNITID = (String) req.getSession().getAttribute("UNITID");
		String acces = (String) req.getSession().getAttribute("acces");

		if (formmode == null || formmode.equals("Pending")) {
			if (Objects.nonNull(valueDate)) {
				// SimpleDateFormat dateFormatWithMonthName = new
				// SimpleDateFormat("dd-MMM-yyyy");
				// String currentDateRef =
				// dateFormatWithMonthName.format(valueDate).toUpperCase();/* 01-JUN-2024 */
				md.addAttribute("formmode", "Pending");
				md.addAttribute("currentDate", valueDate);
				if (acces.equals("UNIT")) {
					String url = env.getProperty("ipsx.url") + "/api/UnitChargeBackListPending?merchant_id=" + merUserId + "&unit_id=" + UNITID;
					BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
					md.addAttribute("Back", response);
				} else {
					String url = env.getProperty("ipsx.url") + "/api/ChargeBackListPending?merchant_id=" + merUserId;
					BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
					md.addAttribute("Back", response);
				}
			} else {
				SimpleDateFormat dateFormatWithMonthName = new SimpleDateFormat("dd-MMM-yyyy");
				Date currentDate = new Date();
				String currentDateRef = dateFormatWithMonthName.format(currentDate).toUpperCase();/* 01-JUN-2024 */
				System.out.println("Current Date for Chargeback" + currentDateRef);
				md.addAttribute("formmode", "Pending");
				md.addAttribute("currentDate", currentDate);
				if (acces.equals("UNIT")) {
					String url = env.getProperty("ipsx.url") + "/api/UnitChargeBackListPendingForWeb?merchant_id=" + merUserId + "&unit_id=" + UNITID
							+ "&currentDate=" + currentDateRef;
					BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
					md.addAttribute("Back", response);
				} else {
					String url = env.getProperty("ipsx.url") + "/api/ChargeBackListPendingForWeb?merchant_id=" + merUserId + "&currentDate="
							+ currentDateRef;
					BIPS_Charge_Back_Entity[] response = restTemplate.getForObject(url, BIPS_Charge_Back_Entity[].class);
					md.addAttribute("Back", response);
				}
			}
		} else if (formmode.equals("addCharge")) {
			md.addAttribute("formmode", "addCharge");
			String url = env.getProperty("ipsx.url") + "/api/ChargebackForOne?message_ref=" + message_ref;
			BIPS_Outward_Trans_Monitoring_Entity response = restTemplate.getForObject(url, BIPS_Outward_Trans_Monitoring_Entity.class);
			md.addAttribute("viewcharge", response);
		}
		return "IPSChargeback.html";
	}

	@PostMapping("/ChargeInitiate")
	@ResponseBody
	public ResponseEntity<String> ChargeInitiate(@RequestParam String userid, @RequestParam String seqUniqueID, @RequestParam String merchant_id,
			HttpServletRequest req) {

		String msg = "";
		String updateUserUrl = env.getProperty("ipsx.url") + "api/InititedChargeBack?&userid=" + userid + "&seqUniqueID=" + seqUniqueID
				+ "&merchant_id=" + merchant_id;
		try {
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			ResponseEntity<String> response = restTemplate.postForEntity(updateUserUrl, HttpMethod.POST, String.class);
			if (response.getStatusCode().is2xxSuccessful()) {
				return ResponseEntity.ok("Initiated successfully!");
			} else {
				return ResponseEntity.status(response.getStatusCode()).body("Error verifying user.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			msg = "Exception occurred while verifying user";
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(msg);
		}
	}
}
