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
 * @author Quentin Bialota (Geomatys)
 */
public final class GetLocation extends AbstractEdrDataQuery {

    private String locationId;

    private Integer limit;

    public GetLocation collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    public GetLocation instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    public GetLocation datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    public GetLocation parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    public GetLocation crs(String crs) {
        setCrs(crs);
        return this;
    }

    public GetLocation f(String f) {
        setF(f);
        return this;
    }

    /**
     * @return the identifier of the location to query.
     */
    public String getLocationId() {
        return locationId;
    }

    /**
     * @param locationId the identifier of the location to query.
     * @see #getLocationId()
     */
    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public GetLocation locationId(String locationId) {
        setLocationId(locationId);
        return this;
    }

    /**
     * @return the maximum number of items to return, or {@code null} for the server default.
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the maximum number of items to return.
     * @see #getLimit()
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public GetLocation limit(Integer limit) {
        setLimit(limit);
        return this;
    }
}
