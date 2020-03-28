/**
 * 
 */
package com.scaha.objects;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class Day extends ScahaObject implements Serializable {
	
	
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private Date dateofday = null;
	private String datemonth = "";
	private String dayofweek = "";
	private String datenumber = "";
	private String visible = "";
	private Boolean iscenternav = false;
	
	public Day() {
	
	}
	
	public Boolean getIscenternav() {
		return iscenternav;
	}

	public void setIscenternav(Boolean myvalue) {
		iscenternav = myvalue;
	}
	
	public String getVisible() {
		return visible;
	}

	public void setVisible(String myvalue) {
		visible = myvalue;
	}
	
	public String getDatenumber() {
		return datenumber;
	}

	public void setDatenumber(String myvalue) {
		datenumber = myvalue;
	}
	
	public String getDayofweek() {
		return dayofweek;
	}

	public void setDayofweek(String myvalue) {
		dayofweek = myvalue;
	}
	
	public String getDatemonth() {
		return datemonth;
	}

	public void setDatemonth(String myvalue) {
		datemonth = myvalue;
	}
	
	public Date getDateofday() {
		return dateofday;
	}

	public void setDateofday(Date myvalue) {
		dateofday = myvalue;
		
		//lets set the other parameters from this date
		if (dateofday != null){
			SimpleDateFormat f = new SimpleDateFormat("MMM");
			SimpleDateFormat d = new SimpleDateFormat("d");
			SimpleDateFormat e = new SimpleDateFormat("EEE");
			this.datemonth = f.format(dateofday);
			this.datenumber = d.format(dateofday);
			this.dayofweek = e.format(dateofday);
			
		}
	}
	
}
