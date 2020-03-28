/**
 * 
 */
package com.gbli.connectors;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.common.ReturnDataResultSet;
import com.gbli.common.ReturnDataRow;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.GeneralSeason;
import com.scaha.objects.Participant;
import com.scaha.objects.Person;
import com.scaha.objects.Profile;
import com.scaha.objects.ScahaTeam;
import com.scaha.objects.Schedule;
import com.scaha.objects.ScheduleWeek;
import com.scaha.objects.Slot;
import com.scaha.objects.TeamGameInfo;

import java.sql.PreparedStatement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.mail.internet.InternetAddress;

/**
 * @author dbigelow
 * 
 */
public class ScahaDatabase extends Database {

	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// Stored Procedure Strings.
	//
	public static String c_sp_profile = "Call scaha.profile(?,?)";
	public static String c_sp_actionlist = "Call scaha.getActionTree(?)";
	public static String c_PS_CHECK_FOR_USER = "Call scaha.checkForUser(?)";
	public static String cs_UPDATE_CLUB_STAFFER = "call scaha.updateClubStaffer(?,?,?,?)";
	public static String c_GET_TEAM_GAME_COUNT_BY_SCHED = "call scaha.getTeamGameCountsBySchedule(?,?)";
	public static String c_GET_DATES_TEAM_GAME = "call scaha.getDatesTeamNotAvailable(?)";
	public static String c_GET_AVAILABLE_PARTICIPANTS = "call scaha.getAvailableParticipants(?,?)";
	public static String c_GET_AVAILABLE_MATCHUPS = "call scaha.getAvailableMatchups(?,?,?,?,?,?,?)";
	public static String c_GET_AVAILABLE_SLOTIDS = "call scaha.getAvailableSlotIDs(?,?,?)";
	public static String c_ARE_CLUBS_BLOCKED_FOR_SCHEDULE = "call areClubsBlockedForSchedule(?,?,?)";
	public static String c_GET_ALL_AVAILABLE_SLOTS = "call scaha.getAllavailableSlots(?,?)";
	public static String c_GET_ALL_USED_SLOTS = "call scaha.getAllUsedSlots(?,?)";
	public static String c_GET_SLOTS_FOR_MATCHUP = "call scaha.getSlotsForMatchup(?)";
	public static String c_GET_HOME_ONLY_DATES = "call scaha.getHomeOnlyDates(?)";
	public static String c_GET_SLOT_DATE = "call scaha.getSlotDate(?)";
	public static String c_GET_CLUB_OFF_DATES = "call scaha.getClubOffDates(?)";
	public static String c_GET_HOME_AWAY_BALANCE = "call scaha.getHomeAwayBalance(?,?,?)";
	public static String c_GET_GAMEID_FOR_SLOT = "call scaha.getGameIDForSlot(?)";
	public static String c_SCHEDULE_GAME = "call scaha.scheduleGameForSeason(?,?,?,?,?,?,?,?)";
	public static String c_SYS_CONTROL = "call scaha.getSysControlRequest(?)";
	
	
	PreparedStatement ps_TeamGameCountBySched = null;
	PreparedStatement ps_DatesTeamGone = null;
	PreparedStatement ps_GetAvailableParticipants = null;
	PreparedStatement ps_GetAvailableMatchups = null;
	PreparedStatement ps_GetAvailableSlotIds = null;
	PreparedStatement ps_AreClubsBlockedForSchedule = null;
	PreparedStatement ps_GetAllAvailableSlots = null;
	PreparedStatement ps_GetAllUsedSlots = null;
	PreparedStatement ps_GetSlotsForMatchup = null;
	PreparedStatement ps_GetHomeOnlyDates = null;
	PreparedStatement ps_GetSlotDate = null;
	PreparedStatement ps_GetClubOffDates = null;
	PreparedStatement ps_getHomeAwayBalance = null;
	PreparedStatement ps_getGameIdForSlot = null;
	PreparedStatement cs_ScheduleGame = null;
	PreparedStatement ps_sysControRequest = null;

