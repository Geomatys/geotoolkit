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
     * Local identifier of a collection (required)
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
    public GetZoneData collectionId(String collectionId) {
        setCollectionId(List.of(collectionId));
        return this;
    }

    /**
     * @param collectionId the collectionId to set
     * @see #getCollectionId()
     */
    public GetZoneData collectionId(List<String> collectionId) {
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
    public GetZoneData dggrsId(String dggrsId) {
        setDggrsId(dggrsId);
        return this;
    }

    /**
     * Identifier for a specific zone of a Discrete Global Grid Systems. This identifier usually includes
     * a component corresponding to a hierarchy level / scale / resolution, components identifying a spatial region, and
     * a optionally a temporal component. (required)
     *
     * @return the zoneId
     */
    public String getZoneId() {
        return zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     * @see #getZoneId()
     */
    public void setZoneId(String zoneId) {
        this.zoneId = zoneId;
    }

    /**
     * @param zoneId the zoneId to set
     * @see #getZoneId()
     */
    public GetZoneData zoneId(String zoneId) {
        setZoneId(zoneId);
        return this;
    }

    /**
     * The format of the zone data response (e.g. GeoJSON, GeoTIFF). (optional)
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
    public GetZoneData format(String format) {
        setFormat(format);
        return this;
    }

    /**
     * Select specific data record fields (measured/observed properties) to be returned using a
     * comma-separated list of field names. The field name must be one of the fields defined in the associated data
     * resource&#39;s logical schema. Extensions may enable the use of complex expressions to support defining derived
     * fields, potentially also including the possibility to use aggregation functons. (optional)
     *
     * @return the properties
     */
    public String getProperties() {
        return properties;
    }

    /**
     * @param properties the properties to set
     * @see #getProperties()
     */
    public void setProperties(String properties) {
        this.properties = properties;
    }

    /**
     * @param properties the properties to set
     * @see #getProperties()
     */
    public GetZoneData properties(String properties) {
        setProperties(properties);
        return this;
    }

    /**
     * Exclude specific data record fields (measured/observed properties) from being returned
     * using a comma-separated list of field names. The field name must be one of the fields defined in the associated
     * data resource&#39;s logical schema. (optional)
     *
     * @return the excludeProperties
     */
    public String getExcludeProperties() {
        return excludeProperties;
    }

    /**
     * @param excludeProperties the excludeProperties to set
     * @see #getExcludeProperties()
     */
    public void setExcludeProperties(String excludeProperties) {
        this.excludeProperties = excludeProperties;
    }

    /**
     * @param excludeProperties the excludeProperties to set
     * @see #getExcludeProperties()
     */
    public GetZoneData excludeProperties(String excludeProperties) {
        setExcludeProperties(excludeProperties);
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
    public GetZoneData subset(List<String> subset) {
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
    public GetZoneData datetime(String datetime) {
        setDatetime(datetime);
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
    public GetZoneData filter(String filter) {
        setFilter(filter);
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
    public GetZoneData crs(String crs) {
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
    public GetZoneData geometry(String geometry) {
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
    public GetZoneData profile(String profile) {
        setProfile(profile);
        return this;
    }

    /**
     * The DGGS resolution levels beyond the requested DGGS zone’s hierarchy level to include in the
     * response, when retrieving data for that zone. This can be either: • A single positive integer value —
     * representing a specific zone depth to return (e.g., &#x60;zone-depth&#x3D;5&#x60;); • A range of positive integer
     * values in the form “{low}-{high}” — representing a continuous range of zone depths to return (e.g.,
     * &#x60;zone-depth&#x3D;1-8&#x60;); or, • A comma separated list of at least two (2) positive integer values —
     * representing a set of specific zone depths to return (e.g., &#x60;zone-depth&#x3D;1,3,7&#x60;). Some or all of
     * these forms of the zone-depth parameter may not be supported with particular data packet encodings (the data
     * encoding may support a fixed depth, a range of depths, and/or an arbitrary selection of depths). When this
     * parameter is omitted, the default value specified in the &#x60;defaultDepth&#x60; property of the
     * &#x60;.../dggs/{dggrsId}&#x60; DGGRS description is used. (optional)
     *
     * @return the zoneDepth
     */
    public String getZoneDepth() {
        return zoneDepth;
    }

    /**
     * @param zoneDepth the zoneDepth to set
     * @see #getZoneDepth()
     */
    public void setZoneDepth(String zoneDepth) {
        this.zoneDepth = zoneDepth;
    }

    /**
     * @param zoneDepth the zoneDepth to set
     * @see #getZoneDepth()
     */
    public GetZoneData zoneDepth(String zoneDepth) {
        setZoneDepth(zoneDepth);
        return this;
    }

    /**
     * Specify the offset for a zone data output format such as PNG not supporting floating-point,
     * to be applied after multiplying by the scale factor and resulting in the encoded integer values (e.g., 8-bit or
     * 16-bit unsigned for PNG). (optional)
     *
     * @return the valuesOffset
     */
    public Double getValuesOffset() {
        return valuesOffset;
    }

    /**
     *
     * @param valuesOffset the valuesOffset to set
     * @see #getValuesOffset()
     */
    public void setValuesOffset(Double valuesOffset) {
        this.valuesOffset = valuesOffset;
    }

    /**
     *
     * @param valuesOffset the valuesOffset to set
     * @see #getValuesOffset()
     */
    public GetZoneData valuesOffset(Double valuesOffset) {
        setValuesOffset(valuesOffset);
        return this;
    }

    /**
     * Specify the scale factor for a zone data output format such as PNG not supporting
     * floating-point, to be applied before adding an offset and resulting in the encoded integer values (e.g., 8-bit or
     * 16-bit unsigned for PNG). (optional)
     *
     * @return the valuesScale
     */
    public Double getValuesScale() {
        return valuesScale;
    }

    /**
     * @param valuesScale the valuesScale to set
     * @see #getValuesScale()
     */
    public void setValuesScale(Double valuesScale) {
        this.valuesScale = valuesScale;
    }

    /**
     * @param valuesScale the valuesScale to set
     * @see #getValuesScale()
     */
    public GetZoneData valuesScale(Double valuesScale) {
        setValuesScale(valuesScale);
        return this;
    }

    /**
     * @return the gzip
     */
    public boolean isGzip() {
        return gzip;
    }

    /**
     * @param gzip the gzip to set
     */
    public void setGzip(boolean gzip) {
        this.gzip = gzip;
    }

    /**
     * @param gzip the gzip to set
     */
    public GetZoneData gzip(boolean gzip) {
        setGzip(gzip);
        return this;
    }

    /**
     * @return the area
     */
    public String getArea() {
        return area;
    }

    /**
     * @param area the area to set
     */
    public void setArea(String area) {
        this.area = area;
    }

    /**
     * @param area the area to set
     */
    public GetZoneData area(String area) {
        setArea(area);
        return this;
    }

    /**
     * @return the areaCrs
     */
    public String getAreaCrs() {
        return areaCrs;
    }

    /**
     * @param areaCrs the areaCrs to set
     */
    public void setAreaCrs(String areaCrs) {
        this.areaCrs = areaCrs;
    }

    /**
     * @param areaCrs the areaCrs to set
     */
    public GetZoneData areaCrs(String areaCrs) {
        setAreaCrs(areaCrs);
        return this;
    }

    /**
     * @return the zoneAccuracy
     */
    public String getZoneAccuracy() {
        return zoneAccuracy;
    }

    /**
     * @param zoneAccuracy the zoneAccuracy to set
     */
    public void setZoneAccuracy(String zoneAccuracy) {
        this.zoneAccuracy = zoneAccuracy;
    }

    /**
     * @param zoneAccuracy the zoneAccuracy to set
     */
    public GetZoneData zoneAccuracy(String zoneAccuracy) {
        setZoneAccuracy(zoneAccuracy);
        return this;
    }
}
