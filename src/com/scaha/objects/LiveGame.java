package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class LiveGame extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private Schedule sched = null;
	private ScahaTeam  hometeam = null;
	private ScahaTeam  awayteam = null;
	private Participant homepart = null;  // Not sure if we really need this
	private Participant awaypart = null;  // Not sure if we really need this

	private String hometeamname = null;
	private String awayteamname = null;
	private String startdate = null;
	private String starttime = null;
	private String typetag = null;
	private String statetag = null;
	private int homescore = 0;
	private int awayscore = 0;
	private String venuetag = null;
	private String sheetname = null;
	private int idgame = 0;
	private String gamenotes = null;
	private String venueshortname = null;
	private String awaystatsstatus = null;
	private String homestatsstatus = null;
	private int scheduleidstub = 0;
	private int homeclubid = 0;
	private int awayclubid = 0;
	private Boolean hostteam = null;
	
	public LiveGame(int _id, Profile _pro, Schedule _sc) {
		ID = _id;	
		idgame = _id;  // nasty kludge
		this.setProfile(_pro);
		this.setSched(_sc);
	}

	//this interface is used for loading up historical schedule
	public LiveGame(int _id) {
		ID = _id;
	}
	
	public LiveGame(int _id, Profile _pro) {
		ID = _id;		
		idgame = _id;  // nasty kludge
		this.setProfile(_pro);
	}
	/**
	 * @return the sched
	 */
	public Schedule getSched() {
		return sched;
	}
	
	public Boolean getHostteam() {
		return hostteam;
	}
	
	public void setHostteam(Boolean value) {
		hostteam=value;
	}
	
	
	
	public int getHomeclubid() {
		return homeclubid;
	}
	
	public void setHomeclubid(int value) {
		homeclubid=value;
	}
	
	public int getAwayclubid() {
		return awayclubid;
	}
	
	public void setAwayclubid(int value) {
		awayclubid=value;
	}
	
	public int getGameId() {
		return ID;
	}

	/**
	 * @param sched the sched to set
	 */
	public void setSched(Schedule sched) {
		this.sched = sched;
	}

	/**
	 * @return the hometeam
	 */
	public ScahaTeam getHometeam() {
		return hometeam;
	}

	/**
	 * @param hometeam the hometeam to set
	 */
	public void setHometeam(ScahaTeam hometeam) {
		this.hometeam = hometeam;
	}

	/**
	 * @return the awayteam
	 */
	public ScahaTeam getAwayteam() {
		return awayteam;
	}

	/**
	 * @param awayteam the awayteam to set
	 */
	public void setAwayteam(ScahaTeam awayteam) {
		this.awayteam = awayteam;
	}

	/**
	 * @return the homepart
	 */
	public Participant getHomepart() {
		return homepart;
	}

	/**
	 * @param homepart the homepart to set
	 */
	public void setHomepart(Participant homepart) {
		this.homepart = homepart;
	}

	/**
	 * @return the awaypart
	 */
	public Participant getAwaypart() {
		return awaypart;
	}

	/**
	 * @param awaypart the awaypart to set
	 */
	public void setAwaypart(Participant awaypart) {
		this.awaypart = awaypart;
	}

	/**
	 * @return the hometeamname
	 */
	public String getHometeamname() {
		return hometeamname;
	}

	/**
	 * @param hometeamname the hometeamname to set
	 */
	public void setHometeamname(String hometeamname) {
		this.hometeamname = hometeamname;
	}

	/**
	 * @return the awayteamname
	 */
	public String getAwayteamname() {
		return awayteamname;
	}

	/**
	 * @param awayteamname the awayteamname to set
	 */
	public void setAwayteamname(String awayteamname) {
		this.awayteamname = awayteamname;
	}

	/**
	 * @return the startdate
	 */
	public String getStartdate() {
		return startdate;
	}

	/**
	 * @param startdate the startdate to set
	 */
	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	/**
	 * @return the starttime
	 */
	public String getStarttime() {
		return starttime;
	}

	/**
	 * @param starttime the starttime to set
	 */
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}

	/**
	 * @return the typetag
	 */
	public String getTypetag() {
		return typetag;
	}

	/**
	 * @param typetag the typetag to set
	 */
	public void setTypetag(String typetag) {
		this.typetag = typetag;
	}

	/**
	 * @return the statetag
	 */
	public String getStatetag() {
		return statetag;
	}

	/**
	 * @param statetag the statetag to set
	 */
	public void setStatetag(String statetag) {
		this.statetag = statetag;
	}

	/**
	 * @return the homescore
	 */
	public int getHomescore() {
		return homescore;
	}

	/**
	 * @param homescore the homescore to set
	 */
	public void setHomescore(int homescore) {
		this.homescore = homescore;
	}

	/**
	 * @return the awayscore
	 */
	public int getAwayscore() {
		return awayscore;
	}

	/**
	 * @param awayscore the awayscore to set
	 */
	public void setAwayscore(int awayscore) {
		this.awayscore = awayscore;
	}

	/**
	 * @return the venuetag
	 */
	public String getVenuetag() {
		return venuetag;
	}

	/**
	 * @param venuetag the venuetag to set
	 */
	public void setVenuetag(String venuetag) {
		this.venuetag = venuetag;
	}

	/**
	 * @return the sheetname
	 */
	public String getSheetname() {
		return sheetname;
	}

	/**
	 * @param sheetname the sheetname to set
	 */
	public void setSheetname(String sheetname) {
		this.sheetname = sheetname;
	}

	public String toString() {
		return this.getHometeamname() + "vs" + this.getAwayteamname() + " @ " + this.venuetag + " " + this.startdate + ":" + this.starttime;
	}

	/**
	 * @return the scheduleidstub
	 */
	public int getScheduleidstub() {
		return scheduleidstub;
	}

	/**
	 * @param scheduleidstub the scheduleidstub to set
	 */
	public void setScheduleidstub(int scheduleidstub) {
		this.scheduleidstub = scheduleidstub;
	}

	/**
	 * @return the idgame
	 */
	public int getIdgame() {
		return idgame;
	}

	/**
	 * @param idgame the idgame to set
	 */
	public void setIdgame(int idgame) {
		this.idgame = idgame;
	}

	/**
	 * @return the gamenotes
	 */
	public String getGamenotes() {
		return gamenotes;
	}

	/**
	 * @param gamenotes the gamenotes to set
	 */
	public void setGamenotes(String gamenotes) {
		this.gamenotes = gamenotes;
	}
	
	/**
	 * This guy updates a livegame
	 * @param _db
	 * @param _trkchanges
	 * @throws SQLException 
	 */
	public void update(ScahaDatabase _db,boolean _trkchanges) throws SQLException {

		CallableStatement ps = _db.prepareCall("call scaha.updateLiveGame(?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		int i = 1;
		ps.setInt(i++,(_trkchanges ? 1 :0));
		ps.setInt(i++,ID);
		ps.setInt(i++, this.getHometeam().ID);
		ps.setInt(i++, this.getAwayteam().ID);
		ps.setString(i++, this.getTypetag());
		ps.setString(i++, this.getStatetag());
		ps.setString(i++, this.getVenuetag());
		ps.setString(i++, this.getSheetname());
		ps.setString(i++, this.getStartdate());
		ps.setString(i++, this.getStarttime());
		ps.setInt(i++, this.getHomescore());
		ps.setInt(i++, this.getAwayscore());
		ps.setString(i++, this.getGamenotes());
		ps.execute();
		
		
		
	}

	/**
	 * @return the venueshortname
	 */
	public String getVenueshortname() {
		return venueshortname;
	}

	/**
	 * @param venueshortname the venueshortname to set
	 */
	public void setVenueshortname(String venueshortname) {
		this.venueshortname = venueshortname;
	}

	public String getHomestatsstatus() {
		return homestatsstatus;
	}

	/**
	 * @param venueshortname the venueshortname to set
	 */
	public void setHomestatsstatus(String venueshortname) {
		this.homestatsstatus = venueshortname;
	}
	
	public String getAwaystatsstatus() {
		return awaystatsstatus;
	}

	/**
	 * @param venueshortname the venueshortname to set
	 */
	public void setAwaystatsstatus(String venueshortname) {
		this.awaystatsstatus = venueshortname;
	}
	
	public boolean isCancelled() {
		return this.getStatetag().equals("Cancelled");
	}
	
	public void updatTeamstatsstatus(ScahaDatabase _db,String homeaway) throws SQLException {

		CallableStatement psa = _db.prepareCall("call scaha.setAwaystatsstatus(?)");
		CallableStatement psh = _db.prepareCall("call scaha.setHomestatsstatus(?)");
		
		if (homeaway.equals("H")){
			psh.setInt(1,ID);
			psh.execute();
		} else {
			psa.setInt(1,ID);
			psa.execute();
		}
		
	}
	
	public void updatGameFinalStatus(ScahaDatabase _db) throws SQLException {

		CallableStatement psa = _db.prepareCall("call scaha.setLiveGameFinal(?)");
		
		psa.setInt(1,ID);
		psa.execute();
		
		
	}
}
