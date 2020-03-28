package com.gbli.common;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;
import com.scaha.objects.UsaHockeyRegistration;



public class USAHRegClient {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	public static void main (String[] args) throws Exception  {
		
		UsaHockeyRegistration reg = USAHRegClient.pullRegistartionRecord("098401125HUMMI");
		System.out.println(reg.toString());
		
	}

	public static UsaHockeyRegistration pullRegistartionRecord(String _strUSAH) throws Exception {
		
		Simple3Des crypt = new Simple3Des();
 
		System.out.println("START:" + crypt.encryptText(_strUSAH));
		//LOGGER.info("(http://108.175.159.184) http://scahaservices.com/ReqUSAH.aspx?usah='" + crypt.encryptText(_strUSAH).replace("+", ":PLUS:") + "'");
		//URL url = new URL("http://108.175.159.184/ReqUSAH.aspx?usah='" + crypt.encryptText(_strUSAH).replace("+", ":PLUS:") + "'");
		URL url = new URL("http://scahaservices.com/ReqUSAH.aspx?usah='" + crypt.encryptText(_strUSAH).replace("+", ":PLUS:") + "'");
		LOGGER.info(url.toString());
		BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
		String inputLine = null;
		String answer = new String ();
		while ((inputLine = in.readLine()) != null) {
        	answer = answer + inputLine;
        }	
        in.close();
        
        String[] temp = answer.split("(<textarea name=\"TextArea1\" id=\"TextArea1\" cols=\"200\" rows=\"20\">{1})|(</textarea></body></html>{1})");
        String cyph = crypt.decryptText(temp[1]); 

        //LOGGER.info("HERE IS cyph:" + cyph);
        
        String [] ans = cyph.split("\\|");
        //LOGGER.info("HERE IS cyph after the split:  len=" + ans.length + ", ans=" + ans);
        UsaHockeyRegistration myUSAHockey =  new UsaHockeyRegistration(0,_strUSAH);
        int i = 2;
        if (ans.length > 2) {
	        myUSAHockey.setLastName(ans[i++]);
	        myUSAHockey.setFirstName(ans[i++]);
	        myUSAHockey.setMiddleInit(ans[i++]);
	        myUSAHockey.setAddress(ans[i++] + ans[i++]);
	        myUSAHockey.setCity(ans[i++]);
	        myUSAHockey.setState(ans[i++]);
	        myUSAHockey.setZipcode(ans[i++]);
	        myUSAHockey.setDOB(ans[i++]);
	        myUSAHockey.setCountry(ans[i++]);
	        myUSAHockey.setForZip(ans[i++]);
	        myUSAHockey.setCitizen(ans[i++]);
	        myUSAHockey.setGender(ans[i++]);
	        myUSAHockey.setHPhone(ans[i++]);
	        myUSAHockey.setBPhone(ans[i++]);
	        myUSAHockey.setPGSLName(ans[i++]);
	        myUSAHockey.setPGSFName(ans[i++]);
	        myUSAHockey.setPGSMName(ans[i++]);
	        myUSAHockey.setEmail(ans[i++]);
	        //LOGGER.info("Done Packing and creating the USAHockey Object");
        } else {
	        myUSAHockey.setLastName("");
	        myUSAHockey.setFirstName("");
	        myUSAHockey.setMiddleInit("");
	        myUSAHockey.setAddress("");
	        myUSAHockey.setCity("");
	        myUSAHockey.setState("");
	        myUSAHockey.setZipcode("");
	        myUSAHockey.setDOB("");
	        myUSAHockey.setCountry("");
	        myUSAHockey.setForZip("");
	        myUSAHockey.setCitizen("");
	        myUSAHockey.setGender("");
	        myUSAHockey.setHPhone("");
	        myUSAHockey.setBPhone("");
	        myUSAHockey.setPGSLName("");
	        myUSAHockey.setPGSFName("");
	        myUSAHockey.setPGSMName("");
	        myUSAHockey.setEmail("");
	        //LOGGER.info("Done Resetting the USA Hockey Object");
        	
        }

        return myUSAHockey;
        
		
 
    }
 
}
