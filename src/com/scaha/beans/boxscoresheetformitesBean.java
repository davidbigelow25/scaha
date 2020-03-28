package com.scaha.beans;

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
import com.scaha.objects.Player;
import com.scaha.objects.Score;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class boxscoresheetformitesBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	transient private ResultSet rs2 = null;
	//lists for generated datamodels
	private Integer team1id;
	private Integer team2id;
	private Integer team3id;
	private Integer team4id;
	private Integer team5id;
	private Integer team6id;
	private Integer team1clubid;
	private Integer team2clubid;
	private Integer team3clubid;
	private Integer team4clubid;
	private Integer team5clubid;
	private Integer team6clubid;
	private String team1name;
	private String team2name;
	private String team3name;
	private String team4name;
	private String team5name;
	private String team6name;
	private String gamedate;
	
	
	
	
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
		    	
		this.loadBoxscore(selectedgame);
    	
    	
		//this.loadBoxscore(gameid);
    	
	}
	
    public boxscoresheetformitesBean() {  
        
    }  
    
    public void setTeam1id(Integer list){
    	this.team1id=list;
    }
    
    public Integer getTeam1id(){
    	return this.team1id;
    }
    
    public void setTeam2id(Integer list){
    	this.team2id=list;
    }
    
    public Integer getTeam2id(){
    	return this.team2id;
    }
    public void setTeam3id(Integer list){
    	this.team3id=list;
    }
    
    public Integer getTeam3id(){
    	return this.team3id;
    }
    
    public void setTeam4id(Integer list){
    	this.team4id=list;
    }
    
    public Integer getTeam4id(){
    	return this.team4id;
    }
    
    public void setTeam5id(Integer list){
    	this.team5id=list;
    }
    
    public Integer getTeam5id(){
    	return this.team5id;
    }
    
    public void setTeam6id(Integer list){
    	this.team6id=list;
    }
    
    public Integer getTeam6id(){
    	return this.team6id;
    }
    
    public void setTeam1clubid(Integer list){
    	this.team1clubid=list;
    }
    
    public Integer getTeam1clubid(){
    	return this.team1clubid;
    }
    
    public void setTeam2clubid(Integer list){
    	this.team2clubid=list;
    }
    
    public Integer getTeam2clubid(){
    	return this.team2clubid;
    }
    
    public void setTeam3clubid(Integer list){
    	this.team3clubid=list;
    }
    
    public Integer getTeam3clubid(){
    	return this.team3clubid;
    }
    
    public void setTeam4clubid(Integer list){
    	this.team4clubid=list;
    }
    
    public Integer getTeam4clubid(){
    	return this.team4clubid;
    }
    
    public void setTeam5clubid(Integer list){
    	this.team5clubid=list;
    }
    
    public Integer getTeam5clubid(){
    	return this.team5clubid;
    }
    
    public void setTeam6clubid(Integer list){
    	this.team6clubid=list;
    }
    
    public Integer getTeam6clubid(){
    	return this.team6clubid;
    }
    
    
    public void setTeam1name(String list){
    	this.team1name=list;
    }
    
    public String getTeam1name(){
    	return this.team1name;
    }
    
    public void setTeam2name(String list){
    	this.team2name=list;
    }
    
    public String getTeam2name(){
    	return this.team2name;
    }
    
    public void setTeam3name(String list){
    	this.team3name=list;
    }
    
    public String getTeam3name(){
    	return this.team3name;
    }
    
    public void setTeam4name(String list){
    	this.team4name=list;
    }
    
    public String getTeam4name(){
    	return this.team4name;
    }
    
    public void setTeam5name(String list){
    	this.team5name=list;
    }
    
    public String getTeam5name(){
    	return this.team5name;
    }
    
    public void setTeam6name(String list){
    	this.team6name=list;
    }
    
    public String getTeam6name(){
    	return this.team6name;
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
			
			//need to load list of teamid's to iterate thru to load the rosters
			cs = db.prepareCall("CALL scaha.getMiteEventTeams(?)");
			cs.setInt("in_livegameid", Integer.parseInt(gameid));
			rs = cs.executeQuery();
			Integer i=0;
			if (rs != null){
				
				while (rs.next()) {
					i++;
			
					switch (i){
						case 1:
							this.setTeam1id(rs.getInt("idteam"));
							this.setTeam1clubid(rs.getInt("idclub"));
							this.setTeam1name(rs.getString("teamname"));
							this.setStartdate(rs.getString("actdate"));
							break;
						case 2:
							this.setTeam2id(rs.getInt("idteam"));
							this.setTeam2clubid(rs.getInt("idclub"));
							this.setTeam2name(rs.getString("teamname"));
							this.setStartdate(rs.getString("actdate"));
							break;
						case 3:
							this.setTeam3id(rs.getInt("idteam"));
							this.setTeam3clubid(rs.getInt("idclub"));
							this.setTeam3name(rs.getString("teamname"));
							this.setStartdate(rs.getString("actdate"));
							break;
						case 4:
							this.setTeam4id(rs.getInt("idteam"));
							this.setTeam4clubid(rs.getInt("idclub"));
							this.setTeam4name(rs.getString("teamname"));
							this.setStartdate(rs.getString("actdate"));
							break;
						case 5:
							this.setTeam5id(rs.getInt("idteam"));
							this.setTeam5clubid(rs.getInt("idclub"));
							this.setTeam5name(rs.getString("teamname"));
							this.setStartdate(rs.getString("actdate"));
							break;
						case 6:
							this.setTeam6id(rs.getInt("idteam"));
							this.setTeam6clubid(rs.getInt("idclub"));
							this.setTeam6name(rs.getString("teamname"));
							this.setStartdate(rs.getString("actdate"));
							break;
					}
					
				}
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
		
	}
		
}

