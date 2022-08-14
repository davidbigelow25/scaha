package com.scaha.beans;

import java.io.Serializable;
import java.util.logging.Logger;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;

import com.gbli.context.ContextManager;
import com.scaha.objects.Venue;

@ManagedBean
@RequestScoped
public class MapBean implements Serializable  {


	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private Venue currentvenue = null;
	
	
	public MapModel getMapModel() {
		
		MapModel simpleModel = new DefaultMapModel();
        
		String[] coord = currentvenue.getGmapparms().split(",");
		Double lat = new Double(0);
		Double lng = new Double(0);
			
		if (coord.length > 0) {
			lat = Double.parseDouble(coord[0].trim());
			lng = Double.parseDouble(coord[1].trim());
		}
		LOGGER.info("Creating Map Model for Venue " + currentvenue + ".  Coords are:" +  lat + ":" + lng);
        //Shared coordinates
        LatLng coord1 = new LatLng(lat,lng);
         
        //Basic marker
        simpleModel.addOverlay(new Marker(coord1, currentvenue.toString()));
        
        return simpleModel;
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
	 * @return the currentvenue
	 */
	public Venue getCurrentvenue() {
		return currentvenue;
	}


	/**
	 * @param currentvenue the currentvenue to set
	 */
	public void setCurrentvenue(Venue currentvenue) {
		this.currentvenue = currentvenue;
	}

	
}
