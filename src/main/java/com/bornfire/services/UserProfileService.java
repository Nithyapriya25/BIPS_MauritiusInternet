package com.bornfire.services;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.bornfire.configuration.SequenceGenerator;

@Service
@ConfigurationProperties("output")
@Transactional
public class UserProfileService {

	@Autowired
	SequenceGenerator sequence;

	@NotNull
	private String exportpath;

	@Autowired
	com.bornfire.configuration.Encryption Encryption;
	
	@Autowired
	Environment env;
	
	// @Value("${default.password}")
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getExportpath() {
		return exportpath;
	}

	public void setExportpath(String exportpath) {
		this.exportpath = exportpath;
	}
	
	public String changePasswords(String oldpass, String newpass, String userid) throws Exception {
	   
		String msg = "";
	    String url = UriComponentsBuilder.fromHttpUrl(env.getProperty("ipsx.url") + "api/changePasswordForWeb2FA")
	            .queryParam("oldpass", oldpass)
	            .queryParam("newpass", newpass)
	            .queryParam("userid", userid)
	            .toUriString();
	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        msg = restTemplate.getForObject(url, String.class);
	    } catch (RestClientException e) {
	        msg = "Error during password change: " + e.getMessage();
	    }
	    return msg;
	}
	
	public String changePasswordsDashboard(String oldpass, String newpass, String userid) throws Exception {
		   
		String msg = "";
	    String url = UriComponentsBuilder.fromHttpUrl(env.getProperty("ipsx.url") + "api/changePasswordForWeb")
	            .queryParam("oldpass", oldpass)
	            .queryParam("newpass", newpass)
	            .queryParam("userid", userid)
	            .toUriString();
	    try {
	        RestTemplate restTemplate = new RestTemplate();
	        msg = restTemplate.getForObject(url, String.class);
	    } catch (RestClientException e) {
	        msg = "Error during password change: " + e.getMessage();
	    }
	    return msg;
	}

}