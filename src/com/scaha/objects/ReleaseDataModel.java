package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class ReleaseDataModel extends ListDataModel<Release> implements Serializable, SelectableDataModel<Release> {

	public ReleaseDataModel() {  
    }  
  
    public ReleaseDataModel(List<Release> data) {  
        super(data);  
    }  
      
    @Override  
    public Release getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Release> results = (List<Release>) getWrappedData();  
          
        for(Release result : results) {  
            if(result.getIdrelease().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Release result) {  
        return result.getIdrelease();  
    }  
	
}
