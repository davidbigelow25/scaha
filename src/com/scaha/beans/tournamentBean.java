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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.RosterEdit;
import com.scaha.objects.RosterEditDataModel;
import com.scaha.objects.TempGame;
import com.scaha.objects.TempGameDataModel;
import com.scaha.objects.Tournament;
import com.scaha.objects.TournamentDataModel;
import com.scaha.objects.TournamentGame;
import com.scaha.objects.TournamentGameDataModel;

//import com.gbli.common.SendMailSSL;

public class tournamentBean implements Serializable {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	transient private ResultSet rs = null;
	
	//bean level properties used by multiple methods
	private Integer teamid = null;
	private String teamname = null;
	private Integer idclub = null;
	private Integer profileid = 0;
	private Boolean ishighschool = null;
	
	//properties for adding tournaments
	private String tournamentname;
	private String startdate;
	private String enddate;
	private String contact;
	private String phone;
	private String sanction;
	private String location;
	private String website;
	private Integer idteamtournament;	
	private String status;
	private String levelplayed;
	
	@PostConstruct
    public void init() {
		
        
        FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{scahaBean}", Object.class );
		ScahaBean scaha = (ScahaBean) expression.getValue( context.getELContext() );
    	this.scaha=scaha;

    	expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );
		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
    	this.pb=pb;

    	this.setProfid(pb.getProfile().ID);
        getClubID();
        isClubHighSchool();
    	
        //will need to load player profile information for displaying loi
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("teamid") != null)
        {
    		this.teamid = Integer.parseInt(hsr.getParameter("teamid").toString());
        }
    	
    	
    	if(hsr.getParameter("tournamentid") != null)
        {
    		this.idteamtournament = Integer.parseInt(hsr.getParameter("tournamentid").toString());
        }
        
    	getTournament();
        
        
	}
	
    public tournamentBean() {  
        
    }  
    
    public Integer getIdteamtournament(){
    	return idteamtournament;
    }
    
    public void setIdteamtournament(Integer name){
    	idteamtournament=name;
    }
    
    public String getStatus(){
    	return status;
    }
    
    public void setStatus(String name){
    	status=name;
    }
    
    public String getLevelplayed(){
    	return levelplayed;
    }
    
    public void setLevelplayed(String name){
    	levelplayed=name;
    }
    
    public String getWebsite(){
    	return website;
    }
    
    public void setWebsite(String name){
    	website=name;
    }
    
    
    public String getLocation(){
    	return location;
    }
    
    public void setLocation(String name){
    	location=name;
    }
    
    public String getSanction(){
    	return sanction;
    }
    
    public void setSanction(String name){
    	sanction=name;
    }
    
    
    public String getPhone(){
    	return phone;
    }
    
    public void setPhone(String name){
    	phone=name;
    }
    
    
    public String getContact(){
    	return contact;
    }
    
    public void setContact(String name){
    	contact=name;
    }
    
    
    public String getEnddate(){
    	return enddate;
    }
    
    public void setEnddate(String name){
    	enddate=name;
    }
    
    
    public String getStartdate(){
    	return startdate;
    }
    
    public void setStartdate(String name){
    	startdate=name;
    }
    
    
    public String getTournamentname(){
    	return tournamentname;
    }
    
    public void setTournamentname(String name){
    	tournamentname=name;
    }
    
    public String getTeamname(){
    	return teamname;
    }
    
    public void setTeamname(String name){
    	teamname=name;
    }
    
    
    public Integer getTeamid(){
    	return teamid;
    }
    
    public void setTeamid(Integer id){
    	teamid=id;
    }
    
    public Integer getIdclub(){
    	return idclub;
    }
    
    public void setIdclub(Integer id){
    	idclub=id;
    }
    
    public Boolean getIshighschool(){
    	return ishighschool;
    }
    
    public void setIshighschool(Boolean value){
    	ishighschool = value;
    }
    
    
    public Integer getProfid(){
    	return profileid;
    }	
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    public void getClubID(){
		
		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.getProfid());
			db.getData("CALL scaha.getClubforPerson(?)", v);
		    ResultSet rs = db.getResultSet();
			while (rs.next()) {
				this.idclub = rs.getInt("idclub");
			}
			rs.close();
			//LOGGER.info("We have results for club for a profile");
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
	
    public void isClubHighSchool(){
			
			//first lets get club id for the logged in profile
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			Integer isschool = 0;
			try{
				Vector<Integer> v = new Vector<Integer>();
				v.add(this.idclub);
				db.getData("CALL scaha.IsClubHighSchool(?)", v);
			    ResultSet rs = db.getResultSet();
				while (rs.next()) {
					isschool = rs.getInt("result");
				}
				//LOGGER.info("We have results for club is a high school");
				db.cleanup();
				
				if (isschool.equals(0)){
					this.ishighschool=false;
				}else{
					this.ishighschool=true;
				}
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

	
	public void updateTournament() {  
    
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.updateTournamentForTeam(?,?,?,?,?,?,?,?,?,?,?)");
    		cs.setInt("teamtournamentid", this.idteamtournament);
    		cs.setInt("teamid", this.teamid);
			cs.setString("newtournamentname", this.tournamentname);
			cs.setString("newstartdate", this.startdate);
			cs.setString("newenddate", this.enddate);
			cs.setString("newcontact", this.contact);
			cs.setString("newphone", this.phone);
			cs.setString("newsanction", this.sanction);
			cs.setString("newlocation", this.location);
			cs.setString("newwebsite", this.website);
			cs.setString("levelplayed", this.levelplayed);
		    cs.executeQuery();
			db.commit();
			db.cleanup();
    		
			//LOGGER.info("manager has added tournament:" + this.tournamentname);
    		
			getTournament();
			
			
			//need to add email to manager and scaha statistician
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN adding tournament");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{managerBean}", Object.class );

		managerBean mb = (managerBean) expression.getValue( context.getELContext() );
    	mb.getTournament();

    	try{
    		context.getExternalContext().redirect("managerportal.xhtml");
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
	}
	
	public void getTournament() {  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getTournamentForTeam(?,?)");
			cs.setInt("teamid", this.teamid);
			cs.setInt("teamtournamentid", this.idteamtournament);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.idteamtournament = rs.getInt("idteamtournament");
    				this.tournamentname = rs.getString("tournamentname");
    				this.startdate = rs.getString("startdate");
    				this.enddate = rs.getString("enddate");
    				this.contact = rs.getString("contact");
    				this.location = rs.getString("location");
    				this.status = rs.getString("status");
    				this.sanction = rs.getString("sanction");
    				this.website = rs.getString("website");
    				this.phone = rs.getString("phone");
    				this.levelplayed = rs.getString("levelplayed");
    			}
				//LOGGER.info("We have results for tourney list by team:" + this.teamid);
			}
			
			
			rs.close();
			db.cleanup();
    		
			//LOGGER.info("manager has added tournament:" + this.tournamentname);
    		//need to add email sent to scaha statistician and manager
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting tournament list for team" + this.teamid);
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

