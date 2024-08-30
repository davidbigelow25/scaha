package com.scaha.beans;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

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
import com.scaha.objects.MailableObject;
import com.scaha.objects.NewsItem;
import com.scaha.objects.NewsItemList;

@ManagedBean
@ViewScoped
public class NewsBean implements Serializable,  MailableObject  {


	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	transient private ResultSet rs = null;
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	private NewsItem currentnewsitem = null;
	private String title = null;
	private String newssubject = null;
	private String newsbody = null;
	private String newsintro = null;
	private String updated = null;
	private String author = null;
	
	public void init(){
		setNewsItemModal(75);
	}

	@Override
	public String getSubject() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTextBody() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getToMailAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	 /**
	 * @return the currentnewsitem
	 */
	public NewsItem getCurrentNewsItem() {
		return currentnewsitem;
	}
	
	public String testMerge() {
		
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("FIRSTNAME:David Bigelow");
		return Utils.mailMerge("/mail/test.mail",myTokens);
		
	}

	public void setCurrentNewsItem(NewsItem currentnewsitem) {
		this.currentnewsitem = currentnewsitem;
	}
	public NewsItemList getNewsItemList() {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		NewsItemList nil = null;
		try {
			nil = NewsItemList.NewsItemListFactory(scaha.getDefaultProfile(), db, "12/12/2013");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		db.free();
		return nil;
		
	 }
	
	public void updateNewsItem(NewsItem current) {
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		ValueExpression expression = app.getExpressionFactory().createValueExpression(context.getELContext(), "#{profileBean}", Object.class );
		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			current.setAuthor(pb.getProfile().getPerson().getsFirstName() + " " + pb.getProfile().getPerson().getsLastName());
			current.update(db);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		db.free();
		LOGGER.info(current.toString());

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
	@Override
	public InternetAddress[] getToMailIAddress() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public InternetAddress[] getPreApprovedICC() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public String getTitle(){
		return title;
	}
	
	public void setTitle(String value){
		title=value;
	}
	
	public String getAuthor(){
		return author;
	}
	
	public void setAuthor(String value){
		author=value;
	}
	
	public String getNewsintro(){
		return newsintro;
	}
	
	public void setNewsintro(String value){
		newsintro=value;
	}
	
	
	public String getNewssubject(){
		return newssubject;
	}
	
	public void setNewssubject(String value){
		newssubject=value;
	}
	
	public String getNewsbody(){
		return newsbody;
	}
	
	public void setNewsbody(String value){
		newsbody=value;
	}
	
	public String getUpdated(){
		return updated;
	}
	
	public void setUpdated(String value){
		updated=value;
	}
	
	
	public void addNewsItem() {
		FacesContext context = FacesContext.getCurrentInstance();
		Application app = context.getApplication();
		ValueExpression expression = app.getExpressionFactory().createValueExpression(context.getELContext(), "#{profileBean}", Object.class );
		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			CallableStatement cs = db.prepareCall("CALL scaha.updateNewsItem(?,?,?,?,?,?,?,?)");
    		cs.setInt("inout_idNewsItem", 0);
			cs.setString("in_subject", this.getNewssubject());
    		cs.setString("in_title", this.getTitle());
    		cs.setString("in_author", pb.getProfile().getPerson().getsFirstName() + " " + pb.getProfile().getPerson().getsLastName());
    		cs.setString("in_body", this.getNewsbody());
    		cs.setInt("in_isactive", 1);
    		cs.setString("in_updated", null);
    		cs.setString("in_state", "publish");
			cs.executeQuery();
			
			cs.close();
			db.cleanup();
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		db.free();
		LOGGER.info("added " + this.getNewssubject() + " news item");

		getNewsItemList();
	}
	
	public void hideNewsItem(NewsItem current) {
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try {
			CallableStatement cs = db.prepareCall("CALL scaha.hideNewsItem(?)");
    		cs.setInt("inout_idNewsItem", Integer.parseInt(current.getIDStr()));
			cs.execute();
			cs.close();
			db.commit();
			db.cleanup();
			db.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		db.free();
		LOGGER.info("added " + this.getNewssubject() + " news item");

		getNewsItemList();
	}
	
	public void setNewsItemModal(Integer newsid){
		//need to hit db to get single item news information then set parameters that are loaded into modal
		LOGGER.info("loading " + newsid + " news item");
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		try{

    			Vector<Integer> v = new Vector<Integer>();
    			v.add(newsid);
    			db.getData("CALL scaha.getNewsItem(?)", v);
    		    
    			if (db.getResultSet() != null){
    				//need to add to an array
    				rs = db.getResultSet();
    				
    				while (rs.next()) {
    					author = rs.getString("author");
    					title = rs.getString("title");
        				newssubject = rs.getString("subject");
        				newsbody = rs.getString("body");
        				updated = rs.getString("updated");
        				newsintro = rs.getString("intro");
        				
        						}
    				LOGGER.info("We have results for player details by player id");
    			}
    			rs.close();
    			db.cleanup();
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN loading news item " + newsid);
    		e.printStackTrace();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
	}
}
