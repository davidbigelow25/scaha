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
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.GeneralSeason;
import com.scaha.objects.GeneralSeasonList;
import com.scaha.objects.LiveGame;
import com.scaha.objects.LiveGameList;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Participant;
import com.scaha.objects.ParticipantList;
import com.scaha.objects.Schedule;
import com.scaha.objects.ScheduleList;

@ManagedBean
@SessionScoped
public class ScoreboardBean implements Serializable,  MailableObject {

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	
	transient private ResultSet rs = null;
	
	private ScheduleList schedules;
	private List<Schedule> schedulelist =  null;
	private List<Participant> partpicklist =  null;
	
	private GeneralSeasonList seasons = null;
	

	private Schedule selectedschedule;	
	private int selectedscheduleid;
	private GeneralSeason selectedseason;
	private int selectedseasonid;
	private Participant selectedpart;
	private int selectedpartid;
	private List<LiveGame> selectedlivegamesforreschedule;
	private String notes;
	private List<String> dates;
	
	private ParticipantList partlist = null;
	private LiveGameList livegamelist = null;
	private LiveGameList livegamelistbyclub = null;
	private LiveGame selectedlivegame = null;

	//email paramters
	private String to = null;
	private String subject = null;
	private String cc = null;
	private static String mail_gamechangerequest_body = Utils.getMailTemplateFromFile("/mail/clubreschedule.html");
	private String gamelist = null;
	private String todaysdate = null;
	private String requestingclub = null;
	//
	// Class Level Variables
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//some variables for the mobile version
	private String agegroup = null;
	private String skillgroup = null;
	private String scheduletitle = null;
	
	//
	// lets go get it!
	//
	public ScoreboardBean() {
	}

	 @PostConstruct
	 public void init() {
		 setTodaysDate();
		 
		// LOGGER.info(" *************** START :POST INIT FOR SCOREBOARD BEAN *****************");
		 //
		 // for now.. we will borrow what was from scaha for schedule.
		 // in the end.. we want to pull the schedule based upon the season they choose..
		 // which means this list gets personalized... (*for history *)
		 // Currently Very cludgy.. but over time.. we can generalize on the season key as the way to 
		 // look at data from past seasons..
		 //
		 this.setSeasons(scaha.getScahaSeasonList());
		 this.selectedseason = scaha.getScahaSeasonList().getCurrentSeason();
		 this.selectedseasonid = selectedseason.ID;
		 this.refreshScheduleList();
		 
		 //LOGGER.info(" *************** FINISH :POST INIT FOR SCOREBOARD BEAN *****************");
	 }
	
	 
	 @PreDestroy
	 public void cleanup() {
		 //LOGGER.info(" *************** PRE DESTROY FOR SCOREBOARD BEAN *****************");
	 }
	public String refreshScoreboard() {
		if (this.getPartlist() != null) {
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			try {
			// Lets get the connections we need
				this.getPartlist().refreshList(pb.getProfile(), db);
			} catch (SQLException ex) {
				ex.printStackTrace();
			} finally {
				db.free();
			}
			  
		}
		return "Date and Time is: "  + (new Date());
	}
	
