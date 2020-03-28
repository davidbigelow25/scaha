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
public class LiveGameList extends ListDataModel<LiveGame> implements Serializable, SelectableDataModel<LiveGame> {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// We need to implement a hashmap so we can directly fetch things we need.
	//
	private HashMap<String, LiveGame> hm = new HashMap<String,LiveGame>();
	
	public LiveGameList(List<LiveGame> _lst,HashMap<String, LiveGame> _hm ) {  
		super(_lst);
		hm = _hm;
   }
	
	public LiveGameList(List<LiveGame> _lst) {  
		super(_lst);
	}
	
	public static LiveGameList NewListFactory() {
		return new LiveGameList(new ArrayList<LiveGame>(),new HashMap<String,LiveGame>());
	}

	
	public static LiveGameList NewListFactory(Profile _pro, ScahaDatabase _db, GeneralSeason currentSeason,TeamList _tl, ScheduleList _schl) throws SQLException {
		List<LiveGame> data = new ArrayList<LiveGame>();
		HashMap<String, LiveGame> hm = new HashMap<String,LiveGame>();
		//LOGGER.info("NewList is looking for LiveGames that match " + currentSeason.getTag());
		PreparedStatement ps = _db.prepareStatement("call scaha.getLiveGamesBySeason(?)");
		ps.setString(1,currentSeason.getTag());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			//LOGGER.info("Livegame:"+rs.getInt(i));
			LiveGame live = new LiveGame(rs.getInt(i++),_pro);
			
			//LOGGER.info("Found a live.." + live.ID);
			live.setTypetag(rs.getString(i++));
			live.setStatetag(rs.getString(i++));
			live.setHometeam(_tl.getScahaTeamAt(rs.getInt(i++)));
			live.setHometeamname(rs.getString(i++));
			live.setHomescore(rs.getInt(i++));
			live.setAwayteam(_tl.getScahaTeamAt(rs.getInt(i++)));
			live.setAwayteamname(rs.getString(i++));
			live.setAwayscore(rs.getInt(i++));
			live.setVenuetag(rs.getString(i++));
			live.setSheetname(rs.getString(i++));
			live.setStartdate(rs.getString(i++));
			live.setStarttime(rs.getString(i++));
			live.setScheduleidstub(rs.getInt(i++));
			live.setGamenotes(rs.getString(i++));
			live.setVenueshortname(rs.getString(i++));
			live.setHomestatsstatus(rs.getString(i++));
			live.setAwaystatsstatus(rs.getString(i++));
			live.setSched(_schl.getRowData(live.getScheduleidstub()+""));
			live.setHomeclubid(live.getHometeam().getTeamClub().ID);
			live.setAwayclubid(live.getAwayteam().getTeamClub().ID);
			live.setHostteam(rs.getBoolean("hostteam"));
			data.add(live);
			hm.put(live.ID+"", live);
		}
		rs.close();
		ps.close();
		//LOGGER.info("NewList is done looking for LiveGames that match " + currentSeason + ". datalen=" + data.size());
		return new LiveGameList(data,hm);
		
	}

	//use this method to retrieve historical game schedules and not hte current one which is loaded in memory when application is started.
	public static LiveGameList NewListFactory(ScahaDatabase _db, Integer selectedschedule) throws SQLException {
		List<LiveGame> data = new ArrayList<LiveGame>();
		//LOGGER.info("NewList is looking for LiveGames that match " + selectedschedule.toString());
		PreparedStatement ps = _db.prepareStatement("call scaha.getHistoricalLiveGamesBySeason(?)");
		ps.setInt(1,selectedschedule);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			//LOGGER.info("Found a row..");
			LiveGame live = new LiveGame(rs.getInt(i++));
			//LOGGER.info("Found a live.." + live);
			live.setTypetag(rs.getString(i++));
			live.setStatetag(rs.getString(i++));
			
			//need to set scahateam objects
			ScahaTeam home = new ScahaTeam(rs.getInt(i++));
			home.setTeamname(rs.getString(i++));
			live.setHometeam(home);
			
			//live.setHometeamname(rs.getString(i++));
			live.setHomescore(rs.getInt(i++));
			
			//need to set scahateam objects
			ScahaTeam away = new ScahaTeam(rs.getInt(i++));
			away.setTeamname(rs.getString(i++));
			
			live.setAwayteam(away);
			//live.setAwayteamname(rs.getString(i++));
			live.setAwayscore(rs.getInt(i++));
			live.setVenuetag(rs.getString(i++));
			live.setSheetname(rs.getString(i++));
			live.setStartdate(rs.getString(i++));
			live.setStarttime(rs.getString(i++));
			live.setScheduleidstub(rs.getInt(i++));
			live.setGamenotes(rs.getString(i++));
			live.setVenueshortname(rs.getString(i++));
			live.setSched(null);
			data.add(live);
		//	LOGGER.info("Found a match " + live);
		}
		rs.close();
		ps.close();
		//LOGGER.info("NewList is done looking for LiveGames that match " + selectedschedule + ". datalen=" + data.size());
		return new LiveGameList(data);
		
	}
	
	
	//use this interface to get the historical schedule by team.
	public static LiveGameList NewListFactory(ScahaDatabase _db, Integer selectedschedule, Integer selectedparticipant) throws SQLException {
		List<LiveGame> data = new ArrayList<LiveGame>();
		//LOGGER.info("NewList is looking for LiveGames that match " + selectedschedule.toString());
		PreparedStatement ps = _db.prepareStatement("call scaha.getHistoricalLiveGamesByScheduleAndTeam(?,?)");
		ps.setInt(1,selectedschedule);
		ps.setInt(2, selectedparticipant);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
		//	LOGGER.info("Found a row..");
			LiveGame live = new LiveGame(rs.getInt(i++));
		//	LOGGER.info("Found a live.." + live);
			live.setTypetag(rs.getString(i++));
			live.setStatetag(rs.getString(i++));
			
			//need to set scahateam objects
			ScahaTeam home = new ScahaTeam(rs.getInt(i++));
			home.setTeamname(rs.getString(i++));
			live.setHometeam(home);
			
			//live.setHometeamname(rs.getString(i++));
			live.setHomescore(rs.getInt(i++));
			
			//need to set scahateam objects
			ScahaTeam away = new ScahaTeam(rs.getInt(i++));
			away.setTeamname(rs.getString(i++));
			
			live.setAwayteam(away);
			//live.setAwayteamname(rs.getString(i++));
			live.setAwayscore(rs.getInt(i++));
			live.setVenuetag(rs.getString(i++));
			live.setSheetname(rs.getString(i++));
			live.setStartdate(rs.getString(i++));
			live.setStarttime(rs.getString(i++));
			live.setScheduleidstub(rs.getInt(i++));
			live.setGamenotes(rs.getString(i++));
			live.setVenueshortname(rs.getString(i++));
			live.setSched(null);
			data.add(live);
		//	LOGGER.info("Found a match " + live);
		}
		rs.close();
		ps.close();
		//LOGGER.info("NewList is done looking for LiveGames that match " + selectedschedule + ". datalen=" + data.size());
		return new LiveGameList(data);
		
	}
	
	/**
	 * Here we get the schedule for a club so a presidenct can request game changes
	 * 
	 * 
	 */
	//use this interface to get the historical schedule by team.
		public static LiveGameList NewListFactory(ScahaDatabase _db, Profile _pro) throws SQLException {
			List<LiveGame> data = new ArrayList<LiveGame>();
			Integer profileid = 0;
			//LOGGER.info("NewList is looking for LiveGames that match " + _pro.toString());
			profileid = _pro.ID;
		
				//first lets get the club id
			PreparedStatement ps = _db.prepareStatement("call scaha.getclubforperson(?)");
			ps.setInt(1,profileid);
			ResultSet rs = ps.executeQuery();
			Integer clubid = 0;
			while (rs.next()) {
				clubid = rs.getInt("idclub");
			}
			
			//need to close recordset to use again
			rs.close();
			
			//since this can only get live games for the current season only need to pass in club id.
			//the entire club game scheduled will be returned in default order by date and time oldest
			//to newest.
			ps = _db.prepareStatement("call scaha.getLiveGamesByClub(?)");
			ps.setInt(1,clubid);
			rs = ps.executeQuery();
			while (rs.next()) {
				int i = 1;
			//	LOGGER.info("Found a row..");
				LiveGame live = new LiveGame(rs.getInt(i++));
			//	LOGGER.info("Found a live.." + live);
				live.setTypetag(rs.getString(i++));
				live.setStatetag(rs.getString(i++));
				
				//need to set scahateam objects
				ScahaTeam home = new ScahaTeam(rs.getInt(i++));
				home.setTeamname(rs.getString(i++));
				live.setHometeam(home);
				
				//live.setHometeamname(rs.getString(i++));
				live.setHomescore(rs.getInt(i++));
				
				//need to set scahateam objects
				ScahaTeam away = new ScahaTeam(rs.getInt(i++));
				away.setTeamname(rs.getString(i++));
				
				live.setAwayteam(away);
				//live.setAwayteamname(rs.getString(i++));
				live.setAwayscore(rs.getInt(i++));
				live.setVenuetag(rs.getString(i++));
				live.setSheetname(rs.getString(i++));
				live.setStartdate(rs.getString(i++));
				live.setStarttime(rs.getString(i++));
				live.setScheduleidstub(rs.getInt(i++));
				live.setGamenotes(rs.getString(i++));
				live.setVenueshortname(rs.getString(i++));
				live.setSched(null);
				data.add(live);
			//	LOGGER.info("Found a match " + live);
			}
			rs.close();
			ps.close();
			//LOGGER.info("NewList is done looking for LiveGames that match " + clubid + ". datalen=" + data.size());
			return new LiveGameList(data);
			
		}
	/** 
	 * Here we get all the games for a particular Season and team...
	 * @param _pro
	 * @param _tm
	 * @return
	 */
	public LiveGameList NewList (Profile _pro, Schedule _sch, ScahaTeam _tm) {
		
		//LOGGER.info("NewList is looking for LiveGames that match " + _sch + ", tm=" + _tm);
		List<LiveGame> data = new ArrayList<LiveGame>();
		HashMap<String, LiveGame> hm = new HashMap<String,LiveGame>();
		@SuppressWarnings("unchecked")
		List<LiveGame> results = (List<LiveGame>) getWrappedData();
		for (LiveGame live : results) {
			if ((live.getHometeam().ID == _tm.ID ||
				live.getAwayteam().ID == _tm.ID) && live.getSched().ID == _sch.ID) {
		//		LOGGER.info("Found a match " + live);
				data.add(live);
				hm.put(live.ID+"", live);
			}
		}
		//LOGGER.info("NewList completed for LiveGames.." + data.size());
		return new LiveGameList(data,hm);
	}
	
