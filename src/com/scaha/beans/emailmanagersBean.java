package com.scaha.beans;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.*;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class emailmanagersBean implements Serializable, MailableObject {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/loireceipt.html");

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;

	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	transient private ResultSet rs = null;

	private Integer profileid=null;
	private String to = null;
	private String subject = null;
	private String cc = null;
	private String body = null;
	private List<Player> players = null;
	private List<Player> selectedplayers = null;
	private PlayerDataModel PlayerDataModel = null;
	private GeneralSeasonList seasons = null;
	private GeneralSeason selectedseason;
	private Integer selectedseasonid = null;
	private ScheduleList schedules;
	private List<Schedule> schedulelist =  null;
	private List<Participant> partpicklist =  null;
	private ParticipantList partlist = null;
	private Integer selectedscheduleid = 0;








	@PostConstruct
    public void init() {
		FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );

		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
    	this.setProfid(pb.getProfile().ID);

		//need to add scaha session object
		ValueExpression scahaexpression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{scahaBean}", Object.class );

		scaha = (ScahaBean) scahaexpression.getValue( context.getELContext() );



		this.setSeasons(scaha.getScahaSeasonList());
		this.selectedseason = scaha.getScahaSeasonList().getCurrentSeason();
		this.selectedseasonid = selectedseason.ID;
		this.refreshScheduleList();

		playersDisplay();
	}


	public emailmanagersBean() {
        
    }

	public int getSelectedscheduleid() {
		return selectedscheduleid;
	}

	/**
	 * @param selectedscheduleid the selectedscheduleid to set
	 */
	public void setSelectedscheduleid(int selectedscheduleid) {
		this.selectedscheduleid = selectedscheduleid;
	}

	public int getSelectedseasonid() {
		return selectedseasonid;
	}

	/**
	 * @param selectedseasonid the selectedseasonid to set
	 */
	public void setSelectedseasonid(int selectedseasonid) {
		this.selectedseasonid = selectedseasonid;
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

	public List<Player> getPlayers(){
		return players;
	}

	public void setPlayers(List<Player> list){
		players = list;
	}

	public List<Player> getSelectedplayers(){
		return selectedplayers;
	}

	public void setSelectedplayers(List<Player> list){
		selectedplayers = list;
	}

	public GeneralSeasonList getSeasons() {
		return seasons;
	}

	@SuppressWarnings("unchecked")
	public List<GeneralSeason> getSeasonlist() {
		return (List<GeneralSeason>)seasons.getWrappedData();
	}

	/**
	 * @param seasons the seasons to set
	 */
	public void setSeasons(GeneralSeasonList seasons) {
		this.seasons = seasons;
		//LOGGER.info("Here is our General Season:");
		//LOGGER.info(this.seasons.toString());
	}


	/**
	 * @return the selectedseason
	 */
	public GeneralSeason getSelectedseason() {
		return selectedseason;
	}

	/**
	 * @param selectedseason the selectedseason to set
	 */
	public void setSelectedseason(GeneralSeason selectedseason) {
		this.selectedseason = selectedseason;
	}

	public List<Schedule> getSchedulelist() {
		return schedulelist;
	}

	@SuppressWarnings("unchecked")
	private List<Schedule> getScheduleList() {
		return (List<Schedule>)schedules.getWrappedData();
	}

	/**
	 * @param schedulelist the schedulelist to set
	 */
	public void setSchedulelist(List<Schedule> schedulelist) {
		this.schedulelist = schedulelist;
	}

	public void onSeasonChange() {
		//LOGGER.info("season change request detected new id is:" + this.selectedseasonid);
		this.selectedseason = this.seasons.getGeneralSeason(this.selectedseasonid);
		refreshScheduleList();
	}

	public void onScheduleChange() {
		//LOGGER.info("season change request detected new id is:" + this.selectedseasonid);
		playersDisplay();
	}
	public void refreshSeasonList() {

		//LOGGER.info("Getting season List");
		//
		// ok.. lets do the seasons now..
		//
		this.seasons = scaha.getScahaSeasonList();

	}

	public void refreshScheduleList() {

		//
		// ok.. lets do the schedules now..
		//
		//LOGGER.info("Refreshing Schedule List for season:" + this.selectedseason);
		this.schedulelist = null;
		this.schedules = null;
		this.partlist = null;
		this.partpicklist = null;
		if (this.selectedseason != null) {

			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
			try {
				this.schedules = ScheduleList.ListFactory(pb.getProfile(), db,this.selectedseason,scaha.getScahaTeamList());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				db.free();
			}


			//LOGGER.info("season schedule is: " + schedules);

			this.schedules = this.selectedseason.getSchedList();
//			LOGGER.info("season schedule is: " + schedules);

			if (schedules != null) {
				if (this.schedules.getRowCount() > 0) {
					this.schedulelist = this.getScheduleList();
				}	else {
					//LOGGER.info("Refresh.. zero list.. leaving null:" + this.schedules.getRowCount());
				}
			}
		}

	}

	//retrieves list of players
	public void playersDisplay(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Player> tempresult = new ArrayList<Player>();

		try{

				CallableStatement cs = db.prepareCall("CALL scaha.getAllManagers(?)");
				cs.setInt("inskilllevel", this.selectedscheduleid);
				rs = cs.executeQuery();

				if (rs != null){

					while (rs.next()) {
						String idplayer = rs.getString("idperson");
						String managername = rs.getString("managername");
						String scurrentteam = rs.getString("teamname");
						String sphone = rs.getString("phone");
						String semail = rs.getString("altemail");


						Player oplayer = new Player();
						oplayer.setIdplayer(idplayer);
						oplayer.setFirstname(managername);
						oplayer.setCurrentteam(scurrentteam);
						oplayer.setPhone(sphone);
						oplayer.setEmail1(semail);
						tempresult.add(oplayer);
					}

					//LOGGER.info("We have results for lois for the team: " + selectedteam);

				}

				db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Searching FOR managers");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		//setResults(tempresult);
		setPlayers(tempresult);
		PlayerDataModel = new PlayerDataModel(players);
	}

	public PlayerDataModel getPlayerdatamodel(){
		return PlayerDataModel;
	}

	public void setPlayerdatamodel(PlayerDataModel odatamodel){
		PlayerDataModel = odatamodel;
	}


	public String getSubject() {
		// TODO Auto-generated method stub
		return subject;
	}
    
    public void setSubject(String ssubject){
    	subject = ssubject;
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
	
    public String getTextBody() {
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		myTokens.add(":BODY:" + this.body);
		return Utils.mergeTokens(emailmanagersBean.mail_reg_body, myTokens);
	}

	
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return cc;
	}
	
	public void setPreApprovedCC(String scc){
		cc = scc;
	}

	public String getBody() {
		// TODO Auto-generated method stub
		return this.body;
	}

	public void setBody(String sto){
		this.body = sto;
	}


	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return to;
	}
    
    public void setToMailAddress(String sto){
    	to = sto;
    }
	
	public Integer getProfid(){
    	return profileid;
    }
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }
    



	public void sendEmail(){


		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
		try{

			if (db.setAutoCommit(false)) {
			
				//Need to check loi code from family first
 				//LOGGER.info("verify loi code provided");
 				CallableStatement cs = db.prepareCall("CALL scaha.getallloibyclub(?)");
 				cs.setInt("personid", 0);
    		    
    		    rs = cs.executeQuery();
    			
    		    Integer resultcount = 0;
    		    if (rs != null){
    				
    				while (rs.next()) {
    					resultcount = rs.getInt("idmember");
    				}
    				//LOGGER.info("We have code validation results for player details by person id");
    				if (resultcount.equals(0)){
    					FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN,"", "The provided LOI signature code is invalid."));
    				}
    			}
    			rs.close();
    		    db.cleanup();
 				
    		        //hard my email address for testing purposes
	    		    //to = "lahockeyfan2@yahoo.com";
	    		    this.setToMailAddress(to);
	    		    this.setPreApprovedCC("");

					SendMailSSL mail = new SendMailSSL(this);
					//LOGGER.info("Finished creating mail object for " + this.firstname + " " + this.lastname + " LOI with " + this.getClubName());
					mail.sendMail();
					
					db.commit();
					db.cleanup();
					//return "True";
					
					FacesContext context = FacesContext.getCurrentInstance();
		    		String origin = ((HttpServletRequest)context.getExternalContext().getRequest()).getRequestURL().toString();
					try{
						context.getExternalContext().redirect("printableloi.xhtml?playerid=");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
    		    } else {
    		    	
    		    }
    		   

			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN EMAIL SEND PROCESS");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}
		
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

	//the following methods are used for adding/removing the selected managers from the list to be emailed.
	public void onSelect(Player car, String typeOfSelection, String indexes) {
		if (null != car) {
			getSelectedplayers().add(car);
		} else if (null != indexes) {
			String[] indexArray = indexes.split(",");
			for (String index:indexArray) {
				int i = Integer.valueOf(index);
				Player newCar=players.get(i);
				if (!selectedplayers.contains(newCar)) {
					getSelectedplayers().add(newCar);
				}
			}
		}
	}

	public void onDeselect(Player car, String typeOfSelection, String indexes) {
		if (null != car) {
			getSelectedplayers().remove(car);
		} else if (null != indexes) {
			String[] indexArray = indexes.split(",");
			for (String index:indexArray) {
				int i = Integer.valueOf(index);
				getSelectedplayers().remove(players.get(i));
			}
		}
	}
}

