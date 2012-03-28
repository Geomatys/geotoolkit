/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */
package org.geotoolkit.csw;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.csw.xml.ResultType;
import org.geotoolkit.csw.xml.TypeNames;
import org.geotoolkit.csw.xml.v202.DistributedSearchType;
import org.geotoolkit.csw.xml.v202.ElementSetNameType;
import org.geotoolkit.csw.xml.v202.GetRecordsType;
import org.geotoolkit.csw.xml.v202.QueryConstraintType;
import org.geotoolkit.csw.xml.v202.QueryType;
import org.geotoolkit.filter.FilterFactoryImpl;
import org.geotoolkit.filter.text.cql2.CQL;
import org.geotoolkit.filter.text.cql2.CQLException;
import org.geotoolkit.ogc.xml.v110.FilterType;
import org.geotoolkit.ogc.xml.v110.SortByType;
import org.geotoolkit.security.ClientSecurity;
import org.opengis.filter.Filter;


/**
 * Abstract implementation of {@link GetRecordsRequest}, which defines the
 * parameters for a GetRecords request.
 *
 * @author Cédric Briançon (Geomatys)
 * @author Mehdi Sidhoum (Geomatys)
 * @author Giuseppe La Scaleia (IMAA)
 * @module pending
 */
public abstract class AbstractGetRecords extends AbstractCSWRequest implements GetRecordsRequest {

    /**
     * The version to use for this webservice request.
     */
    protected final String version;

    private String constraint = null;
    private String constraintLanguage = null;
    private String constraintLanguageVersion = null;
    private ElementSetType elementSetName = null;
    private Integer maxRecords = null;
    private String namespace = null;
    private String outputFormat = null;
    private String outputSchema = null;
    private String requestId = null;
    private ResultType resultType = null;
    private Integer startPosition = null;
    private String typeNames = null;
    private String sortBy = null;
    private Boolean distributedSearch = null;
    private Integer hopcount = null;
    private String responseHandler = null;

    /**
     * Defines the server url and the service version for this kind of request.
     *
     * @param serverURL The server url.
     * @param version The version of the request.
     */
    protected AbstractGetRecords(final String serverURL, final String version, final ClientSecurity security){
        super(serverURL,security);
        this.version = version;
    }

    @Override
    public String getConstraint() {
        return constraint;
    }

    @Override
    public String getConstraintLanguage() {
        return constraintLanguage;
    }

    @Override
    public String getConstraintLanguageVersion() {
        return constraintLanguageVersion;
    }

    @Override
    public Boolean isDistributedSearch() {
        return distributedSearch;
    }

    @Override
    public ElementSetType getElementSetName() {
        return elementSetName;
    }

    @Override
    public Integer getHopcount() {
        return hopcount;
    }

