/**
 * 
 */
package com.scaha.objects;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * @author David
 *
 */
public class ScahaManager extends Person {

	public ScahaManager(int _id, Profile _pro) {
		super(_id, _pro);
		// TODO Auto-generated constructor stub
	}

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	//
	// Lets hold the playerID here.. and allow the super to hold the person...
	//
	protected int ID = -1;
	protected Boolean ismanager = null;
	
	/**
	 * A basic constructor that glues a person to a player given the relationship.
	 * This assumes new record status.. 
	 * @param _id
	 * @param _per
	 * @param _relationship
	 */
	public ScahaManager (Profile _pro) {
		
		//
		// Here we are starting with basically an empty shell of an object..
		// all id's are zero
		//
		super(-1, _pro);
		ID = -1;
	}
	
	public ScahaManager(Profile _pro, Person _per) {
		super( _pro, _per);

	}
	
	public int getID() {
		return ID;
	} 
	
	public Boolean getIsmanager(){
		return ismanager;
	}
	
	public void setIsmanager(Boolean value){
		ismanager = value;
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
		CallableStatement cs = _db.prepareCall("call scaha.updateManager(?,?,?,?)");
		
		//LOGGER.info("HERE IS THE PERSON ID for manager:" + super.ID);
		//LOGGER.info("HERE IS THE manager  ID for manager:" + this.ID);
		
		int i = 1;
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(i++, this.ID);
		cs.setInt(i++, super.ID);
		cs.setInt(i++,1);
		cs.setString(i++,null);
		cs.execute();
				
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(1);
		cs.close();
		//LOGGER.info("HERE IS THE New  Manager ID:" + this.ID);
		
	}

	public Integer getManagerteamid(Integer _profileid){
		
		ScahaDatabase db = (ScahaDatabase)ContextManager.getDatabase("ScahaDatabase");
		this.setIsmanager(false);
		Integer teamid = 0;
		//
		// If this comes back true.. we have a good result set to play with and fill out the profile
		//
		try {
			CallableStatement cs = db.prepareCall("CALL scaha.getTeamIdForManager(?)");
			cs.setInt("profileid", _profileid);

			ResultSet rs = cs.executeQuery();
			if (rs != null){
				while (rs.next()) {
					teamid = rs.getInt("idteam");
					//LOGGER.info("We have results for teamid:" + teamid);
					if (teamid>0){
						this.setIsmanager(true);
					}
				}
				rs.close();
			}
			cs.close();
		} catch (SQLException ex) {
				ex.printStackTrace();
		} finally {
			db.free();
		}
		
		return teamid;
	}
	
	public List<Team> getManagerteams(Integer _profileid){
		
		ScahaDatabase db = (ScahaDatabase)ContextManager.getDatabase("ScahaDatabase");
		this.setIsmanager(false);
		List<Team> tempteams = new ArrayList<Team>();
		//
		// If this comes back true.. we have a good result set to play with and fill out the profile
		//
		try {
			CallableStatement cs = db.prepareCall("CALL scaha.getTeamsForManager(?)");
			cs.setInt("profileid", _profileid);

			ResultSet rs = cs.executeQuery();
			if (rs != null){
				while (rs.next()) {
					
					Integer teamid = rs.getInt("idteam");
					//LOGGER.info("We have results for teamid:" + teamid);
					if (teamid>0){
						this.setIsmanager(true);
					}
					String teamname = rs.getString("teamname");
					Team team = new Team(teamname,teamid.toString());
					
					tempteams.add(team);
				}
				rs.close();
			}
			cs.close();
		} catch (SQLException ex) {
				ex.printStackTrace();
		} finally {
			db.free();
		}
		
		return tempteams;
	}
}
