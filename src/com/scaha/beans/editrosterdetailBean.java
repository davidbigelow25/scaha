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
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.Team;

//import com.gbli.common.SendMailSSL;


public class editrosterdetailBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private Integer rosterid = null;
	private String teamid = null;
	private String playername = null;
	private String eligibility = null;
	private String loidate = null;
	private String dropdate = null;
	private String selectedrelation = null;
	private String active = null;
	private String previousactive = null;
	private String currentteam = null;
	private Boolean newteamentry = false;
	private String neweligibility = null;
	private String newdropdate = null;
	private String newselectedrelation = null;
	private String newactive = null;
	private List<Team> teams = null;
	private String selectedteam = null;
	private Integer personid = null;

	
	@PostConstruct
    public void init() {
        // In @PostConstruct (will be invoked immediately after construction and dependency/property injection).
		//will need to load player profile information for displaying loi
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	if(hsr.getParameter("playerid") != null)
        {
    		this.rosterid = Integer.parseInt(hsr.getParameter("playerid").toString());
        }
    	if(hsr.getParameter("teamid") != null)
        {
    		this.teamid = hsr.getParameter("teamid").toString();
        }
    	setNewteamentry(false);
    	getRosterDetail();
    	getListofTeams();
    }
	
    public editrosterdetailBean() {  
    	
    }  
    
    public void setLoidate(String date){
    	loidate=date;
    }
    
    public String getLoidate(){
    	return loidate;
    }
    
    public void setSelectedteam(String date){
    	selectedteam=date;
    }
    
    public String getSelectedteam(){
    	return selectedteam;
    }
    
    public void setNewactive(String date){
    	newactive=date;
    }
    
    public String getNewactive(){
    	return newactive;
    }
    
    public void setNewselectedrelation(String date){
    	newselectedrelation=date;
    }
    
    public String getNewselectedrelation(){
    	return newselectedrelation;
    }
    
    public void setNewdropdate(String date){
    	newdropdate=date;
    }
    
    public String getNewdropdate(){
    	return newdropdate;
    }
    
    public void setNeweligibility(String date){
    	neweligibility=date;
    }
    
    public String getNeweligibility(){
    	return neweligibility;
    }
    
    
    public Boolean getNewteamentry(){
    	return newteamentry;
    }
    
    public void setNewteamentry(Boolean in){
    	newteamentry=in;
    }
    
    public String getCurrentteam(){
    	return currentteam;
    }
    
    public void setCurrentteam(String date){
    	currentteam=date;
    }
    
    
    public String getTeamid(){
    	return teamid;
    }
    
    public void setTeamid(String date){
    	teamid=date;
    }
    
    public String getPreviousactive(){
    	return previousactive;
    }
    
    public void setPreviousactive(String date){
    	previousactive=date;
    }
        
    public String getActive(){
    	return active;
    }
    
    public void setActive(String date){
    	active=date;
    }
    
    public String getSelectedrelation(){
    	return selectedrelation;
    }
    
    public void setSelectedrelation(String date){
    	selectedrelation=date;
    }
    
    
    public String getDropdate(){
    	return dropdate;
    }
    
    public void setDropdate(String date){
    	dropdate=date;
    }
    
    public String getEligibility(){
    	return eligibility;
    }
    
    public void setEligibility(String eligible){
    	eligibility=eligible;
    }
    
    public String getPlayername(){
    	return playername;
    }
    
    public void setPlayername(String name){
    	playername=name;
    }
    
    public Integer getRosterid(){
    	return rosterid;
    }
    
    public void setRosterid(Integer id){
    	rosterid=id;
    }
    
    public Integer getPersonid(){
    	return personid;
    }
    
    public void setPersonid(Integer id){
    	personid=id;
    }
    
    public List<Team> getListofTeams(){
		List<Team> templist = new ArrayList<Team>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			CallableStatement cs = db.prepareCall("CALL scaha.getTeamsByCurrentYear()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				//need to add to an array
    				//rs = db.getResultSet();
    				
    				while (rs.next()) {
    					String idteam = rs.getString("idteams");
        				String teamname = rs.getString("teamname");
        				
        				Team team = new Team(teamname,idteam);
        				templist.add(team);
    				}
    				//LOGGER.info("We have results for team list by club");
    			}
    			rs.close();
    			db.cleanup();
    		} else {
    		
    		}
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
		
    	setTeams(templist);
		
		return getTeams();
	}
	
	public List<Team> getTeams(){
		return teams;
	}
	
	public void setTeams(List<Team> list){
		teams = list;
	}
	
    
    public void getRosterDetail(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getRosterDetail(?)");
			cs.setInt("rosterid", this.rosterid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.eligibility = rs.getString("eligibility");
					this.loidate = rs.getString("loidate");
					this.selectedrelation = rs.getString("rostertype");
					this.active = rs.getString("active");
					if (this.active.equals("0")){
						setdropdate();
					}
					this.currentteam = rs.getString("currenteam");
					this.dropdate = rs.getString("dropped");
					this.playername = rs.getString("playername");
					this.personid = rs.getInt("idperson");
				}
				//LOGGER.info("We have results for roster detail");
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
		
    }
    
    public void updateRoster(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		
    		if (db.setAutoCommit(false)) {
	    		//first get team name
	    		CallableStatement cs = db.prepareCall("CALL scaha.updateRosterDetail(?,?,?,?,?)");
				cs.setInt("rosterid", this.rosterid);
				cs.setString("seligible", this.eligibility);
				if (this.dropdate.equals("")){
					cs.setString("dropdate", "12/31/2099");
				} else {
					cs.setString("dropdate", this.dropdate);
				}
				cs.setInt("active", Integer.parseInt(this.active));
				cs.setString("rstrtype", this.selectedrelation);
				
			    cs.executeQuery();
				
			    if (this.active.equals("0") && !this.selectedteam.equals("0")){
			    	cs = db.prepareCall("CALL scaha.addRosterDetail(?,?,?,?,?,?)");
					cs.setInt("teamid", Integer.parseInt(this.selectedteam));
					cs.setString("seligible", this.neweligibility);
					if (this.newdropdate.equals("")){
						cs.setString("dropdate", "12/31/2099");
					} else {
						cs.setString("dropdate", this.newdropdate);
					}
					cs.setInt("active", Integer.parseInt(this.newactive));
					cs.setString("rstrtype", this.newselectedrelation);
					cs.setInt("personid", this.personid);
					
				    cs.executeQuery();
				}
			    
			    db.commit();
				db.cleanup();
	    		
				FacesContext context = FacesContext.getCurrentInstance();
				try{
					context.getExternalContext().redirect("editroster.xhtml?teamid=" + teamid);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		} else {
    			
    		}
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
		
    }
    
	public void editrosterdetail(Player splayer){
		String idplayer = splayer.getIdplayer();
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editrosterdetail.xhtml?playerid=" + idplayer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setdropdate(){
		if (this.active.equals("0")){
			//need to set drop date
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date date = new Date();
			this.setDropdate(dateFormat.format(date));
			this.setNeweligibility(dateFormat.format(date));
			setNewteamentry(true);
			setNewactive("1");
			setNewselectedrelation(this.selectedrelation);
		} else {
			//need to clear drop date
			this.setDropdate("");
		}
	}
	
	public void closeurl(){
    	FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editroster.xhtml?teamid=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}

