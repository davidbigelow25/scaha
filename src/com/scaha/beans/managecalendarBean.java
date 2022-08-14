package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.CalendarDataModel;
import com.scaha.objects.CalendarItem;

//import com.gbli.common.SendMailSSL;


public class managecalendarBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private List<CalendarItem> calendaritems = null;
    private CalendarDataModel CalendarDataModel = null;
    private CalendarItem selectedcalendaritem = null;
    private String selectedcalendarid = null;
	
	
    @PostConstruct
    public void init() {
	    calendaritems = new ArrayList<CalendarItem>();  
        CalendarDataModel = new CalendarDataModel(calendaritems);
        
        calendarDisplay(); 
    }  
    
    public String getSelectedcalendarid(){
    	return selectedcalendarid;
    }
    
    public void setSelectedcalendarid(String selidcalendar){
    	selectedcalendarid=selidcalendar;
    }
    
    
    public CalendarItem getSelectedcalendaritem(){
		return selectedcalendaritem;
	}
	
	public void setSelectedcalendaritem(CalendarItem selectedCalendar){
		selectedcalendaritem = selectedCalendar;
	}
    
	public List<CalendarItem> getCalendaritems(){
		return calendaritems;
	}
	
	public void setCalendaritems(List<CalendarItem> list){
		calendaritems = list;
	}
	
	    
    //retrieves list of calendar items
    public void calendarDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<CalendarItem> tempresult = new ArrayList<CalendarItem>();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
				CallableStatement cs = db.prepareCall("CALL scaha.getCalendar()");
    			rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					String idcalendar = rs.getString("idcalendar");
        				String eventname = rs.getString("eventname");
        				String eventdate = rs.getString("eventdate");
        				String eventlocation = rs.getString("eventlocation");
        				
        				CalendarItem ci = new CalendarItem();
        				ci.setCalendarid(Integer.parseInt(idcalendar));
        				ci.setEventname(eventname);
        				ci.setEventdate(eventdate);
        				ci.setEventlocation(eventlocation);
        				tempresult.add(ci);
    				}
    				
    				LOGGER.info("We have results for calendar items");
    			}
    			rs.close();
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading calendar items");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	//setResults(tempresult);
    	setCalendaritems(tempresult);
    	CalendarDataModel = new CalendarDataModel(calendaritems);
    }

    public CalendarDataModel getCalendardatamodel(){
    	return CalendarDataModel;
    }
    
    public void setCalendardatamodel(CalendarDataModel odatamodel){
    	CalendarDataModel = odatamodel;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    public void deleteitem(CalendarItem citem){
    	//need to set to void
    	Integer calenderid = citem.getCalendarid();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("remove calendar item from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.deleteCalendar(?)");
    		    cs.setInt("calendarid", calenderid);
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have deleted the calendar item"));
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Deleting the Calendar Item");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		//now we need to reload the data object for the loi list
		calendarDisplay();
	}
    
    public void edititem(CalendarItem citem){
    	Integer calendarid = citem.getCalendarid();
    	FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("managecalendaritem.xhtml?id=" + calendarid.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void additem(){
    	
    	FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("managecalendaritem.xhtml?id=");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}