	public ScahaDatabase(int _iId, String _sDriver, String _sURL, String _sUser, String _sPwd) {
		super(_iId, _sDriver, _sURL, _sUser, _sPwd);
		
		try {
			ps_TeamGameCountBySched = this.prepareStatement(c_GET_TEAM_GAME_COUNT_BY_SCHED);
			ps_DatesTeamGone = this.prepareStatement(c_GET_DATES_TEAM_GAME);
			ps_GetAvailableParticipants = this.prepareStatement(c_GET_AVAILABLE_PARTICIPANTS);
			ps_GetAvailableMatchups = this.prepareStatement(c_GET_AVAILABLE_MATCHUPS);
			ps_GetAvailableSlotIds = this.prepareStatement(c_GET_AVAILABLE_SLOTIDS);
			ps_AreClubsBlockedForSchedule = this.prepareStatement(c_ARE_CLUBS_BLOCKED_FOR_SCHEDULE);
			ps_GetAllAvailableSlots = this.prepareStatement(c_GET_ALL_AVAILABLE_SLOTS);
			ps_GetAllUsedSlots = this.prepareStatement(c_GET_ALL_USED_SLOTS);
			ps_GetSlotsForMatchup = this.prepareStatement(c_GET_SLOTS_FOR_MATCHUP);
			ps_GetHomeOnlyDates = this.prepareStatement(c_GET_HOME_ONLY_DATES);
			ps_GetSlotDate = this.prepareStatement(c_GET_SLOT_DATE);			
			ps_GetClubOffDates = this.prepareStatement(c_GET_CLUB_OFF_DATES);			
			ps_getHomeAwayBalance = this.prepareStatement(c_GET_HOME_AWAY_BALANCE);			
			ps_getGameIdForSlot = this.prepareStatement(c_GET_GAMEID_FOR_SLOT);			
			cs_ScheduleGame = this.prepareStatement(c_SCHEDULE_GAME);	
			ps_sysControRequest = this.prepareStatement(c_SYS_CONTROL);	

		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public ScahaDatabase(String _sDriver, String _sURL, String _sUser, String _sPwd) {
		super(_sDriver, _sURL, _sUser, _sPwd);
		try {
			
			ps_TeamGameCountBySched = this.prepareStatement(c_GET_TEAM_GAME_COUNT_BY_SCHED);
			ps_DatesTeamGone = this.prepareStatement(c_GET_DATES_TEAM_GAME);
			ps_GetAvailableParticipants = this.prepareStatement(c_GET_AVAILABLE_PARTICIPANTS);
			ps_GetAvailableMatchups = this.prepareStatement(c_GET_AVAILABLE_MATCHUPS);
			ps_GetAvailableSlotIds = this.prepareStatement(c_GET_AVAILABLE_SLOTIDS);
			ps_AreClubsBlockedForSchedule = this.prepareStatement(c_ARE_CLUBS_BLOCKED_FOR_SCHEDULE);
			ps_GetAllAvailableSlots = this.prepareStatement(c_GET_ALL_AVAILABLE_SLOTS);
			ps_GetAllUsedSlots = this.prepareStatement(c_GET_ALL_USED_SLOTS);
			ps_GetSlotsForMatchup = this.prepareStatement(c_GET_SLOTS_FOR_MATCHUP);
			ps_GetHomeOnlyDates = this.prepareStatement(c_GET_HOME_ONLY_DATES);
			ps_GetSlotDate = this.prepareStatement(c_GET_SLOT_DATE);			
			ps_GetClubOffDates = this.prepareStatement(c_GET_CLUB_OFF_DATES);			
			ps_getHomeAwayBalance = this.prepareStatement(c_GET_HOME_AWAY_BALANCE);			
			ps_getGameIdForSlot = this.prepareStatement(c_GET_GAMEID_FOR_SLOT);			
			cs_ScheduleGame = this.prepareCall(c_SCHEDULE_GAME);		
			ps_sysControRequest = this.prepareStatement(c_SYS_CONTROL);	

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This will check to see if the user has the proper credentials and will
	 * set up the result set for interrogation if true
	 * 
	 * @param _strUser
	 * @param _strPass
	 * @return
	 */
	public final boolean verify(String _sUser, String _sPass) {

		Vector<String> v = new Vector<String>();
		v.add(_sUser);
		v.add(_sPass);
		LOGGER.info("db.verify: Parms are:" + _sUser + ":" + _sPass);
		return super.getData(c_sp_profile, v);

	}

	/**
	 * This will check to see if the user has the proper credentials and will
	 * set up the result set for interrogation if true
	 * 
	 * @param _strUser
	 * @param _strPass
	 * @return
	 */
	public final boolean getActionData(int _ipid) {

		Vector<Integer> v = new Vector<Integer>();
		v.add(Integer.valueOf(_ipid));

		return getData(c_sp_actionlist, v);

	}
	
	/**
	 *  General utility to check to see if  this person already exists in the database as a person..
	 *  
	 * @param _sfname
	 * @param _slname
	 * @param _sDOB
	 * @return
	 * @throws SQLException 
	 */
	public boolean checkForPersonByFLDOB(String _sfname, String _slname, String _sDOB) throws SQLException {

		String strAnswer = "N";
		CallableStatement cs = this.prepareCall("call scaha.checkforPersonByFLDOB(?,?,?,?)");
		
		LOGGER.info("FLDOB:" + _sfname + ":" + _slname + ":" + _sDOB); 
		cs.registerOutParameter(1, java.sql.Types.VARCHAR);
		cs.setString(1, strAnswer);
		cs.setString(2,  _sfname);
		cs.setString(3,  _slname);
		cs.setString(4,  _sDOB);
		cs.execute();
		strAnswer = cs.getString(1);
		cs.close();
		LOGGER.info("FLDOB:" + _sfname + ":" + _slname + ":" + _sDOB + ":returns=" + strAnswer); 
		return strAnswer.equals("Y");
		
	}
	
	/**
	 *  General utility to check to see if  this person has yet to verify his bc
	 *  
	 * @param _sfname
	 * @param _slname
	 * @param _sDOB
	 * @return
	 * @throws SQLException 
	 */
	public boolean checkForBC(int _idPerson) throws SQLException {

		String strAnswer = "N";
		CallableStatement cs = this.prepareCall("call scaha.checkForBC(?,?)");
		cs.setInt(1, _idPerson);
		cs.registerOutParameter(2, java.sql.Types.VARCHAR);
		cs.setString(2, strAnswer);
		cs.execute();
		strAnswer = cs.getString(2);
		cs.close();
		return strAnswer.equals("Y");
		
	}
	
	/**
	 *  General utility to check to see if  this person has yet to verify his bc
	 *  
	 * @param _sfname
	 * @param _slname
	 * @param _sDOB
	 * @return
	 * @throws SQLException 
	 */
	public boolean isPersonPlayer(int _idPerson) throws SQLException {

		String strAnswer = "N";
		CallableStatement cs = this.prepareCall("call scaha.isPersonPlayer(?,?)");
		cs.setInt(1, _idPerson);
		cs.registerOutParameter(2, java.sql.Types.VARCHAR);
		cs.setString(2, strAnswer);
		cs.execute();
		strAnswer = cs.getString(2);
		cs.close();
		return strAnswer.equals("Y");
		
	}
	
	/**
	 *  General utility to check to see if  this person has yet to verify his bc
	 *  
	 * @param _sfname
	 * @param _slname
	 * @param _sDOB
	 * @return
	 * @throws SQLException 
	 */
	public boolean isTeamEmpty(int _idTeam) throws SQLException {

		String strAnswer = "N";
		CallableStatement cs = this.prepareCall("call scaha.isTeamEmpty(?,?)");
		cs.setInt(1, _idTeam);
		cs.registerOutParameter(2, java.sql.Types.VARCHAR);
		cs.setString(2, strAnswer);
		cs.execute();
		strAnswer = cs.getString(2);
		cs.close();
		return strAnswer.equals("Y");
		
	}

	public void updateClubPresident(Profile profile, Club _c, Person _oldPrez, Person _newPrez) throws SQLException {
		CallableStatement cs = this.prepareCall(ScahaDatabase.cs_UPDATE_CLUB_STAFFER);
		cs.setInt(1, _c.ID);
		cs.setInt(2, (_oldPrez == null ? 0 :_oldPrez.ID));
		cs.setInt(3, _newPrez.ID);
		cs.setString(4,"C-PRES");
		cs.execute();
		cs.close();
	}

	public void updateClubRegistrar(Profile profile, Club _c, Person _oldPrez, Person _newPrez) throws SQLException {
		CallableStatement cs = this.prepareCall(ScahaDatabase.cs_UPDATE_CLUB_STAFFER);
		cs.setInt(1, _c.ID);
		cs.setInt(2, (_oldPrez == null ? 0 :_oldPrez.ID));
		cs.setInt(3, _newPrez.ID);
		cs.setString(4,"C-REG");
		cs.execute();
		cs.close();
	}

	public void updateClubIceConvenor(Profile profile, Club _c, Person _oldPrez, Person _newPrez) throws SQLException {
		CallableStatement cs = this.prepareCall(ScahaDatabase.cs_UPDATE_CLUB_STAFFER);
		cs.setInt(1, _c.ID);
		cs.setInt(2, (_oldPrez == null ? 0 :_oldPrez.ID));
		cs.setInt(3, _newPrez.ID);
		cs.setString(4,"C-ICE");
		cs.execute();
		cs.close();
	}

	public List<InternetAddress> getClubFamilyEmails(Club c,	GeneralSeason _cs)  throws SQLException {
		List<InternetAddress> tmp = new ArrayList<InternetAddress>();
		PreparedStatement ps = this.prepareStatement("call scaha.getAllMemberEmailsByClubAndSeason(?,?)");
		ps.setInt(1, c.ID);
		ps.setString(2,_cs.getTag()); 
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			LOGGER.info("getCLubFamilyEmails:" + rs.getString(1) + ":" +  rs.getString(2));
			try {
				tmp.add(new InternetAddress(rs.getString(2),rs.getString(1)));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				LOGGER.info(e.getMessage());
			}
		}
		rs.close();
		ps.close();

		return tmp;
	}

	public List<InternetAddress> getRenegadeFamilyEmails(GeneralSeason currentSeason) throws SQLException, UnsupportedEncodingException {
		
		List<InternetAddress> tmp = new ArrayList<InternetAddress>();
		
		PreparedStatement ps = this.prepareStatement("call scaha.getAllMemberEmailsByNoTeamAndSeason(?)");
		ps.setString(1,currentSeason.getTag()); 
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			LOGGER.info("getRenegadeFamilyEmails:" + rs.getString(1) + ":" +  rs.getString(2));
			tmp.add(new InternetAddress(rs.getString(2),rs.getString(1)));
		}
		rs.close();
		ps.close();

		return tmp;
	}

	public List<InternetAddress> getOutstandingMemberSignupEmails() throws SQLException, UnsupportedEncodingException {

		List<InternetAddress> tmp = new ArrayList<InternetAddress>();
		
		PreparedStatement ps = this.prepareStatement("call scaha.getOutstandingMemberSignupEmails()");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			LOGGER.info("getOutstandingMemberSignupEmails:" + rs.getString(1) + ":" +  rs.getString(2));
			tmp.add(new InternetAddress(rs.getString(2),rs.getString(1)));
		}
		rs.close();
		ps.close();

		return tmp;
	}
	
	
	/**
	 * This is a DB manipulation Method that syncs all the Teams to a Certain Schedule
	 * Based upon what they have declared.. 
	 * 
	 * @param _sc
	 */
	public void syncTeamsToSchedule (Schedule _sc, GeneralSeason _gs) throws SQLException {
		
		LOGGER.info("Searching for new teams and defunct teams for Schedule " + _sc);
		
		PreparedStatement ps = this.prepareStatement("call scaha.genParticipantChanges(?,?,?)");
		CallableStatement csAdd = this.prepareCall("call scaha.addParticipantToSchedule(?,?)");
		CallableStatement csRemove = this.prepareCall("call scaha.removeParticipantFromSchedule(?,?)");
		ps.setInt(1, _sc.ID);
		ps.setString(2,_sc.getTag());
		ps.setString(3,_gs.getTag());
		ResultSet rs = ps.executeQuery();
		
		ReturnDataResultSet rdrs = ReturnDataResultSet.NewReturnDataResultSetFactory(rs);
		rs.close();

		for (ReturnDataRow row : rdrs) {
			String sCode = (String)row.elementAt(0);
			int idTeam = Integer.parseInt(row.elementAt(1).toString());
			String sName = (String)row.elementAt(2);
			LOGGER.info("Hello There:" + sCode + ":" + idTeam + ":" + sName);
			if (sCode.equals("D")) {
				LOGGER.info("Found a defunct team id[" + idTeam + "] cleaning up for schedule " + _sc);
				csRemove.setInt(1,idTeam);
				csRemove.setInt(2,_sc.ID);
				csRemove.execute();
			} else if (sCode.equals("A")) {
				LOGGER.info("Adding Team [" + sName + "] to the participants for schedule: " + _sc.getTag());
				csAdd.setInt(1,idTeam);
				csAdd.setInt(2,_sc.ID);
				csAdd.execute();
			}
		}
		
		ps.close();
		csAdd.close();
		csRemove.close();
		this.cleanup();
		
		LOGGER.info("sychTeamSeeding: Reviewing rankings to ensure they are sequential for Schedule: " + _sc);
		//
		// We just keep reexecuting this until we are good.
		//
		PreparedStatement ps1 = this.prepareStatement("call scaha.getParticipantKeysAndRankBySchedule(?)");
		CallableStatement csFixGap = this.prepareCall("call scaha.updateParticipantRankingForSchedule(?,?,?,?)");
		ps1.setInt(1,_sc.ID);
		
		boolean bwork = true;
		while (bwork) {
			bwork = false;

			int itargetrank = 0;
			int irank = 1;
			int ioldrank = 0;
			int ikey = 0;
			
			//
			// rerun sql..
			//
			rs = ps1.executeQuery();
			while (rs.next()) {
				ioldrank = rs.getInt(2);
				if (irank != ioldrank && itargetrank == 0 ) {
					itargetrank = irank;
					LOGGER.info("syncTeamSeeding: Found a ranking gap in Schedule " + _sc  + ". " + rs.getInt(1) + ":" + rs.getInt(2) + ": should be:" + irank);
					bwork = true;
				}
				ikey = rs.getInt(1);
				irank++;
			}	
			rs.close();

			//
			// If there is work to do.. then we do it.
			//
			if (bwork) {
				LOGGER.info("Re-Gapping Participate " + ikey + "] from rank [" + ioldrank + ":" + itargetrank + "] for schedule: " + _sc);
				csFixGap.setInt(1,ikey);
				csFixGap.setInt(2,_sc.ID);
				csFixGap.setInt(3,ioldrank);
				csFixGap.setInt(4,itargetrank);
				csFixGap.execute();
			}
		}
		
		ps1.close();
		csFixGap.close();

		CallableStatement csTcs = this.prepareCall("call scaha.updateScheduleTeamCounts(?)");
		csTcs.setInt(1,_sc.ID);
		csTcs.execute();
		csTcs.close();
		this.cleanup();
		
	}
	
	
	
	/**
	 * genGames
	 * 
	 * This will take a look at the season.. how many teams.. and how many games they have to play.. and generate 
	 * enough games to cover the season through iteration...
	 * 
	 * if the iteration of games are smaller than the number of teams based upon the round robin matrix
	 * then we generate another iteration.. this makes sure we have enough games to pull from to piece the matchups 
	 * together.
	 * 
	 * We always want more games to choose from then possible games for the weekend.
	 * @param _se
	 */
	public void genGames(Schedule _sc) throws SQLException {
		
		LOGGER.info("genGames: Starting to Gen games for Schedule "  + _sc);
		CallableStatement csDelete = this.prepareCall("call scaha.removeGamesByScheduleIteration(?,?)");
		CallableStatement csInsert = this.prepareCall("call scaha.insertGamesByScheduleIteration(?,?)");
		
		// We will always have at least one iteration.
		int iCount = 0;
		int iGames = _sc.getMingamecnt();
		int iGamesPerIteration = _sc.getTeamcount() - 1 ;
		
		LOGGER.info("genGames: Check for " + _sc + ". iCount=" + iCount + ": iGames=" + iGames + ": Team Count-1=" + iGamesPerIteration);
			
		//
		// Lets calculate the number of iterations now..
		//
		while (iGames > (iGamesPerIteration*iCount)) {
			iCount++;
			int i=1;
			csInsert.setInt(i++, _sc.ID);
			csInsert.setInt(i++, iCount);
			csInsert.execute();
			LOGGER.info("genGames: Inserted missing games for iteration :" + iCount + ": for schedule "  + _sc);
		}
		
		csDelete.close();
		csInsert.close();
		
		LOGGER.info("genGames: Inserted missing games finished for season " + _sc);
						
	}	
	
	/**
	 * This little guy will rip through all the seasons that a club participates in and will generate all the slots that
	 * are missing.  There are generic slots that do not care about blackout weekends of clubs, etc.  The slots that get 
	 * generated from here can be played any time.. at any venue.  The scheduling engine does this for them.
	 * @throws SQLException 
	 * 
	 */
	public void syncSlotsToClub(Club _cl, GeneralSeason _gs) throws SQLException {
	
		LOGGER.info("syncSlotsToClub: Reviewing slot requirements for Club:" + _cl);
		
		PreparedStatement ps1 = this.prepareStatement("call scaha.getSlotTemplateForClub(?,?,?)");
		CallableStatement cs1 = this.prepareCall("call scaha.syncSlotsForClubByRank(?,?,?,?,?,?)");
		CallableStatement cs2 = this.prepareCall("call scaha.removeExcessSlotsByClub(?,?,?,?,?,?)");

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
		//
		// lets get the information for the outside loop now..
		//
		ArrayList<String> dates = getWeekendsForAllSchedules(_gs);
		
		//
		// Lets remove too early and too late
		//
		String sEarliestDate = dates.get(0);
		String sLatestDate = dates.get(dates.size() -1);
		pruneSlots(_cl,_gs, sEarliestDate, sLatestDate);
		
		for (String date : dates) {
			ps1.setInt(1, _cl.ID);
			ps1.setString(2,_gs.getTag());
			ps1.setString(3,date);
			LOGGER.info("syncSlotsToClub: calling getSlotTemplateForClub for Club: " + _cl + ", date=" + date);
			ResultSet rs = ps1.executeQuery();
			ReturnDataResultSet rdrs = ReturnDataResultSet.NewReturnDataResultSetFactory(rs);
			rs.close();
			LOGGER.info("syncSlotsToClub: returned from call getSlotTemplateForClub for Club: " + _cl + ", date=" + date);

			for (ReturnDataRow rdr : rdrs) {
				int i=0;
				String sFromDate = df.format((Date)rdr.elementAt(i++));
				LOGGER.info("syncSlotsToClub: sFromDate is:" + sFromDate);
				String sToDate = df.format((Date)rdr.elementAt(i++));
				LOGGER.info("syncSlotsToClub: sToDate is:" + sToDate);
				String sGameTag = (String)rdr.elementAt(i++);
				LOGGER.info("syncSlotsToClub: GameTag is:" + sGameTag);
				int iSlotCount =  Integer.parseInt(rdr.elementAt(i++).toString());
				LOGGER.info("syncSlotsToClub: iSlotCount is:" + iSlotCount);
				LOGGER.info("syncSlotsToClub: Slot req for club " + _cl + " are sc=" + iSlotCount + ". fdate=" + sFromDate + ". todate=" + sToDate + ". gl=" + sGameTag);
				for (int c = 1;c<=iSlotCount;c++) {
					i=1;
					cs1.setInt(i++,_cl.ID);
					cs1.setString(i++,_gs.getTag());
					cs1.setString(i++,sFromDate);
					cs1.setString(i++,sToDate);
					cs1.setInt(i++,c);
					cs1.setString(i++,sGameTag);
					cs1.execute();
				}
				//
				// ok.. for the given week.. we want to remove any slots that exist that are above and beyond
				// andy count
				i=1;
				cs2.setInt(i++,_cl.ID);
				cs2.setString(i++,_gs.getTag());
				cs2.setString(i++,sFromDate);
				cs2.setString(i++,sToDate);
				cs2.setInt(i++,iSlotCount);
				cs2.setString(i++,sGameTag);
				cs2.execute();
				
			 }
			 
		
		}
		
		cs1.close();
		cs2.close();
		ps1.close();
		//
		// ok.. now we have to look to see if we can take our generic slots and see if we can assign any club slots to
		// give them real meaning
		//
		// The club could have recently added club slots to make up for their short commings
		// 
		syncClubSlots(_cl,_gs);  // Match them up based upon date and unassigned
		fillMisfitSlots(_cl, _gs, "1.5"); // take any extra club provided slots and force them into an open gen slot
		fillMisfitSlots(_cl, _gs, "1.25"); // take any extra club provided slots and force them into an open gen slot
		createBonusSlots(_cl, _gs); // Generate bonus slots
		LOGGER.info("syncSlotsToClub: Done reviewing slot requirements for Club:" + _cl);
		
	}
	
	/**
	 * Here we simply return all the Starting Weekends of all the seasons we have to look at
	 * 
	 * @param _cl
	 * @return
	 * @throws SQLException 
	 */
	public ArrayList<String> getWeekendsForAllSchedules(GeneralSeason _gs) throws SQLException {
		
		ArrayList<String> dates = new ArrayList<String>();
		PreparedStatement ps = this.prepareStatement("call scaha.getWeekendStartsForAllSchedules(?)");
		ps.setString(1,_gs.getTag());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			dates.add(rs.getString(i++));
		}
		rs.close();
		return dates;
	}
	
