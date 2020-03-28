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
public class SogList extends ListDataModel<Sog> implements Serializable, SelectableDataModel<Sog> {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// We need to implement a hashmap so we can directly fetch things we need.
	//
	private HashMap<String, Sog> hm = new HashMap<String,Sog>();
	
	public SogList(List<Sog> _lst,HashMap<String, Sog> _hm ) {  
		super(_lst);
		hm = _hm;
   }
	
	public static SogList NewListFactory() {
		return new SogList(new ArrayList<Sog>(),new HashMap<String,Sog>());
	}

	
	public static SogList NewListFactory(Profile _pro, ScahaDatabase _db, LiveGame _lg, ScahaTeam _tm, LiveGameRosterSpotList _rsl) throws SQLException {
		List<Sog> data = new ArrayList<Sog>();
		HashMap<String, Sog> hm = new HashMap<String,Sog>();
		PreparedStatement ps = _db.prepareStatement("call scaha.getSogs(?,?)");
		ps.setInt(1,_lg.ID);
		ps.setInt(2,_tm.ID);
		ResultSet rs = ps.executeQuery();
		int y=1;
		while (rs.next()) {
			int i = 1;
			Sog sog = new Sog(rs.getInt(i++),_pro, _lg, _tm);
			sog.setIdroster(rs.getInt(i++));
			sog.setRosterspot(_rsl.getByKey(sog.getIdroster()));
			sog.setShots1(rs.getInt(i++));
			sog.setShots2(rs.getInt(i++));
			sog.setShots3(rs.getInt(i++));
			sog.setShots4(rs.getInt(i++));
			sog.setShots5(rs.getInt(i++));
			sog.setShots6(rs.getInt(i++));
			sog.setShots7(rs.getInt(i++));
			sog.setShots8(rs.getInt(i++));
			sog.setShots9(rs.getInt(i++));
			
			String rawtime = rs.getString(i++);
			int left = rawtime.indexOf(":");
			sog.setPlaytime(rawtime.substring(left+1));
			
			String [] arrOfStr = rawtime.split(":");
			sog.setPlaymins(arrOfStr[1]);
			sog.setPlaysecs(arrOfStr[2]);
			
			data.add(sog);
			hm.put(sog.ID+"", sog);
			//LOGGER.info("Found a match " + sog);
		}
		rs.close();
		ps.close();
		return new SogList(data,hm);
		
	}



	@SuppressWarnings("unchecked")
	public ArrayList<Sog> getList() {
		return (ArrayList<Sog>)this.getWrappedData();
	}
	
	public Sog getByKey(int _i) {
		return hm.get(_i+"");
	}
	
	@SuppressWarnings("unchecked")
	public void reset() {
		// TODO Auto-generated method stub
		//LOGGER.info("resetting Participant List..");
		((ArrayList<Sog>)this.getWrappedData()).clear();
		hm.clear();
	}  
	
	@SuppressWarnings("unchecked")
	public void add(Sog _p) {
		//LOGGER.info("Adding Penalty to List:" + _p);
		((ArrayList<Sog>)this.getWrappedData()).add(_p);
		hm.put(_p.ID+"", _p);
	}
	
	public String toString() {
		String answer = "";
		@SuppressWarnings("unchecked")
		List<Sog> results = (List<Sog>) getWrappedData();  
		for(Sog result : results) {  
    	  answer = answer +  "SOG: " + result + ContextManager.NEW_LINE;
      }  
      
      return answer;
      
	}

    @Override  
    public Sog getRowData(String rowKey) {  
    	return this.hm.get(rowKey);
    }  
 
    public Sog getAt(int rowKey) {  
    	return this.hm.get(rowKey+"");
    }  
    @Override  
    public Object getRowKey(Sog result) {  
        return Integer.toString(result.ID);  
    }



}