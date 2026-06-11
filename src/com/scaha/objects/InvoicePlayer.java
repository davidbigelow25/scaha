package com.scaha.objects;

import com.gbli.context.ContextManager;

import java.io.Serializable;
import java.util.logging.Logger;

public class InvoicePlayer extends ScahaObject implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private String firstName;
	private String lastName;
	private double invoiceFee;
	private String addedDate;
	private boolean multipleTeams;
	private String otherTeams;

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public double getInvoiceFee() {
		return invoiceFee;
	}

	public void setInvoiceFee(double invoiceFee) {
		this.invoiceFee = invoiceFee;
	}

	public String getAddedDate() {
		return addedDate;
	}

	public void setAddedDate(String addedDate) {
		this.addedDate = addedDate;
	}

	public boolean isMultipleTeams() {
		return multipleTeams;
	}

	public void setMultipleTeams(boolean multipleTeams) {
		this.multipleTeams = multipleTeams;
	}

	public String getOtherTeams() {
		return otherTeams;
	}

	public void setOtherTeams(String otherTeams) {
		this.otherTeams = otherTeams;
	}

	public InvoicePlayer(){
		
	}
}

	
	

