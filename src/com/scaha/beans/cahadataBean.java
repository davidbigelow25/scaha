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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Coach;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class cahadataBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private List<Player> players = null;
    private PlayerDataModel PlayerDataModel = null;
   
 	@PostConstruct
    public void init() {
		players = new ArrayList<Player>();  
        PlayerDataModel = new PlayerDataModel(players);
        
        playersDisplay();
    }
	
    public cahadataBean() {  
         
    }  
    
    
	public List<Player> getPlayers(){
		return players;
	}
	
	public void setPlayers(List<Player> list){
		players = list;
	}
	
	    
    //retrieves list of players
    public void playersDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Player> tempresult = new ArrayList<Player>();
    	java.util.Date date = new java.util.Date();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
				CallableStatement cs = db.prepareCall("CALL scaha.getCahaplayers()");
    			rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					String idplayer = rs.getString("idperson");
    					String srostertype = rs.getString("rostertype");
        				String sfirstname = rs.getString("fname");
        				String slastname = rs.getString("lname");
        				String scurrentteam = rs.getString("teamname");
        				String sclubname = rs.getString("clubname");
        				String sdob = rs.getString("dob");
        				String sloidate = rs.getString("loidate");
        				String seligibilitydate = rs.getString("eligible");
        				String sdropdate = rs.getString("dropped");
        				String sactive = rs.getString("isactive");
        				if (sactive.equals("1")){
        					sactive="Yes";
        				}else {
        					sactive="No";
        				}
        				String steamid = rs.getString("idteam");
        				String splayerup = rs.getString("isplayerup");
        				if (splayerup.equals("1")){
        					splayerup="Yes";
        				}else {
        					splayerup="No";
        				}
        				String sdivision = rs.getString("division_name");
        				
        				Player oplayer = new Player();
        				oplayer.setIdplayer(idplayer);
        				oplayer.setFirstname(sfirstname);
        				oplayer.setLastname(slastname);
        				oplayer.setCurrentteam(scurrentteam);
        				oplayer.setPreviousteam(sclubname);
        				oplayer.setDob(sdob);
        				oplayer.setLoidate(sloidate);
        				oplayer.setPlayerup(splayerup);
        				oplayer.setDivision(sdivision);
        				oplayer.setTeamid(steamid);
        				oplayer.setActive(sactive);
        				oplayer.setEligibility(seligibilitydate);
        				oplayer.setDrop(sdropdate);
        				oplayer.setRostertype(srostertype);
        				tempresult.add(oplayer);
    				}
    				
    				//LOGGER.info("We have results for caha data dump");
    				
    			}
    			rs.close();	
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR Lois for lookup date:" + date.toString());
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	//setResults(tempresult);
    	setPlayers(tempresult);
    	PlayerDataModel = new PlayerDataModel(players);
    }

    public PlayerDataModel getPlayerdatamodel(){
    	return PlayerDataModel;
    }
    
    public void setPlayerdatamodel(PlayerDataModel odatamodel){
    	PlayerDataModel = odatamodel;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    	
	public void CloseLoi(){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("confirmlois.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
}

