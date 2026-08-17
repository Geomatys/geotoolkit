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

import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * Fields shared by every EDR query-type request ({@code position}, {@code radius}, {@code area},
 * {@code cube}, {@code trajectory}, {@code corridor}, {@code locations} and {@code location}):
 * the collection (and optionally instance) being queried, an optional temporal filter, and the
 * response format.
 *
 * <p>Only plain getters/setters are declared here, not fluent builder methods: a fluent method
 * declared on this class would return {@code AbstractEdrQuery}, breaking the chained-builder
 * pattern once a subclass method is called afterwards. Each subclass instead redeclares its own
 * one-line fluent method per inherited field, forwarding to the corresponding setter. The
 * specification quotes below are therefore attached to the getters, and each subclass's fluent
 * method links back to them.</p>
 *
 * @author Quentin Bialota (Geomatys)
 */
public abstract class AbstractEdrQuery extends RequestParameters {

    private String collectionId;

    private String instanceId;

    private String datetime;

    private String f;

    /**
     * <blockquote><q>
     *     local identifier of a collection
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/collectionId.yaml"> OGC API - Features - Part 1: Core - collectionId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     *
     * @return the identifier of the collection to query.
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the identifier of the collection to query.
     * @see #getCollectionId()
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     A unique identifier for the instance of the collection is specified as:
     *     /collections/{collectionId}/instance/{instanceId}
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_428a301b-7a38-4ae8-90de-1991802d77ef"> OGC API - Environmental Data Retrieval (EDR) - Parameter instanceId</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; when omitted, the collection is queried directly.</li>
     * </ul>
     * <pre>/collections/metar/instance/raw</pre>
     *
     * @return the identifier of the instance to query, or {@code null} to query the collection
     * directly.
     */
    public String getInstanceId() {
        return instanceId;
    }

    /**
     * @param instanceId the identifier of the instance to query.
     * @see #getInstanceId()
     */
    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    /**
     * <blockquote><q>
     *     Datetime range to return data for (the available range is defined in the temporal
     *     attribute of the extent section in the collections metadata response)
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_bbda46d4-04c5-426b-bea3-230d592fe1c2"> OGC API - Environmental Data Retrieval (EDR) - Position query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     * <pre>
     * datetime=2018-02-12T00:00Z
     * datetime=2018-02-12T00:00/2018-03-18T12:31Z
     * datetime=2018-02-12T00:00Z,2018-02-12T01:00Z,2018-02-14T12:00Z
     * </pre>
     *
     * @return the temporal filter (a date-time or interval), or {@code null}.
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the temporal filter (a date-time or interval).
     * @see #getDatetime()
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * <blockquote><q>
     *     Data format for the output data (available options are listed in the collections
     *     response), schemas describing JSON and XML outputs can be defined in the OpenAPI
     *     documentation (see https://swagger.io/docs/specification/data-models/)
     *     </q><i><small><a href="https://docs.ogc.org/is/19-086r6/19-086r6.html#_bbda46d4-04c5-426b-bea3-230d592fe1c2"> OGC API - Environmental Data Retrieval (EDR) - Position query structure</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional; content negotiation is used when omitted.</li>
     * </ul>
     * <pre>
     * f=GeoJSON
     * f=netCDF4
     * f=CoverageJSON
     * f=CSV
     * </pre>
     *
     * @return the requested response format, or {@code null} for content negotiation.
     */
    public String getF() {
        return f;
    }

    /**
     * @param f the requested response format.
     * @see #getF()
     */
    public void setF(String f) {
        this.f = f;
    }
}