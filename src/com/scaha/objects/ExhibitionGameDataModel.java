package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class ExhibitionGameDataModel extends ListDataModel<ExhibitionGame> implements Serializable, SelectableDataModel<ExhibitionGame> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ExhibitionGameDataModel() {  
    }  
  
    public ExhibitionGameDataModel(List<ExhibitionGame> data) {  
        super(data);  
    }  
      
    @SuppressWarnings("unchecked")
	@Override  
    public ExhibitionGame getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<ExhibitionGame> results = (List<ExhibitionGame>) getWrappedData();  
          
        for(ExhibitionGame result : results) {  
            if(result.getIdgame().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(ExhibitionGame result) {  
        return result.getIdgame();  
    }  
	
}
