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
 * Tryout is an individual tryout event.  
 * 
 * It simply holds their date, time, location, division, level, and coach for a given club.
 * @author rfoster
 *
 */
public class Tryout extends ScahaObject implements Serializable {
	
	
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String tryoutdate = null;
	private String tryouttime = null;
	private String division = null;
	private String level = null;
	private String location = null;
	private String coach = null;
	private Boolean usewebsite = null;
	private Integer tryoutid = null;
	
	public Tryout() {
	
	}
		/**
	 * @return the tryoutdate
	 */
	public String getTryoutdate() {
		return tryoutdate;
	}

	/**
	 * @param tryoutdate the tryoutdate to set
	 */
	public void setTryoutdate(String myvalue) {
		tryoutdate = myvalue;
	}
	
	/**
	 * @return the tryouttime
	 */
	public String getTryouttime() {
		return tryouttime;
	}

	/**
	 * @param tryouttime the tryouttime to set
	 */
	public void setTryouttime(String myvalue) {
		tryouttime = myvalue;
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
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String myvalue) {
		level = myvalue;
	}
	
	/**
	 * @return the location
	 */
	public String getLocation() {
		return location;
	}

	/**
	 * @param location the location to set
	 */
	public void setLocation(String myvalue) {
		location = myvalue;
	}
	
	/**
	 * @return the coach
	 */
	public String getCoach() {
		return coach;
	}

	/**
	 * @param tryouttime the tryouttime to set
	 */
	public void setCoach(String myvalue) {
		coach = myvalue;
	}
	
	/**
	 * @return the usewebsite //used for displaying a line in the table stating see website
	 */
	public Boolean getUsewebsite() {
		return usewebsite;
	}

	/**
	 * @param usewebsite the usewebsite to set
	 */
	public void setUsewebsite(Boolean myvalue) {
		usewebsite = myvalue;
	}
	
	/**
	 * @return the tryoutid
	 */
	public Integer getTryoutid() {
		return tryoutid;
	}

	/**
	 * @param tryoutid the tryoutid to set
	 */
	public void setTryoutid(Integer myvalue) {
		tryoutid = myvalue;
	}
}
