package com.scaha.beans;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
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
public class invoicePlayerBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;


	transient private ResultSet rs = null;
	//lists for generated datamodels
	private int clubId;
	private int teamId;
	private String teamName;
	private int year;
	private String clubName;
	private List<InvoicePlayer> players;

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

    public invoicePlayerBean() {
        
    }

	public void setClubId(int clubId) {
		this.clubId = clubId;
	}

	public int getClubId() {
		return clubId;
	}

	public int getTeamId() {
		return teamId;
	}

	public void setTeamId(int teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void loadPlayers() {
		List<InvoicePlayer> tempresult = new ArrayList<com.scaha.objects.InvoicePlayer>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try {
			//first get summary
			CallableStatement cs = db.prepareCall("CALL scaha.invoiceplayersperteam(?)");
			cs.setInt(1, this.teamId);
			rs = cs.executeQuery();

			if (rs != null) {

				while (rs.next()) {
					InvoicePlayer dto = new InvoicePlayer();
					dto.setFirstName(rs.getString("fname"));
					dto.setLastName(rs.getString("lname"));
					dto.setInvoiceFee(rs.getDouble("invoice_fee"));
					dto.setAddedDate(rs.getString("added"));
					dto.setMultipleTeams(rs.getBoolean("multiple_teams"));
					dto.setOtherTeams(rs.getString("other_teams"));
					tempresult.add(dto);

				}
				LOGGER.info("We have results for player invoice details");

			}
				rs.close();
				db.cleanup();

		} catch(SQLException e){
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN getting list of players invoice summary");
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally{
			//
			// always clean up after yourself..
			//
			db.free();
		}



		this.players = tempresult;
		tempresult = null;

	}

	public double getGrandTotal() {
		return players.stream()
				.mapToDouble(InvoicePlayer::getInvoiceFee)
				.sum();
	}

	// Getters & Setters
	public List<InvoicePlayer> getPlayers() {
		return this.players;
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

	public String viewPlayers(int teamId) {
		return "invoicePlayers.xhtml?faces-redirect=true"
				+ "&teamId=" + teamId
				+ "&year=" + year;
	}
}

