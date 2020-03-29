/**
 * 
 */
package com.scaha.objects;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class GeneralSeasonList extends ListDataModel<GeneralSeason> implements Serializable, SelectableDataModel<GeneralSeason> {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private GeneralSeason CurrentSeason = null;
	private GeneralSeason PriorSeason = null;
	
	/**
	 * This will get all ScahaNews Items and return a newsItemList
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public static GeneralSeasonList NewClubListFactory(Profile _pro, ScahaDatabase _db, String _sMemberType) throws SQLException {
		
		List<GeneralSeason> data = new ArrayList<GeneralSeason>();
		
		GeneralSeason current = null;
		
		ResultSet rs = null;
		if (_db.getData("call scaha.getAllSeasonsByType('" + _sMemberType + "')")) {
			//
			// lets create new News Items.. and Let them rip
			//
			rs = _db.getResultSet();
			while (rs.next()) {
				int i = 1;
				GeneralSeason gc = new GeneralSeason(_pro, rs.getInt(i++)) ;
				gc.setDescription(rs.getString(i++));
				gc.setFromDate(rs.getString(i++));
				gc.setIsCurrent(rs.getInt(i++));
				gc.setLinkTag(rs.getString(i++));
				gc.setMembershipType(rs.getString(i++));
				gc.setTag(rs.getString(i++));
				gc.setToDate(rs.getString(i++));
				gc.setUSAYear(rs.getString(i++));
				//
				// Lets preserve what is marked as current
				
				//need to comment this out when working locally
				if (gc.getIsCurrent() == 1) {
				//if (gc.getUSAYear().equals("6")) {	
					current = gc;
				}

				data.add(gc);
			}
			rs.close();
			}
		LOGGER.info("Finished all the General Season List Loading!!");
		GeneralSeasonList gcl = new GeneralSeasonList(data);
		gcl.setCurrentSeason(current);
		return gcl;
		
	}

	/**
	 * 
	 * @param data
	 */
    public GeneralSeasonList(List<GeneralSeason> data) {  
        super(data);  
    }  
    
    @Override  
    public GeneralSeason getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<GeneralSeason> results = (List<GeneralSeason>) getWrappedData();  
          
        for(GeneralSeason result : results) {  
            if(Integer.toString(result.ID).equals(rowKey))  
                return result;  
        }  
        return null;  
    }  
  
	@Override
	public Object getRowKey(GeneralSeason arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the priorSeason
	 */
	public GeneralSeason getPriorSeason() {
		return PriorSeason;
	}

	/**
	 * @param priorSeason the priorSeason to set
	 */
	public void setPriorSeason(GeneralSeason priorSeason) {
		PriorSeason = priorSeason;
	}

	/**
	 * @return the currentSeason
	 */
	public GeneralSeason getCurrentSeason() {
		return CurrentSeason;
	}

	/**
	 * @param currentSeason the currentSeason to set
	 */
	public void setCurrentSeason(GeneralSeason currentSeason) {
		CurrentSeason = currentSeason;
	}
	
	public GeneralSeason getGeneralSeason(int _key) {
		@SuppressWarnings("unchecked")
		List<GeneralSeason> results = (List<GeneralSeason>) getWrappedData();  
	    for(GeneralSeason result : results) {  
	    	if (result.ID == _key)  return result;
	    }  
	      
	    return null;
	}

	/**
	 * 
	 */
	public String toString() {
		String answer = "";
	  @SuppressWarnings("unchecked")
	  List<GeneralSeason> results = (List<GeneralSeason>) getWrappedData();  
      for(GeneralSeason result : results) {  
    	  answer = answer +  "GeneralSeason: " + result + ContextManager.NEW_LINE;
      }  
      
      return answer;
	}

	
}