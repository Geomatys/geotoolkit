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

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * The {@code items/{itemId}} query ({@code /collections/{collectionId}/items/{itemId}}).
 *
 * <blockquote><q>
 *     For every feature in a feature collection (path /collections/{collectionId}), the
 *     server SHALL support the HTTP GET operation at the path
 *     /collections/{collectionId}/items/{featureId}.
 *     </q><i><small><a href="https://docs.ogc.org/is/17-069r4/17-069r4.html#_feature_"> OGC API - Features - Part 1: Core - Feature</a></small></i>
 * </blockquote>
 *
 * <p>Also reused as-is by OGC API - EDR's {@code Locations} query, which is defined in terms of
 * this same resource.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetItem extends RequestParameters {

    private String collectionId;
    private String itemId;
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
    public GetItem collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #itemId(java.lang.String)
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @see #itemId(java.lang.String)
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    /**
     * <blockquote><q>
     *     local identifier of a feature
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/featureId.yaml"> OGC API - Features - Part 1: Core - featureId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetItem itemId(String itemId) {
        setItemId(itemId);
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
     *     <li>The coordinate reference system of the response geometry, as a URI.</li>
     *     <li>Default: CRS84 (or CRS84h) if not specified.</li>
     * </ul>
     */
    public GetItem crs(String crs) {
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
     *     <li>Optional; content negotiation from the {@code Accept} header is used when omitted.</li>
     * </ul>
     */
    public GetItem f(String f) {
        setF(f);
        return this;
    }

}
