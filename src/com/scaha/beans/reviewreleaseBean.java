package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Release;
import com.scaha.objects.ReleaseDataModel;

//import com.gbli.common.SendMailSSL;


public class reviewreleaseBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private List<Release> releases = null;
    private ReleaseDataModel ReleaseDataModel = null;
    private Release selectedrelease = null;
	private String selectedreleaseid = null;
	
	
    public reviewreleaseBean() {  
        releases = new ArrayList<Release>();  
        ReleaseDataModel = new ReleaseDataModel(releases);
        
        releasesDisplay(); 
    }  
    
    public String getSelectedreleaseid(){
    	return selectedreleaseid;
    }
    
    public void setSelectedreleaseid(String selidrelease){
    	selectedreleaseid=selidrelease;
    }
    
    
    
    
    public Release getSelectedrelease(){
		return selectedrelease;
	}
	
	public void setSelectedrelease(Release selectedRelease){
		selectedrelease = selectedRelease;
	}
    
	public List<Release> getReleases(){
		return releases;
	}
	
	public void setReleases(List<Release> list){
		releases = list;
	}
	
	    
    //retrieves list of players
    public void releasesDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Release> tempresult = new ArrayList<Release>();
    	java.util.Date date = new java.util.Date();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
				CallableStatement cs = db.prepareCall("CALL scaha.getReleaseList()");
    			rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					String idrelease = rs.getString("idrelease");
        				String stype = rs.getString("type");
        				String ssubmitdate = rs.getString("submitdate");
        				String sfirstname = rs.getString("fname");
        				String slastname = rs.getString("lname");
        				String svalidthru = rs.getString("validthru");
        				String sreleasingclub = rs.getString("releasingclub");
        				String sacceptingclub = rs.getString("acceptingclub");
        				String sreason = rs.getString("reason");
        				String sstatus = rs.getString("status");
        				
        				Release orelease = new Release();
        				orelease.setIdrelease(idrelease);
        				orelease.setType(stype);
        				orelease.setSubmitdate(ssubmitdate);
        				orelease.setFirstname(sfirstname);
        				orelease.setLastname(slastname);
        				orelease.setValidthru(svalidthru);
        				orelease.setReleasingclub(sreleasingclub);
        				orelease.setAcceptingclub(sacceptingclub);
        				orelease.setReason(sreason);
        				orelease.setStatus(sstatus);
        				tempresult.add(orelease);
    				}
    				
    				//LOGGER.info("We have results for release list");
    				
    			}
    				
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR releases");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	
    	setReleases(tempresult);
    	ReleaseDataModel = new ReleaseDataModel(releases);
    }

    public ReleaseDataModel getReleasedatamodel(){
    	return ReleaseDataModel;
    }
    
    public void setReleasedatamodel(ReleaseDataModel odatamodel){
    	ReleaseDataModel = odatamodel;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    	
	public void viewrelease(Release selectedRelease){
	
		String sidrelease = selectedRelease.getIdrelease();
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("reviewrelease.xhtml?releaseid=" + sidrelease);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
}

