package com.scaha.objects;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * @author David
 *
 */
public class TeamGameInfo extends ScahaObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private ScahaTeam Team = null;
	
	private int TotalGames = 0;
	private int HomeGames = 0;
	private int AwayGames = 0;
	private int ExGames = 0;
	private boolean BODLoaded = false;
	private HashMap<String,String> hmBOD = new HashMap<String, String>();

	
	/**
	 * @return the exGames
	 */
	public int getExGames() {
		return ExGames;
	}
	/**
	 * @param exGames the exGames to set
	 */
	public void setExGames(int exGames) {
		ExGames = exGames;
	}
	/**
	 * @return the hmBOD
	 */
	public HashMap<String, String> getHmBOD() {
		return hmBOD;
	}
	/**
	 * @param hmBOD the hmBOD to set
	 */
	public void setHmBOD(HashMap<String, String> hmBOD) {
		this.hmBOD = hmBOD;
	}
	public TeamGameInfo (ScahaTeam _tm) {
		this.setTeam(_tm);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * refreshInfo.. This gives us the latest information about a team that we need to track from the database
	 * 
	 * @param _db
	 * @throws SQLException 
	 */
	public void refreshInfo(ScahaDatabase _db, Schedule _se) throws SQLException {
		
		_db.refresh(this, _se);
		
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TeamGameInfo [TotalGames=" + TotalGames + ", HomeGames="
				+ HomeGames + ", AwayGames=" + AwayGames + ", ExGames=" + ExGames + ", hmBOD=" + hmBOD + "]";
	}
	/**
	 * @return the homeGames
	 */
	public int getHomeGames() {
		return HomeGames;
	}

	/**
	 * @param homeGames the homeGames to set
	 */
	public void setHomeGames(int homeGames) {
		HomeGames = homeGames;
	}

	/**
	 * @return the awayGames
	 */
	public int getAwayGames() {
		return AwayGames;
	}

	/**
	 * @param awayGames the awayGames to set
	 */
	public void setAwayGames(int awayGames) {
		AwayGames = awayGames;
	}

	/**
	 * @return the totalGames
	 */
	public int getTotalGames() {
		return TotalGames;
	}
	/**
	 * @param totalGames the totalGames to set
	 */
	public void setTotalGames(int totalGames) {
		TotalGames = totalGames;
	}
	/**
	 * @return the team
	 */
	public ScahaTeam getTeam() {
		return Team;
	}
	/**
	 * @param team the team to set
	 */
	public void setTeam(ScahaTeam team) {
		Team = team;
	}
	/**
	 * @return the bODLoaded
	 */
	public boolean isBODLoaded() {
		return BODLoaded;
	}
	/**
	 * @param bODLoaded the bODLoaded to set
	 */
	public void setBODLoaded(boolean bODLoaded) {
		BODLoaded = bODLoaded;
	}

}