public LiveGameList NewList (Profile _pro, Schedule _sch, Integer _tm) {
		
		//LOGGER.info("NewList is looking for LiveGames that match " + _sch + ", tm=" + _tm);
		List<LiveGame> data = new ArrayList<LiveGame>();
		HashMap<String, LiveGame> hm = new HashMap<String,LiveGame>();
		@SuppressWarnings("unchecked")
		List<LiveGame> results = (List<LiveGame>) getWrappedData();
		for (LiveGame live : results) {
			if ((live.getHometeam().ID == _tm ||
				live.getAwayteam().ID == _tm) && live.getSched().ID == _sch.ID) {
		//		LOGGER.info("Found a match " + live);
				data.add(live);
				hm.put(live.ID+"", live);
			}
		}
		//LOGGER.info("NewList completed for LiveGames.." + data.size());
		return new LiveGameList(data,hm);
	}
	/** 
	 * Here we get all the games for a particular Season and team...
	 * @param _pro
	 * @param _tm
	 * @return
	 */
	public LiveGameList NewList (Profile _pro, Schedule _sch) {

		List<LiveGame> data = new ArrayList<LiveGame>();
		HashMap<String, LiveGame> hm = new HashMap<String,LiveGame>();
		//LOGGER.info("NewList is looking for LiveGames that match " + _sch);
			
		for (LiveGame live : this) {
			try {
				//LOGGER.info("livegame scheduled id being compared:" + live);
				if (live.getSched().ID == _sch.ID) {
					data.add(live);
					//LOGGER.info("Found a match " + live);
					hm.put(live.ID+"", live);
				}
			
			} catch (IndexOutOfBoundsException e) {
			    System.err.println("IndexOutOfBoundsException: " + e.getMessage());
			} catch (Exception e) {
			    System.err.println("Caught IOException: " + e.getMessage());
			}
		}
		//LOGGER.info("NewList completed for LiveGames for a schedule.." + data.size());
		_sch.setLivegamelist(new LiveGameList(data,hm));
		return  _sch.getLivegamelist();
	}

	//use this for the mobile version
	public LiveGameList NewList (Profile _pro, Integer _sch) {

		List<LiveGame> data = new ArrayList<LiveGame>();
		HashMap<String, LiveGame> hm = new HashMap<String,LiveGame>();
		//LOGGER.info("NewList is looking for LiveGames that match " + _sch);
			
		for (LiveGame live : this) {
			try {
				//LOGGER.info("livegame scheduled id being compared:" + live);
				if (live.getSched().ID == _sch) {
					data.add(live);
					//LOGGER.info("Found a match " + live);
					hm.put(live.ID+"", live);
				}
			
			} catch (IndexOutOfBoundsException e) {
			    System.err.println("IndexOutOfBoundsException: " + e.getMessage());
			} catch (Exception e) {
			    System.err.println("Caught IOException: " + e.getMessage());
			}
		}
		//LOGGER.info("NewList completed for LiveGames for a schedule.." + data.size());
		return  new LiveGameList(data,hm);
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Participant> getParticipantArray() {
		return (ArrayList<Participant>)this.getWrappedData();
	}
	
	public LiveGame getByKey(int _i) {
		return hm.get(_i+"");
	}
	
	@SuppressWarnings("unchecked")
	public void reset() {
		// TODO Auto-generated method stub
		LOGGER.info("resetting Participant List..");
		((ArrayList<LiveGame>)this.getWrappedData()).clear();
		hm.clear();
	}  
	
	@SuppressWarnings("unchecked")
	public void add(LiveGame _p) {
		//LOGGER.info("Adding Participant to List:" + _p);
		((ArrayList<LiveGame>)this.getWrappedData()).add(_p);
		hm.put(_p.ID+"", _p);
	}
	
	public String toString() {
		String answer = "";
		@SuppressWarnings("unchecked")
		List<LiveGame> results = (List<LiveGame>) getWrappedData();  
      for(LiveGame result : results) {  
    	  answer = answer +  "LiveGame: " + result + ContextManager.NEW_LINE;
      }  
      
      return answer;
      
	}

    @Override  
    public LiveGame getRowData(String rowKey) {  
    	return this.hm.get(rowKey);
    }  
 
    public LiveGame getLiveGameAtAt(int rowKey) {  
    	return this.hm.get(rowKey+"");
    }  
    @Override  
    public Object getRowKey(LiveGame result) {  
        return Integer.toString(result.ID);  
    }



}