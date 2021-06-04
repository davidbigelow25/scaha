package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.*;
import com.sun.media.jfxmedia.events.PlayerStateEvent;
import com.sun.org.apache.xpath.internal.operations.Bool;

import static org.apache.commons.lang3.BooleanUtils.and;

//import com.gbli.common.SendMailSSL;


public class loiBean implements Serializable, MailableObject {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/loireceipt.html");
	private static String mailboys18_reg_body = Utils.getMailTemplateFromFile("/mail/loi18receipt.html");
	private static String girlsmail_reg_body = Utils.getMailTemplateFromFile("/mail/girlsloireceipt.html");
	private static String girlsmail18_reg_body = Utils.getMailTemplateFromFile("/mail/girlsloireceipt.html");
	private static String playerupmail_reg_body = Utils.getMailTemplateFromFile("/mail/playeruploireceipt.html");
	private static String sendingnote_reg_body = Utils.getMailTemplateFromFile("/mail/sendingnote.html");
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	transient private ResultSet rs = null;
	
	private String selectedteam = null;
	private String selectedgirlsteam = null;
	private Integer selectedplayer = 0;
	private List<Team> teams = null;
	private List<FamilyRow> parents = null;
	private String firstname = null;
	private String lastname = null;
	private String dob = null;
	private String address = null;
	private String state = null;
	private String city = null;
	private String zip = null;
	private String citizenship = null;
	private String gender = null;
	private String lastyearteam = null;
	private String lastyearclub = null;
	private String loicode = null;
	private String playerupcode = null;
	private Integer clubid = null;
	private String origin = null;
	private Boolean displaygirlteam = null;
	private String displaygender = null;
	private String displayselectedteam = null;
	private String displayselectedgirlsteam = null;
	private String currentdate = null;
	private Integer profileid = 0;
	private String to = null;
	private String subject = null;
	private String cc = null;
	private Integer parentid = 0;
	private Boolean bplayerup = null;
	private Boolean displayplayerup = null;
	private Boolean ishighschool = null;
	private String notes = null;
	private Boolean sendingnote = null;
	private Integer rosteridforconfirm = null;
	private String currentyear = null;
	private String prioryear = null;
	private String page = null;
	private String search = null;
	private Integer suspendloi = null;
	private String safesportfor18 = "N"; //this one is for capturing whether or not has safesport
	private Boolean is18safesport = false; //this one is for displaying the is 18 checkbox
	private String safesportfor18display = null; //this one is for displaying in the email and printable loi
	private String displaysuspendloi = null;
	private List<Player> pdr = null;
	private List<Player> blockrecruitment = null;
	private String pdrcount = null;
	private String totalplayercount = null;
	private Integer currentpdrcount = 0;
	private Integer pdrcountwithplayer = 0;
	private Integer totalplayercountwithplayer = 0;
	private Boolean displaycounts = null;
	private Boolean pdrrequired = null;
	private String playerhistoryforemail = null;

	//added to support covid player history for aa/aaa teams
	private List<PlayerStat> playerhistory = null;

	@PostConstruct
    public void init() {
		this.setSendingnote(false);
		this.setDisplaycounts(false);
		this.setPdrrequired(false);
		//hard code value until we load session variable
    	FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );

		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
    	this.setProfid(pb.getProfile().ID);
    	getClubID();
    	isClubHighSchool();
    	
