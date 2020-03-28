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

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.Team;

//import com.gbli.common.SendMailSSL;


public class registrarloiBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	transient private ResultSet rs = null;
	
	private List<Player> players = null;
    private PlayerDataModel PlayerDataModel = null;
    private Player selectedplayer = null;
	private String selectedtabledisplay = null;
	private List<Team> teams = null;
	private Boolean displayteamlist = null;
	private String selectedteam = null;
	private String selectedplayerid = null;
	private Integer clubid = 0;
	private Integer profileid = 0;
	private String currentyear = null;
	private String prioryear = null;
	
	
    public registrarloiBean() {  
    	clubid = 1;
    	FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );

		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
    	this.setProfid(pb.getProfile().ID);
    	loadClubName();
    	
    	players = new ArrayList<Player>();  
        PlayerDataModel = new PlayerDataModel(players);
        this.setSelectedtabledisplay("1");
        
      //need to add scaha session object
  		ValueExpression scahaexpression = app.getExpressionFactory().createValueExpression( context.getELContext(),
  				"#{scahaBean}", Object.class );

  		scaha = (ScahaBean) scahaexpression.getValue( context.getELContext() );
  		
  		//need to set current year and prior year
  		String cyear = scaha.getScahaSeasonList().getCurrentSeason().getFromDate().substring(0,4);
  		this.setCurrentyear(cyear);
  		
  		Integer pyear = Integer.parseInt(cyear) - 1;
  		this.setPrioryear(pyear.toString());
        
        
        playersDisplay(); 
    }  
    
    public Integer getProfid(){
    	return profileid;
    }
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    public Integer getClubid(){
    	return clubid;
    }
    
    public void setClubid(Integer idclub){
    	clubid = idclub;
    }
    
    public String getCurrentyear(){
		return currentyear;
	}
	
	public void setCurrentyear(String cyear){
		currentyear=cyear;
	}
	
	public String getPrioryear(){
			return prioryear;
	}
	
	public void setPrioryear(String cyear){
		prioryear=cyear;
	}
    
	public String getSelectedteam(){
    	return selectedteam;
    }
    
    public void setSelectedteam(String teamselected){
    	selectedteam=teamselected;
    }
    
    public Boolean getDisplayteamlist(){
    	return displayteamlist;
    }
    
    public void setDisplayteamlist(Boolean team){
    	displayteamlist = team;
    }
    
    public String getSelectedtabledisplay(){
		return selectedtabledisplay;
	}
	
	public void setSelectedtabledisplay(String selectedTabledisplay){
		selectedtabledisplay = selectedTabledisplay;
	}
    
    public Player getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Player selectedPlayer){
		selectedplayer = selectedPlayer;
	}
    
	public List<Player> getPlayers(){
		return players;
	}
	
	public void setPlayers(List<Player> list){
		players = list;
	}
	
	    
    //retrieves list of players
    public void playersDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Player> tempresult = new ArrayList<Player>();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			if (this.selectedteam==null){
    				this.selectedteam = "0";
    			}
    			Integer iteam = Integer.parseInt(this.selectedteam);
    			
				if (iteam.equals(0)){
					CallableStatement cs = db.prepareCall("CALL scaha.getLoiByClub(?)");
					cs.setInt("clubid", this.clubid);
					rs = cs.executeQuery();
				} else {
					CallableStatement cs = db.prepareCall("CALL scaha.getLoiListByTeam(?)");
					cs.setInt("teamid", iteam);
					rs = cs.executeQuery();
				}
				
    			if (rs != null){
    				
    				while (rs.next()) {
    					String idplayer = rs.getString("idperson");
        				String sfirstname = rs.getString("firstname");
        				String slastname = rs.getString("lastname");
        				String scurrentteam = rs.getString("currentteam");
        				String slastyearteam = rs.getString("lastyearteam");
        				if (slastyearteam==null){
        					slastyearteam="N/A";
        				}
        				String sdob = rs.getString("dob");
        				String sloidate = rs.getString("loidate");
        				String susamember = rs.getString("usamembership");
        				String scitizenship = rs.getString("citizenship");
        				String scitizenshipexpiredate = rs.getString("citizenshipexpiredate");
        				String scitizenshiptransfer = rs.getString("citizenshiptransfer");
        				if (scitizenshiptransfer==null){
        					scitizenshiptransfer="0";
        				}
        				String sbirthcertificate = rs.getString("birthcertificate");
        				String splayerup = rs.getString("isplayerup");
        				String sindefinite = rs.getString("indefinite");
        				String sparentname = rs.getString("PG First") + " " + rs.getString("PG Last");
        				String saddress = rs.getString("Address");
        				String scity = rs.getString("City");
        				String sstate = rs.getString("State");
        				String szip = rs.getString("zipcode");
        				String sphone = rs.getString("phone");
        				String semail = rs.getString("e-mail1");
        				String semail2 = rs.getString("e-mail2");
        				String snotes = rs.getString("notes");
        				String safesportindicator = rs.getString("safesportindicator");
        				
        				Player oplayer = new Player();
        				oplayer.setIdplayer(idplayer);
        				oplayer.setFirstname(sfirstname);
        				oplayer.setLastname(slastname);
        				oplayer.setCurrentteam(scurrentteam);
        				oplayer.setPreviousteam(slastyearteam);
        				oplayer.setDob(sdob);
        				oplayer.setCitizenship(scitizenship);
        				oplayer.setCitizenshiptransfer(scitizenshiptransfer);
        				oplayer.setCtverified(scitizenshiptransfer);
        				oplayer.setCTExpirationdate(scitizenshipexpiredate);
        				oplayer.setNotes(snotes);
        				oplayer.setSafesport(safesportindicator);
        				
        				if (scitizenship!=null){
	        				if (!scitizenship.equals("USA")){
		        				if (sindefinite!=null){
		        					if (sindefinite.equals("1")){
		            					oplayer.setCitizenshiplabel("Transfer does not expire");
		            				} else if (sindefinite.equals("1") && scitizenshiptransfer.equals("1")){
		            					oplayer.setCitizenshiplabel("Transfer expires " + scitizenshipexpiredate);
		            				} else if (sindefinite.equals("0") && scitizenshiptransfer.equals("1")){
		            					oplayer.setCitizenshiplabel("Transfer expires " + scitizenshipexpiredate);
		            				} else if (sindefinite.equals("0") && scitizenshiptransfer.equals("0")){
		            					oplayer.setCitizenshiplabel("Transfer is needed.");
		            				} else if (sindefinite.equals("1") && scitizenshiptransfer.equals("0")){
		            					oplayer.setCitizenshiplabel("Transfer is needed.");
		            				} else {
		            					oplayer.setCitizenshiplabel("Transfer is needed.");
		            				}
		        				} else {
		        					oplayer.setCitizenshiplabel("Transfer is needed.");
		        				}
	        				}
        				}
        				
        				oplayer.setBirthcertificate(sbirthcertificate);
        				oplayer.setBcverified(sbirthcertificate);
        				oplayer.setLoidate(sloidate);
        				oplayer.setUsamembership(susamember);
        				oplayer.setPlayerup(IsPlayerup(splayerup));
        				oplayer.setParentname(sparentname);
        				oplayer.setAddress(saddress);
        				oplayer.setCity(scity);
        				oplayer.setState(sstate);
        				oplayer.setZip(szip);
        				oplayer.setPhone(sphone);
        				oplayer.setEmail1(semail);
        				oplayer.setEmail2(semail2);
        				tempresult.add(oplayer);
    				}
    				
    				//LOGGER.info("We have results for lois for the team: " + selectedteam);
    				
    			}
    				
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR Lois for selected team:");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	//setResults(tempresult);
    	setPlayers(tempresult);
    	PlayerDataModel = new PlayerDataModel(players);
    }

    public PlayerDataModel getPlayerdatamodel(){
    	return PlayerDataModel;
    }
    
    public void setPlayerdatamodel(PlayerDataModel odatamodel){
    	PlayerDataModel = odatamodel;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    public String IsPlayerup(String sname){
    	if (sname.equals("1")){
    		return "Yes";
    	} else {
    		return "";
    	}
    	
    }
    
    public List<Team> getListofTeams(){
		List<Team> templist = new ArrayList<Team>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			//Vector<Integer> v = new Vector<Integer>();
    			//v.add(1);
    			//db.getData("CALL scaha.getTeamsByClub(?)", v);
    		    CallableStatement cs = db.prepareCall("CALL scaha.getTeamsbyClubId(?)");
    		    cs.setInt("pclubid", this.clubid);
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				//need to add to an array
    				//rs = db.getResultSet();
    				
    				while (rs.next()) {
    					String idteam = rs.getString("idteams");
        				String teamname = rs.getString("teamname");
        				
        				Team team = new Team(teamname,idteam);
        				team.setIdteam(idteam);
        				team.setTeamname(teamname);
        				
        				
        				templist.add(team);
    				}
    				//LOGGER.info("We have results for team list");
    			}
    			db.cleanup();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading teams");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setTeams(templist);
		
		return getTeams();
	}
    
    public List<Team> getTeams(){
		return teams;
	}
	
	public void setTeams(List<Team> list){
		teams = list;
	}
	
public void loadClubName(){
		
		//first lets get club id for the logged in profile
		String clubname = "";
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

    			
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.getProfid());
			db.getData("CALL scaha.getClubforPerson(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					this.clubid = rs.getInt("idclub");
					
					}
				//LOGGER.info("We have results for club for a profile");
			}
			
			db.cleanup();
    		
			//now lets retrieve club name
			v = new Vector<Integer>();
			v.add(clubid);
			db.getData("CALL scaha.getClubNamebyId(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					clubname = rs.getString("clubname");
				}
				//LOGGER.info("We have results for club name");
			}
			
			db.cleanup();
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading club by profile");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
		
	}

	public void viewLoi(Player selectedPlayer){
	
		String sidplayer = selectedPlayer.getIdplayer();
				
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("viewloi.xhtml?playerid=" + sidplayer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void CloseLoi(){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("registrarviewlois.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

