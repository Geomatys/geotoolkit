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
package org.geotoolkit.ogcapi.request.records;

import org.opengis.geometry.Envelope;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     For every catalog identified in the collections response (path: /collections), the
 *     server SHALL support the HTTP GET operation at the path /collections/{collectionId}/items.
 *     </q><i><small><a href="https://docs.ogc.org/is/20-004r1/20-004r1.html#req_records-api_records-op"> OGC API - Records - Part 1: Core - Records</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetRecords extends RequestParameters {

    private String collectionId;
    private Envelope bbox;
    private String datetime;
    private String q;
    private String type;
    private String externalId;
    private String filter;
    private String filterLang;
    private String sortby;
    private Integer limit;
    private Integer offset;
    private String crs;
    private String f;

    /**
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     local identifier of a collection
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/collectionId.yaml"> OGC API - Features - Part 1: Core - collectionId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing collection (see {@code GET /collections}).</li>
     * </ul>
     */
    public GetRecords collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #bbox(org.opengis.geometry.Envelope)
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @see #bbox(org.opengis.geometry.Envelope)
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * <blockquote><q>
     *     A searchable endpoint SHALL support the Bounding Box Search (bbox) parameter as
     *     defined in the OGC API — Features — Part 1: Core Standard.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-004r1/20-004r1.html#core-query-parameters-bbox"> OGC API - Records - Part 1: Core - bbox parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>The bounding box CRS is derived from this envelope's own coordinate reference system.</li>
     * </ul>
     */
    public GetRecords bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * @see #datetime(java.lang.String)
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @see #datetime(java.lang.String)
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * <blockquote><q>
     *     A searchable endpoint SHALL support a Temporal Searching (datetime) parameter as
     *     defined in the OGC API — Features — Part 1: Core Standard.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-004r1/20-004r1.html#core-query-parameters-datetime"> OGC API - Records - Part 1: Core - datetime parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>A date-time or an interval, per RFC 3339 (as defined by OGC API - Features - Part 1: Core).</li>
     * </ul>
     */
    public GetRecords datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see #q(java.lang.String)
     */
    public String getQ() {
        return q;
    }

    /**
     * @see #q(java.lang.String)
     */
    public void setQ(String q) {
        this.q = q;
    }

    /**
     * <blockquote><q>
     *     The optional q parameter supports keyword searching. Only records
     *     whose text fields contain one or more of the specified search terms are selected.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-004r1/20-004r1.html#core-query-parameters-q"> OGC API - Records - Part 1: Core - q parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Search terms separated by commas are combined with a logical OR.</li>
     *     <li>Search terms separated by white space must all appear, in the order specified.</li>
     *     <li>Keyword searches are case insensitive.</li>
     * </ul>
     */
    public GetRecords q(String q) {
        setQ(q);
        return this;
    }

    /**
     * @see #type(java.lang.String)
     */
    public String getType() {
        return type;
    }

    /**
     * @see #type(java.lang.String)
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * <blockquote><q>
     *     The optional type parameter allows a specific list of records, identified
     *     by their resource type, to be fetched from a catalog. Only records whose
     *     resource type matches one of the values listed for this parameter shall appear in the response.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-004r1/20-004r1.html#core-query-parameters-type"> OGC API - Records - Part 1: Core - type parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetRecords type(String type) {
        setType(type);
        return this;
    }

    /**
     * @see #externalId(java.lang.String)
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @see #externalId(java.lang.String)
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * <blockquote><q>
     *     The optional externalIds parameter allows a specific list of records,
     *     identified by their external identifiers, to be fetched from a catalog.
     *     Only records where one of their associated external identifiers equals one of the values listed for this parameter shall appear in the response.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-004r1/20-004r1.html#core-query-parameters-externalIds"> OGC API - Records - Part 1: Core - externalIds parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetRecords externalId(String externalId) {
        setExternalId(externalId);
        return this;
    }

    /**
     * @see #filter(java.lang.String)
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @see #filter(java.lang.String)
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * <blockquote><q>
     *     The HTTP GET operation on the path that fetches resource instances (e.g.
     *     /collections/{collectionId}/items) SHALL support a parameter filter based on the following OpenAPI 3.0 fragment.
     *     </q><i><small><a href="https://docs.ogc.org/is/19-079r2/19-079r2.html#req_filter_filter-param"> OGC API - Features - Part 3: Filtering - filter parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>An entire record is returned if the filter predicate is matched.</li>
     * </ul>
     */
    public GetRecords filter(String filter) {
        setFilter(filter);
        return this;
    }

    /**
     * @see #filterLang(java.lang.String)
     */
    public String getFilterLang() {
        return filterLang;
    }

    /**
     * @see #filterLang(java.lang.String)
     */
    public void setFilterLang(String filterLang) {
        this.filterLang = filterLang;
    }

    /**
     * <blockquote><q>
     *     The HTTP GET operation on the path that fetches resource instances (e.g.
     *     /collections/{collectionId}/items) SHALL support a parameter filter-lang based on the following OpenAPI 3.0 fragment.
     *     </q><i><small><a href="https://docs.ogc.org/is/19-079r2/19-079r2.html#req_filter_filter-lang-param"> OGC API - Features - Part 3: Filtering - filter-lang parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Allowed values: {@code cql2-text}, {@code cql2-json}.</li>
     *     <li>Default: {@code cql2-text}.</li>
     * </ul>
     */
    public GetRecords filterLang(String filterLang) {
        setFilterLang(filterLang);
        return this;
    }

    /**
     * @see #sortby(java.lang.String)
     */
    public String getSortby() {
        return sortby;
    }

    /**
     * @see #sortby(java.lang.String)
     */
    public void setSortby(String sortby) {
        this.sortby = sortby;
    }

    /**
     * <blockquote><q>
     *     At the searchable catalog endpoint, the operation SHALL support a parameter
     *     sortby with the following characteristics (using an OpenAPI Specification 3.0 fragment).
     *     </q><i><small><a href="https://docs.ogc.org/is/20-004r1/20-004r1.html#req_sorting_sortby-param"> OGC API - Records - Part 1: Core - sortby parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Comma-separated list of property names, each optionally prefixed with {@code +} (ascending) or {@code -} (descending).</li>
     *     <li>Default sort order is ascending ({@code +}).</li>
     * </ul>
     */
    public GetRecords sortby(String sortby) {
        setSortby(sortby);
        return this;
    }

    /**
     * @see #limit(java.lang.Integer)
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @see #limit(java.lang.Integer)
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * <blockquote><q>
     *     A searchable endpoint SHALL support a Paging (limit) parameter as defined in
     *     the OGC API — Features — Part 1: Core Standard.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-004r1/20-004r1.html#core-query-parameters-limit"> OGC API - Records - Part 1: Core - limit parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetRecords limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * @see #offset(java.lang.Integer)
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * @see #offset(java.lang.Integer)
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * <blockquote><q>
     *     ...an additional parameter (e.g., "offset") that a client can use to construct
     *     the next request.
     *     </q><i><small><a href="https://docs.ogc.org/is/17-069r4/17-069r4.html#_parameter_limit"> OGC API - Features - Part 1: Core (example paging scheme)</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; not a normatively-required parameter, provided as a server-specific
     *     paging mechanism (no verbatim spec requirement text available for this field).</li>
     *     <li>The first element has an offset of 0.</li>
     * </ul>
     */
    public GetRecords offset(Integer offset) {
        setOffset(offset);
        return this;
    }

    /**
     * @see #crs(java.lang.String)
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @see #crs(java.lang.String)
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * <blockquote><q>
     *     Each GET request on a 'features' or 'feature' resource SHALL support a query
     *     parameter named crs with the following characteristics:
     *     </q><i><small><a href="https://docs.ogc.org/is/18-058r1/18-058r1.html#req_crs_fc-crs-definition"> OGC API - Features - Part 2: Coordinate Reference Systems by Reference</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Default: {@code http://www.opengis.net/def/crs/OGC/1.3/CRS84} (CRS84) if not specified.</li>
     * </ul>
     */
    public GetRecords crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * @see #f(java.lang.String)
     */
    public String getF() {
        return f;
    }

    /**
     * @see #f(java.lang.String)
     */
    public void setF(String f) {
        this.f = f;
    }

    /**
     * <blockquote><q>
     *     The optional f parameter indicates the output format that the server
     *     shall provide as part of the response document. The default format is JSON.
     *     </q><i><small><a href="https://docs.ogc.org/is/19-072/19-072.html#json-api-definition"> OGC API - Common - Part 1: Core</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Allowed values: {@code json}, {@code html}.</li>
     *     <li>Default: {@code json}.</li>
     * </ul>
     */
    public GetRecords f(String f) {
        setF(f);
        return this;
    }

}
