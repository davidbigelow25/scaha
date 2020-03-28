package com.scaha.objects;

import java.io.Serializable;
import java.sql.Blob;
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
;

public class ManagerList extends ListDataModel<ScahaManager> implements Serializable, SelectableDataModel<ScahaManager> {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	

	public ManagerList() {  
    }  
	
	/**
	 * 
	 * @param data
	 */
    public ManagerList(List<ScahaManager> data) {  
        super(data);  
    }  

	
	/**
	 * This will get all Coaches for a given Team and Season
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public static ManagerList NewManagerListFactory(Profile _pro, PreparedStatement ps,  ScahaTeam _tm) throws SQLException {
		
		List<ScahaManager> data = new ArrayList<ScahaManager>();
		
		ps.setInt(1, _tm.ID);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			ScahaManager sc = new ScahaManager(rs.getInt(i++),_pro);
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
		//
//		LOGGER.info("Finished all the Coach List Loading!!");
		return new ManagerList(data);
	}

    
    @Override  
    public ScahaManager getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<ScahaManager> results = (List<ScahaManager>) getWrappedData();  
          
        for(ScahaManager result : results) {  
            if(Integer.toString(result.ID).equals(rowKey))  
                return result;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(ScahaManager result) {  
        return Integer.toString(result.ID);  
    }
	
}
