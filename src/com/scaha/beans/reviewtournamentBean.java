package com.scaha.beans;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.LiveGame;
import com.scaha.objects.MailableObject;
import com.scaha.objects.TempGame;
import com.scaha.objects.TempGameDataModel;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class reviewtournamentBean implements Serializable, MailableObject{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_nonreportedgames_body = Utils.getMailTemplateFromFile("/mail/nonreportedtournament.html");

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	@ManagedProperty(value="#{scoreboardBean}")
	private ScoreboardBean sb;

	private String to = null;
	private String subject = null;
	private String cc = null;
	private String bodytext = null;
	private String teamname = null;
	private Integer clubid = null;

	transient private ResultSet rs = null;
	transient private ResultSet rs2 = null;
	transient private ResultSet rs3 = null;

	//lists for generated datamodels
	private List<TempGame> games = null;
	private List<TempGame> gamesnoreporting = null;

	//bean level properties used by multiple methods
	private Integer profileid = 0;

	//datamodels for all of the lists on the page
	private TempGameDataModel TempGameDataModel = null;
	private TempGameDataModel TempGameDataModelNoReporting = null;

    //properties for storing the selected row of each of the datatables or drop downs
    private TempGame selectedgame = null;
    private Integer selectedschedule = null;




	@PostConstruct
    public void init() {
		games = new ArrayList<TempGame>();
        TempGameDataModel = new TempGameDataModel(games);

		gamesnoreporting = new ArrayList<TempGame>();
		TempGameDataModelNoReporting = new TempGameDataModel(gamesnoreporting);


		this.setProfid(pb.getProfile().ID);
        this.setPb(pb);

        //Load SCAHA Games without Reporting
		loadtournamentsnoscoresheetReporting();

	}

    public reviewtournamentBean() {
        
    }

	public String getSubject() {
		// TODO Auto-generated method stub
		return subject;
	}

	public void setSubject(String ssubject){
		subject = ssubject;
	}

	public String getTextBody() {
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("GAMEROWS:" + this.bodytext);
		myTokens.add("TEAMNAME:" + this.teamname);
		//return Utils.mergeTokens(reviewscahagamesBean.mail_nonreportedgames_body,myTokens);
		return "";
	}


	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return cc;
	}
	@Override
	public InternetAddress[] getToMailIAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setPreApprovedCC(String scc){
		cc = scc;
	}

	public InternetAddress[] getPreApprovedICC() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return to;
	}

	public void setToMailAddress(String sto){
		to = sto;
	}

	public Integer getClubid(){
		return clubid;
	}

	public void setClubid(Integer idprofile){
		clubid = idprofile;
	}


	public Integer getProfid(){
    	return profileid;
    }	
    
    public void setProfid(Integer idprofile){
    	profileid = idprofile;
    }

	public String getTeamname(){
		return this.teamname;
	}

	public void setTeamname(String selectedGame){
		this.teamname = selectedGame;
	}


	public String getBodytext(){
		return this.bodytext;
	}

	public void setBodytext(String selectedGame){
		this.bodytext = selectedGame;
	}

	public TempGame getSelectedgame(){
		return selectedgame;
	}
	
	public void setSelectedgame(TempGame selectedGame){
		selectedgame = selectedGame;
	}
    
	public Integer getSelectedschedule(){
		return selectedschedule;
	}
	
	public void setSelectedschedule(Integer selectedSchedule){
		selectedschedule = selectedSchedule;
	}
    
	public List<TempGame> getGames(){
		return games;
	}
	
	public void setGames(List<TempGame> list){
		games = list;
	}

	public List<TempGame> getGamesnoreporting(){
		return gamesnoreporting;
	}

	public void setGamesnoreporting(List<TempGame> list){
		gamesnoreporting = list;
	}

	public TempGameDataModel getTempGamedatamodel(){
    	return TempGameDataModel;
    }
    
    public void setTempgamedatamodel(TempGameDataModel odatamodel){
    	TempGameDataModel = odatamodel;
    }

	public TempGameDataModel getTempGameDataModelNoReporting(){
		return TempGameDataModelNoReporting;
	}

	public void setTempGameDataModelNoReporting(TempGameDataModel odatamodel){
		TempGameDataModelNoReporting = odatamodel;
	}

    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
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

	
	public ScoreboardBean getSb() {
		return sb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setSb(ScoreboardBean pb) {
		this.sb = pb;
	}
	
	
    
	
	
		

	public void loadtournamentsnoscoresheetReporting(){
		List<TempGame> tempresult = new ArrayList<TempGame>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.gettournamentscoresheetReporting()");
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					String idteam = rs.getString("idteam");
					String tournament = rs.getString("tournament");
					String scoresheetnotloaded = rs.getString("scoresheetnotloaded");
					String scoresheetcount = rs.getString("scoresheetcount");

					TempGame ogame = new TempGame();
					ogame.setIdgame(Integer.parseInt(idteam));
					ogame.setDate(tournament);
					ogame.setTime(scoresheetnotloaded);
					ogame.setOpponent(scoresheetcount);
					tempresult.add(ogame);

				}
				//LOGGER.info("We have results for scaha games schedule for review by statistician for schedule:" + this.selectedschedule);
			}


			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN getting tournaments without scoresheets");
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		setGamesnoreporting(tempresult);
		TempGameDataModelNoReporting = new TempGameDataModel(gamesnoreporting);
	}

	public void EmailManagers(){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get distinct list of teams
			CallableStatement cs = db.prepareCall("CALL scaha.getDistinctTeamswithNonSCAHAReporting()");
			rs = cs.executeQuery();

			CallableStatement cs2 = db.prepareCall("CALL scaha.getSCAHAGamesNoReportingByTeamId(?)");

			CallableStatement cs3 = db.prepareCall("CALL scaha.getClubRegistrarEmail(?)");

			CallableStatement cs4 = db.prepareCall("CALL scaha.getTeamManagerEmails(?)");
			//iterate through teams and send email containing all games needing game details and scoresheet entry
			if (rs != null) {

				while (rs.next()) {
					this.bodytext = "";
					this.to = "";
					Integer idteam = rs.getInt("teamname");
					cs2.setInt("in_teamid", idteam);
					rs2 = cs2.executeQuery();

					if (rs2 != null) {

						while (rs2.next()) {
							//format email with all of the games the team has not entered.
							String idgame = rs2.getString("idlivegame");
							String division = rs2.getString("division");
							String skilllevel = rs2.getString("skilllevel");
							String offendingteam = rs2.getString("offendingteam");
							Integer clubid = rs2.getInt("idclub");
							String opponent = rs2.getString("opponent");
							String dates = rs2.getString("date");
							String time = rs2.getString("time");
							String location = rs2.getString("location");
							String statusnoreporting = rs2.getString("status");
							String scoresheetstatus = rs2.getString("scoresheet");

							String tempgamerows =  "<tr><td>&nbsp;" + idgame +"&nbsp;</td><td>&nbsp;";
							tempgamerows = tempgamerows + dates +"&nbsp;</td><td>&nbsp;";
							/*tempgamerows = tempgamerows + time.replace(":","") + "&nbsp;</td><td>&nbsp;";*/
							tempgamerows = tempgamerows + division + " " + skilllevel +  "&nbsp;</td><td>&nbsp;";
							/*tempgamerows = tempgamerows + skilllevel + "&nbsp;</td><td>&nbsp;";*/
							tempgamerows = tempgamerows + opponent + "&nbsp;</td><td>&nbsp;";
							tempgamerows = tempgamerows + location + "&nbsp;</td><td>&nbsp;";
							tempgamerows = tempgamerows + statusnoreporting + "&nbsp;</td><td>&nbsp;";
							tempgamerows = tempgamerows + scoresheetstatus + "&nbsp;</td>";
							tempgamerows = tempgamerows+ "</tr>";

							this.bodytext = this.bodytext + tempgamerows ;
							this.teamname = offendingteam;
							this.clubid=clubid;
						}
						//retrieve emails for club registrar and team managers
						cs3.setInt("iclubid", this.clubid);
						rs3 = cs3.executeQuery();
						if (rs3 != null){
							while (rs3.next()) {
								if (!to.equals("")){
									to = to + "," + rs3.getString("usercode");
								}else {
									to = rs3.getString("usercode");
								}
							}
						}
						rs3.close();

						cs4.setInt("in_teamid", idteam);
						rs3 = cs4.executeQuery();
						if (rs3 != null){
							while (rs3.next()) {
								if (!to.equals("")){
									to = to + "," + rs3.getString("usercode");
								}else {
									to = rs3.getString("usercode");
								}
							}
						}
						rs3.close();


						//this.setSubject(this.teamname + " - Missing SCAHA Game Detail Entry and Scoresheet Upload " + this.to);

						//hard my email address for testing purposes
						//this.to = "lahockeyfan2@yahoo.com";

						this.to = to + ",lahockeyfan2@yahoo.com";
						this.setToMailAddress(to);
						this.setPreApprovedCC("lahockeyfan2@yahoo.com");
						this.setSubject(this.teamname + " - Missing SCAHA Game Detail Entry and Scoresheet Upload");

						SendMailSSL mail = new SendMailSSL(this);
						//LOGGER.info("Finished creating mail object for " + this.firstname + " " + this.lastname + " LOI with " + this.getClubName());
						mail.sendMail();


					}
				}
			}
				//save team to the log of teams not entering their game details.


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN getting scaha games no reporting for review by statistician");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}


	}

}

