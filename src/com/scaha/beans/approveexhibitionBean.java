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
public class approveexhibitionBean implements Serializable, MailableObject {

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
	
	//properties for emailing approval/decline of exhibition games
	private String gamedate=null;
	private String gametime=null;
	private String opponent=null;
	private String tourneygamelocation=null;
	private String exhibitiongamelocation=null;
	private String teamname = null;	
	private String status = null;
	//datamodels for all of the lists on the page
	private ExhibitionGameDataModel ExhibitionGameDataModel = null;
    
    //properties for storing the selected row of each of the datatables
	private ExhibitionGame selectedexhibitiongame = null;
	
	
	//properties for emailing to managers, scaha statistician
	private String to = null;
	private String subject = null;
	private String cc = null;
	
	private String todaysdate = null;
		
	
	@PostConstruct
    public void init() {
		
		//Load Exhibition Games
        getExhibitionGames();
        setTodaysDate();
    }
	
    public approveexhibitionBean() {  
        
    }  
    
    public String getStatus(){
    	return status;
    }
    
    public void setStatus(String gdate){
    	status=gdate;
    }
    
    
    public String getGamedate(){
    	return gamedate;
    }
    
    public void setGamedate(String gdate){
    	gamedate=gdate;
    }
    
    public String getGametime(){
    	return gametime;
    }
    
    public void setGametime(String gdate){
    	gametime=gdate;
    }
    
    public String getOpponent(){
    	return opponent;
    }
    
    public void setOpponent(String gdate){
    	opponent=gdate;
    }
    
    public String getTourneygamelocation(){
    	return this.tourneygamelocation;
    }
    
    public void setTourneygamelocation(String name){
    	this.tourneygamelocation=name;
    }
    
    public String getExhibitiongamelocation(){
    	return this.exhibitiongamelocation;
    }
    
    public void setExhibitiongamelocation(String name){
    	this.exhibitiongamelocation=name;
    }
    
    public String getTeamname(){
    	return teamname;
    }
    
    public void setTeamname(String name){
    	teamname=name;
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
		
		myTokens.add("REQUESTINGTEAM: " + this.teamname);
		myTokens.add("REQUESTDATE: " + this.todaysdate);
		myTokens.add("GAMEDATE: " + this.gamedate);
		myTokens.add("GAMETIME: " + this.gametime);
		myTokens.add("OPPONENT: " + this.opponent);
		myTokens.add("LOCATION: " + this.exhibitiongamelocation);
		myTokens.add("STATUS: " + this.status);
		
		result = Utils.mergeTokens(approveexhibitionBean.mail_exhibitiongame_body,myTokens);
		
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
    		CallableStatement cs = db.prepareCall("CALL scaha.getAllExhibitionGames()");
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
    				//Boolean rendered = rs.getBoolean("rendered");
    				String manageremail = rs.getString("manageremail");
    				
    				ExhibitionGame tournament = new ExhibitionGame();
    				tournament.setIdgame(Integer.parseInt(idnonscahagame));
    				tournament.setDate(gamedate);
    				tournament.setTime(gametime);
    				tournament.setOpponent(opponent);
    				tournament.setLocation(location);
    				tournament.setStatus(status);
    				//tournament.setRendered(rendered);
    				tournament.setRequestingteam(requestingteam);
    				tournament.setManageremail(manageremail);
    				templist.add(tournament);
				}
				//LOGGER.info("We have results for all exhibition list");
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
	
		
	public void approveExhibitionGame(ExhibitionGame tournament){
		Integer gameid = tournament.getIdgame();
		this.setTeamname(tournament.getRequestingteam());
		this.setGamedate(tournament.getDate());
		this.setGametime(tournament.getTime());
		this.setOpponent(tournament.getOpponent());
		this.setExhibitiongamelocation(tournament.getLocation());
		this.setStatus("Approved");
		
		//need to add logic to set tournament as approved
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("remove tournament from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.setExhibitionApproved(?)");
    		    cs.setInt("gameid", gameid);
    		    cs.executeQuery();
    		    db.commit();
    			
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have approved the exhibition game"));
                
                //need to send email with approval
                //need to add email to manager and scaha statistician
    			//add statistician email
                to = this.pb.getProfile().getUserName();
    			
                //add manageremail
                cs = db.prepareCall("CALL scaha.getTeamManageremail(?)");
    		    cs.setInt("gameid", gameid);
    		    rs = cs.executeQuery();
    		    if (rs != null){
    				
    				while (rs.next()) {
    					to = to + ',' + rs.getString("altemail");;
    				}
    		    }
      			rs.close();
    		    
      			
    			to = "lahockeyfan2@yahoo.com";
    		    this.setToMailAddress(to);
    		    this.setPreApprovedCC("");
    		    this.setSubject("Exhibition Game Approved for " + tournament.getRequestingteam());
    		    
    			SendMailSSL mail = new SendMailSSL(this);
    			//LOGGER.info("Finished creating mail object for " + tournament.getTournamentname() + " Approved for " + tournament.getRequestingteam());
    			mail.sendMail();
    			db.cleanup();
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
		
		getExhibitionGames();
	}
	
	public void declineExhibitionGame(ExhibitionGame tournament){
		Integer gameid = tournament.getIdgame();
		this.setTeamname(tournament.getRequestingteam());
		this.setGamedate(tournament.getDate());
		this.setGametime(tournament.getTime());
		this.setOpponent(tournament.getOpponent());
		this.setExhibitiongamelocation(tournament.getLocation());
		this.setStatus("Declined");
		
		//need to add logic to set tournament as approved
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("remove tournament from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.setExhibitionDeclined(?)");
    		    cs.setInt("gameid", gameid);
    		    cs.executeQuery();
    		    db.commit();
    			
 				
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have declined the exhibition game"));
                
                //need to send email with decline
                //need to add email to manager and scaha statistician
    			//add statistician email
                to = this.pb.getProfile().getUserName();
    			
                //add manageremail
                cs = db.prepareCall("CALL scaha.getTeamManageremail(?)");
    		    cs.setInt("gameid", gameid);
    		    rs = cs.executeQuery();
    		    if (rs != null){
    				
    				while (rs.next()) {
    					to = to + ',' + rs.getString("altemail");;
    				}
    		    }
      			rs.close();
    		    
      			
    			to = "lahockeyfan2@yahoo.com";
    		    this.setToMailAddress(to);
    		    this.setPreApprovedCC("");
    		    this.setSubject("Exhibition Game Declined for " + tournament.getRequestingteam());
    		    
    			SendMailSSL mail = new SendMailSSL(this);
    			//LOGGER.info("Finished creating mail object for " + tournament.getTournamentname() + " Declined for " + tournament.getRequestingteam());
    			mail.sendMail();
    			db.cleanup();
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
		
		getExhibitionGames();
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