    @Override
    public Integer getMaxRecords() {
        return maxRecords;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getOutputFormat() {
        return outputFormat;
    }

    @Override
    public String getOutputSchema() {
        return outputSchema;
    }

    @Override
    public String getRequestId() {
        return requestId;
    }

    @Override
    public String getResponseHandler() {
        return responseHandler;
    }

    @Override
    public ResultType getResultType() {
        return resultType;
    }

    @Override
    public String getSortBy() {
        return sortBy;
    }

    @Override
    public Integer getStartPosition() {
        return startPosition;
    }

    @Override
    public String getTypeNames() {
        return typeNames;
    }

    @Override
    public void setConstraint(final String constraint) {
        this.constraint = constraint;
    }

    @Override
    public void setConstraintLanguage(final String constraintLanguage) {
        this.constraintLanguage = constraintLanguage;
    }

    @Override
    public void setConstraintLanguageVersion(final String constraintLanguageVersion) {
        this.constraintLanguageVersion = constraintLanguageVersion;
    }

    @Override
    public void setDistributedSearch(final Boolean distributedSearch) {
        this.distributedSearch = distributedSearch;
    }

    @Override
    public void setElementSetName(final ElementSetType elementSetName) {
        this.elementSetName = elementSetName;
    }

    @Override
    public void setHopcount(final Integer hopcount) {
        this.hopcount = hopcount;
    }

    @Override
    public void setMaxRecords(final Integer maxRecords) {
        this.maxRecords = maxRecords;
    }

    @Override
    public void setNamespace(final String namespace) {
        this.namespace = namespace;
    }

    @Override
    public void setOutputFormat(final String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public void setOutputSchema(final String outputSchema) {
        this.outputSchema = outputSchema;
    }

    @Override
    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    @Override
    public void setResponseHandler(final String responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void setResultType(final ResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public void setSortBy(final String sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public void setStartPosition(final Integer startPosition) {
        this.startPosition = startPosition;
    }

    @Override
    public void setTypeNames(final String typeNames) {
        this.typeNames = typeNames;
    }

    @Override
    protected void prepareParameters() {
        super.prepareParameters();
        if (typeNames == null) {
            throw new IllegalArgumentException("The parameter \"TYPENAMES\" is not defined");
        }
        if (constraintLanguage == null) {
            throw new IllegalArgumentException("The parameter \"CONSTRAINTLANGUAGE\" is not defined");
        }
        if (constraintLanguageVersion == null) {
            throw new IllegalArgumentException("The parameter \"CONSTRAINT_LANGUAGE_VERSION\" is not defined");
        }

        requestParameters.put("SERVICE",                     "CSW");
        requestParameters.put("REQUEST",                     "GetRecords");
        requestParameters.put("VERSION",                     version);
        requestParameters.put("TYPENAMES",                   typeNames);
        requestParameters.put("CONSTRAINTLANGUAGE",          constraintLanguage);
        requestParameters.put("CONSTRAINT_LANGUAGE_VERSION", constraintLanguageVersion);

        if (constraint != null) {
            requestParameters.put("CONSTRAINT", constraint);
        }
        if (outputSchema != null) {
            requestParameters.put("OUTPUTSCHEMA", outputSchema);
        }
        if (resultType != null) {
            requestParameters.put("RESULTTYPE", resultType.value());
        }
        if (namespace != null) {
            requestParameters.put("NAMESPACE", namespace);
        }
        if (requestId != null) {
            requestParameters.put("REQUESTID", requestId);
        }
        if (outputFormat != null) {
            requestParameters.put("OUTPUTFORMAT", outputFormat);
        }
        if (startPosition != null) {
            requestParameters.put("STARTPOSITION", startPosition.toString());
        }
        if (maxRecords != null) {
            requestParameters.put("MAXRECORDS", maxRecords.toString());
        }
        if (elementSetName != null) {
            requestParameters.put("ELEMENTSETNAME", elementSetName.value());
        }
        if (sortBy != null) {
            requestParameters.put("SORTBY", sortBy);
        }
        if (distributedSearch != null) {
            requestParameters.put("DISTRIBUTEDSEARCH", distributedSearch.toString());
            if (distributedSearch == true && hopcount != null) {
                requestParameters.put("HOPCOUNT", hopcount.toString());
            }
        }
        if (responseHandler != null) {
            requestParameters.put("RESPONSEHANDLER", responseHandler);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream getResponseStream() throws IOException {
        final URL url = getURL();
        URLConnection conec = url.openConnection();
        security.secure(conec);

        conec.setDoOutput(true);
        conec.setRequestProperty("Content-Type", "text/xml");

        OutputStream stream = conec.getOutputStream();
        stream = security.encrypt(stream);

        Marshaller marsh = null;
        try {
            marsh = POOL.acquireMarshaller();

            /*
             * Getting typeNames value used to build QueryType object
             */
            final List<QName> typNames = new ArrayList<QName>();
            if (typeNames != null) {
                typNames.add(TypeNames.valueOf(typeNames));
            }

            /*
             * Getting ElementSetType value used to build QueryType object
             */
            ElementSetNameType esnt = null;
            if (elementSetName != null) {
                esnt = new ElementSetNameType(elementSetName);
            }

            /*
             * Getting  SortByType value, default is null
             *
             * @TODO if sortBy is not null we must creates SortByType instance
             * the value can be sortBy=Title:A,Abstract:D where A for ascending order and D for decending.
             * see Table 29 - Parameters in GetRecords operation request in document named
             * OpenGIS Catalogue Services Specification 2.0.2 -ISO Metadata Application Profile
             *
             */
            final SortByType sort;
            if (sortBy != null) {
                throw new UnsupportedOperationException("The parameter SortBy is not implemented yet for Method POST.");
            } else {
                sort = null;
            }

            /*
             * Building QueryType from the cql constraint
             */
            QueryConstraintType qct = null;
            if (constraint != null && !constraint.isEmpty()) try {
                final FilterType filterType;
                Filter filter = CQL.toFilter(constraint, new FilterFactoryImpl());
                if (filter instanceof FilterType) {
                    filterType = (FilterType) filter;
                } else {
                    filterType = new FilterType(filter);
                }
                qct = new QueryConstraintType(filterType, constraintLanguageVersion != null ? constraintLanguageVersion : "1.1.0");
            } catch (CQLException ex) {
                //@TODO maybe use another Exception.
                throw new IllegalArgumentException("Constraint cannot be parsed to filter, the constraint parameter value is not in OGC CQL format.", ex);
            }
            final QueryType queryType = new QueryType(typNames, esnt, sort, qct);

            final GetRecordsType recordsXml = new GetRecordsType("CSW", version, resultType,
                    requestId, outputFormat, outputSchema, startPosition, maxRecords,
                    queryType, new DistributedSearchType(hopcount));
            marsh.marshal(recordsXml, stream);
        } catch (JAXBException ex) {
            throw new IOException(ex);
        } finally {
            if (POOL != null && marsh != null) {
                POOL.release(marsh);
            }
        }
        stream.close();
        return security.decrypt(conec.getInputStream());
    }
}
