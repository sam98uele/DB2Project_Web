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
import it.polimi.db2.project.services.ProductService;
import it.polimi.db2.project.services.QuestionnaireAdminService;

/**
 * Servlet implementation class UsersQuestionnaireInspection
 */
@WebServlet("/UsersQuestionnaireInspection")
public class UsersQuestionnaireInspection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;   
	
	@EJB(name = "it.polimi.db2.project.services/QuestionnaireAdminService")
	private QuestionnaireAdminService questAdminSer;
	
	@EJB(name = "it.polimi.db2.project.services/ProductService")
	private ProductService prodSer;
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UsersQuestionnaireInspection() {
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
		String path = "/WEB-INF/UsersQuestionnaireInspection.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		Integer ID = null;
		try {
			ID = Integer.parseInt(request.getParameter("ID")); 
		}catch(Exception e) {
			//Do nothing
		}


		try {
			ctx.setVariable("subUsers", questAdminSer.getListUserCompiledQuestionnaire(ID, true));
			ctx.setVariable("delUsers", questAdminSer.getListUserCompiledQuestionnaire(ID, false));
		}catch(Exception e) {
			//TODO To handle query exception
			e.printStackTrace();
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		String date = sdf.format(prodSer.getProductById(ID).getDate());
		
		try {
			ctx.setVariable("name", prodSer.getProductById(ID).getName());
			ctx.setVariable("date", date);
		}catch(Exception e) {
			e.printStackTrace();
			//TODO handle
		}
		
		ctx.setVariable("IDprod", ID);
		
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
