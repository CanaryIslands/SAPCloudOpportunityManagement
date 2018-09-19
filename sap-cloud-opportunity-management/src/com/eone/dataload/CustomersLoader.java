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
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.persistence.Customer;

import com.eone.loader.FileLoader;

/**
 * Servlet implementation class CustomersLoader
 */
@WebServlet("/CustomersLoader")
public class CustomersLoader extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public CustomersLoader() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		
		response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println(loadCustmersFromFile());
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

	private String loadCustmersFromFile() throws ServletException {

		DataSource ds;
		EntityManagerFactory emf;
		String message = "<h1>Caricamento Anagrafica Cliente</h1>";

		FileLoader fl_customers = new FileLoader();
		List<String[]> customers = fl_customers.getFile("/resources/Customers.csv");
		
		message += "<br><br><h2>Record letti da flat file: " + customers.size() + "</h2>";

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
			for (String[] line : customers) {
				if (line.length > 2 ){
					Customer bp = new Customer();
					bp.setId(id);
					bp.setCustID(line[0]);
					bp.setName(line[1]);
					bp.setCity(line[2]);
					if (line.length == 4) {
						bp.setAddress(line[3]);
					}
					em.getTransaction().begin();
					em.persist(bp);
					em.getTransaction().commit();
					message = message + "<p>Inserito Cliente: " + line[0] + " - " + line[1] + "</p>";
					id += 1;
					
				}
			}
		} finally {
			em.close();
		}

		return message;
	}

}
