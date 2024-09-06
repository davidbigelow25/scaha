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
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.LiveGame;
import com.scaha.objects.Player;
import com.scaha.objects.TempGame;
import com.scaha.objects.TempGameDataModel;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class patchBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	@ManagedProperty(value="#{scoreboardBean}")
	private ScoreboardBean sb;
	

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<Player> players = null;
	
    
	//bean level properties used by multiple methods
	private Integer profileid = 0;
	
	
    //properties for storing the selected value of drop downs
    private Integer selectedpatch = null;
	
	
	@PostConstruct
    public void init() {
		players = new ArrayList<Player>();  
        
                
	}
	
    public patchBean() {  
        
    }  
    
    public List<Player> getPlayers(){
    	return players;
    }
    
    public void setPlayers(List<Player> value){
    	players=value;
    }
    
    
    
    public Integer getSelectedpatch(){
    	return selectedpatch;
    }	
    
    public void setSelectedpatch(Integer value){
    	selectedpatch = value;
    }
    
        
    //retrieves list of players for the club
    public void getList(){
    	List<Player> tempresult = new ArrayList<Player>();
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		if (selectedpatch.equals(1)){
				CallableStatement cs = db.prepareCall("CALL scaha.getHatTrickPatchWinners()");
    			rs = cs.executeQuery();
			} else if (selectedpatch.equals(2)){
				CallableStatement cs = db.prepareCall("CALL scaha.getPlaymakerPatchWinners()");
				rs = cs.executeQuery();
			} else if (selectedpatch.equals(3)){
				CallableStatement cs = db.prepareCall("CALL scaha.getShutoutPatchWinners()");
				rs = cs.executeQuery();
			}
			if (rs != null){
				
				while (rs.next()) {
					String first = rs.getString("fname");
    				String last = rs.getString("lname");
    				String team = rs.getString("team");
    				String gamedate = rs.getString("actdate");
    				String address = rs.getString("address");
    				String city = rs.getString("city");
    				String state = rs.getString("state");
    				String zipcode = rs.getString("zipcode");
    				
    				Player player = new Player();
    				player.setFirstname(first);
    				player.setLastname(last);
    				player.setCurrentteam(team);
    				player.setLoidate(gamedate);
    				player.setAddress(address);
    				player.setCity(city);
    				player.setState(state);
    				player.setZip(zipcode);
    				tempresult.add(player);
    				
				}
				//LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedpatch.toString());
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting players for selection:" + this.selectedpatch.toString());
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setPlayers(tempresult);
    }

    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
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

	
	public ScoreboardBean getSb() {
		return sb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setSb(ScoreboardBean pb) {
		this.sb = pb;
	}
	
	
    
	
	
		
		
		
}