	/**
	 *  * For a given club.. this remove any slots that exist prior to the earliest start date of the club
	 *
	 * @param _cl
	 * @param _sDate
	 * @throws SQLException 
	 */
	public void pruneSlots (Club _cl, GeneralSeason _gs, String _sFromDate, String _sToDate) throws SQLException {
		
		//LOGGER.info("pruneSlots:  for Club:" + _cl + ", gs=" + _gs + ", fd=" + _sFromDate + ", td=" + _sToDate);
		
		CallableStatement csDelete = this.prepareCall("call scaha.pruneSlots(?,?,?,?)");
		int i=1;
		csDelete.setInt(i++, _cl.ID);
		csDelete.setString(i++, _gs.getTag());
		csDelete.setString(i++, _sFromDate);
		csDelete.setString(i++, _sToDate);
		csDelete.execute();
		csDelete.close();
	
	}
	
	/**
	 * synchClubSlots  
	 * 
	 * This will take all the club slots given and assign them to the generic slots created by the software
	 * It assigned them to open slots only.
	 * 
	 *  We need to check to make sure we do not have any clubslots assigned to two diffent genslots
	 *  
	 *  we then have to try to find "best fit" club slots to the remaining open slots in the system
	 *  
	 *  Finally.. we have to remove any excess generated slots that do not have a club slot assigned to it.
	 *  
	 * @param _cl
	 * @throws SQLException 
	 */
	public void syncClubSlots(Club _cl, GeneralSeason _gs) throws SQLException {

		CallableStatement cs1 = this.prepareCall("call scaha.syncClubSlotstoSlots(?,?)");

		int i = 1;
		cs1.setInt(i++, _cl.ID);
		cs1.setString(i++, _gs.getTag());
		cs1.execute();
		LOGGER.info("synchClubSlots: Merged slots to club slots for club :" + _cl);
		cs1.close();
	}
	
