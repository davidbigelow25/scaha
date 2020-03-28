package com.scaha.objects;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.context.ContextManager;

public class ResultDataModel extends ListDataModel<Result> implements Serializable, SelectableDataModel<Result> {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
		
	public ResultDataModel() {  
    }  
  
    public ResultDataModel(List<Result> data) {  
        super(data);  
    }  
      
    @Override  
    public Result getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<Result> results = (List<Result>) getWrappedData();  
        if (results == null)  {
        	//LOGGER.info("getRowData is Null");
        	return null;
        }
        
        for(Result result : results) {  
            if(result.getIdplayer().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Result result) {  
        return result.getIdplayer();  
    }  
	
}
