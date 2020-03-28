package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import org.primefaces.model.StreamedContent;

import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class FamilyMember extends Person implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	public int ID = -1;
	private String usaHockeyNumber = "--------------";  // this is used for family member display... for the given hockey year..
	private String scahaHockeyNumber = "-----";  // this is used for family member display... for the given hockey year..
	private String personname = null;
	private String Relationship = null;
	private String membertypes = null;
	private Family fam = null;
	private Boolean scholarathletedisplay = null;
	private String scholarathletestatus = null;
	
	/**
	 * A basic constructor that glues a person to a relationship for a given family.
	 * 
	 * @param _id
	 * @param _per
	 * @param _relationship
	 */
	public FamilyMember ( Profile _pro, Family _fam, int _personid, int _fmid) {
		super(_personid, _pro);
		this.ID = _fmid;
		this.fam = _fam;
	}

	public FamilyMember ( Profile _pro, Family _fam, Person _per) {
		super(_per.ID, _pro);
		this.ID = -1;
		this.fam = _fam;
	}

	public String getScholarathletestatus() {
		return scholarathletestatus;
	}
	
	public void setScholarathletestatus(String _s) {
		this.scholarathletestatus = _s;
	}
	
	public String getPersonname() {
		return personname;
	}
	
	public void setPersonname(String _s) {
		this.personname = _s;
	}
	
	
	public Boolean getScholarathletedisplay() {
		return scholarathletedisplay;
	}
	
	public void setScholarathletedisplay(Boolean _s) {
		this.scholarathletedisplay = _s;
	}
	
	public String getRelationship() {
		return Relationship;
	}
	
	public void setRelationship(String _s) {
		this.Relationship = _s;
	}
	
	/**
	 *  This updates the record in the table.
	 *  
	 *  The person is the person 
	 *  The person is also 
	 * @throws SQLException 
	 */
	public void updateFamilyMemberStructure(ScahaDatabase _db) throws SQLException {
		
		// 
		// is it an object that is not in the database yet..
		//
	
		CallableStatement cs = _db.prepareCall("call scaha.updateFamilyMember(?,?,?,?,?,?)");
			
		//LOGGER.info("HERE IS THE Pre FamilyMember ID:" + this.ID);

		int i = 1;
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(i++, this.ID);		// Family Member ID
		cs.setInt(i++, super.ID);		// PersonID
		cs.setInt(i++, fam.ID);			// FamilyID
		cs.setString(i++, this.getRelationship());  // Relationship Type
		cs.setInt(i++,1);				// Is Active
		cs.setString(i++,null);			// Now
		cs.execute();
						
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(1);
		cs.close();
		//LOGGER.info("HERE IS THE Family Mamber ID:" + this.ID);
				
		
	}

	/**
	 * @return the membertypes
	 */
	public String getMembertypes() {
		return membertypes;
	}

	/**
	 * @param membertypes the membertypes to set
	 */
	public void setMembertypes(String membertypes) {
		this.membertypes = membertypes;
	}
	
	/**
	 * @return the usaHockeyNumber
	 */
	public String getUsaHockeyNumber() {
		return usaHockeyNumber;
	}

	/**
	 * @param usaHockeyNumber the usaHockeyNumber to set
	 */
	public void setUsaHockeyNumber(String usaHockeyNumber) {
		this.usaHockeyNumber = usaHockeyNumber;
	}

	/**
	 * @return the scahaHockeyNumber
	 */
	public String getScahaHockeyNumber() {
		return scahaHockeyNumber;
	}

	/**
	 * @param scahaHockeyNumber the scahaHockeyNumber to set
	 */
	public void setScahaHockeyNumber(String scahaHockeyNumber) {
		this.scahaHockeyNumber = scahaHockeyNumber;
	}
	
	public int getFamilyMemberID() {
		return ID;
	}

	public StreamedContent getCurrentFMsSCAHAMemberAsBarCode() {
		return Utils.getStreamedBarCodeContent(this.getScahaHockeyNumber());
	}
}
