package com.scaha.validators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.gbli.context.ContextManager;

@FacesValidator("dobAdultValidator")
public class DOBAdultValidator implements Validator {

	
	//
	// Class Level Variables
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
   
    	if (value == null) {
            return; // Let required="true" handle.
        }
    	
        String strDob = (String) value;
        
        if (getAge(strDob) < 19) { 
        	//LOGGER.info("DOB Validation: " + strDob + " means they are too young to register...");
            FacesMessage message = new FacesMessage();
            message.setDetail("Age Validation: the DOB of " + strDob +  " means they are too young to register...");
            message.setSummary("Email Error");
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(message);
        }
    }
    
    
    private int getAge(String _strdate) {
    	
    	
	    int age = 0;
		try {
	    	SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
			Date dateOfBirth = formatter.parse(_strdate);
			
			Calendar today = Calendar.getInstance();
			Calendar birthDate = Calendar.getInstance();
	
		    birthDate.setTime(dateOfBirth);
		    if (birthDate.after(today)) {
		        throw new IllegalArgumentException("Can't be born in the future");
		    }
	
		    age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);
	
		    // If birth date is greater than todays date (after 2 days adjustment of leap year) then decrement age one year   
		    if ( (birthDate.get(Calendar.DAY_OF_YEAR) - today.get(Calendar.DAY_OF_YEAR) > 3) ||
		            (birthDate.get(Calendar.MONTH) > today.get(Calendar.MONTH ))){
		        age--;
	
		     // If birth date and todays date are of same month and birth day of month is greater than todays day of month then decrement age
		    }else if ((birthDate.get(Calendar.MONTH) == today.get(Calendar.MONTH )) &&
		              (birthDate.get(Calendar.DAY_OF_MONTH) > today.get(Calendar.DAY_OF_MONTH ))){
		        age--;
		    }

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return age;
     
    	
    }
    

}