		//will need to load player profile information for displaying loi
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("playerid") != null)
        {
    		selectedplayer = Integer.parseInt(hsr.getParameter("playerid").toString());
        }
    	if(hsr.getParameter("page") != null)
        {
    		page = hsr.getParameter("page").toString();
        }else{
        	page = "";
        }
    	if(hsr.getParameter("search") != null)
        {
    		search = hsr.getParameter("search").toString();
        }else{
        	search = "";
        }
    	
    	
    	loadPlayerProfile(selectedplayer);
    	setDisplayplayerup(false);
    	
    	//need to get roster id if included in query string to support confirming loi from view loi page for scaha registrar
    	//will need to load player profile information for confirming loi
		if(hsr.getParameter("rid") != null)
	      {
	  		rosteridforconfirm = Integer.parseInt(hsr.getParameter("rid").toString());
	      }
		
		//need to add scaha session object
		ValueExpression scahaexpression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{scahaBean}", Object.class );

		scaha = (ScahaBean) scahaexpression.getValue( context.getELContext() );
		
		//need to set current year and prior year
		String cyear = scaha.getScahaSeasonList().getCurrentSeason().getFromDate().substring(0,4);
		this.setCurrentyear(cyear);
		
		Integer pyear = Integer.parseInt(cyear) - 1;
		this.setPrioryear(pyear.toString());

		//need to generate pdr and block recruitment display for confirm loi.
		if (this.rosteridforconfirm!=null) {
			generatePDRForLoiConfirm();
			generateBlockForLOIConfirm();
		}
    }
	
	
	public loiBean() {  
        
    }

	public List<PlayerStat> getPlayerhistory(){
		return playerhistory;
	}

	public void setPlayerhistory(List<PlayerStat> list){
		playerhistory = list;
	}

	public Boolean getPdrrequired(){
		return pdrrequired;
	}

	public void setPdrrequired(Boolean list){
		pdrrequired = list;
	}

	public Boolean getDisplaycounts(){
		return displaycounts;
	}

	public void setDisplaycounts(Boolean list){
		displaycounts = list;
	}

	public Integer getPdrcountwithplayer(){
		return pdrcountwithplayer;
	}

	public void setPdrcountwithplayer(Integer list){
		pdrcountwithplayer = list;
	}

	public Integer getTotalplayercountwithplayer(){
		return totalplayercountwithplayer;
	}

	public void setTotalplayercountwithplayer(Integer list){
		totalplayercountwithplayer = list;
	}


	public Integer getCurrentpdrcount(){
		return currentpdrcount;
	}

	public void setCurrentpdrcount(Integer list){
		currentpdrcount = list;
	}

    public List<Player> getPdr(){
		return pdr;
	}

	public void setPdr(List<Player> list){
		pdr = list;
	}

	public List<Player> getBlockrecruitment(){
		return blockrecruitment;
	}

	public void setBlockrecruitment(List<Player> list){
		blockrecruitment = list;
	}

    public String getPdrcount(){
		return pdrcount;
	}

	public void setPdrcount(String name){
		pdrcount=name;
	}

	public String getTotalplayercount(){
		return totalplayercount;
	}

	public void setTotalplayercount(String name){
		totalplayercount=name;
	}

	public String getDisplaysuspendloi(){
		return displaysuspendloi;
	}

	public void setDisplaysuspendloi(String cyear){
		displaysuspendloi=cyear;
	}


	public String getSafesportfor18(){
		return safesportfor18;
	}
	
	public void setSafesportfor18(String cyear){
		safesportfor18=cyear;
	}
    
	public String getSafesportfor18display(){
		return safesportfor18display;
	}
	
	public void setSafesportfor18display(String cyear){
		safesportfor18display=cyear;
	}
    
	
	public Boolean getIs18safesport(){
		return is18safesport;
	}
	
	public void setIs18safesport(Boolean cyear){
		is18safesport=cyear;
	}
    
	
	
	public Integer getSuspendloi(){
		return suspendloi;
	}
	
	public void setSuspendloi(Integer cyear){
		suspendloi=cyear;
	}
    
	
	public Integer getRosteridforconfirm(){
    	return rosteridforconfirm;
    }
    
    public void setRosteridforconfirm(Integer value){
    	rosteridforconfirm=value;
    }
    
	
	public Boolean getSendingnote(){
    	return sendingnote;
    }
    
    public void setSendingnote(Boolean value){
    	sendingnote=value;
    }
    
    public String getPage(){
		return page;
	}
	
	public void setPage(String cyear){
		page=cyear;
	}
    
	public String getSearch(){
		return search;
	}
	
	public void setSearch(String cyear){
		search=cyear;
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
    
    
    public String getNotes(){
    	return notes;
    }
    
    public void setNotes(String value){
    	notes=value;
    }
    
	public Boolean getIshighschool(){
    	return ishighschool;
    }
    
    public void setIshighschool(Boolean value){
    	ishighschool = value;
    }
    
	public Boolean getDisplayplayerup(){
		return displayplayerup;
	}
	
	public void setDisplayplayerup(Boolean bplay){
		displayplayerup=bplay;
	}

	
	public Boolean getBplayerup(){
		return bplayerup;
	}
	
	public void setBplayerup(Boolean bplay){
		bplayerup=bplay;
	}
	
	public Integer getParentid(){
		return parentid;
	}
	
	public void setParentid(Integer id){
		parentid = id;
	}
	
	public String getSubject() {
		// TODO Auto-generated method stub
		return subject;
	}
    
    public void setSubject(String ssubject){
    	subject = ssubject;
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
	
    public String getTextBody() {
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		myTokens.add(":CURRENTYEAR:" + this.scaha.getScahaSeasonList().getCurrentSeason().getFromDate().substring(0,4));
		myTokens.add("LOIDATE:" + this.currentdate);
		myTokens.add("FIRSTNAME:" + this.firstname);
		myTokens.add("LASTNAME:" + this.lastname);
		myTokens.add("CLUBNAME:" + this.getClubName());
		myTokens.add("CURRENTYTEAR:" + this.getCurrentyear());
		if (this.sendingnote){
			if (this.displaygirlteam){
				myTokens.add("SELECTEDBOYSTEAM:" + this.getThisYearGirlsTeam() + " ");
			}else {
				myTokens.add("SELECTEDBOYSTEAM:" + this.getThisYearBoysTeam() + " ");
			}
				
		}else {
			myTokens.add("SELECTEDBOYSTEAM:" + this.displayselectedteam + " ");
		}
		if (this.displayselectedgirlsteam==null){
			myTokens.add("SELECTEDGRLSTEAM: ");
		}else {
			myTokens.add("SELECTEDGRLSTEAM:" + this.displayselectedgirlsteam + " ");
		}
		myTokens.add("PLAYERHISTORY:" + this.playerhistoryforemail);
		myTokens.add("DOB:" + this.dob);
		myTokens.add("CITIZENSHIP:" + this.citizenship);
		myTokens.add("GENDER:" + this.displaygender);
		myTokens.add("ADDRESS:" + this.address);
		myTokens.add("CITY:" + this.city);
		myTokens.add("STATE:" + this.state);
		myTokens.add("ZIP:" + this.zip);
		if (this.lastyearclub==null){
			myTokens.add("LASTYEARCLUB:  ");
		}else {
			myTokens.add("LASTYEARCLUB:" + this.lastyearclub);
		}
		if (this.lastyearteam==null){
			myTokens.add("LASTYEARTEAM:  ");
		}else {
			myTokens.add("LASTYEARTEAM:" + this.lastyearteam);
		}
		myTokens.add("NOTES: " + this.notes);
		myTokens.add("SAFESPORTDISPLAY: " + this.safesportfor18display);
		if (this.sendingnote){
			return Utils.mergeTokens(loiBean.sendingnote_reg_body, myTokens);
		} else {
			if (this.displayselectedgirlsteam==null){
				if (bplayerup){
					return Utils.mergeTokens(loiBean.playerupmail_reg_body,myTokens);
				} else {
					if (this.is18safesport){
						return Utils.mergeTokens(loiBean.mailboys18_reg_body, myTokens);
					} else {
						return Utils.mergeTokens(loiBean.mail_reg_body,myTokens);
					}
				}
			} else {
				return Utils.mergeTokens(loiBean.girlsmail_reg_body,myTokens);
			}
		}
		
		
	}
	
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return cc;
	}
	
	public void setPreApprovedCC(String scc){
		cc = scc;
	}
	
	
	
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return to;
	}
    
    public void setToMailAddress(String sto){
    	to = sto;
    }
	
	public Integer getProfid(){
    	return profileid;
    }
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    public void setCurrentdate(String scode){
    	currentdate = scode;
    }
    
    public String getCurrentdate(){
    	return currentdate;
    }
    
    public void setDisplaygirlteam(Boolean scode){
    	displaygirlteam = scode;
    }
    
    public Boolean getDisplaygirlteam(){
    	return displaygirlteam;
    }
    
    public void setDisplayselectedgirlsteam(String scode){
    	displayselectedgirlsteam = scode;
    }
    
    public String getDisplayselectedgirlsteam(){
    	return displayselectedgirlsteam;
    }
    
    public void setDisplayselectedteam(String scode){
    	displayselectedteam = scode;
    }
    
    public String getDisplayselectedteam(){
    	return displayselectedteam;
    }
    
    
    public void setLoicode(String scode){
    	loicode = scode;
    }
    
    public Integer getClubid(){
    	return clubid;
    }
    
    public void setClubid(Integer sclub){
    	clubid = sclub;
    }
    
    
    public String getLoicode(){
    	return loicode;
    }
    
    public void setPlayerupcode(String scode){
    	playerupcode = scode;
    }
    
    public String getPlayerupcode(){
    	return playerupcode;
    }
    
    public void setLastyearclub(String sclub){
    	lastyearclub = sclub;
    }
    
    public String getLastyearclub(){
    	return lastyearclub;
    }
    
    public void setLastyearteam(String steam){
    	lastyearteam = steam;
    }
    
    public String getLastyearteam(){
    	return lastyearteam;
    }
    
    public void setDisplaygender(String sgender){
    	displaygender = sgender;
    }
    
    public String getDisplaygender(){
    	return displaygender;
    }
    
    
    public void setGender(String sgender){
    	gender = sgender;
    }
    
    public String getGender(){
    	return gender;
    }
    
    public void setCitizenship(String scitizenship){
    	citizenship = scitizenship;
    }
    
    public String getCitizenship(){
    	return citizenship;
    }
    
    public void setZip(String szip){
    	zip = szip;
    }
    
    public String getZip(){
    	return zip;
    }
    
    public void setState(String sstate){
    	state = sstate;
    }
    
    public String getState(){
    	return state;
    }
    
    public void setCity(String scity){
    	city = scity;
    }
    
    public String getCity(){
    	return city;
    }
    
    public void setAddress(String saddress){
    	address = saddress;
    }
    
    public String getAddress(){
    	return address;
    }
    
    public void setDob(String sdob){
    	dob = sdob;
    }
    
    public String getDob(){
    	return dob;
    }
    
    public void setLastname(String lname){
    	lastname = lname;
    }
    
    public String getLastname(){
    	return lastname;
    }
    public void setFirstname(String fname){
    	firstname = fname;
    }
    public String getFirstname(){
    	return firstname;
    }
    public Integer getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Integer selectedPlayer){
		selectedplayer = selectedPlayer;
	}
	
	public String getSelectedteam(){
		return selectedteam;
	}
	
	public void setSelectedteam(String selectedTeam){
		selectedteam = selectedTeam;
	}
	
	public String getSelectedgirlsteam(){
		return selectedgirlsteam;
	}
	
	public void setSelectedgirlsteam(String selectedTeam){
		selectedgirlsteam = selectedTeam;
	}
	
	public List<Team> getListofTeams(String gender){
		List<Team> templist = new ArrayList<Team>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			CallableStatement cs = db.prepareCall("CALL scaha.getTeamsByClub(?,?)");
    		    cs.setInt("pclubid", clubid);
    		    cs.setString("gender", gender);
    			rs = cs.executeQuery();
    			
    			if (rs != null){
    				//need to add to an array
    				//rs = db.getResultSet();
    				
    				while (rs.next()) {
    					String idteam = rs.getString("idteams");
        				String teamname = rs.getString("teamname");
        				
        				Team team = new Team(teamname,idteam);
        				templist.add(team);
    				}
    				//LOGGER.info("We have results for team list by club");
    			}
    			rs.close();
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
	
	public List<FamilyRow> getParents(){
		return parents;
	}
	
	public void setParents(List<FamilyRow> list){
		parents = list;
	}
	
	
	//used to populate loi form with player information
	public void loadPlayerProfile(Integer selectedplayer){
		//first get player detail information then get family members
		Integer personID = 0;
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (!selectedplayer.equals("")) {
    		
    			
    			
    			Vector<Integer> v = new Vector<Integer>();
    			v.add(selectedplayer);
    			db.getData("CALL scaha.getPlayerInfoByPersonId(?)", v);
    		    
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				
    				while (rs.next()) {
    					firstname = rs.getString("fname");
    					lastname = rs.getString("lname");
        				dob = rs.getString("dob");
        				address = rs.getString("address");
        				city = rs.getString("city");
        				state = rs.getString("state");
        				zip = rs.getString("zipcode");
        				gender = rs.getString("gender");
        				personID = rs.getInt("idperson");
        				lastyearteam = rs.getString("teamname");
        				lastyearclub = rs.getString("clubname");
        				parentid = rs.getInt("parentid");
        				citizenship = rs.getString("citizenship");
        				notes = rs.getString("notes");
        				displaysuspendloi = rs.getString("issuspended");
        				is18safesport = rs.getBoolean("is18display");
        				safesportfor18display = rs.getString("safesportfor18");
        				
        				if (citizenship.equals("CAN")){
        					citizenship="Canada";
        				} else if (citizenship.equals("OTH")){
        					citizenship="Other";
        				} 
        				
        				DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        				Date date = new Date();
        				currentdate = dateFormat.format(date);
        				
        				//need to set the boolean used to display the girl team option or not
        				if (gender.equals("F")){
        					if (this.ishighschool){
        						displaygirlteam = false;
        					} else {
        						displaygirlteam = true;
        					}
        				} else {
        					displaygirlteam = false;
        				}
        				
        				//need to set the display value for gender
        				if (gender.equals("F")){
        		    		displaygender = "Female";
        		    	} else {
        		    		displaygender = "Male";
        		    	}
        				
        			}
    				//LOGGER.info("We have results for player details by player id");
    			}
    			rs.close();

    			//need to load the 2020 and 2019 team history to support covid aa/aaa rules
				v = new Vector<Integer>();
				v.add(selectedplayer);
				db.getData("CALL scaha.getPlayerHistoryInfoByPersonId(?)", v);

				this.playerhistoryforemail = "<table cellpadding=\"5\" cellspacing=\"0\" width=\"90%\"><tr><th>Year</th><th>Club</th><th>Team</th></tr>";
				if (db.getResultSet() != null){
					//need to add to an array
					rs = db.getResultSet();

					List<PlayerStat> temphistory = new ArrayList<PlayerStat>();

					while (rs.next()) {
						PlayerStat tempps = new PlayerStat();
						tempps.setTeamname(rs.getString("teamname"));
						tempps.setGmcount(rs.getString("rosteryear"));
						tempps.setPenalties(rs.getString("clubname"));
						this.playerhistoryforemail = this.playerhistoryforemail + "<tr><td>" + tempps.getGmcount() +
								"</td><td>" + tempps.getPenalties() + "</td><td>" + tempps.getTeamname() + "</td></tr>";

						temphistory.add(tempps);
					}
					//LOGGER.info("We have results for player details by player id");

					this.setPlayerhistory(temphistory);
				}
				this.playerhistoryforemail = this.playerhistoryforemail + "</table>";
				rs.close();

				db.cleanup();


    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading player details");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	
    	
    	//ok now need to get family members that are parents
    	List<FamilyRow> tempparent = new ArrayList<FamilyRow>();
    	try{

    		if (personID>0) {
    			
    			Vector<Integer> v = new Vector<Integer>();
    			v.add(personID);
    			db.getData("CALL scaha.getParentsByPersonId(?)", v);
    		    
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				
    				while (rs.next()) {
    					String pfirstname = rs.getString("fname");
    					String plastname = rs.getString("lname");
    					String pemail = rs.getString("usercode");
    					String pphone = rs.getString("phone");
    					String prelation = rs.getString("reltype");
    					
    					FamilyRow row = new FamilyRow();
    					row.setFirstname(pfirstname);
    					row.setLastname(plastname);
    					row.setEmail(pemail);
    					row.setPhone(pphone);
    					row.setRelation(prelation);
    					
    					tempparent.add(row);
    					}
    				//LOGGER.info("We have results for parents list by person id");
    			}
    			rs.close();
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
		
    	setParents(tempparent);
   }
	
	public void completeLOI(){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to check loi code from family first
 				//LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.validateMemberNumber(?,?)");
 				cs.setString("memnumber", this.loicode);
 				cs.setInt("personid", this.selectedplayer);
    		    
    		    rs = cs.executeQuery();
    			
    		    Integer resultcount = 0;
    		    if (rs != null){
    				
    				while (rs.next()) {
    					resultcount = rs.getInt("idmember");
    				}
    				//LOGGER.info("We have code validation results for player details by person id");
    				if (resultcount.equals(0)){
    					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "The provided LOI signature code is invalid."));
    				}
    			}
    			rs.close();
    		    db.cleanup();
 				
    		    //lets make sure a team was selected
    			if (this.selectedteam==null && this.selectedgirlsteam==null ){
    				resultcount=0;
    				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "A team must be selected for the player"));
    			}else {
    				bplayerup = false;
	    			Integer plupresultcount = 0;
	    			
	    			//need to verify if player is playing up and if player up code is needed if not provided
	    			if (this.playerupcode==null || this.playerupcode==""){
						//Need to check player up code from family next
		 				//LOGGER.info("verify if user needs to enter player up code");
		 				cs = db.prepareCall("CALL scaha.IsPlayerUpNeeded(?,?)");
		 				String year = this.dob.substring(0,4);
		 				cs.setInt("birthyear",Integer.parseInt(year));
		 				if (this.selectedteam==null){
		 					cs.setInt("selectedteam", 0);
		 				}else {
		 					cs.setInt("selectedteam", Integer.parseInt(this.selectedteam));
		 				}
		    		    rs = cs.executeQuery();
		    		    
		    		    if (rs != null){
		    				
		    				while (rs.next()) {
		    					plupresultcount = rs.getInt("divisioncount");
		    				}
		    				//LOGGER.info("We have validation whether player needs player up code or not");
		    			}
		    			rs.close();
		    		    db.cleanup();
		    			
		    			if (plupresultcount.equals(0) && this.selectedgirlsteam==null){
		    				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "The Player Up Code is required for this player for the division selected."));
		    				bplayerup=true;
		    			} else {
		    				if (!(this.selectedgirlsteam==null)){
		    					//resultcount=1;
		    				}
		    			}
	    				
	    			}
    			
	    			//need to verify player up code if user provided it.
	    			if (plupresultcount.equals(0) && this.selectedgirlsteam==null){
	    				//Need to check player up code from family next
		 				//LOGGER.info("verify family code provided for player up");
		 				cs = db.prepareCall("CALL scaha.validateMemberNumber(?,?)");
		 				cs.setString("memnumber", this.playerupcode);
		 				cs.setInt("personid", this.selectedplayer);
		    		    rs = cs.executeQuery();
		    			resultcount = 0;
		    		    
		    		    if (rs != null){
		    				
		    				while (rs.next()) {
		    					resultcount = rs.getInt("idmember");
		    				}
		    				//LOGGER.info("We have code validation results for player details by player id");
		    			}
		    		    rs.close();
		    			db.cleanup();
		    			
		    			if (resultcount.equals(0) && this.selectedgirlsteam==null){
		    				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "The provided Player Up signature code is invalid."));
		    			} else {
		    				bplayerup=true;
		    				
		    			}
	    			}
    			}
    			
	    		
    			
    			
    			
    			//lets make sure a team was selected
    			/*if (this.selectedteam==null && this.selectedgirlsteam==null ){
    				resultcount=0;
    				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "A team must be selected for the player"));
    			}*/
    			
    			
    		    if (resultcount > 0){
    		    	
	    		    //if good save info to person table, then add record to roster then email
	 				//LOGGER.info("updating person record");
	 				cs = db.prepareCall("CALL scaha.updatePersonInfoAddress(?,?,?,?,?)");
	    		    cs.setInt("ipersonid", this.parentid);
	    		    cs.setString("iaddress", this.address);
	    		    cs.setString("icity", this.city);
	    		    cs.setString("istate", this.state);
	    		    cs.setString("izipcode", this.zip);
	    			cs.executeQuery();
	    			
					//LOGGER.info("updating roster record");
					cs = db.prepareCall("CALL scaha.addRosterwithsafesport(?,?,?,?)");
	    		    cs.setInt("ipersonid", this.selectedplayer);
	    		    
	    		    if (bplayerup){
	    		    	cs.setInt("playerup",1);
	    		    } else {
	    		    	cs.setInt("playerup",0);
	    		    }
	    		    
	    		    if (selectedgirlsteam!=null){
	    		    	if ((!selectedgirlsteam.equals("0")) && (!selectedgirlsteam.equals(""))){
		    		    	cs.setInt("iteamid", Integer.parseInt(this.selectedgirlsteam));
		    		    }else{
		    		    	cs.setInt("iteamid", Integer.parseInt(this.selectedteam));
		    		    }
		    		}else {
		    			cs.setInt("iteamid", Integer.parseInt(this.selectedteam));
		    		}
	    		    cs.setString("safesportindicator_in", this.safesportfor18);
	    		    
	    		    cs.executeQuery();
					
	    		    //need to get team name for the newly selected team
	    			if (this.selectedteam!=null && !this.selectedteam.equals("")){
		    			Vector<Integer> v = new Vector<Integer>();
		    			v.add(Integer.parseInt(this.selectedteam));
		    			db.getData("CALL scaha.getTeamByTeamId(?)", v);
		    			
		    			if (db.getResultSet() != null){
		    				//need to add to an array
		    				rs = db.getResultSet();
		    			
			    			if (rs != null){
			    				
			    				while (rs.next()) {
			    					displayselectedteam = rs.getString("teamname");
			    				}
			    				//LOGGER.info("We have loaded the team name for printable loi");
			    			}
			    			rs.close();
			    			db.cleanup();
		    			}
	    			}
	    			
	    			//need to get team name for the newly selected team
	    			if (this.selectedgirlsteam!=null && !this.selectedgirlsteam.equals("")){
		    			Vector<Integer> v = new Vector<Integer>();
		    			v.add(Integer.parseInt(this.selectedgirlsteam));
		    			db.getData("CALL scaha.getTeamByTeamId(?)", v);
		    			
		    			if (db.getResultSet() != null){
		    				//need to add to an array
		    				rs = db.getResultSet();
		    			
			    			if (rs != null){
			    				
			    				while (rs.next()) {
			    					displayselectedgirlsteam = rs.getString("teamname");
			    				}
			    				//LOGGER.info("We have loaded the girls team name for printable loi");
			    			}
			    			rs.close();
			    			db.cleanup();
		    			}
	    			}
	    		    
	    		    to = "";
	    			//LOGGER.info("Sending email to club registrar, family, and scaha registrar");
	    			cs = db.prepareCall("CALL scaha.getClubRegistrarEmail(?)");
	    		    cs.setInt("iclubid", this.clubid);
	    		    rs = cs.executeQuery();
	    		    if (rs != null){
	    				while (rs.next()) {
	    					if (!to.equals("")){
	    						to = to + "," + rs.getString("usercode");
	    					}else {
	    						to = rs.getString("usercode");
	    					}
	    				}
	    			}
					rs.close();
	    		    
	    			//scaha registrar only wants 1 a day for the entire a year
					//previously it was until 8/1 so this is a change and why you will see commented out code.
	    		    Integer emailsenttoday = 0;
	    		    /*Date curdate = new Date();
	    		    Calendar cal = Calendar.getInstance();
	    		    cal.set(2014, Calendar.AUGUST, 1); //Year, month and day of month
	    		    Date targetdate = cal.getTime();
	    		    if (curdate.compareTo(targetdate)<0){*/
    		        cs = db.prepareCall("CALL scaha.HasReceivedEmail()");
	    		    rs = cs.executeQuery();
	    		    if (rs != null){
	    				while (rs.next()) {
	    					emailsenttoday = rs.getInt("emailcount");
	    				}
	    			}
	    		    rs.close();
	    		    //}
	    		    
	    		    if (emailsenttoday.equals(0)){
		    		    cs = db.prepareCall("CALL scaha.getSCAHARegistrarEmail()");
		    		    rs = cs.executeQuery();
		    		    if (rs != null){
		    				while (rs.next()) {
		    					to = to + ',' + rs.getString("usercode");
		    				}
		    			}
		    		    rs.close();
		    		    
		    		    cs = db.prepareCall("CALL scaha.setSCAHARegistrarEmail()");
		    		    cs.executeQuery();
		    		    db.commit();
	    		    }
	    		    
	    		    cs = db.prepareCall("CALL scaha.getFamilyEmail(?)");
	    		    cs.setInt("iplayerid", this.selectedplayer);
	    		    rs = cs.executeQuery();
	    		    if (rs != null){
	    				while (rs.next()) {
	    					to = to + ',' + rs.getString("usercode");
	    				}
	    			}
	    		    rs.close();
					
	    		    if (this.safesportfor18.equals("Y")){
	    		    	this.safesportfor18display="Yes";
	    		    } else {
	    		    	this.safesportfor18display="No";
	    		    }
	    		    
	    		    //hard my email address for testing purposes
	    		    //to = "lahockeyfan2@yahoo.com";
	    		    this.setToMailAddress(to);
	    		    this.setPreApprovedCC("");
	    		    this.setSubject(this.firstname + " " + this.lastname + " LOI with " + this.getClubName());
	    		    
					SendMailSSL mail = new SendMailSSL(this);
					//LOGGER.info("Finished creating mail object for " + this.firstname + " " + this.lastname + " LOI with " + this.getClubName());
					mail.sendMail();
					
					db.commit();
					db.cleanup();
					//return "True";
					
					FacesContext context = FacesContext.getCurrentInstance();
		    		origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
					try{
						context.getExternalContext().redirect("printableloi.xhtml?playerid=" + selectedplayer);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
    		    } else {
    		    	
    		    }
    		   
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedplayer);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
    }
	
	public String getClubName(){
		
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
				rs.close();
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
				rs.close();
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
		
		return clubname;
	}
	
	public void CloseLoi(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{draftplayersBean}", Object.class );

		DraftPlayersBean dpb = (DraftPlayersBean) expression.getValue( context.getELContext());
    	dpb.setSearchcriteria("");
		dpb.playerSearch();

		
		origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
		try{
			context.getExternalContext().redirect("addplayerstoteam.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getThisYearBoysTeam(){
		//lets load the team name for the current year
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		String teamname = "";
		try{

    			
			Vector<Integer> v = new Vector<Integer>();
			v.add(selectedplayer);
			db.getData("CALL scaha.getBoyTeamForPlayer(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					teamname = rs.getString("teamname");
					Integer playerup = rs.getInt("isplayerup");
					if (playerup.equals(0)){
						this.setDisplayplayerup(false);
					}else {
						this.setDisplayplayerup(true);
					}
				}
				rs.close();
				//LOGGER.info("We have results for Team name for a person");
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
		
		return teamname;
		
	}
	
	public String getThisYearGirlsTeam(){
		//lets load the team name for the current year
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		String teamname = "";
		try{

    			
			Vector<Integer> v = new Vector<Integer>();
			v.add(selectedplayer);
			db.getData("CALL scaha.getGirlsTeamForPlayer(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					teamname = rs.getString("teamname");
				}
				rs.close();
				//LOGGER.info("We have results for Team name for a person");
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
		
		return teamname;
	}
	
	public void checkDOB(String sourceteam){

		//perform logic to check if team selected is for player up
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		Boolean is2yearplayerup = false;
		Boolean isbeforeaaa = false;
		try{
			
			//first lets check if the team selected is too young for the players dob
			Integer resultcount = 0;
			Integer ageoldercount = 0;
			Integer pwtobtmcount = 0;
			if (sourceteam.equals("M")){
				CallableStatement cs = db.prepareCall("CALL scaha.IsPlayerOlderThanLevel(?,?)");
				String year = this.dob.substring(0,4);
				cs.setInt("birthyear",Integer.parseInt(year));
				cs.setInt("selectedteam", Integer.parseInt(this.selectedteam));
				
			    rs = cs.executeQuery();
				if (rs != null){
					
					while (rs.next()) {
						ageoldercount = rs.getInt("isolder");
					}
					//LOGGER.info("We have validation whether player needs player up code or not");
				}
				rs.close();
				db.cleanup();
				
				if (ageoldercount.equals(1)){
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "The player is too old for the team selected"));
					this.selectedteam="0";
					this.selectedgirlsteam="0";
					this.setDisplaycounts(false);
				}
				
				
				//need to check if player up code is needed
				//if player is 2 year player up don't allow
				//LOGGER.info("verify if user needs to enter player up code");
				cs = db.prepareCall("CALL scaha.IsPlayerUpNeeded(?,?)");
				cs.setInt("birthyear",Integer.parseInt(year));
				if (sourceteam.equals("M")){
					cs.setInt("selectedteam", Integer.parseInt(this.selectedteam));
				}else {
					cs.setInt("selectedteam", Integer.parseInt(this.selectedgirlsteam));
				}
				
			    rs = cs.executeQuery();
				
			    
			    if (rs != null){
					
					while (rs.next()) {
						resultcount = rs.getInt("divisioncount");
						is2yearplayerup = rs.getBoolean("2yearplayerup");
						isbeforeaaa = rs.getBoolean("beforeaaa");
					}
					//LOGGER.info("We have validation whether player needs player up code or not");
				}
			    rs.close();
			    
			    //need to check if player is peewee trying to play up in bantam b
			    //LOGGER.info("verify if user is player up for pw to bantam");
				cs = db.prepareCall("CALL scaha.IsPlayerUPPeeweeToBantam(?,?)");
				cs.setInt("birthyear",Integer.parseInt(year));
				if (sourceteam.equals("M")){
					cs.setInt("selectedteam", Integer.parseInt(this.selectedteam));
				}else {
					cs.setInt("selectedteam", Integer.parseInt(this.selectedgirlsteam));
				}
				
			    rs = cs.executeQuery();
				
			    if (rs != null){
					
					while (rs.next()) {
						pwtobtmcount = rs.getInt("divisioncount");
					}
					//LOGGER.info("We have validation whether player needs player up code or not");
				}
			    rs.close();
				db.cleanup();
			    
			} else {
				resultcount = 1;
			}
			
			//need to check if a girl player is on boys team already or if on girls team already
			String isgirlsteam = getThisYearGirlsTeam();
			//String isgirlsteam = getThisYearGirlsTeam();
			String isboysteam = getThisYearBoysTeam();
			
			if (!isgirlsteam.equals("") && sourceteam.equals("F")){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "Player is already LOI'd to a girls team."));
				this.selectedteam=null;
				this.selectedgirlsteam=null;
			}
			
			if (!isboysteam.equals("") && sourceteam.equals("M")){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "Player is already LOI'd to a boys team."));
				this.selectedteam=null;
				this.selectedgirlsteam=null;
			}
			
			if (isbeforeaaa){
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "Not allowed to LOI for this division/skill level before tryout date."));
				this.selectedteam=null;
				this.selectedgirlsteam=null;
				this.setDisplaycounts(false);
			}
					
			if (resultcount.equals(0) && ageoldercount.equals(0) && pwtobtmcount.equals(0) && !is2yearplayerup){
				this.setDisplayplayerup(true);
			} else {
				if (is2yearplayerup){
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "2 or more years player up is not allowed."));
					this.selectedteam=null;
					this.selectedgirlsteam=null;
					this.setDisplaycounts(false);
				}else{
					this.setDisplayplayerup(false);
					if (pwtobtmcount.equals(1)){
						FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "Player up from peewee to bantams is not allowed."));
						this.selectedteam=null;
						this.setDisplaycounts(false);
					}
				}
			}

			if (ageoldercount.equals(0) && !isbeforeaaa && pwtobtmcount.equals(0) && !is2yearplayerup){
				if ((this.selectedteam!=null) || (this.selectedgirlsteam!=null)){
					this.generatePDR(sourceteam);
					this.generateBlock(sourceteam);
				}

			}

			if (sourceteam.equals("M")){
				this.selectedgirlsteam=null;
			} else {
				this.selectedteam=null;
			}




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
		
