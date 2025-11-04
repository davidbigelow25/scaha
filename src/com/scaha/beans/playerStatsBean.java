package com.scaha.beans;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.Team;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

//import com.gbli.common.SendMailSSL;


public class playerStatsBean implements Serializable {

	// Class Level Variables
	/*private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;

	transient private ResultSet rs = null;

	private List<Player> players = null;
    private PlayerDataModel PlayerDataModel = null;
    private Player selectedplayer = null;
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
	private List<StatsDisplay> players;

    public playerStatsBean() {
    	clubid = 1;
    	FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );



    public PlayerStatsBean() {
			schedules = Arrays.asList("Schedule A", "Schedule B", "Schedule C");
			selectedSchedule = schedules.get(0);
			loadPlayersForSchedule(selectedSchedule);
		}

		public void onScheduleChange() {
			loadPlayersForSchedule(selectedSchedule);
		}

		private void loadPlayersForSchedule(String schedule) {
			StatsDisplay = new ArrayList<>();
			// Mock player data - replace with DB query in production
			for (int i = 1; i <= 5; i++) {
				StatsDisplay p = new StatsDisplay("Player" + i, "Last" + i, "Team" + i);
				p.setGoals(i * 2);
				p.setAssists(i);
				p.setPoints(p.getGoals() + p.getAssists());
				StatsDisplay.add(p);
			}
		}

		public void saveStats() {
			// Implement DB save logic
			System.out.println("Saving stats for " + players.size() + " players.");
		}

		// Getters and Setters
		public String getSelectedSchedule() { return selectedSchedule; }
		public void setSelectedSchedule(String selectedSchedule) { this.selectedSchedule = selectedSchedule; }
		public List<String> getSchedules() { return schedules; }
		public List<Player> getPlayers() { return players; }*/
}

