package com.bornfire.entity;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class SettlementFileGeneration {

	
	public static void main(String[] args) {
		 int totalNumbers = 10;
		StringBuilder sthd = new StringBuilder();
		 for (int i = 1; i <= totalNumbers; i++) {
			 sthd.append("HD");
	            String orderedNumber = String.format("%019d", i);
	            sthd.append(orderedNumber);
	            
	            String Bussinessdate = new SimpleDateFormat("yyyyMMdd").format(new Date());
	            sthd.append(Bussinessdate);
	            String Bussinessdatetime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
	            sthd.append(Bussinessdatetime);
	            sthd.append("3.0.1\n");
	        }
		 
		 
		 System.out.println(sthd);
		 
		 StringBuilder stdt = new StringBuilder();
			String bankcode= "1";
			String accountingentry= "001";
			String accountingbatch = "228133";
			String Seqaccentry = "2334974";
			 for (int i = 2; i <= totalNumbers; i++) {
				 stdt.append("DT");///Record Type

		            String orderedNumber = String.format("%019d", i);
		            stdt.append(orderedNumber);///Record Sequence
		            
		            stdt.append(String.format("%06d", Integer.valueOf(bankcode)));//Bank Code
		            stdt.append("A00001");///Operation Code
		            stdt.append(accountingentry);//Operation Sequence
		            stdt.append(String.format("%08d", Integer.valueOf(accountingbatch)));//Settlement Account Cutoff Id

		            String Sequenceaccentry = String.format("%08d", Integer.valueOf(Seqaccentry));
		            String spaces = "";
		            for (int j = 0; j < 32; j++) {
		                spaces += " ";
		            }
		            stdt.append(Sequenceaccentry+spaces);///GL Reference
		            String Merchantid = "100000000011782";
		            String Space2 = "";
		            for(int a=0;a< 497;a++) {
		            	Space2+=" ";
		            }
		            stdt.append(Merchantid+Space2);///Reference Of Document
		            
		            //Entry Label
		            String Descrp = "Sales MC MUR - Non Cim Counters";
		            String space3 = "";
		            for(int b=0;b< 49;b++) {
		            	space3+=" ";
		            }
		            stdt.append(Descrp+space3);///Entry Label
		           
		           ////Posting Date
		            String julianDate = GenerateJulianDate();
		            stdt.append(julianDate);
		            ///Entry Date
					String julianDate1 = GenerateJulianDate();
					stdt.append(julianDate1);
		            
		            ///Entry Account Number
		            String Navisioncode = "273140";
		            String spaces4 ="";
		            for(int c = 0; c< 18; c++) {
		            	spaces4+=" ";
		            }
		            stdt.append(Navisioncode+spaces4);///Entry Account Number
		            stdt.append("1 ");
		            ///Amount of the accounting entry
		            int amount = Integer.valueOf("550");
		            String amountaccentry = String.format("%018d", amount);
		            stdt.append(amountaccentry);///Entry Key Account
		            
		            ///Entry Sign
		            stdt.append("C");
		            
		            ///ISO Currencycode
		            String currcode = "480";
		            stdt.append(currcode);
		            
		            ///Flag indicating if the accounting entry was posted or not:
		            stdt.append("N");
		            
		            ///The Business date used to generate the accounting entry.
					String BussjulianDate = GenerateJulianDate();
					stdt.append(BussjulianDate);
		            
		            ///Code of the Card Management System data source from which the transaction originates
		            String CCManagesys = "PMATF";
		            stdt.append(CCManagesys);
		            
		            ///6 digit code related to the issuing bank for which the accounting entry is generated
		            String issuingbank ="000003";
		            stdt.append(issuingbank);
		            
		            ///6 digit code related to the acquiring bank for which the accounting entry is generated
		            String acquiringbank = "000001";
		            stdt.append(acquiringbank);
		            
		            ////2 digit code representing the network from which the transaction originates (Visa, MasterCard, etc.).
		            String Mastercardnum = "02";
		            stdt.append(Mastercardnum);
		            
		            //23-digit identification number assigned by the acquirer or the issuer to every transaction.
		            
		            String Identificationnum = "72303513106100315289427";
		            stdt.append(Identificationnum);
		            
		            ///A 4-digit sequence number assigned to the transaction. IS it for grouping of transaction
		            String grpoftrn = "0002";
		            stdt.append(grpoftrn);
		            
		            ///Label of the destination account where the accounting entry shall be posted.
		            String Labelofdest = "VAT ACCOUNT";
		            
		            if(Labelofdest.length()>32) {
		            	stdt.append(Labelofdest.subSequence(0, 32));
		            }else {
		            	int Labellen = Labelofdest.length();
		            	
		            	int val = 32-Labellen;
		            	
		            	String space5 = "";
		            	for(int d=0; d<val;d++) {
		            		space5+=" ";
		            	}
		            	stdt.append(Labelofdest+space5);
		            }
		            
		            ///Operation Date Label
		            String OperationDateLabel = "BUSINESS DATE";
		            
		            if(OperationDateLabel.length()>32) {
		            	stdt.append(OperationDateLabel.subSequence(0, 32));
		            }else {
		            	int Labellen1 = OperationDateLabel.length();
		            	
		            	int val1 = 32-Labellen1;
		            	
		            	String space6 = "";
		            	for(int e=0; e<val1;e++) {
		            		space6+=" ";
		            	}
		            	stdt.append(OperationDateLabel+space6);
		            }
		            
		            ////Basic Source Label
		            String BasicSourceLabel = "VAT";
		            if(BasicSourceLabel.length()>32) {
		            	stdt.append(BasicSourceLabel.subSequence(0, 32));
		            }else {
		            	int Labellen2 = BasicSourceLabel.length();
		            	
		            	int val2 = 32-Labellen2;
		            	
		            	String space7 = "";
		            	for(int f=0; f<val2;f++) {
		            		space7+=" ";
		            	}
		            	stdt.append(BasicSourceLabel+space7);
		            }
		            
		            ////Operation Grouping Code
		            stdt.append("GL");
		            
		            ////Conversion Rate
		            stdt.append("000000000000000000");
		            
		            ///Operation Grouping Criteria
		            String spaces8 = "";
		            for(int g=0; g<512;g++) {
		            	spaces8 += " ";
		            }
		            stdt.append(spaces8);
		            
		            ////Source Amount
		            stdt.append(amountaccentry);
		            ////Source Currency
		            stdt.append(currcode+"\n");
		        }
			 System.out.println(stdt);
			
			 
			 StringBuilder sttr = new StringBuilder();
				sttr.append("TR"); /// Record Type
						
						///Record Sequence
						int RecSequ = Integer.valueOf("16580");
			            String RecordSequence = String.format("%019d", RecSequ);
			            sttr.append(RecordSequence);
			            
			            ///Record Count
						int Reccount = Integer.valueOf("16580");
			            String RecordCount = String.format("%019d", Reccount);
			            sttr.append(RecordCount);
			            
			            ///Total debit amount 
			            int Totdebamt = Integer.valueOf("1053695803");
			            String Totaldebitamount = String.format("%018d", Totdebamt);
			            sttr.append(Totaldebitamount);
			            
			            ///Total credit amount
			            int Totcreditamt = Integer.valueOf("1053695803");
			            String Totalcreditamount = String.format("%018d", Totcreditamt);
			            sttr.append(Totalcreditamount);
			       
				 System.out.println(sttr);
		}

	
	

		
	// Function to generate the Julian date
   public static String GenerateJulianDate() {
		 // Create a LocalDate for the date you want to convert
           LocalDate newDate = LocalDate.now();
           // Get the year and day of the year
           int year = newDate.getYear();
           int dayOfYear = newDate.getDayOfYear();
           // Format the Julian date with the desired format
           String julianDate = String.format("%d%03d", year, dayOfYear);
			return julianDate;
	 }
	


}
