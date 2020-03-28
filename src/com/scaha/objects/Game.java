package com.scaha.objects;

public class Game extends ScahaObject {

	//game level properties used by multiple methods
	private Integer idlivegame = null;
	private String hometeam = null;
	private String awayteam = null;
	private String typetag = null;
	private String location = null;
	private String gametime = null;
	private String homescore = null;
	private String awayscore = null;
	private String status = null;
	private String displaydivision = null;
	private Integer homeclubid = null;
	private Integer awayclubid = null;
	private Boolean renderboxscore = null;
	private String scoresheeturl = null;	
	private Integer awaywins = null;
	private Integer awaylosses = null;
	private Integer awayties = null;
	private Integer awaypoints = null;
	private Integer homewins = null;
	private Integer homelosses = null;
	private Integer hometies = null;
	private Integer homepoints = null;
	private Boolean renderperiodtotals = null;
	private Integer homeper1goals = null;
	private Integer homeper2goals = null;
	private Integer homeper3goals = null;
	private Integer homeperotgoals = null;
	private Integer awayper1goals = null;
	private Integer awayper2goals = null;
	private Integer awayper3goals = null;
	private Integer awayperotgoals = null;
	private String awaytopscorer = null;
	private String awaytopscorergoals = null;
	private String awaytopscorerassists = null;
	private String awaytopscorerpoints = null;
	private String hometopscorer = null;
	private String hometopscorergoals = null;
	private String hometopscorerassists = null;
	private String hometopscorerpoints = null;
	private String awaytopgoalie = null;
	private String awaytopgoalieshots = null;
	private String awaytopgoaliesaves = null;
	private String awaytopgoaliepercentage = null;
	private String hometopgoalie = null;
	private String hometopgoalieshots = null;
	private String hometopgoaliesaves = null;
	private String hometopgoaliepercentage = null;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public void setScoresheeturl(String value){
		scoresheeturl=value;
	}
	
	public String getScoresheeturl(){
		return scoresheeturl;
	}
	
	public void setRenderboxscore(Boolean value){
		renderboxscore=value;
	}
	
	public Boolean getRenderboxscore(){
		return renderboxscore;
	}
	
	public void setIdlivegame(Integer value){
		idlivegame = value;
	}
	
	public Integer getIdlivegame(){
		return idlivegame;
	}
	
	public void setHometeam(String value){
		hometeam = value;
	}
	
	public String getHometeam(){
		return hometeam;
	}
	
	public void setAwayteam(String value){
		awayteam = value;
	}
	
	public String getAwayteam(){
		return awayteam;
	}
	
	public void setTypetag(String value){
		typetag = value;
	}
	
	public String getTypetag(){
		return typetag;
	}
	
	public void setLocation(String value){
		location = value;
	}
	
	public String getLocation(){
		return location;
	}
	
	public void setGametime(String value){
		gametime = value;
	}
	
	public String getGametime(){
		return gametime;
	}
	
	public void setHomescore(String value){
		homescore = value;
	}
	
	public String getHomescore(){
		return homescore;
	}
	
	public void setAwayscore(String value){
		awayscore = value;
	}
	
	public String getAwayscore(){
		return awayscore;
	}
	
	public void setStatus(String value){
		status = value;
	}
	
	public String getStatus(){
		return status;
	}
	
	public void setDisplaydivision(String value){
		displaydivision = value;
	}
	
	public String getDisplaydivision(){
		return displaydivision;
	}
	
	public void setHomeclubid(Integer value){
		homeclubid = value;
	}
	
	public Integer getHomeclubid(){
		return homeclubid;
	}
	
	public void setAwayclubid(Integer value){
		awayclubid = value;
	}
	
	public Integer getAwayclubid(){
		return awayclubid;
	}
	
	public void setAwaywins(Integer value){
		awaywins = value;
	}
	
	public Integer getAwaywins(){
		return awaywins;
	}
	
	public void setAwaylosses(Integer value){
		awaylosses = value;
	}
	
	public Integer getAwaylosses(){
		return awaylosses;
	}
	
	public void setAwayties(Integer value){
		awayties = value;
	}
	
	public Integer getAwayties(){
		return awayties;
	}
	
	public void setAwaypoints(Integer value){
		awaypoints = value;
	}
	
	public Integer getAwaypoints(){
		return awaypoints;
	}
	
	
	public void setHomewins(Integer value){
		homewins = value;
	}
	
	public Integer getHomewins(){
		return homewins;
	}
	
	public void setHomelosses(Integer value){
		homelosses = value;
	}
	
	public Integer getHomelosses(){
		return homelosses;
	}
	
	public void setHometies(Integer value){
		hometies = value;
	}
	
