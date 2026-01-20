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
     * The collections that should be included in the response. The parameter value is a
     * comma-separated list of collection identifiers. If the parameters is missing, some or all collections will be
     * included. This parameter may be useful for dataset-wide DGGS resources, but it is not defined by OGC API - DGGS -
     * Part 1. (optional)
     *
     * @return the collectionId
     */
    public List<String> getCollectionId() {
        return collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public void setCollectionId(List<String> collectionId) {
        this.collectionId = collectionId;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public GetZoneList collectionId(List<String> collectionId) {
        setCollectionId(collectionId);
        return this;
    }

    /**
     * Identifier for a supported Discrete Global Grid System (required)
     *
     * @return the dggrsId
     */
    public String getDggrsId() {
        return dggrsId;
    }

    /**
     * @param dggrsId the dggrsId to set
     * @see #getDggrsId()
     */
    public void setDggrsId(String dggrsId) {
        this.dggrsId = dggrsId;
    }

    /**
     * @param dggrsId the dggrsId to set
     * @see #getDggrsId()
     */
    public GetZoneList dggrsId(String dggrsId) {
        setDggrsId(dggrsId);
        return this;
    }

    /**
     * Only resources that have a geometry that intersects the bounding box are selected. The bounding box
     * is provided as four or six numbers, depending on whether the coordinate reference system includes a vertical axis
     * (elevation or depth): * Lower left corner, coordinate axis 1 * Lower left corner, coordinate axis 2 * Minimum
     * value, coordinate axis 3 (optional) * Upper right corner, coordinate axis 1 * Upper right corner, coordinate axis
     * 2 * Maximum value, coordinate axis 3 (optional) If the value consists of four numbers, the coordinate reference
     * system is WGS84 longitude/latitude (http://www.opengis.net/def/crs/OGC/1.3/CRS84) unless a different coordinate
     * reference system is specified in the parameter &#x60;bbox-crs&#x60;. If the value consists of six numbers, the
     * coordinate reference system is WGS 84 longitude/latitude/ellipsoidal height
     * (http://www.opengis.net/def/crs/OGC/0/CRS84h) unless a different coordinate reference system is specified in a
     * parameter &#x60;bbox-crs&#x60;. For WGS84 longitude/latitude the values are in most cases the sequence of minimum
     * longitude, minimum latitude, maximum longitude and maximum latitude. However, in cases where the box spans the
     * antimeridian the first value (west-most box edge) is larger than the third value (east-most box edge). If the
     * vertical axis is included, the third and the sixth number are the bottom and the top of the 3-dimensional
     * bounding box. If a resource has multiple spatial geometry properties, it is the decision of the server whether
     * only a single spatial geometry property is used to determine the extent or all relevant geometries. (optional)
     *
     * crs for the specified bbox (optional)
     *
     * @return the bbox
     */
    public Envelope getBbox() {
        return bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public void setBbox(Envelope bbox) {
        this.bbox = bbox;
    }

    /**
     * @param bbox the bbox to set
     * @see #getBbox()
     */
    public GetZoneList bbox(Envelope bbox) {
        setBbox(bbox);
        return this;
    }

    /**
     * The DGGS hierarchy level at which to return the list of zones. The precision of the calculation
     * to return the results depends on this parameter. Returned zones will have a level equal or smaller to this
     * specified level. If &#x60;compact-zones&#x60; is set to true, all returned zones will be of this zone level. If
     * not specified, this defaults to the most detailed zone that the system is able to return for the specific
     * request. (optional)
     *
     * @return the zoneLevel
     */
    public Integer getZoneLevel() {
        return zoneLevel;
    }

    /**
     * @param zoneLevel the zoneLevel to set
     * @see #getZoneLevel()
     */
    public void setZoneLevel(Integer zoneLevel) {
        this.zoneLevel = zoneLevel;
    }

    /**
     * @param zoneLevel the zoneLevel to set
     * @see #getZoneLevel()
     */
    public GetZoneList zoneLevel(Integer zoneLevel) {
        setZoneLevel(zoneLevel);
        return this;
    }

    /**
     * If set to true (default), when the list of DGGS zones to be returned at the requested
     * resolution (zone-level) includes all children of a parent zone, the parent zone will be returned as a shorthand
     * for that list of children zone. If set to false, all zones returned will be of the requested zone level.
     * (optional, default to true)
     *
     * @return the compactZones
     */
    public Boolean getCompactZones() {
        return compactZones;
    }

    /**
     * @param compactZones the compactZones to set
     * @see #getCompactZones()
     */
    public void setCompactZones(Boolean compactZones) {
        this.compactZones = compactZones;
    }

    /**
     * @param compactZones the compactZones to set
     * @see #getCompactZones()
     */
    public GetZoneList compactZones(Boolean compactZones) {
        setCompactZones(compactZones);
        return this;
    }

    /**
     * The optional limit parameter limits the number of zones that are presented in the response document.
     * * Minimum &#x3D; 1 * Maximum &#x3D; 10000 * Default &#x3D; 1000 (optional, default to 1000)
     *
     * @return the limit
     */
    public Integer getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    /**
     * @param limit the limit to set
     * @see #getLimit()
     */
    public GetZoneList limit(Integer limit) {
        setLimit(limit);
        return this;
    }

    /**
     * The optional parent zone parameter restricts a zone query to only return zones within that
     * parent zone. Used together with &#x60;zone-level&#x60;, it allows to explore the response for a large zone query
     * in a hierarchical manner. (optional)
     *
     * @return the parentZone
     */
    public String getParentZone() {
        return parentZone;
    }

    /**
     * @param parentZone the parentZone to set
     * @see #getParentZone()
     */
    public void setParentZone(String parentZone) {
        this.parentZone = parentZone;
    }

    /**
     * @param parentZone the parentZone to set
     * @see #getParentZone()
     */
    public GetZoneList parentZone(String parentZone) {
        setParentZone(parentZone);
        return this;
    }

    /**
     * The optional offset parameter indicates the offset within the result set from which the server
     * shall begin presenting results in the response document. The first element has an offset of 0 (default).
     * (optional, default to 0)
     *
     * @return the offset
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * @param offset the offset to set
     * @see #getOffset()
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    /**
     * @param offset the offset to set
     * @see #getOffset()
     */
    public GetZoneList offset(Integer offset) {
        setOffset(offset);
        return this;
    }

    /**
     * Retrieve only part of the data by slicing or trimming along one or more axis For trimming:
     * {axisAbbrev}({low}:{high}) (preserves dimensionality) For slicing: {axisAbbrev}({value}) (reduces dimensionality)
     * An asterisk (&#x60;*&#x60;) can be used instead of {low} or {high} to indicate the minimum/maximum value. For a
     * temporal dimension, a single asterisk can be used to indicate the high value. Support for &#x60;*&#x60; is
     * required for time, but optional for spatial and other dimensions. (optional)
     *
     * @return the subset
     */
    public List<String> getSubset() {
        return subset;
    }

    /**
     * @param subset the subset to set
     * @see #getSubset()
     */
    public void setSubset(List<String> subset) {
        this.subset = subset;
    }

    /**
     * @param subset the subset to set
     * @see #getSubset()
     */
    public GetZoneList subset(List<String> subset) {
        setSubset(subset);
        return this;
    }

    /**
     * Either a date-time or an interval. Date and time expressions adhere to RFC 3339, section 5.6.
     * Intervals may be bounded or half-bounded (double-dots at start or end). Server implementations may or may not
     * support times expressed using time offsets from UTC, but need to support UTC time with the notation ending with a
     * Z. Examples: * A date-time: \&quot;2018-02-12T23:20:50Z\&quot; * A bounded interval:
     * \&quot;2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\&quot; * Half-bounded intervals:
     * \&quot;2018-02-12T00:00:00Z/..\&quot; or \&quot;../2018-03-18T12:31:12Z\&quot; Only resources that have a
     * temporal property that intersects the value of &#x60;datetime&#x60; are selected. If a feature has multiple
     * temporal properties, it is the decision of the server whether only a single temporal property is used to
     * determine the extent or all relevant temporal properties. (optional)
     *
     * @return the datetime
     */
    public String getDatetime() {
        return datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    /**
     * @param datetime the datetime to set
     * @see #getDatetime()
     */
    public GetZoneList datetime(String datetime) {
        setDatetime(datetime);
        return this;
    }

    /**
     * crs for the specified subset (optional)
     *
     * @return the subsetCrs
     */
    public String getSubsetCrs() {
        return subsetCrs;
    }

    /**
     * @param subsetCrs the subsetCrs to set
     * @see #getSubsetCrs()
     */
    public void setSubsetCrs(String subsetCrs) {
        this.subsetCrs = subsetCrs;
    }

    /**
     * @param subsetCrs the subsetCrs to set
     * @see #getSubsetCrs()
     */
    public GetZoneList subsetCrs(String subsetCrs) {
        setSubsetCrs(subsetCrs);
        return this;
    }

    /**
     * reproject the output to the given crs (optional)
     *
     * @return the crs
     */
    public String getCrs() {
        return crs;
    }

    /**
     * @param crs the crs to set
     * @see #getCrs()
     */
    public void setCrs(String crs) {
        this.crs = crs;
    }

    /**
     * @param crs the crs to set
     * @see #getCrs()
     */
    public GetZoneList crs(String crs) {
        setCrs(crs);
        return this;
    }

    /**
     * For vector output formats, specify how to return the geometry and/or what the features of the
     * response should represent. &#x60;vectorized&#x60;: return features with regular non-rasterized, non-quantized
     * geometry &#x60;zone-centroid&#x60;: rasterize to zone features and use a Point geometry representing that zone
     * centroid &#x60;zone-region&#x60;: rasterize to zone features and use a (Multi)Polygon/Polyhedron geometry
     * representing that zone&#39;s region -- not supported for DGGS-JSON-FG profiles
     * (&#x60;profile&#x3D;jsonfg-dggs*&#x60;) &#x60;none&#x60;: (for zone listing) omit zone geometry -- not supported
     * for DGGS-JSON-FG profiles (&#x60;profile&#x3D;jsonfg-dggs*&#x60;) (optional)
     *
     * @return the geometry
     */
    public String getGeometry() {
        return geometry;
    }

    /**
     * @param geometry the geometry to set
     * @see #getGeometry()
     */
    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    /**
     * @param geometry the geometry to set
     * @see #getGeometry()
     */
    public GetZoneList geometry(String geometry) {
        setGeometry(geometry);
        return this;
    }

    /**
     * Allows negotiating a particular profile of an output format, such as OGC Feature &amp; Geometry
     * JSON (JSON-FG) or DGGS-JSON-FG output when requesting an &#x60;application/geo+json&#x60; media type for zone
     * data or zone list requests. For both zone data and zone lists in GeoJSON (&#x60;application/geo+json&#x60;):
     * &#x60;rfc7946&#x60;: return standard GeoJSON without using any extension &#x60;jsonfg&#x60;: return JSON-FG
     * representation &#x60;jsonfg-plus&#x60;: return JSON-FG representation with GeoJSON compatibility For zone data in
     * GeoJSON (&#x60;application/geo+json&#x60;): &#x60;jsonfg-dggs&#x60;: return DGGS-JSON-FG representation, using
     * &#x60;dggsPlace&#x60; to encode geometry points quantized to sub-zone, represented as local indices from 1 to the
     * number of sub-zones corresponding to the DGGRS deterministic sub-zone order, with a special value of 0
     * representing an artificial node &#x60;jsonfg-dggs-plus&#x60;: return DGGS-JSON-FG representation, with GeoJSON
     * compatibility &#x60;geometry&#x60; &#x60;jsonfg-dggs-zoneids&#x60;: return DGGS-JSON-FG representation, using
     * &#x60;dggsPlace&#x60; to encode geometry points as textual global zone identifiers, with a special value of
     * _null_ representing an artificial node &#x60;jsonfg-dggs-zoneids-plus&#x60;: return DGGS-JSON-FG representation,
     * encoding geometry points as global zone IDs, with GeoJSON compatibility &#x60;geometry&#x60; For zone data in
     * netCDF (&#x60;application/x-netcdf&#x60;): &#x60;netcdf3&#x60;: return NetCDF classic and 64-bit offset format
     * (not quantized to DGGH) &#x60;netcdf3-dggs&#x60;: return NetCDF classic and 64-bit offset format where one axis
     * corresponds to local sub-zone indices &#x60;netcdf3-dggs-zoneids&#x60;: return NetCDF classic and 64-bit offset
     * format where one axis corresponds to the global identifiers of sub-zones (textual or 64-bit integer)
     * &#x60;netcdf4&#x60;: return HFG5-based NetCDF 4 format (not quantized to DGGH) &#x60;netcdf4-dggs&#x60;: return
     * HDF5-based NetCDF 4 format where one axis corresponds to local sub-zone indices &#x60;netcdf4-dggs-zoneids&#x60;:
     * return HDF5-based NetCDF 4 format where one axis corresponds to the global identifiers of sub-zones (textual or
     * 64-bit integer) For zone data in zipped Zarr 2.0 (&#x60;application/zarr+zip&#x60;): &#x60;zarr2&#x60;: return
     * zipped Zarr 2.0 (not quantized to DGGH) &#x60;zarr2-dggs&#x60;: return zipped Zarr 2.0 where one axis corresponds
     * to local sub-zone indices &#x60;zarr2-dggs-zoneids&#x60;: return zipped Zarr 2.0 where one axis corresponds to
     * the global identifiers of sub-zones (textual or 64-bit integer) For zone data in CoverageJSON
     * (&#x60;application/prs.coverage+json&#x60;): &#x60;covjson&#x60;: return CoverageJSON (not quantized to DGGH)
     * &#x60;covjson-dggs&#x60;: return CoverageJSON where one axis corresponds to local sub-zone indices
     * &#x60;covjson-dggs-zoneids&#x60;: return CoverageJSON where one axis corresponds to the global identifiers of
     * sub-zones (textual or 64-bit integer) (optional)
     *
     * @return the profile
     */
    public String getProfile() {
        return profile;
    }

    /**
     * @param profile the profile to set
     * @see #getProfile()
     */
    public void setProfile(String profile) {
        this.profile = profile;
    }

    /**
     * @param profile the profile to set
     * @see #getProfile()
     */
    public GetZoneList profile(String profile) {
        setProfile(profile);
        return this;
    }

    /**
     * The filter parameter specifies an expression in a query language (e.g. CQL2) for which an entire
     * feature will be returned if the filter predicate is matched. The language of the filter is specified by the
     * &#x60;filter-lang&#x60; query parameter. (optional)
     *
     * @return the filter
     */
    public String getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     * @see #getFilter()
     */
    public void setFilter(String filter) {
        this.filter = filter;
    }

    /**
     * @param filter the filter to set
     * @see #getFilter()
     */
    public GetZoneList filter(String filter) {
        setFilter(filter);
        return this;
    }

    /**
     * The &#x60;filter-lang&#x60; parameter specifies the query language for the &#x60;filter&#x60;
     * query parameter. (optional)
     *
     * @return the filterLang
     */
    public String getFilterLang() {
        return filterLang;
    }

    /**
     * @param filterLang the filterLang to set
     * @see #getFilterLang()
     */
    public void setFilterLang(String filterLang) {
        this.filterLang = filterLang;
    }

    /**
     * @param filterLang the filterLang to set
     * @see #getFilterLang()
     */
    public GetZoneList filterLang(String filterLang) {
        setFilterLang(filterLang);
        return this;
    }

    /**
     * The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39;, &#39;html&#39;, &#39;geojson&#39;, &#39;geotiff&#39; or &#39;uint64&#39;.
     * (optional)
     *
     * @return the format
     */
    public String getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     * @see #getFormat()
     */
    public void setFormat(String format) {
        this.format = format;
    }

    /**
     * @param format the format to set
     * @see #getFormat()
     */
    public GetZoneList format(String format) {
        setFormat(format);
        return this;
    }
}
