/**
 * 
 */
package com.gbli.common;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.validator.ValidatorException;

import org.apache.commons.lang3.RandomStringUtils;

import com.gbli.connectors.DatabasePool;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Family;
import com.scaha.objects.FamilyMember;
import com.scaha.objects.Person;
import com.scaha.objects.Profile;
import com.scaha.objects.ScahaCoach;
import com.scaha.objects.ScahaManager;
import com.scaha.objects.ScahaMember;
import com.scaha.objects.ScahaPlayer;
import com.scaha.objects.UsaHockeyRegistration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.krysalis.barcode4j.impl.code128.Code128Bean;
import org.krysalis.barcode4j.impl.code39.Code39Bean;
import org.krysalis.barcode4j.output.bitmap.BitmapCanvasProvider;
import org.krysalis.barcode4j.tools.UnitConv;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


/**
 * @author dbigelow
 *
 */
public class Utils {
	
	static Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	
	public static void main (String[] args) throws Exception  {
		
		genCoacheProfiles();
		
	}
	
	public static String properCase(String s) {
		Pattern p = Pattern.compile("(^|\\W)([a-z])");
		Matcher m = p.matcher(s.toLowerCase());
		StringBuffer sb = new StringBuffer(s.length());
		while(m.find()) {
			m.appendReplacement(sb, m.group(1) + m.group(2).toUpperCase() );
		}
		m.appendTail(sb);
		return sb.toString();
	}

	
	
	public static String getRandom5CharStringUpper() {
		return RandomStringUtils.random(5, true, true).toUpperCase();
	}
	
	
	public static void buildabear() {
		
		
		
		
	}
	/**
	 * This is for all the coaches  that were created under someone elses account.  They need to have their own.
	 */
	public static void genCoacheProfiles () throws SQLException {

		
		ContextManager.setLogger("LoadUSAInfo");
		
		Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
		
		DatabasePool dbp2 = new DatabasePool(ScahaDatabase.class.getSimpleName(),2);
		Thread th2 =  new Thread(dbp2);
		dbp2.setMyThread(th2);
		th2.start();
		
		ScahaDatabase db = (ScahaDatabase)dbp2.getDatabase();  // Outer loop
		ScahaDatabase db2 = (ScahaDatabase)dbp2.getDatabase(); // Inner Loop
		db2.setAutoCommit(false);

		String mySQL =
			" SELECT  " +
			" p1.idperson, " +
			" p1.fname, " +
			" p1.lname, " +
			" p1.altemail, " +
			" us.workphone, " +
			" us.pgemail, " +
			" us.address, " +
			" us.city, " +
			" us.state, " +
			" us.zipcode, " +
			" us.country, " +
			" fm.idfamilymember " + 
			" from scaha.person p1  " +
			" join scaha.usahockey us on " +
			" us.idperson = p1.idperson " +
			" join scaha.familymember fm on  " +
			" fm.idperson = p1.idperson " +
			" where p1.idperson in (select idperson from scaha.coach) and p1.idprofile = 0 " +
			" and (p1.fname, p1.lname) not in (Select p2.fname, p2.lname from scaha.person p2 where idprofile <> 0) ";

		db.getData(mySQL);
		int i=1;
		
		//
		// ok.. we are here to create new profiles.. persons, family records.. player records.. scaha records.. and 
		//
		
		ResultSet rs = db.getResultSet();
		while (rs.next()) {
			int y=1;
			// lets get the person object here!!
			int id = rs.getInt(y++);
			String fname = rs.getString(y++);
			String lname = rs.getString(y++);	
			String altemail = rs.getString(y++);	
			String workphone = rs.getString(y++);	
			String pgemail = rs.getString(y++);	
			String address = rs.getString(y++);	
			String city = rs.getString(y++);	
			String state = rs.getString(y++);	
			String zipcode = rs.getString(y++);	
			String country = rs.getString(y++);			
			int idfm = rs.getInt(y++);

			
			Vector<String> v = new Vector<String>();
			v.add(pgemail);
			db2.getData("CALL scaha.checkforuser(?)", v);
		        
			if (db2.getResultSet() != null && db2.getResultSet().next()){
				//LOGGER.info("Cannot Create Profile due to same e-mail address..." + pgemail +":" + fname + ":" +  lname);
				db2.getResultSet().close();
				continue;
			}
			db2.getResultSet().close();	
			
			// new profile so they can log in ...
			Profile pro = new Profile(pgemail,pgemail, fname + " " + lname);
			pro.update(db2);

			// existing person to update all the needed things
			Person per = new Person(id, pro);
			
			per.setsEmail(altemail);
			per.setsPhone(workphone);
			per.setsEmail(pgemail);
			per.setsAddress1(address);
			per.setsCity(city);
			per.setsState(state);
			per.setiZipCode(Integer.parseInt(zipcode.substring(0, 4)));
			
			per.update(db2);
			
			Family fam = new Family(-1, pro, per);
			fam.setFamilyName("The " + per.getsFirstName() + " " + per.getsLastName() + " Family");
			fam.update(db2, false);
			
			//
			// lets update the family member rec
			//
			FamilyMember fm = new FamilyMember(pro,fam, per);
			fm.ID = idfm;
			fm.setRelationship("Self");
			fm.updateFamilyMemberStructure(db2);
			
			db2.commit();
			
		}
		rs.close();
	
		
		db.free();
		db2.free();
		
	}
			
