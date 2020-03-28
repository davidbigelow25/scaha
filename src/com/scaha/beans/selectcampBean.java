
package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.Division;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.SkillLevel;
import com.scaha.objects.Team;

@ManagedBean
@ViewScoped
public class selectcampBean implements Serializable  {


	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//parameter is used for the manager tool page for displaying the body content
	private String body = null;
	
	 @PostConstruct
	 public void init() {
		 
		//need to load the page content		
		LoadBody();
	 }
	 
	 public String getBody(){
		 return body;
	 }
	 
	 public void setBody(String value){
		 body=value;
	 }

	 	public void closePage(){
    	FacesContext context = FacesContext.getCurrentInstance();
    	try{
    		context.getExternalContext().redirect("Welcome.xhtml");
    	} catch (Exception e){
			e.printStackTrace();
    	
    	}
	}
	
	
	public void LoadBody(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
  		 List<Player> templist = new ArrayList<Player>();
		 
  		 try {
  			CallableStatement cs = db.prepareCall("CALL scaha.getSelectcampBody()");
			ResultSet rs = cs.executeQuery();
   			
			while (rs.next()) {
				this.setBody(rs.getString("body"));
			}
			
			rs.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		db.free();
  		db.cleanup();
  		
  	}
	
	public void updateSelectcamp(){
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
  		 List<Player> templist = new ArrayList<Player>();
		 
  		 try {
  			CallableStatement cs = db.prepareCall("CALL scaha.updateSelectcamp(?)");
  			cs.setString("inbody", this.body);
			ResultSet rs = cs.executeQuery();
   			
			rs.close();
			db.commit();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  		db.free();
  		db.cleanup();
  		
  	}
}
