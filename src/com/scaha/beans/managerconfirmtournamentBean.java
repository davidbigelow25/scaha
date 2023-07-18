package com.scaha.beans;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
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


public class managerconfirmtournamentBean implements Serializable {

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
	private List<Tournament> tournaments = null;

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
	private String currentseason = null;
	private String todaysdate = null;
	private Boolean disableClose = null;

	//datamodels for all of the lists on the page
	private TournamentDataModel TournamentDataModel = null;

    //properties for storing the selected row of each of the datatables
    private Tournament selectedtournament = null;

	@PostConstruct
    public void init() {
		//set as default
        idclub = 0;

        //this.setProfid(pb.getProfile().ID);
        this.setPb(pb);
        getClubID();
        //isClubHighSchool();
    	setTodaysDate();
    	//this.setManagerteams(pb.getProfile().getManagerteams());

    	//lets see if we have a team id passed for the case when multiple teams are managed by same person.
    	//if not we use the team associated with the manager
    	HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

		this.teamid=0;
    	if(hsr.getParameter("id") != null)
        {
    		this.teamid = Integer.parseInt(hsr.getParameter("id").toString());
        }

		//need to get the current season for display
        //this.setCurrentseason(pb.getCurrentSCAHAHockeySeason());


        //Load Tournament and Games
        LoadTournaments();


	}

    public managerconfirmtournamentBean() {
        
    }


	public void setIsmite(Boolean value){
		this.ismite = value;
	}

	public Boolean getIsmite(){
		return this.ismite;
	}

	public String getCurrentseason() {
		return currentseason;
	}

	public void setCurrentseason(String currentseason) {
		this.currentseason = currentseason;
	}

	public String getTodaysdate(){
    	return todaysdate;
    }
    
    public void setTodaysdate(String tdate){
    	todaysdate=tdate;
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
    
    public void setSelectedtournament(Tournament selectedGame){
		selectedtournament = selectedGame;
	}

	public List<com.scaha.objects.Team> getManagerteams() {
		return managerteams;
	}

	public void setManagerteams(List<com.scaha.objects.Team> managerteams) {
		this.managerteams = managerteams;
	}

	public Boolean getDisableClose() {
		return disableClose;
	}

	public void setDisableClose(Boolean disableClose) {
		this.disableClose = disableClose;
	}

	public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("managerportal.xhtml");
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


	public void LoadTournaments() {
		List<Tournament> templist = new ArrayList<Tournament>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	this.disableClose = true;
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getTournamentsForManagerConfirm(?)");
			cs.setInt("inteamid", this.teamid);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer tournamentweekendid = rs.getInt("idtournamentweekend");
					Integer teamtournamentnotificationid = rs.getInt("idteamtournamentnotification");
    				String tournamentweekendname = rs.getString("tournamentweekendname");
					String tournamentname = rs.getString("tournamentname");
    				String startdate = rs.getString("startdate");
    				String enddate = rs.getString("enddate");
    				String sanction = rs.getString("sanction");
					Integer isparticipating = rs.getInt("isparticipating");
					String status = rs.getString("status");
					String levelplayed = rs.getString("levelplayed");
					this.teamname = rs.getString("teamname");

					if (teamtournamentnotificationid > 0 && this.disableClose==true){
						this.disableClose=false;
					}

					Tournament tournament = new Tournament();
    				tournament.setIdtournament(this.teamid);
    				tournament.setTournamentweekendname(tournamentweekendname);
					tournament.setTournamentweekendid(tournamentweekendid);
					tournament.setTournamentname(tournamentname);
					tournament.setStartdate(startdate);
    				tournament.setEnddate(enddate);
    				tournament.setIsparticipating(isparticipating);
					tournament.setLocation(sanction);
					tournament.setStatus(status);
					tournament.setLevelplayed(levelplayed);
					tournament.setTeamtournamentnotificationid(teamtournamentnotificationid);

    				templist.add(tournament);
				}
				LOGGER.info("We have results for tourney confirm list by team:" + this.teamid);
			}
			
			
			rs.close();
			db.cleanup();
    		

    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting tournament confirm list for team" + this.teamid);
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
	
	public void updateTournamentweekend(Tournament selectedtournament){
		//need to set to void
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("remove tournament game from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.UpdateTournamentWeekend(?,?,?,?,?,?,?,?,?,?)");
				cs.setInt("intournamentweekendid", selectedtournament.getTournamentweekendid());
				cs.setInt("inteamid", selectedtournament.getIdtournament());
				cs.setString("intournamentname", selectedtournament.getTournamentname());
				cs.setInt("inteamtournamentnotificationid", selectedtournament.getTeamtournamentnotificationid());
				cs.setString("instartdate", selectedtournament.getStartdate());
				cs.setString("inenddate", selectedtournament.getEnddate());
				cs.setInt("inisparticipating", selectedtournament.getIsparticipating());
				cs.setString("insanction", selectedtournament.getSanction());
				if (selectedtournament.getStatus().equals("Approved")) {
					cs.setString("instatus", "1");
				} else {
					cs.setString("instatus", "0");
				}
				cs.setString("inlevelplayed", selectedtournament.getLevelplayed());

    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				

			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Updating tournament weekend");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		//now we need to reload the data object for the tourney list and the alerts
		LoadTournaments();
	}

	private void setTodaysDate(){
		//need to set todays date for email
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		Date date = new Date();
		this.setTodaysdate(dateFormat.format(date).toString());
		
	}

	public void addScoresheets(Tournament tourney){

		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("manageraddscoresheetsfortournament.xhtml?id=" + tourney.getTeamtournamentnotificationid() + "&amp;teamid=" + this.teamid);
		} catch (Exception e){
			e.printStackTrace();
		}

	}

}
