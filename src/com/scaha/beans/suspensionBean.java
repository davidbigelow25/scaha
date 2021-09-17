package com.scaha.beans;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.internet.InternetAddress;

import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.gbli.context.PenaltyPusher;
import com.scaha.objects.*;

import static java.lang.Integer.parseInt;

//import com.gbli.common.SendMailSSL;
@ManagedBean
@ViewScoped
public class suspensionBean implements Serializable {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private static String mail_penaltypush_body = Utils.getMailTemplateFromFile("/mail/penaltypush.html");

	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;

	@ManagedProperty(value="#{scahaBean}")
	private ScahaBean sb;

	@ManagedProperty(value="#{scoreboardBean}")
	private ScoreboardBean sbb;

	private List<Suspension> suspensions = null;
	private List<Suspension> allsuspensions = null;
	private ResultDataModel listofplayers = null;
	transient private ResultSet rs = null;
	private Result selectedplayer = null;
	private String numberofgames = null;
	private String infraction = null;
	private String eligibility = null;
	private String suspdate = null;
	private Integer served = null;
	private Integer match = null;
	private String playername = null;
	private String team = null;
	private String teamid = null;

	private Integer penaltyid = null;
	private LiveGame selectedlivegame = null;
	private Integer rosterid = null;
	private Boolean displayselectedlivegame = null;
	private Integer livegameid = null;
	private String selectedgamesource = null;
	private Boolean isscahagamesource = null;
	private Boolean istournamentgamesource = null;
	private Boolean isexhibitiongamesource = null;
	private List<TournamentGame> tournamentgames = null;
	private TournamentGameDataModel TournamentGameDataModel = null;
	private List<ExhibitionGame> exhibitiongames = null;
	private ExhibitionGameDataModel ExhibitionGameDataModel = null;
	private TournamentGame selectedtournamentgame = null;




	@PostConstruct
    public void init() {
		
        loadSuspensions();
        
        loadAllSuspensions();

        this.selectedgamesource="S";
        this.displayGameSource();
	}
	
    public suspensionBean() {  
        
    }

	public TournamentGame getSelectedtournamentgame(){
		return selectedtournamentgame;
	}

	public void setSelectedtournamentgame(TournamentGame odatamodel){
		selectedtournamentgame = odatamodel;
	}

	public ExhibitionGameDataModel getExhibitiongamedatamodel(){
		return ExhibitionGameDataModel;
	}

	public void setExhibitiongamedatamodel(ExhibitionGameDataModel odatamodel){
		ExhibitionGameDataModel = odatamodel;
	}

	public List<ExhibitionGame> getExhibitiongames(){
		return exhibitiongames;
	}

	public void setExhibitiongames(List<ExhibitionGame> tgames){
		exhibitiongames = tgames;
	}

	public TournamentGameDataModel getTournamentgamedatamodel(){
		return TournamentGameDataModel;
	}

	public void setTournamentgamedatamodel(TournamentGameDataModel odatamodel){
		TournamentGameDataModel = odatamodel;
	}

	public Boolean getIsscahagamesource(){
		return isscahagamesource;
	}

	public void setIsscahagamesource(Boolean value){
		isscahagamesource = value;
	}

	public Boolean getIstournamentgamesource(){
		return istournamentgamesource;
	}

	public void setIstournamentgamesource(Boolean value){
		istournamentgamesource = value;
	}

	public Boolean getIsexhibitiongamesource(){
		return isexhibitiongamesource;
	}

	public void setIsexhibitiongamesource(Boolean value){
		isexhibitiongamesource = value;
	}


