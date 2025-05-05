package com.scaha.objects;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import org.primefaces.expression.impl.ThisExpressionResolver;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;


/**
 * This Presents the user in our business system.  It contains all the keys and permissions that this user 
 * has access to throughout the system
 * @author dbigelow
 *
 */
public class Profile extends ScahaObject {
	
	//
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	//
	// Member Variables
	//
	private String m_sUser = null;
	private String m_sPass = null;
	private RoleCollection m_rc = null;
	private String m_sNickName = null;
	private ActionList m_al = null;
	private Person m_per = null;
	private ScahaManager m_sman = null;
	private boolean SuperUser = false;
	private Integer managerteamid = null;
	private List<Team> managerteams = null;
	private Integer clubid = 0;


	public Profile (int _id, ScahaDatabase _db, String _sNN, String _sUser, String _sPass, boolean _getActionRoles) {
		//LOGGER.info("setting profile" + _sUser);
		this.ID = _id;
		m_sNickName = _sNN;
		m_sUser = _sUser;
		m_sPass = _sPass;

		
		try {
			//LOGGER.info("getting new person" + _sUser);
			// Lets get the Person...
			m_per = new Person(_db, this);
			//LOGGER.info("finished new person" + _sUser);
			//LOGGER.info("checking action roles" + _sUser);
			if (_getActionRoles) {
				// Lets get the action List...
				//LOGGER.info("starting new action list" + _sUser);
				m_al = new ActionList(this);   // needs to use passed db connection...      
				// What roles do they have ?  Non hierarchical
				//LOGGER.info("finished new action list" + _sUser);
				//LOGGER.info("starting role collection" + _sUser);
				m_rc = new RoleCollection(_db, this);
				//LOGGER.info("finishing role collection" + _sUser);
			}


			//need to instantiate the scahamanager class to be used by when the manager is working on the managerportal
			//need to check if they are a register, if so set them up as manager as well.
			//LOGGER.info("setting scaha manager" + _sUser);
			this.setScahamanager(new ScahaManager(this));
			//LOGGER.info("finished setting scaha manager, setting manager team id" + _sUser);
			this.setManagerteamid(this.getScahamanager().getManagerteamid(this.ID));
			//LOGGER.info("finished setting manager team id, checking role list for c-reg" + _sUser);
			/*if (this.hasRoleList("C-REG",this)){

			}*/
			//LOGGER.info("checking if role list again for c-reg" + _sUser);
			if (this.hasRoleList("C-REG",this)) {
				//LOGGER.info("is c-reg setting is manager" + _sUser);
				this.getScahamanager().ismanager = true;
				//LOGGER.info("finished setting c-reg to manager" + _sUser);

				//LOGGER.info("getting club id for registrar" + _sUser);
				this.setClubid(getClubIDForRegistrar());
				//LOGGER.info("getting club teams for registrar to set to manager" + _sUser);
				this.setManagerteams(getClubteams(this.getClubid()));

			}else {
				//LOGGER.info("setting manager teams" + _sUser);
					this.setManagerteams((this.getScahamanager().getManagerteams(this.ID)));
			}
			//this.setScahamanager(new ScahaManager(this));*/

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//LOGGER.info("error in setting profile" + _sUser + e.toString());
			e.printStackTrace();
			_db.cleanup();
		}
		//LOGGER.info("settting db object to cleanup and free" + _sUser);
		_db.cleanup();
		_db.free();
	}

	/**
	 *  This is used to create a shell of a profile
	 *  
	 *  This will be saved eventually..
	 *  
	 * @param _sUser
	 * @param _sPass
	 * @param _sNN
	 */
	public Profile(String _sUser, String _sPass, String _sNN) {

		m_sNickName = _sNN;
		m_sUser = _sUser;
		m_sPass = _sPass;
		this.ID = 0;

	}
	

	/**
	 * This is a dummy profile that is used for all object that do not need any form
	 * of security. 
	 * @param i
	 */
	public Profile(int i) {
		// TODO Auto-generated constructor stub
		this.ID = i;
	}

	/**
	 * Here we need to simply get a new ID Number.. that is negative to denote a new Object vs
	 * one in the database
	 */
	public Profile() {
		this.ID = Profile.getNewID();
	}

