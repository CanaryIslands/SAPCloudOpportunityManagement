package com.eone.crud;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;

public class DBConnection {
	
	public EntityManager createConnection() {

		DataSource ds;
		EntityManagerFactory emf;
		Connection connection = null;

		try {
			InitialContext ctx = new InitialContext();
			ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
			Map properties = new HashMap();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			emf = Persistence.createEntityManagerFactory("sap-cloud-opportunity-management", properties);
			EntityManager em = emf.createEntityManager();
			return em;

		} catch (Exception e) {
		}

		return null;
	}

}
