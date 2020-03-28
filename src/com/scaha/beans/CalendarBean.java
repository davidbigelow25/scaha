package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.CalendarItem;

public class CalendarBean implements Serializable{

	//
	// Class Level Variables
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private List<CalendarItem> calendaritems = null;
	transient private ResultSet rs = null;
	
	@PostConstruct
    public void init() {
		//lets load the calendar on instantiation
		loadCalendarList();
	}
	
	public List<CalendarItem> getCalendarItems() {
		return calendaritems;
	}
	
	public void setCalendarItems(List<CalendarItem> calendarlist) {
		calendaritems = calendarlist;
	}
	
	public void loadCalendarList() {
		List<CalendarItem> templist = new ArrayList<CalendarItem>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

			CallableStatement cs = db.prepareCall("CALL scaha.getCalendar()");
		    rs = cs.executeQuery();
			
			if (rs != null){
				Integer i =0;
				while (rs.next()) {
					i++;
					String idcalendar = rs.getString("idcalendar");
    				String eventname = rs.getString("eventname");
    				String eventdate = rs.getString("eventdate");
    				String eventlocation = rs.getString("eventlocation");
    				
    				CalendarItem ci = new CalendarItem();
    				ci.setCalendarid(Integer.parseInt(idcalendar));
    				ci.setEventname(eventname);
    				ci.setEventdate(eventdate);
    				ci.setEventlocation(eventlocation);
    				ci.setOrder(i);
    				templist.add(ci);
				}
				//LOGGER.info("We have results for calendar list");
			}
   			rs.close();
   			db.cleanup();
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading calendar");
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setCalendarItems(templist);
	 }
}
