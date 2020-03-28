package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseId;
import javax.mail.internet.InternetAddress;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.ClubAdmin;
import com.scaha.objects.ClubAdminList;
import com.scaha.objects.Game;
import com.scaha.objects.GeneralSeason;
import com.scaha.objects.LiveGame;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Person;
import com.scaha.objects.PersonList;
import com.scaha.objects.TeamList;

@ManagedBean
@SessionScoped
public class ClubBean implements Serializable,  MailableObject {

	private Person currentpresident;  
    private Person currentregistrar;  
	private Person currenticeconvenor;
 
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
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

	private int currentLevel = 1;
	private int clubid = 0;
	
	private Club selectedclub = null;
	private TeamList selectedTeamList = null;
	
	//
	// Class Level Variables
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// lets go get it!
	//
	public ClubBean() {
	}

	 @PostConstruct
	 public void init() {
		 
		 //LOGGER.info(" *************** POST INIT FOR CLUB BEAN *****************");

	 }
	 
	public void handleFileUpload(FileUploadEvent event) {
	    this.selectedclub.getLogo().setMmObject(event.getFile().getContents());
	    this.selectedclub.getLogo().setExtension(event.getFile().getContentType());
	    //LOGGER.info("GET THE INFO:" + event.getFile().getFileName() + ":" + event.getFile().getSize());
	}

	/**
	 * This saves the Club object and along with it.. any changes
	 * @return
	 */
	public String save() {  
	        FacesMessage message =  
	            new FacesMessage(FacesMessage.SEVERITY_INFO, "Club " + this.selectedclub.getClubname() + " has been saved", null);  
	        FacesContext.getCurrentInstance().addMessage(null, message);  
	        
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase",pb.getProfile());
	        try {
				this.selectedclub.getLogo().update(db);
			} catch (SQLException e) {
				e.printStackTrace();
			}
	        db.free();
	        return "true";
    } 
	
	public Club getSelectedclub(){
    	return selectedclub;
    }
    
    public void setSelectedclub(Club clubselected){
    	selectedclub=clubselected;
    }


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

	
	/**
	 * @return the bLogo
	 */
	public StreamedContent getClubLogo() {
		return this.getClubLogo(this.selectedclub);
	}
	
	
	public StreamedContent getClubLogo(Club _cl) {
		 
		boolean bstream = true;
		FacesContext context = FacesContext.getCurrentInstance();
		
		//LOGGER.info("getClub Logo Club is" + _cl);
		if (_cl == null) {
			bstream = false;
		} else 	if (_cl.getLogo() == null) {
			//LOGGER.info("getClub for " + _cl + " get Logo returned null...");
			bstream = false;
		} else if (_cl.getLogo().getMmObject() == null) {
			//LOGGER.info("getClub for " + _cl + " not mm object.. its null...");
			bstream = false;
		} else { 
			
			//
			// we are good
		}
		if (context.getCurrentPhaseId() == PhaseId.RENDER_RESPONSE) {
		    // So, we're rendering the HTML. Return a stub StreamedContent so that it will generate right URL.
			//LOGGER.info("we are just rendering an empty response.. ");
			return  new DefaultStreamedContent();
			
		} else {

			if (bstream) {
				//LOGGER.info("We are going for " + _cl + "'s logo vis getSteamedContent()");
				return _cl.getLogo().getStreamedContent();
			}
            
			try {

			//LOGGER.info("we cannot stream.. so lets stream a default image.... ");

			//
			// ok.. through some text up as a defaul ..
			//
				/*BufferedImage bufferedImg = new BufferedImage(100, 25, BufferedImage.TYPE_INT_RGB);  
	            Graphics2D g2 = bufferedImg.createGraphics();  
	            g2.drawString("NO IMG", 10, 10);  
	            ByteArrayOutputStream os = new ByteArrayOutputStream();  
				ImageIO.write(bufferedImg, "png", os);
				return new DefaultStreamedContent(new ByteArrayInputStream(os.toByteArray()), "image/png");
				*/
			
				return scaha.getNoimage().getStreamedContent();

            } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
            
			return new DefaultStreamedContent();

		}
	}
	
