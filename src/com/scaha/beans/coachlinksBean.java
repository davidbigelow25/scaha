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
import javax.faces.bean.ViewScoped;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Link;

//import com.gbli.common.SendMailSSL;
@ManagedBean
@ViewScoped
public class coachlinksBean implements Serializable {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private List<Link> certificationslinklist = null;
	private List<Link> informationlinklist = null;
	private List<Link> screeningslinklist = null;
	transient private ResultSet rs = null;
	
	
	@PostConstruct
    public void init() {
		
        loadLinks();
        
        
	}
	
    public coachlinksBean() {  
        
    }  
    
    public List<Link> getCertificationslinklist(){
    	return certificationslinklist;
    }
    
    public void setCertificationslinklist(List<Link> list){
    	certificationslinklist=list;
    }
    
    public List<Link> getInformationlinklist(){
    	return informationlinklist;
    }
    
    public void setInformationlinklist(List<Link> list){
    	informationlinklist=list;
    }
    
    public List<Link> getScreeningslinklist(){
    	return screeningslinklist;
    }
    
    public void setScreeningslinklist(List<Link> list){
    	screeningslinklist=list;
    }
    
    
    public void loadLinks() {  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Link> tempcerts = new ArrayList<Link>();
		List<Link> tempinfo = new ArrayList<Link>();
		List<Link> tempscreen = new ArrayList<Link>();
		
    	try{
    		//first get cert links
    		CallableStatement cs = db.prepareCall("CALL scaha.getCertificationlinks()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer linkid = rs.getInt("idlinks");
					String linkurl = rs.getString("linkurl");
					String linklabel = rs.getString("linklabel");
					
					Link link = new Link();
					link.setLinkid(linkid);
					link.setLinkurl(linkurl);
					link.setLinklabel(linklabel);
					
					tempcerts.add(link);
    					}
				LOGGER.info("We have results for certification list");
			}
			rs.close();
			
			//first get info links
    		cs = db.prepareCall("CALL scaha.getInformationlinks()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer linkid = rs.getInt("idlinks");
					String linkurl = rs.getString("linkurl");
					String linklabel = rs.getString("linklabel");
					
					Link link = new Link();
					link.setLinkid(linkid);
					link.setLinkurl(linkurl);
					link.setLinklabel(linklabel);
					
					tempinfo.add(link);
    					}
				LOGGER.info("We have results for certification list");
			}
			
			//first get screening links
    		cs = db.prepareCall("CALL scaha.getScreeninglinks()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer linkid = rs.getInt("idlinks");
					String linkurl = rs.getString("linkurl");
					String linklabel = rs.getString("linklabel");
					
					Link link = new Link();
					link.setLinkid(linkid);
					link.setLinkurl(linkurl);
					link.setLinklabel(linklabel);
					
					tempscreen.add(link);
    					}
				LOGGER.info("We have results for screening list");
			}
			
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting coacing link list");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	this.setCertificationslinklist(tempcerts);
    	this.setInformationlinklist(tempinfo);
    	this.setScreeningslinklist(tempscreen);
		
    }
	
}

