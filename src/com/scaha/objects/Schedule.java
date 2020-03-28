package com.scaha.objects;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;


public class Schedule extends ScahaObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private ParticipantList partlist = null;
	private ScheduleWeekList swlist = null;
	private LiveGameList livegamelist = null;

	
	private String scheduleweektag = null;
	private String seasontag = null;
	private String tag = null;
	private String divsname = null;
	private String gametag = null;
	private String description = null;
	private int teamcount = 0;
	private boolean playonce = false;
	private boolean locked = false;
	private int rank = 0;
	private String startdate = null;
	private String enddate = null;
	private boolean exhibitioncounts = false;
	private int mingamecnt = 0;
	private int maxgamecnt = 0;
	private int maxbyecnt = 0;
	private int maxawaycnt = 0;
	private int maxexmatchup = 0;
	

	/**
	 * Basic Constructor.
	 * s
	 * @param _pro
	 * @param _id
	 */
	public Schedule (Profile _pro, int _id) {
		setProfile(_pro);
		this.ID = _id;
	}
	
	//adding this interface to support showing playoff schedules where we don't attach to a session
	public Schedule (int _id) {
		this.ID = _id;
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}


	/**
	 * @return the scheduleweektag
	 */
	public String getScheduleweektag() {
		return scheduleweektag;
	}


	/**
	 * @param scheduleweektag the scheduleweektag to set
	 */
	public void setScheduleweektag(String scheduleweektag) {
		this.scheduleweektag = scheduleweektag;
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


	/**
	 * @return the divsname
	 */
	public String getDivsname() {
		return divsname;
	}


	/**
	 * @param divsname the divsname to set
	 */
	public void setDivsname(String divsname) {
		this.divsname = divsname;
	}


	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}


	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @return the teamcount
	 */
	public int getTeamcount() {
		return teamcount;
	}


	/**
	 * @param teamcount the teamcount to set
	 */
	public void setTeamcount(int teamcount) {
		this.teamcount = teamcount;
	}



	/**
	 * @return the playonce
	 */
	public boolean isPlayonce() {
		return playonce;
	}


	/**
	 * @param playonce the playonce to set
	 */
	public void setPlayonce(boolean playonce) {
		this.playonce = playonce;
	}


	/**
	 * @return the locked
	 */
	public boolean isLocked() {
		return locked;
	}


	/**
	 * @param locked the locked to set
	 */
	public void setLocked(boolean locked) {
		this.locked = locked;
	}


	/**
	 * @return the rank
	 */
	public int getRank() {
		return rank;
	}


	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		this.rank = rank;
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
	 * @return the enddate
	 */
	public String getEnddate() {
		return enddate;
	}


	/**
	 * @param enddate the enddate to set
	 */
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}


	/**
	 * @return the mingamecnt
	 */
	public int getMingamecnt() {
		return mingamecnt;
	}


	/**
	 * @param mingamecnt the mingamecnt to set
	 */
	public void setMingamecnt(int mingamecnt) {
		this.mingamecnt = mingamecnt;
	}


	/**
	 * @return the maxbyecnt
	 */
	public int getMaxbyecnt() {
		return maxbyecnt;
	}


	/**
	 * @param maxbyecnt the maxbyecnt to set
	 */
	public void setMaxbyecnt(int maxbyecnt) {
		this.maxbyecnt = maxbyecnt;
	}


	/**
	 * @return the maxawaycnt
	 */
	public int getMaxawaycnt() {
		return maxawaycnt;
	}


	/**
	 * @param maxawaycnt the maxawaycnt to set
	 */
	public void setMaxawaycnt(int maxawaycnt) {
		this.maxawaycnt = maxawaycnt;
	}


	/**
	 * @return the gametag
	 */
	public String getGametag() {
		return gametag;
	}


	/**
	 * @param gametag the gametag to set
	 */
	public void setGametag(String gametag) {
		this.gametag = gametag;
	}
	
	
	
	public String toString() {
		return this.getDescription();
	}
	
	/**
	 * @return the maxgamecnt
	 */
	public int getMaxgamecnt() {
		return maxgamecnt;
	}
	/**
	 * @param maxgamecnt the maxgamecnt to set
	 */
	public void setMaxgamecnt(int maxgamecnt) {
		this.maxgamecnt = maxgamecnt;
	}
	
	
	
	/**
	 * @return the partlist
	 */
	public ParticipantList getPartlist() {
		return partlist;
	}
	/**
	 * @param partlist the partlist to set
	 */
	public void setPartlist(ParticipantList partlist) {
		this.partlist = partlist;
	}
	/**
	 * @return the swlist
	 */
	public ScheduleWeekList getSwlist() {
		return swlist;
	}
	/**
	 * @param swlist the swlist to set
	 */
	public void setSwlist(ScheduleWeekList swlist) {
		this.swlist = swlist;
	}
	/**
	 * Get the latest Details of myself..
	 * 
	 * @param _db
	 * @throws SQLException
	 */
	public void refresh(ScahaDatabase _db) throws SQLException {
		
		//LOGGER.info("Refreshing Object Data for schedule " + this);
		PreparedStatement ps  =  _db.prepareStatement("call scaha.getScheduleById(?)");
		ps.setInt(1, this.ID);
		ResultSet rs = ps.executeQuery();
		int i=1;
		if (rs.next()) {
			setDescription(rs.getString(i++));
			setScheduleweektag(rs.getString(i++));
			setSeasontag(rs.getString(i++));
			setGametag(rs.getString(i++));
			setDivsname(rs.getString(i++));
			setTag(rs.getString(i++));
			setRank(rs.getInt(i++));
			setTeamcount(rs.getInt(i++));
			setStartdate(rs.getString(i++));
			setEnddate(rs.getString(i++));
			setLocked((rs.getInt(i++) == 1 ? true : false));
			setPlayonce((rs.getInt(i++) == 1 ? true : false));
			setMingamecnt(rs.getInt(i++));
			setMaxgamecnt(rs.getInt(i++));
			setMaxbyecnt(rs.getInt(i++));
			setMaxawaycnt(rs.getInt(i++));
			this.setExhibitioncounts((rs.getInt(i++) == 1 ? true : false));
			this.setMaxexmatchup(rs.getInt(i++));
		}
		rs.close();
	}
	
	
	/**
	 * This is the massive scheduling Engine..
	 * 
	 * @param _bsqueeze
	 * @throws SQLException
	 */
	public void schedule(boolean _bsqueeze) throws SQLException {

		//LOGGER.info((_bsqueeze ? "SQUEEZE MODE " : " STANDARD MODE "));
		//LOGGER.info("Scheduling Season " +this);

		// Lets get the connections we need
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		boolean bhome = false;
		boolean foundmatchup = false;
		
		// Define all the main object for the match team
		Participant pMain = null;
		ScahaTeam tmMain = null;
		Club clMain = null;
		// Define all the match objects for the match team
		Participant pMatch = null;
		ScahaTeam tmMatch = null;
		Club clMatch = null;
		boolean squeezeit = false;
		
		ArrayList<Integer> slMatchIDs = new ArrayList<Integer>();
		ArrayList<Integer> slMainIDs = new ArrayList<Integer>();
		
		// for each season.. step through the season weeks
		
		
		ArrayList<Participant> parts = this.getPartlist().getParticipantArray();
		ArrayList<ScheduleWeek> sws = getSwlist().getScheduleWeekArray();

		
		//LOGGER.info("Schedule has " + sws.size() + " schedule weeks.");
		//LOGGER.info("Schedule has " + parts.size() + " participants.");
		
		int ibacktrack = 0;
		int iblowupcount = 0;
		for (int y=0;y < sws.size();y++) {
			
			ScheduleWeek sw = sws.get(y);
			
			//LOGGER.info("Looking at SchedulingWeek: " + sw + " for Schedule: " + this);
			
			// for a given season week.. who is available to play..
			// This needs to be fixed..
			sw.setScheduleComplete(false);
			
			// lets reset the process list for this week
			sw.resetProcessList();
			sw.resetBumpList();

			int iloopcount = 0;
			while (!sw.isScheduleComplete()) {
				iloopcount++;
				if (iblowupcount > 2) {
					//LOGGER.info("BLOWUPTIME NO MORE.. lets squeeze take care of this...");
					ibacktrack = y;
					iblowupcount = 0;
					break;
				}

				if (iloopcount > 5) {
					iloopcount=0;
					iblowupcount++;
					//LOGGER.info("BLOWUPTIME  need to backup the seasonweeks... and restart  starting over...");
					if (y >= sws.size() - 2) {
						//LOGGER.info("BLOWUPTIME NOPE.. lets exit");
						break;
					}

					for (int i = y;i>=0 + ibacktrack;i--) {
						ScheduleWeek swb = (ScheduleWeek)sws.get(i);
						swb.backoutSchedule(db, this);
					}
					y=-1 + ibacktrack;
					break;
				}
			
				sw.setScheduleComplete(true);
				sw.setAvailableToPlay(db);
				
				//
				// lets loop through all the teams that have to play 
				//
				for (Integer key : sw.getAvailToPlayKeys()) {
					//LOGGER.info("Schedule: processing:" + key);
					pMain = partlist.getByKey(key.intValue());
					tmMain = pMain.getTeam();
					clMain = tmMain.getTeamClub();
					//LOGGER.info("schedule:Refreshing Main Team:" + pMain.getTeam());
					tmMain.getTeamGameInfo().refreshInfo(db,this);

					if (sw.isAlreadyScheduled(pMain)) {
						//LOGGER.info("schedule:alreadyscheduled a game for team:" + pMain.getTeam() + ". Skipping..");
						continue;
					} 

					if (tmMain.isOutOfTown(sw)) {
						//LOGGER.info("schedule:  Main Team is hout of town this week: " + pMain.getTeam() + ". Skipping..");
						continue;
					} 
						
					//
					// if we are squeezing.. we need to get all available matchups.. even if they have games currently scheduled.
					//
					sw.setAvailableMatchups(db,pMain, this);
					//LOGGER.info("schedule:Matchups are:" +sw.getMatchUpKeys());
											
					if (sw.noMatchups()) {
						if (this.getTeamcount() % 2 != 0 && (_bsqueeze || iloopcount == 4)) {
							//LOGGER.info("ISTS SQUEESE TIME");
							sw.setAvailableMatchupsSqueeze(db,pMain, this);
							squeezeit = true;
						} else if (sw.getBumpCount() > 5 && _bsqueeze) {
							sw.setAvailableMatchupsSqueeze(db,pMain, this);
							squeezeit = true;
						} else if (this.getTeamcount() % 2 == 0 ) {
							if (iloopcount <= 4) {
								sw.backoutSchedule(db, this, pMain);
								sw.setScheduleComplete(false);
								break;
							} else if (iloopcount == 4) {
								sw.setAvailableMatchupsSqueeze(db,pMain, this);
								squeezeit = true;
							}
							
						}  
					}
						
					//
					// if the team is out of town.. then mark the available ice as -2
					//
					
					//LOGGER.info("Scheduling:Main Team Assumes a Home position: " + tmMain);
					
					bhome = true;
					
					if (tmMain.isOutOfTown(sw)) {
						slMainIDs.clear();
						slMainIDs.add(Integer.valueOf(-2));
						//LOGGER.info("Scheduling:" + " out of town detection.. ice is -2 for " + tmMain.toString());
					} else {
						slMainIDs = db.getAvailableSlotIDs(clMain,tmMain, sw);
					}

					if (slMainIDs.size() == 0) {
						//LOGGER.info("Scheduling:" + tmMain + " for " + sw.getFromDate() + " wants home game.. but does not have ice.  Switching to away mode.");
						bhome = false;
					}
					
					//
					// now lets iterate through the potential matchups
					//
					
					while (!sw.getMatchUpKeys().isEmpty()) { 

						pMatch = this.partlist.getByKey((sw.getMatchUpKeys().get(0)));
						slMatchIDs.clear();
						tmMatch = pMatch.getTeam();
						clMatch = tmMatch.getTeamClub();
						tmMatch.getTeamGameInfo().refreshInfo(db,this);
						
						//LOGGER.info("Scheduler: Match Team .. pulled from the rubble:" + tmMatch.getTeamname());

//						if (db.checkChickCounts(this,pMain,pMatch) > this.getMaxexmatchup()) {
//							LOGGER.info("schedule:  Too Many Chick Games in this iteraton already : " + pMain + " " + pMatch + ". ");
//							db.blockChicks
//						}
//						if (tmMatch.gameCapCheck(this,tmMain)) {
//							LOGGER.info("schedule:  Match Team enough games: " + pMain + " " + pMatch + ". Skipping..");
//							sw.getMatchUpKeys().remove(0);
//							continue;
//						}
//						if (tmMain.gameCapCheck(this,tmMatch)) {
//							LOGGER.info("schedule:  Main Team enough games II: [" + tmMain.getTotalGames() +"] " + pMain + " "  + ". Skipping..");
//							LOGGER.info("schedule:  Match Team enough games II: [" + tmMatch.getTotalGames() +"] " + pMatch + " "  + ". Skipping..");
//							sw.getMatchUpKeys().remove(0);
//							continue;
//						}
//						
						//
						// Are these teams blocked from playing each other?
						//
						if (db.checkclubblock(pMatch, pMain, this)) {
							//LOGGER.info("Block alert " + pMatch + ". Skipping..");
							sw.getMatchUpKeys().remove(0);
							continue;
						}
						
						//
						// Out of Town Checker
						//
						if (tmMatch.isOutOfTown(sw)) {
							//LOGGER.info("schedule:Match Team is out of town..." + pMatch + ". Skipping..");
							sw.getMatchUpKeys().remove(0);
							continue;
						}
						
						//
						// only disregard the Match Team already being processed if squeeze is on
						// we need the double up

						// if we had to squeeze.. then lets punch a hole in the schedule
						// and try to make it happen
						if (squeezeit) {
							foundmatchup = false;
							//LOGGER.info("Putting on the squeeezeee A");
							db.getAllAvailableSlots(this,pMatch);
							db.getAllAvailableSlots(this,pMain);
							db.getAllUsedSlots(this,  pMatch);
							db.getAllUsedSlots(this,  pMain);
							for (Slot myslot : pMain.getSlotsAvail()) {
								if (!db.checkHomeOnly(pMatch,myslot.actDate)) {
									if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
										bhome = true;
										slMainIDs.clear();
										slMainIDs.add(Integer.valueOf(myslot.ID));
										foundmatchup = true;
										break;
									}
								}	
							}
							for (Slot myslot : pMatch.getSlotsAvail()) {
								if (!db.checkHomeOnly(pMain,myslot.actDate)) {
									if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
										bhome = false;
										slMatchIDs.clear();
										slMatchIDs.add(Integer.valueOf(myslot.ID));
										foundmatchup = true;
										break;
									}
								}
							}
							if (!foundmatchup) {
								//LOGGER.info("Did not find any squeeze ice.. moving on");
								sw.getMatchUpKeys().remove(0);
								continue;
							}
						} else {
							slMatchIDs= db.getAvailableSlotIDs(clMatch,tmMatch, sw);
						}
					
						if (tmMatch.isOutOfTown(sw)) {
							slMatchIDs.clear();
							slMatchIDs.add(Integer.valueOf(-2));
						}

						if (slMatchIDs.isEmpty() && slMainIDs.isEmpty()) {
							if (_bsqueeze){
								squeezeit = true;
								//LOGGER.info("Putting on the squeeezeee");
								db.getAllAvailableSlots(this,pMatch);
								db.getAllAvailableSlots(this,pMain);
								db.getAllUsedSlots(this,  pMatch);
								db.getAllUsedSlots(this,  pMain);
								for (Slot myslot : pMain.getSlotsAvail()) {
									if (!db.checkHomeOnly(pMatch,myslot.actDate)) {
										if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
											bhome = true;
											slMainIDs.clear();
											slMainIDs.add(Integer.valueOf(myslot.ID));
											foundmatchup = true;
											break;
										}
									}	
								}
								for (Slot myslot : pMatch.getSlotsAvail()) {
									if (!db.checkHomeOnly(pMain,myslot.actDate)) {
										if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
											bhome = false;
											slMatchIDs.clear();
											slMatchIDs.add(Integer.valueOf(myslot.ID));
											break;
										}
									}
								}

								if (slMatchIDs.isEmpty() && slMainIDs.isEmpty()) {
									//LOGGER.info("*** WARNING **** Niether Team Has Ice **** skipping...");
									sw.getMatchUpKeys().remove(0);
									continue;
								}  
							} else {
								//LOGGER.info("*** WARNING **** Niether Team Has Ice **** skipping...");
								sw.getMatchUpKeys().remove(0);
								continue;
							}
						}

						while (!slMatchIDs.isEmpty()) {
							if (db.checkHomeOnly(pMain, db.getSlotDate(slMatchIDs.get(0)))) {
								slMatchIDs.remove(0);
								//LOGGER.info("1 Main has home only on this day clear the Match Slot IDs");
							} else {
								break;
							}
						}						
						while (!slMainIDs.isEmpty()) {
							if (db.checkHomeOnly(pMatch, db.getSlotDate(slMainIDs.get(0)))) {
								slMainIDs.remove(0);
								//LOGGER.info("1 Match has to be a home game.. so we must clear the Main Slot IDs");
							} else {
								break;
							}
						}
						

						while (!slMainIDs.isEmpty()) {
							if (db.checkClubOffDay(pMain, db.getSlotDate(slMainIDs.get(0)))) {
								slMainIDs.remove(0);
								//LOGGER.info("1 Main is gone.. so we must clear  Slot IDs");
							} else {
								break;
							}
						}

						while (!slMatchIDs.isEmpty()) {
							if (db.checkClubOffDay(pMain, db.getSlotDate(slMatchIDs.get(0)))) {
								slMatchIDs.remove(0);
								//LOGGER.info("2 Main is gone.. so we must clear  Slot IDs");
							} else {
								break;
							}
						}

						while (!slMatchIDs.isEmpty()) {
							if (db.checkClubOffDay(pMatch, db.getSlotDate(slMatchIDs.get(0)))) {
								slMatchIDs.remove(0);
								//LOGGER.info("3 Match is gone.. so we must clear  Slot IDs");
							} else {
								break;
							}
						}
						
						while (!slMainIDs.isEmpty()) {
							if (db.checkClubOffDay(pMatch, db.getSlotDate(slMainIDs.get(0)))) {
								slMainIDs.remove(0);
								//LOGGER.info("4 Match is gone.. so we must clear  Slot IDs");
							} else { 
								break;
							}
						}
						
						//
						// we need to double check the slots because a squeeze could have moved a slot forward into a non squeeze week
						//
						db.getAllUsedSlots(this,  pMatch);
						db.getSlotsForMatchup(slMatchIDs,pMatch);
						db.getAllUsedSlots(this,  pMain);
						db.getSlotsForMatchup(slMainIDs,pMain);
						slMainIDs.clear();
						for (Slot myslot : pMain.getSlotsMatchup()) {
							if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
								slMainIDs.add(Integer.valueOf(myslot.ID));
							}
						}	
						slMatchIDs.clear();
						for (Slot myslot : pMatch.getSlotsMatchup()) {
							if (pMain.canIPlay(this, myslot, pMatch) && pMatch.canIPlay(this, myslot, pMain)) {
								slMatchIDs.add(Integer.valueOf(myslot.ID));
							}
						}	
						
						//LOGGER.info("Scheduler:MAIN Slots Scrubbed are:" + slMainIDs);
						//LOGGER.info("Scheduler:Match Slots Scrubbed are:" + slMatchIDs);
						
						if (tmMatch.gameCapCheck(this,tmMain) || tmMain.gameCapCheck(this,tmMatch)) {
							//LOGGER.info("schedule:  Main Team enough games III: [" + tmMain.getTotalGames() +"] " + pMain + " "  + ". Skipping..");
							//LOGGER.info("schedule:  Match Team enough games III: [" + tmMatch.getTotalGames() +"] " + pMatch + " "  + ". Skipping..");
							sw.getMatchUpKeys().remove(0);
							continue;
						}
						if (slMatchIDs.isEmpty() && slMainIDs.isEmpty()) {
							//LOGGER.info("*** WARNING Level 2**** Niether Team Has Ice **** skipping...");
							sw.getMatchUpKeys().remove(0);
							continue;
						} else 

						if (squeezeit) {
							//LOGGER.info("schedule:Closing the squeeze now...");
							squeezeit = false;
						} else if (tmMatch.isOutOfTown(sw) && tmMain.isOutOfTown(sw)) {
							//LOGGER.info(" Both Teams are out of town.. whoops them now.:" + pMatch +":" + pMain);
							sw.getMatchUpKeys().remove(0);
							continue;
						}


						// schedule the game .. see who gets home..
						//
						Participant pHome = db.calcHomeParticipant(this, pMain, pMatch, slMainIDs, slMatchIDs);
						
						if (pHome.equals(pMain)) {
							//LOGGER.info("Main Slots: " + slMainIDs);
								db.scheduleGame(this, sw, pMain, pMatch, slMainIDs.get(0),sw.isBumpOn());
						} else if (pHome.equals(pMatch)) {
							//LOGGER.info("Match Slots: " + slMatchIDs);
							db.scheduleGame(this, sw, pMatch, pMain,  slMatchIDs.get(0),sw.isBumpOn());
						} else {
							//LOGGER.info("SNAFU on scheduling game..");
						}
						//
						// lets place them in the processed list
						// reset the bump list
						// and clear the matchupkeys.
						sw.addPrcoessListPart(pMain);
						sw.addPrcoessListPart(pMatch);
						//sw.resetBumpList();
						
						//LOGGER.info("ProcessedList is now:" + ContextManager.NEW_LINE + sw.getProcessList());
						sw.getMatchUpKeys().clear();
						
					}
				}
			}
			//if (y==7) break;
		}
		
		db.free();

	}
			
	public Participant getParticipantAtID(int intValue) {
		// TODO Auto-generated method stub
		return this.partlist.getByKey(intValue);
	}
	
	
	public int getKey() {
		return ID;
	}
	/**
	 * @return the exhibitioncounts
	 */
	public boolean isExhibitioncounts() {
		return exhibitioncounts;
	}
	/**
	 * @param exhibitioncounts the exhibitioncounts to set
	 */
	public void setExhibitioncounts(boolean exhibitioncounts) {
		this.exhibitioncounts = exhibitioncounts;
	}
	/**
	 * @return the maxexmatchup
	 */
	public int getMaxexmatchup() {
		return maxexmatchup;
	}
	/**
	 * @param maxexmatchup the maxexmatchup to set
	 */
	public void setMaxexmatchup(int maxexmatchup) {
		this.maxexmatchup = maxexmatchup;
	}
	/**
	 * @return the livegamelist
	 */
	public LiveGameList getLivegamelist() {
		return livegamelist;
	}
	/**
	 * @param livegamelist the livegamelist to set
	 */
	public void setLivegamelist(LiveGameList livegamelist) {
		this.livegamelist = livegamelist;
	}
}

