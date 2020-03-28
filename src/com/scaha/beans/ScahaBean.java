package com.scaha.beans;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.imageio.ImageIO;
import javax.mail.internet.InternetAddress;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.ClubAdmin;
import com.scaha.objects.ClubAdminList;
import com.scaha.objects.ClubList;
import com.scaha.objects.GeneralSeason;
import com.scaha.objects.GeneralSeasonList;
import com.scaha.objects.LiveGame;
import com.scaha.objects.LiveGameList;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Member;
import com.scaha.objects.MemberList;
import com.scaha.objects.MultiMedia;
import com.scaha.objects.Participant;
import com.scaha.objects.ParticipantList;
import com.scaha.objects.Profile;
import com.scaha.objects.ScahaTeam;
import com.scaha.objects.Schedule;
import com.scaha.objects.ScheduleList;
import com.scaha.objects.StatsList;
import com.scaha.objects.TeamGameInfo;
import com.scaha.objects.TeamList;
import com.scaha.objects.YearList;

@ManagedBean
@ApplicationScoped
public class ScahaBean implements Serializable,  MailableObject {
		
	
	//
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
		//
	// An appliation wide listing of all the clubs.
	//
	private ClubList ScahaClubList  = null;
	private TeamList ScahaTeamList  = null;
	private LiveGameList ScahaLiveGameList = null;
	private GeneralSeasonList ScahaSeasonList = null;
	private MemberList scahaboardmemberlist = null;
	private MemberList scahaprogramdirectorlist = null;
	private ScheduleList scahaschedule = null;
	private YearList scahayearlist = null;
	private StatsList scahastatslist = null;
	private MultiMedia noimage = null;
	private Profile DefaultProfile = null;
	
	
	
	 @PostConstruct
	 public void init() {
		 
		 LOGGER.info("******************* START: SCAHA BEAN INIT... ***********************");
		 LOGGER.info("\t old level at:" + LOGGER.getLevel());
		 LOGGER.setLevel(Level.ALL);
		 LOGGER.info("\t new level at:" + LOGGER.getLevel());
		 this.setDefaultProfile(new Profile());
		 this.setExecutiveboard();
		 this.setMeetingminutes();
		 this.loadNoimage();
		 this.refreshBean();
		 LOGGER.info("******************* FINISH: SCAHA BEAN INIT... ***********************");
	 }
	 
	 @PreDestroy
	 public void cleanup() {
		 ScahaClubList = null;
		 ScahaSeasonList = null;
		 DefaultProfile = null;
	 }

	 public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	/**
	 * This refreshes everything for this singular App Bean
	 * 
	 */
	public void refreshBean() {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			setScahaSeasonList(GeneralSeasonList.NewClubListFactory(this.DefaultProfile, db, "SCAHA"));
			setScahaClubList(ClubList.NewClubListFactory(this.DefaultProfile, db));
			loadTeamLists(db);
			setScahaschedule(ScheduleList.ListFactory(this.DefaultProfile, db, this.getScahaSeasonList().getCurrentSeason(),this.getScahaTeamList()));
			setScahaLiveGameList(LiveGameList.NewListFactory(this.DefaultProfile,db,this.getScahaSeasonList().getCurrentSeason(), this.getScahaTeamList(), this.getScahaschedule()));
			
			//this is where we will add the loading of the stats 
			//setScahastatslist(loadStats(db));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		 
	 }

