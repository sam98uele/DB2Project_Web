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
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	
	/**
	 * UserService EJB
	 */
	@EJB(name = "it.polimi.db2.project.services/UserService")
	private UserService usrService;
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
    }
    
    /**
     * Init method
     * 
     * @see HttpServlet#HttpServlet()
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
	 * On GET method, we need to display the login page
	 * 
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// setting up the variables
    	String path = "login.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		
		try {
			// checking if there is a message to display
			
			Integer ID = Integer.parseInt(request.getParameter("ID"));
			if(ID == 5) {
				//ID=5 session expired
				ctx.setVariable("errorMsg", "Session Expired!"); 
			}
			if(ID == 6) {
				//ID=6 registration ok
				ctx.setVariable("okMessage", "You registered correctly, please login"); 
			}
			
		}catch(Exception e) {
			//Do nothing, because if there is no id an exception is trown
		}
		
		// rendering the page
		templateEngine.process(path, ctx, response.getWriter());
    }

	/**
	 * On POST method we need to perform the Login action
	 * 
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// initializing the variables
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
			// rendering the error page
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", e.getMessage()); 
			String path = "login.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		// Creating the new user object
		User user;
		try {
			// Check the credentials, if correct return the user
			//	else it will throw an exception
			user = usrService.checkCredentialts(usrn, pwd);
		}catch(CredentialsException | ApplicationErrorException e) {
			// if there is an error
			//	an we render the login page with the error
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
			ctx.setVariable("errorMsg", e.getMessage());
			String path = "login.html";
			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		
		// if everithing is ok
		// adding the user object to the session
		request.getSession().setAttribute("user", user);
		
		if(user.isAdmin()) {
			// if the user is admin
			// we create the admin session with the ProductAdminService statefull bean
			ProductAdminService prodAdminSer = null;
			try {
				// Get the Initial Context for the JNDI lookup for a local EJB
				InitialContext ic = new InitialContext();
				// Retrieve the EJB using JNDI lookup
				prodAdminSer = (ProductAdminService) ic.lookup("java:/openejb/local/ProductAdminServiceLocalBean");
			} catch (Exception e) {
				e.printStackTrace();
			}
			// adding the service as an attribute of the session
			request.getSession().setAttribute("prodAdminSer", prodAdminSer);
		}else {
			// if the user is not admin, then it is a user
			// we create the user session with the QuestionnaireResponseService statefull bean
			QuestionnaireResponseService qRespSer = null;
			try {
				// Get the Initial Context for the JNDI lookup for a local EJB
				InitialContext ic = new InitialContext();
				// Retrieve the EJB using JNDI lookup
				qRespSer = (QuestionnaireResponseService) ic.lookup("java:/openejb/local/QuestionnaireResponseServiceLocalBean");
			} catch (Exception e) {
				e.printStackTrace();
			}
			// we add the service to the session
			request.getSession().setAttribute("qRespSer", qRespSer);
		}
		
		// redirect the user to the correct page
		String path;
		if(user.isAdmin()) { // if the user is admin, redirect to the Admin homepage
			path = getServletContext().getContextPath() + "/Admin";
		}
		else { // else the user is a normal user, and redirect him to the Home
			path = getServletContext().getContextPath() + "/Home";
		}
		
		// performin the redirect action
		response.sendRedirect(path);
	}
}
