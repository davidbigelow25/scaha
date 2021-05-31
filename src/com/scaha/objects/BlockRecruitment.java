package com.scaha.objects;

import com.gbli.context.ContextManager;

import java.io.Serializable;
import java.util.logging.Logger;

public class BlockRecruitment extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());


	private String teamname = null;
	private Integer playercount = null;
	private String year = null;


	public BlockRecruitment(){
	}

	public BlockRecruitment(Profile _pro, int _id) {
		setProfile(_pro);
		ID = _id;
	}
	
	/**
	 * @return the tag
	 */
	public String getYear() {
		return year;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setYear(String tag) {
		this.year = tag;
	}

	/**
	 * @return the divisionname
	 */
	public String getTeamname() {
		return teamname;
	}

	/**
	 * @param divisionname the divisionname to set
	 */
	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	/**
	 * @return the iddivision
	 */
	public Integer getPlayercount() {
		return playercount;
	}

	/**
	 * @param iddivision the iddivision to set
	 */
	public void setPlayercount(Integer div) {
		this.playercount = div;
		this.ID =div.intValue();
	}
	
	

}
