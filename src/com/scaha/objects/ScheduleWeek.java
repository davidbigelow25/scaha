package com.scaha.objects;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;


import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;



public class ScheduleWeek extends ScahaObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	String SName = null;
	String Name = null;
	int Week = 0;
	String FromDate = null;
	String ToDate = null;
	String Tag = null;
	String SeasonTag = null;
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ScheduleWeek [SName=" + SName + ", Name=" + Name + ", Week="
				+ Week + ", FromDate=" + FromDate + ", ToDate=" + ToDate
				+ ", Tag=" + Tag + ", SeasonTag=" + SeasonTag + "]";
	}

	private Schedule Schedule = null;
	private boolean ScheduleComplete = false;
	
	//
	// used to help scheduling objects..
	//
	//
	// used to help scheduling objects..
	//
	ParticipantList pProc = ParticipantList.NewListFactory();
	ParticipantList pBump = ParticipantList.NewListFactory();

	int iBC = 0;

	public HashMap<String, String> hmPattern = new HashMap<String, String>(); 
	ArrayList<Integer> AvauilToPlayKeys = new ArrayList<Integer>();
	ArrayList<Integer> MatchUpKeys = new ArrayList<Integer>();

	
	public ScheduleWeek(int _id, Profile _pro) {
		this.ID = _id;
		this.setProfile(_pro);
	}

	/**
	 * @return the scheduleComplete
	 */
	public boolean isScheduleComplete() {
		return ScheduleComplete;
	}

	/**
	 * @param scheduleComplete the scheduleComplete to set
	 */
	public void setScheduleComplete(boolean scheduleComplete) {
		ScheduleComplete = scheduleComplete;
	}
	
	/**
	 * @return the sName
	 */
	public String getSName() {
		return SName;
	}

	/**
	 * @param sName the sName to set
	 */
	public void setSName(String sName) {
		SName = sName;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * @return the week
	 */
	public int getWeek() {
		return Week;
	}

	/**
	 * @param week the week to set
	 */
	public void setWeek(int week) {
		Week = week;
	}

	/**
	 * @return the fromDate
	 */
	public String getFromDate() {
		return FromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(String fromDate) {
		FromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
	public String getToDate() {
		return ToDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(String toDate) {
		ToDate = toDate;
	}

	/**
	 * @return the tag
	 */
	public String getTag() {
		return Tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		Tag = tag;
	}

	/**
	 * @return the seasonTag
	 */
	public String getSeasonTag() {
		return SeasonTag;
	}

	/**
	 * @param seasonTag the seasonTag to set
	 */
	public void setSeasonTag(String seasonTag) {
		SeasonTag = seasonTag;
	}

	public void resetBumpList() {
		pBump.reset();
	}

	public void resetProcessList() {
		pProc.reset();
	}

	public int getBumpCount () {
		return iBC;
	}
	
	public Participant getBumpListPartAtID(int _i) {
		return pBump.getByKey(_i);
	}

	public Participant getProcessListPartAtID(int _i) {
		return pProc.getByKey(_i);
	}

	public void addBumpListPart(Participant _p) {
		pBump.add(_p);
	}

	public void addPrcoessListPart(Participant _p) {
		pProc.add(_p);
	}
	
	public ParticipantList getProcessList() {
		return pProc;
	}
	public ParticipantList getBumpList() {
		return pBump;
	}

	/**
	 * @return the avauilToPlayKeys
	 */
	public ArrayList<Integer> getAvailToPlayKeys() {
		return AvauilToPlayKeys;
	}

	/**
	 * @param avauilToPlayKeys the avauilToPlayKeys to set
	 */
	public void setAvailToPlayKeys(ArrayList<Integer> availToPlayKeys) {
		AvauilToPlayKeys = availToPlayKeys;
	}
	
	
	
	/**
	 * @return the matchUpKeys
	 */
	public ArrayList<Integer> getMatchUpKeys() {
		return MatchUpKeys;
	}

	/**
	 * @param matchUpKeys the matchUpKeys to set
	 */
	public void setMatchUpKeys(ArrayList<Integer> matchUpKeys) {
		MatchUpKeys = matchUpKeys;
	}

	public boolean isAlreadyScheduled(Participant _p) {
		return pProc.getByKey(_p.ID) != null;
	}
	
	public void setAvailableMatchups(ScahaDatabase _db, Participant _p, Schedule _se) throws SQLException {
		setMatchUpKeys(_db.getAvailableMatchups(_p,_se, this, false));
	}

	public void setAvailableMatchupsSqueeze(ScahaDatabase _db, Participant _p, Schedule _se) throws SQLException {
		setMatchUpKeys(_db.getAvailableMatchups(_p,_se, this, true));
		weedOutMaxGamers(_db);
	}

	
	public boolean noMatchups() {
		
		return this.getMatchUpKeys().isEmpty();
	}
	
	public boolean alreadyProcessed(Participant _p) {
		return pProc.getByKey(_p.ID) != null;
	}
	
	public  Participant calcHomeParticipant(Participant _pMain, Participant  _pMatch, ArrayList<Integer> _aMain, ArrayList<Integer> _aMatch) {
		
	// TODO int iMain = _pMain.getTeam().getTeamGameInfo().getHomeGames();
	// TODO int iMatch = _pMatch.getTeam().getTeamGameInfo().getHomeGames();
		int iMain = 0;
		int iMatch = 0;
		
		if (_aMain.isEmpty() && !_aMatch.isEmpty()) {
			return _pMatch;
		} else 	if (!_aMain.isEmpty() && _aMatch.isEmpty()) {
			return _pMain;
		} else if (_aMain.size() < 2 && iMain < iMatch + 1) {
			return _pMain;
		} else if (_aMatch.size() < 2 && iMatch < iMain + 1) {
			return _pMatch;
		} else 	if (iMain <= iMatch && !_aMain.isEmpty()) {
			return _pMain;
		} else if (iMatch <= iMain && !_aMatch.isEmpty()) {
			return _pMatch;
		}
		return null;
		
	}
	
	public void backoutSchedule(ScahaDatabase _db, Schedule _se, Participant _p) {
		
		this.iBC++;
		this.pBump.add(_p);
		// if bump list grew.. then we have a case where we need to put this at the top top.. blow everything away
		//
		if (pBump.getRowCount() > 1) {
			//LOGGER.info("  Initiate Hard Bump Reset...");
			//_db.backoutSchedule(_se, this, true);
		} else {
			//LOGGER.info("  Initiate Soft Bump Reset...");
			//_db.backoutSchedule(_se, this, false);
		}
		
		//
		// we are not done
		//
		this.resetProcessList();
		this.setScheduleComplete(false);
	}

	
	public void backoutSchedule (ScahaDatabase _db, Schedule _se) {

		// if bump list grew.. then we have a case where we need to put this at the top top.. blow everything away
		//
		if (pBump.getRowCount() > 1) {
			//LOGGER.info("  Initiate Hard Bump Reset...");
		//	_db.backoutSchedule(_se, this, true);
		} else {
			//LOGGER.info("  Initiate Soft Bump Reset...");
		//	_db.backoutSchedule(_se, this, true);
		}
		this.resetProcessList();
		this.setScheduleComplete(false);
	}
	
	public void setAvailableToPlay(ScahaDatabase _db) throws SQLException {
		setAvailToPlayKeys(_db.getAvailableParticipants(this));
	}
	
	
	public boolean isBumpOn () {
		return pBump.getRowCount() > 0;
	}
	
	
	public void weedOutMaxGamers(ScahaDatabase _db) {
		ArrayList<Integer> am = new ArrayList<Integer>();
		for (Integer match : getMatchUpKeys()) {
			Participant pMatch = Schedule.getParticipantAtID(match.intValue());
		//	pMatch.getTeam().getTeamGameInfo().refreshInfo(_db,schedule);
		//	if (pMatch.getTeam().getTotalGames() > schedule.getGameCount()) {
		// TODO		am.add(match);
				//LOGGER.info("schedule:Removing team from matchup.. too many games:" + pMatch);
		//	}

		}
		
		//
		// Now lets weed them out man.
		//
		for (Integer match : am) {
			getMatchUpKeys().remove(match);
		}

	}

	/**
	 * @return the schedule
	 */
	public Schedule getSchedule() {
		return Schedule;
	}

	/**
	 * @param schedule the schedule to set
	 */
	public void setSchedule(Schedule schedule) {
		Schedule = schedule;
	}
	
}
