package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Release extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String idrelease = null;
	private String type = null;
	private String submitdate = null;
	private String firstname = null;
	private String lastname = null;
	private String validthru = null;
	private String releasingclub = null;
	private String acceptingclub = null;
	private String reason = null;
	private String status = null;
	
	public Release (){ 
		
	}
	
	public String getIdrelease(){
    	return idrelease;
    }
    
    public void setIdrelease(String fname){
    	idrelease=fname;
    }
    
	public String getStatus(){
    	return status;
    }
    
    public void setStatus(String fname){
    	status=fname;
    }
	
	public String getReason(){
    	return reason;
    }
    
    public void setReason(String fname){
    	reason=fname;
    }
	
	public String getAcceptingclub(){
    	return acceptingclub;
    }
    
    public void setAcceptingclub(String fname){
    	acceptingclub=fname;
    }
	
	public String getReleasingclub(){
    	return releasingclub;
    }
    
    public void setReleasingclub(String fname){
    	releasingclub=fname;
    }
	
	public String getType(){
    	return type;
    }
    
    public void setType(String fname){
    	type=fname;
    } 
	
    public String getSubmitdate(){
    	return submitdate;
    }
    
    public void setSubmitdate(String fname){
    	submitdate=fname;
    } 
	
    public String getValidthru(){
    	return validthru;
    }
    
    public void setValidthru(String fname){
    	validthru=fname;
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
    	
}
