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
import org.locationtech.jts.geom.Geometry;

/**
 * Request parameters for the EDR {@code position} query type:
 * {@code GET /collections/{collectionId}/position}, or, when {@link #getInstanceId()} is set,
 * {@code GET /collections/{collectionId}/instances/{instanceId}/position}.
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetPosition extends AbstractEdrDataQuery {

    private Geometry coords;

    private String z;

    private Integer limit;

    public GetPosition collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    public GetPosition instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    public GetPosition datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    public GetPosition parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    public GetPosition crs(String crs) {
        setCrs(crs);
        return this;
    }

    public GetPosition f(String f) {
        setF(f);
        return this;
    }

    /**
     * @return the queried POINT or MULTIPOINT position(s), in WKT.
     */
    public Geometry getCoords() {
        return coords;
    }

    /**
     * @param coords the queried POINT or MULTIPOINT position(s).
     * @see #getCoords()
     */
    public void setCoords(Geometry coords) {
        this.coords = coords;
    }

    public GetPosition coords(Geometry coords) {
        setCoords(coords);
        return this;
    }

    /**
     * @return the vertical level(s) filter (e.g. {@code "10"}, {@code "10/100"} or
     * {@code "R20/100/50"}), or {@code null}.
     */
    public String getZ() {
        return z;
    }

    /**
     * @param z the vertical level(s) filter.
     * @see #getZ()
     */
    public void setZ(String z) {
        this.z = z;
    }

    public GetPosition z(String z) {
        setZ(z);
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

    public GetPosition limit(Integer limit) {
        setLimit(limit);
        return this;
    }
}
