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
public class playerdetailBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<Stat> seasonstats;
	private List<Stat> currentseasonstats;
	
	
	//bean level properties used by multiple methods
	
	
	//variables for the box score page
	private Integer awayclubid = null;
	private Integer personid = null;
	private String teamname = null;
	private String playername = null;
	private String jerseynumber = null;
	
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{clubBean}")
    private ClubBean clubbean;
	
	@ManagedProperty(value="#{scoreboardBean}")
    private ScoreboardBean scoreboard;
	
	@PostConstruct
    public void init() {
		//set default flags for hiding some data
		
		//will need to load game detail
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("id") != null)
        {
    		this.personid = Integer.parseInt(hsr.getParameter("id"));
        }
		
    	
    	    	
		this.loadBoxscore(personid);
    	
    }
	
    public playerdetailBean() {  
        
    }  
    
   public List<Stat> getSeasonstats(){
	   return this.seasonstats;
   }
    
   public void setSeasonstats(List<Stat> value){
	   this.seasonstats = value;
   }
   
   public List<Stat> getCurrentseasonstats(){
	   return this.currentseasonstats;
   }
    
   public void setCurrentseasonstats(List<Stat> value){
	   this.currentseasonstats = value;
   }
   
    public String getPlayername(){
    	return playername;
    }
    
    public void setPlayername(String value){
    	playername=value;
    }
    
    
    public String getTeamname(){
    	return teamname;
    }
    
    public void setTeamname(String value){
    	teamname=value;
    }
    
    public String getJerseynumber(){
    	return jerseynumber;
    }
    
    public void setJerseynumber(String value){
    	jerseynumber=value;
    }
    
    public void setAwayclubid(Integer value){
    	awayclubid = value;
    }
    
    public Integer getAwayclubid(){
    	return awayclubid;
    }
    
    public ScahaBean getScaha() {
		return scaha;
	}

	/**
	 * @param club the scaha to set
	 */
	public void setClubbean(ClubBean club) {
		this.clubbean = club;
	}
    
	public ClubBean getClubbean() {
		return this.clubbean;
	}

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
	
	
    
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
   // TODO: error		context.getExternalContext().redirect("gamecentral.xhtml?selecteddate=" + this.selectedgame + "&schedule=" + this.selectedschedule + "&season=" + this.selectedseason);
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    
	
	//this method loads up the objects for the box score page
	public void loadBoxscore(Integer gameid){
		List<Stat> tempseasonstats = new ArrayList<Stat>();
		List<Stat> tempcurrentstats = new ArrayList<Stat>();
		
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get players name, current team and club id
    		CallableStatement cs = db.prepareCall("CALL scaha.getPlayerDetail(?)");
    		
    		cs.setInt("in_personid", gameid);
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					clubbean.setClubid(rs.getInt("awayclubid"));
					this.teamname=rs.getString("teamname");
					this.playername=rs.getString("playername");
					this.jerseynumber=rs.getString("jerseynumber");
				}
				//LOGGER.info("We have selected details for person id:" + gameid);
			}
			rs.close();
			
			//need to load list of seasons stats
			cs = db.prepareCall("CALL scaha.getPlayersStatsSeasonList(?)");
    		cs.setInt("in_personid", gameid);
    		rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Stat score = new Stat();
					score.setTeamname(rs.getString("teamname"));
					score.setJersey(rs.getString("season"));
					score.setGp(rs.getString("gp"));
					score.setGoals(rs.getString("g"));
					score.setAssists(rs.getString("a"));
					score.setPoints(rs.getString("pts"));
					score.setPims(rs.getString("pims"));
					score.setPpg(rs.getString("ppg"));
					score.setPpa(rs.getString("ppa"));
					score.setShg(rs.getString("shg"));
					score.setSha(rs.getString("sha"));
					score.setGwg(rs.getString("gwg"));
					tempseasonstats.add(score);
				}
				//LOGGER.info("We have selected season history for person id:" + gameid);
			}
			
			rs.close();
			
			//need to load scoring by game for person.
			cs = db.prepareCall("CALL scaha.getScoringforCurrentSeason(?)");
    		cs.setInt("in_personid", gameid);
    		
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Stat score2 = new Stat();
					score2.setPlayername(rs.getString("gamedate"));
					score2.setTeamname(rs.getString("opponent"));
					score2.setJersey(rs.getString("result"));
					score2.setGoals(rs.getString("g"));
					score2.setAssists(rs.getString("a"));
					score2.setPoints(rs.getString("pts"));
					score2.setPims(rs.getString("pims"));
					score2.setPpg(rs.getString("ppg"));
					score2.setPpa(rs.getString("ppa"));
					score2.setShg(rs.getString("shg"));
					score2.setSha(rs.getString("sha"));
					//score2.setGwg(rs.getString("gwg"));
							
					tempcurrentstats.add(score2);
				}
				//LOGGER.info("We have selected game by game scoring for person id:" + gameid);
			}
			rs.close();
			cs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN retrieving selected game details for personid" + gameid);
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
		
		this.setSeasonstats(tempseasonstats);
		this.setCurrentseasonstats(tempcurrentstats);
		
	}
	
	
}

