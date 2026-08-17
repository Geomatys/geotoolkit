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
 * Request parameters for retrieving data from a single DGGS zone:
 * {@code GET /dggs/{dggrsId}/zones/{zoneId}/data}.
 *
 * <blockquote><q>
 *     For retrieving data for a single DGGS zone.
 *     </q><i><small><a href="https://docs.ogc.org/is/21-038r1/21-038r1.html#_f7ae3993-75c8-45db-9377-b41f91ab6097"> OGC API - Discrete Global Grid Systems - Retrieving data from a zone</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetZoneData extends RequestParameters {

    private List<String> collectionId;
    private String dggrsId;
    private String zoneId;
    private String format;
    private String properties;
    private String excludeProperties;
    private List<String> subset;
    private String datetime;
    private String filter;
    private String crs;
    private String geometry;
    private String profile;
    private String zoneDepth;
    private Double valuesOffset;
    private Double valuesScale;
    private boolean gzip;
    private String area; //experimental
    private String areaCrs; //experimental
    private String zoneAccuracy; //experimental

    /**
     * @see #collectionId(java.util.List)
     */
    public List<String> getCollectionId() {
        return collectionId;
    }

    /**
     * @see #collectionId(java.util.List)
     */
    public void setCollectionId(List<String> collectionId) {
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
     * </ul>
     */
    public GetZoneData collectionId(String collectionId) {
        setCollectionId(List.of(collectionId));
        return this;
    }

    /**
     * @see #collectionId(java.util.List)
     */
    public GetZoneData collectionId(List<String> collectionId) {
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
    public GetZoneData dggrsId(String dggrsId) {
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
    public GetZoneData zoneId(String zoneId) {
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
     *     <li>Default: {@code json}.</li>
     * </ul>
     */
    public GetZoneData format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * @see #properties(java.lang.String)
     */
    public String getProperties() {
        return properties;
    }

    /**
     * @see #properties(java.lang.String)
     */
    public void setProperties(String properties) {
        this.properties = properties;
    }

    /**
     * <blockquote><q>
     *     Select specific data record fields (measured/observed properties) to be returned using
     *     a comma-separated list of field names.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/properties.yaml"> OGC API - Discrete Global Grid Systems - properties parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>The field name must be one of the fields defined in the resource's logical schema.</li>
     * </ul>
     */
    public GetZoneData properties(String properties) {
        setProperties(properties);
        return this;
    }

    /**
     * @see #excludeProperties(java.lang.String)
     */
    public String getExcludeProperties() {
        return excludeProperties;
    }

    /**
     * @see #excludeProperties(java.lang.String)
     */
    public void setExcludeProperties(String excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    /**
     * <blockquote><q>
     *     Exclude specific data record fields (measured/observed properties) from being returned
     *     using a comma-separated list of field names.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/exclude-properties.yaml"> OGC API - Discrete Global Grid Systems - exclude-properties parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>The field name must be one of the fields defined in the resource's logical schema.</li>
     * </ul>
     */
    public GetZoneData excludeProperties(String excludeProperties) {
        setExcludeProperties(excludeProperties);
        return this;
    }

    /**
     * @see #subset(java.util.List)
     */
    public List<String> getSubset() {
        return subset;
    }

    /**
     * @see #subset(java.util.List)
     */
    public void setSubset(List<String> subset) {
        this.subset = subset;
    }

    /**
     * <blockquote><q>
     *     Retrieve only part of the data by slicing or trimming along one or more axis
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/subset.yaml"> OGC API - Discrete Global Grid Systems - subset parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Trimming: {@code axisAbbrev(low:high)} (preserves dimensionality).</li>
     *     <li>Slicing: {@code axisAbbrev(value)} (reduces dimensionality).</li>
     *     <li>An asterisk ({@code *}) indicates the minimum/maximum value.</li>
     * </ul>
     */
    public GetZoneData subset(List<String> subset) {
        setSubset(subset);
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
     * </ul>
     * <pre>
     * datetime=2018-02-12T23:20:50Z
     * datetime=2018-02-12T00:00:00Z/2018-03-18T12:31:12Z
     * datetime=../2018-03-18T12:31:12Z
     * </pre>
     */
    public GetZoneData datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see #filter(java.lang.String)
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @see #filter(java.lang.String)
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * <blockquote><q>
     *     The filter parameter specifies an expression in a query language (e.g. CQL2) for which
     *     an entire feature will be returned if the filter predicate is matched.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/filter.yaml"> OGC API - Discrete Global Grid Systems - filter parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>The query language is specified by {@code filter-lang}.</li>
     * </ul>
     */
    public GetZoneData filter(String filter) {
        setFilter(filter);
        return this;
    }

    /**
     * @see #crs(java.lang.String)
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @see #crs(java.lang.String)
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * <blockquote><q>
     *     reproject the output to the given crs
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/crs.yaml"> OGC API - Discrete Global Grid Systems - crs parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetZoneData crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * @see #geometry(java.lang.String)
     */
    public String getGeometry() {
        return geometry;
    }

    /**
     * @see #geometry(java.lang.String)
     */
    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    /**
     * <blockquote><q>
     *     For vector output formats, specify how to return the geometry and/or what the features
     *     of the response should represent.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/geometry.yaml"> OGC API - Discrete Global Grid Systems - geometry parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>{@code vectorized}: non-rasterized, non-quantized geometry.</li>
     *     <li>{@code zone-centroid}: rasterize to zone features, Point geometry at the zone centroid.</li>
     *     <li>{@code zone-region}: rasterize to zone features, (Multi)Polygon/Polyhedron region geometry;
     *     not supported for DGGS-JSON-FG profiles.</li>
     * </ul>
     */
    public GetZoneData geometry(String geometry) {
        setGeometry(geometry);
        return this;
    }

    /**
     * @see #profile(java.lang.String)
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @see #profile(java.lang.String)
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * <blockquote><q>
     *     Allows negotiating a particular profile of an output format, such as OGC Feature &amp;
     *     Geometry JSON (JSON-FG) or DGGS-JSON-FG output when requesting an {@code application/geo+json}
     *     media type for zone data or zone list requests.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/profile.yaml"> OGC API - Discrete Global Grid Systems - profile parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>For GeoJSON zone data: {@code rfc7946}, {@code jsonfg}, {@code jsonfg-plus},
     *     {@code jsonfg-dggs}, {@code jsonfg-dggs-plus}, {@code jsonfg-dggs-zoneids},
     *     {@code jsonfg-dggs-zoneids-plus}.</li>
     *     <li>For netCDF, Zarr and CoverageJSON zone data: {@code netcdf3(-dggs[-zoneids])},
     *     {@code netcdf4(-dggs[-zoneids])}, {@code zarr2(-dggs[-zoneids])}, {@code covjson(-dggs[-zoneids])}.</li>
     * </ul>
     */
    public GetZoneData profile(String profile) {
        setProfile(profile);
        return this;
    }

    /**
     * @see #zoneDepth(java.lang.String)
     */
    public String getZoneDepth() {
        return zoneDepth;
    }

    /**
     * @see #zoneDepth(java.lang.String)
     */
    public void setZoneDepth(String zoneDepth) {
        this.zoneDepth = zoneDepth;
    }

    /**
     * <blockquote><q>
     *     The DGGS resolution levels beyond the requested DGGS zone's hierarchy level to include
     *     in the response, when retrieving data for that zone.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/zone-depth.yaml"> OGC API - Discrete Global Grid Systems - zone-depth parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>A single positive integer (e.g. {@code zone-depth=5}).</li>
     *     <li>A range "{low}-{high}" (e.g. {@code zone-depth=1-8}).</li>
     *     <li>A comma-separated list of at least two values (e.g. {@code zone-depth=1,3,7}).</li>
     *     <li>Default: the {@code defaultDepth} property of the {@code .../dggs/{dggrsId}} DGGRS description.</li>
     * </ul>
     */
    public GetZoneData zoneDepth(String zoneDepth) {
        setZoneDepth(zoneDepth);
        return this;
    }

    /**
     * @see #valuesOffset(java.lang.Double)
     */
    public Double getValuesOffset() {
        return valuesOffset;
    }

    /**
     * @see #valuesOffset(java.lang.Double)
     */
    public void setValuesOffset(Double valuesOffset) {
        this.valuesOffset = valuesOffset;
    }

    /**
     * <blockquote><q>
     *     Specify the offset for a zone data output format such as PNG not supporting
     *     floating-point, to be applied after multiplying by the scale factor and resulting in
     *     the encoded integer values (e.g., 8-bit or 16-bit unsigned for PNG).
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/values-offset.yaml"> OGC API - Discrete Global Grid Systems - values-offset parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetZoneData valuesOffset(Double valuesOffset) {
        setValuesOffset(valuesOffset);
        return this;
    }

    /**
     * @see #valuesScale(java.lang.Double)
     */
    public Double getValuesScale() {
        return valuesScale;
    }

    /**
     * @see #valuesScale(java.lang.Double)
     */
    public void setValuesScale(Double valuesScale) {
        this.valuesScale = valuesScale;
    }

    /**
     * <blockquote><q>
     *     Specify the scale factor for a zone data output format such as PNG not supporting
     *     floating-point, to be applied before adding an offset and resulting in the encoded
     *     integer values (e.g., 8-bit or 16-bit unsigned for PNG).
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/values-scale.yaml"> OGC API - Discrete Global Grid Systems - values-scale parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetZoneData valuesScale(Double valuesScale) {
        setValuesScale(valuesScale);
        return this;
    }

    /**
     * @see #gzip(boolean)
     */
    public boolean isGzip() {
        return gzip;
    }

    /**
     * @see #gzip(boolean)
     */
    public void setGzip(boolean gzip) {
        this.gzip = gzip;
    }

    /**
     * Whether the response should be gzip-compressed.
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Not defined by OGC API - DGGS - Part 1 (no verbatim spec text available for this field).</li>
     * </ul>
     */
    public GetZoneData gzip(boolean gzip) {
        setGzip(gzip);
        return this;
    }

    /**
     * @see #area(java.lang.String)
     */
    public String getArea() {
        return area;
    }

    /**
     * @see #area(java.lang.String)
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * Experimental parameter, not defined by OGC API - DGGS - Part 1 (no verbatim spec text
     * available for this field).
     * <br>
     * <ul>
     *     <li>Optional, experimental.</li>
     * </ul>
     */
    public GetZoneData area(String area) {
        setArea(area);
        return this;
    }

    /**
     * @see #areaCrs(java.lang.String)
     */
    public String getAreaCrs() {
        return areaCrs;
    }

    /**
     * @see #areaCrs(java.lang.String)
     */
    public void setAreaCrs(String areaCrs) {
        this.areaCrs = areaCrs;
    }

    /**
     * Experimental parameter, not defined by OGC API - DGGS - Part 1 (no verbatim spec text
     * available for this field).
     * <br>
     * <ul>
     *     <li>Optional, experimental.</li>
     * </ul>
     */
    public GetZoneData areaCrs(String areaCrs) {
        setAreaCrs(areaCrs);
        return this;
    }

    /**
     * @see #zoneAccuracy(java.lang.String)
     */
    public String getZoneAccuracy() {
        return zoneAccuracy;
    }

    /**
     * @see #zoneAccuracy(java.lang.String)
     */
    public void setZoneAccuracy(String zoneAccuracy) {
        this.zoneAccuracy = zoneAccuracy;
    }

    /**
     * Experimental parameter, not defined by OGC API - DGGS - Part 1 (no verbatim spec text
     * available for this field).
     * <br>
     * <ul>
     *     <li>Optional, experimental.</li>
     * </ul>
     */
    public GetZoneData zoneAccuracy(String zoneAccuracy) {
        setZoneAccuracy(zoneAccuracy);
        return this;
    }
}
