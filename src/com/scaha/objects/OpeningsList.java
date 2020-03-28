package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.context.ContextManager;

public class OpeningsList extends ListDataModel<Opening> implements Serializable, SelectableDataModel<Opening> {
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	public OpeningsList() {  
    }  
	
	/**
	 * This will get all Tryout Items and return a TryoutList. We pass around a prepared statement because it 
	 * is a signal to the method that we do not know how nested we are.. so lets save resources
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public static OpeningsList NewOpeningListFactory(PreparedStatement ps, Club _cl) throws SQLException {
		
			List<Opening> data = new ArrayList<Opening>();

			ps.setInt(1,_cl.ID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Opening t = new Opening();
				t.setContactemail(rs.getString("contactemail"));
				t.setContactname(rs.getString("contactname"));
				t.setDivision(rs.getString("division"));
				t.setOpeningcount(rs.getString("openingcount"));
				t.setOpeningid(rs.getInt("openingid"));
				t.setRink(rs.getString("rink"));
				t.setSkilllevel(rs.getString("skilllevel"));
				
				data.add(t);
			}
			rs.close();
		
			return new OpeningsList(data);
	}
	
	
	/**
	 * 
	 * @param data
	 */
    public OpeningsList(List<Opening> data) {  
        super(data);  
    }  
    
    @Override  
    public Opening getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<Opening> results = (List<Opening>) getWrappedData();  
          
        for(Opening result : results) {  
            if(Integer.toString(result.ID).equals(rowKey))  
                return result;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Opening result) {  
        return Integer.toString(result.ID);  
    }
}
