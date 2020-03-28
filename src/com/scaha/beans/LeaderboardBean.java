package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;

import com.gbli.common.ReturnDataResultSet;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.GeneralSeason;
import com.scaha.objects.GeneralSeasonList;
import com.scaha.objects.LiveGame;
import com.scaha.objects.LiveGameList;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Participant;
import com.scaha.objects.ParticipantList;
import com.scaha.objects.Profile;
import com.scaha.objects.Schedule;
import com.scaha.objects.ScheduleList;

@ManagedBean
@ViewScoped
public class LeaderboardBean implements Serializable,  MailableObject {

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	
	private ReturnDataResultSet mydata = null;
	
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

	private ParticipantList partlist = null;

	//
	// Class Level Variables
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// lets go get it!
	//
	public LeaderboardBean() {
	}

	 @PostConstruct
	 public void init() {
		 
		 //LOGGER.info(" *************** START :POST INIT FOR LEADERBOARD BEAN *****************");
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
		 
		 //LOGGER.info(" *************** FINISH :POST INIT FOR LEADERBOARD BEAN *****************");
	 }
	
	 @PreDestroy 
	 public void cleanup() {
		 //LOGGER.info(" *************** START :PRE DESTROY FOR LEADERBOARD BEAN *****************");
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
			this.partlist = this.selectedschedule.getPartlist();
			this.partpicklist = this.getParticipantpicklist();
			this.refreshLeaderBoard(0);
		}
	}
	
	
	/**
	 * get participants for a schedule..
	 */
	public void onPartChange() {
		
		this.selectedpart = this.getPartlist().getByKey(this.selectedpartid);
		//LOGGER.info("participant change request detected new id is:" + this.selectedpartid + ":" + this.selectedpart + " for sched:" + this.selectedschedule);
		if (this.selectedpart == null) {
			this.refreshLeaderBoard(0);
		} else {
			this.refreshLeaderBoard(this.selectedpart.getTeam().ID);
		}
	}
	
	private void refreshScheduleList() {
		
		//
		// ok.. lets do the schedules now..
		//
		//LOGGER.info("Refreshing Schedule List for season:" + this.selectedseason);
		this.schedulelist = null;	
		this.schedules = null;
		this.partlist = null;
		//this.setLivegamelist(null);
		this.partpicklist = null;
		if (this.selectedseason != null) {

			this.schedules = this.selectedseason.getSchedList();
			
			//LOGGER.info("season schedule is: " + schedules);

			this.schedules = this.selectedseason.getSchedList();

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
	
	 
	
	public void refreshLeaderBoard(int _idTeam) {
		 ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		 Profile pro = pb.getProfile(); // logged in profile id..
		 //LOGGER.info("REFRESHING LEADER BOARD FOR: " + this.selectedscheduleid + ", TeamID:" + _idTeam);
		 try {
			 PreparedStatement ps = db.prepareStatement("call scaha.getPlayerLeaderBoard(?,?)");
			 ps.setInt(1,selectedscheduleid);
			 ps.setInt(2, _idTeam);// Team Later down the road
			 ResultSet rs = ps.executeQuery();
			 setMydata(ReturnDataResultSet.NewReturnDataResultSetFactory(rs));
			 rs.close();
			 ps.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
	}
	
	
	@Override
	public String getSubject() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String getTextBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return null;
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

	public ReturnDataResultSet getMydata() {
		return mydata;
	}

	public void setMydata(ReturnDataResultSet mydata) {
		this.mydata = mydata;
	}

	

	
}