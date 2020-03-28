package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class CoachDataModel extends ListDataModel<Coach> implements Serializable, SelectableDataModel<Coach> {

	public CoachDataModel() {  
    }  
  
    public CoachDataModel(List<Coach> data) {  
        super(data);  
    }  
      
    @Override  
    public Coach getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Coach> results = (List<Coach>) getWrappedData();  
          
        for(Coach result : results) {  
            if(result.getIdcoach().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Coach result) {  
        return result.getIdcoach();  
    }  
	
}
