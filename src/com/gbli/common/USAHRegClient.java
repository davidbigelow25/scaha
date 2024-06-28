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
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	public static void main (String[] args) throws Exception  {
		
		UsaHockeyRegistration reg = USAHRegClient.pullRegistartionRecord("056000040TUART");
		System.out.println(reg.toString());
		
	}

	public USAHRegClient() {}

	public static UsaHockeyRegistration pullRegistartionRecord(String _strUSAH) throws Exception {
		LOGGER.info("starting pullregistartionrecord" + _strUSAH);

		//need to add code to handle aws environment calling a wrapper in old scaha site to connect to usa hockey
		//need to put in a boolean variable to indicate which environment it is and which code to run so as to not break backward compatability.
		HttpPost post = new HttpPost("https://uhua224.com/api/getconfirmation");
		post.addHeader("token", "tGvsmisD50XADm");
		LOGGER.info("finished setting up Http post");



		// add request parameter, form parameters
		LOGGER.info("setting up urlparameters");
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("confirmationNumber", _strUSAH));
		LOGGER.info("adding usa hockey number to urlparameters");

		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		LOGGER.info("added urlparameters to setentity");

		UsaHockeyRegistration myUSAHockey =  new UsaHockeyRegistration(0,_strUSAH);
		LOGGER.info("instatiating myUSAHockey");
		LOGGER.info("trying to make a successfull call");

		try (CloseableHttpClient httpClient = HttpClients.createDefault();
			 CloseableHttpResponse response = httpClient.execute(post)) {
			LOGGER.info("call was made and have response object");

			ObjectMapper objectMapper = new ObjectMapper();
			LOGGER.info("getting ready to read response object" + response);

			USAHockeyResponse usaResponse = objectMapper.readValue(response.getEntity().getContent(), USAHockeyResponse.class);
			//
			LOGGER.info("set usa response to value response");

			if ("Success".equals(usaResponse.status)) {
				LOGGER.info("response status was success");

				myUSAHockey.setUSAHnum(usaResponse.data.confirmation_number);
				LOGGER.info("loaded confirmation number");
				myUSAHockey.setFirstName(usaResponse.data.first_name);
				LOGGER.info("loaded first name");
				myUSAHockey.setMiddleInit(usaResponse.data.middle_initial);
				LOGGER.info("loaded middle initial");
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
				LOGGER.info("finished loading details to my usahockey");

			}
		}

		/*myUSAHockey.setUSAHnum("163501250FOSTE");
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
		LOGGER.info("returning my usa hockey");

		return myUSAHockey;

    }
}
