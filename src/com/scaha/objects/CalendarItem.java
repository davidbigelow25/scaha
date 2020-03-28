package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class CalendarItem extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String eventname = null;
	private String eventdate = null;
	private String eventlocation = null;
	private Integer calendarid = null;
	private Integer order = null;
	
	public CalendarItem (){ 
		
	}
	
	public String getEventlocation(){
		return eventlocation;
	}
	
	public void setEventlocation(String sName){
		eventlocation = sName;
	}
	
	
	public String getEventdate(){
		return eventdate;
	}
	
	public void setEventdate(String sName){
		eventdate = sName;
	}
	
	
	public String getEventname(){
		return eventname;
	}
	
	public void setEventname(String sName){
		eventname = sName;
	}
	
	public Integer getCalendarid(){
		return calendarid;
	}
	
	public void setCalendarid(Integer sid){
		calendarid = sid;
	}
	
	public Integer getOrder(){
		return order;
	}
	
	public void setOrder(Integer sid){
		order = sid;
	}
	
		
}
