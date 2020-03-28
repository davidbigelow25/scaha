package com.scaha.objects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.gbli.context.ContextManager;

public class Venue extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// This holds the Club That uses this venue
	//
	
	List<Club> myClubList = new ArrayList<Club>();
	private ClubList myClubs = new ClubList(myClubList);
	
	private String Tag = null;
	private String Description = null;    
	private String Email =  null;
	private String Phone = null;
	private String Address = null;
	private String City = null;
	private String State =  null;
	private String Zipcode = null;
	private String Website = null;
	private String gmapparms = null;
	private boolean Primary = false;
	
	public Venue (Profile _pro, int _id) {
		setProfile(_pro);
		this.ID = _id;
	}
	
	public Venue (Profile _pro, int _id, Club _cl) {
		setProfile(_pro);
		this.ID = _id;
		myClubList.add(_cl);
		//
		// add this team to the list of 
		//
	}

	public Venue(){
		
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
	 * @return the email
	 */
	public String getEmail() {
		return Email;
	}

	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		Email = email;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return Phone;
	}

	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		Phone = phone;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return Address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		Address = address;
	}

	/**
	 * @return the city
	 */
	public String getCity() {
		return City;
	}

	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		City = city;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return State;
	}

	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		State = state;
	}

	/**
	 * @return the zipcode
	 */
	public String getZipcode() {
		return Zipcode;
	}

	/**
	 * @param zipcode the zipcode to set
	 */
	public void setZipcode(String zipcode) {
		Zipcode = zipcode;
	}

	/**
	 * @return the website
	 */
	public String getWebsite() {
		return Website;
	}

	/**
	 * @param website the website to set
	 */
	public void setWebsite(String website) {
		Website = website;
	}

	/**
	 * @return the myClubs
	 */
	public ClubList getMyClubs() {
		return myClubs;
	}

	/**
	 * @param myClubs the myClubs to set
	 */
	public void setMyClubs(ClubList myClubs) {
		this.myClubs = myClubs;
	}

	/**
	 * @return the primary
	 */
	public boolean isPrimary() {
		return Primary;
	}

	/**
	 * @param primary the primary to set
	 */
	public void setPrimary(boolean primary) {
		Primary = primary;
	}

	/**
	 * @return the gmapparms
	 */
	public String getGmapparms() {
		return gmapparms;
	}

	/**
	 * @param gmapparms the gmapparms to set
	 */
	public void setGmapparms(String gmapparms) {
		this.gmapparms = gmapparms;
	}
	
	public String toString() {
		return this.Description;
	}
	
		
}
