package com.scaha.apis;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.logging.Logger;
import com.gbli.context.ContextManager;



/**
 * This is as general holder for general attributes..
 * Its a Key followed by a Value
 * @author David
 *
 */
public class ApisAuthenticationUSAHockey extends Hashtable<String,String> implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public Boolean AuthenticateRequest(HttpServletRequest request){
		LOGGER.info("requesting authorization in header");
		/*String authorizationHeader = request.getHeader("Authorization");
		// Authorization: Basic base64credentials
		LOGGER.info("loading base64credentials");
		String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
		LOGGER.info("loading base64");
		Base64 b = new Base64();
		LOGGER.info("converting base 64 to credentials string");
		String credentials = new String(b.decode(base64Credentials), Charset.forName("UTF-8"));
		// credentials = username:password
		final String[] values = credentials.split(":", 2);
		String userName = values[0];
		String password = values[1];

		LOGGER.info("we have credentials for username:" + userName + ",password:" + password);*/
		Boolean authenticated = false;
		//if (userName.equals("lahockeyfan2@yahoo.com") && password.equals("hockey22")){
			authenticated = true;
		//}
		LOGGER.info("setting authenticated value" + authenticated);
		return authenticated;

	}

}
