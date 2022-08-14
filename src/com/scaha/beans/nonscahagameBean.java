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

public class nonscahagameBean implements Serializable {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	transient private ResultSet rs = null;
	
	//bean level properties used by multiple methods
	private Integer teamid = null;
	private Integer gameid = null;
	private String teamname = null;
	private Integer idclub = null;
	private Integer profileid = 0;
	private Boolean ishighschool = null;
	
	//properties for adding tournaments
	private String gamedate;
	private String gametime;
	private String opponent;
	private String location;
	private Integer idteamtournament;	
	private String status;
	private String selectedtournamentforgame;
	
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
    		setTeamid(Integer.parseInt(hsr.getParameter("teamid").toString()));
        }
    	
    	
    	
    	
    	if(hsr.getParameter("gameid") != null)
        {
    		this.gameid = Integer.parseInt(hsr.getParameter("gameid").toString());
        }
        
        //teamid = 131;
        
        getTournamentGame();
        
        
	}
	
    public nonscahagameBean() {  
        
    }  
    
    public String getSelectedtournamentforgame(){
    	return selectedtournamentforgame;
    }
    
    public void setSelectedtournamentforgame(String svalue){
    	selectedtournamentforgame=svalue;
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
    
    
    public Integer getGameid(){
    	return gameid;
    }
    
    public void setGameid(Integer name){
    	gameid=name;
    }
    
    
    public String getLocation(){
    	return location;
    }
    
    public void setLocation(String name){
    	location=name;
    }
    
    public String getGamedate(){
    	return gamedate;
    }
    
    public void setGamedate(String name){
    	gamedate=name;
    }
    
    
    public String getGametime(){
    	return gametime;
    }
    
    public void setGametime(String name){
    	gametime=name;
    }
    
    
    public String getOpponent(){
    	return opponent;
    }
    
    public void setOpponent(String name){
    	opponent=name;
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
			LOGGER.info("We have results for club for a profile");
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
				LOGGER.info("We have results for club is a high school");
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

	
	public void updateTournamentGame() {  
    
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.updateTournamentGameForTeam(?,?,?,?,?,?,?)");
    		cs.setInt("gameid", this.gameid);
    		cs.setInt("newgametypeid", Integer.parseInt(this.selectedtournamentforgame));
    		cs.setInt("teamid", this.teamid);
			cs.setString("newgamedate", this.gamedate);
			cs.setString("newgametime", this.gametime);
			cs.setString("newopponent", this.opponent);
			cs.setString("newlocation", this.location);
			cs.executeQuery();
			db.commit();
			db.cleanup();
    		
			LOGGER.info("manager has updated game:" + this.gameid);
    		
			getTournamentGame();
			
			
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
    	mb.getTournamentGames();

    	try{
    		context.getExternalContext().redirect("managerportal.xhtml");
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
	}
	
	public void getTournamentGame() {  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getTournamentGameForTeam(?,?)");
			cs.setInt("teamid", this.teamid);
			cs.setInt("gameid", this.gameid);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.idteamtournament = rs.getInt("idteamtournament");
					
					if (rs.getString("gametype").equals("Tournament")){
						this.setSelectedtournamentforgame("1");
					} else {
						this.setSelectedtournamentforgame("2");
					}
					this.gamedate = rs.getString("gamedate");
    				this.gametime = rs.getString("gametime");
    				this.opponent = rs.getString("opponent");
    				this.location = rs.getString("location");
    				this.status = rs.getString("status");
    				
    			}
				LOGGER.info("We have results for tourney game by team:" + this.gameid);
			}
			
			
			rs.close();
			db.cleanup();
    		
			LOGGER.info("loaded detail for tournament game:" + this.gameid);
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting tournament game for team" + this.gameid);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    }
	
	public void updateExhibitionGame() {  
	    
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.updateExhibitionGameForTeam(?,?,?,?,?,?)");
    		cs.setInt("gameid", this.gameid);
    		cs.setInt("teamid", this.teamid);
			cs.setString("newgamedate", this.gamedate);
			cs.setString("newgametime", this.gametime);
			cs.setString("newopponent", this.opponent);
			cs.setString("newlocation", this.location);
			cs.executeQuery();
			db.commit();
			db.cleanup();
    		
			LOGGER.info("manager has updated game:" + this.gameid);
    		
			getTournamentGame();
			
			
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
    	mb.getExhibitionGames();

    	try{
    		context.getExternalContext().redirect("managerportal.xhtml");
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
	}
	
}

