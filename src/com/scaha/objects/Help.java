package com.scaha.objects;

import com.gbli.context.ContextManager;

import java.io.Serializable;
import java.util.logging.Logger;

public class Help extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String videourl = null;
	private String videourltext = null;
	private String helpdescription = null;
	private Integer alertid = null;

	public Help(){
		
	}
	
	public String getVideourl(){
		return videourl;
	}
	
	public void setVideourl(String sName){
		videourl = sName;
	}
	
	
	public String getVideourltext(){
		return videourltext;
	}
	
	public void setVideourltext(String sName){
		videourltext = sName;
	}
	
	
	public String getHelpdescription(){
		return helpdescription;
	}
	
	public void setHelpdescription(String sName){
		helpdescription = sName;
	}
	
	public Integer getAlertid(){
		return alertid;
	}
	
	public void setAlertid(Integer sid){
		alertid = sid;
	}
	

		
}
