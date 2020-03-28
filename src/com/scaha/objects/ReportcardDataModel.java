package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class ReportcardDataModel extends ListDataModel<Reportcard> implements Serializable, SelectableDataModel<Reportcard> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReportcardDataModel() {  
    }  
  
    public ReportcardDataModel(List<Reportcard> data) {  
        super(data);  
    }  
      
    @SuppressWarnings("unchecked")
	@Override  
    public Reportcard getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Reportcard> results = (List<Reportcard>) getWrappedData();  
          
        for(Reportcard result : results) {  
            if(result.getIdperson().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Reportcard result) {  
        return result.getIdperson();  
    }  
	
}
