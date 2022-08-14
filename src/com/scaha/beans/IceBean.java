package com.scaha.beans;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import com.gbli.common.ReturnDataResultSet;
import com.gbli.common.ReturnDataRow;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Profile;
import com.scaha.objects.Role;

@ManagedBean
@RequestScoped
public class IceBean implements Serializable  {

	private ReturnDataResultSet mydata = null;
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private List<ReturnDataRow> filter;
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	
	
	 @PostConstruct
	 public void init() {
		 
		 LOGGER.info(" *************** POST INIT FOR IceBean *****************");
		 ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		 Profile pro = pb.getProfile(); // logged in profile id..
		 
		 int idclub = 0;
		 try {
			 PreparedStatement ps = db.prepareStatement("call scaha.getClubforPerson(?)");
			 ps.setInt(1, pro.ID);
			 ResultSet rs = ps.executeQuery();
			 while (rs.next()) {
				idclub = rs.getInt("idclub");
			}
			rs.close();	
			ps.close();
			if (pb.hasRoleList("S-ICE")) {
				ps = db.prepareStatement("call scaha.getSlotReportMaster()");
			} else {
				ps = db.prepareStatement("call scaha.getSlotReportByClub(?)");
				ps.setInt(1,idclub);
			}
			rs = ps.executeQuery();
			setMydata(ReturnDataResultSet.NewReturnDataResultSetFactory(rs));
			rs.close();
			ps.close();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
			 LOGGER.info(" *************** POST INIT FOR IceBean COMPLETED *****************");
		}
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
	
	
}
