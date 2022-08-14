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
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.GameRequest;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class changerequestBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<GameRequest> requests = null;
	
		
	
	@PostConstruct
    public void init() {
		
        //Load Tournament and Games
        loadRequests();
        
           
	}
	
    public changerequestBean() {  
        
    }  
    
    
    
    public List<GameRequest> getRequests(){
		return requests;
	}
	
	public void setRequests(List<GameRequest> list){
		requests = list;
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

	public void loadRequests() {  
		List<GameRequest> templist = new ArrayList<GameRequest>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getAllChangeRequests()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String requestingteam = rs.getString("requestingteam");
    				String gamedetail = rs.getString("gamedetail");
    				String reason = rs.getString("reason");
    				String notes = rs.getString("notes");
    				
    				GameRequest request = new GameRequest();
    				request.setTeamname(requestingteam);
    				request.setGamedetails(gamedetail);
    				request.setReason(reason);
    				request.setNotes(notes);
    				
    				templist.add(request);
				}
				LOGGER.info("We have results for all request lists");
			}
			
			
			rs.close();
			db.cleanup();
    		
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting request list for schedule change");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setRequests(templist);
    }
	
	
}

