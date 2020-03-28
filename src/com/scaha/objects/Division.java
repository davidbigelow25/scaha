package com.scaha.objects;

import java.io.Serializable;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class Division extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private String divisionname = null;
	private Integer iddivision = null;
	private String tag = null;
	
	public Division (){ 
	}
	
	public Division (Profile _pro, int _id) {
		setProfile(_pro);
		ID = _id;
		setIddivision(new Integer(_id));
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
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * @return the divisionname
	 */
	public String getDivisionname() {
		return divisionname;
	}

	/**
	 * @param divisionname the divisionname to set
	 */
	public void setDivisionname(String divisionname) {
		this.divisionname = divisionname;
	}

	/**
	 * @return the iddivision
	 */
	public Integer getIddivision() {
		return iddivision;
	}

	/**
	 * @param iddivision the iddivision to set
	 */
	public void setIddivision(Integer div) {
		this.iddivision = div;
		this.ID =div.intValue();
	}
	
	
	public String toString() {
		return this.divisionname;
	}
}
