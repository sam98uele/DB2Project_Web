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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import it.polimi.db2.project.services.ProductService;

/**
 * Servlet implementation class Inspection
 */
@WebServlet("/Inspection")
public class Inspection extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	/**
	 * ProductService EJB
	 * To manage the product
	 */
	@EJB(name = "it.polimi.db2.project.services/ProductService")
	private ProductService prodSer;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Inspection() {
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
		// Load the page 
		String path = "/WEB-INF/Inspection.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		//Set the past products
		try {
			ctx.setVariable("pastProd", prodSer.getPastScheduledProductOfTheDay());
			ctx.setVariable("formatter", new SimpleDateFormat("dd/MM/yyyy"));
		}catch(Exception e) {
			// if there are problems in retrieving the product
			// display a 500 error
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		templateEngine.process(path, ctx, response.getWriter());
	}
}
