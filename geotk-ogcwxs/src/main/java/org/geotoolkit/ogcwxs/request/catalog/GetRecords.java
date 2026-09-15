/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.ogcwxs.request.catalog;

import java.util.List;
import javax.xml.namespace.QName;
import org.geotoolkit.csw.xml.CSWVersion;
import org.geotoolkit.csw.xml.ElementSetType;
import org.geotoolkit.csw.xml.ResultType;
import org.geotoolkit.ogcwxs.request.RequestParameters;
import org.opengis.filter.Filter;

/**
 * To retrieve records (metadata) matching a filter on a set of record types.
 *
 * <blockquote><q>
 * Allows clients to ask a catalogue to execute a query that searches the catalogued metadata
 * and produces a result set containing (zero or more) references to all the registered
 * resources that satisfy the query.
 * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_8">OGC 12-168r6, Table 8</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 * @see #version(CSWVersion)
 * @see #typeNames(List)
 * @see #filter(Filter)
 * @see #elementSetName(ElementSetType)
 * @see #resultType(ResultType)
 * @see #startPosition(Integer)
 * @see #maxRecords(Integer)
 * @see #outputSchema(String)
 * @see #outputFormat(String)
 */
public final class GetRecords extends RequestParameters {

    private CSWVersion version;
    private List<QName> typeNames;
    private Filter filter;
    private ElementSetType elementSetName;
    private ResultType resultType;
    private Integer startPosition;
    private Integer maxRecords;
    private String outputSchema;
    private String outputFormat;

    /**
     * The CSW version to request. Defaults to {@link CSWVersion#v300} if left {@code null}.
     *
     * @return the version
     * @see #version(CSWVersion)
     */
    public CSWVersion getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     * @see #version(CSWVersion)
     */
    public void setVersion(CSWVersion version) {
        this.version = version;
    }

    /**
     * @param version the version to set
     * @see #getVersion()
     * @see #setVersion(CSWVersion)
     */
    public GetRecords version(CSWVersion version) {
        setVersion(version);
        return this;
    }

    /**
     * Record types to query (e.g. {@code csw:Record}). (optional, server-dependent default)
     *
     * <blockquote><q>
     * A catalogue may contain references to several different resource types. This parameter
     * provides for the selection of one of those types for retrieval
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_8">OGC 12-168r6, Table 8 (resourceType)</a></small></i>
     * </blockquote>
     *
     * @return the typeNames
     * @see #typeNames(List)
     */
    public List<QName> getTypeNames() {
        return typeNames;
    }

    /**
     * @param typeNames the typeNames to set
     * @see #typeNames(List)
     */
    public void setTypeNames(List<QName> typeNames) {
        this.typeNames = typeNames;
    }

    /**
     * @param typeNames the typeNames to set
     * @see #getTypeNames()
     * @see #setTypeNames(List)
     */
    public GetRecords typeNames(List<QName> typeNames) {
        setTypeNames(typeNames);
        return this;
    }

    /**
     * Filter to apply, expressed in the version-independent {@link Filter} model. It is converted
     * to the version-specific OGC filter encoding before being sent. (optional)
     *
     * <blockquote><q>
     * The query language and predicate expressing query constraints
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_8">OGC 12-168r6, Table 8 (queryExpression)</a></small></i>
     * </blockquote>
     *
     * @return the filter
     * @see #filter(Filter)
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     * @see #filter(Filter)
     */
    public void setFilter(Filter filter) {
        this.filter = filter;
    }

    /**
     * @param filter the filter to set
     * @see #getFilter()
     * @see #setFilter(Filter)
     */
    public GetRecords filter(Filter filter) {
        setFilter(filter);
        return this;
    }

    /**
     * Level of detail of the returned records. (optional, server-dependent default)
     *
     * <blockquote><q>
     * Specifies set name or list of metadata elements to be returned in the context of a
     * specific metadata structure
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_8">OGC 12-168r6, Table 8 (responseElements)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Allowed values: {@code brief}, {@code summary}, {@code full}, {@code browse}.</li>
     * <li>Default value is {@code summary}.</li>
     * </ul>
     *
     * @return the elementSetName
     * @see #elementSetName(ElementSetType)
     */
    public ElementSetType getElementSetName() {
        return elementSetName;
    }

    /**
     * @param elementSetName the elementSetName to set
     * @see #elementSetName(ElementSetType)
     */
    public void setElementSetName(ElementSetType elementSetName) {
        this.elementSetName = elementSetName;
    }

    /**
     * @param elementSetName the elementSetName to set
     * @see #getElementSetName()
     * @see #setElementSetName(ElementSetType)
     */
    public GetRecords elementSetName(ElementSetType elementSetName) {
        setElementSetName(elementSetName);
        return this;
    }

