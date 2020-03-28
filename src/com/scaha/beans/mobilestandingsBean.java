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
public class mobilestandingsBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	
    
	//bean level properties used by multiple methods
	private Integer profileid = 0;
	
	
    //properties for storing the selected row of each of the datatables or drop downs
    private Integer selectedschedule = null;
	private String divisionstring = null;
	private String standings = null;
	
	@PostConstruct
    public void init() {
	    
        //need to grab division
      
  		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      	
      	if(hsr.getParameter("division") != null){
      		divisionstring = hsr.getParameter("division").toString();
      	}
        
      	
        //Load SCAHA Games
        loadScahaStandings();
        
        
	}
	
    public mobilestandingsBean() {  
        
    }  
    
    public String getStandings(){
    	return standings;
    }
    
    public void setStandings(String value){
    	standings=value;
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
    public void loadScahaStandings(){
    	String xmlstring = "";
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get standings
    		CallableStatement cs = db.prepareCall("CALL scaha.getSCAHAStandingsForMobile(?)");
			cs.setString("divisionstring", divisionstring + " Regular Season");
			rs = cs.executeQuery();
			xmlstring = "<standings>";
			if (rs != null){
				
				while (rs.next()) {
					String teamid = rs.getString("idteam");
					String teamimage = rs.getString("teamimage");
    				String teamname = rs.getString("teamname");
    				String gp = rs.getString("gp");
    				String wins = rs.getString("wins");
    				String losses = rs.getString("losses");
    				String ties = rs.getString("ties");
    				String points = rs.getString("points");
    				String gf = rs.getString("gf");
    				String ga = rs.getString("ga");
    				
    				xmlstring = xmlstring + "<standing>";
    				xmlstring = xmlstring + "<teamid>" + teamid + "</teamid>";
    				xmlstring = xmlstring + "<teamimage>" + teamimage + "</teamimage>";
    				xmlstring = xmlstring + "<teamname>" + teamname + "</teamname>";
    				xmlstring = xmlstring + "<gameplayed>" + gp + "</gameplayed>";
    				xmlstring = xmlstring + "<wins>" + wins + "</wins>";
    				xmlstring = xmlstring + "<losses>" + losses + "</losses>";
    				xmlstring = xmlstring + "<ties>" + ties + "</ties>";
    				xmlstring = xmlstring + "<points>" + points + "</points>";
    				xmlstring = xmlstring + "<gf>" + gf + "</gf>";
    				xmlstring = xmlstring + "<ga>" + ga + "</ga>";
    				xmlstring = xmlstring + "</standing>";
				}
				//LOGGER.info("We have results for scaha standings for review by statistician for schedule:" + this.selectedschedule);
			}
			
			xmlstring = xmlstring + "</standings>";
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
		
    	this.setStandings(xmlstring);
    	
    	
    }

        
    
	
	
		
		
	
	
	
}

