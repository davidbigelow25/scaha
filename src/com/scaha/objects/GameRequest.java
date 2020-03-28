package com.scaha.objects;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class GameRequest extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String teamname = null;
	private String gamedetails = null;
	private String reason = null;
	private String notes = null;
	
	
	public GameRequest (){ 
		
	}
	
	
	
	
	
	public String getTeamname(){
		return teamname;
	}
	
	public void setTeamname(String sName){
		teamname = sName;
	}
	
	public String getGamedetails(){
		return gamedetails;
	}
	
	public void setGamedetails(String sName){
		gamedetails = sName;
	}
	
	public String getReason(){
		return reason;
	}
	
	public void setReason(String sName){
		reason = sName;
	}
	
	public String getNotes(){
		return notes;
	}
	
	public void setNotes(String sName){
		notes = sName;
	}
	
	
}