	public StreamedContent getClubLogoByParmId() {
		FacesContext context = FacesContext.getCurrentInstance();
		String get = context.getExternalContext().getRequestParameterMap().get("target");
	    if (get == null) {
    		//return new DefaultStreamedContent();
	    	return scaha.getNoimage().getStreamedContent();
	    } else if (get.length() == 0) {
    		//return new DefaultStreamedContent();
    		return scaha.getNoimage().getStreamedContent();
	    }
    	int id = Integer.parseInt(get);
	    Club myclub  = scaha.findClubByID(id);
	    if (myclub == null) {
			//LOGGER.info("*** Could not find club... for id LOGO ID IS (" + get + ") ");
    		//return new DefaultStreamedContent();
			return scaha.getNoimage().getStreamedContent();
	    }
	    
	   // LOGGER.info("*** club is...("+ myclub + ") for id LOGO ID IS (" + get + ") ");
		return getClubLogo(myclub);
	}
	
	//needs to be used to support passing in game
	public StreamedContent getClubLogoByGameObject(Game game, String homeaway) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		String get = context.getExternalContext().getRequestParameterMap().get("targethome");
		
		/*if (homeaway.equals("Away")){
			get = context.getExternalContext().getRequestParameterMap().get("targetaway"+game.getIdlivegame().toString());;
		}else{
			get = context.getExternalContext().getRequestParameterMap().get("targethome"+game.getIdlivegame().toString());;
		}*/
		
		if (get == null) {
    		//return new DefaultStreamedContent();
			return scaha.getNoimage().getStreamedContent();
	    } else if (get.length() == 0) {
    		//return new DefaultStreamedContent();
	    	return scaha.getNoimage().getStreamedContent();
	    }
    	int id = Integer.parseInt(get);
	    Club myclub  = scaha.findClubByID(id);
	    if (myclub == null) {
			//LOGGER.info("*** Could not find club... for id LOGO ID IS (" + get + ") ");
    		//return new DefaultStreamedContent();
			return scaha.getNoimage().getStreamedContent();
	    }
	    
