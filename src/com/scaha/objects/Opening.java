/**
 * 
 */
package com.scaha.objects;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * 
 * Opening is an individual team opening .  
 * 
 * It simply holds their division, level, # of openings, contact name, contact email, host rink, for a given clubs opening.
 * @author rfoster
 *
 */
public class Opening extends ScahaObject implements Serializable {
	
	
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String division = null;
	private String skilllevel = null;
	private String openingcount = null;
	private String rink = null;
	private String contactname = null;
	private String contactemail = null;
	private Integer openingid = null;
	
	public Opening() {
	
	}
		/**
	 * @return the skillevel
	 */
	public String getSkilllevel() {
		return skilllevel;
	}

	/**
	 * @param skilllevel the skilllevel to set
	 */
	public void setSkilllevel(String myvalue) {
		skilllevel = myvalue;
	}
	
	/**
	 * @return the rink
	 */
	public String getRink() {
		return rink;
	}

	/**
	 * @param rink set
	 */
	public void setRink(String myvalue) {
		rink = myvalue;
	}
	
	/**
	 * @return the division
	 */
	public String getDivision() {
		return division;
	}

	/**
	 * @param division the division to set
	 */
	public void setDivision(String myvalue) {
		division = myvalue;
	}
	
	/**
	 * @return the openingcount
	 */
	public String getOpeningcount() {
		return openingcount;
	}

	/**
	 * @param openingcount the openingcount to set
	 */
	public void setOpeningcount(String myvalue) {
		openingcount = myvalue;
	}
	
	/**
	 * @return the contactname
	 */
	public String getContactname() {
		return contactname;
	}

	/**
	 * @param contactname the contactname to set
	 */
	public void setContactname(String myvalue) {
		contactname = myvalue;
	}
	
	/**
	 * @return the contact email
	 */
	public String getContactemail() {
		return contactemail;
	}

	/**
	 * @param contact email the contact email to set
	 */
	public void setContactemail(String myvalue) {
		contactemail = myvalue;
	}
	
		
	/**
	 * @return the openingid
	 */
	public Integer getOpeningid() {
		return openingid;
	}

	/**
	 * @param openingid the openingid to set
	 */
	public void setOpeningid(Integer myvalue) {
		openingid = myvalue;
	}
}
