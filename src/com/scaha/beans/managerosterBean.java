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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ApplicationScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Coach;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.Team;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class managerosterBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	transient private ResultSet rssub = null;
	private List<Club> clubs = null;
	private Team selectedteam = null;
	
	//properties for adding tournaments
	private String tournamentname;
	private String startdate;
	private String enddate;
	private String contact;
	private String phone;
	private String sanction;
	private String location;
	private String website;
	
	
	@PostConstruct
    public void init() {
        // In @PostConstruct (will be invoked immediately after construction and dependency/property injection).
		clubs = genListofClubs();
    }

	
    public managerosterBean() {  
        
    }  
    
    public Team getSelectedteam(){
		return selectedteam;
	}
	
	public void setSelectedteam(Team steam){
		selectedteam = steam;
	}
    
    public List<Club> genListofClubs(){

    	List<Club> templist = new ArrayList<Club>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	try{

			CallableStatement cs = db.prepareCall("CALL scaha.getClubs()");
			CallableStatement csinner = db.prepareCall("CALL scaha.getTeamCountsByClubId(?)");

		    ResultSet rs = cs.executeQuery();
			
			if (rs != null){
				while (rs.next()) {
					String idclub = rs.getString("idclubs");
					String clubname = rs.getString("clubname");
					Club club = new Club();
					club.setClubid(idclub);
					club.setClubname(clubname);
					templist.add(club);

					club = null;
				}

				rs.close();
				
				for (Club c : templist) {
					
					//need to get list of teams for the club
					csinner.setInt("pclubid", Integer.parseInt(c.getClubid()));
					rssub = csinner.executeQuery();
					if (rssub != null){
						List<Team> tempteamlist = new ArrayList<Team>();
						while (rssub.next()) {
	    					String idteam = rssub.getString("idteams");
	        				String teamname = rssub.getString("teamname");
	        				String activeplayercount = rssub.getString("activeplayercount");
	        				String totalplayercount = rssub.getString("totalplayercount");
	        				String totalcoachcount = rssub.getString("totalcoachcount");

							//these are for displaying tournaments attending
	        				String labordayattend = rssub.getString("labordayattend");
							Boolean laborday = false;
							if (labordayattend.equals("yes")){
								laborday=true;
							}
							String tgivingattend = rssub.getString("tgivingattend");
							Boolean tgiving = false;
							if (tgivingattend.equals("yes")){
								tgiving=true;
							}
							String xmasattend = rssub.getString("xmasattend");
							Boolean xmas = false;
							if (xmasattend.equals("yes")){
								xmas=true;
							}
							String mlkattend = rssub.getString("mlkattend");
							Boolean mlk = false;
							if (mlkattend.equals("yes")){
								mlk=true;
							}
							String pdayattend = rssub.getString("pdayattend");
							Boolean pday = false;
							if (pdayattend.equals("yes")){
								pday=true;
							}
							String byewkdattend = rssub.getString("byewkdattend");
							Boolean byewkd = false;
							if (byewkdattend.equals("yes")){
								byewkd=true;
							}
							Integer bullyingcount = rssub.getInt("bullyingcount");

	        				Team team = new Team(teamname,idteam);
	        				team.setIdteam(idteam);
	        				team.setTeamname(teamname);
	        				team.setActiveplayercount(activeplayercount);
	        				team.setTotalplayercount(totalplayercount);
	        				team.setTotalcoachescount(totalcoachcount);

							//these are for displaying tournaments attending
							team.setLabordayattend(labordayattend);
							team.setTgivingattend(tgivingattend);
							team.setXmasattend(xmasattend);
							team.setMlkattend(mlkattend);
							team.setPdayattend(pdayattend);
							team.setByewkdattend(byewkdattend);
							team.setLaborday(laborday);
							team.setTgiving(tgiving);
							team.setXmas(xmas);
							team.setMlk(mlk);
							team.setPday(pday);
							team.setByewkd(byewkd);

							//this is for displaying bullying count
							team.setBullyingcount(bullyingcount);
							tempteamlist.add(team);

	        				team = null;
						}
						c.setTeams(tempteamlist);
						rssub.close();
						tempteamlist = null;
					}
				}
				LOGGER.info("We have results for division list");
				//cs.close();
				csinner.close();
			}
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading teams");
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		//
    	// Return it
    	//
    	return templist;

	}
    
    public List<Club> getClubs(){
		return clubs;
	}
	
	public void setClubs(List<Club> list){
		clubs = list;
	}
	
	public void viewroster(Team steam){
		String idteam = steam.getIdteam();
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editroster.xhtml?teamid=" + idteam);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

public void updateTeamRostereffectivedate(Team team,String newdate){
		
		//lets first update the entire active roster with the new effective date aka roster date
		//then lets reload the objects
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first update team
    		CallableStatement cs = db.prepareCall("CALL scaha.updateTeamrosterdate(?,?)");
			cs.setInt("teamid", Integer.parseInt(team.getIdteam()));
			cs.setString("srosterdate", newdate);
		    cs.executeQuery();
    		cs.close();
		
    		db.free();
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

	private void loadTournamentDetails(String tournament, Team steam){
		String idteam = steam.getIdteam();

		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("viewtournamentdetails.xhtml?id=" + idteam + "&t="+tournament);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}

