package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.Position;

import javafx.geometry.Pos;
import org.primefaces.event.FlowEvent;
import com.gbli.common.SendMailSSL;
import com.gbli.common.USAHRegClient;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Family;
import com.scaha.objects.FamilyMember;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Person;
import com.scaha.objects.PersonList;
import com.scaha.objects.Profile;
import com.scaha.objects.ScahaCoach;
import com.scaha.objects.ScahaManager;
import com.scaha.objects.ScahaMember;
import com.scaha.objects.ScahaPlayer;
import com.scaha.objects.UsaHockeyRegistration;
import com.scaha.objects.SCAHAPosition;
/**
 * USAHockeyBean is now a Wizard that does the following:
 * 
 * 1) Collects the USA Hockey Number..
 * 2) I
 * @author dbigelow
 *
 */
@ManagedBean
@SessionScoped
public class MemberBean implements Serializable, MailableObject {
	
	private static final long serialVersionUID = 3L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	public static String mail_body = Utils.getMailTemplateFromFile("mail/seasonpass.html");
	private String MergedBody = null;

	private UsaHockeyRegistration usar = null;
	private String regnumber = null;
	private String scahanumber = null;
	private String wizardstate = null;
	private List<String> membertype;  
	private List<FamilyMember> fmemberlist = null;
	private List<SCAHAPosition> positionList = null;
	private String relationship = null;
	private String selectedposition = null;
	private boolean concussion = false;
	private boolean datagood = false;
	private boolean displayrole = false;
	private boolean displayusanumber = false;
	private boolean displayselectmember = false;
	private boolean displayselectrole = false;
	private boolean displayseasonpass = false;
	private boolean playerskater = false;
	private boolean playergoalie = false;
	private boolean coach = false;
	private boolean manager = false;
	
	private PersonList Persons = null;
	private Person selectedPerson = null;  
	
	private boolean fastforward = false;
	private boolean restart = false;
	private boolean stealme = false;
	
	
	private transient UIComponent mcomponent;
	
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	@ManagedProperty(value="#{scahaBean}")
	private ScahaBean scaha;

	public String getSelectedposition() {
		return selectedposition;
	}

	public void setSelectedposition(String selectedposition) {
		this.selectedposition = selectedposition;
	}

	public void setManager(Boolean regtype) {
		this.manager = regtype;
	}
	
	/**
	 * @return the concussion
	 */
	public Boolean getManager() {
		return manager;
	}

	
	public void setCoach(Boolean regtype) {
		this.coach = regtype;
	}
	
	/**
	 * @return the concussion
	 */
	public Boolean getCoach() {
		return coach;
	}

	
	public void setPlayergoalie(Boolean regtype) {
		this.playergoalie = regtype;
	}
	
	/**
	 * @return the concussion
	 */
	public Boolean getPlayergoalie() {
		return playergoalie;
	}

	
	
	public Boolean getDisplayseasonpass() {
		return displayseasonpass;
	}

	/**
	 * @param regtype the regtype to set
	 */
	public void setDisplayseasonpass(Boolean regtype) {
		this.displayseasonpass = regtype;
	}
	
	/**
	 * @param regtype the regtype to set
	 */
	public void setPlayerskater(Boolean regtype) {
		this.playerskater = regtype;
	}
	
	/**
	 * @return the concussion
	 */
	public Boolean getPlayerskater() {
		return playerskater;
	}

	
	/**
	 * @return the concussion
	 */
	public Boolean getDisplayselectrole() {
		return displayselectrole;
	}

	/**
	 * @param regtype the regtype to set
	 */
	public void setDisplayselectrole(Boolean regtype) {
		this.displayselectrole = regtype;
	}
	
	/**
	 * @return the concussion
	 */
	public Boolean getDisplayselectmember() {
		return displayselectmember;
	}

	/**
	 * @param regtype the regtype to set
	 */
	public void setDisplayselectmember(Boolean regtype) {
		this.displayselectmember = regtype;
	}
	
	/**
	 * @return the concussion
	 */
	public Boolean getDisplayusanumber() {
		return displayusanumber;
	}

	/**
	 * @param regtype the regtype to set
	 */
	public void setDisplayusanumber(Boolean regtype) {
		this.displayusanumber = regtype;
	}
	
