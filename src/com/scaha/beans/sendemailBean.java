package com.scaha.beans;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.*;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
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

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class sendemailBean implements Serializable, MailableObject {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/boardofdirectoremail.html");
	private static String mailboys18_reg_body = Utils.getMailTemplateFromFile("/mail/loi18receipt.html");

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;

	transient private ResultSet rs = null;

	private String sendfromemail = null;
	private Integer selectedprofileid = null;
	private String subject = "test email message";
	private String cc = null;
	private String to = null;
	private String emailsubject = null;
	private String emailbody = null;
	private Member selectedmember = null;
	private ClubAdmin selectedclubadmin = null;
	private String fname = null;
	private String lname = null;
	private Boolean isexecutivelist = null;

	@PostConstruct
    public void init() {
		//hard code value until we load session variable
    	FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );

		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );

		//will need to load executive board member profile

		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

		if(hsr.getParameter("source").equals("clublist") )
		{
			loadClubAdminProfile();
		} else {
			loadBODProfile();
		}




    	ValueExpression scahaexpression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{scahaBean}", Object.class );

		scaha = (ScahaBean) scahaexpression.getValue( context.getELContext() );



    }

	public sendemailBean() {
        
    }

	public Boolean getIsexecutivelist(){return this.isexecutivelist;}
	public void setIsexecutivelist(Boolean value){
		this.isexecutivelist=value;
	}

    public Member getSelectedmember(){return this.selectedmember;}
	public void setSelectedmember(Member value){
		this.selectedmember=value;
	}

	public ClubAdmin getSelectedclubadmin(){return this.selectedclubadmin;}
	public void setSelectedclubadmin(ClubAdmin value){
		this.selectedclubadmin=value;
	}

	public String getFname(){return this.fname;}
	public void setFname(String value){
		this.fname=value;
	}

	public String getLname(){return this.lname;}
	public void setLname(String value){
		this.lname=value;
	}

	public String getSendfromemail(){return this.sendfromemail;}
	public void setSendfromemail(String value){
		this.sendfromemail=value;
	}

	public Integer getSelectedprofileid(){return this.selectedprofileid;}
	public void setSelectedprofileid(Integer value){this.selectedprofileid=value;}

	public String getEmailsubject(){return this.emailsubject;}
	public void setEmailsubject(String value){this.emailsubject=value;}

	public String getEmailbody(){return this.emailbody;}
	public void setEmailbody(String value){
		this.emailbody=value;
	}

	public String getSubject() {
		// TODO Auto-generated method stub
		return this.subject;
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
		myTokens.add("EMAILBODY:" + this.emailbody);
		String testemailbody = Utils.mergeTokens(sendemailBean.mail_reg_body, myTokens);
		return testemailbody;

		
		
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
	

	

	//used to populate loi form with player information
	public void loadBODProfile(){
		this.selectedmember = scaha.getSelectedmember();
		this.to = this.selectedmember.getMemberemail();
		this.fname = this.selectedmember.getMembername();
		setIsexecutivelist(true);
   }

	public void loadClubAdminProfile(){
		this.selectedclubadmin = scaha.getSelectedclubadmin();
		this.to = this.selectedclubadmin.getsEmail();
		this.fname = this.selectedclubadmin.getsFirstName() + " " + this.selectedclubadmin.getsLastName();
		setIsexecutivelist(false);
	}
	
	public void SendMessage(){

			//hard my email address for testing purposes

			//to = "lahockeyfan2@yahoo.com";
			this.setToMailAddress(to + "," + this.sendfromemail);
			this.setPreApprovedCC("");
			this.setSubject(this.emailsubject);

			SendMailSSL mail = new SendMailSSL(this);
			//LOGGER.info("Finished creating mail object for " + this.firstname + " " + this.lastname + " LOI with " + this.getClubName());
			mail.sendMail();

			FacesContext context = FacesContext.getCurrentInstance();
			String origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
			try{
				if (this.isexecutivelist){
					context.getExternalContext().redirect("boardofdirectors.xhtml");
				}else {
					context.getExternalContext().redirect("viewallclubs.xhtml");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}




    }
	

	public void BacktoBoardofDirectors(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();

		String origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
		try{
			if (this.isexecutivelist){
				context.getExternalContext().redirect("boardofdirectors.xhtml");
			}else {
				context.getExternalContext().redirect("viewallclubs.xhtml");
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

