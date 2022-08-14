package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.Database;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Family;
import com.scaha.objects.FamilyMember;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Person;
import com.scaha.objects.Profile;

public class RegistrationBean implements Serializable, MailableObject  {
	

	//
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/welcome.html");
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	
	private String username = null;
	private String password = null;
	private String reppassword = null;
	private String firstname = null;
	private String lastname = null;
	private String nickname = null;
	private String phone = null;
	private String email = null;
	private String address = null;
	private String DOB = null;
	private String gender = null;
	private String city = null;
	private String state = null;
	private String zip = null;
	
	@PostConstruct
    public void init() {
		
	}
		
	public ProfileBean getPb() {
		return pb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setPb(ProfileBean pb) {
		this.pb = pb;
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return the reppassword
	 */
	public String getReppassword() {
		return reppassword;
	}
	/**
	 * @param reppassword the reppassword to set
	 */
	public void setReppassword(String reppassword) {
		this.reppassword = reppassword;
	}
	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}
	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	/**
	 * @return the lastname
	 */
	public String getLastname() {
		return lastname;
	}
	/**
	 * @param lastname the lastname to set
	 */
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	/**
	 * @return the nickname
	 */
	public String getNickname() {
		return nickname;
	}
	/**
	 * @param nickname the nickname to set
	 */
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the email
	 */
	public String getEmail() {
		return email;
	}
	/**
	 * @param email the email to set
	 */
	public void setEmail(String email) {
		this.email = email;
	}
	
	/**
	 * 
	 */
	public void setDefaultUserName() {
	    username = email;
	}
	
	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}
	/**
	 * @return the city
	 */
	public String getCity() {
		return city;
	}
	/**
	 * @param city the city to set
	 */
	public void setCity(String city) {
		this.city = city;
	}
	/**
	 * @return the state
	 */
	public String getState() {
		return state;
	}
	/**
	 * @param state the state to set
	 */
	public void setState(String state) {
		this.state = state;
	}
	/**
	 * @return the zip
	 */
	public String getZip() {
		return zip;
	}
	/**
	 * @param zip the zip to set
	 */
	public void setZip(String zip) {
		this.zip = zip;
	}
	/**
	 * This method will create all the required database records to create a "registered User".
	 * 
	 * Typically, the database maintenance is done in business objects.. this one touches a few tables..
	 * so we want to make sure its tight...
	 * 
	 */
	public String createRegistration() {
		
		
		//
		// Lets check how unique and clean the data is first.
		// if we pass everything.. then we insert everyrthing..
		
		// The screen checked the uniqueness of the user.. 
		// we just need to check in once again prior to creating the basic profile
		//
		//
		// We create a profile record
		// we create a person record
		// and we create a default roleset record that gives this person the default role of FAMILY
		//
		//
		// Then at the end of this.. we fire off an e-mail.  we have to pull all the profiles that have register admin roles...
		//  and make sure they are in the blind carbon copy section of the e-mail.
		//
		
		//
		// If everything works.. then we return a "True" back.. so the faces can reroute to a succesffully registered page..
		//
		// otherwise.. we send back a false.. which will keep the user parked on the page with an error message.. 
		//
		// we will need the general msg to fill out that there was some sort of error.. and that if it continues.. to call support.
		//
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

			this.setUsername(pb.getName());
		try{

			if (db.setAutoCommit(false)) {
			
				
				Vector<String> v = new Vector<String>();
				v.add(this.username);
 				db.getData("CALL scaha.checkforuser(?)", v);
			        
 				if (db.getResultSet() != null && db.getResultSet().next()){
 					FacesContext.getCurrentInstance().addMessage(
 							"myform:username",
 		                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
 		                    "USA Hockey Reg",
 		                    "You cannot use this username.  A username already exists in the system!"));
 					db.free();
					return "fail";
				}
 				
 				db.cleanup();

 				//
 				// We do not want to add a person twice (same first and last name!)
 				//
 				if (db.checkForPersonByFLDOB(this.getFirstname().toLowerCase(), this.getLastname().toLowerCase(), this.getDOB())) {
 					
 					FacesContext.getCurrentInstance().addMessage(
 							null,
 		                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
 		                    "USA Hockey Reg",
 		                    "You cannot create a member.  There is already a member with the same first name, last name and date of birth!"));
 					db.free();
					return "fail";

 				} 			
 				
				//
				// we are good to go here..
				//

				Profile pro = new Profile(this.username,this.password, this.nickname);
				
				Person per = new Person(0,pro);
				Family fam = new Family(-1, pro, per);
				
				per.setsAddress1(this.address);
				per.setsFirstName(Utils.properCase(this.firstname));
				per.setsLastName(Utils.properCase(this.lastname));
				per.setsCity(city);
				per.setsState(this.state);
				per.setiZipCode(Integer.valueOf(this.zip));
				per.setsPhone(this.phone);
				per.setsEmail(this.email);
				per.setGender(this.gender);
				per.setDob(this.DOB);
				
				pro.update(db);
				pro.setPerson(per);
				per.update(db);
				
				//
				// now update the family..  Lets give it the default name!!
				//
				fam.setFamilyName("The " + per.getsLastName() + " Family");
				fam.update(db, false);
				
				//
				// Make sure the person is always them selves
				//
				FamilyMember fm = new FamilyMember(pro,fam, per);
				fm.setRelationship("Self");
				fm.updateFamilyMemberStructure(db);
				
				//
				// Lets not forget the family record
				// and possibly the default FamilyMember.. (who ever is creating this record)
				//
				
				
				db.commit();
				db.free();

				// We want to create a family called the <lastname> family...
				LOGGER.info("HERE IS WHERE WE SAVE EVERYTHING COLLECTED FROM REGISTRATION..");
				LOGGER.info("Sending Registration mail here...");
				SendMailSSL mail = new SendMailSSL(this);
				LOGGER.info("Finished creating mail object for " + this.getUsername());
				mail.sendMail();
				
				//
				// This keeps the message alive between redirects!
				//
				FacesContext context = FacesContext.getCurrentInstance();
				context.getExternalContext().getFlash().setKeepMessages(true);			
				
				context.addMessage(
						null,
		                new FacesMessage(FacesMessage.SEVERITY_INFO,
		                "Registration Success",
		                "You have successfully Registered with the system.  You will be receiving a confirmation e-mail shortly"));
				
				
			
			} else {
				LOGGER.info(" ** Cannot set autocommit to false *** ERROR IN REGISTRATION PROCESS FOR " + this.getUsername());
				return "fail";
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN REGISTRATION PROCESS FOR " + this.getUsername());
			e.printStackTrace();
			db.rollback();
			db.free();
		}
		
		//return "Welcome.xhtml?faces-redirect=true";
		FacesContext context = FacesContext.getCurrentInstance();
		
		try{
				context.getExternalContext().redirect("manageUSAHockey.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "fail";

		
	}
		

	@Override
	public String getSubject() {
		// TODO Auto-generated method stub
		return "SCAHA iSite Wecomes You as a New Member!";
	}
	@Override
	public String getTextBody() {
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("FIRSTNAME:" + this.getFirstname());
		myTokens.add("LASTNAME:" + this.getLastname());
		myTokens.add("USERNAME:" + this.getUsername());
		myTokens.add("PASSWORD:" + this.getPassword());
		return Utils.mergeTokens(RegistrationBean.mail_reg_body,myTokens);
	}
	
	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return "info@scaha.com";
	}
	@Override
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return this.username + "," + ((this.email == null || this.email.isEmpty()) ? "" : this.email);
	}
	/**
	 * @return the dOB
	 */
	public String getDOB() {
		return DOB;
	}
	/**
	 * @param dOB the dOB to set
	 */
	public void setDOB(String dOB) {
		DOB = dOB;
	}
	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}
	/**
	 * @param gender the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
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
