package com.gbli.common;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.context.ContextManager;

public class ReturnDataResultSet extends ListDataModel<ReturnDataRow> implements Serializable, SelectableDataModel<ReturnDataRow> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static final String NULL = "";

	ReturnDataRow rheader = null;
	
	/**
	 * 
	 * @param data
	 */
    public ReturnDataResultSet(List<ReturnDataRow> data, ReturnDataRow _header) {  
        super(data);  
        rheader = _header;
        rheader.setRdrs(this);
    }  
    
    
	
	
	/**
	 * Lets gobble up a result set into this object.
	 * 
	 */
	@SuppressWarnings("unchecked")
	public static ReturnDataResultSet NewReturnDataResultSetFactory(ResultSet rs) throws SQLException {
	
		List<ReturnDataRow> data = new ArrayList<ReturnDataRow>();
		ReturnDataRow rdrheader = new ReturnDataRow(-1);
		ReturnDataResultSet rdrs = new ReturnDataResultSet(data,rdrheader);
		if (rs != null && rs.getMetaData() != null) {
			int iColCount = rs.getMetaData().getColumnCount();
			for (int y=1; y<=iColCount; y++) rdrheader.add(rs.getMetaData().getColumnLabel(y),rs.getMetaData().getColumnLabel(y));
			int i = 0;
			while (rs.next()) {
				ReturnDataRow rdr = new ReturnDataRow(i++,rdrs);
				for (int y=1; y<=iColCount; y++) {
					if ( rs.getObject(y) == null) {
						rdr.add(NULL,rdrheader.get(y-1).toString());
					} else  {
						rdr.add(rs.getObject(y),rdrheader.get(y-1).toString());
						
					}
				}
				data.add(rdr);
			}
		}
		
		
		return rdrs;

	}
	
	
	
	@Override
	public ReturnDataRow getRowData (String rowKey)  {
        @SuppressWarnings("unchecked")
		List<ReturnDataRow> results = (List<ReturnDataRow>) getWrappedData();  
          
        for(ReturnDataRow result : results) {  
            if(result.getRowkey().equals(rowKey))  
                return result;  
        }  
        return null;
	}

	@Override
	public Object getRowKey(ReturnDataRow rdr) {
		return rdr.getRowkey();  
	}




	/**
	 * @return the rheader
	 */
	public ReturnDataRow getRheader() {
		return rheader;
	}




	/**
	 * @param rheader the rheader to set
	 */
	public void setRheader(ReturnDataRow rheader) {
		this.rheader = rheader;
	}

	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.rheader.toString());
		sb.append("\r\n");
		for (ReturnDataRow rdr : this) {
			sb.append(rdr.toString());
			sb.append("\r\n");
		}
	
		return sb.toString();
		
	}

	

}
