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
 * The {@code area} EDR query
 * ({@code /collections/{collectionId}/area}, or, when {@link #getInstanceId()} is set,
 * {@code /collections/{collectionId}/instances/{instanceId}/area}).
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetArea extends AbstractEdrDataQuery {

    private Geometry coords;

    private String z;

    private Double resolutionX;

    private Double resolutionY;

    private Integer limit;

    public GetArea collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    public GetArea instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    public GetArea datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    public GetArea parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    public GetArea crs(String crs) {
        setCrs(crs);
        return this;
    }

    public GetArea f(String f) {
        setF(f);
        return this;
    }

    /**
     * @return a WKT {@code POLYGON} geometry defining the query area.
     */
    public Geometry getCoords() {
        return coords;
    }

    /**
     * @param coords the query area.
     * @see #getCoords()
     */
    public void setCoords(Geometry coords) {
        this.coords = coords;
    }

    public GetArea coords(Geometry coords) {
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

    public GetArea z(String z) {
        setZ(z);
        return this;
    }

    /**
     * @return the number of sample points in the response array along the x axis, or
     * {@code null}.
     */
    public Double getResolutionX() {
        return resolutionX;
    }

    /**
     * @param resolutionX the number of sample points in the response array along the x axis.
     * @see #getResolutionX()
     */
    public void setResolutionX(Double resolutionX) {
        this.resolutionX = resolutionX;
    }

    public GetArea resolutionX(Double resolutionX) {
        setResolutionX(resolutionX);
        return this;
    }

    /**
     * @return the number of sample points in the response array along the y axis, or
     * {@code null}.
     */
    public Double getResolutionY() {
        return resolutionY;
    }

    /**
     * @param resolutionY the number of sample points in the response array along the y axis.
     * @see #getResolutionY()
     */
    public void setResolutionY(Double resolutionY) {
        this.resolutionY = resolutionY;
    }

    public GetArea resolutionY(Double resolutionY) {
        setResolutionY(resolutionY);
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

    public GetArea limit(Integer limit) {
        setLimit(limit);
        return this;
    }
}
