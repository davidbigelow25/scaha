package com.scaha.beans;

import java.io.Serializable;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import com.gbli.common.ReturnDataResultSet;
import com.gbli.common.ReturnDataRow;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

@ManagedBean
@RequestScoped
public class MemberDriveBean implements Serializable  {


	private ReturnDataResultSet mydata = null;
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private List<ReturnDataRow> filter;
	
	
	 @PostConstruct
	 public void init() {
		 
		 LOGGER.info(" *************** POST INIT FOR MemberDriveBean *****************");
		 
		 ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
   		 db.getData("call scaha.getMemberSignupsByClub()");
   		 try {
			setMydata(ReturnDataResultSet.NewReturnDataResultSetFactory(db.getResultSet()));
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
   		db.free();
   		
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
	 
	public String getTotRegisters() {
		int i=0;
		for (ReturnDataRow rdr : mydata) {
			i = i + Integer.parseInt(rdr.get(1).toString());
		}
		
		return i + "";
	}

	public String getTotMembers() {
		int i=0;
		for (ReturnDataRow rdr : mydata) {
			i = i + Integer.parseInt(rdr.get(2).toString());
		}
		
		return i + "";
	}
	
	public String getTotPercent() {
		double mem = 0;
		for (ReturnDataRow rdr : mydata) {
			mem = mem + Integer.parseInt(rdr.get(2).toString());
		}
		double reg=0;
		for (ReturnDataRow rdr : mydata) {
			reg = reg + Integer.parseInt(rdr.get(1).toString());
		}
		
		DecimalFormat df = new DecimalFormat("#.##");
		double ans = reg/mem * 100.00; 
		return df.format(ans) + "%";

	}
	
	public Integer getProgressValue() {
		double mem = 0;
		double reg=0;
		for (ReturnDataRow rdr : mydata) {
			mem = mem + Integer.parseInt(rdr.get(2).toString());
			reg = reg + Integer.parseInt(rdr.get(1).toString());
		}
		
		Integer ans = (int) (((reg /mem) * 100)); 
		return ans;
		

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
}
