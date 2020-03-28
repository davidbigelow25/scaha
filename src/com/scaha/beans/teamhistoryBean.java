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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.PlayerStat;
import com.scaha.objects.Result;
import com.scaha.objects.ResultDataModel;
import com.scaha.objects.Team;

@ManagedBean
@ViewScoped
public class teamhistoryBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private Integer profileid = 0;
	private List<Team> listofteams = null;
	
	@ManagedProperty(value = "#{profileBean}")
	private ProfileBean pb = null;
	@ManagedProperty(value = "#{scahaBean}")
	private ScahaBean scaha = null;
	
    
	@PostConstruct
    public void init() {
		
        this.setProfid(pb.getProfile().ID);
        
        //need to load teams data on page load
        generateHistory();
	}
	
    public List<Team> getListofteams(){
    	return listofteams;
    }
    
    public void setListofteams(List<Team> list){
    	listofteams = list;
    }
    
    public Integer getProfid(){
    	return profileid;
    }
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    public void generateHistory(){
		List<Team> templist = new ArrayList<Team>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

   		    CallableStatement cs = db.prepareCall("CALL scaha.getPIMSbyTeam()");
   		    ResultSet rs = cs.executeQuery();
   			
			while (rs.next()) {
				String teamname = rs.getString("teamname");
				String teamid = rs.getString("idteam");
				String gp = rs.getString("gp");
				String minorpenalties = rs.getString("minorpenalties");
				String majorpenalties = rs.getString("majorpenalties");
				String pims = rs.getString("pims");
				String gmcount = rs.getString("gmcount");
				String matchcount = rs.getString("matchcount");
				
				Team team = new Team(teamname,teamid);
				team.setGmcount(gmcount);
				team.setGp(gp);
				team.setMinorpenalties(minorpenalties);
				team.setMajorpenalties(majorpenalties);
				team.setPims(pims);
				team.setMatchcount(matchcount);
   				templist.add(team);
			}
			//LOGGER.info("We have results for penalty history by team");
			rs.close();
			db.cleanup();
	
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading team history");
    		e.printStackTrace();
    	} finally {
    		db.free();
    	}
		
    	this.setListofteams(templist);
		
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
}

