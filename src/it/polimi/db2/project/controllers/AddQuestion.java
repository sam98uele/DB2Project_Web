package it.polimi.db2.project.controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.db2.project.exceptions.InvalidActionException;
import it.polimi.db2.project.services.ProductAdminService;

/**
 * Servlet implementation class AddQuestion
 */
@WebServlet("/AddQuestion")
public class AddQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddQuestion() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String question;
		try {
			question = StringEscapeUtils.escapeJava(request.getParameter("question"));
			if(question == null || question.isEmpty()) {
				throw new Exception("Error in the input!!");
			}
		}catch(Exception e) {
			e.printStackTrace();

			//TODO: handle
			return;
		}
		
		ProductAdminService prodAdminSer = (ProductAdminService) request.getSession().getAttribute("prodAdminSer");
		
		//Add the question
		try {
			prodAdminSer.addMarketingQuestion(question);
		} catch (InvalidActionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Redirect to the creation page
		response.sendRedirect(getServletContext().getContextPath() + "/Creation");

	}
}
