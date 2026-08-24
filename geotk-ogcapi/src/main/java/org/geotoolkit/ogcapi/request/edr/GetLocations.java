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

import org.opengis.geometry.Envelope;

/**
 * The {@code locations} EDR listing query
 * ({@code /collections/{collectionId}/locations}, or, when {@link #getInstanceId()} is set,
 * {@code /collections/{collectionId}/instances/{instanceId}/locations}).
 *
 * <blockquote><q>
 *     If a location id is not defined the API SHALL return a GeoJSON features array of valid
 *     location identifiers, the schema of the GeoJSON response SHOULD be defined in the OpenAPI
 *     definition of the EDR service.
 *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_60c4d31c-62f2-4dc7-9a3e-6a1a9127d29e"> OGC API - Environmental Data Retrieval (EDR) - Locations query</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetLocations extends AbstractEdrQuery {

    private Envelope bbox;

    private Integer limit;

    /**
     * @see AbstractEdrQuery#getCollectionId()
     */
    public GetLocations collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getInstanceId()
     */
    public GetLocations instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getDatetime()
     */
    public GetLocations datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getF()
     */
    public GetLocations f(String f) {
        setF(f);
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
     * Restricts the returned locations to those whose geometry intersects this bounding box.
     *
     * @param bbox the bounding box filter.
     */
    public GetLocations bbox(Envelope bbox) {
        setBbox(bbox);
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
     *     Maximum number of items to return
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_81254e3a-d734-4195-ad15-ce64e208728c"> OGC API - Environmental Data Retrieval (EDR) - Items query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     * <pre>limit=10</pre>
     */
    public GetLocations limit(Integer limit) {
        setLimit(limit);
        return this;
    }
}
