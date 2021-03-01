/**
 * 
 */
package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * 
 * ClubAdmin is purely a derived object based upon the Person
 * 
 * It simply holds their roletag and their role description.. for a given club.
 * @author dbigelow
 *
 */
public class ClubAdmin extends Person implements Serializable {
	
	
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private Role MyRole = null;
	private Club MyClub = null;
	private Boolean renderphone = null;
	private Boolean renderaddress = null;
	private String actionrenderaddress = null;
	private String actionrenderphone = null;

	public ClubAdmin(int _id, Profile _pro, Club _cl, Role _role) {
		super(_id, _pro);
		MyRole = _role;
		MyClub = _cl;
	}
	
	public void update(ScahaDatabase _db) throws SQLException {
		
		// There is no updating this!
		// right now.. unless an admin updates it on their behalf.. down the road.
		
		//
		// Lets update the person here.
		//
		//super.update(_db);
	}

	public Boolean getRenderphone(){return this.renderphone;}
	public void setRenderphone(Boolean value){this.renderphone=value;}
	/**
	 * @return the myRole
	 */
	public Role getMyRole() {
		return MyRole;
	}


	/**
	 * @param myRole the myRole to set
	 */
	public void setMyRole(Role myRole) {
		MyRole = myRole;
	}


	/**
	 * @return the myClub
	 */
	public Club getMyClub() {
		return MyClub;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.MyClub.getSname() + " " + this.MyRole.getDesc() + " - " + this.getsFirstName() + " " + this.getsLastName();
	}

	/**
	 * @param myClub the myClub to set
	 */
	public void setMyClub(Club myClub) {
		MyClub = myClub;
	}



	public String getActionrenderphone() {
		return actionrenderphone;
	}

	public void setActionrenderphone(String fam) {
		this.actionrenderphone = fam;

	}

	public String getActionrenderaddress() {
		return actionrenderaddress;
	}

	public void setActionrenderaddress(String fam) {
		this.actionrenderaddress = fam;
	}

	public Boolean getRenderaddress() {
		return renderaddress;
	}

	public void setRenderaddress(Boolean fam) {
		this.renderaddress = fam;
	}



}
