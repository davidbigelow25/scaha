package com.scaha.objects;

import java.io.Serializable;
import java.util.logging.Logger;

import com.gbli.context.ContextManager;

public class Slot extends ScahaObject implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Slot [idTeamAgainst=" + idTeamAgainst + ", actDate=" + actDate
				+ ", StartTime=" + StartTime + ", ID=" + ID + "]";
	}

	
	int idTeamAgainst  =  0;
	String actDate = null;
	String StartTime = null;

	public Slot (int _id, String _actDate, String _StartTime, int _idT) {
		ID = _id;
		actDate = _actDate;
		StartTime = _StartTime;
		idTeamAgainst = _idT;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}



}