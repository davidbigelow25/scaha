package com.gbli.logging;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.gbli.context.ContextManager;

/**
 * MyLogger
 * 
 * This handles all aspects of system logging.
 * Currently.. Everything is hard coded
 * We will need a resource/properties file to allow for on the fly configuration

 *
 */
public class MyLogger {
	
	
  static private FileHandler m_fileTxt;
  static private SimpleFormatter m_formatterTxt;

  static private FileHandler m_fileHTML;
  static private Formatter m_formatterHTML;
  
  
  private static Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
  
  public static void setup() throws IOException {
    
	  //
	  // Here we set up how we want it to run..
    LOGGER.setLevel(Level.SEVERE);
  
    SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-ddHHmmss");
    String sTimeStamp = date_format.format(new Date());
    System.out.println(sTimeStamp);
    m_fileTxt = new FileHandler("/var/log/tomcat/Logging." + sTimeStamp + ".txt");
    m_fileHTML = new FileHandler("/var/log/tomcat/Logging." + sTimeStamp + ".html");

    // Create text Formatter
    m_formatterTxt = new MyTxtFormatter();
    m_fileTxt.setFormatter(m_formatterTxt);
    LOGGER.addHandler(m_fileTxt);

    // Create HTML Formatter
    m_formatterHTML = new MyHtmlFormatter();
    m_fileHTML.setFormatter(m_formatterHTML);
    LOGGER.addHandler(m_fileHTML);
    
    LOGGER.info("Logger set to: " + LOGGER.getLevel());
  }

  /**
   * Shutting Down The logging Utility.
   * 
   * @throws IOException
   */
  public static void shutdown() throws IOException {

	  LOGGER.info("Shutting down Logger Utility.");
	  LOGGER.removeHandler(m_fileTxt);
	  LOGGER.removeHandler(m_fileHTML);
	  m_fileTxt.flush();
      m_fileTxt.close();
	  m_fileHTML.flush();
      m_fileHTML.close();
      m_fileHTML = null;
      m_fileTxt = null;
      m_formatterTxt = null;
      m_formatterHTML = null;
      LOGGER = null;
	  
  }
  
  
  
 } 

