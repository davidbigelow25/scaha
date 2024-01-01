package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.scaha.objects.*;

//import com.gbli.common.SendMailSSL;

public class editrosterBean implements Serializable, MailableObject {

//public class  implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_reg_body = Utils.getMailTemplateFromFile("/mail/voidloi.html");
	transient private ResultSet rs = null;
	private List<Coach> players = null;
	private List<Coach> coaches = null;
	private Coach selectedcoach = null;
	private List<Player> pdr = null;
	private List<Player> blockrecruitment = null;
	private Player selectedplayer = null;
	private Integer teamid = null;
	private String teamname = null;
	private String enteredrosterdate = null;
	private String newrosterdate = null;
	private String pdrcount = null;
	private String totalplayercount = null;
	private String to = null;
	private String subject = null;
	private String cc = null;
	private Integer clubid = null;
	private String clubname = null;




	@PostConstruct
    public void init() {
        // In @PostConstruct (will be invoked immediately after construction and dependency/property injection).
		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
    	
    	if(hsr.getParameter("teamid") != null)
        {
    		this.teamid = Integer.parseInt(hsr.getParameter("teamid").toString());
        }

		generatePDR();
    	generateBlock();
    	getRoster();
		//updatePDRAAATeams();

    }
	
    public editrosterBean() {  
    	    	
		
    }

	public String getClubname() {
		return clubname;
	}

	public void setClubname(String clubname) {
		this.clubname = clubname;
	}

	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return cc;
	}

	public void setPreApprovedCC(String scc){
		cc = scc;
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
	@Override
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return to;
	}

	public void setToMailAddress(String sto){
		to = sto;
	}

	public String getTextBody() {
		// TODO Auto-generated method stub
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("FIRSTNAME:" + this.selectedplayer.getFirstname());
		myTokens.add("LASTNAME:" + this.selectedplayer.getLastname());
		myTokens.add("CLUBNAME:" + this.getClubname());

		return Utils.mergeTokens(editrosterBean.mail_reg_body,myTokens);

	}


	public Integer getClubid() {
		return clubid;
	}

	public void setClubid(Integer clubid) {
		this.clubid = clubid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getPdrcount(){
		return pdrcount;
	}

	public void setPdrcount(String name){
		pdrcount=name;
	}

	public String getTotalplayercount(){
		return totalplayercount;
	}

	public void setTotalplayercount(String name){
		totalplayercount=name;
	}

	public String getNewrosterdate(){
    	return newrosterdate;
    }
    
    public void setNewrosterdate(String name){
    	newrosterdate=name;
    }
    
    
    public String getEnteredrosterdate(){
    	return enteredrosterdate;
    }
    
    public void setEnteredrosterdate(String name){
    	enteredrosterdate=name;
    }
    
    
    public String getTeamname(){
    	return teamname;
    }
    
    public void setTeamname(String name){
    	teamname=name;
    }
    
    public Integer getTeamid(){
    	return teamid;
    }
    
    public void setTeamid(Integer id){
    	teamid=id;
    }
    
    public Player getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Player splayer){
		selectedplayer = splayer;
	}
    
    
    public void getRoster(){
		List<Coach> templist = new ArrayList<Coach>();
		List<Coach> tempcoachlist = new ArrayList<Coach>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getTeamByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.teamname = rs.getString("teamname");
					this.totalplayercount = rs.getString("totalplayercount");
					this.pdrcount = rs.getString("pdrcount");
				}
				LOGGER.info("We have results for team name");
			}
			rs.close();
			db.cleanup();
    		
    		//next get player roster