	public Integer getHometies(){
		return hometies;
	}
	
	public void setHomepoints(Integer value){
		homepoints = value;
	}
	
	public Integer getHomepoints(){
		return homepoints;
	}
	
	public void setRenderperiodtotals(Boolean value){
		renderperiodtotals = value;
	}
	
	public Boolean getRenderperiodtotals(){
		return renderperiodtotals;
	}
	
	public void setHomeper1goals(Integer value){
		homeper1goals = value;
	}
	
	public Integer getHomeper1goals(){
		return homeper1goals;
	}
	
	public void setHomeper2goals(Integer value){
		homeper2goals = value;
	}
	
	public Integer getHomeper2goals(){
		return homeper2goals;
	}
	
	public void setHomeper3goals(Integer value){
		homeper3goals = value;
	}
	
	public Integer getHomeper3goals(){
		return homeper3goals;
	}
	
	public void setHomeperotgoals(Integer value){
		homeperotgoals = value;
	}
	
	public Integer getHomeperotgoals(){
		return homeperotgoals;
	}
	
	public void setAwayper1goals(Integer value){
		awayper1goals = value;
	}
	
	public Integer getAwayper1goals(){
		return awayper1goals;
	}
	
	public void setAwayper2goals(Integer value){
		awayper2goals = value;
	}
	
	public Integer getAwayper2goals(){
		return awayper2goals;
	}
	
	public void setAwayper3goals(Integer value){
		awayper3goals = value;
	}
	
	public Integer getAwayper3goals(){
		return awayper3goals;
	}
	
	public void setAwayperotgoals(Integer value){
		awayperotgoals = value;
	}
	
	public Integer getAwayperotgoals(){
		return awayperotgoals;
	}
	
	public void setAwaytopscorer(String value){
		awaytopscorer = value;
	}
	
	public String getAwaytopscorer(){
		return awaytopscorer;
	}
	
	public void setAwaytopscorergoals(String value){
		awaytopscorergoals = value;
	}
	
	public String getAwaytopscorergoals(){
		return awaytopscorergoals;
	}
	
	public void setAwaytopscorerassists(String value){
		awaytopscorerassists = value;
	}
	
	public String getAwaytopscorerassists(){
		return awaytopscorerassists;
	}
	
	public void setAwaytopscorerpoints(String value){
		awaytopscorerpoints = value;
	}
	
	public String getAwaytopscorerpoints(){
		return awaytopscorerpoints;
	}
	
	public void setHometopscorer(String value){
		hometopscorer = value;
	}
	
	public String getHometopscorer(){
		return hometopscorer;
	}
	
	public void sethometopscorergoals(String value){
		hometopscorergoals = value;
	}
	
	public String getHometopscorergoals(){
		return hometopscorergoals;
	}
	
	public void setHometopscorerassists(String value){
		hometopscorerassists = value;
	}
	
	public String getHometopscorerassists(){
		return hometopscorerassists;
	}
	
	public void setHometopscorerpoints(String value){
		hometopscorerpoints = value;
	}
	
	public String getHometopscorerpoints(){
		return hometopscorerpoints;
	}
	
	public void setAwaytopgoalie(String value){
		awaytopgoalie = value;
	}
	
	public String getAwaytopgoalie(){
		return awaytopgoalie;
	}
	
	public void setAwaytopgoalieshots(String value){
		awaytopgoalieshots = value;
	}
	
	public String getAwaytopgoalieshots(){
		return awaytopgoalieshots;
	}
	
	public void setAwaytopgoaliesaves(String value){
		awaytopgoaliesaves = value;
	}
	
	public String getAwaytopgoaliesaves(){
		return awaytopgoaliesaves;
	}
	
	public void setAwaytopgoaliepercentage(String value){
		awaytopgoaliepercentage = value;
	}
	
	public String getAwaytopgoaliepercentage(){
		return awaytopgoaliepercentage;
	}
	
	public void setHometopgoalie(String value){
		hometopgoalie = value;
	}
	
	public String getHometopgoalie(){
		return hometopgoalie;
	}
	
	public void setHometopgoalieshots(String value){
		hometopgoalieshots = value;
	}
	
	public String getHometopgoalieshots(){
		return hometopgoalieshots;
	}
	
	public void setHometopgoaliesaves(String value){
		hometopgoaliesaves = value;
	}
	
	public String getHometopgoaliesaves(){
		return hometopgoaliesaves;
	}
	
	public void setHometopgoaliepercentage(String value){
		hometopgoaliepercentage = value;
	}
	
	public String getHometopgoaliepercentage(){
		return hometopgoaliepercentage;
	}
}
