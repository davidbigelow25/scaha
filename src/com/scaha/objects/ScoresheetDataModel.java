package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class ScoresheetDataModel extends ListDataModel<Scoresheet> implements Serializable, SelectableDataModel<Scoresheet> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ScoresheetDataModel() {  
    }  
  
    public ScoresheetDataModel(List<Scoresheet> data) {  
        super(data);  
    }  
      
    @SuppressWarnings("unchecked")
	@Override  
    public Scoresheet getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Scoresheet> results = (List<Scoresheet>) getWrappedData();  
          
        for(Scoresheet result : results) {  
            if(result.getIdgame().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Scoresheet result) {  
        return result.getIdgame();  
    }  
	
}
