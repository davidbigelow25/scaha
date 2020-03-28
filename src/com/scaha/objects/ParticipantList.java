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
public class ParticipantList extends ListDataModel<Participant> implements Serializable, SelectableDataModel<Participant> {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// We need to implement a hashmap so we can directly fetch things we need.
	//
	private HashMap<String, Participant> hm = new HashMap<String,Participant>();
	private Schedule schedule = null;
	private TeamList teamlist = null;
	public ParticipantList(List<Participant> _lst,HashMap<String, Participant> _hm, Schedule _sch, TeamList _tl ) {  
		super(_lst);
		hm = _hm;
		schedule = _sch;
		teamlist = _tl;
   }
	
	public static ParticipantList NewListFactory() {
		return new ParticipantList(new ArrayList<Participant>(),new HashMap<String,Participant>(), null, null);
	}

	
	/**
	 * Here we have to fetch the a list of participants.. (who in turn will be associated with a given Team
	 * Which needs to be retrieved as well.
	 * 
	 * @param _pro
	 * @param _db
	 * @param _sch
	 * @return
	 * @throws SQLException
	 */
	public static ParticipantList NewListFactory (Profile _pro, ScahaDatabase _db, Schedule _sch, TeamList _tl) throws SQLException {
		
		List<Participant> data = new ArrayList<Participant>();
		HashMap<String, Participant> hm = new HashMap<String,Participant>();
		
		PreparedStatement ps = _db.prepareStatement("call scaha.getParticipantsBySchedule(?)");
		ps.setInt(1,_sch.ID);
		ResultSet rs = ps.executeQuery();
		int y = 1;
		while (rs.next()) {
			int i = 1;
			Participant part = new Participant(rs.getInt(i++),_pro);
			part.setRank(rs.getInt(i++));
			part.setTeam(_tl.getScahaTeamAt(rs.getInt(i++)));
			part.setGames(rs.getInt(i++));
			part.setExgames(rs.getInt(i++));
			part.setGamesplayed(rs.getInt(i++));
			part.setWins(rs.getInt(i++));
			part.setLoses(rs.getInt(i++));
			part.setTies(rs.getInt(i++));
			part.setPoints(rs.getInt(i++));
			part.setGf(rs.getInt(i++));
			part.setGa(rs.getInt(i++));
			part.setHasdropped((rs.getInt(i++) == 0 ? false : true));
			i++;
			part.setGd(rs.getInt(i++));
			
			part.setSchedule(_sch);
			part.setPlace(y++);
			//LOGGER.info("Found new Participant for schedule " + _sch + ". " + part);
			data.add(part);
			hm.put(part.ID+"", part);
			part.setSchedule(_sch);
		}
		rs.close();
		ps.close();
		return new ParticipantList(data,hm,_sch,_tl);
		
	}

	
	// use this interface for gathering the historical standings.
	public static ParticipantList NewListFactory (ScahaDatabase _db,Integer selectedschedule) throws SQLException {
		
		List<Participant> data = new ArrayList<Participant>();
		
		PreparedStatement ps = _db.prepareStatement("call scaha.getHistoricalParticipantsBySchedule(?)");
		ps.setInt(1,selectedschedule);
		ResultSet rs = ps.executeQuery();
		int y = 1;
		while (rs.next()) {
			int i = 1;
			Participant part = new Participant(rs.getInt(i++));
			part.setRank(rs.getInt(i++));
			
			//need to set scahateam objects
			ScahaTeam listteam = new ScahaTeam(rs.getInt(i++));
			listteam.setTeamname(rs.getString(i++));
			
			part.setTeam(listteam);
			part.setGames(rs.getInt(i++));
			part.setExgames(rs.getInt(i++));
			part.setGamesplayed(rs.getInt(i++));
			part.setWins(rs.getInt(i++));
			part.setLoses(rs.getInt(i++));
			part.setTies(rs.getInt(i++));
			part.setPoints(rs.getInt(i++));
			part.setGf(rs.getInt(i++));
			part.setGa(rs.getInt(i++));
			part.setHasdropped((rs.getInt(i++) == 0 ? false : true));
			Integer tempgd = rs.getInt(i++);
			if (tempgd.equals(-1)){
				tempgd = part.getGf()- part.getGa();
			}
			part.setGd(tempgd);
			
			
			//part.setGd(part.getGf()- part.getGa());
			
			part.setSchedule(null);
			part.setPlace(y++);
			//LOGGER.info("Found new Participant for schedule " + selectedschedule + ". " + part);
			
			data.add(part);
			
		}
		rs.close();
		ps.close();
		
		return new ParticipantList(data);
		
	}
	
