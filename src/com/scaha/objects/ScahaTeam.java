package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class ScahaTeam extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 45L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// This holds the Club That owns the team
	//
	private Club TeamClub = null;
	private CoachList Coachs = null;
	private ManagerList Managers = null;
	
	private Division teamdivision = null;
	private String sdivision = null;
	private SkillLevel teamskilllevel = null;
	private String sskilllevel = null;
	private String sname = null;
	private String teamname = null;
	private String teamgender = null;    
	private String seasontag = null;
	private String scheduletags = null;
	private int isexhibition = 0;
	private String skillleveltag = null;
	private String divisiontag = null;
	private String xdivisiontag = null;
	private String xskillleveltag = null;
	
	private TeamGameInfo tgi = null;
	
	private int year = 0;

	private String rowkey = "";

	public ScahaTeam (Profile _pro, int _id) {
		setProfile(_pro);
		this.ID = _id;
		this.setRowkey(_id+ "");
	}

	//use this interface for historical schedule
	public ScahaTeam (int _id) {
		this.ID = _id;
	}
	
	/**
	 * @return the teamClub
	 */
	public Club getTeamClub() {
		return TeamClub;
	}

	/**
	 * @param teamClub the teamClub to set
	 */
	public void setTeamClub(Club teamClub) {
		TeamClub = teamClub;
	}

	public ScahaCoach getHeadCoach() {
		if (this.Coachs == null) return null;
		for (ScahaCoach hc : Coachs) {
			if (hc.getGenatt().get("ROSTERTYPE").equals("Head Coach")) return hc;
		}
		return  null;
	}
	
	public ScahaManager getManager() {
		if (this.Managers == null) return null;
		for (ScahaManager hc : Managers) {
			if (hc.getGenatt().get("ROSTERTYPE").equals("Manager") || hc.getGenatt().get("ROSTERTYPE").equals("Assistant Coach/Manager")) return hc;
		}
		return  null;
	}
	
	/**
	 * @return the coachs
	 */
	public CoachList getCoachs() {
		return Coachs;
	}

	/**
	 * @param coachs the coachs to set
	 */
	public void setCoachs(CoachList coachs) {
		Coachs = coachs;
	}

	/**
	 * @return the managers
	 */
	public ManagerList getManagers() {
		return Managers;
	}

	/**
	 * @param managers the managers to set
	 */
	public void setManagers(ManagerList managers) {
		Managers = managers;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getTeamname() + " " + this.getDivisiontag() + this.getSkillleveltag();
	}

	/**
	 * @return the isexhibition
	 */
	public int getIsexhibition() {
		return isexhibition;
	}

	/**
	 * We want to know if this team is an exhibition team
	 * @return
	 */
	public boolean isExhibition() {
		return isexhibition == 1;
	}
	
	/**
	 * @param isexhibition the isexhibition to set
	 */
	public void setIsexhibition(int isexhibition) {
		this.isexhibition = isexhibition;
	}

	/**
	 * @return the scheduletags
	 */
	public String getScheduletags() {
		return scheduletags;
	}

	/**
	 * @param scheduletags the scheduletags to set
	 */
	public void setScheduletags(String scheduletags) {
		this.scheduletags = scheduletags;
	}

	/**
	 * @return the seasontag
	 */
	public String getSeasontag() {
		return seasontag;
	}

	/**
	 * @param seasontag the seasontag to set
	 */
	public void setSeasontag(String seasontag) {
		this.seasontag = seasontag;
	}

	/**
	 * @return the teamgender
	 */
	public String getTeamgender() {
		return teamgender;
	}

	/**
	 * @param teamgender the teamgender to set
	 */
	public void setTeamgender(String teamgender) {
		this.teamgender = teamgender;
	}

	/**
	 * @return the teamdivision
	 */
	public Division getTeamdivision() {
		return teamdivision;
	}

	/**
	 * @param teamdivision the teamdivision to set
	 */
	public void setTeamdivision(Division teamdivision) {
		this.teamdivision = teamdivision;
	}

	/**
	 * @return the teamskilllevel
	 */
	public SkillLevel getTeamskilllevel() {
		return teamskilllevel;
	}

	/**
	 * @param teamskilllevel the teamskilllevel to set
	 */
	public void setTeamskilllevel(SkillLevel teamskilllevel) {
		this.teamskilllevel = teamskilllevel;
	}

	/**
	 * @return the teamname
	 */
	public String getTeamname() {
		return teamname;
	}

	/**
	 * @param teamname the teamname to set
	 */
	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	/**
	 * @return the skillleveltag
	 */
	public String getSkillleveltag() {
		return skillleveltag;
	}

	/**
	 * @param skillleveltag the skillleveltag to set
	 */
	public void setSkillleveltag(String skillleveltag) {
		this.skillleveltag = skillleveltag;
	}

	/**
	 * @return the divisiontag
	 */
	public String getDivisiontag() {
		return divisiontag;
	}

	/**
	 * @param divisiontag the divisiontag to set
	 */
	public void setDivisiontag(String divisiontag) {
		this.divisiontag = divisiontag;
	}

	/**
	 * @return the sdivision
	 */
	public String getSdivision() {
		return sdivision;
	}

	/**
	 * @param sdivision the sdivision to set
	 */
	public void setSdivision(String sdivision) {
		this.sdivision = sdivision;
	}

	/**
	 * @return the sskilllevel
	 */
	public String getSskilllevel() {
		return sskilllevel;
	}

	/**
	 * @param sskilllevel the sskilllevel to set
	 */
	public void setSskilllevel(String sskilllevel) {
		this.sskilllevel = sskilllevel;
	}
	

	/**
	 * Here is where we update the object back to the database...
	 * @param _db
	 * @throws SQLException
	 */
	public void update(ScahaDatabase _db) throws SQLException {
		
		CallableStatement cs = _db.prepareCall("call scaha.updateScahaTeam(?,?,?,?,?,?,?,?,?,?,?,?)");
		
		Club cl = this.getTeamClub();
		
		if (cl == null) {
			//LOGGER.info("SCAHATEAM update.. have no reference to the Club this belongs to.. aborting the update");
			return;
		}
		
		int i = 1;
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(i++, this.ID);
		cs.setInt(i++, cl.ID);
		cs.setString(i++, this.getSname());
		cs.setString(i++, this.getTeamname());
		cs.setString(i++, this.getTeamgender());
		cs.setInt(i++, this.getTeamskilllevel().ID);
		cs.setInt(i++, this.getTeamdivision().ID);
		cs.setInt(i++, this.getIsexhibition());
		cs.setInt(i++, this.getYear());
		cs.setString(i++, this.getSeasontag());
		cs.setInt(i++,(this.isRetire() ? 0 : 1));
		cs.setString(i++,null);
		cs.execute();
				
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(1);
		cs.close();
		
	}

	/**
	 * @return the sname
	 */
	public String getSname() {
		return sname;
	}

	/**
	 * @param sname the sname to set
	 */
	public void setSname(String sname) {
		this.sname = sname;
	}

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}
	
	/**
	 * Return a "1" if I match the passed tag.. otherwise a blank will be returned
	 * 
	 * Not sure if this belongs here .. since it is for UX logic.
	 * 
	 * Should go in xBean
	 * 
	 * @param _strDivTag
	 * @param _strSkillTag
	 * @return
	 */
	public String doIMatch(String _strDivTag, String _strSkillTag) {
		
		if (this.getTeamdivision().getTag().equals(_strDivTag) && 
				this.getTeamskilllevel().getTag().equals(_strSkillTag)) return "1";
		return "";
	}
	
	/**
	 * Return a "1" if I match the passed tag.. otherwise a blank will be returned
	 * 
	 * Not sure if this belongs here .. since it is for UX logic.
	 * 
	 * Should go in xBean
	 * 
	 * @param _strDivTag
	 * @param _strSkillTag
	 * @return
	 */
	public String doIeXMatch(String _strxDivTag, String _strxSkillTag) {
		
		if (getXdivisiontag() == null || getXskillleveltag() == null) return "";
		if (this.getXdivisiontag().equals(_strxDivTag) && 
				this.getXskillleveltag().equals(_strxSkillTag)) return "1";
		return "";
	}
	
	/**
	 * @return the xdivisiontag
	 */
	public String getXdivisiontag() {
		return xdivisiontag;
	}

	/**
	 * @param xdivisiontag the xdivisiontag to set
	 */
	public void setXdivisiontag(String xdivisiontag) {
		this.xdivisiontag = xdivisiontag;
	}

	/**
	 * @return the xskillleveltag
	 */
	public String getXskillleveltag() {
		return xskillleveltag;
	}

	/**
	 * @param xskillleveltag the xskillleveltag to set
	 */
	public void setXskillleveltag(String xskillleveltag) {
		this.xskillleveltag = xskillleveltag;
	}


	/**
	 * @return the rowkey
	 */
	public String getRowkey() {
		return rowkey;
	}

	/**
	 * @param rowkey the rowkey to set
	 */
	public void setRowkey(String rowkey) {
		this.rowkey = rowkey;
	}
	
	
	/**
	 * @return the teamGameInfo
	 */
	public TeamGameInfo getTeamGameInfo() {
		if (tgi == null) {
			this.setTeamGameInfo(new TeamGameInfo(this));
		}
		return tgi;
	}

	public int getTotalGames() {
		return this.getTeamGameInfo().getAwayGames() + this.getTeamGameInfo().getHomeGames();
	}
	
	/**
	 * @param teamGameInfo the teamGameInfo to set
	 */
	public void setTeamGameInfo(TeamGameInfo teamGameInfo) {
		tgi = teamGameInfo;
	}
	
	/**
	 * Returns true if the tean is out of town this weekend..
	 * 
	 * @param _sw
	 * @return
	 */
	public boolean isOutOfTown(ScheduleWeek _sw) {
		
		for (String date : getTeamGameInfo().getHmBOD().keySet()) {
			if (date.compareTo(_sw.getFromDate().trim()) >= 0 && date.compareTo(_sw.getToDate().trim()) <= 0) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Return true if the gameCap Has been reached..
	 * 
	 * @param _se
	 * @param _op
	 * @return
	 */
	public boolean gameCapCheck(Schedule _se, ScahaTeam _op) {

		int imytot = this.getTeamGameInfo().getHomeGames() + this.getTeamGameInfo().getAwayGames();
		int ioptot = _op.getTeamGameInfo().getHomeGames() + _op.getTeamGameInfo().getAwayGames();
		int igamecap = _se.getMaxgamecnt();
		
		//LOGGER.info("gamecapcheck:mytot=" + imytot + ", ioptot=" + ioptot + ", igamecap =" + igamecap + ", teamcount=" + _se.getTeamcount());
		//
		// if we have an odd number of teams.
		//
		// then if my opponent is at the game cap .. but I am not one away.. then we have to play..
		//
		
		if (_se.getTeamcount() % 2 != 0) {
			if (imytot == igamecap + 1) {
				//LOGGER.info("gamecap alert:  I (" + this +  ") ODD I at my limit..");
				return true;
				
			}
			if (imytot == igamecap && ioptot > igamecap) {
				//LOGGER.info("gamecap alert:  I (" + this +  ") ODD am at my game cap.. my opponent is over it..");
				return true;
			}
			if (imytot == igamecap && ioptot == igamecap) {
				//LOGGER.info("gamecap alert:  I (" + this +  ") ODD am at game cap.. and my opponent is as well");
				return true;
			}
			return false;
		}
		// if either are at game cap here.. return 	
		if (_se.getTeamcount() % 2 == 0) {
			if (imytot == igamecap) {
				//LOGGER.info("gamecap alert:  I (" + this +  ") EVEN - I AM AT MY GAME CAP...");
				return true;
			}
		}
		return false;
	}
}
