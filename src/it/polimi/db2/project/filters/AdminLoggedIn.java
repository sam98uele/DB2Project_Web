package it.polimi.db2.project.filters;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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
 * Servlet Filter implementation class AdminLoggedIn
 */
@WebFilter("/AdminLoggedIn")
public class AdminLoggedIn implements Filter {

    /**
     * Default constructor. 
     */
    public AdminLoggedIn() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub
	}

	/**
	 * @see Filter#doFilter(ServletRequest, ServletResponse, FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		System.out.print("Admin Login checker filter executing ...\n");

		
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String loginPath = req.getServletContext().getContextPath() + "/";
		String userPath = req.getServletContext().getContextPath() + "/Home";
		String logoutPath = req.getServletContext().getContextPath() + "/Logout?ID=5"; //ID=5 session expired
		
		HttpSession session = req.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			res.sendRedirect(loginPath);
			return;
		}
		
		User user = (User) session.getAttribute("user");
		if(!user.isAdmin()) {
			res.sendRedirect(userPath);
			return;
		}
		
		Date creationDate = new Date(session.getCreationTime());
		Date todayDate = Calendar.getInstance().getTime();
		
		if(!DateUtils.isSameDay(creationDate, todayDate)) {
			res.sendRedirect(logoutPath);
			return;
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}

	/**
	 * @see Filter#init(FilterConfig)
	 */
	public void init(FilterConfig fConfig) throws ServletException {
		// TODO Auto-generated method stub
	}

}
