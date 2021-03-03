package it.polimi.db2.project.controllers;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.persistence.indirection.IndirectList;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.project.entities.MarketingAnswer;
import it.polimi.db2.project.entities.User;
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
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		QuestionnaireResponseService qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpSession session = req.getSession();
		User user = (User) session.getAttribute("user");
		
		try {
			qRespSer.startQuestionnaire(user);
		} catch (NoProductOfTheDayException | InvalidActionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String path = "/WEB-INF/Questionnaire.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());		
		ctx.setVariable("product", qRespSer.getProduct());
		ctx.setVariable("response", qRespSer.getResponse());
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		QuestionnaireResponseService qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
		
		List<MarketingAnswer> marketingAnswers = new IndirectList<>();
		int numQuestions = qRespSer.getProduct().getMarketingQuestions().size();
		for(Integer i = 0; i<numQuestions; i++) {
			MarketingAnswer questResp = new MarketingAnswer(); 			
			questResp.setAnswer(StringEscapeUtils.escapeJava(request.getParameter("quest" + i.toString())));
			marketingAnswers.add(questResp);
			questResp.setQuestion(qRespSer.getProduct().getMarketingQuestions().get(i));
		}
		
		qRespSer.goToStatisticalSection(marketingAnswers);
		
		System.out.println(qRespSer.getResponse().getResponseMarketingById(0));
		System.out.println(qRespSer.getResponse().getResponseMarketingById(1));
		System.out.println(qRespSer.getResponse().getResponseMarketingById(2));
		
		response.sendRedirect(getServletContext().getContextPath() + "/StatQuestions");
	}
}
