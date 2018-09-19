package com.eone.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.olingo.commons.api.edm.EdmPrimitiveTypeKind;
import org.apache.olingo.commons.api.edm.FullQualifiedName;
import org.apache.olingo.commons.api.edm.provider.CsdlAbstractEdmProvider;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainer;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityContainerInfo;
import org.apache.olingo.commons.api.edm.provider.CsdlEntitySet;
import org.apache.olingo.commons.api.edm.provider.CsdlEntityType;
import org.apache.olingo.commons.api.edm.provider.CsdlProperty;
import org.apache.olingo.commons.api.edm.provider.CsdlPropertyRef;
import org.apache.olingo.commons.api.edm.provider.CsdlSchema;
import org.apache.olingo.commons.api.ex.ODataException;

public class OppMngmtEdmProvider extends CsdlAbstractEdmProvider {

	// Service Namespace
	public static final String NAMESPACE = "OData.OppMngmt";

	// EDM Container
	public static final String CONTAINER_NAME = "Container";
	public static final FullQualifiedName CONTAINER = new FullQualifiedName(NAMESPACE, CONTAINER_NAME);

	// Entity Types Names
	public static final String ET_CUSTOMER_NAME = "Customer";
	public static final FullQualifiedName ET_CUSTOMER_FQN = new FullQualifiedName(NAMESPACE, ET_CUSTOMER_NAME);
	public static final String ET_CUSTOPT_NAME = "CustomerOpportunity";
	public static final FullQualifiedName ET_CUSTOPT_FQN = new FullQualifiedName(NAMESPACE, ET_CUSTOPT_NAME);
	public static final String ET_OPPORTUNITY_NAME = "Opportunity";
	public static final FullQualifiedName ET_OPPORTUNITY_FQN = new FullQualifiedName(NAMESPACE, ET_OPPORTUNITY_NAME);

	// Entity Set Names
	public static final String ES_CUSTOMERS_NAME = "Customers";
	public static final String ES_CUSTOPTS_NAME = "CustomerOpportunities";
	public static final String ES_OPPORTUNITIES_NAME = "Opportunities";

	@Override
	public CsdlEntityContainer getEntityContainer() throws ODataException {

		// create EntitySets
		List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
		entitySets.add(getEntitySet(CONTAINER, ES_CUSTOMERS_NAME));
		entitySets.add(getEntitySet(CONTAINER, ES_CUSTOPTS_NAME));
		entitySets.add(getEntitySet(CONTAINER, ES_OPPORTUNITIES_NAME));

		// create EntitySets for other entity sets here
		// List<CsdlEntitySet> entitySets = new ArrayList<CsdlEntitySet>();
		// entitySets.add(getEntitySet(CONTAINER, ...));

		// create EntityContainer
		CsdlEntityContainer entityContainer = new CsdlEntityContainer();
		entityContainer.setName(CONTAINER_NAME);
		entityContainer.setEntitySets(entitySets);

		return entityContainer;
	}

	@Override
	public CsdlEntityContainerInfo getEntityContainerInfo(FullQualifiedName entityContainerName) throws ODataException {

		// This method is invoked when displaying the Service Document at e.g.
		// http://localhost:8080/DemoService/DemoService.svc
		if (entityContainerName == null || entityContainerName.equals(CONTAINER)) {
			CsdlEntityContainerInfo entityContainerInfo = new CsdlEntityContainerInfo();
			entityContainerInfo.setContainerName(CONTAINER);
			return entityContainerInfo;
		}

		return null;
	}

