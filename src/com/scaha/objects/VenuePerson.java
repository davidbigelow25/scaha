package com.scaha.objects;

import java.io.Serializable;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class VenuePerson extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private String clubname = null;
	private Integer idvenue = null;
	private String venuename = null;
	
	public VenuePerson (){ 
		
	}
	
	public String getClubname(){
		return clubname;
	}
	
	public void setClubname(String sName){
		clubname = sName;
	}
	
	public Integer getIdvenue(){
		return idvenue;
	}
	
	public void setIdvenue(Integer sid){
		idvenue = sid;
	}

	public String getVenuename(){
		return venuename;
	}
	
	public void setVenuename(String sName){
		venuename = sName;
	}
	
}
