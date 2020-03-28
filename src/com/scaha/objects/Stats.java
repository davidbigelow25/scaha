/**
 * 
 */
package com.scaha.objects;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

/**
 * 
 * Tryout is an individual tryout event.  
 * 
 * It simply holds their date, time, location, division, level, and coach for a given club.
 * @author rfoster
 *
 */
public class Stats extends ScahaObject implements Serializable {
	
	
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private List<Stat> statsyearlist = null;
	private String year=null;
	private String division=null;
	private Integer ID=null;
	
	public Stats() {
	
	}
	
	public void setId(Integer value){
		ID=value;
	}
	
	public Integer getId(){
		return ID;
	}
	
	public List<Stat> getStatsyearlist(){
		return statsyearlist;
	}
	
	public void setStatsyearlist(List<Stat> value){
		statsyearlist=value;
	}
	
	public String getYear(){
		return year;
	}
	
	public void setYear(String value){
		year=value;
	}
	
	public String getDivision(){
		return division;
	}
	
	public void setDivision(String value){
		division=value;
	}
	
	
	
}