	/** 
	 * fillMisfitSlots - This will simply loop through all the unassigned generated slots and fill them in with unused 
	 * club slots..
	 * @param _cl
	 * @throws SQLException 
	 */
	public void fillMisfitSlots (Club _cl, GeneralSeason _gs, String _sGameTag) throws SQLException {
		
		LOGGER.info("fillMisfitSlots for Club " +  _cl + ", gs=" + _gs + ",  gt=" + _sGameTag);

		PreparedStatement ps1 = prepareStatement("call scaha.getUnassignedSlotCount(?,?)");
		CallableStatement cs1 = prepareCall("call scaha.syncAlternateClubSlots(?,?,?)");
		CallableStatement cs2 = prepareCall("call scaha.syncAnyClubSlots(?,?,?)");
		
		ps1.setInt(1,_cl.ID);
		ps1.setString(2,_gs.getTag());
		ResultSet rs = ps1.executeQuery();
		rs.next();
		int iCount = rs.getInt(1);
		rs.close();
		//LOGGER.info("fillMisfitSlots for Club " +  _cl + ". iCount = " + iCount);

		for	 (int iv = 1;iv<=iCount;iv++) {
			int i = 1;
			cs1.setInt(i++, _cl.ID);
			cs1.setString(i++, _gs.getTag());
			cs1.setString(i++, _sGameTag);
			cs1.executeUpdate();
			//LOGGER.info("fillMisfitSlots: alternate slot pass:" + iv + " of " + iCount + " for Club " +  _cl + " for gamelength:" + _sGameTag);
		}

		//
		// now lets go after any slot.. and mark the club slot as used by an alternate 
		//
		rs = ps1.executeQuery();
		rs.next();
		iCount = rs.getInt(1);
		rs.close();
		for (int iv = 1;iv<=iCount;iv++) {
			int i = 1;
			cs2.setInt(i++, _cl.ID);
			cs2.setString(i++, _gs.getTag());
			cs2.setString(i++, _sGameTag);
			cs2.executeUpdate();
			//LOGGER.info("fillMisfitSlots: ANY slot pass:" + iv + " of " + iCount + " for Club " +  _cl + " for gamelength:" + _sGameTag);
		}
		
		cs1.close();
		cs2.close();
		ps1.close();
	}
	
