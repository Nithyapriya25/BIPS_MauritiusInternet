package com.bornfire;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Enumeration;
import java.util.regex.Pattern;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

public class main {

	public static void main(String[] args) throws IOException {
		String dataFormat = null;
	
			dataFormat=new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss'Z'").format(new Date());
		
			String cc="33||45||||||";
            String[] st1 = cc.trim().split(Pattern.quote("||"));

          //  System.out.println(String.valueOf(st1.length));
			//System.out.println(String.valueOf(cc.split(Pattern.quote("||"))[3]));
			
			String dataa="100.0";
			
			//BigDecimal data1=100.00;
			
			if("100.00".contains(dataa)) {
				System.out.println("ok");
			}else {
				System.out.println("not");
			}
			

			
			
		XMLGregorianCalendar xgc = null;
		try {
			/*
			 * xgc = DatatypeFactory.newInstance() .newXMLGregorianCalendar(dataFormat);
			 * String base64Credentials =
			 * "Basic QVBJX0dXOjE=".substring("Basic".length()).trim(); byte[] credDecoded =
			 * Base64.getDecoder().decode(base64Credentials); String credentials = new
			 * String(credDecoded, StandardCharsets.UTF_8); System.out.println(credentials);
			 * // credentials = username:password final String[] values =
			 * credentials.split(":", 2); System.out.println(values[0]);
			 * System.out.println(values[1]);
			 * 
			 * String fileName = "C:\\Users\\Administrator\\Desktop\\020920200803001.txt";
			 * File file = new File(fileName); FileReader fr = new FileReader(file);
			 * BufferedReader br = new BufferedReader(fr);
			 * 
			 * String line;
			 * 
			 * int count=0; while((line = br.readLine()) != null){
			 * 
			 * if(count==0) { System.out.println("Header : "+line);
			 * System.out.println("Header Length: "+line.length()); if(line.length()==38) {
			 * 
			 * String receiving_bank=line.substring(1,3).trim(); String
			 * send_bank=line.substring(3,5).trim(); String
			 * file_seq=line.substring(13,16).trim(); String
			 * currency=line.substring(16,19).trim(); String
			 * tot_rec=line.substring(19,24).trim(); String
			 * tot_amount=line.substring(24,37).trim(); String
			 * type=line.substring(37,38).trim();
			 * 
			 * System.out.println("receiving_bank : "+receiving_bank);
			 * System.out.println("send_bank : "+send_bank);
			 * System.out.println("FileNameSeq : "+file_seq);
			 * System.out.println("currency : "+currency);
			 * System.out.println("tot_rec : "+tot_rec);
			 * System.out.println("tot_amount : "+tot_amount);
			 * System.out.println("type : "+type);
			 * 
			 * } }else { System.out.println("Body : "+line);
			 * 
			 * String ref_no=line.substring(0,8).trim(); String
			 * trDate=line.substring(18,26).trim(); String
			 * payeeAcctNumber=line.substring(26,44).trim(); String
			 * payeeReference=line.substring(44,49).trim(); String
			 * payeeName=line.substring(49,89).trim(); String
			 * payeeRef=line.substring(89,119).trim(); String
			 * benAcctNumber=line.substring(119,137).trim(); String
			 * benName=line.substring(137,177).trim(); String
			 * amount=line.substring(177,189).trim();
			 * 
			 * System.out.println("Ref Num :"+ref_no);
			 * System.out.println("Tr Date :"+trDate);
			 * System.out.println("payeeAcctNumber :"+payeeAcctNumber);
			 * System.out.println("payeeReference :"+payeeReference);
			 * System.out.println("payeeName :"+payeeName);
			 * System.out.println("payeeRef :"+payeeRef);
			 * System.out.println("benAcctNumber :"+benAcctNumber);
			 * System.out.println("benName :"+benName);
			 * System.out.println("amount :"+amount);
			 * 
			 * System.out.println("------------"); } count++; }
			 * 
			 * 
			 * System.out.println(xgc.toGregorianCalendar().getTime());
			 */
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}