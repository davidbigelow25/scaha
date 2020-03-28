package com.scaha.objects;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class Tournament extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private Integer idtournament = null;
	private String tournamentname = null;
	private String contact = null;
	private String dates = null;
	private String phone = null;
	private String location = null;
	private String website = null;
	private String status = null;
	private Boolean rendered = null;
	private String sanction = null;
	private String startdate = null;
	private String enddate = null;
	private String email = null;
	private List<Venue> venues = null;
	private List<TournamentDivision> divisions = null;
	private String requestingteam = null;
	
	public Tournament (){ 
		
	}
	
	public String getRequestingteam(){
		return requestingteam;
	}
	
	public void setRequestingteam(String value){
		requestingteam=value;
	}
	
	
	public String getEmail(){
		return email;
	}
	
	public void setEmail(String value){
		email=value;
	}
	
	public String getEnddate(){
		return enddate;
	}
	
	public void setEnddate(String value){
		enddate=value;
	}
	
	
	public List<Venue> getVenues(){
		return venues;
	}
	
	public void setVenues(List<Venue> list){
		venues = list;
	}
	
	public List<TournamentDivision> getDivisions(){
		return divisions;
	}
	
	public void setDivisions(List<TournamentDivision> list){
		divisions = list;
	}
	
	
	public String getStartdate(){
		return startdate;
	}
	
	public void setStartdate(String value){
		startdate=value;
	}
	
	public String getSanction(){
		return sanction;
	}
	
	public void setSanction(String value){
		sanction=value;
	}
	
	
	public Boolean getRendered(){
    	return rendered;
    }
    
    public void setRendered(Boolean fname){
    	rendered=fname;
    } 
	
	public Integer getIdtournament(){
    	return idtournament;
    }
    
    public void setIdtournament(Integer fname){
    	idtournament=fname;
    } 
	
    public String getTournamentname(){
    	return tournamentname;
    }
    
    public void setTournamentname(String fname){
    	tournamentname=fname;
    } 
	
	public String getContact(){
    	return contact;
    }
    
    public void setContact(String fname){
    	contact=fname;
    } 
	
	
	public String getDates(){
    	return dates;
    }
    
    public void setDates(String fname){
    	dates=fname;
    } 
	
	
	public String getPhone(){
    	return phone;
    }
    
    public void setPhone(String fname){
    	phone=fname;
    } 
	
	
	public String getLocation(){
    	return location;
    }
    
    public void setLocation(String fname){
    	location=fname;
    } 
	
	
	public String getWebsite(){
    	return website;
    }
    
    public void setWebsite(String fname){
    	website=fname;
    }
    
    public String getStatus(){
    	return status;
    }
    
    public void setStatus(String fname){
    	status=fname;
    }
	    	
}
