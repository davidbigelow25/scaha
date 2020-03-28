package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class PlayoffbracketDataModel extends ListDataModel<Playoffbracket> implements Serializable, SelectableDataModel<Playoffbracket> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PlayoffbracketDataModel() {  
    }  
  
    public PlayoffbracketDataModel(List<Playoffbracket> data) {  
        super(data);  
    }  
      
    @SuppressWarnings("unchecked")
	@Override  
    public Playoffbracket getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Playoffbracket> results = (List<Playoffbracket>) getWrappedData();  
          
        for(Playoffbracket result : results) {  
            if(result.getIdbracket().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Playoffbracket result) {  
        return result.getIdbracket();  
    }  
	
}
