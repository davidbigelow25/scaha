package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Family extends ScahaObject implements Serializable  {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private Person per = null;
	private List<FamilyMember> FamilyMembers; 
	private String FamilyName = null;
	
	
	public Family (int _id, Profile _pro, Person _per) {
		this.ID = _id;
		super.setProfile(_pro);
		this.per = _per;
		_per.setFam(this);
	}

	public Family (ScahaDatabase _db, Person _per) {
		//LOGGER.info("Creating Family Structure for:" + _per);
		this.per = _per;
		FamilyMembers = new ArrayList<FamilyMember>();
		
		Vector<Integer> v = new Vector<Integer>();
		v.add(_per.ID);
		ResultSet rs = null;
		try {
			if (_db.getData("call scaha.getFamilyByPersonID(?)", v)) {
				rs = _db.getResultSet();
				boolean bfirst = true;
				while (rs.next()) {
					if (bfirst) {
						this.ID = (rs.getInt(1));
						FamilyName = rs.getString(2); // we only need this once
						bfirst = false;
					}
					//
					// ok.. now lets make a family member .. by sticking a person and a responsibility together..
					//
					FamilyMember mem = new FamilyMember(this.getProfile(), this, rs.getInt(5), rs.getInt(3));
					mem.setRelationship(rs.getString(4));
					mem.setsFirstName(rs.getString(6));
					mem.setsLastName(rs.getString(7));
					mem.setsEmail(rs.getString(8));
					mem.setsPhone(rs.getString(9));
					mem.setsAddress1(rs.getString(10));
					mem.setsCity(rs.getString(11));
					mem.setsState(rs.getString(12));
					mem.setiZipCode(rs.getInt(13));
					mem.setGender(rs.getString(14));
					mem.setDob(rs.getString(15));
					mem.setCitizenship(rs.getString(16));
					mem.setMembertypes(rs.getString(17));
					mem.setUsaHockeyNumber(rs.getString(18));
					mem.setScahaHockeyNumber(rs.getString(19));
					mem.setScholarathletedisplay(rs.getBoolean(20));
					mem.setScholarathletestatus(rs.getString(21));
					FamilyMembers.add(mem);
					//LOGGER.info("FamilyMember: adding " + mem + " to " + _per +"'s family tree.");
				}	
					
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			_db.cleanup();
		}
	}
	
	public List<FamilyMember> getFamilyMembers() {
		return FamilyMembers;
	}
	
	/**
	 * @return the per
	 */
	public Person getPer() {
		return per;
	}

	/**
	 * @param per the per to set
	 */
	public void setPer(Person per) {
		this.per = per;
		per.setFam(this);
	}

	/**
	 * @return the familyName
	 */
	public String getFamilyName() {
		return FamilyName;
	}

	/**
	 * @param familyName the familyName to set
	 */
	public void setFamilyName(String familyName) {
		FamilyName = familyName;
	}

	/**
	 * ok.. this guy will maintain the family record...
	 * it either creates one.. ur updates it.. (Most Likely the Name of the Family is being changed).
	 * 
	 * The deep is signalling us to actually go through and update all the family members in this tree..
	 * (adds, changes, deletes)
	 * @param _db
	 * @param _deep
	 */
	public void update (ScahaDatabase _db, boolean _deep) throws SQLException {

		
		//
		// lets check to make sure we have a valid person.. (>0)
		//
		if (this.per != null && this.per.ID > 0) {
			//
			// ok.. we are good here..
			//
				
				// 
				// is it an object that is not in the database yet..
				//
			
			CallableStatement cs = _db.prepareCall("call scaha.updateFamily(?,?,?,?,?)");
				
			//LOGGER.info("HERE IS THE Prior Update Family ID:" + this.ID);

			int i = 1;
			cs.registerOutParameter(1, java.sql.Types.INTEGER);
			cs.setInt(i++, this.ID);			// This will be the family ID
			cs.setInt(i++, this.per.ID);		// This will be the owner Person ID
			cs.setString(i++,this.FamilyName);
			cs.setInt(i++,1);
			cs.setString(i++, null);
			cs.execute();
							
			//
			// Update the new ID from the database...
			//
			this.ID = cs.getInt(1);
			cs.close();
			//LOGGER.info("HERE IS THE New Family ID:" + this.ID);
				
		}
			
	}
	
	
}