	public ParticipantList(List<Participant> _lst) {  
		super(_lst);
	}
	
	
	
	//use this interface to get list of historical teams by division
	public static List<Participant> getHistoricalParticipantList(ScahaDatabase _db,Integer selectedschedule) throws SQLException {
		List<Participant> data = new ArrayList<Participant>();
		
		PreparedStatement ps = _db.prepareStatement("call scaha.getHistoricalParticipantsListBySchedule(?)");
		ps.setInt(1,selectedschedule);
		ResultSet rs = ps.executeQuery();
		int y = 1;
		while (rs.next()) {
			int i = 1;
			Participant part = new Participant(rs.getInt(i++));
			part.setRank(rs.getInt(i++));
			
			//need to set scahateam objects
			ScahaTeam listteam = new ScahaTeam(rs.getInt(i++));
			listteam.setTeamname(rs.getString(i++));
			
			part.setTeam(listteam);
			//LOGGER.info("Found new Participant list for historical schedule " + selectedschedule + ". " + part);
			
			data.add(part);
			
		}
		rs.close();
		ps.close();
		
		return data;
	}
	
	
	/**
	 * Here we have to fetch the a list of participants.. (who in turn will be associated with a given Team
	 * Which needs to be retrieved as well.
	 * 
	 * @param _pro
	 * @param _db
	 * @param _sch
	 * @return
	 * @throws SQLException
	 */
	public void refreshList (Profile _pro, ScahaDatabase _db) throws SQLException {
		
		if (this.teamlist == null || this.schedule == null) {
			//LOGGER.info("**** PARTICIPANT LIST NOT YET INITIALIZED ****");
			return;
		}
		TeamList _tl = this.teamlist;
		Schedule _sch = this.schedule;
		
		PreparedStatement ps = _db.prepareStatement("call scaha.getParticipantsBySchedule(?)");
		ps.setInt(1,_sch.ID);
		ResultSet rs = ps.executeQuery();
		int y = 1;
		while (rs.next()) {
			int i = 1;
			Participant part = this.getByKey(rs.getInt(i++));
			if (part != null) {
				part.setRank(rs.getInt(i++));
				part.setTeam(_tl.getScahaTeamAt(rs.getInt(i++)));
				part.setGames(rs.getInt(i++));
				part.setExgames(rs.getInt(i++));
				part.setGamesplayed(rs.getInt(i++));
				part.setWins(rs.getInt(i++));
				part.setLoses(rs.getInt(i++));
				part.setTies(rs.getInt(i++));
				part.setPoints(rs.getInt(i++));
				part.setGf(rs.getInt(i++));
				part.setGa(rs.getInt(i++));
				part.setHasdropped((rs.getInt(i++) == 0 ? false : true));
				i++;
				part.setGd(rs.getInt(i++));
				part.setPlace(y++);
				part.setSchedule(_sch);
			//	LOGGER.info("Found Participant for schedule " + _sch + ". " + part);
			}
		}
		rs.close();
		ps.close();
		
	}

	@SuppressWarnings("unchecked")
	public ArrayList<Participant> getParticipantArray() {
		return (ArrayList<Participant>)this.getWrappedData();
	}
	
	public Participant getByKey(int _i) {
		return hm.get(_i+"");
	}
	
	@SuppressWarnings("unchecked")
	public void reset() {
		// TODO Auto-generated method stub
		//LOGGER.info("resetting Participant List..");
		((ArrayList<Participant>)this.getWrappedData()).clear();
		hm.clear();
	}  
	
	@SuppressWarnings("unchecked")
	public void add(Participant _p) {
		//LOGGER.info("Adding Participant to List:" + _p);
		((ArrayList<Participant>)this.getWrappedData()).add(_p);
		hm.put(_p.ID+"", _p);
	}
	
	public String toString() {
		String answer = "";
		@SuppressWarnings("unchecked")
		List<Participant> results = (List<Participant>) getWrappedData();  
      for(Participant result : results) {  
    	  answer = answer +  "Participant: " + result + ContextManager.NEW_LINE;
      }  
      
      return answer;
      
	}

    @Override  
    public Participant getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<Participant> results = (List<Participant>) getWrappedData();  
        for(Participant result : results) {  
        	if(Integer.toString(result.ID).equals(rowKey)) return result;  
        }  
          
        return null;  
    }  
    
    @Override  
    public Object getRowKey(Participant result) {  
        return Integer.toString(result.ID);  
    }

	public Schedule getSchedule() {
		return schedule;
	}

	public void setSchedule(Schedule schedule) {
		this.schedule = schedule;
	}


}