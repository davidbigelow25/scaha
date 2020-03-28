package com.scaha.beans;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;

import com.gbli.common.ReturnDataResultSet;
import com.gbli.common.ReturnDataRow;
import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.MailableObject;


@ManagedBean
@ViewScoped
public class FindMeBean implements Serializable, MailableObject  {


	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_body_find_me = Utils.getMailTemplateFromFile("mail/findmeintro.html");

	private ReturnDataResultSet mydata = null;
	private ReturnDataRow selectedrow = null;
	
	private String searchcriteria = null;
	
	 @PostConstruct
	 public void init() {
   		//LOGGER.info("*************** FIND ME BEAN INIT *************");
	 }


	 
	 public void meSearch(){
		    
		 	//LOGGER.info("*************** meSearch(" +  this.searchcriteria + ") called *************");

		 	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
	    	
	    	try{

	   			Vector<String> v = new Vector<String>();
	   			v.add(this.searchcriteria);
	   			db.getData("CALL scaha.srchForMyAccount(?)", v);
	   			ResultSet rs = db.getResultSet();
	   			this.mydata = ReturnDataResultSet.NewReturnDataResultSetFactory(rs);
	    		
	    	} catch (SQLException e) {
	    		LOGGER.info("ERROR IN Searching FOR " + this.searchcriteria);
	    		e.printStackTrace();
	    	} finally {
	    		db.free();
	    	}
	    	
	 }
	 
	 
	/**
	 * @return the mydata
	 */
	public ReturnDataResultSet getMydata() {
		return mydata;
	}


	/**
	 * @param mydata the mydata to set
	 */
	public void setMydata(ReturnDataResultSet mydata) {
		this.mydata = mydata;
	}


	/**
	 * @return the selectedrow
	 */
	public ReturnDataRow getSelectedrow() {
		return selectedrow;
	}


	/**
	 * @param selectedrow the selectedrow to set
	 */
	public void setSelectedrow(ReturnDataRow selectedrow) {
		this.selectedrow = selectedrow;
	}



	/**
	 * @return the searchcriteria
	 */
	public String getSearchcriteria() {
		return searchcriteria;
	}



	/**
	 * @param searchcriteria the searchcriteria to set
	 */
	public void setSearchcriteria(String searchcriteria) {
		this.searchcriteria = searchcriteria;
	}
	
	public String send() {
		SendMailSSL mail = new SendMailSSL(this);
		mail.sendMail();
		//LOGGER.info("Sent out Account Info to " + this.getToMailAddress());
		FacesContext context = FacesContext.getCurrentInstance();
		context.getExternalContext().getFlash().setKeepMessages(true);			
		context.addMessage(
				null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Who Am I",
                "Account Information has been sent to the following e-mail addresse(s): " + this.getToMailAddress()));
				
		//
		// Lets make sure we return a valid URL
		//
		return "Welcome.xhtml?faces-redirect=true";
	}



	@Override
	public String getSubject() {
		// TODO Auto-generated method stub
		return "SCAHA iSite Account Reminder";
	}
	@Override
	public String getTextBody() {
		
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("NAME:" + selectedrow.getByKey("Acount Owner Name"));
		myTokens.add("USERCODE:" + selectedrow.get(13).toString());
		myTokens.add("PASS:" + selectedrow.get(14).toString());
		return Utils.mergeTokens(mail_body_find_me,myTokens);
	}
	
	@Override
	public String getPreApprovedCC() {
		return "info@scaha.com";
	}
	
	@Override
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return selectedrow.get(13).toString() + "," + selectedrow.getByKey("email");
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
