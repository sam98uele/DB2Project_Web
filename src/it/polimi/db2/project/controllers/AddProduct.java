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
	@EJB(name = "it.polimi.db2.project.services/ProductAdminService")
	private ProductAdminService prodAdminSer;
       
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
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			description = StringEscapeUtils.escapeJava(request.getParameter("description"));
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			date = (Date) sdf.parse(request.getParameter("date"));
			
			Part imgFile = request.getPart("image");
			InputStream imgContent = imgFile.getInputStream();
			image = ImageUtils.readImage(imgContent);
			
			if (name == null || description == null || name.isEmpty() || description.isEmpty() || image == null || image.length == 0) {
				throw new Exception("Error in the input!!");
			}
		}catch(Exception e) {
			System.out.print(e);

			//TODO handle
		}
		
		try {
			prodAdminSer.addProduct(name, image, date, description);
		}catch(ProductException | InvalidInputArgumentException | PermissionDeniedException e){
			//TODO: handle
		}
		
		prodAdminSer.saveProduct();
		
		response.sendRedirect(getServletContext().getContextPath() + "/Home");
		//TODO hande the form and so on...
	}
}
