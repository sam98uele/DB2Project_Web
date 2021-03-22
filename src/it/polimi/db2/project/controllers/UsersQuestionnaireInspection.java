package it.polimi.db2.project.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

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
import it.polimi.db2.project.entities.User;
import it.polimi.db2.project.exceptions.ProductException;
import it.polimi.db2.project.services.ProductService;
import it.polimi.db2.project.services.QuestionnaireAdminService;

/**
 * Servlet implementation class UsersQuestionnaireInspection
 */
@WebServlet("/UsersQuestionnaireInspection")
public class UsersQuestionnaireInspection extends HttpServlet {
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
     * Default constructor
     * 
     * @see HttpServlet#HttpServlet()
     */
    public UsersQuestionnaireInspection() {
    }
    
    /**
     * Init class
     */
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// initializing the variables
		String path = "/WEB-INF/UsersQuestionnaireInspection.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		// getting the ID of the questionnaire as get Parameter
		Integer ID = null;
		try {
			ID = Integer.parseInt(request.getParameter("ID")); 
		}catch(Exception e) {
			// if no ID or if badly ID is provided
			// we display a 400 Error 
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "You did't provide a correct ID");
			return;
		}
		
		// Retrieving the product
		Product product;
		try {
			product = prodSer.getProductById(ID);
		} catch (ProductException e1) {
			// if there are problems in retrieving the product
			// display a 400 error
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e1.getMessage());
			return;
		}
		
		// setting the variable product
		ctx.setVariable("product", product);
		
		// if the product is null, it does not have sense to get the list of users that submitted it
		//	so, we will render the page, with the product that is null
		//	the JSP will display an error message
		if(product == null) {
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		// we try to get the list of users who submitted and deleted the questionnaire
		try {
			ctx.setVariable("subUsers", questAdminSer.getListUserCompiledQuestionnaire(ID, true));
			ctx.setVariable("delUsers", questAdminSer.getListUserCompiledQuestionnaire(ID, false));
		}catch(Exception e) {
			// if there are exceptions, we display an error
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to fullfil the request!");
			return;
		}
		
		// we get the data in the correct format
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			String date = sdf.format(product.getDate());
			ctx.setVariable("date", date);
		}catch(Exception e) {
			// if errors display the error page
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Unable to fullfil the request!");
			return;
		}
		
		// render the page
		templateEngine.process(path, ctx, response.getWriter());
	}
	
}
