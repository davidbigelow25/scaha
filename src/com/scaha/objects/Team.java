package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.sun.org.apache.xpath.internal.operations.Div;

public class Team extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	public String teamname = null;
	private String IDteam = null;
	private String skillname = null;
	private String division_name = null;
	private String activeplayercount = null;
	private String totalplayercount = null;
	private String totalcoachescount = null;
	private String gmcount = null;
	private String gp = null;
	private String minorpenalties = null;
	private String majorpenalties = null;
	private String pims = null;
	private String matchcount = null;
	private String newdate = null;
	private String confirmdate = null;

	//these are used for counts for team pdr page
	private Integer playercount = 0;
	private String pdrapply = null;
	private String pdr = null;
	private Integer pdrcount = null;
	private String blockrecruitment = null;
	private String blockrecruitmentteam = null;

	//these are used for displaying tournament attendance
	private String labordayattend = null;
	private String tgivingattend = null;
	private String xmasattend = null;
	private String mlkattend = null;
	private String pdayattend = null;
	private String byewkdattend = null;
	private Boolean laborday = null;
	private Boolean tgiving = null;
	private Boolean xmas = null;
	private Boolean mlk = null;
	private Boolean pday = null;
	private Boolean byewkd = null;

	//this is used for assigning coach role to teams on the coach loi process.
	private String coachrole = null;

	//this is used for displaying count of players on a team that have passed the bullying requirement
	private Integer bullyingcount=null;

	public Team (String sName, String teamid){ 
		teamname = sName;
		IDteam = teamid;
	}

	public String getConfirmdate() {
		return confirmdate;
	}

	public void setConfirmdate(String confirmdate) {
		this.confirmdate = confirmdate;
	}

	public Integer getPlayercount(){
		return playercount;
	}

	public void setPlayercount(Integer sName){
		playercount = sName;
	}

	public String getPdrapply(){
		return pdrapply;
	}

	public void setPdrapply(String sName){
		pdrapply = sName;
	}

	public String getPdr(){
		return pdr;
	}

	public void setPdr(String sName){
		pdr = sName;
	}

	public Integer getPdrcount(){
		return pdrcount;
	}

	public void setPdrcount(Integer sName){
		pdrcount = sName;
	}

	public String getBlockrecruitment(){
		return blockrecruitment;
	}

	public void setBlockrecruitment(String sName){
		blockrecruitment = sName;
	}

	public String getBlockrecruitmentteam(){
		return blockrecruitmentteam;
	}

	public void setBlockrecruitmentteam(String sName){
		blockrecruitmentteam = sName;
	}


	public String getNewdate(){
		return newdate;
	}
	
	public void setNewdate(String sName){
		newdate = sName;
	}
	
	
	public String getMatchcount(){
		return matchcount;
	}
	
	public void setMatchcount(String sName){
		matchcount = sName;
	}
	
	public String getPims(){
		return pims;
	}
	
	public void setPims(String sName){
		pims = sName;
	}
	
	public String getMajorpenalties(){
		return majorpenalties;
	}
	
	public void setMajorpenalties(String sName){
		majorpenalties = sName;
	}
	
	public String getMinorpenalties(){
		return minorpenalties;
	}
	
	public void setMinorpenalties(String sName){
		minorpenalties = sName;
	}
	
	public String getGp(){
		return gp;
	}
	
	public void setGp(String sName){
		gp = sName;
	}
	
	public String getGmcount(){
		return gmcount;
	}
	
	public void setGmcount(String sName){
		gmcount = sName;
	}
	
	public String getCoachrole(){
		return coachrole;
	}
	
	public void setCoachrole(String sName){
		coachrole = sName;
	}
	
	
	public String getTotalcoachescount(){
		return totalcoachescount;
	}
	
	public void setTotalcoachescount(String sName){
		totalcoachescount = sName;
	}
	
	
	public String getTotalplayercount(){
		return totalplayercount;
	}
	
	public void setTotalplayercount(String sName){
		totalplayercount = sName;
	}
	
	public String getActiveplayercount(){
		return activeplayercount;
	}
	
	public void setActiveplayercount(String sName){
		activeplayercount = sName;
	}
	
	
	public String getSkillname(){
		return skillname;
	}
	
	public void setSkillname(String sName){
		skillname = sName;
	}
	
	
	
	public String getDivisionname(){
		return division_name;
	}
	
	public void setDivisionname(String sName){
		division_name = sName;
	}
	
	
	public String getTeamname(){
		return teamname;
	}
	
	public void setTeamname(String sName){
		teamname = sName;
	}
	
	public String getIdteam(){
		return IDteam;
	}
	
	public void setIdteam(String steamid){
		IDteam = steamid;
	}

	public String getLabordayattend() {
		return labordayattend;
	}

	public void setLabordayattend(String labordayattend) {
		this.labordayattend = labordayattend;
	}

	public String getTgivingattend() {
		return tgivingattend;
	}

	public void setTgivingattend(String tgivingattend) {
		this.tgivingattend = tgivingattend;
	}

	public String getXmasattend() {
		return xmasattend;
	}

	public void setXmasattend(String xmasattend) {
		this.xmasattend = xmasattend;
	}

	public String getMlkattend() {
		return mlkattend;
	}

	public void setMlkattend(String mlkattend) {
		this.mlkattend = mlkattend;
	}

	public String getPdayattend() {
		return pdayattend;
	}

	public void setPdayattend(String pdayattend) {
		this.pdayattend = pdayattend;
	}

	public String getByewkdattend() {
		return byewkdattend;
	}

	public void setByewkdattend(String byewkdattend) {
		this.byewkdattend = byewkdattend;
	}

	public Boolean getLaborday() {
		return laborday;
	}

	public void setLaborday(Boolean laborday) {
		this.laborday = laborday;
	}

	public Boolean getTgiving() {
		return tgiving;
	}

	public void setTgiving(Boolean tgiving) {
		this.tgiving = tgiving;
	}

	public Boolean getXmas() {
		return xmas;
	}

	public void setXmas(Boolean xmas) {
		this.xmas = xmas;
	}

	public Boolean getMlk() {
		return mlk;
	}

	public void setMlk(Boolean mlk) {
		this.mlk = mlk;
	}

	public Boolean getPday() {
		return pday;
	}

	public void setPday(Boolean pday) {
		this.pday = pday;
	}

	public Boolean getByewkd() {
		return byewkd;
	}

	public void setByewkd(Boolean byewkd) {
		this.byewkd = byewkd;
	}

	public Integer getBullyingcount() {
		return bullyingcount;
	}

	public void setBullyingcount(Integer bullyingcount) {
		this.bullyingcount = bullyingcount;
	}
}
