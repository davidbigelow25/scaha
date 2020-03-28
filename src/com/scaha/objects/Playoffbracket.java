package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Playoffbracket extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private Integer idbracket = null;
	private String teamname = null;
	private String game1 = null;
	private String game2 = null;
	private String game3 = null;
	private String gametotal = null;
	private String place = null;
	private String newgame1 = null;
	private String newgame2 = null;
	private String newgame3 = null;
	private String newgametotal = null;
	private String newplace = null;
	
	public Playoffbracket (){ 
		
	}
	
	public Integer getIdbracket(){
    	return idbracket;
    }
    
    public void setIdbracket(Integer fname){
    	idbracket=fname;
    } 
	
	public String getTeamname(){
		return teamname;
	}
	
	public void setTeamname(String sName){
		teamname = sName;
	}
	
	public String getGame1(){
		return game1;
	}
	
	public void setGame1(String sid){
		game1 = sid;
	}

	public String getGame2() {
		return game2;
	}

	public void setGame2(String _tag) {
		game2 = _tag;
	}
	
	public void setGame3(String _tag) {
		game3 = _tag;
	}
	
	public String getGame3() {
		return game3;
	}
	
	public void setGametotal(String _tag) {
		gametotal = _tag;
	}
	
	public String getGametotal() {
		return gametotal;
	}
	
	public void setPlace(String _tag) {
		place = _tag;
	}
	
	public String getPlace() {
		return place;
	}
	
	public String getNewgame1(){
		return newgame1;
	}
	
	public void setNewgame1(String sid){
		if (sid!=this.game1){
			//need to set and execute db call here
    		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
        	
        	try{
        		//first get team name
        		CallableStatement cs = db.prepareCall("CALL scaha.updateplayoffbracket(?,?,?)");
    			cs.setInt("bracketid", this.idbracket);
    			cs.setString("newvalue", sid);
    			cs.setString("bracketcell", "game1");
    		    cs.executeQuery();
    			db.commit();
    		    db.cleanup();
        		
        		//LOGGER.info("We have updated the bracket for game:" + this.idbracket + "game1");
    			
        		
        	} catch (SQLException e) {
        		// TODO Auto-generated catch block
        		LOGGER.info("ERROR IN updating bracket cell game 1");
        		e.printStackTrace();
        		db.rollback();
        	} finally {
        		//
        		// always clean up after yourself..
        		//
        		db.free();
        	}
    		
    		//finaly set the old name to match what we saved in the db
    		game1=sid;
    		newgame1=sid;
		}else{
			newgame1 = sid;
		}
	}

	public String getNewgame2() {
		return newgame2;
	}

	public void setNewgame2(String _tag) {
		if (_tag!=this.game2){
			//need to set and execute db call here
    		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
        	
        	try{
        		//first get team name
        		CallableStatement cs = db.prepareCall("CALL scaha.updateplayoffbracket(?,?,?)");
    			cs.setInt("bracketid", this.idbracket);
    			cs.setString("newvalue", _tag);
    			cs.setString("bracketcell", "game2");
    		    cs.executeQuery();
    			db.commit();
    		    db.cleanup();
        		
        		//LOGGER.info("We have updated the bracket for game:" + this.idbracket + "game2");
    			
        		
        	} catch (SQLException e) {
        		// TODO Auto-generated catch block
        		LOGGER.info("ERROR IN updating bracket cell game 2");
        		e.printStackTrace();
        		db.rollback();
        	} finally {
        		//
        		// always clean up after yourself..
        		//
        		db.free();
        	}
    		
    		//finaly set the old name to match what we saved in the db
    		game2=_tag;
    		newgame2=_tag;
		}else{
			newgame2 = _tag;
		}
	}
	
	public void setNewgame3(String _tag) {
		if (_tag!=this.game3){
			//need to set and execute db call here
    		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
        	
        	try{
        		//first get team name
        		CallableStatement cs = db.prepareCall("CALL scaha.updateplayoffbracket(?,?,?)");
    			cs.setInt("bracketid", this.idbracket);
    			cs.setString("newvalue", _tag);
    			cs.setString("bracketcell", "game3");
    		    cs.executeQuery();
    			db.commit();
    		    db.cleanup();
        		
        		//LOGGER.info("We have updated the bracket for game:" + this.idbracket + "game3");
    			
        		
        	} catch (SQLException e) {
        		// TODO Auto-generated catch block
        		LOGGER.info("ERROR IN updating bracket cell game 3");
        		e.printStackTrace();
        		db.rollback();
        	} finally {
        		//
        		// always clean up after yourself..
        		//
        		db.free();
        	}
    		
    		//finaly set the old name to match what we saved in the db
    		game3=_tag;
    		newgame3=_tag;
		}else{
			newgame3 = _tag;
		}
	}
	
	public String getNewgame3() {
		return newgame3;
	}
	
	public void setNewgametotal(String _tag) {
		if (_tag!=this.gametotal){
			//need to set and execute db call here
    		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
        	
        	try{
        		//first get team name
        		CallableStatement cs = db.prepareCall("CALL scaha.updateplayoffbracket(?,?,?)");
    			cs.setInt("bracketid", this.idbracket);
    			cs.setString("newvalue", _tag);
    			cs.setString("bracketcell", "gametotal");
    		    cs.executeQuery();
    			db.commit();
    		    db.cleanup();
        		
        		//LOGGER.info("We have updated the bracket for game:" + this.idbracket + "gametotal");
    			
        		
        	} catch (SQLException e) {
        		// TODO Auto-generated catch block
        		LOGGER.info("ERROR IN updating bracket cell game total");
        		e.printStackTrace();
        		db.rollback();
        	} finally {
        		//
        		// always clean up after yourself..
        		//
        		db.free();
        	}
    		
    		//finaly set the old name to match what we saved in the db
    		gametotal=_tag;
    		newgametotal=_tag;
		}else{
			newgametotal = _tag;
		}
	}
	
	public String getNewgametotal() {
		return newgametotal;
	}
	
	public void setNewplace(String _tag) {
		if (_tag!=this.place){
			//need to set and execute db call here
    		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
        	
        	try{
        		//first get team name
        		CallableStatement cs = db.prepareCall("CALL scaha.updateplayoffbracket(?,?,?)");
    			cs.setInt("bracketid", this.idbracket);
    			cs.setString("newvalue", _tag);
    			cs.setString("bracketcell", "place");
    		    cs.executeQuery();
    			db.commit();
    		    db.cleanup();
        		
        		//LOGGER.info("We have updated the bracket for game:" + this.idbracket + "place");
    			
        		
        	} catch (SQLException e) {
        		// TODO Auto-generated catch block
        		LOGGER.info("ERROR IN updating bracket cell place");
        		e.printStackTrace();
        		db.rollback();
        	} finally {
        		//
        		// always clean up after yourself..
        		//
        		db.free();
        	}
    		
    		//finaly set the old name to match what we saved in the db
    		place=_tag;
    		newplace=_tag;
		}else{
			newplace = _tag;
		}
	}
	
	public String getNewplace() {
		return newplace;
	}
	
}
