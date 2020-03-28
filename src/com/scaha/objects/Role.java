/**
 * 
 */
package com.scaha.objects;

import java.io.Serializable;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;


/**
 * Role  This holds a basic role object from the database
 * 
 * @author dbigelow
 *
 */
public class Role extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	
	private String Name = null;
	private String Desc = null;
	private boolean DefaultRole = false;
	
	private Role ParentRole = null;  
	
	public Role(int _id, String _strName, String _strDescription, boolean _dr) {
		Name = _strName;
		Desc = _strDescription;
		this.DefaultRole = _dr;
		this.ID = _id;
	}
	
	public Role (int _id) {
		this.ID = _id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return Name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		Name = name;
	}

	/**
	 * @return the desc
	 */
	public String getDesc() {
		return Desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		Desc = desc;
	}

	/**
	 * @return the parentRole
	 */
	public Role getParentRole() {
		return ParentRole;
	}

	/**
	 * @param parentRole the parentRole to set
	 */
	public void setParentRole(Role parentRole) {
		ParentRole = parentRole;
	}
	
	/**
	 * @return the defaultRole
	 */
	public boolean isDefaultRole() {
		return DefaultRole;
	}

	/**
	 * @param defaultRole the defaultRole to set
	 */
	public void setDefaultRole(boolean defaultRole) {
		DefaultRole = defaultRole;
	}

	public String toString() {
		return this.Desc + "(" + this.Name.trim() + ")"; 
	}
	
	public boolean isSuperUserRole() {
		return this.Name.equals("SUSER");
	}
	
}
