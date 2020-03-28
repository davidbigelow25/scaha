package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * Ok.. This is a great way for 
 * @author David
 *
 */
public class StatsList extends ListDataModel<Stat> implements Serializable, SelectableDataModel<StatsList> {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private List<Stats> gaaleaders = null;
	private List<Stats> savepercentageleaders = null;
	private List<Stats> pointsleaders = null;
	private List<Stats> assistsleaders = null;
	private List<Stats> goalsleaders = null;
	private Integer ID = null;
	
	public StatsList() {  
    }  
  
    public StatsList(List<Stat> data) {  
        super(data);  
    }  
    
    /**
  	 * This will get an empty StatsList
  	 * @param _db
  	 * @return
  	 * @throws SQLException 
  	 */
  	public static StatsList ListFactory() throws SQLException {
  		
  		List<Stat> data = new ArrayList<Stat>();
  		return new StatsList(data);
  	}
  	
  	
  	
  	
    /**
	 * This will get all stats including leaders and general lists for all years
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public StatsList NewStatListFactory(ScahaDatabase _db) throws SQLException {
		
		List<Stat> tempresult = new ArrayList<Stat>();
    	List<Stat> tempgoals = new ArrayList<Stat>();
    	List<Stat> tempassists = new ArrayList<Stat>();
    	List<Stat> tempgaa = new ArrayList<Stat>();
    	List<Stat> tempsave = new ArrayList<Stat>();
    	
    	List<Stats> templistpoints = new ArrayList<Stats>();
    	List<Stats> templistgoals = new ArrayList<Stats>();
    	List<Stats> templistassists = new ArrayList<Stats>();
    	List<Stats> templistgaa = new ArrayList<Stats>();
    	List<Stats> templistsave = new ArrayList<Stats>();
    	
    	
    	//lets load leaders for division and year
    	CallableStatement cs = _db.prepareCall("CALL scaha.getDivisionYearforStats()");
    	CallableStatement cs2 = _db.prepareCall("CALL scaha.getGoalleaders(?,?)");
    	CallableStatement cs3 = _db.prepareCall("CALL scaha.getPointsleaders(?,?)");
    	CallableStatement cs4 = _db.prepareCall("CALL scaha.getAssistsleaders(?,?)");
    	CallableStatement cs5 = _db.prepareCall("CALL scaha.getGaaleaders(?,?)");
    	CallableStatement cs6 = _db.prepareCall("CALL scaha.getSavepercentageleaders(?,?)");
    	ResultSet rs = cs.executeQuery();
    	String sdivision = "";
    	String syear = "";
    	if (rs != null){
			
    		
			while (rs.next()) {
				//lets create the temporary container for 
				Stats tempstats = new Stats();
				
				sdivision = rs.getString("division");
				syear = rs.getString("year");
				tempstats.setDivision(sdivision);
				tempstats.setYear(syear);
				
				
				//lets get goal leaders first
				
				cs2.setInt("division", Integer.parseInt(sdivision));
				cs2.setInt("inyear", Integer.parseInt(syear));
				ResultSet rs2 = cs2.executeQuery();
				Integer count = 1;
		
				if (rs2 != null){
					while (rs2.next()) {
						Integer rank = count++;
						String playername = rs2.getString("playername");
						String teamname = rs2.getString("teamname");
						String goals = rs2.getString("goals");
						
						Stat stat = new Stat();
						stat.setRank(rank.toString());
						stat.setPlayername(playername);
						stat.setTeamname(teamname);
						stat.setGoals(goals);
						tempgoals.add(stat);
						
					}
					//LOGGER.info("We have results for goal leaders:" + sdivision + ":" + syear);
					rs2.close();
				}
				
				tempstats.setStatsyearlist(tempgoals);
				
				//now lets add the stats object to the stats list object
				templistgoals.add(tempstats);
				
				
				//now lets get points leaders
				tempstats.clear();
				cs3.setInt("division", Integer.parseInt(sdivision));
				cs3.setInt("inyear", Integer.parseInt(syear));
				rs2 = cs3.executeQuery();
				count = 1;
				
				if (rs2 != null){
					
					while (rs2.next()) {
						Integer rank = count++;
						String playername = rs2.getString("playername");
						String teamname = rs2.getString("teamname");
						String points = rs2.getString("points");
						
						Stat stat = new Stat();
						stat.setRank(rank.toString());
						stat.setPlayername(playername);
						stat.setTeamname(teamname);
						stat.setPoints(points);
						tempresult.add(stat);
						
					}
					//LOGGER.info("We have results for points leaders:" + sdivision + ":" + syear);
					
					
				}
				rs2.close();
				
				
				tempstats.setStatsyearlist(tempresult);
				
				//now lets add the stats object to the stats list object
				templistpoints.add(tempstats);
				
				
				
				//get assists leaders
				tempstats.clear();
				cs4.setInt("division", Integer.parseInt(sdivision));
				cs4.setInt("inyear", Integer.parseInt(syear));
				rs2 = cs4.executeQuery();
				count = 1;
				
				if (rs2 != null){
					
					while (rs2.next()) {
						Integer rank = count++;
						String playername = rs2.getString("playername");
						String teamname = rs2.getString("teamname");
						String assists = rs2.getString("assists");
						
						Stat stat = new Stat();
						stat.setRank(rank.toString());
						stat.setPlayername(playername);
						stat.setTeamname(teamname);
						stat.setAssists(assists);
						tempassists.add(stat);
						
					}
					//LOGGER.info("We have results for assists leaders:" + sdivision + ":" + syear);
					
				}
				
				rs2.close();
				tempstats.setStatsyearlist(tempassists);
				
				//now lets add the stats object to the stats list object
				templistassists.add(tempstats);
								
		
				//get gaa leaders
				tempstats.clear();
				cs5.setInt("division", Integer.parseInt(sdivision));
				cs5.setInt("inyear", Integer.parseInt(syear));
				rs2 = cs5.executeQuery();
				count = 1;
				
				if (rs2 != null){
					
					while (rs2.next()) {
						Integer rank = count++;
						String playername = rs2.getString("playername");
						String teamname = rs2.getString("teamname");
						String gaa = rs2.getString("gaa");
						if (gaa==null){
							gaa = "0.00";
						}
						Stat stat = new Stat();
						stat.setRank(rank.toString());
						stat.setPlayername(playername);
						stat.setTeamname(teamname);
						stat.setGaa(gaa);
						tempgaa.add(stat);
						
					}
					//LOGGER.info("We have results for gaa leaders:" + sdivision + ":" + syear);
					
					
				}
				rs2.close();
				tempstats.setStatsyearlist(tempgaa);
				
				//now lets add the stats object to the stats list object
				templistgaa.add(tempstats);
				
				
				
				//get sv % leaders
				tempstats.clear();
				cs6.setInt("division", Integer.parseInt(sdivision));
				cs6.setInt("inyear", Integer.parseInt(syear));
				rs2 = cs6.executeQuery();
				count = 1;
				
				if (rs2 != null){
					
					while (rs2.next()) {
						Integer rank = count++;
						String playername = rs2.getString("playername");
						String teamname = rs2.getString("teamname");
						String savepercentage = rs2.getString("savepercentage");
						
						Stat stat = new Stat();
						stat.setRank(rank.toString());
						stat.setPlayername(playername);
						stat.setTeamname(teamname);
						stat.setSavepercentage(savepercentage);
						tempsave.add(stat);
						
					}
					//LOGGER.info("We have results for save percentage leaders:" + sdivision + ":" + syear);
					
				}
				
				rs2.close();
				tempstats.setStatsyearlist(tempsave);
				
				//now lets add the stats object to the stats list object
				templistsave.add(tempstats);
				
				
			}
			
			
    	}
    	
    	this.setGoalsleaders(templistgoals);
    	this.setPointsleaders(templistpoints);
    	this.setAssistsleaders(templistassists);
    	this.setGaaleaders(templistgaa);
    	this.setSavepercentageleaders(templistsave);
    	
    	rs.close();
		cs.close();
		cs2.close();
		cs3.close();
		cs4.close();
		cs5.close();
		cs6.close();
		
		return this;
	}

      
	public void setGoalsleaders(List<Stats> value){
  		goalsleaders=value;
  	}
  	
  	public List<Stats> getGoalsleaders(){
  		return goalsleaders;
  	}
  	
  	
  	public void setPointsleaders(List<Stats> value){
  		pointsleaders=value;
  	}
  	
  	public List<Stats> getPointsleaders(){
  		return pointsleaders;
  	}
	
  	public void setAssistsleaders(List<Stats> value){
  		assistsleaders=value;
  	}
  	
  	public List<Stats> getAssistsleaders(){
  		return assistsleaders;
  	}
  	
  	public void setGaaleaders(List<Stats> value){
  		gaaleaders=value;
  	}
  	
  	public List<Stats> getGaaleaders(){
  		return gaaleaders;
  	}
  	
  	public void setSavepercentageleaders(List<Stats> value){
  		savepercentageleaders=value;
  	}
  	
  	public List<Stats> getSavepercentageleaders(){
  		return savepercentageleaders;
  	}
  	
	@Override  
    public StatsList getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<StatsList> results = (List<StatsList>) getWrappedData();  
        for(StatsList result : results) {  
        	if(Integer.toString(result.ID).equals(rowKey)) return result;  
        }  
          
        return null;  
    }  
 
    public StatsList getScahaTeamAt(int rowKey) {  
          
        @SuppressWarnings("unchecked")
		List<StatsList> results = (List<StatsList>) getWrappedData();  
        for(StatsList result : results) {  
        	if(result.ID == rowKey) return result;  
        }  
          
        return null;  
    }  
    @Override  
    public Object getRowKey(StatsList result) {  
        return Integer.toString(result.ID);  
    }

	public void setId(Integer value){
		ID=value;
	}
	
	public Integer getId(){
		return ID;
	}
	
}