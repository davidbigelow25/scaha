package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
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

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Division;
import com.scaha.objects.GeneralSeason;
import com.scaha.objects.GeneralSeasonList;
import com.scaha.objects.Schedule;
import com.scaha.objects.ScheduleList;
import com.scaha.objects.Stat;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class statsBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;

	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	
	transient private ResultSet rs = null;
	//list for divisions to select from
	private List<Division> divisions = null;
	private GeneralSeasonList seasons = null;
	
	//list of leader groups to display
	private List<Stat> playergoals = null;
	private List<Stat> playerpoints = null;
	private List<Stat> playerassists = null;
	private List<Stat> playergaa = null;
	private List<Stat> playersavepercentage = null;
	private List<Stat> playerwins = null;
	private List<Stat> completeplayers = null;
	private List<Stat> completegoalies = null;
	
	//value of the selected division and year drop downs
	private String selecteddivision = null;
	private String selectedyear = null;
	private String selectedgametype = null;
	private String selectedcount = null;
	private String displaycompletetitle = null;
	private GeneralSeason selectedseason;
	private int selectedseasonid;
	private int selectedscheduleid;
	private List<Schedule> schedulelist =  null;
	private ScheduleList schedules;
	private Schedule selectedschedule;
	private Boolean isplayers = true;
	private Boolean isgoalies = false;
	private String playerbuttoncolor = "info";
	private String goaliebuttoncolor = "";
	
    @PostConstruct
    public void init() {
	    this.selectedcount="1000";
	    this.selectedgametype="2";
    	this.selecteddivision="0";
    	this.selectedyear=scaha.getScahaSeasonList().getCurrentSeason().getFromDate().substring(0,4);
    			
    	//will need to load division stat information
      	HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
          	
        if(hsr.getParameter("div") != null)
        {
    		this.selecteddivision = hsr.getParameter("div").toString();
        }
        if(hsr.getParameter("year") != null)
        {
    		this.selectedyear = hsr.getParameter("year").toString();
        }
        if(hsr.getParameter("gtype") != null)
        {
    		this.selectedgametype = hsr.getParameter("gtype").toString();
        }
    
        
    	
    	//load divisions to select from
        this.setSeasons(scaha.getScahaSeasonList());
        this.selectedseason = scaha.getScahaSeasonList().getCurrentSeason();
        this.selectedseasonid = selectedseason.ID;
		this.refreshScheduleList();
    	
		
		
        //Load leaders
        loadLeaders();
        
        
	}
	
    public statsBean() {  
        
    }  
    
    public String getDisplaycompletetitle(){
    	return displaycompletetitle;
    }
    
    public void setDisplaycompletetitle(String value){
    	displaycompletetitle=value;
    }
    
    public List<Stat> getPlayergoals(){
    	return playergoals;
    }
    
    public void setPlayergoals(List<Stat> list){
    	playergoals = list;
    }
	
    public List<Stat> getPlayerpoints(){
    	return playerpoints;
    }
    
    public void setPlayerpoints(List<Stat> list){
    	playerpoints = list;
    }
	
    public List<Stat> getPlayerassists(){
    	return playerassists;
    }
    
    public void setPlayerassists(List<Stat> list){
    	playerassists = list;
    }
    
    public List<Stat> getPlayergaa(){
    	return playergaa;
    }
    
    public void setPlayergaa(List<Stat> list){
    	playergaa = list;
    }
	
    public List<Stat> getPlayersavepercentage(){
    	return playersavepercentage;
    }
    
    public void setPlayersavepercentage(List<Stat> list){
    	playersavepercentage = list;
    }
	
    public List<Stat> getPlayerwins(){
    	return playerwins;
    }
    
    public void setPlayerwins(List<Stat> list){
    	playerwins = list;
    }
	
    public List<Stat> getCompleteplayers(){
    	return completeplayers;
    }
    
    public void setCompleteplayers(List<Stat> list){
    	completeplayers = list;
    }
	
    public List<Stat> getCompletegoalies(){
    	return completegoalies;
    }
    
    public void setCompletegoalies(List<Stat> list){
    	completegoalies = list;
    }
	
    //retrieves list of existing teams for the club
    public void loadLeaders(){
    	List<Stat> tempresult = new ArrayList<Stat>();
    	List<Stat> tempgoals = new ArrayList<Stat>();
    	List<Stat> tempassists = new ArrayList<Stat>();
    	List<Stat> tempgaa = new ArrayList<Stat>();
    	List<Stat> tempsave = new ArrayList<Stat>();
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get top 5 goal scorers
    		CallableStatement cs = db.prepareCall("CALL scaha.getGoalleaders(?,?)");
    		cs.setInt("division", Integer.parseInt(this.selecteddivision));
    		cs.setInt("inyear", Integer.parseInt(this.selectedyear));
			rs = cs.executeQuery();
			Integer count = 1;
			
			if (rs != null){
				
				while (rs.next()) {
					Integer rank = count++;
    				String playername = rs.getString("playername");
    				String teamname = rs.getString("teamname");
    				String goals = rs.getString("goals");
    				
    				Stat stat = new Stat();
    				stat.setRank(rank.toString());
    				stat.setPlayername(playername);
    				stat.setTeamname(teamname);
    				stat.setGoals(goals);
    				tempgoals.add(stat);
    				
				}
				//LOGGER.info("We have results for goal leaders:");
				rs.close();
			}
	
			
			cs.close();
			this.setPlayergoals(tempgoals);
			
			
			//get points leaders
			cs = db.prepareCall("CALL scaha.getPointsleaders(?,?)");
			cs.setInt("division", Integer.parseInt(this.selecteddivision));
			cs.setInt("inyear", Integer.parseInt(this.selectedyear));
			rs = cs.executeQuery();
			count = 1;
			
			if (rs != null){
				
				while (rs.next()) {
					Integer rank = count++;
    				String playername = rs.getString("playername");
    				String teamname = rs.getString("teamname");
    				String points = rs.getString("points");
    				
    				Stat stat = new Stat();
    				stat.setRank(rank.toString());
    				stat.setPlayername(playername);
    				stat.setTeamname(teamname);
    				stat.setPoints(points);
    				tempresult.add(stat);
    				
				}
				//LOGGER.info("We have results for leaders:");
				rs.close();
			}
			
			cs.close();
			
			this.setPlayerpoints(tempresult);
			
			//get assists leaders
			cs = db.prepareCall("CALL scaha.getAssistsleaders(?,?)");
			cs.setInt("division", Integer.parseInt(this.selecteddivision));
			cs.setInt("inyear", Integer.parseInt(this.selectedyear));
			rs = cs.executeQuery();
			count = 1;
			
			if (rs != null){
				
				while (rs.next()) {
					Integer rank = count++;
    				String playername = rs.getString("playername");
    				String teamname = rs.getString("teamname");
    				String assists = rs.getString("assists");
    				
    				Stat stat = new Stat();
    				stat.setRank(rank.toString());
    				stat.setPlayername(playername);
    				stat.setTeamname(teamname);
    				stat.setAssists(assists);
    				tempassists.add(stat);
    				
				}
				//LOGGER.info("We have results for leaders:");
				rs.close();
			}
			
			cs.close();
			this.setPlayerassists(tempassists);
			
			
			//get gaa leaders
			cs = db.prepareCall("CALL scaha.getGaaleaders(?,?)");
			cs.setInt("division", Integer.parseInt(this.selecteddivision));
			cs.setInt("inyear", Integer.parseInt(this.selectedyear));
			rs = cs.executeQuery();
			count = 1;
			
			if (rs != null){
				
				while (rs.next()) {
					Integer rank = count++;
    				String playername = rs.getString("playername");
    				String teamname = rs.getString("teamname");
    				String gaa = rs.getString("gaa");
    				if (gaa==null){
    					gaa = "0.00";
    				}
    				Stat stat = new Stat();
    				stat.setRank(rank.toString());
    				stat.setPlayername(playername);
    				stat.setTeamname(teamname);
    				stat.setGaa(gaa);
    				tempgaa.add(stat);
    				
				}
				//LOGGER.info("We have results for leaders:");
				
				rs.close();
			}
			
			cs.close();
			this.setPlayergaa(tempgaa);
			
			//get sv % leaders
			cs = db.prepareCall("CALL scaha.getSavepercentageleaders(?,?)");
			cs.setInt("division", Integer.parseInt(this.selecteddivision));
			cs.setInt("inyear", Integer.parseInt(this.selectedyear));
			rs = cs.executeQuery();
			count = 1;
			
			if (rs != null){
				
				while (rs.next()) {
					Integer rank = count++;
    				String playername = rs.getString("playername");
    				String teamname = rs.getString("teamname");
    				String savepercentage = rs.getString("savepercentage");
    				
    				Stat stat = new Stat();
    				stat.setRank(rank.toString());
    				stat.setPlayername(playername);
    				stat.setTeamname(teamname);
    				stat.setSavepercentage(savepercentage);
    				tempsave.add(stat);
    				
				}
				//LOGGER.info("We have results for leaders:");
				rs.close();
			}

			cs.close();
			this.setPlayersavepercentage(tempsave);
			
    		
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting leaders list");
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	
    }

    public List<Division> getListofDivisions(){
		List<Division> templist = new ArrayList<Division>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			CallableStatement cs = db.prepareCall("CALL scaha.getStatsDivisions()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					Integer iddivision = rs.getInt("iddivisions");
        				String divisionname = rs.getString("division_name");
        				
        				Division division = new Division();
        				division.setIddivision(iddivision);
        				division.setDivisionname(divisionname);
        				templist.add(division);
    				}
    				//LOGGER.info("We have results for division list");
    			}
    			rs.close();
    			cs.close();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading clubs");
    		e.printStackTrace();
    		db.rollback();
			db.free();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setDivisions(templist);
		
		return getDivisions();
	}
    
    public List<Division> getDivisions(){
		return divisions;
	}
	
	public void setDivisions(List<Division> list){
		divisions = list;
	}

	public String getSelectedgametype(){
		return selectedgametype;
	}
	
	public void setSelectedgametype(String value){
		selectedgametype=value;
	}
	
	public String getSelectedcount(){
		return selectedcount;
	}
	
	public void setSelectedcount(String value){
		selectedcount=value;
	}
	
	
	public String getSelecteddivision(){
		return selecteddivision;
	}
	
	public void setSelecteddivision(String value){
		selecteddivision=value;
	}
	
	public String getSelectedyear(){
		return selectedyear;
	}
	
	public void setSelectedyear(String value){
		selectedyear=value;
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
	
	public ProfileBean getPb() {
		return pb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setPb(ProfileBean pb) {
		this.pb = pb;
	}
	
	public void loadCompletePlayerStats() throws Exception{
		List<Stat> tempsave = new ArrayList<Stat>();
		//setDisplaycompletetitle(sortby);
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date   date       = format.parse (this.selectedschedule.getStartdate());
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Integer tYear = cal.get(Calendar.YEAR);;
		this.selectedyear = tYear.toString();
		
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		CallableStatement cs = db.prepareCall("CALL scaha.getAllPlayerStats(?,?,?,?,?)");
    		cs.setInt("division", this.selectedscheduleid);
    		cs.setInt("inyear", Integer.parseInt(this.selectedyear));
    		cs.setString("sorting", "Points");
    		cs.setInt("ingametype", 0);
    		//cs.setInt("count", Integer.parseInt(this.selectedcount));
    		cs.setInt("count", 1000);
			rs = cs.executeQuery();
			Integer count = 1;
			
			if (rs != null){
				
				while (rs.next()) {
					Integer rank = count++;
    				String playername = rs.getString("playername");
    				String teamname = rs.getString("teamname");
    				String goals = rs.getString("goals");
    				String assists = rs.getString("assists");
    				String points = rs.getString("points");
    				String pims = rs.getString("pims");
    				if (pims==null){
    					pims = "0";
    				}
    				
    				String gp=rs.getString("gp");
    				
    				Stat stat = new Stat();
    				stat.setRank(rank.toString());
    				stat.setPlayername(playername);
    				stat.setTeamname(teamname);
    				stat.setGoals(goals);
    				stat.setAssists(assists);
    				stat.setPoints(points);
    				stat.setPims(pims);
    				stat.setGp(gp);
    				tempsave.add(stat);
    				
				}
				//LOGGER.info("We have results for goal leaders:");
			} else {
	    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading clubs");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
			
    	this.setCompleteplayers(tempsave);
		
    	
    }
	
	
	public void loadCompleteGoalieStats() throws ParseException{
		List<Stat> tempsave = new ArrayList<Stat>();
    	
		
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date   date       = format.parse (this.selectedschedule.getStartdate());
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		Integer tYear = cal.get(Calendar.YEAR);;
		this.selectedyear = tYear.toString();
		
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get top 5 goal scorers
    		CallableStatement cs = db.prepareCall("CALL scaha.getGoaliestats(?,?,?,?,?)");
    		cs.setInt("division", this.selectedscheduleid);
    		cs.setInt("inyear", Integer.parseInt(this.selectedyear));
    		cs.setString("sorting", "savepercentage");
    		cs.setInt("ingametype", Integer.parseInt(this.selectedgametype));
    		cs.setInt("count", Integer.parseInt(this.selectedcount));
			
    		rs = cs.executeQuery();
			Integer count = 1;
			
			if (rs != null){
				
				while (rs.next()) {
					Integer rank = count++;
    				String playername = rs.getString("playername");
    				String teamname = rs.getString("teamname");
    				String mins = rs.getString("mins");
    				if (mins==null){
    					mins = "0";
    				}
    				
    				String shots = rs.getString("shots");
    				if (shots==null){
    					shots = "0";
    				}
    				
    				String saves = rs.getString("saves");
    				if (saves==null){
    					saves = "0";
    				}
    				
    				String percentage = rs.getString("percentage");
    				if (percentage==null){
    					percentage = "0.000";
    				}
    				
    				String gaa=rs.getString("gaa");
    				if (gaa==null){
    					gaa = "0.00";
    				}
    				
    				String gp=rs.getString("gp");
    				if (gp==null){
    					gp = "0";
    				}
    				
    				
    				Stat stat = new Stat();
    				stat.setRank(rank.toString());
    				stat.setPlayername(playername);
    				stat.setTeamname(teamname);
    				stat.setMins(mins);
    				stat.setShots(shots);
    				stat.setSaves(saves);
    				stat.setSavepercentage(percentage);
    				stat.setGp(gp);
    				stat.setGaa(gaa);
    				tempsave.add(stat);
    				
				}
				//LOGGER.info("We have results for goal leaders:");
			} else {
	    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading clubs");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	//LazyStatDataModel temp = new LazyStatDataModel(tempsave); 
    	this.setCompletegoalies(tempsave);
		
    }
	
	public void gotoStatsDetail(String sortby){
		
		FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("statsdetail.xhtml?div=" + this.selecteddivision + "&year=" + this.selectedyear + "&sortby=" + sortby + "&gtype=" + this.selectedgametype + "&b=p");
    	} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	public void gotoGoalieStatsDetail(String sortby){
		
		FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("statsdetail.xhtml?div=" + this.selecteddivision + "&year=" + this.selectedyear + "&sortby=" + sortby + "&gtype=" + this.selectedgametype + "&b=g");
    	} catch (Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the selectedseason
	 */
	public GeneralSeason getSelectedseason() {
		return selectedseason;
	}

	/**
	 * @param selectedseason the selectedseason to set
	 */
	public void setSelectedseason(GeneralSeason selectedseason) {
		this.selectedseason = selectedseason;
	}

	/**
	 * @return the selectedseasonid
	 */
	public int getSelectedseasonid() {
		return selectedseasonid;
	}

	/**
	 * @param selectedseasonid the selectedseasonid to set
	 */
	public void setSelectedseasonid(int selectedseasonid) {
		this.selectedseasonid = selectedseasonid;
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneralSeason> getSeasonlist() {
		return (List<GeneralSeason>)seasons.getWrappedData();
	}
	
	public GeneralSeasonList getSeasons() {
		return seasons;
	}
	
	public void setSeasons(GeneralSeasonList seasons) {
		this.seasons = seasons;
		//LOGGER.info("Here is our General Season:");
		//LOGGER.info(this.seasons.toString());
	}
	
	public void onSeasonChange() {
		//LOGGER.info("season change request detected new id is:" + this.selectedseasonid);
		this.selectedseason = this.seasons.getGeneralSeason(this.selectedseasonid);
		refreshScheduleList();
	}
	
	
	/**
	 * @param schedulelist the schedulelist to set
	 */
	public void setSchedulelist(List<Schedule> schedulelist) {
		this.schedulelist = schedulelist;
	}
	
	public int getSelectedscheduleid() {
		return selectedscheduleid;
	}

	/**
	 * @param selectedscheduleid the selectedscheduleid to set
	 */
	public void setSelectedscheduleid(int selectedscheduleid) {
		this.selectedscheduleid = selectedscheduleid;
	}
	
	public void refreshScheduleList() {
		
		//
		// ok.. lets do the schedules now..
		//
		//LOGGER.info("Refreshing Schedule List for season:" + this.selectedseason);
		
		this.schedulelist = null;	
		this.schedules = null;
		if (this.selectedseason != null) {

			if(this.selectedseason!=scaha.getScahaSeasonList().getCurrentSeason()){
				ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
				try {
					this.schedules = ScheduleList.ListFactory(pb.getProfile(), db,this.selectedseason,scaha.getScahaTeamList());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					db.free();
				}
				
			}else {
				this.schedules = this.selectedseason.getSchedList();
			}
			
			//LOGGER.info("season schedule is: " + schedules);

			this.schedules = this.selectedseason.getSchedList();
//			LOGGER.info("season schedule is: " + schedules);

			if (schedules != null) {
			  if (this.schedules.getRowCount() > 0) {
				  this.schedulelist = this.getScheduleList();
			  }	else {
				//LOGGER.info("Refresh.. zero list.. leaving null:" + this.schedules.getRowCount());
			  } 
			}
		}
        
	}
	
	public List<Schedule> getSchedulelist() {
		return schedulelist;
	}
	
	@SuppressWarnings("unchecked")
	private List<Schedule> getScheduleList() {
		return (List<Schedule>)schedules.getWrappedData();
	}
	
	public void onScheduleChange() {
		
		this.selectedschedule = this.schedules.getSchedule(this.selectedscheduleid);
		//LOGGER.info("schedule change request detected new id is:" + this.selectedscheduleid + ":" + this.selectedschedule);

		try {
			if (this.isplayers){
				loadCompletePlayerStats();
			} else {
				loadCompleteGoalieStats();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			
		}
		
		
	}

	public Schedule getSelectedschedule() {
		return selectedschedule;
	}

	/**
	 * @param selectedschedule the selectedschedule to set
	 */
	public void setSelectedschedule(Schedule selectedschedule) {
		this.selectedschedule = selectedschedule;
	}
	
	public void setIsplayers(Boolean value){
		isplayers = value;
	}
	
	public Boolean getIsplayers(){
		return isplayers;
	}
	
	public void setIsgoalies(Boolean value){
		isgoalies = value;
	}
	
	public Boolean getIsgoalies(){
		return isgoalies;
	}
	
	
	
	public void setPlayerbuttoncolor(String value){
		playerbuttoncolor = value;
	}
	
	public String getPlayerbuttoncolor(){
		return playerbuttoncolor;
	}
	
	public void setGoaliebuttoncolor(String value){
		goaliebuttoncolor = value;
	}
	
	public String getGoaliebuttoncolor(){
		return goaliebuttoncolor;
	}

	public void displayGoalies(){
		this.isplayers=false;
		this.isgoalies=true;
		onScheduleChange();
		this.playerbuttoncolor="info";
		this.goaliebuttoncolor="";
	}
	
	public void displayPlayers(){
		this.isplayers=true;
		this.isgoalies=false;
		onScheduleChange();
		
		this.playerbuttoncolor="";
		this.goaliebuttoncolor="info";
	}
}

