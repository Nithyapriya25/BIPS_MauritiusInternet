package com.bornfire.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.bornfire.entity.IPSAccessRole;

@Service
@ConfigurationProperties("output")
@Transactional
public class IPSAccessRoleService {

	@Autowired
	Environment env;

	@Autowired
	private RestTemplate restTemplate;
	
	public IPSAccessRole getRoleMenu(String id) {
		String url1 = env.getProperty("ipsx.url") + "api/getRoleMenu?" + "role_id="+ id;
		IPSAccessRole a=restTemplate.getForObject(url1, IPSAccessRole.class);
		return a;
	}

}