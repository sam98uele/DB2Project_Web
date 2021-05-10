package it.polimi.db2.project.controllers;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.db2.project.exceptions.ApplicationErrorException;
import it.polimi.db2.project.exceptions.NoProductOfTheDayException;
import it.polimi.db2.project.services.ProductUserService;
import it.polimi.db2.project.entities.Product;
import it.polimi.db2.project.entities.QuestionnaireResponse;
import it.polimi.db2.project.entities.User;

import org.eclipse.persistence.indirection.IndirectList;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

/**
 * Servlet implementation class Home
 */
@WebServlet("/Home")
public class Home extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	/**
	 * ProductUserService EJB for the Product of the day
	 */
	@EJB(name = "it.polimi.db2.project.services/ProductUserService")
	private ProductUserService prodUserSer;

       
    /**
     * Default constructor
     * 
     * @see HttpServlet#HttpServlet()
     */
    public Home() {
    }
    
    /**
     * Initialize the Servlet
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
		
		// here we search for the product of the day
		Product prodDay;
		try {
			// Search for the product of the day
			prodDay = prodUserSer.getProductOfTheDay();
		} catch (NoProductOfTheDayException e) {
			// Set product of the day null if there is the exception is thrown
			prodDay = null;
		}
		
		// setting up the page view
		String path = "/WEB-INF/Home.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		// setting the product of the day in the context, null if there is no product of the day
		ctx.setVariable("prodDay", prodDay);
		
		// getting the user from the session
		User user = (User) request.getSession().getAttribute("user");
		
		// Load the Reviews of the product of the day
		List<QuestionnaireResponse> reviews = new IndirectList<>();
		try {
			reviews = prodUserSer.getReviewsOfTheProductOfTheDay(user.getId());
		}catch(Exception e) {
			// Do nothing
		}
		// setting the reviews in the context
		ctx.setVariable("reviews", reviews);
		
		// Load if the user already answered the questionnaire
		boolean alreadyAnswered;
		try {
			alreadyAnswered = prodUserSer.answeredToQuestionnaireOfTheDay(user);
		}
		catch (ApplicationErrorException e) {
			// if there are some problems internal to the server
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		ctx.setVariable("AlreadyAnsweres", alreadyAnswered); // setting the context variable
		
		//Read if there is the cancellation message
		String cancelMessage = request.getParameter("cancelMessage");
		ctx.setVariable("cancelMessage", cancelMessage);
		
		// render the page
		templateEngine.process(path, ctx, response.getWriter());
	}

}
