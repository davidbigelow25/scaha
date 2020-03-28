package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.MinuteFileUploadController;
import com.scaha.objects.Reportcard;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class meetingminuteBean implements Serializable {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	transient private ResultSet rs = null;
	
	private Reportcard uploadedreportcard = null;
	
	//bean level properties used by multiple methods
	private String minutesfilename = null;
	private String meetingdate = null;
	private Boolean enableMinuteButton = null;
	
	//properties for uploading scoresheet
	private MinuteFileUploadController fileuploadcontroller;
	
	@PostConstruct
    public void init() {
		
		this.fileuploadcontroller = new MinuteFileUploadController();
		this.setenableMinuteButton(true);
	}
	
    public meetingminuteBean() {  
        
    }  
    
    public Boolean getenableMinuteButton(){
    	return enableMinuteButton;
    }
    
    public void setenableMinuteButton(Boolean value){
    	enableMinuteButton = value;
    }
    
    public String getMinutesfilename(){
    	return minutesfilename;
    }
    
    public void setMinutesfilename(String gdate){
    	minutesfilename=gdate;
    }
    
    public String getMeetingdate(){
    	return meetingdate;
    }
    
    public void setMeetingdate(String gdate){
    	meetingdate=gdate;
    }
    
    public Reportcard getUploadedreportcard(){
    	return this.uploadedreportcard;
    }
    
    public void setUploadedreportcard(Reportcard obj){
    	this.uploadedreportcard = obj;
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

	public void handleFileUpload(FileUploadEvent event) {  
        
		Reportcard reportcard = new Reportcard();
		reportcard.setIdperson(pb.getProfile().getPerson().getPersonID());
		
		if (this.fileuploadcontroller.handleFileUpload(event,reportcard)){
			
			//lets set the reportcard object in memory to the one used to upload the file.
			this.setUploadedreportcard(reportcard);
			
		}	else {
			//add error message here...
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "Unable to upload file " + this.getUploadedreportcard().getFiledisplayname()));
			this.setUploadedreportcard(reportcard);
			
		}
		
		ShouldWeEnableButton();
	}
	
	public void saveMeetingMinute(){
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save the scoresheet filename, game type and game id
 				//LOGGER.info("remove tournament game from list");
 				CallableStatement cs = db.prepareCall("CALL scaha.addMeetingMinute(?,?)");
    		    cs.setString("infilename", this.getUploadedreportcard().getFilename());
    		    
    		    //need to change meetingdate value to string format mm/dd/yyyy
    		    DateFormat df=new SimpleDateFormat("MM/dd/yyyy");
    		    DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
    	    	Date date;
    	    	String tempstring = "";
    	    	try{
    	    		date = formatter.parse(this.meetingdate);
    	    		tempstring = df.format(date);
        	    } catch (ParseException e){
        	    	e.printStackTrace();
        	    }
    	    	this.setMeetingdate(tempstring);
    	    	
    		    cs.setString("inmeetingdate", this.meetingdate);
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    			//LOGGER.info("You have added the ,meeting minute :" + this.getUploadedreportcard().getFilename() + " meeting date:" + this.meetingdate);
    		    //now to reload the meeting minute collection in scaha object for datatable update
    			scaha.setMeetingminutes();
    			
    			
    			//now lets clear out values for the fields since we were successful
    			this.setenableMinuteButton(true);
    			this.setMeetingdate(null);
    			this.setMinutesfilename("");
    			this.setUploadedreportcard(null);
    		
    		} else {
				//add error message here...
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "Unable to upload file " + this.getUploadedreportcard().getFiledisplayname()));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN adding meeting minute for meeting date:" + this.meetingdate);
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
			context.getExternalContext().redirect("viewmeetingminutes.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}

	public void ShouldWeEnableButton(){
		if (this.uploadedreportcard!=null){
			if (this.getUploadedreportcard().getFilename()!=null && this.meetingdate!=null){
				this.setenableMinuteButton(false);
			}else{
				this.setenableMinuteButton(true);
			}
		} else {
			this.setenableMinuteButton(true);
		}
	}
}

