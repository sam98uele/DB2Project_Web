package it.polimi.db2.project.controllers;

import java.io.IOException;
import javax.ejb.EJB;
import javax.naming.InitialContext;
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

import it.polimi.db2.project.services.ProductAdminService;
import it.polimi.db2.project.services.QuestionnaireResponseService;
import it.polimi.db2.project.services.UserService;
import it.polimi.db2.project.entities.User;
import it.polimi.db2.project.exceptions.ApplicationErrorException;
import it.polimi.db2.project.exceptions.CredentialsException;



/**
 * Servlet implementation class Registration
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "it.polimi.db2.project.services/UserService")
	private UserService usrService;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
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
    	String path = "login.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String usrn = null;
		String pwd = null;
		
		//Check if there is an empty field
		try {
			usrn = StringEscapeUtils.escapeJava(request.getParameter("username"));
			pwd = StringEscapeUtils.escapeJava(request.getParameter("password"));
			if (usrn == null || pwd == null || usrn.isEmpty() || pwd.isEmpty()) {
				throw new Exception("Missing or empty credential value");
			}
		}catch(Exception e) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", e.getMessage()); 
			String path = "login.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		User user;
		try {
			//Check the credentials, if correct return the user, else null
			user = usrService.checkCredentialts(usrn, pwd);
		}catch(CredentialsException | ApplicationErrorException e) {
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", e.getMessage());
			String path = "login.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		request.getSession().setAttribute("user", user);
		
		if(user.isAdmin()) {
			ProductAdminService prodAdminSer = null;
			try {
				// Get the Initial Context for the JNDI lookup for a local EJB
				InitialContext ic = new InitialContext();
				// Retrieve the EJB using JNDI lookup
				prodAdminSer = (ProductAdminService) ic.lookup("java:/openejb/local/ProductAdminServiceLocalBean");
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.getSession().setAttribute("prodAdminSer", prodAdminSer);
		}else {
			QuestionnaireResponseService qRespSer = null;
			try {
				// Get the Initial Context for the JNDI lookup for a local EJB
				InitialContext ic = new InitialContext();
				// Retrieve the EJB using JNDI lookup
				qRespSer = (QuestionnaireResponseService) ic.lookup("java:/openejb/local/QuestionnaireResponseServiceLocalBean");
			} catch (Exception e) {
				e.printStackTrace();
			}
			request.getSession().setAttribute("qRespSer", qRespSer);
		}
		
		String path;
		if(user.isAdmin()) {
			path = getServletContext().getContextPath() + "/Admin";
		}else {
			path = getServletContext().getContextPath() + "/Home";
		}
		response.sendRedirect(path);
	}
}
