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
public class PenaltyList extends ListDataModel<Penalty> implements Serializable, SelectableDataModel<Penalty> {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// We need to implement a hashmap so we can directly fetch things we need.
	//
	private HashMap<String, Penalty> hm = new HashMap<String,Penalty>();
	
	public PenaltyList(List<Penalty> _lst,HashMap<String, Penalty> _hm ) {  
		super(_lst);
		hm = _hm;
   }
	
	public static PenaltyList NewListFactory() {
		return new PenaltyList(new ArrayList<Penalty>(),new HashMap<String,Penalty>());
	}

	
	public static PenaltyList NewListFactory(Profile _pro, ScahaDatabase _db, LiveGame _lg, ScahaTeam _tm, LiveGameRosterSpotList _rsl) throws SQLException {
		List<Penalty> data = new ArrayList<Penalty>();
		HashMap<String, Penalty> hm = new HashMap<String,Penalty>();
		PreparedStatement ps = _db.prepareStatement("call scaha.getPenalties(?,?)");
		ps.setInt(1,_lg.ID);
		ps.setInt(2,_tm.ID);
		ResultSet rs = ps.executeQuery();
		int y=1;
		while (rs.next()) {
			int i = 1;
			Penalty pen = new Penalty(rs.getInt(i++),_pro, _lg, _tm);
			pen.setCount(y++);
			pen.setIdroster(rs.getInt(i++));
			pen.setRosterspot(_rsl.getByKey(pen.getIdroster()));
			pen.setPeriod(rs.getInt(i++));
			pen.setPenaltytype(rs.getString(i++));
			pen.setMinutes(rs.getString(i++));
			String rawtime = rs.getString(i++);
			int left = rawtime.indexOf(":");
			pen.setTimeofpenalty(rawtime.substring(left+1));
			String [] arrOfStr = rawtime.split(":");
			pen.setPenmin(arrOfStr[1]);
			pen.setPensec(arrOfStr[2]);
			
			data.add(pen);
			hm.put(pen.ID+"", pen);
			//LOGGER.info("Found a match " + pen);
		}
		rs.close();
		ps.close();
		return new PenaltyList(data,hm);
		
	}



	@SuppressWarnings("unchecked")
	public ArrayList<Penalty> getList() {
		return (ArrayList<Penalty>)this.getWrappedData();
	}
	
	public Penalty getByKey(int _i) {
		return hm.get(_i+"");
	}
	
	@SuppressWarnings("unchecked")
	public void reset() {
		// TODO Auto-generated method stub
		//LOGGER.info("resetting Participant List..");
		((ArrayList<Penalty>)this.getWrappedData()).clear();
		hm.clear();
	}  
	
	@SuppressWarnings("unchecked")
	public void add(Penalty _p) {
		//LOGGER.info("Adding Penalty to List:" + _p);
		((ArrayList<Penalty>)this.getWrappedData()).add(_p);
		hm.put(_p.ID+"", _p);
	}
	
	public String toString() {
		String answer = "";
		@SuppressWarnings("unchecked")
		List<Penalty> results = (List<Penalty>) getWrappedData();  
		for(Penalty result : results) {  
    	  answer = answer +  "Penalty: " + result + ContextManager.NEW_LINE;
      }  
      
      return answer;
      
	}

    @Override  
    public Penalty getRowData(String rowKey) {  
    	return this.hm.get(rowKey);
    }  
 
    public Penalty getAt(int rowKey) {  
    	return this.hm.get(rowKey+"");
    }  
    @Override  
    public Object getRowKey(Penalty result) {  
        return Integer.toString(result.ID);  
    }



}