	@Override
	public CsdlEntitySet getEntitySet(FullQualifiedName entityContainer, String entitySetName) throws ODataException {

		if (entityContainer.equals(CONTAINER)) {
			if (entitySetName.equals(ES_CUSTOMERS_NAME)) {
				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_CUSTOMERS_NAME);
				entitySet.setType(ET_CUSTOMER_FQN);
				return entitySet;
			} else if (entitySetName.equals(ES_CUSTOPTS_NAME)) {
				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_CUSTOPTS_NAME);
				entitySet.setType(ET_CUSTOPT_FQN);
				return entitySet;
			} else if (entitySetName.equals(ES_OPPORTUNITIES_NAME)) {
				CsdlEntitySet entitySet = new CsdlEntitySet();
				entitySet.setName(ES_OPPORTUNITIES_NAME);
				entitySet.setType(ET_OPPORTUNITY_FQN);
				return entitySet;
			}
		}

		return null;
	}

	@Override
	public CsdlEntityType getEntityType(FullQualifiedName entityTypeName) throws ODataException {
		// TODO Auto-generated method stub
		// return super.getEntityType(entityTypeName);

		if (entityTypeName.equals(ET_CUSTOMER_FQN)) {

			// create EntityType properties
			CsdlProperty id = new CsdlProperty().setName("id")
					.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty custID = new CsdlProperty().setName("custID")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty name = new CsdlProperty().setName("name")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName("id");

			// configure EntityType
			CsdlEntityType entityType = new CsdlEntityType();
			entityType.setName(ET_CUSTOMER_NAME);
			entityType.setProperties(Arrays.asList(id, custID, name));
			entityType.setKey(Collections.singletonList(propertyRef));

			return entityType;

		} else if (entityTypeName.equals(ET_CUSTOPT_FQN)) {

			// create EntityType properties
			CsdlProperty id = new CsdlProperty().setName("id")
					.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty custID = new CsdlProperty().setName("custID")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty oppID = new CsdlProperty().setName("oppID")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty oppText = new CsdlProperty().setName("oppText")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty date = new CsdlProperty().setName("date")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty status = new CsdlProperty().setName("status")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			CsdlProperty estimatedValue = new CsdlProperty().setName("estimatedValue")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());

			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName("id");

			// configure EntityType
			CsdlEntityType entityType = new CsdlEntityType();
			entityType.setName(ET_CUSTOPT_NAME);
			entityType.setProperties(Arrays.asList(id, custID, oppID, oppText, date, status, estimatedValue));
			entityType.setKey(Collections.singletonList(propertyRef));

			return entityType;

		} else if (entityTypeName.equals(ET_OPPORTUNITY_FQN)) {

			// create EntityType properties
			CsdlProperty id = new CsdlProperty().setName("id")
					.setType(EdmPrimitiveTypeKind.Int32.getFullQualifiedName());
			CsdlProperty oppText = new CsdlProperty().setName("oppText")
					.setType(EdmPrimitiveTypeKind.String.getFullQualifiedName());
			// create CsdlPropertyRef for Key element
			CsdlPropertyRef propertyRef = new CsdlPropertyRef();
			propertyRef.setName("id");

			// configure EntityType
			CsdlEntityType entityType = new CsdlEntityType();
			entityType.setName(ET_OPPORTUNITY_NAME);
			entityType.setProperties(Arrays.asList(id, oppText));
			entityType.setKey(Collections.singletonList(propertyRef));
			
			return entityType;

		}

		return null;
	}

	@Override
	public List<CsdlSchema> getSchemas() throws ODataException {
		// TODO Auto-generated method stub
		// return super.getSchemas();
		// create Schema
		CsdlSchema schema = new CsdlSchema();
		schema.setNamespace(NAMESPACE);

		// add EntityTypes

		List<CsdlEntityType> entityTypes = new ArrayList<CsdlEntityType>();

		entityTypes.add(getEntityType(ET_CUSTOMER_FQN));
		entityTypes.add(getEntityType(ET_CUSTOPT_FQN));
		entityTypes.add(getEntityType(ET_OPPORTUNITY_FQN));

		/// add here other entity types
		// entityTypes.add(getEntityType(...));

		schema.setEntityTypes(entityTypes);

		// add EntityContainer
		schema.setEntityContainer(getEntityContainer());

		// finally
		List<CsdlSchema> schemas = new ArrayList<CsdlSchema>();
		schemas.add(schema);

		return schemas;
	}

}