public void getClubID(){
		
		//first lets get club id for the logged in profile
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
				rs.close();
				//LOGGER.info("We have results for club for a profile");
			}
			db.cleanup();
    	} catch (SQLException e) {
    		// TODO nnfo("ERROR IN loading club by profile");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}

    }
	
	public void isClubHighSchool(){
		
		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		Integer isschool = 0;
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.clubid);
			db.getData("CALL scaha.IsClubHighSchool(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
						isschool = rs.getInt("result");
					}
				rs.close();
				//LOGGER.info("We have results for club is a high school");
			}
			db.cleanup();
			
			if (isschool.equals(0)){
				this.ishighschool=false;
			}else{
				this.ishighschool=true;
			}
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


	@Override
	public InternetAddress[] getToMailIAddress() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public InternetAddress[] getPreApprovedICC() {
		// TODO Auto-generated method stub
		return null;
	}

	public void SendNote(){
		this.setSendingnote(true);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to store note first
 				//LOGGER.info("storing note for :" + this.selectedplayer);
				CallableStatement cs = db.prepareCall("CALL scaha.saveNoteSuspend(?,?,?)");
 				cs.setString("innote", this.notes);
 				cs.setInt("personid", this.selectedplayer);
 				cs.setInt("suspendloi", this.suspendloi);
 				
    		    cs.executeQuery();
    			
    		        
    		    to = "";
    			LOGGER.info("Sending email to club registrar, and scaha registrar");
    			cs = db.prepareCall("CALL scaha.getClubRegistrarEmailByPersonID(?)");
    		    cs.setInt("personid", this.selectedplayer);
    		    rs = cs.executeQuery();
    		    if (rs != null){
    				while (rs.next()) {
    					if (!to.equals("")){
    						to = to + "," + rs.getString("usercode");
    					}else {
    						to = rs.getString("usercode");
    					}
    				}
    			}
				rs.close();
	    		    
	    			
		        cs = db.prepareCall("CALL scaha.getSCAHARegistrarEmail()");
    		    rs = cs.executeQuery();
    		    if (rs != null){
    				while (rs.next()) {
    					to = to + ',' + rs.getString("usercode");
    				}
    			}
    		    rs.close();
		    		    
		    	//hard my email address for testing purposes
    		    //to = "lahockeyfan2@yahoo.com";
    		    this.setToMailAddress(to);
    		    this.setPreApprovedCC("");
    		    if (!(this.displaygirlteam)){
    		    	this.setSubject("SCAHA LOI Review Note for: " + this.firstname + " " + this.lastname + " LOI with " + this.getThisYearBoysTeam());
    		    }else {
    		    	this.setSubject("SCAHA LOI Review Note for: " + this.firstname + " " + this.lastname + " LOI with " + this.getThisYearGirlsTeam());
    		    }
    		    
    		    
				SendMailSSL mail = new SendMailSSL(this);
				//LOGGER.info("Finished creating mail note object for " + this.firstname + " " + this.lastname + " LOI with " + this.displayselectedteam);
				mail.sendMail();
					
				db.commit();
				db.cleanup();
					
			} else {
				this.setSendingnote(false);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Sending Note " + this.selectedplayer);
			e.printStackTrace();
			db.rollback();
			this.setSendingnote(false);
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		this.setSendingnote(true);
		FacesContext context = FacesContext.getCurrentInstance();
		origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
		try{
			if (page.equals("bcloi")){
				context.getExternalContext().redirect("workwithbirthcertificate.xhtml?search=" + this.search);
			}
			else if (page.equals("quick")){
				context.getExternalContext().redirect("quickplayerloiconfirm.xhtml");
			}else{
				context.getExternalContext().redirect("confirmlois.xhtml");
			}
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void UpdateNote(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to store note first
 				LOGGER.info("storing note for :" + this.selectedplayer);
 				CallableStatement cs = db.prepareCall("CALL scaha.saveNoteSuspend(?,?,?)");
 				cs.setString("innote", this.notes);
 				cs.setInt("personid", this.selectedplayer);
 				cs.setInt("suspendloi", this.suspendloi);
 				
    		    cs.executeQuery();
    			db.commit();
				db.cleanup();
					
			} else {
				this.setSendingnote(false);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Updating Note " + this.selectedplayer);
			e.printStackTrace();
			db.rollback();
			this.setSendingnote(false);
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		FacesContext context = FacesContext.getCurrentInstance();
		origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
		try{
			if (page.equals("bcloi")){
				context.getExternalContext().redirect("workwithbirthcertificate.xhtml?search=" + this.search);
			}
			else if (page.equals("quick")){
				context.getExternalContext().redirect("quickplayerloiconfirm.xhtml");
			}else{
				context.getExternalContext().redirect("confirmlois.xhtml");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void generatePDR(String sourceteam){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.getTeamByTeamId(?)");
			if (sourceteam.equals("M")){
				cs.setInt("teamid", Integer.parseInt(this.selectedteam));
			}else {
				cs.setInt("teamid", Integer.parseInt(this.selectedgirlsteam));
			}
			rs = cs.executeQuery();

			if (rs != null) {
				while (rs.next()) {
					displayselectedteam = rs.getString("teamname");
				}
			}

			//next get pdr
//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getTeamPDRforLOI(?,?)");
			if (sourceteam.equals("M")){
				cs.setInt("teamid", Integer.parseInt(this.selectedteam));

			}else {
				cs.setInt("teamid", Integer.parseInt(this.selectedgirlsteam));
			}
			cs.setInt("personid", this.selectedplayer);
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					this.currentpdrcount= rs.getInt("playercount");
					this.pdrcountwithplayer=rs.getInt("pdrplayercount");
					this.totalplayercountwithplayer=rs.getInt("totalplayercountwithplayer");
					this.pdrrequired=rs.getBoolean("pdrapplies");

				}
				//LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading pdr");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		this.setDisplaycounts(true);
	}

	public void generateBlock(String sourceteam){
		List<Player> templist = new ArrayList<Player>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.getTeamBlockRecruitment(?)");

			//next get pdr
//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getTeamBlockRecruitmentforLOI(?,?)");
			if (sourceteam.equals("M")){
				cs.setInt("teamid", Integer.parseInt(this.selectedteam));
			}else {
				cs.setInt("teamid", Integer.parseInt(this.selectedgirlsteam));
			}
			cs.setInt("personid", this.selectedplayer);
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					String playerid = rs.getString("playercount");
					String fname = rs.getString("idteams");
					String lname = rs.getString("teamname");

					Player player = new Player();
					player.setDob(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);

					templist.add(player);
				}
				//LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading blockrecruitments");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		setBlockrecruitment(templist);

	}

	public void generatePDRForLoiConfirm(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			//next get pdr
//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			CallableStatement cs = db.prepareCall("CALL scaha.getTeamPDRforRosterID(?,?)");
			cs.setInt("rosterid", this.rosteridforconfirm);
			cs.setInt("personid", this.selectedplayer);
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					this.currentpdrcount= rs.getInt("playercount");
					this.pdrcountwithplayer=rs.getInt("pdrplayercount");
					this.totalplayercountwithplayer=rs.getInt("totalplayercountwithplayer");
					this.pdrrequired=rs.getBoolean("pdrapplies");

				}
				//LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading pdr");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		this.setDisplaycounts(true);
	}

	public void generateBlockForLOIConfirm(){
		List<Player> templist = new ArrayList<Player>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.getTeamBlockRecruitment(?)");

			//next get pdr
//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getTeamBlockRecruitmentByRosterID(?,?)");
			cs.setInt("rosterid", this.rosteridforconfirm);
			cs.setInt("personid", this.selectedplayer);
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					String playerid = rs.getString("playercount");
					String fname = rs.getString("idteams");
					String lname = rs.getString("teamname");

					Player player = new Player();
					player.setDob(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);

					templist.add(player);
				}
				//LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading blockrecruitments");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		setBlockrecruitment(templist);

	}
}

