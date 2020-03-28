package com.scaha.beans;

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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;

//import com.gbli.common.SendMailSSL;

@ManagedBean(name="delinquencyBean")
@ViewScoped
public class DelinquencyBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

    private PlayerDataModel playerlist = null;
    private PlayerDataModel clubplayerlist = null;
    private Player selectedplayer = null;
	private String selectedtabledisplay = null;
	private Boolean displayclublist = null;
	private String selectedclub = null;
	private String selectedplayerid = null;
	private Boolean displayshortlist = null;
	private Integer displayrecordcount = null;
	private Integer totalrecordcount = null;
	private List<Player> filteredplayers = null;

	@ManagedProperty(value = "#{profileBean}")
	private ProfileBean pb = null;
	@ManagedProperty(value = "#{scahaBean}")
	private ScahaBean scaha = null;
	
	
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

	@PostConstruct
	public void init() {
		//LOGGER.info("**************** DelinquencyBean Has Been Init-ed ********************");
		playersDisplay();
		clubplayersDisplay();
    }  
    
	public Integer getTotalrecordcount(){
    	return totalrecordcount;
    }
    
    public void setTotalrecordcount(Integer count){
    	totalrecordcount=count;
    }
	
	public Integer getDisplayrecordcount(){
    	return displayrecordcount;
    }
    
    public void setDisplayrecordcount(Integer count){
    	displayrecordcount=count;
    }
    
	
	public Boolean getDisplayshortlist(){
    	return displayshortlist;
    }
    
    public void setDisplayshortlist(Boolean flag){
    	displayshortlist=flag;
    }
    
    
    public String getSelectedplayerid(){
    	return selectedplayerid;
    }
    
    public void setSelectedplayerid(String selidplayer){
    	selectedplayerid=selidplayer;
    }
    
    public String getSelectedclub(){
    	return selectedclub;
    }
    
    public void setSelectedclub(String clubselected){
    	selectedclub=clubselected;
    }
    
    public Boolean getDisplayclublist(){
    	return displayclublist;
    }
    
    public void setDisplayclublist(Boolean club){
    	displayclublist = club;
    }
    
    public String getSelectedtabledisplay(){
		return selectedtabledisplay;
	}
	
	public void setSelectedtabledisplay(String selectedTabledisplay){
		selectedtabledisplay = selectedTabledisplay;
	}
    
    public Player getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Player selectedPlayer){
		selectedplayer = selectedPlayer;
	}
    
	    
    //retrieves list of players
    public void playersDisplay(){
    	//LOGGER.info("Refreshing the list of delinquent players..");
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Player> tempresult = new ArrayList<Player>();
    	Integer recordcount = 0;
    	
    	//need to set default record count and list format boolean for displaying the list either in pagination format
        //or long list printable format
        this.displayshortlist = true;
        this.displayrecordcount = 10;
        this.totalrecordcount = 0;
        
    	
    	try{

			CallableStatement cs = db.prepareCall("CALL scaha.getDelinquencyList()");
			ResultSet rs = cs.executeQuery();
    			
			while (rs.next()) {
				String idplayer = rs.getString("idplayer");
				String sfirstname = rs.getString("fname");
				String slastname = rs.getString("lname");
				String scurrentteam = rs.getString("currentteam");
				String sdob = rs.getString("dob");
				String delyear = rs.getString("year");
				
				Player oplayer = new Player();
				oplayer.setIdplayer(idplayer);
				oplayer.setFirstname(sfirstname);
				oplayer.setLastname(slastname);
				oplayer.setCurrentteam(scurrentteam);
				oplayer.setDob(sdob);
				oplayer.setEligibility(delyear);
				
				tempresult.add(oplayer);
				
				recordcount++;
			}
    				
   			this.totalrecordcount=recordcount;
   			rs.close();
   			cs.close();
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR delinquency list");
    		e.printStackTrace();
    	} finally {
    		
    		db.free();
    	}
    	
    	setPlayerlist(new PlayerDataModel(tempresult));
    	LOGGER.info("scaha view delinquency list");
    }

    public void clubplayersDisplay(){
    	//LOGGER.info("Refreshing the list of delinquent players..");
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Player> tempresult = new ArrayList<Player>();
    	Integer recordcount = 0;
    	
    	//need to set default record count and list format boolean for displaying the list either in pagination format
        //or long list printable format
        this.displayshortlist = true;
        this.displayrecordcount = 10;
        this.totalrecordcount = 0;
        
    	
    	try{

			CallableStatement cs = db.prepareCall("CALL scaha.getClubviewDelinquencyList()");
			ResultSet rs = cs.executeQuery();
    			
			while (rs.next()) {
				String idplayer = rs.getString("idplayer");
				String sfirstname = rs.getString("fname");
				String slastname = rs.getString("lname");
				String scurrentteam = rs.getString("currentteam");
				String sdob = rs.getString("dob");
				String delyear = rs.getString("year");
				
				Player oplayer = new Player();
				oplayer.setIdplayer(idplayer);
				oplayer.setFirstname(sfirstname);
				oplayer.setLastname(slastname);
				oplayer.setCurrentteam(scurrentteam);
				oplayer.setDob(sdob);
				oplayer.setEligibility(delyear);
				
				tempresult.add(oplayer);
				
				recordcount++;
			}
    				
   			this.totalrecordcount=recordcount;
   			rs.close();
   			cs.close();
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR delinquency list");
    		e.printStackTrace();
    	} finally {
    		db.free();
    	}
    	
    	setClubplayerlist(new PlayerDataModel(tempresult));
    	LOGGER.info("club view delinquency list");
    }
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    public void removePlayer(Player play){
		
    	this.selectedplayer = play;
    	
		for (Player p : playerlist) {
			if (p.getIdplayer().equals(this.selectedplayer.getIdplayer())) {
				selectedplayer = p;
				break;
			}
		}
		
		String sidplayer = selectedplayer.getIdplayer();
		String playname = selectedplayer.getFirstname() + " " + selectedplayer.getLastname();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			//LOGGER.info("update player in delinquency list");
			CallableStatement cs = db.prepareCall("CALL scaha.removeDelinquency(?)");
		    cs.setInt("iplayerid", Integer.parseInt(sidplayer));
		    cs.executeUpdate();
			//LOGGER.info("We are remove the delinquency for player id:" + sidplayer);
		    cs.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN removing the delinquency for " + this.selectedplayer);
			e.printStackTrace();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		this.filteredplayers = null;
		List<Player> pl = (List<Player>) playerlist.getWrappedData();
		pl.remove(selectedplayer);
		FacesContext context = FacesContext.getCurrentInstance();  
        context.addMessage(null, new FacesMessage("Successful", "You have removed the delinquency for: " + playname));
		//playersDisplay();
        LOGGER.info("remove delinquency");
	}
    
    public void displayLongList(){
    	this.displayshortlist=false;
    	this.displayrecordcount=0;
    }
    
    public void displayShortList(){
    	this.displayshortlist=true;
    	this.displayrecordcount=10;
    }

    /**
	 * @return the playerlist
	 */
	public PlayerDataModel getClubplayerlist() {
		return clubplayerlist;
	}

	/**
	 * @param playerlist the playerlist to set
	 */
	public void setClubplayerlist(PlayerDataModel playerlist) {
		this.clubplayerlist = playerlist;
	}
    
    /**
	 * @return the playerlist
	 */
	public PlayerDataModel getPlayerlist() {
		return playerlist;
	}

	/**
	 * @param playerlist the playerlist to set
	 */
	public void setPlayerlist(PlayerDataModel playerlist) {
		this.playerlist = playerlist;
	}



	/**
	 * @return the filteredplayers
	 */
	public List<Player> getFilteredplayers() {
		return filteredplayers;
	}

	/**
	 * @param filteredplayers the filteredplayers to set
	 */
	public void setFilteredplayers(List<Player> filteredplayers) {
		this.filteredplayers = filteredplayers;
	}
}