	/**
	 * This guy builds up the scoreboard related information..
	 * Then routes the user to the correct page
	 * 
	 * We need to pass schedule and possibly team pointers as URL parms 
	 * 
	 * So that clubs and teams can point to this page from other locations
	 * 
	 * @return
	 */
	public void buildIt() {
		
		//
		// We also will want to pass parms here.. if applicable..

		try {
			FacesContext.getCurrentInstance().getExternalContext().redirect("scoreboard.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	/**
	 * Recalc schedule for season..
	 */
	public void onSeasonChange() {
		//LOGGER.info("season change request detected new id is:" + this.selectedseasonid);
		this.selectedseason = this.seasons.getGeneralSeason(this.selectedseasonid);
		refreshScheduleList();
	}
	
	/**
	 * get participants for a schedule..
	 */
	public void onScheduleChange() {
		
		this.selectedschedule = this.schedules.getSchedule(this.selectedscheduleid);
		//LOGGER.info("schedule change request detected new id is:" + this.selectedscheduleid + ":" + this.selectedschedule);
		this.partlist = null;
		if (this.selectedschedule != null) {
			
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			
			//if historical season lets look up the games and standings for the season.
			if(this.selectedseason!=scaha.getScahaSeasonList().getCurrentSeason()){
				
				try {
					//generating historical standings and adding them to the partlist.
					this.partlist = ParticipantList.NewListFactory(db, selectedscheduleid);
					this.partpicklist = ParticipantList.getHistoricalParticipantList(db, selectedscheduleid);
					
					//generating the historical schedule of games and adding them to the livegamelist
					this.setLivegamelist(scaha.getScahaLiveGameList().NewListFactory(db, selectedscheduleid));
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					db.free();
				}
				
			}else {
				
				this.partlist = this.selectedschedule.getPartlist();
				
				//this.partpicklist = this.getParticipantpicklist();
				
				try {
					//generating historical standings and adding them to the partlist.
					this.partpicklist = ParticipantList.getHistoricalParticipantList(db, selectedscheduleid);
					
					//this is hear for testing purposes only
					//this.partpicklist = this.getParticipantpicklist();
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					db.free();
				}
				//need to perfrom role check here for displaying schedule
				this.setLivegamelist(scaha.getScahaLiveGameList().NewList(scaha.getDefaultProfile(),selectedschedule));
				
			}
		}
	}
	
	
	/**
	 * get participants for a schedule..
	 */
	public void onPartChange() {
		
		if(this.selectedseason!=scaha.getScahaSeasonList().getCurrentSeason()){
			//need to load historical schedule by team name
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			try {
				this.setLivegamelist(scaha.getScahaLiveGameList().NewListFactory(db, selectedscheduleid,this.selectedpartid));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				db.free();
			}
			
		} else {
		
			this.selectedpart = this.getPartlist().getByKey(this.selectedpartid);
			//LOGGER.info("participant change request detected new id is:" + this.selectedpartid + ":" + this.selectedpart + " for sched:" + this.selectedschedule);
			//need to perfrom role check here for displaying schedule
			
			
				if (this.selectedpart != null) {
						this.setLivegamelist(this.selectedschedule.getLivegamelist().NewList(scaha.getDefaultProfile(), selectedschedule, selectedpart.getTeam()));
					
				} else {
					if (this.selectedpartid > 0){
						this.setLivegamelist(this.selectedschedule.getLivegamelist().NewList(scaha.getDefaultProfile(), selectedschedule, this.selectedpartid));
					}
					else {
						this.setLivegamelist(this.selectedschedule.getLivegamelist());
					}
					
					
				}
			
		}
	}
	
	public void refreshScheduleList() {
		
		//
		// ok.. lets do the schedules now..
		//
		//LOGGER.info("Refreshing Schedule List for season:" + this.selectedseason);
		this.schedulelist = null;	
		this.schedules = null;
		this.partlist = null;
		this.setLivegamelist(null);
		this.partpicklist = null;
		if (this.selectedseason != null) {

			if(this.selectedseason!=scaha.getScahaSeasonList().getCurrentSeason()){
				ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
				try {
					this.schedules = ScheduleList.ListFactory(pb.getProfile(), db,this.selectedseason,scaha.getScahaTeamList());
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
					db.free();
				}
				
			}else {
				this.schedules = this.selectedseason.getSchedList();
			}
			
			//LOGGER.info("season schedule is: " + schedules);

			this.schedules = this.selectedseason.getSchedList();
//			LOGGER.info("season schedule is: " + schedules);

			if (schedules != null) {
			  if (this.schedules.getRowCount() > 0) {
				  this.schedulelist = this.getScheduleList();
			  }	else {
				//LOGGER.info("Refresh.. zero list.. leaving null:" + this.schedules.getRowCount());
			  } 
			}
		}
        
	}
	
	
	public void refreshSeasonList() {

		//LOGGER.info("Getting season List");
		//
		// ok.. lets do the seasons now..
		//
		this.seasons = scaha.getScahaSeasonList();
		
}
	
	
	@Override
	public String getSubject() {
		// TODO Auto-generated method stub
		return this.subject;
	}

	public String getScheduletitle(){
    	return scheduletitle;
    }
    
    public void setScheduletitle(String tdate){
    	scheduletitle=tdate;
    }
    
	public String getAgegroup(){
    	return agegroup;
    }
    
    public void setAgegroup(String tdate){
    	agegroup=tdate;
    }
    
    public String getSkillgroup(){
    	return skillgroup;
    }
    
    public void setSkillgroup(String tdate){
    	skillgroup=tdate;
    }
	
	public String getRequestingclub(){
    	return requestingclub;
    }
    
    public void setRequestingclub(String tdate){
    	requestingclub=tdate;
    }
    
	public String getTodaysdate(){
    	return todaysdate;
    }
    
    public void setTodaysdate(String tdate){
    	todaysdate=tdate;
    }
    
	public String getGamelist(){
		return this.gamelist;
	}
	
	public void setGamelist(String value){
		this.gamelist = value;
	}
	
	
	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return to;
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
	
	public void setPreApprovedCC(String scc){
		cc = scc;
	}
	
	public void setToMailAddress(String sto){
    	to = sto;
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

	public void setNotes(String value){
		this.notes = value;
	}
	
	public String getNotes(){
		return this.notes;
	}
	
	/**
	 * @return the schedules
	 */
	public ScheduleList getSchedules() {
		return schedules;
	}

	/**
	 * @param schedules the schedules to set
	 */
	public void setSchedules(ScheduleList schedules) {
		this.schedules = schedules;
	}

	/**
	 * @return the seasons
	 */
	public GeneralSeasonList getSeasons() {
		return seasons;
	}
	
	@SuppressWarnings("unchecked")
	public List<GeneralSeason> getSeasonlist() {
		return (List<GeneralSeason>)seasons.getWrappedData();
	}

	/**
	 * @param seasons the seasons to set
	 */
	public void setSeasons(GeneralSeasonList seasons) {
		this.seasons = seasons;
		//LOGGER.info("Here is our General Season:");
		//LOGGER.info(this.seasons.toString());
	}

	
	/**
	 * @return the selectedseason
	 */
	public GeneralSeason getSelectedseason() {
		return selectedseason;
	}

	/**
	 * @param selectedseason the selectedseason to set
	 */
	public void setSelectedseason(GeneralSeason selectedseason) {
		this.selectedseason = selectedseason;
	}

	/**
	 * @return the selectedschedule
	 */
	public Schedule getSelectedschedule() {
		return selectedschedule;
	}

	/**
	 * @param selectedschedule the selectedschedule to set
	 */
	public void setSelectedschedule(Schedule selectedschedule) {
		this.selectedschedule = selectedschedule;
	}

	/**
	 * @return the selectedseasonid
	 */
	public int getSelectedseasonid() {
		return selectedseasonid;
	}

	/**
	 * @param selectedseasonid the selectedseasonid to set
	 */
	public void setSelectedseasonid(int selectedseasonid) {
		this.selectedseasonid = selectedseasonid;
	}

	/**
	 * @return the partlist
	 */
	public ParticipantList getPartlist() {
		return partlist;
	}

	/**
	 * @param partlist the partlist to set
	 */
	public void setPartlist(ParticipantList partlist) {
		this.partlist = partlist;
	}

	/**
	 * @return the selectedscheduleid
	 */
	public int getSelectedscheduleid() {
		return selectedscheduleid;
	}

	/**
	 * @param selectedscheduleid the selectedscheduleid to set
	 */
	public void setSelectedscheduleid(int selectedscheduleid) {
		this.selectedscheduleid = selectedscheduleid;
	}

	/**
	 * @return the schedulelist
	 */
	public List<Schedule> getSchedulelist() {
		return schedulelist;
	}

	/**
	 * @param schedulelist the schedulelist to set
	 */
	public void setSchedulelist(List<Schedule> schedulelist) {
		this.schedulelist = schedulelist;
	}
	
	@SuppressWarnings("unchecked")
	private List<Schedule> getScheduleList() {
		return (List<Schedule>)schedules.getWrappedData();
	}
	
	@SuppressWarnings("unchecked")
	private List<Participant> getParticipantpicklist() {
		return (List<Participant>)partlist.getWrappedData();
	}

	/**
	 * @return the livegamelist for a club for reschedule request
	 */
	public LiveGameList getLivegamelistByClub() {
		return this.livegamelistbyclub;
	}

	public void setLivegamelistByClub(LiveGameList lgl){
		this.livegamelistbyclub = lgl;
	}
	
	public void loadLiveGamesByClub(){
		//need to go out and populate this livegame object and load
		//only games for a club into this object.
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		LiveGameList lgl = null;
		try {
			lgl = scaha.getScahaLiveGameList().NewListFactory(db, pb.getProfile());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} finally{
			db.free();
		}
		
		this.setLivegamelistByClub(lgl);
	}
	
	/**
	 * @return the livegamelist
	 */
	public LiveGameList getLivegamelist() {
		return livegamelist;
	}

	/**
	 * @param livegamelist the livegamelist to set
	 */
	public void setLivegamelist(LiveGameList livegamelist) {
		this.livegamelist = livegamelist;
	}

	/**
	 * @return the partpicklist
	 */
	public List<Participant> getPartpicklist() {
		return partpicklist;
	}

	/**
	 * @param partpicklist the partpicklist to set
	 */
	public void setPartpicklist(List<Participant> partpicklist) {
		this.partpicklist = partpicklist;
	}

	/**
	 * @return the selectedpart
	 */
	public Participant getSelectedpart() {
		return selectedpart;
	}

	/**
	 * @param selectedpart the selectedpart to set
	 */
	public void setSelectedpart(Participant selectedpart) {
		this.selectedpart = selectedpart;
	}

	/**
	 * @return the selectedpartid
	 */
	public int getSelectedpartid() {
		return selectedpartid;
	}

	/**
	 * @param selectedpartid the selectedpartid to set
	 */
	public void setSelectedpartid(int selectedpartid) {
		this.selectedpartid = selectedpartid;
	}

	/**
	 * @return the selectedlivegame
	 */
	public LiveGame getSelectedlivegame() {
		return selectedlivegame;
	}
	
	public void setSelectedlivegamesforreschedule(List<LiveGame> lg){
		this.selectedlivegamesforreschedule = lg;
	}
	
	public List<LiveGame> getSelectedlivegamesforreschedule(){
		return this.selectedlivegamesforreschedule;
	}

	/**
	 * @param selectedlivegame the selectedlivegame to set
	 */
	public void setSelectedlivegame(LiveGame selectedlivegame) {
		this.selectedlivegame = selectedlivegame;
	}


	 public void editLiveGame(LiveGame lg) {  
		 
		 //this.selectedlivegame = this.getLivegamelist().getByKey(this.selectedlivegame.ID);
		 this.selectedlivegame = lg;
		 pb.setSelectedlivegame(this.selectedlivegame);
		 pb.setLivegameeditreturn("scoreboard.xhtml");
		 
		// LOGGER.info("!!!!! Real Selected Game is" + selectedlivegame);
		  
	     ExternalContext context = FacesContext.getCurrentInstance().getExternalContext(); 
	     try {
	    	 context.redirect("gamesheetcentral.xhtml");
	     } catch (IOException e) {
	    	 // TODO Auto-generated catch block
			e.printStackTrace();
		}
	     
	 } 


	 public void sendGameChangeRequest(){
		 //first we need to get all of the game info toinclude in the email.
		 //second we add each games request in the database.
		 //third send the email.
		 Integer lgid;
		 String hometeam;
		 String awayteam;
		 String startdate;
		 String starttime;
		 String venue;
		 String sheet;
		 
		 ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		 this.gamelist = "";
		 try{
			 CallableStatement cs = db.prepareCall("CALL scaha.addGameChangeRequest(?,?,?,?,?)");
	    	for (LiveGame lg : selectedlivegamesforreschedule){
					 
				String locallist = "";	
	    		lgid = lg.getGameId();
					 hometeam = lg.getHometeam().getTeamname();
					 awayteam = lg.getAwayteam().getTeamname();
					 startdate = lg.getStartdate();
					 starttime = lg.getStarttime();
					 venue = lg.getVenueshortname();
					 sheet = lg.getSheetname();
					 
					//first get team name
		    		cs.setInt("in_idlivegame", lgid);
		    		cs.setInt("requestingteamid", 0);
					cs.setString("reason", "");
					cs.setString("in_notes", this.notes);
					cs.setInt("in_idperson",pb.getProfile().getPerson().ID);
					cs.executeQuery();
					
					//now build the html string for the email to convenor, club president, and statistician
					locallist = locallist + "<tr>";
					locallist = locallist + "<td>";
					locallist = locallist + lgid.toString();
					locallist = locallist + "</td>";
					
					locallist = locallist + "<td>";
					locallist = locallist + hometeam;
					locallist = locallist + "</td>";
					
					locallist = locallist + "<td>";
					locallist = locallist + awayteam;
					locallist = locallist + "</td>";
					
					locallist = locallist + "<td>";
					locallist = locallist + startdate;
					locallist = locallist + "</td>";
					
					locallist = locallist + "<td>";
					locallist = locallist + starttime;
					locallist = locallist + "</td>";
					
					locallist = locallist + "<td>";
					locallist = locallist + venue;
					locallist = locallist + "</td>";
					
					locallist = locallist + "<td>";
					locallist = locallist + sheet;
					locallist = locallist + "</td>";
					
					
					locallist = locallist + "</tr>";
					
					this.gamelist = this.gamelist + locallist;
					
					//LOGGER.info("game change request has been added:" + lgid);
			}
			 
			db.commit();
			db.cleanup();
		 
			//add ability to send email right here to scheduler and president
			to = pb.getAltemail();
			
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
		    this.setSubject("SCAHA Game Change Request");
		    
			SendMailSSL mail = new SendMailSSL(this);
			//LOGGER.info("Finished creating mail object for Game Change request for " + pb.getClubID());
			
			mail.sendMail();
			
			this.setSelectedlivegamesforreschedule(null);
			this.setNotes("");
			
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
		 
	 }
	 
	 public String getTextBody() {
			// TODO Auto-generated method stub
			List<String> myTokens = new ArrayList<String>();
			String result = "";
			
			myTokens.add("GAMELIST:: " + this.gamelist);
			myTokens.add("NOTES:: " + this.notes);
			myTokens.add("REQUESTDATE::" + this.todaysdate);
			myTokens.add("REQUESTINGCLUB::" + pb.getName());
			
			result = Utils.mergeTokens(this.mail_gamechangerequest_body,myTokens,"::");
				
			return result;
			
	}
	 
	 private void setTodaysDate(){
		//need to set todays date for email
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/YYYY");
		Date date = new Date();
		this.setTodaysdate(dateFormat.format(date).toString());
		
	}
	 
	public void loadgamesformobile(){
		Integer scheduleidformobile = 0;
		
		//need to find out the age group and skill group selected and then refresh the live game list
		if (agegroup.equals("squirt") && skillgroup.equals("A")){
			scheduleidformobile=409;
			scheduletitle = "Squirt A";
		}
		if (agegroup.equals("squirt") && skillgroup.equals("BB")){
			scheduleidformobile=408;
			scheduletitle = "Squirt BB";
		}
		if (agegroup.equals("squirt") && skillgroup.equals("B")){
			scheduleidformobile=407;
			scheduletitle = "Squirt B";
		}
		if (agegroup.equals("peewee") && skillgroup.equals("AA")){
			scheduleidformobile=419;
			scheduletitle = "Peewee AA";
		}
		if (agegroup.equals("peewee") && skillgroup.equals("A")){
			scheduleidformobile=412;
			scheduletitle = "Peewee A";
		}
		if (agegroup.equals("peewee") && skillgroup.equals("BB")){
			scheduleidformobile=411;
			scheduletitle = "Peewee BB";
		}
		if (agegroup.equals("peewee") && skillgroup.equals("B")){
			scheduleidformobile=410;
			scheduletitle = "Peewee B";
		}
		if (agegroup.equals("bantam") && skillgroup.equals("AA")){
			scheduleidformobile=420;
			scheduletitle = "Bantam AA";
		}
		if (agegroup.equals("bantam") && skillgroup.equals("A")){
			scheduleidformobile=414;
			scheduletitle = "Bantam A";
		}
		if (agegroup.equals("bantam") && skillgroup.equals("B")){
			scheduleidformobile=413;
			scheduletitle = "Bantam B";
		}
		if (agegroup.equals("midget16") && skillgroup.equals("AA")){
			scheduleidformobile=421;
			scheduletitle = "16U AA";
		}
		if (agegroup.equals("midget18") && skillgroup.equals("AA")){
			scheduleidformobile=422;
			scheduletitle = "18U AA";
		}
		
		//need to perfrom role check here for displaying schedule
		this.setLivegamelist(scaha.getScahaLiveGameList().NewList(scaha.getDefaultProfile(),scheduleidformobile));
		
	}
	
	public void lahoanav(){
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext(); 
	     try {
	    	 context.redirect("lahoa.xhtml");
	     } catch (IOException e) {
	    	 // TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}