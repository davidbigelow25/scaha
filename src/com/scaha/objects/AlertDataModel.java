package com.scaha.objects;

import org.primefaces.model.SelectableDataModel;

import javax.faces.model.ListDataModel;
import java.io.Serializable;
import java.util.List;

public class AlertDataModel extends ListDataModel<Alert> implements Serializable, SelectableDataModel<Alert> {

	public AlertDataModel() {
    }

    public AlertDataModel(List<Alert> data) {
        super(data);  
    }  
      
    @Override  
    public Alert getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Alert> results = (List<Alert>) getWrappedData();
          
        for(Alert result : results) {
            if(result.getAlertid().equals(rowKey))
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Alert result) {
        return result.getAlertid();
    }  
	
}
