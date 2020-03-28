
package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Division;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.SkillLevel;
import com.scaha.objects.Team;

@ManagedBean
@ViewScoped
public class safesportBean implements Serializable  {


	private PlayerDataModel playerlist = null;
	private PlayerDataModel personlist = null;
	
    //these are for viewing the safesport lsit.
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private String searchcriteria = "";			// Start out with no search criteria
	private Boolean isscahareg = null;
	private String fname = null;
	private String lname = null;
	private String email = null;
	private Player selectedsearchperson = null;
	
	//parameter is used for the safesport page for displaying the body content
	private String body = null;
	
	 @PostConstruct
	 public void init() {
		 
		//LOGGER.info(" *************** POST INIT FOR safesportBean *****************");
		FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );

		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
		setIsscahareg(pb.hasRoleList("S-REG"));
		 
		//LoadSelectionToAddList();
		LoadList();
		
		LoadBody();
	 }
	 
	 public String getBody(){
		 return body;
	 }
	 
	 public void setBody(String value){
		 body=value;
	 }

	 public Player getSelectedsearchperson(){
	    	return selectedsearchperson;
	    }
	    
	    public void setSelectedsearchperson(Player teamselected){
	    	selectedsearchperson=teamselected;
	    }
	 
	 
	 public String getSearchcriteria ()
	    {
	        return searchcriteria;
	    }
	    
	    public void setSearchcriteria (final String searchcriteria)
	    {
	        this.searchcriteria = searchcriteria;
	    }

	 
	  
	 public String getEmail(){
		return email;
	}
	
	public void setEmail(String value){
		email=value;
	}
		
	public String getFname(){
		return fname;
	}
	
	public void setFname(String value){
		fname=value;
	}
	 
	public String getLname(){
		return lname;
	}
	
	public void setLname(String value){
		lname=value;
	}
	 
	 public Boolean getIsscahareg(){
		return isscahareg;
	}
	
	public void setIsscahareg(Boolean value){
		isscahareg=value;
	}
	 
	public PlayerDataModel getPersonlist() {
		return personlist;
}

public void setPersonlist(PlayerDataModel playerlist) {
	this.personlist = playerlist;
}
	
	public PlayerDataModel getPlayerlist() {
			return playerlist;
	}

	/**
	 * @param playerlist the playerlist to set
	 */
	public void setPlayerlist(PlayerDataModel playerlist) {
		this.playerlist = playerlist;
	}

	public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
    	
    	}
	}
	
	public void setSafesport(Player inplayer,Integer flagstatus){
		String coachid = inplayer.getIdplayer();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		 
		try {
			CallableStatement cs = db.prepareCall("CALL scaha.setSafesportStatusByCoach(?,?)");
			cs.setInt("safesportstatusin", flagstatus);
			cs.setInt("incoachid",Integer.parseInt(coachid));
			ResultSet rs = cs.executeQuery();
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		db.cleanup();
		
		LoadList();
	 }
	
	public void LoadList(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
  		 List<Player> templist = new ArrayList<Player>();
		 
  		 try {
  			CallableStatement cs = db.prepareCall("CALL scaha.getSafeSportRegisteredPersons()");
			ResultSet rs = cs.executeQuery();
   			
			while (rs.next()) {
				String idplayer = rs.getString("idplayer");
				String sfirstname = rs.getString("fname");
				String slastname = rs.getString("lname");
				String status = rs.getString("safesportstatus");
				
				Player oplayer = new Player();
				oplayer.setIdplayer(idplayer);
				oplayer.setFirstname(sfirstname);
				oplayer.setLastname(slastname);
				oplayer.setEligibility(status);
				
				templist.add(oplayer);
				
			}
			
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		db.free();
  		db.cleanup();
  		
  		setPlayerlist(new PlayerDataModel(templist));
	}
	
	
	//load list of persons for scaha reg to select from to add to the list.
	public void LoadSelectionToAddList(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
  		 List<Player> templist = new ArrayList<Player>();
  		 
  		 
  		 try {
  			CallableStatement cs = db.prepareCall("CALL scaha.getSafeSportCandidates(?)");
			cs.setString("insearchcriteria", this.searchcriteria);
  			ResultSet rs = cs.executeQuery();
   			
			while (rs.next()) {
				String idplayer = rs.getString("idplayer");
				String sfirstname = rs.getString("fname");
				String slastname = rs.getString("lname");
				
				
				Player oplayer = new Player();
				oplayer.setIdplayer(idplayer);
				oplayer.setFirstname(sfirstname);
				oplayer.setLastname(slastname);
				
				templist.add(oplayer);
				
			}
			
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		db.free();
  		db.cleanup();
  		
  		setPersonlist(new PlayerDataModel(templist));
	}
	
		
		
	public void addPersontolist(){
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		Integer playerid = Integer.parseInt(this.selectedsearchperson.getIdplayer());
		
		
		//first need to create person then member then membership
		 
		try {
			CallableStatement cs = db.prepareCall("CALL scaha.addToSafesportList(?)");
			cs.setInt("playerid", playerid);
			
			cs.executeQuery();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		db.cleanup();
		
		
		LoadList();
	}
	
	public void LoadBody(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
  		 List<Player> templist = new ArrayList<Player>();
		 
  		 try {
  			CallableStatement cs = db.prepareCall("CALL scaha.getSafeSportBody()");
			ResultSet rs = cs.executeQuery();
   			
			while (rs.next()) {
				this.setBody(rs.getString("body"));
			}
			
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		db.free();
  		db.cleanup();
  		
  	}
	
	public void updateSafesport(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
  		 List<Player> templist = new ArrayList<Player>();
		 
  		 try {
  			CallableStatement cs = db.prepareCall("CALL scaha.updateSafeSportBody(?)");
  			cs.setString("inbody", this.body);
			ResultSet rs = cs.executeQuery();
   			
			while (rs.next()) {
				this.setBody(rs.getString("body"));
			}
			
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		db.free();
  		db.cleanup();
  		
  	}
}
