package com.scaha.beans;

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


public class reviewloiBean implements Serializable, MailableObject {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/voidloi.html");
	transient private ResultSet rs = null;
	private List<Player> players = null;
    private PlayerDataModel PlayerDataModel = null;
    private Player selectedplayer = null;
	private String selectedtabledisplay = null;
	private List<Club> clubs = null;
	private Boolean displayclublist = null;
	private String selectedclub = null;
	private String selectedplayerid = null;
	private String notes = null;
	private Integer rosteridforconfirm = null;
	private String page = null;
	private String to = null;
	private String subject = null;
	private String cc = null;
	private Integer clubid = null;
	private String clubname = null;
	private String search = null;
	
 	@PostConstruct
    public void init() {
		players = new ArrayList<Player>();  
        PlayerDataModel = new PlayerDataModel(players);
        this.setSelectedtabledisplay("1");
        
      //will need to load player profile information for displaying loi
      	HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
          	
        if(hsr.getParameter("page") != null)
        {
    		page = hsr.getParameter("page").toString();
        }else{
        	page = "";
        }
        if(hsr.getParameter("search") != null)
        {
    		search = hsr.getParameter("search").toString();
        }else{
        	search = "";
        }
        playersDisplay();
        
    }
	
    public reviewloiBean() {  
         
    }  
    
    public String getSearch(){
    	return search;
    }
    
    public void setSearch(String value){
    	search = value;
    }
    
    public Integer getClubid(){
    	return clubid;
    }
    
    public void setClubid(Integer sclub){
    	clubid = sclub;
    }
    
    public String getClubname() {
		// TODO Auto-generated method stub
		return clubname;
	}
    
    public void setClubname(String ssubject){
    	clubname = ssubject;
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
    
    public Player getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Player selectedPlayer){
		selectedplayer = selectedPlayer;
	}
    
	public List<Player> getPlayers(){
		return players;
	}
	
