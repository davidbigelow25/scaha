package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Link;
import com.scaha.objects.Team;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class modifytournamentBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//private static String mail_exhibitiongame_body = Utils.getMailTemplateFromFile("/mail/exhibitiongame.html");
	transient private ResultSet rs = null;
	
	//lists for generated datamodels
	//private List<ExhibitionGame> exhibitiongames = null;
	
	//properties for creating the heart of the tournament records
	private String tournamentname=null;
	private String startdate=null;
	private String enddate=null;
	private String sanction=null;
	private String website=null;
	private String director=null;
	private String phone = null;	
	private String email = null;

	//lookup list variables
	private List<Link> venues = null;
	
	//datamodels for all of the lists on the page
	//private ExhibitionGameDataModel ExhibitionGameDataModel = null;
    
    //properties for storing the selected row of each of the datatables
	private String[] selectedvenues = null;
	private String[] selectedlevels = null;
	
	
		
	
	@PostConstruct
    public void init() {
		
		//not sure what is needed yet.
		loadVenues();
    }
	
    public modifytournamentBean() {  
    
    }  
    
    public String[] getSelectedvenues() {
        return selectedvenues;
    }
 
    public void setSelectedvenues(String[] selectedVenues) {
        this.selectedvenues = selectedVenues;
    }
    
    public String[] getSelectedlevels() {
        return selectedlevels;
    }
 
    public void setSelectedlevels(String[] selectedVenues) {
        this.selectedlevels = selectedVenues;
    }
    
    public List<Link> getVenues(){
    	return venues;
    }
    
    public void setVenues(List<Link> value){
    	venues=value;
    }
    
    public String getTournamentname(){
    	return tournamentname;
    }
    
    public void setTournamentname(String gdate){
    	tournamentname=gdate;
    }
    
    
    public String getStartdate(){
    	return startdate;
    }
    
    public void setStartdate(String gdate){
    	startdate=gdate;
    }
    
    public String getEnddate(){
    	return enddate;
    }
    
    public void setEnddate(String gdate){
    	enddate=gdate;
    }
    
    public String getSanction(){
    	return sanction;
    }
    
    public void setSanction(String gdate){
    	sanction=gdate;
    }
    
    
    public String getWebsite(){
    	return website;
    }
    
    public void setWebsite(String gdate){
    	website=gdate;
    }
    
    public String getDirector(){
    	return director;
    }
    
    public void setDirector(String value){
    	director=value;
    }
    
    public void setPhone(String name){
    	phone=name;
    }
    
    public String getPhone(){
    	return phone;
    }
    
    public void setEmail(String name){
    	email=name;
    }
    
    public String getEmail(){
    	return email;
    }
    
    
    
    
	
	public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    	
		
	
		
	public void addTournament(){
		
		//need to add logic to add base tournament record meta data
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to provide info to the stored procedure to save or update
 				//LOGGER.info("add tournament to scaha list");
 				CallableStatement cs = db.prepareCall("CALL scaha.addScahaTournament(?,?,?,?,?,?,?,?)");
    		    cs.setString("tourneyname", this.tournamentname);
    		    cs.setString("startdate", this.startdate);
    		    cs.setString("enddate", this.enddate);
    		    cs.setString("sanction", this.sanction);
    		    cs.setString("website", this.website);
    		    cs.setString("director", this.director);
    		    cs.setString("phone", this.phone);
    		    cs.setString("email", this.email);
    		    rs = cs.executeQuery();
    			
    		    Integer tourneyid = null;
    			if (rs != null){
    				
    				while (rs.next()) {
    			
    					tourneyid = rs.getInt(1);
    					CallableStatement cs2 = db.prepareCall("CALL scaha.addtournamentvenue(?,?)");
    					
		    		    //ok now we need to add venues
		    		    for (int i = 0; i < this.selectedvenues.length; i++) {
		    		    	cs2.setInt("tourneyid", tourneyid);
		        		    cs2.setInt("venueid", Integer.parseInt(this.selectedvenues[i]));
		        		    cs2.executeQuery();
		        		}
		    		    cs2.close();
		    		    
		    		    
		    		    
		    		    //and now we finish up with the levels and age groups
		    		    cs2 = db.prepareCall("CALL scaha.addtournamentlevel(?,?,?,?)");
		    		    String tempagegroup = "";
		    		    String tempskillgroup = "";
		    		    Integer flag=0;
		    		    for (int i = 0; i < this.selectedlevels.length; i++) {
		    		    	if (this.selectedlevels[i].equals("hsaaa")){
		    		    		tempagegroup = "High School Varsity";
		    		    		tempskillgroup = "AAA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("hsaa")){
		    		    		tempagegroup = "High School Varsity";
		    		    		tempskillgroup = "AA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("hsa")){
		    		    		tempagegroup = "High School Varsity";
		    		    		tempskillgroup = "A";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("hsjvaaa")){
		    		    		tempagegroup = "High School Junior Varsity";
		    		    		tempskillgroup = "AAA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("hsjvaa")){
		    		    		tempagegroup = "High School Junior Varsity";
		    		    		tempskillgroup = "AA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("hsjva")){
		    		    		tempagegroup = "High School Junior Varsity";
		    		    		tempskillgroup = "A";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("18uaaa")){
		    		    		tempagegroup = "Midget 18U";
		    		    		tempskillgroup = "AAA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("18uaa")){
		    		    		tempagegroup = "Midget 18U";
		    		    		tempskillgroup = "AA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("18ua")){
		    		    		tempagegroup = "Midget 18U";
		    		    		tempskillgroup = "A";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("16uaaa")){
		    		    		tempagegroup = "Midget 18U";
		    		    		tempskillgroup = "AAA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("16uaa")){
		    		    		tempagegroup = "Midget 16U";
		    		    		tempskillgroup = "AA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("16ua")){
		    		    		tempagegroup = "Midget 16U";
		    		    		tempskillgroup = "A";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("14uaaa")){
		    		    		tempagegroup = "Bantam";
		    		    		tempskillgroup = "AAA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("14uaa")){
		    		    		tempagegroup = "Bantam";
		    		    		tempskillgroup = "AA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("14ua")){
		    		    		tempagegroup = "Bantam";
		    		    		tempskillgroup = "A";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("14ub")){
		    		    		tempagegroup = "Bantam";
		    		    		tempskillgroup = "B";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("12uaaa")){
		    		    		tempagegroup = "Peewee";
		    		    		tempskillgroup = "AAA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("12uaa")){
		    		    		tempagegroup = "Peewee";
		    		    		tempskillgroup = "AA";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("12ua")){
		    		    		tempagegroup = "Peewee";
		    		    		tempskillgroup = "A";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("12ubb")){
		    		    		tempagegroup = "Peewee";
		    		    		tempskillgroup = "BB";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("12ub")){
		    		    		tempagegroup = "Peewee";
		    		    		tempskillgroup = "B";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("10ua")){
		    		    		tempagegroup = "Squirt";
		    		    		tempskillgroup = "A";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("10ubb")){
		    		    		tempagegroup = "Squirt";
		    		    		tempskillgroup = "BB";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("10ub")){
		    		    		tempagegroup = "Squirt";
		    		    		tempskillgroup = "B";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("8utr1")){
		    		    		tempagegroup = "Mite";
		    		    		tempskillgroup = "Track1";
		    		    		flag=1;
		    		    	}
		    		    	if (this.selectedlevels[i].equals("8utr2")){
		    		    		tempagegroup = "Mite";
		    		    		tempskillgroup = "Track2";
		    		    		flag=1;
		    		    	}
		    		    	
		    		    	
		    		    	
		    		    	cs2.setInt("tourneyid", tourneyid);
		    		    	cs2.setString("AgeGroup", tempagegroup);
		    		    	cs2.setString("Skill", tempskillgroup);
		        		    cs2.setInt("flag", flag);
		        		    cs2.executeQuery();
		        		    
		        		    //need to clear the variables.
		        		    tempagegroup="";
		        		    tempskillgroup="";
		        		    flag=0;
		        		}
		    		    
    				}
    			}
		    				
    		    db.commit();
    			
    		    FacesContext context = FacesContext.getCurrentInstance();  
                context.addMessage(null, new FacesMessage("Successful", "You have add the scaha tournament"));
                
                
    			db.cleanup();
    			
    			
			} else {
		
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Adding the Tournament");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
		//need to clear the fields now.
		this.director="";
		this.email="";
		this.enddate="";
		this.phone="";
		this.sanction="";
		this.selectedlevels=null;
		this.selectedvenues=null;
		this.startdate="";
		this.tournamentname="";
		this.website="";
		
	}

	public void loadVenues(){
		List<Link> templist = new ArrayList<Link>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{

    		if (db.setAutoCommit(false)) {
    		
    			//Vector<Integer> v = new Vector<Integer>();
    			//v.add(1);
    			//db.getData("CALL scaha.getTeamsByClub(?)", v);
    		    CallableStatement cs = db.prepareCall("CALL scaha.getVenueList()");
    		    rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					Integer idvenue = rs.getInt("idvenue");
        				String description = rs.getString("description");
        				
        				Link link = new Link();
        				link.setLinkid(idvenue);
        				link.setLinklabel(description);
        				templist.add(link);
    				}
    				//LOGGER.info("We have results for get venue list");
    			}
    			rs.close();
    			cs.close();
    			db.cleanup();
    		} else {
    		
    		}
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading venues");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setVenues(templist);
	}
}

