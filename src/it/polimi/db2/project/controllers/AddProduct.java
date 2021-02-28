package it.polimi.db2.project.controllers;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;
import javax.servlet.http.Part;

import it.polimi.db2.project.exceptions.InvalidInputArgumentException;
import it.polimi.db2.project.exceptions.PermissionDeniedException;
import it.polimi.db2.project.exceptions.ProductException;
import it.polimi.db2.project.services.ProductAdminService;
import it.polimi.db2.project.utils.*;

/**
 * Servlet implementation class AddProduct
 */
@WebServlet("/AddProduct")
@MultipartConfig
public class AddProduct extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddProduct() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//TODO: check if is login and is admin, otherwise return or to the login page, or to the home page of a standard user.
		String name = null;
		String description = null;
		Date date = null;
		byte[] image = null;
		
		try {
			//Read the name of the product from the form
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			
			//Read the description of the product from the form
			description = StringEscapeUtils.escapeJava(request.getParameter("description"));
			
			//Read the date
			try {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				date = (Date) sdf.parse(request.getParameter("date"));
			}catch(Exception e) {
				throw new Exception("Input error!");
			}
			
			//Read the image and transform the image in a string of byte using readImage in it.polimi.db2.project.utils
			Part imgFile = request.getPart("image");
			InputStream imgContent = imgFile.getInputStream();
			image = ImageUtils.readImage(imgContent);
			
			//Check if everything is ok, otherwise throw and exception
			if (name == null || description == null || name.isEmpty() || description.isEmpty() || image == null || image.length == 0) {
				throw new Exception("Input error!");
			}
		}catch(Exception e) {
			e.printStackTrace();
			response.sendRedirect(getServletContext().getContextPath() + "/Creation?error=" + e.getMessage() + "");
			return;
		}
		
		ProductAdminService prodAdminSer = (ProductAdminService) request.getSession().getAttribute("prodAdminSer");
		
		try {
			//Add the product
			prodAdminSer.addProduct(name, image, date, description);
		}catch(ProductException | InvalidInputArgumentException | PermissionDeniedException e){
			e.printStackTrace();
			response.sendRedirect(getServletContext().getContextPath() + "/Creation?error=" + e.getMessage() + "");
			return;
		}
		
		response.sendRedirect(getServletContext().getContextPath() + "/Creation");
	}
}
