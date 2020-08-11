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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.PlayerStat;
import com.scaha.objects.Result;
import com.scaha.objects.ResultDataModel;
import com.scaha.objects.Suspension;

//import com.gbli.common.SendMailSSL;
@ManagedBean
@ViewScoped
public class editsuspensionBean implements Serializable {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	private String numberofgames = null;
	private String infraction = null;
	private String eligibility = null;
	private String suspdate = null;
	private Integer served = null;
	private Integer match = null;
	private String playername = null;
	private String team = null;
	private Integer suspensionid = 0;
	private String searchcriteria = "";			// Start out with no search criteria
	private ResultDataModel listofplayers = null;
	
	@PostConstruct
    public void init() {
		
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	if(hsr.getParameter("suspensionid") != null)
        {
    		this.suspensionid = Integer.parseInt(hsr.getParameter("suspensionid").toString());
        }
    	
    	loadSuspension();
	}
	
    public editsuspensionBean() {  
        
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
    
    public Integer getSuspensionid(){
    	return suspensionid;
    }
    
    public void setSuspensionid(Integer value){
    	suspensionid = value;
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
    
    
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("managesuspensionslist.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    
	public void loadSuspension() {  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getSuspension(?)");
    		cs.setInt("suspensionid", this.suspensionid);
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer idsuspension = rs.getInt("idsuspensions");
					String splayername = rs.getString("playername");
					String steam = rs.getString("team");
					String sinfraction = rs.getString("infraction");
					String snogames = rs.getString("numberofgames");
					String eligibility = rs.getString("eligibility");
					Integer imatch = rs.getInt("matchpenalty");
					Integer iserved = rs.getInt("served");
					String ssuspensiondate = rs.getString("suspensiondate");
					
					this.playername=splayername;
					this.team=steam;
					this.infraction=sinfraction;
					this.numberofgames=snogames;
					this.match=imatch;
					this.served=iserved;
					this.suspdate=ssuspensiondate;
					this.eligibility=eligibility;
				}
				//LOGGER.info("We have results for suspension id:" + this.suspensionid.toString());
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting suspension for id:" + this.suspensionid.toString());
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	
		
    }
	
	
	
	
	public void Updatesuspension(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.updateSuspension(?,?,?,?,?,?,?)");
			cs.setInt("inpersonid", this.suspensionid);
			cs.setString("numgames", this.numberofgames);
			cs.setString("ininfraction", this.infraction);
			cs.setInt("ismatch", this.match);
			cs.setInt("served", this.served);
			cs.setString("susdate", this.suspdate);
			cs.setString("elgibility", this.eligibility);
    		cs.executeQuery();
			
			//LOGGER.info("set suspension for:" + this.suspensionid);
						
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
    	
    	closePage();
	}
	

	public void LoadPerson(Result result){
		this.playername = result.getPlayername();
		this.team = result.getAddress();
		
	}
	
	
	
	public String getSearchcriteria ()
    {
        return searchcriteria;
    }
    
    public void setSearchcriteria (final String searchcriteria)
    {
        this.searchcriteria = searchcriteria;
    }

    
   
    public void playerSearch(){
    
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Result> tempresult = new ArrayList<Result>();
    	
    	try{

   			Vector<String> v = new Vector<String>();
   			v.add(this.searchcriteria);
   			db.getData("CALL scaha.playersearchforhistory(?)", v);
   			ResultSet rs = db.getResultSet();
   
   			while (rs.next()) {
				Integer idroster = rs.getInt("idroster");
   				String idperson = rs.getString("idperson");
        		String playername = rs.getString("playername");
        		String teamname = rs.getString("teamname");
        		String dob = rs.getString("dob");
        		
        		Result result = new Result(playername,idperson,teamname,dob);
        		result.setIdroster(idroster);
        		tempresult.add(result);
    		}
    				
    		//LOGGER.info("We have results for search criteria " + this.searchcriteria);
    		rs.close();
    		db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR " + this.searchcriteria);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		db.free();
    	}
    	
    	listofplayers = new ResultDataModel(tempresult);
    }

	public void playerSearchForLiveGame(Integer livegameid){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Result> tempresult = new ArrayList<Result>();

		try{

			Vector<Integer> v = new Vector<Integer>();
			v.add(livegameid);
			db.getData("CALL scaha.playersearchlivegame(?)", v);
			ResultSet rs = db.getResultSet();

			while (rs.next()) {
				Integer idroster = rs.getInt("idroster");
				String idperson = rs.getString("idperson");
				String playername = rs.getString("playername");
				String teamname = rs.getString("teamname");
				String dob = rs.getString("dob");

				Result result = new Result(playername,idperson,teamname,dob);
				result.setIdroster(idroster);
				tempresult.add(result);
			}

			//LOGGER.info("We have results for search criteria " + this.searchcriteria);
			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Searching FOR " + this.searchcriteria);
			e.printStackTrace();
			db.rollback();
		} finally {
			db.free();
		}

		listofplayers = new ResultDataModel(tempresult);
	}

    /**
	 * @return the listofplayers
	 */
	public ResultDataModel getListofplayers() {
		return listofplayers;
	}

	/**
	 * @param listofplayers the listofplayers to set
	 */
	public void setListofplayers(ResultDataModel listofplayers) {
		this.listofplayers = listofplayers;
	}
	
	
}

