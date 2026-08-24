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
import org.opengis.geometry.Envelope;

/**
 * The {@code cube} EDR query
 * ({@code /collections/{collectionId}/cube}, or, when {@link #getInstanceId()} is set,
 * {@code /collections/{collectionId}/instances/{instanceId}/cube}).
 *
 * <blockquote><q>
 *     The Cube query returns a data cube defined by the bbox and z parameters.
 *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_fe30ac95-7038-4dd1-902d-f4fcd2f31c8d"> OGC API - Environmental Data Retrieval (EDR) - Cube query</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetCube extends AbstractEdrDataQuery {

    private Envelope bbox;

    private String z;

    private Double resolutionX;

    private Double resolutionY;

    private Double resolutionZ;

    /**
     * @see AbstractEdrQuery#getCollectionId()
     */
    public GetCube collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getInstanceId()
     */
    public GetCube instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getDatetime()
     */
    public GetCube datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getParameterName()
     */
    public GetCube parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getCrs()
     */
    public GetCube crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getF()
     */
    public GetCube f(String f) {
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
     * <blockquote><q>
     *     The coordinates are defined by a BBOX string. Only data that has a geometry that
     *     intersects the area defined by the bbox are selected.
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_fe30ac95-7038-4dd1-902d-f4fcd2f31c8d"> OGC API - Environmental Data Retrieval (EDR) - Cube query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>Format: {@code bbox=minx,miny,maxx,maxy}.</li>
     *     <li>Coordinates use the CRS given by {@link #getCrs()}, or WGS84 longitude/latitude
     *     (heights in meters above mean sea level) if unset.</li>
     * </ul>
     * <pre>bbox=-6.0,50.0,-4.35,52.0</pre>
     */
    public GetCube bbox(Envelope bbox) {
        setBbox(bbox);
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
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_fe30ac95-7038-4dd1-902d-f4fcd2f31c8d"> OGC API - Environmental Data Retrieval (EDR) - Cube query structure</a></small></i>
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
    public GetCube z(String z) {
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
    public GetCube resolutionX(Double resolutionX) {
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
    public GetCube resolutionY(Double resolutionY) {
        setResolutionY(resolutionY);
        return this;
    }

    /**
     * @see #resolutionZ(java.lang.Double)
     */
    public Double getResolutionZ() {
        return resolutionZ;
    }

    /**
     * @see #resolutionZ(java.lang.Double)
     */
    public void setResolutionZ(Double resolutionZ) {
        this.resolutionZ = resolutionZ;
    }

    /**
     * <blockquote><q>
     *     Defined if the user requires data at a different resolution from the native
     *     resolution of the data along the z-axis, it denotes the number of intervals to
     *     retrieve data for along the z-axis
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_e6b32ddf-d053-46ad-b5fd-c4daec62f4bb"> OGC API - Environmental Data Retrieval (EDR) - Corridor query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; the native data resolution is used when omitted.</li>
     * </ul>
     * <pre>resolution-z=100</pre>
     */
    public GetCube resolutionZ(Double resolutionZ) {
        setResolutionZ(resolutionZ);
        return this;
    }
}
