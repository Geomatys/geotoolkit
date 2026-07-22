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
 * The {@code trajectory} EDR query
 * ({@code /collections/{collectionId}/trajectory}, or, when {@link #getInstanceId()} is set,
 * {@code /collections/{collectionId}/instances/{instanceId}/trajectory}).
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetTrajectory extends AbstractEdrDataQuery {

    private Geometry coords;

    private String z;

    public GetTrajectory collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    public GetTrajectory instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    public GetTrajectory datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    public GetTrajectory parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    public GetTrajectory crs(String crs) {
        setCrs(crs);
        return this;
    }

    public GetTrajectory f(String f) {
        setF(f);
        return this;
    }

    /**
     * @return a WKT {@code LINESTRING} geometry (with optional M-values encoding time/level per
     * the EDR trajectory syntax) defining the query trajectory.
     */
    public Geometry getCoords() {
        return coords;
    }

    /**
     * @param coords the trajectory geometry.
     * @see #getCoords()
     */
    public void setCoords(Geometry coords) {
        this.coords = coords;
    }

    public GetTrajectory coords(Geometry coords) {
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

    public GetTrajectory z(String z) {
        setZ(z);
        return this;
    }
}
