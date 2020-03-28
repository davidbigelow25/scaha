package com.scaha.objects;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.common.GeneralAttributes;
import com.gbli.context.ContextManager;

public abstract class ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static int newID = 0;
	private Profile m_pro = null;
	private List<String> options = null;
	private boolean retire = false;
	//
	//  This represents the Key for the object
	//
	public int ID = -1;
	
	
	
	
	
	//
	// Scaha Object have Inherent Collections..
	//
	Vector<ScahaObject> vct = new Vector<ScahaObject>();
	Hashtable<String,ScahaObject> hsh = new Hashtable<String, ScahaObject>();
	private GeneralAttributes genatt = new GeneralAttributes();

	public ScahaObject get(int _i, String _strObjClass) {
		return hsh.get(_i + ":" + _strObjClass);
	}
	
	public ScahaObject getAt(int _index) {
		return vct.elementAt(_index);
	}
	
	public int getSize() {
		return vct.size();
	}
	
	public void put(ScahaObject _so) {
		hsh.put(_so.ID + ":" + _so.getClass().getSimpleName(), _so);
		vct.add(_so);
	}
	
	public void remove (int _id,String _strObjClass) {
		ScahaObject so = hsh.remove(_id + ":" + _strObjClass);
		vct.removeElement(so);
	}

	public void removeAt (int _index) {
		ScahaObject so = vct.elementAt(_index);
		vct.removeElement(so);
		hsh.remove(so.ID + ":" +so.getClass().getSimpleName());
	}
	
	public List getList() {
		return new ArrayList(vct);
	}

	public void initCollection() {
		
		if (vct != null) {
			vct.removeAllElements();
		}
		
		if (hsh != null) {
			hsh.clear();
		}

		vct = new Vector<ScahaObject>();
		hsh = new Hashtable<String, ScahaObject>();
	}
	
	public void setProfile (Profile _pro) {
		m_pro = _pro;
	}

	public Profile getProfile () {
		return m_pro;
	}

	/**
	 * @return the genatt
	 */
	public GeneralAttributes getGenatt() {
		return genatt;
	}

	/**
	 * @param genatt the genatt to set
	 */
	public void setGenatt(GeneralAttributes genatt) {
		this.genatt = genatt;
	}
	
	public String getGenAttByKey(String _key) {
		return genatt.get(_key);
	}
	
	/**
	 * close out and set everything to null
	 */
	public void clear() {
		
		this.vct.clear();
		this.hsh.clear();
		this.m_pro = null;
		this.genatt.clear();

	}
	
	protected static synchronized int getNewID() {
		return ScahaObject.newID++;
	}

	/**
	 * @return the options
	 */
	public List<String> getOptions() {
		return options;
	}

	/**
	 * @param options the options to set
	 */
	public void setOptions(List<String> options) {
		this.options = options;
	}

	/**
	 * @return the retire
	 */
	public boolean isRetire() {
		return retire;
	}

	/**
	 * @param retire the retire to set
	 */
	public void setRetire(boolean retire) {
		this.retire = retire;
	}
	
}
