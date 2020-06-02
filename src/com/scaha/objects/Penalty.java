package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Penalty extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private ScahaTeam  team = null;
	private LiveGame livegame = null;
	private LiveGameRosterSpot rosterspot = null;
	private int idroster = 0;
	private String penaltytype = null;
	private int count = 0;
	private int period = 0;
	private String timeofpenalty = null;
	private String penmin = null;
	private String pensec = null;
	private String minutes = null;
	private String teamname = null;
	private String playername = null;
	
	public Integer getId() {return ID;}

	public String getPensec() {
		return pensec;
	}

	public void setPensec(String value) {
		this.pensec = value;
	}
	
	public String getPenmin() {
		return penmin;
	}

	public void setPenmin(String value) {
		this.penmin = value;
	}
	
	public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String value) {
		this.teamname = value;
	}
	
	public String getPlayername() {
		return playername;
	}

	public void setPlayername(String value) {
		this.playername = value;
	}
	
	/**
	 * @return the timeofpenalty
	 */
	public String getTimeofpenalty() {
		return timeofpenalty;
	}

	/**
	 * @param timeofpenalty the timeofpenalty to set
	 */
	public void setTimeofpenalty(String timeofpenalty) {
		this.timeofpenalty = timeofpenalty;
	}

	public Penalty(int _id, Profile _pro, LiveGame _lg, ScahaTeam _tm) {
		ID = _id;	
		this.setProfile(_pro);
		this.livegame = _lg;
		this.team = _tm;
	}

	public Penalty(int _id, Profile _pro) {
		ID = _id;		
		this.setProfile(_pro);
	}
	
	//this interface is used for the box score page
	public Penalty(){
		
	}

	/**
	 * @return the team
	 */
	public ScahaTeam getTeam() {
		return team;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(ScahaTeam team) {
		this.team = team;
	}

	/**
	 * @return the livegame
	 */
	public LiveGame getLivegame() {
		return livegame;
	}

	/**
	 * @param livegame the livegame to set
	 */
	public void setLivegame(LiveGame livegame) {
		this.livegame = livegame;
	}

	
	/**
	 * @return the period
	 */
	public int getPeriod() {
		return period;
	}

	/**
	 * @param period the period to set
	 */
	public void setPeriod(int period) {
		this.period = period;
	}

	

	public void setCount(int i) {
		this.count = i;
	}
	
	public int getCount() {
		return this.count;
	}

	public void delete(ScahaDatabase db) throws SQLException {

		CallableStatement cs = db.prepareCall("call scaha.deletePenalty(?)");
		cs.setInt(1, this.ID);
		cs.execute();
		cs.close();
		
	}
	
	public void update(ScahaDatabase db) throws SQLException {
		// 
		// is it an object that is not in the database yet..
		//
		
		CallableStatement cs = db.prepareCall("call scaha.updatePenalty(?,?,?,?,?,?,?,?,?,?)");
		
		//LOGGER.info("HERE IS THE Starting Penalty ID:" + this.ID);

		int i = 1;
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(i++, this.ID);
		//LOGGER.info("A");
		cs.setInt(i++, this.idroster);
		//LOGGER.info("B");
		cs.setInt(i++, this.getLivegame().ID);
		//LOGGER.info("C");
		cs.setInt(i++, this.getTeam().ID);
		//LOGGER.info("D");
		cs.setString(i++, this.penaltytype);
		//LOGGER.info("E");
		cs.setInt(i++, this.period);
		//LOGGER.info("F");
		cs.setString(i++, this.minutes);
		//LOGGER.info("G");
		cs.setString(i++, this.timeofpenalty);
		//LOGGER.info("H");
		cs.setInt(i++,1);
		//LOGGER.info("I");
		cs.setString(i++,null);
		//LOGGER.info("J");
		cs.execute();
		
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(1);
		cs.close();

		//LOGGER.info("HERE IS THE new Penalty ID:" + this.ID);

		
	}

	/**
	 * @return the minutes
	 */
	public String getMinutes() {
		return minutes;
	}

	/**
	 * @param minutes the minutes to set
	 */
	public void setMinutes(String minutes) {
		this.minutes = minutes;
	}

	/**
	 * @return the rosterspot
	 */
	public LiveGameRosterSpot getRosterspot() {
		return rosterspot;
	}

	/**
	 * @param rosterspot the rosterspot to set
	 */
	public void setRosterspot(LiveGameRosterSpot rosterspot) {
		this.rosterspot = rosterspot;
		if (this.rosterspot != null) {
			this.idroster = this.rosterspot.ID;
		}
	}

	/**
	 * @return the idroster
	 */
	public int getIdroster() {
		return idroster;
	}

	/**
	 * @param idroster the idroster to set
	 */
	public void setIdroster(int idroster) {
		this.idroster = idroster;
	}

	
	/**
	 * @return the penaltytype
	 */
	public String getPenaltytype() {
		return penaltytype;
	}

	/**
	 * @param penaltytype the penaltytype to set
	 */
	public void setPenaltytype(String penaltytype) {
		this.penaltytype = penaltytype;
	}


}
