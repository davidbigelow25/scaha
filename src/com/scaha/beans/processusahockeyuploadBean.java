package com.scaha.beans;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import com.gbli.common.Utils;

import org.primefaces.event.FileUploadEvent;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.gbli.common.SendMailSSL;


@ManagedBean
@SessionScoped
public class processusahockeyuploadBean implements Serializable,  MailableObject {

	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	private Integer totalcount;
	private static String mail_reg_body_playerwithoutloi = Utils.getMailTemplateFromFile("/mail/isusahockeynoloi.html");
	transient private ResultSet rs = null;
	private String playername = "";
	private String teamname = "";
	private String to = null;
	private Integer clubid = null;
	private String cc = null;
	private String subject = null;
	private Integer recordid = null;



	public void setSubject(String ssubject){
		this.subject = ssubject;
	}



	 public String getTeamname() {
		return teamname;
	}

	public void setTeamname(String teamname) {
		this.teamname = teamname;
	}

	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return cc;
	}

	public void setPreApprovedCC(String scc){
		cc = scc;
	}



	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return to;
	}

	public Integer getRecordid() {
		return recordid;
	}

	public void setRecordid(Integer recordid) {
		this.recordid = recordid;
	}

	public void setToMailAddress(String sto){
		to = sto;
	}

	public String getPlayername() {
		return playername;
	}

	public void setPlayername(String playername) {
		this.playername = playername;
	}

	public Integer getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(Integer totalcount) {
		this.totalcount = totalcount;
	}

	/**
	 * @return the pb
	 */
	public ProfileBean getPb() {
		return pb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setPb(ProfileBean pb) {
		this.pb = pb;
	}

	// Class Level Variables
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	//
	// lets go get it!
	//
	public processusahockeyuploadBean() {
	}

	 @PostConstruct
	 public void init() {
		 
		 LOGGER.info(" *************** POST INIT FOR PROCESS USA HOCKEY ROSTER UPLOAD BEAN *****************");
		this.totalcount=0;
	 }
	 
	public Boolean handleFileUpload(FileUploadEvent event) {
		//need to add grabbing file name, file contents, then passing through to process method
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		InputStream stream = null;
		FileOutputStream output = null;

		String prefix = FilenameUtils.getBaseName(event.getFile().getFileName().replace("#", ""));
		String suffix = FilenameUtils.getExtension(event.getFile().getFileName());

		try {
			//first save file to the server
			String destPath ="C:/Program Files (x86)/Apache Software Foundation/Tomcat 8.5/webapps/uploadfiles/usahockeyimport/" + prefix + "." + suffix;
			File destFile = new File(destPath);
			stream = event.getFile().getInputstream();
			output = new FileOutputStream(destFile);

			IOUtils.copy(stream, output);
			Integer startstring = 0;
			Integer endstring = 0;
			String tempstring = "";
			String finalpartialstring = "";

			//now lets read the file and start parsing line by line
			BufferedReader br = new BufferedReader(new FileReader(destFile));
			Integer y=0;

			//this is used for incase there is an error.
			String errormessage = "";

			try{
				//setting stored procedures for the calls to be made when iterating thru
				CallableStatement cs = db.prepareCall("CALL scaha.logto_usahockeyrosterimportlog(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				CallableStatement cs2 = db.prepareCall("CALL scaha.logto_checkforloiusaroster(?,?)");
				CallableStatement cs3 = db.prepareCall("CALL scaha.getClubRegistrarandScahaRegistrarEmail(?)");

				//these are used to determine if player/coach has loi or not
				String usahockeynumber = "";
				String usahockeyteamnumber = "";
				Boolean isusahockeynoloi = false;

				for(String line; (line = br.readLine()) != null; ) {
					// process the line.
					errormessage = line;
					LOGGER.info("processing file line:" + line);

					//need to remove commas in coaching modules field//
					startstring = line.indexOf('"');
					endstring = line.lastIndexOf('"');

					//need to set values from usahockeynumber and usa teamnumber fields

						if (startstring>0) {
							tempstring = line.substring(startstring,endstring);
							finalpartialstring = tempstring.replace(",","");

							line = line.replace(tempstring,finalpartialstring);
						}

						//now split the string into separate fields
						String[] linearr = line.split(",");
						Integer x = 1;


						//check to see if it's the header row, if so don't insert headers//
						if (y!=0){
							//now submit all of the data to the stored procedure.  The sp will log to the log table,
							// then create a record in the matching loi record table or a record in no matching loi table
							//

							for (String value : linearr) {
								//need to add fields to cs for records for standard regular season teams
								cs.setString(x, value);

								//load value into usahockeynumber
								if (x==10){
									usahockeynumber=value;
								}

								//load value into usahockeyteamnumber
								if (x==27){
									usahockeyteamnumber=value;
								}
								x++;
							}

							//insert entire row into import logging table and return the player name
							rs = cs.executeQuery();
							if (rs != null){
								while (rs.next()) {
									this.recordid = rs.getInt("logid");
									this.playername = rs.getString("playername");
								}
							}

							rs.close();

							//lets determine if they have a matching loi if so update
							// , if not log as on usahockey roster but missing loi and return player name team name to send in email
							cs2.setString("usanumber", usahockeynumber);
							cs2.setString("usateamnumber", usahockeyteamnumber);


							rs = cs2.executeQuery();
							if (rs != null){
								while (rs.next()) {
									isusahockeynoloi = rs.getBoolean("isusahockeynoloi");
									//this.playername = rs.getString("playername");
									this.teamname = rs.getString("teamname");
									this.clubid	= rs.getInt("clubid");
								}
							}

							rs.close();

						}

						if (isusahockeynoloi) {
							to = "";
							LOGGER.info("Sending email to club registrar for player without loi");

							//need to combine next two sp's into one to reduce number of db calls.
							cs3.setInt("iclubid", this.clubid);
							rs = cs3.executeQuery();
							if (rs != null) {
								while (rs.next()) {
									if (!to.equals("")) {
										to = to + "," + rs.getString("usercode");
									} else {
										to = rs.getString("usercode");
									}
								}
							}

							//to = "lahockeyfan2@yahoo.com";
							this.setToMailAddress(to);
							this.setPreApprovedCC("");
							this.setSubject(playername + "-" + teamname + " Missing LOI");

							SendMailSSL mail = new SendMailSSL(this);
							LOGGER.info("Finished creating mail object for " + playername + "-" + teamname + " Missing LOI");

							mail.sendMail();

							rs.close();

						}

					y++;
					this.totalcount = y;
					//clear values for use in the next record
					usahockeynumber = "";
					usahockeyteamnumber = "";
					isusahockeynoloi = false;
					this.playername = "";
					this.teamname = "";
				}

				cs.close();
				cs2.close();
				cs3.close();
				db.cleanup();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.info("ERROR IN inserting import line into db" + errormessage);
				e.printStackTrace();
				db.free();
			} finally {
				//
				// always clean up after yourself..
				//
				db.free();
			}


			br.close();
			br = null;

			//now that we have completed the file, need to move to the archive folder with the time stamp in the file name.
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(stream);

			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
			String movedestPath ="C:/Program Files (x86)/Apache Software Foundation/Tomcat 8.5/webapps/uploadfiles/usahockeyimport/archive/" + prefix + "_" + timeStamp + "." + suffix;
			/*File archivefile = new File(movedestPath);
			destFile.renameTo(archivefile);*/
			Path source = Paths.get(destPath);
			Path result = Paths.get(movedestPath);
			Files.move(source, result);


			return true;
		}catch (IOException e){
			e.printStackTrace();
			return false;
		} finally {

		}


	}

	/**
	 * This saves the Club object and along with it.. any changes
	 * @return
	 */
	/*public void processFile() {
	        FacesMessage message =  
	            new FacesMessage(FacesMessage.SEVERITY_INFO, "Club " + this.selectedclub.getClubname() + " has been saved", null);  
	        FacesContext.getCurrentInstance().addMessage(null, message);  
	        
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase",pb.getProfile());
	        try {
				this.selectedclub.getLogo().update(db);


			} catch (SQLException e) {
				e.printStackTrace();
				db.free();
			}
	        db.free();
	        return "true";
    } */
	
	@Override
	public String getSubject() {
		return this.subject;
	}

	@Override
	public String getTextBody() {
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("PLAYERNAME:" + this.playername);
		myTokens.add("TEAMNAME:" + this.teamname);
		return Utils.mergeTokens(this.mail_reg_body_playerwithoutloi, myTokens);
	}




	

	@Override
	public InternetAddress[] getToMailIAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InternetAddress[] getPreApprovedICC() {
		// TODO Auto-generated method stub
		return null;
	}



	
}
