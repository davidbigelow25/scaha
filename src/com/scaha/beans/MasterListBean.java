package com.scaha.beans;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import com.gbli.common.ReturnDataResultSet;
import com.gbli.common.ReturnDataRow;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

@ManagedBean
@RequestScoped
public class MasterListBean implements Serializable  {


	private ReturnDataResultSet mydata = null;
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private int idclub = 0;
	private List<ReturnDataRow> filter;
	
	@ManagedProperty(value = "#{profileBean}")
	private ProfileBean pb = null;
	@ManagedProperty(value = "#{scahaBean}")
	private ScahaBean scaha = null;
	
	
	 @PostConstruct
	 public void init() {
		 
		 LOGGER.info(" *************** START: POST INIT FOR MasterListBean *****************");
		 
		 this.setIdclub(pb.getClubID());
		 
		 ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		 db.getData("call scaha.getAllPlayersByClubAndSeason(" + this.getIdclub() + ",'" + scaha.getScahaSeasonList().getCurrentSeason().getTag().trim() + "')");
		 
   		 try {
			setMydata(ReturnDataResultSet.NewReturnDataResultSetFactory(db.getResultSet()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		 
   		db.free();
   		LOGGER.info(" *************** FINISH: POST INIT FOR MasterListBean *****************");
   		
	 }


	/**
	 * @return the mydata
	 */
	public ReturnDataResultSet getMydata() {
		return mydata;
	}


	/**
	 * @param mydata the mydata to set
	 */
	public void setMydata(ReturnDataResultSet mydata) {
		this.mydata = mydata;
	}
	

	/**
	 * @return the filter
	 */
	public List<ReturnDataRow> getFilter() {
		return filter;
	}


	/**
	 * @param filter the filter to set
	 */
	public void setFilter(List<ReturnDataRow> filter) {
		this.filter = filter;
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
	 * @return the idclub
	 */
	public int getIdclub() {
		return idclub;
	}


	/**
	 * @param idclub the idclub to set
	 */
	public void setIdclub(int idclub) {
		this.idclub = idclub;
	}
	
}
