package com.scaha.beans;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.mysql.cj.x.protobuf.MysqlxCursor;
import com.scaha.objects.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

@ManagedBean
@ViewScoped
public class clubopeningsBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private Integer profileid = 0;
	private List<Opening> openinglist = null;
	private List<Opening> newopeninglist = null;
	private Opening selectedopening = null;
	private Opening newselectedopening = null;
	private Integer clubid = null;
	private String clubname = null;
	private List<Division> divisions= null;
	private List<SkillLevel> skilllevels= null;
	private List<Venue> venues = null;
	private List<Team> listofteams = null;
	private Boolean bcopylist = null;


	@ManagedProperty(value = "#{profileBean}")
	private ProfileBean pb = null;
	@ManagedProperty(value = "#{scahaBean}")
	private ScahaBean scaha = null;

	transient private ResultSet rs = null;
    
	@PostConstruct
    public void init() {
		
        this.setProfid(pb.getProfile().ID);
		getClubID();
        this.bcopylist = false;

        //need to load field look up data on page load
		loadOpeningList();
		ListofDivisions();
		ListofSkillLevels();
		ListofVenues();

	}

	public List<Venue> getVenues(){
		return venues;
	}

	public void setVenues(List<Venue> list){
		venues = list;
	}

	public List<Division> getDivisions(){
		return divisions;
	}

	public void setDivisions(List<Division> list){
		divisions = list;
	}

	public List<SkillLevel> getSkilllevels(){
		return skilllevels;
	}

	public void setSkilllevels(List<SkillLevel> list){
		skilllevels = list;
	}

	public List<Opening> getOpeninglist() {
		return openinglist;
	}

	public void setOpeninglist(List<Opening> openinglist) {
		this.openinglist = openinglist;
	}
	public Opening getSelectedopening() {
		return selectedopening;
	}

	public void setSelectedopening(Opening openinglist) {
		this.selectedopening = openinglist;
	}
	public List<Opening> getNewopeninglist() {
		return newopeninglist;
	}

	public void setNewopeninglist(List<Opening> openinglist) {
		this.newopeninglist = openinglist;
	}
	public Opening getNewselectedopening() {
		return newselectedopening;
	}

	public void setNewselectedopening(Opening openinglist) {
		this.newselectedopening = openinglist;
	}

	public String getClubname(){
		return clubname;
	}

	public void setClubname(String sclub){
		clubname = sclub;
	}

	public Integer getClubid(){
		return clubid;
	}

	public void setClubid(Integer sclub){
		clubid = sclub;
	}
	public List<Team> getListofteams(){
    	return listofteams;
    }
    
    public void setListofteams(List<Team> list){
    	listofteams = list;
    }
    
    public Integer getProfid(){
    	return profileid;
    }
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    

	    
    	/**
	 * @return the pb
	 */
	public ProfileBean getPb() {
		return pb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setPb(ProfileBean pb) {
		this.pb = pb;
	}

	/**
	 * @return the scaha
	 */
	public ScahaBean getScaha() {
		return scaha;
	}

	/**
	 * @param scaha the scaha to set
	 */
	public void setScaha(ScahaBean scaha) {
		this.scaha = scaha;
	}

	public void getClubID(){

		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.getProfid());
			db.getData("CALL scaha.getClubforPerson(?)", v);

			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();

				while (rs.next()) {
					this.clubid = rs.getInt("idclub");
				}
				rs.close();
				//LOGGER.info("We have results for club for a profile");
			}

			//now lets retrieve club name
			v = new Vector<Integer>();
			v.add(clubid);
			db.getData("CALL scaha.getClubNamebyId(?)", v);

			if (db.getResultSet() != null){
				//need to add to an array
				rs = db.getResultSet();

				while (rs.next()) {
					clubname = rs.getString("clubname");
				}
				rs.close();
				//LOGGER.info("We have results for club name");
			}

			db.cleanup();
		} catch (SQLException e) {
			// TODO nnfo("ERROR IN loading club by profile");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

	}
	public void loadOpeningList(){
		List<Opening> data = new ArrayList<Opening>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");


		try{

			CallableStatement cs = db.prepareCall("CALL scaha.getClubOpeningInfoforMaintenance(?)");
			cs.setInt(1,this.clubid);
			ResultSet rs = cs.executeQuery();


			while (rs.next()) {
				Opening o = new Opening();
				Integer idvision = rs.getInt("division");
				o.setDivision(idvision.toString());
				Integer ilevel = rs.getInt("level");
				o.setSkilllevel(ilevel.toString());
				Integer ilocation = rs.getInt("location");
				o.setRink(ilocation.toString());
				o.setOpeningcount(rs.getString("openingcount"));
				o.setContactname(rs.getString("contactname"));
				o.setContactemail(rs.getString("contactemail"));
				o.setOpeningid(rs.getInt("idclubopenings"));
				data.add(o);
			}
			//LOGGER.info("We have results for tryouts for club");
			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading team history");
			e.printStackTrace();
		} finally {
			db.free();
		}

		this.setOpeninglist((data));

		//need to set a blank opening for the new opening list
		if (!bcopylist) {
			Opening no = new Opening();
			no.setDivision("0");
			no.setSkilllevel("0");
			no.setRink("");
			no.setOpeningcount("");
			no.setContactname("");
			no.setContactemail("");
			no.setOpeningid(0);
			List<Opening> newdata = new ArrayList<Opening>();
			newdata.add(no);
			this.setNewopeninglist(newdata);
		}
	}

	public void ListofDivisions(){
		List<Division> templist = new ArrayList<Division>();
		List<String> sdivs = new ArrayList<String>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			CallableStatement cs = null;

			cs = db.prepareCall("CALL scaha.getDivisionsForSCAHA()");

			ResultSet rs = cs.executeQuery();
			while (rs.next()) {
				Integer iddivision = rs.getInt("iddivisions");
				String divisionname = rs.getString("division_name");
				Division div = new Division();
				div.setDivisionname(divisionname);
				div.setIddivision(iddivision);
				templist.add(div);
				sdivs.add(divisionname);
			}
			rs.close();
			cs.close();
		} catch (SQLException e) {
			LOGGER.info("ERROR IN loading teams");
			e.printStackTrace();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		this.setDivisions(templist);

	}

	public void ListofSkillLevels(){
		List<SkillLevel> templist = new ArrayList<SkillLevel>();
		List<String> sdivs = new ArrayList<String>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			CallableStatement cs = null;

			cs = db.prepareCall("CALL scaha.getSkilllevelsForSCAHA()");


			ResultSet rs = cs.executeQuery();

			if (rs != null){
				//need to add to an array
				//rs = db.getResultSet();

				while (rs.next()) {
					Integer idskilllevel = rs.getInt("idskilllevels");
					String levelsname = rs.getString("levelsname");

					SkillLevel level = new SkillLevel();
					level.setSkilllevelname(levelsname);
					level.setIdskilllevel(idskilllevel);
					sdivs.add(levelsname);
					templist.add(level);
				}
			}
			rs.close();
			db.cleanup();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading teams");
			e.printStackTrace();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}


		this.setSkilllevels(templist);

	}

	public void ListofVenues(){
		List<Venue> templist = new ArrayList<Venue>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			CallableStatement cs = null;

			cs = db.prepareCall("CALL scaha.getClubVenueInfo(?)");
			cs.setInt(1,this.clubid);

			ResultSet rs = cs.executeQuery();

			if (rs != null){
				//need to add to an array
				//rs = db.getResultSet();

				while (rs.next()) {
					Integer idvenue = rs.getInt("idvenue");
					String description = rs.getString("description");

					Venue nvenue = new Venue();
					nvenue.setVenueid(idvenue);
					nvenue.setDescription(description);
					templist.add(nvenue);
				}
			}
			rs.close();
			db.cleanup();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading teams");
			e.printStackTrace();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}


		this.setVenues(templist);

	}

	public void saveOpening(Opening opening){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		if (opening.getOpeningid()==0){
			this.bcopylist=false;
		}
		try{

			CallableStatement cs = null;

			cs = db.prepareCall("CALL scaha.saveOpening(?,?,?,?,?,?,?,?)");
			cs.setInt("in_divisionid",Integer.parseInt(opening.getDivision()));
			cs.setInt("in_skilllevelid",Integer.parseInt(opening.getSkilllevel()));
			cs.setInt("in_locationid",Integer.parseInt(opening.getRink()));
			cs.setString("in_openingcount",opening.getOpeningcount());
			cs.setString("in_contactname",opening.getContactname());
			cs.setString("in_contactemail",opening.getContactemail());
			cs.setInt("in_clubid",this.clubid);
			rs = cs.executeQuery();

			if (rs != null){
				//need to add to an array
				//rs = db.getResultSet();

				while (rs.next()) {
					opening.setOpeningid(cs.getInt(1));
				}
			}
			rs.close();
			db.cleanup();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading teams");
			e.printStackTrace();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		loadOpeningList();
		scaha.refreshClubList();
	}

	public void copyOpening(Opening opening){
		//need to update new openinglist with the values from the opening passed into the method.
		List<Opening> data = new ArrayList<Opening>();
		opening.setOpeningid(0);
		data.add(opening);
		this.setNewopeninglist(data);
		this.bcopylist = true;
		this.loadOpeningList();
	}

	public void deleteOpening(Opening opening){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			CallableStatement cs = null;

			cs = db.prepareCall("CALL scaha.deleteOpening(?)");
			cs.setInt("inout_Idopening",opening.getOpeningid());
			cs.executeQuery();

			db.cleanup();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading teams");
			e.printStackTrace();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		loadOpeningList();
		scaha.refreshClubList();
	}
}

