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
public class Link extends ScahaObject implements Serializable {
	
	
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String linkurl = null;
	private String linklabel = null;
	private Integer linkid = null;
	
	public Link() {
	
	}
	
	public String getLinklabel() {
		return linklabel;
	}

	public void setLinklabel(String myvalue) {
		linklabel = myvalue;
	}
	
	public String getLinkurl() {
		return linkurl;
	}

	public void setLinkurl(String myvalue) {
		linkurl = myvalue;
	}
	
	public Integer getLinkid() {
		return linkid;
	}

	public void setLinkid(Integer myvalue) {
		linkid = myvalue;
	}
}
