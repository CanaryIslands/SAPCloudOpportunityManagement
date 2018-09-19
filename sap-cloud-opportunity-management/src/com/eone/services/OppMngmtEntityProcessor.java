package com.eone.services;

import java.io.InputStream;
import java.util.List;

import org.apache.olingo.commons.api.data.ContextURL;
import org.apache.olingo.commons.api.data.Entity;
import org.apache.olingo.commons.api.data.Property;
import org.apache.olingo.commons.api.data.ValueType;
import org.apache.olingo.commons.api.edm.EdmEntitySet;
import org.apache.olingo.commons.api.edm.EdmEntityType;
import org.apache.olingo.commons.api.format.ContentType;
import org.apache.olingo.commons.api.http.HttpHeader;
import org.apache.olingo.commons.api.http.HttpMethod;
import org.apache.olingo.commons.api.http.HttpStatusCode;
import org.apache.olingo.server.api.OData;
import org.apache.olingo.server.api.ODataApplicationException;
import org.apache.olingo.server.api.ODataLibraryException;
import org.apache.olingo.server.api.ODataRequest;
import org.apache.olingo.server.api.ODataResponse;
import org.apache.olingo.server.api.ServiceMetadata;
import org.apache.olingo.server.api.processor.EntityProcessor;
import org.apache.olingo.server.api.serializer.EntitySerializerOptions;
import org.apache.olingo.server.api.serializer.ODataSerializer;
import org.apache.olingo.server.api.deserializer.DeserializerResult;
import org.apache.olingo.server.api.deserializer.ODataDeserializer;
import org.apache.olingo.server.api.serializer.SerializerResult;
import org.apache.olingo.server.api.uri.UriInfo;
import org.apache.olingo.server.api.uri.UriParameter;
import org.apache.olingo.server.api.uri.UriResource;
import org.apache.olingo.server.api.uri.UriResourceEntitySet;

import com.eone.crud.CustomerCRUD;
import com.eone.crud.CustomerOpportunityCRUD;
import com.eone.util.Util;

public class OppMngmtEntityProcessor implements EntityProcessor {

	private OData odata;
	private ServiceMetadata serviceMetadata;

	@Override
	public void init(OData arg0, ServiceMetadata arg1) {

		this.odata = arg0;
		this.serviceMetadata = arg1;

	}

	@Override
	public void createEntity(ODataRequest arg0, ODataResponse arg1, UriInfo arg2, ContentType requestFormat,
			ContentType responseFormat) throws ODataApplicationException, ODataLibraryException {

		// 1. Retrieve the entity type from the URI
		EdmEntitySet edmEntitySet = Util.getEdmEntitySet(arg2);
		EdmEntityType edmEntityType = edmEntitySet.getEntityType();

		// 2. create the data in backend
		// 2.1. retrieve the payload from the POST request for the entity to create and
		// deserialize it
		InputStream requestInputStream = arg0.getBody();
		ODataDeserializer deserializer = this.odata.createDeserializer(requestFormat);
		DeserializerResult result = deserializer.entity(requestInputStream, edmEntityType);
		Entity requestEntity = result.getEntity();

		// 2.2 do the creation in backend, which returns the newly created entity
		Entity createdEntity = putData(edmEntitySet, requestEntity);

		// 3. serialize the response (we have to return the created entity)
		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
		EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build(); // expand and
																											// select
																											// currently
																											// not
																											// supported

		ODataSerializer serializer = this.odata.createSerializer(responseFormat);
		SerializerResult serializedResponse = serializer.entity(serviceMetadata, edmEntityType, createdEntity, options);

		// 4. configure the response object
		arg1.setContent(serializedResponse.getContent());
		arg1.setStatusCode(HttpStatusCode.CREATED.getStatusCode());
		arg1.setHeader(HttpHeader.CONTENT_TYPE, responseFormat.toContentTypeString());

	}

	@Override
	public void deleteEntity(ODataRequest arg0, ODataResponse arg1, UriInfo arg2)
			throws ODataApplicationException, ODataLibraryException {
		// TODO Auto-generated method stub

	}

