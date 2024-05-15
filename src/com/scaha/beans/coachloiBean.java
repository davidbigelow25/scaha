package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.scaha.objects.MailableObject;
import com.scaha.objects.Team;
import com.scaha.objects.TeamDataModel;


public class coachloiBean implements Serializable, MailableObject {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/coachloireceipt.html");
	private static String mail_reg_manager_body = Utils.getMailTemplateFromFile("/mail/managerloireceipt.html");
	private static String sendingnote_reg_body = Utils.getMailTemplateFromFile("/mail/sendingcoachnote.html");
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	transient private ResultSet rs = null;
	
	private List<String> selectedteams = null;
	private List<String> selectedgirlsteams = null;
	private String cepmodulesselected = null;
	private Integer selectedcoach = 0;
	private List<Team> teams = null;
	private String firstname = null;
	private String lastname = null;
	private String address = null;
	private String state = null;
	private String city = null;
	private String zip = null;
	private String homenumber = null;
	private String email = null;
	private String loicode = null;
	private Integer clubid = null;
	private String displayselectedteam = null;
	private String displayselectedgirlsteam = null;
	private String currentdate = null;
	private Integer profileid = 0;
	private String screeningexpires = null;
	private String cepnumber = null;
	private Integer ceplevel = 0;
	private String cepleveldisplay = null;
	private String cepexpires = null;
	private List<String> boysteams = null;
	private List<String> girlsteams = null;
	private String cepmoduledisplaystring = null;
	private String u8 = null;
	private String u10 = null;
	private String u12 = null;
	private String u14 = null;
	private String u18 = null;
	private String girls = null;
	private String to = null;
	private String subject = null;
	private String cc = null;
	private String textbody = null;
	private String clubname = "";
	private String listofboysteams;
	private String listofgirlsteams;
	private Integer safesport = null;
	private String displaysafesport = null;
	private String sportexpires = null;
	private String coachrole = null;
	private Boolean displaycoachcredentials = null;
	private String notes = null;
	private Boolean sendingnote = null;
	private Boolean updatingnoteonly = null;
	private String origin = null;
	private String currentyear = null;
	private Team selectedteam = null;
	private String page = null;
	private String searchcriteria = "";
	private Integer boysteamcount = 0;
	private Integer girlsteamcount = 0;
	private String suspended = "";
	private String aadisplay = "";
	private String abdisplay = "";
	private String u8display = "";
	private String aaadisplay = "";
	private String errormessage = "";

	//these are used for creating the team select role tables.
	private TeamDataModel boysteamdatamodel = null;
	private TeamDataModel girlsteamdatamodel = null;
	
	@PostConstruct
    public void init() {
		//hard code value until we load session variable
		//hard code value until we load session variable
		this.sendingnote=false;
    	clubid = 1;
    	this.displaycoachcredentials=true;
 		this.setErrormessage(" ");

    	//load profile id from which we can get club id later
    	FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );

		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
    	this.setProfid(pb.getProfile().ID);
    	this.setClubname(this.getClubName());
    	
