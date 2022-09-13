package com.gbli.common;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.gbli.connectors.ScahaDatabase;
import org.apache.commons.codec.binary.Base64;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

/**
 * This is as general holder for general attributes..
 * Its a Key followed by a Value
 * @author David
 *
 */
public class ApisAuthentication extends Hashtable<String,String> implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public Boolean AuthenticateRequest(HttpServletRequest request, ScahaDatabase db, ResultSet rs){

		String authorizationHeader = request.getHeader("Authorization");
		// Authorization: Basic base64credentials
		String base64Credentials = authorizationHeader.substring("Basic".length()).trim();
		Base64 b = new Base64();
		String credentials = new String(b.decode(base64Credentials), Charset.forName("UTF-8"));
		// credentials = username:password
		final String[] values = credentials.split(":", 2);
		String userName = values[0];
		String password = values[1];

		Boolean authenticated = false;

		try{
			//lets plug in authentication query right here
			CallableStatement cs = db.prepareCall("CALL scaha.verifyforapi(?,?)");
			cs.setString(1,userName);
			cs.setString(2,password);
			rs = cs.executeQuery();
			if (rs != null) {
				while (rs.next()) {
					authenticated = rs.getBoolean(1);
				}
			}


			cs.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			/*LOGGER.info("ERROR IN getting suspension list");*/
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		return authenticated;

	}

}