	@Override
	public void readEntity(ODataRequest arg0, ODataResponse arg1, UriInfo arg2, ContentType arg3)
			throws ODataApplicationException, ODataLibraryException {

		// 1. retrieve the Entity Type
		List<UriResource> resourcePaths = arg2.getUriResourceParts();
		// Note: only in our example we can assume that the first segment is the
		// EntitySet
		UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
		EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();

		// 2. retrieve the data from backend
		List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
		Entity entity = this.getData(edmEntitySet, keyPredicates);

		// 3. serialize
		EdmEntityType entityType = edmEntitySet.getEntityType();

		ContextURL contextUrl = ContextURL.with().entitySet(edmEntitySet).build();
		// expand and select currently not supported
		EntitySerializerOptions options = EntitySerializerOptions.with().contextURL(contextUrl).build();

		ODataSerializer serializer = odata.createSerializer(arg3);
		SerializerResult serializerResult = serializer.entity(serviceMetadata, entityType, entity, options);
		InputStream entityStream = serializerResult.getContent();

		// 4. configure the response object
		arg1.setContent(entityStream);
		arg1.setStatusCode(HttpStatusCode.OK.getStatusCode());
		arg1.setHeader(HttpHeader.CONTENT_TYPE, arg3.toContentTypeString());

	}

	@Override
	public void updateEntity(ODataRequest request, ODataResponse response, UriInfo uriInfo, ContentType requestFormat, ContentType responseFormat)
			throws ODataApplicationException, ODataLibraryException {
		
		// 1. Retrieve the entity set which belongs to the requested entity
		  List<UriResource> resourcePaths = uriInfo.getUriResourceParts();
		  // Note: only in our example we can assume that the first segment is the EntitySet
		  UriResourceEntitySet uriResourceEntitySet = (UriResourceEntitySet) resourcePaths.get(0);
		  EdmEntitySet edmEntitySet = uriResourceEntitySet.getEntitySet();
		  EdmEntityType edmEntityType = edmEntitySet.getEntityType();

		  // 2. update the data in backend
		  // 2.1. retrieve the payload from the PUT request for the entity to be updated
		  InputStream requestInputStream = request.getBody();
		  ODataDeserializer deserializer = this.odata.createDeserializer(requestFormat);
		  DeserializerResult result = deserializer.entity(requestInputStream, edmEntityType);
		  Entity requestEntity = result.getEntity();
		  // 2.2 do the modification in backend
		  List<UriParameter> keyPredicates = uriResourceEntitySet.getKeyPredicates();
		  // Note that this updateEntity()-method is invoked for both PUT or PATCH operations
		  HttpMethod httpMethod = request.getMethod();
		  
		  updateData(edmEntitySet, keyPredicates, requestEntity, httpMethod);
		  
		//3. configure the response object
		  response.setStatusCode(HttpStatusCode.NO_CONTENT.getStatusCode());

	}

	private void updateData(EdmEntitySet edmEntitySet, List<UriParameter> keyPredicates, Entity requestEntity,
			HttpMethod httpMethod) {
		
		if(httpMethod.toString().equals(httpMethod.PUT.toString())) {
			
			switch (edmEntitySet.getName()) {
			case OppMngmtEdmProvider.ES_CUSTOPTS_NAME:
				CustomerOpportunityCRUD coCRUD = new CustomerOpportunityCRUD();
				coCRUD.updateEntity(keyPredicates,requestEntity);
			}
			
		}
		
		
	}

	private Entity getData(EdmEntitySet edmEntitySet, List<UriParameter> keyPredicates) {

		switch (edmEntitySet.getName()) {
		case OppMngmtEdmProvider.ES_CUSTOMERS_NAME:
			CustomerCRUD cCRUD = new CustomerCRUD();
			return cCRUD.getEntity(keyPredicates);
		case OppMngmtEdmProvider.ES_CUSTOPTS_NAME:
			CustomerOpportunityCRUD coCRUD = new CustomerOpportunityCRUD();
			return coCRUD.getEntity(keyPredicates);
		default:
			return null;
		}

	}

	private Entity putData(EdmEntitySet edmEntitySet, Entity requestEntity) {
		
		long CustOppId=0;
		
		switch (edmEntitySet.getName()) {
		case OppMngmtEdmProvider.ES_CUSTOPTS_NAME:
			CustomerOpportunityCRUD coCRUD = new CustomerOpportunityCRUD();
			CustOppId = coCRUD.createEntity(requestEntity.getProperty("custID").getValue().toString(), 
					                    requestEntity.getProperty("oppID").getValue().toString(), 
					                    requestEntity.getProperty("date").getValue().toString(), 
					                    requestEntity.getProperty("status").getValue().toString(),
					                    requestEntity.getProperty("estimatedValue").getValue().toString());
			requestEntity.getProperty("id").setValue(ValueType.PRIMITIVE, CustOppId);
			return (CustOppId!=0)? requestEntity:null;
		default:
			return null;
		}

	}

}
