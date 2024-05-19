package com.scaha.beans;

import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

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
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import com.scaha.objects.Coach;


//import com.gbli.common.SendMailSSL;

public class cahabullyingbean implements Serializable{

//public class  implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	transient private ResultSet rs2 = null;
	private List<Coach> coaches = null;
	private List<Coach> coacheswithissues = null;
	private Coach selectedcoach = null;



	@PostConstruct
    public void init() {

		loadProcessedwithIssues();
    }

    public cahabullyingbean() {
    	    	
		
    }


    
    public void processBullyinglist(){
		List<Coach> templist = new ArrayList<Coach>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getlatestbullying()");
			CallableStatement cs2 = db.prepareCall("CALL scaha.updaterosterforbullying(?)");

			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer rosterid = rs.getInt("idroster");
					cs2.setInt("rosterid",rosterid);
					rs2 = cs2.executeQuery();
					LOGGER.info("We have results for roster id" + rosterid);

				}

			}
			rs2.close();
			rs.close();
			db.cleanup();
    		

    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN process bullying records");
    		e.printStackTrace();
    		db.rollback();
			db.free();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	loadProcessed();
	}

	public void loadProcessed() {
		List<Coach> templist = new ArrayList<Coach>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try {
			//next get coach roster
			CallableStatement cs = db.prepareCall("CALL scaha.getprocessedbullying()");
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					String rosterid = rs.getString("idroster");
					String cfname = rs.getString("fname");
					String clname = rs.getString("lname");
					String teamname = rs.getString("teamname");
					String completeddate = rs.getString("completeddate");



					Coach coach = new Coach();
					coach.setIdcoach(rosterid);
					coach.setFirstname(cfname);
					coach.setLastname(clname);
					coach.setEligibility(completeddate);
					coach.setNotes(teamname);

					templist.add(coach);
					coach = null;
				}
				LOGGER.info("We have results for list of processed bullying");
			}
			rs.close();
			db.cleanup();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading processed bullying");
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		setCoaches(templist);
		templist=null;

	}

	public void loadProcessedwithIssues() {
		List<Coach> templist = new ArrayList<Coach>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try {
			//next get coach roster
			CallableStatement cs = db.prepareCall("CALL scaha.getbullyingwithissues()");
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					String rosterid = rs.getString("idroster");
					String cfname = rs.getString("fname");
					String clname = rs.getString("lname");
					String teamname = rs.getString("teamname");
					String dob = rs.getString("dob");
					String completeddate = rs.getString("completeddate");
					String loadedfirstname = rs.getString("loadedfirstname");
					String loadedlastname = rs.getString("loadedlastname");
					String loadeddob = rs.getString("loadeddob");
					String rostertype = rs.getString("rostertype");
					String usahockeynumber = rs.getString("usahockeynumber");
					String loadedusahockeynumber = rs.getString("loadedusahockeynumber");


					Coach coach = new Coach();
					coach.setIdcoach(rosterid);
					coach.setFirstname(cfname);
					coach.setLastname(clname);
					coach.setEligibility(completeddate);
					coach.setCurrentteam(teamname);
					coach.setCitizenship(loadedfirstname);
					coach.setCitizenshiptransfer(loadedlastname);
					coach.setCTExpirationdate(loadeddob);
					coach.setDob(dob);
					coach.setPreviousteam(rostertype);
					coach.setCoachname(loadedusahockeynumber);
					coach.setCepnumber(usahockeynumber);



					templist.add(coach);
					coach = null;
				}
				LOGGER.info("We have results for list of bullying records with issues");
			}
			rs.close();
			db.cleanup();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading bullying errors with issues");
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		setCoacheswithissues(templist);
		templist=null;

	}

	public void updateInvidualbullying(Coach coach){
		String rosterid = coach.getIdcoach();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.updaterosterforbullying(?)");
			cs.setInt("rosterid",Integer.parseInt(rosterid));
			rs = cs.executeQuery();

			rs.close();
			db.cleanup();


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN process bullying records");
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		loadProcessedwithIssues();
	}

	public List<Coach> getCoacheswithissues() {
		return coacheswithissues;
	}

	public void setCoacheswithissues(List<Coach> coacheswithissues) {
		this.coacheswithissues = coacheswithissues;
	}

	public List<Coach> getCoaches(){
		return coaches;
	}
	
	public void setCoaches(List<Coach> list){
		coaches = list;
	}

	public Coach getSelectedcoach() {
		return selectedcoach;
	}

	public void setSelectedcoach(Coach selectedcoach) {
		this.selectedcoach = selectedcoach;
	}
}


