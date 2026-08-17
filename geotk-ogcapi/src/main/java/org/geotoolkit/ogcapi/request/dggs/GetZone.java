/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2025, Geomatys
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
package org.geotoolkit.ogcapi.request.dggs;

import org.geotoolkit.ogcapi.request.RequestParameters;
import java.util.List;

/**
 * Request parameters for retrieving information about a single DGGS zone:
 * {@code GET /dggs/{dggrsId}/zones/{zoneId}}, or, when {@link #getCollectionId()} is set,
 * {@code GET /collections/{collectionId}/dggs/{dggrsId}/zones/{zoneId}}.
 *
 * <blockquote><q>
 *     For retrieving information for a particular DGGRS zone
 *     </q><i><small><a href="https://docs.ogc.org/is/21-038r1/21-038r1.html#_7f61498d-9ced-46ab-9f26-0f65e889b805"> OGC API - Discrete Global Grid Systems - Zone Info</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetZone extends RequestParameters {

    private String collectionId;
    private String dggrsId;
    private String zoneId;
    private String format;
    private List<String> collections;
    private String datetime;

    /**
     * @see #collectionId(java.lang.String)
     */
    public String getCollectionId() {
        return collectionId;
    }

    /**
     * @see #collectionId(java.lang.String)
     */
    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * <blockquote><q>
     *     local identifier of a collection
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/collectionId.yaml"> OGC API - Features - Part 1: Core - collectionId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional: only set when requesting a zone for a specific collection.</li>
     * </ul>
     */
    public GetZone collectionId(String collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * @see #dggrsId(java.lang.String)
     */
    public String getDggrsId() {
        return dggrsId;
    }

    /**
     * @see #dggrsId(java.lang.String)
     */
    public void setDggrsId(String dggrsId) {
        this.dggrsId = dggrsId;
    }

    /**
     * <blockquote><q>
     *     Identifier for a supported Discrete Global Grid System
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/dggrsId.yaml"> OGC API - Discrete Global Grid Systems - dggrsId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     */
    public GetZone dggrsId(String dggrsId) {
        setDggrsId(dggrsId);
        return this;
    }

    /**
     * @see #zoneId(java.lang.String)
     */
    public String getZoneId() {
        return zoneId;
    }

    /**
     * @see #zoneId(java.lang.String)
     */
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * <blockquote><q>
     *     Identifier for a specific zone of a Discrete Global Grid Systems. This identifier usually includes
     *     a component corresponding to a hierarchy level / scale / resolution, components identifying a
     *     spatial region, and a optionally a temporal component.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/zoneId.yaml"> OGC API - Discrete Global Grid Systems - zoneId parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Required.</li>
     * </ul>
     * <pre>zoneId=B2-1-A</pre>
     */
    public GetZone zoneId(String zoneId) {
        setZoneId(zoneId);
        return this;
    }

    /**
     * @see #format(java.lang.String)
     */
    public String getFormat() {
        return format;
    }

    /**
     * @see #format(java.lang.String)
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * <blockquote><q>
     *     The optional f parameter indicates the output format that the server
     *     shall provide as part of the response document. The default format is JSON.
     *     </q><i><small><a href="https://docs.ogc.org/is/19-072/19-072.html#json-api-definition"> OGC API - Common - Part 1: Core</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Allowed values: {@code json}, {@code html}.</li>
     *     <li>Default: {@code json}.</li>
     * </ul>
     */
    public GetZone format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * @see #collections(java.util.List)
     */
    public List<String> getCollections() {
        return collections;
    }

    /**
     * @see #collections(java.util.List)
     */
    public void setCollections(List<String> collections) {
        this.collections = collections;
    }

    /**
     * The collections that should be included in the response. The parameter value is a
     * comma-separated list of collection identifiers. If the parameters is missing, some or all
     * collections will be included.
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Useful for dataset-wide DGGS resources, but not defined by OGC API - DGGS - Part 1
     *     (no verbatim spec text available for this field).</li>
     * </ul>
     */
    public GetZone collections(List<String> collections) {
        setCollections(collections);
        return this;
    }

    /**
     * @see #datetime(java.lang.String)
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @see #datetime(java.lang.String)
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * <blockquote><q>
     *     Either a date-time or an interval.
     *     </q><i><small><a href="https://docs.ogc.org/is/21-038r1/21-038r1.html#_d848832e-add3-4f04-8b3c-c2cb57359cb7"> OGC API - Discrete Global Grid Systems - datetime parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Date and time expressions adhere to RFC 3339.</li>
     *     <li>Intervals may be bounded or half-bounded (double-dots at start or end).</li>
     *     <li>Only resources with a temporal property intersecting the value are selected.</li>
     * </ul>
     * <pre>
     * datetime=2018-02-12T23:20:50Z
     * datetime=2018-02-12T00:00:00Z/2018-03-18T12:31:12Z
     * datetime=../2018-03-18T12:31:12Z
     * </pre>
     */
    public GetZone datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }
}
