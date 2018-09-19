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
import org.apache.olingo.server.api.uri.UriParameter;
import org.persistence.Customer;
import org.persistence.CustomerOpportunity;
import org.persistence.Opportunity;

public class CustomerOpportunityCRUD {
	
	private EntityManager getConnection() {
		DBConnection dbConn = new DBConnection();
		return dbConn.createConnection();
	}
	
	public List<Entity> getEntyties() {
		
		String oppText = null;

		EntityCollection opportunitiesCollection = new EntityCollection();
		List<Entity> opportunityList = opportunitiesCollection.getEntities();
		EntityManager em = this.getConnection();
		em.getTransaction().begin();
		Query q = em.createQuery("SELECT co FROM CustomerOpportunity co", CustomerOpportunity.class);
		List<CustomerOpportunity> co = q.getResultList();
		q = em.createQuery("SELECT op FROM Opportunity op", Opportunity.class);
		List<Opportunity> op = q.getResultList();

		for (CustomerOpportunity opportunities : co) {
			
			for (Opportunity opportunity : op) {
				if (opportunity.getId() == Long.parseLong(opportunities.getOppID())) {
					oppText = opportunity.getOppText();
					break;
				}
			}
			
			final Entity e = new Entity().addProperty(new Property(null, "id", ValueType.PRIMITIVE, opportunities.getId()))
					.addProperty(new Property(null, "custID", ValueType.PRIMITIVE, opportunities.getCustID()))
					.addProperty(new Property(null, "oppID", ValueType.PRIMITIVE, opportunities.getOppID()))
					.addProperty(new Property(null, "oppText", ValueType.PRIMITIVE, oppText))
					.addProperty(new Property(null, "date", ValueType.PRIMITIVE, opportunities.getDate()))
					.addProperty(new Property(null, "status", ValueType.PRIMITIVE, opportunities.getStatus()))
					.addProperty(new Property(null, "estimatedValue", ValueType.PRIMITIVE, opportunities.getEstimatedValue()));
			e.setId(createId("Opportunity", opportunities.getId()));
			opportunityList.add(e);
		}
		
		em.close();

		return opportunityList;
	}
	
	public long createEntity(String custID, String oppID, String date, String status, String estimatedValue) {
		
		EntityManager em = this.getConnection();
		
		em.getTransaction().begin();
		
		Query q = em.createQuery("SELECT max(co.id) FROM CustomerOpportunity co", CustomerOpportunity.class);
		long maxId = (Long)q.getSingleResult();
		maxId+=1;
		
		CustomerOpportunity co = new CustomerOpportunity();
		co.setId(maxId);
		co.setCustID(custID);
		co.setOppID(oppID);
		co.setDate(date);
		co.setStatus(status);
		co.setEstimatedValue(estimatedValue);
		
		em.persist(co);
		em.getTransaction().commit();
		
		return maxId;
		
	}
	
	public void updateEntity(List<UriParameter> keyPredicates, Entity requestEntity) {
		
		String status                           = null;
		String estimatedValue                   = null;
		CustomerOpportunity custumerOpportunity = null;
		EntityManager em                        = this.getConnection();
		
		em.getTransaction().begin();
		
		for(UriParameter key: keyPredicates) {
			if(key.getName().equals("id")){
				custumerOpportunity = em.find(CustomerOpportunity.class, Long.parseLong(key.getText()));
			}
			
		}
		
		List<Property> entityPropertiesList= requestEntity.getProperties();
		
		for (Property p: entityPropertiesList) {
			if (p.getName().toString().equals("status")) {
				status = p.getValue().toString(); 
			} else if (p.getName().toString().equals("estimatedValue")){
				estimatedValue = p.getValue().toString();
			}
		}
		
		custumerOpportunity.setStatus(status);
		custumerOpportunity.setEstimatedValue(estimatedValue);
		
		em.getTransaction().commit();
		em.close();
		
	}
	
	public Entity getEntity(List<UriParameter> keyPredicates) {
		
		CustomerOpportunity customerOpportunity = null;
		Opportunity opportunity                 = null;
		Entity e                                = null;
        EntityManager em                        = this.getConnection();
		
		em.getTransaction().begin();
		
		for(UriParameter key: keyPredicates) {
			if(key.getName().equals("id")){
				customerOpportunity = em.find(CustomerOpportunity.class, Long.parseLong(key.getText()));
				opportunity         = em.find(Opportunity.class, Long.parseLong(customerOpportunity.getOppID()));
				e = new Entity().addProperty(new Property(null, "id", ValueType.PRIMITIVE, customerOpportunity.getId()))
						.addProperty(new Property(null, "custID", ValueType.PRIMITIVE, customerOpportunity.getCustID()))
						.addProperty(new Property(null, "oppID", ValueType.PRIMITIVE, customerOpportunity.getOppID()))
						.addProperty(new Property(null, "oppText", ValueType.PRIMITIVE, opportunity.getOppText()))
				        .addProperty(new Property(null, "date", ValueType.PRIMITIVE, customerOpportunity.getDate()))
				        .addProperty(new Property(null, "status", ValueType.PRIMITIVE, customerOpportunity.getStatus()))
				        .addProperty(new Property(null, "estimatedValue", ValueType.PRIMITIVE, customerOpportunity.getEstimatedValue()));
				e.setId(createId("CustomerOpportunity", key.getText()));
				return e;
			}
			
		}
		return null;
	}

	private URI createId(String entitySetName, Object id) {
		try {
			return new URI(entitySetName + "(" + String.valueOf(id) + ")");
		} catch (URISyntaxException e) {
			throw new ODataRuntimeException("Unable to create id for entity: " + entitySetName, e);
		}
	}

}
