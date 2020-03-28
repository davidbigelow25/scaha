package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Sog extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private ScahaTeam  team = null;
	private LiveGame livegame = null;
	private LiveGameRosterSpot rosterspot = null;
	private int idroster = 0;
	private String playtime = "";
	private String playmins = "";
	private String playsecs = "";
	private int shots1 = 0;
	private int shots2 = 0;
	private int shots3 = 0;
	private int shots4 = 0;
	private int shots5 = 0;
	private int shots6 = 0;
	private int shots7 = 0;
	private int shots8 = 0;
	private int shots9 = 0;
	private String jerseynumber ="";
	
	
	public Sog(int _id, Profile _pro, LiveGame _lg, ScahaTeam _tm) {
		ID = _id;	
		this.setProfile(_pro);
		this.livegame = _lg;
		this.team = _tm;
	}

	public Sog(int _id, Profile _pro) {
		ID = _id;		
		this.setProfile(_pro);
	}
	
	public Sog(){
		
	}

	/**
	 * @return the team
	 */
	public ScahaTeam getTeam() {
		return team;
	}

	/**
	 * @param team the team to set
	 */
	public void setTeam(ScahaTeam team) {
		this.team = team;
	}

	/**
	 * @return the livegame
	 */
	public LiveGame getLivegame() {
		return livegame;
	}

	/**
	 * @param livegame the livegame to set
	 */
	public void setLivegame(LiveGame livegame) {
		this.livegame = livegame;
	}

	
		
	public void delete(ScahaDatabase db) throws SQLException {

		CallableStatement cs = db.prepareCall("call scaha.deleteSog(?)");
		cs.setInt(1, this.ID);
		cs.execute();
		cs.close();
		
	}
	
	public void update(ScahaDatabase db) throws SQLException {
		// 
		// is it an object that is not in the database yet..
		//
		
		CallableStatement cs = db.prepareCall("call scaha.updateSog(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
		
		//LOGGER.info("HERE IS THE Starting Sog ID:" + this.ID + ", idRoster is:" + this.idroster);

		int i = 1;
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(i++, this.ID);
		cs.setInt(i++, this.idroster);
		cs.setInt(i++, this.getLivegame().ID);
		cs.setInt(i++, this.getTeam().ID);
		cs.setString(i++, this.getPlaytime());
		cs.setInt(i++, this.shots1);
		cs.setInt(i++, this.shots2);
		cs.setInt(i++, this.shots3);
		cs.setInt(i++, this.shots4);
		cs.setInt(i++, this.shots5);
		cs.setInt(i++, this.shots6);
		cs.setInt(i++, this.shots7);
		cs.setInt(i++, this.shots8);
		cs.setInt(i++, this.shots9);
		cs.setInt(i++,1);
		cs.setString(i++,null);
		cs.execute();
		
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(1);
		cs.close();

		//LOGGER.info("HERE IS THE new Sog ID:" + this.ID);

		
	}

	

	/**
	 * @return the rosterspot
	 */
	public LiveGameRosterSpot getRosterspot() {
		return rosterspot;
	}

	/**
	 * @param rosterspot the rosterspot to set
	 */
	public void setRosterspot(LiveGameRosterSpot rosterspot) {
		this.rosterspot = rosterspot;
		if (this.rosterspot != null) {
			this.idroster = this.rosterspot.ID;
		}
	}

	/**
	 * @return the idroster
	 */
	public int getIdroster() {
		return idroster;
	}

	/**
	 * @param idroster the idroster to set
	 */
	public void setIdroster(int idroster) {
		this.idroster = idroster;
	}
	

	public String getJerseynumber(){
		return jerseynumber;
	}
	
	public void setJerseynumber(String value){
		jerseynumber = value;
	}
	
	/**
	 * @return the shots1
	 */
	public int getShots1() {
		return shots1;
	}

	/**
	 * @param shots1 the shots1 to set
	 */
	public void setShots1(int shots1) {
		this.shots1 = shots1;
	}

	/**
	 * @return the shots2
	 */
	public int getShots2() {
		return shots2;
	}

	/**
	 * @param shots2 the shots2 to set
	 */
	public void setShots2(int shots2) {
		this.shots2 = shots2;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Sog [team=" + team + ", livegame=" + livegame + ", rosterspot="
				+ rosterspot + ", idroster=" + idroster + ", shots1=" + shots1
				+ ", shots2=" + shots2 + ", shots3=" + shots3 + ", shots4="
				+ shots4 + ", shots5=" + shots5 + ", shots6=" + shots6
				+ ", shots7=" + shots7 + ", shots8=" + shots8 + ", shots9="
				+ shots9 + "]";
	}

	/**
	 * @return the shots3
	 */
	public int getShots3() {
		return shots3;
	}

	/**
	 * @param shots3 the shots3 to set
	 */
	public void setShots3(int shots3) {
		this.shots3 = shots3;
	}

	/**
	 * @return the shots4
	 */
	public int getShots4() {
		return shots4;
	}

	/**
	 * @param shots4 the shots4 to set
	 */
	public void setShots4(int shots4) {
		this.shots4 = shots4;
	}

	/**
	 * @return the shots5
	 */
	public int getShots5() {
		return shots5;
	}

	/**
	 * @param shots5 the shots5 to set
	 */
	public void setShots5(int shots5) {
		this.shots5 = shots5;
	}

	/**
	 * @return the shots6
	 */
	public int getShots6() {
		return shots6;
	}

	/**
	 * @param shots6 the shots6 to set
	 */
	public void setShots6(int shots6) {
		this.shots6 = shots6;
	}

	/**
	 * @return the shots7
	 */
	public int getShots7() {
		return shots7;
	}

	/**
	 * @param shots7 the shots7 to set
	 */
	public void setShots7(int shots7) {
		this.shots7 = shots7;
	}

	/**
	 * @return the shots8
	 */
	public int getShots8() {
		return shots8;
	}

	/**
	 * @param shots8 the shots8 to set
	 */
	public void setShots8(int shots8) {
		this.shots8 = shots8;
	}

	/**
	 * @return the shots9
	 */
	public int getShots9() {
		return shots9;
	}

	/**
	 * @param shots9 the shots9 to set
	 */
	public void setShots9(int shots9) {
		this.shots9 = shots9;
	}

	public int getOTShots() {
		return this.shots4 
				+ this.shots5
				+ this.shots6
				+ this.shots7
				+ this.shots8
				+ this.shots9;
	}
	
	public int getTotalShots() {
		return 	  this.shots1
				+ this.shots2
				+ this.shots3
				+ this.shots4
				+ this.shots5
				+ this.shots6
				+ this.shots7
				+ this.shots8
				+ this.shots9;
		
	}

	/**
	 * @return the playtime
	 */
	public String getPlaytime() {
		return playtime;
	}

	/**
	 * @param playtime the playtime to set
	 */
	public void setPlaytime(String playtime) {
		this.playtime = playtime;
	}
	
	public String getPlaymins() {
		return playmins;
	}

	/**
	 * @param playtime the playtime to set
	 */
	public void setPlaymins(String playtime) {
		this.playmins = playtime;
	}
	
	public String getPlaysecs() {
		return playsecs;
	}

	/**
	 * @param playtime the playtime to set
	 */
	public void setPlaysecs(String playtime) {
		this.playsecs = playtime;
	}
}
