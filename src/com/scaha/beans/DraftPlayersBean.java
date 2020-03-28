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
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Result;
import com.scaha.objects.ResultDataModel;
import com.scaha.objects.Team;

@ManagedBean(name="draftplayersBean")
@ViewScoped
public class DraftPlayersBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String searchcriteria = "";			// Start out with no search criteria
	private String selectedteam = null;
	private String selectedgirlsteam = null;
	private Result selectedplayer = null;
	private List<Team> teams = null;
    private ResultDataModel listofplayers = null;
	private String origin = null;
	private Boolean ishighschool = null;
	private Integer profileid = 0;
	private Integer clubid = null;
	
	@ManagedProperty(value = "#{profileBean}")
	private ProfileBean pb = null;
	@ManagedProperty(value = "#{scahaBean}")
	private ScahaBean scaha = null;
	
    
	@PostConstruct
    public void init() {
		
        //will need to load player profile information
        clubid = 1;  
     	this.setProfid(pb.getProfile().ID);
        getClubID();
        isClubHighSchool();
        //playerSearch(); 
  
	}
	
    public Integer getClubid(){
    	return clubid;
    }
    
    public void setClubid(Integer sclub){
    	clubid = sclub;
    }
    
    
    public Integer getProfid(){
    	return profileid;
    }
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    public Boolean getIshighschool(){
    	return ishighschool;
    }
    
    public void setIshighschool(Boolean value){
    	ishighschool = value;
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
	
	public String getSelectedgirlsteam(){
		return selectedgirlsteam;
	}
	
	public void setSelectedgirlsteam(String selectedTeam){
		selectedgirlsteam = selectedTeam;
	}
	
	public List<Team> getListofTeams(String gender){
		List<Team> templist = new ArrayList<Team>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

   		    CallableStatement cs = db.prepareCall("CALL scaha.getTeamsByClub(?,?)");
   		    cs.setInt("pclubid", 1);
   		    cs.setString("gender", gender);
   			ResultSet rs = cs.executeQuery();
   			
			while (rs.next()) {
				String idteam = rs.getString("idteams");
   				String teamname = rs.getString("teamname");
   				Team team = new Team(teamname,idteam);
   				templist.add(team);
			}
			//LOGGER.info("We have results for team list by club");
			rs.close();
			db.cleanup();
	
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading teams");
    		e.printStackTrace();
    	} finally {
    		db.free();
    	}
		
    	setTeams(templist);
		return getTeams();
	}
	
	public List<Team> getTeams(){
		return teams;
	}
	
	public void setTeams(List<Team> list){
		teams = list;
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
   			db.getData("CALL scaha.playerloisearch(?)", v);
   			ResultSet rs = db.getResultSet();
   
   			while (rs.next()) {
   				String idperson = rs.getString("idperson");
        		String playername = rs.getString("fname") + " " + db.getResultSet().getString("lname");
        		String address = rs.getString("address");
        		String city = db.getResultSet().getString("city");
        		String state = db.getResultSet().getString("state");
        		String zip = db.getResultSet().getString("zipcode");
        				
        		if (address == null){
        			address = "";
        		}
        		if (city != null){
        			address = address + ", " + city;
        		}
        		if (state != null){
        			address = address + ", " + state;
        		}
        		if (zip != null){
        			address = address + " " + zip;
        		}
        				
        		String dob = rs.getString("dob");
        		
        		Result result = new Result(playername,idperson,address,dob);
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

    public void delinquentplayerSearch(){
        
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Result> tempresult = new ArrayList<Result>();
    	
    	try{

   			Vector<String> v = new Vector<String>();
   			v.add(this.searchcriteria);
   			db.getData("CALL scaha.delinquentplayerloisearch(?)", v);
   			ResultSet rs = db.getResultSet();
   
   			while (rs.next()) {
   				String idperson = rs.getString("idperson");
        		String playername = rs.getString("fname") + " " + db.getResultSet().getString("lname");
        		String address = rs.getString("address");
        		String city = db.getResultSet().getString("city");
        		String state = db.getResultSet().getString("state");
        		String zip = db.getResultSet().getString("zipcode");
        				
        		if (address == null){
        			address = "";
        		}
        		if (city != null){
        			address = address + ", " + city;
        		}
        		if (state != null){
        			address = address + ", " + state;
        		}
        		if (zip != null){
        			address = address + " " + zip;
        		}
        				
        		String dob = rs.getString("dob");
        		
        		Result result = new Result(playername,idperson,address,dob);
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
   
    
    //Generates the loi for family to confirm info and add registration code.  
    //If player is on delinquency or has previously loi'd message will be displayed.
    public void generateLOI(Result result){
    	Result tempResult = result;
    	String selectedPlayerid = tempResult.getIdplayer();
    	String selectedPlayername = tempResult.getPlayername();
    	
    	//need to check if selected player is on delinquent list 
    	Boolean delinquent = verifyDelinquency(selectedPlayerid);
    	Boolean previousloi = verifyNoPreviousLOI(selectedPlayerid);
    	
    	//display message that player is delinquent
    	if (delinquent){
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", selectedPlayername + " is delinquent on a prior financial commitment.  An LOI can't be generated."));
    	}
    	
    	//display message that player is already loi'd
    	if (previousloi){
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", selectedPlayername + " has rostered with another club.  An LOI can't be generated."));
    	}
    	
    	//we are successful lets display the loi page
    	if (!delinquent && !previousloi){
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"", selectedPlayername + "'s LOI is being generated..."));
    		
    		FacesContext context = FacesContext.getCurrentInstance();
    		origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
			try{
				context.getExternalContext().redirect("editableloi.xhtml?playerid=" + selectedPlayerid);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
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

	private Boolean verifyDelinquency(String playerid){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	String isdelinquent = "";
    	
    	try{

    		
			Vector<Integer> v = new Vector<Integer>();
			v.add(Integer.parseInt(playerid));
			db.getData("CALL scaha.getDelinquencyByPlayerId(?)", v);
		    ResultSet rs = db.getResultSet();
			if (rs.next()){
				isdelinquent = rs.getString("delinquencycleared");
	    		//LOGGER.info("We have matching delinquency for:" + playerid);
			}
			rs.close();

    	} catch (SQLException e) {
    		LOGGER.info("ERROR IN Searching FOR " + this.searchcriteria);
    		e.printStackTrace();
    	} finally {
    		db.free();
    	}
    
    	if (isdelinquent.equals("N")){
    		return true;
    	}else{
    		return false;
    	}
    }
    
    private Boolean verifyNoPreviousLOI(String playerid){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	Integer previousTeamID = 0;
    	
    	try{

    		CallableStatement cs = null;
    		if (this.ishighschool){
    			cs = db.prepareCall("CALL scaha.isPlayerRosteredHS(?)");
    		} else {
    			cs = db.prepareCall("CALL scaha.isPlayerRostered(?)");
    		}
    		cs.setInt("in_idPerson", Integer.parseInt(playerid));
    		ResultSet rs = cs.executeQuery();
			if (rs.next()){
				previousTeamID = rs.getInt("idteam");
				//LOGGER.info("We have matching delinquency for:" + playerid);
			}
    		rs.close();		
    		cs.close();

    	} catch (SQLException e) {
    		LOGGER.info("ERROR IN Searching FOR " + this.searchcriteria);
    		e.printStackTrace();
    	} finally {
    		db.free();
    	}
    
    	if (previousTeamID>0){
    		return true;
    	}else{
    		return false;
    	}
    			
    }
    
    public void searchClose(){
    	searchcriteria = "";

    	FacesContext context = FacesContext.getCurrentInstance();
		origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
		try{
			context.getExternalContext().redirect("Welcome.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    public void addtoDelinquency(Result result){
    	Result tempResult = result;
    	int selectedPlayerid = Integer.parseInt(tempResult.getIdplayer());
    	String selectedPlayername = tempResult.getPlayername();
    	
    	//LOGGER.info("Adding to D-List: playerid=" + selectedPlayerid + " ,selectedPlayerName=" + selectedPlayername);
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		
    		CallableStatement cs = db.prepareCall("CALL scaha.addToDelinquency(?)");
			cs.setInt(1,selectedPlayerid);
		    cs.executeUpdate();
		    cs.close();
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"", selectedPlayername + "has been added to the Delinquency List"));
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR " + this.searchcriteria);
    		e.printStackTrace();
    		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,"", "Due to a SQL System error, we were unable to add " + selectedPlayername + "to the Delinquency List."));
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    		
    	}
		
		
    }
    
    public void isClubHighSchool(){
		
		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		Integer isschool = 0;
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.clubid);
			db.getData("CALL scaha.IsClubHighSchool(?)", v);
			ResultSet rs = db.getResultSet();
			while (rs.next()) {
				isschool = rs.getInt("result");
			}
			//LOGGER.info("We have results for club is a high school");
			rs.close();
			
			if (isschool.equals(0)){
				this.ishighschool=false;
			} else {
				this.ishighschool=true;
			}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading club by profile");
    		e.printStackTrace();
    	} finally {
    		db.free();
    	}
    }
    
    public void getClubID(){
		
		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.getProfid());
			db.getData("CALL scaha.getClubforPerson(?)", v);
		    
			ResultSet rs = db.getResultSet();
			while (rs.next()) {
				this.clubid = rs.getInt("idclub");
			}
			//LOGGER.info("We have results for club for a profile");
			rs.close();
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading club by profile");
    		e.printStackTrace();
    	} finally {
    		db.free();
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

