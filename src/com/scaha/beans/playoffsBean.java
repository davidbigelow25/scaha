package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.GeneralSeason;
import com.scaha.objects.Participant;
import com.scaha.objects.Playoff;
import com.scaha.objects.Playoffbracket;
import com.scaha.objects.PlayoffbracketDataModel;
import com.scaha.objects.ScahaTeam;
import com.scaha.objects.Schedule;
import com.scaha.objects.TempGame;
import com.scaha.objects.TempGameDataModel;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class playoffsBean implements Serializable{

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
	private List<TempGame> games = null;
	private List<GeneralSeason> seasonlist = null;
	private List<Schedule> schedulelist =  null;
	private List<Playoff> playoffdetails = null;
	private List<Participant> partlist = null;
	private List<Playoffbracket> playoffbrackets1 = null;
	private List<Playoffbracket> playoffbrackets2 = null;
	
    
	//bean level properties used by multiple methods
	private Integer profileid = 0;
	
	//datamodels for all of the lists on the page
	private TempGameDataModel TempGameDataModel = null;
	private PlayoffbracketDataModel Bracket1DataModel = null;
	private PlayoffbracketDataModel Bracket2DataModel = null;
	
    //properties for storing the selected row of each of the datatables or drop downs
    private TempGame selectedgame = null;
    private Integer selectedschedule = null;
    private String selectedseasonid = null;
    private String Champion = null;
    private String Runnerup = null;
    
    //some variables for the mobile version
  	private String agegroup = null;
  	private String skillgroup = null;
  	private String scheduletitle = null;

	@PostConstruct
    public void init() {
		games = new ArrayList<TempGame>();  
        TempGameDataModel = new TempGameDataModel(games);
        seasonlist = new ArrayList<GeneralSeason>();
        playoffdetails = new ArrayList<Playoff>();
        
        
        //Load Default Lists Seasons, Standing, and Games
        loadSeasonlist();
        
        
	}
	
    public playoffsBean() {  
        
    }  
    
    public String getScheduletitle(){
    	return scheduletitle;
    }
    
    public void setScheduletitle(String tdate){
    	scheduletitle=tdate;
    }
    
	public String getAgegroup(){
    	return agegroup;
    }
    
    public void setAgegroup(String tdate){
    	agegroup=tdate;
    }
    
    public String getSkillgroup(){
    	return skillgroup;
    }
    
    public void setSkillgroup(String tdate){
    	skillgroup=tdate;
    }
	
    
    public String getChampion() {
		return Champion;
	}

    public void setChampion(String value){
    	Champion = value;
    }
    
    public String getRunnerup() {
		return Runnerup;
	}

    public void setRunnerup(String value){
    	Runnerup = value;
    }
    /**
	 * @return the selectedseasonid
	 */
	public String getSelectedseasonid() {
		return selectedseasonid;
	}

	/**
	 * @param selectedseasonid the selectedseasonid to set
	 */
	public void setSelectedseasonid(String selectedseasonid) {
		this.selectedseasonid = selectedseasonid;
	}
    
	/**
	 * @return the schedulelist
	 */
	public List<Schedule> getSchedulelist() {
		return schedulelist;
	}

	/**
	 * @param schedulelist the schedulelist to set
	 */
	public void setSchedulelist(List<Schedule> schedulelist) {
		this.schedulelist = schedulelist;
	}
	
	public void setSeasonlist(List<GeneralSeason> list){
		seasonlist = list;
	}
	
	public List<GeneralSeason> getSeasonlist(){
		return seasonlist;
	}
    
	public void setPlayoffdetails(List<Playoff> list){
		playoffdetails = list;
	}
	
	public List<Playoff> getPlayoffdetails(){
		return playoffdetails;
	}
	
	public void setPlayoffbrackets1(List<Playoffbracket> list){
		playoffbrackets1 = list;
	}
	
	public List<Playoffbracket> getPlayoffbrackets1(){
		return playoffbrackets1;
	}
	
	public void setPlayoffbrackets2(List<Playoffbracket> list){
		playoffbrackets2 = list;
	}
	
	public List<Playoffbracket> getPlayoffbrackets2(){
		return playoffbrackets2;
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
    
	public Integer getSelectedschedule(){
		return selectedschedule;
	}
	
	public void setSelectedschedule(Integer selectedSchedule){
		selectedschedule = selectedSchedule;
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
    		CallableStatement cs = db.prepareCall("CALL scaha.getSCAHAGamesForPlayoffs(?)");
			cs.setInt("scheduleid", this.selectedschedule);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String idgame = rs.getString("idlivegame");
    				String hometeam = rs.getString("hometeam");
    				String awayteam = rs.getString("awayteam");
    				String homescore = rs.getString("homescore");
    				if (homescore.equals("-1")){
    					homescore = "";
    				}
    				String awayscore = rs.getString("awayscore");
    				if (awayscore.equals("-1")){
    					awayscore = "";
    				}
    				
    				
    				
    				if (!(homescore=="") && !(awayscore=="")){
	    					if (Integer.parseInt(homescore) > Integer.parseInt(awayscore)){
		    					this.setChampion(hometeam);
		    					this.setRunnerup(awayteam);
		    				} else {
		    					this.setChampion(awayteam);
		    					this.setRunnerup(hometeam);
		    				}
	    			} else {
    					this.setChampion("");
    					this.setRunnerup("");
    				}
    				
    				/*if (this.selectedschedule>349){
    					this.setChampion("");
    					this.setRunnerup("");
    				}*/
    				
    				String otinfo = rs.getString("otinfo");
    				String othomeaway = rs.getString("othomeaway");
    				
    				if (otinfo != null){
	    				if (othomeaway.equals("home")){
	    					homescore=homescore+otinfo;
	    				}else {
	    					awayscore=awayscore+otinfo;
	    				}
    				}
    				String dates = rs.getString("date");
    				String time = rs.getString("time");
    				String location = rs.getString("location");
    				
    				TempGame ogame = new TempGame();
    				ogame.setIdgame(Integer.parseInt(idgame));
    				ogame.setDate(dates);
    				ogame.setTime(time);
    				ogame.setVisitor(awayteam);
    				ogame.setHome(hometeam);
    				ogame.setLocation(location);
    				ogame.setOldawayscore(awayscore);
    				ogame.setOldhomescore(homescore);
    				ogame.setAwayscore(awayscore);
    				ogame.setHomescore(homescore);
    				tempresult.add(ogame);
    				
				}
				LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedschedule);
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting scaha games schedule for review by statistician for schedule:" + this.selectedschedule);
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

    
  //retrieves list of existing teams for the club
    public void loadSeasonlist(){
    	List<GeneralSeason> tempresult = new ArrayList<GeneralSeason>();
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getSCAHAPlayoffSeasons");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String seasontag = rs.getString("seasontag");
    				String seasondescription = rs.getString("description");
    				
    				GeneralSeason oseason = new GeneralSeason();
    				oseason.setDescription(seasondescription);
    				oseason.setTag(seasontag);
    				tempresult.add(oseason);
    				
				}
				LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedschedule);
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting scaha games schedule for review by statistician for schedule:" + this.selectedschedule);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setSeasonlist(tempresult);
    }
    
    public void loadDivisions(){
    	List<Schedule> tempresult = new ArrayList<Schedule>();
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getSCAHAPlayoffSchedules(?)");
    		cs.setString("seasontag", this.selectedseasonid);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer scheduleid = rs.getInt("idschedule");
    				String description = rs.getString("description");
    				
    				Schedule oschedule = new Schedule(scheduleid);
    				oschedule.setDescription(description);
    				tempresult.add(oschedule);
    				
				}
				LOGGER.info("We have results for scaha playoff schedule " + this.selectedseasonid);
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting scaha playoffs schedule list" + this.selectedseasonid);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setSchedulelist(tempresult);
    	games = new ArrayList<TempGame>();  
        TempGameDataModel = new TempGameDataModel(games);
        playoffdetails = new ArrayList<Playoff>();
        setPlayoffbrackets1(new ArrayList<Playoffbracket>());
    	setPlayoffbrackets2(new ArrayList<Playoffbracket>());
    	this.setPartlist(new ArrayList<Participant>());
    	
    }
    
    public void onScheduleChange(){
    	//need to load standings, schedule, brackets, and division details
    	
    	List<Playoff> tempresult = new ArrayList<Playoff>();
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get playoff details
    		CallableStatement cs = db.prepareCall("CALL scaha.getSCAHAPlayoffDetails(?)");
    		cs.setInt("scheduleid", this.selectedschedule);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String division = rs.getString("division");
    				String startdate = rs.getString("startdate");
    				String enddate = rs.getString("enddate");
    				String numberteams = rs.getString("numberteams");
    				String formatheader = rs.getString("formatheader");
    				String formatadvancement = rs.getString("formatadvancement");
    				String formatgroups = rs.getString("formatgroups");
    				String location = rs.getString("location");
    				
    				Playoff oplayoff = new Playoff();
    				oplayoff.setDivision(division);
    				oplayoff.setStartdate(startdate);
    				oplayoff.setEnddate(enddate);
    				oplayoff.setNumberteams(numberteams);
    				oplayoff.setFormatadvancement(formatadvancement);
    				oplayoff.setFormatheader(formatheader);
    				oplayoff.setFormatgroups(formatgroups);
    				oplayoff.setLocation(location);
    				
    				tempresult.add(oplayoff);
    				
				}
				LOGGER.info("We have results for scaha playoff details " + this.selectedschedule);
			}
			
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting scaha playoffs details" + this.selectedschedule);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setPlayoffdetails(tempresult);
    	//now let's load scaha games
    	loadScahaGames();
    	//now let's load regular season division standings
    	loadStandings();
    	//now let's load playoff brackets;
    	loadBrackets();
    }
    
    public TempGameDataModel getTempGamedatamodel(){
    	return TempGameDataModel;
    }
    
    public void setTempgamedatamodel(TempGameDataModel odatamodel){
    	TempGameDataModel = odatamodel;
    }

    public PlayoffbracketDataModel getPlayoffbracket1datamodel(){
    	return Bracket1DataModel;
    }
    
    public void setPlayoffbracket1datamodel(PlayoffbracketDataModel odatamodel){
    	Bracket1DataModel = odatamodel;
    }
    
    public PlayoffbracketDataModel getPlayoffbracket2datamodel(){
    	return Bracket2DataModel;
    }
    
    public void setPlayoffbracket2datamodel(PlayoffbracketDataModel odatamodel){
    	Bracket2DataModel = odatamodel;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
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
	
	public void loadStandings(){
		List<Participant> data = new ArrayList<Participant>();
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		PreparedStatement ps = db.prepareStatement("call scaha.getHistoricalPlayoffParticipantsBySchedule(?)");
    		ps.setInt(1,this.selectedschedule);
    		ResultSet rs = ps.executeQuery();
    		int y = 1;
    		while (rs.next()) {
    			int i = 1;
    			Participant part = new Participant(rs.getInt(i++));
    			part.setRank(rs.getInt(i++));
    			
    			//need to set scahateam objects
    			ScahaTeam listteam = new ScahaTeam(rs.getInt(i++));
    			listteam.setTeamname(rs.getString(i++));
    			
    			part.setTeam(listteam);
    			part.setGames(rs.getInt(i++));
    			part.setExgames(rs.getInt(i++));
    			part.setGamesplayed(rs.getInt(i++));
    			part.setWins(rs.getInt(i++));
    			part.setLoses(rs.getInt(i++));
    			part.setTies(rs.getInt(i++));
    			part.setPoints(rs.getInt(i++));
    			part.setGf(rs.getInt(i++));
    			part.setGa(rs.getInt(i++));
    			part.setHasdropped((rs.getInt(i++) == 0 ? false : true));
    			
    			if (this.selectedschedule<350){
	    			part.setGd(part.getGf()- part.getGa());
    			}
    			else {
    				part.setGd(rs.getInt(i++));
    			}
    			
    			part.setSchedule(null);
    			part.setPlace(y++);
    			LOGGER.info("Found new Participant for schedule " + selectedschedule + ". " + part);
    			
    			data.add(part);
    			
    		}
    		rs.close();
    		ps.close();
    		db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting scaha games schedule for review by statistician for schedule:" + this.selectedschedule);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setPartlist(data);
    	
    }
	
	/**
	 * @return the partlist
	 */
	public List<Participant> getPartlist() {
		return partlist;
	}

	/**
	 * @param partlist the partlist to set
	 */
	public void setPartlist(List<Participant> partlist) {
		this.partlist = partlist;
	}
	
	public void loadBrackets(){
    	List<Playoffbracket> tempresult1 = new ArrayList<Playoffbracket>();
    	List<Playoffbracket> tempresult2 = new ArrayList<Playoffbracket>();
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getPlayoffBrackets(?,?)");
			cs.setInt("scheduleid", this.selectedschedule);
			cs.setInt("displaybracket", 1);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer idbracket = rs.getInt("idplayoffsbrackets");
					String teamname = rs.getString("teamname");
    				String game1 = rs.getString("game1");
    				String game2 = rs.getString("game2");
    				String game3 = rs.getString("game3");
    				String gametotal = rs.getString("gametotal");
    				String place = rs.getString("place");
    				
    				Playoffbracket obracket = new Playoffbracket();
    				obracket.setIdbracket(idbracket);
    				obracket.setTeamname(teamname);
    				obracket.setGame1(game1);
    				obracket.setNewgame1(game1);
    				obracket.setGame2(game2);
    				obracket.setNewgame2(game2);
    				obracket.setGame3(game3);
    				obracket.setNewgame3(game3);
    				obracket.setGametotal(gametotal);
    				obracket.setNewgametotal(gametotal);
    				obracket.setPlace(place);
    				obracket.setNewplace(place);tempresult1.add(obracket);
    				
				}
				LOGGER.info("We have results for playoff bracket one:" + this.selectedschedule);
			}
			
			
			rs.close();
			
			cs = db.prepareCall("CALL scaha.getPlayoffBrackets(?,?)");
			cs.setInt("scheduleid", this.selectedschedule);
			cs.setInt("displaybracket", 2);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer idbracket = rs.getInt("idplayoffsbrackets");
					String teamname = rs.getString("teamname");
					String game1 = rs.getString("game1");
    				String game2 = rs.getString("game2");
    				String game3 = rs.getString("game3");
    				String gametotal = rs.getString("gametotal");
    				String place = rs.getString("place");
    				
    				Playoffbracket obracket = new Playoffbracket();
    				obracket.setIdbracket(idbracket);
    				obracket.setTeamname(teamname);
    				obracket.setGame1(game1);
    				obracket.setNewgame1(game1);
    				obracket.setGame2(game2);
    				obracket.setNewgame2(game2);
    				obracket.setGame3(game3);
    				obracket.setNewgame3(game3);
    				obracket.setGametotal(gametotal);
    				obracket.setNewgametotal(gametotal);
    				obracket.setPlace(place);
    				obracket.setNewplace(place);
    				tempresult2.add(obracket);
    				
				}
				LOGGER.info("We have results for playoff bracket 2:" + this.selectedschedule);
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting playoff brackets:" + this.selectedschedule);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	Bracket1DataModel = new PlayoffbracketDataModel(tempresult1);
    	Bracket2DataModel = new PlayoffbracketDataModel(tempresult2);
    	this.setPlayoffbrackets1(tempresult1);
    	this.setPlayoffbrackets2(tempresult2);
    	
    }
	
	public void setPlayoffs(){
		Integer scheduleidformobile = 0;
		
		//need to find out the age group and skill group selected and then refresh the live game list
		if (agegroup.equals("squirt") && skillgroup.equals("A")){
			scheduleidformobile=401;
			scheduletitle = "Squirt A";
		}
		if (agegroup.equals("squirt") && skillgroup.equals("BB")){
			scheduleidformobile=400;
			scheduletitle = "Squirt BB";
		}
		if (agegroup.equals("squirt") && skillgroup.equals("B")){
			scheduleidformobile=399;
			scheduletitle = "Squirt B";
		}
		if (agegroup.equals("peewee") && skillgroup.equals("AA")){
			scheduleidformobile=415;
			scheduletitle = "Peewee AA";
		}
		if (agegroup.equals("peewee") && skillgroup.equals("A")){
			scheduleidformobile=404;
			scheduletitle = "Peewee A";
		}
		if (agegroup.equals("peewee") && skillgroup.equals("BB")){
			scheduleidformobile=403;
			scheduletitle = "Peewee BB";
		}
		if (agegroup.equals("peewee") && skillgroup.equals("B")){
			scheduleidformobile=402;
			scheduletitle = "Peewee B";
		}
		if (agegroup.equals("bantam") && skillgroup.equals("AA")){
			scheduleidformobile=416;
			scheduletitle = "Bantam AA";
		}
		if (agegroup.equals("bantam") && skillgroup.equals("A")){
			scheduleidformobile=406;
			scheduletitle = "Bantam A";
		}
		if (agegroup.equals("bantam") && skillgroup.equals("B")){
			scheduleidformobile=405;
			scheduletitle = "Bantam B";
		}
		if (agegroup.equals("midget16") && skillgroup.equals("AA")){
			scheduleidformobile=417;
			scheduletitle = "16U AA";
		}
		if (agegroup.equals("midget18") && skillgroup.equals("AA")){
			scheduleidformobile=418;
			scheduletitle = "18U AA";
		}
		
		this.setSelectedschedule(scheduleidformobile);
		onScheduleChange();
	}
}

