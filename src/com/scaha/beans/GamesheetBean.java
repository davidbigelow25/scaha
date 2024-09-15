package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.LiveGame;
import com.scaha.objects.LiveGameList;
import com.scaha.objects.LiveGameRosterSpot;
import com.scaha.objects.LiveGameRosterSpotList;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Penalty;
import com.scaha.objects.PenaltyList;
import com.scaha.objects.RosterForPrint;
import com.scaha.objects.ScahaTeam;
import com.scaha.objects.Scoring;
import com.scaha.objects.ScoringList;
import com.scaha.objects.Sog;
import com.scaha.objects.SogList;
import net.bootsfaces.utils.FacesMessages;
import com.scaha.objects.ScahaCoach;

@ManagedBean
@ViewScoped
public class GamesheetBean implements Serializable,  MailableObject {
	
	private static final String mail_reg_body = Utils.getMailTemplateFromFile("/mail/gamechange.html");
	private static final String mail_teampims_body = Utils.getMailTemplateFromFile("/mail/teampims.html");

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	
	@ManagedProperty(value="#{rosterBean}")
	private rosterBean rb;
	
	private LiveGame livegame = null;
	private LiveGameRosterSpot selectedhomerosterspot;
	private LiveGameRosterSpot selectedhomecoachrosterspot;
	private LiveGameRosterSpot selectedawayrosterspot;
	private LiveGameRosterSpot selectedteam1rosterspot;
	private LiveGameRosterSpot selectedteam2rosterspot;
	private LiveGameRosterSpot selectedteam3rosterspot;
	private LiveGameRosterSpot selectedteam4rosterspot;

	private Scoring selectedhomescore = null;
	private Scoring selectedawayscore = null;
	private Scoring currentscore =null;
	private Penalty currentpenalty = null;
	private Sog currentsog = null;
	private Penalty selectedhomepenalty = null;
	private Penalty selectedawaypenalty = null;
	private Sog selectedhomesog = null;
	private Sog selectedawaysog = null;
	
	private LiveGameRosterSpotList awayteam = null;
	private LiveGameRosterSpotList awayteamforpl = null;
	private LiveGameRosterSpotList hometeam = null;
	private LiveGameRosterSpotList hometeamcoach = null;
	private LiveGameRosterSpotList hometeamforpl = null;
	private LiveGameRosterSpotList team1 = null;
	private LiveGameRosterSpotList team2 = null;
	private LiveGameRosterSpotList team3 = null;
	private LiveGameRosterSpotList team4 = null;
	private LiveGameRosterSpotList team1coach = null;
	private LiveGameRosterSpotList team2coach = null;
	private LiveGameRosterSpotList team3coach = null;
	private LiveGameRosterSpotList team4coach = null;

	private ScoringList awayscoring = null;
	private ScoringList homescoring = null;
	private PenaltyList homepenalties = null;
	private PenaltyList awaypenalties = null;
	private SogList awaysogs = null;
	private SogList homesogs = null;
			
	
	private List<LiveGameRosterSpot> scoringpicklist = null;
	private ScahaTeam scoringteam = null;
	private LiveGameRosterSpotList scoringroster = null;
	private int selectedgoalroseterid = 0;
	private int selecteda1roseterid = 0;
	private int selecteda2roseterid = 0;
	private int selectedpenrosterid = 0;
	private int selectedsogrosterid = 0;
	
	private List<LiveGameRosterSpot> penpicklist = null;
	private List<LiveGameRosterSpot> team1penpicklist = null;
	private List<LiveGameRosterSpot> team2penpicklist = null;
	private List<LiveGameRosterSpot> team3penpicklist = null;
	private List<LiveGameRosterSpot> team4penpicklist = null;

	private ScahaTeam penteam = null;
	private LiveGameRosterSpotList penroster = null;
	
	private List<LiveGameRosterSpot> sogpicklist = null;
	private ScahaTeam sogteam = null;
	private LiveGameRosterSpotList sogroster = null;
	
	private int goalperiod = 0;
	private String goaltype = null;
	private String goalmin = null;
	private String goalsec = null;
	
	private int penperiod = 0;
	private String pentype = null;
	private String penminutes = null;
	private String penmin = null;
	private String pensec = null;
	
	private String sogplaytime = null;
	private int sogshots1 = 0;
	private int sogshots2 = 0;
	private int sogshots3 = 0;
	private int sogshots4 = 0;
	private int sogshots5 = 0;
	private int sogshots6 = 0;
	private int sogshots7 = 0;
	private int sogshots8 = 0;
	private int sogshots9 = 0;

	//
	// Class Level Variables
	//
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private List<String> penalties = new ArrayList<>();
	private Map<String, String> venues = new HashMap<>();
	private Map<String, String> htpick = new HashMap<>();
	private Map<String, String> atpick = new HashMap<>();
	private Map<String, String> typepick = new HashMap<>();
	private Map<String, String> statepick = new HashMap<>();
	
	private String lgsheet = null;
	private String lgvenue = null;
	private String lgtype = null;
	private String lgstate = null;
	private String lgdate = null;
	private String lgtime = null;
	private String lgateam = null;
	private String lghteam = null;
	
	private String lgateamval = null;
	private String lghteamval = null;
	private String lgvenueval = null;
	private String lgtypeval = null;
	private String lgstateval = null;
 
	//these are used for game entry
	private boolean editgame = false;
	private Integer teamid = 0;
	private Integer numgoalsadd = 10;
	private Integer numsogadd = 6;
	private Integer numpimsadd = 10;
	private String note = "";
	
	//these are used for printing scoresheet
	private List<RosterForPrint> rfplist = null;
	private List<RosterForPrint> rfplisthome = null;
	private List<RosterForPrint> rfplistaway = null;
	private Integer livegameid = null;
	private Integer numberofgrows = 18;
	private Integer teamclubid = null;
	private Integer teamclubid1 = null;
	private Integer teamclubid2 = null;
	private Integer teamclubid3 = null;
	private Integer teamclubid4 = null;
	private Integer miteteamid1 = null;
	private Integer miteteamid2 = null;
	private Integer miteteamid3 = null;
	private Integer miteteamid4 = null;

	private Boolean home = false;
	private Integer maxcoachsizehome = 0;
	private Integer maxcoachsizeaway = 0;
	private Boolean iscoachselectedforgame = false;
	private Boolean navigatetoprint = false;

	//bean variables for emailing deputy commissioner when team ecxeeds pims guideline
	private String totalpims = "";
	private String pimsteamname = "";
	private String managerrows = "";
	private String playerrows = "";
	private String todaysdate = "";
	private Boolean hometeamexceeds = false;
	private Boolean awayteamexceeds = false;


	//
	// lets go get it!
	//
	public GamesheetBean() {
	}

	 @PostConstruct
	 @SuppressWarnings("unchecked")
	 public void init() {
		 
		 LOGGER.info(" *************** START :POST INIT FOR GAMESHEET  BEAN *****************");
		 
		 //lets grab team id so we know which team we are working with
		 HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		 
		 if(hsr.getParameter("gotoprint") != null)
	        {
	    	 navigatetoprint = true;
	        }		
		
		 
		 
		 if(hsr.getParameter("teamid") != null)
	        {
	    		teamid = Integer.parseInt(hsr.getParameter("teamid").toString());
	        }		
		 
		 if(hsr.getParameter("id") != null)
	        {
	    		livegameid = Integer.parseInt(hsr.getParameter("id").toString());
	        }		
		
		 if (livegameid==null){
			 this.setLivegame(pb.getSelectedlivegame());
		 } else {
			 loadLivegame(livegameid);
		 }
		 LOGGER.info("/// here is selected live game.." + this.getLivegame());
		 this.setTeamclubid(pb.getClubID());
		 
		 
		 
		 refreshBean();
		 
		 FacesMessages.info("Select upto 4 coaches to be printed on the scoresheet for this game.");
	 }
	 
	 private void refreshBean(){
		if (this.getLivegame() != null) {
			if (this.getLivegame().getSheetname().equals("Mites")){
				refreshMites();
			}else {

				if (this.teamid.equals(this.livegame.getHometeam().ID)) {
					this.setHometeam(this.refreshHomeRoster());
					this.setHometeamcoach(this.refreshHomeCoachRoster());
					this.setHometeamforpl(this.refreshHomeRosterforpl());
					this.scoringpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
					this.setHomescoring(this.refreshHomeScoring(false));
					this.sogpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
					this.setHomesogs(this.refreshHomeSog(false));
					this.penpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
					this.setHomepenalties(this.refreshHomePenalty(false));
					this.setTeamclubid(this.livegame.getHomeclubid());
					this.setNote(this.loadNote());
				} else {
					this.setAwayteam(this.refreshAwayRoster());
					this.setHometeam(this.refreshAwayRoster());
					this.setHometeamcoach(this.refreshAwayCoachRoster());
					this.setAwayteamforpl(this.refreshAwayRosterforpl());
					this.scoringpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
					this.setHomescoring(this.refreshHomeScoring(false));
					this.sogpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
					this.setHomesogs(this.refreshHomeSog(false));
					this.penpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
					this.setHomepenalties(this.refreshHomePenalty(false));
					this.setTeamclubid(this.livegame.getAwayclubid());
					this.setNote(this.loadNote());
				}

			}
			 
			 //this.setAwayteam(this.refreshAwayRoster());
			 
			 
			 
			 
			 
			 
			 //this.setAwaysogs(this.refreshAwaySog());
		 } else {
			 LOGGER.info(" ##### NO LIVE GAME PASSED... IT WAS NULL #####");
			 
		 }
		 
		 setPenaltiesPickList();
		 setVenuesPickList();

		 if (this.livegame != null){
			 this.htpick.put(this.livegame.getHometeam().getTeamname(), this.livegame.getHometeam().ID+"");
			 this.htpick.put(this.livegame.getAwayteam().getTeamname(), this.livegame.getAwayteam().ID+"");
			 
		 	 this.atpick.put(this.livegame.getAwayteam().getTeamname(),this.livegame.getAwayteam().ID+"");
			 this.atpick.put(this.livegame.getHometeam().getTeamname(),this.livegame.getHometeam().ID+"");
		 }
		 
		 this.typepick.put("Pre Season","Pre");
		 this.typepick.put("Exhibition","Exh");
		 this.typepick.put("Game","Game");
		 
		 this.statepick.put("Scheduled","Scheduled");
		 this.statepick.put("Cancelled","Cancelled");
		 this.statepick.put("Forfiet","Forfiet");
		 this.statepick.put("In Progress","InProgress");
		 this.statepick.put("Complete","Complete");
		 this.statepick.put("Stats Review","StatsReview");
		 this.statepick.put("Final","Final");
		 this.statepick.put("Pending","Pending");

		 this.setDisplayValues();

		 LOGGER.info(" *************** FINISH :POST INIT FOR GAMESHEET BEAN *****************");
		 
	 }
	
	
	 /**
	  * for the given live game.. lets set up the display values
	  */
	private void setDisplayValues() {
		
		/*LOGGER.info("id Live Game is:" + this.livegame.ID);*/
		if (this.livegame != null){
			this.lgdate = this.livegame.getStartdate();
			this.lgtime = this.livegame.getStarttime();
			this.lgsheet = this.livegame.getSheetname();
			this.lgvenue = getStringKeyFromValue(this.venues,this.livegame.getVenuetag());
			this.lgtype =  getStringKeyFromValue(this.typepick, this.livegame.getTypetag());
			this.lgstate = getStringKeyFromValue(this.statepick, this.livegame.getStatetag());
			this.lgstateval =this.livegame.getStatetag();
			this.lgtypeval = this.livegame.getTypetag();
			this.lgvenueval =this.livegame.getVenuetag();
			this.lghteam = this.livegame.getHometeamname();
			this.lgateam = this.livegame.getAwayteamname();
			this.lghteamval = this.livegame.getHometeam().ID+"";
			this.lgateamval = this.livegame.getAwayteam().ID+"";
		}
	}

	public void setSelectedteam1rosterspot(LiveGameRosterSpot tid){
		this.selectedteam1rosterspot=tid;
	}

	public LiveGameRosterSpot getSelectedteam1rosterspot() {
		return selectedteam1rosterspot;
	}

	public void setSelectedteam2rosterspot(LiveGameRosterSpot tid){
		this.selectedteam2rosterspot=tid;
	}

	public LiveGameRosterSpot getSelectedteam2rosterspot() {
		return selectedteam2rosterspot;
	}

	public void setSelectedteam3rosterspot(LiveGameRosterSpot tid){
		this.selectedteam3rosterspot=tid;
	}

	public LiveGameRosterSpot getSelectedteam3rosterspot() {
		return selectedteam3rosterspot;
	}

	public void setSelectedteam4rosterspot(LiveGameRosterSpot tid){
		this.selectedteam4rosterspot=tid;
	}

