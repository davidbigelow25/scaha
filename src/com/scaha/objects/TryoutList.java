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

public class TryoutList extends ListDataModel<Tryout> implements Serializable, SelectableDataModel<Tryout> {
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	public TryoutList() {  
    }  
	
	/**
	 * This will get all Tryout Items and return a TryoutList. We pass around a prepared statement because it 
	 * is a signal to the method that we do not know how nested we are.. so lets save resources
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public static TryoutList NewTryoutListFactory(PreparedStatement ps, Club _cl) throws SQLException {
		
			List<Tryout> data = new ArrayList<Tryout>();

			ps.setInt(1,_cl.ID);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Tryout t = new Tryout();
				t.setTryoutdate(rs.getString("tryoutdate"));
				t.setTryouttime(rs.getString("tryouttime"));
				t.setDivision(rs.getString("division"));
				t.setLevel(rs.getString("level"));
				t.setLocation(rs.getString("location"));
				t.setCoach(rs.getString("coach"));
				t.setUsewebsite(rs.getBoolean("usewebsite"));
				t.setTryoutid(rs.getInt("idclubtryouts"));
				data.add(t);
			}
			rs.close();
		
			return new TryoutList(data);
	}
	
	
	/**
	 * 
	 * @param data
	 */
    public TryoutList(List<Tryout> data) {  
        super(data);  
    }  
    
    @Override  
    public Tryout getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<Tryout> results = (List<Tryout>) getWrappedData();  
          
        for(Tryout result : results) {  
            if(Integer.toString(result.ID).equals(rowKey))  
                return result;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Tryout result) {  
        return Integer.toString(result.ID);  
    }
}
