package it.polimi.db2.project.filters;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.time.DateUtils;

import it.polimi.db2.project.entities.User;

/**
 * Servlet Filter implementation class UserLoggedIn
 */
@WebFilter("/UserLoggedIn")
public class UserLoggedIn implements Filter {

    /**
     * Default constructor. 
     */
    public UserLoggedIn() {
        // Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		// System.out.print("User Login checker filter executing ...\n");

		// initializing variables
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginPath = req.getServletContext().getContextPath() + "/";
		String adminPath = req.getServletContext().getContextPath() + "/Admin";
		String logoutPath = req.getServletContext().getContextPath() + "/Logout?ID=5";//ID=5 session expired
		
		HttpSession session = req.getSession();
		// if the session is valid
		if (session.isNew() || session.getAttribute("user") == null) {
			res.sendRedirect(loginPath); // if it is not a valid session, redirect to the login page
			return;
		}
		
		User user = (User) session.getAttribute("user");
		// checking if he is admin
		if(user.isAdmin()) {
			res.sendRedirect(adminPath); // if the user is admin, redirect him to his homepage
			return;
		}
		
		Date creationDate = new Date(session.getCreationTime());
		Date todayDate = Calendar.getInstance().getTime();
		
		// this will destroy the session at the end of the day 
		if(!DateUtils.isSameDay(creationDate, todayDate)) {
			res.sendRedirect(logoutPath); // executing logout
			return;
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// Auto-generated method stub
	}

}
