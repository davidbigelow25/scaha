package com.scaha.beans;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.*;

import javax.annotation.PostConstruct;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
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
public class invoiceTeamBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;


	transient private ResultSet rs = null;
	//lists for generated datamodels
	private int clubId;
	private int year;
	private String clubName;
	private List<TeamInvoice> teams;

	//bean level properties used by multiple methods
	private Integer profileid = 0;

	@PostConstruct
    public void init() {
		/*FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();

		HttpServletRequest hsr = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

		if(hsr.getParameter("clubId") != null)
		{
			this.clubId = Integer.parseInt(hsr.getParameter("clubId").toString());
		}else {
			this.clubId=1;
		}*/


		//Load Default Lists Seasons, Standing, and Games
		//loadTeamInvoices();

	}

    public invoiceTeamBean() {
        
    }

	public void setClubId(int clubId) {
		this.clubId = clubId;
	}

	public int getClubId() {
		return clubId;
	}

	public void loadTeamInvoices() {
		List<TeamInvoice> tempresult = new ArrayList<com.scaha.objects.TeamInvoice>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try {
			//first get summary
			CallableStatement cs = db.prepareCall("CALL scaha.getteaminvoicesummaryforclub(?)");
			cs.setInt(1, this.clubId);
			rs = cs.executeQuery();

			if (rs != null) {

				while (rs.next()) {
					TeamInvoice dto = new TeamInvoice();
					dto.setTeamId(rs.getInt("idteams"));
					dto.setTeamName(rs.getString("teamname"));
					dto.setPlayerCount(rs.getInt("player_count"));
					dto.setTotalAmount(rs.getDouble("total_amount"));
					tempresult.add(dto);

				}
				LOGGER.info("We have results for team invoice summary");

			}
				rs.close();
				db.cleanup();

		} catch(SQLException e){
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN getting team invoice summary");
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally{
			//
			// always clean up after yourself..
			//
			db.free();
		}



		this.teams = tempresult;
		tempresult = null;

	}

	public double getGrandTotal() {
		return teams.stream()
				.mapToDouble(TeamInvoice::getTotalAmount)
				.sum();
	}

	// Getters & Setters
	public List<TeamInvoice> getTeams() {
		return teams;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String viewTeams(int clubId) {
		return "invoiceteams.xhtml?faces-redirect=true&clubId=" + clubId + "&year=" + year;
	}

	public void viewPlayers(int teamId) {
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("invoiceplayers.xhtml?clubId=" + clubId + "&teamId=" + teamId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		/*return "invoicePlayers.xhtml?faces-redirect=true"
				+ "&teamId=" + teamId
				+ "&year=" + year;*/
	}
}

