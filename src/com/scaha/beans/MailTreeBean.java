package com.scaha.beans;

import java.io.Serializable;  
import java.io.UnsupportedEncodingException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;  

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;  
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;  
import javax.mail.internet.InternetAddress;
import org.primefaces.model.TreeNode;  
import org.primefaces.model.CheckboxTreeNode;  

import com.gbli.common.SendMailSSL;
import com.gbli.connectors.ScahaDatabase;
import com.gbli.context.ContextManager;
import com.scaha.objects.Club;
import com.scaha.objects.ClubAdmin;
import com.scaha.objects.ClubList;
import com.scaha.objects.GeneralSeason;
import com.scaha.objects.MailableObject;
import com.scaha.objects.Person;
import com.scaha.objects.ScahaTeam;
import com.scaha.objects.TeamList;

@ManagedBean
@ViewScoped
public class MailTreeBean implements Serializable, MailableObject {  
      
	//
	// Class Level Variables
	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(ContextManager.getLoggerContext());
	
	
	@ManagedProperty(value="#{scahaBean}")
    private ScahaBean scaha;
	
	@ManagedProperty(value="#{profileBean}")
	private ProfileBean pb;
	
	//
	// The basic constructs here!
	//
    private TreeNode root;  
    private TreeNode[] selectedNodes;  
      
    private String subject = null;
    private String body = null;
    
    private List<InternetAddress> myEmails = null;
	private String ccemail;
    
    /**
	 * @return the myEmails
	 */
	public List<InternetAddress> getMyEmails() {
		return myEmails;
	}

	/**
	 * @param myEmails the myEmails to set
	 */
	public void setMyEmails(List<InternetAddress> myEmails) {
		this.myEmails = myEmails;
	}

	public MailTreeBean() {  
	
    }  

    @PostConstruct
    public void init() {
    	//
		// Now lets get all the 
		//

		root = new CheckboxTreeNode("iSCAHA Mailing Tree Widget", null);  
		TreeNode clubTop = new CheckboxTreeNode("All Clubs", root); 
		TreeNode adminTop = new CheckboxTreeNode("All Admin", root); 
		TreeNode adminPres = new CheckboxTreeNode("All Presidents", adminTop); 
		TreeNode adminReg = new CheckboxTreeNode("All Registrars", adminTop); 
		TreeNode adminIce = new CheckboxTreeNode("All Ice Convenors", adminTop); 
		TreeNode adminNon = new CheckboxTreeNode("All Members With No Home Team", root);
		TreeNode adminNotMem= new CheckboxTreeNode("All Accounts still needing membership(s)", root);
		
		
		ClubList cl = scaha.getScahaClubList();
		
		//
		// Now we focus on all the roles without any parent..
		//
		for (Club c : cl) {
			//
			// we add the club..
			//
			this.addClubChild(c,clubTop);
			
		}
		
		for (Club c : cl) {

			TreeNode node = new CheckboxTreeNode(c.getCal().getStaffer("C-PRES"),adminPres);
			node = new CheckboxTreeNode(c.getCal().getStaffer("C-ICE"),adminIce);
			node = new CheckboxTreeNode(c.getCal().getStaffer("C-REG"),adminReg);
			
		}
		
		//
		// Now lets load all the presidents, registrars, and ice guys
		//
			
    }

