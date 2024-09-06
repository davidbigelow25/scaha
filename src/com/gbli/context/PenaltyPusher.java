package com.gbli.context;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.InternetAddress;

import com.gbli.common.SendMailSSL;
import com.gbli.common.Utils;
import com.gbli.connectors.ScahaDatabase;
import com.scaha.objects.Club;
import com.scaha.objects.LiveGame;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Penalty;
import com.scaha.objects.PenaltyList;
import com.scaha.objects.ScahaTeam;

public class PenaltyPusher  implements Serializable,  MailableObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	private static String mail_penaltypush_body = Utils.getMailTemplateFromFile("/mail/penaltypush.html");
	private static String mail_penaltypush_served = Utils.getMailTemplateFromFile("/mail/penaltypushserved.html");
	private static String mail_penaltypush_remove = Utils.getMailTemplateFromFile("/mail/penaltypushremove.html");

	private Penalty penalty;
	private LiveGame livegame=null;
	private PenaltyList penaltylist = null;
	private String penaltyrows = null;
	private String emailsubject = null;
	private String servedrows = null;
	private Boolean isServed = null;
	private Boolean isRemoved = null;

	
	public PenaltyPusher (Penalty _pen) {
		penalty = _pen;
	}
	
	public PenaltyPusher () {
		super();
	}
	
	public void pushPenalty () {
		SendMailSSL mail = new SendMailSSL(this);
		mail.sendMail();
		mail.cleanup();
	}

	@Override
	public String getSubject() {
		return this.emailsubject;
	}

	public void setEmailsubject(String insubject){
		this.emailsubject=insubject;
	}


	@Override
	public String getTextBody() {
		// TODO Auto-generated method stub
		ScahaTeam team = penalty.getTeam();
		//Club club = team.getTeamClub();
		
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("SCHEDULE|" + this.livegame.getSched().getDescription());
		myTokens.add("DATE|" + this.livegame.getStartdate());
		myTokens.add("HOMETEAM|" + this.livegame.getHometeamname());
		myTokens.add("AWAYTEAM|" + this.livegame.getAwayteamname());
		myTokens.add("GAMENUMBER|" + this.livegame.ID+"");
		myTokens.add("PENALTYROWS|" + this.getPenaltyrows());
		myTokens.add("MUSTSERVE|" + this.getServedrows());
		//myTokens.add("CLUBNAME|" + club.getClubname());
		myTokens.add("TEAMNAME|" + penalty.getTeamname());
		if (this.isServed){
			return Utils.mergeTokens(PenaltyPusher.mail_penaltypush_served, myTokens, "\\|");
		}else if (this.isRemoved) {
			return Utils.mergeTokens(PenaltyPusher.mail_penaltypush_remove, myTokens, "\\|");
		}else {
			return Utils.mergeTokens(PenaltyPusher.mail_penaltypush_body, myTokens, "\\|");
		}
	}

	public String getServedrows(){return this.servedrows;}

	public void setServedrows(String value) {this.servedrows=value;}

	@Override
	public String getPreApprovedCC() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getToMailAddress() {
		return null;
	}



	@Override
	public InternetAddress[] getToMailIAddress() {
		//
		// Here is where we get all the e-mails we need to get
		//
		ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		List<InternetAddress> data = new ArrayList<InternetAddress>();
		try {
			PreparedStatement ps = db.prepareCall("call scaha.getPenaltyPushEmails(?)");
			ps = db.prepareCall("call scaha.getPenaltyPushEmailsforTeam(?)");
			if (this.penalty.ID==0){
				ps.setInt(1, this.penalty.getTeam().ID);
			}else {
				ps.setInt(1, this.penalty.getTeam().ID);
			}

			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				InternetAddress tempaddress =new InternetAddress(rs.getString(2),rs.getString(1));
				//tempaddress = new InternetAddress("lahockeyfan2@yahoo.com","Rob-Foster");
				data.add(tempaddress);

			}
			rs.close();
			ps.close();
			
			for (InternetAddress ia : data) {
				//LOGGER.info("e-mail:" + ia);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			db.rollback();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}

		return data.toArray(new InternetAddress[data.size()]);
	}

	@Override
	public InternetAddress[] getPreApprovedICC() {
		// THis is the Stats and the President of SCAHA
		return null;
	}

	public Penalty getPenalty() {
		return penalty;
	}

	public void setPenalty(Penalty penalty) {
		this.penalty = penalty;
	}

	public LiveGame getLivegame() {
		return livegame;
	}

	public void setLivegame(LiveGame livegame) {
		this.livegame = livegame;
	}
	
	public String getPenaltyrows(){
		return penaltyrows;
	}
	
	public void setPenaltyrows(String value){
		penaltyrows=value;
	}

	public Boolean getIsServed() {
		return isServed;
	}

	public void setIsServed(Boolean value) {
		this.isServed = value;
	}

	public Boolean getIsRemoved() {
		return isRemoved;
	}

	public void setIsRemoved(Boolean value) {
		this.isRemoved = value;
	}

}
