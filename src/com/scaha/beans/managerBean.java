package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.*;
import com.sun.org.apache.xpath.internal.operations.Bool;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class managerBean implements Serializable, MailableObject {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	@ManagedProperty(value="#{scoreboardBean}")
	private ScoreboardBean sb;
	

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<RosterEdit> players = null;
	private List<RosterEdit> coaches = null;
	private List<TempGame> games = null;
	private List<TournamentGame> tournamentgames = null;
	private List<Tournament> tournaments = null;
	private List<ExhibitionGame> exhibitiongames = null;
	private List<Alert> displyalerts = null;
	private List<Help> helptopics = null;
    
	//bean level properties used by multiple methods
	private Integer teamid = null;
	private List<Team> managerteams = null;
	private String teamname = null;
	private Integer idclub = null;
	private Integer profileid = 0;
	private Boolean ishighschool = null;
	private String currentpimcount = "0";
	private String maxpimcount = "0";
	private Boolean ismite = false;
	
	//datamodels for all of the lists on the page
	private TempGameDataModel TempGameDataModel = null;
    private TournamentDataModel TournamentDataModel = null;
    private TournamentGameDataModel TournamentGameDataModel = null;
    private ExhibitionGameDataModel ExhibitionGameDataModel = null;
    private RosterEditDataModel RosterEditDataModel = null;
    private RosterEditDataModel RosterCoachDataModel = null;
    private AlertDataModel AlertsDataModel = null;
    
    //properties for storing the selected row of each of the datatables
    private RosterEdit selectedplayer = null;
    private TempGame selectedgame = null;
	private TournamentGame selectedtournamentgame = null;
	private ExhibitionGame selectedexhibitiongame = null;
	private Tournament selectedtournament = null;
	private String selectedtournamentforgame = null;
	
	//properties for adding tournaments
	private String tournamentname;
	private String startdate;
	private String enddate;
	private String contact;
	private String phone;
	private String sanction;
	private String location;
	private String website;
	private String levelplayed;
	private String status;
	
	//properties for adding tournament/exhibition games
	private Integer gametype=null;
	private String gamedate=null;
	private String gametime=null;
	private String opponent=null;
	private String tourneygamelocation=null;
	private String exhibitiongamelocation=null;
	private Boolean displaymultiple = null;
	private String playoffeligible = null;
	
	//properties for emailing to managers, scaha statistician
	private String to = null;
	private String subject = null;
	private String cc = null;
	private static String mail_tournament_body = Utils.getMailTemplateFromFile("/mail/tournament.html");
	private static String mail_tournamentgame_body = Utils.getMailTemplateFromFile("/mail/tournamentgame.html");
	private static String mail_exhibitiongame_body = Utils.getMailTemplateFromFile("/mail/exhibitiongame.html");
	private String todaysdate = null;
	private Boolean addingtournament = null;
	private Boolean addingtournamentgame = null;
	private Boolean addingexhibitiongame = null;
	private String currentseason = null;
		
	//miscellaneous variables
	private Boolean displaymessage = null;
	private String from = null;

	//setting default coaches
	private Integer headcoach = null;
	private Integer	assistantcoach1 = null;
	private Integer	assistantcoach2 = null;
	private Integer	assistantcoach3 = null;
	private List<LiveGameRosterSpot> penpicklist = null;

	//boolean for displaying tournament exhibition game area
	private Boolean isaaoraaa = null;
	private String NumberofTournaments = null;


	@PostConstruct
    public void init() {
		games = new ArrayList<TempGame>();  
        TempGameDataModel = new TempGameDataModel(games);
        
        idclub = 1;  
        
        this.setProfid(pb.getProfile().ID);
        this.setPb(pb);
        getClubID();
        //isClubHighSchool();
    	setTodaysDate();
    	setAddingflags();
    	this.setManagerteams(pb.getProfile().getManagerteams());
    	
    	//lets see if we have a team id passed for the case when multiple teams are managed by same person.
    	//if not we use the team associated with the manager
    	HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("id") != null)
        {
    		this.teamid = Integer.parseInt(hsr.getParameter("id").toString());
        } else {
        	this.teamid = pb.getProfile().getManagerteamid();
        }
		if(hsr.getParameter("from") != null)
		{
			this.from =hsr.getParameter("from");
		} else {
			this.from = "managerportal";
		}

    	
        
        //do we display multiple option or single option.
        this.setDisplaymultiple(false);
        isMultipleTeamManager();
        
        //need to get the current season for display
        this.setCurrentseason(pb.getCurrentSCAHAHockeySeason());
        
        //Load team roster
        getRoster();
        
        //Load SCAHA Games
        loadScahaGames();
        
        //Load Tournament and Games
        getTournament();
        getTournamentGames();
        
        //Load Exhibition Games
        getExhibitionGames();

        //let's load the default coaches for the team
		getDefaultCoaches();

        //now lets highlight the areas needing action.
        setAlerts();

        //need to load pick lists for default coach selection
		this.penpicklist = (List<LiveGameRosterSpot>) this.refreshCoachRosterforpl().getWrappedData();
		//need to load selected game
		if(hsr.getParameter("gameid") != null)
		{
			Integer gameid = Integer.parseInt(hsr.getParameter("gameid").toString());
			this.selectedgame = this.TempGameDataModel.getRowDataByInteger(gameid);
			getDefaultCoachesForGame();
		} else {
			this.selectedgame = null;
		}

		//now lets check if the manager has added any tournament info if not let's redirect them to the tournament page.
		checkTournaments();
		checkForAAorAAA();
	}
	
    public managerBean() {  
        
    }

	public String getNumberofTournaments() {
		return NumberofTournaments;
	}

	public void setNumberofTournaments(String numberofTournaments) {
		NumberofTournaments = numberofTournaments;
	}

	public Boolean getIsaaoraaa() {
		return isaaoraaa;
	}

	public void setIsaaoraaa(Boolean isaaoraaa) {
		this.isaaoraaa = isaaoraaa;
	}

	public AlertDataModel getAlertsDataModel() {
		return AlertsDataModel;
	}

	public void setAlertsDataModel(AlertDataModel alertsDataModel) {
		AlertsDataModel = alertsDataModel;
	}

	public List<Help> getHelptopics(){
		return helptopics;
	}

	public void setHelptopics(List<Help> list){
		helptopics=list;
	}

	public List<Alert> getDisplyalerts(){
		return displyalerts;
	}
	public void setDisplyalerts(List<Alert> list){
		displyalerts=list;
	}
	public List<LiveGameRosterSpot> getPenpicklist() {
		return penpicklist;
	}
	public void setPenpicklist(List<LiveGameRosterSpot> penpicklist) {
		this.penpicklist = penpicklist;
	}
	public Integer getHeadcoach(){
		return this.headcoach;
	}
	public void setHeadcoach(Integer coach){this.headcoach=coach;}
	public Integer getAssistantcoach1(){
		return this.assistantcoach1;
	}
	public void setAssistantcoach1(Integer coach){this.assistantcoach1=coach;}
	public Integer getAssistantcoach2(){
		return this.assistantcoach2;
	}
	public void setAssistantcoach2(Integer coach){this.assistantcoach2=coach;}
	public Integer getAssistantcoach3(){
		return this.assistantcoach3;
	}
	public void setAssistantcoach3(Integer coach){this.assistantcoach3=coach;}

	public void setGametype(Integer value){
    	this.gametype = value;
    }
    
    public Integer getGametype(){
    	return this.gametype;
    }

	public void setFrom(String value){
		this.from = value;
	}

	public String getFrom(){return this.from;}

	public void setIsmite(Boolean value){
		this.ismite = value;
	}

	public Boolean getIsmite(){
		return this.ismite;
	}


	public void setStatus(String value){
    	this.status = value;
    }
    
    public String getStatus(){
    	return this.status;
    }

	public void setOpponent(String value){
		this.opponent = value;
	}

	public String getOpponent(){
		return this.opponent;
	}

	public void setPlayoffeligible(String value){
		this.playoffeligible = value;
	}

	public String getPlayoffeligible(){
		return this.playoffeligible;
	}

	public void setDisplaymessage(Boolean value){
    	this.displaymessage = value;
    }
    
    public Boolean getDisplaymessage(){
    	return this.displaymessage;
    }
    
    
    public void setDisplaymultiple(Boolean value){
    	this.displaymultiple = value;
    }
    
    public Boolean getDisplaymultiple(){
    	return this.displaymultiple;
    }
    
    public void setManagerteams(List<Team> value){
    	this.managerteams = value;
    }
    
    public List<Team> getManagerteams(){
    	return this.managerteams;
    }
    
    public String getCurrentpimcount(){
    	return this.currentpimcount;
    }
    
    public void setCurrentpimcount(String value){
    	currentpimcount=value;
    }
    
    public String getMaxpimcount(){
    	return this.maxpimcount;
    }
    
    public void setMaxpimcount(String value){
    	maxpimcount=value;
    }
    
    public Boolean getAddingtournament(){
    	return addingtournament;
    }
    
    public void setAddingtournament(Boolean in){
    	addingtournament=in;
    }
    
    public Boolean getAddingtournamentgame(){
    	return addingtournamentgame;
    }
    
    public void setAddingtournamentgame(Boolean in){
    	addingtournamentgame=in;
    }
    
    public Boolean getAddingexhibitiongame(){
    	return addingexhibitiongame;
    }
    
    public void setAddingexhibitiongame(Boolean in){
    	addingexhibitiongame=in;
    }
    
    
    public String getTodaysdate(){
    	return todaysdate;
    }
    
    public void setTodaysdate(String tdate){
    	todaysdate=tdate;
    }
    
    public String getSubject() {
		// TODO Auto-generated method stub
		return subject;
	}
    
    public void setSubject(String ssubject){
    	subject = ssubject;
    }
    
    public String getTextBody() {
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		String result = "";
		if (this.addingtournament){
			myTokens.add("REQUESTINGTEAM: " + this.teamname);
			myTokens.add("REQUESTDATE: " + this.todaysdate);
			myTokens.add("TOURNAMENTNAME: " + this.tournamentname);
			myTokens.add("LEVELPLAYED: " + this.levelplayed);
			myTokens.add("STARTDATE: " + this.startdate);
			myTokens.add("ENDDATE: " + this.enddate);
			myTokens.add("CONTACT: " + this.contact);
			myTokens.add("PHONE: " + this.phone);
			myTokens.add("SANCTION: " + this.sanction);
			myTokens.add("LOCATION: " + this.location);
			myTokens.add("WEBSITE: " + this.website);
			myTokens.add("STATUS: " + this.status);
			
			result = Utils.mergeTokens(managerBean.mail_tournament_body,myTokens);
		}
		
		if (this.addingtournamentgame){
			myTokens.add("REQUESTINGTEAM: " + this.teamname);
			myTokens.add("REQUESTDATE: " + this.todaysdate);
			if (this.gametype.equals("2")){
				myTokens.add("GAMETYPE:Exhibition");
			}else{
				myTokens.add("GAMETYPE:Tournament");
			}
			myTokens.add("GAMEDATE: " + this.gamedate);
			myTokens.add("GAMETIME: " + this.gametime);
			myTokens.add("OPPONENT: " + this.opponent);
			myTokens.add("LOCATION: " + this.tourneygamelocation);
			myTokens.add("STATUS: " + "Pending");
			
			result = Utils.mergeTokens(managerBean.mail_tournamentgame_body,myTokens);
		}
		
		if (this.addingexhibitiongame){
			myTokens.add("REQUESTINGTEAM: " + this.teamname);
			myTokens.add("REQUESTDATE: " + this.todaysdate);
			myTokens.add("GAMEDATE: " + this.gamedate);
			myTokens.add("GAMETIME: " + this.gametime);
			myTokens.add("OPPONENT: " + this.opponent);
			myTokens.add("LOCATION: " + this.exhibitiongamelocation);
			myTokens.add("STATUS: " + "Pending");
			
			result = Utils.mergeTokens(managerBean.mail_exhibitiongame_body,myTokens);
		}
		
		return result;
		
	}
	
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return cc;
	}
	
	public void setPreApprovedCC(String scc){
		cc = scc;
	}
	
	public String getCurrenseason() {
		// TODO Auto-generated method stub
		return currentseason;
	}
	
	public void setCurrentseason(String scc){
		currentseason = scc;
	}
	
	
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return to;
	}
    
    public void setToMailAddress(String sto){
    	to = sto;
    }
    
    public String getGamedate(){
    	return gamedate;
    }
    
    public void setGamedate(String gdate){
    	gamedate=gdate;
    }
    
    public String getGametime(){
    	return gametime;
    }
    
    public void setGametime(String gdate){
    	gametime=gdate;
    }

    public String getLevelplayed(){
    	return levelplayed;
    }
    
    public void setLevelplayed(String name){
    	levelplayed=name;
    }
    
    public String getWebsite(){
    	return website;
    }
    
    public void setWebsite(String name){
    	website=name;
    }
    
    
    public String getLocation(){
    	return this.location;
    }
    
    public void setLocation(String name){
    	this.location=name;
    }
    
    public String getTourneygamelocation(){
    	return this.tourneygamelocation;
    }
    
    public void setTourneygamelocation(String name){
    	this.tourneygamelocation=name;
    }
    
    public String getExhibitiongamelocation(){
    	return this.exhibitiongamelocation;
    }
    
    public void setExhibitiongamelocation(String name){
    	this.exhibitiongamelocation=name;
    }
    
    public String getSanction(){
    	return sanction;
    }
    
    public void setSanction(String name){
    	sanction=name;
    }
    
    
    public String getPhone(){
    	return phone;
    }
    
    public void setPhone(String name){
    	phone=name;
    }
    
    
    public String getContact(){
    	return contact;
    }
    
    public void setContact(String name){
    	contact=name;
    }
    
    
    public String getEnddate(){
    	return enddate;
    }
    
    public void setEnddate(String name){
    	enddate=name;
    }
    
    
    public String getStartdate(){
    	return startdate;
    }
    
    public void setStartdate(String name){
    	startdate=name;
    }
    
    
    public String getTournamentname(){
    	return tournamentname;
    }
    
    public void setTournamentname(String name){
    	tournamentname=name;
    }
    
    public RosterEdit getSelectedplayer(){
    	return selectedplayer;
    }
    
    public void setSelectedplayer(RosterEdit name){
    	selectedplayer=name;
    }
    
    
    public String getTeamname(){
    	return teamname;
    }
    
    public void setTeamname(String name){
    	teamname=name;
    }
    
    
    public Integer getTeamid(){
    	return teamid;
    }
    
    public void setTeamid(Integer id){
    	teamid=id;
    }
    
    public Integer getIdclub(){
    	return idclub;
    }
    
    public void setIdclub(Integer id){
    	idclub=id;
    }
    
    public Boolean getIshighschool(){
    	return ishighschool;
    }
    
    public void setIshighschool(Boolean value){
    	ishighschool = value;
    }
    
    
    public Integer getProfid(){
    	return profileid;
    }	
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    
    
    public TempGame getSelectedgame(){
		return selectedgame;
	}
	
	public void setSelectedgame(TempGame selectedGame){
		selectedgame = selectedGame;
	}
    
	public TournamentGame getSelectedtournamentgame(){
		return selectedtournamentgame;
	}
	
	public void setSelectedtournamentgame(TournamentGame selectedGame){
		selectedtournamentgame = selectedGame;
	}
	
	public ExhibitionGame getSelectedexhibitiongame(){
		return selectedexhibitiongame;
	}
	
	public void setSelectedexhibitiongame(ExhibitionGame selectedGame){
		selectedexhibitiongame = selectedGame;
	}
    
	public Tournament getSelectedtournament(){
		return selectedtournament;
	}
	
	public void setSelectedtournament(Tournament selectedGame){
		selectedtournament = selectedGame;
	}
	
	public String getSelectedtournamentforgame(){
		return selectedtournamentforgame;
	}
	
	public void setSelectedtournamentforgame(String selected){
		selectedtournamentforgame = selected;
	}
	
	public List<TempGame> getGames(){
		return games;
	}
	
	public void setGames(List<TempGame> list){
		games = list;
	}
	
	    
    //retrieves list of existing teams for the club
    public void loadScahaGames(){
    	List<TempGame> tempresult = new ArrayList<TempGame>();
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getSCAHAGamesForHomeTeam(?)");
			cs.setInt("teamid", this.teamid);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String idgame = rs.getString("idlivegame");
    				String hometeam = rs.getString("hometeam");
    				String awayteam = rs.getString("awayteam");
    				String homescore = rs.getString("homescore");
    				String awayscore = rs.getString("awayscore");
    				String dates = rs.getString("date");
    				String time = rs.getString("time");
    				String location = rs.getString("location");
    				String status = rs.getString("status");
    				String teamstatsstatus = rs.getString("teamstatsstatus");
    				Boolean teamstatus = true;
    				if (teamstatsstatus.equals("Complete")){
    					teamstatus = false;
    				}
    				Boolean iscomplete = true;
    				if (status.equals("Final")){
    					teamstatus = false;
    				}
    				Boolean canreschedule = rs.getBoolean("canreschedule");
    				Boolean is8u = rs.getBoolean("divisiontag");
    				Boolean homecoachflag = rs.getBoolean("homecoachflag");
    				Boolean coachcountflag = rs.getBoolean("coachcountflag");
    				Boolean printeligibleflag = rs.getBoolean("printeligibleflag");
    				Boolean mitehostflag = rs.getBoolean("mitehostflag");
    				
    				TempGame ogame = new TempGame();
    				ogame.setIdgame(Integer.parseInt(idgame));
    				ogame.setDate(dates);
    				ogame.setTime(time);
    				ogame.setVisitor(awayteam);
    				ogame.setHome(hometeam);
    				ogame.setLocation(location);
    				ogame.setOldawayscore(awayscore);
    				ogame.setOldhomescore(homescore);
    				ogame.setIscomplete(iscomplete);
    				ogame.setStatus(status);
    				ogame.setAwayscore(awayscore);
    				ogame.setHomescore(homescore);
    				ogame.setRetire(canreschedule);
    				ogame.setTeamsstatsstatus(teamstatus);
    				ogame.setIs8u(is8u);
    				ogame.setHomecoachflag(homecoachflag);
					ogame.setPrinturl("printscoresheet.xhtml?id=" + idgame + "&teamid=" + this.teamid.toString());
    				/*if (coachcountflag){
    					ogame.setPrinturl("printscoresheet.xhtml?selco=1&id=" + idgame + "&teamid=" + this.teamid.toString());
    				}else {
    					ogame.setPrinturl("printscoresheet.xhtml?id=" + idgame + "&teamid=" + this.teamid.toString());
    				}*/
    				ogame.setCoachcountflag(coachcountflag);
    				ogame.setPrinteligibleflag(printeligibleflag);
					ogame.setMitehostflag(mitehostflag);
    				tempresult.add(ogame);
    				
				}
				LOGGER.info("We have results for tourney list by team:" + this.teamid);
			}
			
			
			rs.close();
			db.cleanup();
    		
			LOGGER.info("manager has added tournament:" + this.tournamentname);
    		//need to add email sent to scaha statistician and manager
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting tournament list for team" + this.teamid);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setGames(tempresult);
    	TempGameDataModel = new TempGameDataModel(games);
    }



	public TempGameDataModel getTempGamedatamodel(){
    	return TempGameDataModel;
    }
    
    public void setTempgamedatamodel(TempGameDataModel odatamodel){
    	TempGameDataModel = odatamodel;
    }

    
    public TournamentGameDataModel getTournamentgamedatamodel(){
    	return TournamentGameDataModel;
    }
    
    public void setTournamentgamedatamodel(TournamentGameDataModel odatamodel){
    	TournamentGameDataModel = odatamodel;
    }
    
    public ExhibitionGameDataModel getExhibitiongamedatamodel(){
    	return ExhibitionGameDataModel;
    }
    
    public void setExhibitiongamedatamodel(ExhibitionGameDataModel odatamodel){
    	ExhibitionGameDataModel = odatamodel;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    public void getClubID(){
		
		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.getProfid());
			db.getData("CALL scaha.getclubformanager(?)", v);
		    ResultSet rs = db.getResultSet();
			while (rs.next()) {
				this.idclub = rs.getInt("idclub");
			}
			rs.close();
			LOGGER.info("We have results for club for a profile");
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading club by profile");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}

    }
	
    public void isClubHighSchool(){
			
			//first lets get club id for the logged in profile
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			Integer isschool = 0;
			try{
				Vector<Integer> v = new Vector<Integer>();
				v.add(this.idclub);
				db.getData("CALL scaha.IsClubHighSchool(?)", v);
			    ResultSet rs = db.getResultSet();
				while (rs.next()) {
					isschool = rs.getInt("result");
				}
				LOGGER.info("We have results for club is a high school");
				db.cleanup();
				
				if (isschool.equals(0)){
					this.ishighschool=false;
				}else{
					this.ishighschool=true;
				}
	    	} catch (SQLException e) {
	    		// TODO Auto-generated catch block
	    		LOGGER.info("ERROR IN loading club by profile");
	    		e.printStackTrace();
	    		db.rollback();
	    	} finally {
	    		//
	    		// always clean up after yourself..
	    		//
	    		db.free();
	    	}
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

	
	public ScoreboardBean getSb() {
		return sb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setSb(ScoreboardBean pb) {
		this.sb = pb;
	}
	
	public RosterEditDataModel getRostereditdatamodel(){
    	return RosterEditDataModel;
    }
    
    public void setRostereditdatamodel(RosterEditDataModel odatamodel){
    	RosterEditDataModel = odatamodel;
    }
    
    public RosterEditDataModel getRostercoachdatamodel(){
    	return RosterCoachDataModel;
    }
    
    public void setRostercoachdatamodel(RosterEditDataModel odatamodel){
    	RosterCoachDataModel = odatamodel;
    }
    
    
	public void getRoster(){
		List<RosterEdit> templist = new ArrayList<RosterEdit>();
		List<RosterEdit> templist2 = new ArrayList<RosterEdit>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getTeamByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.teamname = rs.getString("teamname");
					this.ismite = rs.getBoolean("ismite");
				}
				LOGGER.info("We have results for team name");
			}
			rs.close();
			db.cleanup();
    		
    		//next get player roster
			cs = db.prepareCall("CALL scaha.getRosterPlayersForManagerByTeamID(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String playerid = rs.getString("idroster");
					String fname = rs.getString("fname");
					String lname = rs.getString("lname");
					String jerseynumber = rs.getString("jerseynumber");
					String suspended = rs.getString("suspended");
					
					RosterEdit player = new RosterEdit();
					player.setIdplayer(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);
					player.setOldjerseynumber(jerseynumber);
					player.setJerseynumber(jerseynumber);
					player.setSuspended(suspended);
					templist.add(player);
				}
				LOGGER.info("We have results for team roster");
			}
			rs.close();
			
			cs = db.prepareCall("CALL scaha.getRosterCoachesForManagerByTeamID(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String playerid = rs.getString("idroster");
					String fname = rs.getString("fname");
					String lname = rs.getString("lname");
					String jerseynumber = rs.getString("teamrole");
					String suspended = rs.getString("suspended");
					
					RosterEdit player = new RosterEdit();
					player.setIdplayer(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);
					player.setOldjerseynumber(jerseynumber);
					player.setJerseynumber(jerseynumber);
					player.setSuspended(suspended);
					templist2.add(player);
				}
				LOGGER.info("We have results for team roster");
			}
			rs.close();
			
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading teams");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setPlayers(templist);
    	setCoaches(templist2);
    	RosterEditDataModel = new RosterEditDataModel(players);
    	RosterCoachDataModel = new RosterEditDataModel(coaches);
	}
    
    public List<RosterEdit> getPlayers(){
		return players;
	}
	
	public void setPlayers(List<RosterEdit> list){
		players = list;
	}
	
	public List<RosterEdit> getCoaches(){
		return coaches;
	}
	
	public void setCoaches(List<RosterEdit> list){
		coaches = list;
	}
	
	public void addTournament() {  
    
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.addTournamentForTeam(?,?,?,?,?,?,?,?,?,?)");
			cs.setInt("teamid", this.teamid);
			cs.setString("newtournamentname", this.tournamentname);
			cs.setString("newlevelplayed", this.levelplayed);
			cs.setString("newstartdate", this.startdate);
			cs.setString("newenddate", this.enddate);
			cs.setString("newcontact", this.contact);
			cs.setString("newphone", this.phone);
			cs.setString("newsanction", this.sanction);
			cs.setString("newlocation", this.location);
			cs.setString("newwebsite", this.website);
		    cs.executeQuery();
			db.commit();
			
			LOGGER.info("manager has added tournament:" + this.tournamentname);
    		
			getTournament();
			
			
			
			//need to add email to manager and scaha statistician
			to = "";
			cs = db.prepareCall("CALL scaha.getManagersforTeam(?)");
			cs.setInt("teamid", this.teamid);
  		    rs = cs.executeQuery();
  		    if (rs != null){
  				while (rs.next()) {
  					if(to.equals("")){
  						to = rs.getString("altemail");
  					}else {
  						to = to + ',' + rs.getString("altemail");
  					}
  				}
  			}
  		    rs.close();
  		    
			
			
			
			/*cs = db.prepareCall("CALL scaha.getSCAHAStatisticianEmail()");
  		    rs = cs.executeQuery();
  		    if (rs != null){
  				while (rs.next()) {
  					to = to + ',' + rs.getString("usercode");
  				}
  			}
  		    rs.close();
  		    */

  		    cs = db.prepareCall("CALL scaha.getTournamentStatusByDate(?)");
  		    cs.setString("startdate", this.startdate);
		    rs = cs.executeQuery();
		    if (rs != null){
				while (rs.next()) {
					this.status = rs.getString("status");
				}
			}
		    
  		    db.cleanup();
  		    
			//to = "lahockeyfan2@yahoo.com";
		    this.setToMailAddress(to);
		    this.setPreApprovedCC("");
		    this.setSubject(this.tournamentname + " Added for " + this.teamname);
		    
			SendMailSSL mail = new SendMailSSL(this);
			LOGGER.info("Finished creating mail object for " + this.tournamentname + " Added for " + this.teamname);
			
			//set flag for getbody to know which template and values to use
			this.addingtournament=true;
			mail.sendMail();
			
			//set flag back to false;
			this.addingtournament=false;
			
			//need to send successful message
			FacesContext.getCurrentInstance().addMessage("tournamentmessages", new FacesMessage(FacesMessage.SEVERITY_INFO,this.tournamentname + " has been added for team: " + this.teamname, ""));
			
			//need to clear values from overlay form after done
			this.tournamentname="";
			this.levelplayed="";
			this.startdate="";
			this.enddate="";
			this.contact="";
			this.phone="";
			this.sanction="";
			this.location="";
			this.website="";
		    
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN adding tournament");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	//now update the alerts
    	setAlerts();
    	
    	FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("managerportal.xhtml?id="+this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getTournament() {  
		List<Tournament> templist = new ArrayList<Tournament>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getTournamentsForTeam(?)");
			cs.setInt("teamid", this.teamid);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String idteam = rs.getString("idteamtournament");
    				String tournamentname = rs.getString("tournamentname");
    				String dates = rs.getString("dates");
    				String contact = rs.getString("contact");
    				String location = rs.getString("location");
    				String status = rs.getString("status");
    				Boolean rendered = rs.getBoolean("rendered");
    				
    				Tournament tournament = new Tournament();
    				tournament.setIdtournament(Integer.parseInt(idteam));
    				tournament.setTournamentname(tournamentname);
    				tournament.setDates(dates);
    				tournament.setContact(contact);
    				tournament.setLocation(location);
    				tournament.setStatus(status);
    				tournament.setRendered(rendered);
    				templist.add(tournament);
				}
				LOGGER.info("We have results for tourney list by team:" + this.teamid);
			}
			
			
			rs.close();
			db.cleanup();
    		
			LOGGER.info("manager has added tournament:" + this.tournamentname);
    		//need to add email sent to scaha statistician and manager
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting tournament list for team" + this.teamid);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setTournaments(templist);
    	TournamentDataModel = new TournamentDataModel(tournaments);
	}
	
	public List<Tournament> getTournaments(){
		return tournaments;
	}
	
	public void setTournaments(List<Tournament> list){
		tournaments = list;
	}
	
	public TournamentDataModel getTournamentdatamodel(){
    	return TournamentDataModel;
    }
    
    public void setTournamentdatamodel(TournamentDataModel odatamodel){
    	TournamentDataModel = odatamodel;
    }
	
	public void editGame(TempGame sgame) {  
	        String oldValue = sgame.getHome();
	        
	        
	        String newvalue = oldValue;
	        getRoster();
	        setAlerts();
	}
	
	/*public void deleteTournament(Tournament tourn){
		//need to set to void
    	Integer tournamentid = tourn.getIdtournament();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("remove tournament from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.deleteTeamTournament(?)");
    		    cs.setInt("teamtournamentid", tournamentid);
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have deleted the tournament"));
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Deleting the Tournament");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		//now we need to reload the data object for the tournament list and the alerts
		getTournament();
		setAlerts();
	}*/
	
	public void editTournamentDetail(Tournament tournament){
		String tourneyid = tournament.getIdtournament().toString();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("edittournamentform.xhtml?teamid=" + this.teamid + "&tournamentid=" + tourneyid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void requestGameChange(TempGame game){
		String gameid = game.getIdgame().toString();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("requestgamechangeform.xhtml?id=" + gameid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void viewTournamentDetail(Tournament tournament){
		String tourneyid = tournament.getIdtournament().toString();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("viewtournamentform.xhtml?teamid=" + this.teamid + "&tournamentid=" + tourneyid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void getTournamentGames() {  
		List<TournamentGame> templist = new ArrayList<TournamentGame>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getTournamentGamesForTeam(?)");
			cs.setInt("teamid", this.teamid);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String idnonscahagame = rs.getString("idnonscahagames");
    				String gametype = rs.getString("gametype");
    				String gamedate = rs.getString("gamedate");
    				String gametime = rs.getString("gametime");
    				String opponent = rs.getString("opponent");
    				String location = rs.getString("location");
    				String status = rs.getString("status");
    				Boolean rendered = rs.getBoolean("rendered");
    				
    				TournamentGame tournament = new TournamentGame();
    				tournament.setIdgame(Integer.parseInt(idnonscahagame));
    				tournament.setGametype(gametype);
    				tournament.setDate(gamedate);
    				tournament.setTime(gametime);
    				tournament.setOpponent(opponent);
    				tournament.setLocation(location);
    				tournament.setStatus(status);
    				tournament.setRendered(rendered);
    				templist.add(tournament);
				}
				LOGGER.info("We have results for tourney list by team:" + this.teamid);
			}
			
			
			rs.close();
			db.cleanup();
    		
			LOGGER.info("manager has added tournament:" + this.tournamentname);
    		//need to add email sent to scaha statistician and manager
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting tournament list for team" + this.teamid);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setTournamentgames(templist);
    	TournamentGameDataModel = new TournamentGameDataModel(tournamentgames);
	}
	
	public List<TournamentGame> getTournamentgames(){
		return tournamentgames;
	}
	
	public void setTournamentgames(List<TournamentGame> tgames){
		tournamentgames = tgames;
	}
	
	public List<ExhibitionGame> getExhibitiongames(){
		return exhibitiongames;
	}
	
	public void setExhibitiongames(List<ExhibitionGame> tgames){
		exhibitiongames = tgames;
	}
	
	public void addTournamentGame(){
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.addTournamentGameForTeam(?,?,?,?,?,?,?)");
			cs.setInt("teamid", this.teamid);
			cs.setInt("newgametypeid", this.gametype);
			cs.setString("newgamedate", this.gamedate);
			cs.setString("newgametime", this.gametime);
			cs.setString("newopponent", this.opponent);
			cs.setString("newlocation", this.tourneygamelocation);
			cs.setInt("playoffeligible", Integer.parseInt(this.playoffeligible));
			rs=cs.executeQuery();
			/*if (rs != null){
  				while (rs.next()) {
  					this.tournamentname=rs.getString("tournamentname");
  				}
  			}*/
  		    db.commit();
			
			LOGGER.info("manager has added tournament game:" + this.gamedate);
    		
			
			//need to add email to manager and scaha statistician
			to = "";
			cs = db.prepareCall("CALL scaha.getManagersforTeam(?)");
			cs.setInt("teamid", this.teamid);
  		    rs = cs.executeQuery();
  		    if (rs != null){
  				while (rs.next()) {
  					if(to.equals("")){
  						to = rs.getString("altemail");
  					}else {
  						to = to + ',' + rs.getString("altemail");
  					}
  				}
  			}
  		    rs.close();
  		    
			/*cs = db.prepareCall("CALL scaha.getSCAHAStatisticianEmail()");
  		    rs = cs.executeQuery();
  		    if (rs != null){
  				while (rs.next()) {
  					to = to + ',' + rs.getString("usercode");
  				}
  			}
  		    rs.close();
  		    */
			db.cleanup();
  		    
			//to = "lahockeyfan2@yahoo.com";
		    this.setToMailAddress(to);
		    this.setPreApprovedCC("");
		    this.setSubject("Game Added vs " + this.opponent);
		    
			SendMailSSL mail = new SendMailSSL(this);
			LOGGER.info("Finished creating mail object for Tournament Game Added vs " + this.opponent);
			//set flag for getbody to know which template and values to use
			if (this.gametype.equals(1)){
				this.addingtournamentgame=true;
			}else {
				this.addingexhibitiongame=true;
			}
			
			
			mail.sendMail();
			
			//set flag back to false;
			if (this.gametype.equals(1)){
				this.addingtournamentgame=false;
			}else {
				this.addingtournamentgame=false;
			}
			
			
			
			//need to clear overlay panel values
			this.selectedtournamentforgame="0";
			this.gametype=0;
			this.gamedate="";
			this.gametime="";
			this.opponent="";
			this.tourneygamelocation="";
			
			
			
			getTournamentGames();
			
			//need to send successful message
			FacesContext.getCurrentInstance().addMessage("tournamentgamemessages", new FacesMessage(FacesMessage.SEVERITY_INFO,this.tournamentname + "game has been added for team: " + this.teamname, ""));
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN adding tournament");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	//need to update the alerts
    	setAlerts();
    	BacktoManagerPortal();
	}
	
	public void deleteTournamentGame(TournamentGame tourn){
		//need to set to void
    	Integer gameid = tourn.getIdgame();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("remove tournament game from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.deleteTeamTournamentGame(?)");
    		    cs.setInt("gameid", gameid);
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have deleted the game."));
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Deleting the Tournament");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		//now we need to reload the data object for the tourney games list and the alerts
		getTournamentGames();
		setAlerts();
	}

	public void editGameDetail(TournamentGame tournament){
		String gameid = tournament.getIdgame().toString();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("edittournamentgameform.xhtml?teamid=" + this.teamid + "&gameid=" + gameid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addExhibitionGame(){
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.addExhibitionGameForTeam(?,?,?,?,?)");
			cs.setInt("teamid", this.teamid);
			cs.setString("newgamedate", this.gamedate);
			cs.setString("newgametime", this.gametime);
			cs.setString("newopponent", this.opponent);
			cs.setString("newlocation", this.exhibitiongamelocation);
			cs.executeQuery();
			db.commit();
			
			LOGGER.info("manager has added exhibition game:" + this.gamedate);
    		
			//need to add email to manager and scaha statistician
			to = "";
			cs = db.prepareCall("CALL scaha.getManagersforTeam(?)");
			cs.setInt("teamid", this.teamid);
  		    rs = cs.executeQuery();
  		    if (rs != null){
  				while (rs.next()) {
  					if(to.equals("")){
  						to = rs.getString("altemail");
  					}else {
  						to = to + ',' + rs.getString("altemail");
  					}
  				}
  			}
  		    rs.close();
  		    
			cs = db.prepareCall("CALL scaha.getSCAHAStatisticianEmail()");
  		    rs = cs.executeQuery();
  		    if (rs != null){
  				while (rs.next()) {
  					to = to + ',' + rs.getString("usercode");
  				}
  			}
  		    rs.close();
  		    db.cleanup();
  		    
			//to = "lahockeyfan2@yahoo.com";
		    this.setToMailAddress(to);
		    this.setPreApprovedCC("");
		    this.setSubject("Exhibition game Added vs " + this.opponent);
		    
			SendMailSSL mail = new SendMailSSL(this);
			LOGGER.info("Finished creating mail object for exhibition game Added vs " + this.opponent);
			
			//set flag for getbody to know which template and values to use
			this.addingexhibitiongame=true;
			mail.sendMail();
			
			//set flag back to false;
			this.addingexhibitiongame=false;
			
			//need to send successful message
			FacesContext.getCurrentInstance().addMessage("exhibitiongamemessages", new FacesMessage(FacesMessage.SEVERITY_INFO,"The exhibition game against " + this.opponent + " has been added for team: " + this.teamname, ""));
			
			//need to clear fields for overlay panel
			this.gamedate="";
			this.gametime="";
			this.opponent="";
			this.exhibitiongamelocation="";
			
			
			getExhibitionGames();
			
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN adding exhibition game");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	//need to update the alerts now
    	setAlerts();
	}
	
	public void getExhibitionGames() {  
		List<ExhibitionGame> templist = new ArrayList<ExhibitionGame>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getExhibitionGamesForTeam(?)");
			cs.setInt("teamid", this.teamid);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String idnonscahagame = rs.getString("idnonscahagames");
    				String gamedate = rs.getString("gamedate");
    				String gametime = rs.getString("gametime");
    				String opponent = rs.getString("opponent");
    				String location = rs.getString("location");
    				String status = rs.getString("status");
    				Boolean rendered = rs.getBoolean("rendered");
    				
    				ExhibitionGame tournament = new ExhibitionGame();
    				tournament.setIdgame(Integer.parseInt(idnonscahagame));
    				tournament.setDate(gamedate);
    				tournament.setTime(gametime);
    				tournament.setOpponent(opponent);
    				tournament.setLocation(location);
    				tournament.setStatus(status);
    				tournament.setRendered(rendered);
    				templist.add(tournament);
				}
				LOGGER.info("We have results for exhibition list by team:" + this.teamid);
			}
			
			
			rs.close();
			db.cleanup();
    		
			//need to add email sent to scaha statistician and manager
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting tournament list for team" + this.teamid);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setExhibitiongames(templist);
    	ExhibitionGameDataModel = new ExhibitionGameDataModel(exhibitiongames);
	}
	
	public void deleteExhibitionTournamentGame(ExhibitionGame game){
		Integer gameid = game.getIdgame();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("remove tournament game from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.deleteTeamTournamentGame(?)");
    		    cs.setInt("gameid", gameid);
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have deleted the exhibition game"));
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Deleting the Tournament");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		//now we need to reload the data object for the loi list
		getExhibitionGames();
	}
	
	public void editExhibitionGameDetail(ExhibitionGame game){
		String gameid = game.getIdgame().toString();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("editexhibitiongameform.xhtml?teamid=" + this.teamid + "&gameid=" + gameid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void uploadSCAHAScoresheet(TempGame game){
		String gameid = game.getIdgame().toString();
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("managegamescoresheet.xhtml?scaha=yes&id=" + gameid + "&teamid=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void uploadTournamentScoresheet(TournamentGame game){
		String gameid = game.getIdgame().toString();
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("managegamescoresheet.xhtml?id=" + gameid + "&teamid=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void uploadExhibitionScoresheet(ExhibitionGame game){
		String gameid = game.getIdgame().toString();
		FacesContext context = FacesContext.getCurrentInstance();
				
		try{
			context.getExternalContext().redirect("managegamescoresheet.xhtml?id=" + gameid + "&teamid=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setAlerts(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Alert> templist = new ArrayList<Alert>();
		List<Help> temphelplist = new ArrayList<Help>();

		try{

			
				//get list of  alerts.
				CallableStatement cs = db.prepareCall("CALL scaha.getManageAlertsWHelp(?)");
 				cs.setInt("teamid",this.teamid);
    		    rs = cs.executeQuery();
    		    
    		    if (rs != null){

    				while (rs.next()) {
						//this is for a list of all help videos accessed via the help link.
						Help help = new Help();
						help.setAlertid(rs.getInt("idalertvideolinks"));
						help.setHelpdescription(rs.getString("helpdescription"));
						help.setVideourl(rs.getString("videourl"));
						help.setVideourltext(rs.getString("helplinkvalue"));

						//this is for alerts displayed on manager portal page specific to the team
						if (rs.getBoolean("displayalert")) {
							Alert alert = new Alert();
							alert.setAlertid(rs.getInt("idalertvideolinks"));
							alert.setTaskdescription(rs.getString("taskdescription"));
							alert.setVideourl(rs.getString("videourl"));
							alert.setVideourltext(rs.getString("videourltext"));

							if (this.ismite && rs.getBoolean("is8u")) {
								templist.add(alert);
							}else if (!this.ismite && !rs.getBoolean("is8u")){
								templist.add(alert);
							}
							alert = null;
						}

						if (this.ismite && rs.getBoolean("is8u")) {
							temphelplist.add(help);
						}else if (!this.ismite && !rs.getBoolean("is8u")){
							temphelplist.add(help);
						}
    				}
    				//LOGGER.info("We have results for manager team flags list by team:" + this.teamid);
    			}
    			
    			rs.close();
    		    db.commit();
    			db.cleanup();
 				
    		    
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN getting alerts");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		setDisplyalerts(templist);
		setHelptopics(temphelplist);
		templist=null;
		temphelplist=null;
	}
	
	private FacesMessage.Severity getSeverity(String messageseverity){
		
		FacesMessage.Severity fmessage = null;
		if (messageseverity.equals("Info")){
			fmessage = FacesMessage.SEVERITY_INFO;
		} else if (messageseverity.equals("Warn")){
			fmessage = FacesMessage.SEVERITY_WARN;
		} else {
			fmessage = FacesMessage.SEVERITY_ERROR;
		}
		
		return fmessage;
	}
	
	private void setTodaysDate(){
		//need to set todays date for email
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		Date date = new Date();
		this.setTodaysdate(dateFormat.format(date).toString());
		
	}

	private void setAddingflags(){
		//need to set todays date for email
		this.addingtournament=false;
		this.addingtournamentgame=false;
		this.addingexhibitiongame=false;
		
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
	
	public void editLiveGame(TempGame game) {  
		
		Integer gameid = game.getIdgame();
		//locate the livegame in the list.. via the above id vs creating a new one from scratch
		//it does not have all the internal data that the one in the applevel bean does.
		// (Scaha Bean)  ScahaBean has a master copy of all games .. that are already pointing to the teams
		// in memory.. all hooked up..
		LiveGame lg = scaha.getScahaLiveGameList().getByKey(gameid);
		pb.setSelectedlivegame(lg);
		pb.setLivegameeditreturn("managerportal.xhtml");
		
		 LOGGER.info("!!!!! Real Selected Game is" + lg);
		  
	     ExternalContext context = FacesContext.getCurrentInstance().getExternalContext(); 
	     try {
	    	 context.redirect("teamgamedetails.xhtml?teamid=" + this.teamid);
	     } catch (IOException e) {
	    	 // TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	 }
	
	public void addScore(TempGame game){
		Integer gameid = game.getIdgame();
		//locate the livegame in the list.. via the above id vs creating a new one from scratch
		//it does not have all the internal data that the one in the applevel bean does.
		// (Scaha Bean)  ScahaBean has a master copy of all games .. that are already pointing to the teams
		// in memory.. all hooked up..
		LiveGame lg = scaha.getScahaLiveGameList().getByKey(gameid);
		pb.setSelectedlivegame(lg);
		pb.setLivegameeditreturn("managerportal.xhtml");
		
		 LOGGER.info("!!!!! Real Selected Game is" + lg);
		  
	     ExternalContext context = FacesContext.getCurrentInstance().getExternalContext(); 
	     try {
	    	 context.redirect("enterscoreforgame.xhtml");
	     } catch (IOException e) {
	    	 // TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	public void loadForSelectedTeam(){
		//Load team roster
        getRoster();
        
        //Load SCAHA Games
        loadScahaGames();
        
        //Load Tournament and Games
        getTournament();
        getTournamentGames();
        
        //Load Exhibition Games
        getExhibitionGames();

        //load default coaches
		getDefaultCoaches();

		//lets load default coaches for game if the game is selected
		if (this.selectedgame != null){
			getDefaultCoachesForGame();
		}

        //now lets highlight the areas needing action.
        setAlerts();

		//now lets check if the manager has added any tournament info if not let's redirect them to the tournament page.
		checkTournaments();
		checkForAAorAAA();
	}
	
	public void isMultipleTeamManager(){
		Integer arraysize = this.managerteams.size();
		if (arraysize>1){
			this.setDisplaymultiple(true);
		}else {
			this.setDisplaymultiple(false);
		}
	}
	
	public void navigatetoaddtournament(){
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("addtournamentmanager.xhtml?id="+this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void navigatetomanagetournament(){
		FacesContext context = FacesContext.getCurrentInstance();

		try{
			context.getExternalContext().redirect("managerconfirmtournaments.xhtml?id="+this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void navigatetoaddgame(){
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("addgame.xhtml?id="+this.teamid+"&from=managerportal");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void BacktoManagerPortal(){
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			if (this.from.equals("suspension")){
				context.getExternalContext().redirect("addsuspensions.xhtml?id=" + this.teamid);
			}else {
				context.getExternalContext().redirect("managerportal.xhtml?id=" + this.teamid);
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setCoachesforGame(TempGame game, Integer teamid){
		
		FacesContext context = FacesContext.getCurrentInstance();
		this.selectedgame = game;

		try{
			context.getExternalContext().redirect("setgamecoaches.xhtml?gameid=" + game.getIdgame() + "&id=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void setCoachesforGamePrint(String url){
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect(url);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public void setDefaultCoaches(){

		FacesContext context = FacesContext.getCurrentInstance();

		try{
			context.getExternalContext().redirect("setdefaultcoaches.xhtml?&id=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public LiveGameRosterSpotList refreshCoachRosterforpl() {

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameRosterSpotList list = null;
		try {
			list = LiveGameRosterSpotList.NewListFactoryByTeamid(pb.getProfile(), db, this.teamid);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			db.free();
		}

		return list;
	}

	public void SetasDefault(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			if (db.setAutoCommit(false)) {

				//Need to provide info to the stored procedure to save or update
				LOGGER.info("save default coaches");
				CallableStatement cs = db.prepareCall("CALL scaha.SaveDefaultCoachesForTeam(?,?,?,?,?)");
				cs.setInt("in_teamid", this.teamid);
				cs.setInt("in_headcoachrosterid", this.headcoach);
				cs.setInt("in_assistantcoach1rosterid", this.assistantcoach1);
				cs.setInt("in_assistantcoach2rosterid", this.assistantcoach2);
				cs.setInt("in_assistantcoach3rosterid", this.assistantcoach3);
				cs.executeQuery();
				db.commit();
				db.cleanup();

				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Successful", "You have saved your default coaches"));
			} else {

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Deleting the Tournament");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

	}

	public void getDefaultCoaches() {
		List<ExhibitionGame> templist = new ArrayList<ExhibitionGame>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.getDefaultCoaches(?)");
			cs.setInt("teamid", this.teamid);
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					this.headcoach = rs.getInt("headcoachrosterid");
					this.assistantcoach1 = rs.getInt("assistantcoach1rosterid");
					this.assistantcoach2 = rs.getInt("assistantcoach2rosterid");
					this.assistantcoach3 = rs.getInt("assistantcoach3rosterid");
					LOGGER.info("We have results for exhibition list by team:" + this.teamid);
				}
			}


			rs.close();
			db.cleanup();

			//need to add email sent to scaha statistician and manager


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN getting default coaches for team:" + this.teamid);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

	}

	public void SetasGameDefault(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			if (db.setAutoCommit(false)) {

				//Need to provide info to the stored procedure to save or update
				LOGGER.info("save default coaches");
				CallableStatement cs = db.prepareCall("CALL scaha.SaveDefaultCoachesForGame(?,?,?,?,?,?)");
				cs.setInt("in_livegameid", this.selectedgame.getIdgame());
				cs.setInt("in_teamid", this.teamid);
				cs.setInt("in_headcoachrosterid", this.headcoach);
				cs.setInt("in_assistantcoach1rosterid", this.assistantcoach1);
				cs.setInt("in_assistantcoach2rosterid", this.assistantcoach2);
				cs.setInt("in_assistantcoach3rosterid", this.assistantcoach3);
				cs.executeQuery();
				db.commit();
				db.cleanup();

				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Successful", "You have saved your default coaches"));
			} else {

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Saving Coaches for the Game:" + this.selectedgame.getIdgame());
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

	}

	public void getDefaultCoachesForGame() {
		List<ExhibitionGame> templist = new ArrayList<ExhibitionGame>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.getDefaultCoachesForGame(?,?)");
			cs.setInt("teamid", this.teamid);
			cs.setInt("gameid", this.selectedgame.getIdgame());

			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					this.headcoach = rs.getInt("headcoachrosterid");
					this.assistantcoach1 = rs.getInt("assistantcoach1rosterid");
					this.assistantcoach2 = rs.getInt("assistantcoach2rosterid");
					this.assistantcoach3 = rs.getInt("assistantcoach3rosterid");
					LOGGER.info("We have results for exhibition list by team:" + this.teamid);
				}
			}


			rs.close();
			db.cleanup();

			//need to add email sent to scaha statistician and manager


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN getting default coaches for team:" + this.teamid);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

	}

	private void checkTournaments(){
		//let's check the count of tournament notification records.  If equal to 0 then we redirect.
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		Integer Notournaments = 0;
		this.setNumberofTournaments("0");
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.teamid);
			db.getData("CALL scaha.AnyTeamTourmanetNotificationRecords(?)", v);
			ResultSet rs = db.getResultSet();
			while (rs.next()) {
				Notournaments = rs.getInt("result");
				this.setNumberofTournaments(Notournaments.toString());
			}
			LOGGER.info("We have results for no tournament notification lookup");
			db.cleanup();

			if (Notournaments.equals(0)) {
				FacesContext context = FacesContext.getCurrentInstance();
				try{
					context.getExternalContext().redirect("managerconfirmtournaments.xhtml?id="+this.teamid);
				} catch (Exception e){
					e.printStackTrace();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading tournament notification counts for team");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
	}

	private void checkForAAorAAA(){
		//let's check the count of tournament notification records.  If equal to 0 then we redirect.
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		this.isaaoraaa = true;
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.teamid);
			db.getData("CALL scaha.IsTeamAAorAAA(?)", v);
			ResultSet rs = db.getResultSet();
			while (rs.next()) {
				this.isaaoraaa = rs.getBoolean("result");
			}
			LOGGER.info("We have results for if team is aa or aaa");
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading tournament notification counts for team");
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
