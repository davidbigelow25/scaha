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
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.FamilyMember;
import com.scaha.objects.Result;
import com.scaha.objects.ResultDataModel;

//import com.gbli.common.SendMailSSL;
@ManagedBean
@ViewScoped

public class scholarathleteBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private Result selectedplayer = null;
	private Result selectedpendingscholarathlete = null;
	private List<Result> results = null;
    private Integer clubid = null;
    private Integer profileid = null;
    private ResultDataModel listofplayers = null;
    private ResultDataModel listofpendingplayers = null;
    private Integer selecteddisplayfilter = null;
	
    @PostConstruct
    public void init() {
    	//setting the default filter for the 
    	this.setSelecteddisplayfilter(0);
    	
    	//lets set the list for reviewing the applications
    	
    	//lets load the list for the general public view
    	getScholarAthletes();
    }  
    
    public Result getSelectedpendingscholarathlete(){
    	return selectedpendingscholarathlete;
    }
    
    public void setSelectedpendingscholarathlete(Result value){
    	selectedpendingscholarathlete = value;
    }
    
    public Integer getSelecteddisplayfilter(){
    	return selecteddisplayfilter;
    }
    
    public void setSelecteddisplayfilter(Integer value){
    	selecteddisplayfilter = value;
    }
    
    public Integer getProfid(){
    	return profileid;
    }
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    
    public Integer getClubid(){
    	return clubid;
    }
    
    public void setClubid(Integer sclub){
    	clubid = sclub;
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
	
	/**
	 * @return the listofpendingplayers
	 */
	public ResultDataModel getListofpendingplayers() {
		return listofpendingplayers;
	}

	/**
	 * @param listofpendingplayers the listofpendingplayers to set
	 */
	public void setListofpendingplayers(ResultDataModel listofplayers) {
		this.listofpendingplayers = listofplayers;
	}
	    
    //retrieves list of approved players to display on general public view page. 
    public void getScholarAthletes(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Result> tempresult = new ArrayList<Result>();
    	
    	try{

   			Vector<String> v = new Vector<String>();
   			db.getData("CALL scaha.getScholarathletes()", v);
   			ResultSet rs = db.getResultSet();
   
   			while (rs.next()) {
   				String playername = rs.getString("name");
        		String division = rs.getString("division");
        		String skilllevel = db.getResultSet().getString("skilllevel");
        		String club = db.getResultSet().getString("club");
        			
        		Result result = new Result(playername,"0",skilllevel,division);
        		result.setCurrentteam(club);
        		tempresult.add(result);
    		}
    				
    		//LOGGER.info("We have results for scholar athletes ");
    		rs.close();
    		db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting results for scholar athletes");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		db.free();
    	}
    	
    	listofplayers = new ResultDataModel(tempresult);
    }

    public void uploadreportcoard(FamilyMember fm){
    	FacesContext context = FacesContext.getCurrentInstance();
    	Integer personid = fm.getPersonID();
    	
    	try{
    		context.getExternalContext().redirect("managereportcard.xhtml?id=" + personid.toString());
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
    }
    
    
    //this method populates the approve scholar athlete players data model list.
    public void playersDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Result> tempresult = new ArrayList<Result>();
    	
    	try{

    		CallableStatement cs = db.prepareCall("CALL scaha.getScholarAthletesByType(?)");
			cs.setInt("selectedfilter", this.selecteddisplayfilter);
			rs = cs.executeQuery();
			
   
   			while (rs.next()) {
   				String playername = rs.getString("name");
        		String division = rs.getString("division");
        		String skilllevel = rs.getString("skilllevel");
        		String club = rs.getString("club");
        		Integer personid = rs.getInt("personid");
        		String reportcard = rs.getString("reportcard");
        		Integer scholarathleteid = rs.getInt("idscholarathlete");
        		Boolean isapproved = rs.getBoolean("isapproved");
        		String address = rs.getString("address");
        		String city = rs.getString("city");
        		String state = rs.getString("state");
        		String zip = rs.getString("zip"); 
        			
        		Result result = new Result(playername,"0",skilllevel,division);
        		result.setCurrentteam(club);
        		result.setIdperson(personid);
        		result.setReportcard(reportcard);
        		result.setIdscholarathlete(scholarathleteid);
        		result.setIsapproved(isapproved);
        		result.setAddress(address);
        		result.setCity(city);
        		result.setState(state);
        		result.setZip(zip);
        		tempresult.add(result);
    		}
    				
    		//LOGGER.info("We have results for scholar athletes ");
    		rs.close();
    		db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting results for scholar athletes");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		db.free();
    	}
    	
    	listofpendingplayers = new ResultDataModel(tempresult);
    }
    
    public void approveScholar(Result result){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		CallableStatement cs = db.prepareCall("CALL scaha.ApproveScholarAthlete(?)");
			cs.setInt("scholarathleteid", result.getIdscholarathlete());
			rs = cs.executeQuery();
			
    		//LOGGER.info("We have approved the scholar athlete");
    		rs.close();
    		db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN approving for scholar athletes");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		db.free();
    	}
    	
    	this.playersDisplay();
    }
}
    
    

    


