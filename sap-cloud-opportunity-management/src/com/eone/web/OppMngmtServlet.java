package com.eone.web;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.olingo.commons.api.edmx.EdmxReference;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataHttpHandler;
import org.apache.olingo.server.api.ServiceMetadata;

import com.eone.services.OppMngmtCollectionProcessor;
import com.eone.services.OppMngmtEdmProvider;
import com.eone.services.OppMngmtEntityProcessor;

/**
 * Servlet implementation class CustomerServlet
 */
@WebServlet("/OppMngmtServlet")
public class OppMngmtServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OppMngmtServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#service(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		 try {
		      // create odata handler and configure it with CsdlEdmProvider and Processor
		      OData odata = OData.newInstance();
		      ServiceMetadata edm = odata.createServiceMetadata(new OppMngmtEdmProvider(), new ArrayList<EdmxReference>());
		      ODataHttpHandler handler = odata.createHandler(edm);
		      handler.register(new OppMngmtCollectionProcessor());
		      handler.register(new OppMngmtEntityProcessor());

		      // let the handler do the work
		      handler.process(request, response);
		    } catch (RuntimeException e) {
		    	throw new ServletException(e);
		    }
	}

}