	/** 
	 * createBonusSlots - This will simply look for any extra slots that were given.   For each one found we:
	 * Create a Slot for it with the appropriate from, to date
	 * make the ranking 99
	 * tie the current club slot you are on the the newly generated slot.
	 * We will need to find a seasonweek the fits between the actdate.. any seasonweek will do for the given club
	 * @param _cl
	 * @throws SQLException 
	 */
	public void createBonusSlots (Club _cl, GeneralSeason _gs) throws SQLException {
		
		CallableStatement cs2 = prepareCall("call scaha.genBonusSlots(?,?)");
		int i = 1;
		cs2.setInt(i++, _cl.ID);
		cs2.setString(i++, _gs.getTag());
		cs2.executeUpdate();
		cs2.close();
		
	}

	public ArrayList<Integer>  getAvailableParticipants(ScheduleWeek sw) throws SQLException {

		LOGGER.info("getAvailableParticipants:for ScheduleWeekId:" + sw.ID + "ScheduleId" + sw.getSchedule().ID);
		 

		ArrayList<Integer> keys = new ArrayList<Integer>();
		
		ps_GetAvailableParticipants.setInt(1, sw.ID);
		ps_GetAvailableParticipants.setInt(2, sw.getSchedule().ID);
		ResultSet rs = ps_GetAvailableParticipants.executeQuery();
		while (rs.next()) {
			keys.add(Integer.valueOf(rs.getInt(1)));	
		}
		rs.close();
		LOGGER.info("getAvailableParticipants:for scheduleweek:" + sw.ID  + ". " +   keys);
		return keys;
		
	}

