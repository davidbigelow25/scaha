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
public class approvetournamentBean implements Serializable, MailableObject {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<Tournament> tournaments = null;
	
	
	//datamodels for all of the lists on the page
	private TournamentDataModel TournamentDataModel = null;
    
    //properties for storing the selected row of each of the datatables
    private Tournament selectedtournament = null;
	
	
	//properties for emailing to managers, scaha statistician
	private String to = null;
	private String subject = null;
	private String cc = null;
	private static String mail_tournament_body = Utils.getMailTemplateFromFile("/mail/tournamentapproval.html");
	private String todaysdate = null;
		
	
	@PostConstruct
    public void init() {
		setTodaysDate();
    	
        //Load Tournament and Games
        getTournament();
        
           
	}
	
    public approvetournamentBean() {  
        
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
		myTokens.add("REQUESTINGTEAM: " + this.getSelectedtournament().getRequestingteam());
		myTokens.add("REQUESTDATE: " + this.todaysdate);
		myTokens.add("TOURNAMENTNAME: " + this.getSelectedtournament().getTournamentname());
		//myTokens.add("LEVELPLAYED: " + this.getSelectedtournament().get);
		myTokens.add("DATES: " + this.getSelectedtournament().getDates());
		//myTokens.add("ENDDATE: " + this.enddate);
		//myTokens.add("CONTACT: " + this.contact);
		//myTokens.add("PHONE: " + this.phone);
		myTokens.add("SANCTION: " + this.getSelectedtournament().getSanction());
		myTokens.add("LOCATION: " + this.getSelectedtournament().getLocation());
		//myTokens.add("WEBSITE: " + this.website);
		myTokens.add("STATUS: " + this.getSelectedtournament().getStatus());
		
		result = Utils.mergeTokens(approvetournamentBean.mail_tournament_body,myTokens);
		
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
    
    public Tournament getSelectedtournament(){
		return selectedtournament;
	}
	
	public void setSelectedtournament(Tournament selectedGame){
		selectedtournament = selectedGame;
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

	public void getTournament() {  
		List<Tournament> templist = new ArrayList<Tournament>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getAllTournaments()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String idteam = rs.getString("idteamtournament");
    				String tournamentname = rs.getString("tournamentname");
    				String requestingteam = rs.getString("requestingteam");
    				String teammanageremail = rs.getString("teammanageremail");
    				String dates = rs.getString("dates");
    				String sanction = rs.getString("sanction");
    				String contact = rs.getString("contact");
    				String location = rs.getString("location");
    				String status = rs.getString("status");
    				//Boolean rendered = rs.getBoolean("rendered");
    				
    				Tournament tournament = new Tournament();
    				tournament.setIdtournament(Integer.parseInt(idteam));
    				tournament.setTournamentname(tournamentname);
    				tournament.setDates(dates);
    				tournament.setContact(contact);
    				tournament.setLocation(location);
    				tournament.setStatus(status);
    				tournament.setSanction(sanction);
    				//tournament.setRendered(rendered);
    				tournament.setRequestingteam(requestingteam);
    				tournament.setEmail(teammanageremail);
    				
    				templist.add(tournament);
				}
				//LOGGER.info("We have results for all tourney list ");
			}
			
			
			rs.close();
			db.cleanup();
    		
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting tournament list for statistician");
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
	
	public void approveTournament(Tournament tournament){
		this.setSelectedtournament(tournament);
		Integer tourneyid = tournament.getIdtournament();
		
		//need to add logic to set tournament as approved
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("remove tournament from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.setTournamentApproved(?)");
    		    cs.setInt("tourneyid", tourneyid);
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have approved the tournament"));
                
                //need to add email to manager and scaha statistician
    			//add statistician email
                to = this.pb.getProfile().getUserName();
    			
                //add manageremail
    			to = to + ',' + tournament.getEmail();
      			
    			//to = "lahockeyfan2@yahoo.com";
    		    this.setToMailAddress(to);
    		    this.setPreApprovedCC("");
    		    this.setSubject(tournament.getTournamentname() + " Approved for " + tournament.getRequestingteam());
    		    
    			SendMailSSL mail = new SendMailSSL(this);
    			//LOGGER.info("Finished creating mail object for " + tournament.getTournamentname() + " Approved for " + tournament.getRequestingteam());
    			
    			//need to pass the approved status for the email
    			this.selectedtournament.setStatus("Approved");
    			mail.sendMail();
    			
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
		
		getTournament();
	}
	
	public void declineTournament(Tournament tournament){
		this.setSelectedtournament(tournament);
		Integer tourneyid = tournament.getIdtournament();
		
		//need to add logic to set tournament as approved
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("decline tournament");
 				CallableStatement cs = db.prepareCall("CALL scaha.setTournamentDeclined(?)");
    		    cs.setInt("tourneyid", tourneyid);
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have decline the tournament"));
                
              //need to add email to manager and scaha statistician
    			//add statistician email
                to = this.pb.getProfile().getUserName();
    			
                //add manageremail
    			to = to + ',' + tournament.getEmail();
      			
    			//to = "lahockeyfan2@yahoo.com";
    		    this.setToMailAddress(to);
    		    this.setPreApprovedCC("");
    		    this.setSubject(tournament.getTournamentname() + " Declined for " + tournament.getRequestingteam());
    		    
    			SendMailSSL mail = new SendMailSSL(this);
    			//LOGGER.info("Finished creating mail object for " + tournament.getTournamentname() + " Declined for " + tournament.getRequestingteam());
    			
    			//need to pass the approved status for the email
    			this.selectedtournament.setStatus("Declined");
    			mail.sendMail();
    			
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
		
		getTournament();
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

