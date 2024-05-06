/**
 * 
 */
package com.scaha.objects;

import com.gbli.context.ContextManager;

import java.io.Serializable;
import java.util.logging.Logger;

/**
 * 
 * Opening is an individual team opening .  
 * 
 * It simply holds their division, level, # of openings, contact name, contact email, host rink, for a given clubs opening.
 * @author rfoster
 *
 */
public class SCAHAPosition extends ScahaObject implements Serializable {


	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String positionname = null;
	private Integer idposition = null;
	private String positioncode = null;

	public SCAHAPosition() {
	
	}

	public String getPositioncode() {
		return positioncode;
	}

	public void setPositioncode(String positioncode) {
		this.positioncode = positioncode;
	}

	public String getPositionname() {
		return positionname;
	}

	public void setPositionname(String positionname) {
		this.positionname = positionname;
	}

	public Integer getIdposition() {
		return idposition;
	}

	public void setIdposition(Integer idposition) {
		this.idposition = idposition;
	}
}
