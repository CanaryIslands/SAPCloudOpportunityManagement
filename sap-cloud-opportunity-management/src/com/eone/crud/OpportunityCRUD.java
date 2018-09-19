package com.eone.crud;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.ex.ODataRuntimeException;
import org.persistence.Opportunity;

public class OpportunityCRUD {

	private EntityManager getConnection() {

		DBConnection dbConn = new DBConnection();
		return dbConn.createConnection();

	}

	public List<Entity> getEntyties() {

		EntityCollection opportunityCollection = new EntityCollection();
		List<Entity> opportunityList = opportunityCollection.getEntities();
		EntityManager em = this.getConnection();
		em.getTransaction().begin();
		Query q = em.createQuery("SELECT op FROM Opportunity op", Opportunity.class);
		List<Opportunity> op = q.getResultList();

		for (Opportunity opportunity : op) {
			final Entity e = new Entity().addProperty(new Property(null, "id", ValueType.PRIMITIVE, opportunity.getId()))
					.addProperty(new Property(null, "oppText", ValueType.PRIMITIVE, opportunity.getOppText()));
			e.setId(createId("Opportunities", opportunity.getId()));
			opportunityList.add(e);
		}

		em.close();

		return opportunityList;
	}
	
	private URI createId(String entitySetName, Object id) {
		try {
			return new URI(entitySetName + "(" + String.valueOf(id) + ")");
		} catch (URISyntaxException e) {
			throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
		}
	}

}
