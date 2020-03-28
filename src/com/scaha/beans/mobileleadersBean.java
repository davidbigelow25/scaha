package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.TempGame;
import com.scaha.objects.TempGameDataModel;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class mobileleadersBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	
    
	//bean level properties used by multiple methods
	private Integer profileid = 0;
	
	
    //properties for storing the selected row of each of the datatables or drop downs
    private Integer selectedschedule = null;
	private String divisionstring = null;
	private String leaders = null;
	
	@PostConstruct
    public void init() {
	    
        //need to grab division
      
  		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      	
      	if(hsr.getParameter("division") != null){
      		divisionstring = hsr.getParameter("division").toString();
      	}
        
      	
        //Load SCAHA Games
        loadScahaLeaders();
        
        
	}
	
    public mobileleadersBean() {  
        
    }  
    
    public String getLeaders(){
    	return leaders;
    }
    
    public void setLeaders(String value){
    	leaders=value;
    }
    
    public String getDivisionstring(){
    	return divisionstring;
    }
    
    public void setDivisionstring(String value){
    	divisionstring=value;
    }
    
    public Integer getProfid(){
    	return profileid;
    }	
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    public Integer getSelectedschedule(){
		return selectedschedule;
	}
	
	public void setSelectedschedule(Integer selectedSchedule){
		selectedschedule = selectedSchedule;
	}
    
	//retrieves list of existing teams for the club
    public void loadScahaLeaders(){
    	String xmlstring = "";
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get standings
    		CallableStatement cs = db.prepareCall("CALL scaha.getSCAHALeadersForMobile(?,?)");
			cs.setString("divisionstring", divisionstring + " Regular Season");
			cs.setString("playertype", "playerpoints");
			rs = cs.executeQuery();
			xmlstring = "<leaders>";
			if (rs != null){
				
				while (rs.next()) {
					String playername = rs.getString("playername");
					String teamname = rs.getString("teamname");
    				String gp = rs.getString("gp");
    				String goals = rs.getString("goals");
    				String assists = rs.getString("assists");
    				String points = rs.getString("points");
    				
    				xmlstring = xmlstring + "<playerleader>";
    				xmlstring = xmlstring + "<playername>" + playername + "</playername>";
    				xmlstring = xmlstring + "<teamname>" + teamname + "</teamname>";
    				xmlstring = xmlstring + "<gameplayed>" + gp + "</gameplayed>";
    				xmlstring = xmlstring + "<goals>" + goals + "</goals>";
    				xmlstring = xmlstring + "<assists>" + assists + "</assists>";
    				xmlstring = xmlstring + "<points>" + points + "</points>";
    				xmlstring = xmlstring + "</playerleader>";
				}
				//LOGGER.info("We have results for scaha player leaders for review by statistician for schedule:" + this.selectedschedule);
			}
			
			cs = db.prepareCall("CALL scaha.getSCAHALeadersForMobile(?,?)");
			cs.setString("divisionstring", divisionstring + " Regular Season");
			cs.setString("playertype", "playerassists");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					String playername = rs.getString("playername");
					String teamname = rs.getString("teamname");
					String gp = rs.getString("gp");
    				String goals = rs.getString("goals");
    				String assists = rs.getString("assists");
    				String points = rs.getString("points");
    				
    				xmlstring = xmlstring + "<playerleaderassists>";
    				xmlstring = xmlstring + "<playername>" + playername + "</playername>";
    				xmlstring = xmlstring + "<teamname>" + teamname + "</teamname>";
    				xmlstring = xmlstring + "<gameplayed>" + gp + "</gameplayed>";
    				xmlstring = xmlstring + "<goals>" + goals + "</goals>";
    				xmlstring = xmlstring + "<assists>" + assists + "</assists>";
    				xmlstring = xmlstring + "<points>" + points + "</points>";
    				xmlstring = xmlstring + "</playerleaderassists>";
				}
				//LOGGER.info("We have results for scaha player leaders for review by statistician for schedule:" + this.selectedschedule);
			}
			
			cs = db.prepareCall("CALL scaha.getSCAHALeadersForMobile(?,?)");
			cs.setString("divisionstring", divisionstring + " Regular Season");
			cs.setString("playertype", "playergoals");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					String playername = rs.getString("playername");
					String teamname = rs.getString("teamname");
					String gp = rs.getString("gp");
    				String goals = rs.getString("goals");
    				String assists = rs.getString("assists");
    				String points = rs.getString("points");
    				
    				xmlstring = xmlstring + "<playerleadergoals>";
    				xmlstring = xmlstring + "<playername>" + playername + "</playername>";
    				xmlstring = xmlstring + "<teamname>" + teamname + "</teamname>";
    				xmlstring = xmlstring + "<gameplayed>" + gp + "</gameplayed>";
    				xmlstring = xmlstring + "<goals>" + goals + "</goals>";
    				xmlstring = xmlstring + "<assists>" + assists + "</assists>";
    				xmlstring = xmlstring + "<points>" + points + "</points>";
    				xmlstring = xmlstring + "</playerleadergoals>";
				}
				//LOGGER.info("We have results for scaha player leaders for review by statistician for schedule:" + this.selectedschedule);
			}
			
			
			cs = db.prepareCall("CALL scaha.getSCAHALeadersForMobile(?,?)");
			cs.setString("divisionstring", divisionstring + " Regular Season");
			cs.setString("playertype", "goaliesv");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					String playername = rs.getString("playername");
					String teamname = rs.getString("teamname");
					String gp = rs.getString("gp");
    				String mins = rs.getString("mins");
    				String shots = rs.getString("shots");
    				String saves = rs.getString("saves");
    				String sva = rs.getString("sva");
    				String gaa = rs.getString("gaa");
    				
    				xmlstring = xmlstring + "<goalieleader>";
    				xmlstring = xmlstring + "<playername>" + playername + "</playername>";
    				xmlstring = xmlstring + "<teamname>" + teamname + "</teamname>";
    				xmlstring = xmlstring + "<gameplayed>" + gp + "</gameplayed>";
    				xmlstring = xmlstring + "<mins>" + mins + "</mins>";
    				xmlstring = xmlstring + "<shots>" + shots + "</shots>";
    				xmlstring = xmlstring + "<saves>" + saves + "</saves>";
    				xmlstring = xmlstring + "<sva>" + sva + "</sva>";
    				xmlstring = xmlstring + "<gaa>" + gaa + "</gaa>";
    				xmlstring = xmlstring + "</goalieleader>";
				}
				//LOGGER.info("We have results for scaha player leaders for review by statistician for schedule:" + this.selectedschedule);
			}
			
			cs = db.prepareCall("CALL scaha.getSCAHALeadersForMobile(?,?)");
			cs.setString("divisionstring", divisionstring + " Regular Season");
			cs.setString("playertype", "goaliegaa");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					String playername = rs.getString("playername");
					String teamname = rs.getString("teamname");
    				String gp = rs.getString("gp");
    				String mins = rs.getString("mins");
    				String shots = rs.getString("shots");
    				String saves = rs.getString("saves");
    				String sva = rs.getString("sva");
    				String gaa = rs.getString("gaa");
    				
    				xmlstring = xmlstring + "<goalieleadergaa>";
    				xmlstring = xmlstring + "<playername>" + playername + "</playername>";
    				xmlstring = xmlstring + "<teamname>" + teamname + "</teamname>";
    				xmlstring = xmlstring + "<gameplayed>" + gp + "</gameplayed>";
    				xmlstring = xmlstring + "<mins>" + mins + "</mins>";
    				xmlstring = xmlstring + "<shots>" + shots + "</shots>";
    				xmlstring = xmlstring + "<saves>" + saves + "</saves>";
    				xmlstring = xmlstring + "<sva>" + sva + "</sva>";
    				xmlstring = xmlstring + "<gaa>" + gaa + "</gaa>";
    				xmlstring = xmlstring + "</goalieleadergaa>";
				}
				//LOGGER.info("We have results for scaha player leaders for review by statistician for schedule:" + this.selectedschedule);
			}
			
			xmlstring = xmlstring + "</leaders>";
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting scaha games schedule for review by statistician for schedule:" + this.selectedschedule);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	this.setLeaders(xmlstring);
    	
    	
    }

        
    
	
	
		
		
	
	
	
}

