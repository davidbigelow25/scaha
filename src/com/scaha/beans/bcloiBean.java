package com.scaha.beans;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.Coach;
import com.scaha.objects.CoachDataModel;


import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;

//import com.gbli.common.SendMailSSL;


public class bcloiBean implements Serializable, MailableObject {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/voidloi.html");
	transient private ResultSet rs = null;
	private List<Coach> players = null;
    private CoachDataModel PlayerDataModel = null;
    private Coach selectedplayer = null;
	private String selectedtabledisplay = null;
	private String selectedplayerid = null;
	private String notes = null;
	private Integer rosteridforconfirm = null;
	private String page = null;
	private String to = null;
	private String subject = null;
	private String cc = null;
	private Integer clubid = null;
	private String clubname = null;
	private String searchcriteria = null;
	
 	@PostConstruct
    public void init() {
		players = new ArrayList<Coach>();
        PlayerDataModel = new CoachDataModel(players);
        this.setSelectedtabledisplay("1");
        
      //will need to load player profile information for displaying loi
      	HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
		LOGGER.info("We are getting page request:");

		if(hsr.getParameter("page") != null)
        {
    		page = hsr.getParameter("page").toString();
        }else{
        	page = "";
        }
        if(hsr.getParameter("search") != null)
        {
    		searchcriteria = hsr.getParameter("search").toString();
        }else{
        	searchcriteria = "";
        }
        
        if (!searchcriteria.equals("")){
			LOGGER.info("bcloi calling players display");

			playersDisplay();
			LOGGER.info("bcloi finished calling players display");

		}
    }
	
    public bcloiBean() {  
         
    }  
    
    public String getClubname(){
    	return clubname;
    }
    
    public void setClubname(String value){
    	clubname = value;
    }
    
    
    public String getSearchcriteria(){
    	return searchcriteria;
    }
    
    public void setSearchcriteria(String value){
    	searchcriteria = value;
    }
    
    public String getSubject() {
		// TODO Auto-generated method stub
		return subject;
	}
    
    public void setSubject(String ssubject){
    	subject = ssubject;
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
	
    public Integer getRosteridforconfirm(){
    	return rosteridforconfirm;
    }
    
    public void setRosteridforconfirm(Integer value){
    	rosteridforconfirm=value;
    }
    
    public String getPage(){
		return page;
	}
	
	public void setPage(String cyear){
		page=cyear;
	}
    
    public String getSelectedplayerid(){
    	return selectedplayerid;
    }
    
    public void setSelectedplayerid(String selidplayer){
    	selectedplayerid=selidplayer;
    }
    
    public String getSelectedtabledisplay(){
		return selectedtabledisplay;
	}
	
	public void setSelectedtabledisplay(String selectedTabledisplay){
		selectedtabledisplay = selectedTabledisplay;
	}
    
    public Coach getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Coach selectedPlayer){
		selectedplayer = selectedPlayer;
	}
    
	public List<Coach> getPlayers(){
		return players;
	}
	
