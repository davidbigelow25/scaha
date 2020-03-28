package com.scaha.objects;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * Ok.. This is a great way for 
 * @author David
 *
 */
public class TeamList extends ListDataModel<ScahaTeam> implements Serializable, SelectableDataModel<ScahaTeam> {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	public TeamList() {  
    }  
  
    public TeamList(List<ScahaTeam> data) {  
        super(data);  
    }  
    
    /**
  	 * This will get an empty TeamList
  	 * @param _db
  	 * @return
  	 * @throws SQLException 
  	 */
  	public static TeamList ListFactory() throws SQLException {
  		
  		List<ScahaTeam> data = new ArrayList<ScahaTeam>();
  		return new TeamList(data);
  	}
  		
    /**
	 * This will get all ScahaTeams for a given Club and return a TeamList
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public static TeamList NewTeamListFactory(Profile _pro, ScahaDatabase _db, Club _cl, GeneralSeason _gs,boolean _badmin, boolean _roster) throws SQLException {
		
		List<ScahaTeam> data = new ArrayList<ScahaTeam>();
		
		PreparedStatement pst = _db.prepareStatement("call scaha.getAllTeamsByClubAndSeason(?,?)");
		PreparedStatement ps = _db.prepareStatement("call scaha.getAllCoachByTeam(?)");
		PreparedStatement ps2 = _db.prepareStatement("call scaha.getAllManagerByTeam(?)");
		
		
		pst.setInt(1, _cl.ID);
		pst.setString(2, _gs.getTag());
		ResultSet rs = pst.executeQuery();
		while (rs.next()) {
			
			
			int i = 1;
			ScahaTeam tm = new ScahaTeam(_pro,rs.getInt(i++));
			tm.setSname(rs.getString(i++));
			tm.setTeamname(rs.getString(i++));
			tm.setTeamgender(rs.getString(i++));
			int idSkill = rs.getInt(i++);
			tm.setSkillleveltag(rs.getString(i++));
			int idDivs = rs.getInt(i++);
			tm.setDivisiontag(rs.getString(i++));
			tm.setIsexhibition(rs.getInt(i++));
			tm.setSeasontag(rs.getString(i++));
			tm.setYear(rs.getInt(i++));
			tm.setScheduletags(rs.getString(i++));
			int idSkill2 = rs.getInt(i++);
			SkillLevel sl = new SkillLevel(_pro, idSkill);
			sl.setSkilllevelname(rs.getString(i++));
			sl.setTag(rs.getString(i++));
			Division div = new Division(_pro,idDivs);
			int idiv2 = rs.getInt(i++);
			div.setDivisionname(rs.getString(i++));
			div.setTag(rs.getString(i++));
			tm.setXdivisiontag(rs.getString(i++));
			tm.setXskillleveltag(rs.getString(i++));
			tm.setSdivision(div.getDivisionname());
			tm.setSskilllevel(sl.getSkilllevelname());
			//
			// We will worry about creating the other objects later.. they are reference objects
			// and we need to be carefull not to create a ton of copies floating around
			//
			// they are the skilllevel, divisions, and seasons.
			tm.setTeamdivision(div);
			tm.setTeamskilllevel(sl);
			tm.setTeamClub(_cl);
			data.add(tm);
		}
		rs.close();
			
		//
		// ok.. if we need to get all the admin staff.. then list go get this right now!
		//
		// For any team.. the admin staff is coaches.. assistant Coaches.. and Managers
		// for any team..
		if (_badmin) {
			for (ScahaTeam tm : data) {
				tm.setCoachs(CoachList.NewCoachListFactory(_pro, ps, tm));
				tm.setManagers(ManagerList.NewManagerListFactory(_pro, ps2, tm));
			}
		}
				
		pst.close();
		ps.close();
		ps2.close();
		return new TeamList(data);
	}

      
    @Override  
    public ScahaTeam getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<ScahaTeam> results = (List<ScahaTeam>) getWrappedData();  
        for(ScahaTeam result : results) {  
        	if(Integer.toString(result.ID).equals(rowKey)) return result;  
        }  
          
        return null;  
    }  
 
    public ScahaTeam getScahaTeamAt(int rowKey) {  
          
        @SuppressWarnings("unchecked")
		List<ScahaTeam> results = (List<ScahaTeam>) getWrappedData();  
        for(ScahaTeam result : results) {  
        	if(result.ID == rowKey) return result;  
        }  
          
        return null;  
    }  
    @Override  
    public Object getRowKey(ScahaTeam result) {  
        return Integer.toString(result.ID);  
    }

	@SuppressWarnings("unchecked")
	public void addTeamsToList(TeamList tl) {
		List<ScahaTeam> data = (List<ScahaTeam>) this.getWrappedData();
		List<ScahaTeam> newdata = (List<ScahaTeam>) tl.getWrappedData();
		data.addAll(newdata);
	}  
	
	public String toString() {
		String answer = "";
	  @SuppressWarnings("unchecked")
	  List<ScahaTeam> results = (List<ScahaTeam>) getWrappedData();  
      for(ScahaTeam result : results) {  
    	  answer = answer +  "Team: " + result + ContextManager.NEW_LINE;
      }  
      
      return answer;
	}
	
}