package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Coach;
import com.scaha.objects.CoachDataModel;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;

//import com.gbli.common.SendMailSSL;


public class coachbcloiBean implements Serializable, MailableObject {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/voidloi.html");
	transient private ResultSet rs = null;
	transient private ResultSet rs2 = null;
	private List<Coach> coaches = null;
    private CoachDataModel CoachDataModel = null;
    private Coach selectedcoach = null;
	private String selectedtabledisplay = null;
	private String selectedcoachid = null;
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
		coaches = new ArrayList<Coach>();  
        CoachDataModel = new CoachDataModel(coaches);
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
    		searchcriteria = hsr.getParameter("search").toString();
        }else{
        	searchcriteria = "";
        }
        
        if (!searchcriteria.equals("")){
        	coachesDisplay();
        }
    }
	
    public coachbcloiBean() {  
         
    }  
    
    public String getSelectedcoachid(){
    	return selectedcoachid;
    }
    
    public void setSelectedcoachid(String selidplayer){
    	selectedcoachid=selidplayer;
    }
    
    public List<Coach> getCoaches(){
		return coaches;
	}
	
	public void setCoaches(List<Coach> list){
		coaches = list;
	}
	
    public String getClubname(){
    	return clubname;
    }
    
    public CoachDataModel getCoachdatamodel(){
    	return CoachDataModel;
    }
    
    public void setCoachdatamodel(CoachDataModel odatamodel){
    	CoachDataModel = odatamodel;
    }
    
    public Coach getSelectedcoach(){
		return selectedcoach;
	}
	
	public void setSelectedcoach(Coach selectedPlayer){
		selectedcoach = selectedPlayer;
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
    
    
    public String getSelectedtabledisplay(){
		return selectedtabledisplay;
	}
	
	public void setSelectedtabledisplay(String selectedTabledisplay){
		selectedtabledisplay = selectedTabledisplay;
	}
    
    //retrieves list of coaches
    public void coachesDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Coach> tempresult = new ArrayList<Coach>();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
				CallableStatement cs = db.prepareCall("CALL scaha.getCoachLoiListByNameSearch(?)");
    			cs.setString("search", this.searchcriteria);
    			rs2 = cs.executeQuery();
    			
    		    if (rs2 != null){
    				while (rs2.next()) {
    					String idcoach = rs2.getString("idroster");
        				String sfirstname = rs2.getString("fname");
        				String slastname = rs2.getString("lname");
        				String steam = rs2.getString("teamname");
        				String sloidate = rs2.getString("loidate");
        				String screeningexpires = rs2.getString("screeningexpires");
        				String cepnumber = rs2.getString("cepnumber");
        				String ceplevel = rs2.getString("ceplevel");
        				String cepexpires = rs2.getString("cepexpires");
        				String u8 = rs2.getString("eightu");
        				String u10 = rs2.getString("tenu");
        				String u12 = rs2.getString("twelveu");
        				String u14 = rs2.getString("fourteenu");
        				String u18 = rs2.getString("eighteenu");
        				String girls = rs2.getString("girls");
        				Integer safesport = rs2.getInt("safesport");
        				String confirmed = rs2.getString("confirmed");
        				String notes = rs2.getString("notes");
        				String sportexpires = rs2.getString("sportexpires");
						String suspended =rs2.getString("suspended");

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
        				ocoach.setSafesportforcoachlist(safesport);
        				ocoach.setConfirmed(confirmed);
        				ocoach.setNotes(notes);
        				ocoach.setSportexpires(sportexpires);
        				ocoach.setSuspended(suspended);
						ocoach.setSuspend(suspended);


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

						tempresult.add(ocoach);

        				ocoach = null;
        				templist=null;
    				}
    				
    				LOGGER.info("We have results for coach bc lois for the search criteria:" + this.searchcriteria);
    				
    			}
				rs2.close();
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR coach bc Lois for search criteria:" + this.searchcriteria);
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
    	tempresult=null;
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
			context.getExternalContext().redirect("addcoachdetails.xhtml?page=bcloi&coachid=" + sidcoach + "&rosterid=" + rosterid + "&search=" + this.searchcriteria);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
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
	
    public void viewLoi(Coach selectedCoach){
		
		String sidcoach = selectedCoach.getIdcoach();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.getPersonIdbyCoachId(?)");
    		    cs.setInt("icoachid", Integer.parseInt(sidcoach));
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
			context.getExternalContext().redirect("scahaviewcoachloi.xhtml?page=bcloi&coachid=" + sidcoach + "&search=" + this.searchcriteria);
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
	//	myTokens.add("FIRSTNAME:" + this.selectedplayer.getFirstname());
//		myTokens.add("LASTNAME:" + this.selectedplayer.getLastname());
		myTokens.add("CLUBNAME:" + this.getClubname());
		
		return Utils.mergeTokens(coachbcloiBean.mail_reg_body,myTokens);
		
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
				CallableStatement cs = db.prepareCall("CALL scaha.updateCoachbyCoachIdforlist(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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

