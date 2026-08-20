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

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     For every record in a catalog the server SHALL support the HTTP GET operation at the
 *     /collection/{collectionId}/items/{recordId} path.
 *     </q><i><small><a href="https://docs.ogc.org/is/20-004r1/20-004r1.html#req_records-api_record-op"> OGC API - Records - Part 1: Core - Record</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetRecord extends RequestParameters {

    private String collectionId;
    private String recordId;
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
    public GetRecord collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #recordId(java.lang.String)
     */
    public String getRecordId() {
        return recordId;
    }

    /**
     * @see #recordId(java.lang.String)
     */
    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    /**
     * <blockquote><q>
     *     A unique identifier of the catalog record.
     *     </q><i><small><a href="https://docs.ogc.org/is/20-004r1/20-004r1.html#req_records-api_record-response"> OGC API - Records - Part 1: Core - record schema</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Local identifier of the record within the catalog.</li>
     * </ul>
     */
    public GetRecord recordId(String recordId) {
        setRecordId(recordId);
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
    public GetRecord crs(String crs) {
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
    public GetRecord f(String f) {
        setF(f);
        return this;
    }

}
