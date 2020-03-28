package com.scaha.objects;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;


public class ActionList extends ScahaObject{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private Hashtable<Integer,Action> m_hActions = new Hashtable<Integer,Action>();
	private Hashtable<Integer,Action> m_hTopActions = new Hashtable<Integer,Action>();

	private Action m_curAct = null;
	
	/**
	 * We simply get a complete action list for the given profile that maps out
	 * what kinds of things they can do based upon their profile
	 * 
	 * @param _pro
	 */
	
	/**
	 * This constructor will simply instantiate a completed Action List at the top level of the action tree for the given Profile..
	 * 
	 * @param _pro
	 */
	public ActionList (Profile _pro) {

		//LOGGER.info("Generating ActionList for Profile " + _pro);
		setProfile(_pro);
		this.ID = 0;
		this.refresh();
		
	}
	public void refresh() {
		
		//
		// Lets reset some things here.
		//
		m_hActions.clear();
		this.m_hTopActions.clear();
		//
		// get an iSiteDatabase Connection..
		//
		ScahaDatabase db = (ScahaDatabase)ContextManager.getDatabase("ScahaDatabase", this.getProfile());
		ResultSet rs = null;
		
		//
		// If this comes back true.. we have a good result set to play with and fill out the profile
		//
		
		try {
			if (db.getActionData(super.getProfile().ID)) {
				rs = db.getResultSet();
				//
				// These are all the actions for the given profile.. We need to stuff them to preserve the parent child relationship
				// This is a flattened set of actions.. Ste
				while (rs.next()) {
					Action act = new Action(rs.getInt(1), rs.getInt(2), rs.getString(3),rs.getString(4), rs.getString(5), rs.getString(6));
					//
					// if this is a top level action.. lets put it in the top level list as well as the normal list
					if (rs.getInt(2) == 0) {
						this.putTopLevelAction(act);
					} 
					this.putAction(act);
				}
			} 
		} catch (SQLException ex) {
				ex.printStackTrace();
		} finally {
			db.free();
		}
		
		//
		// Now.. we link them all up!! for all levels.
		//
		// We iterate through them once.. and search to find all the children nodes by key and add them to that actions
		// child list.
		//
		// Lets iterate through the list
		// THis is slow.. but it will work for now..
		
		// This is the who's my parent game..
		
		Enumeration<Integer> keys = this.m_hActions.keys();
		while( keys.hasMoreElements() ) {
		  Integer key = keys.nextElement();
		  Action act = m_hActions.get(key);
		  Action actPar = m_hActions.get(act.getPID());
		  if (actPar != null) {
			  actPar.putChildAction(act);
		  }
		}

	}
	//
	// Get a list of actions that are children of the Existing Action by Key
	//
	public Action[] getChildActionsbyKey(int _iKey) {
		
		if (_iKey == 0) {
			return this.m_hTopActions.values().toArray(new Action[this.m_hTopActions.size()]);
			
		} else {
			return this.m_hActions.get(Integer.valueOf(_iKey)).getChildActionsAsArray();
		}

	}
	
	//
	// Put and action in the action list..
	//
	
	public void putAction(Action _act) {
		this.m_hActions.put(Integer.valueOf(_act.ID), _act);
	}
	
	//
	// Return Action from Action List
	//
	
	public Action getAction(int _ikey) {
		return this.m_hActions.get(Integer.valueOf(_ikey));
	}
	
	
	//
	// Put and action in the action list..
	//
	
	public void putTopLevelAction(Action _act) {
		this.m_hTopActions.put(Integer.valueOf(_act.ID), _act);
	}

	//
	// Set the current action
	//
	public void setCurAction(Action _act) {
		this.m_curAct = _act;
	}
}

