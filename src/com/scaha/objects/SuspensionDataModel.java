package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class SuspensionDataModel extends ListDataModel<Suspension> implements Serializable, SelectableDataModel<Suspension> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SuspensionDataModel() {  
    }  
  
    public SuspensionDataModel(List<Suspension> data) {  
        super(data);  
    }  
      
    @SuppressWarnings("unchecked")
	@Override  
    public Suspension getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Suspension> results = (List<Suspension>) getWrappedData();  
          
        for(Suspension result : results) {  
            if(result.getIdsuspension().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Suspension result) {  
        return result.getIdsuspension();  
    }  
	
}
