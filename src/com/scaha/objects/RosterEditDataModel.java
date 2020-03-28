package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class RosterEditDataModel extends ListDataModel<RosterEdit> implements Serializable, SelectableDataModel<RosterEdit> {

	public RosterEditDataModel() {  
    }  
  
    public RosterEditDataModel(List<RosterEdit> data) {  
        super(data);  
    }  
      
    @Override  
    public RosterEdit getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<RosterEdit> results = (List<RosterEdit>) getWrappedData();  
          
        for(RosterEdit result : results) {  
            if(result.getIdplayer().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(RosterEdit result) {  
        return result.getIdplayer();  
    }  
	
}
