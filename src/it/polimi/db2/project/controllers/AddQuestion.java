package it.polimi.db2.project.controllers;

import java.io.IOException;

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
	 * Handle when the admin insert a question to the questionnaire
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String question;
		try {
			//Get the question from the request
			question = StringEscapeUtils.escapeJava(request.getParameter("question"));
			
			//Check if the string is null or empty, if it is raise an exception
			if(question == null || question.isEmpty()) {
				throw new Exception("Error in the input!!");
			}
		}catch(Exception e) {
			//e.printStackTrace();
			//Redirect to the Creation page with the error
			response.sendRedirect(getServletContext().getContextPath() + "/Creation?errorQuestion=" + e.getMessage() + "");
			return;
		}
		
		//Get the productAdminService  statefull bean from the session
		ProductAdminService prodAdminSer = (ProductAdminService) request.getSession().getAttribute("prodAdminSer");
		
		//Add the question
		try {
			prodAdminSer.addMarketingQuestion(question);
		} catch (InvalidActionException e) {
			//e.printStackTrace();
			//Redirect to the Creation page with the error
			response.sendRedirect(getServletContext().getContextPath() + "/Creation?errorQuestion=" + e.getMessage() + "");
			return;
		}
		
		//Redirect to the creation page
		response.sendRedirect(getServletContext().getContextPath() + "/Creation");

	}
}
