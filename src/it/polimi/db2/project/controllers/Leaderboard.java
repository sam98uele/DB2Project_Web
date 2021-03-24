package it.polimi.db2.project.controllers;

import java.io.IOException;
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

import it.polimi.db2.project.entities.User;
import it.polimi.db2.project.exceptions.ApplicationErrorException;
import it.polimi.db2.project.services.UserService;

/**
 * Servlet implementation class Leaderboard
 */
@WebServlet("/Leaderboard")
public class Leaderboard extends HttpServlet {
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
    public Leaderboard() {
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
     * It will display the leaderboard
     * 
     *  @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// getting the leaderboard from the Backend
		List<User> leaderboard;
		
		try {
			leaderboard = this.usrService.getLeaderboard();
		} catch (ApplicationErrorException e) {
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		// rendering the page
		String path = "/WEB-INF/Leaderboard.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("pathHome", getServletContext().getContextPath() + "/Home");
		ctx.setVariable("leaderboard", leaderboard);
		templateEngine.process(path, ctx, response.getWriter());
	}

}
