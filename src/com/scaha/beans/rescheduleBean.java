package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.MailableObject;
@ManagedBean
@ViewScoped
public class rescheduleBean implements Serializable, MailableObject {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	transient private ResultSet rs = null;
	
	//properties for emailing to managers, scaha statistician
	private String to = null;
	private String subject = null;
	private String cc = null;
	private static String mail_gamechangerequest_body = Utils.getMailTemplateFromFile("/mail/reschedule.html");
	private String todaysdate = null;	
	
	//bean level properties used by multiple methods
	private String gamedetails = null;
	private String requestingteam = null;
	private String selectedreason = null;
	private String notes = null;
	private Integer gameid = 0;
	private Integer profileid = 0;
	private Integer requestingteamid = 0;
	@PostConstruct
    public void init() {
		setTodaysDate();
        
        FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );
		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
    	this.pb=pb;

    	this.setProfid(pb.getProfile().ID);
        
        //will need to load player profile information for displaying loi
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("id") != null)
        {
    		this.gameid = Integer.parseInt(hsr.getParameter("id").toString());
        }
        
    	loadGame();
        
        
	}
	
    public rescheduleBean() {  
        
    }  
    
    public String getGamedetails(){
    	return gamedetails;
    }
    
    public void setGamedetails(String name){
    	gamedetails=name;
    }
    
    public String getRequestingteam(){
    	return requestingteam;
    }
    
    public void setRequestingteam(String name){
    	requestingteam=name;
    }
    
    
    public String getSelectedreason(){
    	return selectedreason;
    }
    
    public void setSelectedreason(String name){
    	selectedreason=name;
    }
    
    
    public String getNotes(){
    	return notes;
    }
    
    public void setNotes(String name){
    	notes=name;
    }
    
    public Integer getGameid(){
    	return gameid;
    }
    
    public void setGameid(Integer name){
    	gameid=name;
    }
    
    
    public Integer getProfid(){
    	return profileid;
    }	
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    public Integer getRequestingteamid(){
    	return requestingteamid;
    }	
    
    public void setRequestingteamid(Integer idprofile){
    	requestingteamid = idprofile;
    }
    
    public String getTodaysdate(){
    	return todaysdate;
    }
    
    public void setTodaysdate(String tdate){
    	todaysdate=tdate;
    }
    
    
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
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
		String result = "";
		
		myTokens.add("GAME: " + this.gamedetails);
		myTokens.add("REQUESTINGTEAM: " + this.requestingteam);
		myTokens.add("REASON: " + this.selectedreason);
		myTokens.add("NOTES: " + this.notes);
		myTokens.add("REQUESTDATE:" + this.todaysdate);
		
		result = Utils.mergeTokens(this.mail_gamechangerequest_body,myTokens);
			
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
	
	public void finalizeRequest() {  
    
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.addGameChangeRequest(?,?,?,?,?)");
    		cs.setInt("in_idlivegame", this.gameid);
    		cs.setInt("requestingteamid", pb.getProfile().getManagerteamid());
			cs.setString("reason", this.selectedreason);
			cs.setString("in_notes", this.notes);
			cs.setInt("in_idperson", 0);
			cs.executeQuery();
			db.commit();
			db.cleanup();
    		
			//LOGGER.info("game change request has been added:" + this.gameid);
    		
			//add ability to send email right here to manager and scheduler
			//need to add email to manager and scaha statistician
			to = "";
			cs = db.prepareCall("CALL scaha.getManagersforTeam(?)");
			cs.setInt("teamid", pb.getProfile().getManagerteamid());
  		    rs = cs.executeQuery();
  		    if (rs != null){
  				while (rs.next()) {
  					if(to.equals("")){
  						to = rs.getString("altemail");
  					}else {
  						to = to + ',' + rs.getString("altemail");
  					}
  				}
  			}
  		    rs.close();
  		    
  		    //to="";
  		    
			cs = db.prepareCall("CALL scaha.getSCAHAEmailForGameChangeRequest()");
  		    rs = cs.executeQuery();
  		    if (rs != null){
  				while (rs.next()) {
  					to = to + ',' + rs.getString("usercode");
  				}
  			}
  		    rs.close();
  		    db.cleanup();
  		    
			//to = "lahockeyfan2@yahoo.com";
		    this.setToMailAddress(to);
		    this.setPreApprovedCC("");
		    this.setSubject("Game Change Request for " + this.requestingteam);
		    
			SendMailSSL mail = new SendMailSSL(this);
			//LOGGER.info("Finished creating mail object for Game Change request for " + this.requestingteam);
			
			//set flag for getbody to know which template and values to use
			mail.sendMail();
			
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN adding game change request");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("managerportal.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}

		
	}
	
	public void loadGame() {  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getLiveGameSummary(?,?)");
			cs.setInt("gameid", this.gameid);
			cs.setInt("teamid", pb.getProfile().getManagerteamid());
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.gamedetails = rs.getString("gamedetails");
    				this.requestingteam = rs.getString("requestingteam");
    			}
				//LOGGER.info("We have results for game details:" + this.gameid);
			}
			
			
			rs.close();
			db.cleanup();
    		
			//LOGGER.info("game change reqeust added:" + this.gameid);
    		
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN adding game change request" + this.gameid);
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    }
	
	private void setTodaysDate(){
		//need to set todays date for email
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		Date date = new Date();
		this.setTodaysdate(dateFormat.format(date).toString());
		
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

