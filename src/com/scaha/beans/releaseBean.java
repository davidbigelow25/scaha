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
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Division;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Reason;
import com.scaha.objects.SkillLevel;

//import com.gbli.common.SendMailSSL;


public class releaseBean implements Serializable, MailableObject {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_permreg_body = Utils.getMailTemplateFromFile("/mail/permanentrelease.html");
	private static String mail_tempreg_body = Utils.getMailTemplateFromFile("/mail/temporaryrelease.html");
	private static String mail_eosreg_body = Utils.getMailTemplateFromFile("/mail/endofseasonrelease.html");
	transient private ResultSet rs = null;
	private String firstname = null;
	private String lastname = null;
	private String dob = null;
	private String usanumber = null;
	private String parentname = null;
	private String parentemail = null;
	private String selectedreason = null;
	private String selectedsuspension = null;
	private String beginningdate = null;
	private String endingdate = null;
	private String releasingclubdivision = null;
	private String selectedacceptingclub = null;
	private String selectedacceptingdivision = null;
	private String selectedacceptingskilllevel = null;
	private String selectedfinancial = null;
	private String comments = null;
	private Integer selectedplayer = null;  //this will now be the release id
	private Integer personid = null;
	private String subject = null;
	private String textbody = null;
	private String cc = null;
	private String to = null;
	private Integer profileid = null;
	private Integer clubid = null;
	private List<Club> clubs = null;
	private List<Reason> reasons = null;
	private List<Division> divisions = null;
	private List<SkillLevel> skilllevels = null;
	private String clubname = null;
	private Boolean displaypermanent = null;
	private Boolean displaytemporary = null;
	private Boolean displayfreeandclearonly = null;
	private String displayreason = null;
	private String displaysuspension = null;
	private String displayacceptingclub = null;
	private String displayacceptingdivision = null;
	private String displayacceptingskilllevel = null;
	private String displayfinancial = null;
	
	
	@PostConstruct
    public void init() {
		//hard code value until we load session variable
    	FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );

		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
    	this.setProfid(pb.getProfile().ID);
    	this.getClubName();
    	
