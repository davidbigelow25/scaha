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

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Coach;
import com.scaha.objects.CoachDataModel;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;

//import com.gbli.common.SendMailSSL;


public class quickreviewcoachloiBean implements Serializable {

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
	private String completedloicount = null;
	private String totalloicount = null;

	
	@PostConstruct
    public void init() {
	    coaches = new ArrayList<Coach>();  
        CoachDataModel = new CoachDataModel(coaches);
        this.setSelectedtabledisplay("1");
        
        //this loads the number of completed and total number of lois to be done.
        loadLoiCounts();
        
        //lets load the next ten oldest coach loi's
        coachesDisplay(); 
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
	
	    
	//retrieves list of coaches, for this page we will load the next 10 loi's to be processed.
	//the logic will be to take the 10 oldest loi's for this season that do not have the 
	//confirmed flag set to 1, notes has values, and/or is active is set to 1 from the roster table 
	    
    public void coachesDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Coach> tempresult = new ArrayList<Coach>();
    	java.util.Date date = new java.util.Date();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
				CallableStatement cs = db.prepareCall("CALL scaha.getLatestQuickCoachLois()");
    			rs = cs.executeQuery();
    			
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
        				String sportexpires = rs.getString("sportexpires");
        				String suspended =rs.getString("suspended");
						Integer usaroster = rs.getInt("usaroster");
						String coachrole = rs.getString("rostertype");
        				String abi = rs.getString("abi");

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
						ocoach.setSafesportforcoachlist(Integer.parseInt(safesport));
        				ocoach.setConfirmed(confirmed);
        				ocoach.setNotes(notes);
        				ocoach.setSportexpires(sportexpires);
        				ocoach.setSuspended(suspended);
						if (suspended.equals("Y")){
							ocoach.setSuspend("1");
						}
						else {
							ocoach.setSuspend("0");
						}


						String templist = "";
						if (ocoach.getU8().equals("Yes")){
							templist = templist.concat("8U");
						}
						if (ocoach.getU10().equals("Yes")){
							templist = templist.concat(",10U");
						}
						if (ocoach.getU12().equals("Yes")){
							templist = templist.concat(",12U");
						}
						if (ocoach.getU14().equals("Yes")){
							templist = templist.concat(",14U");
						}
						if (ocoach.getU18().equals("Yes")){
							templist = templist.concat(",18U");
						}
						if (ocoach.getGirls().equals("Yes")){
							templist = templist.concat(",Girls");
						}
						ocoach.setCepmodulesselected(templist);
						ocoach.setUsaroster(usaroster);
						ocoach.setTeamrole(coachrole);
						ocoach.setIsbullying(abi);

						tempresult.add(ocoach);

        				ocoach = null;
    				}
    				rs.close();
    				LOGGER.info("We have results for last 10 lois");
    				
    			}
    				
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR last 10 coach Lois");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
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
	
	public void voidLoi(Coach selectedPlayer){
		
		String sidcoach = selectedPlayer.getIdcoach();
		String coachname = selectedPlayer.getFirstname() + " " + selectedPlayer.getLastname();
		
		//need to set to void
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.setcoachtoVoid(?)");
    		    cs.setInt("coachid", Integer.parseInt(sidcoach));
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    		    //logging 
    			LOGGER.info("We are voiding the loi for coach id:" + sidcoach);
    		    
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
 				LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.getCoachIdByCoachRosterId(?)");
    		    cs.setInt("coachrosterid", Integer.parseInt(sidcoach));
    		    rs=cs.executeQuery();
    		    
    		    if (rs != null){
    				
    				while (rs.next()) {
    					Integer idplayer = rs.getInt("idcoach");
    					sidcoach = idplayer.toString();
        			}
    				LOGGER.info("We have results for person id by coach");
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
			context.getExternalContext().redirect("addcoachdetails.xhtml?page=quick&coachid=" + sidcoach + "&rosterid=" + rosterid);
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
 				LOGGER.info("retrieve person id from the roster id");
 				CallableStatement cs = db.prepareCall("CALL scaha.getPersonIdByRosterId(?)");
    		    cs.setInt("rosterid", Integer.parseInt(sidcoach));
    		    rs=cs.executeQuery();
    		    
    		    if (rs != null){
    				
    				while (rs.next()) {
    					Integer idplayer = rs.getInt("idperson");
    					sidcoach = idplayer.toString();
        			}
    				LOGGER.info("We have results for person id by coach");
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
			context.getExternalContext().redirect("scahaviewcoachloi.xhtml?page=quick&coachid=" + sidcoach);
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
 				LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.confirmCoachLoi(?)");
    		    cs.setInt("icoachid", Integer.parseInt(sidcoach));
    		    cs.executeQuery();
    		    LOGGER.info("We have confirmed loi for coach id:" + sidcoach);
    			
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
			context.getExternalContext().redirect("confirmcoachlois.xhtml");
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
 				LOGGER.info("confirming coach");
 				CallableStatement cs = db.prepareCall("CALL scaha.confirmCoachLoi(?)");
    		    cs.setInt("icoachid", Integer.parseInt(scoachid));
    		    cs.executeQuery();
    		    LOGGER.info("We have confirmed loi for coach id:" + scoachid);
    			
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
	
public void loadLoiCounts(){
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("loading loi counts");
 				CallableStatement cs = db.prepareCall("CALL scaha.loadcoachLoiCounts()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					this.completedloicount = rs.getString("completedcount");
        				this.totalloicount = rs.getString("totalcount");
        			}
    				LOGGER.info("We have results for coach loi counts");
    			}
    			rs.close();
    			db.cleanup();
    		
    		    
 			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading coach loi counts");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
	
		
	}

	public void updateCoach(Coach coach){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			if (db.setAutoCommit(false)) {

				//Need to provide info to the stored procedure to save or update
				LOGGER.info("update coach details");
				CallableStatement cs = db.prepareCall("CALL scaha.updateCoachbyCoachIdforlist(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				cs.setInt("coachid", Integer.parseInt(coach.getIdcoach()));
				cs.setString("screenexpires", coach.getScreeningexpires());
				cs.setString("cepnum", coach.getCepnumber());
				cs.setInt("levelcep", Integer.parseInt(coach.getCeplevel()));
				cs.setString("cepexpire", coach.getCepexpires());
				cs.setInt("insafesport",coach.getSafesportforcoachlist());
				cs.setString("inrostertype", coach.getTeamrole());
				cs.setInt("inrosterid", Integer.parseInt(coach.getIdcoach()));
				//need to set values for modules
				Integer u8 = 0;
				Integer u10 = 0;
				Integer u12 = 0;
				Integer u14 = 0;
				Integer u18 = 0;
				Integer ugirls = 0;

				List<String> cepmodulesselectedstring = Arrays.asList(coach.getCepmodulesselected().split(","));
				for (int i = 0; i < cepmodulesselectedstring.size(); i++) {
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("8U")){
						u8 = 1;
					}
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("10U")){
						u10 = 1;
					}
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("12U")){
						u12 = 1;
					}
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("14U")){
						u14 = 1;
					}
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("18U")){
						u18 = 1;
					}
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("Girls")){
						ugirls = 1;
					}
				}
				cs.setInt("u8", u8);
				cs.setInt("u10", u10);
				cs.setInt("u12", u12);
				cs.setInt("u14", u14);
				cs.setInt("u18", u18);
				cs.setInt("ugirls", ugirls);
				cs.setString("infirstname",coach.getFirstname());
				cs.setString("inlastname",coach.getLastname());
				cs.setString("sportexpires", coach.getSportexpires());
				cs.setInt("issuspend_in", Integer.parseInt(coach.getSuspend()));
				cs.setInt("inusaroster",coach.getUsaroster());
				cs.setString("inteamrole",coach.getTeamrole());
				cs.setInt("abi",Integer.parseInt(coach.getIsbullying()));
				rs = cs.executeQuery();

				db.commit();
				rs.close();

				db.cleanup();

				//logging
				LOGGER.info("We are updating transfer info for coach id:" + coach);



				this.coachesDisplay();


			} else {

			}

		} catch (SQLException e) {
			/*TODO Auto-generated catch block*/
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedcoach);
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

