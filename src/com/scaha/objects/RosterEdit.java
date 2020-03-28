package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class RosterEdit extends ScahaObject implements Serializable {
	
	//this class is used for the rosteredit by team managers and has a special method for setting property on jerseynumber
	//the method checks to see if new value being set is different than the value loaded from the db in the oldjerseynumber param
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	
	private String firstname = null;
	private String lastname = null;
	private String IDplayer = null;
	private String oldjerseynumber = null;
	private String jerseynumber = null;
	
	public RosterEdit (){ 
		
	}
	
	public String getJerseynumber(){
    	return jerseynumber;
    }
    
    public void setJerseynumber(String fname){
    	if (fname!=oldjerseynumber){
    		//need to set and execute db call here
    		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
        	
        	try{
        		//first get team name
        		CallableStatement cs = db.prepareCall("CALL scaha.updateJerseyNumber(?,?)");
    			cs.setInt("rosterid", Integer.parseInt(this.IDplayer));
    			cs.setString("newjerseynumber", fname);
    		    cs.executeQuery();
    			db.commit();
    		    db.cleanup();
        		
        		//LOGGER.info("We have updated the jersey number rosterid:" + this.IDplayer + "jersey number: " + fname);
    			
        		
        	} catch (SQLException e) {
        		// TODO Auto-generated catch block
        		LOGGER.info("ERROR IN updating jersey number");
        		e.printStackTrace();
        		db.rollback();
        	} finally {
        		//
        		// always clean up after yourself..
        		//
        		db.free();
        	}
    		
    		//finaly set the old name to match what we saved in the db
    		jerseynumber=fname;
    		oldjerseynumber=fname;
    	}	
    		
    	 else {
    		jerseynumber=fname;
    	}
    	
    } 
	
    public String getOldjerseynumber(){
    	return oldjerseynumber;
    }
    
    public void setOldjerseynumber(String fname){
    	oldjerseynumber=fname;
    }
    
    public String getFirstname(){
    	return firstname;
    }
    
    public void setFirstname(String fname){
    	firstname=fname;
    } 
	
    public String getLastname(){
    	return lastname;
    }
    
    public void setLastname(String lname){
    	lastname=lname;
    }
    
	public String getIdplayer(){
		return IDplayer;
	}
	
	public void setIdplayer(String sName){
		IDplayer = sName;
	}
	
		
		
}