	public ScoreboardBean getSbb() {
		return sbb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setSbb(ScoreboardBean pb) {
		this.sbb = pb;
	}

	public ScahaBean getSb() {
		return sb;
	}

	/**
	 * @param pb the pb to set
	 */
	public void setSb(ScahaBean pb) {
		this.sb = pb;
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

	public Boolean getDisplayselectedlivegame(){
		return displayselectedlivegame;
	}

	public void setDisplayselectedlivegame(Boolean value){
		displayselectedlivegame = value;
	}


	public LiveGame getSelectedlivegame(){
		return selectedlivegame;
	}

	public void setSelectedlivegame(LiveGame value){
		selectedlivegame = value;
	}


	public String getSelectedgamesource(){
		return selectedgamesource;
	}

	public void setSelectedgamesource(String value){
		selectedgamesource = value;
	}


	public String getEligibility(){
    	return eligibility;
    }
    
    public void setEligibility(String value){
    	eligibility = value;
    }
    
    
    public String getPlayername(){
    	return playername;
    }
    
    public void setPlayername(String value){
    	playername = value;
    }

	public Integer getPenaltyid(){
		return penaltyid;
	}

	public void setPenaltyid(Integer value){
		penaltyid=value;
	}


	public String getTeam(){
    	return team;
    }
    
    public void setTeam(String value){
    	team=value;
    }

	public String getTeamid(){
		return teamid;
	}

	public void setTeamid(String value){
		teamid = value;
	}


	public Integer getMatch(){
    	return match;
    }
    
    public void setMatch(Integer value){
    	match = value;
    }
    
    public Integer getServed(){
    	return served;
    }
    
    public void setServed(Integer value){
    	served = value;
    }
    
    
    public String getSuspdate(){
    	return suspdate;
    }
    
    public void setSuspdate(String value){
    	suspdate = value;
    }
    
    
    public String getInfraction(){
    	return infraction;
    }
    
    public void setInfraction(String value){
    	infraction = value;
    }
    
    public String getNumberofgames(){
    	return numberofgames;
    }
    
    public void setNumberofgames(String value){
    	numberofgames = value;
    }
    
    
    public Result getSelectedplayer(){
		return selectedplayer;
	}
	
	public void setSelectedplayer(Result selectedPlayer){
		selectedplayer = selectedPlayer;
	}
	
    public List<Suspension> getSuspensions(){
    	return suspensions;
    }
    
    public void setSuspensions(List<Suspension> list){
    	suspensions=list;
    }
    
    public List<Suspension> getAllSuspensions(){
    	return allsuspensions;
    }
    
    public void setAllSuspensions(List<Suspension> list){
    	allsuspensions=list;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    
	public void loadSuspensions() {  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Suspension> tempresult = new ArrayList<Suspension>();
		
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getSuspensions()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer idsuspension = rs.getInt("idsuspensions");
					String playername = rs.getString("playername");
					String team = rs.getString("team");
					String infraction = rs.getString("infraction");
					String nogames = rs.getString("numberofgames");
					String eligibility = rs.getString("eligibility");
					String match = rs.getString("matchpenalty");
					String suspensiondate = rs.getString("suspensiondate");
					
					Suspension susp = new Suspension();
					susp.setIdsuspension(idsuspension);
					susp.setPlayername(playername);
					susp.setTeam(team);
					susp.setInfraction(infraction);
					susp.setGames(nogames);
					susp.setEligibility(eligibility);
					susp.setMatch(match);
					susp.setSuspensiondate(suspensiondate);
					
					tempresult.add(susp);
    					}
				//LOGGER.info("We have results for suspension list");
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting suspension list");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	this.setSuspensions(tempresult);
		
    }
	
	
	public void loadAllSuspensions() {  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Suspension> tempresult = new ArrayList<Suspension>();
		
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getAllSuspensions()");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					Integer idsuspension = rs.getInt("idsuspensions");
					String playername = rs.getString("playername");
					String team = rs.getString("team");
					String infraction = rs.getString("infraction");
					String nogames = rs.getString("numberofgames");
					String eligibility = rs.getString("eligibility");
					String match = rs.getString("matchpenalty");
					if (match=="Yes"){
						nogames = "--";
					}
					String suspensiondate = rs.getString("suspensiondate");
					String served = rs.getString("served");
					
					Suspension susp = new Suspension();
					susp.setIdsuspension(idsuspension);
					susp.setPlayername(playername);
					susp.setTeam(team);
					susp.setInfraction(infraction);
					susp.setGames(nogames);
					susp.setEligibility(eligibility);
					susp.setMatch(match);
					susp.setSuspensiondate(suspensiondate);
					susp.setServed(served);
					
					tempresult.add(susp);
    					}
				//LOGGER.info("We have results for suspension list");
			}
			
			
			rs.close();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting suspension list");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	this.setAllSuspensions(tempresult);
		
    }
	
