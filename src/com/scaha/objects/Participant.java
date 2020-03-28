package com.scaha.objects;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class Participant extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private ScahaTeam  team = null;
	private Schedule schedule = null;
	private int Rank = 0;
	private int place = 0;
	private int games = 0;
	private int exgames = 0;
	private int gamesplayed = 0;
	private int wins = 0;
	private int loses = 0;
	private int ties = 0;
	private int points = 0;
	private int gf = 0;
	private int ga = 0;
	private int gd = 0;
	private boolean hasdropped = false;
	
	private Vector<Slot> SlotsAvail = new Vector<Slot>();
	private Vector<Slot> SlotsPlaying = new Vector<Slot>();
	private Vector<Slot> SlotsMatchup = new Vector<Slot>();
	
	public Participant (int _id, int _irank, Schedule _se, ScahaTeam _team) {
		ID = _id;
		this.setRank(_irank);
		this.setSchedule(_se);
		this.setTeam(_team);
		
	}
	
	public Participant(int _id, Profile _pro) {
		ID = _id;		
		this.setProfile(_pro);
	}

	//use this interface for loading historical standings.
	public Participant(int _id) {
		ID = _id;		
	}

	
	/**
	 * @return the slotsPlaying
	 */
	public Vector<Slot> getSlotsPlaying() {
		return SlotsPlaying;
	}

	/**
	 * @param slotsPlaying the slotsPlaying to set
	 */
	public void setSlotsPlaying(Vector<Slot> slotsPlaying) {
		SlotsPlaying = slotsPlaying;
	}

	/**
	 * @return the SlotsAvail
	 */
	public Vector<Slot> getSlotsAvail() {
		return SlotsAvail;
	}

	public ScahaTeam getTeam() {
		// TODO Auto-generated method stub
		return this.team;
	}
	public void setTeam(ScahaTeam _team) {
		this.team = _team;
	}

	/**
	 * @return the schedule
	 */
	public Schedule getSchedule() {
		return schedule;
	}

	/**
	 * @param schedule the schedule to set
	 */
	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}

	/**
	 * @return the rank
	 */
	public int getRank() {
		return Rank;
	}

	/**
	 * @param rank the rank to set
	 */
	public void setRank(int rank) {
		Rank = rank;
	}

	/**
	 * @return the slotsMatchup
	 */
	public Vector<Slot> getSlotsMatchup() {
		return SlotsMatchup;
	}

	/**
	 * @param slotsMatchup the slotsMatchup to set
	 */
	public void setSlotsMatchup(Vector<Slot> slotsMatchup) {
		SlotsMatchup = slotsMatchup;
	}
	
	@Override
	public String toString() {
		return "Participant [Team=" + team + ", Rank=" + Rank + ", ID=" + ID + "]";
	}

	/**
	 * Rules checker to make sure this game can be played...
	 * Given we are running in squeeze mode
	 * @param _sl
	 * @param _pAgainst
	 * @return
	 */
	public boolean canIPlay(Schedule _se, Slot _sl, Participant _pAgainst) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 

		try {
			 Date dateAvail = dateFormat.parse(_sl.actDate + " " + _sl.StartTime); 
			 if (this.getSlotsPlaying().isEmpty()) {
		    	return true;
			 }
			 for (Slot slot: this.getSlotsPlaying()) {
				Date datePlaying = dateFormat.parse(slot.actDate + " " + slot.StartTime); 
			    long diff = dateAvail.getTime() - datePlaying.getTime();
			    long hours = Math.abs(diff / (1000*60*60));
				
				if (_pAgainst.getTeam().ID ==  slot.idTeamAgainst && hours < 24 * (_se.getTeamcount() * 2)) {
			    	//LOGGER.info("I cannot play...not enough time between matchups.. needs to be more than:" +(24 * (_se.getTeamcount() * 1.25)));
			    	return false;
			    }
			    if (hours < 7 ) {
			    	
			    	//LOGGER.info("I cannot play... da=" + dateAvail + ", dp=" + datePlaying + " not enough time (" + hours + ") between matchups..  already have a game within 7 hours of start time");
			    	return false;
			    }
			    
			    //
			    // lets make sure its not in the middle of a club or team out of town
			    //
			    
			    for (String bodate: _pAgainst.getTeam().getTeamGameInfo().getHmBOD().keySet()) {
			    	Date datebo = dateFormat.parse(bodate + " 00:00:00");
			        long bodiff = dateAvail.getTime() - datebo.getTime();
			    	long bodhours = Math.abs(bodiff / (1000*60*60));
					if (bodhours < 72) {
				    	//LOGGER.info("I cannot play..club or team is out of town");
					   	return false;
					}
			    }
			    
			}
			 
		} catch (Exception e) {
			e.printStackTrace();
		}
    	//LOGGER.info("I (" + this.getTeam() + ") can play on this slot:" + _sl);
		
		return true;
	}

	public void resetSlotsMatchup() {
		SlotsMatchup = new Vector<Slot>();
	}
	
	public void resetSlotsAvail() {
		this.SlotsAvail = new Vector<Slot>();
	}
	
	public void resetSlotsPlayed() {
		SlotsPlaying = new Vector<Slot>();
	}

	/**
	 * @return the place
	 */
	public int getPlace() {
		return place;
	}

	/**
	 * @param place the place to set
	 */
	public void setPlace(int place) {
		this.place = place;
	}

	/**
	 * @return the wins
	 */
	public int getWins() {
		return wins;
	}

	/**
	 * @param wins the wins to set
	 */
	public void setWins(int wins) {
		this.wins = wins;
	}

	/**
	 * @return the loses
	 */
	public int getLoses() {
		return loses;
	}

	/**
	 * @param loses the loses to set
	 */
	public void setLoses(int loses) {
		this.loses = loses;
	}

	/**
	 * @return the ties
	 */
	public int getTies() {
		return ties;
	}

	/**
	 * @param ties the ties to set
	 */
	public void setTies(int ties) {
		this.ties = ties;
	}

	/**
	 * @return the points
	 */
	public int getPoints() {
		return points;
	}

	/**
	 * @param points the points to set
	 */
	public void setPoints(int points) {
		this.points = points;
	}

	/**
	 * @return the gf
	 */
	public int getGf() {
		return gf;
	}

	/**
	 * @param gf the gf to set
	 */
	public void setGf(int gf) {
		this.gf = gf;
	}

	/**
	 * @return the ga
	 */
	public int getGa() {
		return ga;
	}

	/**
	 * @param ga the ga to set
	 */
	public void setGa(int ga) {
		this.ga = ga;
	}

	/**
	 * @return the gd
	 */
	public int getGd() {
		return gd;
	}

	/**
	 * @param gd the gd to set
	 */
	public void setGd(int gd) {
		this.gd = gd;
	}

	/**
	 * @return the gamesplayed
	 */
	public int getGamesplayed() {
		return gamesplayed;
	}

	/**
	 * @param gamesplayed the gamesplayed to set
	 */
	public void setGamesplayed(int gamesplayed) {
		this.gamesplayed = gamesplayed;
	}
	
	public int getKey() {
		return ID;
	}

	/**
	 * @return the games
	 */
	public int getGames() {
		return games;
	}

	/**
	 * @param games the games to set
	 */
	public void setGames(int games) {
		this.games = games;
	}

	/**
	 * @return the exgames
	 */
	public int getExgames() {
		return exgames;
	}

	/**
	 * @param exgames the exgames to set
	 */
	public void setExgames(int exgames) {
		this.exgames = exgames;
	}

	public boolean isHasdropped() {
		return hasdropped;
	}

	public void setHasdropped(boolean hasdropped) {
		this.hasdropped = hasdropped;
	}
}