	/**
	 * verify - Gathers the profile information from the target system
	 * If it returns false.. authentication failed.
	 * 
	 * @return boolean
	 */
	//
	public static final Profile verify(String _sUser, String _sPass) {

		//
		// get an iSiteDatabase Connection..
		//
		//LOGGER.info("starting profile verify routine instantiating db" + _sUser + ',' + _sPass);

		ScahaDatabase db = (ScahaDatabase)ContextManager.getDatabase("ScahaDatabase");


		ResultSet rs = null;
		Profile prof = null;
		boolean bgood = false;
		
		//
		// If this comes back true.. we have a good result set to play with and fill out the profile
		//
		int id  = -1;
		String sNickName = null;
		try {
			//LOGGER.info("calling db.verify sp" + _sUser + ',' + _sPass);
			if (db.verify(_sUser, _sPass)) {
				//LOGGER.info("got thru db.verify sp" + _sUser + ',' + _sPass);

				rs = db.getResultSet();
				//LOGGER.info("getting result set " + _sUser + ',' + _sPass);

				if (rs.next()) {
					//LOGGER.info("iterating thru result set of db.verify" +_sUser);
					id = rs.getInt(1);
					//LOGGER.info("getting id" +id);

					sNickName = rs.getString(2);
					//LOGGER.info("getting nickname" + sNickName);

					bgood = true;
				}
			}
		
		} catch (SQLException ex) {
			LOGGER.info("errored in db.verify call" + _sUser + ex.toString());
				ex.printStackTrace();
				db.cleanup();
				db.free();
		} finally {
			db.cleanup();
			db.free();
			//LOGGER.info("finishing db verify" + _sUser);
		}
		
		// Lets generate the profile
		//
		//LOGGER.info("checking if we have profile" + _sUser + ',' + bgood);
		if (bgood) {
			//LOGGER.info("instantiating x` object" + _sUser);
			prof =  new Profile (id, db, sNickName, _sUser, _sPass, true);
			//LOGGER.info("finished instantiating profile" + _sUser);
			//LOGGER.info("starting db.setprofile" + _sUser);
			db.setProfile(prof);
			//LOGGER.info("finished db.setprofile" + _sUser);

		}
		//LOGGER.info("setting db.free" + _sUser);
		db.free();
		return prof;
				
	}

	/**
	 * 
	 * @return
	 */
	public final String getNickName() {
		return this.m_sNickName;
	}
	
	
	public final String getUserName() {
		return this.m_sUser;
	}
	
	public void setUserName(String _semail) {
		this.m_sUser = _semail;
	}

	public void setNickName(String _sNickName) {
		this.m_sNickName = _sNickName;
	}
	
	public void setLivePassword(String _sNP) {
		this.m_sPass = _sNP;
	}
	
	public void setManagerteams(List<Team> value){
		this.managerteams = value;
	}
	
	public List<Team> getManagerteams(){
		return this.managerteams;
	}
	
	/**
	 * gets the actionlist associated with the given Profile.
	 * THe user can peruse this structure to find out what actions 
	 * this particular Profile has access to
	 * @return
	 */
	public final ActionList getActionList() {
		return this.m_al;
	}
	
	public ScahaManager getScahamanager() {
		return this.m_sman;
	}

	public void setScahamanager(ScahaManager _man) {
		this.m_sman = _man;
	}
	
	/**
	 * This returns the person object from the profile
	 * 
	 */
	public Person getPerson() {
		return this.m_per;
	}

	public void setPerson(Person _per) {
		this.m_per = _per;
	}

	@SuppressWarnings("unchecked")
	public List<Role> getRoles() {
		return this.m_rc.getList();
	}
	
	public String toString() {
		return this.m_sUser + "[" + this.getNickName() + "]";
	}
	
	
	/**
	 * This guy will take an existing database connection and update and or insert the record..
	 * 
	 * @param db
	 */
	public void update(ScahaDatabase db) throws SQLException {
		
		// 
		// is it an object that is not in the database yet..
		//
		
		CallableStatement cs = db.prepareCall("call scaha.updateprofile(?,?,?,?,?,?,?)");
		
		cs.setInt(1, this.ID);
		cs.setString(2, this.m_sUser);
		cs.setString(3, this.m_sPass);
		cs.setString(4, this.m_sNickName);
		cs.setInt(5,1);
		cs.setString(6,null);
		cs.registerOutParameter(7, java.sql.Types.INTEGER);
		cs.execute();

		//LOGGER.info(this + ": Has just " + (this.ID < 1 ? " created " : " updated ") + " their profile information.");
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(7);
		cs.close();
		
		db.free();

	}
	
