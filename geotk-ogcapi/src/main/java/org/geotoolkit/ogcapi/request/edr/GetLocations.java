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
 * @author Quentin Bialota (Geomatys)
 */
public final class GetLocations extends AbstractEdrQuery {

    private Envelope bbox;

    private Integer limit;

    public GetLocations collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    public GetLocations instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    public GetLocations datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    public GetLocations f(String f) {
        setF(f);
        return this;
    }

    /**
     * @return the bounding box: only locations whose geometry intersects it are selected, or
     * {@code null}.
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bounding box filter.
     * @see #getBbox()
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    public GetLocations bbox(Envelope bbox) {
        setBbox(bbox);
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

    public GetLocations limit(Integer limit) {
        setLimit(limit);
        return this;
    }
}
