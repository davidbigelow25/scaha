package com.scaha.beans;

import java.io.IOException;

import java.io.Serializable;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Player;
import com.scaha.objects.PlayerDataModel;
import com.scaha.objects.Venue;

//import com.gbli.common.SendMailSSL;

@ManagedBean
@ViewScoped
public class venueBean implements Serializable {

	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	private List<Venue> venues = null;
    //private VenueDataModel VDataModel = null;
   
 	@PostConstruct
    public void init() {
		venues = new ArrayList<Venue>();  
        //PlayerDataModel = new PlayerDataModel(players);
        
        venuesDisplay();
    }
	
    public venueBean() {  
         
    }  
    
    
	public List<Venue> getVenues(){
		return venues;
	}
	
	public void setVenues(List<Venue> list){
		venues = list;
	}
	
	    
    //retrieves list of players
    public void venuesDisplay(){
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	List<Venue> tempresult = new ArrayList<Venue>();
    	
    	try{

    		if (db.setAutoCommit(false)) {
    			
				CallableStatement cs = db.prepareCall("CALL scaha.getActiveSCAHAVenues()");
    			rs = cs.executeQuery();
    			
    			if (rs != null){
    				
    				while (rs.next()) {
    					String description = rs.getString("description");
    					String email = rs.getString("email");
        				String phone = rs.getString("phone");
        				String address = rs.getString("address");
        				String city = rs.getString("city");
        				String state = rs.getString("state");
        				String zipcode = rs.getString("zipcode");
        				String website = rs.getString("website");
        				String gmapparms = rs.getString("gmapparms");
        				
        				Venue ovenue = new Venue();
        				ovenue.setDescription(description);
        				ovenue.setEmail(email);
        				ovenue.setPhone(phone);
        				ovenue.setAddress(address);
        				ovenue.setCity(city);
        				ovenue.setState(state);
        				ovenue.setZipcode(zipcode);
        				ovenue.setWebsite(website);
        				ovenue.setGmapparms(gmapparms);
        				tempresult.add(ovenue);
    				}
    				
    				//LOGGER.info("We have results for venue lookup");
    				
    			}
    			rs.close();	
    			db.cleanup();
    		} else {

    		}
    		
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN Searching FOR venues:");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	//setResults(tempresult);
    	setVenues(tempresult);
    	//PlayerDataModel = new PlayerDataModel(players);
    }

    /*public PlayerDataModel getPlayerdatamodel(){
    	return PlayerDataModel;
    }
    
    public void setPlayerdatamodel(PlayerDataModel odatamodel){
    	PlayerDataModel = odatamodel;
    }*/
    
    public MapModel getMapModel(Venue venue) {
		
		MapModel simpleModel = new DefaultMapModel();
        
		String[] coord = venue.getGmapparms().split(",");
		Double lat = new Double(0);
		Double lng = new Double(0);
			
		if (coord.length > 0) {
			lat = Double.parseDouble(coord[0].trim());
			lng = Double.parseDouble(coord[1].trim());
		}
		//LOGGER.info("Creating Map Model for Venue " + venue + ".  Coords are:" +  lat + ":" + lng);
        //Shared coordinates
        LatLng coord1 = new LatLng(lat,lng);
         
        //Basic marker
        simpleModel.addOverlay(new Marker(coord1, venue.toString()));
        
        return simpleModel;
	}
	
	
}

