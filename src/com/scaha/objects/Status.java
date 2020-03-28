package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Status extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private String statusname = null;
	private String statusid = null;
	
	public Status (){ 
		
		
	}
	
	public String getStatusname(){
		return statusname;
	}
	
	public void setStatusname(String sName){
		statusname = sName;
	}
	
	public String getStatusid(){
		return statusid;
	}
	
	public void setStatusid(String sName){
		statusid = sName;
	}
	
	
}
