package com.eone.services;

import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.EntityCollection;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityCollectionProcessor;
import org.apache.olingo.server.api.serializer.EntityCollectionSerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;
import org.apache.olingo.server.api.uri.queryoption.FilterOption;
import org.apache.olingo.server.api.uri.queryoption.expression.Expression;
import org.apache.olingo.server.api.uri.queryoption.expression.ExpressionVisitException;

import com.eone.crud.CustomerCRUD;
import com.eone.crud.CustomerOpportunityCRUD;
import com.eone.crud.OpportunityCRUD;

public class OppMngmtCollectionProcessor implements EntityCollectionProcessor {
	
	private OData odata;
	private ServiceMetadata serviceMetadata;

	@Override
	public void init(OData arg0, ServiceMetadata arg1) {
		this.odata = arg0;
		this.serviceMetadata = arg1;
	}

	@Override
	public void readEntityCollection(ODataRequest arg0, ODataResponse arg1, UriInfo arg2, ContentType arg3)
			throws ODataApplicationException, ODataLibraryException {
		
		// 1st we have retrieve the requested EntitySet from the arg2 object (representation of the parsed service URI)
		  List<UriResource> resourcePaths = arg2.getUriResourceParts();
		  UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0); // in our example, the first segment is the EntitySet
		  EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

		  // 2nd: fetch the data from backend for this requested EntitySetName
		  // it has to be delivered as EntitySet object
		  EntityCollection entitySet = getData(edmEntitySet);
		  
		  FilterOption filterOption = arg2.getFilterOption();
		  if(filterOption != null) {
			  
			  Expression filterExpression = filterOption.getExpression();
			  
			  try {
				  List<Entity> entityList = entitySet.getEntities();
				  Iterator<Entity> entityIterator = entityList.iterator();

				  // Evaluate the expression for each entity
				  // If the expression is evaluated to "true", keep the entity otherwise remove it from
				  // the entityList
				  while (entityIterator.hasNext()) {
				    // To evaluate the the expression, create an instance of the Filter Expression
				    // Visitor and pass the current entity to the constructor
				    Entity currentEntity = entityIterator.next();
				    FilterExpressionVisitor expressionVisitor = new FilterExpressionVisitor(currentEntity);

				    // Evaluating the expression
				    Object visitorResult = filterExpression.accept(expressionVisitor);
				 // The result of the filter expression must be of type Edm.Boolean
				     if(visitorResult instanceof Boolean) {
				        if(!Boolean.TRUE.equals(visitorResult)) {
				          // The expression evaluated to false (or null), so we have to remove the
				          // currentEntity from entityList
				          entityIterator.remove();
				        }
				     } else {
				         throw new ODataApplicationException("A filter expression must evaulate to type Edm.Boolean", HttpStatusCode.BAD_REQUEST.getStatusCode(), Locale.ENGLISH);
				     }
				  } // End while
				} catch (ExpressionVisitException e) {
				   throw new ODataApplicationException("Exception in filter evaluation",
				                 HttpStatusCode.INTERNAL_SERVER_ERROR.getStatusCode(), Locale.ENGLISH);
				}
		  }
		  
		  // 3rd: create a serializer based on the requested format (json)
		  ODataSerializer serializer = odata.createSerializer(arg3);

		  // 4th: Now serialize the content: transform from the EntitySet object to InputStream
		  EdmEntityType edmEntityType = edmEntitySet.getEntityType();
		  ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();

		  final String id = arg0.getRawBaseUri() + "/" + edmEntitySet.getName();
		  EntityCollectionSerializerOptions opts = EntityCollectionSerializerOptions.with().id(id).contextURL(contextUrl).build();
		  SerializerResult serializerResult = serializer.entityCollection(serviceMetadata, edmEntityType, entitySet, opts);
		  InputStream serializedContent = serializerResult.getContent();

		  // Finally: configure the response object: set the body, headers and status code
		  arg1.setContent(serializedContent);
		  arg1.setStatusCode(HttpStatusCode.OK.getStatusCode());
		  arg1.setHeader(HttpHeader.CONTENT_TYPE, arg3.toContentTypeString());

	}
	
	private EntityCollection getData(EdmEntitySet edmEntitySet) {
		
		switch (edmEntitySet.getName()) {
		case OppMngmtEdmProvider.ES_CUSTOMERS_NAME:
			EntityCollection customersCollection = new EntityCollection();
			CustomerCRUD cCRUD = new CustomerCRUD();
		    List<Entity> customerList = customersCollection.getEntities();
		    customerList.addAll(cCRUD.getEntyties());
		    return customersCollection;
		case OppMngmtEdmProvider.ES_CUSTOPTS_NAME:
			EntityCollection customersOptsCollection = new EntityCollection();
			CustomerOpportunityCRUD coCRUD = new CustomerOpportunityCRUD();
		    List<Entity> customerOptsList = customersOptsCollection.getEntities();
		    customerOptsList.addAll(coCRUD.getEntyties());
		    return customersOptsCollection;
		case OppMngmtEdmProvider.ES_OPPORTUNITIES_NAME:
			EntityCollection opportunitiesCollection = new EntityCollection();
			OpportunityCRUD opCRUD = new OpportunityCRUD();
		    List<Entity> opportunityList = opportunitiesCollection.getEntities();
		    opportunityList.addAll(opCRUD.getEntyties());
		    return opportunitiesCollection;
		default:
			return null;
		}
				
	}

}
