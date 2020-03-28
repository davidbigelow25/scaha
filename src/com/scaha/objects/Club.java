package com.scaha.objects;

import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class Club extends ScahaObject implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	//
	// Multi Media Objects Meta
	//
	public static final String MM_ENTITYTYPE = "CLUB";
	public static final String MM_ATTTYPE = "LOGO";
	
	private String Tag = null;
	private String Sname = null;
	private String clubname = null;
	private String CahaNumber = null;
	private String idclub = null;
	private String WebSite = null;
	private MultiMedia Logo = null;
	private ClubAdminList cal = null;
	private List<Team> teams = null; // This is from LOI side
	private TryoutList tryoutlist = null;
	private TeamList Teams = null;
	private VenueList Venues = null;
	private OpeningsList openings = null;
	
	/**
	 * @return the tag
	 */
	public String getTag() {
		return Tag;
	}

	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		Tag = tag;
	}

	/**
	 * This is basically an empty shell of a club
	 * 
	 */
	public Club (){ 
		this.ID = -1;
	}
	
	/**
	 * Another way to generalize a new Club..
	 * @param _id
	 * @param _pro
	 */
	public Club(int _id, Profile _pro) {
		this.ID  = _id;
		this.setProfile(_pro);
	}
	
	/**
	 * This is to pull club detail.. given the ID
	 * @param _db
	 * @param _pro
	 * @param _id
	 */
	public Club(ScahaDatabase _db, Profile _pro, int _id) {
		
	}
	
	
	public List<Team> getTeams(){
		return teams;
	}
	
	public void setTeams(List<Team> list){
		teams = list;
	}
	
	public String getClubname(){
		return clubname;
	}
	
	public void setClubname(String sName){
		clubname = sName;
	}
	
	public String getClubid(){
		return idclub;
	}
	
	public void setClubid(String sName){
		idclub = sName;
	}

	/**
	 * @return the cahaNumber
	 */
	public String getCahaNumber() {
		return CahaNumber;
	}

	/**
	 * @param cahaNumber the cahaNumber to set
	 */
	public void setCahaNumber(String cahaNumber) {
		CahaNumber = cahaNumber;
	}


	/**
	 * @return the sname
	 */
	public String getSname() {
		return Sname;
	}

	/**
	 * @param sname the sname to set
	 */
	public void setSname(String sname) {
		Sname = sname;
	}

	/**
	 * @return the webSite
	 */
	public String getWebSite() {
		return WebSite;
	}

	/**
	 * @param webSite the webSite to set
	 */
	public void setWebSite(String webSite) {
		WebSite = webSite;
	}

	/**
	 * @return the cal
	 */
	public ClubAdminList getCal() {
		return cal;
	}

	/**
	 * @param cal the cal to set
	 */
	public void setCal(ClubAdminList cal) {
		this.cal = cal;
	}

	/**
	 * @return the tryoutlist
	 */
	public TryoutList getTryoutlist() {
		return tryoutlist;
	}

	/**
	 * @param openinglist the openinglist to set
	 */
	public void setOpeninglist(OpeningsList tl) {
		this.openings = tl;
	}
	
	/**
	 * @return the tryoutlist
	 */
	public OpeningsList getOpeninglist() {
		return openings;
	}

	/**
	 * @param tryoutlist the tryoutlist to set
	 */
	public void setTryoutlist(TryoutList tl) {
		this.tryoutlist = tl;
	}
	
	/**
	 * @return the logo
	 */
	public MultiMedia getLogo() {
		return Logo;
	}

	/**
	 * @param logo the logo to set
	 */
	public void setLogo(MultiMedia logo) {
		Logo = logo;
	}
	
	public int getClubID () {
		return this.ID;
	}

	/**
	 * @return the teams
	 */
	public TeamList getScahaTeams() {
		return Teams;
	}

	/**
	 * @param teams the teams to set
	 */
	public void setScahaTeams(TeamList teams) {
		//LOGGER.info("Setting Team List for Club:" + this.getClubname());
		Teams = teams;
	}

	/**
	 * @return the venues
	 */
	public VenueList getVenues() {
		return Venues;
	}

	/**
	 * @param venues the venues to set
	 */
	public void setVenues(VenueList venues) {
		Venues = venues;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.getClubname();
	}
	
	/**
	 * OK.  we are going to count the number of teams this club has given the skill set 
	 * 
	 * @param _strDivision
	 * @param _strSkill
	 * @return
	 */
	public int getTeamICounts(String _strDivision, String _strSkill) {
		
		int icount = 0;
		for (ScahaTeam t : this.Teams) {
			if (t.getTeamdivision().getTag().equals(_strDivision) && 
					t.getTeamskilllevel().getTag().equals(_strSkill)) {
				icount++;
			}
		}
		
		return icount;
	}

	public String getTeamCounts(String _strDivision, String _strSkill) {
		int icount = getTeamICounts(_strDivision, _strSkill);
		return (icount == 0 ? "" : icount+"");
	}
	
	/**
	 * Returns the total count of teams.. 
	 * @return
	 */
	public int getTotalITeamCount(boolean _exh) {
		if (this.Teams == null) return 0;
		int icount = 0;
		for (ScahaTeam t : this.Teams) {
			if (_exh || (!_exh && (t.getIsexhibition() == 0))) { 
				icount++;
			}
		}
		
		return icount;
	}
	
	public String getTotalTeamCount(boolean _exh) {
		int icount = getTotalITeamCount(_exh);
		return (icount == 0 ? "" : icount+"");
	}
	
	/**
	 * OK.  we are going to count the number of teams this club has given the skill set 
	 * 
	 * @param _strDivision
	 * @param _strSkill
	 * @return
	 */
	public int getXTeamICounts(String _strDivision, String _strSkill) {
		
		int icount = 0;
		for (ScahaTeam t : this.Teams) {
			if (t.getIsexhibition() == 0) continue;
			if (t.getTeamdivision().getTag().equals(_strDivision) && 
					t.getTeamskilllevel().getTag().equals(_strSkill)) {
				icount++;
			}
		}
		
		return icount;
	}

	/**
	 * OK.  we are going to count the number of teams this club has given the skill set 
	 * 
	 * @param _strDivision
	 * @param _strSkill
	 * @return
	 */
	public int geteXTeamICounts(String _strDivision, String _strSkill) {
		
		int icount = 0;
		for (ScahaTeam t : this.Teams) {
			if (t.getIsexhibition() == 0) continue;
			if (t.getXdivisiontag() == null || t.getXskillleveltag() == null) continue;
			if (t.getXdivisiontag().equals(_strDivision) && 
					t.getXskillleveltag().equals(_strSkill)) {
				icount++;
			}
		}
		
		return icount;
	}
	/**
	 * OK.  we are going to count the number of total X Teams
	 * @return
	 */
	public int getTotalXITeamCount() {
		
		int icount = 0;
		for (ScahaTeam t : this.Teams) {
			if (t.getIsexhibition() == 1) icount++;
		}
		
		return icount;
	}
}
