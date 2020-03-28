package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import org.primefaces.event.CellEditEvent;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class TournamentDivision extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	private Integer idtournament = null;
	private String divisionname = null;
	private Boolean aaa = null;
	private Boolean aa = null;
	private Boolean a = null;
	private Boolean bb = null;
	private Boolean b = null;
	private Boolean track1 = null;
	private Boolean track2 = null;
	
	public TournamentDivision (){ 
		
	}
	
	public String getDivisionname(){
		return divisionname;
	}
	
	public void setDivisionname(String value){
		divisionname=value;
	}
	
	public Boolean getAaa(){
    	return aaa;
    }
    
    public void setAaa(Boolean fname){
    	aaa=fname;
    } 
	
    public Boolean getAa(){
    	return aa;
    }
    
    public void setAa(Boolean fname){
    	aa=fname;
    }
    
    public Boolean getA(){
    	return a;
    }
    
    public void setA(Boolean fname){
    	a=fname;
    }
    
    public Boolean getBb(){
    	return bb;
    }
    
    public void setBb(Boolean fname){
    	bb=fname;
    }
    
    public Boolean getB(){
    	return b;
    }
    
    public void setB(Boolean fname){
    	b=fname;
    }
    
    public Boolean getTrack1(){
    	return track1;
    }
    
    public void setTrack1(Boolean fname){
    	track1=fname;
    }
	 
    public Boolean getTrack2(){
    	return track2;
    }
    
    public void setTrack2(Boolean fname){
    	track2=fname;
    }
}
