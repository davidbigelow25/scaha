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
public class mobiledetailBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	
    
	//bean level properties used by multiple methods
	private Integer profileid = 0;
	
	
    //properties for storing the selected row of each of the datatables or drop downs
    private Integer selectedgame = null;
	private String gamedetail = null;
	
	@PostConstruct
    public void init() {
	    
    	HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      	
      	if(hsr.getParameter("gameid") != null){
      		selectedgame = Integer.parseInt(hsr.getParameter("gameid").toString());
      	}
      	      	
        //Load Game Detail
        loadGameDetail();
        
        
	}
	
    public mobiledetailBean() {  
        
    }  
    
    public String getGamedetail(){
    	return gamedetail;
    }
    
    public void setGamedetail(String value){
    	gamedetail=value;
    }
    
    
    public Integer getProfid(){
    	return profileid;
    }	
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    
    //retrieves list of existing teams for the club
    public void loadGameDetail(){
    	String xmlstring = "";
    	
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get home players name
    		CallableStatement cs = db.prepareCall("CALL scaha.getSCAHAGameDetailForMobile(?,?)");
			cs.setInt("gameid", selectedgame);
			cs.setString("playertype", "homeplayers");
			rs = cs.executeQuery();
			xmlstring = "<players>";
			xmlstring = xmlstring + "<homelogo>kings</homelogo><awaylogo>ducks</awaylogo>";
			if (rs != null){
				
				while (rs.next()) {
					String jersey = rs.getString("jersey");
    				String playername = rs.getString("name");
    				String gp = rs.getString("gp");
    				String goals = rs.getString("goals");
    				String assists = rs.getString("assists");
    				String points = rs.getString("points");
    				String pims = rs.getString("pims");
    				
    				xmlstring = xmlstring + "<homeplayer>";
    				xmlstring = xmlstring + "<number>" + jersey + "</number>";
    				xmlstring = xmlstring + "<name>" + playername + "</name>";
    				xmlstring = xmlstring + "<gamesplayed>" + gp + "</gamesplayed>";
    				xmlstring = xmlstring + "<goals>" + goals + "</goals>";
    				xmlstring = xmlstring + "<assists>" + assists + "</assists>";
    				xmlstring = xmlstring + "<points>" + points + "</points>";
    				xmlstring = xmlstring + "<pims>" + pims + "</pims>";
    				xmlstring = xmlstring + "</homeplayer>";
				}
				//LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedgame);
			}
			
			//.get home goalies details
			cs = db.prepareCall("CALL scaha.getSCAHAGameDetailForMobile(?,?)");
			cs.setInt("gameid", selectedgame);
			cs.setString("playertype", "homegoalie");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					String jersey = rs.getString("jersey");
    				String playername = rs.getString("name");
    				String gp = rs.getString("gp");
    				String mins = rs.getString("mins");
    				String shots = rs.getString("shots");
    				String saves = rs.getString("saves");
    				String sva = rs.getString("sva");
    				String gaa = rs.getString("gaa");
    				String pims = rs.getString("pims");
    				
    				xmlstring = xmlstring + "<homegoalie>";
    				xmlstring = xmlstring + "<number>" + jersey + "</number>";
    				xmlstring = xmlstring + "<name>" + playername + "</name>";
    				xmlstring = xmlstring + "<gamesplayed>" + gp + "</gamesplayed>";
    				xmlstring = xmlstring + "<mins>" + mins + "</mins>";
    				xmlstring = xmlstring + "<shots>" + shots + "</shots>";
    				xmlstring = xmlstring + "<saves>" + saves + "</saves>";
    				xmlstring = xmlstring + "<sva>" + sva + "</sva>";
    				xmlstring = xmlstring + "<gaa>" + gaa + "</gaa>";
    				xmlstring = xmlstring + "<pims>" + pims + "</pims>";
    				xmlstring = xmlstring + "</homegoalie>";
				}
				//LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedgame);
			}
			
			//get away players
			cs = db.prepareCall("CALL scaha.getSCAHAGameDetailForMobile(?,?)");
			cs.setInt("gameid", selectedgame);
			cs.setString("playertype", "awayplayers");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					String jersey = rs.getString("jersey");
    				String playername = rs.getString("name");
    				String gp = rs.getString("gp");
    				String goals = rs.getString("goals");
    				String assists = rs.getString("assists");
    				String points = rs.getString("points");
    				String pims = rs.getString("pims");
    				
    				xmlstring = xmlstring + "<awayplayer>";
    				xmlstring = xmlstring + "<number>" + jersey + "</number>";
    				xmlstring = xmlstring + "<name>" + playername + "</name>";
    				xmlstring = xmlstring + "<gamesplayed>" + gp + "</gamesplayed>";
    				xmlstring = xmlstring + "<goals>" + goals + "</goals>";
    				xmlstring = xmlstring + "<assists>" + assists + "</assists>";
    				xmlstring = xmlstring + "<points>" + points + "</points>";
    				xmlstring = xmlstring + "<pims>" + pims + "</pims>";
    				xmlstring = xmlstring + "</awayplayer>";
				}
				//LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedgame);
			}
			
			//get away goalies
			cs = db.prepareCall("CALL scaha.getSCAHAGameDetailForMobile(?,?)");
			cs.setInt("gameid", selectedgame);
			cs.setString("playertype", "awaygoalie");
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					String jersey = rs.getString("jersey");
    				String playername = rs.getString("name");
    				String gp = rs.getString("gp");
    				String mins = rs.getString("mins");
    				String shots = rs.getString("shots");
    				String saves = rs.getString("saves");
    				String sva = rs.getString("sva");
    				String gaa = rs.getString("gaa");
    				String pims = rs.getString("pims");
    				
    				xmlstring = xmlstring + "<homegoalie>";
    				xmlstring = xmlstring + "<number>" + jersey + "</number>";
    				xmlstring = xmlstring + "<name>" + playername + "</name>";
    				xmlstring = xmlstring + "<gamesplayed>" + gp + "</gamesplayed>";
    				xmlstring = xmlstring + "<mins>" + mins + "</mins>";
    				xmlstring = xmlstring + "<shots>" + shots + "</shots>";
    				xmlstring = xmlstring + "<saves>" + saves + "</saves>";
    				xmlstring = xmlstring + "<sva>" + sva + "</sva>";
    				xmlstring = xmlstring + "<gaa>" + gaa + "</gaa>";
    				xmlstring = xmlstring + "<pims>" + pims + "</pims>";
    				xmlstring = xmlstring + "</homegoalie>";
				}
				//LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedgame);
			}
			
			xmlstring = xmlstring + "</players>";
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting scaha games schedule for review by statistician for schedule:" + this.selectedgame);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	this.setGamedetail(xmlstring);
    	
    	
    }

    
    
    
	
	
		
		
	
	
	
}