	public void setPlayers(List<Player> list){
		players = list;
	}
	
	    
    //retrieves list of players
    public void playersDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Player> tempresult = new ArrayList<Player>();
    	java.util.Date date = new java.util.Date();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
    			if (selectedtabledisplay.equals("1")){
    				CallableStatement cs = db.prepareCall("CALL scaha.getLoiListByClub(?,?)");
	    			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
	    			cs.setDate("lookupdate", sqlDate);
	    			if (this.selectedclub==null){
	    				cs.setInt("clubid", 0);
	    			}else {
	    				cs.setInt("clubid", Integer.parseInt(this.selectedclub));
	    			}
	    			
	    			rs = cs.executeQuery();
    			} else if (selectedtabledisplay.equals("2")){
    				CallableStatement cs = db.prepareCall("CALL scaha.getLoiMissingBirthCertificateByClub(?)");
    				if (this.selectedclub==null){
	    				cs.setInt("clubid", 0);
	    			}else {
	    				cs.setInt("clubid", Integer.parseInt(this.selectedclub));
	    			}
	    			
	    			rs = cs.executeQuery();
    			} else if (selectedtabledisplay.equals("3")){
    				CallableStatement cs = db.prepareCall("CALL scaha.getLoiMissingTransferByClub(?)");
    				if (this.selectedclub==null){
	    				cs.setInt("clubid", 0);
	    			}else {
	    				cs.setInt("clubid", Integer.parseInt(this.selectedclub));
	    			}
	    			
	    			rs = cs.executeQuery();
    			} else if (selectedtabledisplay.equals("4")){
    				CallableStatement cs = db.prepareCall("CALL scaha.getLoiByClub(?)");
	    			cs.setInt("clubid", Integer.parseInt(this.selectedclub));
    				rs = cs.executeQuery();
    			} else if (selectedtabledisplay.equals("5")){
    				CallableStatement cs = db.prepareCall("CALL scaha.getAllLoiByClub(?)");
    				if (this.selectedclub==null){
	    				cs.setInt("clubid", 0);
	    			}else {
	    				cs.setInt("clubid", Integer.parseInt(this.selectedclub));
	    			}
    				rs = cs.executeQuery();
    				
	    			
    			}
    		    
    		    
    			if (rs != null){
    				
    				while (rs.next()) {
    					String idplayer = rs.getString("idperson");
        				String sfirstname = rs.getString("firstname");
        				String slastname = rs.getString("lastname");
        				String scurrentteam = rs.getString("currentteam");
        				String slastyearteam = rs.getString("lastyearteam");
        				if (slastyearteam==null){
        					slastyearteam="N/A";
        				}
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
        				
        				Player oplayer = new Player();
        				oplayer.setIdplayer(idplayer);
        				oplayer.setFirstname(sfirstname);
        				oplayer.setLastname(slastname);
        				oplayer.setCurrentteam(scurrentteam);
        				oplayer.setPreviousteam(slastyearteam);
        				oplayer.setDob(sdob);
        				oplayer.setCitizenship(scitizenship);
        				oplayer.setCitizenshiptransfer(scitizenshiptransfer);
        				oplayer.setCtverified(scitizenshiptransfer);
        				oplayer.setCTExpirationdate(scitizenshipexpiredate);
        				oplayer.setNotes(notes);
        				
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
        				
        				oplayer.setBirthcertificate(sbirthcertificate);
        				oplayer.setBcverified(sbirthcertificate);
        				oplayer.setLoidate(sloidate);
        				oplayer.setPlayerup(IsPlayerup(splayerup));
        				oplayer.setConfirmed(confirmed);
        				oplayer.setRosterid(rosterid);
        				tempresult.add(oplayer);
    				}
    				
    				//LOGGER.info("We have results for lois for the lookup date" + date.toString());
    				
    			}
    			rs.close();	
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR Lois for lookup date:" + date.toString());
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
    	PlayerDataModel = new PlayerDataModel(players);
    }

    public PlayerDataModel getPlayerdatamodel(){
    	return PlayerDataModel;
    }
    
    public void setPlayerdatamodel(PlayerDataModel odatamodel){
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
    				}
    				//LOGGER.info("We have results for division list");
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
		
    	setClubs(templist);
		
		return getClubs();
	}
    
    public List<Club> getClubs(){
		return clubs;
	}
	
	public void setClubs(List<Club> list){
		clubs = list;
	}
	
	public void addTransferCitizenship(Player selectedPlayer){
	
		String sidplayer = selectedPlayer.getIdplayer();
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("addtransfercitizenship.xhtml?playerid=" + sidplayer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void voidLoi(Player selectedPlayer){
		
		String sidplayer = selectedPlayer.getIdplayer();
		String playname = selectedPlayer.getFirstname() + ' ' + selectedPlayer.getLastname();
		this.setSelectedplayer(selectedPlayer);
		
		getClubID(sidplayer);
		//need to set to void
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.settoVoid(?)");
    		    cs.setInt("playerid", Integer.parseInt(sidplayer));
    		    cs.executeQuery();
    		    
    		    db.commit();
    			db.cleanup();
 				
    		    //logging 
    			//LOGGER.info("We are voiding the loi for player id:" + sidplayer);
    		    
    			//need to send an email acknowledging the loi is voided.
    			to = "";
    			//LOGGER.info("Sending void loi email to club registrar, and family");
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
    		    this.setToMailAddress(to);
    		    this.setPreApprovedCC("");
    		    this.setSubject(this.selectedplayer.getFirstname() + " " + this.selectedplayer.getLastname() + " LOI Void with " + this.getClubname());
    		    
				SendMailSSL mail = new SendMailSSL(this);
				//LOGGER.info("Finished creating mail object for " + this.selectedplayer.getFirstname() + " " + this.selectedplayer.getLastname() + " LOI Void with " + this.getClubname());
				mail.sendMail();
				
    			
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
		
		//now we need to reload the data object for the loi list
		playersDisplay();
	}
	
	public void viewLoi(Player selectedPlayer){
		
		String sidplayer = selectedPlayer.getIdplayer();
		String sidroster = selectedPlayer.getRosterid();
				
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("scahaviewloi.xhtml?playerid=" + sidplayer + "&rid=" + sidroster);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void confirmLoi(Player selectedPlayer){
	
	String sidplayer = selectedPlayer.getRosterid();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.confirmCoachLoi(?)");
    		    cs.setInt("icoachid", Integer.parseInt(sidplayer));
    		    cs.executeQuery();
    		    //LOGGER.info("We have confirmed loi for player id:" + sidplayer);
    			
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
		
		playersDisplay();
	}
	
	public void CloseLoi(String spage){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			if (spage.equals("bcloi")){
				context.getExternalContext().redirect("workwithbirthcertificate.xhtml?search=" + this.search);
			}
			else if (spage.equals("quick")){
				context.getExternalContext().redirect("quickplayerloiconfirm.xhtml");
			}else{
				context.getExternalContext().redirect("confirmlois.xhtml");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void CloseLoi(String spage, String ssearch){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			if (spage.equals("bcloi")){
				context.getExternalContext().redirect("workwithbirthcertificate.xhtml?search=" + ssearch);
			}
			else if (spage.equals("quick")){
				context.getExternalContext().redirect("quickplayerloiconfirm.xhtml");
			}else{
				context.getExternalContext().redirect("confirmlois.xhtml");
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
	 				//LOGGER.info("confirming player :" + sidplayer);
	 				CallableStatement cs = db.prepareCall("CALL scaha.confirmCoachLoi(?)");
	    		    cs.setInt("icoachid", sidplayer);
	    		    cs.executeQuery();
	    		    //LOGGER.info("We have confirmed loi for player id:" + sidplayer.toString());
	    			
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
				if (spage.equals("quick")){
					context.getExternalContext().redirect("quickplayerloiconfirm.xhtml");
				}else{
					context.getExternalContext().redirect("confirmlois.xhtml");
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}

	public void confirmLoifromview(Integer sidplayer,String spage, String ssearch){
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("confirming player :" + sidplayer);
 				CallableStatement cs = db.prepareCall("CALL scaha.confirmCoachLoi(?)");
    		    cs.setInt("icoachid", sidplayer);
    		    cs.executeQuery();
    		    //LOGGER.info("We have confirmed loi for player id:" + sidplayer.toString());
    			
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
			if (spage.equals("bcloi")){
				context.getExternalContext().redirect("workwithbirthcertificate.xhtml?search=" + ssearch);
			}
			else if (spage.equals("quick")){
				context.getExternalContext().redirect("quickplayerloiconfirm.xhtml");
			}else{
				context.getExternalContext().redirect("confirmlois.xhtml");
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
		
		return Utils.mergeTokens(reviewloiBean.mail_reg_body,myTokens);
		
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

    }
}

