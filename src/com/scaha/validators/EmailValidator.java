package com.scaha.validators;

import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;

public class EmailValidator implements Validator {
	
	//
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	

	public EmailValidator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void validate(FacesContext arg0, UIComponent arg1, Object value)
			throws ValidatorException {
		// TODO Auto-generated method stub
		
		if (value == null) {
            return; // Let required="true" handle, if any.
        }

		String username = (String) value;
	    
        try {
        	InternetAddress emailAddr = new InternetAddress(username);
        	emailAddr.validate();
       	} catch (AddressException ex) {
        	//LOGGER.info("E-mail Validation: " + username + "is not an e-mail address");
            FacesMessage message = new FacesMessage();
            message.setDetail("E-mail Validation: " + username + "is not an e-mail address");
            message.setSummary("Email Error");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
       	}

        //LOGGER.info("E-mail Validation: " + username + "appears to look copesetic..");
        
		//
		// ok.. lets check the database to make sure its unique..
		// 

        ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
        
        Vector<String> v = new Vector<String>();
		v.add(username);

		db.getData("call scaha.checkforuser(?)", v);

		//
		// iF a row comes back.. We ALREADY Have THAT USERNAME 
		// and in this case, thats great.. because we are going to send 
		// 
		try {
			if (db.getResultSet() != null && db.getResultSet().next()){
			} else {
				throw new ValidatorException(new FacesMessage(
						FacesMessage.SEVERITY_ERROR, "This UserName Cannot be found in the system.", null));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}  finally {
			db.free();
		}

	}

}