	public LiveGameRosterSpot getSelectedteam4rosterspot() {
		return selectedteam4rosterspot;
	}

	public void setTeam1penpicklist(List<LiveGameRosterSpot> tid){
		this.team1penpicklist=tid;
	}


	public  List<LiveGameRosterSpot> getTeam1penpicklist() {
		return team1penpicklist;
	}

	public void setTeam2penpicklist(List<LiveGameRosterSpot> tid){
		this.team2penpicklist=tid;
	}

	public List<LiveGameRosterSpot> getTeam2penpicklist() {
		return team2penpicklist;
	}

	public void setTeam3penpicklist(List<LiveGameRosterSpot> tid){
		this.team3penpicklist=tid;
	}

	public List<LiveGameRosterSpot> getTeam3penpicklist() {
		return team3penpicklist;
	}

	public void setTeam4penpicklist(List<LiveGameRosterSpot> tid){
		this.team4penpicklist=tid;
	}

	public List<LiveGameRosterSpot> getTeam4penpicklist() {
		return team4penpicklist;
	}
	public void setTeam1coach(LiveGameRosterSpotList tid){
		this.team1coach=tid;
	}

	public LiveGameRosterSpotList getTeam1coach() {
		return team1coach;
	}

	public void setTeam2coach(LiveGameRosterSpotList tid){
		this.team2coach=tid;
	}

	public LiveGameRosterSpotList getTeam2coach() {
		return team2coach;
	}

	public void setTeam3coach(LiveGameRosterSpotList tid){
		this.team3coach=tid;
	}

	public LiveGameRosterSpotList getTeam3coach() {
		return team3coach;
	}

	public void setTeam4coach(LiveGameRosterSpotList tid){
		this.team4coach=tid;
	}

	public LiveGameRosterSpotList getTeam4coach() {
		return team4coach;
	}



	public void setTeam1(LiveGameRosterSpotList tid){
		this.team1=tid;
	}

	public LiveGameRosterSpotList getTeam1() {
		return team1;
	}

	public void setTeam2(LiveGameRosterSpotList tid){
		this.team2=tid;
	}

	public LiveGameRosterSpotList getTeam2() {
		return team2;
	}

	public void setTeam3(LiveGameRosterSpotList tid){
		this.team3=tid;
	}

	public LiveGameRosterSpotList getTeam3() {
		return team3;
	}

	public void setTeam4(LiveGameRosterSpotList tid){
		this.team4=tid;
	}

	public LiveGameRosterSpotList getTeam4() {
		return team4;
	}

	public String getNote(){
		return this.note;
	}
	public void setNote(String tid){
		this.note=tid;
	}




	public void setLivegameid(Integer tid){
		livegameid=tid;
	}
	
	
	public Integer getLivegameid(){
		return livegameid;
	}
	
	
	public void setNavigatetoprint(Boolean tid){
		navigatetoprint=tid;
	}
	
	
	public Boolean getNavigatetoprint(){
		return navigatetoprint;
	}
	
	
	
	public void setIscoachselectedforgame(Boolean tid){
		iscoachselectedforgame=tid;
	}
	
	
	public Boolean getIscoachselectedforgame(){
		return iscoachselectedforgame;
	}
	
	
	public void setNumberofgrows(Integer tid){
		numberofgrows=tid;
	}
	
	
	public List<Integer> getNumberofgrows(){
		
	        List<Integer> values = new ArrayList<Integer>();
	        for (int i = 1; i < 18; i++) {
	            values.add(i);
	        }
	        return values;
	    
	}
	
	public void setMaxcoachsizehome(Integer tid){
		maxcoachsizehome=tid;
	}
	
	
	public Integer getMaxcoachsizehome(){
		return maxcoachsizehome;
	}
	
	public void setMaxcoachsizeaway(Integer tid){
		maxcoachsizeaway=tid;
	}
	
	
	public Integer getMaxcoachsizeaway(){
		return maxcoachsizeaway;
	}
	
	
	
	public void setHome(Boolean tid){
		home=tid;
	}
	
	
	public Boolean getHome(){
		return home;
	}
	
	
	
	public void setTeamclubid(Integer tid){
		teamclubid=tid;
	}
	
	
	public Integer getTeamclubid(){
		return teamclubid;
	}

	public void Miteteamid1(Integer tid){
		miteteamid1=tid;
	}


	public Integer getMiteteamid1(){
		return miteteamid1;
	}

	public void setMiteteamid2(Integer tid){
		miteteamid2=tid;
	}


	public Integer getMiteteamid2(){
		return miteteamid2;
	}

	public void setMiteteamid3(Integer tid){
		miteteamid3=tid;
	}


	public Integer getMiteteamid3(){
		return miteteamid3;
	}

	public void setMiteteamid4(Integer tid){
		miteteamid4=tid;
	}

	public Integer setMiteteamid4(){
		return miteteamid4;
	}

	public void setTeamclubid1(Integer tid){
		teamclubid1=tid;
	}


	public Integer getTeamclubid1(){
		return teamclubid1;
	}

	public void setTeamclubid2(Integer tid){
		teamclubid2=tid;
	}


	public Integer getTeamclubid2(){
		return teamclubid2;
	}

	public void setTeamclubid3(Integer tid){
		teamclubid3=tid;
	}


	public Integer getTeamclubid3(){
		return teamclubid3;
	}

	public void setTeamclubid4(Integer tid){
		teamclubid4=tid;
	}


	public Integer getTeamclubid4(){
		return teamclubid4;
	}

	public Integer getTeamclubid(String label){
		Integer clubid = 0;
		switch(label) {
			case "Team1":
				// code block
				clubid= getTeamclubid1();
				break;
			case "Team2":
				// code block
				clubid= getTeamclubid2();
				break;
			case "Team3":
				// code block
				clubid=getTeamclubid3();
				break;
			case "Team4":
				// code block
				clubid= getTeamclubid4();
				break;
			default:
				// code block
				clubid=getTeamclubid1();
		}

		return clubid;
	}



	public void setNumpimsadd(Integer tid){
		numpimsadd=tid;
	}
	
	
	public Integer getNumpimsadd(){
		return numpimsadd;
	}
	
	
	public void setNumsogadd(Integer tid){
		numsogadd=tid;
	}
	
	public Integer getNumsogadd(){
		return numsogadd;
	}
	
	
	public void setNumgoalsadd(Integer tid){
		numgoalsadd=tid;
	}
	
	public Integer getNumgoalsadd(){
		return numgoalsadd;
	}
	
	
	public void setTeamid(Integer tid){
		teamid=tid;
	}
	
	public Integer getTeamid(){
		return teamid;
	}
	
	/**
	 * @return the selectedhomerosterspot
	 */
	public LiveGameRosterSpot getSelectedhomerosterspot() {
		return selectedhomerosterspot;
	}

	/**
	 * @param selectedhomerosterspot the selectedhomerosterspot to set
	 */
	public void setSelectedhomerosterspot(LiveGameRosterSpot selectedhomerosterspot) {
		this.selectedhomerosterspot = selectedhomerosterspot;
	}

	public LiveGameRosterSpot getSelectedhomecoachrosterspot() {
		return selectedhomecoachrosterspot;
	}

	public void setSelectedhomecoachrosterspot(LiveGameRosterSpot selectedhomecoachrosterspot) {
		this.selectedhomecoachrosterspot = selectedhomecoachrosterspot;
	}

	/**
	 * @return the selectedawayrosterspot
	 */
	public LiveGameRosterSpot getSelectedawayrosterspot() {
		return selectedawayrosterspot;
	}

	/**
	 * @param selectedawayrosterspot the selectedawayrosterspot to set
	 */
	public void setSelectedawayrosterspot(LiveGameRosterSpot selectedawayrosterspot) {
		this.selectedawayrosterspot = selectedawayrosterspot;
	}

	/**
	 * @return the awayteam
	 */
	public LiveGameRosterSpotList getAwayteam() {
		return awayteam;
	}

	/**
	 * @param awayteam the awayteam to set
	 */
	public void setAwayteamforpl(LiveGameRosterSpotList awayteam) {
		this.awayteamforpl = awayteam;
	}

	public LiveGameRosterSpotList getAwayteamforpl() {
		return awayteamforpl;
	}

	/**
	 * @param awayteam the awayteam to set
	 */
	public void setAwayteam(LiveGameRosterSpotList awayteam) {
		this.awayteam = awayteam;
	}

	/**
	 * @return the hometeam
	 */
	public LiveGameRosterSpotList getHometeam() {
		return hometeam;
	}

	public LiveGameRosterSpotList getHometeamcoach() {
		return hometeamcoach;
	}

	/**
	 * @param hometeam the hometeam to set
	 */
	public void setHometeamforpl(LiveGameRosterSpotList hometeam) {
		this.hometeamforpl = hometeam;
	}


	/**
	 * @return the hometeam
	 */
	public LiveGameRosterSpotList getHometeamforpl() {
		return hometeamforpl;
	}

	/**
	 * @param hometeam the hometeam to set
	 */
	public void setHometeam(LiveGameRosterSpotList hometeam) {
		this.hometeam = hometeam;
	}

	public void setHometeamcoach(LiveGameRosterSpotList hometeam) {
		this.hometeamcoach = hometeam;
	}

	/**
	 * @return the scaha
	 */
	public ScahaBean getScaha() {
		return scaha;
	}

	/**
	 * @param scaha the scaha to set
	 */
	public void setScaha(ScahaBean scaha) {
		this.scaha = scaha;
	}

	/**
	 * @return the pb
	 */
	public ProfileBean getPb() {
		return pb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setPb(ProfileBean pb) {
		this.pb = pb;
	}

	/**
	 * @return the pb
	 */
	public rosterBean getRb() {
		return rb;
	}

	public void setRb(rosterBean rb) {
		this.rb = rb;
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

	public LiveGameRosterSpotList refreshHomeRoster() {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactory(pb.getProfile(), db, this.getLivegame(), scaha.getScahaTeamList(), "H");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		return list;
	}

	public LiveGameRosterSpotList refreshHomeCoachRoster() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactoryCoach(pb.getProfile(), db, this.getLivegame(), scaha.getScahaTeamList(), "H");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			db.free();
		}

		Integer x = list.getRowCount();

		for (int i = x; i < 4; i++) {
				LiveGameRosterSpot spot = new LiveGameRosterSpot(0,pb.getProfile());
				spot.setIdRoster(0);
				spot.setIdPerson(0);
				spot.setRostertype("");
				spot.setJerseynumber("");
				spot.setLname("");
				spot.setFname("");
				spot.setMia(false);
				spot.setTeam(this.getLivegame().getHometeam());
				spot.setLivegame(this.getLivegame());
				spot.setTag("");
				spot.setRank(i+1);
				list.add(spot);
				LOGGER.info("Found a match " + spot);
		}

		return list;
	}

	public LiveGameRosterSpotList refreshAwayCoachRoster() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactoryCoach(pb.getProfile(), db, this.getLivegame(), scaha.getScahaTeamList(), "A");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			db.free();
		}
		Integer x = list.getRowCount();

		for (int i = x; i < 4; i++) {
			LiveGameRosterSpot spot = new LiveGameRosterSpot(0,pb.getProfile());
			spot.setIdRoster(0);
			spot.setIdPerson(0);
			spot.setRostertype("");
			spot.setJerseynumber("");
			spot.setLname("");
			spot.setFname("");
			spot.setMia(false);
			spot.setTeam(this.getLivegame().getAwayteam());
			spot.setLivegame(this.getLivegame());
			spot.setTag("");
			spot.setRank(i+1);
			list.add(spot);
			LOGGER.info("Found a match " + spot);
		}

		return list;
	}

	public LiveGameRosterSpotList refreshHomeRosterforpl() {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactoryByJerseyNumber(pb.getProfile(), db, this.getLivegame(), scaha.getScahaTeamList(), "H");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		return list;
	}
	
	public LiveGameRosterSpotList refreshAwayRoster() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactory(pb.getProfile(), db, this.getLivegame(), scaha.getScahaTeamList(), "A");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		
		return list;
	}
	
	public LiveGameRosterSpotList refreshAwayRosterforpl() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactoryByJerseyNumber(pb.getProfile(), db, this.getLivegame(), scaha.getScahaTeamList(), "A");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		
		return list;
	}
	
