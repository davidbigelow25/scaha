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

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.ExhibitionGame;
import com.scaha.objects.ExhibitionGameDataModel;
import com.scaha.objects.LiveGame;
import com.scaha.objects.MailableObject;
import com.scaha.objects.RosterEdit;
import com.scaha.objects.RosterEditDataModel;
import com.scaha.objects.TempGame;
import com.scaha.objects.TempGameDataModel;
import com.scaha.objects.Tournament;
import com.scaha.objects.TournamentDataModel;
import com.scaha.objects.TournamentGame;
import com.scaha.objects.TournamentGameDataModel;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class printallscahagamesBean implements Serializable{

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
	
    
	//bean level properties used by multiple methods
	private Integer profileid = 0;
	
	//datamodels for all of the lists on the page
	private TempGameDataModel TempGameDataModel = null;
    
    //properties for storing the selected row of each of the datatables or drop downs
    private TempGame selectedgame = null;
    private Integer selectedschedule = null;
    private Integer venueid = null;
    private Integer clubid = null;
    private String clubname = null;
	
	
	@PostConstruct
    public void init() {
		games = new ArrayList<TempGame>();  
        TempGameDataModel = new TempGameDataModel(games);
        
        
        this.setProfid(pb.getProfile().ID);
        this.setPb(pb);
        this.setVenueid(getVenueID(pb.getProfile().ID));
        
        //Load SCAHA Games for venue
        loadGamesforvenue(venueid);
        
        
	}
	
    public printallscahagamesBean() {  
        
    }  
    
    public String getClubname(){
    	return clubname;
    }	
    
    public void setClubname(String idprofile){
    	clubname = idprofile;
    }
    
    
    public Integer getClubid(){
    	return clubid;
    }	
    
    public void setClubid(Integer idprofile){
    	clubid = idprofile;
    }
    
    public Integer getVenueid(){
    	return venueid;
    }	
    
    public void setVenueid(Integer idprofile){
    	venueid = idprofile;
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
    public void loadGamesforvenue(Integer idvenue){
    	List<TempGame> tempresult = new ArrayList<TempGame>();
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getSCAHAGamesForVenue(?)");
			cs.setInt("venueid",this.profileid);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String idgame = rs.getString("idlivegame");
    				String hometeam = rs.getString("hometeam");
    				String awayteam = rs.getString("awayteam");
    				String homescore = rs.getString("homescore");
    				String awayscore = rs.getString("awayscore");
    				String dates = rs.getString("date");
    				String time = rs.getString("time");
    				String location = rs.getString("location");
    				String status = rs.getString("status");
    				String scoresheet = rs.getString("scoresheet");
    				Integer hometeamid = rs.getInt("idteamhome");
    				Integer awayteamid = rs.getInt("idteamhome");
    				Integer homefontsize = 24;
    				if (hometeam.length()>15){
    					homefontsize = 18;
    				}
    				Integer awayfontsize = 24;
    				if (awayteam.length()>15){
    					awayfontsize = 18;
    				}
    				Integer homeclubid = rs.getInt("homeclubid");
    				Integer awayclubid = rs.getInt("awayclubid");
    				String division = rs.getString("division");
    				
    				
    				
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
    				ogame.setScoresheet(scoresheet);
    				ogame.setStatus(status);
    				ogame.setIdhome(hometeamid);
    				ogame.setIdaway(awayteamid);
    				ogame.setFontsizehome(homefontsize);
    				ogame.setFontsizevisitor(awayfontsize);
    				ogame.setHomeclubid(homeclubid);
    				ogame.setAwayclubid(awayclubid);
    				ogame.setDivision(division);
    				tempresult.add(ogame);
    				
				}
				//LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedschedule);
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

    public TempGameDataModel getTempGamedatamodel(){
    	return TempGameDataModel;
    }
    
    public void setTempgamedatamodel(TempGameDataModel odatamodel){
    	TempGameDataModel = odatamodel;
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
	
	
    
	
	
		
	public void uploadSCAHAScoresheet(TempGame game){
		String gameid = game.getIdgame().toString();
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("managegamescoresheet.xhtml?redirect=reviewscahagames&scaha=yes&id=" + gameid + "&scheduleid=" + sb.getSelectedscheduleid());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		
	public void editLiveGame(TempGame game) {  
		
		Integer gameid = game.getIdgame();
		//locate the livegame in the list.. via the above id vs creating a new one from scratch
		//it does not have all the internal data that the one in the applevel bean does.
		// (Scaha Bean)  ScahaBean has a master copy of all games .. that are already pointing to the teams
		// in memory.. all hooked up..
		LiveGame lg = scaha.getScahaLiveGameList().getByKey(gameid);
		pb.setSelectedlivegame(lg);
		pb.setLivegameeditreturn("reviewscahagames.xhtml");
		
		 //LOGGER.info("!!!!! Real Selected Game is" + selectedlivegame);
		  
	     ExternalContext context = FacesContext.getCurrentInstance().getExternalContext(); 
	     try {
	    	 context.redirect("scahagamedetailsconfirm.xhtml");
	     } catch (IOException e) {
	    	 // TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	 }
	
	
	public Integer getVenueID(Integer splayerid){
		
		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(splayerid);
			db.getData("CALL scaha.getVenueforPerson(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					this.setClubname(rs.getString("description"));
					this.setVenueid(rs.getInt("idvenue"));
					}
				rs.close();
				//LOGGER.info("We have results for club for a profile");
			}
			db.cleanup();
    	} catch (SQLException e) {
    		// TODO nnfo("ERROR IN loading club by profile");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}

		return this.venueid;
    }
}

