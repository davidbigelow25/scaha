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
public class mobileBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<TempGame> games = null;
	
    
	//bean level properties used by multiple methods
	private Integer profileid = 0;
	
	//datamodels for all of the lists on the page
	private TempGameDataModel TempGameDataModel = null;
    
    //properties for storing the selected row of each of the datatables or drop downs
    private TempGame selectedgame = null;
    private Integer selectedschedule = null;
	private String divisionstring = null;
	private String weeklabel = null;
	private Integer weekcount = 0;
	
	@PostConstruct
    public void init() {
		games = new ArrayList<TempGame>();  
        TempGameDataModel = new TempGameDataModel(games);
        
        //need to grab division
      
  		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
      	
      	if(hsr.getParameter("division") != null){
      		divisionstring = hsr.getParameter("division").toString();
      	}
      	if(hsr.getParameter("week_id") != null){
      		weekcount = Integer.parseInt(hsr.getParameter("week_id").toString());
      	}
        
      	
        //Load SCAHA Games
        loadScahaGames();
        
        
	}
	
    public mobileBean() {  
        
    }  
    
    public String getWeeklabel(){
    	return weeklabel;
    }
    
    public void setWeeklabel(String value){
    	weeklabel=value;
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
    
    
    
    public TempGame getSelectedgame(){
		return selectedgame;
	}
	
	public void setSelectedgame(TempGame selectedGame){
		selectedgame = selectedGame;
	}
    
	public Integer getSelectedschedule(){
		return selectedschedule;
	}
	
	public void setSelectedschedule(Integer selectedSchedule){
		selectedschedule = selectedSchedule;
	}
    
	public List<TempGame> getGames(){
		return games;
	}
	
	public void setGames(List<TempGame> list){
		games = list;
	}
	
	    
    //retrieves list of existing teams for the club
    public void loadScahaGames(){
    	String xmlstring = "";
    	ArrayList<Integer> gameids = new ArrayList();
    	ArrayList<String> homeimages = new ArrayList();
    	ArrayList<String> awayimages = new ArrayList();
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	String weeklabel = "";
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getWeekLabel(?)");
			cs.setInt("weekcount", weekcount);
			rs = cs.executeQuery();
			if (rs != null){
				
				while (rs.next()) {
					weeklabel = rs.getString("weeklabel");
				}
			}
    		
    		cs = db.prepareCall("CALL scaha.getSCAHAGamesForMobile(?,?)");
			cs.setString("divisionstring", divisionstring + " Regular Season");
			cs.setInt("weekcount", weekcount);
			rs = cs.executeQuery();
			xmlstring = "<games>";
			xmlstring = xmlstring + "<week>" + weeklabel + "</week>";
			if (rs != null){
				
				while (rs.next()) {
					String idgame = rs.getString("idlivegame");
					gameids.add(Integer.parseInt(idgame));
					String hometeam = rs.getString("hometeam");
    				String awayteam = rs.getString("awayteam");
    				String homescore = rs.getString("homescore");
    				String awayscore = rs.getString("awayscore");
    				String dates = rs.getString("date");
    				String time = rs.getString("time");
    				String location = rs.getString("location");
    				String locationaddress = rs.getString("locationaddress");
    				String status = rs.getString("status");
    				String homeimage = rs.getString("homeimage");
    				homeimages.add(homeimage);
    				String awayimage = rs.getString("awayimage");
    				awayimages.add(awayimage);
    				
    				xmlstring = xmlstring + "<game>";
    				xmlstring = xmlstring + "<id>" + idgame + "</id>";
    				xmlstring = xmlstring + "<date>" + dates + "</date>";
    				xmlstring = xmlstring + "<time>" + time + "</time>";
    				xmlstring = xmlstring + "<home>" + hometeam + "</home>";
    				xmlstring = xmlstring + "<away>" + awayteam + "</away>";
    				xmlstring = xmlstring + "<location>" + location + "</location>";
    				xmlstring = xmlstring + "<locationaddress>" + locationaddress + "</locationaddress>";
    				xmlstring = xmlstring + "<awayscore>" + awayscore + "</awayscore>";
    				xmlstring = xmlstring + "<homescore>" + homescore + "</homescore>";
    				xmlstring = xmlstring + "<hometeamimage>" + homeimage + "</hometeamimage>";
    				xmlstring = xmlstring + "<awayteamimage>" + awayimage + "</awayteamimage>";
    				xmlstring = xmlstring + "<submitstatus>" + status + "</submitstatus>";
    				xmlstring = xmlstring + "</game>";
				}
				LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedschedule);
			}
			
			for (Integer i=0;i<gameids.size();i++){
				
				cs = db.prepareCall("CALL scaha.getSCAHAGameDetailForMobile(?,?)");
				cs.setInt("gameid", gameids.get(i));
				cs.setString("playertype", "homeplayers");
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
	    				
	    				if (pims==null) {
	    					pims="0";
	    				}
	    				
	    				xmlstring = xmlstring + "<homeplayer" + gameids.get(i).toString() + ">";
	    				xmlstring = xmlstring + "<number>" + jersey + "</number>";
	    				xmlstring = xmlstring + "<name>" + playername + "</name>";
	    				xmlstring = xmlstring + "<gameplayed>" + gp + "</gameplayed>";
	    				xmlstring = xmlstring + "<goals>" + goals + "</goals>";
	    				xmlstring = xmlstring + "<assists>" + assists + "</assists>";
	    				xmlstring = xmlstring + "<points>" + points + "</points>";
	    				xmlstring = xmlstring + "<pims>" + pims + "</pims>";
	    				xmlstring = xmlstring + "</homeplayer" + gameids.get(i).toString() + ">";
					}
					LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedgame);
				}
			
			
				//.get home goalies details
				cs = db.prepareCall("CALL scaha.getSCAHAGameDetailForMobile(?,?)");
				cs.setInt("gameid", gameids.get(i));
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
	    				
	    				if (pims==null) {
	    					pims="0";
	    				}
	    				
	    				xmlstring = xmlstring + "<homegoalie" + gameids.get(i).toString() + ">";
	    				xmlstring = xmlstring + "<number>" + jersey + "</number>";
	    				xmlstring = xmlstring + "<name>" + playername + "</name>";
	    				xmlstring = xmlstring + "<gameplayed>" + gp + "</gameplayed>";
	    				xmlstring = xmlstring + "<mins>" + mins + "</mins>";
	    				xmlstring = xmlstring + "<shots>" + shots + "</shots>";
	    				xmlstring = xmlstring + "<saves>" + saves + "</saves>";
	    				xmlstring = xmlstring + "<sva>" + sva + "</sva>";
	    				xmlstring = xmlstring + "<gaa>" + gaa + "</gaa>";
	    				xmlstring = xmlstring + "<pims>" + pims + "</pims>";
	    				xmlstring = xmlstring + "</homegoalie" + gameids.get(i).toString() + ">";
					}
					LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedgame);
				}
				
				//get away players
				cs = db.prepareCall("CALL scaha.getSCAHAGameDetailForMobile(?,?)");
				cs.setInt("gameid", gameids.get(i));
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
	    				
	    				if (pims==null) {
	    					pims="0";
	    				}
	    				
	    				xmlstring = xmlstring + "<awayplayer" + gameids.get(i).toString() + ">";
	    				xmlstring = xmlstring + "<number>" + jersey + "</number>";
	    				xmlstring = xmlstring + "<name>" + playername + "</name>";
	    				xmlstring = xmlstring + "<gameplayed>" + gp + "</gameplayed>";
	    				xmlstring = xmlstring + "<goals>" + goals + "</goals>";
	    				xmlstring = xmlstring + "<assists>" + assists + "</assists>";
	    				xmlstring = xmlstring + "<points>" + points + "</points>";
	    				xmlstring = xmlstring + "<pims>" + pims + "</pims>";
	    				xmlstring = xmlstring + "</awayplayer" + gameids.get(i).toString() + ">";
					}
					LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedgame);
				}
				
				//get away goalies
				cs = db.prepareCall("CALL scaha.getSCAHAGameDetailForMobile(?,?)");
				cs.setInt("gameid", gameids.get(i));
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
	    				
	    				if (pims==null) {
	    					pims="0";
	    				}
	    				
	    				xmlstring = xmlstring + "<awaygoalie" + gameids.get(i).toString() + ">";
	    				xmlstring = xmlstring + "<number>" + jersey + "</number>";
	    				xmlstring = xmlstring + "<name>" + playername + "</name>";
	    				xmlstring = xmlstring + "<gameplayed>" + gp + "</gameplayed>";
	    				xmlstring = xmlstring + "<mins>" + mins + "</mins>";
	    				xmlstring = xmlstring + "<shots>" + shots + "</shots>";
	    				xmlstring = xmlstring + "<saves>" + saves + "</saves>";
	    				xmlstring = xmlstring + "<sva>" + sva + "</sva>";
	    				xmlstring = xmlstring + "<gaa>" + gaa + "</gaa>";
	    				xmlstring = xmlstring + "<pims>" + pims + "</pims>";
	    				xmlstring = xmlstring + "</awaygoalie" + gameids.get(i).toString() + ">";
					}
					LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedgame);
				}
				
				xmlstring = xmlstring + "<homelogo" + gameids.get(i).toString() + ">" + homeimages.get(i) + "</homelogo" + gameids.get(i).toString() + "><awaylogo" + gameids.get(i).toString() + ">" + awayimages.get(i) + "</awaylogo" + gameids.get(i).toString() + ">";
				
			}
			
			xmlstring = xmlstring + "</games>";
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
		
    	this.setWeeklabel(xmlstring);
    	
    	
    }

    public TempGameDataModel getTempGamedatamodel(){
    	return TempGameDataModel;
    }
    
    public void setTempgamedatamodel(TempGameDataModel odatamodel){
    	TempGameDataModel = odatamodel;
    }

    
    
	
	
		
		
	
	
	
}

