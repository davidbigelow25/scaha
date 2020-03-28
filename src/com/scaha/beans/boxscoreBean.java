package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.SelectEvent;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Game;
import com.scaha.objects.Penalty;
import com.scaha.objects.Score;
import com.scaha.objects.ScoreSummary;
import com.scaha.objects.Stat;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class boxscoreBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<Score> gamescores;
	private List<ScoreSummary> summaryscoring;
	private List<Penalty> gamepenalties;
	private List<Stat> gamehomestats;
	private List<Stat> gamehomegoaliestats;
	private List<Stat> gameawaystats;
	private List<Stat> gameawaygoaliestats;
	
	
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
	private String homeppcount;
	private String awayppcount;
	private String homeppgoalcount;
	private String awayppgoalcount;
	private Boolean noperiod1goals;
	private Boolean noperiod2goals;
	private Boolean noperiod3goals;
	private Boolean noperiod4goals;
	private Boolean noperiod1penalties;
	private Boolean noperiod2penalties;
	private Boolean noperiod3penalties;
	private Boolean noperiod4penalties;
	private String selecteddate = null;
	private String selectedseason = null;
	private String selectedschedule = null;
	private String selectedgame = null;
	
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{clubBean}")
    private ClubBean clubbean;
	
	@ManagedProperty(value="#{scoreboardBean}")
    private ScoreboardBean scoreboard;
	
	@PostConstruct
    public void init() {
		//set default flags for hiding some data
		this.setNoperiod1goals(true);
		this.setNoperiod2goals(true);
		this.setNoperiod3goals(true);
		this.setNoperiod4goals(true);
		this.setNoperiod1penalties(true);
		this.setNoperiod2penalties(true);
		this.setNoperiod3penalties(true);
		this.setNoperiod4penalties(true);
		
		
		
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
		    	
		this.loadBoxscore(selectedgame);
    	
    	
		//this.loadBoxscore(gameid);
    	
	}
	
    public boxscoreBean() {  
        
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
    
    public void setNoperiod1goals(Boolean value){
    	noperiod1goals=value;
    }
    
    public Boolean getNoperiod1goals(){
    	return noperiod1goals;
    }
    
    public void setNoperiod2goals(Boolean value){
    	noperiod2goals=value;
    }
    
    public Boolean getNoperiod2goals(){
    	return noperiod2goals;
    }
    
    public void setNoperiod3goals(Boolean value){
    	noperiod3goals=value;
    }
    
    public Boolean getNoperiod3goals(){
    	return noperiod3goals;
    }
    
    public void setNoperiod4goals(Boolean value){
    	noperiod4goals=value;
    }
    
    public Boolean getNoperiod4goals(){
    	return noperiod4goals;
    }
    
    public void setNoperiod1penalties(Boolean value){
    	noperiod1penalties=value;
    }
    
    public Boolean getNoperiod1penalties(){
    	return noperiod1penalties;
    }
    
    public void setNoperiod2penalties(Boolean value){
    	noperiod2penalties=value;
    }
    
    public Boolean getNoperiod2penalties(){
    	return noperiod2penalties;
    }
    
    public void setNoperiod3penalties(Boolean value){
    	noperiod3penalties=value;
    }
    
    public Boolean getNoperiod3penalties(){
    	return noperiod3penalties;
    }
    
    public void setNoperiod4penalties(Boolean value){
    	noperiod4penalties=value;
    }
    
    public Boolean getNoperiod4penalties(){
    	return noperiod4penalties;
    }
    
    public void setGamehomestats(List<Stat> value){
    	gamehomestats = value;
    }
    
    public List<Stat> getGamehomestats(){
    	return gamehomestats;
    }
    
    public void setGamehomegoaliestats(List<Stat> value){
    	gamehomegoaliestats = value;
    }
    
    public List<Stat> getGamehomegoaliestats(){
    	return gamehomegoaliestats;
    }
    
    public void setGameawaystats(List<Stat> value){
    	gameawaystats = value;
    }
    
    public List<Stat> getGameawaystats(){
    	return gameawaystats;
    }
    
    public void setGameawaygoaliestats(List<Stat> value){
    	gameawaygoaliestats = value;
    }
    
    public List<Stat> getGameawaygoaliestats(){
    	return gameawaygoaliestats;
    }
    
    
    
    public void setGamepenalties(List<Penalty> value){
    	gamepenalties = value;
    }
    
    public List<Penalty> getGamepenalties(){
    	return gamepenalties;
    }
    
    
    public void setSummaryscoring(List<ScoreSummary> value){
    	summaryscoring = value;
    }
    
    public List<ScoreSummary> getSummaryscoring(){
    	return summaryscoring;
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
		List<Score> scores = new ArrayList<Score>();
		List<ScoreSummary> scoresummarys = new ArrayList<ScoreSummary>();
		List<Penalty> penalties = new ArrayList<Penalty>();
		List<Stat> homestats = new ArrayList<Stat>();
		List<Stat> awaystats = new ArrayList<Stat>();
		List<Stat> homegoaliestats = new ArrayList<Stat>();
		List<Stat> awaygoaliestats = new ArrayList<Stat>();
		
		
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
				}
				//LOGGER.info("We have selected details for live game id:" + gameid);
			}
			rs.close();
			
			//need to load shots by period
			cs = db.prepareCall("CALL scaha.getScoreboardShotTotals(?,?)");
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Score score = new Score();
					score.setTeamname(rs.getString("teamname"));
					score.setPeriod1goals(rs.getInt("period1goals"));
					score.setPeriod1shots(rs.getInt("period1shots"));
					score.setPeriod2goals(rs.getInt("period2goals"));
					score.setPeriod2shots(rs.getInt("period2shots"));
					score.setPeriod3goals(rs.getInt("period3goals"));
					score.setPeriod3shots(rs.getInt("period3shots"));
					score.setPeriod4shots(rs.getInt("period4shots"));
					score.setPeriodOTgoals(rs.getInt("periodotgoals"));
					score.setTotalshots(rs.getInt("totalshots"));
					score.setTotalgoals(rs.getInt("totalgoals"));
				
					scores.add(score);
				}
				//LOGGER.info("We have selected details for live game id:" + gameid);
			}
			
			rs.close();
			
			//need to load scoring summary for either team by period.
			cs = db.prepareCall("CALL scaha.getScoreboardGameScoringSummary(?,?)");
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					ScoreSummary ss = new ScoreSummary();
					
					ss.setAssist1(rs.getString("assist1"));
					ss.setAssist2(rs.getString("assist2"));
					ss.setAssist1personid(rs.getString("assist1personid"));
					ss.setAssist2personid(rs.getString("assist2personid"));
					ss.setGoalscorer(rs.getString("goalscorer"));
					ss.setGoalscorerpersonid(rs.getString("goalscorerpersonid"));
					ss.setGoaltime(rs.getString("goaltime"));
					ss.setTeamname(rs.getString("teamname"));
					ss.setGoaltype(rs.getString("goaltype"));
					Integer period = rs.getInt("period");
					setSummaryPeriodFlags(period);
					ss.setPeriod(period);
					
					scoresummarys.add(ss);
				}
				//LOGGER.info("We have selected details for live game id:" + gameid);
			}
			rs.close();
			
    		
			//need to load power plays by team
			cs = db.prepareCall("CALL scaha.getScoreboardPowerplays(?,?)");
    		
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					this.setHomeppcount(rs.getString("hometeamppcount"));
					this.setAwayppcount(rs.getString("awayteamppcount"));
					this.setHomeppgoalcount(rs.getString("homeppgoalcount"));
					this.setAwayppgoalcount(rs.getString("awayppgoalcount"));
				}
				//LOGGER.info("We have selected details for live game id:" + gameid);
			}
			rs.close();
			
			//need to load penalty summary by period
    		cs = db.prepareCall("CALL scaha.getScoreboardPenaltySummary(?,?)");
    		
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					Penalty pen = new Penalty();
					Integer period = rs.getInt("period");
					this.setSummaryPeriodPenaltyFlags(period);
					
					pen.setPeriod(period);
					pen.setTeamname(rs.getString("teamname"));
					pen.setPlayername(rs.getString("playername"));
					pen.setPenaltytype(rs.getString("penaltytype"));
					pen.setMinutes(rs.getString("minutes"));
					pen.setTimeofpenalty(rs.getString("penaltytime"));
					
					penalties.add(pen);
				}
				//LOGGER.info("We have selected penalties summary for live game id:" + gameid);
			}
			rs.close();
			
    		//need to load home team player stats
			cs = db.prepareCall("CALL scaha.getScoreboardHometeamstats(?,?)");
    		
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					Stat stat = new Stat();
					stat.setJersey(rs.getString("jerseynumber"));
					stat.setPlayername(rs.getString("playername"));
					stat.setGoals(rs.getString("goals"));
					stat.setAssists(rs.getString("assists"));
					stat.setPoints(rs.getString("points"));
					stat.setPims(rs.getString("pims"));
					
					homestats.add(stat);
				}
				//LOGGER.info("We have selected penalties summary for live game id:" + gameid);
			}
			rs.close();
			
			
    		//need to load home team goalie stats
			cs = db.prepareCall("CALL scaha.getScoreboardHometeamGoaliestats(?,?)");
    		
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					Stat stat = new Stat();
					stat.setJersey(rs.getString("jerseynumber"));
					stat.setPlayername(rs.getString("playername"));
					stat.setShots(rs.getString("shots"));
					stat.setSaves(rs.getString("saves"));
					
					homegoaliestats.add(stat);
				}
				//LOGGER.info("We have selected penalties summary for live game id:" + gameid);
			}
			rs.close();
			
    		//need to load away team player stats
			cs = db.prepareCall("CALL scaha.getScoreboardAwayteamstats(?,?)");
    		
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					Stat stat = new Stat();
					stat.setJersey(rs.getString("jerseynumber"));
					stat.setPlayername(rs.getString("playername"));
					stat.setGoals(rs.getString("goals"));
					stat.setAssists(rs.getString("assists"));
					stat.setPoints(rs.getString("points"));
					stat.setPims(rs.getString("pims"));
					
					awaystats.add(stat);
				}
				//LOGGER.info("We have selected penalties summary for live game id:" + gameid);
			}
			rs.close();
			
    		//need to load away team goalie stats
			cs = db.prepareCall("CALL scaha.getScoreboardAwayteamGoaliestats(?,?)");
    		
    		cs.setInt("in_livegameid", Integer.parseInt(gameid));
    		cs.setString("in_selectedseason", this.selectedseason);
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					Stat stat = new Stat();
					stat.setJersey(rs.getString("jerseynumber"));
					stat.setPlayername(rs.getString("playername"));
					stat.setShots(rs.getString("shots"));
					stat.setSaves(rs.getString("saves"));
					
					awaygoaliestats.add(stat);
				}
				//LOGGER.info("We have selected penalties summary for live game id:" + gameid);
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
		
		
		setGamescores(scores);
		setSummaryscoring(scoresummarys);
		setGamepenalties(penalties);
		setGamehomestats(homestats);
		setGamehomegoaliestats(homegoaliestats);
		setGameawaystats(awaystats);
		setGameawaygoaliestats(awaygoaliestats);
	}
	
	public void setSummaryPeriodFlags(Integer period){
		if (period==1){
			this.setNoperiod1goals(false);
		}
		
		if (period==2){
			this.setNoperiod2goals(false);
		}
		
		if (period==3){
			this.setNoperiod3goals(false);
		}
		
		if (period==4){
			this.setNoperiod4goals(false);
		}
	}
	
	public void setSummaryPeriodPenaltyFlags(Integer period){
		if (period==1){
			this.setNoperiod1penalties(false);
		}
		
		if (period==2){
			this.setNoperiod2penalties(false);
		}
		
		if (period==3){
			this.setNoperiod3penalties(false);
		}
		
		if (period==4){
			this.setNoperiod4penalties(false);
		}
	}
}

