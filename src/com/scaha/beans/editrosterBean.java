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
import com.scaha.objects.Coach;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.Team;

//import com.gbli.common.SendMailSSL;


public class editrosterBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private List<Player> players = null;
	private List<Coach> coaches = null;
	private Coach selectedcoach = null;
	private Player selectedplayer = null;
	private Integer teamid = null;
	private String teamname = null;
	private String enteredrosterdate = null;
	private String newrosterdate = null;
	
	@PostConstruct
    public void init() {
        // In @PostConstruct (will be invoked immediately after construction and dependency/property injection).
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("teamid") != null)
        {
    		this.teamid = Integer.parseInt(hsr.getParameter("teamid").toString());
        }
    	
    	getRoster();

    }
	
    public editrosterBean() {  
    	    	
		
    }  
    
    public String getNewrosterdate(){
    	return newrosterdate;
    }
    
    public void setNewrosterdate(String name){
    	newrosterdate=name;
    }
    
    
    public String getEnteredrosterdate(){
    	return enteredrosterdate;
    }
    
    public void setEnteredrosterdate(String name){
    	enteredrosterdate=name;
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
    
    public Player getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Player splayer){
		selectedplayer = splayer;
	}
    
    
    public void getRoster(){
		List<Player> templist = new ArrayList<Player>();
		List<Coach> tempcoachlist = new ArrayList<Coach>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getTeamByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.teamname = rs.getString("teamname");
				}
				//LOGGER.info("We have results for team name");
			}
			rs.close();
			db.cleanup();
    		
    		//next get player roster
//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getRosterPlayersByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String playerid = rs.getString("idroster");
					String fname = rs.getString("fname");
					String lname = rs.getString("lname");
					String eligibility = rs.getString("eligibility");
					String loidate = rs.getString("loidate");
					String drop = rs.getString("dropped");
					String added = rs.getString("added");
					String active = rs.getString("active");
					String updated = rs.getString("updated");
					String jerseynumber = rs.getString("jerseynumber");
					String dob = rs.getString("dob");
					String gp = rs.getString("gp");
					
					Player player = new Player();
					player.setIdplayer(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);
					player.setEligibility(loidate);
					player.setRosterdate(eligibility);
					player.setDrop(drop);
					player.setAdded(added);
					player.setActive(active);
					player.setUpdated(updated);
					player.setJerseynumber(jerseynumber);
					player.setDob(dob);
					player.setGp(gp);
					
					templist.add(player);
				}
				//LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();
    		
			//next get coach roster
			//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getRosterCoachesByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String coachid = rs.getString("idroster");
					String cfname = rs.getString("fname");
					String clname = rs.getString("lname");
					String celigibility = rs.getString("eligibility");
					String cloidate = rs.getString("loidate");
					String cdrop = rs.getString("dropped");
					String cadded = rs.getString("added");
					String cactive = rs.getString("active");
					String cupdated = rs.getString("updated");
					String teamrole = rs.getString("teamrole");
					
					
					Coach coach = new Coach();
					coach.setIdcoach(coachid);
					coach.setFirstname(cfname);
					coach.setLastname(clname);
					coach.setEligibility(celigibility);
					coach.setLoidate(cloidate);
					coach.setDrop(cdrop);
					coach.setAdded(cadded);
					coach.setActive(cactive);
					coach.setUpdated(cupdated);
					coach.setTeamrole(teamrole);
					
					tempcoachlist.add(coach);
				}
				//LOGGER.info("We have results for team roster");
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
    	setCoaches(tempcoachlist);
		
	}
    
    public List<Coach> getCoaches(){
		return coaches;
	}
	
	public void setCoaches(List<Coach> list){
		coaches = list;
	}
	
    
    public List<Player> getPlayers(){
		return players;
	}
	
	public void setPlayers(List<Player> list){
		players = list;
	}
	
	public void editrosterdetail(Player splayer){
		String idplayer = splayer.getIdplayer();
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editrosterdetail.xhtml?playerid=" + idplayer + "&teamid=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void editcoachrosterdetail(Coach scoach){
		String idplayer = scoach.getIdcoach();
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editrosterdetail.xhtml?playerid=" + idplayer + "&teamid=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateTeamRostereffectivedate(Team team){
		
		//lets first update the entire active roster with the new effective date aka roster date
		//then lets reload the objects
		this.teamid = team.ID;
		this.enteredrosterdate = team.getNewdate();
		
		
		List<Player> templist = new ArrayList<Player>();
		List<Coach> tempcoachlist = new ArrayList<Coach>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first update team
    		CallableStatement cs = db.prepareCall("CALL scaha.updateTeamrosterdate(?,?)");
			cs.setInt("teamid", this.teamid);
			cs.setString("srosterdate", this.enteredrosterdate);
		    cs.executeQuery();
    		cs.close();
		    
    		//first get team name
    		cs = db.prepareCall("CALL scaha.getTeamByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.teamname = rs.getString("teamname");
				}
				//LOGGER.info("We have results for team name");
			}
			rs.close();
			db.cleanup();
    		
    		//next get player roster
//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getRosterPlayersByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String playerid = rs.getString("idroster");
					String fname = rs.getString("fname");
					String lname = rs.getString("lname");
					String eligibility = rs.getString("eligibility");
					String loidate = rs.getString("loidate");
					String drop = rs.getString("dropped");
					String added = rs.getString("added");
					String active = rs.getString("active");
					String updated = rs.getString("updated");
					String jerseynumber = rs.getString("jerseynumber");
					String dob = rs.getString("dob");
					String gp = rs.getString("gp");
					
					Player player = new Player();
					player.setIdplayer(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);
					player.setEligibility(loidate);
					player.setRosterdate(eligibility);
					player.setDrop(drop);
					player.setAdded(added);
					player.setActive(active);
					player.setUpdated(updated);
					player.setJerseynumber(jerseynumber);
					player.setDob(dob);
					player.setGp(gp);
					
					templist.add(player);
				}
				//LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();
    		
			//next get coach roster
			//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getRosterCoachesByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String coachid = rs.getString("idroster");
					String cfname = rs.getString("fname");
					String clname = rs.getString("lname");
					String celigibility = rs.getString("eligibility");
					String cloidate = rs.getString("loidate");
					String cdrop = rs.getString("dropped");
					String cadded = rs.getString("added");
					String cactive = rs.getString("active");
					String cupdated = rs.getString("updated");
					String teamrole = rs.getString("teamrole");
					
					
					Coach coach = new Coach();
					coach.setIdcoach(coachid);
					coach.setFirstname(cfname);
					coach.setLastname(clname);
					coach.setEligibility(celigibility);
					coach.setLoidate(cloidate);
					coach.setDrop(cdrop);
					coach.setAdded(cadded);
					coach.setActive(cactive);
					coach.setUpdated(cupdated);
					coach.setTeamrole(teamrole);
					
					tempcoachlist.add(coach);
				}
				//LOGGER.info("We have results for team roster");
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
    	setCoaches(tempcoachlist);
	
	}
	
	public void viewScoresheets(){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("reviewscoresheetsforateam.xhtml?teamid=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