    /**
     * Whether to return matching records, only their count, or to just validate the request.
     * Defaults to {@link ResultType#RESULTS} if left {@code null}.
     *
     * <blockquote><q>
     * Specifies how client wants result set presented and the behaviour of the catalogue as to
     * when a response is sent
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_8">OGC 12-168r6, Table 8 (resultType)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>{@code validate}: response returned as soon as the request is known to be valid; query processing continues afterwards.</li>
     * <li>{@code hits}: response returned once the number of hits is known; no metadata records included.</li>
     * <li>{@code results}: response returned once the query has completed and results are formatted; metadata records included.</li>
     * </ul>
     *
     * @return the resultType
     * @see #resultType(ResultType)
     */
    public ResultType getResultType() {
        return resultType;
    }

    /**
     * @param resultType the resultType to set
     * @see #resultType(ResultType)
     */
    public void setResultType(ResultType resultType) {
        this.resultType = resultType;
    }

    /**
     * @param resultType the resultType to set
     * @see #getResultType()
     * @see #setResultType(ResultType)
     */
    public GetRecords resultType(ResultType resultType) {
        setResultType(resultType);
        return this;
    }

    /**
     * Index (1-based) of the first record to return, for paging through a result set. (optional)
     *
     * <blockquote><q>
     * First result set resource to be returned for this operation request
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_8">OGC 12-168r6, Table 8 (cursorPosition)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Default value is {@code 1}.</li>
     * </ul>
     *
     * @return the startPosition
     * @see #startPosition(Integer)
     */
    public Integer getStartPosition() {
        return startPosition;
    }

    /**
     * @param startPosition the startPosition to set
     * @see #startPosition(Integer)
     */
    public void setStartPosition(Integer startPosition) {
        this.startPosition = startPosition;
    }

    /**
     * @param startPosition the startPosition to set
     * @see #getStartPosition()
     * @see #setStartPosition(Integer)
     */
    public GetRecords startPosition(Integer startPosition) {
        setStartPosition(startPosition);
        return this;
    }

    /**
     * Maximum number of records to return. (optional)
     *
     * <blockquote><q>
     * Specifies maximum number of result set resources to be returned
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_8">OGC 12-168r6, Table 8 (iteratorSize)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Default value is {@code 10}.</li>
     * </ul>
     *
     * @return the maxRecords
     * @see #maxRecords(Integer)
     */
    public Integer getMaxRecords() {
        return maxRecords;
    }

    /**
     * @param maxRecords the maxRecords to set
     * @see #maxRecords(Integer)
     */
    public void setMaxRecords(Integer maxRecords) {
        this.maxRecords = maxRecords;
    }

    /**
     * @param maxRecords the maxRecords to set
     * @see #getMaxRecords()
     * @see #setMaxRecords(Integer)
     */
    public GetRecords maxRecords(Integer maxRecords) {
        setMaxRecords(maxRecords);
        return this;
    }

    /**
     * Schema in which the records are described (e.g. the ISO 19115 metadata namespace).
     * (optional, server-dependent default)
     *
     * <blockquote><q>
     * The name of the "well-known" or advertised (in the capabilities) schema of the response
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_8">OGC 12-168r6, Table 8 (responseSchema)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Default value is {@code OGCCORE}.</li>
     * </ul>
     *
     * @return the outputSchema
     * @see #outputSchema(String)
     */
    public String getOutputSchema() {
        return outputSchema;
    }

    /**
     * @param outputSchema the outputSchema to set
     * @see #outputSchema(String)
     */
    public void setOutputSchema(String outputSchema) {
        this.outputSchema = outputSchema;
    }

    /**
     * @param outputSchema the outputSchema to set
     * @see #getOutputSchema()
     * @see #setOutputSchema(String)
     */
    public GetRecords outputSchema(String outputSchema) {
        setOutputSchema(outputSchema);
        return this;
    }

    /**
     * Requested output format. (optional, server-dependent default)
     *
     * <blockquote><q>
     * Specifies format (MIME or Internet media type) for returning result set metadata
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_8">OGC 12-168r6, Table 8 (returnFormat)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Default value is {@code XML}.</li>
     * </ul>
     *
     * @return the outputFormat
     * @see #outputFormat(String)
     */
    public String getOutputFormat() {
        return outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set
     * @see #outputFormat(String)
     */
    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * @param outputFormat the outputFormat to set
     * @see #getOutputFormat()
     * @see #setOutputFormat(String)
     */
    public GetRecords outputFormat(String outputFormat) {
        setOutputFormat(outputFormat);
        return this;
    }

}
