package it.polimi.db2.project.controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.project.entities.QuestionnaireResponse;
import it.polimi.db2.project.exceptions.QueryException;
import it.polimi.db2.project.services.QuestionnaireAdminService;

/**
 * Servlet implementation class UserAnswerInspection
 */
@WebServlet("/UserAnswerInspection")
public class UserAnswerInspection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;  
	
	/**
	 * QuestionnaireAdminService EJB
	 * The service uused by the Admin to manage the questionnaire responses
	 */
	@EJB(name = "it.polimi.db2.project.services/QuestionnaireAdminService")
	private QuestionnaireAdminService questAdminSer;
       
    /**
     * Default Construction
     * 
     * @see HttpServlet#HttpServlet()
     */
    public UserAnswerInspection() {
    }
    
    /**
     * Init method
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
		// initializing the servlet variables
		String path = "/WEB-INF/UserAnswerInspection.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		// getting the variables form the GET
		Integer IDuser = null;
		Integer IDprod = null;
		try {
			IDuser = Integer.parseInt(request.getParameter("IDuser"));
			IDprod = Integer.parseInt(request.getParameter("IDprod"));
			
			ctx.setVariable("date", StringEscapeUtils.escapeJava(request.getParameter("date")));
			ctx.setVariable("name", StringEscapeUtils.escapeJava(request.getParameter("name")));
		}catch(Exception e) {
			// if there are problems with them
			//	show a 400 error
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Your request is not valid");
			return;
		}
		
		// retrieving the response object by the user
		QuestionnaireResponse quest_response;
		try {			
			quest_response = questAdminSer.getAllQuestionnaireAnsweredBySpecificUserAndProduct(IDuser, IDprod);
		}
		catch (QueryException e) {
			// 500 error if problems during the execution
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		// setting the variable
		ctx.setVariable("qResponse", quest_response);
		
		// if the response is not null, we can ge the parameters of it
		if(quest_response != null) {
			ctx.setVariable("markAns", quest_response.getOrderedMarketingAnswers());
			ctx.setVariable("statAns", quest_response.getStatisticalAnswers());
			ctx.setVariable("user", quest_response.getUser().getUsername());
		}
		
		// render the page
		templateEngine.process(path, ctx, response.getWriter());
	}

}
