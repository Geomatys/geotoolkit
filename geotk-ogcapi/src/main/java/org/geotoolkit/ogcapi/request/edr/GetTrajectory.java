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
 * <blockquote><q>
 *     The Trajectory query returns data along the path defined by the coords parameter.
 *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_5181b3c7-ada4-40d9-bcf0-4c890a6087f1"> OGC API - Environmental Data Retrieval (EDR) - Trajectory query</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetTrajectory extends AbstractEdrDataQuery {

    private Geometry coords;

    private String z;

    /**
     * @see AbstractEdrQuery#getCollectionId()
     */
    public GetTrajectory collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getInstanceId()
     */
    public GetTrajectory instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getDatetime()
     */
    public GetTrajectory datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getParameterName()
     */
    public GetTrajectory parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getCrs()
     */
    public GetTrajectory crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getF()
     */
    public GetTrajectory f(String f) {
        setF(f);
        return this;
    }

    /**
     * @see #coords(org.locationtech.jts.geom.Geometry)
     */
    public Geometry getCoords() {
        return coords;
    }

    /**
     * @see #coords(org.locationtech.jts.geom.Geometry)
     */
    public void setCoords(Geometry coords) {
        this.coords = coords;
    }

    /**
     * <blockquote><q>
     *     The coordinates are defined by one of the following Well Known Text (WKT) strings:
     *     LINESTRING LINESTRINGZ LINESTRINGM LINESTRINGZM
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_5181b3c7-ada4-40d9-bcf0-4c890a6087f1"> OGC API - Environmental Data Retrieval (EDR) - Trajectory query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>The Z value of a {@code LINESTRINGZ}/{@code LINESTRINGZM} is the vertical level.</li>
     *     <li>The M value of a {@code LINESTRINGM}/{@code LINESTRINGZM} is the datetime, in Unix
     *     epoch seconds, of each coordinate.</li>
     * </ul>
     * <pre>
     * coords=LINESTRING(-2.87 51.14,-2.98 51.36,-3.15 51.03)
     * coords=LINESTRINGZ(-2.87 51.14 0.1,-2.98 51.36 0.05,-3.15 51.03 0.15)
     * coords=LINESTRINGM(-2.87 51.14 1560507000,-2.98 51.36 1560507600,-3.15 51.03 1560508200)
     * coords=LINESTRINGZM(-2.87 51.14 0.1 1560507000,-2.98 51.36 0.05 1560507600,-3.15 51.03 0.15 1560508200)
     * </pre>
     */
    public GetTrajectory coords(Geometry coords) {
        setCoords(coords);
        return this;
    }

    /**
     * @see #z(java.lang.String)
     */
    public String getZ() {
        return z;
    }

    /**
     * @see #z(java.lang.String)
     */
    public void setZ(String z) {
        this.z = z;
    }

    /**
     * <blockquote><q>
     *     The vertical level to return data for (available options are defined in the vertical
     *     attribute of the extent section in the collections metadata response)
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_5181b3c7-ada4-40d9-bcf0-4c890a6087f1"> OGC API - Environmental Data Retrieval (EDR) - Trajectory query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; ignored if the Z value is already given in a {@code LINESTRINGZ}/
     *     {@code LINESTRINGZM} coords value.</li>
     * </ul>
     * <pre>
     * z=850
     * z=1000,900,850,700
     * z=2/100
     * </pre>
     */
    public GetTrajectory z(String z) {
        setZ(z);
        return this;
    }
}
