package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

//import com.gbli.common.SendMailSSL;


public class coachrosteractionBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private String selectedcoach = null;
	private String coachname = null;
	private String screeningexpires = null;
	private String cepnumber = null;
	private Integer ceplevel = null;
	private String cepexpires = null;
	private String u8 = null;
	private String u10 = null;
	private String u12 = null;
	private String u14 = null;
	private String u18 = null;
	private String girls = null;
	private List<String> cepmodulesselected = null;
	private String cepmodulesselectedstring = null;
	private String coachrole = null;
	private String safesport = null;
	private String sportexpires = null;
	private Boolean displaycoachcredentials = null;
	private Integer rosterid = null;
	private String page = null;
	private String firstname = null;
	private String lastname = null;
	private String searchcriteria = "";
	private String suspend = "";
	private String cepexpiresdisplayvalue = "";

	@PostConstruct
    public void init() {
	    //will need to load coach profile information for displaying certification details
    	
    	
    	HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("coachid") != null)
        {
    		setSelectedcoach(hsr.getParameter("coachid").toString());
        }
    	if(hsr.getParameter("rosterid") != null)
        {
    		setRosterid(Integer.parseInt(hsr.getParameter("rosterid").toString()));
        }
    	if(hsr.getParameter("page") != null)
        {
    		page = hsr.getParameter("page").toString();
        }else{
        	page = "";
        }
    	if(hsr.getParameter("search") != null)
        {
    		searchcriteria = hsr.getParameter("search").toString();
        }else{
        	searchcriteria = "";
        }
    	
    	loadCoachProfile(selectedcoach);

    	//doing anything else right here
    }

	public String getCepexpiresdisplayvalue(){
		return cepexpiresdisplayvalue;
	}

	public void setCepexpiresdisplayvalue(String cyear){
		cepexpiresdisplayvalue=cyear;
	}

	public String getSuspend(){
		return suspend;
	}

	public void setSuspend(String cyear){
		suspend=cyear;
	}

	public String getSportexpires(){
		return sportexpires;
	}
	
	public void setSportexpires(String cyear){
		sportexpires=cyear;
	}
	
	public String getCepmodulesselectedstring(){
		return cepmodulesselectedstring;
	}
	
	public void setCepmodulesselectedstring(String cyear){
		cepmodulesselectedstring=cyear;
	}
	
	public String getSearchcriteria(){
		return searchcriteria;
	}
	
	public void setSearchriteria(String cyear){
		searchcriteria=cyear;
	}
	
	public String getLastname(){
		return lastname;
	}
	
	public void setLastname(String cyear){
		lastname=cyear;
	}
	
	public String getFirstname(){
		return firstname;
	}
	
	public void setFirstname(String cyear){
		firstname=cyear;
	}
	
	public String getPage(){
		return page;
	}
	
	public void setPage(String cyear){
		page=cyear;
	}
    
	public void setRosterid(Integer id){
		rosterid = id;
	}
	
	public Integer getRosterid(){
		return rosterid;
	}
	
    public void setCepmodulesselected(List<String> selectedmodules){
    	cepmodulesselected = selectedmodules;
    }
    
    public List<String> getCepmodulesselected(){
    	return cepmodulesselected;
    }
    
    public void setDisplaycoachcredentials(String crole){
    	if (crole.equals("Manager")){
    		displaycoachcredentials = false;
    	}else {
    		displaycoachcredentials = true;
    	}
    	
    }
    
    public Boolean getDisplaycoachcredentials(){
    	return displaycoachcredentials;
    }
    
    public void setSafesport(String crole){
    	safesport = crole;
    }
    
    public String getSafesport(){
    	return safesport;
    }
    
    public void setCoachrole(String crole){
    	coachrole = crole;
    }
    
    public String getCoachrole(){
    	return coachrole;
    }
    
    public void setGirls(String snumber){
    	if (snumber.equals("0")){
    		girls = "No";
    	} else {
    		girls = "Yes";
    	}
		
    }
    
	public String getGirls(){
    	return girls;
    }
	
	public void setU18(String snumber){
    	if (snumber.equals("0")){
    		u18 = "No";
    	} else {
    		u18 = "Yes";
    	}
		
    }
    
	public String getU18(){
    	return u18;
    }
	
	public void setU14(String snumber){
    	if (snumber.equals("0")){
    		u14 = "No";
    	} else {
    		u14 = "Yes";
    	}
		
    }
    
	public String getU14(){
    	return u14;
    }
	
	public void setU12(String snumber){
    	if (snumber.equals("0")){
    		u12 = "No";
    	} else {
    		u12 = "Yes";
    	}
		
    }
    
	public String getU12(){
    	return u12;
    }
	
	public void setU10(String snumber){
    	if (snumber.equals("0")){
    		u10 = "No";
    	} else {
    		u10 = "Yes";
    	}
		
    }
    
	public String getU10(){
    	return u10;
    }
	public void setU8(String snumber){
    	if (snumber.equals("0")){
    		u8 = "No";
    	} else {
    		u8 = "Yes";
    	}
		
    }
    
	public String getU8(){
    	return u8;
    }
    
    public void setCepexpires(String snumber){
    	cepexpires = snumber;
    }
    
    public String getCepexpires(){
    	return cepexpires;
    }
    
    public void setCeplevel(Integer snumber){
    	ceplevel = snumber;
    }
    
    public Integer getCeplevel(){
    	return ceplevel;
    }
    
    public void setCepnumber(String snumber){
    	cepnumber = snumber;
    }
    
    public String getCepnumber(){
    	return cepnumber;
    }
    
    public void setScreeningexpires(String snumber){
    	screeningexpires = snumber;
    }
    
    public String getScreeningexpires(){
    	return screeningexpires;
    }
    
    
    public String getSelectedcoach(){
    	return selectedcoach;
    }
    
    public void setSelectedcoach(String selectedPlayer){
    	selectedcoach = selectedPlayer;
    }
    
    public String getCoachname(){
    	return coachname;
    }
    
    public void setCoachname(String playerName){
    	coachname = playerName;
    }
    
    
    
	//used to populate loi form with player information
	public void loadCoachProfile(String selectedplayer){
		//first get player detail information then get family members
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (!selectedplayer.equals("")) {
    		
    			Vector<Integer> v = new Vector<Integer>();
    			v.add(rosterid);
    			db.getData("CALL scaha.getCoachRostertypeByRosterId(?)", v);
    		    
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				while (rs.next()) {
    					coachrole = rs.getString("rostertype");
    				}
    				LOGGER.info("We have results for coach roster type by coach id");
    			}
    			rs.close();
    			setDisplaycoachcredentials(coachrole);
    			
    			Vector<Integer> ve = new Vector<Integer>();
    			ve.add(Integer.parseInt(selectedplayer));
    			db.getData("CALL scaha.getCoachInfoByCoachId(?)", ve);
    		    
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				
    				
    				while (rs.next()) {
    					coachname = rs.getString("fname") + ' ' + rs.getString("lname");
    					firstname = rs.getString("fname");
    					lastname = rs.getString("lname");
    					screeningexpires = rs.getString("screeningexpires");
        				cepnumber = rs.getString("cepnumber");
        				ceplevel = rs.getInt("ceplevel");
        				safesport = rs.getString("safesport");
        				cepexpires = rs.getString("cepexpires");
        				sportexpires = rs.getString("sportexpires");
        				suspend = rs.getString("suspended");

        				String templist = "";
        				u8 = rs.getString("eightu");
        				if (u8.equals("1")){
        					templist = templist.concat("8U");
        				}
        				u10 = rs.getString("tenu");
        				if (u10.equals("1")){
        					templist = templist.concat(",10U");
        				}
        				u12 = rs.getString("twelveu");
        				if (u12.equals("1")){
        					templist = templist.concat(",12U");
        				}
        				u14 = rs.getString("fourteenu");
        				if (u14.equals("1")){
        					templist = templist.concat(",14U");
        				}
        				u18 = rs.getString("eighteenu");
        				if (u18.equals("1")){
        					templist = templist.concat(",18U");
        				}
        				girls = rs.getString("girls");
        				if (girls.equals("1")){
        					templist = templist.concat(",Girls");
        				}
        			
        				setCepmodulesselectedstring(templist);
        			}
    				LOGGER.info("We have results for coach details by coach id");
    			}
    			rs.close();
    			db.cleanup();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading coach details");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
   }
	
	public void completeCoachDetails(){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("update coach details");
 				CallableStatement cs = db.prepareCall("CALL scaha.updateCoachbyCoachId(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    		    cs.setInt("coachid", Integer.parseInt(this.selectedcoach));
    		    cs.setString("screenexpires", this.screeningexpires);
    		    cs.setString("cepnum", this.cepnumber);
    		    cs.setInt("levelcep", this.ceplevel);
    		    if (this.cepexpiresdisplayvalue.equals("N/A")){
					cs.setString("cepexpire", this.cepexpires);
				} else {
    		    	this.cepexpires=this.cepexpiresdisplayvalue;
					cs.setString("cepexpire", this.cepexpires);
				}

    		    cs.setString("insafesport", this.safesport);
    		    cs.setString("inrostertype", this.coachrole);
    		    cs.setInt("inrosterid", this.rosterid);
				//need to set values for modules
    		    Integer u8 = 0;
    		    Integer u10 = 0;
    		    Integer u12 = 0;
    		    Integer u14 = 0;
    		    Integer u18 = 0;
    		    Integer ugirls = 0;
    		    
    		    this.cepmodulesselected = Arrays.asList(this.cepmodulesselectedstring.split(","));
    		    for (int i = 0; i < this.cepmodulesselected.size(); i++) {
    		    	if (this.cepmodulesselected.get(i).equalsIgnoreCase("8U")){
    		    		u8 = 1;
    		    	}
    		    	if (this.cepmodulesselected.get(i).equalsIgnoreCase("10U")){
    		    		u10 = 1;
    		    	}
    		    	if (this.cepmodulesselected.get(i).equalsIgnoreCase("12U")){
    		    		u12 = 1;
    		    	}
    		    	if (this.cepmodulesselected.get(i).equalsIgnoreCase("14U")){
    		    		u14 = 1;
    		    	}
    		    	if (this.cepmodulesselected.get(i).equalsIgnoreCase("18U")){
    		    		u18 = 1;
    		    	}
    		    	if (this.cepmodulesselected.get(i).equalsIgnoreCase("Girls")){
    		    		ugirls = 1;
    		    	}
				}
    		    
    		    cs.setInt("u8", u8);
    		    cs.setInt("u10", u10);
    		    cs.setInt("u12", u12);
    		    cs.setInt("u14", u14);
    		    cs.setInt("u18", u18);
    		    cs.setInt("ugirls", ugirls);
    		    cs.setString("infirstname",this.firstname);
    		    cs.setString("inlastname",this.lastname);
    		    cs.setString("sportexpires", this.sportexpires);
				if (this.suspend.equals("Y")){
					cs.setInt("issuspend_in", 1);
				} else {
					cs.setInt("issuspend_in", 0);
				}
				rs = cs.executeQuery();
    			
    		    db.commit();
    		    rs.close();
			
    		    db.cleanup();
 				
    		    //logging 
    			LOGGER.info("We are updating transfer info for coach id:" + this.selectedcoach);
    		    
    			
    			
    			FacesContext context = FacesContext.getCurrentInstance();
    			Application app = context.getApplication();
    			ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
    					"#{reviewcoachloiBean}", Object.class );

    			reviewcoachloiBean rclb = (reviewcoachloiBean) expression.getValue( context.getELContext() );
    	    	rclb.coachesDisplay();
    			
                context.addMessage(null, new FacesMessage("Successful", "You ave updated the Coach"));
                try{
                	
                	if (page.equals("quick")){
        				context.getExternalContext().redirect("quickcoachloiconfirm.xhtml");
                	}else if (page.equals("bcloi")){
        				context.getExternalContext().redirect("workwithcoaches.xhtml?search=" + this.searchcriteria);
        			}else{
        				context.getExternalContext().redirect("confirmcoachlois.xhtml");
        			}
        			
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedcoach);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
	}	
	
	public void Close(){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			if (page.equals("quick")){
				context.getExternalContext().redirect("quickcoachloiconfirm.xhtml");
			}else if (page.equals("bcloi")){
				context.getExternalContext().redirect("workwithcoaches.xhtml?search=" + this.searchcriteria);
			}else{
				context.getExternalContext().redirect("confirmcoachlois.xhtml");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void checkRole(){
		//need to set coach credential fields to hide when manager is selected.  For all others display.
		if (this.coachrole.equals("Manager")){
			this.displaycoachcredentials=false;
		}else {
			this.displaycoachcredentials=true;
		}
	}

	public void setExpireDateByLevel(){
		//need to set coach credential fields to hide when manager is selected.  For all others display.
		if (this.ceplevel==4 || this.ceplevel==5){
			this.cepexpiresdisplayvalue="N/A";
		}else {
			this.cepexpiresdisplayvalue=this.cepexpires;
		}
	}
}

