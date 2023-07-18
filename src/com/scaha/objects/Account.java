/**
 * 
 */
package com.scaha.objects;

import com.gbli.context.ContextManager;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * 
 * Opening is an individual team opening .  
 * 
 * It simply holds their division, level, # of openings, contact name, contact email, host rink, for a given clubs opening.
 * @author rfoster
 *
 */
public class Account extends ScahaObject implements Serializable {


	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String fname = null;
	private String fnamecompare = null;
	private String lname = null;
	private String lnamecompare = null;
	private String dob = null;
	private String dobcompare = null;
	private String usahockeynumber = null;
	private Integer personid = null;
	private Integer comparepersonid = null;

	public Account() {
	
	}

	public String getFname() {
		return fname;
	}

	public void setFname(String fname) {
		this.fname = fname;
	}

	public String getFnamecompare() {
		return fnamecompare;
	}

	public void setFnamecompare(String fnamecompare) {
		this.fnamecompare = fnamecompare;
	}

	public String getLname() {
		return lname;
	}

	public void setLname(String lname) {
		this.lname = lname;
	}

	public String getLnamecompare() {
		return lnamecompare;
	}

	public void setLnamecompare(String lnamecompare) {
		this.lnamecompare = lnamecompare;
	}

	public String getDob() {
		return dob;
	}

	public void setDob(String dob) {
		this.dob = dob;
	}

	public String getDobcompare() {
		return dobcompare;
	}

	public void setDobcompare(String dobcompare) {
		this.dobcompare = dobcompare;
	}

	public String getUsahockeynumber() {
		return usahockeynumber;
	}

	public void setUsahockeynumber(String usahockeynumber) {
		this.usahockeynumber = usahockeynumber;
	}

	public Integer getPersonid() {
		return personid;
	}

	public void setPersonid(Integer personid) {
		this.personid = personid;
	}

	public Integer getComparepersonid() {
		return comparepersonid;
	}

	public void setComparepersonid(Integer comparepersonid) {
		this.comparepersonid = comparepersonid;
	}
}
