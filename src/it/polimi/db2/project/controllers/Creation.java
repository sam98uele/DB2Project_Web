package it.polimi.db2.project.controllers;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.project.exceptions.ProductException;
import it.polimi.db2.project.services.ProductAdminService;

/**
 * Servlet implementation class Creation
 */
@WebServlet("/Creation")
public class Creation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Creation() {
        super();
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	/**
	 * Print the creation page with the relative errors if they exists.
	 * err = is the error in the insertion of the product
	 * err2 = error in the saving of the product
	 * err3 = error in the submission of a question
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ProductAdminService prodAdminSer = (ProductAdminService) request.getSession().getAttribute("prodAdminSer");
		
		String path = "/WEB-INF/Creation.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("product", prodAdminSer.getProduct());

		//If there is an error in the insertion of the product, print the error passed
		String err = request.getParameter("error");
		ctx.setVariable("errorMessageProduct", err);

		//If there is an error in the saving of the product, print the error passed
		String err2 = request.getParameter("errorSave");
		ctx.setVariable("errorSave", err2);

		//If there is an error in the submission of a question
		String err3 = request.getParameter("errorQuestion");
		ctx.setVariable("errorQuestion", err3);

		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * Request to save the product created, if something goes wrong redirect to the creation page with the error.
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Load ProductAdminService statefull bean from the session
		ProductAdminService prodAdminSer = (ProductAdminService) request.getSession().getAttribute("prodAdminSer");
		
		//When the person save the product and the questions
		try {
			prodAdminSer.saveProduct();
		} catch (ProductException e) {
			//e.printStackTrace();
			//Redirect to the creation page with the respective error
			response.sendRedirect(getServletContext().getContextPath() + "/Creation?errorSave=" + e.getMessage() + "");
			return;
		}
		
		//Redirect to the home page with the message that all went fine
		response.sendRedirect(getServletContext().getContextPath() + "/Admin?insertionMessage=Your product has been inserted successfully");

	}
}
