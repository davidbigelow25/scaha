package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Coach;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.Coach;
import com.scaha.objects.CoachDataModel;


//import com.gbli.common.SendMailSSL;


public class quickreviewloiBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private List<Coach> players = null;
    private CoachDataModel PlayerDataModel = null;
    private Coach selectedplayer = null;
	private String selectedtabledisplay = null;
	private List<Club> clubs = null;
	private Boolean displayclublist = null;
	private String selectedclub = null;
	private String selectedplayerid = null;
	private String notes = null;
	private Integer rosteridforconfirm = null;
	private String completedloicount = null;
	private String totalloicount = null;
	
 	@PostConstruct
    public void init() {
		players = new ArrayList<Coach>();
        PlayerDataModel = new CoachDataModel(players);
        this.setSelectedtabledisplay("1");
        
        //this loads the number of completed and total number of lois to be done.
        loadLoiCounts();
        
        //this loads the 10 oldest loi's needing confirmation.
        playersDisplay();
    }
	
    public quickreviewloiBean() {  
         
    }  
    
    public String getCompletedLoiCount(){
    	return completedloicount;
    }
    
    public void setCompletedLoiCount(String value){
    	completedloicount=value;
    }
    
    public String getTotalLoiCount(){
    	return totalloicount;
    }
    
    public void setTotalLoiCount(String value){
    	totalloicount=value;
    }
    public Integer getRosteridforconfirm(){
    	return rosteridforconfirm;
    }
    
    public void setRosteridforconfirm(Integer value){
    	rosteridforconfirm=value;
    }
    
    public String getSelectedplayerid(){
    	return selectedplayerid;
    }
    
    public void setSelectedplayerid(String selidplayer){
    	selectedplayerid=selidplayer;
    }
    
    public String getSelectedclub(){
    	return selectedclub;
    }
    
    public void setSelectedclub(String clubselected){
    	selectedclub=clubselected;
    }
    
    public Boolean getDisplayclublist(){
    	return displayclublist;
    }
    
    public void setDisplayclublist(Boolean club){
    	displayclublist = club;
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
	
	    
    //retrieves list of player, for this page we will load the next 10 loi's to be processed.
	//the logic will be to take the 10 oldest loi's for this season that do not have the 
	//confirmed flag set to 1, notes has values, and/or is active is set to 1 from the roster table 
    public void playersDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Coach> tempresult = new ArrayList<Coach>();
    	java.util.Date date = new java.util.Date();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
				CallableStatement cs = db.prepareCall("CALL scaha.getLatestQuickLois()");
				rs = cs.executeQuery();
    			
				if (rs != null){
    				
    				while (rs.next()) {
    					String idplayer = rs.getString("idperson");
        				String sfirstname = rs.getString("firstname");
        				String slastname = rs.getString("lastname");
        				String scurrentteam = rs.getString("currentteam");
        				String slastyearteam = rs.getString("lastyearteam");
        				if (slastyearteam==null) {
							slastyearteam = "N/A";
						}
        				String sprioryearteam = rs.getString("prioryearteam");
						if (sprioryearteam==null) {
							sprioryearteam = "N/A";
						}
						//this is no longer being used
						//Integer icovid = rs.getInt("covidcount");
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
						String suspended =rs.getString("suspended");
						Integer transferid = rs.getInt("idcitizenshiptransfers");
						Integer transfer = rs.getInt("citizenshiptransfers");
						Integer transferindefinite = rs.getInt("indefinite");
						Integer birthcertificate = rs.getInt("birthcertificate");
						String citizenship = rs.getString("citizenship");
						Boolean is18safesport = rs.getBoolean("is18display");
						String safesportfor18 = rs.getString("safesportfor18");
						String expirationdate = rs.getString("expirationdate");
						Integer usaroster = rs.getInt("usaroster");
						Integer applypdr = rs.getInt("pdrapply");
						String abi = rs.getString("iscahabullying");

						Coach oplayer = new Coach();
        				oplayer.setIdcoach(idplayer);
        				oplayer.setFirstname(sfirstname);
        				oplayer.setLastname(slastname);
        				oplayer.setCurrentteam(scurrentteam);
        				oplayer.setPreviousteam(slastyearteam);
						/*oplayer.setPriorteam(sprioryearteam);*/
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
						oplayer.setPdrapply(applypdr);
						oplayer.setIsbullying(abi);

						tempresult.add(oplayer);

        				oplayer = null;
    				}
    				
    				LOGGER.info("We have results for lois for the lookup date" + date.toString());
    				
    			}
    			rs.close();	
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Loading Quick Loi's for:" + date.toString());
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	//setResults(tempresult);
    	setPlayers(tempresult);
    	PlayerDataModel = new CoachDataModel(players);
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
    
    public List<Club> getListofClubs(){
		List<Club> templist = new ArrayList<Club>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			CallableStatement cs = db.prepareCall("CALL scaha.getClubs()");
    		    rs = cs.executeQuery();
    			
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
    				LOGGER.info("We have results for division list");
    			}
    			rs.close();
    			db.cleanup();
				db.free();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading teams");
    		e.printStackTrace();
    		db.rollback();
			db.free();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setClubs(templist);
		
		return getClubs();
	}
    
    public List<Club> getClubs(){
		return clubs;
	}
	
	public void setClubs(List<Club> list){
		clubs = list;
	}
	
	public void addTransferCitizenship(Coach selectedPlayer){
	
		String sidplayer = selectedPlayer.getIdcoach();
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("addtransfercitizenship.xhtml?page=quick&playerid=" + sidplayer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void voidLoi(Coach selectedPlayer){
		
		String sidplayer = selectedPlayer.getIdcoach();
		String playname = selectedPlayer.getFirstname() + ' ' + selectedPlayer.getLastname();
		
		//need to set to void
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.settoVoid(?)");
    		    cs.setInt("playerid", Integer.parseInt(sidplayer));
    		    cs.executeQuery();
    		    
    		    db.commit();
    			db.cleanup();
 				
    		    //logging 
    			LOGGER.info("We are voiding the loi for player id:" + sidplayer);

    			FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have voided the loi for:" + playname));
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedplayer);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		//this loads the number of completed and total number of lois to be done.
        loadLoiCounts();
		
		//now we need to reload the data object for the loi list
		playersDisplay();
	}
	
	public void viewLoi(Coach selectedPlayer){
		
		String sidplayer = selectedPlayer.getIdcoach();
		String sidroster = selectedPlayer.getRosterid();
				
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("scahaviewloi.xhtml?page=quick&playerid=" + sidplayer + "&rid=" + sidroster);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void confirmLoi(Coach selectedPlayer){
	
		String sidplayer = selectedPlayer.getRosterid();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("verify loi code provided");
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
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		//this loads the number of completed and total number of lois to be done.
        loadLoiCounts();
		playersDisplay();
	}
	
	public void CloseLoi(){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("confirmlois.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void confirmLoifromview(Integer sidplayer){
		
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
			} finally {
				//
				// always clean up after yourself..
				//
				db.free();
			}
		
			FacesContext context = FacesContext.getCurrentInstance();
			try{
				context.getExternalContext().redirect("confirmlois.xhtml");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	
	public void loadLoiCounts(){
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("loading loi counts");
 				CallableStatement cs = db.prepareCall("CALL scaha.loadLoiCounts()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					this.completedloicount = rs.getString("completedcount");
        				this.totalloicount = rs.getString("totalcount");
        			}
    				LOGGER.info("We have results for loi counts");
    			}
    			rs.close();
    			db.cleanup();
    		
    		    
 			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading loi counts");
			e.printStackTrace();
			db.rollback();
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
				LOGGER.info("verify loi code provided");
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
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		//this loads the number of completed and total number of lois to be done.
		loadLoiCounts();
		playersDisplay();
	}

	public void completeTransfer(Coach coach){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			if (db.setAutoCommit(false)) {

				//Need to provide info to the stored procedure to save or update
				//LOGGER.info("verify loi code provided");
				CallableStatement cs = db.prepareCall("CALL scaha.saveTransferforManageRoster(?,?,?,?,?,?,?,?,?,?,?)");
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
				cs.setInt("inabix",Integer.parseInt(coach.getIsbullying()));
				cs.executeQuery();

				db.commit();
				db.cleanup();

				//logging
				//LOGGER.info("We are updating transfer info for player id:" + this.selectedplayer);

				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Successful", "You ave updated the Transfer"));
			} else {

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedplayer);
			e.printStackTrace();
			db.rollback();
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
				//LOGGER.info("verify loi code provided");
				CallableStatement cs = db.prepareCall("CALL scaha.saveCertificateandDOB(?,?,?,?,?,?,?)");
				cs.setInt("playerid", Integer.parseInt(coach.getIdcoach()));
				cs.setInt("certificate", coach.getBirthcertificate());
				cs.setString("indob", coach.getDob());
				cs.setString("firstname", coach.getFirstname());
				cs.setString("lastname", coach.getLastname());
				cs.setInt("inrosterid",Integer.parseInt(coach.getRosterid()));
				cs.setInt("pdrapplyin", coach.getPdrapply());
				cs.executeQuery();

				db.commit();
				db.cleanup();

				//logging
				//LOGGER.info("We are updating birth certificate for player id:" + this.selectedplayer);

				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Successful", "You ave updated the Birth Certificate"));
			} else {

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedplayer);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
	}

	public void editrosterdetail(Player splayer, String teamid){
		String idroster = splayer.getIdplayer();

		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editrosterdetail.xhtml?playerid=" + idroster + "&teamid=" + teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

