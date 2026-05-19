package com.scaha.objects;

import com.gbli.context.ContextManager;

import java.io.Serializable;
import java.util.logging.Logger;

public class TeamInvoice extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private int teamId;
	private String teamName;
	private int playerCount;
	private double totalAmount;

	// Getters & Setters
	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	// Getter and Setter for teamName
	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	// Getter and Setter for playerCount
	public int getPlayerCount() {
		return playerCount;
	}

	public void setPlayerCount(int playerCount) {
		this.playerCount = playerCount;
	}

	// Getter and Setter for totalAmount
	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public TeamInvoice(){
		
	}
}

	
	

