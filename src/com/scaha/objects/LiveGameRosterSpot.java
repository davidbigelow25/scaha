package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class LiveGameRosterSpot extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private ScahaTeam  team = null;
	private LiveGame livegame = null;
	private boolean mia = false;
	private int idRoster = 0;
	private int idPerson = 0;
	private String jerseynumber = "";
	private String fname = "";
	private String lname = "";
	private String rostertype = "";
	
	private String tag = "";
	
	public LiveGameRosterSpot(int _id, Profile _pro, Schedule _sc) {
		ID = _id;		
		this.setProfile(_pro);
	}

	public LiveGameRosterSpot(int _id, Profile _pro) {
		ID = _id;		
		this.setProfile(_pro);
	}
	
	public int getGameId() {
		return ID;
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
	 * @return the idRoster
	 */
	public int getIdRoster() {
		return idRoster;
	}

	/**
	 * @param idRoster the idRoster to set
	 */
	public void setIdRoster(int idRoster) {
		this.idRoster = idRoster;
	}

	/**
	 * @return the mia
	 */
	public boolean isMia() {
		return mia;
	}

	/**
	 * @param mia the mia to set
	 */
	public void setMia(boolean mia) {
		this.mia = mia;
	}

	/**
	 * @return the jerseynumber
	 */
	public String getJerseynumber() {
		return jerseynumber;
	}

	/**
	 * @param jerseynumber the jerseynumber to set
	 */
	public void setJerseynumber(String jerseynumber) {
		this.jerseynumber = jerseynumber;
	}
	
	

	/**
	 * @return the fname
	 */
	public String getFname() {
		return fname;
	}

	/**
	 * @param fname the fname to set
	 */
	public void setFname(String fname) {
		this.fname = fname;
	}

	/**
	 * @return the lname
	 */
	public String getLname() {
		return lname;
	}

	/**
	 * @param lname the lname to set
	 */
	public void setLname(String lname) {
		this.lname = lname;
	}

	/**
	 * @return the idPerson
	 */
	public int getIdPerson() {
		return idPerson;
	}

	/**
	 * @param idPerson the idPerson to set
	 */
	public void setIdPerson(int idPerson) {
		this.idPerson = idPerson;
	}

	/**
	 * @return the rostertype
	 */
	public String getRostertype() {
		return rostertype;
	}

	/**
	 * @param rostertype the rostertype to set
	 */
	public void setRostertype(String rostertype) {
		this.rostertype = rostertype;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LiveGameRosterSpot [team=" + team + ", livegame=" + livegame
				+ ", mia=" + mia + ", idRoster=" + idRoster + ", idPerson="
				+ idPerson + ", jerseynumber=" + jerseynumber + ", fname="
				+ fname + ", lname=" + lname + ", rostertype=" + rostertype
				+ "]";
	}
	
	public int getKey() {
		return ID;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
	

}
