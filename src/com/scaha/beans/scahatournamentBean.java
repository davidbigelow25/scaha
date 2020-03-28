package com.scaha.beans;

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

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Tournament;
import com.scaha.objects.TournamentDivision;
import com.scaha.objects.Venue;

//import com.gbli.common.SendMailSSL;
@ManagedBean
@ViewScoped

public class scahatournamentBean implements Serializable {

	private static final long serialVersionUID = 2L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	transient private ResultSet rs = null;
	transient private ResultSet rs2 = null;
	transient private ResultSet rs3 = null;
	private Integer tournament = null;
	
	//properties for generating tournaments
	private List<Tournament> tournaments;
	
	@PostConstruct
    public void init() {
		
        loadTournaments();
    
	}
	
    public scahatournamentBean() {  
        
    }  
    
    public Integer getTournament(){
    	return tournament;
    }
    
    public void setTournament(Integer value){
    	tournament=value;
    }
    
    public List<Tournament> getTournaments(){
    	return tournaments;
    }
    
    public void setTournaments(List<Tournament> list){
    	tournaments=list;
    }
    
    
    
    
	
	
	public void loadTournaments() {  
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<Tournament> tempresult = new ArrayList<Tournament>();
		
    	try{
    		//first get team name
    		CallableStatement cs = db.prepareCall("CALL scaha.getSCAHATournaments()");
    		CallableStatement cs2 = db.prepareCall("CALL scaha.getSCAHATournamentVenues(?)");
    		CallableStatement cs3 = db.prepareCall("CALL scaha.getSCAHATournamentDivisions(?)");
			rs = cs.executeQuery();
			
			if (rs != null){
				
				while (rs.next()) {
					String idscahatournament = rs.getString("idscahatournaments");
					String tournamentname = rs.getString("tournamentname");
					String tournamentsanction = rs.getString("sanction");
					String tourneywebsite = rs.getString("website");
					String startdate = rs.getString("startdate");
					String enddate = rs.getString("enddate");
					String contactname = rs.getString("contactname");
					String contactphone = rs.getString("contactphone");
					String contactemail = rs.getString("contactemail");
					
					cs2.setInt("scahatournamentid", Integer.parseInt(idscahatournament));
					rs2 = cs2.executeQuery();
					
					List<Venue> tempvenue = new ArrayList<Venue>();
					List<TournamentDivision> tempdivision = new ArrayList<TournamentDivision>();
					
					
					while (rs2.next()) {
						String tag = rs2.getString("tag");
						String description = rs2.getString("description");
						String address = rs2.getString("address");
						String city = rs2.getString("city");
						String state = rs2.getString("state");
						String zip = rs2.getString("zipcode");
						String phone = rs2.getString("phone");
						String email = rs2.getString("email");
						String website = rs2.getString("website");
						String gmap = rs2.getString("gmapparms");
						
						Venue v = new Venue();
						v.setPrimary(false);
						v.setTag(tag);
						v.setDescription(description);
						v.setAddress(address);
						v.setCity(city);
						v.setState(state);
						v.setZipcode(zip);
						v.setPhone(phone);
						v.setEmail(email);
						v.setWebsite(website);
						v.setGmapparms(gmap);
						
						tempvenue.add(v);
						v=null;
					}
					
					cs3.setInt("scahatournamentid", Integer.parseInt(idscahatournament));
					rs3 = cs3.executeQuery();
					
					while (rs3.next()) {
						String divisionname = rs3.getString("divisionname");
						Boolean aaa = rs3.getBoolean("aaa");
						Boolean aa = rs3.getBoolean("aa");
						Boolean a = rs3.getBoolean("a");
						Boolean bb = rs3.getBoolean("bb");
						Boolean b = rs3.getBoolean("b");
						Boolean track1 = rs3.getBoolean("track1");
						Boolean track2 = rs3.getBoolean("track2");
						
						TournamentDivision td = new TournamentDivision();
						td.setDivisionname(divisionname);
						td.setAaa(aaa);
						td.setAa(aa);
						td.setA(a);
						td.setBb(bb);
						td.setB(b);
						td.setTrack1(track1);
						td.setTrack2(track2);
						
						tempdivision.add(td);
						td=null;
					}
					
					Tournament tournament = new Tournament();
					tournament.setIdtournament(Integer.parseInt(idscahatournament));
					tournament.setTournamentname(tournamentname);
					tournament.setSanction(tournamentsanction);
					tournament.setWebsite(tourneywebsite);
					tournament.setStartdate(startdate);
					tournament.setEnddate(enddate);
					tournament.setContact(contactname);
					tournament.setPhone(contactphone);
					tournament.setEmail(contactemail);
					tournament.setVenues(tempvenue);
					tournament.setDivisions(tempdivision);
					tempresult.add(tournament);
					
					tempvenue=null;
					tempdivision=null;
    			}
				//LOGGER.info("We have results for tourney list");
			}
			
			rs.close();
			db.cleanup();
    		
			
			
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		LOGGER.info("ERROR IN getting tournament list ");
    		e.printStackTrace();
    		db.rollback();
    	} finally {
    		//
    		// always clean up after yourself..
    		//
    		db.free();
    	}
    	
    	setTournaments(tempresult);
		
    }
	
	
	
}

