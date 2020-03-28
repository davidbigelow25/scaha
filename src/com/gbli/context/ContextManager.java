/**
 * 
 */
package com.gbli.context;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


import com.gbli.common.SendMailSSL;
import com.gbli.connectors.Database;
import com.gbli.connectors.DatabasePool;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.logging.MyLogger;
import com.scaha.objects.Profile;

/**
 * This is an interem object that manages the context of this application.  It gets / sets app parms 
 * and runs the database pooling, etc.
 * 
 * To Do List:
 * 
 * 1) Migrate to an Application Scope Bean for a Web Based appl
 * 
 * @author dbigelow
 * 
 */
public class ContextManager implements ServletContextListener {

	public static String NEW_LINE = System.getProperty("line.separator");
	//
	// Static Member Variables
	//
	private static final String ATTRIBUTE_NAME = "config";  // Give this config a handle to the system
	private static boolean c_bLoaded = false;    			// Make shift block to determine if the context has completed loading
	private static String c_sPath = null;					// What is the absolute path of the running app
	private static ServletContext c_context;				// This is the Context of the Servlet App
	private static String c_sLoggerContext = null;			// Used to determine logger Name
	private static Logger c_Logger = null;  				// Used to initialize the logger
	private static Hashtable<String, DatabasePool> c_hDBPools = new Hashtable<String, DatabasePool>();  // Used to hold a map of database pools
	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.ServletContextListener#contextInitialized(javax.servlet
	 * .ServletContextEvent)
	 */
	@Override
	public void contextInitialized(ServletContextEvent _contextEvent) {
		
		//
		// We want to make sure this is the only thread running this section.  Vs getting two race conditions.
		//
		synchronized (this) {

			if (!c_bLoaded) {
				
				c_context = _contextEvent.getServletContext();  // Retrieve the Context from the Startup
				c_context.setAttribute(ATTRIBUTE_NAME, this);  	// Set this as the context 
				c_sPath = c_context.getRealPath("/");  			// Get the real path
				c_sLoggerContext = "scaha";						// Set the logger context

				//
				// STEP 1 Lets Get the Logger Established right now
				//
				c_Logger = Logger.getLogger(c_sLoggerContext);

				try {
					MyLogger.setup();
				} catch (Exception e) {
					e.printStackTrace();
				}

				//c_Logger.info("Context Created...");
				//c_Logger.info("AbsolutePath is: " + c_context.getRealPath("/"));
				//c_Logger.info("Check List Initiated...");

				// Set up Scaha Database Database Pool 
				// Lets always default to two connections in the pool..
				//
				Integer ipc = 2;
				try {
					Context ic;
					ic = new InitialContext();
					ipc = (Integer) ic.lookup("java:comp/env/scaha/dbpoolcount");
					//c_Logger.info("**** Pool Count is..." + ipc);
					
					String sMailAuth = (String)ic.lookup("java:comp/env/scaha/emailauth");
					String[] saMailAuth = sMailAuth.split(":");
					SendMailSSL.setUsername(saMailAuth[0]);
					SendMailSSL.setPassword(saMailAuth[1]);
					//c_Logger.info("***** Mail Info is:" + SendMailSSL.getUsername() + ", " + SendMailSSL.getPassword());
					ic.close();
				} catch (NamingException e) {
					e.printStackTrace();
				}
				
				//
				DatabasePool dbp2 = new DatabasePool(ScahaDatabase.class.getSimpleName(),ipc.intValue());
				c_hDBPools.put(ScahaDatabase.class.getSimpleName(),dbp2);
				Thread th2 =  new Thread(dbp2);
				dbp2.setMyThread(th2);
				th2.start();
			
				//c_Logger.info("Check List Completed...");
				c_bLoaded = true;
				
			} else {
				
				//c_Logger.info("Systems already Initialized...");
			}
		}

	}

	/**
	 * When the context is unhooked from the Web App Server.. we need to clean things up.
	 * Here is where we can do alot to null out all objects and session/app info 
	 * and perform a final garbage collection process to make sure there are no membery leaks 
	 * 
	 * Again, this will be part of an application context session bean.. 
	 * 
	 * To Do:
	 * 
	 * 	1) Clean up each database connection
	 * 	2) Clean up the logger
	 *  3) Make sure all referenece variable in this object are nulled out
	 */
	@Override
	public void contextDestroyed(ServletContextEvent _contextEvent) {

		//
		// Right now.. lets loop through all the database pools and request shutdown
		//
		//
		// We have to make sure each database object is properly closing and cleaning up all the connections
		//
		for (Enumeration<DatabasePool> en = c_hDBPools.elements(); en.hasMoreElements();) {
			DatabasePool dp = en.nextElement();
			//c_Logger.info("Found a Database Pool to Shut Down!");
			Thread th = dp.getMyThread();
			dp.shutdown();
			th.interrupt();
			try {
				//c_Logger.info("Joining Database Pool Thread for shutdown wait...");
				th.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			//
			// Lets Shut down the logger handles and close files..
			//
			
			try {
				MyLogger.shutdown();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			c_hDBPools.clear();
			c_Logger = null;
		}	

	}

	public static String getRealPath() {
		return c_sPath;
	}
	
	public static String getLoggerContext() {
		return c_sLoggerContext;
	}
	
	//
	// This hands out a good database connection
	//
	public static Database getDatabase(String _sClassName, Profile _pro) {
		return c_hDBPools.get(_sClassName).getDatabase(_pro);
	}
	//
	// This hands out a good database connection
	//
	public static Database getDatabase(String _sClassName) {
		return c_hDBPools.get(_sClassName).getDatabase();
	}
	
	/**
	 * This sets up the LOGGER for the given context
	 * 
	 * @param _strloggerContext
	 */
	public static void setLogger(String _strloggerContext) {
		//
		// STEP 1 Lets Get the Logger Established right now
		//
		
		c_sLoggerContext = _strloggerContext;
		c_Logger = Logger.getLogger(_strloggerContext);

		try {
			MyLogger.setup();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
