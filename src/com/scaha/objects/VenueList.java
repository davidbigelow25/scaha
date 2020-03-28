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

public class VenueList extends ListDataModel<Venue> implements Serializable, SelectableDataModel<Venue> {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	/**
	 * 
	 * @param data
	 */
    public VenueList(List<Venue> data) {  
        super(data);  
    }  
    	
	public static VenueList NewVenueListFactory(Profile _pro, PreparedStatement psVen, Club c) throws SQLException {
		
		List<Venue> data = new ArrayList<Venue>();

		psVen.setInt(1,c.ID);
		ResultSet rs = psVen.executeQuery();
		while (rs.next()) {
			int i = 1;
			Venue v = new Venue(_pro, rs.getInt(i++), c);
			v.setPrimary(rs.getString(i++).equals("Y"));
			v.setTag(rs.getString(i++));
			v.setDescription(rs.getString(i++));
			v.setAddress(rs.getString(i++));
			v.setCity(rs.getString(i++));
			v.setState(rs.getString(i++));
			v.setZipcode(rs.getString(i++));
			v.setPhone(rs.getString(i++));
			v.setEmail(rs.getString(i++));
			v.setWebsite(rs.getString(i++));
			v.setGmapparms(rs.getString(i++));
			data.add(v);
		}
		rs.close();
	
		return new VenueList(data);
	}

    @Override  
    public Venue getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<Venue> results = (List<Venue>) getWrappedData();  
          
        for(Venue result : results) {  
            if(Integer.toString(result.ID).equals(rowKey))  
                return result;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Venue result) {  
        return Integer.toString(result.ID);  
    }

	
}
