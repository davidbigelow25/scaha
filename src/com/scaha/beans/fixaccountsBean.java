package com.scaha.beans;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Account;
import com.scaha.objects.AccountDataModel;

import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

//import com.gbli.common.SendMailSSL;


public class fixaccountsBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private List<Account> accounts = null;
    private AccountDataModel AccountDataModel = null;
    private Account selectedaccount = null;
	private String selectedpersonid = null;


    public fixaccountsBean() {
        accounts = new ArrayList<Account>();
        AccountDataModel = new AccountDataModel(accounts);
        
        AccountsDisplay();
    }

	public List<Account> getAccounts() {
		return accounts;
	}

	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}

	public AccountDataModel getAccountDataModel() {
		return AccountDataModel;
	}

	public void setAccountDataModel(AccountDataModel accountDataModel) {
		AccountDataModel = accountDataModel;
	}

	public Account getSelectedaccount() {
		return selectedaccount;
	}

	public void setSelectedaccount(Account selectedaccount) {
		this.selectedaccount = selectedaccount;
	}

	public String getSelectedpersonid() {
		return selectedpersonid;
	}

	public void setSelectedpersonid(String selectedpersonid) {
		this.selectedpersonid = selectedpersonid;
	}

	//retrieves list of players
    public void AccountsDisplay(){
		LOGGER.info("instantiating db");

		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Account> tempresult = new ArrayList<Account>();

    	try{

    		if (db.setAutoCommit(false)) {
				LOGGER.info("instantiating cs");

				CallableStatement cs = db.prepareCall("CALL scaha.getaccountstofix()");
				LOGGER.info("about to execute cs query");

				rs = cs.executeQuery();
				LOGGER.info("executing cs query getaccountstofix");


				if (rs != null){
					LOGGER.info("itertaing through records");

					while (rs.next()) {
						LOGGER.info("next record");

						Integer idperson = rs.getInt("idperson");
        				String fname = rs.getString("fname");
        				String lname = rs.getString("lname");
        				String dob = rs.getString("dob");
        				String usahockeynumber = rs.getString("usahockeynumber");
						Integer comparepersonid = rs.getInt("compareidperson");
						String comparefname = rs.getString("comparefname");
						String comparelname = rs.getString("comparelname");
						String comparedob = rs.getString("comparedob");

						LOGGER.info("adding data to account object");


						Account orelease = new Account();
        				orelease.setPersonid(idperson);
        				orelease.setFname(fname);
        				orelease.setLname(lname);
        				orelease.setDob(dob);
        				orelease.setUsahockeynumber(usahockeynumber);
						orelease.setComparepersonid(comparepersonid);
						orelease.setFnamecompare(comparefname);
						orelease.setLnamecompare(comparelname);
						orelease.setDobcompare(comparedob);

						tempresult.add(orelease);
        				orelease = null;
    				}
    				
    				//LOGGER.info("We have results for release list");
    				
    			}

				LOGGER.info("about to close db");

				db.cleanup();

				LOGGER.info("closed db");

			} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR accounts");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}

		LOGGER.info("adding records to accounts object");

		setAccounts(tempresult);
    	tempresult=null;
    	AccountDataModel = new AccountDataModel(accounts);
    }

    public AccountDataModel getAccountdatamodel(){
    	return AccountDataModel;
    }
    
    public void setAccountdatamodel(AccountDataModel odatamodel){
    	AccountDataModel = odatamodel;
    }
    
    public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
		}
    	
    }
    
    	
	/*public void viewrelease(Release selectedRelease){
	
		String sidrelease = selectedRelease.getIdrelease();
		
		FacesContext context = FacesContext.getCurrentInstance();
		try{
			context.getExternalContext().redirect("reviewrelease.xhtml?releaseid=" + sidrelease);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}*/
	
}

