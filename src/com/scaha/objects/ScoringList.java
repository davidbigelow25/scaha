package com.scaha.objects;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
* This holds all the smarts for a given Schedule.  There are many combinations of schedules between teams and seasons
* In this system
* @author David
*
*/
public class ScoringList extends ListDataModel<Scoring> implements Serializable, SelectableDataModel<Scoring> {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// We need to implement a hashmap so we can directly fetch things we need.
	//
	private HashMap<String, Scoring> hm = new HashMap<String,Scoring>();
	
	public ScoringList(List<Scoring> _lst,HashMap<String, Scoring> _hm ) {  
		super(_lst);
		hm = _hm;
   }
	
	public static ScoringList NewListFactory() {
		return new ScoringList(new ArrayList<Scoring>(),new HashMap<String,Scoring>());
	}

	
	public static ScoringList NewListFactory(Profile _pro, ScahaDatabase _db, LiveGame _lg, ScahaTeam _tm, LiveGameRosterSpotList _rsl) throws SQLException {
		List<Scoring> data = new ArrayList<Scoring>();
		HashMap<String, Scoring> hm = new HashMap<String,Scoring>();
		PreparedStatement ps = _db.prepareStatement("call scaha.getScoring(?,?)");
		ps.setInt(1,_lg.ID);
		ps.setInt(2,_tm.ID);
		ResultSet rs = ps.executeQuery();
		int y=1;
		while (rs.next()) {
			int i = 1;

			Scoring score = new Scoring(rs.getInt(i++),_pro, _lg, _tm);
			
			//this is used for goals without an assigned player
			LiveGameRosterSpot lgrs = new LiveGameRosterSpot(0, _pro);
			
			score.setCount(y++);
			score.setIdrostergoal(rs.getInt(i++));
			if (score.getIdrostergoal()==0){
				score.setLgrosterspotgoal(lgrs);
			}else {
				score.setLgrosterspotgoal(_rsl.getByKey(score.getIdrostergoal()));
			}
			score.setIdrostera1(rs.getInt(i++));
			if (score.getIdrostera1()==0){
				score.setLgrosterspota1(lgrs);
			}else {
				score.setLgrosterspota1(_rsl.getByKey(score.getIdrostera1()));
			}
			score.setIdrostera2(rs.getInt(i++));
			if (score.getIdrostera2()==0){
				score.setLgrosterspota2(lgrs);
			}else {
				score.setLgrosterspota2(_rsl.getByKey(score.getIdrostera2()));
			}
			
			score.setGoaltype(rs.getString(i++));
			score.setPeriod(rs.getInt(i++));
			String rawtime = rs.getString(i++);
			int left = rawtime.indexOf(":");
			score.setTimescored(rawtime.substring(left+1));
			
			String [] arrOfStr = rawtime.split(":");
			score.setGoalmin(arrOfStr[1]);
			score.setGoalsec(arrOfStr[2]);
			data.add(score);
			hm.put(score.ID+"", score);
			//LOGGER.info("Found a match " + score);
		}
		rs.close();
		ps.close();
		return new ScoringList(data,hm);
		
	}



	@SuppressWarnings("unchecked")
	public ArrayList<Scoring> getLiveGameRosterSpotArray() {
		return (ArrayList<Scoring>)this.getWrappedData();
	}
	
	public Scoring getByKey(int _i) {
		return hm.get(_i+"");
	}
	
	@SuppressWarnings("unchecked")
	public void reset() {
		// TODO Auto-generated method stub
		//LOGGER.info("resetting Participant List..");
		((ArrayList<Scoring>)this.getWrappedData()).clear();
		hm.clear();
	}  
	
	@SuppressWarnings("unchecked")
	public void add(Scoring _p) {
		//LOGGER.info("Adding Participant to List:" + _p);
		((ArrayList<Scoring>)this.getWrappedData()).add(_p);
		hm.put(_p.ID+"", _p);
	}
	
	public String toString() {
		String answer = "";
		@SuppressWarnings("unchecked")
		List<Scoring> results = (List<Scoring>) getWrappedData();  
		for(Scoring result : results) {  
    	  answer = answer +  "Scoring: " + result + ContextManager.NEW_LINE;
      }  
      
      return answer;
      
	}

    @Override  
    public Scoring getRowData(String rowKey) {  
    	return this.hm.get(rowKey);
    }  
 
    public Scoring getScoringAt(int rowKey) {  
    	return this.hm.get(rowKey+"");
    }  
    @Override  
    public Object getRowKey(Scoring result) {  
        return Integer.toString(result.ID);  
    }



}