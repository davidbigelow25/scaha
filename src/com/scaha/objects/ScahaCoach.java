/**
 * 
 */
package com.scaha.objects;

import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * @author dbigelow
 *
 */
public class ScahaCoach extends Person {
	
	
	
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String screeningexpires = "01/01/1980";   // iTS A Date
	private String cepnumber = "";
	private int ceplevel = 0;
	private String cepexpires = "01/01/1980";  // Its a Date
	private int u8 = 0;
	private int u10 = 0;
	private int u12 = 0;
	private int u14 = 0;
	private int u18 = 0;
	
	private Person MyPerson = null;				// ok.. new strat.. this owns the member object
	private Person TopPerson = null; 	// This represents the Top owner of this object.. Usually the account holder

	//
	// Lets hold the playerID here.. and allow the super to hold the person...
	//
	protected int ID = -1;

	public ScahaCoach(int _id, Profile _pro) {
		super(_id, _pro);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * A basic constructor that glues a person to a player given the relationship.
	 * This assumes new record status.. 
	 * @param _id
	 * @param _per
	 * @param _relationship
	 */
	public ScahaCoach (Profile _pro) {
		
		//
		// Here we are starting with basically an empty shell of an object..
		// all id's are zero
		//
		super(-1, _pro);
		ID = -1;
	}
	
	public ScahaCoach(Profile _pro, Person _per) {
		super( _pro, _per);
	}
	

	
	public void update(ScahaDatabase _db) throws SQLException {
		
		//
		// Lets update the person here.
		//
		super.update(_db);
		
		//
		// now.. we have to update the pertinat parts of the player table 
		// to add the player extension data..
		
		// 
		// is it an object that is not in the database yet..
		//
		//
		CallableStatement cs = _db.prepareCall("call scaha.updateScahaCoach(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		//LOGGER.info("HERE IS THE PERSON ID for coach:" + super.ID);
		//LOGGER.info("HERE IS THE coach ID for coach:" + this.ID);

		int i = 1;
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(i++, this.ID);
		cs.setInt(i++, super.ID);
		cs.setString(i++, this.getCepnumber());
		cs.setString(i++, this.getCepexpires());
		cs.setInt(i++, this.getCeplevel());
		cs.setString(i++, (this.getScreeningexpires() == null ? "01/01/1980" : this.getScreeningexpires()));
		cs.setInt(i++, this.getU8());
		cs.setInt(i++, this.getU10());
		cs.setInt(i++, this.getU12());
		cs.setInt(i++, this.getU14());
		cs.setInt(i++, this.getU18());
		cs.setInt(i++,1);
		cs.setString(i++,null);

		cs.execute();
				
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(1);
		cs.close();
		//LOGGER.info("HERE IS THE NEW Coach ID:" + this.ID);
		
	}
	
	/**
	 * @return the screeningexpires
	 */
	public String getScreeningexpires() {
		return screeningexpires;
	}

	/**
	 * @param screeningexpires the screeningexpires to set
	 */
	public void setScreeningexpires(String screeningexpires) {
		this.screeningexpires = screeningexpires;
	}

	/**
	 * @return the cepnumber
	 */
	public String getCepnumber() {
		return cepnumber;
	}

	/**
	 * @param cepnumber the cepnumber to set
	 */
	public void setCepnumber(String cepnumber) {
		this.cepnumber = cepnumber;
	}

	/**
	 * @return the ceplevel
	 */
	public int getCeplevel() {
		return ceplevel;
	}

	/**
	 * @return the cepexpires
	 */
	public String getCepexpires() {
		return cepexpires;
	}

	/**
	 * @param cepexpires the cepexpires to set
	 */
	public void setCepexpires(String cepexpires) {
		this.cepexpires = cepexpires;
	}

	/**
	 * @return the u8
	 */
	public int getU8() {
		return u8;
	}

	/**
	 * @param u8 the u8 to set
	 */
	public void setU8(int u8) {
		this.u8 = u8;
	}

	/**
	 * @return the u10
	 */
	public int getU10() {
		return u10;
	}

	/**
	 * @param u10 the u10 to set
	 */
	public void setU10(int u10) {
		this.u10 = u10;
	}

	/**
	 * @return the u12
	 */
	public int getU12() {
		return u12;
	}

	/**
	 * @param u12 the u12 to set
	 */
	public void setU12(int u12) {
		this.u12 = u12;
	}

	/**
	 * @return the u14
	 */
	public int getU14() {
		return u14;
	}

	/**
	 * @param u14 the u14 to set
	 */
	public void setU14(int u14) {
		this.u14 = u14;
	}

	/**
	 * @return the u18
	 */
	public int getU18() {
		return u18;
	}

	/**
	 * @param u18 the u18 to set
	 */
	public void setU18(int u18) {
		this.u18 = u18;
	}


	/**
	 * @return the myPerson
	 */
	public Person getMyPerson() {
		return MyPerson;
	}


	/**
	 * @param myPerson the myPerson to set
	 */
	public void setMyPerson(Person myPerson) {
		MyPerson = myPerson;
	}


	/**
	 * @return the topPerson
	 */
	public Person getTopPerson() {
		return TopPerson;
	}


	/**
	 * @param topPerson the topPerson to set
	 */
	public void setTopPerson(Person topPerson) {
		TopPerson = topPerson;
	}


	/**
	 * @param ceplevel the ceplevel to set
	 */
	public void setCeplevel(int ceplevel) {
		this.ceplevel = ceplevel;
	}


}
