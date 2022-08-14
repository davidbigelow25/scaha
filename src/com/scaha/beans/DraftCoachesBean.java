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

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Result;
import com.scaha.objects.ResultDataModel;
import com.scaha.objects.Team;

//import com.gbli.common.SendMailSSL;
@ManagedBean(name="draftcoachesBean")
@ViewScoped
public class DraftCoachesBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private String searchcriteria = "";
	private String selectedteam = null;
	private String selectedgirlsteam = null;
	private Result selectedcoach = null;
	private List<Team> teams = null;
    private ResultDataModel listofcoaches = null;
	
    @ManagedProperty(value = "#{profileBean}")
	private ProfileBean pb = null;
	@ManagedProperty(value = "#{scahaBean}")
	private ScahaBean scaha = null;
	
    @PostConstruct
    public void init() {
    	//coachSearch(); 
    }  
    
    public Result getSelectedcoach(){
		return selectedcoach;
	}
	
	public void setSelectedcoach(Result selectedCoach){
		selectedcoach = selectedCoach;
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
				team = null;
			}
			LOGGER.info("We have results for team list by club");
   			rs.close();
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading teams");
    		e.printStackTrace();
    	} finally {
    		db.free();
    	}
		
    	setTeams(templist);
    	templist=null;
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

    
    //retrieves list of players for registrar to select from based on criteria provided
    public ResultDataModel coachSearch(){
    
    	LOGGER.info("************WE HAVE A REQUEST TO RUN COACH SEARCH ******************");
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Result> tempresult = new ArrayList<Result>();
    	
    	try{
			Vector<String> v = new Vector<String>();
			v.add(this.searchcriteria);
			db.getData("CALL scaha.coachsearch(?)", v);
			ResultSet rs = db.getResultSet();
				
			while (rs.next()) {
				String idperson = rs.getString("idperson");
				String coachname = rs.getString("fname") + " " + db.getResultSet().getString("lname");
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
    				
				Result res = new Result("",idperson,address,"");
				res.setCoachname(coachname);
				res.setIdcoach(idperson);
				tempresult.add(res);
				res = null;
			}
				
			LOGGER.info("We have results for search criteria " + this.searchcriteria);
			rs.close();

    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR " + this.searchcriteria);
    		e.printStackTrace();
    	} finally {

    		db.free();
    	}
    	
    	listofcoaches = new ResultDataModel(tempresult);
    	tempresult=null;
    	return listofcoaches;
    }

    
  //retrieves list of coaches for adding to teams after they have been loid.  To return in the results they must have 
    //been loid previously
    public ResultDataModel loidcoachSearch(){
    
    	LOGGER.info("************WE HAVE A REQUEST TO RUN COACH SEARCH ******************");
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Result> tempresult = new ArrayList<Result>();
    	
    	try{
			Vector<String> v = new Vector<String>();
			v.add(this.searchcriteria);
			db.getData("CALL scaha.coachloidsearch(?)", v);
			ResultSet rs = db.getResultSet();
				
			while (rs.next()) {
				String idperson = rs.getString("idperson");
				String coachname = rs.getString("fname") + " " + db.getResultSet().getString("lname");
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
    				
				Result res = new Result("",idperson,address,"");
				res.setCoachname(coachname);
				res.setIdcoach(idperson);
				tempresult.add(res);
				res = null;
			}
				
			LOGGER.info("We have results for search criteria " + this.searchcriteria);
			rs.close();

    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR " + this.searchcriteria);
    		e.printStackTrace();
    	} finally {

    		db.free();
    	}
    	
    	listofcoaches = new ResultDataModel(tempresult);
    	tempresult=null;
    	return listofcoaches;
    }
    
    //Generates the loi for family to confirm info and add registration code.  
    //If player is on delinquency or has previously loi'd message will be displayed.
    public void generateLOI(Result result){
    	//Result tempResult = selectedcoach;
    	Result tempResult = result;
    	String selectedCoachid = tempResult.getIdcoach();
    	String selectedCoachname = tempResult.getCoachname();
    	
    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"", selectedCoachname + "'s LOI is being generated..."));
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editablecoachloi.xhtml?coachid=" + selectedCoachid);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void searchClose(){
    	searchcriteria = "";
    	FacesContext context = FacesContext.getCurrentInstance();
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

	/**
	 * @return the listofcoaches
	 */
	public ResultDataModel getListofcoaches() {
		return listofcoaches;
	}

	/**
	 * @param listofcoaches the listofcoaches to set
	 */
	public void setListofcoaches(ResultDataModel listofcoaches) {
		this.listofcoaches = listofcoaches;
	}
	
	//Generates the loi for family to confirm info and add registration code.  
    //If player is on delinquency or has previously loi'd message will be displayed.
    public void AddtoTeam(Result result){
    	
    	Result tempResult = result;
    	String selectedCoachid = tempResult.getIdcoach();
    	String selectedCoachname = tempResult.getCoachname();
    	
    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,"", selectedCoachname + "'s being added to teams after loi is done..."));
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("addcoachtoteams.xhtml?coachid=" + selectedCoachid);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
