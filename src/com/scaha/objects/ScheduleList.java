package com.scaha.objects;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.mysql.jdbc.CallableStatement;

public class ScheduleList extends ListDataModel<Schedule> implements Serializable, SelectableDataModel<Schedule> {
	
	//
	// We need to implement a hashmap so we can directly fetch things we need.
	//
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	public ScheduleList(List<Schedule> _lst) {  
		super(_lst);
    }  
 
	
	 /**
	 * This will get all Schedule and return a ScheduleList
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public static ScheduleList ListFactory(Profile _pro, ScahaDatabase _db, GeneralSeason _gs, TeamList _tl) throws SQLException {
		
		List<Schedule> data = new ArrayList<Schedule>();
		//LOGGER.info("Loading Schedules for General Season" + _gs);
		//
		// Lets go get all the schedules for a given general season..
		// 
		
		PreparedStatement ps  =  _db.prepareStatement("call scaha.getAllSchedulesBySeasonTag(?)");
		ps.setString(1, _gs.getTag());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			Schedule sch = new Schedule(_pro,rs.getInt(i++));
			sch.setDescription(rs.getString(i++));
			sch.setScheduleweektag(rs.getString(i++));
			sch.setSeasontag(rs.getString(i++));
			sch.setGametag(rs.getString(i++));
			sch.setDivsname(rs.getString(i++));
			sch.setTag(rs.getString(i++));
			sch.setRank(rs.getInt(i++));
			sch.setTeamcount(rs.getInt(i++));
			sch.setStartdate(rs.getString(i++));
			sch.setEnddate(rs.getString(i++));
			sch.setLocked((rs.getInt(i++) == 1 ? true : false));
			sch.setPlayonce((rs.getInt(i++) == 1 ? true : false));
			sch.setMingamecnt(rs.getInt(i++));
			sch.setMaxgamecnt(rs.getInt(i++));
			sch.setMaxbyecnt(rs.getInt(i++));
			sch.setMaxawaycnt(rs.getInt(i++));
			sch.setExhibitioncounts((rs.getInt(i++) == 1 ? true : false));
			sch.setMaxexmatchup(rs.getInt(i++));
			data.add(sch);
			sch.setPartlist(ParticipantList.NewListFactory(_pro, _db, sch, _tl));
			//LOGGER.info("Part List is " + ContextManager.NEW_LINE  + sch.getPartlist().toString());
			sch.setSwlist(ScheduleWeekList.ListFactory(_pro, _db, sch));
			//LOGGER.info("ScheduleWeekList is " + ContextManager.NEW_LINE  + sch.getSwlist().toString());
			//LOGGER.info("Adding schedule " + sch + " to the list...");
		}
		rs.close();
		ps.close();
		
		ScheduleList sl = new ScheduleList(data);
		_gs.setSchedList(sl);

		return sl;
	}
		
    
	@Override
	public Schedule getRowData(String _rowkey) {
	    @SuppressWarnings("unchecked")
		List<Schedule> results = (List<Schedule>) getWrappedData();  
	    for(Schedule schedule : results) {  
	    	if(Integer.toString(schedule.ID).equals(_rowkey)) return schedule;  
	    }  
		return null;
	}
	
	public Schedule getSchedule (int _key) {
		@SuppressWarnings("unchecked")
		List<Schedule> results = (List<Schedule>) getWrappedData();  
	    for(Schedule result : results) {  
	    	if (result.ID == _key)  return result;
	    }  
	      
	    return null;
	}
	
	@Override
	public Object getRowKey(Schedule arg0) {
		// TODO Auto-generated method stub
		return Integer.toString(arg0.ID);	
	}

	public String toString() {
		String answer = "";
		@SuppressWarnings("unchecked")
		List<Schedule> results = (List<Schedule>) getWrappedData();  
      for(Schedule result : results) {  
    	  answer = answer +  "Schedule: " + result + ContextManager.NEW_LINE;
      }  
      
      return answer;
      
	}
	
}
