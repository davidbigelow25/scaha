package com.scaha.objects;

import java.io.Serializable;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class Playoff extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private String division = null;
	private String startdate = null;
	private String enddate = null;
	private String numberteams = null;
	private String formatheader = null;
	private String formatadvancement = null;
	private String formatgroups = null;
	private String location = null;
	
	public Playoff (){ 
		
	}
	
	
	public String getDivision(){
		return division;
	}
	
	public void setDivision(String sName){
		division = sName;
	}
	
	public String getStartdate(){
		return startdate;
	}
	
	public void setStartdate(String sid){
		startdate = sid;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String _tag) {
		enddate = _tag;
	}
	
	public void setNumberteams(String _tag) {
		numberteams = _tag;
	}
	
	public String getNumberteams() {
		return numberteams;
	}
	
	public void setFormatheader(String _tag) {
		formatheader = _tag;
	}
	
	public String getFormatheader() {
		return formatheader;
	}
	
	public void setFormatadvancement(String _tag) {
		formatadvancement = _tag;
	}
	
	public String getFormatadvancement() {
		return formatadvancement;
	}
	
	public void setFormatgroups(String _tag) {
		formatgroups = _tag;
	}
	
	public String getFormatgroups() {
		return formatgroups;
	}
	
	
	public void setLocation(String _tag) {
		location = _tag;
	}
	
	public String getLocation() {
		return location;
	}
}