	public ArrayList<Integer> getAvailableMatchups(Participant _p, Schedule _se, ScheduleWeek _sw, boolean _sq) throws SQLException {
		ArrayList<Integer> keys = new ArrayList<Integer>();
	
		//LOGGER.info("pRank:"+ _p.getRank() + ":idseason:" + _se.ID + ":squeeze:" + _sq);

		int i = 1;
		ps_GetAvailableMatchups.setInt(i++, _sw.ID);
		ps_GetAvailableMatchups.setInt(i++, _se.ID);
		ps_GetAvailableMatchups.setInt(i++, _p.getTeam().ID);
		ps_GetAvailableMatchups.setInt(i++, _p.getRank());
		ps_GetAvailableMatchups.setString(i++, _sw.getFromDate());
		ps_GetAvailableMatchups.setString(i++, _sw.getToDate());
		ps_GetAvailableMatchups.setInt(i++,(_sq ? 1: 0));
		
		ResultSet rs = ps_GetAvailableMatchups.executeQuery();
		while (rs.next()) {
		  keys.add(Integer.valueOf(rs.getInt(1)));	
		}
		rs.close();
		//LOGGER.fine("getAvailableMatchups:all available matchups:" + keys);
		return keys;
	}
	
	/**
	 *  refresh - TeamGameInfo .. updates all the pertinant information you want to track for a team
	 *  from the database
	 * @param _tgi
	 * @throws SQLException 
	 */
	public void refresh(TeamGameInfo _tgi, Schedule _se) throws SQLException {
		
		ScahaTeam tm = _tgi.getTeam();
		ps_TeamGameCountBySched.setInt(1,tm.ID);
		ps_TeamGameCountBySched.setInt(2,_se.ID);
		
		ResultSet rs = ps_TeamGameCountBySched.executeQuery();
		while (rs.next()) {
			_tgi.setTotalGames(rs.getInt(1));
			_tgi.setHomeGames(rs.getInt(2));
			_tgi.setAwayGames(rs.getInt(3));
			_tgi.setExGames(rs.getInt(4));
		}
		rs.close();
		
		if (!_tgi.isBODLoaded()) {
			ps_DatesTeamGone.setInt(1,tm.ID);
			rs = ps_DatesTeamGone.executeQuery();
			while (rs.next()) {
					_tgi.getHmBOD().put(rs.getString(1),"");
			}
			
			rs.close();
		}
		//
		// ok.. lets mark the out of town as loaded..
		//
		_tgi.setBODLoaded(true);

	}

	public ArrayList<Integer> getAvailableSlotIDs(Club _cl, ScahaTeam _tm, ScheduleWeek _sw) throws SQLException {
		
		//LOGGER.info("getAvailableSlotIDs: Club =" + _cl + ", tm=" + _tm + ",sw=" +  _sw.ID);
		
		ArrayList<Integer> keys = new ArrayList<Integer>() ;
		
		int i=1;
		ps_GetAvailableSlotIds.setInt(i++, _sw.ID);
		ps_GetAvailableSlotIds.setInt(i++, _cl.ID);
		ps_GetAvailableSlotIds.setInt(i++, _tm.ID);
		ResultSet rs = ps_GetAvailableSlotIds.executeQuery();
		while (rs.next()) {
			keys.add(Integer.valueOf(rs.getInt(1)));	
		}
		rs.close();
		//LOGGER.info("getAvailableSlotIDs: found  the following slots for " + _tm + ", sw= " + _sw.ID + " [" + keys + "]");
		if (keys.size() == 0) {
			//LOGGER.info("getAvailableSlotIDs: *** WARNING *** NO AVAILABLE SLOTS for  " + _tm + ":" + _sw.ID);
		}
		return keys;
	}

