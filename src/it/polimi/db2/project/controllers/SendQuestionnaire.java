package it.polimi.db2.project.controllers;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.db2.project.entities.StatisticalAnswer;
import it.polimi.db2.project.services.QuestionnaireResponseService;

/**
 * Servlet implementation class SendQuestionnaire
 */
@WebServlet("/SendQuestionnaire")
public class SendQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendQuestionnaire() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		//QuestionnaireResponseService qRespSer = (QuestionnaireResponseService) request.getSession().getAttribute("qRespSer");
		
		
		//Get statistical answer from the form
		
		//StatisticalAnswer statistcalAnswer;
		
		//qRespSer.submit(statistcalAnswer);
	//	TODO
	}
}
