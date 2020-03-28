package com.scaha.validators;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.component.html.HtmlInputSecret;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.gbli.context.ContextManager;

public class ConfirmPasswordValidator implements Validator {
	
	//
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	

     	@Override
        public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
            String password = (String) value;
            String confirm = (String) component.getAttributes().get("confirm");

            //LOGGER.info("pass=" + password + ", confirm=" + confirm);
            
            if (password == null || confirm == null || password.isEmpty()) {
                return; // Just ignore and let required="true" do its job.
            }

            if (!password.equals(confirm)) {
                throw new ValidatorException(new FacesMessage("Passwords are not equal."));
            }
        }
    }

