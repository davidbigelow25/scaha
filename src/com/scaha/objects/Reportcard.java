package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Reportcard extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private String filename = null;
	private String filedisplayname = null;
	private String filesize = null;
	private String filelocation = null;
	private Integer idreportcard = null;
	private Integer idperson = null;
	private String uploaddate = null;
	
	public Reportcard (){ 
		
	}
	
	public String getUploaddate(){
    	return uploaddate;
    }
    
    public void setUploaddate(String fname){
    	uploaddate=fname;
    }
	
	
	public String getFilename(){
    	return filename;
    }
    
    public void setFilename(String fname){
    	filename=fname;
    }
	
    public String getFiledisplayname(){
    	return filedisplayname;
    }
    
    public void setFiledisplayname(String fname){
    	filedisplayname=fname;
    }
	
    
    public String getFilesize(){
    	return filesize;
    }
    
    public void setFilesize(String fname){
    	filesize=fname;
    } 
	
	
	public String getFilelocation(){
    	return filelocation;
    }
    
    public void setFilelocation(String fname){
    	filelocation=fname;
    } 
		
	public Integer getIdperson(){
    	return idperson;
    }
    
    public void setIdperson(Integer fname){
    	idperson=fname;
    } 
	
    public Integer getIdreportcard(){
    	return idreportcard;
    }
    
    public void setIdreportcard(Integer fname){
    	idreportcard=fname;
    }
}
