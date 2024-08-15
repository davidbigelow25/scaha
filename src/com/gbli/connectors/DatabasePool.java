package com.gbli.connectors;
import java.util.Vector;
import java.util.logging.Logger;
import com.gbli.context.ContextManager;
import com.scaha.objects.Profile;

/**
 * @author dbigelow
 *
 */
public class DatabasePool implements Runnable {
	
	private final static Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private int m_iCount = 2;	// The dnumber of database connections in the pool
	private int m_itxSum = 0;

	private Object c_olock = new Object();
	private Object dbGetLock = new Object();

	private boolean m_bshutdown = false;
	private String m_sName = null;
	private Vector<Database> m_vConnections = new Vector<Database>();
	
	private Thread m_thMyThread = null;
	
	public DatabasePool (String _str, int _ipoolcount) {
		
		m_iCount = _ipoolcount;   // Lets set up the count for the number of connections in the pool

		//
		// Lets set up all the information here
		//
	
		synchronized (c_olock) {
			
			setName(_str);
			
			// This needs to be moved  to the web.xml file...
			//
			if (_str.equals(ScahaDatabase.class.getSimpleName())) {
				for (int i=0; i < m_iCount;i++) {
					m_vConnections.add(new ScahaDatabase(i,
							"com.mysql.cj.jdbc.Driver",
							//"jdbc:mysql://scaha-dev.cb8ss84o0mjb.us-west-1.rds.amazonaws.com",//scaha-dev
							 "jdbc:mysql://scaha-prod.cb8ss84o0mjb.us-west-1.rds.amazonaws.com",//scaha-prod

																//"jdbc:mysql://192.241.229.21:3306/scaha", //scaha old

							//"jdbc:mysql://192.241.229.21:3306",
//							"jdbc:mysql://192.241.211.230:3306/scaha",  // original site
							"scaha", "shiloh24"));
				}
				
			}
			
		}
		
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub

		//
		// Initialize the pool
		
		// Sleep and wake up to report on Pool

		
		while (!m_bshutdown) {
			
			LOGGER.info("======================================================================================");
			LOGGER.info("DB Pool ("  + getName() + ") dbcount is (" + m_iCount + ") Connections.  txCnt:" + this.m_itxSum);
			LOGGER.info("======================================================================================");
			for (int i=0; i < m_iCount;i++) {
				LOGGER.info(this.m_vConnections.get(i).toString());
			}
			LOGGER.info("======================================================================================");
			try {
				Thread.sleep(60000);
			} catch (InterruptedException  ex) {
				LOGGER.info("Database Pool Interrupt on Thread Detected..");
				ex.printStackTrace();
			}
		}
		
		LOGGER.info("Database Pool Shudown Request Detected..");
		
		//
		// Lets close out the connections..
		//
		for (int i=0; i < m_iCount;i++) {
			Database db = m_vConnections.get(i);
			db.close();
			LOGGER.info(this.m_vConnections.get(i).toString() + ":" + "Closing out connection..");
		}

		//
		//  gracefull shutdown.
		//
		m_vConnections.clear();
		
	}
	
	/**
	 * Provide a reference to my own thread..
	 */

	public void setMyThread(Thread _th) {
		this.m_thMyThread = _th;
	}

	/**
	 * What is the Thread my watch dog is running under?
	 * @return
	 */
	public Thread getMyThread() {
		return m_thMyThread;
	}
	
	/**
	 * This will set the flag for a shutdown request...
	 */
	public void shutdown() {
		
		this.m_bshutdown = true;
		
	}

	private void closeHungConnections(){
		//
		// Lets close out the connections..
		//
		for (int i=0; i < m_iCount;i++) {
			Database db = m_vConnections.get(i);
			db.free();
			LOGGER.info(this.m_vConnections.get(i).toString() + ":" + "Closing out connection..");
		}
	}
	/**
	 * Here we search for the next available Database Connection
	 * Mark it as inuse
	 * And hand it back
	 * 
	 * The caller is responsible for freeing releasing the database back to the pool.
	 * 
	 * @return
	 */
	public Database getDatabase(Profile _pro) {
		this.m_vConnections.size();
		int icount = 0;
		Database db = null;
		while (db == null && icount < 10) {
			for (int i=0; i < this.m_iCount;i++) {
				db = this.m_vConnections.get(i);
				synchronized (dbGetLock) {
					if (!db.isInUse()) {
						db.checkHeath();
						db.setInUse(_pro);
						this.incTxCount();
						return db;
					}
				}
			}
			icount++;
			try {
				//thread to sleep for the specified number of milliseconds
				LOGGER.info(" All connections busy.. sleeping for a bit...");
				closeHungConnections();
			 	Thread.sleep(5000);
  		    } catch ( java.lang.InterruptedException ie) {
				LOGGER.info(" All connections busy.. woke up afer wait...");
            };
		}
		return null;
	}

	
	public Database getDatabase() {
		Profile tmp = new Profile();
		tmp.setUserName("private");
		tmp.setNickName("transient");
		return this.getDatabase(tmp);
		
		
	}
	public void incTxCount() {
		this.m_itxSum++;
	}
	
	public int getIxCount() {
		return this.m_itxSum;
	}
	/**
	 * @return the m_sName
	 */
	public String getName() {
		return m_sName;
	}
	/**
	 * @param m_sName the m_sName to set
	 */
	public void setName(String m_sName) {
		this.m_sName = m_sName;
	}
}