		//will need to load player profile information for displaying loi
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("coachid") != null)
        {
    		selectedcoach = Integer.parseInt(hsr.getParameter("coachid").toString());
        }
    	if(hsr.getParameter("page") != null)
        {
    		page = hsr.getParameter("page").toString();
        }else{
        	page = "";
        }
    	
    	if(hsr.getParameter("search") != null)
        {
    		searchcriteria = hsr.getParameter("search").toString();
        }else{
        	searchcriteria = "";
        }
    	loadCoachProfile(selectedcoach);

    	//need to add scaha session object
		ValueExpression scahaexpression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{scahaBean}", Object.class );

		scaha = (ScahaBean) scahaexpression.getValue( context.getELContext() );
		
		//need to set current year and prior year
		String cyear = scaha.getScahaSeasonList().getCurrentSeason().getFromDate().substring(0,4);
		this.setCurrentyear(cyear);
		
		//lets load the list of boys and girls teams from whic hto select from
		this.getListofTeams("M");
		boysteamdatamodel = new TeamDataModel(getTeams());
		
		this.getListofTeams("F");
		girlsteamdatamodel = new TeamDataModel(getTeams());
		
		
    }
    
    public coachloiBean() {  
        
    	
    	//doing anything else right here
    }

	public String getErrormessage() {
		return errormessage;
	}

	public void setErrormessage(String errormessage) {
		this.errormessage = errormessage;
	}

	public Integer getBoysteamcount(){
		return boysteamcount;
	}
	
	public void setBoysteamcount(Integer cyear){
		boysteamcount=cyear;
	}
    
	public Integer getGirlsteamcount(){
		return girlsteamcount;
	}
	
	public void setGirlsteamcount(Integer cyear){
		girlsteamcount=cyear;
	}

	public String getSuspended(){
		return suspended;
	}

	public void setSuspended(String cyear){
		suspended=cyear;
	}

	public String getAaadisplay(){
		return aaadisplay;
	}

	public void setAaadisplay(String cyear){
		aaadisplay=cyear;
	}

	public String getAadisplay(){
		return aadisplay;
	}

	public void setAadisplay(String cyear){
		aadisplay=cyear;
	}

	public String getAbdisplay(){
		return abdisplay;
	}

	public void setAbdisplay(String cyear){
		abdisplay=cyear;
	}

	public String getU8display(){
		return u8display;
	}

	public void setU8display(String cyear){
		u8display=cyear;
	}


	public String getSportexpires(){
		return sportexpires;
	}
	
	public void setSportexpires(String cyear){
		sportexpires=cyear;
	}
    
    
    public String getSearchcriteria(){
		return searchcriteria;
	}
	
	public void setSearchcriteria(String cyear){
		searchcriteria=cyear;
	}
    
	public String getPage(){
		return page;
	}
	
	public void setPage(String cyear){
		page=cyear;
	}
    
    public TeamDataModel getBoysteamdatamodel(){
    	return boysteamdatamodel;
    }
    
    public void setBoysteamdatamodel(TeamDataModel odatamodel){
    	boysteamdatamodel = odatamodel;
    }
    
    public TeamDataModel getGirlsteamdatamodel(){
    	return girlsteamdatamodel;
    }
    
    public void setGirlsteamdatamodel(TeamDataModel odatamodel){
    	girlsteamdatamodel = odatamodel;
    }
    
    
    
    public Boolean getUpdatingnoteonly(){
    	return updatingnoteonly;
    }
    
    public void setUpdatingnoteonly(Boolean value){
    	updatingnoteonly=value;
    }
    
    public Boolean getSendingnote(){
    	return sendingnote;
    }
    
    public void setSendingnote(Boolean value){
    	sendingnote=value;
    }
    
	
	public String getNotes(){
    	return notes;
    }
    
    public void setNotes(String value){
    	notes=value;
    }
    
    public String getCurrentyear(){
		return currentyear;
}

	public void setCurrentyear(String cyear){
		currentyear=cyear;
	}
    
	public Team getSelectedteam(){
    	return selectedteam;
    }
    
    public void setSelectedteam(Team name){
    	selectedteam=name;
    }
	
    public Boolean getDisplaycoachcredentials() {
		// TODO Auto-generated method stub
		return displaycoachcredentials;
	}
    
    public void setDisplaycoachcredentials(Boolean ssubject){
    	displaycoachcredentials = ssubject;
    }
    
    public String getCoachrole() {
		// TODO Auto-generated method stub
		return coachrole;
	}
    
    public void setCoachrole(String ssubject){
    	coachrole = ssubject;
    }
    
    public String getDisplaysafesport() {
		// TODO Auto-generated method stub
		return displaysafesport;
	}
    
    public void setDisplaysafesport(String ssubject){
    	if (ssubject.equals("0")){
    		displaysafesport = "No";
    	} else {
    		displaysafesport = "Yes";
    	}
    }
    
    
    public Integer getSafesport() {
		// TODO Auto-generated method stub
		return safesport;
	}
    
    public void setSafesport(Integer ssubject){
    	safesport = ssubject;
    }
    
    public String getListofgirlsteams() {
		// TODO Auto-generated method stub
		return listofgirlsteams;
	}
    
    public void setListofgirlsteams(String ssubject){
    	listofgirlsteams = ssubject;
    }
    
    
    public String getListofboysteams() {
		// TODO Auto-generated method stub
		return listofboysteams;
	}
    
    public void setListofboysteams(String ssubject){
    	listofboysteams = ssubject;
    }
    
    public String getClubname() {
		// TODO Auto-generated method stub
		return clubname;
	}
    
    public void setClubname(String ssubject){
    	clubname = ssubject;
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
		myTokens.add("CURRENTYEAR:" + this.getCurrentyear());
		myTokens.add("LOIDATE: " + this.currentdate);
		myTokens.add("FIRSTNAME: " + this.firstname);
		myTokens.add("LASTNAME: " + this.lastname);
		myTokens.add("CLUBNAME: " + this.getClubName());
		
		if (this.sendingnote){
			myTokens.add("BOYSTEAMS:" + this.displayselectedteam + " ");
			myTokens.add("GIRLSTEAMS:" + this.displayselectedgirlsteam + " ");
		} else {
			if (this.listofboysteams==null){
				myTokens.add("BOYSTEAMS:  ");
			} else {
				myTokens.add("BOYSTEAMS: " + this.listofboysteams + " ");
			}
			if (this.listofgirlsteams==null){
				myTokens.add("GIRLSTEAMS:  ");
			}else {
				myTokens.add("GIRLSTEAMS: " + this.listofgirlsteams + " ");
			}
			
		}
		myTokens.add("ADDRESS: " + this.address);
		myTokens.add("CITY: " + this.city);
		myTokens.add("STATE: " + this.state);
		myTokens.add("ZIP: " + this.zip);
		myTokens.add("SCREENINGEXPIRES: " + this.screeningexpires);
		myTokens.add("CEPNUM: " + this.cepnumber);
		myTokens.add("CEPLEVEL: " + this.cepleveldisplay);
		myTokens.add("CEPEXPIRES: " + this.cepexpires);
		myTokens.add("CEPMODULE: " + this.cepmoduledisplaystring);
		myTokens.add("SAFESPORT: " + this.displaysafesport);
		myTokens.add("HOMENUMBER: " + this.homenumber);
		myTokens.add("EMAIL: " + this.email);
		/*myTokens.add("COACHROLE: " + this.coachrole);*/
		myTokens.add("NOTES: " + this.notes);
		//need to add logic for displaying the correct link in the email for manager instructions.
		myTokens.add("AAADisplay: " + this.aaadisplay);
		myTokens.add("AADisplay: " + this.aadisplay);
		myTokens.add("ABDisplay: " + this.abdisplay);
		myTokens.add("8UDisplay: " + this.u8display);

		if (this.sendingnote){
			return Utils.mergeTokens(coachloiBean.sendingnote_reg_body, myTokens);
		} else {
			if (this.coachrole.equals("Manager")){
				return Utils.mergeTokens(coachloiBean.mail_reg_manager_body,myTokens);
			} else {
				return Utils.mergeTokens(coachloiBean.mail_reg_body,myTokens);
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
	
	public void setCepmoduledisplaystring(String snumber){
    	cepmoduledisplaystring = snumber;
    }
    
    public String getCepmoduledisplaystring(){
    	return cepmoduledisplaystring;
    }
    
    public void setGirls(String snumber){
    	if (snumber.equals("0")){
    		girls = "No";
    	} else {
    		girls = "Yes";
    	}
		
    }
    
	public String getGirls(){
    	return girls;
    }
	
	public void setU18(String snumber){
    	if (snumber.equals("0")){
    		u18 = "No";
    	} else {
    		u18 = "Yes";
    	}
		
    }
    
	public String getU18(){
    	return u18;
    }
	
	public void setU14(String snumber){
    	if (snumber.equals("0")){
    		u14 = "No";
    	} else {
    		u14 = "Yes";
    	}
		
    }
    
	public String getU14(){
    	return u14;
    }
	
	public void setU12(String snumber){
    	if (snumber.equals("0")){
    		u12 = "No";
    	} else {
    		u12 = "Yes";
    	}
		
    }
    
	public String getU12(){
    	return u12;
    }
	
	public void setU10(String snumber){
    	if (snumber.equals("0")){
    		u10 = "No";
    	} else {
    		u10 = "Yes";
    	}
		
    }
    
	public String getU10(){
    	return u10;
    }
	public void setU8(String snumber){
    	if (snumber.equals("0")){
    		u8 = "No";
    	} else {
    		u8 = "Yes";
    	}
		
    }
    
	public String getU8(){
    	return u8;
    }
    
    public void setGirlsteams(List<String> snumber){
    	girlsteams = snumber;
    }
    
    public List<String> getGirlsteams(){
    	return girlsteams;
    }
    
    public void setBoysteams(List<String> snumber){
    	boysteams = snumber;
    }
    
    public List<String> getBoysteams(){
    	return boysteams;
    }
    
    public void setCepexpires(String snumber){
    	cepexpires = snumber;
    }
    
    public String getCepexpires(){
    	return cepexpires;
    }
    
    public void setCeplevel(Integer snumber){
    	ceplevel = snumber;
    }
    
    public Integer getCeplevel(){
    	return ceplevel;
    }
    
    public void setCepleveldisplay(String snumber){
    	cepleveldisplay = snumber;
    }
    
    public String getCepleveldisplay(){
    	return cepleveldisplay;
    }
    
    public void setCepnumber(String snumber){
    	cepnumber = snumber;
    }
    
    public String getCepnumber(){
    	return cepnumber;
    }
    
    public void setScreeningexpires(String snumber){
    	screeningexpires = snumber;
    }
    
    public String getScreeningexpires(){
    	return screeningexpires;
    }
    
    public void setHomenumber(String snumber){
    	homenumber = snumber;
    }
    
    public String getHomenumber(){
    	return homenumber;
    }
    
    
    public void setEmail(String snumber){
    	email = snumber;
    }
    
    public String getEmail(){
    	return email;
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
    
    public String getLoicode(){
    	return loicode;
    }
    
    public Integer getClubid(){
    	return clubid;
    }
    
    public void setClubid(Integer sclub){
    	clubid = sclub;
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
    public Integer getSelectedcoach(){
		return selectedcoach;
	}
	
	public void setSelectedcoach(Integer selectedCoach){
		selectedcoach = selectedCoach;
	}
	
	public List<String> getSelectedteams(){
		return selectedteams;
	}
	
	public void setSelectedteams(List<String> selectedTeams){
		selectedteams = selectedTeams;
	}
	
	public String getCepmodulesselected(){
		return cepmodulesselected;
	}
	
	public void setCepmodulesselected(String selectedTeams){
		cepmodulesselected = selectedTeams;
	}
	
	public List<String> getSelectedgirlsteams(){
		return selectedgirlsteams;
	}
	
	public void setSelectedgirlsteams(List<String> selectedTeams){
		selectedgirlsteams = selectedTeams;
	}
	
	public void getListofTeams(String gender){
		List<Team> templist = new ArrayList<Team>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			//Vector<Integer> v = new Vector<Integer>();
    			//v.add(1);
    			//db.getData("CALL scaha.getTeamsByClub(?)", v);
    		    CallableStatement cs = db.prepareCall("CALL scaha.getTeamsByClub(?,?)");
    		    cs.setInt("pclubid", clubid);
    		    cs.setString("gender", gender);
    			rs = cs.executeQuery();
    			
    			Integer count=0;
    			
    			if (rs != null){
    				//need to add to an array
    				//rs = db.getResultSet();
    				
    				while (rs.next()) {
    					String idteam = rs.getString("idteams");
        				String teamname = rs.getString("teamname");
        				
        				Team team = new Team(teamname,idteam);
        				team.setCoachrole("No Role");
        				templist.add(team);
        				team = null;
        				count++;
    				}
    				LOGGER.info("We have results for team list by club");
    			}
    			
    			if (gender.equals("M")){
    				this.boysteamcount=count;
    			}else {
    				this.girlsteamcount=count;
    			}
    			rs.close();
    			cs.close();
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
		templist=null;
		//return templist;
	}
	
	public List<Team> getTeams(){
		return teams;
	}
	
	public void setTeams(List<Team> list){
		teams = list;
	}
	
	//used to populate loi form with player information
	public void loadCoachProfile(Integer selectedcoach){
		
		//first get player detail information then get family members
		Integer personID = 0;
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = new Date();
		currentdate = dateFormat.format(date);
		
		try{

    		if (!selectedcoach.equals("")) {
    		
    			Vector<Integer> v = new Vector<Integer>();
    			v.add(selectedcoach);
    			db.getData("CALL scaha.getCoachInfoByPersonId(?)", v);
    		    
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				this.cepmoduledisplaystring="";
    				while (rs.next()) {
    					firstname = rs.getString("fname");
    					lastname = rs.getString("lname");
        				address = rs.getString("address");
        				city = rs.getString("city");
        				state = rs.getString("state");
        				zip = rs.getString("zipcode");
        				personID = rs.getInt("idperson");
        				homenumber = rs.getString("phone");
        				screeningexpires = rs.getString("screeningexpires");
        				cepnumber = rs.getString("cepnumber");
        				ceplevel = rs.getInt("ceplevel");
        				email = rs.getString("email");
        				safesport = rs.getInt("safesport");
        				sportexpires = rs.getString("sportexpires");
        				notes = rs.getString("notes");
        				suspended = rs.getString("displayissuspended");
						if (suspended.equals("Yes")){
							suspended="1";
						}else{
							suspended="0";
						}
						this.setDisplaysafesport(safesport.toString());
        				
        				if (ceplevel.equals(1)){
        					cepleveldisplay = "Level 1";
        				}
        				if (ceplevel.equals(2)){
        					cepleveldisplay = "Level 2";
        				}
        				if (ceplevel.equals(3)){
        					cepleveldisplay = "Level 3";
        				}
        				if (ceplevel.equals(4)){
        					cepleveldisplay = "Level 4";
        				}
        				if (ceplevel.equals(5)){
        					cepleveldisplay = "Level 5";
        				}
        				if (ceplevel.equals(0)){
        					cepleveldisplay = "";
        				}
        				cepexpires = rs.getString("cepexpires");
        				u8 = rs.getString("eightu");
        				if (u8.equals("1")){
        					this.cepmoduledisplaystring="8U";
        				}
        				u10 = rs.getString("tenu");
        				if (u10.equals("1")){
        					if (!this.cepmoduledisplaystring.equals("")){
        						this.cepmoduledisplaystring=this.cepmoduledisplaystring+", ";
        					}
        					this.cepmoduledisplaystring=this.cepmoduledisplaystring + "10U";
        				}
        				u12 = rs.getString("twelveu");
        				if (u12.equals("1")){
        					if (!this.cepmoduledisplaystring.equals("")){
        						this.cepmoduledisplaystring=this.cepmoduledisplaystring+", ";
        					}
        					this.cepmoduledisplaystring=this.cepmoduledisplaystring + "12U";
        				}
        				u14 = rs.getString("fourteenu");
        				if (u14.equals("1")){
        					if (!this.cepmoduledisplaystring.equals("")){
        						this.cepmoduledisplaystring=this.cepmoduledisplaystring+", ";
        					}
        					this.cepmoduledisplaystring=this.cepmoduledisplaystring + "14U";
        				}
        				u18 = rs.getString("eighteenu");
        				if (u18.equals("1")){
        					if (!this.cepmoduledisplaystring.equals("")){
        						this.cepmoduledisplaystring=this.cepmoduledisplaystring+", ";
        					}
        					this.cepmoduledisplaystring=this.cepmoduledisplaystring + "18U";
        				}
        				girls = rs.getString("girls");
        				if (girls.equals("1")){
        					if (!this.cepmoduledisplaystring.equals("")){
        						this.cepmoduledisplaystring=this.cepmoduledisplaystring+", ";
        					}
        					this.cepmoduledisplaystring=this.cepmoduledisplaystring + "Girls";
        				}
        				
        				setCepmodulesselected(this.cepmoduledisplaystring);
        			}
    				LOGGER.info("We have results for player details by player id");
    			}
    			rs.close();
    			db.cleanup();
    			
    			//need to get list of boys teams signed for
    			v = new Vector<Integer>();
    			v.add(selectedcoach);
    			db.getData("CALL scaha.getCoachTeams(?)", v);
    		    List<String> tempteams = new ArrayList<String>();
    			String teamname = null;
    			
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				
    				while (rs.next()) {
    					String newcoachrole = rs.getString("rostertype");
    					teamname = rs.getString("teamname");
    					if (displayselectedteam==null){
    						displayselectedteam = teamname + " - " + newcoachrole;
    					} else {
    						if (displayselectedteam.equals("")){
	    						displayselectedteam = teamname + " - " + newcoachrole;
	    					} else {
	    						displayselectedteam = displayselectedteam + "," + teamname + " - " + newcoachrole;
	    					}
    					}
    					tempteams.add(teamname + ", " + newcoachrole);
    				}
    				LOGGER.info("We have results for teams for the coach");
    			}
    			this.setBoysteams(tempteams);
    			tempteams=null;
    			teamname = "";
    			rs.close();
    			db.cleanup();
    			
    			if (this.coachrole!=null){
	    			if (this.coachrole.equals("Manager")){
	    				this.displaycoachcredentials=false;
	    			}
    			}
    			//need to get list of girls teams signed for
    			v = new Vector<Integer>();
    			v.add(selectedcoach);
    			db.getData("CALL scaha.getCoachGirlsTeams(?)", v);
    			List<String> tempgirlteams = new ArrayList<String>();
    			
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				
    				while (rs.next()) {
    					String newcoachrole = rs.getString("rostertype");
    					teamname = rs.getString("teamname");
    					if (displayselectedteam==null){
    						displayselectedteam = teamname + " - " + newcoachrole;
    					} else {
    						if (displayselectedteam.equals("")){
	    						displayselectedteam = teamname + " - " + newcoachrole;
	    					} else {
	    						displayselectedteam = displayselectedteam + "," + teamname + " - " + newcoachrole;
	    					}
    					}
    					tempgirlteams.add(teamname + ", " + newcoachrole);
    				}
    				LOGGER.info("We have results for teams for the coach");
    			}
    			this.setGirlsteams(tempgirlteams);
    			tempgirlteams=null;
    			teamname = "";
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
    	
    	
    	
		
		
   }
	
	public void completeLOI(){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to check loi code from family first
 				LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.validateMemberNumber(?,?)");
 				cs.setString("memnumber", this.loicode);
 				cs.setInt("personid", this.selectedcoach);
    		    
    		    rs = cs.executeQuery();
    			
    		    Integer resultcount = 0;
    		    if (rs != null){
    				
    				while (rs.next()) {
						resultcount = rs.getInt("idmember");
    				}
    				LOGGER.info("We have coach season pass code validation results for coach details by coach id");
    			}
    		    rs.close();
    		    db.cleanup();
 				
    		    
    			if (resultcount > 0){
    		    	
	    		    //if good save info to person table, then add record to roster then email
	 				LOGGER.info("updating person record");
	 				cs = db.prepareCall("CALL scaha.updatePersonInfoAddress(?,?,?,?,?)");
	    		    cs.setInt("ipersonid", this.selectedcoach);
	    		    cs.setString("iaddress", this.address);
	    		    cs.setString("icity", this.city);
	    		    cs.setString("istate", this.state);
	    		    cs.setString("izipcode", this.zip);
	    		    rs = cs.executeQuery();
	    			rs.close();
	    			
	    			//need to add a roster record for each team a role is assigned.  If No role is assigned then we ignore that team
	    			//if any of the team have a role of manager or assistant coach/manager call the update manager
	    			cs = db.prepareCall("CALL scaha.addCoachRoster(?,?,?,?)");
	    			
	    			//need to add to the coach roster table for each boys team
	    			for(Team team : this.boysteamdatamodel) {  
	    				if (!team.getCoachrole().equals("No Role")){
    						LOGGER.info("updating coach roster record for:" + team.getIdteam());
    						cs.setInt("ipersonid", this.selectedcoach);
    		    		    cs.setInt("iteamid", Integer.parseInt(team.getIdteam()));
    		    		    cs.setInt("setyear", 2014);
    		    		    cs.setString("inrostertype", team.getCoachrole());
    		    		    rs = cs.executeQuery();
    		    		    rs.close();
    		    		    
    		    		    if (this.listofboysteams==null){
	    						this.listofboysteams = team.getTeamname() + "-" + team.getCoachrole();
	    					} else {
	    						this.listofboysteams = this.listofboysteams + "<br>" + team.getTeamname() + "-" + team.getCoachrole();
	    					}
    		    		    
    		    		    //not sure if we need to iterate through recordset anymore for retrieving team name since we have in the team object.
    		    		    /*if (rs != null){
    		    				while (rs.next()) {
    		    					if (this.listofboysteams==null){
    		    						this.listofboysteams = rs.getString("teamname");
    		    					} else {
    		    						this.listofboysteams = this.listofboysteams + "<br>" + rs.getString("teamname");
    		    					}
    		    				}
    		    				LOGGER.info("We have player up code validation results for player details by player id");
    		    			}*/
	    		    		
    		    		    
    		    		    //now lets set the coach/manager flag.  If they are a manager we will only add the 
    		    		    //safesport and caha screening values, if not we add all.
    		    		    if (team.getCoachrole().equals("Manager")){
    		    		    	this.coachrole="Manager";
    		    		    } else {
    		    		    	this.coachrole=team.getCoachrole().toString();
    		    		    }
	    				}
	    			} 
	    			
	    			//need to add to the coach roster table for each boys team
	    			for(Team team : this.girlsteamdatamodel) {  
	    				if (!team.getCoachrole().equals("No Role")){
	    					LOGGER.info("updating coach roster record for:" + team.getIdteam());
    						cs.setInt("ipersonid", this.selectedcoach);
    		    		    cs.setInt("iteamid", Integer.parseInt(team.getIdteam()));
    		    		    cs.setInt("setyear", 2014);
    		    		    cs.setString("inrostertype", team.getCoachrole());
    		    		    rs = cs.executeQuery();
    		    		    rs.close();
    		    		    
    		    		    if (this.listofgirlsteams==null){
	    						this.listofgirlsteams = team.getTeamname();
	    					} else {
	    						this.listofgirlsteams = this.listofgirlsteams + "<br>" + team.getTeamname();
	    					}
    		    		    
    		    		    //not sure if we need to iterate through recordset anymore for retrieving team name since we have in the team object.
    		    		    /*if (rs != null){
    		    				while (rs.next()) {
    		    					if (this.listofboysteams==null){
    		    						this.listofboysteams = rs.getString("teamname");
    		    					} else {
    		    						this.listofboysteams = this.listofboysteams + "<br>" + rs.getString("teamname");
    		    					}
    		    				}
    		    				LOGGER.info("We have player up code validation results for player details by player id");
    		    			}*/

							if (team.getCoachrole().equals("Manager")){
								this.coachrole="Manager";
							} else {
								this.coachrole=team.getCoachrole().toString();
							}
    		    		    
	    				}
	    			}
	    			
	    			//need to determine if it's a manager which set of website instructions need to be included.
	    			if (this.coachrole.equals("Manager")) {
						this.u8display="none";
						this.aadisplay="none";
						this.abdisplay="none";
						this.aaadisplay="none";

						cs = db.prepareCall("CALL scaha.whatlevelisteam(?)");

						//need to add to the coach roster table for each boys team
						for(Team team : this.boysteamdatamodel) {
							if (team.getCoachrole().equals("Manager")){
								cs.setInt("iteamid", Integer.parseInt(team.getIdteam()));
								rs = cs.executeQuery();

								if (rs != null){

									while (rs.next()) {
										String tempaadisplay = rs.getString("aadisplay");
										String tempabdisplay = rs.getString("abdisplay");
										String tempu8display = rs.getString("u8display");

										if (tempaadisplay.equals("Block")){
											this.aadisplay = "Block";
											this.aaadisplay = "Block";
										}
										if (tempabdisplay.equals("Block")){
											this.abdisplay = "Block";
											this.aaadisplay = "Block";
										}
										if (tempu8display.equals("Block")){
											this.u8display = "Block";
											this.aaadisplay = "Block";
										}
									}
									LOGGER.info("We have coach season pass code validation results for coach details by coach id");
								}

								rs.close();
							}
						}
					}

	    			to = "";
	    			LOGGER.info("Sending email to club registrar, family, and scaha registrar");
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
		    		    rs = cs.executeQuery();
		    		    db.commit();
		    		    rs.close();
		    		}
	    		    
					
	    			//need to add the coaches email to the to string
	    			to = to + "," + this.email;
	    			
	    			//to = "lahockeyfan2@yahoo.com";
	    		    this.setToMailAddress(to);
	    		    this.setPreApprovedCC("");
	    		    this.setSubject(this.firstname + " " + this.lastname + " LOI with " + this.getClubName());
	    		    
					SendMailSSL mail = new SendMailSSL(this);
					LOGGER.info("Finished creating mail object for ");
					mail.sendMail();
					db.commit();
					db.cleanup();
					
					
					FacesContext context = FacesContext.getCurrentInstance();
		    		try{
						context.getExternalContext().redirect("printablecoachloi.xhtml?coachid=" + selectedcoach);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
    		    } else {
    		    	FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "The provided LOI signature code is invalid."));
    		    }
    		   
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedcoach);
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
		Integer clubid = 0;
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
				LOGGER.info("We have results for club for a profile");
			}
			rs.close();
			db.cleanup();
    		
			//now lets retrieve club name
			v = new Vector<Integer>();
			v.add(this.clubid);
			db.getData("CALL scaha.getClubNamebyId(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					clubname = rs.getString("clubname");
				}
				LOGGER.info("We have results for club name");
			}
			rs.close();
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
		try{
			context.getExternalContext().redirect("addcoachestoteam.xhtml");
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
			v.add(selectedcoach);
			db.getData("CALL scaha.getBoyTeamForPlayer(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					teamname = rs.getString("teamname");
				}
				LOGGER.info("We have results for Team name for a person");
			}
			rs.close();
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
			v.add(selectedcoach);
			db.getData("CALL scaha.getGirlsTeamForPlayer(?)", v);
		    
			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();
				
				while (rs.next()) {
					teamname = rs.getString("teamname");
				}
				LOGGER.info("We have results for Team name for a person");
			}
			rs.close();
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
	
	
	public void checkRole(){
		//need to set coach credential fields to hide when manager is selected.  For all others display.
		this.displaycoachcredentials=false;
		
		/*if (this.coachrole.equals("Manager")){
			this.displaycoachcredentials=false;
		}else {
			this.displaycoachcredentials=true;
		}*/
	}
	
	public void SendNote(){
		this.setSendingnote(true);
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to store note first
 				LOGGER.info("storing note for :" + this.selectedcoach);
 				CallableStatement cs = db.prepareCall("CALL scaha.saveNote(?,?,?)");
 				cs.setString("innote", this.notes);
 				cs.setInt("personid", this.selectedcoach);
				 cs.setInt("in_suspend", Integer.parseInt(this.suspended));
    		    
    		    cs.executeQuery();
    			
    		    
    		  //need to get list of girls teams signed for
    		    Vector<Integer> v = new Vector<Integer>();
    			v.add(this.selectedcoach);
    			db.getData("CALL scaha.getCoachGirlsTeams(?)", v);
    			List<String> tempgirlteams = new ArrayList<String>();
    			String teamname = "";
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				
    				while (rs.next()) {
    					teamname = rs.getString("teamname");
    					if (displayselectedgirlsteam==null){
    						displayselectedgirlsteam = teamname;
    					} else {
    						if (displayselectedgirlsteam.equals("")){
    							displayselectedgirlsteam = teamname;
	    					} else {
	    						displayselectedgirlsteam = displayselectedgirlsteam + "," + teamname;
	    					}
    					}
    					
    					
    					
    					tempgirlteams.add(teamname);
    				}
    				LOGGER.info("We have results for teams for the coach");
    			}
    			this.setGirlsteams(tempgirlteams);
    			tempgirlteams=null;
    			teamname = "";
    			rs.close();
    		
    			//need to get list of boys teams signed for
    			v = new Vector<Integer>();
    			v.add(selectedcoach);
    			db.getData("CALL scaha.getCoachTeams(?)", v);
    		    List<String> tempteams = new ArrayList<String>();
    			
    			
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				
    				while (rs.next()) {
    					coachrole = rs.getString("rostertype");
    					teamname = rs.getString("teamname");
    					if (displayselectedteam==null){
    						displayselectedteam = teamname;
    					} else {
    						if (displayselectedteam.equals("")){
	    						displayselectedteam = teamname;
	    					} else {
	    						displayselectedteam = displayselectedteam + "," + teamname;
	    					}
    					}
    					tempteams.add(teamname);
    				}
    				LOGGER.info("We have results for teams for the coach");
    			}
    			this.setBoysteams(tempteams);
    			tempteams=null;
    			teamname = "";
    			rs.close();
    			
    			
    		        
    		    to = this.email;
    		    
    			LOGGER.info("Sending email to club registrar, and scaha registrar");
    			cs = db.prepareCall("CALL scaha.getClubRegistrarEmailByPersonID(?)");
    		    cs.setInt("personid", this.selectedcoach);
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
    		    this.setSubject("SCAHA LOI Review Note for: " + this.firstname + " " + this.lastname + " LOI");
    		    
				SendMailSSL mail = new SendMailSSL(this);
				LOGGER.info("Finished creating mail note object for " + this.firstname + " " + this.lastname + " LOI");
				mail.sendMail();
					
				db.commit();
				db.cleanup();
					
			} else {
				this.setSendingnote(false);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Sending Note " + this.selectedcoach);
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
			if (page.equals("quick")){
				context.getExternalContext().redirect("quickcoachloiconfirm.xhtml");
			}else if (page.equals("bcloi")){
				context.getExternalContext().redirect("workwithcoaches.xhtml?search=" + this.searchcriteria);
			}else{
				context.getExternalContext().redirect("confirmcoachlois.xhtml");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void UpdateNoteOnly(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to store note first
 				LOGGER.info("storing note for :" + this.selectedcoach);
				CallableStatement cs = db.prepareCall("CALL scaha.saveNote(?,?,?)");
				cs.setString("innote", this.notes);
				cs.setInt("personid", this.selectedcoach);
				cs.setInt("in_suspend", Integer.parseInt(this.suspended));

				cs.executeQuery();
    			cs.close();
    		    
    		} else {
				//nothing to do here
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Updating Note " + this.selectedcoach);
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
			if (page.equals("quick")){
				context.getExternalContext().redirect("quickcoachloiconfirm.xhtml");
			}else if (page.equals("bcloi")){
				context.getExternalContext().redirect("workwithcoaches.xhtml?search=" + this.searchcriteria);
			}else{
				context.getExternalContext().redirect("confirmcoachlois.xhtml");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void Close(){
		FacesContext context = FacesContext.getCurrentInstance();
		origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
		try{
				context.getExternalContext().redirect("addcoachestoteam.xhtml");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void checkCoachFromDifferentClub(Team team){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		Integer currentteamid = Integer.parseInt(team.getIdteam());
		Boolean donotaddplayer = false;
		String ViolatedTeam = "";
		try{

			if (db.setAutoCommit(false)) {

				//Need to store note first
				LOGGER.info("checking if coach can be added to team :" + this.selectedcoach);
				CallableStatement cs = db.prepareCall("CALL scaha.checkNumberofPlayersFromCoachesPriorClub(?,?)");
				cs.setInt("incoachid", this.selectedcoach);
				cs.setInt("inteamid",currentteamid);
				rs = cs.executeQuery();
				if (rs != null){
					while (rs.next()) {
						Integer playercount = rs.getInt("playercount");
						Integer currentteam = rs.getInt("currentteamid");
						if (playercount >= 3 && currentteam.equals(currentteamid)){
							donotaddplayer = true;
							ViolatedTeam = rs.getString("lastyearteam");
							break;
						}
					}
				}
				cs.executeQuery();
				cs.close();

			} else {
				//nothing to do here
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN checking coach 3 player rule " + this.selectedcoach);
			e.printStackTrace();
			db.rollback();
			this.setSendingnote(false);
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		if (donotaddplayer){
			this.errormessage = "Unable to add coach, more than 3 players from " + ViolatedTeam + " have been added.";
			team.setCoachrole("No Role");
			//add the failed attempt to the db.
			try{

				if (db.setAutoCommit(false)) {

					//Need to store note first
					//LOGGER.info("checking if coach can be added to team :" + this.selectedteam);
					CallableStatement cs = db.prepareCall("CALL scaha.failedloiattempt(?,?,?,?)");
					cs.setInt("inidperson", this.selectedcoach);
					cs.setString("inloitype","Coach");
					cs.setInt("inattemptingclubteamid",currentteamid);
					cs.setString("infailedreason","CoachSwitchClub3Players");
					cs.executeQuery();
					cs.close();

				} else {
					//nothing to do here
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.info("ERROR IN insterting into failed coach table checking coach 3 player rule " + this.selectedcoach);
				e.printStackTrace();
				db.rollback();
				this.setSendingnote(false);
				db.free();
			} finally {
				//
				// always clean up after yourself..
				//
				db.free();
			}
		} else {
			this.errormessage = " ";
		}
	}
}

