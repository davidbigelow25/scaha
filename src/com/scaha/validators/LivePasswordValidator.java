package com.scaha.validators;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import com.scaha.beans.ProfileBean;



public class LivePasswordValidator implements Validator {

	public LivePasswordValidator() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void validate(FacesContext context, UIComponent component, Object value)
			throws ValidatorException {
		
		
		Application app = context.getApplication();

		ValueExpression expression = app.getExpressionFactory().createValueExpression( context.getELContext(),
				"#{profileBean}", Object.class );

		ProfileBean pb = (ProfileBean) expression.getValue( context.getELContext() );
	
		if (value == null) {
            return; // Let required="true" handle, if any.
        }

		String curPassCheck = (String) value;
		
		//context.addMessage("mp-form:add-member:add-member-grid:usah-dob", new FacesMessage(PASSWORDS_DONT_MATCH, PASSWORDS_DONT_MATCH));

        if (!curPassCheck.equals(pb.getLive_password())) {
        	
        	FacesMessage message = new FacesMessage();
        	message.setSeverity(FacesMessage.SEVERITY_ERROR);
        	message.setSummary("The Current Password is not correct.");
        	message.setDetail("The Current Password is not correct.");
            throw new ValidatorException(message);
        }
	}

}
