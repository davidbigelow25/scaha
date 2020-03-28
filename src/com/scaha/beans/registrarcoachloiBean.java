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
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Coach;
import com.scaha.objects.CoachDataModel;

//import com.gbli.common.SendMailSSL;


public class registrarcoachloiBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	transient private ResultSet rs = null;
	private List<Coach> coaches = null;
    private CoachDataModel CoachDataModel = null;
    private Coach selectedcoach = null;
	private String selectedtabledisplay = null;
	private List<Club> clubs = null;
	private Boolean displayclublist = null;
	private String selectedclub = null;
	private String selectedcoachid = null;
	private Integer profileid = 0;
	private Integer clubid = 0;
	private String currentyear = null;
	
	@PostConstruct
    public void init() {
		this.clubid = 1;
    	FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );

		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
    	this.setProfid(pb.getProfile().ID);
    	loadClubName();
		
		coaches = new ArrayList<Coach>();  
        CoachDataModel = new CoachDataModel(coaches);
        this.setSelectedtabledisplay("1");
        
        //need to add scaha session object
		ValueExpression scahaexpression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{scahaBean}", Object.class );

		scaha = (ScahaBean) scahaexpression.getValue( context.getELContext() );
		
		//need to set current year and prior year
		String cyear = scaha.getScahaSeasonList().getCurrentSeason().getFromDate().substring(0,4);
		this.setCurrentyear(cyear);
        
        coachesDisplay(); 
    }  
    
	public String getCurrentyear(){
		return currentyear;
}

	public void setCurrentyear(String cyear){
		currentyear=cyear;
	}
	
	public Integer getProfid(){
    	return profileid;
    }
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    public Integer getClubid(){
    	return clubid;
    }
    
    public void setClubid(Integer idclub){
    	clubid = idclub;
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

			CallableStatement cs = db.prepareCall("CALL scaha.getCoachLoiByClub(?)");
   			cs.setInt("clubid", this.clubid);
			rs = cs.executeQuery();
    			
   			if (rs != null){
				
				while (rs.next()) {
					String idcoach = rs.getString("idroster");
    				String sfirstname = rs.getString("fname");
    				String slastname = rs.getString("lname");
    				String steam = rs.getString("teamname");
    				String sloidate = rs.getString("loidate");
    				String susamember = rs.getString("usamembership");
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
    				String address = rs.getString("address");
    				String city = rs.getString("city");
    				String state = rs.getString("state");
    				String zip = rs.getString("zipcode");
    				String email1 = rs.getString("email1");
    				String email2 = rs.getString("email2");
    				String phone = rs.getString("phone");
    				String notes = rs.getString("notes");
    				
    				Coach ocoach = new Coach();
    				ocoach.setIdcoach(idcoach);
    				ocoach.setFirstname(sfirstname);
    				ocoach.setLastname(slastname);
    				ocoach.setAddress(address);
    				ocoach.setCity(city);
    				ocoach.setState(state);
    				ocoach.setZip(zip);
    				ocoach.setEmail1(email1);
    				ocoach.setEmail2(email2);
    				ocoach.setPhone(phone);
    				ocoach.setLoidate(sloidate);
    				ocoach.setUsamembership(susamember);
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
    				ocoach.setNotes(notes);
    				tempresult.add(ocoach);
				}
   				rs.close();
   				//LOGGER.info("We have results for lois for the lookup date" + date.toString());
    				
   			}
  				
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR Lois for lookup date:" + date.toString());
    		e.printStackTrace();
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
			context.getExternalContext().redirect("viewcoachloi.xhtml?coachid=" + sidcoach);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	}
	
	
	public void CloseLoi(){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("registrarcoachlois.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadClubName(){
		
		//first lets get club id for the logged in profile
		String clubname = "";
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

    			
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.getProfid());
			db.getData("CALL scaha.getClubforPerson(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					this.clubid = rs.getInt("idclub");
					
					}
				//LOGGER.info("We have results for club for a profile");
			}
			
			db.cleanup();
    		
			//now lets retrieve club name
			v = new Vector<Integer>();
			v.add(clubid);
			db.getData("CALL scaha.getClubNamebyId(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					clubname = rs.getString("clubname");
				}
				//LOGGER.info("We have results for club name");
			}
			
			db.cleanup();
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading club by profile");
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

