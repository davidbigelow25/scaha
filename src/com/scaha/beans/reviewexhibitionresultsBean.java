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
public class reviewexhibitionresultsBean implements Serializable, MailableObject {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	private static String mail_exhibitiongame_body = Utils.getMailTemplateFromFile("/mail/exhibitiongame.html");
	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<ExhibitionGame> exhibitiongames = null;
	
	//datamodels for all of the lists on the page
	private ExhibitionGameDataModel ExhibitionGameDataModel = null;
    
    //properties for storing the selected row of each of the datatables
	private ExhibitionGame selectedexhibitiongame = null;
	
	private String todaysdate = null;
	private String subject = null;
	private String cc = null;
	private String to = null;
		
	
	@PostConstruct
    public void init() {
		
		//Load Exhibition Games
        getExhibitionGames();
        setTodaysDate();
    }
	
    public reviewexhibitionresultsBean() {  
        
    }  
    
    
    public String getTodaysdate(){
    	return todaysdate;
    }
    
    public void setTodaysdate(String tdate){
    	todaysdate=tdate;
    }
    
    public String getSubject() {
		// TODO Auto-generated method stub
		return subject;
	}
    
    public void setSubject(String ssubject){
    	subject = ssubject;
    }
    
    public String getTextBody() {
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		String result = "";
		/*
		myTokens.add("REQUESTINGTEAM: " + this.teamname);
		myTokens.add("REQUESTDATE: " + this.todaysdate);
		myTokens.add("GAMEDATE: " + this.gamedate);
		myTokens.add("GAMETIME: " + this.gametime);
		myTokens.add("OPPONENT: " + this.opponent);
		myTokens.add("LOCATION: " + this.exhibitiongamelocation);
		myTokens.add("STATUS: " + this.status);
		
		result = Utils.mergeTokens(approveexhibitionBean.mail_exhibitiongame_body,myTokens);
		*/
		return result;
	}
	
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return cc;
	}
	
	public void setPreApprovedCC(String scc){
		cc = scc;
	}
	
	
	
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return to;
	}
    
    public void setToMailAddress(String sto){
    	to = sto;
    }
    
    public ExhibitionGame getSelectedexhibitiongame(){
		return selectedexhibitiongame;
	}
	
	public void setSelectedexhibitiongame(ExhibitionGame selectedGame){
		selectedexhibitiongame = selectedGame;
	}
    
	public ExhibitionGameDataModel getExhibitiongamedatamodel(){
    	return ExhibitionGameDataModel;
    }
    
    public void setExhibitiongamedatamodel(ExhibitionGameDataModel odatamodel){
    	ExhibitionGameDataModel = odatamodel;
    }
    
    public List<ExhibitionGame> getExhibitiongames(){
		return exhibitiongames;
	}
	
	public void setExhibitiongames(List<ExhibitionGame> tgames){
		exhibitiongames = tgames;
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

	
	public void getExhibitionGames() {  
		List<ExhibitionGame> templist = new ArrayList<ExhibitionGame>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getAllExhibitionGamesPlayed()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String idnonscahagame = rs.getString("idnonscahagames");
    				String requestingteam = rs.getString("requestingteam");
					String gamedate = rs.getString("gamedate");
    				String gametime = rs.getString("gametime");
    				String opponent = rs.getString("opponent");
    				String location = rs.getString("location");
    				String status = rs.getString("status");
    				Boolean rendered = rs.getBoolean("rendered");
    				String manageremail = rs.getString("manageremail");
    				Boolean scoresheetrendered = rs.getBoolean("scoresheetrendered");
    				
    				ExhibitionGame tournament = new ExhibitionGame();
    				tournament.setIdgame(Integer.parseInt(idnonscahagame));
    				tournament.setDate(gamedate);
    				tournament.setTime(gametime);
    				tournament.setOpponent(opponent);
    				tournament.setLocation(location);
    				tournament.setStatus(status);
    				tournament.setRendered(rendered);
    				tournament.setRequestingteam(requestingteam);
    				tournament.setManageremail(manageremail);
    				tournament.setScoresheetrendered(scoresheetrendered);
    				templist.add(tournament);
				}
				//LOGGER.info("We have results for all exhibition list for confirming scoresheets");
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting all exhiibtion list ");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setExhibitiongames(templist);
    	ExhibitionGameDataModel = new ExhibitionGameDataModel(exhibitiongames);
	}
	
	//following methods are to work with the exhibition game.	
	public void deleteExhibitionTournamentGame(ExhibitionGame game){
		Integer gameid = game.getIdgame();
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
                context.addMessage(null, new FacesMessage("Successful", "You have deleted the exhibition game"));
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
		getExhibitionGames();
	}
	
	public void editExhibitionGameDetail(ExhibitionGame game){
		String gameid = game.getIdgame().toString();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("editreviewexhibitiongameform.xhtml?teamid=0&gameid=" + gameid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//method will take the user to the upload scoresheet page where they can review or add the scoresheet.
	public void uploadExhibitionScoresheet(ExhibitionGame game){
		String gameid = game.getIdgame().toString();
		FacesContext context = FacesContext.getCurrentInstance();
				
		try{
			context.getExternalContext().redirect("manageexhibitiongamescoresheet.xhtml?id=" + gameid + "&teamid=0");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	public InternetAddress[] getToMailIAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InternetAddress[] getPreApprovedICC() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void setTodaysDate(){
		//need to set todays date for email
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		Date date = new Date();
		this.setTodaysdate(dateFormat.format(date).toString());
		
	}
}

