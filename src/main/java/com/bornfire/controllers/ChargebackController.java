package com.bornfire.controllers;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.bornfire.entity.BIPS_Outward_Trans_Monitoring_Entity;

@RestController
public class ChargebackController {

	@Autowired
	Environment env;

	@Autowired
	private RestTemplate restTemplate;

	@RequestMapping(value = "InitiateSingleDateforChargeback", method = RequestMethod.GET)
	public List<BIPS_Outward_Trans_Monitoring_Entity> InitiateSingleDateforChargeback(@RequestParam String tranRecord,
			@RequestParam(required = false) @DateTimeFormat(pattern = "dd-MM-yyyy") Date valueDate,
			HttpServletRequest req) {

		System.out.println("TransactionType & SysDate" + tranRecord + " : " + valueDate);
		String valueDateRef = new SimpleDateFormat("dd-MMM-yyyy").format(valueDate).toUpperCase();
		System.out.println("After conversion SysDate : " + valueDateRef);
		String merUserId = (String) req.getSession().getAttribute("MER_USER_ID");
		String UNITID = (String) req.getSession().getAttribute("UNITID");
		List<BIPS_Outward_Trans_Monitoring_Entity> records = new ArrayList<>();
		String url = env.getProperty("ipsx.url") + "/api/InitiateSingleDateforChargeback?merchant_id=" + merUserId
				+ "&ValueDate=" + valueDateRef + "&unit_id=" + UNITID;
		BIPS_Outward_Trans_Monitoring_Entity[] responseArray = restTemplate.getForObject(url,
				BIPS_Outward_Trans_Monitoring_Entity[].class);
		if (responseArray != null) {
			records = Arrays.asList(responseArray);
		}
		return records;
	}
	
}
