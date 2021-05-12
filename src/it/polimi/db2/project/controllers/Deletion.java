package it.polimi.db2.project.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.ejb.EJB;
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

import it.polimi.db2.project.entities.Product;
import it.polimi.db2.project.exceptions.InvalidActionException;
import it.polimi.db2.project.exceptions.ProductException;
import it.polimi.db2.project.services.ProductService;
import it.polimi.db2.project.services.QuestionnaireAdminService;

/**
 * Servlet implementation class Deletion
 */
@WebServlet("/Deletion")
public class Deletion extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	/**
	 * QuestionnaireAdminService EJB
	 * Contains all the services to manage the questions Admin Side
	 */
	@EJB(name = "it.polimi.db2.project.services/QuestionnaireAdminService")
	private QuestionnaireAdminService questAdminSer;
	
	/**
	 * ProductService EJB
	 * To manage the product
	 */
	@EJB(name = "it.polimi.db2.project.services/ProductService")
	private ProductService prodSer;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Deletion() {
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
	 * Handle the deletion requests with the related errors, an ID is in the get request parameters. Otherwise only the page is printed.
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "/WEB-INF/Deletion.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		Product p = null;
		try {
			//Get the id passed in the get if it exists
			Integer ID = null;
			if(request.getParameter("ID")!=null) {
				ID = Integer.parseInt(request.getParameter("ID"));
				
				//Delete the questionnaire and it will return the deleted product
				p = questAdminSer.deleteQuestionnaires(ID);
			}	
		}catch(InvalidActionException | ProductException e) {
			//e.printStackTrace();
			//Handle the exception thrown by the deletion
			ctx.setVariable("errorMsg", e.getMessage()); 
		}catch(NumberFormatException e) {
			//e.printStackTrace();
			//Handle the exception of the parteInd
			ctx.setVariable("errorMsg", "The ID is not correct"); 
		}		
		
		//If the product is not null this means that the deletion went fine
		if(p!=null) ctx.setVariable("okMessage", "Product " + p.getName() + " is been deleted correctly"); 
		
		try {
			//Retrieve the list of all the past products
			ctx.setVariable("pastProd", prodSer.getPastScheduledProductOfTheDay());
			ctx.setVariable("formatter", new SimpleDateFormat("dd/MM/yyyy"));
		}catch(Exception e) {
			//e.printStackTrace();
			//Handle all the other errors
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		templateEngine.process(path, ctx, response.getWriter());
	}
}
