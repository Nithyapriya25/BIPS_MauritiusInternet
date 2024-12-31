package com.bornfire.configuration;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import org.springframework.stereotype.Component;

@Component
public class SequenceGenerator {
	
	private static final String CHAR_LIST = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	private static final int DOC_SEQ_ID = 5;
	private static final String NUM_LIST= "0123456789";
	private static final int MERCHANT_FEE = 7;
	private static final int MERCHANT_QR_P_ID = 6;


	private int getRandomNumber() {
		int randomInt = 0;
		Random randomGenerator = new Random();
		randomInt = randomGenerator.nextInt(NUM_LIST.length());

		if (randomInt - 1 == -1) {
			return randomInt;
		} else {
			return randomInt - 1;
		}
	}
	
	public String generateDocRefID() {

		StringBuffer randStr = new StringBuffer();
		randStr.append("CIM");
		randStr.append(new SimpleDateFormat("YYMMdd").format(new Date()));
		
		for (int i = 0; i < DOC_SEQ_ID; i++) {
			int number = getRandomNumber();
			char ch = NUM_LIST.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}
	
	public String generateMerchantQRPID() {
        // Define the date format pattern
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        
        // Set the time zone to the system's default time zone
        dateFormat.setTimeZone(TimeZone.getDefault());
        
        // Get the current date and time
        Date currentDate = new Date();
        
        // Format the current date and time as a string
        String pid = dateFormat.format(currentDate);
        
        // Return the generated PID
        return pid;
    }
	
	public String getRandom4Digit() {
        // Create an instance of Random
        Random random = new Random();
        
        // Generate a random number between 1000 and 9999
        int number = 1000 + random.nextInt(9000); // 9000 is used to ensure 4 digits
        
        // Convert the number to a string and return
        return Integer.toString(number);
    }
	
	public String generateMandateRefNo() {

		StringBuffer randStr = new StringBuffer();
		randStr.append("MAN");
		randStr.append(new SimpleDateFormat("YYMMdd").format(new Date()));
		
		for (int i = 0; i < DOC_SEQ_ID; i++) {
			int number = getRandomNumber();
			char ch = NUM_LIST.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}
	
	public String generateMerchantRefNo() {

		StringBuffer randStr = new StringBuffer();
		randStr.append("MER");
		randStr.append(new SimpleDateFormat("YYMMdd").format(new Date()));
		
		for (int i = 0; i < DOC_SEQ_ID; i++) {
			int number = getRandomNumber();
			char ch = NUM_LIST.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}
	
	
	
	public String generateMerchantFeeRefNo() {

		StringBuffer randStr = new StringBuffer();
		
		//randStr.append(new SimpleDateFormat("YYMMdd").format(new Date()));
		
		for (int i = 0; i < MERCHANT_FEE; i++) {
			int number = getRandomNumber();
			char ch = NUM_LIST.charAt(number);
			randStr.append(ch);
		}
		return randStr.toString();
	}
	
}


