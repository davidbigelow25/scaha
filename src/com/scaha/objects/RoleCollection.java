/**
 * 
 */
package com.scaha.objects;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * @author dbigelow
 *
 */
public class RoleCollection extends ScahaObject  {
	
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	static String sGetAllRoles = "call scaha.getAllRoles()";  // The returns all possible roles in the system
	static String sGetAllRolesByProfile = "call scaha.getAllRolesByProfile(?)";  // This just returns all direct roles
	static String sGetAllImpRolesByProfile = "call scaha.getAllImpliedRolesByProfile(?)";
	/**
	 * This creates a tree type object of all roles and their interrelations
	 * in the system
	 * @param _db
	 * @throws SQLException 
	 */
	public RoleCollection(ScahaDatabase _db)  throws SQLException {
		
		// We want to load them all up first.. in pass i
		// then link them all up in pass 2
		
		if (_db.getData(sGetAllRoles)) {
			ResultSet rs = _db.getResultSet();
			while (rs.next()) {
				int i = 1;
				int id = rs.getInt(i++);
				String sName = rs.getString(i++);
				String sDesc = rs.getString(i++);
				int iparentid = rs.getInt(i++);
				boolean idr = (rs.getInt(i++) == 1 ? true : false);
				// lets see if this role has already been stubbed out in this collection
				//LOGGER.info("getAllRoles:" + id + ":" + sName + ":" + sDesc +":" + iparentid + ":" + idr);
				Role rl = (Role)this.get(id,Role.class.getSimpleName());
				if (rl == null) {
					rl = new Role(id, sName, sDesc,idr);
					rl.initCollection();
					// put the role in this collection
					this.put(rl);
				} else {
					rl.setName(sName);
					rl.setDesc(sDesc);
					rl.setDefaultRole(idr);
				}
				Role prole = null;
				if (iparentid != 0) {
					prole = (Role)this.get(iparentid, Role.class.getSimpleName());
					if (prole == null) {
						prole = new Role(iparentid);
						prole.initCollection();
						this.put(prole);
					}
					rl.setParentRole(prole);
					prole.put(rl);
				}
				
			}
			
			rs.close();
			
			
		}
		
		
	}

	/**
	 * This creates a tree type object of all roles and their interrelations
	 * in the system
	 * @param _db
	 * @throws SQLException 
	 */
	public RoleCollection(ScahaDatabase _db, Profile _pro)  throws SQLException {
		
		// We want to load them all up first.. in pass i
		// then link them all up in pass 2
		Vector<Integer> vct = new Vector<Integer>();
		vct.add(new Integer(_pro.ID));
		
		
		if (_db.getData(sGetAllImpRolesByProfile,vct)) {
			ResultSet rs = _db.getResultSet();
			while (rs.next()) {
				int i = 1;
				int id = rs.getInt(i++);
				String sName = rs.getString(i++);
				String sDesc = rs.getString(i++);
				int iparentid = rs.getInt(i++);
				boolean idr = (rs.getInt(i++) == 1 ? true : false);
				// lets see if this role has already been stubbed out in this collection
				if (sName.equals("SUSER")) _pro.setSuperUser(true);
				Role rl = (Role)this.get(id,Role.class.getSimpleName());
				if (rl == null) {
					rl = new Role(id, sName, sDesc,idr);
					rl.initCollection();
					// put the role in this collection
					this.put(rl);
				} else {
					rl.setName(sName);
					rl.setDesc(sDesc);
					rl.setDefaultRole(idr);
				}
				Role prole = null;
				if (iparentid != 0) {
					prole = (Role)this.get(iparentid, Role.class.getSimpleName());
					if (prole == null) {
						prole = new Role(iparentid);
						prole.initCollection();
						this.put(prole);
					}
					rl.setParentRole(prole);
					prole.put(rl);
				}
			}
			rs.close();
		}
	}

}
