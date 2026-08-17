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
 * The {@code corridor} EDR query
 * ({@code /collections/{collectionId}/corridor}, or, when {@link #getInstanceId()} is set,
 * {@code /collections/{collectionId}/instances/{instanceId}/corridor}).
 *
 * <blockquote><q>
 *     The Corridor query returns data along and around the path defined by the coords parameter.
 *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_e6b32ddf-d053-46ad-b5fd-c4daec62f4bb"> OGC API - Environmental Data Retrieval (EDR) - Corridor query</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetCorridor extends AbstractEdrDataQuery {

    private Geometry coords;

    private String z;

    private Double corridorWidth;

    private Double corridorHeight;

    private String widthUnits;

    private String heightUnits;

    private Double resolutionX;

    private Double resolutionY;

    /**
     * @see AbstractEdrQuery#getCollectionId()
     */
    public GetCorridor collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getInstanceId()
     */
    public GetCorridor instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getDatetime()
     */
    public GetCorridor datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getParameterName()
     */
    public GetCorridor parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getCrs()
     */
    public GetCorridor crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getF()
     */
    public GetCorridor f(String f) {
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
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_e6b32ddf-d053-46ad-b5fd-c4daec62f4bb"> OGC API - Environmental Data Retrieval (EDR) - Corridor query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; the trajectory supplied is the center line of the corridor.</li>
     *     <li>The Z value of a {@code LINESTRINGZ}/{@code LINESTRINGZM} is the vertical level.</li>
     *     <li>The M value of a {@code LINESTRINGM}/{@code LINESTRINGZM} is the datetime, in Unix
     *     epoch seconds, of each coordinate.</li>
     * </ul>
     * <pre>
     * coords=LINESTRING(-2.87 51.14,-2.98 51.36,-3.15 51.03)
     * coords=LINESTRINGZ(-2.87 51.14 0.1,-2.98 51.36 0.05,-3.15 51.03 0.15)
     * </pre>
     */
    public GetCorridor coords(Geometry coords) {
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
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_e6b32ddf-d053-46ad-b5fd-c4daec62f4bb"> OGC API - Environmental Data Retrieval (EDR) - Corridor query structure</a></small></i>
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
    public GetCorridor z(String z) {
        setZ(z);
        return this;
    }

    /**
     * @see #corridorWidth(java.lang.Double)
     */
    public Double getCorridorWidth() {
        return corridorWidth;
    }

    /**
     * @see #corridorWidth(java.lang.Double)
     */
    public void setCorridorWidth(Double corridorWidth) {
        this.corridorWidth = corridorWidth;
    }

    /**
     * <blockquote><q>
     *     The width value represents the whole width of the corridor where the trajectory
     *     supplied in the coords query parameter is the center point of the corridor
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_e6b32ddf-d053-46ad-b5fd-c4daec62f4bb"> OGC API - Environmental Data Retrieval (EDR) - Corridor query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; unit given by {@link #getWidthUnits()}.</li>
     * </ul>
     * <pre>corridor-width=100</pre>
     */
    public GetCorridor corridorWidth(Double corridorWidth) {
        setCorridorWidth(corridorWidth);
        return this;
    }

    /**
     * @see #corridorHeight(java.lang.Double)
     */
    public Double getCorridorHeight() {
        return corridorHeight;
    }

    /**
     * @see #corridorHeight(java.lang.Double)
     */
    public void setCorridorHeight(Double corridorHeight) {
        this.corridorHeight = corridorHeight;
    }

    /**
     * <blockquote><q>
     *     The height value represents the whole height of the corridor where the trajectory
     *     supplied in the coords query parameter is the center point of the corridor
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_e6b32ddf-d053-46ad-b5fd-c4daec62f4bb"> OGC API - Environmental Data Retrieval (EDR) - Corridor query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required; unit given by {@link #getHeightUnits()}.</li>
     * </ul>
     * <pre>corridor-height=100</pre>
     */
    public GetCorridor corridorHeight(Double corridorHeight) {
        setCorridorHeight(corridorHeight);
        return this;
    }

    /**
     * @see #widthUnits(java.lang.String)
     */
    public String getWidthUnits() {
        return widthUnits;
    }

    /**
     * @see #widthUnits(java.lang.String)
     */
    public void setWidthUnits(String widthUnits) {
        this.widthUnits = widthUnits;
    }

    /**
     * <blockquote><q>
     *     Defines the distance unit to be used to interpret the corridor-width parameter
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_e6b32ddf-d053-46ad-b5fd-c4daec62f4bb"> OGC API - Environmental Data Retrieval (EDR) - Corridor query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     * <pre>width-units=KM</pre>
     */
    public GetCorridor widthUnits(String widthUnits) {
        setWidthUnits(widthUnits);
        return this;
    }

    /**
     * @see #heightUnits(java.lang.String)
     */
    public String getHeightUnits() {
        return heightUnits;
    }

    /**
     * @see #heightUnits(java.lang.String)
     */
    public void setHeightUnits(String heightUnits) {
        this.heightUnits = heightUnits;
    }

    /**
     * <blockquote><q>
     *     Defines the distance unit to be used to interpret the corridor-height parameter
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_e6b32ddf-d053-46ad-b5fd-c4daec62f4bb"> OGC API - Environmental Data Retrieval (EDR) - Corridor query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     * <pre>height-units=hPa</pre>
     */
    public GetCorridor heightUnits(String heightUnits) {
        setHeightUnits(heightUnits);
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
    public GetCorridor resolutionX(Double resolutionX) {
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
    public GetCorridor resolutionY(Double resolutionY) {
        setResolutionY(resolutionY);
        return this;
    }
}
