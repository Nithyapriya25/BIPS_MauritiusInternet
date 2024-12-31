package com.bornfire;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
@ComponentScan(basePackages = { "com.bornfire", "com.bornfire.entity", "com.bornfire.controllers",
		"com.bornfire.services" })
public class IpsAdminApplication {

	public static void main(String[] args) {
		SpringApplication.run(IpsAdminApplication.class, args);
	}

}