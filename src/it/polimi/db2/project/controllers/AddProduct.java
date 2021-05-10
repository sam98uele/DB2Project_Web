package it.polimi.db2.project.controllers;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.db2.project.exceptions.ApplicationErrorException;
import it.polimi.db2.project.exceptions.InvalidInputArgumentException;
import it.polimi.db2.project.exceptions.PermissionDeniedException;
import it.polimi.db2.project.exceptions.ProductException;
import it.polimi.db2.project.services.ProductAdminService;
import it.polimi.db2.project.utils.ImageUtils;

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
	 * Handle when an admin insert a product to the questionnaire
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String name = null;
		String description = null;
		Date date = null;
		byte[] image = null;
		
		try {
			//Read the name of the product from the form
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			
			//Read the description of the product from the form
			//No backspace allowed
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
			
			//Check if everything is OK, otherwise throw and exception
			if (name == null || description == null || name.isEmpty() || description.isEmpty() || image == null || image.length == 0) {
				throw new Exception("Input error!");
			}
		}catch(Exception e) {
			//e.printStackTrace();
			//Redirect to the Creation page with the error
			response.sendRedirect(getServletContext().getContextPath() + "/Creation?error=" + e.getMessage() + "");
			return;
		}
		
		//Get the productAdminService from the session
		ProductAdminService prodAdminSer = (ProductAdminService) request.getSession().getAttribute("prodAdminSer");
		
		try {
			//Add the product
			prodAdminSer.addProduct(name, image, date, description);
		}catch(ProductException | InvalidInputArgumentException | PermissionDeniedException e){
			//e.printStackTrace();
			//Redirect to the Creation page with the error
			response.sendRedirect(getServletContext().getContextPath() + "/Creation?error=" + e.getMessage() + "");
			return;
		} catch (ApplicationErrorException e) {
			// If an application error occurred, returning 500 error
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
			return;
		}
		
		//All OK redirect to the creation page
		response.sendRedirect(getServletContext().getContextPath() + "/Creation");
	}
}
