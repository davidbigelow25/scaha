package com.scaha.objects;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.connectors.ScahaDatabase;

public class NewsItemList extends ListDataModel<NewsItem> implements Serializable, SelectableDataModel<NewsItem> {
	
	private static final long serialVersionUID = 1L;

	public NewsItemList() {  
    }  
	
	
	/**
	 * This will get all ScahaNews Items and return a newsItemList
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public static NewsItemList NewsItemListFactory(Profile _pro, ScahaDatabase _db, String _strDate) throws SQLException {
		
		List<NewsItem> data = new ArrayList<NewsItem>();
		
		//
		// ok. .lets fill it up with news items!!
		//
		Vector<String> v = new Vector<String>();
		v.add(_strDate);
		ResultSet rs = null;
		if (_db.getData("call scaha.getAllNewsItems(?)",v)) {
			//
			// lets create new News Items.. and Let them rip
			//
			rs = _db.getResultSet();
			while (rs.next()) {
				int i = 1;
				NewsItem ni = new NewsItem(rs.getInt(i++),_pro) ;
				ni.setAuthor(rs.getString(i++));
				ni.setTitle(rs.getString(i++));
				ni.setSubject(rs.getString(i++));
				ni.setBody(rs.getString(i++));
				ni.setUpdated(rs.getString(i++));
				ni.setIntro(rs.getString(i++));
				ni.setRefid(rs.getString(i++));
				data.add(ni);
			}
		}
			
		return new NewsItemList(data);
	}
	/**
	 * 
	 * @param data
	 */
    public NewsItemList(List<NewsItem> data) {  
        super(data);  
    }  
    
    @Override  
    public NewsItem getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<NewsItem> results = (List<NewsItem>) getWrappedData();  
          
        for(NewsItem result : results) {  
            if(Integer.toString(result.ID).equals(rowKey))  
                return result;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(NewsItem result) {  
        return Integer.toString(result.ID);  
    }
	
}

