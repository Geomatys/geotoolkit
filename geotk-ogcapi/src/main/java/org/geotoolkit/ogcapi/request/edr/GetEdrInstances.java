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
package org.geotoolkit.ogcapi.request.edr;

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * To retrieve the list of instances of a collection ({@code /collections/{collectionId}/instances}).
 *
 * <blockquote><q>
 *     Having multiple versions or instances of the same collection, where the same information
 *     is reprocessed or regenerated is not unusal.
 *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_5bd2378d-8c59-4516-949e-22db29b30170"> OGC API - Environmental Data Retrieval (EDR) - Instances query</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetEdrInstances extends RequestParameters {

    private String collectionId;
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
     * </ul>
     */
    public GetEdrInstances collectionId(String collectionId) {
        setCollectionId(collectionId);
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
     * The output format of the response. If not given, the content type is negotiated from the
     * {@code Accept} header.
     *
     * @param f the requested response format, or {@code null} for content negotiation.
     */
    public GetEdrInstances f(String f) {
        setF(f);
        return this;
    }
}
