package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
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
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Penalty;
import com.scaha.objects.Player;
import com.scaha.objects.Score;
import com.scaha.objects.ScoreSummary;
import com.scaha.objects.Sog;
import com.scaha.objects.Stat;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class boxscoresheetBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<Player> homeplayers;
	private List<Player> awayplayers;
	private List<Score> gamescores;
	private List<ScoreSummary> homesummaryscoring;
	private List<ScoreSummary> awaysummaryscoring;
	private List<Penalty> gamehomepenalties;
	private List<Penalty> gameawaypenalties;
	private List<Stat> gamehomestats;
	private List<Sog> gamehomegoaliestats;
	private List<Stat> gameawaystats;
	private List<Sog> gameawaygoaliestats;
	
	
	//bean level properties used by multiple methods
	
	
	//variables for the box score page
	private Integer homeclubid = null;
	private Integer awayclubid = null;
	private String homescore = null;
	private String awayscore = null;
	private String hometeam = null;
	private String awayteam = null;
	private String location = null;
	private String statetag = null;
	private String typetag = null;
	private String startdate = null;
	private String starttime = null;
	private String homeppcount;
	private String awayppcount;
	private String homeppgoalcount;
	private String awayppgoalcount;
	private String selecteddate = null;
	private String selectedseason = null;
	private String selectedschedule = null;
	private String selectedgame = null;
	private String teamid = null;
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{clubBean}")
    private ClubBean clubbean;
	
	@ManagedProperty(value="#{scoreboardBean}")
    private ScoreboardBean scoreboard;
	
	@PostConstruct
    public void init() {
		
		
		
		//will need to load game detail
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("selecteddate") != null)
        {
    		this.selecteddate = hsr.getParameter("selecteddate");
        }
		
    	if(hsr.getParameter("schedule") != null)
        {
    		this.selectedschedule = hsr.getParameter("schedule");
        }
    	
    	if(hsr.getParameter("season") != null)
        {
    		this.selectedseason = hsr.getParameter("season");
        }
    	
    	if(hsr.getParameter("id") != null)
        {
    		selectedgame = hsr.getParameter("id");
        }
		
    	if(hsr.getParameter("teamid") != null)
        {
    		teamid = hsr.getParameter("teamid");
        }
		
    	
    	if(hsr.getParameter("selco") != null)
        {
    		//need to redirect to coach selection page
    		gotoSelectcoaches();
        }
		
    	
		this.loadBoxscore(selectedgame);
    	
    	
		//this.loadBoxscore(gameid);
    	
	}
	
    public boxscoresheetBean() {  
        
    }  
    
    public void setTeamid(String list){
    	this.teamid=list;
    }
    
    public String getTeamid(){
    	return this.teamid;
    }
    
    
    public void setHomeplayers(List<Player> list){
    	this.homeplayers=list;
    }
    
    public List<Player> getHomeplayers(){
    	return this.homeplayers;
    }
    
    public void setAwayplayers(List<Player> list){
    	this.awayplayers=list;
    }
    
    public List<Player> getAwayplayers(){
    	return this.awayplayers;
    }
    
    public String getStartdate(){
    	return startdate;
    }
    
    public void setStartdate(String value){
    	startdate=value;
    }
    
    public String getStarttime(){
    	return starttime;
    }
    
    public void setStarttime(String value){
    	starttime=value;
    }
    
    
    public String getSelectedgame(){
    	return selectedgame;
    }
    
    public void setSelectedgame(String value){
    	selectedgame=value;
    }
    
    
    public String getSelecteddate(){
    	return selecteddate;
    }
    
    public void setSelecteddate(String value){
    	selecteddate=value;
    }
    
    public String getSelectedseason(){
    	return selectedseason;
    }
    
    public void setSelectedseason(String value){
    	selectedseason=value;
    }
    
    public String getSelectedschedule(){
    	return selectedschedule;
    }
    
    public void setSelectedschedule(String value){
    	selectedschedule = value;
    }
    
    
    public void setGamehomestats(List<Stat> value){
    	gamehomestats = value;
    }
    
    public List<Stat> getGamehomestats(){
    	return gamehomestats;
    }
    
    public void setGamehomegoaliestats(List<Sog> value){
    	gamehomegoaliestats = value;
    }
    
    public List<Sog> getGamehomegoaliestats(){
    	return gamehomegoaliestats;
    }
    
    public void setGameawaystats(List<Stat> value){
    	gameawaystats = value;
    }
    
    public List<Stat> getGameawaystats(){
    	return gameawaystats;
    }
    
    public void setGameawaygoaliestats(List<Sog> value){
    	gameawaygoaliestats = value;
    }
    
    public List<Sog> getGameawaygoaliestats(){
    	return gameawaygoaliestats;
    }
    
    
    
    public void setGamehomepenalties(List<Penalty> value){
    	gamehomepenalties = value;
    }
    
    public List<Penalty> getGamehomepenalties(){
    	return gamehomepenalties;
    }
    
    public void setGameawaypenalties(List<Penalty> value){
    	gameawaypenalties = value;
    }
    
    public List<Penalty> getGameawaypenalties(){
    	return gameawaypenalties;
    }
    
    
    public void setHomesummaryscoring(List<ScoreSummary> value){
    	homesummaryscoring = value;
    }
    
    public List<ScoreSummary> getHomesummaryscoring(){
    	return homesummaryscoring;
    }
    
    public void setAwaysummaryscoring(List<ScoreSummary> value){
    	awaysummaryscoring = value;
    }
    
    public List<ScoreSummary> getAwaysummaryscoring(){
    	return awaysummaryscoring;
    }
    
    public void setGamescores(List<Score> score){
    	gamescores = score;
    }
    
    public List<Score> getGamescores(){
    	return gamescores;
    }
    
    public void setHomeclubid(Integer value){
    	homeclubid = value;
    }
    
    public Integer getHomeclubid(){
    	return homeclubid;
    }
    
    public void setAwayclubid(Integer value){
    	awayclubid = value;
    }
    
    public Integer getAwayclubid(){
    	return awayclubid;
    }
    
    public void setHometeam(String value){
    	hometeam = value;
    }
    
    public String getHometeam(){
    	return hometeam;
    }
    
    public void setAwayteam(String value){
    	awayteam = value;
    }
    
    public String getAwayteam(){
    	return awayteam;
    }
    
    public void setHomescore(String value){
    	homescore = value;
    }
    
    public String getHomescore(){
    	return homescore;
    }
    
    public void setAwayscore(String value){
    	awayscore = value;
    }
    
    public String getAwayscore(){
    	return awayscore;
    }
    
    public void setLocation(String value){
    	location = value;
    }
    
    public String getLocation(){
    	return location;
    }
    
    public void setStatetag(String value){
    	statetag = value;
    }
    
    public String getStatetag(){
    	return statetag;
    }
    
    public void setTypetag(String value){
    	typetag = value;
    }
    
    public String getTypetag(){
    	return typetag;
    }
    
    
    public ScahaBean getScaha() {
		return scaha;
	}

	/**
	 * @param scaha the scaha to set
	 */
	public void setClubbean(ClubBean club) {
		this.clubbean = club;
	}
    
	public ClubBean getClubbean() {
		return this.clubbean;
	}

	/**
	 * @param scaha the scaha to set
	 */
	public void setScoreboard(ScoreboardBean club) {
		this.scoreboard = club;
	}
    
	public ScoreboardBean getScoreboard() {
		return this.scoreboard;
	}
	
	/**
	 * @param scaha the scaha to set
	 */
	public void setScaha(ScahaBean scaha) {
		this.scaha = scaha;
	}
	
	
    public String getHomeppcount(){
    	return homeppcount;
    }
    
    public void setHomeppcount(String value){
    	homeppcount=value;
    }
    
    public String getAwayppcount(){
    	return awayppcount;
    }
    
    public void setAwayppcount(String value){
    	awayppcount=value;
    }
    
    public String getHomeppgoalcount(){
    	return homeppgoalcount;
    }
    
    public void setHomeppgoalcount(String value){
    	homeppgoalcount=value;
    }
    
    public String getAwayppgoalcount(){
    	return awayppgoalcount;
    }
    
    public void setAwayppgoalcount(String value){
    	awayppgoalcount=value;
    }
    
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("gamecentral.xhtml?selecteddate=" + this.selectedgame + "&schedule=" + this.selectedschedule + "&season=" + this.selectedseason);
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    
	
	//this method loads up the objects for the box score page
	public void loadBoxscore(String gameid){
		List<Player> temphomeplayers = new ArrayList<Player>();
		List<Player> tempawayplayers = new ArrayList<Player>();
		List<ScoreSummary> temphomescoresummary = new ArrayList<ScoreSummary>();
		List<ScoreSummary> tempawayscoresummary = new ArrayList<ScoreSummary>();
		List<Penalty> homepenalties = new ArrayList<Penalty>();
		List<Penalty> awaypenalties = new ArrayList<Penalty>();
		List<Stat> homestats = new ArrayList<Stat>();
		List<Stat> awaystats = new ArrayList<Stat>();
		List<Sog> homegoaliestats = new ArrayList<Sog>();
		List<Sog> awaygoaliestats = new ArrayList<Sog>();
		
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//need to load game details - score, location, date/time
    		CallableStatement cs = db.prepareCall("CALL scaha.getScoreboardGameDetail(?,?)");
    		
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					this.awayclubid=rs.getInt("awayclubid");
					this.homeclubid=rs.getInt("homeclubid");
					this.hometeam=rs.getString("hometeam");
					this.awayteam=rs.getString("awayteam");
					this.homescore=rs.getString("homescore");
					this.awayscore=rs.getString("awayscore");
					this.location=rs.getString("location");
					this.statetag=rs.getString("statetag");
					this.typetag=rs.getString("typetag");
					this.startdate=rs.getString("startdate");
					this.starttime=rs.getString("starttime");
				}
				//LOGGER.info("We have selected details for live game id:" + gameid);
			}
			rs.close();
			
			//need to load home and away team rosters
			cs = db.prepareCall("CALL scaha.getScoreboardRosters(?,?,?)");
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
    		cs.setString("homeawayflag", "home");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Player player = new Player();
					player.setJerseynumber(rs.getString("jerseynumber"));
					player.setFirstname(rs.getString("firstname"));
					player.setLastname(rs.getString("lastname"));
					
					temphomeplayers.add(player);
				}
				//LOGGER.info("We have home players for live game id:" + gameid);
			}
			
			rs.close();
			
			cs = db.prepareCall("CALL scaha.getScoreboardRosters(?,?,?)");
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
    		cs.setString("homeawayflag", "away");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Player player = new Player();
					player.setJerseynumber(rs.getString("jerseynumber"));
					player.setFirstname(rs.getString("firstname"));
					player.setLastname(rs.getString("lastname"));
					
					tempawayplayers.add(player);
				}
				//LOGGER.info("We have selected details for live game id:" + gameid);
			}
			
			rs.close();
			
			
			//need to load scoring for home and away teams
			cs = db.prepareCall("CALL scaha.getScoreboardScoring(?,?,?)");
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
    		cs.setString("homeawayflag", "home");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					ScoreSummary ss = new ScoreSummary();
					
					ss.setAssist1(rs.getString("assist1"));
					ss.setAssist2(rs.getString("assist2"));
					ss.setGoalscorer(rs.getString("goalscorer"));
					ss.setGoaltime(rs.getString("goaltime"));
					ss.setGoaltype(rs.getString("goaltype"));
					Integer period = rs.getInt("period");
					ss.setPeriod(period);
					
					temphomescoresummary.add(ss);
				}
				//LOGGER.info("We have selected scoring for live game id:" + gameid);
			}
			rs.close();
			
			cs = db.prepareCall("CALL scaha.getScoreboardScoring(?,?,?)");
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
    		cs.setString("homeawayflag", "away");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					ScoreSummary ss = new ScoreSummary();
					
					ss.setAssist1(rs.getString("assist1"));
					ss.setAssist2(rs.getString("assist2"));
					ss.setGoalscorer(rs.getString("goalscorer"));
					ss.setGoaltime(rs.getString("goaltime"));
					ss.setGoaltype(rs.getString("goaltype"));
					Integer period = rs.getInt("period");
					ss.setPeriod(period);
					
					tempawayscoresummary.add(ss);
				}
				//LOGGER.info("We have selected scoring for live game id:" + gameid);
			}
			rs.close();
			
    		
			
			
    		//need to load home team goalie stats
			cs = db.prepareCall("CALL scaha.getScoreboardShots(?,?,?)");
			cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
    		cs.setString("homeawayflag", "home");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					Sog sog = new Sog();
					sog.setJerseynumber(rs.getString("jerseynumber"));
					sog.setShots1(rs.getInt("shots1"));
					sog.setShots2(rs.getInt("shots2"));
					sog.setShots3(rs.getInt("shots3"));
					sog.setShots8(rs.getInt("otshots"));
					sog.setShots9(rs.getInt("totalshots"));
					homegoaliestats.add(sog);
				}
				//LOGGER.info("We have selected home goalie stats for live game id:" + gameid);
			}
			rs.close();
			
    		//need to load away goalie stats
			cs = db.prepareCall("CALL scaha.getScoreboardShots(?,?,?)");
			cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
    		cs.setString("homeawayflag", "away");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					Sog sog = new Sog();
					sog.setJerseynumber(rs.getString("jerseynumber"));
					sog.setShots1(rs.getInt("shots1"));
					sog.setShots2(rs.getInt("shots2"));
					sog.setShots3(rs.getInt("shots3"));
					sog.setShots9(rs.getInt("totalshots"));
					sog.setShots8(rs.getInt("otshots"));
					awaygoaliestats.add(sog);
				}
				//LOGGER.info("We have selected home goalie stats for live game id:" + gameid);
			}
			rs.close();
    		
			//need to load home penalty stats
			cs = db.prepareCall("CALL scaha.getScoreboardPenalties(?,?,?)");
			cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
    		cs.setString("homeawayflag", "home");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					Penalty penalty = new Penalty();
					penalty.setPlayername(rs.getString("jerseynumber"));
					penalty.setPeriod(rs.getInt("period"));
					penalty.setPenaltytype(rs.getString("penaltytype"));
					penalty.setMinutes(rs.getString("minutes"));
					penalty.setTimeofpenalty(rs.getString("timeofpenalty"));
					homepenalties.add(penalty);
				}
				//LOGGER.info("We have selected home goalie stats for live game id:" + gameid);
			}
			rs.close();
    		
			//need to load away penalty stats
			cs = db.prepareCall("CALL scaha.getScoreboardPenalties(?,?,?)");
			cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
    		cs.setString("homeawayflag", "away");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					Penalty penalty = new Penalty();
					penalty.setPlayername(rs.getString("jerseynumber"));
					penalty.setPeriod(rs.getInt("period"));
					penalty.setPenaltytype(rs.getString("penaltytype"));
					penalty.setMinutes(rs.getString("minutes"));
					penalty.setTimeofpenalty(rs.getString("timeofpenalty"));
					awaypenalties.add(penalty);
				}
				//LOGGER.info("We have selected home goalie stats for live game id:" + gameid);
			}
			rs.close();
			
			cs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN retrieving selected game details for gameid" + gameid);
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
		
		this.setHomeplayers(temphomeplayers);
		this.setAwayplayers(tempawayplayers);
		
		setHomesummaryscoring(temphomescoresummary);
		setAwaysummaryscoring(tempawayscoresummary);
		setGamehomepenalties(homepenalties);
		setGameawaypenalties(awaypenalties);
		setGamehomestats(homestats);
		setGamehomegoaliestats(homegoaliestats);
		setGameawaystats(awaystats);
		setGameawaygoaliestats(awaygoaliestats);
	}
	

	public void gotoSelectcoaches(){
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("setgamecoaches.xhtml?id=" + this.selectedgame + "&teamid=" + this.teamid + "&gotoprint=1");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}

