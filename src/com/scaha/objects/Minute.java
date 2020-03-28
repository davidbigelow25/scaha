package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;

import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Minute extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String filename = null;
	private String meetingdate = null;
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String fam) {
		this.filename = fam;
	}
	
	public String getMeetingdate() {
		return meetingdate;
	}

	public void setMeetingdate(String fam) {
		this.meetingdate = fam;
	}
}
