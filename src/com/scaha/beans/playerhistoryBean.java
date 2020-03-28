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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.PlayerStat;
import com.scaha.objects.Result;
import com.scaha.objects.ResultDataModel;
import com.scaha.objects.Team;

@ManagedBean
@ViewScoped
public class playerhistoryBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String searchcriteria = "";			// Start out with no search criteria
	private String selectedteam = null;
	private Result selectedplayer = null;
	private ResultDataModel listofplayers = null;
	private Integer profileid = 0;
	private List<PlayerStat> playerstats = null;
	private String playername = null;
	
	@ManagedProperty(value = "#{profileBean}")
	private ProfileBean pb = null;
	@ManagedProperty(value = "#{scahaBean}")
	private ScahaBean scaha = null;
	
    
	@PostConstruct
    public void init() {
		
        this.setProfid(pb.getProfile().ID);
        //playerSearch(); 
  
	}
	
    public Integer getProfid(){
    	return profileid;
    }
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    public Result getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Result selectedPlayer){
		selectedplayer = selectedPlayer;
	}
	
	public String getSelectedteam(){
		return selectedteam;
	}
	
	public void setSelectedteam(String selectedTeam){
		selectedteam = selectedTeam;
	}
	
	public String getPlayername(){
		return playername;
	}
	
	public void setPlayername(String value){
		playername = value;
	}
	
	public void generateHistory(String type){
		List<PlayerStat> templist = new ArrayList<PlayerStat>();
		this.setPlayername(this.selectedplayer.getPlayername());
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

   		    CallableStatement cs = db.prepareCall("CALL scaha.getPenaltyHistoryByPlayer(?)");
   		    cs.setInt("personid", Integer.parseInt(this.selectedplayer.getIdplayer()));
   		    ResultSet rs = cs.executeQuery();
   			
			while (rs.next()) {
				String teamname = rs.getString("teamname");
				String gp = rs.getString("gp");
				String penalties = rs.getString("penalties");
				String pims = rs.getString("pims");
				String gmcount = rs.getString("gmcount");
				
				PlayerStat ps = new PlayerStat();
				ps.setGmcount(gmcount);
				ps.setGp(gp);
				ps.setPenalties(penalties);
				ps.setPims(pims);
				ps.setTeamname(teamname);
   				templist.add(ps);
			}
			//LOGGER.info("We have results for penalty history by player");
			rs.close();
			db.cleanup();
	
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading player history");
    		e.printStackTrace();
    	} finally {
    		db.free();
    	}
		
    	this.setPlayerstats(templist);
		
	}
	
	public List<PlayerStat> getPlayerstats(){
		return playerstats;
	}
	
	public void setPlayerstats(List<PlayerStat> list){
		playerstats = list;
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
   				String idperson = rs.getString("idperson");
        		String playername = rs.getString("playername");
        		String teamname = rs.getString("teamname");
        		String dob = rs.getString("dob");
        		
        		Result result = new Result(playername,idperson,teamname,dob);
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

	
    
    public void searchClose(){
    	searchcriteria = "";

    	FacesContext context = FacesContext.getCurrentInstance();
		//String origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
		try{
			context.getExternalContext().redirect("Welcome.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
}

