package com.gbli.connectors;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Vector;
import java.util.logging.Logger;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import com.gbli.context.ContextManager;
import com.scaha.objects.Profile;

import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Connection;

/**
 * Database - This is a basic Databases Connection Object
 * 
 * Based upon how it is Instantiated It will connect to a database. It will mark
 * itself busy.. and free itself up.. And have basic utilities as to managing
 * itself, running sql commands
 * 
 * 
 */
public class Database {

	//  Let Establish the Logging object for loggin information and data
	private final static Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	// In Use Boolean
	private boolean m_binuse = false;
	private String m_sName = "Database";
	private int m_iId = 0; // This is an identifier that help identifies it when
	private int m_iTxCount = 0;

	private String m_sDriver = null; // The Driver for the connection
	private String m_sURL = null; // The Connection URL..
									// "jdbc:sqlserver://airsk-sql-01:1433;databaseName=MEdat2011;"
	private String m_sUser = null; // The User Name to use in the connect
	private String m_sPwd = null; // The Password for the connection
	private Profile m_prof = null; // hold the profile of the caller.. gets set when we are in use.. gets cleared on the free

	// Declare the JDBC objects.
	private Connection m_con = null;
	private Statement m_stmt = null;
	private PreparedStatement m_pstmt = null;
	private CallableStatement m_cstmt = null;
	private ResultSet m_rs = null;
	private ResultSetMetaData m_rsmd = null;
	
	/**
	 * Cannot simply create this object w/o the required parms
	 */
	private Database() {

	}

	/**
	 * This is the main constructor. It sets up the entire object and makes it
	 * ready to be used. This is called typically from a Database Pool.. (why
	 * the ID number is passed)
	 * 
	 * @param _iId
	 * @param _sDriver
	 * @param _sURL
	 * @param _sUser
	 * @param _sPwd
	 * 
	 * @ToDo - We have to tighten up exception processing.. What happens when
	 *       the connection fails.
	 * 
	 */
	public Database(int _iId, String _sDriver, String _sURL, String _sUser,
			String _sPwd) {

		m_sName = this.getClass().getSimpleName();
		m_iId = _iId;

		// Establish the connection.
		m_sDriver = _sDriver;
		m_sURL = _sURL;
		m_sUser = _sUser;
		m_sPwd = _sPwd;

		this.primeConnection();

	}

	/**
	 * This is the main constructor. It sets up the entire object and makes it
	 * ready to be used. This is called typically NOT from a Database Pool..
	 * (why the ID number is missing)
	 * 
	 * @param _sDriver
	 * @param _sURL
	 * @param _sUser
	 * @param _sPwd
	 */
	public Database(String _sDriver, String _sURL, String _sUser, String _sPwd) {
		// Establish the connection.
		m_sDriver = _sDriver;
		m_sURL = _sURL;
		m_sUser = _sUser;
		m_sPwd = _sPwd;
		this.primeConnection();

	}

	/**
	 * Returns the Connection from this Database Object
	 * 
	 * @return
	 */
	public final Connection getConnection() {
		return m_con;
	}

	/**
	 * This sets the Database Object as inUse
	 */
	public void busy() {
		m_binuse = true;
	}

	/**
	 * This frees up the connection and releases all the dbresources. This keeps
	 * the connection live.
	 * 
	 */
	public void free() {

		try {
			this.cleanup();
			m_con.setAutoCommit(true); // Always set it back.. caller may have been lazy
		} catch (SQLException ex) {
			ex.printStackTrace();
			//LOGGER.info(this + "DB Free SNAFU");
			this.cleanup();
		} finally {
			//LOGGER.info(this + " Freeing Up Connection.");
			m_binuse = false;
			setProfile(null);
		}
	}

	public void cleanup() {

		try {
			m_rsmd = null;
			/*if (m_con != null){
				m_con.close();
				m_con=null;
			}*/
			if (m_rs != null) {
				m_rs.close();
				m_rs = null;
			}
			if (m_stmt != null) {
				m_stmt.close();
			}
			if (m_pstmt != null) {
				m_pstmt.close();
			}
			if (m_cstmt != null) {
				m_cstmt.close();
			}
		} catch (SQLException ex) {
			ex.printStackTrace();
			//LOGGER.info(this + "Clean up SNAFU");
		}

	}

