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
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Player;
import com.scaha.objects.Result;
import com.scaha.objects.ResultDataModel;

//import com.gbli.common.SendMailSSL;

public class playerreleaseBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private String searchcriteria = null;
	private Result selectedplayer = null;
	private List<Result> results = null;
    private ResultDataModel resultDataModel = null;
    private Integer clubid = null;
    private Integer profileid = null;
	
    @PostConstruct
    public void init() {
	    results = new ArrayList<Result>();  
        
        //need to load registrar's club id
        FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );

		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
    	this.setProfid(pb.getProfile().ID);
    	
        this.setClubid(loadClubid());
        playerSearch(); 
  
        resultDataModel = new ResultDataModel(results);
        
        //will need to load player profile information
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
    
    public Result getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Result selectedPlayer){
		selectedplayer = selectedPlayer;
	}
	
	public List<Result> getResults(){
		return results;
	}
	
	public void setResults(List<Result> list){
		results = list;
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
    public void playerSearch(){
    
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Result> tempresult = new ArrayList<Result>();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			CallableStatement cs = db.prepareCall("CALL scaha.playerreleasesearch(?,?)");
    		    cs.setString("sName", this.searchcriteria);
    		    cs.setInt("clubid", this.clubid);
    			rs = cs.executeQuery();
    			
    			if (rs != null){
    				while (rs.next()) {
    					String idperson = rs.getString("idperson");
        				String playername = rs.getString("fname") + " " + rs.getString("lname");
        				String address = rs.getString("address");
        				String city = rs.getString("city");
        				String state = rs.getString("state");
        				String zip = rs.getString("zipcode");
        				
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
        				String currentteam = rs.getString("currentteam");
        				
        				Result result = new Result(playername,idperson,address,dob);
        				result.setCurrentteam(currentteam);
        				tempresult.add(result);
    				}
    				
    				//LOGGER.info("We have results for search criteria " + this.searchcriteria);
    				
    			}
    			rs.close();	
    			db.cleanup();

    			//LOGGER.info("We have searched players for " + this.searchcriteria);
    			//return "true";
    		} else {
    			//return "False";
    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR " + this.searchcriteria);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	resultDataModel = new ResultDataModel(tempresult);
    }

    public ResultDataModel getResultdatamodel(){
    	return resultDataModel;
    }
    
public Integer loadClubid(){
		
		//first lets get club id for the logged in profile
		Integer clubid = 0;
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{
    			
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.getProfid());
			db.getData("CALL scaha.getClubforPerson(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					clubid = rs.getInt("idclub");
					}
				rs.close();
				//LOGGER.info("We have results for club for a profile");
			}
			
			db.cleanup();
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading club by profile");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
		return clubid;
	}

	public void startPermanentRelease(Result selectedPlayer){
		
		String sidplayer = selectedPlayer.getIdplayer();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("releaseform.xhtml?releasetype=p&playerid=" + sidplayer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void startTemporaryRelease(Result selectedPlayer,String type){
		
		String sidplayer = selectedPlayer.getIdplayer();
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
			context.getExternalContext().redirect("releaseform.xhtml?playerid=" + sidplayer + "&releasetype=" + type);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}

