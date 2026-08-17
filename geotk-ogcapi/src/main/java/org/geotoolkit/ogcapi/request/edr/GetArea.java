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
 * <blockquote><q>
 *     The Area query returns data within the polygon defined by the coords parameter.
 *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_c92d1888-dc80-454f-8452-e2f070b90dcd"> OGC API - Environmental Data Retrieval (EDR) - Area query</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetArea extends AbstractEdrDataQuery {

    private Geometry coords;

    private String z;

    private Double resolutionX;

    private Double resolutionY;

    private Integer limit;

    /**
     * @see AbstractEdrQuery#getCollectionId()
     */
    public GetArea collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getInstanceId()
     */
    public GetArea instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getDatetime()
     */
    public GetArea datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getParameterName()
     */
    public GetArea parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getCrs()
     */
    public GetArea crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getF()
     */
    public GetArea f(String f) {
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
     *     The coordinates are defined by a Polygon Well Known Text (WKT) string
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_c92d1888-dc80-454f-8452-e2f070b90dcd"> OGC API - Environmental Data Retrieval (EDR) - Area query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>{@code POLYGON} or {@code MULTIPOLYGON} WKT geometry.</li>
     * </ul>
     * <pre>
     * coords=POLYGON((-6.1 50.3,-4.35 51.4,-2.6 51.6,-2.8 50.6,-5.3 49.9,-6.1 50.3))
     * coords=MULTIPOLYGON(((-15 48.8,-15 60.95,5 60.85,5 48.8,-15 48.8)),-6.1 50.3,-4.35 51.4,-2.6 51.6,-2.8 50.6,-5.3 49.9,-6.1 50.3)
     * </pre>
     */
    public GetArea coords(Geometry coords) {
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
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_c92d1888-dc80-454f-8452-e2f070b90dcd"> OGC API - Environmental Data Retrieval (EDR) - Area query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     * <pre>
     * z=850
     * z=1000,900,850,700
     * z=2/100
     * </pre>
     */
    public GetArea z(String z) {
        setZ(z);
        return this;
    }

    /**
     * @see #resolutionX(java.lang.Double)
     */
    public Double getResolutionX() {
        return resolutionX;
    }

    /**
     * @see #resolutionX(java.lang.Double)
     */
    public void setResolutionX(Double resolutionX) {
        this.resolutionX = resolutionX;
    }

    /**
     * <blockquote><q>
     *     Defined if the user requires data at a different resolution from the native
     *     resolution of the data along the x-axis, it denotes the number of intervals to
     *     retrieve data for along the x-axis
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_e6b32ddf-d053-46ad-b5fd-c4daec62f4bb"> OGC API - Environmental Data Retrieval (EDR) - Corridor query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; the native data resolution is used when omitted.</li>
     * </ul>
     * <pre>resolution-x=10</pre>
     */
    public GetArea resolutionX(Double resolutionX) {
        setResolutionX(resolutionX);
        return this;
    }

    /**
     * @see #resolutionY(java.lang.Double)
     */
    public Double getResolutionY() {
        return resolutionY;
    }

    /**
     * @see #resolutionY(java.lang.Double)
     */
    public void setResolutionY(Double resolutionY) {
        this.resolutionY = resolutionY;
    }

    /**
     * <blockquote><q>
     *     Defined if the user requires data at a different resolution from the native
     *     resolution of the data along the y-axis, it denotes the number of intervals to
     *     retrieve data for along the y-axis
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_e6b32ddf-d053-46ad-b5fd-c4daec62f4bb"> OGC API - Environmental Data Retrieval (EDR) - Corridor query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; the native data resolution is used when omitted.</li>
     * </ul>
     * <pre>resolution-y=5</pre>
     */
    public GetArea resolutionY(Double resolutionY) {
        setResolutionY(resolutionY);
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
    public GetArea limit(Integer limit) {
        setLimit(limit);
        return this;
    }
}
