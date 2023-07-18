package com.scaha.objects;

import org.primefaces.model.SelectableDataModel;

import javax.faces.model.ListDataModel;
import java.io.Serializable;
import java.util.List;

public class AccountDataModel extends ListDataModel<Account> implements Serializable, SelectableDataModel<Account> {

	public AccountDataModel() {
    }

    public AccountDataModel(List<Account> data) {
        super(data);  
    }  
      
    @Override
    public Account getRowData(String rowKey) {
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Account> results = (List<Account>) getWrappedData();
          
        for(Account result : results) {
            if(result.getPersonid().equals(rowKey))
                return result;  
        }  
          
        return null;  
    }  
  
    @Override
    public Object getRowKey(Account result) {
        return result.getPersonid();
    }  
	
}