	/**
	 * Is the passed peson the profile owner?
	 * 
	 * We need to know this to ensure that only the profile owner uses the idprofile when saving the person..
	 * 
	 * @param _per
	 * @return
	 */
	public boolean isProfileOwner(Person _per) {
		return this.m_per.ID == _per.ID;
	}

	/**
	 * @return the superUser
	 */
	public boolean isSuperUser() {
		return SuperUser;
	}

	/**
	 * @param superUser the superUser to set
	 */
	public void setSuperUser(boolean superUser) {
		SuperUser = superUser;
	}
	
	/**
	 * This performs a topical cleanup..
	 * 
	 */
	public void clear () {
		
		super.clear();
		this.m_al.clear();
		this.m_al = null;
		this.m_rc.clear();
		this.m_rc = null;
		this.m_per = null;
	}
	
	public Integer getManagerteamid(){
		return this.managerteamid;
	}
	
	public void setManagerteamid(Integer _teamid){
		this.managerteamid=_teamid;
	}

	public boolean hasRoleList(String _strRoles, Profile pro) {
		if (pro == null) return false;
		String[] roles = _strRoles.split(";");
		for (String role : roles) {
			if (!role.equals("T-MAN")){
				for (Role myrole : pro.getRoles()) {
					if (myrole.getName() != null && myrole.getName().equals(role)) return true;
				}
			} else {
				if (this.getProfile().getScahamanager().getIsmanager()){
					return true;
				}
				else {
					return false;
				}
			}
		}
		return false;
	}

	public List<Team> getClubteams(Integer clubid){
		//first lets get club id for the logged in profile
		List<Team> data = new ArrayList<Team>();
		//LOGGER.info("starting getclubteams");
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try{
			//LOGGER.info("starting getteamsbyclubid");
			Vector<Integer> v = new Vector<Integer>();
			v.add(clubid);
			db.getData("CALL scaha.getTeamsbyClubId(?)", v);
			ResultSet rs = db.getResultSet();
			//LOGGER.info("iterating thru the teams");
			while (rs.next()) {
				Team tm = new Team(rs.getString("teamname"),rs.getString("idteams"));
				data.add(tm);
				tm = null;
			}

			rs.close();
			rs = null;
			//LOGGER.info("We have results for teams for a club");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//LOGGER.info("ERROR IN loading teams for a club" + e.toString());
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
			//LOGGER.info("finishing loading teams for a club");
		}

		return data;
	}

	public Integer getClubID(){

		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		Integer clubid = 0;
		try{
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.ID);
			db.getData("CALL scaha.getclubformanager(?)", v);
			ResultSet rs = db.getResultSet();
			while (rs.next()) {
				clubid = rs.getInt("idclub");
			}
			rs.close();
			//LOGGER.info("We have results for club for a profile");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			//LOGGER.info("ERROR IN loading club by profile");
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		return clubid;
	}

	public Integer getClubIDForRegistrar(){
		LOGGER.info("starting gettting club id for registrar");
		//first lets get club id for the logged in profile
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		Integer clubid = 0;
		try{
			//LOGGER.info("calling getclubforregistrar");
			Vector<Integer> v = new Vector<Integer>();
			v.add(this.ID);
			db.getData("CALL scaha.getclubforregistrar(?)", v);
			ResultSet rs = db.getResultSet();
			//LOGGER.info("iterating thru roleset");
			while (rs.next()) {
				clubid = rs.getInt("idclub");
			}
			rs.close();
			rs = null;
			//LOGGER.info("finishing get club for registrar");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading club for registrar" + e.toString());
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		//LOGGER.info("finished get club for registrar");

		return clubid;
	}

	public Integer getClubid() {
		return clubid;
	}

	public void setClubid(Integer clubid) {
		this.clubid = clubid;
	}
}

