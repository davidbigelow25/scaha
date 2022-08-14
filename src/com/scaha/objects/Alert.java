package com.scaha.objects;

import com.gbli.context.ContextManager;

import java.io.Serializable;
import java.util.logging.Logger;

public class Alert extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String videourl = null;
	private String videourltext = null;
	private String taskdescription = null;
	private Integer alertid = null;

	public Alert(){
		
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
	
	
	public String getTaskdescription(){
		return taskdescription;
	}
	
	public void setTaskdescription(String sName){
		taskdescription = sName;
	}
	
	public Integer getAlertid(){
		return alertid;
	}
	
	public void setAlertid(Integer sid){
		alertid = sid;
	}
	

		
}
