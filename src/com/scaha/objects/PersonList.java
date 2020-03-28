package com.scaha.objects;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


import javax.faces.model.ListDataModel;

import org.primefaces.model.SelectableDataModel;

import com.gbli.common.GeneralAttributes;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
;

public class PersonList extends ListDataModel<Person> implements Serializable, SelectableDataModel<Person> {
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	

	public PersonList() {  
    }  
	
	/**
	 * 
	 * @param data
	 */
    public PersonList(List<Person> data) {  
        super(data);  
    }  

	
	/**
	 * This will get all Coaches for a given Team and Season
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public static PersonList NewPersonListFactory(Profile _pro, ScahaDatabase _db,  UsaHockeyRegistration  _usah) throws SQLException {
	
		
		//LOGGER.info ("USAR:" + _usah.toString());
		//LOGGER.info ("USAR:FIRST NAME:" + _usah.getFirstName());
		//LOGGER.info ("USAR:LAST NAME:" + _usah.getLastName());
		//LOGGER.info ("USAR:DOB:" + _usah.getDOB());

		int loopcount = 0;
		List<Person> data = new ArrayList<Person>();

		boolean goodparms = true;
		
		if (_pro.getPerson() == null) {
			//LOGGER.info("**SERVER ERRROR ****, Profile getPerson returns nothing");
			goodparms = false;
		} else if (_pro.getPerson().getFamily() == null) {
			//LOGGER.info("**SERVER ERRROR ****, Person in the Profile Has No Family Structure.. THis is bad!!");
			goodparms = false;
		}
		
		if (!goodparms) {
			return new PersonList(data);
		}

		/*PreparedStatement ps = _db.prepareStatement("call scaha.getPersonbyUSAHockeyMatchFNLNDOB(?,?,?)");*/
		PreparedStatement ps = _db.prepareStatement("call scaha.getPersonbyUSAHockeyMatchLNDOBYear(?,?,?)");

		//
		// OK.. we simply want to seach until we get a hit.. The first SQL is the most likely candidate
		// All subsequent calls are based upon an empty answer from the previous iteration..
		//
		boolean loadnew = false;  // We always load a new person when 1) nothing found.. or if loop 3 or 4 has been triggered 
		while (true) {
			loopcount++;
			if (loopcount ==1) {
				ps.setString(1,_usah.getFirstName());
				ps.setString(2,_usah.getLastName());
				ps.setString(3,_usah.getDOB());
			} else if (loopcount == 2  && data.isEmpty()) {
				ps.setString(1,"NOOP");
				ps.setString(2,_usah.getLastName());
				ps.setString(3,_usah.getDOB());
				loadnew = true;
			} else if (loopcount == 3 && data.isEmpty()) {
				ps.setString(1,_usah.getFirstName());
				ps.setString(2,_usah.getLastName());
				ps.setString(3,"NOOP");
				loadnew = true;  // load new person placeholder
//			} else if (loopcount == 4 && data.isEmpty()) {
//					ps.setString(1,"NOOP");
//					ps.setString(2,_usah.getLastName());
//					ps.setString(3,"NOOP");
//					loadnew = true;  // load new person placeholder
			} else {
				break;
			}
			ResultSet rs = ps.executeQuery();
			//need to check if first, last, and dob change, if so add person to person list.
			Integer newpersonid = 0;
			Integer oldpersonid = null;
			
			while (rs.next()) {
				int i = 1;
				newpersonid = rs.getInt(i++);
				Person per = new Person(newpersonid,_pro);
				
				
				
				//LOGGER.info("Person ID is:" + per.ID);
				per.setsFirstName(rs.getString(i++));
				per.setsLastName(rs.getString(i++));
				per.setsEmail(rs.getString(i++));
				per.setsPhone(rs.getString(i++));
				per.setsAddress1(rs.getString(i++));
				per.setsCity(rs.getString(i++));
				per.setsState(rs.getString(i++));
				per.setiZipCode(rs.getInt(i++));
				per.setGender(rs.getString(i++));
				per.setDob(rs.getString(i++));
				per.setCitizenship(rs.getString(i++));
				String strRelType = rs.getString(i++);
				int tmpFamilyid = rs.getInt(i++);
				String strFamilyName = rs.getString(i++);
				String strIsPlayer = rs.getString(i++);
				String strIsCoach = rs.getString(i++);
				String strIsManager = rs.getString(i++);
				String strIsGoalie = rs.getString(i++);
				per.setNotes(rs.getString(i++));
				
				
				boolean found = false;
				for(Person result : data) {  
		            if(result.ID == per.ID) {
		            	found=true;
		            	per = result;
		            	break;
			        }  
				}
				
				/*if (!found) { */
				if (!newpersonid.equals(oldpersonid)){
					oldpersonid=newpersonid;
					data.add(per);
				}
				
				
					/*String  strNotes = "<li>This is an existing member to SCAHA</li>";
					if (tmpFamilyid != _pro.getPerson().getFamily().ID) {
						strNotes = strNotes + "<li>The person is <b>*** NOT ***</b> part of your family account<br/>Use Caution when completing this membership.</li>";
						per.getGenatt().put("INFAMILY","false");
					} else {
						per.getGenatt().put("INFAMILY","true");
					}
					if (per.ID == _pro.getPerson().ID) {
						strNotes = strNotes + "<li>This is a selfie - meaning It is You :)</li>";
					}*/
					
					if (strRelType.equals("")){
						per.getGenatt().put("RELTYPE",strRelType);
					}else {
						per.getGenatt().put("RELTYPE","(" + strRelType + ")" );
					}
					
					per.getGenatt().put("FAMILYNAME",strFamilyName);
					per.getGenatt().put("ISPLAYER",strIsPlayer);
					per.getGenatt().put("ISCOACH",strIsCoach);
					per.getGenatt().put("ISMANAGER",strIsManager);
					per.getGenatt().put("ISGOALIE",strIsGoalie);
				/*}*/
				
			}
			rs.close();
			
			//
			// need to create an option for new record as well.. 
			// 
			//
			Person per = new Person(0,_pro);
			per.setsFirstName("Create New Member");
			per.setsLastName("");
			per.setsEmail("");
			per.setsPhone("");
			per.setsAddress1("");
			per.setsCity("");
			per.setsState("");
			per.setiZipCode(0);
			per.setGender("");
			per.setDob("");
			per.setCitizenship("");
			per.setNotes("");
			per.getGenatt().put("RELTYPE","");
			per.getGenatt().put("FAMILYNAME","");
			per.getGenatt().put("ISPLAYER","");
			per.getGenatt().put("ISCOACH","");
			per.getGenatt().put("ISMANAGER","");
			per.getGenatt().put("ISGOALIE","");
			data.add(per);
		}
		ps.close();


