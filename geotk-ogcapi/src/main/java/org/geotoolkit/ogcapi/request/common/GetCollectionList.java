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
package org.geotoolkit.ogcapi.request.common;

import org.geotoolkit.ogcapi.request.RequestParameters;
import org.opengis.geometry.Envelope;

/**
 * <blockquote><q>
 *     Implementations of OGC API Standards typically enable deployers to organize their
 *     geospatial data into collections.
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/20-024.html#collections-list"> OGC API - Common - Part 2: Geospatial Data</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetCollectionList extends RequestParameters {

    private String datetime;
    private Envelope bbox;
    private Integer limit;
    private String format;

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
     *     Either a date-time or an interval. Date and time expressions adhere to RFC 3339.
     *     Intervals may be bounded or half-bounded (double-dots at start or end).
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/datetime.yaml"> OGC API - Features - Part 1: Core - datetime parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Either a single date-time or an interval.</li>
     *     <li>Values conform to RFC 3339.</li>
     *     <li>Intervals may be bounded or half-bounded using {@code ..}.</li>
     * </ul>
     * <pre>
     * Examples:
     *
     * * A date-time: "2018-02-12T23:20:50Z"
     * * A bounded interval: "2018-02-12T00:00:00Z/2018-03-18T12:31:12Z"
     * * Half-bounded intervals: "2018-02-12T00:00:00Z/.." or "../2018-03-18T12:31:12Z"
     * </pre>
     */
    public GetCollectionList datetime(String datetime) {
        setDatetime(datetime);
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
     *     Only features that have a geometry that intersects the bounding box are selected.
     *     The bounding box is provided as four or six numbers, depending on whether the
     *     coordinate reference system includes a vertical axis (height or depth):
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/bbox.yaml"> OGC API - Features - Part 1: Core - bbox parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Four numbers (2D) or six numbers (3D).</li>
     *     <li>Default CRS: CRS84 (4 numbers) or CRS84h (6 numbers), unless {@code bbox-crs} is specified.</li>
     *     <li>A first longitude greater than the second indicates a box crossing the antimeridian.</li>
     * </ul>
     */
    public GetCollectionList bbox(Envelope bbox) {
        setBbox(bbox);
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
     *     The optional limit parameter limits the number of items that are presented in the response document.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/limit.yaml"> OGC API - Features - Part 1: Core - limit parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Minimum: 1.</li>
     *     <li>Maximum: 10000.</li>
     *     <li>Default: 10.</li>
     * </ul>
     */
    public GetCollectionList limit(Integer limit) {
        setLimit(limit);
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
    public GetCollectionList format(String format) {
        setFormat(format);
        return this;
    }
}