//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getRosterPlayersByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String playerid = rs.getString("idroster");
					String fname = rs.getString("fname");
					String lname = rs.getString("lname");
					String eligibility = rs.getString("eligibility");
					String loidate = rs.getString("loidate");
					String drop = rs.getString("dropped");
					String added = rs.getString("added");
					String active = rs.getString("active");
					String updated = rs.getString("updated");
					String jerseynumber = rs.getString("jerseynumber");
					String dob = rs.getString("dob");
					String gp = rs.getString("gp");
					String scitizenship = rs.getString("citizenship");
					String scitizenshipexpiredate = rs.getString("citizenshipexpiredate");
					String scitizenshiptransfer = rs.getString("citizenshiptransfer");
					if (scitizenshiptransfer==null){
						scitizenshiptransfer="0";
					}
					String sbirthcertificate = rs.getString("birthcertificate");
					String sindefinite = rs.getString("indefinite");
					String safesport = rs.getString("safesportindicator");
					String suspended =rs.getString("suspended");
					Integer transferid = rs.getInt("idcitizenshiptransfers");
					Integer transfer = rs.getInt("citizenshiptransfers");
					Integer transferindefinite = rs.getInt("indefinite");
					Integer birthcertificate = rs.getInt("birthcertificate");
					String citizenship = rs.getString("citizenship");
					Boolean is18safesport = rs.getBoolean("is18display");
					String safesportfor18 = rs.getString("safesportfor18");
					String expirationdate = rs.getString("expirationdate");
					Integer usaroster = rs.getInt("usaroster");
					String notes = rs.getString("notes");
					String isbullying = rs.getString("isbullying");

					Coach player = new Coach();
					player.setIdcoach(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);
					player.setEligibility(loidate);
					player.setRosterdate(eligibility);
					player.setDrop(drop);
					player.setAdded(added);
					player.setActive(active);
					player.setUpdated(updated);
					player.setJerseynumber(jerseynumber);
					player.setDob(dob);
					player.setGp(gp);
					player.setCitizenship(scitizenship);
					player.setCitizenshiptransfer(scitizenshiptransfer);
					player.setCtverified(scitizenshiptransfer);
					player.setCTExpirationdate(scitizenshipexpiredate);
					player.setNotes(notes);
					player.setSafesport(safesport);
					player.setSuspended(suspended);
					if (scitizenship!=null){
						if (!scitizenship.equals("USA")){
							if (sindefinite!=null){
								if (sindefinite.equals("1")){
									player.setCitizenshiplabel("Transfer does not expire");
								} else if (sindefinite.equals("1") && scitizenshiptransfer.equals("1")){
									player.setCitizenshiplabel("Transfer expires " + scitizenshipexpiredate);
								} else if (sindefinite.equals("0") && scitizenshiptransfer.equals("1")){
									player.setCitizenshiplabel("Transfer expires " + scitizenshipexpiredate);
								} else if (sindefinite.equals("0") && scitizenshiptransfer.equals("0")){
									player.setCitizenshiplabel("Transfer is needed.");
								} else if (sindefinite.equals("1") && scitizenshiptransfer.equals("0")){
									player.setCitizenshiplabel("Transfer is needed.");
								} else {
									player.setCitizenshiplabel("Transfer is needed.");
								}
							} else {
								player.setCitizenshiplabel("Transfer is needed.");
							}
						}
					}

					player.setBirthcertificate(birthcertificate);
					player.setBcverified(sbirthcertificate);
					player.setTransferid (transferid);
					player.setTransfer(transfer);
					player.setTransferindefinite(transferindefinite);
					player.setExpirationdate(expirationdate);
					player.setBirthcertificate(birthcertificate);
					player.setCitizenship(citizenship);
					player.setIs18safesport(is18safesport);
					player.setSafesportfor18(safesportfor18);
					player.setUsaroster(usaroster);
					player.setIsbullying(isbullying);

					templist.add(player);
					player = null;
				}
				LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();
    		
			//next get coach roster
			cs = db.prepareCall("CALL scaha.getRosterCoachesByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String coachid = rs.getString("idroster");
					String cfname = rs.getString("fname");
					String clname = rs.getString("lname");
					String celigibility = rs.getString("eligibility");
					String cloidate = rs.getString("loidate");
					String cdrop = rs.getString("dropped");
					String cadded = rs.getString("added");
					String cactive = rs.getString("active");
					String cupdated = rs.getString("updated");
					String teamrole = rs.getString("teamrole");
					String screeningexpires = rs.getString("screeningexpires");
					String cepnumber = rs.getString("cepnumber");
					String ceplevel = rs.getString("ceplevel");
					String cepexpires = rs.getString("cepexpires");
					String u8 = rs.getString("eightu");
					String u10 = rs.getString("tenu");
					String u12 = rs.getString("twelveu");
					String u14 = rs.getString("fourteenu");
					String u18 = rs.getString("eighteenu");
					String girls = rs.getString("girls");
					Integer safesport = rs.getInt("safesport");
					String notes = rs.getString("notes");
					String sportexpires = rs.getString("sportexpires");
					String suspended =rs.getString("suspended");
					String isbullying = rs.getString("isbullying");


					Coach coach = new Coach();
					coach.setIdcoach(coachid);
					coach.setFirstname(cfname);
					coach.setLastname(clname);
					coach.setEligibility(celigibility);
					coach.setLoidate(cloidate);
					coach.setDrop(cdrop);
					coach.setAdded(cadded);
					coach.setActive(cactive);
					coach.setUpdated(cupdated);
					coach.setTeamrole(teamrole);
					coach.setScreeningexpires(screeningexpires);
					coach.setCepnumber(cepnumber);
					coach.setCeplevel(ceplevel);
					coach.setCepexpires(cepexpires);
					coach.setU8(u8);
					coach.setU10(u10);
					coach.setU12(u12);
					coach.setU14(u14);
					coach.setU18(u18);
					coach.setGirls(girls);
					coach.setSafesportforcoachlist(safesport);
					coach.setNotes(notes);
					coach.setSportexpires(sportexpires);
					coach.setSuspended(suspended);
					coach.setSuspend(suspended);

					String coachlist = "";
					if (coach.getU8().equals("Yes")){
						coachlist = coachlist.concat("8U");
					}
					if (coach.getU10().equals("Yes")){
						coachlist = coachlist.concat(",10U");
					}
					if (coach.getU12().equals("Yes")){
						coachlist = coachlist.concat(",12U");
					}
					if (coach.getU14().equals("Yes")){
						coachlist = coachlist.concat(",14U");
					}
					if (coach.getU18().equals("Yes")){
						coachlist = coachlist.concat(",18U");
					}
					if (coach.getGirls().equals("Yes")){
						coachlist = coachlist.concat(",Girls");
					}
					coach.setCepmodulesselected(coachlist);
					coach.setIsbullying(isbullying);

					tempcoachlist.add(coach);
					coach = null;
				}
				LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();
			    		
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading teams");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setPlayers(templist);
    	setCoaches(tempcoachlist);
    	templist=null;
    	tempcoachlist=null;
		
	}
    
    public List<Coach> getCoaches(){
		return coaches;
	}
	
	public void setCoaches(List<Coach> list){
		coaches = list;
	}
	
    
    public List<Coach> getPlayers(){
		return players;
	}
	
	public void setPlayers(List<Coach> list){
		players = list;
	}

	public List<Player> getPdr(){
		return pdr;
	}

	public void setPdr(List<Player> list){
		pdr = list;
	}

	public List<Player> getBlockrecruitment(){
		return blockrecruitment;
	}

	public void setBlockrecruitment(List<Player> list){
		blockrecruitment = list;
	}


	public void editrosterdetail(Coach splayer){
		String idplayer = splayer.getIdcoach();
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editrosterdetail.xhtml?playerid=" + idplayer + "&teamid=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void editcoachrosterdetail(Coach scoach){
		String idplayer = scoach.getIdcoach();
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editrosterdetail.xhtml?playerid=" + idplayer + "&teamid=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void updateTeamRostereffectivedate(Team team){
		
		//lets first update the entire active roster with the new effective date aka roster date
		//then lets reload the objects
		this.teamid = team.ID;
		this.enteredrosterdate = team.getNewdate();
		
		
		List<Coach> templist = new ArrayList<Coach>();
		List<Coach> tempcoachlist = new ArrayList<Coach>();
		
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	
    	try{
    		//first update team
    		CallableStatement cs = db.prepareCall("CALL scaha.updateTeamrosterdate(?,?)");
			cs.setInt("teamid", this.teamid);
			cs.setString("srosterdate", this.enteredrosterdate);
		    cs.executeQuery();
    		cs.close();
		    
    		//first get team name
    		cs = db.prepareCall("CALL scaha.getTeamByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					this.teamname = rs.getString("teamname");
				}
				LOGGER.info("We have results for team name");
			}
			rs.close();
			db.cleanup();
    		
    		//next get player roster
//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getRosterPlayersByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String playerid = rs.getString("idroster");
					String fname = rs.getString("fname");
					String lname = rs.getString("lname");
					String eligibility = rs.getString("eligibility");
					String loidate = rs.getString("loidate");
					String drop = rs.getString("dropped");
					String added = rs.getString("added");
					String active = rs.getString("active");
					String updated = rs.getString("updated");
					String jerseynumber = rs.getString("jerseynumber");
					String dob = rs.getString("dob");
					String gp = rs.getString("gp");
					
					Coach player = new Coach();
					player.setIdcoach(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);
					player.setEligibility(loidate);
					player.setRosterdate(eligibility);
					player.setDrop(drop);
					player.setAdded(added);
					player.setActive(active);
					player.setUpdated(updated);
					player.setJerseynumber(jerseynumber);
					player.setDob(dob);
					player.setGp(gp);
					
					templist.add(player);
					player = null;
				}
				LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();
    		
			//next get coach roster
			//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getRosterCoachesByTeamId(?)");
			cs.setInt("teamid", this.teamid);
		    rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String coachid = rs.getString("idroster");
					String cfname = rs.getString("fname");
					String clname = rs.getString("lname");
					String celigibility = rs.getString("eligibility");
					String cloidate = rs.getString("loidate");
					String cdrop = rs.getString("dropped");
					String cadded = rs.getString("added");
					String cactive = rs.getString("active");
					String cupdated = rs.getString("updated");
					String teamrole = rs.getString("teamrole");
					
					
					Coach coach = new Coach();
					coach.setIdcoach(coachid);
					coach.setFirstname(cfname);
					coach.setLastname(clname);
					coach.setEligibility(celigibility);
					coach.setLoidate(cloidate);
					coach.setDrop(cdrop);
					coach.setAdded(cadded);
					coach.setActive(cactive);
					coach.setUpdated(cupdated);
					coach.setTeamrole(teamrole);
					
					tempcoachlist.add(coach);
					coach = null;
				}
				LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();
			    		
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading teams");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
		
    	setPlayers(templist);
    	setCoaches(tempcoachlist);
    	templist=null;
    	tempcoachlist=null;
	
	}
	
	public void viewScoresheets(){
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("reviewscoresheetsforateam.xhtml?teamid=" + this.teamid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void generatePDR(){
		List<Player> templist = new ArrayList<Player>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.getTeamPDR(?)");
			cs.setInt("teamid", this.teamid);
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					String playerid = rs.getString("playercount");
					String fname = rs.getString("idclub");
					String lname = rs.getString("clubname");
					Boolean bitalicize = rs.getBoolean("bitalics");

					Player player = new Player();
					player.setDob(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);
					player.setBitalics(bitalicize);

					templist.add(player);
					player = null;
				}
				LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading pdr");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		setPdr(templist);
		templist = null;
	}

	public void generateBlock(){
		List<Player> templist = new ArrayList<Player>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.getTeamBlockRecruitment(?)");

			//next get pdr
//TODO			cs = db.prepareCall("CALL scaha.getRosterByTeamId(?)");
			cs = db.prepareCall("CALL scaha.getTeamBlockRecruitment(?)");
			cs.setInt("teamid", this.teamid);
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					String playerid = rs.getString("playercount");
					String fname = rs.getString("idteams");
					String lname = rs.getString("teamname");

					Player player = new Player();
					player.setDob(playerid);
					player.setFirstname(fname);
					player.setLastname(lname);

					templist.add(player);
					player = null;
				}
				LOGGER.info("We have results for block recruitment");
			}
			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading blockrecruitments");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		setBlockrecruitment(templist);
		templist=null;

	}

	public List<BlockRecruitment> Blockrecruitmentforteam() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		List<BlockRecruitment> tempresult = new ArrayList<>();

		try {
			//first get all teams
			CallableStatement cs = db.prepareCall("CALL scaha.getTeamBlockRecruitmentById(?)");
			cs.setInt("teamid", this.teamid);
			rs = cs.executeQuery();

			if (rs != null) {


				while (rs.next()) {

					BlockRecruitment di = new BlockRecruitment();

					di.setPlayercount(rs.getInt("playercount"));
					di.setTeamname(rs.getString("teamname"));
					di.setYear(rs.getString("year"));

					if (di.getPlayercount() == 0) {
						di.setTeamname("None");
					}

					tempresult.add(di);
					di = null;

				}
				LOGGER.info("We have results for team roster brteamlist");
			}


			LOGGER.info("We have results for team roster");

			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading pdr");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		return tempresult;

	}

	public void updatePDRAAATeams(){
		List<Player> templist = new ArrayList<Player>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.getTeamsNeedingPDRCleanup()");
			CallableStatement cs2 = db.prepareCall("CALL scaha.cleanupteamspdrcounts(?,?)");

			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					Integer personid = rs.getInt("idperson");
					Integer teamid = rs.getInt("idteam");

					cs2.setInt("ipersonid", personid);
					cs2.setInt("iteamid", teamid);

					cs2.executeQuery();

					personid = null;
					teamid = null;
				}
				LOGGER.info("We have results for team roster");
			}
			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN loading pdr");
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		setPdr(templist);

	}

	public void updateCoach(Coach coach){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			if (db.setAutoCommit(false)) {

				//Need to provide info to the stored procedure to save or update
				LOGGER.info("update coach details");
				CallableStatement cs = db.prepareCall("CALL scaha.updateCoachbyCoachIdforlistManageRoster(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
				cs.setInt("coachid", Integer.parseInt(coach.getIdcoach()));
				cs.setString("screenexpires", coach.getScreeningexpires());
				cs.setString("cepnum", coach.getCepnumber());
				cs.setInt("levelcep", Integer.parseInt(coach.getCeplevel()));
				cs.setString("cepexpire", coach.getCepexpires());
				cs.setInt("insafesport",coach.getSafesportforcoachlist());
				cs.setString("inrostertype", coach.getTeamrole());
				cs.setInt("inrosterid", Integer.parseInt(coach.getIdcoach()));
				//need to set values for modules
				Integer u8 = 0;
				Integer u10 = 0;
				Integer u12 = 0;
				Integer u14 = 0;
				Integer u18 = 0;
				Integer ugirls = 0;

				List<String> cepmodulesselectedstring = Arrays.asList(coach.getCepmodulesselected().split(","));
				for (int i = 0; i < cepmodulesselectedstring.size(); i++) {
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("8U")){
						u8 = 1;
					}
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("10U")){
						u10 = 1;
					}
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("12U")){
						u12 = 1;
					}
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("14U")){
						u14 = 1;
					}
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("18U")){
						u18 = 1;
					}
					if (cepmodulesselectedstring.get(i).equalsIgnoreCase("Girls")){
						ugirls = 1;
					}
				}
				cs.setInt("u8", u8);
				cs.setInt("u10", u10);
				cs.setInt("u12", u12);
				cs.setInt("u14", u14);
				cs.setInt("u18", u18);
				cs.setInt("ugirls", ugirls);
				cs.setString("infirstname",coach.getFirstname());
				cs.setString("inlastname",coach.getLastname());
				cs.setString("sportexpires", coach.getSportexpires());
				cs.setInt("issuspend_in", Integer.parseInt(coach.getSuspend()));
				String teamrole = "";
				if (coach.getTeamrole().equals("PL")){
					teamrole="Player";
				}
				if (coach.getTeamrole().equals("AC")){
					teamrole = "Assistant Coach";
				}
				if (coach.getTeamrole().equals("SC")){
					teamrole = "Student Coach";
				}
				if (coach.getTeamrole().equals("HC")){
					teamrole = "Head Coach";
				}
				if (coach.getTeamrole().equals("AM")){
					teamrole = "Assistant Coach/Manager";
				}
				if (coach.getTeamrole().equals("MA")){
					teamrole = "Manager";
				}
				cs.setString("in_rostertype", teamrole);
				cs.setString("in_notes", coach.getNotes());
				cs.setInt("abi",Integer.parseInt(coach.getIsbullying()));
				rs = cs.executeQuery();

				db.commit();
				rs.close();

				db.cleanup();

				//logging
				LOGGER.info("We are updating transfer info for coach id:" + coach);



				this.getRoster();


			} else {

			}

		} catch (SQLException e) {
			/*TODO Auto-generated catch block*/
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedcoach);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

	}

	public void voidLoi(Coach selectedPlayer){

		String sidcoach = selectedPlayer.getIdcoach();
		String coachname = selectedPlayer.getFirstname() + " " + selectedPlayer.getLastname();

		//need to set to void
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			if (db.setAutoCommit(false)) {

				//Need to provide info to the stored procedure to save or update
				LOGGER.info("verify loi code provided");
				CallableStatement cs = db.prepareCall("CALL scaha.setcoachtoVoid(?)");
				cs.setInt("coachid", Integer.parseInt(sidcoach));
				cs.executeQuery();
				db.commit();
				db.cleanup();

				//logging
				LOGGER.info("We are voiding the loi for coach id:" + sidcoach);

				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Successful", "You have voided the loi for: " + coachname));
			} else {

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedcoach);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		this.getRoster();
	}

	public void voidplayerLoi(Coach selectedPlayer){

		String sidplayer = selectedPlayer.getIdcoach();
		String playname = selectedPlayer.getFirstname() + ' ' + selectedPlayer.getLastname();

		//getClubID(sidplayer);
		///need to set to void
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{

			if (db.setAutoCommit(false)) {
				//need to get club id first before setting the loi to void.
				CallableStatement cs = db.prepareCall("CALL scaha.getClubIdbyRosterID(?)");
				/*cs.setInt("rosterid", Integer.parseInt(sidplayer));
				rs = cs.executeQuery();

				if (rs != null) {

					while (rs.next()) {
						this.clubid = rs.getInt("idclub");
					}
				}*/

				//Need to provide info to the stored procedure to save or update
				LOGGER.info("verify loi code provided");
				cs = db.prepareCall("CALL scaha.settoVoid(?)");
				cs.setInt("playerid", Integer.parseInt(sidplayer));
				cs.executeQuery();

				db.commit();
				db.cleanup();

				//logging
				LOGGER.info("We are voiding the loi for player id:" + sidplayer);

				//need to send an email acknowledging the loi is voided.
				/*to = "";
				LOGGER.info("Sending void loi email to club registrar, and family");
				cs = db.prepareCall("CALL scaha.getClubRegistrarEmail(?)");
				cs.setInt("iclubid", this.clubid);
				rs = cs.executeQuery();
				if (rs != null){
					while (rs.next()) {
						if (!to.equals("")){
							to = to + "," + rs.getString("usercode");
						}else {
							to = rs.getString("usercode");
						}
					}
				}
				rs.close();

				cs = db.prepareCall("CALL scaha.getFamilyEmail(?)");
				cs.setInt("iplayerid", Integer.parseInt(sidplayer));
				rs = cs.executeQuery();
				if (rs != null){
					while (rs.next()) {
						to = to + ',' + rs.getString("usercode");
					}
				}
				rs.close();


				//hard my email address for testing purposes
				to = "lahockeyfan2@yahoo.com";
				this.setSubject(this.selectedplayer.getFirstname() + " " + this.selectedplayer.getLastname() + " LOI Void with " + this.getClubname());


				SendMailSSL mail = new SendMailSSL(this);
				LOGGER.info("Finished creating mail object for " + this.selectedplayer.getFirstname() + " " + this.selectedplayer.getLastname() + " LOI Void with " + this.getClubname());
				mail.sendMail();


				FacesContext context = FacesContext.getCurrentInstance();
				context.addMessage(null, new FacesMessage("Successful", "You have voided the loi for:" + playname));

				 */
			} else {

			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN LOI Generation Process" + this.selectedplayer);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		//now we need to reload the data object for the loi list
		getRoster();
	}
}