		if (data.isEmpty() || loadnew) {
			Person per = new Person(_pro);		// This will be the new person
			per.gleanUSAHinfo(_usah);
			per.getGenatt().put("RELTYPE","New Member");
			per.getGenatt().put("FAMILYNAME",_pro.getPerson().getFamily().getFamilyName());
			per.getGenatt().put("NOTES", "<li><b>THIS IS A NEW MEMBER TO THE SYSTEM</b></li>");
			per.getGenatt().put("ISPLAYER","N");
			per.getGenatt().put("ISCOACH","N");
			per.getGenatt().put("ISMANAGER","N");
			per.getGenatt().put("ISGOALIE","N");
			per.getGenatt().put("INFAMILY","true");
			data.add(per);
		}

		return new PersonList(data);
	}

	
	/**
	 * This will get all Coaches for a given Team and Season
	 * @param _db
	 * @return
	 * @throws SQLException 
	 */
	public static PersonList NewPersonListFactory(ScahaDatabase _db,  String _strLastName) throws SQLException {
	
		
		//LOGGER.info ("getPersonWithProfileByLastNamePattern:" + _strLastName);
		List<Person> data = new ArrayList<Person>();
		PreparedStatement ps = _db.prepareStatement("call scaha.getPersonWithProfileByLastNamePattern(?)");
		ps.setString(1,_strLastName);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			Person per = new Person(rs.getInt(i++));
			//LOGGER.info("Person ID is:" + per.ID);
			per.setsFirstName(rs.getString(i++));
			per.setsLastName(rs.getString(i++));
			per.setsEmail(rs.getString(i++));
			per.setsPhone(rs.getString(i++));
			per.setsAddress1(rs.getString(i++));
			per.setsCity(rs.getString(i++));
			per.setsState(rs.getString(i++));
			per.setiZipCode(rs.getInt(i++));
			per.setGender(rs.getString(i++));
			per.setDob(rs.getString(i++));
			per.setCitizenship(rs.getString(i++));
			data.add(per);
		}
		rs.close();
		ps.close();
		
		return new PersonList(data);
	}
    
    @Override  
    public Person getRowData(String rowKey) {  
        //In a real app, a more efficient way like a query by rowKey should be implemented to deal with huge data  
          
        @SuppressWarnings("unchecked")
		List<Person> results = (List<Person>) getWrappedData();  
          
        for(Person result : results) {  
            if(Integer.toString(result.ID).equals(rowKey))  
                return result;  
        }  
        return null;  
    }  
  
    @Override  
    public Object getRowKey(Person result) {  
        return Integer.toString(result.ID);
    }

    /**
     * Pull back a list of persons matching the persn ID
     * 
     * @param _db
     * @param _number
     * @return
     * @throws SQLException 
     */
	public static PersonList NewPersonListFactory(ScahaDatabase _db, int _number) throws SQLException {
		//LOGGER.info ("getPersonByID:" + _number);
		List<Person> data = new ArrayList<Person>();
		PreparedStatement ps = _db.prepareStatement("call scaha.getPersonByID(?)");
		ps.setInt(1,_number);
		ResultSet rs = ps.executeQuery();
		while (rs.next()) {
			int i = 1;
			Person per = new Person(rs.getInt(i++));
			//LOGGER.info("Person ID is:" + per.ID);
			per.setsFirstName(rs.getString(i++));
			per.setsLastName(rs.getString(i++));
			per.setsEmail(rs.getString(i++));
			per.setsPhone(rs.getString(i++));
			per.setsAddress1(rs.getString(i++));
			per.setsCity(rs.getString(i++));
			per.setsState(rs.getString(i++));
			per.setiZipCode(rs.getInt(i++));
			per.setGender(rs.getString(i++));
			per.setDob(rs.getString(i++));
			per.setCitizenship(rs.getString(i++));
			data.add(per);
		}
		rs.close();
		ps.close();
		
		return new PersonList(data);
	}
}
