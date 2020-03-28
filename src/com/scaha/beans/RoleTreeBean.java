package com.scaha.beans;

import java.io.Serializable;  
import java.sql.SQLException;
import java.util.logging.Logger;  
import javax.faces.application.FacesMessage;  
import javax.faces.context.FacesContext;  
  
import org.primefaces.model.TreeNode;  
  
import org.primefaces.model.CheckboxTreeNode;  

import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Role;
import com.scaha.objects.RoleCollection;
  
public class RoleTreeBean implements Serializable {  
      
	//
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());  

	private RoleCollection rc = null;
    private TreeNode root;  
      
    private TreeNode[] selectedNodes;  
      
    public RoleTreeBean() {  
    	
    	//
    	// Lets get all the roles!
    	//
    	
    	ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase");
		
			
		try {
			this.rc = new RoleCollection(db);
			
			//
			// Now lets get all the 
			//

			root = new CheckboxTreeNode("iSCAHA User Roles", null);  
			
			//
			// Now we focus on all the roles without any parent..
			//
			for (int i=0;i< rc.getSize();i++) {
				Role rl = (Role)rc.getAt(i);
				if (rl.getParentRole() == null) {
					this.addChild(rl,root);
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			db.free();
		}
		
			
    }  


    /**
     * Recursive method to add nodes to a tree
     * @param _rl
     * @param _node
     */
    public void addChild(Role _rl, TreeNode _node) {
    	TreeNode node = new CheckboxTreeNode(_rl, _node); 
    	
    	
    	for (int i=0;i < _rl.getSize();i++) {
			Role rl = (Role)_rl.getAt(i);
			addChild(rl, node);
    	}
    }

    public TreeNode getRoot() {  
        return root;  
    }  
  
    public void setRoot(TreeNode root2) {  
        this.root = root2;  
    }  
      
    public TreeNode[] getSelectedNodes() {  
        return selectedNodes;  
    }  
  
    public void setSelectedNodes(TreeNode[] selectedNodes) {  
        this.selectedNodes = selectedNodes;  
    }  
      
    public void displaySelectedMultiple() {  
        if(selectedNodes != null && selectedNodes.length > 0) {  
            StringBuilder builder = new StringBuilder();  
  
            for(TreeNode node : selectedNodes) {  
                builder.append(node.getData().toString());  
                builder.append("<br />");  
            }  
  
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", builder.toString());  
  
            FacesContext.getCurrentInstance().addMessage(null, message);  
        }  
    }  
}  