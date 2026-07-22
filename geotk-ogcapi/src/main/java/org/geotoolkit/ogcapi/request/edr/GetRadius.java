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
 * The {@code radius} EDR query
 * ({@code /collections/{collectionId}/radius}, or, when {@link #getInstanceId()} is set,
 * {@code /collections/{collectionId}/instances/{instanceId}/radius}).
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetRadius extends AbstractEdrDataQuery {

    private Geometry coords;

    private String z;

    private Double within;

    private String withinUnits;

    private Integer limit;

    public GetRadius collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    public GetRadius instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    public GetRadius datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    public GetRadius parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    public GetRadius crs(String crs) {
        setCrs(crs);
        return this;
    }

    public GetRadius f(String f) {
        setF(f);
        return this;
    }

    /**
     * @return the queried POINT center of the radius, in WKT.
     */
    public Geometry getCoords() {
        return coords;
    }

    /**
     * @param coords the queried POINT center of the radius.
     * @see #getCoords()
     */
    public void setCoords(Geometry coords) {
        this.coords = coords;
    }

    public GetRadius coords(Geometry coords) {
        setCoords(coords);
        return this;
    }

    /**
     * @return the vertical level(s) filter, or {@code null}.
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

    public GetRadius z(String z) {
        setZ(z);
        return this;
    }

    /**
     * @return the radius distance, or {@code null}.
     */
    public Double getWithin() {
        return within;
    }

    /**
     * @param within the radius distance.
     * @see #getWithin()
     */
    public void setWithin(Double within) {
        this.within = within;
    }

    public GetRadius within(Double within) {
        setWithin(within);
        return this;
    }

    /**
     * @return the unit of {@link #getWithin()}, or {@code null} for the default.
     */
    public String getWithinUnits() {
        return withinUnits;
    }

    /**
     * @param withinUnits the unit of {@link #getWithin()}.
     * @see #getWithinUnits()
     */
    public void setWithinUnits(String withinUnits) {
        this.withinUnits = withinUnits;
    }

    public GetRadius withinUnits(String withinUnits) {
        setWithinUnits(withinUnits);
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

    public GetRadius limit(Integer limit) {
        setLimit(limit);
        return this;
    }
}
