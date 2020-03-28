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
 * @author David
 *
 */
public class NewsItem extends ScahaObject implements Serializable {

	/**
	 * Get the obligatory stuff for serializable
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private String title = null;
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "NewsItem [title=" + title + ", subject=" + subject
				+ ", author=" + author + ", updated=" + updated + ", body="
				+ body + ", ID=" + ID + ", vct=" + vct + ", hsh=" + hsh + "]";
	}

	private String subject = null;
	private String author = null;
	private String updated = null;
	private String body = null;
	private String intro = null;
	private String refid = null;
	
	public NewsItem (Profile _pro) {
		this.ID = -1;
		this.setProfile(_pro);
	}

	public NewsItem (int _id, Profile _pro) {
		this.ID = _id;
		this.setProfile(_pro);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

	public String getIntro() {
		return intro;
	}

	/**
	 * @param title the title to set
	 */
	public void setIntro(String intro) {
		this.intro = intro;
	}

	public String getRefid() {
		return refid;
	}

	/**
	 * @param title the title to set
	 */
	public void setRefid(String intro) {
		this.refid = intro;
	}
	
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the author
	 */
	public String getAuthor() {
		return author;
	}

	/**
	 * @param author the author to set
	 */
	public void setAuthor(String author) {
		this.author = author;
	}

	/**
	 * @return the updated
	 */
	public String getUpdated() {
		return updated;
	}

	/**
	 * @param updated the updated to set
	 */
	public void setUpdated(String updated) {
		this.updated = updated;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}
	
	public String getIDStr() {
		return this.ID + "";
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}

	public void update(ScahaDatabase _db) throws SQLException {
		
		CallableStatement cs = _db.prepareCall("call scaha.updateNewsItem(?,?,?,?,?,?,?,?)");
		
		int i = 1;
		cs.registerOutParameter(1, java.sql.Types.INTEGER);
		cs.setInt(i++, this.ID);
		cs.setString(i++,this.getAuthor());
		cs.setString(i++, this.getTitle());
		cs.setString(i++, this.getSubject());
		cs.setString(i++, "publish");
		cs.setString(i++, this.getBody());
		cs.setInt(i++, 1);										//	IN in_isactive tinyint,
		cs.setString(i++, null);								//	IN in_updated timestamp
		
		cs.execute();
		
		//
		// Update the new ID from the database...
		//
		this.ID = cs.getInt(1);
		cs.close();
		_db.cleanup();
	}
}
