package com.eone.crud;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.sql.DataSource;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.apache.olingo.server.api.uri.UriParameter;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.persistence.Customer;

public class CustomerCRUD {

	private EntityManager getConnection() {
		
		DBConnection dbConn = new DBConnection();
		return dbConn.createConnection();
		
//		This code snippets is now moved into DBConnection Class

//		DataSource ds;
//		EntityManagerFactory emf;
//		Connection connection = null;
//
//		try {
//			InitialContext ctx = new InitialContext();
//			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
//			Map properties = new HashMap();
//			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
//			emf = Persistence.createEntityManagerFactory("sap-cloud-opportunity-management", properties);
//			EntityManager em = emf.createEntityManager();
//			return em;
//
//		} catch (Exception e) {
//		}
//
//		return null;
	}

	public List<Entity> getEntyties() {

		EntityCollection customersCollection = new EntityCollection();
		List<Entity> customerList = customersCollection.getEntities();
		EntityManager em = this.getConnection();
		em.getTransaction().begin();
		Query q = em.createQuery("SELECT c FROM Customer c", Customer.class);
		List<Customer> cl = q.getResultList();

		for (Customer customer : cl) {
			final Entity e = new Entity().addProperty(new Property(null, "id", ValueType.PRIMITIVE, customer.getId()))
					.addProperty(new Property(null, "custID", ValueType.PRIMITIVE, customer.getCustID()))
					.addProperty(new Property(null, "name", ValueType.PRIMITIVE, customer.getName()));
			e.setId(createId("Customers", customer.getId()));
			customerList.add(e);
		}
		
		em.close();

		return customerList;
	}
	
	public Entity getEntity(List<UriParameter> keyPredicates) {
		
		Entity e = null;
		EntityManager em = this.getConnection();
		em.getTransaction().begin();
		
		for(UriParameter key: keyPredicates) {
			if(key.getName().equals("id")){
				// To get customer data you can use the method "find" instead of the method "createQuery"
				// See CustomerOpportunityCRUD.getEntity() and CustomerOpportunityCRUD.updateEntity() as an example
				Query q = em.createQuery("SELECT c FROM Customer c WHERE c.id=:id", Customer.class);
				List<Customer> cl = q.setParameter("id", Integer.parseInt(key.getText())).getResultList();
				for (Customer customer : cl) {
					 e = new Entity().addProperty(new Property(null, "id", ValueType.PRIMITIVE, Integer.parseInt(key.getText())))
							.addProperty(new Property(null, "custID", ValueType.PRIMITIVE, customer.getCustID()))
							.addProperty(new Property(null, "name", ValueType.PRIMITIVE, customer.getName()));
					e.setId(createId("Customers", key.getText()));
				}
				
				em.close();
			}
		}
		
		return e;
		
	}

	private URI createId(String entitySetName, Object id) {
		try {
			return new URI(entitySetName + "(" + String.valueOf(id) + ")");
		} catch (URISyntaxException e) {
			throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
		}
	}

}
