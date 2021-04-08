package it.polimi.db2.project.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.persistence.indirection.IndirectList;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.project.entities.MarketingAnswer;
import it.polimi.db2.project.entities.User;
import it.polimi.db2.project.exceptions.ApplicationErrorException;
import it.polimi.db2.project.exceptions.InvalidActionException;
import it.polimi.db2.project.exceptions.NoProductOfTheDayException;
import it.polimi.db2.project.services.QuestionnaireResponseService;

/**
 * Servlet implementation class Questionnaire
 */
@WebServlet("/Questionnaire")
public class Questionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
    
	/**
     * @see HttpServlet#HttpServlet()
     */
    public Questionnaire() {
        super();
    }
    
    public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
		templateResolver.setCharacterEncoding("UTF-8");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Load the QuestionnaireResponseService statefull bean from the session
		QuestionnaireResponseService qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
		
		//Retrieve the username of the user from the session
		User user = (User) request.getSession().getAttribute("user");
		
		//Start the questionnaire for the user
		//The product cannot exist or the user can already have answered the questionnaire
		try {
			if(qRespSer.getResponse() == null) qRespSer.startQuestionnaire(user);
		} catch (NoProductOfTheDayException | InvalidActionException e) {
			//Print the error if there is no questionnaire or the questionnaire is already answered
			//e.printStackTrace();
			String path = "/WEB-INF/Questionnaire.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", e.getMessage()); 
			templateEngine.process(path, ctx, response.getWriter());
			return;
		} catch (ApplicationErrorException e) {
			// If an application error occurred, returning 500 error
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		String path = "/WEB-INF/Questionnaire.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());		
		//Set the product of the day for the marketing questions
		ctx.setVariable("product", qRespSer.getProduct());
		//Set the response variable, that are the responses already responded by the user
		ctx.setVariable("response", qRespSer.getResponse());
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Load the QuestionnaireResponseService statefull bean from the session
		QuestionnaireResponseService qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
		
		
		List<MarketingAnswer> marketingAnswers = new IndirectList<>();
		
		//Number of marketing questions in the questionnaire
		int numQuestions = qRespSer.getProduct().getMarketingQuestions().size();
		
		//Read all the questions' form
		for(Integer i = 0; i<numQuestions; i++) {
			MarketingAnswer questResp = new MarketingAnswer();
			
			//Read the i-th form
			String answ = request.getParameter("quest" + i.toString());
			questResp.setAnswer(answ);
			
			//Append to the list the answer
			marketingAnswers.add(questResp);
			
			//Set the marketing question to the marketing answer
			questResp.setQuestion(qRespSer.getProduct().getMarketingQuestions().get(i));
		}
		
		//Set the marketing questions and go to the marketing answers.
		qRespSer.goToStatisticalSection(marketingAnswers);
		
		response.sendRedirect(getServletContext().getContextPath() + "/StatQuestions");
	}
}
