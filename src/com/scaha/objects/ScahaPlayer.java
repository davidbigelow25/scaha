/**
 * 
 */
package com.scaha.objects;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * @author dbigelow
 *
 */
public class ScahaPlayer extends Person {
	
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private boolean goalie = false;

	//
	// Lets hold the playerID here.. and allow the super to hold the person...
	//
	protected int ID = 0;
	
	/**
	 * A basic constructor that glues a person to a player given the relationship.
	 * This assumes new record status.. 
	 * @param _id
	 * @param _per
	 * @param _relationship
	 */
	public ScahaPlayer (Profile _pro) {
		
		//
		// Here we are starting with basically an empty shell of an object..
		// all id's are zero
		//
		super(-1, _pro);
		ID = -1;
	}
	
	public ScahaPlayer(Profile _pro, Person _per) {
		super( _pro, _per);
		//
		// now .. how do you take on the 
		
	}
	
	
	
	public void update(ScahaDatabase _db) throws SQLException {
		
		//
		// Lets update the person here.
		//
		super.update(_db);
	
		// 
		// is it an object that is not in the database yet..
		//
		
		CallableStatement cs = _db.prepareCall("call scaha.updatePlayer(?,?,?,?,?)");
		
		//LOGGER.info("HERE IS THE Person ID:" + super.ID);

		int i = 1;
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(i++, this.ID);
		cs.setInt(i++, super.ID);
		cs.setInt(i++, (this.isGoalie() ? 1 : 0));
		cs.setInt(i++,1);
		cs.setString(i++,null);
		cs.execute();
				
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(1);
		cs.close();
		//LOGGER.info("HERE IS THE Player ID:" + this.ID);
		
	}

	/**
	 * @return the goalie
	 */
	public boolean isGoalie() {
		return goalie;
	}

	/**
	 * @param goalie the goalie to set
	 */
	public void setGoalie(boolean goalie) {
		this.goalie = goalie;
	}



}