public SogList refreshHomeSog(Boolean bAddsogrows) {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		SogList list = null;
		try {
			if (this.teamid.equals(this.livegame.getHometeam().ID)){
				list = SogList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getHometeam(), this.getHometeam());
			}else{
				list = SogList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getAwayteam(), this.getAwayteam());	
			}
			

			Sog sog = new Sog(0,pb.getProfile());
			if (this.teamid.equals(this.livegame.getHometeam().ID)){
				sog.setTeam(this.getLivegame().getHometeam());
			} else {
				sog.setTeam(this.getLivegame().getAwayteam());
			}
			sog.setLivegame(this.getLivegame());

			LiveGameRosterSpot lgrs = new LiveGameRosterSpot(0, pb.getProfile());

			sog.setIdroster(0);
			sog.setRosterspot(lgrs);
			sog.setShots1(0);
			sog.setShots2(0);
			sog.setShots3(0);
			sog.setShots4(0);
			sog.setShots5(0);
			sog.setShots6(0);
			sog.setShots7(0);
			sog.setShots8(0);
			sog.setShots9(0);
			sog.setPlaymins("00");
			sog.setPlaysecs("00");
			sog.setPlaytime("00:00:00");
			list.add(sog);


			

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
			
			
		}
		
	
		return list;
	}


	public SogList refreshAwaySog() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		SogList list = null;
		try {
			list = SogList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getAwayteam(), this.getAwayteam());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		return list;
	}



	private ScoringList refreshHomeScoring(Boolean bAddgoalrows) {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		ScoringList list = null;
		try {
			if (this.teamid.equals(this.livegame.getHometeam().ID)){
				list = ScoringList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getHometeam(), this.getHometeam());
			}else {
				list = ScoringList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getAwayteam(), this.getAwayteam());
			}
			

			Scoring score = new Scoring(0,pb.getProfile(), this.getLivegame(), this.getLivegame().getHometeam());
			if (this.teamid.equals(this.livegame.getHometeam().ID)){
				score.setTeam(this.getLivegame().getHometeam());
			} else {
				score.setTeam(this.getLivegame().getAwayteam());
			}
				
				
			LiveGameRosterSpot lgrs = new LiveGameRosterSpot(0, pb.getProfile());
			score.setCount(list.getRowCount()+1);
			score.setIdrostergoal(0);
			score.setLgrosterspotgoal(lgrs);
			score.setIdrostera1(0);
			score.setLgrosterspota1(lgrs);
			score.setIdrostera2(0);
			score.setLgrosterspota2(lgrs);
			score.setGoaltype("");
			score.setPeriod(0);
			score.setGoalmin("00");
			score.setGoalsec("00");
			score.setTimescored("");

			list.add(score);
				

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
			db.free();
		}
		
		
		
		return list;
	}

	private ScoringList refreshAwayScoring() {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		ScoringList list = null;
		try {
			list = ScoringList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getAwayteam(), this.getAwayteam());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		
			db.free();
		}
		
		return list;
	}
	
	private PenaltyList refreshHomePenalty(Boolean bAddPimrows) {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		PenaltyList list = null;
		try {
			if (this.teamid.equals(this.livegame.getHometeam().ID)){
				list = PenaltyList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getHometeam(), this.getHometeam());
			}else {
				list = PenaltyList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getAwayteam(), this.getAwayteam());
			}
			
			Penalty pen = new Penalty(0,pb.getProfile());
			LiveGameRosterSpot lgrs = new LiveGameRosterSpot(0, pb.getProfile());
			pen.setCount(list.getRowCount()+1);
			if (this.teamid.equals(this.livegame.getHometeam().ID)){
				pen.setTeam(this.getLivegame().getHometeam());
			} else {
				pen.setTeam(this.getLivegame().getAwayteam());
			}
			pen.setLivegame(this.getLivegame());
			pen.setIdroster(0);
			pen.setRosterspot(lgrs);
			pen.setPeriod(0);
			pen.setPenaltytype("");
			pen.setMinutes("");
			pen.setPenmin("00");
			pen.setPensec("00");
			pen.setTimeofpenalty("00:00:00");
			list.add(pen);

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
		
		return list;
	}
	
	private PenaltyList refreshAwayPenalty() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		PenaltyList list = null;
		try {
			list = PenaltyList.NewListFactory(pb.getProfile(), db, this.getLivegame(), this.getLivegame().getAwayteam(), this.getAwayteam());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
		
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public void newGoal(String _ha) {
		
		if (_ha.equals("H")) { 
			this.scoringteam = this.livegame.getHometeam();
			this.scoringroster = this.getHometeam();
			this.scoringpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentscore = new Scoring(0,pb.getProfile(),this.livegame,this.scoringteam);
		} else {
			this.scoringteam = this.livegame.getAwayteam();
			this.scoringroster = this.getAwayteam();
			this.scoringpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentscore = new Scoring(0,pb.getProfile(),this.livegame,this.scoringteam);
		}
		
		//
		// reinitialize the info
		//
		this.goalperiod = 0;
		this.goalmin = null;
		this.goalsec = null;
		this.goaltype = null;
		this.selectedgoalroseterid = 0;
		this.selecteda1roseterid = 0;
		this.selecteda2roseterid = 0;

		
	}
	
	@SuppressWarnings("unchecked")
	public void newPenalty(String _ha) {
		
		if (_ha.equals("H")) { 
			this.penteam = this.livegame.getHometeam();
			this.penroster = this.getHometeam();
			this.penpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentpenalty = new Penalty(0,pb.getProfile(),this.livegame,this.penteam);
		} else {
			this.penteam = this.livegame.getAwayteam();
			this.penroster = this.getAwayteam();
			this.penpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentpenalty = new Penalty(0,pb.getProfile(),this.livegame,this.penteam);
		}
		
		//
		// reinitialize the info
		//
		this.penperiod = 0;
		this.penminutes = null;
		this.penmin = null;
		this.pensec = null;
		this.pentype = null;
		this.selectedpenrosterid = 0;
		
	}
	
	@SuppressWarnings("unchecked")
	public void editGoal(String _ha) {
		
		if (_ha.equals("H")) { 
			this.scoringteam = this.livegame.getHometeam();
			this.scoringroster = this.getHometeam();
			this.scoringpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentscore = this.homescoring.getByKey(this.selectedhomescore.ID);
		} else {
			this.scoringteam = this.livegame.getAwayteam();
			this.scoringroster = this.getAwayteam();
			this.scoringpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentscore = this.awayscoring.getByKey(this.selectedawayscore.ID);
		}

		
		//
		// reinitialize the info to the current selected score
		//
		LOGGER.info("currentscore is: " + currentscore);
		
		this.goalperiod = currentscore.getPeriod();
		String[] ms = currentscore.getTimescored().split(":");
		this.goalmin = ms[0];
		this.goalsec = ms[1];
		this.goaltype = currentscore.getGoaltype();
		this.selectedgoalroseterid = currentscore.getIdrostergoal();
		this.selecteda1roseterid = currentscore.getIdrostera1();
		this.selecteda2roseterid = currentscore.getIdrostera2();
		
	}
	
	
	@SuppressWarnings("unchecked")
	public void editPenalty(String _ha) {
		
		if (_ha.equals("H")) { 
			this.penteam = this.livegame.getHometeam();
			this.penroster = this.getHometeam();
			this.penpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentpenalty = this.homepenalties.getByKey(this.selectedhomepenalty.ID);
		} else {
			this.penteam = this.livegame.getAwayteam();
			this.penroster = this.getAwayteam();
			this.penpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentpenalty = this.awaypenalties.getByKey(this.selectedawaypenalty.ID);
		}
		
		//
		// reinitialize the info
		//
		this.penperiod = currentpenalty.getPeriod();
		this.penminutes = currentpenalty.getMinutes();
		String[] ms = currentpenalty.getTimeofpenalty().split(":");
		this.penmin = ms[0];
		this.pensec = ms[1];
		this.pentype = currentpenalty.getPenaltytype();
		this.selectedpenrosterid = currentpenalty.getIdroster();
		
	}
	
	@SuppressWarnings("unchecked")
	public void editSog(String _ha) {
		
		if (_ha.equals("H")) { 
			this.sogteam = this.livegame.getHometeam();
			this.sogroster = this.getHometeam();
			this.sogpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentsog = this.homesogs.getByKey(this.selectedhomesog.ID);
		} else {
			this.sogteam = this.livegame.getAwayteam();
			this.sogroster = this.getAwayteam();
			this.sogpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentsog = this.awaysogs.getByKey(this.selectedawaysog.ID);
		}
		
		//
		// reinitialize the info
		//
		this.sogplaytime = currentsog.getPlaytime();
		this.sogshots1 = currentsog.getShots1();
		this.sogshots2 = currentsog.getShots2();
		this.sogshots3 = currentsog.getShots3();
		this.sogshots4 = currentsog.getShots4();
		this.sogshots5 = currentsog.getShots5();
		this.sogshots6 = currentsog.getShots6();
		this.sogshots7 = currentsog.getShots7();
		this.sogshots8 = currentsog.getShots8();
		this.sogshots9 = currentsog.getShots9();
		this.selectedsogrosterid = currentsog.getIdroster();
		
	}
	
	@SuppressWarnings("unchecked")
	public void newSog(String _ha) {
		
		if (_ha.equals("H")) { 
			this.sogteam = this.livegame.getHometeam();
			this.sogroster = this.getHometeam();
			this.sogpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
			this.currentsog = new Sog(0,pb.getProfile(),this.livegame,this.sogteam);
		} else {
			this.sogteam = this.livegame.getAwayteam();
			this.sogroster = this.getAwayteam();
			this.sogpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
			this.currentsog = new Sog(0,pb.getProfile(),this.livegame,this.sogteam);
		}
		
		//
		// reinitialize the info
		//
		this.sogplaytime = "";
		this.sogshots1 = 0;
		this.sogshots2 = 0;
		this.sogshots3 = 0;
		this.sogshots4 = 0;
		this.sogshots5 = 0;
		this.sogshots6 = 0;
		this.sogshots7 = 0;
		this.sogshots8 = 0;
		this.sogshots9 = 0;
		this.selectedsogrosterid = 0;
		
	}
	
	
	/**
	 * For the selected Player.. we need to toggle his MIA...
	 * Then refresh the list
	 */
	public void toggleMIA(LiveGameRosterSpot spot) {
		
		LOGGER.info("toggling MIA for " + spot);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			CallableStatement pc = db.prepareCall("call scaha.toggleMIA(?,?,?)");
			pc.setInt(1,this.livegame.ID);
			pc.setInt(2, spot.ID);
			pc.setInt(3, (spot.isMia() ? 1 : 0));
			pc.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 	
		db.free();
		
		spot.setMia(!spot.isMia());
//		if (_ha.equals("H")) {
//			refreshHomeRoster();
//		} else {
//			refreshAwayRoster();
//		}
		
	}
	
	public String getHomeClubId() {
		return this.getLivegame().getHometeam().getTeamClub().ID+"";
	}

	public String getAwayClubId() {
		
		return this.getLivegame().getAwayteam().getTeamClub().ID+"";
	}

	@Override
	public String getSubject() {
		String subject;
		if (hometeamexceeds || awayteamexceeds) {
			subject = this.pimsteamname + " Exceeded Penalty Minute Threshold";
		}else {
			subject = "SCAHA Game Change Notification for " + this.getLivegame();
		}
		return subject;
	}

	@Override
	public String getTextBody() {
		List<String> myTokens = new ArrayList<String>();
		if (hometeamexceeds || awayteamexceeds){
			myTokens.add("TOTALPIMS|" + this.totalpims);
			myTokens.add("SENDDATE|" + this.todaysdate);
			myTokens.add("TEAMNAME|" + this.pimsteamname);
			myTokens.add("MANAGERROWS|" + this.managerrows);
			myTokens.add("PLAYERROWS|" + this.playerrows);
			return Utils.mergeTokens(this.mail_teampims_body, myTokens, "\\|");
		}else {
			myTokens.add("SCHEDULE|" + this.livegame.getSched().getDescription());
			myTokens.add("HOMETEAM|" + this.livegame.getHometeamname());
			myTokens.add("AWAYTEAM|" + this.livegame.getAwayteamname());

			myTokens.add("GAMENUMBER|" + this.livegame.ID+"");
			myTokens.add("OLDTYPE|" + this.livegame.getTypetag());
			myTokens.add("OLDSTATE|" + this.livegame.getStatetag());
			myTokens.add("OLDHOMETEAM|" + this.livegame.getHometeamname());
			myTokens.add("OLDAWAYTEAM|" + this.livegame.getAwayteamname());
			myTokens.add("OLDVENUE|" + getStringKeyFromValue(this.venues,this.livegame.getVenuetag()));
			myTokens.add("OLDSHEET|" + this.livegame.getSheetname());
			myTokens.add("OLDDATE|" + this.livegame.getStartdate());
			myTokens.add("OLDTIME|" + this.livegame.getStarttime());
			myTokens.add("NEWTYPE|" + this.lgtypeval);
			myTokens.add("NEWSTATE|" + this.lgstate);
			myTokens.add("NEWHOMETEAM|" + this.lghteam);
			myTokens.add("NEWAWAYTEAM|" + this.lgateam);
			myTokens.add("NEWVENUE|" + this.lgvenue);
			myTokens.add("NEWSHEET|" + this.lgsheet);
			myTokens.add("NEWDATE|" + this.lgdate);
			myTokens.add("NEWTIME|" + this.lgtime);
			return Utils.mergeTokens(GamesheetBean.mail_reg_body,myTokens,"\\|");
		}
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

	@Override
	public InternetAddress[] getToMailIAddress() {
		//
		// Here is where we get all the e-mails we need to get
		//R
		if (hometeamexceeds || awayteamexceeds) {
			List<InternetAddress> data = new ArrayList<InternetAddress>();
			try {

				data.add(new InternetAddress("lahockeyfan2@yahoo.com", "Rob Foster"));
				data.add(new InternetAddress("rvoulelikas@gmail.com", "Rosemary Voulelikas"));

			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return data.toArray(new InternetAddress[data.size()]);
		}
		else {
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			List<InternetAddress> data = new ArrayList<InternetAddress>();

			LOGGER.info(this.livegame.toString());
			try {
				PreparedStatement ps = db.prepareCall("call scaha.getLiveGameEmails(?)");
				ps.setInt(1, this.livegame.ID);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					data.add(new InternetAddress(rs.getString(2), rs.getString(1)));
				}
				rs.close();
				ps.close();

				for (InternetAddress ia : data) {
					LOGGER.info("e-mail:" + ia);
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			db.free();

			return data.toArray(new InternetAddress[data.size()]);
		}
	}

	@Override
	public InternetAddress[] getPreApprovedICC() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the awayscoring
	 */
	public ScoringList getAwayscoring() {
		return awayscoring;
	}

	/**
	 * @param awayscoring the awayscoring to set
	 */
	public void setAwayscoring(ScoringList awayscoring) {
		this.awayscoring = awayscoring;
	}

	/**
	 * @return the homescoring
	 */
	public ScoringList getHomescoring() {
		return homescoring;
	}
	
	public int getDerivedHomeScore() {
		return this.homescoring.getRowCount();
	}

	public int getDerivedAwayScore() {
		return this.awayscoring.getRowCount();
	}

	
	/**
	 * @param homescoring the homescoring to set
	 */
	public void setHomescoring(ScoringList homescoring) {
		this.homescoring = homescoring;
	}

	/**
	 * @return the selectedhomescore
	 */
	public Scoring getSelectedhomescore() {
		return selectedhomescore;
	}

	/**
	 * @param selectedhomescore the selectedhomescore to set
	 */
	public void setSelectedhomescore(Scoring selectedhomescore) {
		this.selectedhomescore = selectedhomescore;
	}

	/**
	 * @return the selectedawayscore
	 */
	public Scoring getSelectedawayscore() {
		return selectedawayscore;
	}

	/**
	 * @param selectedawayscore the selectedawayscore to set
	 */
	public void setSelectedawayscore(Scoring selectedawayscore) {
		this.selectedawayscore = selectedawayscore;
	}

	
	public void deleteGoal(Scoring sc) {
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try {
			sc.delete(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		this.setAwayscoring(this.refreshAwayScoring());
		this.setHomescoring(this.refreshHomeScoring(false));
		
	}
	
	public void deletePenalty(Penalty pen) {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase","GsB.deletePenalty");
		try {
			pen.delete(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
		//this.setAwaypenalties(this.refreshAwayPenalty());
		this.setHomepenalties(this.refreshHomePenalty(false));
	}

	public void deleteSog(Sog sog) {
		LOGGER.info("we need to delete: " + sog);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", "GsB.deleteSog");
		try {
			sog.delete(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
		this.setHomesogs(this.refreshHomeSog(false));
		this.setAwaysogs(this.refreshAwaySog());
		
	}
	
	public void saveSog(Sog sog) {
		
		//LOGGER.info("HERE IS WHERE WE save a SOG for " + this.sogteam.getTeamname());
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", "GsB.saveSog");
		
		/*sog.setShots1(this.getSogshots1());
		sog.setShots2(this.getSogshots2());
		sog.setShots3(this.getSogshots3());
		sog.setShots4(this.getSogshots4());*/
		
		sog.setPlaytime(("00:" + sog.getPlaymins() + ":" + sog.getPlaysecs()));
		   
	   sog.setShots5(this.getSogshots5());
		sog.setShots6(this.getSogshots6());
		sog.setShots7(this.getSogshots7());
		sog.setShots8(this.getSogshots8());
		sog.setShots9(this.getSogshots9());
		try {
			sog.update(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
		this.setHomesogs(this.refreshHomeSog(false));
	}
	public void saveGoal(Scoring score) {
		
		//LOGGER.info("HERE IS WHERE WE save a GOAL for " + this.scoringteam.getTeamname());
		
		score.setTimescored("00:" + score.getGoalmin() + ":" + score.getGoalsec());
		
		//LOGGER.info("updating score for " + score);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try {
			score.update(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		
		this.setHomescoring(this.refreshHomeScoring(false));
		
	}

	public void savePenalty(Penalty pen) {
		
		//LOGGER.info("HERE IS WHERE WE save a Penalty for " + this.penteam.getTeamname());
		
		pen.setTimeofpenalty(("00:" + pen.getPenmin() + ":" + pen.getPensec()));
		
		//LOGGER.info("updating score for " + pen);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try {
			pen.update(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		//this.setAwaypenalties(this.refreshAwayPenalty());
		this.setHomepenalties(this.refreshHomePenalty(false));
		
	}

	/**
	 * @return the goalperiod
	 */
	public int getGoalperiod() {
		return goalperiod;
	}

	/**
	 * @param goalperiod the goalperiod to set
	 */
	public void setGoalperiod(int goalperiod) {
		this.goalperiod = goalperiod;
	}

	/**
	 * @return the goaltype
	 */
	public String getGoaltype() {
		return goaltype;
	}

	/**
	 * @param goaltype the goaltype to set
	 */
	public void setGoaltype(String goaltype) {
		this.goaltype = goaltype;
	}

	/**
	 * @return the goalmin
	 */
	public String getGoalmin() {
		return goalmin;
	}

	/**
	 * @param goalmin the goalmin to set
	 */
	public void setGoalmin(String goalmin) {
		this.goalmin = goalmin;
	}

	/**
	 * @return the goalsec
	 */
	public String getGoalsec() {
		return goalsec;
	}

	/**
	 * @param goalsec the goalsec to set
	 */
	public void setGoalsec(String goalsec) {
		this.goalsec = goalsec;
	}

	/**
	 * @return the selectedgoalroseterid
	 */
	public int getSelectedgoalroseterid() {
		return selectedgoalroseterid;
	}

	/**
	 * @param selectedgoalroseterid the selectedgoalroseterid to set
	 */
	public void setSelectedgoalroseterid(int selectedgoalroseterid) {
		this.selectedgoalroseterid = selectedgoalroseterid;
	}

	/**
	 * @return the selecteda1roseterid
	 */
	public int getSelecteda1roseterid() {
		return selecteda1roseterid;
	}

	/**
	 * @param selecteda1roseterid the selecteda1roseterid to set
	 */
	public void setSelecteda1roseterid(int selecteda1roseterid) {
		this.selecteda1roseterid = selecteda1roseterid;
	}

	/**
	 * @return the selecteda2roseterid
	 */
	public int getSelecteda2roseterid() {
		return selecteda2roseterid;
	}

	/**
	 * @param selecteda2roseterid the selecteda2roseterid to set
	 */
	public void setSelecteda2roseterid(int selecteda2roseterid) {
		this.selecteda2roseterid = selecteda2roseterid;
	}

	/**
	 * @return the scoringpicklist
	 */
	public List<LiveGameRosterSpot> getScoringpicklist() {
		return scoringpicklist;
	}

	/**
	 * @param scoringpicklist the scoringpicklist to set
	 */
	public void setScoringpicklist(List<LiveGameRosterSpot> scoringpicklist) {
		this.scoringpicklist = scoringpicklist;
	}

	/**
	 * @return the scoringteam
	 */
	public ScahaTeam getScoringteam() {
		return scoringteam;
	}

	/**
	 * @param scoringteam the scoringteam to set
	 */
	public void setScoringteam(ScahaTeam scoringteam) {
		this.scoringteam = scoringteam;
	}

	/**
	 * @return the scoringroster
	 */
	public LiveGameRosterSpotList getScoringroster() {
		return scoringroster;
	}

	/**
	 * @param scoringroster the scoringroster to set
	 */
	public void setScoringroster(LiveGameRosterSpotList scoringroster) {
		this.scoringroster = scoringroster;
	}

	/**
	 * @return the currentscore
	 */
	public Scoring getCurrentscore() {
		return currentscore;
	}

	/**
	 * @param currentscore the currentscore to set
	 */
	public void setCurrentscore(Scoring currentscore) {
		this.currentscore = currentscore;
	}



	/**
	 * @return the awaypenalties
	 */
	public PenaltyList getAwaypenalties() {
		return awaypenalties;
	}

	/**
	 * @param awaypenalties the awaypenalties to set
	 */
	public void setAwaypenalties(PenaltyList awaypenalties) {
		this.awaypenalties = awaypenalties;
	}

	/**
	 * @return the homepenalties
	 */
	public PenaltyList getHomepenalties() {
		return homepenalties;
	}

	/**
	 * @param homepenalties the homepenalties to set
	 */
	public void setHomepenalties(PenaltyList homepenalties) {
		this.homepenalties = homepenalties;
	}

	/**
	 * @return the penperiod
	 */
	public int getPenperiod() {
		return penperiod;
	}

	/**
	 * @param penperiod the penperiod to set
	 */
	public void setPenperiod(int penperiod) {
		this.penperiod = penperiod;
	}

	/**
	 * @return the pentype
	 */
	public String getPentype() {
		return pentype;
	}

	/**
	 * @param pentype the pentype to set
	 */
	public void setPentype(String pentype) {
		this.pentype = pentype;
	}

	/**
	 * @return the penminutes
	 */
	public String getPenminutes() {
		return penminutes;
	}

	/**
	 * @param penminutes the penminutes to set
	 */
	public void setPenminutes(String penminutes) {
		this.penminutes = penminutes;
	}

	
	/**
	 * @return the selectedpenrosterid
	 */
	public int getSelectedpenrosterid() {
		return selectedpenrosterid;
	}

	/**
	 * @param selectedpenrosterid the selectedpenrosterid to set
	 */
	public void setSelectedpenrosterid(int selectedpenrosterid) {
		this.selectedpenrosterid = selectedpenrosterid;
	}

	/**
	 * @return the penroster
	 */
	public LiveGameRosterSpotList getPenroster() {
		return penroster;
	}

	/**
	 * @param penroster the penroster to set
	 */
	public void setPenroster(LiveGameRosterSpotList penroster) {
		this.penroster = penroster;
	}

	/**
	 * @return the penteam
	 */
	public ScahaTeam getPenteam() {
		return penteam;
	}

	/**
	 * @param penteam the penteam to set
	 */
	public void setPenteam(ScahaTeam penteam) {
		this.penteam = penteam;
	}

	/**
	 * @return the penpicklist
	 */
	public List<LiveGameRosterSpot> getPenpicklist() {
		return penpicklist;
	}

	/**
	 * @param penpicklist the penpicklist to set
	 */
	public void setPenpicklist(List<LiveGameRosterSpot> penpicklist) {
		this.penpicklist = penpicklist;
	}

	/**
	 * @return the penmin
	 */
	public String getPenmin() {
		return penmin;
	}

	/**
	 * @param penmin the penmin to set
	 */
	public void setPenmin(String penmin) {
		this.penmin = penmin;
	}

	/**
	 * @return the pensec
	 */
	public String getPensec() {
		return pensec;
	}

	/**
	 * @param pensec the pensec to set
	 */
	public void setPensec(String pensec) {
		this.pensec = pensec;
	}

	/**
	 * @return the currentpenalty
	 */
	public Penalty getCurrentpenalty() {
		return currentpenalty;
	}

	/**
	 * @param currentpenalty the currentpenalty to set
	 */
	public void setCurrentpenalty(Penalty currentpenalty) {
		this.currentpenalty = currentpenalty;
	}

	/**
	 * @return the selectedhomepenalty
	 */
	public Penalty getSelectedhomepenalty() {
		return selectedhomepenalty;
	}

	/**
	 * @param selectedhomepenalty the selectedhomepenalty to set
	 */
	public void setSelectedhomepenalty(Penalty selectedhomepenalty) {
		this.selectedhomepenalty = selectedhomepenalty;
	}

	/**
	 * @return the selectedawaypenalty
	 */
	public Penalty getSelectedawaypenalty() {
		return selectedawaypenalty;
	}

	/**
	 * @param selectedawaypenalty the selectedawaypenalty to set
	 */
	public void setSelectedawaypenalty(Penalty selectedawaypenalty) {
		this.selectedawaypenalty = selectedawaypenalty;
	}

	/**
	 * @return the penalties
	 */
	public List<String> getPenalties() {
		return penalties;
	}

	/**
	 * @param penalties the penalties to set
	 */
	public void setPenalties(List<String> penalties) {
		this.penalties = penalties;
	}

	/**
	 * @return the selectedhomesog
	 */
	public Sog getSelectedhomesog() {
		return selectedhomesog;
	}

	/**
	 * @param selectedhomesog the selectedhomesog to set
	 */
	public void setSelectedhomesog(Sog selectedhomesog) {
		this.selectedhomesog = selectedhomesog;
	}

	/**
	 * @return the selectedawaysog
	 */
	public Sog getSelectedawaysog() {
		return selectedawaysog;
	}

	/**
	 * @param selectedawaysog the selectedawaysog to set
	 */
	public void setSelectedawaysog(Sog selectedawaysog) {
		this.selectedawaysog = selectedawaysog;
	}

	/**
	 * @return the selectedsogrosterid
	 */
	public int getSelectedsogrosterid() {
		return selectedsogrosterid;
	}

	/**
	 * @param selectedsogrosterid the selectedsogrosterid to set
	 */
	public void setSelectedsogrosterid(int selectedsogrosterid) {
		this.selectedsogrosterid = selectedsogrosterid;
	}

	/**
	 * @return the sogpicklist
	 */
	public List<LiveGameRosterSpot> getSogpicklist() {
		return sogpicklist;
	}

	/**
	 * @param sogpicklist the sogpicklist to set
	 */
	public void setSogpicklist(List<LiveGameRosterSpot> sogpicklist) {
		this.sogpicklist = sogpicklist;
	}

	public List<RosterForPrint> getRfplist() {
		return rfplist;
	}
	
	/**
	 * @param sogpicklist the sogpicklist to set
	 */
	public void setRfplisthome(List<RosterForPrint> sogpicklist) {
		this.rfplisthome = sogpicklist;
	}

	public List<RosterForPrint> getRfplisthome() {
		return rfplisthome;
	}
	
	public List<RosterForPrint> getRfplistaway() {
		return rfplistaway;
	}
	
	/**
	 * @param sogpicklist the sogpicklist to set
	 */
	public void setRfplistaway(List<RosterForPrint> sogpicklist) {
		this.rfplistaway = sogpicklist;
	}
	
	/**
	 * @param sogpicklist the sogpicklist to set
	 */
	public void setRfplist(List<RosterForPrint> sogpicklist) {
		this.rfplist = sogpicklist;
	}
	
	/**
	 * @return the sogteam
	 */
	public ScahaTeam getSogteam() {
		return sogteam;
	}

	/**
	 * @param sogteam the sogteam to set
	 */
	public void setSogteam(ScahaTeam sogteam) {
		this.sogteam = sogteam;
	}

	/**
	 * @return the sogroster
	 */
	public LiveGameRosterSpotList getSogroster() {
		return sogroster;
	}

	/**
	 * @param sogroster the sogroster to set
	 */
	public void setSogroster(LiveGameRosterSpotList sogroster) {
		this.sogroster = sogroster;
	}



	/**
	 * @return the awaysogs
	 */
	public SogList getAwaysogs() {
		return awaysogs;
	}

	/**
	 * @param awaysogs the awaysogs to set
	 */
	public void setAwaysogs(SogList awaysogs) {
		this.awaysogs = awaysogs;
	}

	/**
	 * @return the homesogs
	 */
	public SogList getHomesogs() {
		return homesogs;
	}

	/**
	 * @param homesogs the homesogs to set
	 */
	public void setHomesogs(SogList homesogs) {
		this.homesogs = homesogs;
	}

	/**
	 * @return the currentsog
	 */
	public Sog getCurrentsog() {
		return currentsog;
	}

	public void setCurrentsog(Sog curentsog) {
		this.currentsog = curentsog;
	}

	/**
	 * @return the sogshots1
	 */
	public int getSogshots1() {
		return sogshots1;
	}

	/**
	 * @param sogshots1 the sogshots1 to set
	 */
	public void setSogshots1(int sogshots1) {
		this.sogshots1 = sogshots1;
	}

	/**
	 * @return the sogshots2
	 */
	public int getSogshots2() {
		return sogshots2;
	}

	/**
	 * @param sogshots2 the sogshots2 to set
	 */
	public void setSogshots2(int sogshots2) {
		this.sogshots2 = sogshots2;
	}

	/**
	 * @return the sogshots3
	 */
	public int getSogshots3() {
		return sogshots3;
	}

	/**
	 * @param sogshots3 the sogshots3 to set
	 */
	public void setSogshots3(int sogshots3) {
		this.sogshots3 = sogshots3;
	}

	/**
	 * @return the sogshots4
	 */
	public int getSogshots4() {
		return sogshots4;
	}

	/**
	 * @param sogshots4 the sogshots4 to set
	 */
	public void setSogshots4(int sogshots4) {
		this.sogshots4 = sogshots4;
	}

	/**
	 * @return the sogshots5
	 */
	public int getSogshots5() {
		return sogshots5;
	}

	/**
	 * @param sogshots5 the sogshots5 to set
	 */
	public void setSogshots5(int sogshots5) {
		this.sogshots5 = sogshots5;
	}

	/**
	 * @return the sogshots6
	 */
	public int getSogshots6() {
		return sogshots6;
	}

	/**
	 * @param sogshots6 the sogshots6 to set
	 */
	public void setSogshots6(int sogshots6) {
		this.sogshots6 = sogshots6;
	}

	/**
	 * @return the sogshots7
	 */
	public int getSogshots7() {
		return sogshots7;
	}

	/**
	 * @param sogshots7 the sogshots7 to set
	 */
	public void setSogshots7(int sogshots7) {
		this.sogshots7 = sogshots7;
	}

	/**
	 * @return the sogshots8
	 */
	public int getSogshots8() {
		return sogshots8;
	}

	/**
	 * @param sogshots8 the sogshots8 to set
	 */
	public void setSogshots8(int sogshots8) {
		this.sogshots8 = sogshots8;
	}

	/**
	 * @return the sogshots9
	 */
	public int getSogshots9() {
		return sogshots9;
	}

	/**
	 * @param sogshots9 the sogshots9 to set
	 */
	public void setSogshots9(int sogshots9) {
		this.sogshots9 = sogshots9;
	}

	/**
	 * @return the venues
	 */
	public Map<String, String> getVenues() {
		return venues;
	}

	/**
	 * @param venues the venues to set
	 */
	public void setVenues(Map<String, String> venues) {
		this.venues = venues;
	}

	/**
	 * @return the htpick
	 */
	public Map<String, String> getHtpick() {
		return htpick;
	}

	/**
	 * @param htpick the htpick to set
	 */
	public void setHtpick(Map<String, String> htpick) {
		this.htpick = htpick;
	}

	/**
	 * @return the atpick
	 */
	public Map<String, String> getAtpick() {
		return atpick;
	}

	/**
	 * @param atpick the atpick to set
	 */
	public void setAtpick(Map<String, String> atpick) {
		this.atpick = atpick;
	}

	/**
	 * @return the typepick
	 */
	public Map<String, String> getTypepick() {
		return typepick;
	}

	/**
	 * @param typepick the typepick to set
	 */
	public void setTypepick(Map<String, String> typepick) {
		this.typepick = typepick;
	}

	/**
	 * @return the statepick
	 */
	public Map<String, String> getStatepick() {
		return statepick;
	}

	/**
	 * @param statepick the statepick to set
	 */
	public void setStatepick(Map<String, String> statepick) {
		this.statepick = statepick;
	}

	/**
	 * @return the editgame
	 */
	public boolean isEditgame() {
		return editgame;
	}

	/**
	 * @param editgame the editgame to set
	 */
	public void setEditgame(boolean editgame) {
		this.editgame = editgame;
	}

	/**
	 * @return the lgsheet
	 */
	public String getLgsheet() {
		return lgsheet;
	}

	/**
	 * @param lgsheet the lgsheet to set
	 */
	public void setLgsheet(String lgsheet) {
		this.lgsheet = lgsheet;
	}

	/**
	 * @return the lgvenue
	 */
	public String getLgvenue() {
		return lgvenue;
	}

	/**
	 * @param lgvenue the lgvenue to set
	 */
	public void setLgvenue(String lgvenue) {
		this.lgvenue = lgvenue;
	}

	/**
	 * @return the lgtype
	 */
	public String getLgtype() {
		return lgtype;
	}

	/**
	 * @param lgtype the lgtype to set
	 */
	public void setLgtype(String lgtype) {
		this.lgtype = lgtype;
	}

	/**
	 * @return the lgdate
	 */
	public String getLgdate() {
		return lgdate;
	}

	/**
	 * @param lgdate the lgdate to set
	 */
	public void setLgdate(String lgdate) {
		this.lgdate = lgdate;
	}

	/**
	 * @return the lgtime
	 */
	public String getLgtime() {
		return lgtime;
	}

	public void setLgtime(String lgtime) {
		this.lgtime = lgtime;
	}

	public String getLgstate() {
		return lgstate;
	}

	public void setLgstate(String lgstate) {
		this.lgstate = lgstate;
	}

	public String getLgvenueval() {
		return lgvenueval;
	}

	public void setLgvenueval(String lgvenueval) {
		this.lgvenueval = lgvenueval;
	}

	public String getLgtypeval() {
		return lgtypeval;
	}

	public void setLgtypeval(String lgtypeval) {
		this.lgtypeval = lgtypeval;
	}

	public String getLghteam() {
		return lghteam;
	}

	public void setLghteam(String lghteam) {
		this.lghteam = lghteam;
	}

	public String getLgateam() {
		return lgateam;
	}

	public void setLgateam(String lgateam) {
		this.lgateam = lgateam;
	}

	public String getLgstateval() {
		return lgstateval;
	}

	public void setLgstateval(String lgstateval) {
		this.lgstateval = lgstateval;
	}

	public void setGameStarted() {
		this.livegame.setAwayscore(getDerivedAwayScore());
		this.livegame.setHomescore(getDerivedHomeScore());
		this.livegame.setStatetag("InProgress");
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", "GsB.setGameStarted");
		try {
			this.livegame.update(db, false);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.free();
		}
	}

	public void setGameComplete() {
		this.livegame.setAwayscore(getDerivedAwayScore());
		this.livegame.setHomescore(getDerivedHomeScore());
		this.livegame.setStatetag("Complete");
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase","GsB.setGameComplete");
		try {
			this.livegame.update(db, false);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.free();
		}
	}
	
	public void setStatsComplete(Integer teamid) {
		if (this.teamid.equals(this.livegame.getHometeam().ID)){
			this.livegame.setStatetag("HomeStatsReview");
		}else {
			this.livegame.setStatetag("AwayStatsReview");
		}
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase","GsB.setStatsComplete");
		try {
			if (this.livegame.getStatetag().equals("HomeStatsReview")){
				this.livegame.updatTeamstatsstatus(db, "H");
				this.livegame.setHomestatsstatus("Complete");
				this.livegame.setHomescore(this.getUpdatedScore("home"));
				if (!(this.livegame.getAwaystatsstatus()==null)){
					if (this.livegame.getAwaystatsstatus().equals("Complete")){
						this.livegame.setStatetag("Complete");
						scaha.refreshLiveGameList();
					}
				}
			} else {
				this.livegame.updatTeamstatsstatus(db, "A");
				this.livegame.setAwaystatsstatus("Complete");
				this.livegame.setAwayscore(this.getUpdatedScore("away"));
				if (!(this.livegame.getHomestatsstatus()==null)){
					if (this.livegame.getHomestatsstatus().equals("Complete")){
						this.livegame.setStatetag("Complete");
						scaha.refreshLiveGameList();
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			db.free();
		}

		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("managerportal.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setGameFinal() {
		this.livegame.setStatetag("Final");
		pb.setLivegameeditreturn("reviewscahagames.xhtml");
		this.hometeamexceeds = false;
		this.awayteamexceeds = false;
		String hometeamtotalpims = "0";
		String awayteamtotalpims = "0";
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", "GsB.setGameFinal");
		try {
			LOGGER.info("updating game results:");
			this.livegame.updatGameFinalStatus(db);
			//need to update stats table a	s game is being finalized.
			//pass in team id and livegame id
			PreparedStatement ps = db.prepareCall("call scaha.updatestatsforLiveGame(?,?)");
			ps.setInt(1,this.livegame.ID);
			ps.setInt(2,this.livegame.getAwayteam().ID);
			LOGGER.info("updating stats for away team:");
			ps.executeQuery();
			ps.setInt(1,this.livegame.ID);
			ps.setInt(2,this.livegame.getHometeam().ID);
			LOGGER.info("updating stats for home team:");
			ps.executeQuery();
			//need to add a check for a teams total penalty minutes to send an email to deputy commissioner.
			ps = db.prepareCall("call scaha.doteamshaveextensivepenaltymins(?)");
			ps.setInt(1,this.livegame.ID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				this.hometeamexceeds = rs.getBoolean("hometeamexceeds");
				this.awayteamexceeds = rs.getBoolean("awayteamexceeds");
				hometeamtotalpims = rs.getString("hometeampims");
				awayteamtotalpims = rs.getString("awayteampims");
				this.todaysdate = rs.getString("todaysdate");
			}
			rs.close();
			ps.close();

			//first check home team to see if they exceeded pims
			if (this.hometeamexceeds){
				this.totalpims = hometeamtotalpims;
				this.pimsteamname = this.livegame.getHometeam().getTeamname() + this.livegame.getHometeam().getDivisiontag()+ this.livegame.getHometeam().getSkillleveltag();
				for (ScahaCoach i: this.livegame.getHometeam().getCoachs()){
					managerrows = managerrows + "<tr>";
					managerrows = managerrows + "<td>" + i.getsFirstName() + " " + i.getsLastName() + "</td>";
					managerrows = managerrows + "<td>" + i.getGenatt().get("ROSTERTYPE") + "</td>";
					managerrows = managerrows + "<td>" + i.getsEmail() + "</td>";
					managerrows = managerrows + "</tr>";
				}

				//now grab the top 3 penalty minute leaders
				ps = db.prepareCall("call scaha.getteamspenaltyminuteleaders(?)");
				ps.setInt(1,this.livegame.getHometeam().ID);
				rs = ps.executeQuery();
				while (rs.next()) {
					String playername = rs.getString("fname") + " " + rs.getString("lname");
					Integer pims = rs.getInt("pims");
					playerrows = playerrows + "<tr>";
					playerrows = playerrows + "<td>" + playername + "</td>";
					playerrows = playerrows + "<td>" + pims + "</td>";
					playerrows = playerrows + "</tr>";
				}
				rs.close();
				ps.close();
				//this.setSubject(this.pimsteamname + "Exceeded Penalty Minute Threshold");
				SendMailSSL mail = new SendMailSSL(this);
				LOGGER.info("Finished creating mail object for team penalty minutes");
				mail.sendMail();
			}
			//next check away team if penalty mins have been exceeded
			if (this.awayteamexceeds){
				this.totalpims = hometeamtotalpims;
				this.pimsteamname = this.livegame.getAwayteam().getTeamname() + this.livegame.getAwayteam().getDivisiontag()+ this.livegame.getAwayteam().getSkillleveltag();
				for (ScahaCoach i: this.livegame.getAwayteam().getCoachs()){
					managerrows = managerrows + "<tr>";
					managerrows = managerrows + "<td>" + i.getsFirstName() + " " + i.getsLastName() + "</td>";
					managerrows = managerrows + "<td>" + i.getGenatt().get("ROSTERTYPE") + "</td>";
					managerrows = managerrows + "<td>" + i.getsEmail() + "</td>";
					managerrows = managerrows + "</tr>";
				}
				//now grab the top 3 penalty minute leaders
				ps = db.prepareCall("call scaha.getteamspenaltyminuteleaders(?)");
				ps.setInt(1,this.livegame.getAwayteam().ID);
				rs = ps.executeQuery();
				while (rs.next()) {
					String playername = rs.getString("fname") + " " + rs.getString("lname");
					Integer pims = rs.getInt("pims");
					playerrows = playerrows + "<tr>";
					playerrows = playerrows + "<td>" + playername + "</td>";
					playerrows = playerrows + "<td>" + pims + "</td>";
					playerrows = playerrows + "</tr>";
				}
				rs.close();
				ps.close();
				//this.setSubject(this.pimsteamname + "Exceeded Penalty Minute Threshold");
				SendMailSSL mail = new SendMailSSL(this);
				LOGGER.info("Finished creating mail object for team penalty minutes");
				mail.sendMail();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}

		scaha.refreshLiveGameList();
		gamesheetClose();

		//This code isn't needed anymore.  Suspensions are emailed from a different page. leaving code just in case we need to return this functionality back
		// ok.. now we want to check all penalties from both sides..and report any game misconducts
		// or matches..
		//
		/*LOGGER.info("pushing penalties:");
		PenaltyPusher pp = new PenaltyPusher();
		String temppenaltyrows = "";
		this.setTeamid(this.livegame.getAwayteam().ID);
		for (Penalty p : this.refreshHomePenalty(false)) {
			
			if (p.getPenaltytype().equals("Game Misconduct") || 
				p.getPenaltytype().equals("Match Penalty")) {
				
				//need to add to the penalty row string so we send all suspensions in one email, not multiple emails
				temppenaltyrows = temppenaltyrows + "<tr><td>&nbsp;" + p.getRosterspot().getFname() + " " + p.getRosterspot().getLname() +"&nbsp;</td><td>&nbsp;" + p.getRosterspot().getJerseynumber() + "&nbsp;</td><td>&nbsp;" + p.getPenaltytype() + "&nbsp;</td></tr>";
				
				pp.setPenalty(p);
				pp.setLivegame(this.livegame);
				
			}
			
			
		}
		
		pp.setPenaltyrows(temppenaltyrows);
		pp.pushPenalty();
		
		//need to reset for away teams
		temppenaltyrows = "";
		this.setTeamid(this.livegame.getHometeam().ID);
		//need to check if we need to load the home roster
		if (this.getHometeamforpl()==null){
			this.setHometeamforpl(this.refreshHomeRosterforpl());
			this.setHometeam(this.refreshHomeRoster());
		}
		for (Penalty p : this.refreshHomePenalty(false)) {
			
			if (p.getPenaltytype().equals("Game Misconduct") || 
				p.getPenaltytype().equals("Match Penalty")) {
				
				//need to add to the penalty row string so we send all suspensions in one email, not multiple emails
				temppenaltyrows = temppenaltyrows + "<tr><td>&nbsp;" + p.getRosterspot().getFname() + " " + p.getRosterspot().getLname() +"&nbsp;</td><td>&nbsp;" + p.getRosterspot().getJerseynumber() + "&nbsp;</td><td>&nbsp;" + p.getPenaltytype() + "&nbsp;</td></tr>";
				
				pp.setPenalty(p);
				pp.setLivegame(this.livegame);
				
			}
		}
		
		pp.setPenaltyrows(temppenaltyrows);
		pp.pushPenalty();*/
		
		//need to reset

	}
	public void saveScheduleInfo() {
		
		//
		// Lets set up some changed display values..  temp solution until we save and refresh from object
		//
		//
		this.lgvenue = getStringKeyFromValue(this.venues,this.lgvenueval);
		this.lgtype =  getStringKeyFromValue(this.typepick, this.lgtypeval);
		this.lgstate = getStringKeyFromValue(this.statepick, this.lgstateval);
		this.lghteam = getStringKeyFromValue(this.htpick, this.lghteamval);
		this.lgateam = getStringKeyFromValue(this.atpick, this.lgateamval);
		
		LOGGER.info("Start Date: " + this.lgdate + ", orig value is " + this.livegame.getStartdate());
		LOGGER.info("Start Time: " + this.lgtime + ", orig value is " + this.livegame.getStarttime());
		LOGGER.info("Type: " + this.lgtypeval + ":" + this.lgtype + ", orig value is " + this.livegame.getTypetag());
		LOGGER.info("State: " + this.lgstateval + ":" + this.lgstate + ", orig value is " + this.livegame.getStatetag());
		LOGGER.info("Venue: " + this.lgvenueval + ":" + this.lgvenue + ", orig value is " + this.livegame.getVenuetag());
		LOGGER.info("Sheet: " + this.lgsheet +  ", orig value is " + this.livegame.getSheetname());
		LOGGER.info("Away Team + " + this.lgateamval + ":" + this.lgateam + ", orig value is " + this.livegame.getAwayteam().ID);
		LOGGER.info("Home Team + " + this.lghteamval + ":" + this.lghteam + ", orig value is " + this.livegame.getHometeam().ID);
		
		//
		// ok.. lets generate the e-mail.. so we can put prior information.. and current information..
		//
		LOGGER.info("HERE IS WHERE WE SAVE EVERYTHING COLLECTED FROM GameChange And Send Mail..");
		LOGGER.info("Sending Game Change mail here...");
		SendMailSSL mail = new SendMailSSL(this);
		mail.sendMail();
		
		//
		// we do not save score here..
		//
		//this.livegame.setAwayscore(getDerivedAwayScore());
		//this.livegame.setHomescore(getDerivedHomeScore());
		
		//
		// Lets check to see if home and away teams have been swapped
		//
		if (this.livegame.getHometeam().ID != Integer.parseInt(lghteamval)) {
			ScahaTeam tmp = this.livegame.getHometeam();
			this.livegame.setHometeam(this.livegame.getAwayteam());
			this.livegame.setAwayteam(tmp);
			this.livegame.setHometeamname(this.lghteam);
			this.livegame.setAwayteamname(this.lgateam);
		}
		
		this.livegame.setStartdate(this.lgdate);
		this.livegame.setStarttime(this.lgtime);
		this.livegame.setVenuetag(this.lgvenueval);
		this.livegame.setVenueshortname(this.lgvenue);
		this.livegame.setSheetname(this.lgsheet);
		this.livegame.setStatetag(this.lgstateval);
		this.livegame.setTypetag(this.lgtypeval);

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
				
		try {
			this.livegame.update(db, true);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		db.free();
		
		//
		// ok.. we should be all good here..
		//
		this.setDisplayValues();
		this.setEditgame(false);
		
	}
	
	public void cancelScheduleInfoChanges() {
		
		LOGGER.info(this.lgvenueval + ":" + this.lgvenue);
		//
		// reset the display values..
		//
		this.setDisplayValues();
		this.setEditgame(false);
		
	}

	/**
	 * @return the lgateamval
	 */
	public String getLgateamval() {
		return lgateamval;
	}

	/**
	 * @param lgateamval the lgateamval to set
	 */
	public void setLgateamval(String lgateamval) {
		this.lgateamval = lgateamval;
	}

	/**
	 * @return the lghteamval
	 */
	public String getLghteamval() {
		return lghteamval;
	}

	/**
	 * @param lghteamval the lghteamval to set
	 */
	public void setLghteamval(String lghteamval) {
		this.lghteamval = lghteamval;
	}
	
	 public  String getStringKeyFromValue(Map<String, String> hm, String value) {
	    for (String o : hm.keySet()) {
	      if (hm.get(o).equals(value)) {
	        return o;
	      }
	    }
	    return null;
	  }

	/**
	 * @return the sogplaytime
	 */
	public String getSogplaytime() {
		return sogplaytime;
	}

	/**
	 * @param sogplaytime the sogplaytime to set
	 */
	public void setSogplaytime(String sogplaytime) {
		this.sogplaytime = sogplaytime;
	}
	
    public void gamesheetClose(){

    	FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect(pb.getLivegameeditreturn());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void saveScore(){
    	
    	//lets update the livegame with the new scores, all other details should remain the same
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
		try {
			this.livegame.setStatetag("Final");
			this.livegame.update(db, true);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		db.free();
		
    
    	//ok now that we saved the score lets redirect back to the manager portal
    	FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect(pb.getLivegameeditreturn());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void setPenaltiesPickList(){
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
    	try {
			PreparedStatement ps = db.prepareCall("call scaha.getPenaltyList()");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String penalty = rs.getString("penaltyname");
				
				this.penalties.add(penalty);
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		
		
	
    	//need to add these to database
		 /*this.penalties.put("15 Team Penalties","15 Team Penalties");
		 this.penalties.put("2 Majors","2 Majors");
		 this.penalties.put("3rd Man in","3rd Man in");
		 this.penalties.put("5 Penalties","5 Penalties");
		 this.penalties.put("Abuse of Officials", "Abuse of Officials");
		 this.penalties.put("Attempt to injure","Attempt to injure");
		 this.penalties.put("Bench","Bench" );
		 this.penalties.put("Boarding","Boarding");
		 this.penalties.put("Body Checking","Body Checking");
		 this.penalties.put("Butt-Ending", "Butt-Ending");
		 this.penalties.put("Charging","Charging");
		 this.penalties.put("Hooking", "Hooking");
		 this.penalties.put("Checking from Behind", "Checking from Behind");
		 this.penalties.put("Cross-Checking", "Cross-Checking");
		 this.penalties.put("Delay of Game", "Delay of Game");
		 this.penalties.put("Elbowing", "Elbowing");
		 this.penalties.put("Fisticuffs/Fighting", "Fisticuffs/Fighting");
		 this.penalties.put("Game Misconduct", "Game Misconduct");
		 this.penalties.put("Head Contact", "Head Contact");
		 this.penalties.put("High-Sticking", "High-Sticking");
		 this.penalties.put("Holding","Holding");
		 this.penalties.put("Holding the Facemask","Holding the Facemask");
		 this.penalties.put("Hooking", "Hooking");
		 this.penalties.put("Illegal Equipment", "Illegal Equipment");
		 this.penalties.put("Interference", "Interference");
		 this.penalties.put("Kicking", "Kicking");
		 this.penalties.put("Kneeing", "Kneeing");
		 this.penalties.put("Major", "Major");
		 this.penalties.put("Match Penalty","Match Penalty");
		 this.penalties.put("Misconduct","Misconduct");
		 this.penalties.put("Mouthpiece","Mouthpiece");
		 this.penalties.put("Roughing","Roughing");
		 this.penalties.put("Slashing","Slashing");
		 this.penalties.put("Spearing","Spearing");
		 this.penalties.put("Too Many Men","Too Many Men");
		 this.penalties.put("Tripping","Tripping");
		 this.penalties.put("Unsportsmanlike","Unsportsmanlike");*/
    }
    
    public void setVenuesPickList(){
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
    	try {
			PreparedStatement ps = db.prepareCall("call scaha.getVenueList()");
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				this.venues.put(rs.getString("description"),rs.getString("tag"));
			}
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
   
		//make sure all of these are in the db
		/*this.venues.put("The Rinks - Yorba Linda ICE","YLICE");
		 this.venues.put("The Rinks - Anaheim ICE","AICE");
		 this.venues.put("The Rinks - Westminster ICE","WICE");
		 this.venues.put("The Rinks - Lakewood ICE","LAKEWOOD");
		 this.venues.put("Bakersfield Ice Sports Center","BAKERICE");
		 this.venues.put("Skating Edge Ice Center","BHSEIC");
		 this.venues.put("Iceoplex Simi Valley","SIMI");
		 this.venues.put("Valencia Ice Station","ICESTATION");
		 this.venues.put("Pickwick Ice Arena","PICKWICK");
		 this.venues.put("LA Kings Valley Ice Center","VALLEYICE");
		 this.venues.put("East West Ice Palace","EWICEP");
		 this.venues.put("Ontario Center Ice Arena","OCIA");
		 this.venues.put("Channel Islands Ice Center","CIIC");
		 this.venues.put("LA Kings Icetown Riverside","RIVICE");
		 this.venues.put("Desert Ice Castle","DICE");
		 this.venues.put("KHS Ice Arena","KHS");
		 this.venues.put("Toyota Sports Center","TSC");
		 this.venues.put("Lake Forest Ice Palace","LFIP");
		 this.venues.put("Ontario Ice Skating Center","ONTICE");
		 this.venues.put("Pasadena Skating Center","PISC");
		 this.venues.put("Iceoplex Escondido","ESICOPLEX");
		 this.venues.put("Kroc Center Ice Arena","KROC");
		 this.venues.put("San Diego Ice Arena","SDIA");
		 this.venues.put("Carlsbad  Ice Arena","CARLSBAD");*/
		 
    }
    
    public void loadRosterForEdit(String homeaway){
    	if (homeaway.equals("H")){
    		rb.setSelectedteam(this.livegame.getHometeam().ID);
        }else {
        	rb.setSelectedteam(this.livegame.getAwayteam().ID);
    	}
    	rb.onTeamChange();
    }
    
    public void reloadTeamRoster(String homeaway){
    	if (homeaway.equals("H")){
    		this.setHometeam(this.refreshHomeRoster());
    		this.setHomepenalties(this.refreshHomePenalty(false));
    		this.setHomescoring(this.refreshHomeScoring(false));
    		this.setHomesogs(this.refreshHomeSog(false));
    	}else {
    		this.setAwayteam(this.refreshAwayRoster());
    		this.setAwaypenalties(this.refreshAwayPenalty());
    		this.setAwayscoring(this.refreshAwayScoring());
    		this.setAwaysogs(this.refreshAwaySog());
    	}
    	
    }
 
    public void addGoalRows(){
    	this.setHomescoring(this.refreshHomeScoring(true));
    }
    
    
    public void addSogRows(){
    	this.setHomesogs(this.refreshHomeSog(true));
    }
    
    public void addPenRows(){
    	this.setHomepenalties(this.refreshHomePenalty(true));
    }
    
    
    public List<RosterForPrint> getRosterforPrint(String homeaway) {
 
    	Integer teamid = null;
    	if (homeaway.equals("H")){
    		teamid = this.livegame.getHometeam().ID;
    	} else {
    		teamid = this.livegame.getAwayteam().ID;
    	}
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<RosterForPrint> templist = new ArrayList<RosterForPrint>();
    	Integer rsetcount = 0;
    	
    	
    	try {
    	
	    	PreparedStatement ps = db.prepareStatement("call scaha.getRosterforPrintbyID(?,?)");
			
	    	
	    	
			ps.setInt(1,teamid);
			ps.setString(2, this.livegame.getStartdate().toString());
			ResultSet rs = ps.executeQuery();
			
			
			while (rs.next()) {
				int i = 1;
				rsetcount++;
				RosterForPrint rfp = new RosterForPrint();
				rfp.setJerseynumber(rs.getString("jerseynumber"));
				rfp.setLastname(rs.getString(i++));
				rfp.setFirstname(rs.getString(i++));
				rfp.setIsgoalie(rs.getBoolean("isgoalie"));
				rfp.setPlayernameislong(rs.getBoolean("playernameislong"));
				templist.add(rfp);
			}
		
			//this creates the blank rows up to 20 for roster label
			for (Integer x=rsetcount; x < 20; x++) {
				RosterForPrint rfp = new RosterForPrint();
				rfp.setJerseynumber(" ");
				rfp.setLastname(" ");
				rfp.setFirstname(" ");
				templist.add(rfp);
	        }
	        
			
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			db.free();
		}
    	db.free();
    	rfplist=templist;
    	
		return rfplist;
    }
    
    public List<RosterForPrint> getRosterofCoachesforPrint(String homeaway) {
    	 
    	Integer teamid = null;
    	if (homeaway.equals("H")){
    		teamid = this.livegame.getHometeam().ID;
    	} else {
    		teamid = this.livegame.getAwayteam().ID;
    	}
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<RosterForPrint> templist = new ArrayList<RosterForPrint>();
    	List<RosterForPrint> carryover = new ArrayList<RosterForPrint>();
    	Integer rowcount = 0;
    	
    	try {
    	
	    	PreparedStatement ps = db.prepareStatement("call scaha.getRosterofCoachesforPrintbyID(?,?,?)");
			
	    	
	    	
			ps.setInt(1,teamid);
			ps.setString(2, this.livegame.getStartdate().toString());
			ps.setInt(3, this.livegame.getIdgame());
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int i = 1;
				rowcount++;
				RosterForPrint rfp = new RosterForPrint();
				rfp.setJerseynumber(rs.getString("jerseynumber"));
				rfp.setLastname(rs.getString(i++));
				rfp.setFirstname(rs.getString(i++));
				rfp.setCepinfo(rs.getString("cepinfo"));
				templist.add(rfp);
				
				
			}
		
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			db.free();
		}
    	db.free();
    	rfplist=templist;
    	
    	
    	
    	if (homeaway.equals("H")){
    		rfplisthome=carryover;
    	} else {
    		rfplistaway=carryover;
    	}
    	
    	
    	return rfplist;
    }
    
    public void loadLivegame(Integer gameid){
 
    	LiveGameList lgl = scaha.getScahaLiveGameList();
    	
    	for (LiveGame lg : lgl) {
			if (lg.ID==livegameid){
				this.livegame = lg;
			}
    		
		}
	}
    
   public void viewHome(){
	   this.setTeamid(this.livegame.getHometeam().ID);
	   this.setHometeam(this.refreshHomeRoster());
	   this.setHometeamcoach(this.refreshHomeCoachRoster());
	   this.setHometeamforpl(this.refreshHomeRosterforpl());
	   this.scoringpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
	   this.setHomescoring(this.refreshHomeScoring(false));
	   this.sogpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
	   this.setHomesogs(this.refreshHomeSog(false));
	   this.penpicklist = (List<LiveGameRosterSpot>) this.getHometeamforpl().getWrappedData();
	   this.setHomepenalties(this.refreshHomePenalty(false));
	   this.setTeamclubid(this.livegame.getHomeclubid());
	   this.setNote(this.loadNote());
   }
   
   public void viewAway(){
	   this.setTeamid(this.livegame.getAwayteam().ID);
	   this.setHometeam(this.refreshAwayRoster());
	   this.setHometeamcoach(this.refreshAwayCoachRoster());
	   this.setHometeamforpl(this.refreshAwayRosterforpl());
	   this.scoringpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
	   this.setHomescoring(this.refreshHomeScoring(false));
	   this.sogpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
	   this.setHomesogs(this.refreshHomeSog(false));
	   this.penpicklist = (List<LiveGameRosterSpot>) this.getAwayteamforpl().getWrappedData();
	   this.setHomepenalties(this.refreshHomePenalty(false));
	   this.setTeamclubid(this.livegame.getAwayclubid());
	   this.setNote(this.loadNote());
   }
   
   
   public List<RosterForPrint> getRosterofPlayersfor8UPrint(Integer teamid, String gamedate) {
	   
   	
   	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
   	List<RosterForPrint> templist = new ArrayList<RosterForPrint>();
   	Integer rsetcount = 0;
   	
   	
   	try {
   	
	    	PreparedStatement ps = db.prepareStatement("CALL scaha.getRosterforPrintbyID(?,?)");
			
	    	ps.setInt(1,teamid);
			ps.setString(2, gamedate);
			ResultSet rs = ps.executeQuery();
			
			
			while (rs.next()) {
				int i = 1;
				
				if (teamid > 0){
					rsetcount++;
					RosterForPrint rfp = new RosterForPrint();
					rfp.setJerseynumber(rs.getString("jerseynumber"));
					rfp.setLastname(rs.getString(i++));
					rfp.setFirstname(rs.getString(i++));
					templist.add(rfp);
				}
			}
	
			//this creates the blank rows up to 20 for roster label
			for (Integer x=rsetcount; x < 16; x++) {
				RosterForPrint rfp = new RosterForPrint();
				rfp.setJerseynumber(" ");
				rfp.setLastname(" ");
				rfp.setFirstname(" ");
				templist.add(rfp);
	        }
	        
			
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			db.free();
		}
   	db.free();
   	rfplist=templist;
   	
	return rfplist;
   }
   
   public List<RosterForPrint> getRosterofCoachesfor8UPrint(Integer teamid, String gamedate) {
  	 
   	
   	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
   	List<RosterForPrint> templist = new ArrayList<RosterForPrint>();
   	
   	try {
   	
	    	PreparedStatement ps = db.prepareStatement("call scaha.getRosterofCoachesforPrintbyIDfor8u(?,?)");
			
	    	
	    	
			ps.setInt(1,teamid);
			ps.setString(2, gamedate);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int i = 1;
				RosterForPrint rfp = new RosterForPrint();
				rfp.setJerseynumber(rs.getString("jerseynumber"));
				rfp.setLastname(rs.getString(i++));
				rfp.setFirstname(rs.getString(i++));
				rfp.setCepinfo(rs.getString("cepinfo"));
				templist.add(rfp);
			}
		
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			db.free();
		}
   	db.free();
   	rfplist=templist;
   	
		return rfplist;
   }
   
   public List<RosterForPrint> getSecondPageofCoaches(String homeaway){
	
	   List<RosterForPrint> templist = new ArrayList<RosterForPrint>();
	   
	   if (homeaway.equals("H")){
		   templist = rfplisthome;
	   }else {
		   templist = rfplistaway;
	   }
	   
	   return templist;
	   
   }
   
   public List<RosterForPrint> getRosterofCoachesforSelection() {
  	 
   	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
   	List<RosterForPrint> templist = new ArrayList<RosterForPrint>();
   	
   	Integer rowcount = 0;
   	
   	try {
   	
	    	PreparedStatement ps = db.prepareStatement("call scaha.getRosterofCoachesforPrintbyID(?,?,?)");
			
	    	ps.setInt(1,this.teamid);
			ps.setString(2, this.livegame.getStartdate().toString());
			ps.setInt(3, 0);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int i = 1;
				rowcount++;
				RosterForPrint rfp = new RosterForPrint();
				rfp.setJerseynumber(rs.getString("jerseynumber"));
				rfp.setLastname(rs.getString(i++));
				rfp.setFirstname(rs.getString(i++));
				rfp.setCepinfo(rs.getString("cepinfo"));
				rfp.setRosterid(rs.getInt("idroster"));
				rfp.setLgpid(rs.getBoolean("lgpid"));
				templist.add(rfp);
			}
		
			rs.close();
			ps.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			db.free();
		}
   	db.free();
   	rfplist=templist;
   	
   	//FacesContext.getCurrentInstance().addMessage("form:messages", new FacesMessage(FacesMessage.SEVERITY_INFO,"Select upto 4 coaches to be printed on the scoresheet for this game.",""));
   	
   	return rfplist;
   }
   
   public void setCoachforPrint(RosterForPrint rfp, Boolean thisaction){
	
	   //need to set this rfp to the action coming into the routine simulating a checkbox
	   rfp.setLgpid(thisaction);
	   //lets first check if less than 4 coaches have been selected.  if so we can add this coach, if not we display a message
	   //that says they need to remove a coach to add this coach.
	   Integer x = 0;
	   Boolean canadd = false;
	   for (RosterForPrint rf : rfplist) {
			if (rf.getLgpid()){
				x++;
			}
   		
			
		}
	   
	   if (x<=4){
		   canadd = true;
	   }
	   
	   if (canadd){
		   ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			try {
				CallableStatement pc = db.prepareCall("call scaha.addlivegamecoachesforprint(?,?,?,?)");
				pc.setInt(1,0);
				pc.setInt(2,this.livegame.ID);
				pc.setInt(3, this.teamid);
				pc.setInt(4, rfp.getRosterid());
				pc.executeUpdate();
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			 	
			db.free();
	   } else {
		   //FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(FacesMessage.SEVERITY_INFO,"",""));
		   FacesMessages.error("You can only have a maximum of 4 coaches for the game.");
		   rfp.setLgpid(false);
		   //getRosterofCoachesforSelection();
	   }
		//spot.setMia(!spot.isMia());   
	   
   }
   
   public Integer getUpdatedScore(String homeaway){
	   
	   	Integer goalcount = 0;
	   
	   	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
	   	
	   	
	   	try {
	   	
		    	PreparedStatement ps = db.prepareStatement("call scaha.getTeamScore(?,?)");
				
		    	ps.setInt(1,this.livegame.getGameId());
				ps.setString(2, homeaway);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					goalcount = rs.getInt(1);
				}
			
				rs.close();
				ps.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				db.free();
			}
	   	db.free();

	   	return goalcount;
	   
   }
   
   public void statsSavegoals() {
	
	   ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
	   
	   for (Scoring rf : this.homescoring) {
			
		   rf.setTimescored("00:" + rf.getGoalmin() + ":" + rf.getGoalsec());
		   if (!(rf.getGoalmin().equals("00") && rf.getGoalsec().equals("00")) || !rf.getGoaltype().equals("0") || rf.getIdrostera1()!=0 && 
					rf.getIdrostera2()!=0 || rf.getIdrostergoal()!=0 || rf.getPeriod()!=0){
				try{
					rf.update(db);
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
  		}
	   
	 //now lets do goalies
	   for (Sog sog : this.homesogs){
		   	
		   if (!(sog.getPlaymins().equals("00") && sog.getPlaysecs().equals("00")) || sog.getShots1()!=0 || sog.getShots2()!=0
				   || sog.getShots3()!=0 || sog.getShots4()!=0 || sog.getIdroster()!=0){
		   
			   sog.setPlaytime(("00:" + sog.getPlaymins() + ":" + sog.getPlaysecs()));
			   
			   sog.setShots5(this.getSogshots5());
				sog.setShots6(this.getSogshots6());
				sog.setShots7(this.getSogshots7());
				sog.setShots8(this.getSogshots8());
				sog.setShots9(this.getSogshots9());
				try {
					sog.update(db);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   }
			
		   
		   
	   }
	   	
	   //this.setHomesogs(this.refreshHomeSog(false));
	   //this.setAwaysogs(this.refreshAwaySog());
		
	   
	   
	   	//finally lets do the penalties
	   for (Penalty pen : this.homepenalties){
		   
		   if (!(pen.getPensec().equals("00") && pen.getPenmin().equals("00")) || !pen.getPenaltytype().equals("")
				   || pen.getIdroster()!=0 || pen.getPeriod()!=0 || !pen.getMinutes().equals("")){
			   
		   
			   pen.setTimeofpenalty(("00:" + pen.getPenmin() + ":" + pen.getPensec()));
				
				LOGGER.info("updating score for " + pen);
				
				try {
					pen.update(db);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   }
			
			//this.setAwaypenalties(this.refreshAwayPenalty());
			//this.setHomepenalties(this.refreshHomePenalty(false));
			
	   }
	
	   
	   
	   db.free();
	   
   }
   
   public void statsSaveAll() {
		
	   ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
	   //first lets do goals
	   for (Scoring rf : this.homescoring) {
			
		   rf.setTimescored("00:" + rf.getGoalmin() + ":" + rf.getGoalsec());
		   if (!(rf.getGoalmin().equals("00") && rf.getGoalsec().equals("00")) || !rf.getGoaltype().equals("0") || rf.getIdrostera1()!=0 && 
					rf.getIdrostera2()!=0 || rf.getIdrostergoal()!=0 || rf.getPeriod()!=0){
				try{
					rf.update(db);
					
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			}
  		
	   		
		}
	   
	 //now lets do goalies
	   for (Sog sog : this.homesogs){
		   	
		   if (!(sog.getPlaymins().equals("00") && sog.getPlaysecs().equals("00")) || sog.getShots1()!=0 || sog.getShots2()!=0
				   || sog.getShots3()!=0 || sog.getShots4()!=0 || sog.getIdroster()!=0){
		   
			   sog.setPlaytime(("00:" + sog.getPlaymins() + ":" + sog.getPlaysecs()));
			   
			   sog.setShots5(this.getSogshots5());
				sog.setShots6(this.getSogshots6());
				sog.setShots7(this.getSogshots7());
				sog.setShots8(this.getSogshots8());
				sog.setShots9(this.getSogshots9());
				try {
					sog.update(db);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   }
			
		   
		   
	   }
	   	
	   //this.setHomesogs(this.refreshHomeSog(false));
	   //this.setAwaysogs(this.refreshAwaySog());
		
	   
	   
	   	//finally lets do the penalties
	   for (Penalty pen : this.homepenalties){
		   
		   if (!(pen.getPensec().equals("00") && pen.getPenmin().equals("00")) || !pen.getPenaltytype().equals("")
				   || pen.getIdroster()!=0 || pen.getPeriod()!=0 || !pen.getMinutes().equals("0")){
			   
		   
			   pen.setTimeofpenalty(("00:" + pen.getPenmin() + ":" + pen.getPensec()));
				
				LOGGER.info("updating score for " + pen);
				
				try {
					pen.update(db);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		   }
			
			//this.setAwaypenalties(this.refreshAwayPenalty());
			//this.setHomepenalties(this.refreshHomePenalty(false));
			
	   }
	   
	   
	   db.free();
	   
   }
   
   public void changeJerseynumber(LiveGameRosterSpot spot){
	 //need to set and execute db call here
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
   	
	   	try{
	   		//first get team name
	   		CallableStatement cs = db.prepareCall("CALL scaha.updateJerseyNumber(?,?)");
				cs.setInt("rosterid", spot.getIdRoster());
				cs.setString("newjerseynumber", spot.getJerseynumber());
			    cs.executeQuery();
				db.commit();
			    db.cleanup();
	   		
	   		LOGGER.info("We have updated the jersey number rosterid:");
				
	   		
	   	} catch (SQLException e) {
	   		// TODO Auto-generated catch block
	   		LOGGER.info("ERROR IN updating jersey number");
	   		e.printStackTrace();
	   		db.rollback();
	   	} finally {
	   		//
	   		// always clean up after yourself..
	   		//
	   		db.free();
	   	}
		
		
   }
   
   public void SetasDefault(){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			
				//Need to provide info to the stored procedure to save or update
				LOGGER.info("get flags for alerts");
				CallableStatement cs = db.prepareCall("CALL scaha.SetCoachesAsDefault(?,?)");
				cs.setInt("teamid",this.teamid);
				cs.setInt("livegameid",this.livegameid);
   		    cs.executeQuery();
   		    db.commit();
   			
   			
   		    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN setting default coaches");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
	    
	}

	public void saveCoach(LiveGameRosterSpot spot) {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", "GsB.saveCoach");
		try {
			CallableStatement pc = db.prepareCall("call scaha.saveLivegameCoachRosterSpot(?,?,?,?)");
			pc.setInt(1, spot.ID);
			pc.setInt(2, (spot.getIdRoster()));
			pc.setInt(3, (spot.getLivegame().getIdgame()));
			pc.setInt(4, (spot.getRank()));
			pc.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
		refreshHomeCoachRoster();
		refreshAwayCoachRoster();
	}

	public void saveNote() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase","GsB.saveNote");
		try {
			CallableStatement cs = db.prepareCall("CALL scaha.saveManagerNote(?,?,?)");
			cs.setInt("livegameid",this.getLivegame().getGameId());
			cs.setInt("teamid",this.teamid);
			cs.setString("note",this.getNote());
			cs.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
	}

	public String loadNote(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase","GsB.saveNote");
		String finalnote = "";
		try {
			CallableStatement cs = db.prepareCall("CALL scaha.getManagerNote(?,?)");
			cs.setInt("livegameid",this.getLivegame().getGameId());
			cs.setInt("teamid",this.teamid);
			ResultSet rs = cs.executeQuery();
			while (rs.next()) {
				finalnote = rs.getString("gamenotes");
			}
			rs.close();
			cs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
		return finalnote;
	}

	public void refreshLists(){
		this.refreshBean();
	}

	public void reFinalGames() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", "GsB.reFinalGames");
		try {
			LiveGameList templist = this.scaha.getScahaLiveGameList();
			for (LiveGame lg : templist) {
				if (lg.getStatetag().equals("Final") && lg.getScheduleidstub() > 695) {
					//need to update stats table a	s game is being finalized.
					//pass in team id and livegame id
					PreparedStatement ps = db.prepareCall("call scaha.updatestatsforLiveGame(?,?)");
					ps.setInt(1, lg.ID);
					ps.setInt(2, lg.getAwayteam().ID);
					LOGGER.info("updating stats for away team:");
					ps.executeQuery();
					ps.setInt(1, lg.ID);
					ps.setInt(2, lg.getHometeam().ID);
					LOGGER.info("updating stats for home team:");
					ps.executeQuery();
					ps.close();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
	}

	public void refreshMites() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", "GsB.refreshMites");
		try {
			PreparedStatement ps = db.prepareCall("call scaha.getMiteEventTeams(?)");
			ps.setInt(1, this.livegame.ID);
			ResultSet rs = ps.executeQuery();
			Integer count = 0;

			while (rs.next()) {
				if (count.equals(0)) {
					this.miteteamid1 = rs.getInt("idteam");
					this.teamclubid1 = rs.getInt("idclub");
				} else if (count.equals(1)) {
					this.miteteamid2 = rs.getInt("idteam");
					this.teamclubid2 = rs.getInt("idclub");
				} else if (count.equals(2)) {
					this.miteteamid3 = rs.getInt("idteam");
					this.teamclubid3 = rs.getInt("idclub");
				} else if (count.equals(3)) {
					this.miteteamid4 = rs.getInt("idteam");
					this.teamclubid4 = rs.getInt("idclub");
				}
				count++;
			}
			rs.close();
			ps.close();

			this.team1 = LiveGameRosterSpotList.NewListFactoryByTeamid(pb.getProfile(), db, miteteamid1);
			this.team2 = LiveGameRosterSpotList.NewListFactoryByTeamid(pb.getProfile(), db, miteteamid2);
			this.team3 = LiveGameRosterSpotList.NewListFactoryByTeamid(pb.getProfile(), db, miteteamid3);
			this.team4 = LiveGameRosterSpotList.NewListFactoryByTeamid(pb.getProfile(), db, miteteamid4);

			//next load the roster lists for the 4 teams.
			this.team1 = LiveGameRosterSpotList.NewListFactoryByTeamid(pb.getProfile(), db, miteteamid1);
			this.team2 = LiveGameRosterSpotList.NewListFactoryByTeamid(pb.getProfile(), db, miteteamid2);
			this.team3 = LiveGameRosterSpotList.NewListFactoryByTeamid(pb.getProfile(), db, miteteamid3);
			this.team4 = LiveGameRosterSpotList.NewListFactoryByTeamid(pb.getProfile(), db, miteteamid4);

			/*this.team1coach = LiveGameRosterSpotList.NewListFactoryCoachForTeam(pb.getProfile(),db,miteteamid1);
			this.team2coach = LiveGameRosterSpotList.NewListFactoryCoachForTeam(pb.getProfile(),db,miteteamid2);
			this.team3coach = LiveGameRosterSpotList.NewListFactoryCoachForTeam(pb.getProfile(),db,miteteamid3);
			this.team4coach = LiveGameRosterSpotList.NewListFactoryCoachForTeam(pb.getProfile(),db,miteteamid4);
			*/
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
	}
}

