package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.CalendarItem;

//import com.gbli.common.SendMailSSL;


public class managecalitemBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private String calendarid = null;
	private String startdate = null;
	private String enddate = null;
	private String eventname = null;
	private String eventlocation = null;
	
	
	@PostConstruct
    public void init() {
		//need to get item's calendar id if it exists
    	//will need to load player profile information for displaying loi
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("id") != null)
        {
    		calendarid = hsr.getParameter("id");
        }

        if (!calendarid.equals("")){
        	loadcalendarItem();
        }
    }  
    
    public String getEventname(){
    	return eventname;
    }
    
    public void setEventname(String sdate){
    	eventname = sdate;
    }
    
    public String getEventlocation(){
    	return eventlocation;
    }
    
    public void setEventlocation(String sdate){
    	eventlocation = sdate;
    }
    
    public String getEnddate(){
    	return enddate;
    }
    
    public void setEnddate(String sdate){
    	enddate = sdate;
    }
    
    
    public String getStartdate(){
    	return startdate;
    }
    
    public void setStartdate(String sdate){
    	startdate = sdate;
    }
    
    public String getCalendarid(){
    	return calendarid;
    }
    
    public void setCalendarid(String selidcalendar){
    	calendarid=selidcalendar;
    }
    
    
        
    //retrieves list of calendar items
    public void loadcalendarItem(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
				CallableStatement cs = db.prepareCall("CALL scaha.getCalendarItem(?)");
				cs.setInt("calendarid", Integer.parseInt(this.calendarid));
    			rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					this.eventname = rs.getString("eventname");
        				this.startdate = rs.getString("startdate");
        				this.enddate = rs.getString("enddate");
        				this.eventlocation = rs.getString("eventlocation");
        			}
    				//LOGGER.info("We have results for calendar item:" + this.calendarid.toString());
    			}
    			rs.close();
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading calendar item:" + this.calendarid.toString());
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    }

    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    public void saveItem(){
    	//need to set to void
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		if (this.calendarid.equals("")){
			this.calendarid="0";
		}
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("remove calendar item from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.updateCalendar(?,?,?,?,?)");
    		    cs.setInt("calendarid", Integer.parseInt(this.calendarid));
    		    cs.setString("startdate",this.startdate);
    		    cs.setString("enddate", this.enddate);
    		    cs.setString("eventname", this.eventname);
    		    cs.setString("eventlocation", this.eventlocation);
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    		    FacesContext context = FacesContext.getCurrentInstance();  
    		    context.addMessage(null, new FacesMessage("Successful", "You have added the calendar item"));
    		    
    		    Application app = context.getApplication();

    			ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
    					"#{managecalendarBean}", Object.class );

    			managecalendarBean mcb = (managecalendarBean) expression.getValue( context.getELContext() );
    	    	mcb.calendarDisplay();
    			
                try{
        			context.getExternalContext().redirect("managecalendar.xhtml");
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Adding the Calendar Item");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
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
}

