package it.polimi.db2.project.filters;

import java.io.IOException;

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

import it.polimi.db2.project.entities.User;

/**
 * Servlet Filter implementation class LoginAndRegistration
 */
@WebFilter("/LoginAndRegistration")
public class LoginAndRegistration implements Filter {

    /**
     * Default constructor. 
     */
    public LoginAndRegistration() {
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
		//Check if the user is loggedIn and redirect to the respective home page
		
		// initializations
				HttpServletRequest req = (HttpServletRequest) request;
				HttpServletResponse res = (HttpServletResponse) response;
				String userPath = req.getServletContext().getContextPath() + "/Home";
				String adminPath = req.getServletContext().getContextPath() + "/Home";
				
				HttpSession session = req.getSession();
				// checking the session is valid
				if (!session.isNew() && session.getAttribute("user") != null) {
					//Load the username from the session
					User user = (User) session.getAttribute("user");
					
					//Check if is an admin or not
					if(user.isAdmin()) {
						//Redirect to the admin home page if he is an admin
						res.sendRedirect(adminPath); 
					} else {
						//Redirect to the user home page if he is not an admin
						res.sendRedirect(userPath); 
					}	
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
