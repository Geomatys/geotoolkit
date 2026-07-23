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
 * The {@code items} query ({@code /collections/{collectionId}/items}) on a records catalog.
 *
 * <p>OGC API - Records Part 1: Core.</p>
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
     * Local identifier of the catalog. (required)
     *
     * @return the collectionId
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public GetRecords collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Only records that have a geometry that intersects the bounding box are selected. The
     * bounding box CRS (query parameter {@code bbox-crs}) is derived from this envelope's own
     * coordinate reference system. (optional)
     *
     * @return the bbox
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public GetRecords bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * A date-time or a period, per RFC 3339. (optional)
     *
     * @return the datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public GetRecords datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * Free-text search on the record's textual properties (e.g. title, description, keywords).
     * (optional)
     *
     * @return the q
     */
    public String getQ() {
        return q;
    }

    /**
     * @param q the q to set
     * @see #getQ()
     */
    public void setQ(String q) {
        this.q = q;
    }

    /**
     * @param q the q to set
     * @see #getQ()
     */
    public GetRecords q(String q) {
        setQ(q);
        return this;
    }

    /**
     * Only records of the given resource type are selected (e.g. {@code dataset},
     * {@code series}). (optional)
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     * @see #getType()
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @param type the type to set
     * @see #getType()
     */
    public GetRecords type(String type) {
        setType(type);
        return this;
    }

    /**
     * Only the record with the given external identifier (e.g. a DOI) is selected. (optional)
     *
     * @return the externalId
     */
    public String getExternalId() {
        return externalId;
    }

    /**
     * @param externalId the externalId to set
     * @see #getExternalId()
     */
    public void setExternalId(String externalId) {
        this.externalId = externalId;
    }

    /**
     * @param externalId the externalId to set
     * @see #getExternalId()
     */
    public GetRecords externalId(String externalId) {
        setExternalId(externalId);
        return this;
    }

    /**
     * An expression in a query language (e.g. CQL2) for which an entire record will be returned
     * if the filter predicate is matched. Sent as-is to the server, not parsed client-side.
     * (optional)
     *
     * @return the filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     * @see #getFilter()
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * @param filter the filter to set
     * @see #getFilter()
     */
    public GetRecords filter(String filter) {
        setFilter(filter);
        return this;
    }

    /**
     * The query language of the {@code filter} parameter (e.g. {@code cql2-text}). (optional)
     *
     * @return the filterLang
     */
    public String getFilterLang() {
        return filterLang;
    }

    /**
     * @param filterLang the filterLang to set
     * @see #getFilterLang()
     */
    public void setFilterLang(String filterLang) {
        this.filterLang = filterLang;
    }

    /**
     * @param filterLang the filterLang to set
     * @see #getFilterLang()
     */
    public GetRecords filterLang(String filterLang) {
        setFilterLang(filterLang);
        return this;
    }

    /**
     * A comma-separated list of property names to sort by, each optionally prefixed with
     * {@code +} (ascending, default) or {@code -} (descending). (optional)
     *
     * @return the sortby
     */
    public String getSortby() {
        return sortby;
    }

    /**
     * @param sortby the sortby to set
     * @see #getSortby()
     */
    public void setSortby(String sortby) {
        this.sortby = sortby;
    }

    /**
     * @param sortby the sortby to set
     * @see #getSortby()
     */
    public GetRecords sortby(String sortby) {
        setSortby(sortby);
        return this;
    }

    /**
     * The optional limit parameter limits the number of records presented in the response.
     * (optional)
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public GetRecords limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * The optional offset parameter indicates the offset within the result set from which the
     * server shall begin presenting results in the response document. The first element has an
     * offset of 0. (optional)
     *
     * @return the offset
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     * @see #getOffset()
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * @param offset the offset to set
     * @see #getOffset()
     */
    public GetRecords offset(Integer offset) {
        setOffset(offset);
        return this;
    }

    /**
     * The coordinate reference system of the response geometries, as a URI. If not given, the
     * default CRS (CRS84) is used. (optional)
     *
     * @return the crs
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the crs to set
     * @see #getCrs()
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * @param crs the crs to set
     * @see #getCrs()
     */
    public GetRecords crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * The output format of the response. If not given, the content type is negotiated from the
     * {@code Accept} header. (optional)
     *
     * @return the f
     */
    public String getF() {
        return f;
    }

    /**
     * @param f the f to set
     * @see #getF()
     */
    public void setF(String f) {
        this.f = f;
    }

    /**
     * @param f the f to set
     * @see #getF()
     */
    public GetRecords f(String f) {
        setF(f);
        return this;
    }

}
