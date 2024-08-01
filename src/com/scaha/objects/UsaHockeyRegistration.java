package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class UsaHockeyRegistration extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String USAHnum = null;
	private String LastName = null;
	private String FirstName = null;
	private String MiddleInit = null;
	private String Address = null;
	private String City = null;
	private String State = null;
	private String Zipcode = null;
	private String DOB = null;
	private String Country = null;
	private	String ForZip = null;
	private	String Citizen = null;
	private String Gender = null;
	private String HPhone = null;
	private String BPhone = null;
	private String PGSLName = null;
	private String PGSFName = null;
	private String PGSMName = null;
	private String Email = null;
	
	protected int ID = 0;
	
	/**
	 * A basic object to hold all the USAH Information coming back from a call..
	 * 
	 * @param _id
	 * @param _sUSAHnum
	 */
	public UsaHockeyRegistration(int _id, String _sUSAHnum ) {
		this.ID = _id;
		this.setUSAHnum(_sUSAHnum);
	}
	/**
	 * @return the lastName
	 */
	public String getLastName() {
		return LastName;
	}
	/**
	 * @param lastName the lastName to set
	 */
	public void setLastName(String lastName) {
		LastName = lastName;
	}
	/**
	 * @return the firstName
	 */
	public String getFirstName() {
		return FirstName;
	}
	/**
	 * @param firstName the firstName to set
	 */
	public void setFirstName(String firstName) {
		FirstName = firstName;
	}
	/**
	 * @return the middleInit
	 */
	public String getMiddleInit() {
		return MiddleInit;
	}
	/**
	 * @param middleInit the middleInit to set
	 */
	public void setMiddleInit(String middleInit) {
		MiddleInit = middleInit;
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
	 * @return the dOB
	 */
	public String getDOB() {
		return DOB;
	}
	/**
	 * @param dOB the dOB to set
	 */
	public void setDOB(String dOB) {
		DOB = dOB;
	}
	/**
	 * @return the country
	 */
	public String getCountry() {
		return Country;
	}
	/**
	 * @param country the country to set
	 */
	public void setCountry(String country) {
		Country = country;
	}
	/**
	 * @return the forZip
	 */
	public String getForZip() {
		return ForZip;
	}
	/**
	 * @param forZip the forZip to set
	 */
	public void setForZip(String forZip) {
		ForZip = forZip;
	}
	/**
	 * @return the citizen
	 */
	public String getCitizen() {
		return Citizen;
	}
	/**
	 * @param citizen the citizen to set
	 */
	public void setCitizen(String citizen) {
		Citizen = citizen;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return Gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		Gender = gender;
	}
	/**
	 * @return the hPhone
	 */
	public String getHPhone() {
		return HPhone;
	}
	/**
	 * @param hPhone the hPhone to set
	 */
	public void setHPhone(String hPhone) {
		HPhone = hPhone;
	}
	/**
	 * @return the bPhone
	 */
	public String getBPhone() {
		return BPhone;
	}
	/**
	 * @param bPhone the bPhone to set
	 */
	public void setBPhone(String bPhone) {
		BPhone = bPhone;
	}
	/**
	 * @return the pGSLName
	 */
	public String getPGSLName() {
		return PGSLName;
	}
	/**
	 * @param pGSLName the pGSLName to set
	 */
	public void setPGSLName(String pGSLName) {
		PGSLName = pGSLName;
	}
	/**
	 * @return the pGSFName
	 */
	public String getPGSFName() {
		return PGSFName;
	}
	/**
	 * @param pGSFName the pGSFName to set
	 */
	public void setPGSFName(String pGSFName) {
		PGSFName = pGSFName;
	}
	/**
	 * @return the pGSMName
	 */
	public String getPGSMName() {
		return PGSMName;
	}
	/**
	 * @param pGSMName the pGSMName to set
	 */
	public void setPGSMName(String pGSMName) {
		PGSMName = pGSMName;
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
	 * @return the uSAHnum
	 */
	public String getUSAHnum() {
		return USAHnum;
	}
	/**
	 * @param _uSAHnum the uSAHnum to set
	 */
	public void setUSAHnum(String _uSAHnum) {
		USAHnum = _uSAHnum;
	}
	public String getMembership201XYear() {
		//LOGGER.info("GMY:" + getUSAHnum());
		
		return "201" +  this.getUSAHnum().substring(3,4);
		
	}
	
	/**
	 * This returns 
	 * @return
	 */
	public String getMemberShipYear() {
		return this.getUSAHnum().substring(3,4);
	}
	
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	/**
	 * ok. this guy /adds / updates the USA Hockey Record in the database 
	 *  and ensures it is tied to a the Person of Interest
	 */
	public void update(ScahaDatabase _db, Person _per) throws SQLException {

				// 
				// is it an object that is not in the database yet..
				//
				
				CallableStatement cs = _db.prepareCall("call scaha.updateUSAHockey(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

				int i = 1;
				cs.registerOutParameter(1, java.sql.Types.INTEGER);
				cs.setInt(i++, this.ID);								//	INOUT inout_idusahockey INT(10),
				cs.setString(i++, this.getUSAHnum());					//	IN in_usahockeynumber varchar(14),
				cs.setInt(i++, _per.ID);							//	IN in_idperson INT(10),
				cs.setString(i++, this.getUSAHnum().substring(3,4));  	//	IN in_usayear char(1),
				cs.setString(i++, this.getLastName());					//	IN in_lastname varchar(45),
				cs.setString(i++, this.getFirstName());					//	IN in_firstname varchar(45), 
				cs.setString(i++, this.getMiddleInit());				//	IN in_middleinit char(1),
				cs.setString(i++, this.getAddress());					//	IN in_address varchar(64),
				cs.setString(i++, this.getCity());						//	IN in_city varchar(45),
				cs.setString(i++, this.getState());						//	IN in_state char(2),
				cs.setString(i++, this.getZipcode());					//	IN in_zipcode varchar(9),
				cs.setString(i++, this.getDOB());						//	IN in_dob date,
				cs.setString(i++, this.getCountry());					//	IN in_country varchar(45),
				cs.setString(i++, this.getForZip());					//	IN in_forzip varchar(16),
				cs.setString(i++, this.getCitizen());					//	IN in_citizenship varchar(45),
				cs.setString(i++, this.getGender());					//	IN in_gender char(1),
				cs.setString(i++, this.getHPhone());					//	IN in_homephone varchar(16),
				cs.setString(i++, this.getBPhone());					//	IN in_workphone varchar(16),
				cs.setString(i++, this.getPGSFName());					//	IN in_pgfirstname varchar(45),
				cs.setString(i++, this.getPGSLName());					//	IN in_pglastname varchar(45),
				cs.setString(i++, this.getPGSMName().substring(0,1));	//	IN in_pgmiddleinit char(1),
				cs.setString(i++, this.getEmail());						//	IN in_pgemail varchar(45),
				cs.setInt(i++, 1);										//	IN in_isactive tinyint,
				cs.setString(i++, null);								//	IN in_updated timestamp
				
				cs.execute();
				
				//
				// Update the new ID from the database...
				//
				this.ID = cs.getInt(1);
				cs.close();

			//	LOGGER.info("HERE IS THE NEW ID:" + this.ID);
				
				_per.setUsaHockeyRegistration(this);
	
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UsaHockeyRegistration [LastName=" + LastName + ", FirstName="
				+ FirstName + ", MiddleInit=" + MiddleInit + ", Address="
				+ Address + ", City=" + City + ", State=" + State
				+ ", Zipcode=" + Zipcode + ", DOB=" + DOB + ", Country="
				+ Country + ", ForZip=" + ForZip + ", Citizen=" + Citizen
				+ ", Gender=" + Gender + ", HPhone=" + HPhone + ", BPhone="
				+ BPhone + ", PGSLName=" + PGSLName + ", PGSFName=" + PGSFName
				+ ", PGSMName=" + PGSMName + ", Email=" + Email + "]";
	}

	
	

}