	public boolean checkclubblock(Participant pMatch, Participant pMain, Schedule schedule) throws SQLException {
		
		boolean bok = false;
		
		int i=1;
		ps_AreClubsBlockedForSchedule.setInt(i++,pMatch.getTeam().ID);
		ps_AreClubsBlockedForSchedule.setInt(i++,pMain.getTeam().ID);
		ps_AreClubsBlockedForSchedule.setInt(i++,schedule.ID);
		ResultSet rs = ps_AreClubsBlockedForSchedule.executeQuery();
		while (rs.next()) {
			bok = true;
			//LOGGER.info("CB:Participant: " + pMatch + " cannot play " + pMain + " for " + schedule);
		}
		rs.close();
		
		return bok;
			
	}

	public void getAllAvailableSlots(Schedule _se, Participant _part) throws SQLException {
		
		_part.resetSlotsAvail();

		int i=1;
		
		ps_GetAllAvailableSlots.setInt(i++, _se.ID);
		ps_GetAllAvailableSlots.setInt(i++, _part.ID);

		ResultSet rs = ps_GetAllAvailableSlots.executeQuery();
		
		while (rs.next()) {
			Slot sl = new Slot(rs.getInt(1),rs.getString(2),rs.getString(3),0);
			_part.getSlotsAvail().add(sl);
		}
		rs.close();
				
		//LOGGER.info("getAllAvailableSlots for (" + _part.getTeam() + ") are:" + _part.getSlotsAvail());
	}

	public void getAllUsedSlots(Schedule _se, Participant _part) throws SQLException {
		_part.resetSlotsPlayed();

		int i = 1;
		ps_GetAllUsedSlots.setInt(i++, _se.ID);
		ps_GetAllUsedSlots.setInt(i++, _part.ID);
		
		ResultSet rs = ps_GetAllUsedSlots.executeQuery();
		
		while (rs.next()) {
			Slot sl = new Slot(rs.getInt(1),rs.getString(2),rs.getString(3), rs.getInt(4));
			_part.getSlotsPlaying().add(sl);
		}
		rs.close();
		
	}

	public boolean checkHomeOnly(Participant pMatch, String actDate) throws SQLException {

		boolean bok = false;
		
		ps_GetHomeOnlyDates.setInt(1, pMatch.getTeam().ID);
		ResultSet rs = ps_GetHomeOnlyDates.executeQuery();
		while (rs.next()) {
			if (rs.getString(1).equals(actDate)) {
				bok = true;
				//LOGGER.info("CB:Participant: " + pMatch + " cannot have an away game ");
				break;
			}
		}
		rs.close();
		return bok;
		
	}

	/**
	 * Lets return the given actual date of the slot for the given slot ID.
	 * 
	 * @param _idSlot
	 * @return
	 * @throws SQLException
	 */
	public String getSlotDate(Integer _idSlot) throws SQLException {
		String sreturn = "1980-01-01";
		ps_GetSlotDate.setInt(1,_idSlot);
		ResultSet rs = ps_GetSlotDate.executeQuery();
		while (rs.next()) {
			sreturn = rs.getString(1);
		}
		rs.close();
		return sreturn;
		
	}

	public boolean checkClubOffDay(Participant _p1, String slotDate) throws SQLException {
		boolean bok = false;

		//LOGGER.info("Checking club off day (" + slotDate + ") Team=" + _p1.getTeam());
		ps_GetClubOffDates.setInt(1,_p1.getTeam().ID);
		ResultSet rs = ps_GetClubOffDates.executeQuery();
		while (rs.next()) {
			if (rs.getString(1).equals(slotDate)) {
				bok = true;
				//LOGGER.info("COG:Team " + _p1.getTeam() + " cannot play on this day!");
				break;
			}
				
		}
		rs.close();
		return bok;

}


	public void getSlotsForMatchup(ArrayList<Integer> _slotList, Participant _part) throws SQLException {
		
		String sSlotIDs = "";
		_part.resetSlotsMatchup();

		for (Integer Slotid : _slotList) {
			sSlotIDs = sSlotIDs + Slotid.toString() + ",";
		}
		if (sSlotIDs.isEmpty()) {
			//LOGGER.info("getSlotsForMatchup ... is empty.. nothing to parse");
			return;
		}

		sSlotIDs = sSlotIDs.substring(0,sSlotIDs.length()-1);
		//LOGGER.info("getSlotsForMatchup, sSlotIDs=" + sSlotIDs);
		
		ps_GetSlotsForMatchup.setString(1,sSlotIDs);
		
		ResultSet rs = ps_GetSlotsForMatchup.executeQuery();
		while (rs.next()) {
			Slot sl = new Slot(rs.getInt(1),rs.getString(2),rs.getString(3),0);
			_part.getSlotsMatchup().add(sl);
		}
		//LOGGER.info("getSlotsForMatchup:" + _part.getSlotsMatchup());
		
	}

