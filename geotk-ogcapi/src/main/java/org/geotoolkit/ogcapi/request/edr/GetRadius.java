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
 * <blockquote><q>
 *     The Radius query returns data within the defined radius of the requested coordinate.
 *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_fa3fccfc-df4b-43c0-9195-243d3594226e"> OGC API - Environmental Data Retrieval (EDR) - Radius query</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetRadius extends AbstractEdrDataQuery {

    private Geometry coords;

    private String z;

    private Double within;

    private String withinUnits;

    private Integer limit;

    /**
     * @see AbstractEdrQuery#getCollectionId()
     */
    public GetRadius collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getInstanceId()
     */
    public GetRadius instanceId(String instanceId) {
        setInstanceId(instanceId);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getDatetime()
     */
    public GetRadius datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getParameterName()
     */
    public GetRadius parameterName(List<String> parameterName) {
        setParameterName(parameterName);
        return this;
    }

    /**
     * @see AbstractEdrDataQuery#getCrs()
     */
    public GetRadius crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * @see AbstractEdrQuery#getF()
     */
    public GetRadius f(String f) {
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
     *     The coordinates are defined by a Point Well Known Text (WKT) string
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_fa3fccfc-df4b-43c0-9195-243d3594226e"> OGC API - Environmental Data Retrieval (EDR) - Radius query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     *     <li>{@code POINT} or {@code MULTIPOINT} WKT geometry.</li>
     * </ul>
     * <pre>
     * coords=POINT(-3 51)
     * coords= MULTIPOINT((-77 38.9),(2.35 48.85),(116.38 39.92),(149.1 -35.29),(-0.1 51.5))
     * </pre>
     */
    public GetRadius coords(Geometry coords) {
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
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_fa3fccfc-df4b-43c0-9195-243d3594226e"> OGC API - Environmental Data Retrieval (EDR) - Radius query structure</a></small></i>
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
    public GetRadius z(String z) {
        setZ(z);
        return this;
    }

    /**
     * @see #within(java.lang.Double)
     */
    public Double getWithin() {
        return within;
    }

    /**
     * @see #within(java.lang.Double)
     */
    public void setWithin(Double within) {
        this.within = within;
    }

    /**
     * <blockquote><q>
     *     Defines radius of area around defined coordinates to include in the data selection
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_fa3fccfc-df4b-43c0-9195-243d3594226e"> OGC API - Environmental Data Retrieval (EDR) - Radius query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     * <pre>within=20</pre>
     */
    public GetRadius within(Double within) {
        setWithin(within);
        return this;
    }

    /**
     * @see #withinUnits(java.lang.String)
     */
    public String getWithinUnits() {
        return withinUnits;
    }

    /**
     * @see #withinUnits(java.lang.String)
     */
    public void setWithinUnits(String withinUnits) {
        this.withinUnits = withinUnits;
    }

    /**
     * <blockquote><q>
     *     Distance units for the within parameter (available options are defined in the
     *     within_units attribute of the radius data_query section in the collections metadata
     *     response)
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_fa3fccfc-df4b-43c0-9195-243d3594226e"> OGC API - Environmental Data Retrieval (EDR) - Radius query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     * <pre>within-units=Miles</pre>
     */
    public GetRadius withinUnits(String withinUnits) {
        setWithinUnits(withinUnits);
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
    public GetRadius limit(Integer limit) {
        setLimit(limit);
        return this;
    }
}
