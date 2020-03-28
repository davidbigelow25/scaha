package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Reason extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private String reasonname = null;
	private String reasonid = null;
	
	public Reason (){ 
		
		
	}
	
	public String getReasonname(){
		return reasonname;
	}
	
	public void setReasonname(String sName){
		reasonname = sName;
	}
	
	public String getReasonid(){
		return reasonid;
	}
	
	public void setReasonid(String sName){
		reasonid = sName;
	}
	
	
}
