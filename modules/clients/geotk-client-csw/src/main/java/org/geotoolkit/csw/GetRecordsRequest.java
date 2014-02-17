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

import org.geotoolkit.client.Request;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.csw.xml.ResultType;


/**
 *
 * @author Cédric Briançon (Geomatys)
 * @module pending
 */
public interface GetRecordsRequest extends Request {
    String getConstraint();

    void setConstraint(String constraint);

    String getConstraintLanguage();

    void setConstraintLanguage(String constraintLanguage);

    String getConstraintLanguageVersion();

    void setConstraintLanguageVersion(String constraintLanguageVersion);

    Boolean isDistributedSearch();

    void setDistributedSearch(Boolean distributedSearch);

    ElementSetType getElementSetName();

    void setElementSetName(ElementSetType elementSetName);

    Integer getHopcount();

    void setHopcount(Integer hopcount);

    Integer getMaxRecords();

    void setMaxRecords(Integer maxRecords);

    String getNamespace();

    void setNamespace(String namespace);

    String getOutputFormat();

    void setOutputFormat(String outputFormat);

    String getOutputSchema();

    void setOutputSchema(String outputSchema);

    String getRequestId();

    void setRequestId(String value);

    String getResponseHandler();

    void setResponseHandler(String responseHandler);

    ResultType getResultType();

    void setResultType(ResultType resultType);

    String getSortBy();

    void setSortBy(String sortBy);

    Integer getStartPosition();

    void setStartPosition(Integer startPosition);

    String getTypeNames();

    void setTypeNames(String typeNames);
}
