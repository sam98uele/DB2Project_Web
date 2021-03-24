package it.polimi.db2.project.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.db2.project.exceptions.InvalidInputArgumentException;
import it.polimi.db2.project.services.ProductAdminService;

/**
 * Servlet implementation class DeleteQuestion
 */
@WebServlet("/DeleteQuestion")
public class DeleteQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteQuestion() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Load ProductAdminService statefull bean from the session
		ProductAdminService prodAdminSer = (ProductAdminService) request.getSession().getAttribute("prodAdminSer");
		
		Integer questionId;
		
		try {
			questionId = Integer.parseInt(request.getParameter("questionId"));
			
			//Remove the question
			prodAdminSer.deleteMarketingQuestion(questionId);
		}catch(InvalidInputArgumentException | NumberFormatException e) {
			//e.printStackTrace();
			//handle number format exception and InvalidInputArgumentException deleting nothing
			response.sendRedirect(getServletContext().getContextPath() + "/Creation");
			return;
		}
		
		//Redirect to the creation page.
		response.sendRedirect(getServletContext().getContextPath() + "/Creation");
	}
}
