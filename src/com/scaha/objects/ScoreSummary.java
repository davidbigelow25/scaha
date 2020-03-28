package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class ScoreSummary extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer homescore;
	private Integer awayscore;
	private String teamname;
	private String goaltime;
	private String goalscorer;
	private String assist1;
	private String assist2;
	private String goaltype;
	private Integer period;
	private String goalscorerpersonid;
	private String assist1personid;
	private String assist2personid;
	
	public void setHomescore(Integer value){
		homescore = value;
	}
	
	public Integer getHomescore(){
		return homescore;
	}
	
	public void setAwayscore(Integer value){
		awayscore = value;
	}
	
	public Integer getAwayscore(){
		return awayscore;
	}
	
	public void setGoalscorerpersonid(String value){
		goalscorerpersonid = value;
	}
	
	public String getGoalscorerpersonid(){
		return goalscorerpersonid;
	}
	
	public void setAssist1personid(String value){
		assist1personid = value;
	}
	
	public String getAssist1personid(){
		return assist1personid;
	}
	
	public void setAssist2personid(String value){
		assist2personid = value;
	}
	
	public String getAssist2personid(){
		return assist2personid;
	}
	
	public void setTeamname(String value){
		teamname = value;
	}
	
	public String getTeamname(){
		return teamname;
	}
	
	public void setGoaltime(String value){
		goaltime = value;
	}
	
	public String getGoaltime(){
		return goaltime;
	}
	
	public void setGoalscorer(String value){
		goalscorer = value;
	}
	
	public String getGoalscorer(){
		return goalscorer;
	}
	
	public void setAssist1(String value){
		assist1 = value;
	}
	
	public String getAssist1(){
		return assist1;
	}
	
	public void setAssist2(String value){
		assist2 = value;
	}
	
	public String getAssist2(){
		return assist2;
	}
	
	public void setGoaltype(String value){
		goaltype = value;
	}
	
	public String getGoaltype(){
		return goaltype;
	}
	
	public void setPeriod(Integer value){
		period = value;
	}
	
	public Integer getPeriod(){
		return period;
	}
}
