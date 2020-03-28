package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class TeamDataModel extends ListDataModel<Team> implements Serializable, SelectableDataModel<Team> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TeamDataModel() {  
    }  
  
    public TeamDataModel(List<Team> data) {  
        super(data);  
    }  
      
    @SuppressWarnings("unchecked")
	@Override  
    public Team getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Team> results = (List<Team>) getWrappedData();  
          
        for(Team result : results) {  
            if(result.getIdteam().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Team result) {  
        return result.getIdteam();  
    }  
	
}
