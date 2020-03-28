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
public class ScheduleWeekList extends ListDataModel<ScheduleWeek> implements Serializable, SelectableDataModel<Schedule> {

	//
	// We need to implement a hashmap so we can directly fetch things we need.
	//
	
	private HashMap<String, ScheduleWeek> hm = new HashMap<String,ScheduleWeek>();
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	public ScheduleWeekList(List<ScheduleWeek> _lst) {  
		super(_lst);
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
	public static ScheduleWeekList ListFactory (Profile _pro, ScahaDatabase _db, Schedule _sch) throws SQLException {
		
		List<ScheduleWeek> data = new ArrayList<ScheduleWeek>();
		
		//LOGGER.info("Loading Schdule Weeks for schedule " + _sch);
		
		PreparedStatement ps = _db.prepareStatement("call scaha.getScheduleWeeks(?,?)");
		ps.setInt(1,_sch.ID);
		ps.setString(2,_sch.getScheduleweektag());
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			ScheduleWeek sw = new ScheduleWeek(rs.getInt(i++),_pro);
			sw.setWeek(rs.getInt(i++));
			sw.setSName(rs.getString(i++));
			sw.setName(rs.getString(i++));
			sw.setTag(rs.getString(i++));
			sw.setSeasonTag(rs.getString(i++));
			sw.setFromDate(rs.getString(i++));
			sw.setToDate(rs.getString(i++));
			
			//LOGGER.info("Found new Schedule Week for schedule " + _sch + ". " + sw);
			data.add(sw);
			sw.setSchedule(_sch);
		}
		rs.close();

		ps.close();
		return new ScheduleWeekList(data);
		
	}

	@Override
	public Schedule getRowData(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getRowKey(Schedule arg0) {
		// TODO Auto-generated method stub
		return null;
	}  
	

	@SuppressWarnings("unchecked")
	public ArrayList<ScheduleWeek> getScheduleWeekArray() {
		return (ArrayList<ScheduleWeek>)this.getWrappedData();
	}
	
	public ScheduleWeek getByKey(int _i) {
		return hm.get(_i+"");
	}
	
	@SuppressWarnings("unchecked")
	public void reset() {
		// TODO Auto-generated method stub
		((ArrayList<ScheduleWeek>)this.getWrappedData()).clear();
		hm.clear();
	}  
	
	@SuppressWarnings("unchecked")
	public void add(ScheduleWeek _sw) {
		hm.put(_sw.ID+"", _sw);
		((ArrayList<ScheduleWeek>)this.getWrappedData()).add(_sw);
	}

	/**
	 * 
	 */
	public String toString() {
		
		String answer = "";
		@SuppressWarnings("unchecked")
		List<ScheduleWeek> results = (List<ScheduleWeek>) getWrappedData();  
		for(ScheduleWeek result : results) {  
			answer = answer +  "ScheduleWeek: " + result + ContextManager.NEW_LINE;
		}  
    	return answer;

	}
}