	public void markServed(Suspension suspension){
		Integer suspensionid = suspension.getIdsuspension();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
    	try{
    		//first lets get all of the suspension info
			CallableStatement cs = db.prepareCall("CALL scaha.getSuspension(?)");
			cs.setInt("suspensionid", suspension.getIdsuspension());
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					Integer idsuspension = rs.getInt("idsuspensions");
					String splayername = rs.getString("playername");
					String steam = rs.getString("team");
					String sinfraction = rs.getString("infraction");
					String snogames = rs.getString("numberofgames");
					String eligibility = "";
					Integer imatch = rs.getInt("matchpenalty");
					Integer iserved = 1;
					String ssuspensiondate = rs.getString("suspensiondate");
					Integer livegameid = rs.getInt("idlivegame");
					Integer idteam = rs.getInt("idteam");

					this.playername=splayername;
					this.team=steam;
					this.infraction=sinfraction;
					this.numberofgames=snogames;
					this.match=imatch;
					this.served=iserved;
					this.suspdate=ssuspensiondate;
					this.eligibility=eligibility;
					this.teamid=idteam.toString();

					//this.selectedlivegame = sb.getScahaLiveGameList().getRowData(livegameid.toString());

					for (LiveGame item : sb.getScahaLiveGameList()) {
						if (item.getIdgame()==livegameid){
							this.selectedlivegame=item;
							break;
						}
					}



				}
				//LOGGER.info("We have results for suspension id:" + this.suspensionid.toString());
			}


			rs.close();


			//then lets get the email all setup
			//now lets create a penalty object and push email from the object.
			String temppenaltyrows =  "<tr><td>&nbsp;" + this.playername +"&nbsp;</td><td>&nbsp;";
			temppenaltyrows = temppenaltyrows + this.numberofgames +" games&nbsp;</td><td>&nbsp;";
			if (this.match.equals(1)){
				temppenaltyrows = temppenaltyrows + "Yes&nbsp;</td><td>&nbsp;Infraction(s): ";
			}else {
				temppenaltyrows = temppenaltyrows + "NO&nbsp;</td><td>&nbsp;Infraction(s): ";
			}
			temppenaltyrows = temppenaltyrows + this.infraction + "&nbsp;</td></tr>";

			ScahaTeam team = sb.getScahaTeamList().getRowData(this.teamid);

			Penalty pen = new Penalty(suspension.getIdsuspension(),pb.getProfile(), this.selectedlivegame, team);
			PenaltyPusher pp = new PenaltyPusher();
			pp.setPenalty(pen);
			pp.setPenaltyrows(temppenaltyrows);
			pp.setServedrows(this.eligibility);
			pp.setIsServed(true);
			pp.setServedrows(" ");
			pp.setLivegame(this.selectedlivegame);


    		//then lets update the suspension as served
    		cs = db.prepareCall("CALL scaha.setSuspensionServed(?)");
			cs.setInt("suspensionid", suspensionid);
    		cs.executeQuery();

    		//then lets send the email
			pp.pushPenalty();
			
			//LOGGER.info("set suspension as served for:" + suspensionid.toString());

