package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Coach extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private String CoachName = null;
	private String firstname = null;
	private String lastname = null;
	private String IDcoach = null;
	private String teamname = null;
	private String loidate = null;
	private String screeningexpires = null;
	private String cepnumber = null;
	private String ceplevel = null;
	private String cepexpires = null;
	private String u8 = null;
	private String u10 = null;
	private String u12 = null;
	private String u14 = null;
	private String u18 = null;
	private String girls = null;
	private String safesport = null;
	private Integer safesportforcoachlist = null;
	private String sportexpires = null;
	private String confirmed = null;
	private String usamembership = null;
	private String address = null;
	private String city = null;
	private String state = null;
	private String zip = null;
	private String phone = null;
	private String email1 = null;
	private String email2 = null;
	private String notes = null;
	private String eligibility = null;
	private String drop = null;
	private String added = null;
	private String active = null;
	private String updated = null;
	private String teamrole = null;
	private String suspended = null;
	private String cepmodulesselected = null;
	
	public Coach (){ 
		
	}

	public Integer getSafesportforcoachlist(){
		return safesportforcoachlist;
	}

	public void setSafesportforcoachlist(Integer fname){
		safesportforcoachlist=fname;
	}


	public String getCepmodulesselected(){
		return cepmodulesselected;
	}

	public void setCepmodulesselected(String fname){
		cepmodulesselected=fname;
	}

	public String getSuspended(){
		return suspended;
	}

	public void setSuspended(String fname){
		suspended=fname;
	}


	public String getSportexpires(){
    	return sportexpires;
    }
    
    public void setSportexpires(String fname){
    	sportexpires=fname;
    } 
	
	
	public String getTeamrole(){
    	return teamrole;
    }
    
    public void setTeamrole(String fname){
    	teamrole=fname;
    } 
	
    public String getUpdated(){
    	return updated;
    }
    
    public void setUpdated(String fname){
    	updated=fname;
    } 
	
	public String getActive(){
    	return active;
    }
    
    public void setActive(String fname){
    	active=fname;
    } 
	
	
	public String getAdded(){
    	return added;
    }
    
    public void setAdded(String fname){
    	added=fname;
    } 
	
	
	public String getDrop(){
    	return drop;
    }
    
    public void setDrop(String fname){
    	drop=fname;
    } 
	
	
	public String getEligibility(){
    	return eligibility;
    }
    
    public void setEligibility(String fname){
    	eligibility=fname;
    }
	
	public String getNotes(){
		return notes;
	}
	
	public void setNotes(String value){
		notes=value;
	}
	
	public String getAddress(){
		return address;
	}
	
	public void setAddress(String value){
		address=value;
	}
	
	
	public String getCity(){
		return city;
	}
	
	public void setCity(String value){
		city=value;
	}
	
	
	public String getState(){
		return state;
	}
	
	public void setState(String value){
		state=value;
	}
	
	
	public String getZip(){
		return zip;
	}
	
	public void setZip(String value){
		zip=value;
	}
	
	
	public String getPhone(){
		return phone;
	}
	
	public void setPhone(String value){
		phone=value;
	}
	
	
	public String getEmail1(){
		return email1;
	}
	
	public void setEmail1(String value){
		email1=value;
	}
	
	
	public String getEmail2(){
		return email2;
	}
	
	public void setEmail2(String value){
		email2=value;
	}
	
	public String getUsamembership(){
		return usamembership;
	}
	
	public void setUsamembership(String value){
		usamembership=value;
	}
	
	
	public void setSafesport(String ssafesport){
    	if (ssafesport.equals("1")){
    		safesport="Yes";
    	} else {
    		safesport="No";
    	}
		
		
    }
    
	
	public String getConfirmed(){
    	return confirmed;
    }
	
	public void setConfirmed(String snumber){
    	if (snumber.equals("0")){
    		confirmed = "No";
    	} else {
    		confirmed = "Yes";
    	}
		
    }
    
	
	public String getSafesport(){
    	return safesport;
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
    
	
	public void setScreeningexpires(String snumber){
    	screeningexpires = snumber;
    }
    
	public String getScreeningexpires(){
    	return screeningexpires;
    }
    
	
	public void setCepexpires(String snumber){
    	cepexpires = snumber;
    }
    
    public String getCepexpires(){
    	return cepexpires;
    }
    
    public void setCeplevel(String snumber){
    	if (snumber.equals("0")){
    		ceplevel = "None";
    	} else if (snumber.equals("1")){
    		ceplevel = "Level 1";
    	} else if (snumber.equals("2")){
    		ceplevel = "Level 2";
    	} else if (snumber.equals("3")){
    		ceplevel = "Level 3";
    	} else if (snumber.equals("4")){
    		ceplevel = "Level 4";
    	} else {
    		ceplevel = "None";
    	}
    	
    	ceplevel = snumber;
    }
    
    public String getCeplevel(){
    	return ceplevel;
    }
    
    public void setCepnumber(String snumber){
    	cepnumber = snumber;
    }
    
    public String getCepnumber(){
    	return cepnumber;
    }
    
	public String getFirstname(){
    	return firstname;
    }
    
    public void setFirstname(String fname){
    	firstname=fname;
    } 
	
    public String getLastname(){
    	return lastname;
    }
    
    public void setLastname(String lname){
    	lastname=lname;
    }
    
	
	public String getLoidate(){
		return loidate;
	}
	
	public void setLoidate(String sName){
		loidate = sName;
	}
	
	public String getIdcoach(){
		return IDcoach;
	}
	
	public void setIdcoach(String sName){
		IDcoach = sName;
	}
	
	public String getCoachname(){
		return CoachName;
	}
	
	public void setCoachname(String sName){
		CoachName = sName;
	}
	
	public String getTeamname(){
		return teamname;
	}
	
	public void setTeamname(String sName){
		teamname = sName;
	}
		
}
