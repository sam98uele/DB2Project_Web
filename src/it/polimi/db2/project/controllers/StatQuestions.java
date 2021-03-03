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
		QuestionnaireResponseService qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
		
		StatisticalAnswer statAns = new StatisticalAnswer();
		
		try {
			statAns.setQ1(Integer.parseInt(request.getParameter("Age")));
			//TODO exception for age range
		}catch(Exception e) {
			//Age not allowed
		}
		
		try {
			statAns.setQ2(Integer.parseInt(request.getParameter("sex")));
			statAns.setQ3(Integer.parseInt(request.getParameter("level")));
			//TODO that the value are between 0 and 2
		}catch(Exception e) {
			//input non valid
		}
		
		
		if(StringEscapeUtils.escapeJava(request.getParameter("submit")) != null) {
			try {
				qRespSer.submit(statAns);
				
				//Redirect to the home
				response.sendRedirect(getServletContext().getContextPath() + "/Home");
			} catch (Exception e) {
				e.printStackTrace(); //TODO
			}
		}else if(StringEscapeUtils.escapeJava(request.getParameter("marketingQuestions")) != null) {
			qRespSer.goToMarketingSection(statAns);
			
			//Redirect to the Marketing section
			response.sendRedirect(getServletContext().getContextPath() + "/Questionnaire");
		}
	}
}
