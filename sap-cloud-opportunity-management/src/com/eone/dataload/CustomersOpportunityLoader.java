package com.eone.dataload;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.persistence.CustomerOpportunity;
import com.eone.loader.FileLoader;

/**
 * Servlet implementation class CustomersOpportunityLoader
 */
@WebServlet("/CustomersOpportunityLoader")
public class CustomersOpportunityLoader extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CustomersOpportunityLoader() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println(loadCustomersOpportunityFromFile());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	private String loadCustomersOpportunityFromFile() throws ServletException {

		DataSource ds;
		EntityManagerFactory emf;
		String message = "<h1>Caricamento Opportunità</h1>";

		FileLoader fl_customersOpportunity = new FileLoader();
		List<String[]> customersOpportunity = fl_customersOpportunity.getFile("/resources/CustomersOpportunity.csv");

		message += "<br><br><h2>Record letti da flat file: " + customersOpportunity.size() + "</h2>";

		Connection connection = null;
		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");

			Map properties = new HashMap();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			emf = Persistence.createEntityManagerFactory("sap-cloud-opportunity-management", properties);
		} catch (NamingException e) {
			throw new ServletException(e);
		}

		EntityManager em = emf.createEntityManager();
		message += "<br>";
		long id = 1;

		try {
			for (String[] line : customersOpportunity) {
				CustomerOpportunity co = new CustomerOpportunity();
				co.setId(id);
				co.setCustID(line[0]);
				co.setOppID(line[1]);
				co.setDate(line[2]);
				co.setStatus(line[3]);
				co.setEstimatedValue(line[4]);

				em.getTransaction().begin();
				em.persist(co);
				em.getTransaction().commit();
				message = message + "<p>Inserita Opportunità-Cliente: " + line[0] + " - " + line[1] + "</p>";
				id += 1;
			}
		} finally {
			em.close();
		}

		return message;

	}

}
