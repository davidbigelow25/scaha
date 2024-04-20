package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Person extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String m_strDB = "Call scaha.getPersonByProfileID(?)";
	
	private String sFirstName = null;
	private String sLastName = null;
	private String sEmail = null;
	private String sPhone = null;
	private String sAddress1 = null;
	private String sCity = null;
	private String sState = null;
	private String gender = null;
	private String dob = null;
	private String citizenship = null;
	private String notes = null;
	
	private int iZipCode = 0;
	private Family fam = null;
	private UsaHockeyRegistration UsaHockeyRegistration = null;
	private ScahaMember SMember = null;
	

	/**
	 * @return the fam
	 */
	public Family getFam() {
		return fam;
	}

	/**
	 * @param fam the fam to set
	 */
	public void setFam(Family fam) {
		this.fam = fam;
	}

	public int ID = 0;
	
	public Person (ScahaDatabase _db, Profile _pro) {
		
		//
		//  Lets pull from the database now..
		// 
		this.setProfile(_pro);
		
		ResultSet rs = _db.getResultSet();
		Vector<Integer> v = new Vector<Integer>();
		v.add(_pro.ID);

		try {
			if (_db.getData(m_strDB, v)) {
				rs = _db.getResultSet();
				if (rs.next()) {
					int i = 1;
					ID = rs.getInt(i++);
					this.setsFirstName(rs.getString(i++));
					this.setsLastName(rs.getString(i++));
					this.setsEmail(rs.getString(i++));
					this.setsPhone(rs.getString(i++));
					this.setsAddress1(rs.getString(i++));
					this.setsCity(rs.getString(i++));
					this.setsState(rs.getString(i++));
					this.setiZipCode(rs.getInt(i++));
					this.setGender(rs.getString(i++));
					this.setDob(rs.getString(i++));
					this.setCitizenship(rs.getString(i++));
					//LOGGER.info("Successfully Created the person: " + this);

				}
				
				rs.close();
			}
		} catch (SQLException ex) {
				ex.printStackTrace();
				_db.cleanup();
		} finally {
			_db.cleanup();
		}
		
		//
		// Lets round out the family here..
		//
		this.fam = new Family (_db, this);
		
	}

	public Person (Profile _pro, Person _per) {
		
		//LOGGER.info("Attempting to Create the Person.. from another person");
		//
		//  Lets pull from the database now..
		// 
		this.setProfile(_pro);
		
		if (_per == null) {
			this.ID = -1;
			this.setProfile(_pro);
		} else {
			ID = _per.ID;
			this.setsFirstName(_per.getsFirstName());
			this.setsLastName(_per.getsLastName());
			this.setsEmail(_per.getsEmail());
			this.setsPhone(_per.getsPhone());
			this.setsAddress1(_per.getsAddress1());
			this.setsCity(_per.getsCity());
			this.setsState(_per.getsState());
			this.setiZipCode(_per.getiZipCode());
			this.setGender(_per.getGender());
			this.setDob(_per.getDob());
			this.setCitizenship(_per.getCitizenship());
			this.fam = _per.getFamily();
			//LOGGER.info("Successfully Created the Person Object.. from another person..");
		}
	}
	
	/**
	 * Here we build one from scratch w/o any DB Support
	 * @param _id
	 * @param _pro
	 */
	public Person (int _id, Profile _pro) {
		
		this.ID = _id;
		this.setProfile(_pro);
	}

	/**
	 * Here we build one from scratch w/o any DB Support
	 * @param _id
	 * @param _pro
	 */
	public Person (Profile _pro) {
		
		this.ID = -1;
		this.setProfile(_pro);
	}
	
	public Person(int int1) {
		// TODO Auto-generated constructor stub
		this.ID = int1;
	}

	/**
	 * @return the sPhone
	 */
	public String getsPhone() {
		return sPhone;
	}



	/**
	 * @param sPhone the sPhone to set
	 */
	public void setsPhone(String sPhone) {
		this.sPhone = sPhone;
	}



	/**
	 * @return the sFirstName
	 */
	public String getsFirstName() {
		return sFirstName;
	}


	/**
	 * @param sFirstName the sFirstName to set
	 */
	public void setsFirstName(String sFirstName) {
		this.sFirstName = sFirstName;
	}


	/**
	 * @return the sLastName
	 */
	public String getsLastName() {
		return sLastName;
	}


	/**
	 * @param sLastName the sLastName to set
	 */
	public void setsLastName(String sLastName) {
		this.sLastName = sLastName;
	}


	/**
	 * @return the sEmail
	 */
	public String getsEmail() {
		return sEmail;
	}


	/**
	 * @param sEmail the sEmail to set
	 */
	public void setsEmail(String sEmail) {
		this.sEmail = sEmail;
	}



	/**
	 * @return the sAddress1
	 */
	public String getsAddress1() {
		return sAddress1;
	}


	/**
	 * @param sAddress1 the sAddress1 to set
	 */
	public void setsAddress1(String sAddress1) {
		this.sAddress1 = sAddress1;
	}

	
	/**
	 * @return the sCity
	 */
	public String getsCity() {
		return sCity;
	}


	/**
	 * @param sCity the sCity to set
	 */
	public void setsCity(String sCity) {
		this.sCity = sCity;
	}


	/**
	 * @return the sState
	 */
	public String getsState() {
		return sState;
	}


	/**
	 * @param sState the sState to set
	 */
	public void setsState(String sState) {
		this.sState = sState;
	}


	/**
	 * @return the iZipCpde
	 */
	public int getiZipCode() {
		return iZipCode;
	}


	/**
	 * @param iZipCpde the iZipCpde to set
	 */
	public void setiZipCode(int iZipCode) {
		this.iZipCode = iZipCode;
	}

	/**
	 * This returns the family that was constructed when the person was created..
	 * @return
	 */
	public Family getFamily() {
		return this.fam;
	}

	/**
	 * This guy will take an existing database connection and update and or insert the record..
	 * 
	 * @param db
	 */
	public void update(ScahaDatabase db) throws SQLException {
		
		// 
		// is it an object that is not in the database yet..
		//
		
		CallableStatement cs = db.prepareCall("call scaha.updatePerson(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");


		LOGGER.info("HERE IS THE Starting Person ID:" + this.ID);

		int i = 1;
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(i++, this.ID);
		
		//
		// This is bad.. but in a pinch
		// if the attached person is not in db yet.. we want to see if this matches profile person obj in memory
		// otherwise.. ids must match..
		//
		if (getProfile().getPerson().ID < 1) {
			cs.setInt(i++, (getProfile().getPerson() == this ? getProfile().ID : 0));
		} else {
			cs.setInt(i++, (getProfile().getPerson().ID == this.ID ? getProfile().ID : 0));
		}
		cs.setString(i++, this.sFirstName);
		cs.setString(i++, this.sLastName);
		cs.setString(i++, this.sEmail);
		cs.setString(i++, this.sPhone);
		cs.setString(i++, this.sAddress1);
		cs.setString(i++, this.sCity);
		cs.setString(i++, this.sState);
		cs.setInt(i++, 	this.iZipCode);
		cs.setString(i++, this.gender);
		cs.setString(i++, this.dob);
		cs.setString(i++, this.citizenship);
		cs.setInt(i++,1);
		cs.setString(i++,null);
		cs.execute();
		
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(1);
		cs.close();

		LOGGER.info("HERE IS THE new Person ID:" + this.ID);

	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the dob
	 */
	public String getDob() {
		return dob;
	}

	/**
	 * @param dob the dob to set
	 */
	public void setDob(String dob) {
		this.dob = dob;
	}
	
	public String getCitizenship(){
		return citizenship;
	}
	
	public void setCitizenship(String sName){
		citizenship = sName;
	}
	
	public String getNotes(){
		return notes;
	}
	
	public void setNotes(String sName){
		notes = sName;
	}
	
	
	/**
	 * @return the usaHockeyRegistration
	 */
	public UsaHockeyRegistration getUsaHockeyRegistration() {
		return UsaHockeyRegistration;
	}

	/**
	 * @param usaHockeyRegistration the usaHockeyRegistration to set
	 */
	public void setUsaHockeyRegistration(UsaHockeyRegistration usaHockeyRegistration) {
		UsaHockeyRegistration = usaHockeyRegistration;
	}

	/**
	 * Here we clean all the USA hockey Information we can and jamb it into the Person Record..
	 * @param usar
	 */
	public void gleanUSAHinfo(UsaHockeyRegistration usar) {
		// TODO WILL NEED TO ADD COUNTRY AS WELL
		setsFirstName(Utils.properCase(usar.getFirstName()));
		setsLastName(Utils.properCase(usar.getLastName()));
		setCitizenship(usar.getCitizen());
		setDob(usar.getDOB());
		setGender(usar.getGender());
		this.setsEmail(usar.getEmail().toLowerCase()); // Always carry this through to the actual person
	}
	
	public int getPersonID() {
		return this.ID;
	}
	
	/** General anotated stuff here
	 */
	
	public String getXFamilyName() {
		return getGenAttByKey("FAMILYNAME");
	}
	public String getXRelType() {
		return getGenAttByKey("RELTYPE");
	}
	public String getXNotes() {
		return getGenAttByKey("NOTES");
	}
	
	public String isInFamily () {
		return getGenAttByKey("INFAMILY");
	}

	/**
	 * @return the sMember
	 */
	public ScahaMember getSMember() {
		return SMember;
	}

	/**
	 * @param sMember the sMember to set
	 */
	public void setSMember(ScahaMember sMember) {
		SMember = sMember;
	}
	
	public String toString() {
		return this.getsFirstName() + " " + this.getsLastName();
	}
}