			cs.close();
			db.commit();
			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN settings suspension");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	loadAllSuspensions();
		
	}
	
	public void Addsuspension(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		//lets check if we have a scaha game or a exhibition game
		if (this.selectedlivegame==null){
			LiveGame live = new LiveGame(1,pb.getProfile());
			Schedule sched = new Schedule(1);
			sched.setDescription("schedule");
			live.setStartdate(this.suspdate);
			live.setHometeamname(this.selectedplayer.getAddress());
			live.setAwayteamname(this.selectedtournamentgame.getOpponent());
			live.setIdgame(0);
			live.setSched(sched);

			this.selectedlivegame = live;
		}

    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.addSuspension(?,?,?,?,?,?,?,?,?)");
			cs.setInt("inpersonid", parseInt(this.selectedplayer.getIdplayer()));
			if (this.match==1){
				cs.setString("numgames", "0");
			} else {
				cs.setString("numgames", this.numberofgames);
			}
			cs.setString("ininfraction", this.infraction);
			cs.setInt("ismatch", this.match);
			cs.setInt("served", this.served);
			cs.setString("susdate", this.suspdate);
			cs.setString("elgibility", this.eligibility);
			cs.setInt("inrosterid", this.rosterid);
			cs.setInt("inlivegameid", this.selectedlivegame.getGameId());
			cs.executeQuery();
			
			//LOGGER.info("set suspension for:" + this.selectedplayer.getIdplayer().toString());
						
			db.commit();
			db.cleanup();

			//now lets create a penalty object and push email from the object.
			String temppenaltyrows =  "<tr><td>&nbsp;" + this.playername +"&nbsp;</td><td>&nbsp;";
			temppenaltyrows = temppenaltyrows + this.numberofgames +" games&nbsp;</td><td>&nbsp;";
			if (this.match.equals(1)){
				temppenaltyrows = temppenaltyrows + "Yes&nbsp;</td><td>&nbsp;Infraction(s): ";
			}else {
				temppenaltyrows = temppenaltyrows + "NO&nbsp;</td><td>&nbsp;Infraction(s): ";
			}
			temppenaltyrows = temppenaltyrows + this.infraction + "&nbsp;</td></tr>";

			ScahaTeam team = sb.getScahaTeamList().getRowData(this.teamid);

			Penalty pen = new Penalty(this.penaltyid,pb.getProfile(), this.selectedlivegame, team);

			if (!this.isscahagamesource){
				pen.setTeamname(team.getTeamname());
			}
			PenaltyPusher pp = new PenaltyPusher();
			pp.setPenalty(pen);
			pp.setPenaltyrows(temppenaltyrows);
			pp.setServedrows(this.eligibility);
			pp.setLivegame(this.selectedlivegame);
			pp.setIsServed(false);
			pp.setIsRemoved(false);
			pp.setEmailsubject(this.playername + " Suspension Details");
			pp.pushPenalty();


		} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN settings suspension");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	loadAllSuspensions();
		this.setSelectedplayer(null);
		this.setInfraction("");
		this.setMatch(0);
		this.setServed(0);
		this.setSuspdate("");
		this.setNumberofgames("");
		this.setEligibility("");
		this.rosterid=0;
		FacesContext context = FacesContext.getCurrentInstance();
    	Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{playerhistoryBean}", Object.class );

		playerhistoryBean pb = (playerhistoryBean) expression.getValue( context.getELContext() );
		pb.setSearchcriteria("");
		closePage();
	}
	
	public void manageSuspension(Suspension suspension){
		String suspensionid = suspension.getIdsuspension().toString();
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("editsuspensions.xhtml?suspensionid=" + suspensionid);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addSuspension(){
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("addsuspensions.xhtml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void LoadPerson(Result result){
		this.selectedplayer= result;
		this.playername = result.getPlayername();
		this.team = result.getAddress();
		this.rosterid = result.getIdroster();
		if (isscahagamesource) {
			this.suspdate = this.selectedlivegame.getStartdate();

			//need to get the number of games
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

			try {
				//lets get number of games, infraction, teamid and penalty id
				CallableStatement cs = db.prepareCall("CALL scaha.getpenaltygamecount(?,?)");
				cs.setInt("inrosterid", result.getIdroster());
				cs.setInt("ingameid", this.selectedlivegame.getIdgame());
				//cs.setInt("ingameid", 9999999);
				rs = cs.executeQuery();

				this.numberofgames = "--";
				this.match = 0;
				this.infraction = "";
				this.teamid = "0";
				this.penaltyid = 0;

				if (rs != null) {

					while (rs.next()) {
						this.numberofgames = rs.getString("numberofgamescount");
						this.match = rs.getInt("matchcount");
						this.infraction = rs.getString("infractions");
						this.teamid = rs.getString("teamid");
						this.penaltyid = rs.getInt("penaltyid");
						//this.eligibility = rs.getString("eligibility");
					}
					//LOGGER.info("We have results for suspension list");
				}

				cs = db.prepareCall("CALL scaha.getpenaltyeligibility(?,?)");
				cs.setInt("inlivegameid", this.selectedlivegame.getIdgame());
				if (this.teamid == null) {
					this.teamid = "0";
				}
				cs.setInt("inteamid", parseInt(this.teamid));

				rs = cs.executeQuery();

				Integer i = 0;
				String newDate = "";
				if (this.match > 0) {
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					Calendar c = Calendar.getInstance();
					try {
						//Setting the date to the given date
						c.setTime(sdf.parse(this.suspdate));
					} catch (ParseException e) {
						e.printStackTrace();
					}

					//Number of Days to add
					c.add(Calendar.DAY_OF_MONTH, 30);
					//Date after adding the days to the given date
					newDate = sdf.format(c.getTime());
					this.eligibility = "Can not participate in any USA hockey event until " + newDate + " pending a hearing";
				} else {
					this.eligibility = "Must be served ";
				}
				if (rs != null) {

					while (rs.next()) {
						i++;
						if (this.match > 0) {
							if (rs.getInt("numdays") > 30) {
								if (rs.getString("eventtype").equals("Tournament")) {
									this.eligibility = this.eligibility + ", GM must be served in the appropriate game(s) in the " + rs.getString("opponent");
									this.eligibility = this.eligibility + " starting on " + rs.getString("eventdate");
								} else {
									this.eligibility = this.eligibility + ", GM must be served on " + rs.getString("eventdate");
									this.eligibility = this.eligibility + " vs " + rs.getString("opponent");
								}
							}
						} else {
							if (i <= parseInt(this.numberofgames)) {
								if (i > 1) {
									this.eligibility = this.eligibility + ", and ";
								}

								if (rs.getString("eventtype").equals("Tournament")) {
									this.eligibility = this.eligibility + " in the appropriate game(s) in the " + rs.getString("opponent");
									this.eligibility = this.eligibility + " starting on " + rs.getString("eventdate");
								} else {
									this.eligibility = this.eligibility + "on " + rs.getString("eventdate");
									this.eligibility = this.eligibility + " vs " + rs.getString("opponent");
								}
							}

						}

					}
					//LOGGER.info("We have results for suspension list");
					if (this.match > 0) {
						this.numberofgames = "0";
					}
				}

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.info("ERROR IN settings suspension");
				e.printStackTrace();
				db.rollback();
			} finally {
				//
				// always clean up after yourself..
				//
				db.free();
			}
		} else{
			//need to get the teamid for the player selected in a non scaha game.
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

			try {
				CallableStatement cs = db.prepareCall("CALL scaha.getTeamIdbyRosterId(?)");
				cs.setInt("inrosterid", this.selectedplayer.getIdroster());

				rs = cs.executeQuery();

				this.numberofgames = "--";
				this.match = 0;
				this.infraction = "";
				this.teamid = "0";
				this.penaltyid = 0;

				if (rs != null) {

					while (rs.next()) {
						this.teamid = rs.getString("idteam");
					}
					//LOGGER.info("We have results for suspension list");
				}


			} catch (SQLException e) {
				// TODO Auto-generated catch block
				LOGGER.info("ERROR IN settings suspension");
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

	public void SelectLiveGame(LiveGame tempgame){
		this.selectedlivegame = tempgame;
		this.displayselectedlivegame = true;
		this.playerSearchForLiveGame(tempgame.getGameId());
	}

	public void DeSelectLiveGame(LiveGame tempgame){
		this.selectedlivegame = null;
		this.displayselectedlivegame = false;

	}

	public void playerSearchForLiveGame(Integer livegameid){

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Result> tempresult = new ArrayList<Result>();

		try{

			Vector<Integer> v = new Vector<Integer>();
			v.add(livegameid);
			db.getData("CALL scaha.playersearchlivegame(?)", v);
			ResultSet rs = db.getResultSet();

			while (rs.next()) {
				Integer idroster = rs.getInt("idroster");
				String idperson = rs.getString("idperson");
				String playername = rs.getString("playername");
				String teamname = rs.getString("teamname");
				String dob = rs.getString("dob");

				Result result = new Result(playername,idperson,teamname,dob);
				result.setIdroster(idroster);
				tempresult.add(result);
			}

			//LOGGER.info("We have results for search criteria " + this.searchcriteria);
			rs.close();
			db.cleanup();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN Searching FOR " + livegameid);
			e.printStackTrace();
			db.rollback();
		} finally {
			db.free();
		}

		listofplayers = new ResultDataModel(tempresult);
	}

	/**
	 * @return the listofplayers
	 */
	public ResultDataModel getListofplayers() {
		return listofplayers;
	}

	/**
	 * @param listofplayers the listofplayers to set
	 */
	public void setListofplayers(ResultDataModel listofplayers) {
		this.listofplayers = listofplayers;
	}

	public void displayGameSource(){
		if (this.selectedgamesource.equals("S")){
			isscahagamesource = true;
			istournamentgamesource = false;
			isexhibitiongamesource = false;
		}else if (this.selectedgamesource.equals("T")){
			isscahagamesource = false;
			istournamentgamesource = true;
			isexhibitiongamesource = false;
		}
	}

	public void loadGames(){

	}

	public void onPartChange(){
		Integer tempteamid = null;

		tempteamid = sbb.getSelectedpartid();
		this.teamid= tempteamid.toString();

		getTournamentGames(tempteamid);
	}

	public void getTournamentGames(Integer teamid) {
		List<TournamentGame> templist = new ArrayList<TournamentGame>();
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try{
			//first get team name
			CallableStatement cs = db.prepareCall("CALL scaha.getTournamentGamesForTeam(?)");
			cs.setInt("teamid", teamid);
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					String idnonscahagame = rs.getString("idnonscahagames");
					String gametype = rs.getString("gametype");
					String gamedate = rs.getString("gamedate");
					String gametime = rs.getString("gametime");
					String opponent = rs.getString("opponent");
					String location = rs.getString("location");
					String status = rs.getString("status");
					Boolean rendered = rs.getBoolean("rendered");

					TournamentGame tournament = new TournamentGame();
					tournament.setIdgame(parseInt(idnonscahagame));
					tournament.setGametype(gametype);
					tournament.setDate(gamedate);
					tournament.setTime(gametime);
					tournament.setOpponent(opponent);
					tournament.setLocation(location);
					tournament.setStatus(status);
					tournament.setRendered(rendered);
					templist.add(tournament);
				}
				//LOGGER.info("We have results for tourney list by team:" + this.teamid);
			}


			rs.close();
			db.cleanup();

			//LOGGER.info("manager has added tournament:" + this.tournamentname);
			//need to add email sent to scaha statistician and manager


		} catch (SQLException e) {
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN getting tournament list for team" + this.teamid);
			e.printStackTrace();
			db.rollback();
		} finally {
			//
			// always clean up after yourself..
			//
			db.free();
		}

		setTournamentgames(templist);
		TournamentGameDataModel = new TournamentGameDataModel(tournamentgames);
	}

	public List<TournamentGame> getTournamentgames(){
		return tournamentgames;
	}

	public void setTournamentgames(List<TournamentGame> tgames){
		tournamentgames = tgames;
	}

	public void playerSearchForTournamentGame(TournamentGame tgame){

		this.selectedtournamentgame = tgame;
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Result> tempresult = new ArrayList<Result>();
		Integer teamId = this.sbb.getSelectedpartid();


		this.suspdate = tgame.getDate();

		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
		Date date = null;
		String convertedDate = null;
		try {
			date = dateFormat.parse(this.suspdate);
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			this.suspdate = simpleDateFormat.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}



		try{
			//next get player roster
			CallableStatement cs = db.prepareCall("CALL scaha.getRosterPlayersForManagerByTeamID(?)");
			cs.setInt("teamid", teamId);
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					Integer idroster = rs.getInt("idroster");
					String idperson = rs.getString("idperson");
					String playername = rs.getString("fname") + ' ' + rs.getString("lname");
					String teamname = rs.getString("teamname");
					String dob = rs.getString("dob");

					Result result = new Result(playername,idperson,teamname,dob);
					result.setIdroster(idroster);
					tempresult.add(result);

				}
				//LOGGER.info("We have results for team roster");
			}
			rs.close();

			cs = db.prepareCall("CALL scaha.getRosterCoachesForManagerByTeamID(?)");
			cs.setInt("teamid", teamId);
			rs = cs.executeQuery();

			if (rs != null){

				while (rs.next()) {
					Integer idroster = rs.getInt("idroster");
					String idperson = rs.getString("idperson");
					String playername = rs.getString("fname") + ' ' + rs.getString("lname");
					String teamname = rs.getString("teamname");
					String dob = rs.getString("dob");

					Result result = new Result(playername,idperson,teamname,dob);
					result.setIdroster(idroster);
					tempresult.add(result);

				}
				//LOGGER.info("We have results for team roster");
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

		listofplayers = new ResultDataModel(tempresult);
	}

	public void navigatetoaddgame(){
		FacesContext context = FacesContext.getCurrentInstance();

		try{
			context.getExternalContext().redirect("addgame.xhtml?id="+this.teamid+"&from=suspension");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}

