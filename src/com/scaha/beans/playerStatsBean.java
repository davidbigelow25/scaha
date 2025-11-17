package com.scaha.beans;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.StatsDisplay;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.Team;
import com.scaha.objects.StatsDisplay;
import org.primefaces.component.schedule.Schedule;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import com.scaha.objects.GeneralSeason;
import com.scaha.objects.GeneralSeasonList;

//import com.scaha.objects.Schedule;
import com.scaha.objects.ScheduleList;
import com.gbli.common.Utils;


//import com.gbli.common.SendMailSSL;

@ManagedBean(name="playerStatsBean")
@ViewScoped
public class playerStatsBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value = "#{scahaBean}")
	private ScahaBean scaha;

	transient private ResultSet rs = null;

	private List<StatsDisplay> players = null;
	private PlayerDataModel PlayerDataModel = null;
	private StatsDisplay selectedplayer = null;
	private String selectedtabledisplay = null;
	private List<Team> teams = null;
	private Boolean displayteamlist = null;
	private String selectedteam = null;
	private String selectedplayerid = null;
	private Integer clubid = 0;
	private Integer profileid = 0;
	private String currentyear = null;
	private String prioryear = null;
	private String selectedSchedule;
	private List<String> schedules;
	private GeneralSeason selectedseason;
	private int selectedpartid;
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





	public Schedule getSelectedschedule() {
		return selectedschedule;
	}

	public void setSelectedschedule(Schedule selectedschedule) {
		this.selectedschedule = selectedschedule;
	}

	private Schedule selectedschedule;

	public int getSelectedscheduleid() {
		return selectedscheduleid;
	}

	public void setSelectedscheduleid(int selectedscheduleid) {
		this.selectedscheduleid = selectedscheduleid;
	}

	private int selectedscheduleid;


	public playerStatsBean() {
		clubid = 1;
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression(context.getELContext(),
				"#{profileBean}", Object.class);

	}

    public void init() {

	}

		/*public void onScheduleChange () {
			loadPlayersForSchedule(selectedSchedule);
		}*/
		/*public void onScheduleChange() {

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
		}*/

	public void onPartChange() {

		loadPlayersForTeam();
	}




		private void loadPlayersForTeam (){
			//need to load list of players and their stats
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			List<StatsDisplay> tempresult = new ArrayList<StatsDisplay>();

			try {
				CallableStatement cs = db.prepareCall("CALL scaha.getstatsdisplayforteamid(?)");
					cs.setInt("teamid",this.selectedpartid);
				rs = cs.executeQuery();
				if (rs != null){
					while (rs.next()) {
						StatsDisplay oplayer = new StatsDisplay(rs.getString("fname"),rs.getString("fname"),rs.getString("teamname"));
						oplayer.setIdStatsDisplay(rs.getInt("idstatsdisplay"));
						oplayer.setTeamid(rs.getInt("idteam"));
						oplayer.setIdClub(rs.getInt("idclub"));
						oplayer.setGoals(rs.getInt("goals"));
						oplayer.setAssists(rs.getInt("assists"));
						oplayer.setPoints(rs.getInt("points"));
						oplayer.setPims(rs.getInt("pims"));
						oplayer.setShots(rs.getInt("shots"));
						oplayer.setSaves(rs.getInt("saves"));
						oplayer.setGa(rs.getInt("ga"));
						oplayer.setGaa(rs.getDouble("gaa"));
						oplayer.setSavePct(rs.getDouble("savepercentage"));
						oplayer.setGamesPlayed(rs.getInt("gamesplayed"));
						oplayer.setMinutes(rs.getInt("minutes"));
						oplayer.setIsgoalie(rs.getInt("isgoalie"));

						tempresult.add(oplayer);
					}
				}
				rs.close();
				db.cleanup();



			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally{
				db.free();
			}

			this.setPlayers(tempresult);
		}

		public void saveStats (StatsDisplay sd) {
			// Implement DB save logic

			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

			 try{
				CallableStatement cs = db.prepareCall("CALL scaha.updateplayersstats(?,?,?,?,?,?,?,?,?,?,?,?,?)");
				cs.setInt("in_idlivegame", sd.getIdStatsDisplay());
				cs.setInt("goals", sd.getGoals());
				cs.setInt("assists", sd.getAssists());
				cs.setInt("points",sd.getPoints());
				cs.setInt("pims",sd.getPims());
				cs.setInt("shots",sd.getShots());
				cs.setInt("saves",sd.getSaves());
				cs.setInt("ga",sd.getGa());
				cs.setDouble("gaa",sd.getGaa());
				cs.setDouble("savepercentage",sd.getSavePct());
				cs.setInt("gamesplayed",sd.getGamesPlayed());
				cs.setInt("minutes",sd.getMinutes());
				cs.setInt("isgoalie",sd.getIsgoalie());

				cs.executeQuery();

				//LOGGER.info("game change request has been added:" + lgid);


				db.commit();
				rs.close();
				db.cleanup();



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
		// Getters and Setters
		public String getSelectedSchedule () {
			return selectedSchedule;
		}
		public void setSelectedSchedule (String selectedSchedule){
			this.selectedSchedule = selectedSchedule;
		}
		public List<String> getSchedules () {
			return schedules;
		}
		public List<StatsDisplay> getPlayers () {
			return players;
		}
		public void  setPlayers (List<StatsDisplay> sd) {this.players = sd;}
}


