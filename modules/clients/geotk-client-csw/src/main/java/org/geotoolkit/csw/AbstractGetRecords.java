/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;
import org.geotoolkit.client.AbstractRequest;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.csw.xml.ResultType;
import org.geotoolkit.util.logging.Logging;


/**
 * Abstract implementation of {@link GetRecordsRequest}, which defines the
 * parameters for a GetRecords request.
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public abstract class AbstractGetRecords extends AbstractRequest implements GetRecordsRequest {
    /**
     * Default logger for all GetRecords requests.
     */
    protected static final Logger LOGGER = Logging.getLogger(AbstractGetRecords.class);

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
    protected AbstractGetRecords(final String serverURL, final String version){
        super(serverURL);
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
    public void setConstraint(String constraint) {
        this.constraint = constraint;
    }

    @Override
    public void setConstraintLanguage(String constraintLanguage) {
        this.constraintLanguage = constraintLanguage;
    }

    @Override
    public void setConstraintLanguageVersion(String constraintLanguageVersion) {
        this.constraintLanguageVersion = constraintLanguageVersion;
    }

    @Override
    public void setDistributedSearch(Boolean distributedSearch) {
        this.distributedSearch = distributedSearch;
    }

    @Override
    public void setElementSetName(ElementSetType elementSetName) {
        this.elementSetName = elementSetName;
    }

    @Override
    public void setHopcount(Integer hopcount) {
        this.hopcount = hopcount;
    }

    @Override
    public void setMaxRecords(Integer maxRecords) {
        this.maxRecords = maxRecords;
    }

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    @Override
    public void setOutputSchema(String outputSchema) {
        this.outputSchema = outputSchema;
    }

    @Override
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public void setResponseHandler(String responseHandler) {
        this.responseHandler = responseHandler;
    }

    @Override
    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    @Override
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    @Override
    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    @Override
    public void setTypeNames(String typeNames) {
        this.typeNames = typeNames;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public URL getURL() throws MalformedURLException {
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

        return super.getURL();
    }

    @Override
    public InputStream getSOAPResponse() throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