	public void refreshClubList() {

		this.resetClubLists();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			setScahaClubList(ClubList.NewClubListFactory(this.DefaultProfile, db));
			loadTeamLists(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
	}
	
	public void refreshLiveGameList() {

		this.resetLiveGameList();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			setScahaLiveGameList(LiveGameList.NewListFactory(this.DefaultProfile,db,this.getScahaSeasonList().getCurrentSeason(), this.getScahaTeamList(), this.getScahaschedule()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
	}
	
	public void refreshScheduleList() {
		this.resetScheduleList();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			setScahaschedule(ScheduleList.ListFactory(this.DefaultProfile, db, this.getScahaSeasonList().getCurrentSeason(), this.getScahaTeamList()));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.free();
		
	}
	
	
	

	/**
	 * Wait some seconds before freeing up the connection
	 * @param _isec
	 */
	public void testDB(int _isec) {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
		PreparedStatement ps = db.prepareStatement("call scaha.getLiveGamesBySeason(?)");
		ps.setString(1,"SCAHA-1415");
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			LOGGER.info("found row");
		}
		db.free();
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	private void resetClubLists() {
		if (this.ScahaClubList != null) {
			List<Club> list = (List<Club>) this.ScahaClubList.getWrappedData();
			for (Club c : list) {
				ClubAdminList cal = c.getCal();
				TeamList tl = c.getScahaTeams();
				if (cal != null) {
					List<ClubAdmin> lca = (List<ClubAdmin>) cal.getWrappedData();
					lca.clear();
				}
				if (tl != null) {
					List<ScahaTeam> lst = (List<ScahaTeam>) tl.getWrappedData();
					lst.clear();
				}
			}
			list.clear();
		}
			
	}
	
	@SuppressWarnings("unchecked")
	private void resetLiveGameList() {
		if (this.ScahaLiveGameList != null) {
			List<LiveGame> list = (List<LiveGame>) this.ScahaLiveGameList.getWrappedData();
			list.clear();
		}
	}

	@SuppressWarnings("unchecked")
	public void syncClubSlots() {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			if (this.ScahaClubList != null) {
				List<Club> list = (List<Club>) this.ScahaClubList.getWrappedData();
				for (Club c : list) {
					db.syncSlotsToClub(c, ScahaSeasonList.getCurrentSeason());
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.free();
			
	}
	
	@SuppressWarnings("unchecked")
	public void syncGameMatrix() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			if (this.scahaschedule != null) {
				List<Schedule> list = (List<Schedule>) this.scahaschedule.getWrappedData();
				for (Schedule sch : list) {
					db.syncTeamsToSchedule(sch,  ScahaSeasonList.getCurrentSeason());
					sch.refresh(db);
					db.genGames(sch);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.free();
		
		this.refreshClubList();
		this.refreshScheduleList();
		
	}
	
	@SuppressWarnings("unchecked")
	private void resetScheduleList() {
		if (this.scahaschedule != null) {
			List<Schedule> list = (List<Schedule>) this.scahaschedule.getWrappedData();
			for (Schedule c : list) {
				// TODO
//				ClubAdminList cal = c.getS;
//				TeamList tl = c.getScahaTeams();
//				if (cal != null) {
//					List<ClubAdmin> lca = (List<ClubAdmin>) cal.getWrappedData();
//					lca.clear();
//				}
//				if (tl != null) {
//					List<ScahaTeam> lst = (List<ScahaTeam>) tl.getWrappedData();
//					lst.clear();
//				}
			}
			list.clear();
		}
		this.scahaschedule = null;
		
	}
	
	/**
	 * Load all the team lists
	 * @throws SQLException 
	 * 
	 */
	private void loadTeamLists(ScahaDatabase _db) throws SQLException {
	
		LOGGER.info("loading Team Lists for SCAHA Application");
		
		GeneralSeason scahags = this.getScahaSeasonList().getCurrentSeason();
		this.setScahaTeamList(TeamList.ListFactory());
		
		for (Club c : ScahaClubList) {
			TeamList tl = c.getScahaTeams();
			if (tl != null) {
				@SuppressWarnings("unchecked")
				List<ScahaTeam> lst = (List<ScahaTeam>) tl.getWrappedData();
				lst.clear();
			}
			tl = TeamList.NewTeamListFactory(this.DefaultProfile, _db, c, scahags, true, false);
			c.setScahaTeams(tl);
			this.getScahaTeamList().addTeamsToList(tl);
			
			//
			// here we need to add the team to the overall team list of the application.
			//
			
		}
		
		LOGGER.info("Combined List is:" + this.getScahaTeamList());
			
	}

	@Override
	public String getSubject() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTextBody() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	/**
	 * @return the scahaClubList
	 */
	public ClubList getScahaClubList() {
		return ScahaClubList;
	}
	/**
	 * @param scahaClubList the scahaClubList to set
	 */
	public void setScahaClubList(ClubList scahaClubList) {
		ScahaClubList = scahaClubList;
	}
	
	 /**
	 * @return the scahaTeamList
	 */
	public TeamList getScahaTeamList() {
		return ScahaTeamList;
	}

	/**
	 * @param scahaTeamList the scahaTeamList to set
	 */
	public void setScahaTeamList(TeamList scahaTeamList) {
		ScahaTeamList = scahaTeamList;
	}

	public long getDate() {
	    return System.currentTimeMillis();
	 }

	/**
	 * find a club object given the club id
	 * @param _id
	 * @return
	 */
	public Club findClubByID (int _id) {
		for (Club c : ScahaClubList) {
			if (c.ID == _id) { 
				return c;
			}
		}
		return null;
	}

	
	
	/**
	 * We return all Exhibition teams in a list
	 * @return
	 */
	public TeamList getXTeamList() {

		List<ScahaTeam> tl = new ArrayList<ScahaTeam>();
		
		for (Club c : ScahaClubList) {
			for (ScahaTeam t : c.getScahaTeams()) {
				if (t.getIsexhibition() == 1) tl.add(t);
			}
		}
		
		return new TeamList(tl);
		
	}

	/**
	 * @return the scahaSeasonList
	 */
	public GeneralSeasonList getScahaSeasonList() {
		return ScahaSeasonList;
	}


	/**
	 * @param scahaSeasonList the scahaSeasonList to set
	 */
	public void setScahaSeasonList(GeneralSeasonList scahaSeasonList) {
		ScahaSeasonList = scahaSeasonList;
	}

	/**
	 * @return the scahaSeasonList
	 */
	public MemberList getScahaboardmemberlist() {
		return scahaboardmemberlist;
	}


	
	/**
	 * @param scahaSeasonList the scahaSeasonList to set
	 */
	public void setScahaprogramdirectorlist(MemberList List) {
		scahaprogramdirectorlist = List;
	}
	
	/**
	 * @return the scahaSeasonList
	 */
	public MemberList getScahaprogramdirectorlist() {
		return scahaprogramdirectorlist;
	}


	
	/**
	 * @param scahaSeasonList the scahaSeasonList to set
	 */
	public void setScahaboardmemberlist(MemberList List) {
		scahaboardmemberlist = List;
	}
	/**
	 * @return the scahaYearList
	 */
	public YearList getScahayearlist() {
		return scahayearlist;
	}


	
	/**
	 * @param scahaSeasonList the scahaSeasonList to set
	 */
	public void setScahayearlist(YearList List) {
		scahayearlist = List;
	}
	
	
	/**
	 * @return the scahaYearList
	 */
	public StatsList getScahastatslist() {
		return scahastatslist;
	}


	
	/**
	 * @param scahaSeasonList the scahaSeasonList to set
	 */
	public void setScahastatslist(StatsList List) {
		scahastatslist = List;
	}

	/**
	 * @return the defaultProfile
	 */
	public Profile getDefaultProfile() {
		return DefaultProfile;
	}


	/**
	 * @param defaultProfile the defaultProfile to set
	 */
	public void setDefaultProfile(Profile defaultProfile) {
		DefaultProfile = defaultProfile;
	}

	@Override
	public InternetAddress[] getToMailIAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InternetAddress[] getPreApprovedICC() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setExecutiveboard() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			setScahaboardmemberlist(MemberList.NewBoardmemberListFactory(db,"call scaha.getScahaBoardMembers()"));
			setScahaprogramdirectorlist(MemberList.NewBoardmemberListFactory(db,"call scaha.getScahaProgramDirectors()"));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
	}

	public void setMeetingminutes() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			setScahayearlist(YearList.NewYearListFactory(db));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
	}
	
	/**
	 * @return the scahaschedule
	 */
	public ScheduleList getScahaschedule() {
		return scahaschedule;
	}

	/**
	 * @param scahaschedule the scahaschedule to set
	 */
	public void setScahaschedule(ScheduleList scahaschedule) {
		this.scahaschedule = scahaschedule;
	}
	
	public void setDisplay(Member member,String flagtype){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		String profileid = member.getProfileid();
		String flagstate = "";
		if (flagtype.equals("Address")){
			flagstate=member.getActionrenderaddress();
		}else{
			flagstate=member.getActionrenderphone();
		}
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.setBoardMemberDisplay(?,?,?)");
    		    cs.setInt("profileid", Integer.parseInt(profileid));
    		    cs.setInt("flagstate", Integer.parseInt(flagstate));
    		    cs.setString("flagtype", flagtype);
    		    cs.executeQuery();
    		    
    		    db.commit();
    			db.cleanup();
 				
    		    //logging 
    			LOGGER.info("We are have set " + flagtype + " display for " + profileid);
    		    
    		} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN set " + flagtype + " display for " + profileid);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		this.setExecutiveboard();
	}
	
	/******************************************************************************
	 *  Scheduling software method.. 
	 *  Here we go
	 */
	/**
	 *  
	 * OK.. this is the meat of it.
	 * 
	 * We loop until we are done scheduling everything..
	 * 
	 */
	public void schedule() {
	
		
		// We go until we are finished...
		boolean keepgoing = true;
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		GeneralSeason gs = this.ScahaSeasonList.getCurrentSeason();
		
		this.refreshScheduleList();
		//
		// Step through each schedule for the given season
		// this needs to be in priority order somehow
		// TODO order by rank here.. 
		
		try {
			for (Schedule sch: gs.getSchedList()) {
				if (sch.isLocked()) {
					LOGGER.info("SCHEDULING: SEASON IS LOCKED.. " + sch);
					continue;
				}
				sch.schedule(false);
			}
			
			
			for (Schedule sch: gs.getSchedList()) {
				if (db.stopScheduleEngine()) {
					LOGGER.info("SCHEDULING: Control Software asked to Bail .. " + sch);
					continue;
				}
				if (sch.isLocked()) {
					LOGGER.info("SCHEDULING: with Bounce on  SEASON IS LOCKED.. " + sch);
					continue;
				}
				sch.schedule(true);
			}
			
			if (db.stopScheduleEngine()) {
				LOGGER.info("SCHEDULING: Control Software asked to Bail .. " );
				keepgoing = false;
			}
			
			//
			// iok.. lets check overall games - exhibition games for each team in each season..
			/// we will loop on one season until a good matchup pops out..
			//
			boolean loopalot = !db.stopScheduleEngine();
			while (keepgoing) {
				keepgoing = false;
				for (Schedule sch: gs.getSchedList()) {
					if (sch.isLocked()) {
						LOGGER.info("SCHEDULING: secondary loop  SEASON IS LOCKED.. " + sch);
						continue;
					}
					
					sch.schedule(true);
					
					if (db.stopScheduleEngine()) {
						//LOGGER.info("SCHEDULING: Control Software asked to Bail .. " + sch);
						break;
					}
					ParticipantList parts = sch.getPartlist();
					for (Participant p : parts) {
						ScahaTeam tm = p.getTeam();
						TeamGameInfo tgi = tm.getTeamGameInfo();
						tgi.refreshInfo(db, sch);
						LOGGER.info("GAME COUNT CHECK... " + tgi.toString() + " with a min count of:" +  sch.getMingamecnt());
						// int iagmax = ((tm.getTotalGames() / 2) + 2 + ((tm.getTotalGames() % 2 != 0 ? 1 : 0)));
						if (tm.isExhibition()) {
							LOGGER.info("Team Info:" + tm.getTeamname() + " is exhibition.. not too worried");
						} else if ((tm.getTotalGames() - (sch.isExhibitioncounts() ? 0 : tm.getTeamGameInfo().getExGames())) < sch.getMingamecnt()) { 
							LOGGER.info("Team Info:" + tm.getTeamname() + "not enough games.. try again...");
							if (loopalot) {
								db.resetGames(sch);
								keepgoing = true;
							}
							break;
						} else if (tm.getTeamGameInfo().getAwayGames() < 0  ) {
							LOGGER.info("Team Info:" + tm.getTeamname() + "no away games...");
							if (loopalot) {
								db.resetGames(sch);
								keepgoing = true;
							}
							break;
							// we have to bypass carlbad teams.. they have to play all away games until ice is available
						} else if (tm.getTeamGameInfo().getAwayGames() > sch.getMaxawaycnt() && tm.ID != 462 && tm.ID != 573 && tm.ID != 539 ) {
							LOGGER.info("Team Info:" + tm.getTeamname() + "too many away games...");
							if (loopalot) {
								db.resetGames(sch);
								keepgoing = true;
							}
							break;
						}
					}	
					
					if (keepgoing) {
						sch.schedule(false);
						if (db.stopScheduleEngine()) {
							LOGGER.info("SCHEDULING: Control Software asked to Bail .. " + sch);
						} else {
							sch.schedule(true);
						}
					}
						
				}
			}	
		} catch (SQLException ex) {
			ex.printStackTrace();
		}
		db.free();

	}

	/**
	 * @return the scahaLiveGameList
	 */
	public LiveGameList getScahaLiveGameList() {
		return ScahaLiveGameList;
	}

	/**
	 * @param scahaLiveGameList the scahaLiveGameList to set
	 */
	public void setScahaLiveGameList(LiveGameList scahaLiveGameList) {
		ScahaLiveGameList = scahaLiveGameList;
	}
	
	public void setNoimage(MultiMedia mm){
		this.noimage = mm;
	}
	
	public MultiMedia getNoimage(){
		return this.noimage;
	}
	
	public StreamedContent getClubLogoByParmId() {
		FacesContext context = FacesContext.getCurrentInstance();
		String get = context.getExternalContext().getRequestParameterMap().get("target");
	    if (get == null) {
    		return new DefaultStreamedContent();
	    } else if (get.length() == 0) {
    		return new DefaultStreamedContent();
	    }
    	int id = Integer.parseInt(get);
	    Club myclub  = this.findClubByID(id);
	    if (myclub == null) {
			LOGGER.info("*** Could not find club... for id LOGO ID IS (" + get + ") ");
    		return new DefaultStreamedContent();
	    }
	    
	    LOGGER.info("*** club is...("+ myclub + ") for id LOGO ID IS (" + get + ") ");
		return getClubLogo(myclub);
	}
	
	public StreamedContent getClubLogo(Club _cl) {
		 
		boolean bstream = true;
		FacesContext context = FacesContext.getCurrentInstance();
		
		LOGGER.info("getClub Logo Club is" + _cl);
		if (_cl == null) {
			bstream = false;
		} else 	if (_cl.getLogo() == null) {
			LOGGER.info("getClub for " + _cl + " get Logo returned null...");
			bstream = false;
		} else if (_cl.getLogo().getMmObject() == null) {
			LOGGER.info("getClub for " + _cl + " not mm object.. its null...");
			bstream = false;
		} else { 
			
			//
			// we are good
		}
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
		    // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
			LOGGER.info("we are just rendering an empty response.. ");
			return  new DefaultStreamedContent();
			
		} else {

			if (bstream) {
				LOGGER.info("We are going for " + _cl + "'s logo vis getSteamedContent()");
				return _cl.getLogo().getStreamedContent();
			}
            
			try {

			LOGGER.info("we cannot stream.. so lets stream a default image.... ");

			//
			// ok.. through some text up as a defaul ..
			//
				BufferedImage bufferedImg = new BufferedImage(100, 25, BufferedImage.TYPE_INT_RGB);  
	            Graphics2D g2 = bufferedImg.createGraphics();  
	            g2.drawString("NO IMG", 10, 10);  
	            ByteArrayOutputStream os = new ByteArrayOutputStream();  
				ImageIO.write(bufferedImg, "png", os);
				return new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/png");

            } catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            
			return new DefaultStreamedContent();

		}
	}
	
	private StatsList loadStats(ScahaDatabase _db) throws SQLException {
		
		//LOGGER.info("loading Stats Lists for SCAHA Application");
		
		StatsList templist = new StatsList();
		templist.NewStatListFactory(_db);
		
		return templist;			
	}
	
	public void loadNoimage(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try{
			PreparedStatement ps = db.prepareStatement("call scaha.getMultiMedia(?,?,?)");
			MultiMedia mm = new MultiMedia(this.DefaultProfile, "CLUB", 35, "LOGO");
			mm.get(ps);
			this.setNoimage(mm);
		} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading no image logo");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
	}
		
}
