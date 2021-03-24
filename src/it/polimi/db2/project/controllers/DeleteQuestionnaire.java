package it.polimi.db2.project.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.db2.project.exceptions.ApplicationErrorException;
import it.polimi.db2.project.services.QuestionnaireResponseService;

/**
 * Servlet implementation class DeleteQuestionnaire
 */
@WebServlet("/DeleteQuestionnaire")
public class DeleteQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteQuestionnaire() {
        super();
    }

	/**
	 * Handle the questionnaire cancellation and redirect to the home page
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Get the QuestionnaireResponseService statefull bean from the session
		QuestionnaireResponseService qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
		
		//Call the questionnaire cancellation
		try {
			qRespSer.cancel();
		} catch (ApplicationErrorException e) {
			// display 500 error
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		//Redirect to the home page
		response.sendRedirect(getServletContext().getContextPath() + "/Home?cancelMessage=Questionnaire deletion successful"); 
	}
}
