package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class FamilyRow extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private String firstname = null;
	private String lastname = null;
	private String email = null;
	private String phone = null;
	private String relation = null;
	
	public FamilyRow (){ 
		
	}
	
	public String getFirstname(){
		return firstname;
	}
	
	public void setFirstname(String sName){
		firstname = sName;
	}
	
	public String getLastname(){
		return lastname;
	}
	
	public void setLastname(String sName){
		lastname = sName;
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String sName){
		email = sName;
	}
	
	public String getRelation(){
		return relation;
	}
	
	public void setRelation(String sName){
		relation = sName;
	}
	
	
	public String getPhone(){
		return phone;
	}
	
	public void setPhone(String sName){
		phone = sName;
	}
	
	
	
		
}