	public void setPlayers(List<Coach> list){
		players = list;
	}
	
	    
    //retrieves list of players
    public void playersDisplay(){
		LOGGER.info("bcloi  players display starting loading db");

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Coach> tempresult = new ArrayList<Coach>();
    	
    	try{

    		if (db.setAutoCommit(false)) {
				LOGGER.info("bcloi calling name search" + this.searchcriteria);

				CallableStatement cs = db.prepareCall("CALL scaha.getLoiListByNameSearch(?)");
    			cs.setString("search", this.searchcriteria);
    			rs = cs.executeQuery();
				LOGGER.info("bcloi executing query getloilistbynamesearch" + this.searchcriteria);
    		    if (rs != null){
    				
    				while (rs.next()) {
						LOGGER.info("bcloi pulling data to variables");
						String idplayer = rs.getString("idperson");
        				String sfirstname = rs.getString("firstname");
        				String slastname = rs.getString("lastname");
        				String scurrentteam = rs.getString("currentteam");
						String slastyearteam = rs.getString("lastyearteam");
						if (slastyearteam==null){
							slastyearteam="N/A";
						}
						String sprioryearteam = rs.getString("prioryearteam");
						if (sprioryearteam==null){
							sprioryearteam="N/A";
						}
						Boolean bcovid = false;
						String sdob = rs.getString("dob");
        				String sloidate = rs.getString("loidate");
        				String scitizenship = rs.getString("citizenship");
        				String scitizenshipexpiredate = rs.getString("citizenshipexpiredate");
        				String scitizenshiptransfer = rs.getString("citizenshiptransfer");
        				if (scitizenshiptransfer==null){
        					scitizenshiptransfer="0";
        				}
						String sbirthcertificate = rs.getString("birthcertificate");
						String splayerup = rs.getString("isplayerup");
        				String sindefinite = rs.getString("indefinite");
        				String confirmed = rs.getString("confirmed");
        				String rosterid = rs.getString("idroster");
        				String notes = rs.getString("notes");
        				String safesport = rs.getString("safesportindicator");
        				String suspended = rs.getString("issuspended");
						Integer transferid = rs.getInt("idcitizenshiptransfers");
						Integer transfer = rs.getInt("citizenshiptransfers");
						Integer transferindefinite = rs.getInt("indefinite");
						Integer birthcertificate = rs.getInt("birthcertificate");
						String citizenship = rs.getString("citizenship");
						Boolean is18safesport = rs.getBoolean("is18display");
						String safesportfor18 = rs.getString("safesportfor18");
						String expirationdate = rs.getString("expirationdate");
						Integer usaroster = rs.getInt("usaroster");
						Integer pdr = rs.getInt("pdrapply");
						String abi = rs.getString("abi");

						LOGGER.info("bcloi loading variables in coach object");
        				Coach oplayer = new Coach();
						oplayer.setIdcoach(idplayer);
						oplayer.setFirstname(sfirstname);
        				oplayer.setLastname(slastname);
        				oplayer.setCurrentteam(scurrentteam);
						oplayer.setPreviousteam(slastyearteam);
						//oplayer.setPriorteam(sprioryearteam);
						oplayer.setBcovid(bcovid);
						oplayer.setDob(sdob);
        				oplayer.setCitizenship(scitizenship);
        				oplayer.setCitizenshiptransfer(scitizenshiptransfer);
        				oplayer.setCtverified(scitizenshiptransfer);
        				oplayer.setCTExpirationdate(scitizenshipexpiredate);
        				oplayer.setNotes(notes);
        				oplayer.setSafesport(safesport);
        				oplayer.setSuspended(suspended);
						if (suspended.equals("Y")){
							oplayer.setSuspendedmessage("UnSuspend LOI");
						}else{
							oplayer.setSuspendedmessage("Suspend LOI");
						}

						if (scitizenship!=null){
	        				if (!scitizenship.equals("USA")){
		        				if (sindefinite!=null){
		        					if (sindefinite.equals("1")){
		            					oplayer.setCitizenshiplabel("Transfer does not expire");
		            				} else if (sindefinite.equals("1") && scitizenshiptransfer.equals("1")){
		            					oplayer.setCitizenshiplabel("Transfer expires " + scitizenshipexpiredate);
		            				} else if (sindefinite.equals("0") && scitizenshiptransfer.equals("1")){
		            					oplayer.setCitizenshiplabel("Transfer expires " + scitizenshipexpiredate);
		            				} else if (sindefinite.equals("0") && scitizenshiptransfer.equals("0")){
		            					oplayer.setCitizenshiplabel("Transfer is needed.");
		            				} else if (sindefinite.equals("1") && scitizenshiptransfer.equals("0")){
		            					oplayer.setCitizenshiplabel("Transfer is needed.");
		            				} else {
		            					oplayer.setCitizenshiplabel("Transfer is needed.");
		            				}
		        				} else {
		        					oplayer.setCitizenshiplabel("Transfer is needed.");
		        				}
	        				}
        				}

						oplayer.setBirthcertificate(birthcertificate);
						oplayer.setBcverified(sbirthcertificate);
						oplayer.setLoidate(sloidate);
        				oplayer.setPlayerup(IsPlayerup(splayerup));
        				oplayer.setConfirmed(confirmed);
        				oplayer.setRosterid(rosterid);
						oplayer.setTransferid (transferid);
						oplayer.setTransfer(transfer);
						oplayer.setTransferindefinite(transferindefinite);
						oplayer.setExpirationdate(expirationdate);
						oplayer.setBirthcertificate(birthcertificate);
						oplayer.setCitizenship(citizenship);
						oplayer.setIs18safesport(is18safesport);
						oplayer.setSafesportfor18(safesportfor18);
						oplayer.setUsaroster(usaroster);
						oplayer.setPdrapply(pdr);
        				oplayer.setIsbullying(abi);
						tempresult.add(oplayer);

        				oplayer = null;
    				}
    				
    				LOGGER.info("We have results for bc lois for the search criteria:" + this.searchcriteria);
    				
    			}
				LOGGER.info("bcloi cleaning up record set and db");
    			rs.close();	
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR bc Lois for search criteria:" + this.searchcriteria);
    		e.printStackTrace();
    		db.rollback();
			db.free();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	//setResults(tempresult);
    	setPlayers(tempresult);
    	PlayerDataModel = new CoachDataModel(players);
		tempresult = null;
    }

    public CoachDataModel getPlayerdatamodel(){
    	return PlayerDataModel;
    }
    
    public void setPlayerdatamodel(CoachDataModel odatamodel){
    	PlayerDataModel = odatamodel;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    public String IsPlayerup(String sname){
    	if (sname.equals("1")){
    		return "Yes";
    	} else {
    		return "";
    	}
    	
    }
    
    public void addTransferCitizenship(Coach selectedPlayer){

		String sidplayer = selectedPlayer.getIdcoach();

		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("addtransfercitizenship.xhtml?playerid=" + sidplayer + "&page=bcloi&search=" + this.searchcriteria);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void voidLoi(Coach selectedPlayer){
		LOGGER.info("bcloi starting voidingloi" + selectedPlayer);
		String sidplayer = selectedPlayer.getIdcoach();
		String playname = selectedPlayer.getFirstname() + ' ' + selectedPlayer.getLastname();
		this.setSelectedplayer(selectedPlayer);

		LOGGER.info("bcloi calling getclubid " + sidplayer);
		getClubID(sidplayer);
		LOGGER.info("bcloi finished clubid for " + sidplayer);

		//need to set to void
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
				LOGGER.info("bcloi calling getclubidbyrosterid " + sidplayer);

				//need to get club id first before setting the loi to void.
				CallableStatement cs = db.prepareCall("CALL scaha.getClubIdbyRosterID(?)");
				cs.setInt("rosterid", Integer.parseInt(sidplayer));
				rs = cs.executeQuery();
				LOGGER.info("bcloi executing query " + sidplayer);

				if (rs != null) {

					while (rs.next()) {
						LOGGER.info("bcloi getclubid from recordset" + sidplayer);

						this.clubid = rs.getInt("idclub");
					}
				}

				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("verify loi code provided");
 				cs = db.prepareCall("CALL scaha.settoVoid(?)");
    		    cs.setInt("playerid", Integer.parseInt(selectedPlayer.getRosterid()));
    		    cs.executeQuery();
    		    
    		    db.commit();
    			db.cleanup();
 				
    		    //logging 
    			LOGGER.info("We are voiding the loi for player id:" + sidplayer);
    		    
    			//need to send an email acknowledging the loi is voided.
    			to = "";
    			LOGGER.info("Sending void loi email to club registrar, and family");
    			cs = db.prepareCall("CALL scaha.getClubRegistrarEmail(?)");
    		    cs.setInt("iclubid", this.clubid);
    		    rs = cs.executeQuery();
    		    if (rs != null){
    				while (rs.next()) {
    					if (!to.equals("")){
    						to = to + "," + rs.getString("usercode");
    					}else {
    						to = rs.getString("usercode");
    					}
    				}
    			}
				rs.close();

				LOGGER.info("bcloi calling family email sp " + sidplayer);

				cs = db.prepareCall("CALL scaha.getFamilyEmail(?)");
    		    cs.setInt("iplayerid", Integer.parseInt(sidplayer));
    		    rs = cs.executeQuery();
    		    if (rs != null){
    				while (rs.next()) {
    					to = to + ',' + rs.getString("usercode");
    				}
    			}
    		    rs.close();
				
    		    
    		    //hard my email address for testing purposes
    		    //to = "lahockeyfan2@yahoo.com";
				LOGGER.info("bcloi setting email objeccts");

				this.setToMailAddress(to);
    		    this.setPreApprovedCC("");
    		    this.setSubject(this.selectedplayer.getFirstname() + " " + this.selectedplayer.getLastname() + " LOI Void with " + this.getClubname());
    		    
				SendMailSSL mail = new SendMailSSL(this);
				LOGGER.info("Finished creating mail object for " + this.selectedplayer.getFirstname() + " " + this.selectedplayer.getLastname() + " LOI Void with " + this.getClubname());
				//mail.sendMail();
				
    			
    			FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have voided the loi for:" + playname));
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedplayer);
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		//now we need to reload the data object for the loi list
		playersDisplay();
	}
	
	public void viewLoi(Coach selectedPlayer){
		LOGGER.info("bcloi calling viewing loi");

		String sidplayer = selectedPlayer.getIdcoach();
		String sidroster = selectedPlayer.getRosterid();
				
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("scahaviewloi.xhtml?playerid=" + sidplayer + "&rid=" + sidroster + "&page=bcloi&search=" + this.searchcriteria);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void confirmLoi(Coach selectedPlayer){
		LOGGER.info("bcloi starting confirm loi ");

		String sidplayer = selectedPlayer.getRosterid();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("bcloi calling confirmcoachloi");
 				CallableStatement cs = db.prepareCall("CALL scaha.confirmCoachLoi(?)");
    		    cs.setInt("icoachid", Integer.parseInt(sidplayer));
    		    cs.executeQuery();
    		    LOGGER.info("We have confirmed loi for player id:" + sidplayer);
    			
    			db.commit();
    			db.cleanup();
 			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Confirming player id " + sidplayer);
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		LOGGER.info("bcloi calling player display after confirming loi" );

		playersDisplay();

		LOGGER.info("bcloi finished calling player display after confirming loi");

	}
	
	public void CloseLoi(String spage){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			if (spage.equals("quick")){
				context.getExternalContext().redirect("quickplayerloiconfirm.xhtml");
			}else{
				context.getExternalContext().redirect("workingwithbirthcertificate.xhtml&search=" + this.searchcriteria);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void confirmLoifromview(Integer sidplayer,String spage){
		
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			
			try{

				if (db.setAutoCommit(false)) {
				
					//Need to provide info to the stored procedure to save or update
	 				LOGGER.info("confirming player :" + sidplayer);
	 				CallableStatement cs = db.prepareCall("CALL scaha.confirmCoachLoi(?)");
	    		    cs.setInt("icoachid", sidplayer);
	    		    cs.executeQuery();
	    		    LOGGER.info("We have confirmed loi for player id:" + sidplayer.toString());
	    			
	    			db.commit();
	    			db.cleanup();
	 			} else {
			
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.info("ERROR IN Confirming player id " + sidplayer.toString());
				e.printStackTrace();
				db.rollback();
				db.free();
			} finally {
				//
				// always clean up after yourself..
				//
				db.free();
			}
		
			FacesContext context = FacesContext.getCurrentInstance();
			try{
				if (spage.equals("bcloi")){
					context.getExternalContext().redirect("workwithbirthcertificate.xhtml&search=" + this.searchcriteria);
				}
				else if (spage.equals("quick")){
					context.getExternalContext().redirect("quickplayerloiconfirm.xhtml");
				}else{
					context.getExternalContext().redirect("workwithbirthcertificate.xhtml");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	public String getTextBody() {
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("FIRSTNAME:" + this.selectedplayer.getFirstname());
		myTokens.add("LASTNAME:" + this.selectedplayer.getLastname());
		myTokens.add("CLUBNAME:" + this.getClubname());
		
		return Utils.mergeTokens(bcloiBean.mail_reg_body,myTokens);
		
	}
	
	public void getClubID(String splayerid){
		
		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(Integer.parseInt(splayerid));
			db.getData("CALL scaha.getClubforPersonId(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					this.clubid = rs.getInt("idclub");
					this.setClubname(rs.getString("clubname"));
					}
				rs.close();
				LOGGER.info("We have results for club for a profile");
			}
			db.cleanup();
    	} catch (SQLException e) {
    		// TODO nnfo("ERROR IN loading club by profile");
    		e.printStackTrace();
    		db.rollback();
			db.free();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}

    }

	public void suspendLoi(Coach selectedPlayer){

		String sidplayer = selectedPlayer.getRosterid();
		String ssuspended = selectedPlayer.getSuspended();
		Integer suspend = 1;
		if (ssuspended.equals("Y")){
			suspend = 0;
		}
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			if (db.setAutoCommit(false)) {

				//Need to provide info to the stored procedure to save or update
				LOGGER.info("calling suspend loi");
				CallableStatement cs = db.prepareCall("CALL scaha.setSuspendLoi(?,?)");
				cs.setInt("rosterid", Integer.parseInt(sidplayer));
				cs.setInt("in_suspend", suspend);
				cs.executeQuery();
				LOGGER.info("We have confirmed loi for player id:" + sidplayer);

				db.commit();
				db.cleanup();
			} else {

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Confirming player id " + sidplayer);
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		//this loads the number of completed and total number of lois to be done.
		playersDisplay();
	}

	public void completeTransfer(Coach coach){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			if (db.setAutoCommit(false)) {

				//Need to provide info to the stored procedure to save or update
				LOGGER.info("bcloi calling complete transfer ");
				CallableStatement cs = db.prepareCall("CALL scaha.saveTransfer(?,?,?,?,?,?,?,?,?,?,?)");
				cs.setInt("transferid", coach.getTransferid());
				cs.setInt("playerid", Integer.parseInt(coach.getIdcoach()));
				cs.setInt("transfer", coach.getTransfer());
				cs.setInt("transferindefinite", coach.getTransferindefinite());
				if (coach.getExpirationdate().equals("")){
					cs.setString("sexpirationdate",null);
				}else{
					cs.setString("sexpirationdate",coach.getExpirationdate());
				}
				cs.setString("incitizenship", coach.getCitizenship());
				if (coach.getIs18safesport()){
					cs.setString("safesportindicator_in",coach.getSafesportfor18());
				}else{
					cs.setString("safesportindicator_in",null);
				}
				cs.setInt("suspended_in",Integer.parseInt(coach.getSuspended()));
				cs.setString("innotes",coach.getNotes());
				cs.setInt("inusaroster",coach.getUsaroster());
				cs.setInt("abi",Integer.parseInt(coach.getIsbullying()));
				cs.executeQuery();

				db.commit();
				db.cleanup();

				//logging
				LOGGER.info("We are updating transfer info for player id:" + this.selectedplayer);

				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Successful", "You ave updated the Transfer"));
			} else {

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedplayer);
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		//now lets do the name information
		completeCertificate(coach);
	}

	public void completeCertificate(Coach coach){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			if (db.setAutoCommit(false)) {

				//Need to provide info to the stored procedure to save or update
				LOGGER.info("calling save certificate and dob");
				CallableStatement cs = db.prepareCall("CALL scaha.saveCertificateandDOB(?,?,?,?,?,?,?)");
				cs.setInt("playerid", Integer.parseInt(coach.getIdcoach()));
				cs.setInt("certificate", coach.getBirthcertificate());
				cs.setString("indob", coach.getDob());
				cs.setString("firstname", coach.getFirstname());
				cs.setString("lastname", coach.getLastname());
				cs.setString("inrosterid", coach.getRosterid());
				cs.setInt("pdrapplyin", coach.getPdrapply());
				cs.executeQuery();

				db.commit();

				/*cs = db.prepareCall("CALL scaha.updateteamspder(?,?)");
				cs.setString("inrosterid", coach.getRosterid());
				cs.setInt("pdrapplyin", coach.getPdrapply());

				cs.executeQuery();
				db.commit();*/

				db.cleanup();

				//logging
				LOGGER.info("We are updating birth certificate for player id:" + this.selectedplayer);

				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Successful", "You ave updated the Birth Certificate"));
			} else {

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedplayer);
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
	}
}

