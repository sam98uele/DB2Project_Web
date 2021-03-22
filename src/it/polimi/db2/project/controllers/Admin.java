package it.polimi.db2.project.controllers;

import java.io.IOException;

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

/**
 * Servlet implementation class Admin
 */
@WebServlet("/Admin")
public class Admin extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
       
    /**
     * Default constructor 
     * 
     * @see HttpServlet#HttpServlet()
     */
    public Admin() {
        
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
    	// we render the page
		String path = "/WEB-INF/Admin.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		try {
			// if there is a message, then rendering it
			String insertionMessage = request.getParameter("insertionMessage");
			ctx.setVariable("insertionMessage", insertionMessage);
		}catch(Exception e) {
			// if there is not a message
			// an exception is thrown
			// no message thrown
			ctx.setVariable("insertionMessage", "");
		}
		
		// rendering the page
		templateEngine.process(path, ctx, response.getWriter());
	}
}
