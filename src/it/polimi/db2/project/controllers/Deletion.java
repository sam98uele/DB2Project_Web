package it.polimi.db2.project.controllers;

import java.io.IOException;

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
	
	@EJB(name = "it.polimi.db2.project.services/QuestionnaireAdminService")
	private QuestionnaireAdminService questAdminSer;
	
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
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//Check the session
		String path = "/WEB-INF/Deletion.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		try {
			Integer ID = Integer.parseInt(request.getParameter("ID"));
			
			String name=  prodSer.getProductById(ID).getName();
				
			questAdminSer.deleteQuestionnaires(ID);	
				
				
			ctx.setVariable("okMessage", "Product " + name + " is been deleted correctly"); 
		}catch(InvalidActionException | ProductException e) {
			ctx.setVariable("errorMsg", e.getMessage()); 
		}catch(Exception e) {
			//Do nothing, because if there is no id an exception is thrown, so there are no product to delete
		}
		
		try {
			ctx.setVariable("pastProd", prodSer.getPastScheduledProductOfTheDay());
		}catch(Exception e) {
			//TODO handle
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