	/**
	 * @return the scaha
	 */
	public ScahaBean getScaha() {
		return scaha;
	}

	/**
	 * @param scaha the scaha to set
	 */
	public void setScaha(ScahaBean scaha) {
		this.scaha = scaha;
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
	
	 @PostConstruct
	 public void init() {
		 membertype = new ArrayList<String>(); 
		 this.wizardstate="1";
		 //test code
		 
		 displayusanumber = true;
		 displayselectmember = false;
		 displayselectrole = false;
		 displayseasonpass = false;

		 LoadPositionsList();
	 }

	public List<SCAHAPosition> getPositionList() {
		return positionList;
	}

	public void setPositionList(List<SCAHAPosition> positionList) {
		this.positionList = positionList;
	}

	/**
	 * @return the usar
	 */
	public UsaHockeyRegistration getUsar() {
		return usar;
	}

	/**
	 * @param usar the usar to set
	 */
	public void setUsar(UsaHockeyRegistration usar) {
		this.usar = usar;
	}

	
	/**
	 * @return the regnumber
	 */
	public String getWizardstate() {
		return wizardstate;
	}

	/**
	 * @param regnumber the regnumber to set
	 */
	public void setWizardstate(String wstate) {
		this.wizardstate = wstate;
	}

	public String getRegnumber() {
		return regnumber;
	}

	/**
	 * @param regnumber the regnumber to set
	 */
	public void setRegnumber(String regnumber) {
		this.regnumber = regnumber;
	}

	public String getScahanumber() {
		return scahanumber;
	}

	/**
	 * @param regnumber the regnumber to set
	 */
	public void setScahanumber(String regnumber) {
		this.scahanumber = regnumber;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean fetchUSAHockey() {
		
		
		FacesContext context = FacesContext.getCurrentInstance();
		//
		// clear it out first!!
		//
		this.usar = null;
		try {
			
			this.usar = USAHRegClient.pullRegistartionRecord(this.getRegnumber());
			
			if (this.usar == null) {
				LOGGER.info("usaHockeyBean:Did NOT Get any USAH Number info Back!! BAD SERVICE CALL");
				FacesContext.getCurrentInstance().addMessage(
						mcomponent.getClientId(),
	                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Connection ISSUES",
	                    "Could Not Connect with the USA Hockey database at this time.."));
				return false;

			}

			//
			// If we got nothing back at all from the first name
			//
			// Then we did not find anything.
			//
			if (usar == null || usar.getFirstName() == null || usar.getFirstName().trim().length()== 0) {
				
				LOGGER.info(mcomponent.getClientId());
				context.addMessage(
						mcomponent.getClientId(),
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "USA Hockey Info Not Found",
                    "Could Not find the USA Registration record. Please check your number and try again"));

				return false;
			} 
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		if (this.usar.getUSAHnum().contains("XX")) {
			if (!membertype.contains("Manager")) {
				this.membertype.add("Manager");
			}
		}
		LOGGER.info("fetchUSAHockey made it back alive.");		
		//
		// We are not going anywhere.. so simply pass true vs routing..
		//
		return true;
	}

	private void buildMailBody(ScahaDatabase _db, Person tper, Person per, UsaHockeyRegistration usar2, ScahaMember mem) {
	
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("PFIRST:" + per.getsFirstName());
		myTokens.add("PLAST:" + per.getsLastName());
		myTokens.add("FIRSTNAME:" + tper.getsFirstName());
		myTokens.add("LASTNAME:" + tper.getsLastName());
		myTokens.add("USAHNUM:" + usar2.getUSAHnum());
		myTokens.add("SCAHANUM:" + mem.getSCAHANumber());
		//myTokens.add("SEASON:" + scaha.getScahaSeasonList().getCurrentSeason().getDescription());
		myTokens.add("SEASON:SCAHA 2024-2025 Season");
		try {
			if (_db.isPersonPlayer(per.ID)) {
				if (_db.checkForBC(per.ID)) {
					myTokens.add("BCSENTENCE:" + "We already have the Player's Birth Certificate on file so you do not need to bring a copy to any SCAHA tryout.");
				} else {
					myTokens.add("BCSENTENCE:" + "MAKE SURE TO BRING A COPY OF THE PLAYER'S Birth Certificate to ALL  SCAHA TRYOUTS.  We do not have any BC on file for this player yet.");
				}
			} else {
				myTokens.add("BCSENTENCE:" + "You have not registered this person as a SCAHA player, so no Birth Certificate Needed.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//myTokens.add("SEASON:" + scaha.getScahaSeasonList().getCurrentSeason().getDescription());
			
		this.MergedBody =  Utils.mergeTokens(MemberBean.mail_body,myTokens);
		
	}
	
	@Override
	public String getSubject() {
		return "SCAHA Membership Information For " + this.selectedPerson.getsFirstName() + " " + this.selectedPerson.getsLastName();
	}
	@Override
	public String getTextBody() {
		
			return this.MergedBody;
		
	}

	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return "info@scaha.com";
	}
	@Override
	public String getToMailAddress() {
		
		/**
		 *  The top person has both an e-mail in the profile and an e-mail (alt if their person record)
		 */
		Profile tpro = pb.getProfile();
		Person tper = tpro.getPerson();
		
		LOGGER.info("SendMail To:" + tpro.getUserName() + (tper.getsEmail() != null ? "," + tper.getsEmail() : ""));

		//return "lahockeyfan2@yahoo.com";
		return tpro.getUserName() + (tper.getsEmail() != null ? "," + tper.getsEmail() : "")  + "";
		
		//			(this.selectedPerson.getsEmail() != null ? "," + this.selectedPerson : "");
		
	
	}
	

	/**
	 * @return the membertype
	 */
	public List<String>  getMembertype() {
		return membertype;
	}

	/**
	 * @param membertype the  to set
	 */
	public void setMembertype(List<String> selectedOptions2) {
		this.membertype = selectedOptions2;
	}

	public List<FamilyMember>  getFmemberlist() {
		return fmemberlist;
	}

	/**
	 * @param membertype the  to set
	 */
	public void setFmemberlist(List<FamilyMember> selectedOptions2) {
		this.fmemberlist = selectedOptions2;
	}
	
	/**
	 * @return the regtype
	 */
	public String getRelationship() {
		return relationship;
	}

	/**
	 * @param regtype the regtype to set
	 */
	public void setRelationship(String regtype) {
		this.relationship = regtype;
	}
	
	/**
	 * @return the concussion
	 */
	public Boolean getConcussion() {
		return concussion;
	}

	/**
	 * @param regtype the regtype to set
	 */
	public void setConcussion(Boolean regtype) {
		this.concussion = regtype;
	}

	/**
	 * @return the datagood
	 */
	public boolean isDatagood() {
		return datagood;
	}

	/**
	 * @param datagood the datagood to set
	 */
	public void setDatagood(boolean datagood) {
		this.datagood = datagood;
	}

	/**
	 * This guy adds a new member via a USAHockey pull request...
	 * @return
	 */
	public String createMember() {
		
		//
		//
		// We have to be really clear here.  We are making a change here.  instead of hunting for a person record that matches the description of the USA Hockey Record
		// we are going to use the user selected matching person record work with unless they selected create new:
		//
		// 1) match the Last Name and BirthDate of what was pulled.
		// 
		// Three things can happen..
		//
		// 1) Cannot find that person anywhere.. a totally new person.. So.. we simply create all the records needed
		// 2) user selected a person on the previous steps.. and that person is currently in the requesting families account...
		// 		This is just like #1.. but the person and family structure is already there..
		// 3) user selected a person on the previous steps.. .. but that person belongs to another family.
		//      In this case.. we want to put the person in both families.. since both have claim to them
		//  
		// In all cases.. we need to correct the first name with what was sent with USA hockey..
		//
		//
		LOGGER.info("starting created/added member");
		
		FacesContext context = FacesContext.getCurrentInstance();
		
		Profile pro = pb.getProfile();   // This is the profile of the user
		Person tper = pro.getPerson();   //  This is their person record who controlls the account
		Family tfam = tper.getFamily();  //  This is their Family Structure
		
		Person per = this.selectedPerson;
		ScahaManager sm = null;
		ScahaPlayer sp = null;
		ScahaCoach sc = null;		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", pro);
		boolean newpeep = false;
		if (per.ID < 1) {
			newpeep = true;
			LOGGER.info("THIS IS A NEW PERSON");
		} else {
			LOGGER.info("THIS IS AN EXISTING PERSON");
		}
		//
		// Lets start off with the basics person is always implied in the extended object.
		// we are really just saving off alot of stuff through the objects.. then reloading the family .. once done
		// 
		
		//
		// First.. if the added person is looks just like another person in the family.. then we have trouble brewing..
		// We must stop this add in its tracks and tell them to use an update existing family member function
		//
		try {
			
			//this should only be triggered in the case of creating a new member
			//taking this out since we are now giving the option to create a new member if they can't find 
			//their old record.
			/*if (db.checkForPersonByFLDOB(usar.getFirstName(), usar.getLastName(), usar.getDOB()) && newpeep ) {
				
				FacesContext.getCurrentInstance().addMessage(
						"form:growl",
	                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
	                    "USA Hockey Reg",
	                    "You cannot create a member.  There is already a member with the same first name, last name and date of birth!"));
				
				return "false";

			}*/
			// And we need to create the membership record as well..
			//
			// Here is where we update everything at once.
			// we have auto commit off so its an all or nothing approach.
			//
			//
			//  we have either
			//
			//  1) An existing person that is outside the family.. all we want to do where is create a membership.. update the person record
			//  	Possibly create a coach and or manager record if one not already created
			//		In addition.. if they are going to be stolen.. we do it here and tree them up to the owners family..
			//
			//  2) If its a new person.. we just do the normal stuff.
			//
			//
			//
			//  This is either a new person.. or an existing person to the system..
			//

			//
			// Is this a new person...
			//

			
			//
			// lets quickly see if you are trying to steal yourself into your own account
			//
			
			if (this.stealme && tper.ID == per.ID) {
				per = tper;
			}

			per.gleanUSAHinfo(this.usar);
			per.update(db);
			usar.update(db, per);
			ScahaMember mem = new ScahaMember(pro,per);
			mem.setSCAHAYear(this.usar.getMemberShipYear());
			LOGGER.info("Time to Create the Membership.. ");
			mem.generateMembership(db);
			mem.setTopPerson(tper);
			scahanumber = mem.getSCAHANumber();
			
			LOGGER.info("Member Type is" + membertype.toString());
				
			//
			// ok.. lets update the trifecta and see if it sticks!!
			//
			if (this.manager) {
				sm = new ScahaManager(pro,per);
				sm.update(db);
				
				
				sc = new ScahaCoach(pro,per);
				sc.update(db);

			} else if (this.coach) {

				sc = new ScahaCoach(pro,per);
				sc.update(db);

			}
			if (!this.selectedposition.equals("0")) {
				sp = new ScahaPlayer(pro, per);
				sp.gleanUSAHinfo(this.usar);
				/*if (this.playergoalie) {
					sp.setGoalie(true);
				}*/
				if (this.selectedposition!=null) {
					sp.setPosition(this.selectedposition);
				}
				sp.update(db);
			}
	
			//
			// Now we need to get the Family Object from the Person in the Profile..
			// and add this person to the database.. and the object
			//
			// no matter how many types of people we have.. they all point to the same person...
			
			LOGGER.info("STEAL ME is:" + this.stealme);
			if (newpeep || (!newpeep && this.stealme && per.ID != tper.ID)) {
				FamilyMember fm = new FamilyMember(pro, tfam, per);
				fm.setRelationship(this.getRelationship());
				fm.updateFamilyMemberStructure(db);
			}
			
			//
			// Now lets update the family structure now
			//
			tper.setFam(new Family(db, tper));

			//now lets log confirmation of concussion policy
			//mem.logConcussion(db, pro.ID, per.ID);
			
			//now lets create a family member object for display
			/*FamilyMember fmdisplay = new FamilyMember(pro, tfam, per);
			fmdisplay.setPersonname(usar.getFirstName()+' '+usar.getLastName());
			fmdisplay.setUsaHockeyNumber(usar.getUSAHnum());
			fmdisplay.setScahaHockeyNumber(mem.getSCAHANumber());
			fmdisplay.setDob(usar.getDOB());
			
			List<FamilyMember> fmlist = new ArrayList<FamilyMember>();
			fmlist.add(fmdisplay);
			this.setFmemberlist(fmlist);
			*/
			
			this.buildMailBody(db, tper, per, usar, mem);
			SendMailSSL mail = new SendMailSSL(this);
			//for now we comment this out to test all different accounts
			mail.sendMail();
			
		} catch (SQLException ex) {
			ex.printStackTrace();
			db.free();
		}
		
		db.free();
		
		//
		// This keeps the message alive between redirects!
		//
		context.getExternalContext().getFlash().setKeepMessages(true);			
		

		context.addMessage(
				"m_scaha_number",
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                "Member Add a Success",
                "You have successfully Added/Updated a new member for the upcoming season..  You will be receiving a confirmation e-mail shortly"));
		
		LOGGER.info("ending created/added member");
		
		return "Welcome.xhtml";

	}
	


	public void reset() {
		LOGGER.info("usaHockeyBean:reseting member variables...");
		usar = null;
		regnumber = null;
		membertype.clear();
		relationship = null;
		datagood = false;
		this.Persons = null;
		this.selectedPerson = null;
		this.fastforward = false;
		this.restart = true;
		this.concussion = false;
		System.gc();
	}
	
	
	/**
	 * ok.. This is generated using the USAHockey provided information.  
	 * 
	 * We will improve the search capability later.. This one just uses lastname and dob to pull up a list of people.
	 * 
	 * We will also use Last Name and First Name 
	 * 
	 * we want to sort by most likely on top
	 * 
	 * @return
	 */
	private PersonList genPersonsList() {
		
  	    LOGGER.entering(MemberBean.class.getName(),"genPersonsList:");  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", pb.getProfile());

		LOGGER.info("Is PB.getProfile().. null?:" + pb.getProfile()); 

		PersonList pers = null;
		try {
			pers = PersonList.NewPersonListFactory(pb.getProfile(), db, this.usar);
			for (Person per : pers) {
				//selectedPerson = per;
				break;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LOGGER.info("error generating person list ");
		}	
		
		db.free();
		

		return pers;
		
	}
	
	public String onFlowProcess(FlowEvent event) {  

        LOGGER.info("Current wizard step:" + event.getOldStep());
        LOGGER.info("Next step:" + event.getNewStep());

        if (this.fastforward && !event.getOldStep().equals("concussion")) {
        	this.fastforward = false;
        	return "usahockey";
        }
        if (this.restart) {
        	this.restart = false;
        	return "usahockey";
        }
        
        if (event.getOldStep().equals("usahockey") && event.getNewStep().equals("review")) {
        	// do nothing now.
        		
        } else if (event.getOldStep().equals("usahockey") && event.getNewStep().equals("choose")) {
        	if (this.fetchUSAHockey()) {
        		
        		//
        		// ok.. lets also load the players here.. if only ONE hit comes back.. we fast forward to the end.. no need to worrry..
        		//
    			// 
    			this.Persons = this.genPersonsList();
    			this.membertype = new ArrayList<String>();
    			this.relationship = "";
    			
    			// ok.. find the closest match.. if there is only one.. then its eason
    			if (this.Persons.getRowCount()==1) {
    				//this.selectedPerson =  Persons.iterator().next();
    				fastforward = true;
    			} else {  
    				
    				for (Person p : Persons) {
    					if (p.getsFirstName().toLowerCase().equals(this.usar.getFirstName().toLowerCase()) &&
    							p.getsLastName().toLowerCase().equals(this.usar.getLastName().toLowerCase()) ) {
    						//this.selectedPerson = p; 
    						break;
    					}
    				}
    			}
    				
   				/*if (selectedPerson.getGenatt().get("ISPLAYER").equals("Y") && selectedPerson.getGenatt().get("ISGOALIE").equals("Y")) membertype.add("Player-Goalie");
   				if (selectedPerson.getGenatt().get("ISPLAYER").equals("Y") && !selectedPerson.getGenatt().get("ISGOALIE").equals("Y")) membertype.add("Player-Skater");
   				if (selectedPerson.getGenatt().get("ISMANAGER").equals("Y")) membertype.add("Manager");
    				
   				this.relationship = selectedPerson.getXRelType();
				*/
   				return "choose";
   			} else {
   				//
   				// stay put..
   				//
   				return "usahockey";
   			}	
        }  else if (event.getNewStep().equals("concussion")) {

        	if (event.getOldStep().equals("choose")){
        		this.selectedPerson = this.findPersonByID(selectedPerson.ID);
        		return "concussion";
        	} else {
				this.selectedPerson = this.findPersonByID(selectedPerson.ID);
				return "finish";
        	}
        }else if (event.getNewStep().equals("role")){	
		
        	//is the following needed?
			this.selectedPerson = this.findPersonByID(selectedPerson.ID);
			
			this.membertype = new ArrayList<String>();
			if (selectedPerson.getGenatt().get("ISPLAYER").equals("Y") && selectedPerson.getGenatt().get("ISGOALIE").equals("Y")) membertype.add("Player-Goalie");
			if (selectedPerson.getGenatt().get("ISPLAYER").equals("Y") && !selectedPerson.getGenatt().get("ISGOALIE").equals("Y")) membertype.add("Player-Skater");
			if (selectedPerson.getGenatt().get("ISMANAGER").equals("Y")) membertype.add("Manager");
			if (selectedPerson.getGenatt().get("ISCOACH").equals("Y")) membertype.add("Coach");
			this.relationship = selectedPerson.getXRelType();
			
        
        } else if (event.getNewStep().equals("finish")) {


			/*if (this.concussion){*/
				
			createMember();
				
			
				
			/*} else {
				
				//this should never happen with the disabling of the next button until the 
				//the checkbox is selected.
				return "concussion";
			}*/
			
		}
        return event.getNewStep();  
    }  
	

	public void registrationwizard(String nextstep){
		LOGGER.info("registration wizard" + nextstep);
		if (nextstep.equals("usahockeynumber")) {
        	//set panel display variables
			displayusanumber = true;
			displayselectmember = false;
			displayselectrole = false;
			displayseasonpass = false;
        		
        } else if (nextstep.equals("selectmember")){
        	displayusanumber = false;
			displayselectmember = true;
			displayselectrole = false;
			displayseasonpass = false;
        	
			//lets go out, get the info from usahockey and do our thing.  then we will display the results
			LOGGER.info("calling fetchusahockey" + nextstep);
			if (this.fetchUSAHockey()) {
        		
        		//
        		// ok.. lets also load the players here.. if only ONE hit comes back.. we fast forward to the end.. no need to worrry..
        		//
    			//
				LOGGER.info("registration wizard getting persons list" + nextstep);
    			this.Persons = this.genPersonsList();
    			this.membertype = new ArrayList<String>();
    			this.relationship = "";
    			
    			// ok.. find the closest match.. if there is only one.. then its eason
    			if (this.Persons.getRowCount()==1) {
    				//this.selectedPerson =  Persons.iterator().next();
    				fastforward = true;
    			} else {  
    				
    				for (Person p : Persons) {
    					if (p.getsFirstName().toLowerCase().equals(this.usar.getFirstName().toLowerCase()) &&
    							p.getsLastName().toLowerCase().equals(this.usar.getLastName().toLowerCase()) ) {
    						//this.selectedPerson = p; 
    						break;
    					}
    				}
    			}
    				
   				/*if (selectedPerson.getGenatt().get("ISPLAYER").equals("Y") && selectedPerson.getGenatt().get("ISGOALIE").equals("Y")) membertype.add("Player-Goalie");
   				if (selectedPerson.getGenatt().get("ISPLAYER").equals("Y") && !selectedPerson.getGenatt().get("ISGOALIE").equals("Y")) membertype.add("Player-Skater");
   				if (selectedPerson.getGenatt().get("ISMANAGER").equals("Y")) membertype.add("Manager");
    				
   				this.relationship = selectedPerson.getXRelType();
				*/
			}	
			
        } else if (nextstep.equals("selectrole")){
        	displayusanumber = false;
			displayselectmember = false;
			displayselectrole = true;
			displayseasonpass = false;

			LOGGER.info("registration wizard generating persons list" + nextstep);
			this.genPersonsList();
			//if (selectedPerson.getGenatt().get("ISPLAYER").equals("Y") && selectedPerson.getGenatt().get("ISGOALIE").equals("Y")) this.playergoalie=true;
			//if (selectedPerson.getGenatt().get("ISPLAYER").equals("Y") && !selectedPerson.getGenatt().get("ISGOALIE").equals("Y")) this.playerskater=true;
			if (selectedPerson.getGenatt().get("ISMANAGER").equals("Y")) this.manager=true;
			if (selectedPerson.getGenatt().get("ISCOACH").equals("Y")) this.coach=true;
			this.relationship = selectedPerson.getXRelType();

			//we should add check here for is selected person is in the delinquency list
			
        } else if (nextstep.equals("displayseasonpass")){
        	displayusanumber = false;
			displayselectmember = false;
			displayselectrole = false;
			displayseasonpass = true;
			LOGGER.info("creating member" + nextstep);
			createMember();
			
        } else if (nextstep.equals("leavewizard")){
        	displayusanumber = true;
			displayselectmember = false;
			displayselectrole = false;
			displayseasonpass = false;
			LOGGER.info("registration wizard finishing up the wizard" + nextstep);
			FacesContext context = FacesContext.getCurrentInstance();
			try{
				context.getExternalContext().redirect("Welcome.xhtml");
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
			
		LOGGER.info("registration wizard---" + nextstep);
		
	}
	
	//new method for parsing an object from user list on registering users
	public void selectMember(Person person){
		
		this.selectedPerson = person;
		
		registrationwizard("selectrole");
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UsaHockeyBean [usar=" + usar + ", regnumber=" + regnumber
				+ ", dob=" + ", membertype=" + membertype
				+ ", relationship=" + relationship + "]";
	}

	/**
	 * @return the persons
	 */
	public PersonList getPersons() {
		return Persons;
	}

	/**
	 * @param persons the persons to set
	 */
	public void setPersons(PersonList persons) {
		Persons = persons;
	}

	/**
	 * @return the selectedPerson
	 */
	public Person getSelectedPerson() {
		return selectedPerson;
	}

	/**
	 * @param selectedPerson the selectedPerson to set
	 */
	public void setSelectedPerson(Person selectedperson) {
		 LOGGER.info("Setting selected person to..." + selectedperson.toString());
		this.selectedPerson = selectedperson;
		
	}
	
	public void DisplayRoles(){
		this.displayrole = true;
		//need to populate the checkboxes
	}
	
	public Person findPersonByID (int _id) {
		for (Person c : Persons) {
			if (c.ID == _id) { 
				return c;
			}
		}
		return null;
	}

	/**
	 * @return the mcomponent
	 */
	public UIComponent getMcomponent() {
		return mcomponent;
	}

	/**
	 * @param mcomponent the mcomponent to set
	 */
	public void setMcomponent(UIComponent mcomponent) {
		this.mcomponent = mcomponent;
	}

	/**
	 * @return the stealme
	 */
	public Boolean getStealme() {
		return stealme;
	}

	/**
	 * @param stealme the stealme to set
	 */
	public void setStealme(Boolean stealme) {
		this.stealme = stealme;
	}

	public Boolean getDisplayrole() {
		return displayrole;
	}

	/**
	 * @param stealme the stealme to set
	 */
	public void setDisplayrole(Boolean brole) {
		this.displayrole = brole;
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

	public void LoadPositionsList(){
		List<SCAHAPosition> templist = new ArrayList<SCAHAPosition>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.getPositionList()");
			ResultSet rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {;
					SCAHAPosition oposition = new SCAHAPosition();
					oposition.setPositionname(rs.getString("description"));
					oposition.setPositioncode(rs.getString("PositionCode"));
					oposition.setIdposition(rs.getInt("IdPositionList"));

					templist.add(oposition);
				}
				LOGGER.info("We have results for team name");
			}
			rs.close();
			db.cleanup();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading positions");
			e.printStackTrace();
			db.rollback();
			db.free();

		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		this.setPositionList(templist);
		templist = null;
	}
	
}