	/**
	 * This clears everything up and closes everything down for the database..
	 * 
	 */
	public void close() {

		//LOGGER.info(this + "Close Out Connection:" + this.m_iId);
		
		try {
			this.m_binuse = true;
			this.cleanup();
			m_con.close();
		} catch (SQLException ex) {
			ex.printStackTrace();
			LOGGER.info(this + "DB Close SNAFU");
		}

	}

	/**
	 * This is a method that assumes no variables or Parms that need to be set
	 * from the SQL.
	 * 
	 * @param _sPath
	 * @return
	 */
	public boolean getDataFromSQLFile(String _sPath) {
		return this.getDataFromSQLFile(_sPath, null);
	}

	/**
	 * getDataFromSQLFile
	 * 
	 * This method assumes you provide the file name as an absolute file path..
	 * since this method needs to be context independent.
	 * 
	 * THis will populate the result set and its meta data counterpart for the
	 * caller to retrieve and do what they want
	 * 
	 * 
	 * @param _str
	 * @return
	 */
	public boolean getDataFromSQLFile(String _sPath, Vector _vParms) {

		String sAbsolutePath = ContextManager.getRealPath() + _sPath;
		LOGGER.info(this + ":" + sAbsolutePath);

		StringBuffer sb = new StringBuffer();

		try (BufferedReader br = new BufferedReader(new FileReader(
				sAbsolutePath))) {
			String sCurrentLine;
			while ((sCurrentLine = br.readLine()) != null) {
				sb.append(sCurrentLine);
			}
			br.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return getData(sb.toString(), _vParms);
	}

	/**
	 * 
	 * @param _sSQL
	 * @return
	 */
	public boolean getData(String _sSQL) {
		return this.getData(_sSQL, null);
	}

	/**
	 * This retrieves all data into the ResultSet and the ResultSetMetaData for
	 * the given SQL Statement
	 * 
	 * @param _strSQL
	 * @return
	 */
	public boolean getData(String _sSQL, Vector _vParms) {

		try {
			//
			// If there are parms.. we need to set up the SQL statement abit
			// better
			//
			if (_vParms != null) {

				//
				// We set based upon the type of data here...
				//
				m_pstmt = m_con.prepareStatement(_sSQL);
				for (int i = 0; i < _vParms.size(); i++) {
					m_pstmt.setObject(i + 1, _vParms.elementAt(i));
				}

				//
				// OK.. lets execute away
				m_rs = m_pstmt.executeQuery();
				// m_pstmt.closeOnCompletion();

			} else {

				m_stmt = m_con.createStatement();
				//
				// Here there are no parameters present..
				// So we will simply create and execute..
				m_rs = m_stmt.executeQuery(_sSQL);
				// m_pstmt.closeOnCompletion();

			}
			m_rsmd = m_rs.getMetaData();
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
		}

		return false;

	}
	
	
	/**
	 * This retrieves all data into the ResultSet and the ResultSetMetaData for
	 * the given SQL Statement
	 * 
	 * @param _strSQL
	 * @return
	 * @throws SQLException 
	 */
	
	public CallableStatement prepareCall(String _sql) throws SQLException {
		
		
		return m_cstmt = m_con.prepareCall(_sql);
		
	}


	public PreparedStatement prepareStatement(String _sql) throws SQLException {
		
		
		return this.m_pstmt = m_con.prepareStatement(_sql);
		
	}

	
	public void updateCallable(String _sSQL, Vector _vParms, Vector<String> _vDirection) throws SQLException {

		//
		// If there are parms.. we need to set up the SQL statement abit
		// better
		//
		if (_vParms != null) {

			//
			// We set based upon the type of data here...
			//
			this.m_cstmt = m_con.prepareCall(_sSQL);
			for (int i = 0; i < _vParms.size(); i++) {

				if (_vDirection.elementAt(i).equals("IN")) {
					m_cstmt.setObject(i + 1, _vParms.elementAt(i));
				} else if (_vDirection.elementAt(i).equals("INOUT")) {
					m_cstmt.setObject(i + 1, _vParms.elementAt(i));
					m_cstmt.registerOutParameter(i+1,this.getSQLType(_vParms.elementAt(i)));

				} else if (_vDirection.elementAt(i).equals("OUT")) {
					m_cstmt.registerOutParameter(i+1,this.getSQLType(_vParms.elementAt(i)));
				} else {
					m_cstmt.setObject(i + 1, _vParms.elementAt(i));
				}
				
			}

			m_pstmt.executeUpdate();

		} else {

			m_stmt = m_con.createStatement();
			m_stmt.executeUpdate(_sSQL);

		}

		
	}

	public ResultSet getResultSet() {
		return this.m_rs;
	}

	public ResultSetMetaData getResultSetMetaData() {
		return this.m_rsmd;
	}

	public Statement createStatement() throws SQLException {
		this.m_stmt = m_con.createStatement();
		return m_stmt;
	}

	public Statement getStatement() {
		return this.m_stmt;
	}

	public void commit() throws SQLException {
		if (!this.m_con.getAutoCommit())
			this.m_con.commit();
		if (this.m_stmt != null) {
			this.m_stmt.close();
			this.m_stmt = null;
		}
		if (this.m_pstmt != null) {
			this.m_pstmt.close();
			this.m_pstmt = null;
		}
		if (this.m_cstmt != null) {
			this.m_cstmt.close();
			this.m_cstmt = null;
		}
		if (this.m_rs != null) {
			this.m_rs.close();
			this.m_rs = null;
		}
	}

	/**
	 * This will clean out the connection and reset it..
	 * 
	 */
	public void reset() {

		LOGGER.info(this + "Cleaning and Reseting Connection...");
		this.close();
		this.primeConnection();

	}
	
	public boolean setAutoCommit(boolean _val) {
		LOGGER.info(this + "Setting autocommit to (" + _val + ")");
			try {
			m_con.setAutoCommit(_val);
			LOGGER.info(this + "Setting autocommit completed successfully to (" + m_con.getAutoCommit() + ")");
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info(this + "Setting autocommit failed..");
		return false;
		
	}
	
	public boolean rollback() {
		try {
			LOGGER.info(this + "ROLLBACK INITIATED!!!");
			m_con.rollback();
			LOGGER.info(this + "ROLLBACK COMPLETE!!!");
			return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info(this + "#### ERROR IN ROLLBACK!!!");
			e.printStackTrace();
		}
		return false;
	}

	private void primeConnection() {

		try {

			LOGGER.info(this + ":DRIVER:" + m_sDriver
					+ ".  Starting to instanciate...");
			Class.forName(m_sDriver).newInstance();
			LOGGER.info(this + ":DRIVER:" + m_sDriver + ".  Driver okay...");
			//m_con = DriverManager.getConnection(m_sURL, m_sUser, m_sPwd);
			//trying a fix for adding autoreconnect to prevent db connections from being closed by db
			m_con = DriverManager.getConnection(m_sURL + "?autoReconnect=true", m_sUser, m_sPwd);
			LOGGER.info(this + "Connection okay..");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isInUse() {
		return this.m_binuse;
	}

	public void setInUse(Profile _prof) {
		setProfile(_prof);
		//LOGGER.info(this + " is being placed in use");
		m_binuse = true;
		this.incTx();
	}
	
	public void incTx() {
		this.m_iTxCount++;
	}
	
	public int getTxCount() {
		return this.m_iTxCount;
	}

	/**
	 *  This checks to see if the connection was closes.. and reprimes it if it is closed.
	 */
	public void checkHeath() {
		try {
			if (m_con.isClosed()) {
				this.primeConnection();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Very simple mapping routine for SQL Type and Java Class
	 * 
	 * @param _obj
	 * @return
	 */
	public int getSQLType(Object _obj) {

		if (_obj instanceof Integer) {
			return java.sql.Types.INTEGER;
		} else {
			return java.sql.Types.VARCHAR;
			
		}
		
	}

	@Override
	public String toString() {
		return "DB[" + m_iId + "]:" + (m_binuse ? "busy" : "free") + "(" + (getProfile() == null ? "NPF" : getProfile()) + ")" + ":txcnt:" + this.getTxCount();
	}

	/**
	 * Lets see if we cannot update a pic!
	 * @param _cs
	 * @param bs
	 * @throws IOException
	 * @throws SerialException
	 * @throws SQLException
	 */
	public void updateMultiMedia(CallableStatement _cs, byte[] bs) throws IOException, SerialException, SQLException {
		
		final ByteArrayOutputStream baos = new ByteArrayOutputStream();        
		final ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(bs);
		_cs.setBlob(1, new SerialBlob(baos.toByteArray()));
		
	}
	
	public void setProfile(Profile _pro) {
		this.m_prof = _pro;
	}
	
	public Profile getProfile() {
		return this.m_prof;
	}
	
}
