/**
 * 
 */
package com.scaha.objects;

import javax.mail.internet.InternetAddress;

/**
 * Mailable Object - This is an interface that standardizes a mailable object to the system.
 * When a class wants information mailed about it.. it uses this interface..
 * 
 * @author dbigelow
 *
 */
public interface MailableObject {
	
	public String getSubject();
	public String getTextBody();
	public String getPreApprovedCC();
	public String getToMailAddress();
	public InternetAddress[] getToMailIAddress();
	public InternetAddress[] getPreApprovedICC();

}
