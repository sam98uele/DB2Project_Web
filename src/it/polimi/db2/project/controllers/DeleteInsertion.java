package it.polimi.db2.project.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.db2.project.services.ProductAdminService;

/**
 * Servlet implementation class DeleteInsertion
 */
@WebServlet("/DeleteInsertion")
public class DeleteInsertion extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteInsertion() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductAdminService prodAdminSer = (ProductAdminService) request.getSession().getAttribute("prodAdminSer");
		
		//Remove the product
		prodAdminSer.undoCreation();
		
		//Redirect to the home page, with the message TODO: message your product is been removed correctly
		response.sendRedirect(getServletContext().getContextPath() + "/Admin?insertionMessage=Your product has been deleted successfully");
	}

}
