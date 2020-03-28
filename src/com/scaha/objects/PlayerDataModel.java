package com.scaha.objects;

import java.io.Serializable;
import java.util.List;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

public class PlayerDataModel extends ListDataModel<Player> implements Serializable, SelectableDataModel<Player> {

	public PlayerDataModel() {  
    }  
  
    public PlayerDataModel(List<Player> data) {  
        super(data);  
    }  
      
    @Override  
    public Player getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        List<Player> results = (List<Player>) getWrappedData();  
          
        for(Player result : results) {  
            if(result.getIdplayer().equals(rowKey))  
                return result;  
        }  
          
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Player result) {  
        return result.getIdplayer();  
    }  
	
}
