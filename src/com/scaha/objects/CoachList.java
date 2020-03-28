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

import com.gbli.context.ContextManager;
;

public class CoachList extends ListDataModel<ScahaCoach> implements Serializable, SelectableDataModel<ScahaCoach> {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	

	public CoachList() {  
    }  
	
	/**
	 * 
	 * @param data
	 */
    public CoachList(List<ScahaCoach> data) {  
        super(data);  
    }  

	
	/**
	 * This will get all Coaches for a given Team and Schedule
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public static CoachList NewCoachListFactory(Profile _pro, PreparedStatement ps,  ScahaTeam _tm) throws SQLException {
		
		List<ScahaCoach> data = new ArrayList<ScahaCoach>();
		
		ps.setInt(1, _tm.ID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			ScahaCoach sc = new ScahaCoach(rs.getInt(i++),_pro);
			sc.setsFirstName(rs.getString(i++));
			sc.setsLastName(rs.getString(i++));
			sc.getGenatt().put("ROSTERTYPE",rs.getString(i++));
			sc.setsEmail(rs.getString(i++));
			sc.setsPhone(rs.getString(i++));
			data.add(sc);
		}
		rs.close();
		
		//
		// now lets get the second level stuff..
		return new CoachList(data);
	}

    
    @Override  
    public ScahaCoach getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<ScahaCoach> results = (List<ScahaCoach>) getWrappedData();  
          
        for(ScahaCoach result : results) {  
            if(Integer.toString(result.ID).equals(rowKey))  
                return result;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(ScahaCoach result) {  
        return Integer.toString(result.ID);  
    }
	
}
