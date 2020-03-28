package com.scaha.objects;

import java.io.Serializable;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class EmailGroup extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private String displayname = null;
	private Integer idemailgroup = null;
	private Integer divisionid = null;
	private Integer skilllevelid = null;
	private String email = null;
	
	public EmailGroup (){ 
		
	}
	
	public String getDisplayname(){
		return displayname;
	}
	
	public void setDisplayname(String sName){
		displayname = sName;
	}
	
	public Integer getDivisionid(){
		return divisionid;
	}
	
	public void setDivisionid(Integer sid){
		divisionid = sid;
	}

	public Integer getSkilllevelid(){
		return skilllevelid;
	}
	
	public void setSkilllevelid(Integer sid){
		skilllevelid = sid;
	}

	public Integer getIdemailgroup(){
		return idemailgroup;
	}
	
	public void setIdemailgroup(Integer sid){
		idemailgroup = sid;
	}
	
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String sName){
		email = sName;
	}
	
}
