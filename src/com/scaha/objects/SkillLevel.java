package com.scaha.objects;

import java.io.Serializable;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class SkillLevel extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private String skilllevelname = null;
	private Integer idskilllevel = null;
	private String tag = null;
	
	public SkillLevel (){ 
		
	}
	
	public SkillLevel (Profile _pro, int _id) {
		ID = _id;
		setProfile(_pro);
	}
	
	public String getSkilllevelname(){
		return skilllevelname;
	}
	
	public void setSkilllevelname(String sName){
		skilllevelname = sName;
	}
	
	public Integer getIdskilllevel(){
		return idskilllevel;
	}
	
	public void setIdskilllevel(Integer sid){
		idskilllevel = sid;
		this.ID =sid.intValue();
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String _tag) {
		tag = _tag;
	}
	
	
		
}
