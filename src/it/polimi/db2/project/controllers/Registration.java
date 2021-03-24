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

import it.polimi.db2.project.exceptions.RegistrationException;
import it.polimi.db2.project.services.UserService;

//import it.polimi.db2.project.services.UserService;


/**
 * Servlet implementation class Registration
 */
@WebServlet("/Registration")
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	/**
	 * UserService EJB
	 * That manages all the actions connected to the User management
	 */
	@EJB(name = "it.polimi.db2.project.services/UserService")
	private UserService usrService;
	
       
    /**
     * Default constructor
     * 
     * @see HttpServlet#HttpServlet()
     */
    public Registration() {
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
	 * Display the registration page
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String path = "/WEB-INF/Registration.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
	}

	/**
	 * Process the registration
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// initializing the variables
		String usrn = null;
		String pwd = null;
		String email = null;
		
		//Check if there is an empty field
		try {
			usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
			pwd = StringEscapeUtils.escapeJava(request.getParameter("password"));
			email = StringEscapeUtils.escapeJava(request.getParameter("email"));
			if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty() || email == null || email.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}
		}catch(Exception e) {
			// if there are some problems, we render the error page
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", e.getMessage()); 
			String path = "/WEB-INF/Registration.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		//Registration
		try {
			// we perform the registration in the user service
			usrService.registration(usrn, email, pwd);
		}catch(RegistrationException e) {
			// if there are problems, render the error page
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", e.getMessage()); 
			String path = "/WEB-INF/Registration.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		//Redirect to the login page
		response.sendRedirect(getServletContext().getContextPath() + "/Login?ID=6");
	}
}
