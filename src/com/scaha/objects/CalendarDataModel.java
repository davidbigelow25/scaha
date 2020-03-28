package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class CalendarDataModel extends ListDataModel<CalendarItem> implements Serializable, SelectableDataModel<CalendarItem> {

	public CalendarDataModel() {  
    }  
  
    public CalendarDataModel(List<CalendarItem> data) {  
        super(data);  
    }  
      
    @Override  
    public CalendarItem getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<CalendarItem> results = (List<CalendarItem>) getWrappedData();  
          
        for(CalendarItem result : results) {  
            if(result.getCalendarid().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(CalendarItem result) {  
        return result.getCalendarid();  
    }  
	
}
