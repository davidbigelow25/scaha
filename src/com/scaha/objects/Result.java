package com.scaha.objects;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class Result extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String playername = null;
	private String idplayer = null;
	private String address = null;
	private String dob = null;
	private String idcoach = null;
	private String coachname = null;
	private String currentteam = null;
	private String status = null;
	private Integer idperson = null;
	private String reportcard = null;
	private Integer idscholarathlete = null;
	private Boolean isapproved = null;
	private String state = null;
	private String city = null;
	private String zip = null;
	
	
	public Result (String sName, String playerid,String saddress, String sdob){ 
		
		playername = sName;
		idplayer = playerid;
		address = saddress;
		dob = sdob;
	
	}
	
	public Boolean getIsapproved(){
		return isapproved;
	}
	
	public void setIsapproved(Boolean value){
		isapproved = value;
	}
	public Integer getIdperson(){
		return idperson;
	}
	
	public void setIdperson(Integer sName){
		idperson = sName;
	}
	
	public Integer getIdscholarathlete(){
		return idscholarathlete;
	}
	
	public void setIdscholarathlete(Integer sName){
		idscholarathlete = sName;
	}
	
	
	public String getReportcard(){
		return reportcard;
	}
	
	public void setReportcard(String sName){
		reportcard = sName;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setStatus(String sName){
		status = sName;
	}
	
	public String getCurrentteam(){
		return currentteam;
	}
	
	public void setCurrentteam(String sName){
		currentteam = sName;
	}
	
	public String getPlayername(){
		return playername;
	}
	
	public void setPlayername(String sName){
		playername = sName;
	}
	
	public String getIdplayer(){
		return idplayer;
	}
	
	public void setIdplayer(String playerid){
		idplayer = playerid;
	}
	
	public String getAddress(){
		return address;
	}
	
	public void setAddress(String saddress){
		address = saddress;
	}
	
	public String getDob(){
		return dob;
	}
	
	public void setDob(String sdob){
		dob = sdob;
	}
	
	public String getCoachname(){
		return coachname;
	}
	
	public void setCoachname(String sName){
		coachname = sName;
	}
	
	public String getIdcoach(){
		return idcoach;
	}
	
	public void setIdcoach(String coachid){
		idcoach = coachid;
	}
	
	public String getCity(){
		return city;
	}
	
	public void setCity(String value){
		city = value;
	}
	
	public String getState(){
		return state;
	}
	
	public void setState(String value){
		state = value;
	}
	
	public String getZip(){
		return zip;
	}
	
	public void setZip(String value){
		zip = value;
	}
}
