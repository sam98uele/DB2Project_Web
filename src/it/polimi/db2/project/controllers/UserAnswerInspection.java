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

import org.apache.commons.lang.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.project.services.QuestionnaireAdminService;

/**
 * Servlet implementation class UserAnswerInspection
 */
@WebServlet("/UserAnswerInspection")
public class UserAnswerInspection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;  
	
	@EJB(name = "it.polimi.db2.project.services/QuestionnaireAdminService")
	private QuestionnaireAdminService questAdminSer;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserAnswerInspection() {
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
		String path = "/WEB-INF/UserAnswerInspection.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		Integer IDuser = null;
		Integer IDprod = null;
		try {
			IDuser = Integer.parseInt(request.getParameter("IDuser"));
			IDprod = Integer.parseInt(request.getParameter("IDprod"));
			
			ctx.setVariable("date", StringEscapeUtils.escapeJava(request.getParameter("date")));
			ctx.setVariable("name", StringEscapeUtils.escapeJava(request.getParameter("name")));
		}catch(Exception e) {
			//Do nothing
		}


		try {
			ctx.setVariable("markAns", questAdminSer.getAllQuestionnaireAnsweredBySpecificUserAndProduct(IDuser, IDprod).getMarketingAnswers());
			ctx.setVariable("statAns", questAdminSer.getAllQuestionnaireAnsweredBySpecificUserAndProduct(IDuser, IDprod).getStatisticalAnswers());
			ctx.setVariable("user", questAdminSer.getAllQuestionnaireAnsweredBySpecificUserAndProduct(IDuser, IDprod).getUser().getUsername());
		}catch(Exception e) {
			//TODO To handle query exception
			e.printStackTrace();
		}
		
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
