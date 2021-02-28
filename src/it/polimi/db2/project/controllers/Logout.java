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
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
    }

	/**
	 * Get session, invalidate it and return to the home page.
	 *  @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession(false);
		ProductAdminService prodAdminSer;
		QuestionnaireResponseService qRespSer;
		if(session != null) {
			try {
				prodAdminSer = (ProductAdminService) request.getSession().getAttribute("prodAdminSer");
				if(prodAdminSer!=null) prodAdminSer.remove();
			}catch(Exception e) {
			}
			try {
				qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
				if(qRespSer!=null) qRespSer.remove();
			}catch(Exception e) {
			}
			
			session.invalidate();
		}
		String path = getServletContext().getContextPath() + "/";
		response.sendRedirect(path);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