    /**
     * Recursive method to add nodes to a tree
     * @param _rl
     * @param _node
     */
    public void addClubChild(Club _cl, TreeNode _node) {
    	
    	TreeNode club = new CheckboxTreeNode(_cl, _node); 
    	TreeNode staffTop = new CheckboxTreeNode("All Staff", club); 
    	TreeNode teamTop = new CheckboxTreeNode("All Teams", club); 
    	TreeNode FamTop = new CheckboxTreeNode("All Club Families", club); 
    	
    	for (ClubAdmin ca : _cl.getCal()) {
    		TreeNode node = new CheckboxTreeNode(ca, staffTop); 
    	}
    	
    	//
    	// chances are.. teams may or may not be set yet.. 
    	//
		if (_cl.getScahaTeams() == null) {
			//LOGGER.info("The Selected Club Has no Teams Set!:" + _cl);

			GeneralSeason scahags = scaha.getScahaSeasonList().getCurrentSeason();
			ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", pb.getProfile());
			try {
				_cl.setScahaTeams(TeamList.NewTeamListFactory(pb.getProfile(), db, _cl, scahags, true, false));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			db.free();
		}
    	for (ScahaTeam t : _cl.getScahaTeams()) {
    		TreeNode node = new CheckboxTreeNode(t, teamTop); 
    		FamTop = new CheckboxTreeNode("All Team Families",node);
    		staffTop = new CheckboxTreeNode("Coaching Staff",node);
    		staffTop = new CheckboxTreeNode("Managers",node);
    		staffTop = new CheckboxTreeNode("Locker Room Attendants",node);
    		// need to get coaches here..
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
      
      
    /**
     *  Lets collect the e-mails from the objects that were selected.
     *  
     * @return
     */
    public void collectEmails() {
    	
    	List<InternetAddress> emails  = new ArrayList<InternetAddress>();
    	
    	//
    	// First.. lets go after the actual people objects.
    	//
    	if(selectedNodes != null && selectedNodes.length > 0) {  
    		try {
	
    			for(TreeNode node : selectedNodes) {  
    				Object obj = node.getData();
					if (obj instanceof Person) {
						Person per = (Person)obj;
						emails.add(new InternetAddress(per.getsEmail(),per.getsFirstName() + " " + per.getsLastName()));
					} else if (obj instanceof ClubAdmin) {
						Person per = (Person)obj;
						emails.add(new InternetAddress(per.getsEmail(),per.getsFirstName() + " " + per.getsLastName()));
					}  
    			}  
			
    		} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
              
    		//
    		// now.. lets go after all the seasonal information.. 
    		// This is only used for Parents tied to a club for a particular season..
    		//
    		for(TreeNode node : selectedNodes) {  
    			//LOGGER.info(node.getData().getClass().getName());
    			Object obj = node.getData();
    			if (obj instanceof String && node.getData().toString().contains("All Club Families")) {
    				Club c = (Club)node.getParent().getData();
    				ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", pb.getProfile());
    				try {
					  emails.addAll(db.getClubFamilyEmails(c,scaha.getScahaSeasonList().getCurrentSeason()));
    				} catch (SQLException e) {
    					e.printStackTrace();
    				}
    				db.free();
    			} else if (obj instanceof String && node.getData().toString().contains("All Members With No Home Team")) {
    				ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", pb.getProfile());
    				try {
    					emails.addAll(db.getRenegadeFamilyEmails(scaha.getScahaSeasonList().getCurrentSeason()));
    				} catch (SQLException e) {
    					e.printStackTrace();
    				} catch (UnsupportedEncodingException e) {
    					e.printStackTrace();
    				}
    				db.free();
    			} else if (obj instanceof String && node.getData().toString().contains("All Accounts still needing membership(s)")) {
    				ScahaDatabase db = (ScahaDatabase) ContextManager.getDatabase("ScahaDatabase", pb.getProfile());
    				try {
    					emails.addAll(db.getOutstandingMemberSignupEmails());
    				} catch (SQLException e) {
    					e.printStackTrace();
    				} catch (UnsupportedEncodingException e) {
    					e.printStackTrace();
    				}
    				db.free();
    			}  
   			}  
    	}

    	this.myEmails = emails;
    	
    }

    public void sendMail() {
    	
    	//LOGGER.info("Email: going to send quickmail " + this.subject + ":" + this.body);
    	
    	this.collectEmails();
		SendMailSSL mail = new SendMailSSL(this);
		mail.sendMail();
		
		for (InternetAddress s : this.myEmails) {
	    	//LOGGER.info("Email: going to send quickmail to the following:" + s);
		}
    	FacesContext context = FacesContext.getCurrentInstance();  
		context.addMessage(null, new FacesMessage("Successful", "Your iSite Quick Message has been sent out to all target members."));  

    }
    
	/**
	 * @return the scaha
	 */
	public ScahaBean getScaha() {
		return scaha;
	}


	/**
	 * @param scaha the scaha to set
	 */
	public void setScaha(ScahaBean scaha) {
		this.scaha = scaha;
	}


	/**
	 * @return the pb
	 */
	public ProfileBean getPb() {
		return pb;
	}


	/**
	 * @param pb the pb to set
	 */
	public void setPb(ProfileBean pb) {
		this.pb = pb;
	}

	/**
	 * @return the subject
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}

	/**
	 * @return the body
	 */
	public String getBody() {
		return body;
	}

	/**
	 * @param body the body to set
	 */
	public void setBody(String body) {
		this.body = body;
	}  
	
	public void resetTree()	{
		
		this.body = null;
		this.subject = null;
		this.setCcemail(null);
		if (this.selectedNodes == null) {
			//LOGGER.info("selectedNode are null.." );
			return;
		}
		
		for (TreeNode node : selectedNodes) {
			node.setSelected(false);
			node.setPartialSelected(false);
			node.setExpanded(false);
			resetParentTree(node);
			this.resetChildrenTree(node);
		}
		
		this.selectedNodes = null;

	}
	
	private void resetChildrenTree(TreeNode _tn)	{
		for (TreeNode node : _tn.getChildren()) {
			//LOGGER.info("Before NODE:" + node.toString() + "=" + node.isSelected());
			node.setSelected(false);
			node.setPartialSelected(false);
			node.setExpanded(false);
			//LOGGER.info("After NODE:" + node.toString() + "=" + node.isSelected());
			resetParentTree(node);
			this.resetChildrenTree(node);
		}
	}

	private void resetParentTree(TreeNode _tn)	{
		if (_tn == null) return;
		TreeNode pnode = _tn.getParent();
		pnode.setPartialSelected(false);
		pnode.setSelected(false);
		if (pnode.getParent() != null && pnode.getParent().isSelected() || pnode.getParent().isPartialSelected()) resetParentTree(pnode);
	}

	
	@Override
	public String getTextBody() {
		return this.body;
	}

	@Override
	public String getPreApprovedCC() {
	  return null;
	}

	@Override
	public String getToMailAddress() {
		return pb.getProfile().getUserName();
	}

	/**
	 * @return the ccemail
	 */
	public String getCcemail() {
		return ccemail;
	}

	/**
	 * @param ccemail the ccemail to set
	 */
	public void setCcemail(String ccemail) {
		this.ccemail = ccemail;
	}

	@Override
	public InternetAddress[] getToMailIAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public InternetAddress[] getPreApprovedICC()  {
		String [] emails = this.ccemail.split(",");
		try {
			myEmails.add(new InternetAddress("info@scaha.com","iScaha Web Site"));
			for (String s : emails) {
				if (s.trim().length() > 0) {
					myEmails.add(new InternetAddress(s,s));
				}
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myEmails.toArray(new InternetAddress[]{});
	}


}  