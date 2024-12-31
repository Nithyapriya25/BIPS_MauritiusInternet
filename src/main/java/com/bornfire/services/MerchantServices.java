package com.bornfire.services;

import java.text.SimpleDateFormat;
import java.util.Date;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;

@Service
@ConfigurationProperties("output")

public class MerchantServices {

	private static final String NUM_LIST = "0123456789";

	private int getRandomMsgNumber() {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(NUM_LIST.length());

		if (randomInt - 1 == -1) {
			return randomInt;
		} else {
			return randomInt - 1;
		}
	}

	public String generateBIPSNumber() {

		StringBuffer randStr = new StringBuffer();
		randStr.append("BOB");
		randStr.append(new SimpleDateFormat("yyMMdd").format(new Date()));

		for (int i = 0; i < 6; i++) {
			int number = getRandomMsgNumber();
			char ch = NUM_LIST.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}
}

	
	