	    //LOGGER.info("*** club is...("+ myclub + ") for id LOGO ID IS (" + get + ") ");
		return getClubLogo(myclub);
	}
	
	public StreamedContent getClubLogoByGameObjectAway(Game game, String homeaway) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		String get = context.getExternalContext().getRequestParameterMap().get("targetaway");
		
		/*if (homeaway.equals("Away")){
			get = context.getExternalContext().getRequestParameterMap().get("targetaway"+game.getIdlivegame().toString());;
		}else{
			get = context.getExternalContext().getRequestParameterMap().get("targethome"+game.getIdlivegame().toString());;
		}*/
		
		if (get == null) {
    		//return new DefaultStreamedContent();
			return scaha.getNoimage().getStreamedContent();
	    } else if (get.length() == 0) {
    		//return new DefaultStreamedContent();
	    	return scaha.getNoimage().getStreamedContent();
	    }
    	int id = Integer.parseInt(get);
	    Club myclub  = scaha.findClubByID(id);
	    if (myclub == null) {
			//LOGGER.info("*** Could not find club... for id LOGO ID IS (" + get + ") ");
    		//return new DefaultStreamedContent();
			return scaha.getNoimage().getStreamedContent();
	    }
	    
	    //LOGGER.info("*** club is...("+ myclub + ") for id LOGO ID IS (" + get + ") ");
		return getClubLogo(myclub);
	}
	
	
	public StreamedContent getClubLogoByGameObjectHome(Game game, String homeaway) {
		FacesContext context = FacesContext.getCurrentInstance();
		
		String get = context.getExternalContext().getRequestParameterMap().get("targethome2");
		
		/*if (homeaway.equals("Away")){
			get = context.getExternalContext().getRequestParameterMap().get("targetaway"+game.getIdlivegame().toString());;
		}else{
			get = context.getExternalContext().getRequestParameterMap().get("targethome"+game.getIdlivegame().toString());;
		}*/
		
		if (get == null) {
    		//return new DefaultStreamedContent();
			return scaha.getNoimage().getStreamedContent();
	    } else if (get.length() == 0) {
    		//return new DefaultStreamedContent();
	    	return scaha.getNoimage().getStreamedContent();
	    }
    	int id = Integer.parseInt(get);
	    Club myclub  = scaha.findClubByID(id);
	    if (myclub == null) {
			//LOGGER.info("*** Could not find club... for id LOGO ID IS (" + get + ") ");
    		//return new DefaultStreamedContent();
			return scaha.getNoimage().getStreamedContent();
	    }
	    
	    //LOGGER.info("*** club is...("+ myclub + ") for id LOGO ID IS (" + get + ") ");
		return getClubLogo(myclub);
	}

	//needs to be used to support passing in game
	public StreamedContent getClubLogoByLiveGameObject(LiveGame game, String homeaway) {
		FacesContext context = FacesContext.getCurrentInstance();
		String get = context.getExternalContext().getRequestParameterMap().get("targethome");
		/*if (homeaway.equals("Away")){
			get = context.getExternalContext().getRequestParameterMap().get("targetaway"+game.getIdlivegame().toString());;
		}else{
			get = context.getExternalContext().getRequestParameterMap().get("targethome"+game.getIdlivegame().toString());;
		}*/
		
		if (get == null) {
    		//return new DefaultStreamedContent();
			return scaha.getNoimage().getStreamedContent();
	    } else if (get.length() == 0) {
    		//return new DefaultStreamedContent();
	    	return scaha.getNoimage().getStreamedContent();
	    }
    	int id = Integer.parseInt(get);
	    Club myclub  = scaha.findClubByID(id);
	    if (myclub == null) {
			//LOGGER.info("*** Could not find club... for id LOGO ID IS (" + get + ") ");
    		//return new DefaultStreamedContent();
			return scaha.getNoimage().getStreamedContent();
	    }
	    
	    //LOGGER.info("*** club is...("+ myclub + ") for id LOGO ID IS (" + get + ") ");
		return getClubLogo(myclub);
	}
	
	/**
	 * This sets the call..
	 * @param _id
	 * @return
	 */
	public String setClub(int _id) {
		//LOGGER.info("ClubBean received a Club id of:" + + _id);
		this.selectedclub = scaha.findClubByID(_id);
		//LOGGER.info((this.selectedclub == null ? "setClub found nothing in masterlist!" : "setting to club " + this.selectedclub.getClubname()));
		return "";
	}
	
	public void setClubViaParm() {
		this.selectedclub = scaha.findClubByID(this.clubid);
		//LOGGER.info((this.selectedclub == null ? "setClub found nothing in masterlist!" : "setting to club " + this.selectedclub.getClubname()));
	
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
	 * @return the currentlevel
	 */
	public int getCurrentLevel() {
		return this.currentLevel;
	}
	/**
	 * @param currentlevel the currentlevel to set
	 */
	public void setCurrentLevel(int currentlevel) {
		this.currentLevel = currentlevel;
	}
	
	/**
 	* This gets the current list of teams given the season setting in the ScahaBean Application Session 
 	* 
	*/
	public TeamList getTeams() {
		
		if (selectedclub == null) {
			//LOGGER.info("SELECTED CLUB IS NULL!!!");
			return null;
		}
		
		if (selectedclub.getScahaTeams() != null) return selectedclub.getScahaTeams();

		//LOGGER.info("The Selected Club Has no Teams Set!:" + selectedclub.getClubname());

		// lets refresh the team base
		GeneralSeason scahags = scaha.getScahaSeasonList().getCurrentSeason();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase",pb.getProfile());
		
		try {
			selectedTeamList = TeamList.NewTeamListFactory(pb.getProfile(), db, this.selectedclub, scahags, true, false);
			selectedclub.setScahaTeams(selectedTeamList);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		db.free();
		//
		// we are not going to stitch it to the Club just yet.. its a question of needing to at the moment.
		// do we want to get all teams in memory over time as people look?
		//
		// no.. 
		return selectedTeamList;
	}

	
	 public void setUpStaffEditing() {
		//LOGGER.info("SetUpStaffEditing for " + getSelectedclub());
		
		this.setCurrentpresident(getSelectedclub().getCal().getStaffer("C-PRES"));
		this.setCurrentregistrar(getSelectedclub().getCal().getStaffer("C-REG"));
		this.setCurrenticeconvenor(getSelectedclub().getCal().getStaffer("C-ICE"));
	 }
	 
	 @SuppressWarnings("unchecked")
	 public List<Person> completePerson(String query) {  
    	if (query.length() > 2) {
    		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", pb.getProfile());
    		PersonList pl = null;
			try {
				pl = PersonList.NewPersonListFactory(db, query);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			db.free();
			return (List<Person>) pl.getWrappedData();
    	}
    	return null;
		
	 } 
	 
	 /**
	 * This saves the Club object and along with it.. any changes
	 * @return
	 */
	public String saveStaff() {  
		
	        ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", pb.getProfile());
	        
	        //
	        // ok.. we track three Club Staff Positions right now!
	        // so. we have to see if they have changed or not.. 
	        // if they have changed.. we save
	        // if they are the same.. do nothing
	        
	        Person curPres = getSelectedclub().getCal().getStaffer("C-PRES");
	        Person curReg = getSelectedclub().getCal().getStaffer("C-REG");
	        Person curIce = getSelectedclub().getCal().getStaffer("C-ICE");
	       
	        try {

		        //
		        // There is a much more general way to do this.. but we do not have time.. and we need to be non abstract
		        // to make support easier.
		        //
		        if (curPres == null && this.currentpresident != null) { 
		        	//LOGGER.info("Curr President is null.. selected president is NOT");
		        	db.updateClubPresident(pb.getProfile(),this.selectedclub, curPres, this.currentpresident);
		        } else if (curPres != null && curPres.ID != this.currentpresident.ID) {
		        	//LOGGER.info("Curr President is NOT null.. selected president and cur president are different");
		        	db.updateClubPresident(pb.getProfile(),this.selectedclub, curPres, this.currentpresident);
		        } else {
		        	//LOGGER.info("Could Not tell if the president changed");
		        }
	
		        if (curReg == null && this.currentregistrar != null) { 
		        	//LOGGER.info("Curr Registrar is null.. selected Registrar is NOT");
		        	db.updateClubRegistrar(pb.getProfile(),this.selectedclub, curReg, this.currentregistrar);
		        } else if (curReg != null && curReg.ID != this.currentregistrar.ID) {
		        	//LOGGER.info("Curr Registrar is NOT null.. selected Registrar and cur Registrar are different");
		        	db.updateClubRegistrar(pb.getProfile(),this.selectedclub, curReg, this.currentregistrar);
		        } else {
		        	//LOGGER.info("Could Not tell if the Registrar changed");
		        }
	
		        if (curIce == null && this.currenticeconvenor != null) { 
		        	//LOGGER.info("Curr IceMan is null.. selected IceMan is NOT");
	        	db.updateClubIceConvenor(pb.getProfile(),this.selectedclub, curIce, this.currenticeconvenor);
		        } else if (curIce != null && curIce.ID != this.currenticeconvenor.ID) {
		        	//LOGGER.info("Curr IceMan is NOT null.. selected IceMan and cur IceMan are different");
		        	db.updateClubIceConvenor(pb.getProfile(),this.selectedclub, curIce, this.currenticeconvenor);
		        } else {
		        	//LOGGER.info("Could Not tell if the Ice Man changed");
		        }
	        
		        //
		        // Wait.. we need to get updated the ClubAdmin section of the global Club List here!
		        //
		        //
		        // Crude but effective
		        //
		        @SuppressWarnings("unchecked")
				List<ClubAdmin> lca = (List<ClubAdmin>)this.selectedclub.getCal().getWrappedData();
				lca.clear();
				PreparedStatement psCa = db.prepareStatement("call scaha.getClubAdminInfo(?)");
				//LOGGER.info("Reloading the Club Staff Information Back to the Object");
				this.selectedclub.setCal(ClubAdminList.NewClubAdminListFactory(pb.getProfile(), psCa, selectedclub));
				psCa.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
		        db.free();
			}
	        
	        //
	        // now we need to send out e-mails to all effected partied!!
	        //
	        return "true";
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

	/**
	 * @return the currentpresident
	 */
	public Person getCurrentpresident() {
		return currentpresident;
	}

	/**
	 * @param currentpresident the currentpresident to set
	 */
	public void setCurrentpresident(Person currentpresident) {
		this.currentpresident = currentpresident;
	}

	/**
	 * @return the currentregistrar
	 */
	public Person getCurrentregistrar() {
		return currentregistrar;
	}

	/**
	 * @param currentregistrar the currentregistrar to set
	 */
	public void setCurrentregistrar(Person currentregistrar) {
		this.currentregistrar = currentregistrar;
	}

	/**
	 * @return the currenticeconvenor
	 */
	public Person getCurrenticeconvenor() {
		return currenticeconvenor;
	}

	/**
	 * @param currenticeconvenor the currenticeconvenor to set
	 */
	public void setCurrenticeconvenor(Person currenticeconvenor) {
		this.currenticeconvenor = currenticeconvenor;
	} 

	/**
	 * Simply return the team counts for a given Division and Skill Set;
	 * @param _strDivision
	 * @param _strSkill
	 * @return
	 */
	public String getTeamTotals(String _strDivision, String _strSkill) {
		
		int icount = 0;
		if (scaha.getScahaClubList() == null) return "";
		for (Club c : scaha.getScahaClubList()) {
			icount = icount + c.getTeamICounts(_strDivision, _strSkill);
		}
		
		return Integer.toString(icount);
		
	}
	
	/**
	 * Simply return the team counts for a given Division and Skill Set;
	 * @param _strDivision
	 * @param _strSkill
	 * @return
	 */
	public String getTotalTeamCount(boolean _exh) {
		
		int icount = 0;
		for (Club c : scaha.getScahaClubList()) {
			icount = icount + c.getTotalITeamCount(_exh);
		}
		
		return Integer.toString(icount);
		
	}

	public String geteXTeamTotals (String _strDivision, String _strSkill) {
		
		int icount = 0;
		if (scaha.getScahaClubList() == null) return "";
		for (Club c : scaha.getScahaClubList()) {
			icount = icount + c.geteXTeamICounts(_strDivision, _strSkill);
		}
		return Integer.toString(icount);
	}
	
	public String getGTotalTeams (String _strDivision, String _strSkill) {
		
		int icount = 0;
		if (scaha.getScahaClubList() == null) return "";
		for (Club c : scaha.getScahaClubList()) {
			icount = icount + c.geteXTeamICounts(_strDivision, _strSkill) + c.getTeamICounts(_strDivision, _strSkill);
		}
		return Integer.toString(icount);

	}
	
	/**
	 * Simply return the team counts for a given Division and Skill Set;
	 * @param _strDivision
	 * @param _strSkill
	 * @return
	 */
	public String getTotalXTeamCount() {
		
		int icount = 0;
		if (scaha.getScahaClubList() == null) return "";
		for (Club c : scaha.getScahaClubList()) {
			icount = icount + c.getTotalXITeamCount();
		}
		
		return Integer.toString(icount);
		
	}

	/**
	 * @return the clubid
	 */
	public int getClubid() {
		return clubid;
	}

	/**
	 * @param clubid the clubid to set
	 */
	public void setClubid(int clubid) {
		this.clubid = clubid;
	}
	
	public void Viewclubdetail(Club club){
		this.setSelectedclub(club);
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("viewclubdetail.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
