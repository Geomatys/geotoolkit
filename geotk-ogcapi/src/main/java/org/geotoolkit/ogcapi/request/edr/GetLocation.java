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

import java.util.List;

/**
 * The {@code locations/{locationId}} EDR query
 * ({@code /collections/{collectionId}/locations/{locationId}}, or, when
 * {@link #getInstanceId()} is set,
 * {@code /collections/{collectionId}/instances/{instanceId}/locations/{locationId}}).
 *
 * <blockquote><q>
 *     The Locations query returns data for the named location.
 *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_60c4d31c-62f2-4dc7-9a3e-6a1a9127d29e"> OGC API - Environmental Data Retrieval (EDR) - Locations query</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetLocation extends AbstractEdrDataQuery {

    private String locationId;

    private Integer limit;

    /**
     * @see AbstractEdrQuery#getCollectionId()
     */
    public GetLocation collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getInstanceId()
     */
    public GetLocation instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getDatetime()
     */
    public GetLocation datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getParameterName()
     */
    public GetLocation parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getCrs()
     */
    public GetLocation crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getF()
     */
    public GetLocation f(String f) {
        setF(f);
        return this;
    }

    /**
     * @see #locationId(java.lang.String)
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * @see #locationId(java.lang.String)
     */
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    /**
     * <blockquote><q>
     *     A unique identifier for the required location, such as a GeoHash, a World
     *     Meteorological Organization (WMO) station identifier or place name.
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_60c4d31c-62f2-4dc7-9a3e-6a1a9127d29e"> OGC API - Environmental Data Retrieval (EDR) - Locations query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     * <pre>
     * locationId=EGLL
     * locationId=Ottawa
     * locationId=limit.broom.flip
     * locationId=gbsvn
     * </pre>
     */
    public GetLocation locationId(String locationId) {
        setLocationId(locationId);
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
    public GetLocation limit(Integer limit) {
        setLimit(limit);
        return this;
    }
}
