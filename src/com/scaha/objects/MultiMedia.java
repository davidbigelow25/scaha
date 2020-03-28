/**
 * 
 */
package com.scaha.objects;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialBlob;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

/**
 * @author dbigelow
 *
 */
public class MultiMedia extends ScahaObject implements Serializable {
	
	/**
	 *  All the private static information here.
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private int EntitytId = 0;
	private String EntityType = null;
	private String AttributeType = null;
	private String Extension = null;
	private String Updateed = null;
	private byte[] mmObject = null;

	
	public MultiMedia(Profile _pro, String _et, int _eid, String _at) {
		setProfile(_pro);
		EntityType = _et;
		EntitytId = _eid;
		AttributeType = _at;

	}
	
	/**
	 * @return the entitytId
	 */
	public int getEntitytId() {
		return EntitytId;
	}
	/**
	 * @param entitytId the entitytId to set
	 */
	public void setEntitytId(int entitytId) {
		EntitytId = entitytId;
	}
	/**
	 * @return the entityType
	 */
	public String getEntityType() {
		return EntityType;
	}
	/**
	 * @param entityType the entityType to set
	 */
	public void setEntityType(String entityType) {
		EntityType = entityType;
	}
	
	/**
	 * @return the extension
	 */
	public String getExtension() {
		return Extension;
	}
	/**
	 * @param extension the extension to set
	 */
	public void setExtension(String extension) {
		Extension = extension;
	}
	/**
	 * @return the updateed
	 */
	public String getUpdateed() {
		return Updateed;
	}
	/**
	 * @param updateed the updateed to set
	 */
	public void setUpdateed(String updateed) {
		Updateed = updateed;
	}
	/**
	 * @return the mmObject
	 */
	public byte[] getMmObject() {
		return mmObject;
	}
	/**
	 * @param mmObject the mmObject to set
	 * 
	 * Everytime we set this.. we need to update the streamed content
	 */
	public void setMmObject(byte[] mmObject) {
		this.mmObject = mmObject;
	}

	/**
	 * @return the attributeType
	 */
	public String getAttributeType() {
		return AttributeType;
	}

	/**
	 * @param attributeType the attributeType to set
	 */
	public void setAttributeType(String attributeType) {
		AttributeType = attributeType;
	}
	
	
	/**
	 * Update it back to the system..
	 * 
	 * @param _db
	 * @throws SQLException
	 */
	public void update(ScahaDatabase _db) throws SQLException {

		CallableStatement cs = _db.prepareCall("call scaha.updateMultiMedia(?,?,?,?,?,?,?)");
		int i=1;
		cs.setInt(i++, this.EntitytId);
		cs.setString(i++, this.EntityType);
		cs.setString(i++, this.AttributeType);
		cs.setString(i++, this.Extension);
		cs.setInt(i++,1);
		cs.setString(i++,null);
		cs.setBlob(i++, new SerialBlob(this.mmObject));
		cs.execute();
		cs.close();
	}
	
	/**
	 * Lets get from database if there is one! 
	 * @param _db
	 * @throws SQLException 
	 */
	public void get(PreparedStatement ps) throws SQLException {
		
		int i=1;
		ps.setInt(i++, this.EntitytId);
		ps.setString(i++, this.EntityType);
		ps.setString(i++, this.AttributeType);
		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			this.setExtension(rs.getString(1));
			Blob bl = rs.getBlob(2);
			this.setMmObject(bl.getBytes(1, (int) bl.length()));
		}
		rs.close();
		
	}
	
	public StreamedContent getStreamedContent() {
		 if (this.mmObject == null) {
			 //LOGGER.info("getStreamedContecnt... no mm object. returning DefaultStreameContent()");
			 return new DefaultStreamedContent();  
		 }
		 return new DefaultStreamedContent(new ByteArrayInputStream(this.mmObject),this.getExtension());
	}
	
	public int getMMLength() {
		if (this.mmObject == null) return 0;
		return this.mmObject.length;
	}

}