		//will need to load player profile information for displaying loi
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("playerid") != null)
        {
    		selectedplayer = Integer.parseInt(hsr.getParameter("playerid").toString());
        }
    	
    	String releasetype = "";
    	if(hsr.getParameter("releasetype") != null)
        {
    		releasetype = hsr.getParameter("releasetype").toString();
        }
    	
    	setReleaseType(releasetype);
    	loadPlayerProfile(selectedplayer);

    	//doing anything else right here

    }
	
	public releaseBean() {  
        
    	
    }  
	
	public String getDisplayfinancial() {
		// TODO Auto-generated method stub
		return displayfinancial;
	}
    
    public void setDisplayfinancial(String snum){
    	displayfinancial = snum;
    }
	
	
	public String getDisplayacceptingskilllevel() {
		// TODO Auto-generated method stub
		return displayacceptingskilllevel;
	}
    
    public void setDisplayacceptingskilllevel(String snum){
    	displayacceptingskilllevel = snum;
    }
	
	
	public String getDisplayacceptingdivision() {
		// TODO Auto-generated method stub
		return displayacceptingdivision;
	}
    
    public void setDisplayacceptingdivision(String snum){
    	displayacceptingdivision = snum;
    }
	
	
	public String getDisplayacceptingclub() {
		// TODO Auto-generated method stub
		return displayacceptingclub;
	}
    
    public void setDisplayacceptingclub(String snum){
    	displayacceptingclub = snum;
    }
	
	
	public String getDisplaysuspension() {
		// TODO Auto-generated method stub
		return displaysuspension;
	}
    
    public void setDisplaysuspension(String snum){
    	displaysuspension = snum;
    }
	
	
	public String getDisplayreason() {
		// TODO Auto-generated method stub
		return displayreason;
	}
    
    public void setDisplayreason(String snum){
    	displayreason = snum;
    }
	
	
	public Boolean getDisplaypermanent() {
		// TODO Auto-generated method stub
		return displaypermanent;
	}
    
    public void setDisplaypermanent(Boolean snum){
    	displaypermanent = snum;
    }
	
    public Boolean getDisplaytemporary() {
		// TODO Auto-generated method stub
		return displaytemporary;
	}
    
    public void setDisplaytemporary(Boolean snum){
    	displaytemporary = snum;
    }
	
    public Boolean getDisplayfreeandclearonly() {
		// TODO Auto-generated method stub
		return displayfreeandclearonly;
	}
    
    public void setDisplayfreeandclearonly(Boolean snum){
    	displayfreeandclearonly = snum;
    }
    
	public String getClubname() {
		// TODO Auto-generated method stub
		return clubname;
	}

	public void setPersonid(Integer number){personid=number;}
	public Integer getPersonid(){return personid;}

    public void setClubname(String snum){
    	clubname = snum;
    }
	
	public String getComments() {
		// TODO Auto-generated method stub
		return comments;
	}
    
    public void setComments(String snum){
    	comments = snum;
    }
    
    public String getSelectedfinancial() {
		// TODO Auto-generated method stub
		return selectedfinancial;
	}
    
    public void setSelectedfinancial(String snum){
    	selectedfinancial = snum;
    }
    
    public String getSelectedacceptingskilllevel() {
		// TODO Auto-generated method stub
		return selectedacceptingskilllevel;
	}
    
    public void setSelectedacceptingskilllevel(String snum){
    	selectedacceptingskilllevel = snum;
    }
    
	public String getSelectedacceptingdivision() {
		// TODO Auto-generated method stub
		return selectedacceptingdivision;
	}
    
    public void setSelectedacceptingdivision(String snum){
    	selectedacceptingdivision = snum;
    }
    
	
	public String getSelectedacceptingclub() {
		// TODO Auto-generated method stub
		return selectedacceptingclub;
	}
    
    public void setSelectedacceptingclub(String snum){
    	selectedacceptingclub = snum;
    }
    
	
	public String getReleasingclubdivision() {
		// TODO Auto-generated method stub
		return releasingclubdivision;
	}
    
    public void setReleasingclubdivision(String snum){
    	releasingclubdivision = snum;
    }
    
	
	public String getEndingdate() {
		// TODO Auto-generated method stub
		return endingdate;
	}
    
    public void setEndingdate(String snum){
    	endingdate = snum;
    }
    
	
	public String getBeginningdate() {
		// TODO Auto-generated method stub
		return beginningdate;
	}
    
    public void setBeginningdate(String snum){
    	beginningdate = snum;
    }
    
	
	public String getSelectedsuspension() {
		// TODO Auto-generated method stub
		return selectedsuspension;
	}
    
    public void setSelectedsuspension(String snum){
    	selectedsuspension = snum;
    }
    
	public String getSelectedreason() {
		// TODO Auto-generated method stub
		return selectedreason;
	}
    
    public void setSelectedreason(String snum){
    	selectedreason = snum;
    }
    
	public String getParentemail() {
		// TODO Auto-generated method stub
		return parentemail;
	}
    
    public void setParentemail(String snum){
    	parentemail = snum;
    }
    
	public String getParentname() {
		// TODO Auto-generated method stub
		return parentname;
	}
    
    public void setParentname(String snum){
    	parentname = snum;
    }
    
	
	public String getUsanumber() {
		// TODO Auto-generated method stub
		return usanumber;
	}
    
    public void setUsanumber(String snum){
    	usanumber = snum;
    }
    
	
	public String getSubject() {
		// TODO Auto-generated method stub
		return subject;
	}
    
    public void setSubject(String ssubject){
    	subject = ssubject;
    }
    
	public String getTextBody() {
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("RELEASEDATE:" + " ");
		myTokens.add("FIRSTNAME:" + this.firstname);
		myTokens.add("LASTNAME:" + this.lastname);
		myTokens.add("CLUBNAME:" + this.clubname);
		myTokens.add("DOB:" + this.dob);
		myTokens.add("USANUMBER:" + this.usanumber);
		myTokens.add("PARENTNAME:" + this.parentname);
		myTokens.add("PARENTEMAIL:" + this.parentemail);
		myTokens.add("REASON: " + this.displayreason);
		myTokens.add("RELEASINGCLUB:" + this.releasingclubdivision);
		if (this.displayfreeandclearonly){
			/*myTokens.add("SUSPENSION:" + this.displaysuspension);
			if (this.displaypermanent){
				myTokens.add("FINANCIAL:" + this.displayfinancial);
			}*/
			if (this.displaytemporary){
				if (this.beginningdate==null || this.beginningdate.equals("")){
					myTokens.add("BEGINNINGDATE: " + this.beginningdate);
				} else {
					myTokens.add("BEGINNINGDATE:" + this.beginningdate);
				}
				if (this.endingdate==null || this.endingdate.equals("")){
					myTokens.add("ENDINGDATE: " + this.endingdate);
				} else {
					myTokens.add("ENDINGDATE:" + this.endingdate);
				}
			}
			
			if (!this.displayfreeandclearonly){
				if (this.displayacceptingclub==""){
					myTokens.add("ACCEPTINGCLUB: ");
				}else {
					myTokens.add("ACCEPTINGCLUB:" + this.displayacceptingclub);
				}
			}else {
				myTokens.add("ACCEPTINGCLUB:" + this.displayacceptingclub);
			}
			if (!this.displayfreeandclearonly){
				if (this.displayacceptingdivision==""){
					myTokens.add("DIVISION:  ");
				} else {
					myTokens.add("DIVISION:" + this.displayacceptingdivision);
				}
			} else {
				myTokens.add("DIVISION:" + this.displayacceptingdivision);
			}
			if (!this.displayfreeandclearonly){
				if (this.displayacceptingskilllevel==""){
					myTokens.add("SKILLLEVEL:  ");
				}else {
					myTokens.add("SKILLLEVEL:" + this.displayacceptingskilllevel);
				}
			}else {
				myTokens.add("SKILLLEVEL:" + this.displayacceptingskilllevel);
			}
			if (this.comments==null || this.comments.equals("")){
				myTokens.add("COMMENTS: " + this.comments);
			} else {
				myTokens.add("COMMENTS:" + this.comments);
			}
		}
		String result = null;
		if (!this.displayfreeandclearonly){
			result = Utils.mergeTokens(releaseBean.mail_eosreg_body,myTokens);
		}
		if (this.displaypermanent){
			result = Utils.mergeTokens(releaseBean.mail_permreg_body,myTokens);
		}
		if (this.displaytemporary){
			if (!this.displayfreeandclearonly){
				result = Utils.mergeTokens(releaseBean.mail_eosreg_body,myTokens);
			}else{
				result = Utils.mergeTokens(releaseBean.mail_tempreg_body,myTokens);
			}
		}
		
		
		return result;
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
    
    
    
    public Integer getClubid(){
    	return clubid;
    }
    
    public void setClubid(Integer sclub){
    	clubid = sclub;
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
	
	
	public List<Club> getListofClubs(){
		List<Club> templist = new ArrayList<Club>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			CallableStatement cs = db.prepareCall("CALL scaha.getClubs()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					String idclub = rs.getString("idclubs");
        				String clubname = rs.getString("clubname");
        				
        				Club club = new Club();
        				club.setClubid(idclub);
        				club.setClubname(clubname);
        				
        				templist.add(club);
    				}
    				//LOGGER.info("We have results for club list");
    			}
    			rs.close();
    			db.cleanup();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading clubs");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	//need to add a non-scaha option for accepting club
    	Club club = new Club();
		club.setClubid("999");
		club.setClubname("Non-SCAHA");
		templist.add(club);
    	
    	setClubs(templist);
		
		return getClubs();
	}
	
	public List<Club> getClubs(){
		return clubs;
	}
	
	public void setClubs(List<Club> list){
		clubs = list;
	}
	
	public List<Reason> getListofReasons(){
		List<Reason> templist = new ArrayList<Reason>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			if (this.displaypermanent){
    				CallableStatement cs = db.prepareCall("CALL scaha.getPermanentReleaseReasons()");	
	    		    rs = cs.executeQuery();
    			}else {
	    			CallableStatement cs = db.prepareCall("CALL scaha.getReleaseReasons()");	
	    		    rs = cs.executeQuery();
    			}
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					String idreason = rs.getString("idreleasereasons");
        				String releaselabel = rs.getString("reasonlabel");
        				
        				Reason reason = new Reason();
        				reason.setReasonid(idreason);
        				reason.setReasonname(releaselabel);
        				
        				//need to check if end of season free and clear release, then only display this option
        				if (!this.displayfreeandclearonly){
        					if (idreason.equals("3")){
        						templist.add(reason);
        					}
        				}else{
        					templist.add(reason);
        				}
        				
    				}
    				//LOGGER.info("We have results for club list");
    			}
    			rs.close();
    			db.cleanup();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading clubs");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setReasons(templist);
		
		return getReasons();
	}
	
	public List<Reason> getReasons(){
		return reasons;
	}
	
	public void setReasons(List<Reason> list){
		reasons = list;
	}
	
	public List<Division> getListofDivisions(){
		List<Division> templist = new ArrayList<Division>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			//Vector<Integer> v = new Vector<Integer>();
    			//v.add(1);
    			//db.getData("CALL scaha.getTeamsByClub(?)", v);
    		    CallableStatement cs = db.prepareCall("CALL scaha.getDivisions()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					Integer iddivision = rs.getInt("iddivisions");
        				String divisionname = rs.getString("division_name");
        				
        				Division division = new Division();
        				division.setIddivision(iddivision);
        				division.setDivisionname(divisionname);
        				templist.add(division);
    				}
    				//LOGGER.info("We have results for club list");
    			}
    			rs.close();
    			db.cleanup();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading clubs");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setDivisions(templist);
		
		return getDivisions();
	}
	
	public List<Division> getDivisions(){
		return divisions;
	}
	
	public void setDivisions(List<Division> list){
		divisions = list;
	}
	
	public List<SkillLevel> getListofSkillLevel(){
		List<SkillLevel> templist = new ArrayList<SkillLevel>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			CallableStatement cs = db.prepareCall("CALL scaha.getSkillLevels()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					Integer idskill = rs.getInt("idskilllevels");
        				String skilllevelname = rs.getString("levelsname");
        				
        				SkillLevel skill = new SkillLevel();
        				skill.setIdskilllevel(idskill);
        				skill.setSkilllevelname(skilllevelname);
        				templist.add(skill);
    				}
    				//LOGGER.info("We have results for club list");
    			}
    			rs.close();
    			db.cleanup();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading clubs");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setSkilllevels(templist);
		
		return getSkilllevels();
	}
	
	public List<SkillLevel> getSkilllevels(){
		return skilllevels;
	}
	
	public void setSkilllevels(List<SkillLevel> list){
		skilllevels = list;
	}
	
	//used to populate form with player information
	public void loadPlayerProfile(Integer selectedplayer){
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (!selectedplayer.equals("")) {
    		
    			Vector<Integer> v = new Vector<Integer>();
    			v.add(selectedplayer);
    			db.getData("CALL scaha.getPlayerReleaseInfoByRosterId(?)", v);
    		    
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				
    				while (rs.next()) {
    					firstname = rs.getString("fname");
    					lastname = rs.getString("lname");
        				dob = rs.getString("dob");
        				usanumber = rs.getString("usahockeynumber");
        				parentname = rs.getString("parentname");
        				parentemail = rs.getString("parentemail");
        				releasingclubdivision = rs.getString("releasingclubdivision");
        				personid = rs.getInt("idperson");
        			}
    				rs.close();
    				//LOGGER.info("We have results for player details by player id");
    			}
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
	
		
	public void getClubName(){
		
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
				rs.close();
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
					this.clubname = rs.getString("clubname");
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
		
	}
	
	public void CloseLoi(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{playerreleaseBean}", Object.class );

		playerreleaseBean dpb = (playerreleaseBean) expression.getValue( context.getELContext());
    	dpb.setSearchcriteria("");
		dpb.playerSearch();

		
		try{
			context.getExternalContext().redirect("startplayerrelease.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public void sendRelease(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			CallableStatement cs = db.prepareCall("CALL scaha.AddReleaseWRosterId(?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
    			cs.setInt("personid", this.personid);
    			cs.setInt("reason", Integer.parseInt(this.selectedreason));
    			if (!this.displayfreeandclearonly){
    				cs.setInt("suspension", 0);
    			}else{
    				//cs.setInt("suspension", Integer.parseInt(this.selectedsuspension));
    				//commented out since we removed the suspension field.
    				cs.setInt("suspension", 0);
    			}
    			
    			cs.setString("beginningdate", this.beginningdate);
    			cs.setString("endingdate", this.endingdate);
    			if (!this.displayfreeandclearonly){
    				cs.setInt("acceptingclub", 0);
        			cs.setInt("acceptingdivision", 0);
        			cs.setInt("acceptingskilllevel", 0);
        		}else{
    				cs.setInt("acceptingclub", Integer.parseInt(this.selectedacceptingclub));
        			cs.setInt("acceptingdivision", Integer.parseInt(this.selectedacceptingdivision));
        			cs.setInt("acceptingskilllevel", Integer.parseInt(this.selectedacceptingskilllevel));
        		}
    			cs.setString("comments", this.comments);
    			cs.setInt("clubid", this.clubid);
    			
    			//this says if release has been approved or not, 2=approved
    			if (displaypermanent){
    				cs.setInt("status", 1);
    			}else{
    				cs.setInt("status", 2);
    			}
    			
    			
    			//may need to check if this will always go to into null option because field will not be there
    			if (this.selectedfinancial==null){
    				if (displaypermanent){
    					cs.setString("releasetype", "P");
    					cs.setInt("financial", 0);
    				} else {
    					cs.setString("releasetype", "T");
        				cs.setInt("financial", 0);
    				}
    				
    			} else {
    				//commented out since we removed the suspension field.
    				//cs.setInt("financial", Integer.parseInt(this.selectedfinancial));
    				cs.setString("releasetype", "P");
    				cs.setInt("financial", 0);
    			}
				cs.setInt("rosterid", this.selectedplayer);
    		    cs.executeQuery();
    			
    		    //need to get display values for all lookup fields for the email sent.
    		    //LOGGER.info("Sending email to club registrar, family, and scaha registrar");
    			cs = db.prepareCall("CALL scaha.getReleaseLookups(?,?,?,?,?,?)");
    		    cs.setInt("reason", Integer.parseInt(this.selectedreason));
    		    if (!this.displayfreeandclearonly){
    		    	cs.setInt("suspension", 0);
        			cs.setInt("acceptingclub", 0);
        			cs.setInt("acceptingdivision", 0);
        			cs.setInt("acceptingskilllevel", 0);
        		}else{
    		    	
        			//commented out as this field has been removed
        			//cs.setInt("suspension", Integer.parseInt(this.selectedsuspension));
        			cs.setInt("suspension", 0);
        			cs.setInt("acceptingclub", Integer.parseInt(this.selectedacceptingclub));
        			cs.setInt("acceptingdivision", Integer.parseInt(this.selectedacceptingdivision));
        			cs.setInt("acceptingskilllevel", Integer.parseInt(this.selectedacceptingskilllevel));
        		}
    		    if (this.selectedfinancial==null){
    				cs.setInt("financial", 0);
    			} else {
    				cs.setInt("financial", Integer.parseInt(this.selectedfinancial));
    			}
    			
    			rs = cs.executeQuery();
    			if (rs != null){
    				
    				while (rs.next()) {
    					this.displayreason = rs.getString("reasonname");
    					this.displaysuspension = rs.getString("suspension");
    					if (!this.displayfreeandclearonly){
    						this.displayacceptingclub = "";
    					}else{
    						if (this.selectedacceptingclub.equals("999")){
        						this.displayacceptingclub = "Non-SCAHA";
        					}else{
        						this.displayacceptingclub = rs.getString("acceptingclubname");
        					}
    					}
    					
    					
    					this.displayacceptingdivision = rs.getString("division_name");
    					this.displayacceptingskilllevel = rs.getString("levelsname");
    					this.displayfinancial = rs.getString("financial");
    				}
    				//LOGGER.info("We have results for release info");
    			}
    			rs.close();
    			db.cleanup();
    			//need to send email to club registrars, family, and scaha registrar
    			//there is some special logic here
    			//Season Free and Clear goes to releasing registrar, scaha member services, and parents
    			//Permanent email only goes to releasing club, and scaha member services
    			//Temporary goes to both registrars, scaha member services, and parents
    			
    			//first releasing club
    			
    			
    			to = "";
    			//LOGGER.info("Sending email to club registrar, family, and scaha registrar");
    			cs = db.prepareCall("CALL scaha.getClubRegistrarEmail(?)");
    		    cs.setInt("iclubid", this.clubid);
    		    rs = cs.executeQuery();
    		    if (rs != null){
    				while (rs.next()) {
    					if (to.equals("")){
    						to = rs.getString("usercode");
    					}else{
    						to = to + ',' + rs.getString("usercode");
    					}
    				}
    			}
    		    rs.close();
    		    
    		    if (this.displayfreeandclearonly){
				
	    		    //next receiving club
	    			//LOGGER.info("Sending email to club registrar, family, and scaha registrar");
	    			cs = db.prepareCall("CALL scaha.getClubRegistrarEmail(?)");
	    		    cs.setInt("iclubid", Integer.parseInt(this.getSelectedacceptingclub()));
	    		    rs = cs.executeQuery();
	    		    if (rs != null){
	    				while (rs.next()) {
	    					if (to.equals("")){
	    						to = rs.getString("usercode");
	    					}else{
	    						to = to + ',' + rs.getString("usercode");
	    					}
	    					
	    				}
	    			}
	    		    rs.close();
    		    }
    		    //next scaha registrar
    		    cs = db.prepareCall("CALL scaha.getSCAHARegistrarEmail()");
    		    rs = cs.executeQuery();
    		    if (rs != null){
    				while (rs.next()) {
    					to = to + ',' + rs.getString("usercode");
    				}
    			}
    		    rs.close();
    		    db.commit();
    			db.cleanup();
    			
    			if (!this.displayfreeandclearonly || this.displaytemporary){
    				to = to + ',' + this.getParentemail();
    			}
    		    
    		    
    		    //use this will testing the site.  send emails to rob's personal account
    		    
    		    //to = "lahockeyfan2@yahoo.com";
    		    this.setToMailAddress(to);
    		    this.cc="";
    		    this.setSubject(this.firstname + " " + this.lastname + " Released from " + this.clubname);
    		    
				SendMailSSL mail = new SendMailSSL(this);
				//LOGGER.info("Finished creating mail object for Release");
				mail.sendMail();
				
    		    
    		    FacesContext context = FacesContext.getCurrentInstance();
    		    try{
					context.getExternalContext().redirect("startplayerrelease.xhtml");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		    
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading clubs");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
	
    	
    	
    }
	
	public void setReleaseType(String releasetype){
		
		
		if (releasetype.equals("p")){
			this.displaypermanent = true;
			this.displaytemporary = false;
			this.displayfreeandclearonly = true;
		} else if (releasetype.equals("f")){
			this.displaytemporary = false;
			this.displaypermanent = false;
			this.displayfreeandclearonly = false;
			//need to set value of the reason drop down
			this.selectedreason="3";
		} else {
			this.displaypermanent = false;
			this.displaytemporary = true;
			this.displayfreeandclearonly = true;
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
}

