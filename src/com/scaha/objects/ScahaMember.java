package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class ScahaMember extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String SCAHANumber = null;			// This is the 6 digit automatically generated number..
	private String SCAHAYear = null;		// This is they year (single digit) its valid in..
	private Person MyPerson = null;				// ok.. new strat.. this owns the member object
	private Person TopPerson = null; 	// This represents the Top owner of this object.. Usually the account holder
	
	
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
	protected int ID = -1;
	
	/**
	 * A basic object to hold all the USAH Information coming back from a call..
	 * 
	 * @param _id
	 * @param _sUSAHnum
	 */
	
	
	public ScahaMember(Profile _pro, Person _per) {
		this.setProfile(_pro);
		this.setMyPerson(_per);
	}
	/**
	
	
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
		MyPerson.setSMember(this);
	}
	/**
	 * @return the sCAHANumber
	 */
	public String getSCAHANumber() {
		return SCAHANumber;
	}
	/**
	 * @param sCAHANumber the sCAHANumber to set
	 */
	public void setSCAHANumber(String sCAHANumber) {
		SCAHANumber = sCAHANumber;
	}
	/**
	 * @return the sCAHAYear
	 */
	public String getSCAHAYear() {
		return SCAHAYear;
	}
	/**
	 * @param sCAHAYear the sCAHAYear to set
	 */
	public void setSCAHAYear(String sCAHAYear) {
		SCAHAYear = sCAHAYear;
	}
	/**
	 *  This guy really just cranks out a membership record.
	 *  The membership object should really never be updated progromatically.
	 *  Because the number has to be unique.. we need to:
	 *  
	 *  1) Lock the table
	 *  2) Genereate the Unique Number (by gen and check against DB)
	 *  3) then create the table
	 *  4) The unlock the table.
	 */
	public void generateMembership(ScahaDatabase _db) throws SQLException {
		
		
		if (this.ID > 0 ) {
			//LOGGER.info("Blocking an Update Call to a Member Object that has a positive ID");
			return;
		}

		// 
		// is it an object that is not in the database yet..
		//
		//

		int ians = 1;
		
		while (ians > 0 ) {
		
			//need to put in logic to remove 0,O,l,and 1 to prevent any confusion when using the code on loi
			//need to include season number to beginning of code something like 7_WVOZC
			

			
			this.setSCAHANumber(Utils.getRandom5CharStringUpper());
			this.setSCAHANumber(this.SCAHANumber.replaceAll("0", "Y"));
			this.setSCAHANumber(this.SCAHANumber.replaceAll("O", "Y"));
			this.setSCAHANumber(this.SCAHANumber.replaceAll("1", "Z"));
			this.setSCAHANumber(this.SCAHANumber.replaceAll("I", "X"));
			this.setSCAHANumber(this.SCAHAYear.concat(SCAHANumber));
			
			Vector<String> v = new Vector<String>();
			v.add(this.getSCAHAYear());
			v.add(this.getSCAHANumber());
		
			//
			// Lets check to see if it is a Unique Number
			// 
		
			_db.getData("call scaha.checkMemberNumberUnique(?,?)", v);
			_db.getResultSet().next();
			ians = _db.getResultSet().getInt(1);
			
			
		}
		
		// We should have a unique number by now..
		
		_db.cleanup();
		
		CallableStatement cs = _db.prepareCall("call scaha.updateMember(?,?,?,?,?,?)");
				
		int i = 1;
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(i++, this.ID);
		cs.setInt(i++,this.getMyPerson().ID);			
		cs.setString(i++, this.getSCAHAYear());
		cs.setString(i++, this.getSCAHANumber());
		cs.setInt(i++, 1);										//	IN in_isactive tinyint,
		cs.setString(i++, null);								//	IN in_updated timestamp
		
		cs.execute();
		
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(1);
		cs.close();
		_db.cleanup();

	}
	
	/**
	 *  This guy really just cranks out a membership record.
	 *  The membership object should really never be updated progromatically.
	 *  Because the number has to be unique.. we need to:
	 *  
	 *  1) Lock the table
	 *  2) Genereate the Unique Number (by gen and check against DB)
	 *  3) then create the table
	 *  4) The unlock the table.
	 */
	public void logConcussion(ScahaDatabase _db, Integer profileid, Integer personid) throws SQLException {
	
		CallableStatement cs = _db.prepareCall("call scaha.logconcussionacknowledgement(?,?)");
		
		//we need to track who confirmed the concussion policy and who did they confirm it for.
		cs.setInt("inprofileid", profileid);
		cs.setInt("inpersonid",personid);			
		cs.execute();
		
		cs.close();
		_db.cleanup();
	}
}

