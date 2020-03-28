package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class TournamentDataModel extends ListDataModel<Tournament> implements Serializable, SelectableDataModel<Tournament> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TournamentDataModel() {  
    }  
  
    public TournamentDataModel(List<Tournament> data) {  
        super(data);  
    }  
      
    @SuppressWarnings("unchecked")
	@Override  
    public Tournament getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Tournament> results = (List<Tournament>) getWrappedData();  
          
        for(Tournament result : results) {  
            if(result.getIdtournament().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Tournament result) {  
        return result.getIdtournament();  
    }  
	
}
