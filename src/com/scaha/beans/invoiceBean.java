package com.scaha.beans;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class invoiceBean implements Serializable{

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;


	transient private ResultSet rs = null;
	//lists for generated datamodels
	private List<Invoice> invoices;
	private int year = 2026;

	//bean level properties used by multiple methods
	private Integer profileid = 0;

	@PostConstruct
    public void init() {

        //Load Default Lists Seasons, Standing, and Games
		loadInvoices();

	}

    public invoiceBean() {
        
    }

	public void loadInvoices() {
		List<Invoice> tempresult = new ArrayList<com.scaha.objects.Invoice>();

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");

		try {
			//first get summary
			CallableStatement cs = db.prepareCall("CALL scaha.getclubinvoicesummary()");
			rs = cs.executeQuery();

			if (rs != null) {

				while (rs.next()) {
					Invoice dto = new Invoice();
					dto.setClubId(rs.getInt("idclubs"));
					dto.setClubName(rs.getString("club_name"));
					dto.setPlayerCount(rs.getInt("player_count"));
					dto.setPricePerPlayer(rs.getDouble("invoiceperplayer"));
					dto.setTotalAmount(rs.getDouble("total_amount"));
					tempresult.add(dto);

				}
				LOGGER.info("We have results for invoice summary");

			}
				rs.close();
				db.cleanup();

		} catch(SQLException e){
			// TODO Auto-generated catch block
			LOGGER.info("ERROR IN getting invoice summary");
			e.printStackTrace();
			db.rollback();
			db.free();
		} finally{
			//
			// always clean up after yourself..
			//
			db.free();
		}



		this.invoices = tempresult;
	}

	public double getGrandTotal() {
		return invoices.stream()
				.mapToDouble(Invoice::getTotalAmount)
				.sum();
	}

	// Getters & Setters
	public List<Invoice> getInvoices() {
		return invoices;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void viewTeams(int clubId) {
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("invoiceteams.xhtml?clubId=" + clubId);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

