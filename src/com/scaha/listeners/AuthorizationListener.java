package com.scaha.listeners;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import javax.servlet.http.HttpSession;

/**
 * This is an artifact from various versions of login approaches
 * @author dbigelow
 *
 */
public class AuthorizationListener implements PhaseListener {

	/**
	 *  
	 */
	private static final long serialVersionUID = 1L;

	public void afterPhase(PhaseEvent event) {

		FacesContext facesContext = event.getFacesContext();
		String currentPage = facesContext.getViewRoot().getViewId();

		boolean isLoginPage = (currentPage.lastIndexOf("login.xhtml") > -1);
		HttpSession session = (HttpSession) facesContext.getExternalContext()
				.getSession(false);

		if (session == null) {
			NavigationHandler nh = facesContext.getApplication()
					.getNavigationHandler();
			nh.handleNavigation(facesContext, null, "loginPage");
		}

		else {
			Object currentUser = session.getAttribute("username");

			if (!isLoginPage && (currentUser == null || currentUser == "")) {
				NavigationHandler nh = facesContext.getApplication()
						.getNavigationHandler();
				nh.handleNavigation(facesContext, null, "loginPage");
			}
		}
	}

	public void beforePhase(PhaseEvent event) {

	}

	public PhaseId getPhaseId() {
		return PhaseId.RESTORE_VIEW;
	}
}