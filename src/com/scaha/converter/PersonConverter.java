package com.scaha.converter;

import java.sql.SQLException;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;


import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Person;
import com.scaha.objects.PersonList;

@FacesConverter(value = "personConverter")
public class PersonConverter implements Converter {
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());

	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String submittedValue) throws ConverterException {
		
		//LOGGER.info("PersonConverter.. String is:" + submittedValue);
		if (submittedValue.trim().equals("")) {  
			return null;  
		} else {
			 
			 try {  
				
				ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
				int number = Integer.parseInt(submittedValue);  
				PersonList pl = PersonList.NewPersonListFactory(db, number);
				db.free();
				for (Person p :pl) {  
					if (p.ID == number) {  
						return p;  
					}  
				}  
			 } catch(NumberFormatException exception) {  
				 throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid player"));  
		     } catch (SQLException e) {
		    	 e.printStackTrace();
		     }  
		 }  
		return null;  
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object obj) throws ConverterException {
		return ((Person)obj).ID + "";
	}
}
