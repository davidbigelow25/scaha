package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Score extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private Integer period1goals;
	private Integer period2goals;
	private Integer period3goals;
	private Integer periodOTgoals;
	private Integer totalgoals;
	
	private Integer period1shots;
	private Integer period2shots;
	private Integer period3shots;
	private Integer period4shots;
	private Integer totalshots;
	
	private Integer goodpowerplays;
	private Integer totalpowerplays;
	private String powerplaypercentage;
	private String teamname;
	
	public void setPeriod1goals(Integer value){
		period1goals = value;
	}
	
	public Integer getPeriod1goals(){
		return period1goals;
	}
	
	public void setPeriod2goals(Integer value){
		period2goals = value;
	}
	
	public Integer getPeriod2goals(){
		return period2goals;
	}
	
	public void setPeriod3goals(Integer value){
		period3goals = value;
	}
	
	public Integer getPeriod3goals(){
		return period3goals;
	}
	
	public void setPeriodOTgoals(Integer value){
		periodOTgoals = value;
	}
	
	public Integer getPeriodOTgoals(){
		return periodOTgoals;
	}
	
	public void setTotalgoals(Integer value){
		totalgoals = value;
	}
	
	public Integer getTotalgoals(){
		return totalgoals;
	}
	
	public void setPeriod1shots(Integer value){
		period1shots = value;
	}
	
	public Integer getPeriod1shots(){
		return period1shots;
	}
	
	public void setPeriod2shots(Integer value){
		period2shots = value;
	}
	
	public Integer getPeriod2shots(){
		return period2shots;
	}
	
	public void setPeriod3shots(Integer value){
		period3shots = value;
	}
	
	public Integer getPeriod3shots(){
		return period3shots;
	}
	
	public void setPeriod4shots(Integer value){
		period4shots = value;
	}
	
	public Integer getPeriod4shots(){
		return period4shots;
	}
	
	public void setTotalshots(Integer value){
		totalshots = value;
	}
	
	public Integer getTotalshots(){
		return totalshots;
	}
	
	public void setGoodpowerplays(Integer value){
		goodpowerplays = value;
	}
	
	public Integer getGoodpowerplays(){
		return goodpowerplays;
	}
	
	public void setTotalpowerplays(Integer value){
		totalpowerplays = value;
	}
	
	public Integer getTotalpowerplays(){
		return totalpowerplays;
	}
	
	public void setPowerplaypercentage(String value){
		powerplaypercentage = value;
	}
	
	public String getPowerplaypercentage(){
		return powerplaypercentage;
	}
	
	public void setTeamname(String value){
		teamname = value;
	}
	
	public String getTeamname(){
		return teamname;
	}
}
