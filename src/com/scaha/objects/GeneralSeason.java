/**
 * 
 */
package com.scaha.objects;

import java.io.Serializable;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

/**
 * This represents a particular Scaha Season.  
 * 
 * Generally.. this is kept in a collection ScahaSeasonList (there can be many over time)
 * 
 * This allows the app developer to use this as a filter against any seasonally related data..
 * 
 * EX:  Pull teams for a given season.. vs all teams for all seasons!
 * 
 * @author David
 *
 */
public class GeneralSeason extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String DescriptiveContext = null;;
	private String MembershipType = null;
	private String FromDate = null;
	private String ToDate = null;
	private String LinkTag = null;
	private String Tag = null;
	private String Description = null;
	private int isCurrent = 0;
	private String USAYear = null;

	private ScheduleList SchedList = null;

	
	/** 
	 * A Generic Constructor - these will mostly be filled by GeneralSeasonList Constructors
	 * @param _pro
	 */
	public GeneralSeason (Profile _pro) {
		this.setProfile(_pro);
		this.ID = -1;
	}
	
	/** 
	 * A Generic Constructor - these will mostly be filled by GeneralSeasonList Constructors
	 * @param _pro
	 */
	public GeneralSeason (Profile _pro, int _i) {
		this.setProfile(_pro);
		this.ID = _i;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/** 
	 * A Generic Constructor - this will be used to populate the seasonlist for playoffs
	 * @param _pro
	 */
	public GeneralSeason () {
		
	}

	/**
	 * @return the membershipType
	 */
	public String getMembershipType() {
		return MembershipType;
	}



	/**
	 * @param membershipType the membershipType to set
	 */
	public void setMembershipType(String membershipType) {
		MembershipType = membershipType;
	}



	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return FromDate;
	}



	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		FromDate = fromDate;
	}



	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return ToDate;
	}



	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		ToDate = toDate;
	}



	/**
	 * @return the linkTag
	 */
	public String getLinkTag() {
		return LinkTag;
	}



	/**
	 * @param linkTag the linkTag to set
	 */
	public void setLinkTag(String linkTag) {
		LinkTag = linkTag;
	}



	/**
	 * @return the tag
	 */
	public String getTag() {
		return Tag;
	}



	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		Tag = tag;
	}



	/**
	 * @return the description
	 */
	public String getDescription() {
		return Description;
	}



	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		Description = description;
	}



	/**
	 * @return the isCurrent
	 */
	public int getIsCurrent() {
		return isCurrent;
	}



	/**
	 * @param isCurrent the isCurrent to set
	 */
	public void setIsCurrent(int isCurrent) {
		this.isCurrent = isCurrent;
	}



	/**
	 * @return the uSAYear
	 */
	public String getUSAYear() {
		return USAYear;
	}



	/**
	 * @param uSAYear the uSAYear to set
	 */
	public void setUSAYear(String uSAYear) {
		USAYear = uSAYear;
	}

	/**
	 * @return the descriptiveContext
	 */
	public String getDescriptiveContext() {
		return DescriptiveContext;
	}

	/**
	 * @param descriptiveContext the descriptiveContext to set
	 */
	public void setDescriptiveContext(String descriptiveContext) {
		DescriptiveContext = descriptiveContext;
	}
	
	public String toString() {
		return (this.DescriptiveContext != null ? this.DescriptiveContext + " for the " : "") + this.getDescription();
	}
	
	public int getKey() {
		return ID;
	}

	/**
	 * @return the schedList
	 */
	public ScheduleList getSchedList() {
		return SchedList;
	}

	/**
	 * @param schedList the schedList to set
	 */
	public void setSchedList(ScheduleList schedList) {
		SchedList = schedList;
	}
	


}
