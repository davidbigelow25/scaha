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
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.primefaces.event.FileUploadEvent;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Family;
import com.scaha.objects.FamilyMember;
import com.scaha.objects.Reportcard;
import com.scaha.objects.ReportcardDataModel;
import com.scaha.objects.ReportcardFileUploadController;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class reportcardBean implements Serializable {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	
	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<Reportcard> reportcards = null;
	
	//bean level properties used by multiple methods
	private Integer teamid = null;
	private Integer idclub = null;
	private Integer profileid = 0;
	private String gametype = null;
	private Integer idperson = null;
	private Reportcard selectedreportcard = null;
	private String gamedate = null;
	private String opponent = null;
	private String gametime = null;
	private String isscaha = null;
	private String redirect = null;
	private String fname = null;
	private String lname = null;
	
	//datamodels for all of the lists on the page
	private ReportcardDataModel ReportcardDataModel = null;
    
	//properties for uploading scoresheet
	private ReportcardFileUploadController fileuploadcontroller;
	
	@PostConstruct
    public void init() {
		//load defaults
		idclub = 1;  
        this.setProfid(pb.getProfile().ID);
        getClubID();
        
      //will need to load player profile information for displaying loi
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
	  	
	  	if(hsr.getParameter("id") != null)
	    {
	  		this.idperson = Integer.parseInt(hsr.getParameter("id").toString());
	    }
	  	
	  	
	  	
	  
	    this.redirect = "scholarathleteapplication.xhtml";
	  	
	  	
	  	this.fileuploadcontroller = new ReportcardFileUploadController();
	  	//need to load player info
	  	getPersoninformation();
	  	getPersonReportCards();
	}
	
    public reportcardBean() {  
        
    }  
    
    public String getRedirect(){
    	return redirect;
    }
    
    public void setRedirect(String gdate){
    	redirect=gdate;
    }
    
    
    public String getIsscaha(){
    	return isscaha;
    }
    
    public void setIsscaha(String gdate){
    	isscaha=gdate;
    }
    
    public String getFname(){
    	return fname;
    }
    
    public void setFname(String gdate){
    	fname=gdate;
    }
    
    public String getLname(){
    	return lname;
    }
    
    public void setLname(String gdate){
    	lname=gdate;
    }
    
    
    public String getOpponent(){
    	return opponent;
    }
    
    public void setOpponent(String gdate){
    	opponent=gdate;
    }
    
    
    public String getGametime(){
    	return gametime;
    }
    
    public void setGametime(String gdate){
    	gametime=gdate;
    }
    
    
    public String getGamedate(){
    	return gamedate;
    }
    
    public void setGamedate(String gdate){
    	gamedate=gdate;
    }
    
    
    public Reportcard getSelectedreportcard(){
    	return selectedreportcard;
    }
    
    public void setSelectedreportcard(Reportcard selected){
    	selectedreportcard=selected;
    }
    
    public List<Reportcard> getReportcards(){
		return reportcards;
	}
	
	public void setReportcards(List<Reportcard> list){
		reportcards = list;
	}
    
    public ReportcardDataModel getReportcarddatamodel(){
    	return ReportcardDataModel;
    }
    
    public void setReportcarddatamodel(ReportcardDataModel odatamodel){
    	ReportcardDataModel = odatamodel;
    }

    
    public ReportcardFileUploadController getFileuploadcontroller(){
    	return fileuploadcontroller;
    }
    
    public void setFileuploadcontroller(ReportcardFileUploadController gdate){
    	fileuploadcontroller=gdate;
    }
    
    public Integer getTeamid(){
    	return teamid;
    }
    
    public void setTeamid(Integer id){
    	teamid=id;
    }
    
    public Integer getIdperson(){
    	return idperson;
    }
    
    public void setIdperson(Integer id){
    	idperson=id;
    }
    
    
    public Integer getIdclub(){
    	return idclub;
    }
    
    public void setIdclub(Integer id){
    	idclub=id;
    }
    
    public Integer getProfid(){
    	return profileid;
    }	
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    public void getClubID(){
		
		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.getProfid());
			db.getData("CALL scaha.getClubforPerson(?)", v);
		    ResultSet rs = db.getResultSet();
			while (rs.next()) {
				this.idclub = rs.getInt("idclub");
			}
			rs.close();
			//LOGGER.info("We have results for club for a profile");
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
		reportcard.setIdperson(this.idperson);
		
		if (this.fileuploadcontroller.handleFileUpload(event,reportcard)){
			
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			
			try{

				if (db.setAutoCommit(false)) {
				
					//Need to provide info to the stored procedure to save the scoresheet filename, game type and game id
	 				//LOGGER.info("add report card");
	 				CallableStatement cs = db.prepareCall("CALL scaha.addReportCardForPerson(?,?,?)");
	    		    cs.setInt("personid", this.idperson);
	    		    cs.setString("infilename", reportcard.getFilename());
	    		    cs.setString("indisplayname",reportcard.getFiledisplayname());
	    		    
	    		    cs.executeQuery();
	    		    db.commit();
	    			db.cleanup();
	 				
	    			//LOGGER.info("You have added the report card:" + reportcard.getFilename() + " perosnid:" + this.idperson.toString());
	    		    //now to reload the scoresheet collection for datatable update
	    			getPersonReportCards();
				} else {
					//add error message here...
					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "Unable to upload file " + reportcard.getFiledisplayname()));
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.info("ERROR IN adding reportcard for person id:" + this.idperson);
				e.printStackTrace();
				db.rollback();
			} finally {
				//
				// always clean up after yourself..
				//
				db.free();
			}
			
		}
		
	}
	
	public void deleteReportcard(Reportcard reportcard){
		
		Integer idreportcard= reportcard.getIdreportcard();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to delete the scoresheet
 				//LOGGER.info("remove report card from list for the person");
 				CallableStatement cs = db.prepareCall("CALL scaha.deleteReportcard(?)");
    		    cs.setInt("reportcardid", idreportcard);
    		    cs.executeQuery();
    		    db.commit();
    			db.cleanup();
 				
    			//LOGGER.info("You have deleted the scoresheet :" + idreportcard);
    		    //now to reload the reportcards collection for datatable update to display
    			getPersonReportCards();
			} else {
				//add error message here...
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "Unable to delete the reportcard " + idreportcard));
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN adding reportcard for person id:" + this.idperson);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
	
		
	}

	public void getPersonReportCards() {  
		List<Reportcard> templist = new ArrayList<Reportcard>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
		if (this.idperson!=null){
			try{
	    		//first get team name
	    		CallableStatement cs = db.prepareCall("CALL scaha.getReportCardsForPerson(?)");
				cs.setInt("personid", this.idperson);
				rs = cs.executeQuery();
				
				if (rs != null){
					
					while (rs.next()) {
						Integer idreportcards = rs.getInt("idreportcards");
	    				String filename = rs.getString("filename");
	    				String displayname = rs.getString("displayname");
	    				String uploaddate = rs.getString("uploaddate");
	    				
	    				Reportcard reportcard = new Reportcard();
	    				reportcard.setFilename(filename);
	    				reportcard.setIdreportcard(idreportcards);
	    				reportcard.setUploaddate(uploaddate);
	    				reportcard.setFiledisplayname(displayname);
	    				templist.add(reportcard);
					}
					//LOGGER.info("We have results for Report card for person:" + this.idperson);
				}
				
				
				rs.close();
				db.cleanup();
	    		
			} catch (SQLException e) {
	    		// TODO Auto-generated catch block
	    		LOGGER.info("ERROR IN getting report cards for person:" + this.idperson);
	    		e.printStackTrace();
	    		db.rollback();
	    	} finally {
	    		//
	    		// always clean up after yourself..
	    		//
	    		db.free();
	    	}
		}
    	
		
    	setReportcards(templist);
    	ReportcardDataModel = new ReportcardDataModel(reportcards);
	}
	
	
	
	public void getPersoninformation() {  
		
		
		for(FamilyMember fm : pb.getProfile().getPerson().getFamily().getFamilyMembers()) {  
            
			if(fm.getPersonID()==this.idperson){
				this.fname = fm.getsFirstName();
				this.lname = fm.getsLastName();
            }
                  
        }
    }
	

	public void submitApplication(){
		FacesContext context = FacesContext.getCurrentInstance();
    	
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		CallableStatement cs = db.prepareCall("CALL scaha.addPersontoScholarAthlete(?)");
			cs.setInt("personid", this.idperson);
			rs = cs.executeQuery();
			//LOGGER.info("We have added " + this.idperson.toString() + " to scholar athletes");
    		rs.close();
    		db.cleanup();
    		
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting results for scholar athletes");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		db.free();
    	}
    	
    	try{
    		
    		//lets clear out old family object
    		pb.getProfile().getPerson().setFam(null);
    		
    		//lets create updated family object and add to the profile.
    		Family fm = new Family(db,pb.getProfile().getPerson());
    		pb.getProfile().getPerson().setFam(fm);
    		
    		context.getExternalContext().redirect(this.redirect);
    	} catch (IOException e) {
    		// TODO Auto-generated catch block
    		e.printStackTrace();
    	}
	}
}

