package com.scaha.beans;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.mail.internet.InternetAddress;
import java.io.*;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@ManagedBean
@SessionScoped
public class processusahockeyuploadBean implements Serializable,  MailableObject {

	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

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
			for(String line; (line = br.readLine()) != null; ) {
				// process the line.
				LOGGER.info("processing file line:" + line);

				//need to remove commas in coaching modules field//
				startstring = line.indexOf('"');
				endstring = line.lastIndexOf('"');

				if (startstring>0) {
					tempstring = line.substring(startstring,endstring);
					finalpartialstring = tempstring.replace(",","");

					line = line.replace(tempstring,finalpartialstring);
				}

				//now split the string into separate fields
				String[] linearr = line.split(",");
				Integer x = 1;

				try{
					//check to see if it's the header row, if so don't insert headers//
					if (y!=0){
						CallableStatement cs = db.prepareCall("CALL scaha.logto_usahockeyrosterimportlog(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

						for (String value : linearr) {
							//need to add fields to cs for records for standard regular season teams
							cs.setString(x, value);
							x++;
						}

						//insert entire row into db
						cs.executeQuery();

						cs.close();
						db.cleanup();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					LOGGER.info("ERROR IN inserting import line into db" + line);
					e.printStackTrace();
					db.free();
				} finally {
					//
					// always clean up after yourself..
					//
					db.free();
				}

				y++;
			}

			br.close();
			br = null;

			return true;
		}catch (IOException e){
			e.printStackTrace();
			return false;
		} finally {
			IOUtils.closeQuietly(output);
			IOUtils.closeQuietly(stream);
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
		return null;
	}

	@Override
	public String getTextBody() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return null;
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
