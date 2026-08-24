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
package org.geotoolkit.ogcapi.request.feature;

import org.opengis.geometry.Envelope;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code items} query ({@code /collections/{collectionId}/items}).
 *
 * <blockquote><q>
 *     For every feature collection identified in the feature collections response (path
 *     /collections), the server SHALL support the HTTP GET operation at the path
 *     /collections/{collectionId}/items.
 *     </q><i><small><a href="https://docs.ogc.org/is/17-069r4/17-069r4.html#_items_"> OGC API - Features - Part 1: Core - Features</a></small></i>
 * </blockquote>
 *
 * <p>Also reused as-is by OGC API - EDR's {@code Locations} query, which is defined in terms of
 * this same resource.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetItems extends RequestParameters {

    private String collectionId;
    private Envelope bbox;
    private String datetime;
    private Integer limit;
    private Integer offset;
    private String crs;
    private String filter;
    private String filterLang;
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
    public GetItems collectionId(String collectionId) {
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
     *     Only features that have a geometry that intersects the bounding box are selected.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/bbox.yaml"> OGC API - Features - Part 1: Core - bbox parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Four or six numbers, depending on whether the CRS includes a vertical axis.</li>
     *     <li>Default CRS: WGS84 longitude/latitude (CRS84), derived from this envelope's own
     *     coordinate reference system.</li>
     * </ul>
     */
    public GetItems bbox(Envelope bbox) {
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
     *     Either a date-time or an interval. Date and time expressions adhere to RFC 3339.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/datetime.yaml"> OGC API - Features - Part 1: Core - datetime parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Intervals may be bounded or half-bounded (double-dots at start or end).</li>
     *     <li>Only features with a temporal property intersecting the value are selected.</li>
     * </ul>
     * <pre>
     * datetime=2018-02-12T23:20:50Z
     * datetime=2018-02-12T00:00:00Z/2018-03-18T12:31:12Z
     * </pre>
     */
    public GetItems datetime(String datetime) {
        setDatetime(datetime);
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
     *     The optional limit parameter limits the number of items that are
     *     presented in the response document.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/limit.yaml"> OGC API - Features - Part 1: Core - limit parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Minimum = 1.</li>
     *     <li>Maximum = 10000.</li>
     *     <li>Default = 10.</li>
     *     <li>Only counts items at the first level of the response document.</li>
     * </ul>
     */
    public GetItems limit(Integer limit) {
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
     * The offset within the result set from which the server shall begin presenting results,
     * the first element having an offset of {@code 0}.
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Not part of OGC API - Features - Part 1: Core, which paginates using the
     *     {@code next}/{@code prev} links instead (no verbatim spec text available for this
     *     field); several server implementations nonetheless expose it as a convenience
     *     parameter.</li>
     * </ul>
     */
    public GetItems offset(Integer offset) {
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
     *     Each GET request on a 'features' or 'feature' resource SHALL support a query parameter
     *     named crs with the following characteristics:
     *     </q><i><small><a href="https://docs.opengeospatial.org/is/18-058r1/18-058r1.html#_parameter_crs"> OGC API - Features - Part 2: CRS by Reference - Parameter crs</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>The coordinate reference system of the response geometries, as a URI.</li>
     *     <li>Default: CRS84 (or CRS84h) if not specified.</li>
     * </ul>
     * <pre>.../collections/buildings/items?crs=http%3A%2F%2Fwww.opengis.net%2Fdef%2Fcrs%2FEPSG%2F0%2F26703&amp;...</pre>
     */
    public GetItems crs(String crs) {
        setCrs(crs);
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
     *     The Filter requirements class defines a general parameter, filter, whose
     *     value is a filter expression to be applied when retrieving resources.
     *     </q><i><small><a href="https://docs.ogc.org/is/19-079r2/19-079r2.html#filter-param"> OGC API - Features - Part 3: Filtering - Parameter filter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>The query language is specified by {@code filterLang}.</li>
     *     <li>Sent as-is to the server, not parsed client-side.</li>
     * </ul>
     */
    public GetItems filter(String filter) {
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
     *     In order to specify that specific language that is being used, this clause
     *     defines the filter-lang parameter.
     *     </q><i><small><a href="https://docs.ogc.org/is/19-079r2/19-079r2.html#filter-lang-param"> OGC API - Features - Part 3: Filtering - Parameter filter-lang</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Allowed values: {@code cql2-text}, {@code cql2-json}.</li>
     *     <li>Default: {@code cql2-text}.</li>
     * </ul>
     */
    public GetItems filterLang(String filterLang) {
        setFilterLang(filterLang);
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
     *     <li>Optional; content negotiation from the {@code Accept} header is used when omitted.</li>
     * </ul>
     */
    public GetItems f(String f) {
        setF(f);
        return this;
    }

}
