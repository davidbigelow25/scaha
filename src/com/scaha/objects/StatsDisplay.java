package com.scaha.objects;

import com.gbli.context.ContextManager;

import java.io.Serializable;
import java.util.logging.Logger;

public class StatsDisplay extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String firstName;
	private String lastName;
	private String teamName;

	private Integer IdStatsDisplay;
	private Integer IdClub;



	private Integer isgoalie;
	private Integer goals;
	private Integer assists;
	private Integer points;
	private Integer pims;
	private Integer shots;
	private Integer saves;
	private Integer ga;
	private Integer gamesPlayed;
	private Integer minutes;


	private Integer teamid;
	private Double gaa, savePct;

	public StatsDisplay(String firstName, String lastName, String teamName) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.teamName = teamName;
	}

	// --- Getters and Setters ---
	public String getFirstName() { return firstName; }
	public String getLastName() { return lastName; }
	public String getTeamName() { return teamName; }
	public void setFirstName(String firstName) {this.firstName = firstName;}
	public void setLastName(String lastName) {this.lastName = lastName;	}
	public void setTeamName(String teamName) {this.teamName = teamName;}
	public Integer getIdStatsDisplay() {return IdStatsDisplay;}
	public void setIdStatsDisplay(Integer idStatsDisplay) {IdStatsDisplay = idStatsDisplay;}
	public Integer getIdClub() {return IdClub;}
	public void setIdClub(Integer idClub) {IdClub = idClub;	}
	public Integer getIsgoalie() {return isgoalie;}
	public void setIsgoalie(Integer isgoalie) {this.isgoalie = isgoalie;}
	public Integer getTeamid() {return teamid; }
	public void setTeamid(Integer teamid) {this.teamid = teamid; }
	public Integer getGoals() { return goals; }
	public void setGoals(Integer goals) { this.goals = goals; }
	public Integer getAssists() { return assists; }
	public void setAssists(Integer assists) { this.assists = assists; }
	public Integer getPoints() { return points; }
	public void setPoints(Integer points) { this.points = points; }
	public Integer getPims() { return pims; }
	public void setPims(Integer pims) { this.pims = pims; }
	public Integer getShots() { return shots; }
	public void setShots(Integer shots) { this.shots = shots; }
	public Integer getSaves() { return saves; }
	public void setSaves(Integer saves) { this.saves = saves; }
	public Integer getGa() { return ga; }
	public void setGa(Integer ga) { this.ga = ga; }
	public Double getGaa() { return gaa; }
	public void setGaa(Double gaa) { this.gaa = gaa; }
	public Double getSavePct() { return savePct; }
	public void setSavePct(Double savePct) { this.savePct = savePct; }
	public Integer getGamesPlayed() { return gamesPlayed; }
	public void setGamesPlayed(Integer gamesPlayed) { this.gamesPlayed = gamesPlayed; }
	public Integer getMinutes() { return minutes; }
	public void setMinutes(Integer minutes) { this.minutes = minutes; }
}
