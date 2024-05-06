package com.gbli.common;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;
import com.scaha.objects.UsaHockeyRegistration;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import com.fasterxml.jackson.databind.ObjectMapper;

public class USAHRegClient {
	
	private static final long serialVersionUID = 1L;
//	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	public static void main (String[] args) throws Exception  {
		
		UsaHockeyRegistration reg = USAHRegClient.pullRegistartionRecord("056000040TUART");
		System.out.println(reg.toString());
		
	}

	public USAHRegClient() {}

	public static UsaHockeyRegistration pullRegistartionRecord(String _strUSAH) throws Exception {

		HttpPost post = new HttpPost("https://uhua224.com/api/getconfirmation");
		post.addHeader("token", "tGvsmisD50XADm");

		// add request parameter, form parameters
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("confirmationNumber", _strUSAH));
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		UsaHockeyRegistration myUSAHockey =  new UsaHockeyRegistration(0,_strUSAH);
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
			 CloseableHttpResponse response = httpClient.execute(post)) {
			ObjectMapper objectMapper = new ObjectMapper();
			USAHockeyResponse usaResponse = objectMapper.readValue(response.getEntity().getContent(), USAHockeyResponse.class);
			//
			if ("Success".equals(usaResponse.status)) {
				myUSAHockey.setUSAHnum(usaResponse.data.confirmation_number);
				myUSAHockey.setFirstName(usaResponse.data.first_name);
				myUSAHockey.setMiddleInit(usaResponse.data.middle_initial);
				myUSAHockey.setLastName(usaResponse.data.last_name);
				myUSAHockey.setAddress(new String(usaResponse.data.address_1 + " " + usaResponse.data.address_2).trim());
				myUSAHockey.setCity(usaResponse.data.city);
				myUSAHockey.setZipcode(usaResponse.data.zip);
				myUSAHockey.setState(usaResponse.data.state);
				myUSAHockey.setCountry(usaResponse.data.country);
				myUSAHockey.setCitizen(usaResponse.data.citizenship);
				myUSAHockey.setForZip(usaResponse.data.for_zip);
				myUSAHockey.setDOB(usaResponse.data.dob);
				myUSAHockey.setGender(usaResponse.data.gender);
				myUSAHockey.setHPhone(usaResponse.data.h_phone);
				myUSAHockey.setBPhone(usaResponse.data.b_phone);
				myUSAHockey.setEmail(usaResponse.data.email);
				myUSAHockey.setPGSFName(usaResponse.data.PGSF_name);
				myUSAHockey.setPGSLName(usaResponse.data.PGSL_name);
				myUSAHockey.setPGSMName(usaResponse.data.PGSM_name);
			}
		}
/*
		myUSAHockey.setUSAHnum("119450831FOSTE");
		myUSAHockey.setFirstName("ROBERT");
		myUSAHockey.setMiddleInit("E");
		myUSAHockey.setLastName("FOSTER");
		myUSAHockey.setAddress("119 LAKE HARBOR DR");
		myUSAHockey.setCity("HENDERSONVILLE");
		myUSAHockey.setZipcode("370750000");
		myUSAHockey.setState("TN");
		myUSAHockey.setCountry("USA");
		myUSAHockey.setCitizen("USA");
		myUSAHockey.setForZip("");
		myUSAHockey.setDOB("10/25/1971");
		myUSAHockey.setGender("M");
		myUSAHockey.setHPhone("6613734323");
		myUSAHockey.setBPhone("6613734323");
		myUSAHockey.setEmail("LAHOCKEYFAN2@YAHOO.COM");
		myUSAHockey.setPGSFName("ROBERT");
		myUSAHockey.setPGSLName("FOSTER");
		myUSAHockey.setPGSMName("");
*/
		// lets return our answer
        return myUSAHockey;

    }
}