	//
	// All this guy does is generate accounts from the import file given by USAHockey
	//
	public static void genAccountsParentPlayer() throws SQLException {
		
		
		ContextManager.setLogger("LoadUSAInfo");
		
		Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
		
		DatabasePool dbp2 = new DatabasePool(ScahaDatabase.class.getSimpleName(),2);
		Thread th2 =  new Thread(dbp2);
		dbp2.setMyThread(th2);
		th2.start();
		
		ScahaDatabase db = (ScahaDatabase)dbp2.getDatabase();  // Outer loop
		ScahaDatabase db2 = (ScahaDatabase)dbp2.getDatabase(); // Inner Loop
		db2.setAutoCommit(false);
		

		String mySQL = 
				" select distinct " + 
				" ipf.prim_email, " +
				" case when ipf.pg1_first = ''  then ipf.first_name else ipf.pg1_first end, " +
				" case when ipf.pg1_last= ''  then ipf.last_name else ipf.pg1_last end, " +
				" concat(ipf.address1,' ', ipf.address2) as address, " +
				" ipf.city, " +
				" ipf.st, " +
				" ipf.zip_code as zip, " +
				" ipf.forzip as forzip, " +
				" ipf.prim_phone, " +
				" ipf.sec_phone, " +
				" ipf.part_email, " +
				" ipf.first_name as first_name, " +
				" ipf.last_name as last_name, " +
				" ipf.mi as mi, " + 
				" DATE_FORMAT(ipf.dob, '%m/%d/%Y') as dob, " +
				" ipf.ver_dob, " +
				" ipf.country as country, " +
				" ipf.gender as gender, " +
				" ipf.citz as citizenship, " +
				" ipf.conf_no as usahockeynumber, " +
				" substring(ipf.conf_no,4,1) as usayear " +
				" from scaha.USADataFile ipf " +
//				" where  2013 - YEAR(ipf.dob) <= 18 and ipf.pg1_last= '' " +
				" where  2013 - YEAR(ipf.dob) <= 18 " +
				" and last_name = 'STICKLEY' " + 
				" order by 3,2,1,4 ";  // They could have played last year...
		
		db.getData(mySQL);
		int i=1;
		
		//
		// ok.. we are here to create new profiles.. persons, family records.. player records.. scaha records.. and 
		//
		
		Profile pro = null;
	
		while (db.getResultSet().next()) {
			int y=1;
			String email = db.getResultSet().getString(y++).toLowerCase().trim();
			String fname = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String lname = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String addr = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String city = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String st = db.getResultSet().getString(y++).trim().toUpperCase();
			String zip = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String forzip = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String phone = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String secphone = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String altemail = db.getResultSet().getString(y++).toLowerCase().trim();
			String kidfirst =Utils.properCase(db.getResultSet().getString(y++)).trim();
			String kidlast = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String kidmi = db.getResultSet().getString(y++).toUpperCase().trim();
			String kiddob = db.getResultSet().getString(y++);
			String verdob = db.getResultSet().getString(y++);
			String country = db.getResultSet().getString(y++);
			String gender = db.getResultSet().getString(y++);
			String citizen = db.getResultSet().getString(y++);
			String usahockey = db.getResultSet().getString(y++);
			String usayear = db.getResultSet().getString(y++);
			
			System.out.println("Here is a row:" + (i++) + " "+ 
			email + ", " +
			fname + ", " +
			lname + ", " +
			addr + ", " +
			city + ", " +
			st + ", " +
			zip + ", " +
			forzip + ", " +
			phone + ", " +
			secphone + ", " + 
			altemail + ", " +
			kidfirst + ", " +
			kidlast + ", " +
			kidmi + ", " +
			kiddob + ", " +
			verdob + ", " +
			country + ", " +
			gender + ", " +
			citizen + ", " +
			usahockey + ", " +
			usayear
			);

			if (usahockey.length() != 14) {
				//LOGGER.info("usaHockey # for " + fname + " " + lname + "is bad =" + usahockey);
				continue;
			}
			

			int iProfileid = 0;
			String struser = null;
			String strpwd = null;
			String strnn = null;
			
			Vector<String> v = new Vector<String>();
			v.add(email);
			db2.getData("CALL scaha.checkforuser(?)", v);
		        
			//
			// This should always be true..
			//
			if (db2.getResultSet() != null && db2.getResultSet().next()) {
				iProfileid = db2.getResultSet().getInt(1);
				struser = db2.getResultSet().getString(2);
				strpwd = db2.getResultSet().getString(3);
				strnn = db2.getResultSet().getString(4);
			} 

			db2.getResultSet().close();
			db2.cleanup();

			//
			// if profile is still 0 then we need to make a new one.. else.. we need to fetch the existing one..
			//
			
			if (iProfileid > 0 ) {
				
				// LOGGER.info("Profile already exists.. loading it (and Person, family)");
				 pro =  new Profile (iProfileid, db2, strnn, struser, strpwd, false);
				 //LOGGER.info("Profile already exists.. DONE loading it (and Person, family)");

			} else {

				//LOGGER.info("Creating new Profile.. person for email");

				pro = new Profile(email, email, fname + " " + lname);
				pro.update(db2);
				Person per = new Person(pro);
				pro.setPerson(per);
				per.setsFirstName(fname);
				per.setsLastName(lname);
				per.setsAddress1(addr);
				per.setsCity(city);
				per.setsState(st);
				per.setiZipCode(Integer.parseInt(zip.substring(0,5)));
				per.setsPhone(phone);
				per.setsEmail(altemail);
				per.setGender("U");

				// Lets update now ..
				per.update(db2);
				Family fam = new Family(-1, pro, per);
				//LOGGER.info("Creating new Family.");
				fam.setFamilyName("The " + per.getsLastName() + " Family");
				fam.update(db2, false);
				
				//
				// Make sure the person is always them selves
				//
				//LOGGER.info("Creating new FamilyMember (self).");
				FamilyMember fm = new FamilyMember(pro,fam, per);
				fm.setRelationship("Self");
				fm.updateFamilyMemberStructure(db2);
			
			}
			
			//
			// ok.. now we have a top solid person.. lets grab all the things we need here...
			//
			
			//
			//  The person already may be there.. lets see if usa hockey # already there.
			//

			v = new Vector<String>();
	        v.add(usahockey);
			db2.getData("CALL scaha.checkForExistingUSAHNum(?)", v);
			
    		//
			// iF THE COUNT COMES BACK > 0 THEN SOMEONE ALREADY HAS THAT USA Hockey In play
			// 
			if (db2.getResultSet() != null && db2.getResultSet().next()){
				if (db2.getResultSet().getInt(1) > 0) {
					//LOGGER.info("!!USA HOCKEY NUMBER ALREADY IN SYSTEM.. SKIPPING: " + usahockey);
					db2.commit();
					continue;
				}
			}
			
			Person tper = pro.getPerson();   //  This is their person record
			Family tfam = tper.getFamily();  //  This is their Family Structure
			
			UsaHockeyRegistration usar = new UsaHockeyRegistration(-1,usahockey);
 			usar.setAddress(addr);
			usar.setBPhone(phone);
			usar.setCitizen(citizen);
			usar.setCity(city);
			usar.setCountry(country);
			usar.setDOB(kiddob);
			usar.setEmail(email);
			usar.setFirstName(kidfirst);
			usar.setForZip(forzip);
			usar.setGender(gender);
			usar.setHPhone(secphone);
			usar.setLastName(kidlast);
			usar.setMiddleInit(kidmi);
			usar.setPGSFName(fname);
			usar.setPGSLName(lname);
			usar.setPGSMName("");
			usar.setState(st);
			usar.setUSAHnum(usahockey);
			usar.setZipcode(zip);
			
			
			//
			// If the 
			//
			Person per = new Person(pro);		// This will be the new person
			//
			// If the person is the child.. (meaning) they registered themselves.. (when they are 18.. or a true adult)
			if (fname.equals(kidfirst) && lname.equals(kidlast) && kidfirst.equals(tper.getsFirstName()) && kidlast.equals(tper.getsLastName())) {
				per = tper;
				//LOGGER.info("!!!!!Selfie Found for " + tper);
			} else {
				per.gleanUSAHinfo(usar);
				per.update(db2);
				FamilyMember fm = new FamilyMember(pro, tfam, per);
				fm.setRelationship("Child");
				fm.updateFamilyMemberStructure(db2);
			}

			ScahaPlayer sp = null;
			ScahaMember mem = null;
			
			//
			// here we make the USAHockey Information available..
			//
			//
			// lets always make a new one!
			//
			usar.update(db2, per);
			mem = new ScahaMember(pro,per);
			mem.setSCAHAYear(usar.getMemberShipYear());
			mem.generateMembership(db2);
			mem.setTopPerson(tper);
			sp = new ScahaPlayer(pro, per);
			sp.gleanUSAHinfo(usar);
			sp.update(db2);

			db2.commit();

		}
		
		db2.free();
		db.free();
		
		Thread th = dbp2.getMyThread();
		dbp2.shutdown();
		th.interrupt();
		try {
			th.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	//
	// All this guy does is generate accounts from the import file given by USAHockey
	//
	public static void genAccountsCoaches() throws SQLException {
		
		
		//
		// Get a privat database pool going.
		//
		
		ContextManager.setLogger("LoadUSAInfo");
		
		Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
		
		DatabasePool dbp2 = new DatabasePool(ScahaDatabase.class.getSimpleName(),2);
		Thread th2 =  new Thread(dbp2);
		dbp2.setMyThread(th2);
		th2.start();
		
		ScahaDatabase db = (ScahaDatabase)dbp2.getDatabase();  // Outer loop
		ScahaDatabase db2 = (ScahaDatabase)dbp2.getDatabase(); // Inner Loop
		db2.setAutoCommit(false);

		String mySQL = 
				" select distinct   " +
				" ipf.prim_email,   " +
				" case when ipf.pg1_first = ''  then ipf.first_name else ipf.pg1_first end,   " +
				" case when ipf.pg1_last= ''  then ipf.last_name else ipf.pg1_last end,   " +
				" concat(ipf.address1,' ', ipf.address2) as address,   " +
				" ipf.city,   " +
				"  ipf.st,   " +
				"  ipf.zip_code as zip,   " +
				"  ipf.forzip as forzip,   " +
				"  ipf.prim_phone,   " +
				"  ipf.sec_phone,   " +
				"  ipf.part_email,   " +
				"  ipf.first_name as first_name,   " +
				"  ipf.last_name as last_name,   " +
				"  ipf.mi as mi,    " +
				"  DATE_FORMAT(ipf.dob, '%m/%d/%Y') as dob,   " +
				"  ipf.ver_dob,   " +
				"  ipf.country as country,   " +
				"  ipf.gender as gender,   " +
				"  ipf.citz as citizenship,   " +
				"  ipf.conf_no as usahockeynumber,   " +
				"  substring(ipf.conf_no,4,1) as usayear,  " +
				"  ipf.cep_level, " +
				"  ipf.cep_card, " +
				"  DATE_FORMAT(ipf.cep_expire, '%m/%d/%Y') as cep_expire,   " +
				"  mt08, " +
				"  sq10, " +
				"  pw12, " +
				"  ba14, " +
				"  md18 " +
				" from scaha.USADataFile ipf   " +
				" join scaha.cahaassociation ca on " +
				" ca.cahanum = ipf.assoc_code " +
				" and ca.inscahacoach = 1 " +
				" where ipf.cep_card <> '' " + 
				" and ca.cahanum = 'CAH1050' " +
  			    " order by 3,2,1,4 ";

		
		db.getData(mySQL);
		int i=1;
		
		//
		// ok.. we are here to create new profiles.. persons, family records.. player records.. scaha records.. and 
		//
		
		Profile pro = null;
		
		while (db.getResultSet().next()) {
			int y=1;
			String email = db.getResultSet().getString(y++).toLowerCase().trim();
			String fname = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String lname = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String addr = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String city = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String st = db.getResultSet().getString(y++).trim().toUpperCase();
			String zip = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String forzip = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String phone = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String secphone = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String altemail = db.getResultSet().getString(y++).toLowerCase().trim();
			String kidfirst =Utils.properCase(db.getResultSet().getString(y++)).trim();
			String kidlast = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String kidmi = db.getResultSet().getString(y++).toUpperCase().trim();
			String kiddob = db.getResultSet().getString(y++);
			String verdob = db.getResultSet().getString(y++);
			String country = db.getResultSet().getString(y++);
			String gender = db.getResultSet().getString(y++);
			String citizen = db.getResultSet().getString(y++);
			String usahockey = db.getResultSet().getString(y++);
			String usayear = db.getResultSet().getString(y++);
			int ceplevel = db.getResultSet().getInt(y++);
			String cepnumber = db.getResultSet().getString(y++);
			String cepexpires = db.getResultSet().getString(y++);
			String mt = db.getResultSet().getString(y++);
			String sq = db.getResultSet().getString(y++);
			String pw = db.getResultSet().getString(y++);
			String ba = db.getResultSet().getString(y++);
			String md = db.getResultSet().getString(y++);
			
			System.out.println("Here is a row:" + (i++) + " "+ 
			email + ", " +
			fname + ", " +
			lname + ", " +
			addr + ", " +
			city + ", " +
			st + ", " +
			zip + ", " +
			forzip + ", " +
			phone + ", " +
			secphone + ", " + 
			altemail + ", " +
			kidfirst + ", " +
			kidlast + ", " +
			kidmi + ", " +
			kiddob + ", " +
			verdob + ", " +
			country + ", " +
			gender + ", " +
			citizen + ", " +
			usahockey + ", " +
			usayear + " , " +
			 ceplevel + ", " +
			 cepnumber + ", " +
			 cepexpires + ", " +
			 mt + ", " +
			 sq + ", " +
			 pw + ", " +
			 ba + ", " +
			 md 
			
			);
			
		
			if (usahockey.length() != 14) {
				//LOGGER.info("usaHockey # for " + fname + " " + lname + "is bad =" + usahockey);
				continue;
			}
			

			int iProfileid = 0;
			String struser = null;
			String strpwd = null;
			String strnn = null;
			
			Vector<String> v = new Vector<String>();
			v.add(email);
			db2.getData("CALL scaha.checkforuser(?)", v);
		        
			//
			// This should always be true..
			//
			if (db2.getResultSet() != null && db2.getResultSet().next()) {
				iProfileid = db2.getResultSet().getInt(1);
				struser = db2.getResultSet().getString(2);
				strpwd = db2.getResultSet().getString(3);
				strnn = db2.getResultSet().getString(4);
			} 

			db2.getResultSet().close();
			db2.cleanup();

			//
			// if profile is still 0 then we need to make a new one.. else.. we need to fetch the existing one..
			//
			
			if (iProfileid > 0 ) {
				
				 //LOGGER.info("Profile already exists.. loading it (and Person, family)");
				 pro =  new Profile (iProfileid, db2, strnn, struser, strpwd, false);
				// LOGGER.info("Profile already exists.. DONE loading it (and Person, family)");

			} else {

				//LOGGER.info("Creating new Profile.. person for email");

				pro = new Profile(email, email, fname + " " + lname);
				pro.update(db2);
				Person per = new Person(pro);
				pro.setPerson(per);
				per.setsFirstName(fname);
				per.setsLastName(lname);
				per.setsAddress1(addr);
				per.setsCity(city);
				per.setsState(st);
				per.setiZipCode(Integer.parseInt(zip.substring(0,5)));
				per.setsPhone(phone);
				per.setsEmail(altemail);
				per.setGender("U");

				// Lets update now ..
				per.update(db2);
				Family fam = new Family(-1, pro, per);
				//LOGGER.info("Creating new Family.");
				fam.setFamilyName("The " + per.getsLastName() + " Family");
				fam.update(db2, false);
				
				//
				// Make sure the person is always them selves
				//
				//LOGGER.info("Creating new FamilyMember (self).");
				FamilyMember fm = new FamilyMember(pro,fam, per);
				fm.setRelationship("Self");
				fm.updateFamilyMemberStructure(db2);
			
			}
			
			//
			// ok.. now we have a top solid person.. lets grab all the things we need here...
			//
			
			//
			//  The person already may be there.. lets see if usa hockey # already there.
			//

			v = new Vector<String>();
	        v.add(usahockey);
			db2.getData("CALL scaha.checkForExistingUSAHNum(?)", v);
			
    		//
			// iF THE COUNT COMES BACK > 0 THEN SOMEONE ALREADY HAS THAT USA Hockey In play
			// 
			if (db2.getResultSet() != null && db2.getResultSet().next()){
				if (db2.getResultSet().getInt(1) > 0) {
					//LOGGER.info("!!USA HOCKEY NUMBER ALREADY IN SYSTEM.. SKIPPING: " + usahockey);
					db2.commit();
					continue;
				}
			}
			
			Person tper = pro.getPerson();   //  This is their person record
			Family tfam = tper.getFamily();  //  This is their Family Structure
			
			UsaHockeyRegistration usar = new UsaHockeyRegistration(-1,usahockey);
 			usar.setAddress(addr);
			usar.setBPhone(phone);
			usar.setCitizen(citizen);
			usar.setCity(city);
			usar.setCountry(country);
			usar.setDOB(kiddob);
			usar.setEmail(email);
			usar.setFirstName(kidfirst);
			usar.setForZip(forzip);
			usar.setGender(gender);
			usar.setHPhone(secphone);
			usar.setLastName(kidlast);
			usar.setMiddleInit(kidmi);
			usar.setPGSFName(fname);
			usar.setPGSLName(lname);
			usar.setPGSMName("");
			usar.setState(st);
			usar.setUSAHnum(usahockey);
			usar.setZipcode(zip);
			
			
			//
			// If the 
			//
			Person per = new Person(pro);		// This will be the new person
			//
			// If the person is the child.. (meaning) they registered themselves.. (when they are 18.. or a true adult)
			if (fname.equals(kidfirst) && lname.equals(kidlast) && kidfirst.equals(tper.getsFirstName()) && kidlast.equals(tper.getsLastName())) {
				per = tper;
				//LOGGER.info("!!!!!Selfie Found for " + tper);
			} else {
				per.gleanUSAHinfo(usar);
				per.update(db2);
				FamilyMember fm = new FamilyMember(pro, tfam, per);
				fm.setRelationship("Child");
				fm.updateFamilyMemberStructure(db2);
			}

			ScahaCoach sc = null;
			ScahaMember mem = null;
			
			//
			// here we make the USAHockey Information available..
			//
			//
			// lets always make a new one!
			//
			usar.update(db2, per);
			mem = new ScahaMember(pro,per);
			mem.setSCAHAYear(usar.getMemberShipYear());
			mem.generateMembership(db2);
			mem.setTopPerson(tper);
			sc = new ScahaCoach(pro,per);
			sc.setCepexpires(cepexpires);
			sc.setCeplevel(ceplevel);
			sc.setCepnumber(cepnumber);
			sc.setScreeningexpires("01/01/1980");
			sc.setU8((mt.equals("Y") ? 1 :0));
			sc.setU10((sq.equals("Y") ? 1 :0));
			sc.setU12((pw.equals("Y") ? 1 :0));
			sc.setU14((ba.equals("Y") ? 1 :0));
			sc.setU18((md.equals("Y") ? 1 :0));
			sc.gleanUSAHinfo(usar);
			sc.update(db2);

			
			db2.commit();

		}
		
		db2.free();
		db.free();
		
		Thread th = dbp2.getMyThread();
		dbp2.shutdown();
		th.interrupt();
		try {
			th.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	
	//
	// All this guy does is generate accounts from the import file given by USAHockey
	//
	public static void genAccountsManagers() throws SQLException {
		
		
		//
		// Get a privat database pool going.
		//
		
		ContextManager.setLogger("LoadUSAInfo");
		
		Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
		
		DatabasePool dbp2 = new DatabasePool(ScahaDatabase.class.getSimpleName(),2);
		Thread th2 =  new Thread(dbp2);
		dbp2.setMyThread(th2);
		th2.start();
		
		ScahaDatabase db = (ScahaDatabase)dbp2.getDatabase();  // Outer loop
		ScahaDatabase db2 = (ScahaDatabase)dbp2.getDatabase(); // Inner Loop
		db2.setAutoCommit(false);

		String mySQL = 
				" select distinct   " +
				" ipf.prim_email,   " +
				" case when ipf.pg1_first = ''  then ipf.first_name else ipf.pg1_first end,   " +
				" case when ipf.pg1_last= ''  then ipf.last_name else ipf.pg1_last end,   " +
				" concat(ipf.address1,' ', ipf.address2) as address,   " +
				" ipf.city,   " +
				"  ipf.st,   " +
				"  ipf.zip_code as zip,   " +
				"  ipf.forzip as forzip,   " +
				"  ipf.prim_phone,   " +
				"  ipf.sec_phone,   " +
				"  ipf.part_email,   " +
				"  ipf.first_name as first_name,   " +
				"  ipf.last_name as last_name,   " +
				"  ipf.mi as mi,    " +
				"  DATE_FORMAT(ipf.dob, '%m/%d/%Y') as dob,   " +
				"  ipf.ver_dob,   " +
				"  ipf.country as country,   " +
				"  ipf.gender as gender,   " +
				"  ipf.citz as citizenship,   " +
				"  ipf.conf_no as usahockeynumber,   " +
				"  substring(ipf.conf_no,4,1) as usayear,  " +
				"  ipf.cep_level, " +
				"  ipf.cep_card, " +
				"  DATE_FORMAT(ipf.cep_expire, '%m/%d/%Y') as cep_expire,   " +
				"  mt08, " +
				"  sq10, " +
				"  pw12, " +
				"  ba14, " +
				"  md18 " +
				" from scaha.USADataFile ipf   " +
				" join scaha.cahaassociation ca on " +
				" ca.cahanum = ipf.assoc_code " +
				" and ca.inscahacoach = 1 " +
				" where ipf.conf_no like '%XX%' or ipf.mtype like '%M%' " + 
				" order by 3,2,1,4 ";

		
		db.getData(mySQL);
		int i=1;
		
		//
		// ok.. we are here to create new profiles.. persons, family records.. player records.. scaha records.. and 
		//
		
		Profile pro = null;
		
		while (db.getResultSet().next()) {
			int y=1;
			String email = db.getResultSet().getString(y++).toLowerCase().trim();
			String fname = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String lname = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String addr = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String city = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String st = db.getResultSet().getString(y++).trim().toUpperCase();
			String zip = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String forzip = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String phone = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String secphone = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String altemail = db.getResultSet().getString(y++).toLowerCase().trim();
			String kidfirst =Utils.properCase(db.getResultSet().getString(y++)).trim();
			String kidlast = Utils.properCase(db.getResultSet().getString(y++)).trim();
			String kidmi = db.getResultSet().getString(y++).toUpperCase().trim();
			String kiddob = db.getResultSet().getString(y++);
			String verdob = db.getResultSet().getString(y++);
			String country = db.getResultSet().getString(y++);
			String gender = db.getResultSet().getString(y++);
			String citizen = db.getResultSet().getString(y++);
			String usahockey = db.getResultSet().getString(y++);
			String usayear = db.getResultSet().getString(y++);
			int ceplevel = db.getResultSet().getInt(y++);
			String cepnumber = db.getResultSet().getString(y++);
			String cepexpires = db.getResultSet().getString(y++);
			String mt = db.getResultSet().getString(y++);
			String sq = db.getResultSet().getString(y++);
			String pw = db.getResultSet().getString(y++);
			String ba = db.getResultSet().getString(y++);
			String md = db.getResultSet().getString(y++);
			
			System.out.println("Here is a row:" + (i++) + " "+ 
			email + ", " +
			fname + ", " +
			lname + ", " +
			addr + ", " +
			city + ", " +
			st + ", " +
			zip + ", " +
			forzip + ", " +
			phone + ", " +
			secphone + ", " + 
			altemail + ", " +
			kidfirst + ", " +
			kidlast + ", " +
			kidmi + ", " +
			kiddob + ", " +
			verdob + ", " +
			country + ", " +
			gender + ", " +
			citizen + ", " +
			usahockey + ", " +
			usayear + " , " +
			 ceplevel + ", " +
			 cepnumber + ", " +
			 cepexpires + ", " +
			 mt + ", " +
			 sq + ", " +
			 pw + ", " +
			 ba + ", " +
			 md 
			
			);
			
		
			if (usahockey.length() != 14) {
				//LOGGER.info("usaHockey # for " + fname + " " + lname + "is bad =" + usahockey);
				continue;
			}
			

			int iProfileid = 0;
			String struser = null;
			String strpwd = null;
			String strnn = null;
			
			Vector<String> v = new Vector<String>();
			v.add(email);
			db2.getData("CALL scaha.checkforuser(?)", v);
		        
			//
			// This should always be true..
			//
			if (db2.getResultSet() != null && db2.getResultSet().next()) {
				iProfileid = db2.getResultSet().getInt(1);
				struser = db2.getResultSet().getString(2);
				strpwd = db2.getResultSet().getString(3);
				strnn = db2.getResultSet().getString(4);
			} 

			db2.getResultSet().close();
			db2.cleanup();

			//
			// if profile is still 0 then we need to make a new one.. else.. we need to fetch the existing one..
			//
			
			if (iProfileid > 0 ) {
				
				 //LOGGER.info("Profile already exists.. loading it (and Person, family)");
				 pro =  new Profile (iProfileid, db2, strnn, struser, strpwd, false);
				// LOGGER.info("Profile already exists.. DONE loading it (and Person, family)");

			} else {

				//LOGGER.info("Creating new Profile.. person for email");

				pro = new Profile(email, email, fname + " " + lname);
				pro.update(db2);
				Person per = new Person(pro);
				pro.setPerson(per);
				per.setsFirstName(fname);
				per.setsLastName(lname);
				per.setsAddress1(addr);
				per.setsCity(city);
				per.setsState(st);
				per.setiZipCode(Integer.parseInt(zip.substring(0,5)));
				per.setsPhone(phone);
				per.setsEmail(altemail);
				per.setGender("U");

				// Lets update now ..
				per.update(db2);
				Family fam = new Family(-1, pro, per);
				//LOGGER.info("Creating new Family.");
				fam.setFamilyName("The " + per.getsLastName() + " Family");
				fam.update(db2, false);
				
				//
				// Make sure the person is always them selves
				//
				//LOGGER.info("Creating new FamilyMember (self).");
				FamilyMember fm = new FamilyMember(pro,fam, per);
				fm.setRelationship("Self");
				fm.updateFamilyMemberStructure(db2);
			
			}
			
			//
			// ok.. now we have a top solid person.. lets grab all the things we need here...
			//
			
			//
			//  The person already may be there.. lets see if usa hockey # already there.
			//
			
			Person tper = pro.getPerson();   //  This is their person record
			Family tfam = tper.getFamily();  //  This is their Family Structure
			
			UsaHockeyRegistration usar = new UsaHockeyRegistration(-1,usahockey);
 			usar.setAddress(addr);
			usar.setBPhone(phone);
			usar.setCitizen(citizen);
			usar.setCity(city);
			usar.setCountry(country);
			usar.setDOB(kiddob);
			usar.setEmail(email);
			usar.setFirstName(kidfirst);
			usar.setForZip(forzip);
			usar.setGender(gender);
			usar.setHPhone(secphone);
			usar.setLastName(kidlast);
			usar.setMiddleInit(kidmi);
			usar.setPGSFName(fname);
			usar.setPGSLName(lname);
			usar.setPGSMName("");
			usar.setState(st);
			usar.setUSAHnum(usahockey);
			usar.setZipcode(zip);
			
			
			//
			// If the 
			//
			Person per = new Person(pro);		// This will be the new person
			//
			// If the person is the child.. (meaning) they registered themselves.. (when they are 18.. or a true adult)
			if (fname.equals(kidfirst) && lname.equals(kidlast) && kidfirst.equals(tper.getsFirstName()) && kidlast.equals(tper.getsLastName())) {
				per = tper;
				//LOGGER.info("!!!!!Selfie Found for " + tper);
			} else {
				per.gleanUSAHinfo(usar);
				per.update(db2);
				FamilyMember fm = new FamilyMember(pro, tfam, per);
				fm.setRelationship("Other");
				fm.updateFamilyMemberStructure(db2);
			}

			//
			// here we make the USAHockey Information available..
			//
			//
			// lets always make a new one!
			//
			
			v = new Vector<String>();
	        v.add(usahockey);
			db2.getData("CALL scaha.checkForExistingUSAHNum(?)", v);
			
    		//
			// iF THE COUNT COMES BACK > 0 THEN SOMEONE ALREADY HAS THAT USA Hockey In play
			// 
			if (db2.getResultSet() != null && db2.getResultSet().next()){
				if (db2.getResultSet().getInt(1) > 0) {

					//LOGGER.info("!!USA HOCKEY NUMBER ALREADY IN SYSTEM.. SKIPPING: Saving this and Creating a Membership.. " + usahockey);
					
				} else {

					usar.update(db2, per);
					//
					// lets make the membership as well.
					//
					ScahaMember mem = null;
					mem = new ScahaMember(pro,per);
					mem.setSCAHAYear(usar.getMemberShipYear());
					mem.generateMembership(db2);
					mem.setTopPerson(tper);
				}
			}

			ScahaManager sm = null;
			sm = new ScahaManager(pro,per);
			sm.gleanUSAHinfo(usar);
			sm.update(db2);
			
			db2.commit();

		}
		
		db2.free();
		db.free();
		
		Thread th = dbp2.getMyThread();
		dbp2.shutdown();
		th.interrupt();
		try {
			th.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	 /**
     * 
     * This will do a merge of Tokens to Values from a text file and bring it back as one whole string
     * 
     * The _sparm format is TOKEN:VALUE
     * 
     * @return
     */
    public static String mailMerge(String _sPath, List<String> _sparms) {

		return mergeTokens(getMailTemplateFromFile(_sPath), _sparms);
    }
    

	 /**
     * 
     * This will pull the mail template from the file system
     * 
     * The _sparm format is TOKEN:VALUE
     * 
     * @return
     */
    public static String getMailTemplateFromFile(String _sPath) {

    	String sAbsolutePath = ContextManager.getRealPath() + _sPath;
		//LOGGER.info("absolute Path is here:" + sAbsolutePath);

		StringBuffer sb = new StringBuffer();
		
    	try (BufferedReader br = new BufferedReader(new FileReader(sAbsolutePath))) {
    		String sCurrentLine;
    		while ((sCurrentLine = br.readLine()) != null) {
    			sb.append(sCurrentLine);
    		}
    		br.close();
    	} catch (IOException ex) {
    		ex.printStackTrace();
    	} 
    		return sb.toString();
     }
    
    /**
     * Lets pepper with tokens!
     * 
     * @param _str
     * @param _sparms
     * @return
     */
	public static String mergeTokens(String _str, List<String> _sparms) {
		
		//LOGGER.info("Utilites.. mergingTokens now..");
	
		for (String s : _sparms ){
			
			String[] parts = s.split(":");
			String stoken = parts[0];
			String value = parts[1];
			_str = _str.replace(":"+ stoken + ":",value);
		}
		
		return _str;
	}

	public static String mergeTokens(String _str, List<String> _sparms, String _delim) {
		
		//LOGGER.info("Utilites.. mergingTokens now..");
	
		for (String s : _sparms ){
			
			String[] parts = s.split(_delim);
			String stoken = parts[0];
			String value = parts[1];
			_str = _str.replace(":"+ stoken + ":",value);
		}
		
		return _str;
	}
	private static byte [] createBarCode39 (String _barcodeme) {

          Code39Bean bean39 = new Code39Bean();
          final int dpi = 160;
          byte [] bout = null;

          try {

          //Configure the barcode generator
          bean39.setModuleWidth(UnitConv.in2mm(2.8f / dpi));

          bean39.doQuietZone(false);

          ByteArrayOutputStream out = new ByteArrayOutputStream ();

          //Set up the canvas provider for monochrome PNG output
          BitmapCanvasProvider canvas = new BitmapCanvasProvider(
              out, "image/x-png", dpi, BufferedImage.TYPE_BYTE_BINARY, false, 0);

          //Generate the barcode
          bean39.generateBarcode(canvas, _barcodeme);
       
          //Signal end of generation
          canvas.finish();
        
          System.out.println("Bar Code is generated successfully…");
          
          bout = out.toByteArray();
          
          
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
 
          return bout;
        
    }

	public static StreamedContent getStreamedBarCodeContent(String _barcodeme) {
		return new DefaultStreamedContent(new ByteArrayInputStream(Utils.createBarCode39(_barcodeme)),"image/x-png");
	}
}