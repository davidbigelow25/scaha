package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Season extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	public String seasonname = null;
	private String seasonid = null;
	
	public Season (){ 
	
	}
	
	public String getSeasonname(){
		return seasonname;
	}
	
	public void setSeasonname(String sName){
		seasonname = sName;
	}
	
	public String getSeasonid(){
		return seasonid;
	}
	
	public void setSeasonid(String value){
		seasonid = value;
	}
		
}
