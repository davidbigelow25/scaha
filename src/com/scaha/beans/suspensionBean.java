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
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Result;
import com.scaha.objects.Suspension;

//import com.gbli.common.SendMailSSL;
@ManagedBean
@ViewScoped
public class suspensionBean implements Serializable {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private List<Suspension> suspensions = null;
	private List<Suspension> allsuspensions = null;
	transient private ResultSet rs = null;
	private Result selectedplayer = null;
	private String numberofgames = null;
	private String infraction = null;
	private String eligibility = null;
	private String suspdate = null;
	private Integer served = null;
	private Integer match = null;
	private String playername = null;
	private String team = null;
	
	
	
	@PostConstruct
    public void init() {
		
        loadSuspensions();
        
        loadAllSuspensions();
	}
	
    public suspensionBean() {  
        
    }  
    
    public String getEligibility(){
    	return eligibility;
    }
    
    public void setEligibility(String value){
    	eligibility = value;
    }
    
    
    public String getPlayername(){
    	return playername;
    }
    
    public void setPlayername(String value){
    	playername = value;
    }
    
    public String getTeam(){
    	return team;
    }
    
    public void setTeam(String value){
    	team=value;
    }
    
    public Integer getMatch(){
    	return match;
    }
    
    public void setMatch(Integer value){
    	match = value;
    }
    
    public Integer getServed(){
    	return served;
    }
    
    public void setServed(Integer value){
    	served = value;
    }
    
    
    public String getSuspdate(){
    	return suspdate;
    }
    
    public void setSuspdate(String value){
    	suspdate = value;
    }
    
    
    public String getInfraction(){
    	return infraction;
    }
    
    public void setInfraction(String value){
    	infraction = value;
    }
    
    public String getNumberofgames(){
    	return numberofgames;
    }
    
    public void setNumberofgames(String value){
    	numberofgames = value;
    }
    
    
    public Result getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Result selectedPlayer){
		selectedplayer = selectedPlayer;
	}
	
    public List<Suspension> getSuspensions(){
    	return suspensions;
    }
    
    public void setSuspensions(List<Suspension> list){
    	suspensions=list;
    }
    
    public List<Suspension> getAllSuspensions(){
    	return allsuspensions;
    }
    
    public void setAllSuspensions(List<Suspension> list){
    	allsuspensions=list;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    
	public void loadSuspensions() {  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Suspension> tempresult = new ArrayList<Suspension>();
		
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getSuspensions()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer idsuspension = rs.getInt("idsuspensions");
					String playername = rs.getString("playername");
					String team = rs.getString("team");
					String infraction = rs.getString("infraction");
					String nogames = rs.getString("numberofgames");
					String eligibility = rs.getString("eligibility");
					String match = rs.getString("matchpenalty");
					String suspensiondate = rs.getString("suspensiondate");
					
					Suspension susp = new Suspension();
					susp.setIdsuspension(idsuspension);
					susp.setPlayername(playername);
					susp.setTeam(team);
					susp.setInfraction(infraction);
					susp.setGames(nogames);
					susp.setEligibility(eligibility);
					susp.setMatch(match);
					susp.setSuspensiondate(suspensiondate);
					
					tempresult.add(susp);
    					}
				//LOGGER.info("We have results for suspension list");
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting suspension list");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	this.setSuspensions(tempresult);
		
    }
	
	
	public void loadAllSuspensions() {  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Suspension> tempresult = new ArrayList<Suspension>();
		
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getAllSuspensions()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer idsuspension = rs.getInt("idsuspensions");
					String playername = rs.getString("playername");
					String team = rs.getString("team");
					String infraction = rs.getString("infraction");
					String nogames = rs.getString("numberofgames");
					String eligibility = rs.getString("eligibility");
					String match = rs.getString("matchpenalty");
					if (match=="Yes"){
						nogames = "--";
					}
					String suspensiondate = rs.getString("suspensiondate");
					String served = rs.getString("served");
					
					Suspension susp = new Suspension();
					susp.setIdsuspension(idsuspension);
					susp.setPlayername(playername);
					susp.setTeam(team);
					susp.setInfraction(infraction);
					susp.setGames(nogames);
					susp.setEligibility(eligibility);
					susp.setMatch(match);
					susp.setSuspensiondate(suspensiondate);
					susp.setServed(served);
					
					tempresult.add(susp);
    					}
				//LOGGER.info("We have results for suspension list");
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting suspension list");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	this.setAllSuspensions(tempresult);
		
    }
	
	public void markServed(Suspension suspension){
		Integer suspensionid = suspension.getIdsuspension();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.setSuspensionServed(?)");
			cs.setInt("suspensionid", suspensionid);
    		cs.executeQuery();
			
			//LOGGER.info("set suspension as served for:" + suspensionid.toString());
						
			db.commit();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN settings suspension");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	loadAllSuspensions();
		
	}
	
	public void Addsuspension(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.addSuspension(?,?,?,?,?,?,?)");
			cs.setInt("inpersonid", Integer.parseInt(this.selectedplayer.getIdplayer()));
			if (this.match==1){
				cs.setString("numgames", "0");
			} else {
				cs.setString("numgames", this.numberofgames);
			}
			cs.setString("ininfraction", this.infraction);
			cs.setInt("ismatch", this.match);
			cs.setInt("served", this.served);
			cs.setString("susdate", this.suspdate);
			cs.setString("elgibility", this.eligibility);
    		cs.executeQuery();
			
			//LOGGER.info("set suspension for:" + this.selectedplayer.getIdplayer().toString());
						
			db.commit();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN settings suspension");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	loadAllSuspensions();
		this.setSelectedplayer(null);
		this.setInfraction("");
		this.setMatch(0);
		this.setServed(0);
		this.setSuspdate("");
		this.setNumberofgames("");
		this.setEligibility("");
		FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{playerhistoryBean}", Object.class );

		playerhistoryBean pb = (playerhistoryBean) expression.getValue( context.getELContext() );
		pb.setSearchcriteria("");
	}
	
	public void manageSuspension(Suspension suspension){
		String suspensionid = suspension.getIdsuspension().toString();
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editsuspensions.xhtml?suspensionid=" + suspensionid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addSuspension(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("addsuspensions.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void LoadPerson(Result result){
		this.selectedplayer= result;
		this.playername = result.getPlayername();
		this.team = result.getAddress();
	}
	
}

