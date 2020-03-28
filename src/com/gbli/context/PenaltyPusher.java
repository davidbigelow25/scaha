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
	private Penalty penalty;
	private LiveGame livegame=null;
	private PenaltyList penaltylist = null;
	private String penaltyrows = null;
	
	public PenaltyPusher (Penalty _pen) {
		penalty = _pen;
	}
	
	public PenaltyPusher () {
		super();
	}
	
	public void pushPenalty () {
		SendMailSSL mail = new SendMailSSL(this);
		//mail.sendMail();
		mail.cleanup();
	}
	@Override
	public String getSubject() {
		ScahaTeam team = penalty.getTeam();
		Club club = team.getTeamClub();

		return null;
	}

	@Override
	public String getTextBody() {
		// TODO Auto-generated method stub
		ScahaTeam team = penalty.getTeam();
		Club club = team.getTeamClub();
		
		List<String> myTokens = new ArrayList<String>();
		myTokens.add("SCHEDULE|" + this.livegame.getSched().getDescription());
		myTokens.add("DATE|" + this.livegame.getStartdate());
		myTokens.add("HOMETEAM|" + this.livegame.getHometeamname());
		myTokens.add("AWAYTEAM|" + this.livegame.getAwayteamname());
		myTokens.add("GAMENUMBER|" + this.livegame.ID+"");
		myTokens.add("PENALTYROWS|" + this.getPenaltyrows());
		myTokens.add("CLUBNAME|" + club.getClubname());
		myTokens.add("TEAMNAME|" + team.getTeamname());
		return Utils.mergeTokens(PenaltyPusher.mail_penaltypush_body,myTokens,"\\|");
	}

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
			ps.setInt(1, this.penalty.ID);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				data.add(new InternetAddress(rs.getString(2),rs.getString(1)));
			}
			rs.close();
			ps.close();
			
			for (InternetAddress ia : data) {
				//LOGGER.info("e-mail:" + ia);
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}finally {db.free();}
		//db.free();
		
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
}
