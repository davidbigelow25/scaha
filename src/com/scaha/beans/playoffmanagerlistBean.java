package com.scaha.beans;

import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Coach;
import com.scaha.objects.CoachDataModel;
import com.scaha.objects.MailableObject;

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
import java.util.Vector;
import java.util.logging.Logger;

//import com.gbli.common.SendMailSSL;


public class playoffmanagerlistBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/voidloi.html");
	transient private ResultSet rs2 = null;

	transient private ResultSet rs = null;
	private List<Coach> coaches = null;
    private CoachDataModel CoachDataModel = null;
    private Coach selectedcoach = null;
	private Integer selectedscheduleid = null;

 	@PostConstruct
    public void init() {
		coaches = new ArrayList<Coach>();
        CoachDataModel = new CoachDataModel(coaches);


        //coachesDisplay();

    }

	public void onScheduleChange(){
		coachesDisplay();
	}

    public playoffmanagerlistBean() {
         
    }

	public Integer getSelectedscheduleid() {
		return selectedscheduleid;
	}

	public void setSelectedscheduleid(Integer selectedschedule) {
		this.selectedscheduleid = selectedschedule;
	}

	public List<Coach> getCoaches(){
		return coaches;
	}
	
	public void setCoaches(List<Coach> list){
		coaches = list;
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
    


    //retrieves list of managers
    public void coachesDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Coach> tempresult = new ArrayList<Coach>();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
				CallableStatement cs = db.prepareCall("CALL scaha.getManagerListBySchedule(?)");
    			/* need to update this parameter after adding the season and division selectors*/
				cs.setInt("scheduleidin", this.selectedscheduleid);
    			rs2 = cs.executeQuery();
    			
    		    if (rs2 != null){
    				while (rs2.next()) {
    					String idcoach = rs2.getString("idroster");
        				String sfirstname = rs2.getString("fname");
        				String slastname = rs2.getString("lname");
        				String steam = rs2.getString("teamname");
        				String sphonenumber = rs2.getString("phone");
        				String semail = rs2.getString("email");
        				String saltemail = rs2.getString("altemail");
						String rostertype = rs2.getString("rostertype");


        				Coach ocoach = new Coach();
        				ocoach.setIdcoach(idcoach);
        				ocoach.setFirstname(sfirstname);
        				ocoach.setLastname(slastname);
        				ocoach.setTeamname(steam);
        				ocoach.setPhone(sphonenumber);
        				ocoach.setEmail1(semail);
        				ocoach.setEmail2(saltemail);
						ocoach.setTeamrole(rostertype);

						tempresult.add(ocoach);

        				ocoach = null;

    				}
    				
    				LOGGER.info("We have results for manager list for " + this.getSelectedscheduleid());
    				
    			}
				rs2.close();
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loadding manager list for :" + this.selectedscheduleid);
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
    


}

