/**
 * 
 */
package com.scaha.objects;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * 
 * Tryout is an individual tryout event.  
 * 
 * It simply holds their date, time, location, division, level, and coach for a given club.
 * @author rfoster
 *
 */
public class Stat extends ScahaObject implements Serializable {
	
	
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String rank=null;
	private String playername=null;
	private String goals=null;
	private String teamname=null;
	private String points=null;
	private String assists=null;
	private String gaa=null;
	private String savepercentage=null;
	private String pims=null;
	private String gp=null;
	private String mins=null;
	private String shots=null;
	private String saves=null;
	private Integer id = null;
	private String jersey = null;
	private String ppg = null;
	private String ppa = null;
	private String shg = null;
	private String sha = null;
	private String gwg = null;
	
	public Stat() {
	
	}
	
	public String getGwg(){
		return gwg;
	}
	
	public void setGwg(String value){
		gwg=value;
	}
	
	
	public String getSha(){
		return sha;
	}
	
	public void setSha(String value){
		sha=value;
	}
	
	
	public String getShg(){
		return shg;
	}
	
	public void setShg(String value){
		shg=value;
	}
	
	public String getPpa(){
		return ppa;
	}
	
	public void setPpa(String value){
		ppa=value;
	}
	
	
	public String getPpg(){
		return ppg;
	}
	
	public void setPpg(String value){
		ppg=value;
	}
	
	
	
	public String getJersey(){
		return jersey;
	}
	
	public void setJersey(String value){
		jersey=value;
	}
	
	public Integer getId(){
		return id;
	}
	
	public void setId(Integer value){
		id=value;
	}
	
	
	public String getRank(){
		return rank;
	}
	
	public void setRank(String value){
		rank=value;
	}
	
	public String getPlayername(){
		return playername;
	}
	
	public void setPlayername(String value){
		playername=value;
	}
	
	public String getGoals(){
		return goals;
	}
	
	public void setGoals(String value){
		goals=value;
	}
	
	public String getAssists(){
		return assists;
	}
	
	public void setAssists(String value){
		assists=value;
	}
	
	public String getPoints(){
		return points;
	}
	
	public void setPoints(String value){
		points=value;
	}
	
	public String getTeamname(){
		return teamname;
	}
	
	public void setTeamname(String value){
		teamname=value;
	}
	
	public String getGaa(){
		return gaa;
	}
	
	public void setGaa(String value){
		gaa=value;
	}
	
	public String getSavepercentage(){
		return savepercentage;
	}
	
	public void setSavepercentage(String value){
		savepercentage=value;
	}
	
	public String getPims(){
		return pims;
	}
	
	public void setPims(String value){
		pims=value;
	}
	
	public String getGp(){
		return gp;
	}
	
	public void setGp(String value){
		gp=value;
	}
	
	public String getMins(){
		return mins;
	}
	
	public void setMins(String value){
		mins=value;
	}
	
	public String getShots(){
		return shots;
	}
	
	public void setShots(String value){
		shots=value;
	}
	
	public String getSaves(){
		return saves;
	}
	
	public void setSaves(String value){
		saves=value;
	}
	
	
	
}
