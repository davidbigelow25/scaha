package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import org.primefaces.event.CellEditEvent;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Player extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private String PlayerName = null;
	private String firstname = null;
	private String lastname = null;
	private String idplayer = null;
	private String currentteam = null;
	private String previousteam = null;
	private String priorteam = null;
	private String citizenship = null;
	private Boolean citizenshiptransfer = null;
	private String citizenshipexpirationdate = null;
	private Boolean ctverified = null;
	private String birthcertificate = null;
	private Boolean bcverified = null;
	private String dob = null;
	private String loidate = null;
	private String playerup = null;
	private String citizenshiplabel = null;
	private String eligibility = null;
	private String drop = null;
	private String added = null;
	private String active = null;
	private String updated = null;
	private String jerseynumber = null;
	private String confirmed = null;
	private String rosterid = null;
	private String usamembership = null;
	private String parentname = null;
	private String address = null;
	private String city = null;
	private String state = null;
	private String zip = null;
	private String phone = null;
	private String email1 = null;
	private String email2 = null;
	private String notes = null;
	private String division = null;
	private String teamid = null;
	private String rostertype = null;
	private String gp = null;
	private String rosterdate = null;
	private String safesport = null;
	private String suspended = null;
	private Boolean bitalics = false;
	private Boolean bcovid = false;
	private String suspendedmessage = null;
	private String usaroster = null;
	private String isbullying = null;

	public Player (){ 
		
	}

	public String getUsaroster() {
		return usaroster;
	}

	public void setUsaroster(String usaroster) {
		this.usaroster = usaroster;
	}

	public String getSuspended(){
		return suspended;
	}

	public void setSuspended(String value){
		suspended=value;
	}

	public String getSuspendedmessage(){
		return suspendedmessage;
	}

	public void setSuspendedmessage(String value){
		suspendedmessage=value;
	}

	public String getSafesport(){
		return safesport;
	}
	
	public void setSafesport(String value){
		safesport=value;
	}
	
	
	
	public String getGp(){
		return gp;
	}
	
	public void setGp(String value){
		gp=value;
	}
	
	public String getRostertype(){
		return rostertype;
	}
	
	public void setRostertype(String value){
		rostertype=value;
	}
	
	public String getRosterdate(){
		return rosterdate;
	}
	
	public void setRosterdate(String value){
		rosterdate=value;
	}
	
	public String getDivision(){
		return division;
	}
	
	public void setDivision(String value){
		division=value;
	}
	
	public String getTeamid(){
		return teamid;
	}
	
	public void setTeamid(String value){
		teamid=value;
	}
	
	
	public String getNotes(){
		return notes;
	}
	
	public void setNotes(String value){
		notes=value;
	}
	
	public String getParentname(){
		return parentname;
	}
	
	public void setParentname(String value){
		parentname=value;
	}
	
	
	public String getAddress(){
		return address;
	}
	
	public void setAddress(String value){
		address=value;
	}
	
	
	public String getCity(){
		return city;
	}
	
	public void setCity(String value){
		city=value;
	}
	
	
	public String getState(){
		return state;
	}
	
	public void setState(String value){
		state=value;
	}
	
	
	public String getZip(){
		return zip;
	}
	
	public void setZip(String value){
		zip=value;
	}
	
	
	public String getPhone(){
		return phone;
	}
	
	public void setPhone(String value){
		phone=value;
	}
	
	
	public String getEmail1(){
		return email1;
	}
	
	public void setEmail1(String value){
		email1=value;
	}
	
	
	public String getEmail2(){
		return email2;
	}
	
	public void setEmail2(String value){
		email2=value;
	}
	
	public String getUsamembership(){
		return usamembership;
	}
	
	public void setUsamembership(String value){
		usamembership=value;
	}
	
	public String getRosterid(){
    	return rosterid;
    }
	
	public void setRosterid(String snumber){
    	rosterid = snumber;
    }
	
	
	public String getConfirmed(){
    	return confirmed;
    }
	
	public void setConfirmed(String snumber){
    	if (snumber.equals("0")){
    		confirmed = "No";
    	} else {
    		confirmed = "Yes";
    	}
		
    }
	
	public String getJerseynumber(){
    	return jerseynumber;
    }
    
    public void setJerseynumber(String fname){
    	jerseynumber=fname;
    } 
	
    public String getUpdated(){
    	return updated;
    }
    
    public void setUpdated(String fname){
    	updated=fname;
    } 
	
	public String getActive(){
    	return active;
    }
    
    public void setActive(String fname){
    	active=fname;
    } 
	
	
	public String getAdded(){
    	return added;
    }
    
    public void setAdded(String fname){
    	added=fname;
    } 
	
	
	public String getDrop(){
    	return drop;
    }
    
    public void setDrop(String fname){
    	drop=fname;
    } 
	
	
	public String getEligibility(){
    	return eligibility;
    }
    
    public void setEligibility(String fname){
    	eligibility=fname;
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
    
	public String getCitizenshiplabel(){
	    	return citizenshiplabel;
	    }
	    
	    public void setCitizenshiplabel(String selidplayer){
	    	citizenshiplabel=selidplayer;
	    }
	
	public String getCTExpirationdate(){
		return citizenshipexpirationdate;
	}
	
	public void setCTExpirationdate(String sname){
		citizenshipexpirationdate = sname;
	}
	
	public String getPlayerup(){
		return playerup;
	}
	
	public void setPlayerup(String sName){
		playerup = sName;
	}
	
	public String getLoidate(){
		return loidate;
	}
	
	public void setLoidate(String sName){
		loidate = sName;
	}
	
	public String getDob(){
		return dob;
	}
	
	public void setDob(String sName){
		dob = sName;
	}
	
	public String getBirthcertificate(){
		return birthcertificate;
	}
	
	public void setBirthcertificate(String sName){
		if (sName.equals("0")){
			birthcertificate = "Needed";
		} else {
			birthcertificate = "";
		}
		
	}

	public Boolean getBitalics(){
		return bitalics;
	}

	public void setBitalics(Boolean value){
		bitalics=value;
	}

	public Boolean getBcverified(){
		return bcverified;
	}
	
	public void setBcverified(String sName){
		if (sName.equals("0")){
			bcverified = false;
		} else {
			bcverified = true;
		}
		
	}
	
	public Boolean getCitizenshiptransfer(){
		return citizenshiptransfer;
	}
	
	public void setCitizenshiptransfer(String sName){
		if (sName!=null){
			if (sName.equals("0")){
				citizenshiptransfer = true;
			} else {
				citizenshiptransfer = false;
			}
		} else {
			citizenshiptransfer = true;
		}
		
		
	}
	
	public Boolean getCtverified(){
		return ctverified;
	}
	
	public void setCtverified(String sName){
		if (sName!=null){
			if (sName.equals("1")){
				ctverified = false;
			} else {
				ctverified = true;
			}
		} else {
			ctverified = false;
		}
		
		
	}
	
	
	public String getCitizenship(){
		return citizenship;
	}
	
	public void setCitizenship(String sName){
		citizenship = sName;
	}
	
	
	public String getIdplayer(){
		return idplayer;
	}
	
	public void setIdplayer(String sName){
		idplayer = sName;
	}
	
	public String getPlayername(){
		return PlayerName;
	}
	
	public void setPlayername(String sName){
		PlayerName = sName;
	}
	
	public String getCurrentteam(){
		return currentteam;
	}
	
	public void setCurrentteam(String sName){
		currentteam = sName;
	}
	
	public String getPreviousteam(){
		return previousteam;
	}
	
	public void setPreviousteam(String steamid){
		previousteam = steamid;
	}

	public String getPriorteam(){
		return priorteam;
	}

	public void setPriorteam(String steamid){
		priorteam = steamid;
	}

	public Boolean getBcovid() {return bcovid;}

	public void setBcovid(Boolean steamid) {bcovid=steamid;}

	public String getIsbullying() {
		return isbullying;
	}

	public void setIsbullying(String isbullying) {
		this.isbullying = isbullying;
	}
}
