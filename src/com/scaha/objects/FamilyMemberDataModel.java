package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class FamilyMemberDataModel extends ListDataModel<FamilyMember> implements Serializable, SelectableDataModel<FamilyMember> {
	
	private static final long serialVersionUID = 1L;

	public FamilyMemberDataModel() {  
    }  
  
    public FamilyMemberDataModel(List<FamilyMember> data) {  
        super(data);  
    }  
      
    @Override  
    public FamilyMember getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<FamilyMember> results = (List<FamilyMember>) getWrappedData();  
          
        for(FamilyMember result : results) {  
            if(Integer.toString(result.ID).equals(rowKey))  
                return result;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(FamilyMember result) {  
        return Integer.toString(result.ID);  
    }  
	
}
