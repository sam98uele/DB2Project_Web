package it.polimi.db2.project.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.HttpSession;

import it.polimi.db2.project.services.ProductAdminService;
import it.polimi.db2.project.services.QuestionnaireResponseService;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * Default constructor
     * 
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
    }

	/**
	 * Get session, invalidate it and return to the home page.
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// getting the session
		HttpSession session = request.getSession(false);
		
		ProductAdminService prodAdminSer;
		QuestionnaireResponseService qRespSer;
		
		if(session != null) {
			// if the session exists
			// delete the Stateful EJBs
			try {
				prodAdminSer = (ProductAdminService) request.getSession().getAttribute("prodAdminSer");
				if(prodAdminSer!=null) prodAdminSer.remove();
			} catch(Exception e) { /* do nothing */ }
			
			try {
				qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
				if(qRespSer!=null) qRespSer.remove();
			} catch(Exception e) { /* do nothing */ }
			
			// invalidate the session
			session.invalidate();
		}
		
		String path;
		try {
			Integer ID = Integer.parseInt(request.getParameter("ID"));			
			path = getServletContext().getContextPath() + "/Login?ID=" +ID;
		}catch(Exception e) {
			//If there is no ID an exception is trown
			path = getServletContext().getContextPath() + "/Login";
		}
		
		// redirect to the correct path
		response.sendRedirect(path);
	}
	
}
