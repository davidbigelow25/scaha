package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class RosterForPrint extends ScahaObject implements Serializable {
	
	//this class is used for the rosteredit by team managers and has a special method for setting property on jerseynumber
	//the method checks to see if new value being set is different than the value loaded from the db in the oldjerseynumber param
	
	
	private static final long serialVersionUID = 1L;
	private String firstname = null;
	private String lastname = null;
	private String IDplayer = null;
	private String jerseynumber = null;
	private String cepinfo = null;
	private Integer rosterid = null;
	private Boolean lgpid = null;
	private Boolean isgoalie = null;
	private Boolean playernameislong = null;
	
	public RosterForPrint (){ 
		
	}
	
	public Boolean getLgpid(){
    	return lgpid;
    }
    
    public void setLgpid(Boolean fname){
    	lgpid=fname;
    }	
    
    public Boolean getIsgoalie(){
    	return isgoalie;
    }
    
    public void setIsgoalie(Boolean fname){
    	isgoalie=fname;
    }	
    
	
    public Boolean getPlayernameislong(){
    	return playernameislong;
    }
    
    public void setPlayernameislong(Boolean fname){
    	playernameislong=fname;
    }	
    
    
	public Integer getRosterid(){
    	return rosterid;
    }
    
    public void setRosterid(Integer fname){
    		rosterid=fname;
    }	
    
	public String getCepinfo(){
    	return cepinfo;
    }
    
    public void setCepinfo(String fname){
    		cepinfo=fname;
    }	
    
	public String getJerseynumber(){
    	return jerseynumber;
    }
    
    public void setJerseynumber(String fname){
    		jerseynumber=fname;
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
    
	public String getIdplayer(){
		return IDplayer;
	}
	
	public void setIdplayer(String sName){
		IDplayer = sName;
	}
	
		
		
}
