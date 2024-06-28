package com.gbli.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scaha.objects.UsaHockeyRegistration;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.List;
import com.gbli.context.ContextManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.util.EntityUtils;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;

public class USAHRegClientforAWS {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	public static void main (String[] args) throws Exception  {

		UsaHockeyRegistration reg = USAHRegClient.pullRegistartionRecord("056000040TUART");
		System.out.println(reg.toString());

	}

	public USAHRegClientforAWS() {}

	public static UsaHockeyRegistration pullRegistartionRecord(String _strUSAH) throws Exception {

		//need to add code to handle aws environment calling a wrapper in old scaha site to connect to usa hockey
		//need to put in a boolean variable to indicate which environment it is and which code to run so as to not break backward compatability.
		HttpPost post = new HttpPost("http://192.241.229.21:8080/scaha/apis/GetUsaHockey");
		/*post.addHeader("username", "lahockeyfan2@yahoo.com");
		post.addHeader("password", "hockey22");*/


		// add request parameter, form parameters
		List<NameValuePair> urlParameters = new ArrayList<>();
		urlParameters.add(new BasicNameValuePair("USANumber", _strUSAH));
		post.setEntity(new UrlEncodedFormEntity(urlParameters));
		UsaHockeyRegistration myUSAHockey =  new UsaHockeyRegistration(0,_strUSAH);
		try (CloseableHttpClient httpClient = HttpClients.createDefault();
			 CloseableHttpResponse response = httpClient.execute(post)) {
			StatusLine statusLine = response.getStatusLine();

			HttpEntity responseBodyentity = response.getEntity();
			String responseBodyString = EntityUtils.toString(responseBodyentity);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode node = objectMapper.readTree(responseBodyString);


			//
			if (statusLine.getStatusCode()==200) {
				myUSAHockey.setUSAHnum(node.get("USAHnum").asText());
				myUSAHockey.setFirstName(node.get("FirstName").asText());
				myUSAHockey.setMiddleInit(node.get("MiddleInit").asText());
				myUSAHockey.setLastName(node.get("LastName").asText());
				myUSAHockey.setAddress(new String(node.get("Address").asText()).trim());
				myUSAHockey.setCity(node.get("City").asText());
				myUSAHockey.setZipcode(node.get("Zipcode").asText());
				myUSAHockey.setState(node.get("State").asText());
				myUSAHockey.setCountry(node.get("Country").asText());
				myUSAHockey.setCitizen(node.get("Citizen").asText());
				myUSAHockey.setForZip(node.get("ForZip").asText());
				myUSAHockey.setDOB(node.get("DOB").asText());
				myUSAHockey.setGender(node.get("Gender").asText());
				myUSAHockey.setHPhone(node.get("HPhone").asText());
				myUSAHockey.setBPhone(node.get("BPhone").asText());
				myUSAHockey.setEmail(node.get("Email").asText());
				myUSAHockey.setPGSFName(node.get("PGSFName").asText());
				myUSAHockey.setPGSLName(node.get("PGSLName").asText());
				myUSAHockey.setPGSMName(node.get("PGSMName").asText());
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
        return myUSAHockey;

    }
}
