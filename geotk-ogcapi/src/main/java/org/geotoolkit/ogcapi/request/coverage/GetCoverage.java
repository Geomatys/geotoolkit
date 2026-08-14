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
package org.geotoolkit.ogcapi.request.coverage;

import java.util.List;
import org.geotoolkit.ogcapi.request.RequestParameters;

/**
 * <blockquote><q>
 *     The implementation SHALL support the HTTP GET operation to retrieve the coverage (from
 *     the coverage resource linked to as [ogc-rel:coverage], typically /collections/{collectionId}/coverage).
 *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/19-087.html#_coverage_data_retrieval"> OGC API - Coverages - Part 1: Core</a></small></i>
 * </blockquote>
 *
 * @author Quentin Bialota (Geomatys)
 */
public final class GetCoverage extends RequestParameters {

    private String collectionId;
    private String format;
    private List<Double> bbox;
    private String bboxCrs;
    private List<String> properties;
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
     *     <li>Required.</li>
     *     <li>Must match the {@code id} of an existing collection (see {@code GET /collections}).</li>
     * </ul>
     */
    public GetCoverage collectionId(String collectionId) {
        setCollectionId(collectionId);
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
    public GetCoverage format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * @see #bbox(java.util.List)
     */
    public List<Double> getBbox() {
        return bbox;
    }

    /**
     * @see #bbox(java.util.List)
     */
    public void setBbox(List<Double> bbox) {
        this.bbox = bbox;
    }

    /**
     * <blockquote><q>
     *     The coverage resource SHALL support a bbox parameter representing a spatial area
     *     to subset. The bounding box SHALL be provided as four or six numbers, depending on whether the coordinate reference system includes a vertical axis (height or depth):
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/19-087.html#bbox-parameter"> OGC API - Coverages - Part 1: Core</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Four numbers (2D) or six numbers (3D).</li>
     *     <li>Default CRS: CRS84 (4 numbers) or CRS84h (6 numbers), unless {@code bbox-crs} is specified.</li>
     *     <li>A first longitude greater than the second indicates a box crossing the antimeridian.</li>
     * </ul>
     * <pre>bbox=160.6,-55.95,-170,-25.89</pre>
     */
    public GetCoverage bbox(List<Double> bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * @see #bboxCrs(java.lang.String)
     */
    public String getBboxCrs() {
        return bboxCrs;
    }

    /**
     * @see #bboxCrs(java.lang.String)
     */
    public void setBboxCrs(String bboxCrs) {
        this.bboxCrs = bboxCrs;
    }

    /**
     * <blockquote><q>
     *     The coverage resource SHALL support a bbox-crs parameter specifying the CRS used
     *     for the bbox parameter.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/19-087.html#bbox-crs-parameter"> OGC API - Coverages - Part 1: Core</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Default: {@code https://www.opengis.net/def/crs/OGC/1.3/CRS84} if not specified.</li>
     *     <li>The native storage CRS is always supported as a value.</li>
     * </ul>
     */
    public GetCoverage bboxCrs(String bboxCrs) {
        setBboxCrs(bboxCrs);
        return this;
    }

    /**
     * @see #properties(java.util.List)
     */
    public List<String> getProperties() {
        return properties;
    }

    /**
     * @see #properties(java.util.List)
     */
    public void setProperties(List<String> properties) {
        this.properties = properties;
    }

    /**
     * <blockquote><q>
     *     The coverage retrieval operation SHALL support a properties query parameter
     *     where the value is a comma-separated list of field names to be returned.
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/19-087.html#_query_parameter_properties"> OGC API - Coverages - Part 1: Core</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Comma-separated list of field names.</li>
     *     <li>An unrecognized field returns a 400 error.</li>
     *     <li>Response fields are ordered as requested, if the format supports field order.</li>
     * </ul>
     * <pre>GET /ogcapi/collections/sentinel2-l2a/coverage?bbox=30,40,50,70&amp;properties=B08,B04 HTTP/1.1</pre>
     */
    public GetCoverage properties(List<String> properties) {
        setProperties(properties);
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
     *     The datetime convenience query parameter maps to a trimming or slicing operation
     *     along the primary temporal dimension (which could also be expressed using subset and the time dimension).
     *     </q><i><small><a href="https://docs.ogc.org/DRAFTS/19-087.html#datetime-parameter-subset-requirements"> OGC API - Coverages - Part 1: Core</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>A single instant performs a slicing operation; an interval (separated by {@code /}) performs a trimming operation.</li>
     *     <li>Instants use RFC 3339 (UTC / Gregorian calendar).</li>
     *     <li>A double-dot ({@code ..}) or empty string denotes an unbounded or half-bounded interval.</li>
     * </ul>
     * <pre>
     * datetime=2020-01-01
     * datetime=2020-01-01/2020-01-31
     * </pre>
     */
    public GetCoverage datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }
}