	public Participant calcHomeParticipant(Schedule _se,Participant _pMain, Participant _pMatch, ArrayList<Integer> _aMain, ArrayList<Integer> _aMatch) throws SQLException {
		int iMainHitPoints = 0;
		int iMatchHitPoints = 0;
		//
		//
		// distance hit
		//
		Club clMain = _pMain.getTeam().getTeamClub();
		Club clMatch = _pMatch.getTeam().getTeamClub();
		
		if (clMain.getTag().equals("DRAGONS") 
				|| clMain.getTag().equals("BLAZE")
				) {
			iMainHitPoints = 1;
		}
		if (clMatch.getTag().equals("DRAGONS") 
				| clMatch.getTag().equals("BLAZE")
				) {
			iMatchHitPoints = 1;
		}
		if (_pMain.getTeam().isExhibition()) { 
			iMainHitPoints = 0;
		}
		if (_pMatch.getTeam().isExhibition()) { 
			iMatchHitPoints = 0;
		}

		//
		// lets get see who wins on matchup homes..
		//
		int ihomeadv = 0;
		int i=1;
		ps_getHomeAwayBalance.setInt(i++,_pMain.getTeam().ID);
		ps_getHomeAwayBalance.setInt(i++,_pMatch.getTeam().ID);
		ps_getHomeAwayBalance.setInt(i++,_se.ID);
		ResultSet rs = ps_getHomeAwayBalance.executeQuery();
		while (rs.next()) {
			ihomeadv = rs.getInt(1);
			//LOGGER.info("homeAtvantage:is:" + ihomeadv  + " to Main " + _pMain);
		}
		rs.close();
		
		int iMain = _pMain.getTeam().getTeamGameInfo().getHomeGames();
		int iMatch = _pMatch.getTeam().getTeamGameInfo().getHomeGames();
		
		//
		// Bakersfield effect
		//
		
		if (_pMain.getTeam().ID == 746 && iMain > 7 && !_aMatch.isEmpty()) {
			return _pMatch;
		}
		if (_pMatch.getTeam().ID == 746 && iMatch > 7 && !_aMain.isEmpty()) {
			return _pMain;
		}
		
		if (_aMain.isEmpty() && !_aMatch.isEmpty()) {
			return _pMatch;
		} else 	if (!_aMain.isEmpty() && _aMatch.isEmpty()) {
			return _pMain;
		} else if (_pMain.getTeam().getTeamGameInfo().getAwayGames() == _se.getMaxawaycnt()) {
			return _pMain;
		} else if (_pMatch.getTeam().getTeamGameInfo().getAwayGames() == _se.getMaxawaycnt()) {
			return _pMatch;
		} else if (_aMain.size() < 2 && (iMain + iMainHitPoints) < (iMatch  + iMatchHitPoints)) {
			return _pMain;
		} else if (_aMatch.size() < 2 && (iMatch + iMatchHitPoints) < (iMain + iMainHitPoints)) {
			return _pMatch;
		} else if (ihomeadv - iMainHitPoints > 0 && !_aMatch.isEmpty()) {
			return _pMatch;
		} else if (ihomeadv + iMainHitPoints < 0 && !_aMain.isEmpty()) {
			return _pMain;
		} else 	if (iMain + iMainHitPoints < iMatch + iMatchHitPoints && !_aMain.isEmpty()) {
			return _pMain;
		} else if (iMatch + iMatchHitPoints < iMain + iMainHitPoints && !_aMatch.isEmpty()) {
			return _pMatch;
		} else {
			return _pMain;
		}
	
			}

	public void scheduleGame(Schedule _se, ScheduleWeek _sw, Participant _pMain, Participant _pMatch, Integer _iSlotID, boolean _bumpon) throws SQLException {
		
		ResultSet rs = null;
		if (_iSlotID > 0) {
			ps_getGameIdForSlot.setInt(1,_iSlotID);
			rs = ps_getGameIdForSlot.executeQuery();
			while (rs.next()) {
				//LOGGER.info("ATTEMPTING TO USE A FILLED SLOT!! SCREATCHING HALT" + _iSlotID);
				System.exit(0);
			}
			rs.close();
		}
		int i = 1;
		//LOGGER.info("P" + i + ":idhometeam:" + _pMain.getTeam().ID);
		cs_ScheduleGame.setInt(i++, _pMain.getTeam().ID);
		//LOGGER.info("P" + i + ":idawayteam:" + _pMatch.getTeam().ID);
		cs_ScheduleGame.setInt(i++, _pMatch.getTeam().ID);
		//LOGGER.info("P" + i + ":participant1:" + _pMain.getRank());
		cs_ScheduleGame.setInt(i++, _pMain.getRank());
		//LOGGER.info("P" + i + ":participant2:" + _pMatch.getRank());
		cs_ScheduleGame.setInt(i++, _pMatch.getRank());
		//LOGGER.info("P" + i + ":idSchedule:" + _se.ID);
		cs_ScheduleGame.setInt(i++, _se.ID);
		//LOGGER.info("P" + i + ":idseasonweeks:" + _sw.ID);
		cs_ScheduleGame.setInt(i++, _sw.ID);
		//LOGGER.info("P" + i + ":idslot:" + _iSlotID.intValue());
		cs_ScheduleGame.setInt(i++, _iSlotID.intValue());
		//LOGGER.info("P" + i + ":isbumpsticky:" + _bumpon);
		cs_ScheduleGame.setInt(i++, (_bumpon ? 1: 0));
		cs_ScheduleGame.executeUpdate();
		//LOGGER.info("scheduleGame: game scheduled between:" + _pMain + " and " + _pMatch + " for sw:" + _sw.toString());
		
					
	}

	/**
	 * Lets reset all the games for a given schedule..
	 * @param sch
	 * @throws SQLException 
	 */
	public void resetGames(Schedule sch) throws SQLException {
		//LOGGER.info("resetGames:" + sch);
		CallableStatement cs = this.prepareCall("call scaha.resetgames(?)");
		cs.setInt(1, sch.ID);
		cs.executeUpdate();
		cs.close();
	}
	
	public boolean stopScheduleEngine() throws SQLException {
		
		//
		// Asumme schedule goes deep until returns a true
		boolean b = false;
		
		ps_sysControRequest.setString(1,"STOPSCHEDULE");
		ResultSet rs = ps_sysControRequest.executeQuery();
		if (rs.next()) {
			b = (rs.getInt(1) == 1 ? true : false);
		}
		
		return b;
	}
}
	

