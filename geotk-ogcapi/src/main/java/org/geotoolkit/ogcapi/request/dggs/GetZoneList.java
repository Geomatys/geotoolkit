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
import org.opengis.geometry.Envelope;

/**
 * Request parameters for listing the DGGS zones available from a dataset:
 * {@code GET /dggs/{dggrsId}/zones}.
 *
 * <blockquote><q>
 *     For retrieving a list of DGGS zones.
 *     </q><i><small><a href="https://docs.ogc.org/is/21-038r1/21-038r1.html#_ffff8566-cf21-40a4-8473-4f5971589fd8"> OGC API - Discrete Global Grid Systems - Listing zones</a></small></i>
 * </blockquote>
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GetZoneList extends RequestParameters {

    private List<String> collectionId;
    private String dggrsId;
    private Envelope bbox;
    private Integer zoneLevel;
    private Boolean compactZones;
    private Integer limit;
    private String parentZone;
    private Integer offset;
    private List<String> subset;
    private String datetime;
    private String subsetCrs;
    private String crs;
    private String geometry;
    private String profile;
    private String filter;
    private String filterLang;
    private String format;

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
    public GetZoneList collectionId(List<String> collectionId) {
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
    public GetZoneList dggrsId(String dggrsId) {
        setDggrsId(dggrsId);
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
     *     Only resources that have a geometry that intersects the bounding box are selected.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/features/part1/1.0/openapi/parameters/bbox.yaml"> OGC API - Features - Part 1: Core - bbox parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Four or six numbers, depending on whether the CRS includes a vertical axis.</li>
     *     <li>Default CRS: WGS84 longitude/latitude (CRS84), unless overridden by {@code bbox-crs}.</li>
     * </ul>
     */
    public GetZoneList bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * @see #zoneLevel(java.lang.Integer)
     */
    public Integer getZoneLevel() {
        return zoneLevel;
    }

    /**
     * @see #zoneLevel(java.lang.Integer)
     */
    public void setZoneLevel(Integer zoneLevel) {
        this.zoneLevel = zoneLevel;
    }

    /**
     * <blockquote><q>
     *     The DGGS hierarchy level at which to return the list of zones. The precision of the calculation
     *     to return the results depends on this parameter.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/zone-level.yaml"> OGC API - Discrete Global Grid Systems - zone-level parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Returned zones have a level equal or smaller to this value.</li>
     *     <li>If {@code compact-zones} is true, all returned zones are exactly this level.</li>
     *     <li>Default: the most detailed zone level the system can return for the request.</li>
     * </ul>
     */
    public GetZoneList zoneLevel(Integer zoneLevel) {
        setZoneLevel(zoneLevel);
        return this;
    }

    /**
     * @see #compactZones(java.lang.Boolean)
     */
    public Boolean getCompactZones() {
        return compactZones;
    }

    /**
     * @see #compactZones(java.lang.Boolean)
     */
    public void setCompactZones(Boolean compactZones) {
        this.compactZones = compactZones;
    }

    /**
     * <blockquote><q>
     *     If set to true (default), when the list of DGGS zones to be returned at the requested
     *     resolution (zone-level) includes all children of a parent zone, the parent zone will be
     *     returned as a shorthand for that list of children zone.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/compact-zones.yaml"> OGC API - Discrete Global Grid Systems - compact-zones parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>If set to false, all zones returned will be of the requested zone level.</li>
     *     <li>Default: {@code true}.</li>
     * </ul>
     */
    public GetZoneList compactZones(Boolean compactZones) {
        setCompactZones(compactZones);
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
     *     The optional limit parameter limits the number of zones that are
     *     presented in the response document.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/limit.yaml"> OGC API - Discrete Global Grid Systems - limit parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Minimum = 1.</li>
     *     <li>Maximum = 10000.</li>
     *     <li>Default = 1000.</li>
     * </ul>
     * <pre>limit=10</pre>
     */
    public GetZoneList limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * @see #parentZone(java.lang.String)
     */
    public String getParentZone() {
        return parentZone;
    }

    /**
     * @see #parentZone(java.lang.String)
     */
    public void setParentZone(String parentZone) {
        this.parentZone = parentZone;
    }

    /**
     * <blockquote><q>
     *     The optional parent zone parameter restricts a zone query to only return zones within
     *     that parent zone.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/parent-zone.yaml"> OGC API - Discrete Global Grid Systems - parent-zone parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Used together with {@code zone-level}, allows exploring a large zone query hierarchically.</li>
     * </ul>
     */
    public GetZoneList parentZone(String parentZone) {
        setParentZone(parentZone);
        return this;
    }

    /**
     * @see #offset(java.lang.Integer)
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * @see #offset(java.lang.Integer)
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * The optional offset parameter indicates the offset within the result set from which the
     * server shall begin presenting results in the response document. The first element has an
     * offset of 0.
     * <br>
     * <ul>
     *     <li>Optional.</li>
     *     <li>Default: {@code 0}.</li>
     *     <li>No verbatim quotable text found for this parameter in the DGGS spec or its
     *     referenced OpenAPI schemas at the time of writing; wording follows the common OGC API
     *     pagination convention used elsewhere in this spec family.</li>
     * </ul>
     */
    public GetZoneList offset(Integer offset) {
        setOffset(offset);
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
    public GetZoneList subset(List<String> subset) {
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
     *     <li>Only resources with a temporal property intersecting the value are selected.</li>
     * </ul>
     * <pre>
     * datetime=2018-02-12T23:20:50Z
     * datetime=2018-02-12T00:00:00Z/2018-03-18T12:31:12Z
     * datetime=../2018-03-18T12:31:12Z
     * </pre>
     */
    public GetZoneList datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * @see #subsetCrs(java.lang.String)
     */
    public String getSubsetCrs() {
        return subsetCrs;
    }

    /**
     * @see #subsetCrs(java.lang.String)
     */
    public void setSubsetCrs(String subsetCrs) {
        this.subsetCrs = subsetCrs;
    }

    /**
     * <blockquote><q>
     *     crs for the specified subset
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/subset-crs.yaml"> OGC API - Discrete Global Grid Systems - subset-crs parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetZoneList subsetCrs(String subsetCrs) {
        setSubsetCrs(subsetCrs);
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
    public GetZoneList crs(String crs) {
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
     *     <li>{@code none}: omit zone geometry; not supported for DGGS-JSON-FG profiles.</li>
     * </ul>
     */
    public GetZoneList geometry(String geometry) {
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
     *     <li>For GeoJSON zone lists: {@code rfc7946}, {@code jsonfg}, {@code jsonfg-plus}.</li>
     * </ul>
     */
    public GetZoneList profile(String profile) {
        setProfile(profile);
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
    public GetZoneList filter(String filter) {
        setFilter(filter);
        return this;
    }

    /**
     * @see #filterLang(java.lang.String)
     */
    public String getFilterLang() {
        return filterLang;
    }

    /**
     * @see #filterLang(java.lang.String)
     */
    public void setFilterLang(String filterLang) {
        this.filterLang = filterLang;
    }

    /**
     * <blockquote><q>
     *     The {@code filter-lang} parameter specifies the query language for the {@code filter} query parameter.
     *     </q><i><small><a href="https://schemas.opengis.net/ogcapi/dggs/1.0/openapi/parameters/dggs-core/filter-lang.yaml"> OGC API - Discrete Global Grid Systems - filter-lang parameter</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     *     <li>Optional.</li>
     * </ul>
     */
    public GetZoneList filterLang(String filterLang) {
        setFilterLang(filterLang);
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
     *     <li>Allowed values: {@code json}, {@code html}, {@code geojson}, {@code geotiff}, {@code uint64}.</li>
     *     <li>Default: {@code json}.</li>
     * </ul>
     */
    public GetZoneList format(String format) {
        setFormat(format);
        return this;
    }
}
