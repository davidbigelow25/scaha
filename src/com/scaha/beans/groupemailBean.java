package com.scaha.beans;

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
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.EmailGroup;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Team;

//import com.gbli.common.SendMailSSL;


public class groupemailBean implements Serializable, MailableObject {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/generalemail.html");
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	transient private ResultSet rs = null;
	
	//email variables
	private String to = "";
	private String subject = null;
	private String cc = "";
	private String message = null;
	
	//list variables for display on the page
	private List<EmailGroup> groups = null;
	private List<EmailGroup> scahagroups = null;
	
	//list variable for selected groups on the page
	private String[] selectedgroups = null;
	private String[] selectedscahagroups = null;
	
	
	@PostConstruct
    public void init() {
		FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );
		
		getListofGroups();
	}
	
	
	public groupemailBean() {  
        
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
		myTokens.add("MESSAGE:" + this.message);
		
		return Utils.mergeTokens(groupemailBean.mail_reg_body,myTokens);
		
		
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
	
	public void setGroups(List<EmailGroup> value){
		groups = value;
	}
	
	public List<EmailGroup> getGroups(){
		return groups;
	}
	
	public void setScahagroups(List<EmailGroup> value){
		scahagroups = value;
	}
	
	public List<EmailGroup> getScahagroups(){
		return scahagroups;
	}
	public void setSelectedgroups(String[] value){
		selectedgroups = value;
	}
	
	public String[] getSelectedgroups(){
		return selectedgroups;
	}
	
	public void setSelectedscahagroups(String[] value){
		selectedscahagroups = value;
	}
	
	public String[] getSelectedscahagroups(){
		return selectedscahagroups;
	}
	public void setMessage(String value){
		message = value;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void sendEmail(){
		//need to iterate through each group selected and retrieve the emails for the managers of the group
		
		//lets get the cc email addresses of the board members selected.
		EmailGroup em = new EmailGroup();
		for(String selectedgroup : this.selectedscahagroups) {
			em = scahagroups.get(Integer.parseInt(selectedgroup));
			
			if (cc.equals("")){
				cc = em.getEmail();
			} else {
				cc = cc + "," + em.getEmail();
			}
			
			
		}
		
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
	    
		
    	try{
    		Integer i = 0;
    		CallableStatement cs = db.prepareCall("CALL scaha.getEmailsForGroup(?)");
    		for(String selectedgroup : this.selectedgroups) {
	    		cs.setInt("groupid", Integer.parseInt(selectedgroup));
	    		rs = cs.executeQuery();
	    		if (rs != null){
	
					while (rs.next()) {
						i++;
						String email = rs.getString("altemail");
						to = email;
	    				/*if (to.equals("")){
	    					to = email;
	    				} else {
	    					to = to + "," + email;
	    				}*/
						
						//hard my email address for testing purposes
					    //to = "lahockeyfan2@yahoo.com";
					    this.setToMailAddress(to);
					    if (i==1){
					    	this.setPreApprovedCC(cc);
					    }
					    
					    
						SendMailSSL mail = new SendMailSSL(this);
						LOGGER.info("Finished creating mail object");
						mail.sendMail();
			    		LOGGER.info("We have sent email with email list:" + to);
						
					}
					LOGGER.info("We have results for group list");
				}
	    		
				rs.close();
    		}
			db.cleanup();
    		
			
			
					
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading email for group details");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	this.setMessage("");
    	this.setSubject("");
    	this.setSelectedgroups(null);
    	this.setSelectedscahagroups(null);
    	
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

	public List<EmailGroup> getListofGroups(){
		List<EmailGroup> templist = new ArrayList<EmailGroup>();
		List<EmailGroup> templistscaha = new ArrayList<EmailGroup>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			CallableStatement cs = db.prepareCall("CALL scaha.getEmailsGroups()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				//need to add to an array
    				//rs = db.getResultSet();
    				
    				while (rs.next()) {
    					Integer idgroup = rs.getInt("iddivisiontoskillforemail");
        				Integer divisionid = rs.getInt("divisionid");
        				Integer skilllevelid = rs.getInt("skilllevelid");
        				String displaylabel = rs.getString("displaylabel");
        				
        				EmailGroup em = new EmailGroup();
        				em.setDisplayname(displaylabel);
        				em.setDivisionid(divisionid);
        				em.setSkilllevelid(skilllevelid);
        				em.setIdemailgroup(idgroup);

        				templist.add(em);
    				}
    				LOGGER.info("We have results for group list");
    			}
    			rs.close();
    			
    			//now lets get scaha members
    			cs = db.prepareCall("CALL scaha.getscahaboardmembers()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				//need to add to an array
    				//rs = db.getResultSet();
    				Integer idgroup = 0;
    				while (rs.next()) {
    					String displaylabel = rs.getString("membername");
        				String email = rs.getString("memberemail");
        				
        				EmailGroup em = new EmailGroup();
        				em.setDisplayname(displaylabel);
        				em.setIdemailgroup(idgroup);
        				em.setEmail(email);
        				templistscaha.add(em);
        				idgroup = idgroup + 1;
        				
    				}
    				LOGGER.info("We have results for scaha group list");
    			}
    			rs.close();
    			db.cleanup();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading group list");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	this.setGroups(templist);
    	this.setScahagroups(templistscaha);
		
		return this.getGroups();
	}
}

