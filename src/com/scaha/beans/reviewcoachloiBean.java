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
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Coach;
import com.scaha.objects.CoachDataModel;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;

//import com.gbli.common.SendMailSSL;


public class reviewcoachloiBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private List<Coach> coaches = null;
    private CoachDataModel CoachDataModel = null;
    private Coach selectedcoach = null;
	private String selectedtabledisplay = null;
	private List<Club> clubs = null;
	private Boolean displayclublist = null;
	private String selectedclub = null;
	private String selectedcoachid = null;
	private String page = "";
	private String searchcriteria = "";
	
	@PostConstruct
    public void init() {
	    coaches = new ArrayList<Coach>();  
        CoachDataModel = new CoachDataModel(coaches);
        this.setSelectedtabledisplay("1");
      
        HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      	
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
          	
        
        coachesDisplay(); 
    }  
    
	public String getSearchcriteria(){
		return searchcriteria;
	}
	
	public void setSearchcriteria(String cyear){
		searchcriteria=cyear;
	}
    
	public String getPage(){
		return page;
	}
	
	public void setPage(String cyear){
		page=cyear;
	}
    
    
    public String getSelectedcoachid(){
    	return selectedcoachid;
    }
    
    public void setSelectedcoachid(String selidplayer){
    	selectedcoachid=selidplayer;
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
    
    public Coach getSelectedcoach(){
		return selectedcoach;
	}
	
	public void setSelectedcoach(Coach selectedPlayer){
		selectedcoach = selectedPlayer;
	}
    
	public List<Coach> getCoaches(){
		return coaches;
	}
	
	public void setCoaches(List<Coach> list){
		coaches = list;
	}
	
	    
    //retrieves list of players
    public void coachesDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Coach> tempresult = new ArrayList<Coach>();
    	java.util.Date date = new java.util.Date();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
    			if (selectedtabledisplay.equals("1")){
    				CallableStatement cs = db.prepareCall("CALL scaha.getCoachLoiListByClub(?,?)");
	    			java.sql.Date sqlDate = new java.sql.Date(date.getTime());
	    			if (this.selectedclub==null){
	    				cs.setInt("clubid", 0);
	    			}else {
	    				cs.setInt("clubid", Integer.parseInt(this.selectedclub));
	    			}
	    			
	    			cs.setDate("lookupdate", sqlDate);
	    			rs = cs.executeQuery();
    			} else if (selectedtabledisplay.equals("4")){
    				CallableStatement cs = db.prepareCall("CALL scaha.getCoachLoiByClub(?)");
	    			cs.setInt("clubid", Integer.parseInt(this.selectedclub));
    				rs = cs.executeQuery();
    			} else if (selectedtabledisplay.equals("5")){
					CallableStatement cs = db.prepareCall("CALL scaha.getAllCoachLoiByClub(?)");
					if (this.selectedclub==null){
	    				cs.setInt("clubid", 0);
	    			}else {
	    				cs.setInt("clubid", Integer.parseInt(this.selectedclub));
	    			}
	    			rs = cs.executeQuery();
				}
		    
    			
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					String idcoach = rs.getString("idroster");
        				String sfirstname = rs.getString("fname");
        				String slastname = rs.getString("lname");
        				String steam = rs.getString("teamname");
        				String sloidate = rs.getString("loidate");
        				String screeningexpires = rs.getString("screeningexpires");
        				String cepnumber = rs.getString("cepnumber");
        				String ceplevel = rs.getString("ceplevel");
        				String cepexpires = rs.getString("cepexpires");
        				String u8 = rs.getString("eightu");
        				String u10 = rs.getString("tenu");
        				String u12 = rs.getString("twelveu");
        				String u14 = rs.getString("fourteenu");
        				String u18 = rs.getString("eighteenu");
        				String girls = rs.getString("girls");
        				String safesport = rs.getString("safesport");
        				String confirmed = rs.getString("confirmed");
        				String notes = rs.getString("notes");
        				
        				Coach ocoach = new Coach();
        				ocoach.setIdcoach(idcoach);
        				ocoach.setFirstname(sfirstname);
        				ocoach.setLastname(slastname);
        				ocoach.setLoidate(sloidate);
        				ocoach.setTeamname(steam);
        				ocoach.setScreeningexpires(screeningexpires);
        				ocoach.setCepnumber(cepnumber);
        				ocoach.setCeplevel(ceplevel);
        				ocoach.setCepexpires(cepexpires);
        				ocoach.setU8(u8);
        				ocoach.setU10(u10);
        				ocoach.setU12(u12);
        				ocoach.setU14(u14);
        				ocoach.setU18(u18);
        				ocoach.setGirls(girls);
        				ocoach.setSafesport(safesport);
        				ocoach.setConfirmed(confirmed);
        				ocoach.setNotes(notes);
        				tempresult.add(ocoach);
    				}
    				rs.close();
    				//LOGGER.info("We have results for lois for the lookup date" + date.toString());
    				
    			}
    				
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
    	setCoaches(tempresult);
    	CoachDataModel = new CoachDataModel(coaches);
    }

    public CoachDataModel getCoachdatamodel(){
    	return CoachDataModel;
    }
    
    public void setCoachdatamodel(CoachDataModel odatamodel){
    	CoachDataModel = odatamodel;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    public List<Club> getListofClubs(){
		List<Club> templist = new ArrayList<Club>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			//Vector<Integer> v = new Vector<Integer>();
    			//v.add(1);
    			//db.getData("CALL scaha.getTeamsByClub(?)", v);
    		    CallableStatement cs = db.prepareCall("CALL scaha.getClubs()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				//need to add to an array
    				//rs = db.getResultSet();
    				
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
	
	public void voidLoi(Coach selectedPlayer){
		
		String sidcoach = selectedPlayer.getIdcoach();
		String coachname = selectedPlayer.getFirstname() + " " + selectedPlayer.getLastname();
		
		//need to set to void
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.setcoachtoVoid(?)");
    		    cs.setInt("coachid", Integer.parseInt(sidcoach));
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    		    //logging 
    			//LOGGER.info("We are voiding the loi for coach id:" + sidcoach);
    		    
    			FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have voided the loi for: " + coachname));
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedcoach);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		this.coachesDisplay();
	}

	public void addCoachdetails(Coach selectedCoach){
		
		String sidcoach = selectedCoach.getIdcoach();
		String rosterid = sidcoach;
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.getCoachIdByCoachRosterId(?)");
    		    cs.setInt("coachrosterid", Integer.parseInt(sidcoach));
    		    rs=cs.executeQuery();
    		    
    		    if (rs != null){
    				
    				while (rs.next()) {
    					Integer idplayer = rs.getInt("idcoach");
    					sidcoach = idplayer.toString();
        			}
    				//LOGGER.info("We have results for person id by coach");
    			}
    			rs.close();
    		    db.commit();
    			db.cleanup();
 			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Retrieving PersonId" + this.selectedcoach);
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
			context.getExternalContext().redirect("addcoachdetails.xhtml?coachid=" + sidcoach + "&rosterid=" + rosterid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public void viewLoi(Coach selectedCoach){
		
		String sidcoach = selectedCoach.getIdcoach();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.getPersonIdbyCoachId(?)");
    		    cs.setInt("icoachid", Integer.parseInt(sidcoach));
    		    rs=cs.executeQuery();
    		    
    		    if (rs != null){
    				
    				while (rs.next()) {
    					Integer idplayer = rs.getInt("idperson");
    					sidcoach = idplayer.toString();
        			}
    				//LOGGER.info("We have results for person id by coach");
    			}
    		    rs.close();
    			db.commit();
    			db.cleanup();
 			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Retrieving PersonId" + this.selectedcoach);
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
			context.getExternalContext().redirect("scahaviewcoachloi.xhtml?coachid=" + sidcoach);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
public void confirmLoi(Coach selectedCoach){
		
		String sidcoach = selectedCoach.getIdcoach();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.confirmCoachLoi(?)");
    		    cs.setInt("icoachid", Integer.parseInt(sidcoach));
    		    cs.executeQuery();
    		    //LOGGER.info("We have confirmed loi for coach id:" + sidcoach);
    			
    			db.commit();
    			db.cleanup();
 			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Confirming coach id " + sidcoach);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		coachesDisplay();
	}
	
	public void CloseLoi(){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			if (page.equals("bcloi")){
				context.getExternalContext().redirect("workwithcoaches.xhtml?search=" + this.searchcriteria);
			}else {
				context.getExternalContext().redirect("confirmcoachlois.xhtml");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void confirmLoiFromViewLoi(String scoachid){
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("confirming coach");
 				CallableStatement cs = db.prepareCall("CALL scaha.confirmCoachLoi(?)");
    		    cs.setInt("icoachid", Integer.parseInt(scoachid));
    		    cs.executeQuery();
    		    //LOGGER.info("We have confirmed loi for coach id:" + scoachid);
    			
    			db.commit();
    			db.cleanup();
 			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Confirming coach id " + scoachid);
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
			context.getExternalContext().redirect("confirmcoachlois.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void CloseLoi(String spage, String searchriteria){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			if (spage.equals("bcloi")){
				context.getExternalContext().redirect("workwithcoaches.xhtml?search=" + searchriteria);
			}
			else{
				context.getExternalContext().redirect("quickcoachloiconfirm.xhtml");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

