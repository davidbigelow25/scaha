package com.scaha.beans;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;  
import java.util.List;  
import javax.faces.application.FacesMessage;  
import javax.faces.context.FacesContext;  
import org.primefaces.event.TransferEvent;  
  
import org.primefaces.model.DualListModel;  

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Role;
import com.scaha.objects.RoleCollection;
  
public class PicklistRoleBean implements Serializable {  
  
    private DualListModel<Role> roles;  
    private RoleCollection rc = null;
  
    public PicklistRoleBean() {  
        //Roles

    	List<Role> source = new ArrayList<Role>();  
        List<Role> target = new ArrayList<Role>();  
        
        //
    	// Lets get all the roles!
    	//
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
    	//
    	// you want profile here.. so you can preplace all the roles you have access to..
    	//
			
		try {
			this.rc = new RoleCollection(db);
			
			for (int i=0;i<rc.getSize();i++) {
				Role rl = (Role)rc.getAt(i);
				if (!rl.isDefaultRole()) {
					source.add((Role)rc.getAt(i));
				} else {
					target.add((Role)rc.getAt(i));
				}
			}
			roles = new DualListModel<Role>(source, target);  
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			db.free();
		}
    }  
      
    public DualListModel<Role> getRoles() {  
        return roles;    
}  
    public void setRoles(DualListModel<Role> roles) {  
        this.roles = roles;  
    }  
      
    public void onTransfer(TransferEvent event) {  
        StringBuilder builder = new StringBuilder();  
        for(Object item : event.getItems()) {  
            builder.append(((Role) item).getName()).append("<br />");  
        }  
          
        FacesMessage msg = new FacesMessage();  
        msg.setSeverity(FacesMessage.SEVERITY_INFO);  
        msg.setSummary("Items Transferred");  
        msg.setDetail(builder.toString());  
          
        FacesContext.getCurrentInstance().addMessage(null, msg);  
    }  
}  
