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
public class LiveGameRosterSpotList extends ListDataModel<LiveGameRosterSpot> implements Serializable, SelectableDataModel<LiveGameRosterSpot> {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// We need to implement a hashmap so we can directly fetch things we need.
	//
	private HashMap<String, LiveGameRosterSpot> hm = new HashMap<String,LiveGameRosterSpot>();
	
	public LiveGameRosterSpotList(List<LiveGameRosterSpot> _lst,HashMap<String, LiveGameRosterSpot> _hm ) {  
		super(_lst);
		hm = _hm;
   }
	
	public static LiveGameRosterSpotList NewListFactory() {
		return new LiveGameRosterSpotList(new ArrayList<LiveGameRosterSpot>(),new HashMap<String,LiveGameRosterSpot>());
	}

	
	public static LiveGameRosterSpotList NewListFactory(Profile _pro, ScahaDatabase _db, LiveGame _lg, TeamList _tl, String _strHomeAway) throws SQLException {
		List<LiveGameRosterSpot> data = new ArrayList<LiveGameRosterSpot>();
		HashMap<String, LiveGameRosterSpot> hm = new HashMap<String,LiveGameRosterSpot>();
		PreparedStatement ps = _db.prepareStatement("call scaha.getLiveGameRosterSpots(?,?)");
		
		//
		// Do we have a live game?
		//
		if (_lg == null) return new LiveGameRosterSpotList(data,hm);
		
		ps.setInt(1,_lg.ID);
		ps.setString(2,_strHomeAway);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			LiveGameRosterSpot spot = new LiveGameRosterSpot(rs.getInt(i++),_pro);
			spot.setIdRoster(spot.ID);
			spot.setIdPerson(rs.getInt(i++));
			spot.setRostertype(rs.getString(i++));
			spot.setJerseynumber(rs.getString(i++));
			spot.setLname(rs.getString(i++));
			spot.setFname(rs.getString(i++));
			spot.setMia((rs.getInt(i++) == 1 ? true : false));
			spot.setTeam((_strHomeAway.equals("H") ? _lg.getHometeam() : _lg.getAwayteam()));
			spot.setLivegame(_lg);
			if (spot.getRostertype().equals("Player")) {
				spot.setTag("PL");
			} else if (spot.getRostertype().equals("Head Coach")) {
				spot.setTag("HC");
			} else if (spot.getRostertype().equals("Assistant Coach")) {
				spot.setTag("AC");
			} else if (spot.getRostertype().equals("Student Coach")) {
				spot.setTag("SC");
			} else if (spot.getRostertype().equals("Manager")) {
				spot.setTag("MN");
			} else if (spot.getRostertype().equals("Assistant Coach/Manager")) {
				spot.setTag("AC/MN");
			} else {
				spot.setTag("UN");
			}
			data.add(spot);
			hm.put(spot.ID+"", spot);
			//LOGGER.info("Found a match " + spot);
		}
		rs.close();
		ps.close();
		return new LiveGameRosterSpotList(data,hm);
		
	}

	public static LiveGameRosterSpotList NewListFactoryByJerseyNumber(Profile _pro, ScahaDatabase _db, LiveGame _lg, TeamList _tl, String _strHomeAway) throws SQLException {
		List<LiveGameRosterSpot> data = new ArrayList<LiveGameRosterSpot>();
		HashMap<String, LiveGameRosterSpot> hm = new HashMap<String,LiveGameRosterSpot>();
		PreparedStatement ps = _db.prepareStatement("call scaha.getLiveGameRosterSpotsByJerseyNumber(?,?)");
		
		//
		// Do we have a live game?
		//
		if (_lg == null) return new LiveGameRosterSpotList(data,hm);
		
		ps.setInt(1,_lg.ID);
		ps.setString(2,_strHomeAway);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			LiveGameRosterSpot spot = new LiveGameRosterSpot(rs.getInt(i++),_pro);
			spot.setIdRoster(spot.ID);
			spot.setIdPerson(rs.getInt(i++));
			spot.setRostertype(rs.getString(i++));
			spot.setJerseynumber(rs.getString(i++));
			spot.setLname(rs.getString(i++));
			spot.setFname(rs.getString(i++));
			spot.setMia((rs.getInt(i++) == 1 ? true : false));
			spot.setTeam((_strHomeAway.equals("H") ? _lg.getHometeam() : _lg.getAwayteam()));
			spot.setLivegame(_lg);
			if (spot.getRostertype().equals("Player")) {
				spot.setTag("PL");
			} else if (spot.getRostertype().equals("Head Coach")) {
				spot.setTag("HC");
			} else if (spot.getRostertype().equals("Assistant Coach")) {
				spot.setTag("AC");
			} else if (spot.getRostertype().equals("Student Coach")) {
				spot.setTag("SC");
			} else if (spot.getRostertype().equals("Manager")) {
				spot.setTag("MN");
			} else if (spot.getRostertype().equals("Assistant Coach/Manager")) {
				spot.setTag("AC/MN");
			} else {
				spot.setTag("UN");
			}
			data.add(spot);
			hm.put(spot.ID+"", spot);
			//LOGGER.info("Found a match " + spot);
		}
		rs.close();
		ps.close();
		return new LiveGameRosterSpotList(data,hm);
		
	}


	@SuppressWarnings("unchecked")
	public ArrayList<LiveGameRosterSpot> getLiveGameRosterSpotArray() {
		return (ArrayList<LiveGameRosterSpot>)this.getWrappedData();
	}
	
	public LiveGameRosterSpot getByKey(int _i) {
		return hm.get(_i+"");
	}
	
	@SuppressWarnings("unchecked")
	public void reset() {
		// TODO Auto-generated method stub
		//LOGGER.info("resetting Participant List..");
		((ArrayList<LiveGameRosterSpot>)this.getWrappedData()).clear();
		hm.clear();
	}  
	
	@SuppressWarnings("unchecked")
	public void add(LiveGameRosterSpot _p) {
		//LOGGER.info("Adding Participant to List:" + _p);
		((ArrayList<LiveGameRosterSpot>)this.getWrappedData()).add(_p);
		hm.put(_p.ID+"", _p);
	}
	
	public String toString() {
		String answer = "";
		@SuppressWarnings("unchecked")
		List<LiveGameRosterSpot> results = (List<LiveGameRosterSpot>) getWrappedData();  
      for(LiveGameRosterSpot result : results) {  
    	  answer = answer +  "LiveGame: " + result + ContextManager.NEW_LINE;
      }  
      
      return answer;
      
	}

    @Override  
    public LiveGameRosterSpot getRowData(String rowKey) {  
    	return this.hm.get(rowKey);
    }  
 
    public LiveGameRosterSpot getLiveGameRosterSpotAtAt(int rowKey) {  
    	return this.hm.get(rowKey+"");
    }  
    @Override  
    public Object getRowKey(LiveGameRosterSpot result) {  
        return Integer.toString(result.ID);  
    }



}