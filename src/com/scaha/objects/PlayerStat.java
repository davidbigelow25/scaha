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
 * Link is an individual link.  
 * 
 * It simply holds their url, and label for a given link.
 * @author rfoster
 *
 */
public class PlayerStat extends ScahaObject implements Serializable {
	
	
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String teamname = null;
	private String gp = null;
	private String pims = null;
	private String penalties = null;
	private String gmcount = null;
	
	public PlayerStat() {
	
	}
	
	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String myvalue) {
		teamname = myvalue;
	}
	
	public String getGp() {
		return gp;
	}

	public void setGp(String myvalue) {
		gp = myvalue;
	}
	
	public String getPenalties() {
		return penalties;
	}

	public void setPenalties(String myvalue) {
		penalties = myvalue;
	}

	public String getPims() {
		return pims;
	}

	public void setPims(String myvalue) {
		pims = myvalue;
	}
	
	public String getGmcount() {
		return gmcount;
	}

	public void setGmcount(String myvalue) {
		gmcount = myvalue;
	}
	
	
}
