package it.polimi.db2.project.controllers;

import java.io.IOException;

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

import it.polimi.db2.project.entities.StatisticalAnswer;
import it.polimi.db2.project.exceptions.ApplicationErrorException;
import it.polimi.db2.project.exceptions.InvalidAnswerException;
import it.polimi.db2.project.exceptions.ResponseException;
import it.polimi.db2.project.services.QuestionnaireResponseService;

/**
 * Servlet implementation class StatQuestions
 */
@WebServlet("/StatQuestions")
public class StatQuestions extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public StatQuestions() {
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		QuestionnaireResponseService qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
		
		String path = "/WEB-INF/StatQuestions.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("stat", qRespSer.getResponseStat());
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Load the QuestionnaireResponseService statefull bean from the session
		QuestionnaireResponseService qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
		
		StatisticalAnswer statAns = new StatisticalAnswer();
		
		//Initialization of the page in case of error
		String path = "/WEB-INF/StatQuestions.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		boolean error = false;
		
		//Read the age
		try {
			//Check if the age is the empty string, this means that it is empty
			String ageString = StringEscapeUtils.escapeJava(request.getParameter("Age"));
			if(ageString.equals("")) {
				//Set the answer to null
				statAns.setQ1(null);
			} else {
				Integer age = Integer.parseInt(request.getParameter("Age"));
				if((age<18 || age>130)) throw new Exception();
				
				//Set the answer
				statAns.setQ1(age);
			}
		}catch(Exception e) {
			e.printStackTrace();
			//Input non valid, out of range or it is not a string
			error = true;
			ctx.setVariable("ageError", "This input is not valid");
		}
		
		//Read the sex
		try {
			Integer sex = Integer.parseInt(request.getParameter("sex"));
			if(sex<0 || sex>3) throw new Exception();
			
			//Set sex to null if equal to 0
			if(sex == 0) sex=null;
			
			//Set the answer
			statAns.setQ2(sex);
		}catch(Exception e) {
			//Input non valid, out of range or it is not a string
			error = true;
			ctx.setVariable("sexError", "This input is not valid");
		}
		
		//Read the level
		try {
			Integer level = Integer.parseInt(request.getParameter("level"));
			if(level<0 || level>3) throw new Exception();
			
			//Set level to null if equal to 0
			if(level == 0) level=null;
			
			//Set answer
			statAns.setQ3(level);
		}catch(Exception e) {
			//Input non valid, out of range or it is not a string
			error = true;
			ctx.setVariable("expertiseError", "This input is not valid");
		}
		
		//If there is an error load the page with the errors
		if(error) {
			qRespSer.getResponse().setStatisticalAnswers(statAns);
			ctx.setVariable("stat", qRespSer.getResponseStat());
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		//If i want to submit else I want to return to the questionnaire 
		if(StringEscapeUtils.escapeJava(request.getParameter("submit")) != null) {
			try {
				//Submit the questionnaire, that can throw the following two exceptions
				qRespSer.submit(statAns);
				
				//Redirect to the home
				response.sendRedirect(getServletContext().getContextPath() + "/Congratulations");
				
			} catch (InvalidAnswerException e) {
				//marketing answers are mandatory
				//Load the error
				ctx.setVariable("stat", qRespSer.getResponseStat());
				ctx.setVariable("submitError", e.getMessage());
				templateEngine.process(path, ctx, response.getWriter());
				return;
				 
			} catch (ResponseException e) {
				//Bad words
				response.sendRedirect(getServletContext().getContextPath() + "/Logout?ID=7");
			} 
			catch (ApplicationErrorException e) {
				// if there are problems in retrieving the product
				// display a 500 error
				response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
				return;
			}
		}else if(StringEscapeUtils.escapeJava(request.getParameter("marketingQuestions")) != null) {
			//Call the method go to marketing section that will save the statistical answers
			qRespSer.goToMarketingSection(statAns);
			
			//Redirect to the Marketing section
			response.sendRedirect(getServletContext().getContextPath() + "/Questionnaire");
		}
	}
}
