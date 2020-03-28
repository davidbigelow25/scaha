package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class TempGameDataModel extends ListDataModel<TempGame> implements Serializable, SelectableDataModel<TempGame> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TempGameDataModel() {  
    }  
  
    public TempGameDataModel(List<TempGame> data) {  
        super(data);  
    }  
      
    @SuppressWarnings("unchecked")
	@Override  
    public TempGame getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<TempGame> results = (List<TempGame>) getWrappedData();  
          
        for(TempGame result : results) {  
            if(result.getIdgame().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(TempGame result) {  
        return result.getIdgame();  
    }  
	
}
