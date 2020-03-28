package com.scaha.objects;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class Suspension extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private Integer idsuspension = null;
	private String playername = null;
	private String match = null;
	private String team = null;
	private String infraction = null;
	private String games = null;
	private String eligibility = null;
	private String suspensiondate = null;
	private String served = null;
	
	
	public Suspension (){ 
		
	}
	
	public Integer getIdsuspension(){
		return idsuspension;
	}
	
	public void setIdsuspension(Integer value){
		idsuspension=value;
	}
	
	
	public String getPlayername(){
		return playername;
	}
	
	public void setPlayername(String value){
		playername=value;
	}
	
	public String getServed(){
		return served;
	}
	
	public void setServed(String value){
		served=value;
	}
	
	
	public String getMatch(){
		return match;
	}
	
	public void setMatch(String value){
		match=value;
	}
	
	
	public String getTeam(){
		return team;
	}
	
	public void setTeam(String value){
		team = value;
	}
	
	public String getInfraction(){
		return infraction;
	}
	
	public void setInfraction(String value){
		infraction=value;
	}
	
	public String getGames(){
		return games;
	}
	
	public void setGames(String value){
		games=value;
	}
	
	
	public String getEligibility(){
    	return eligibility;
    }
    
    public void setEligibility(String value){
    	eligibility=value;
    } 
	
    public String getSuspensiondate(){
    	return suspensiondate;
    }
    
    public void setSuspensiondate(String value){
    	suspensiondate=value;
    }
}
