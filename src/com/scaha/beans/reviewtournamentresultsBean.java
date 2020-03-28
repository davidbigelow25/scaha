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
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.ExhibitionGame;
import com.scaha.objects.ExhibitionGameDataModel;
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
public class reviewtournamentresultsBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<TournamentGame> tournamentgames = null;
	
	//datamodels for all of the lists on the page
	private TournamentGameDataModel TournamentGameDataModel = null;
    
    //properties for storing the selected row of each of the datatables
	private TournamentGame selectedtournamentgame = null;
	private Boolean scoresheetrendered = null;
	
		
	
	@PostConstruct
    public void init() {
		
		//Load Exhibition Games
		getTournamentGames();
    }
	
    public reviewtournamentresultsBean() {  
        
    }  
    
 
	
    public TournamentGame getSelectedtournamentgame(){
		return selectedtournamentgame;
	}
	
	public void setSelectedtournamentgame(TournamentGame selectedGame){
		selectedtournamentgame = selectedGame;
	}
	
	public TournamentGameDataModel getTournamentgamedatamodel(){
    	return TournamentGameDataModel;
    }
    
    public void setTournamentgamedatamodel(TournamentGameDataModel odatamodel){
    	TournamentGameDataModel = odatamodel;
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

	
	public void getTournamentGames() {  
		List<TournamentGame> templist = new ArrayList<TournamentGame>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getAllTournamentGamesPlayed()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String idnonscahagame = rs.getString("idnonscahagames");
    				String requestingteam = rs.getString("requestingteam");
					String tournamentname = rs.getString("tournamentname");
    				String gamedate = rs.getString("gamedate");
    				String gametime = rs.getString("gametime");
    				String opponent = rs.getString("opponent");
    				String location = rs.getString("location");
    				String status = rs.getString("status");
    				Boolean rendered = rs.getBoolean("rendered");
    				Boolean scoresheetrendered = rs.getBoolean("scoresheetrendered");
    				
    				
    				TournamentGame tournament = new TournamentGame();
    				tournament.setIdgame(Integer.parseInt(idnonscahagame));
    				tournament.setRequestingteam(requestingteam);
    				tournament.setTournamentname(tournamentname);
    				tournament.setDate(gamedate);
    				tournament.setTime(gametime);
    				tournament.setOpponent(opponent);
    				tournament.setLocation(location);
    				tournament.setStatus(status);
    				tournament.setRendered(rendered);
    				tournament.setScoresheetrendered(scoresheetrendered);
    				templist.add(tournament);
				}
				//LOGGER.info("We have results for tourney games:");
			}
			
			
			rs.close();
			db.cleanup();
    		
		} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting tournament games for the year");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setTournamentgames(templist);
    	TournamentGameDataModel = new TournamentGameDataModel(tournamentgames);
	}
	
	
	public List<TournamentGame> getTournamentgames(){
		return tournamentgames;
	}
	
	public void setTournamentgames(List<TournamentGame> tgames){
		tournamentgames = tgames;
	}
	
	public void editGameDetail(TournamentGame tournament){
		String gameid = tournament.getIdgame().toString();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("editstattournamentgameform.xhtml?teamid=0&gameid=" + gameid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteTournamentGame(TournamentGame tourn){
		//need to set to void
    	Integer gameid = tourn.getIdgame();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("remove tournament game from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.deleteTeamTournamentGame(?)");
    		    cs.setInt("gameid", gameid);
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have deleted the tournament game"));
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Deleting the Tournament");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		//now we need to reload the data object for the loi list
		getTournamentGames();
	}
		
	public void uploadTournamentScoresheet(TournamentGame game){
		String gameid = game.getIdgame().toString();
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("managetournamentgamescoresheet.xhtml?id=" + gameid + "&teamid=0");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}

