package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class TempGame extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private String date = null;
	private String visitor = null;
	private String home = null;
	private String location = null;
	private Integer idgame = null;
	private String time = null;
	private String homescore = null;
	private String oldhomescore = null;
	private String awayscore = null;
	private String oldawayscore = null;
	private String scoresheet = null;
	private String rinkaddress = null;
	private String hometeamimage = null;
	private String awayteamimage = null;
	private String status = null;
	private Boolean iscomplete = null;
	private Boolean teamstatsstatus = null;
	private Boolean is8u = null;
	private Boolean homecoachflag = null;
	private Boolean coachcountflag = null;
	private Boolean printeligibleflag = null;
	private String printurl = null;
	private Integer idhome = null;
	private Integer idaway = null;
	private Integer fontsizehome = null;
	private Integer fontsizevisitor = null;
	private Integer homeclubid = null;
	private Integer awayclubid = null;
	
	
	public TempGame (){ 
		
	}
	
	public Integer getHomeclubid(){
		return homeclubid;
	}
	
	public void setHomeclubid(Integer value){
		homeclubid=value;
	}
	
	public Integer getAwayclubid(){
		return awayclubid;
	}
	
	public void setAwayclubid(Integer value){
		awayclubid=value;
	}
	
	
	public Integer getFontsizevisitor(){
		return fontsizevisitor;
	}
	
	public void setFontsizevisitor(Integer value){
		fontsizevisitor=value;
	}
	
	public Integer getFontsizehome(){
		return fontsizehome;
	}
	
	public void setFontsizehome(Integer value){
		fontsizehome=value;
	}
	
	
	public Integer getIdhome(){
		return idhome;
	}
	
	public void setIdhome(Integer value){
		idhome=value;
	}
	
	public Integer getIdaway(){
		return idaway;
	}
	
	public void setIdaway(Integer value){
		idaway=value;
	}
	
	
	
	public String getPrinturl(){
		return printurl;
	}
	
	public void setPrinturl(String value){
		printurl=value;
	}
	
	
	public Boolean getPrinteligibleflag(){
		return printeligibleflag;
	}
	
	public void setPrinteligibleflag(Boolean value){
		printeligibleflag=value;
	}
	
	
	public Boolean getCoachcountflag(){
		return coachcountflag;
	}
	
	public void setCoachcountflag(Boolean value){
		coachcountflag=value;
	}
	
	
	
	public Boolean getHomecoachflag(){
		return homecoachflag;
	}
	
	public void setHomecoachflag(Boolean value){
		homecoachflag=value;
	}
	
	
	public Boolean getIs8u(){
		return is8u;
	}
	
	public void setIs8u(Boolean value){
		is8u=value;
	}
	
	
	public Boolean getTeamsstatsstatus(){
		return teamstatsstatus;
	}
	
	public void setTeamsstatsstatus(Boolean value){
		teamstatsstatus=value;
	}
	
	
	
	public Boolean getIscomplete(){
		return iscomplete;
	}
	
	public void setIscomplete(Boolean value){
		iscomplete=value;
	}
	
	public String getRinkaddress(){
    	return rinkaddress;
    }
    
    public void setRinkaddress(String fname){
    	rinkaddress=fname;
    } 
	
    public String getHometeamimage(){
    	return hometeamimage;
    }
    
    public void setHometeamimage(String fname){
    	hometeamimage=fname;
    } 
	
    public String getAwayteamimage(){
    	return awayteamimage;
    }
    
    public void setAwayteamimage(String fname){
    	awayteamimage=fname;
    } 
	
	
	public String getStatus(){
    	return status;
    }
    
    public void setStatus(String fname){
    	status=fname;
    } 
	
	
	public String getScoresheet(){
    	return scoresheet;
    }
    
    public void setScoresheet(String fname){
    	scoresheet=fname;
    } 
	
	
	public String getDate(){
    	return date;
    }
    
    public void setDate(String fname){
    	date=fname;
    } 
	
	
	public String getVisitor(){
    	return visitor;
    }
    
    public void setVisitor(String fname){
    	visitor=fname;
    } 
	
	public String getHome(){
    	return home;
    }
    
    public void setHome(String fname){
    	home=fname;
    } 
	
	
	public String getLocation(){
    	return location;
    }
    
    public void setLocation(String fname){
    	location=fname;
    } 
	
	
	public Integer getIdgame(){
    	return idgame;
    }
    
    public void setIdgame(Integer fname){
    	idgame=fname;
    } 
	
	
	public String getTime(){
    	return time;
    }
    
    public void setTime(String fname){
    	time=fname;
    } 
	
	
	public String getHomescore(){
    	return homescore;
    }
    
    public void setHomescore(String fname){
    	if (fname!=this.oldhomescore){
    		//need to set and execute db call here
    		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
        	
        	try{
        		//first get team name
        		CallableStatement cs = db.prepareCall("CALL scaha.updateScore(?,?,?)");
    			cs.setInt("livegameid", this.idgame);
    			cs.setString("newawayscore", fname);
    			cs.setString("homeaway", "home");
    		    cs.executeQuery();
    			db.commit();
    		    db.cleanup();
        		
        		//LOGGER.info("We have updated the home score:" + this.idgame + "home score: " + fname);
    			
        		
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
    		homescore=fname;
    		oldhomescore=fname;
    	}	
    		
    	 else {
    		 homescore=fname;
    	}
    	
    } 
	
    public String getOldhomescore(){
    	return oldhomescore;
    }
    
    public void setOldhomescore(String fname){
    	oldhomescore=fname;
    } 
	
    
    public String getAwayscore(){
    	return awayscore;
    }
    
    public void setAwayscore(String lname){
    	if (lname!=this.oldawayscore){
    		//need to set and execute db call here
    		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
        	
        	try{
        		//first get team name
        		CallableStatement cs = db.prepareCall("CALL scaha.updateScore(?,?,?)");
    			cs.setInt("livegameid", this.idgame);
    			cs.setString("newawayscore", lname);
    			cs.setString("homeaway", "away");
    		    cs.executeQuery();
    			db.commit();
    		    db.cleanup();
        		
        		//LOGGER.info("We have updated the away score:" + this.idgame + "away score: " + lname);
    			
        		
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
    		awayscore=lname;
    		oldawayscore=lname;
    	}	
    		
    	 else {
    		 awayscore=lname;
    	}
    	
    }
    
    public String getOldawayscore(){
    	return oldawayscore;
    }
    
    public void setOldawayscore(String lname){
    	oldawayscore=lname;
    }
    	
	
		
}
