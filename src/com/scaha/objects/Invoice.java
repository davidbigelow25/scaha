package com.scaha.objects;

import com.gbli.context.ContextManager;

import java.io.Serializable;
import java.util.logging.Logger;

public class Invoice extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private int clubId;
	private String clubName;
	private int playerCount;
	private double pricePerPlayer;
	private double totalAmount;

	// Getters & Setters
	public int getClubId() { return clubId; }
	public void setClubId(int clubId) { this.clubId = clubId; }

	public String getClubName() { return clubName; }
	public void setClubName(String clubName) { this.clubName = clubName; }

	public int getPlayerCount() { return playerCount; }
	public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }

	public double getPricePerPlayer() { return pricePerPlayer; }
	public void setPricePerPlayer(double pricePerPlayer) { this.pricePerPlayer = pricePerPlayer; }

	public double getTotalAmount() { return totalAmount; }
	public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

	public Invoice(){
		
	}
}

	
	

