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
package org.geotoolkit.ogcapi.client.dggs;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;
import org.geotoolkit.ogcapi.model.dggs.Dggrs;
import org.geotoolkit.ogcapi.model.dggs.DggrsData;
import org.geotoolkit.ogcapi.model.dggs.DggrsListResponse;
import org.geotoolkit.ogcapi.model.dggs.DggrsZonesResponse;
import org.geotoolkit.ogcapi.model.dggs.Enumeration;
import org.geotoolkit.ogcapi.model.dggs.ZoneInfo;

public final class DggsApi extends AbstractOpenApi {

    /**
     * Subset name for latitude geographic CRS.
     */
    public static final String SUBSET_LAT = "Lat";
    /**
     * Subset name for longitude geographic CRS.
     */
    public static final String SUBSET_LON = "Lon";
    /**
     * Subset name for east projected CRS, which are to be interpreted as the related data axis in the CRS definition.
     */
    public static final String SUBSET_E = "E";
    /**
     * Subset name for north projected CRS, which are to be interpreted as the related data axis in the CRS definition.
     */
    public static final String SUBSET_N = "N";
    /**
     * Subset name for elevation above the ellipsoid in EPSG:4979 or CRS84h
     */
    public static final String SUBSET_H = "h";
    /**
     * Subset name for elevation in projected CRS, which are to be interpreted as the vertical axis in the CRS definition.
     */
    public static final String SUBSET_Z = "z";
    /**
     * Subset name for time for a temporal dataset.
     */
    public static final String SUBSET_TIME = "time";
    /**
     * For vector output formats, retrieve the zone centroid as geometry.
     */
    public static final String GEOMETRY_ZONE_CENTROID = "zone-centroid";
    /**
     * For vector output formats, retrieve the zone region as geometry.
     */
    public static final String GEOMETRY_ZONE_REGION = "zone-region";
    /**
     * For vector output formats, retrieve original geometries.
     */
    public static final String GEOMETRY_ZONE_VECTORIZED = "vectorized";
    /**
     * For vector output formats, do not return the geometry.
     */
    public static final String GEOMETRY_ZONE_NONE = "none";

    public static final String PROFILE_rfc7946 = "rfc7946";
    public static final String PROFILE_JSONFG = "jsonfg";
    public static final String PROFILE_JSONFG_PLUS = "jsonfg-plus";
    public static final String PROFILE_JSONFG_DGGS = "jsonfg-dggs";
    public static final String PROFILE_JSONFG_DGGS_PLUS = "jsonfg-dggs-plus";
    public static final String PROFILE_JSONFG_DGGS_ZONEIDS = "jsonfg-dggs-zoneids";
    public static final String PROFILE_JSONFG_DGGS_ZONEIDS_PLUS = "jsonfg-dggs-zoneids-plus";
    public static final String PROFILE_ZARR2 = "zarr2";
    public static final String PROFILE_ZARR2_DGGS = "zarr2-dggs";
    public static final String PROFILE_ZARR2_DGGS_ZONEIDS = "zarr2-dggs-zoneids";
    public static final String PROFILE_ZARR3 = "zarr3";
    public static final String PROFILE_ZARR3_DGGS = "zarr3-dggs";
    public static final String PROFILE_ZARR3_DGGS_ZONEIDS = "zarr3-dggs-zoneids";

    public DggsApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the list of collections available from this API implementation
     * &amp; deployment.
     *
     * @param f The format of the response. If no value is provided, the accept
     * header is used to determine the format. Accepted values are
     * &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ServiceResponse&lt;Enumeration&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<Enumeration> getAPICollections(@jakarta.annotation.Nullable String f) throws ServiceException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/api/all-collections", toPairs("f", f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), Enumeration.class);
    }

    /**
     * Retrieve the list of shared Discrete Global Grid Reference Systems
     * available from this API implementation &amp; deployment.
     *
     * @param f The format of the response. If no value is provided, the accept
     * header is used to determine the format. Accepted values are
     * &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ServiceResponse&lt;Enumeration&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<Enumeration> getAPIDGGRS(@jakarta.annotation.Nullable String f) throws ServiceException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/api/dggs", toPairs("f", f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), Enumeration.class);
    }

    /**
     * Retrieve the list of available DGGRS for the specified collection
     *
     * @param collectionId Local identifier of a collection (required)
     * @param f The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ServiceResponse&lt;DatasetGetDGGRSList200Response&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsListResponse> collectionGetDGGRSList(
            @jakarta.annotation.Nonnull String collectionId,
            @jakarta.annotation.Nullable String f)
            throws ServiceException {
        if (collectionId == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRSList");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(collectionId) + "/dggs", toPairs("f", f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), DggrsListResponse.class);
    }

    /**
     * Retrieve the description of the specified Discrete Global Grid Reference System in the context of a specified
     * collection
     *
     * @param dggrsId Identifier for a supported Discrete Global Grid System (required)
     * @param collectionId Local identifier of a collection (required)
     * @param f The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ServiceResponse&lt;Dggrs&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<Dggrs> collectionGetDGGRS(
            @jakarta.annotation.Nonnull String dggrsId,
            @jakarta.annotation.Nonnull String collectionId,
            @jakarta.annotation.Nullable String f)
            throws ServiceException {

        if (dggrsId == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling collectionGetDGGRS");
        }
        if (collectionId == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRS");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(collectionId) + "/dggs/" + urlEncode(dggrsId), toPairs("f", f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), Dggrs.class);
    }

    /**
     * Retrieve information about a DGGRS Zone, such as geometry and data availability, in the context of a specific
     * collection.
     *
     * @param dggrsId Identifier for a supported Discrete Global Grid System (required)
     * @param zoneId Identifier for a specific zone of a Discrete Global Grid Systems. This identifier usually includes
     * a component corresponding to a hierarchy level / scale / resolution, components identifying a spatial region, and
     * a optionally a temporal component. (required)
     * @param collectionId Local identifier of a collection (required)
     * @param collections The collections that should be included in the response. The parameter value is a
     * comma-separated list of collection identifiers. If the parameters is missing, some or all collections will be
     * included. This parameter may be useful for dataset-wide DGGS resources, but it is not defined by OGC API - DGGS -
     * Part 1. (optional)
     * @param datetime Either a date-time or an interval. Date and time expressions adhere to RFC 3339, section 5.6.
     * Intervals may be bounded or half-bounded (double-dots at start or end). Server implementations may or may not
     * support times expressed using time offsets from UTC, but need to support UTC time with the notation ending with a
     * Z. Examples: * A date-time: \&quot;2018-02-12T23:20:50Z\&quot; * A bounded interval:
     * \&quot;2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\&quot; * Half-bounded intervals:
     * \&quot;2018-02-12T00:00:00Z/..\&quot; or \&quot;../2018-03-18T12:31:12Z\&quot; Only resources that have a
     * temporal property that intersects the value of &#x60;datetime&#x60; are selected. If a feature has multiple
     * temporal properties, it is the decision of the server whether only a single temporal property is used to
     * determine the extent or all relevant temporal properties. (optional)
     * @return ServiceResponse&lt;Void&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<ZoneInfo> collectionGetDGGRSZoneInfo(
            @jakarta.annotation.Nonnull String dggrsId,
            @jakarta.annotation.Nonnull String zoneId,
            @jakarta.annotation.Nonnull String collectionId,
            @jakarta.annotation.Nullable List<String> collections,
            @jakarta.annotation.Nullable String datetime)
            throws ServiceException {
        if (dggrsId == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling collectionGetDGGRSZoneInfo");
        }
        if (zoneId == null) {
            throw new ServiceException(400, "Missing the required parameter 'zoneId' when calling collectionGetDGGRSZoneInfo");
        }
        if (collectionId == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRSZoneInfo");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", collections));
        queryParams.addAll(toPairs("datetime", datetime));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(collectionId) + "/dggs/" + urlEncode(dggrsId) + "/zones/" + urlEncode(zoneId), queryParams));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), ZoneInfo.class);
    }

    /**
     * Retrieve the description of the specified Discrete Global Grid Reference System
     *
     * @param dggrsId Identifier for a supported Discrete Global Grid System (required)
     * @param f The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ServiceResponse&lt;Dggrs&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<Dggrs> datasetGetDGGRS(
            @jakarta.annotation.Nonnull String dggrsId,
            @jakarta.annotation.Nullable String f)
            throws ServiceException {
        if (dggrsId == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling datasetGetDGGRS");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/dggs/" + urlEncode(dggrsId), toPairs("f", f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), Dggrs.class);
    }

    /**
     * Retrieve the list of available DGGRSs
     *
     * @param f The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ServiceResponse&lt;DatasetGetDGGRSList200Response&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsListResponse> datasetGetDGGRSList(
            @jakarta.annotation.Nullable String f)
            throws ServiceException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/dggs", toPairs("f", f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), DggrsListResponse.class);
    }

    /**
     * Retrieve information about a DGGRS Zone, such as geometry and data availability.
     *
     * @param dggrsId Identifier for a supported Discrete Global Grid System (required)
     * @param zoneId Identifier for a specific zone of a Discrete Global Grid Systems. This identifier usually includes
     * a component corresponding to a hierarchy level / scale / resolution, components identifying a spatial region, and
     * a optionally a temporal component. (required)
     * @param collections The collections that should be included in the response. The parameter value is a
     * comma-separated list of collection identifiers. If the parameters is missing, some or all collections will be
     * included. This parameter may be useful for dataset-wide DGGS resources, but it is not defined by OGC API - DGGS -
     * Part 1. (optional)
     * @param datetime Either a date-time or an interval. Date and time expressions adhere to RFC 3339, section 5.6.
     * Intervals may be bounded or half-bounded (double-dots at start or end). Server implementations may or may not
     * support times expressed using time offsets from UTC, but need to support UTC time with the notation ending with a
     * Z. Examples: * A date-time: \&quot;2018-02-12T23:20:50Z\&quot; * A bounded interval:
     * \&quot;2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\&quot; * Half-bounded intervals:
     * \&quot;2018-02-12T00:00:00Z/..\&quot; or \&quot;../2018-03-18T12:31:12Z\&quot; Only resources that have a
     * temporal property that intersects the value of &#x60;datetime&#x60; are selected. If a feature has multiple
     * temporal properties, it is the decision of the server whether only a single temporal property is used to
     * determine the extent or all relevant temporal properties. (optional)
     * @return ServiceResponse&lt;ZoneInfo&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<ZoneInfo> datasetGetDGGRSZoneInfo(
            @jakarta.annotation.Nonnull String dggrsId,
            @jakarta.annotation.Nonnull String zoneId,
            @jakarta.annotation.Nullable List<String> collections,
            @jakarta.annotation.Nullable String datetime)
            throws ServiceException {
        if (dggrsId == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling datasetGetDGGRSZoneInfo");
        }
        if (zoneId == null) {
            throw new ServiceException(400, "Missing the required parameter 'zoneId' when calling datasetGetDGGRSZoneInfo");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", collections));
        queryParams.addAll(toPairs("datetime", datetime));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/dggs/" + urlEncode(dggrsId) + "/zones/" + urlEncode(zoneId), queryParams));
        request.header("Accept", "application/json, application/geo+json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), ZoneInfo.class);
    }

    /**
     * Retrieve data from a DGGRS Zone for a specific collection. For a DGGRS defining a sub-zone order, optimized zone
     * data packets such as DGGS-(UB)JSON for raster data, or DGGS-(UB)JSON-FG for vector data can be used.
     *
     * @param dggrsId Identifier for a supported Discrete Global Grid System (required)
     * @param zoneId Identifier for a specific zone of a Discrete Global Grid Systems. This identifier usually includes
     * a component corresponding to a hierarchy level / scale / resolution, components identifying a spatial region, and
     * a optionally a temporal component. (required)
     * @param collectionId Local identifier of a collection (required)
     * @param f The format of the zone data response (e.g. GeoJSON, GeoTIFF). (optional)
     * @param properties Select specific data record fields (measured/observed properties) to be returned using a
     * comma-separated list of field names. The field name must be one of the fields defined in the associated data
     * resource&#39;s logical schema. Extensions may enable the use of complex expressions to support defining derived
     * fields, potentially also including the possibility to use aggregation functons. (optional)
     * @param excludeProperties Exclude specific data record fields (measured/observed properties) from being returned
     * using a comma-separated list of field names. The field name must be one of the fields defined in the associated
     * data resource&#39;s logical schema. (optional)
     * @param subset Retrieve only part of the data by slicing or trimming along one or more axis For trimming:
     * {axisAbbrev}({low}:{high}) (preserves dimensionality) For slicing: {axisAbbrev}({value}) (reduces dimensionality)
     * An asterisk (&#x60;*&#x60;) can be used instead of {low} or {high} to indicate the minimum/maximum value. For a
     * temporal dimension, a single asterisk can be used to indicate the high value. Support for &#x60;*&#x60; is
     * required for time, but optional for spatial and other dimensions. (optional)
     * @param filter The filter parameter specifies an expression in a query language (e.g. CQL2) for which an entire
     * feature will be returned if the filter predicate is matched. The language of the filter is specified by the
     * &#x60;filter-lang&#x60; query parameter. (optional)
     * @param crs reproject the output to the given crs (optional)
     * @param geometry For vector output formats, specify how to return the geometry and/or what the features of the
     * response should represent. &#x60;vectorized&#x60;: return features with regular non-rasterized, non-quantized
     * geometry &#x60;zone-centroid&#x60;: rasterize to zone features and use a Point geometry representing that zone
     * centroid &#x60;zone-region&#x60;: rasterize to zone features and use a (Multi)Polygon/Polyhedron geometry
     * representing that zone&#39;s region -- not supported for DGGS-JSON-FG profiles
     * (&#x60;profile&#x3D;jsonfg-dggs*&#x60;) &#x60;none&#x60;: (for zone listing) omit zone geometry -- not supported
     * for DGGS-JSON-FG profiles (&#x60;profile&#x3D;jsonfg-dggs*&#x60;) (optional)
     * @param profile Allows negotiating a particular profile of an output format, such as OGC Feature &amp; Geometry
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
     * @param datetime Either a date-time or an interval. Date and time expressions adhere to RFC 3339, section 5.6.
     * Intervals may be bounded or half-bounded (double-dots at start or end). Server implementations may or may not
     * support times expressed using time offsets from UTC, but need to support UTC time with the notation ending with a
     * Z. Examples: * A date-time: \&quot;2018-02-12T23:20:50Z\&quot; * A bounded interval:
     * \&quot;2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\&quot; * Half-bounded intervals:
     * \&quot;2018-02-12T00:00:00Z/..\&quot; or \&quot;../2018-03-18T12:31:12Z\&quot; Only resources that have a
     * temporal property that intersects the value of &#x60;datetime&#x60; are selected. If a feature has multiple
     * temporal properties, it is the decision of the server whether only a single temporal property is used to
     * determine the extent or all relevant temporal properties. (optional)
     * @param zoneDepth The DGGS resolution levels beyond the requested DGGS zone’s hierarchy level to include in the
     * response, when retrieving data for that zone. This can be either: • A single positive integer value —
     * representing a specific zone depth to return (e.g., &#x60;zone-depth&#x3D;5&#x60;); • A range of positive integer
     * values in the form “{low}-{high}” — representing a continuous range of zone depths to return (e.g.,
     * &#x60;zone-depth&#x3D;1-8&#x60;); or, • A comma separated list of at least two (2) positive integer values —
     * representing a set of specific zone depths to return (e.g., &#x60;zone-depth&#x3D;1,3,7&#x60;). Some or all of
     * these forms of the zone-depth parameter may not be supported with particular data packet encodings (the data
     * encoding may support a fixed depth, a range of depths, and/or an arbitrary selection of depths). When this
     * parameter is omitted, the default value specified in the &#x60;defaultDepth&#x60; property of the
     * &#x60;.../dggs/{dggrsId}&#x60; DGGRS description is used. (optional)
     * @param valuesOffset Specify the offset for a zone data output format such as PNG not supporting floating-point,
     * to be applied after multiplying by the scale factor and resulting in the encoded integer values (e.g., 8-bit or
     * 16-bit unsigned for PNG). (optional)
     * @param valuesScale Specify the scale factor for a zone data output format such as PNG not supporting
     * floating-point, to be applied before adding an offset and resulting in the encoded integer values (e.g., 8-bit or
     * 16-bit unsigned for PNG). (optional)
     * @return ServiceResponse&lt;DggsJson&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsData> collectionGetDGGRSZoneData(
            @jakarta.annotation.Nonnull String dggrsId,
            @jakarta.annotation.Nonnull String zoneId,
            @jakarta.annotation.Nonnull String collectionId,
            @jakarta.annotation.Nullable String f,
            @jakarta.annotation.Nullable String properties,
            @jakarta.annotation.Nullable String excludeProperties,
            @jakarta.annotation.Nullable List<String> subset,
            @jakarta.annotation.Nullable String filter,
            @jakarta.annotation.Nullable String crs,
            @jakarta.annotation.Nullable String geometry,
            @jakarta.annotation.Nullable String profile,
            @jakarta.annotation.Nullable String datetime,
            @jakarta.annotation.Nullable Object zoneDepth,
            @jakarta.annotation.Nullable Double valuesOffset,
            @jakarta.annotation.Nullable Double valuesScale)
            throws ServiceException {

        if (dggrsId == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling collectionGetDGGRSZoneData");
        }
        if (zoneId == null) {
            throw new ServiceException(400, "Missing the required parameter 'zoneId' when calling collectionGetDGGRSZoneData");
        }
        if (collectionId == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRSZoneData");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("f", f));
        queryParams.addAll(toPairs("properties", properties));
        queryParams.addAll(toPairs("exclude-properties", excludeProperties));
        queryParams.addAll(toPairs("csv", "subset", subset));
        queryParams.addAll(toPairs("filter", filter));
        queryParams.addAll(toPairs("crs", crs));
        queryParams.addAll(toPairs("geometry", geometry));
        queryParams.addAll(toPairs("profile", profile));
        queryParams.addAll(toPairs("datetime", datetime));
        queryParams.addAll(toPairs("zone-depth", zoneDepth));
        queryParams.addAll(toPairs("values-offset", valuesOffset));
        queryParams.addAll(toPairs("values-scale", valuesScale));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(collectionId) + "/dggs/" + urlEncode(dggrsId) + "/zones/" + urlEncode(zoneId) + "/data", queryParams));
        request.header("Accept", "application/json, image/png, image/tiff; application=geotiff, application/geo+json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), DggrsData.class);
    }

    /**
     * Retrieve data from a DGGRS Zone. For a DGGRS defining a sub-zone order, optimized zone data packets such as
     * DGGS-(UB)JSON for raster data, or DGGS-(UB)JSON-FG for vector data can be used.
     *
     * @param dggrsId Identifier for a supported Discrete Global Grid System (required)
     * @param zoneId Identifier for a specific zone of a Discrete Global Grid Systems. This identifier usually includes
     * a component corresponding to a hierarchy level / scale / resolution, components identifying a spatial region, and
     * a optionally a temporal component. (required)
     * @param collections The collections that should be included in the response. The parameter value is a
     * comma-separated list of collection identifiers. If the parameters is missing, some or all collections will be
     * included. This parameter may be useful for dataset-wide DGGS resources, but it is not defined by OGC API - DGGS -
     * Part 1. (optional)
     * @param f The format of the zone data response (e.g. GeoJSON, GeoTIFF). (optional)
     * @param properties Select specific data record fields (measured/observed properties) to be returned using a
     * comma-separated list of field names. The field name must be one of the fields defined in the associated data
     * resource&#39;s logical schema. Extensions may enable the use of complex expressions to support defining derived
     * fields, potentially also including the possibility to use aggregation functons. (optional)
     * @param excludeProperties Exclude specific data record fields (measured/observed properties) from being returned
     * using a comma-separated list of field names. The field name must be one of the fields defined in the associated
     * data resource&#39;s logical schema. (optional)
     * @param subset Retrieve only part of the data by slicing or trimming along one or more axis For trimming:
     * {axisAbbrev}({low}:{high}) (preserves dimensionality) For slicing: {axisAbbrev}({value}) (reduces dimensionality)
     * An asterisk (&#x60;*&#x60;) can be used instead of {low} or {high} to indicate the minimum/maximum value. For a
     * temporal dimension, a single asterisk can be used to indicate the high value. Support for &#x60;*&#x60; is
     * required for time, but optional for spatial and other dimensions. (optional)
     * @param filter The filter parameter specifies an expression in a query language (e.g. CQL2) for which an entire
     * feature will be returned if the filter predicate is matched. The language of the filter is specified by the
     * &#x60;filter-lang&#x60; query parameter. (optional)
     * @param crs reproject the output to the given crs (optional)
     * @param geometry For vector output formats, specify how to return the geometry and/or what the features of the
     * response should represent. &#x60;vectorized&#x60;: return features with regular non-rasterized, non-quantized
     * geometry &#x60;zone-centroid&#x60;: rasterize to zone features and use a Point geometry representing that zone
     * centroid &#x60;zone-region&#x60;: rasterize to zone features and use a (Multi)Polygon/Polyhedron geometry
     * representing that zone&#39;s region -- not supported for DGGS-JSON-FG profiles
     * (&#x60;profile&#x3D;jsonfg-dggs*&#x60;) &#x60;none&#x60;: (for zone listing) omit zone geometry -- not supported
     * for DGGS-JSON-FG profiles (&#x60;profile&#x3D;jsonfg-dggs*&#x60;) (optional)
     * @param profile Allows negotiating a particular profile of an output format, such as OGC Feature &amp; Geometry
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
     * @param datetime Either a date-time or an interval. Date and time expressions adhere to RFC 3339, section 5.6.
     * Intervals may be bounded or half-bounded (double-dots at start or end). Server implementations may or may not
     * support times expressed using time offsets from UTC, but need to support UTC time with the notation ending with a
     * Z. Examples: * A date-time: \&quot;2018-02-12T23:20:50Z\&quot; * A bounded interval:
     * \&quot;2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\&quot; * Half-bounded intervals:
     * \&quot;2018-02-12T00:00:00Z/..\&quot; or \&quot;../2018-03-18T12:31:12Z\&quot; Only resources that have a
     * temporal property that intersects the value of &#x60;datetime&#x60; are selected. If a feature has multiple
     * temporal properties, it is the decision of the server whether only a single temporal property is used to
     * determine the extent or all relevant temporal properties. (optional)
     * @param zoneDepth The DGGS resolution levels beyond the requested DGGS zone’s hierarchy level to include in the
     * response, when retrieving data for that zone. This can be either: • A single positive integer value —
     * representing a specific zone depth to return (e.g., &#x60;zone-depth&#x3D;5&#x60;); • A range of positive integer
     * values in the form “{low}-{high}” — representing a continuous range of zone depths to return (e.g.,
     * &#x60;zone-depth&#x3D;1-8&#x60;); or, • A comma separated list of at least two (2) positive integer values —
     * representing a set of specific zone depths to return (e.g., &#x60;zone-depth&#x3D;1,3,7&#x60;). Some or all of
     * these forms of the zone-depth parameter may not be supported with particular data packet encodings (the data
     * encoding may support a fixed depth, a range of depths, and/or an arbitrary selection of depths). When this
     * parameter is omitted, the default value specified in the &#x60;defaultDepth&#x60; property of the
     * &#x60;.../dggs/{dggrsId}&#x60; DGGRS description is used. (optional)
     * @param valuesOffset Specify the offset for a zone data output format such as PNG not supporting floating-point,
     * to be applied after multiplying by the scale factor and resulting in the encoded integer values (e.g., 8-bit or
     * 16-bit unsigned for PNG). (optional)
     * @param valuesScale Specify the scale factor for a zone data output format such as PNG not supporting
     * floating-point, to be applied before adding an offset and resulting in the encoded integer values (e.g., 8-bit or
     * 16-bit unsigned for PNG). (optional)
     * @return ServiceResponse&lt;DggsJson&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsData> datasetGetDGGRSZoneData(
            @jakarta.annotation.Nonnull String dggrsId,
            @jakarta.annotation.Nonnull String zoneId,
            @jakarta.annotation.Nullable List<String> collections,
            @jakarta.annotation.Nullable String f,
            @jakarta.annotation.Nullable String properties,
            @jakarta.annotation.Nullable String excludeProperties,
            @jakarta.annotation.Nullable List<String> subset,
            @jakarta.annotation.Nullable String filter,
            @jakarta.annotation.Nullable String crs,
            @jakarta.annotation.Nullable String geometry,
            @jakarta.annotation.Nullable String profile,
            @jakarta.annotation.Nullable String datetime,
            @jakarta.annotation.Nullable Object zoneDepth,
            @jakarta.annotation.Nullable Double valuesOffset,
            @jakarta.annotation.Nullable Double valuesScale)
            throws ServiceException {

        if (dggrsId == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling datasetGetDGGRSZoneData");
        }
        if (zoneId == null) {
            throw new ServiceException(400, "Missing the required parameter 'zoneId' when calling datasetGetDGGRSZoneData");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", collections));
        queryParams.addAll(toPairs("f", f));
        queryParams.addAll(toPairs("properties", properties));
        queryParams.addAll(toPairs("exclude-properties", excludeProperties));
        queryParams.addAll(toPairs("csv", "subset", subset));
        queryParams.addAll(toPairs("filter", filter));
        queryParams.addAll(toPairs("crs", crs));
        queryParams.addAll(toPairs("geometry", geometry));
        queryParams.addAll(toPairs("profile", profile));
        queryParams.addAll(toPairs("datetime", datetime));
        queryParams.addAll(toPairs("zone-depth", zoneDepth));
        queryParams.addAll(toPairs("values-offset", valuesOffset));
        queryParams.addAll(toPairs("values-scale", valuesScale));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/dggs/" + urlEncode(dggrsId) + "/zones/" + urlEncode(zoneId) + "/data", queryParams));
        request.header("Accept", "application/json, image/png, image/tiff; application=geotiff, application/geo+json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), DggrsData.class);
    }

    /**
     * Retrieve the list of zones with data for a specific collection, or for a particular query
     *
     * @param collectionId Local identifier of a collection (required)
     * @param dggrsId Identifier for a supported Discrete Global Grid System (required)
     * @param collections The collections that should be included in the response. The parameter value is a
     * comma-separated list of collection identifiers. If the parameters is missing, some or all collections will be
     * included. This parameter may be useful for dataset-wide DGGS resources, but it is not defined by OGC API - DGGS -
     * Part 1. (optional)
     * @param bbox Only resources that have a geometry that intersects the bounding box are selected. The bounding box
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
     * @param bboxCrs crs for the specified bbox (optional)
     * @param compactZones If set to true (default), when the list of DGGS zones to be returned at the requested
     * resolution (zone-level) includes all children of a parent zone, the parent zone will be returned as a shorthand
     * for that list of children zone. If set to false, all zones returned will be of the requested zone level.
     * (optional, default to true)
     * @param limit The optional limit parameter limits the number of zones that are presented in the response document.
     * * Minimum &#x3D; 1 * Maximum &#x3D; 10000 * Default &#x3D; 1000 (optional, default to 1000)
     * @param parentZone The optional parent zone parameter restricts a zone query to only return zones within that
     * parent zone. Used together with &#x60;zone-level&#x60;, it allows to explore the response for a large zone query
     * in a hierarchical manner. (optional)
     * @param offset The optional offset parameter indicates the offset within the result set from which the server
     * shall begin presenting results in the response document. The first element has an offset of 0 (default).
     * (optional, default to 0)
     * @param datetime Either a date-time or an interval. Date and time expressions adhere to RFC 3339, section 5.6.
     * Intervals may be bounded or half-bounded (double-dots at start or end). Server implementations may or may not
     * support times expressed using time offsets from UTC, but need to support UTC time with the notation ending with a
     * Z. Examples: * A date-time: \&quot;2018-02-12T23:20:50Z\&quot; * A bounded interval:
     * \&quot;2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\&quot; * Half-bounded intervals:
     * \&quot;2018-02-12T00:00:00Z/..\&quot; or \&quot;../2018-03-18T12:31:12Z\&quot; Only resources that have a
     * temporal property that intersects the value of &#x60;datetime&#x60; are selected. If a feature has multiple
     * temporal properties, it is the decision of the server whether only a single temporal property is used to
     * determine the extent or all relevant temporal properties. (optional)
     * @param subset Retrieve only part of the data by slicing or trimming along one or more axis For trimming:
     * {axisAbbrev}({low}:{high}) (preserves dimensionality) For slicing: {axisAbbrev}({value}) (reduces dimensionality)
     * An asterisk (&#x60;*&#x60;) can be used instead of {low} or {high} to indicate the minimum/maximum value. For a
     * temporal dimension, a single asterisk can be used to indicate the high value. Support for &#x60;*&#x60; is
     * required for time, but optional for spatial and other dimensions. (optional)
     * @param subsetCrs crs for the specified subset (optional)
     * @param crs reproject the output to the given crs (optional)
     * @param geometry For vector output formats, specify how to return the geometry and/or what the features of the
     * response should represent. &#x60;vectorized&#x60;: return features with regular non-rasterized, non-quantized
     * geometry &#x60;zone-centroid&#x60;: rasterize to zone features and use a Point geometry representing that zone
     * centroid &#x60;zone-region&#x60;: rasterize to zone features and use a (Multi)Polygon/Polyhedron geometry
     * representing that zone&#39;s region -- not supported for DGGS-JSON-FG profiles
     * (&#x60;profile&#x3D;jsonfg-dggs*&#x60;) &#x60;none&#x60;: (for zone listing) omit zone geometry -- not supported
     * for DGGS-JSON-FG profiles (&#x60;profile&#x3D;jsonfg-dggs*&#x60;) (optional)
     * @param profile Allows negotiating a particular profile of an output format, such as OGC Feature &amp; Geometry
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
     * @param filter The filter parameter specifies an expression in a query language (e.g. CQL2) for which an entire
     * feature will be returned if the filter predicate is matched. The language of the filter is specified by the
     * &#x60;filter-lang&#x60; query parameter. (optional)
     * @param filterLang The &#x60;filter-lang&#x60; parameter specifies the query language for the &#x60;filter&#x60;
     * query parameter. (optional)
     * @param f The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39;, &#39;html&#39;, &#39;geojson&#39;, &#39;geotiff&#39; or &#39;uint64&#39;.
     * (optional)
     * @return ServiceResponse&lt;DatasetGetDGGRSZones200Response&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsZonesResponse> collectionGetDGGRSZones(
            @jakarta.annotation.Nonnull String collectionId,
            @jakarta.annotation.Nonnull String dggrsId,
            @jakarta.annotation.Nullable List<String> collections,
            @jakarta.annotation.Nullable List<BigDecimal> bbox,
            @jakarta.annotation.Nullable String bboxCrs,
            @jakarta.annotation.Nullable Boolean compactZones,
            @jakarta.annotation.Nullable Integer limit,
            @jakarta.annotation.Nullable String parentZone,
            @jakarta.annotation.Nullable Integer offset,
            @jakarta.annotation.Nullable String datetime,
            @jakarta.annotation.Nullable List<String> subset,
            @jakarta.annotation.Nullable String subsetCrs,
            @jakarta.annotation.Nullable String crs,
            @jakarta.annotation.Nullable String geometry,
            @jakarta.annotation.Nullable String profile,
            @jakarta.annotation.Nullable String filter,
            @jakarta.annotation.Nullable String filterLang,
            @jakarta.annotation.Nullable String f)
            throws ServiceException {

        if (collectionId == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRSZones");
        }
        if (dggrsId == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling collectionGetDGGRSZones");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", collections));
        queryParams.addAll(toPairs("csv", "bbox", bbox));
        queryParams.addAll(toPairs("bbox-crs", bboxCrs));
        queryParams.addAll(toPairs("compact-zones", compactZones));
        queryParams.addAll(toPairs("limit", limit));
        queryParams.addAll(toPairs("parent-zone", parentZone));
        queryParams.addAll(toPairs("offset", offset));
        queryParams.addAll(toPairs("datetime", datetime));
        queryParams.addAll(toPairs("csv", "subset", subset));
        queryParams.addAll(toPairs("subset-crs", subsetCrs));
        queryParams.addAll(toPairs("crs", crs));
        queryParams.addAll(toPairs("geometry", geometry));
        queryParams.addAll(toPairs("profile", profile));
        queryParams.addAll(toPairs("filter", filter));
        queryParams.addAll(toPairs("filter-lang", filterLang));
        queryParams.addAll(toPairs("f", f));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(collectionId)+ "/dggs/" + urlEncode(dggrsId) + "/zones", queryParams));
        request.header("Accept", "application/json, image/png, application/geo+json, image/tiff; application=geotiff, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), DggrsZonesResponse.class);
    }

    /**
     * Retrieve the list of zones with data for this dataset, or for a particular query
     *
     * @param dggrsId Identifier for a supported Discrete Global Grid System (required)
     * @param collections The collections that should be included in the response. The parameter value is a
     * comma-separated list of collection identifiers. If the parameters is missing, some or all collections will be
     * included. This parameter may be useful for dataset-wide DGGS resources, but it is not defined by OGC API - DGGS -
     * Part 1. (optional)
     * @param bbox Only resources that have a geometry that intersects the bounding box are selected. The bounding box
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
     * @param bboxCrs crs for the specified bbox (optional)
     * @param zoneLevel The DGGS hierarchy level at which to return the list of zones. The precision of the calculation
     * to return the results depends on this parameter. Returned zones will have a level equal or smaller to this
     * specified level. If &#x60;compact-zones&#x60; is set to true, all returned zones will be of this zone level. If
     * not specified, this defaults to the most detailed zone that the system is able to return for the specific
     * request. (optional)
     * @param compactZones If set to true (default), when the list of DGGS zones to be returned at the requested
     * resolution (zone-level) includes all children of a parent zone, the parent zone will be returned as a shorthand
     * for that list of children zone. If set to false, all zones returned will be of the requested zone level.
     * (optional, default to true)
     * @param limit The optional limit parameter limits the number of zones that are presented in the response document.
     * * Minimum &#x3D; 1 * Maximum &#x3D; 10000 * Default &#x3D; 1000 (optional, default to 1000)
     * @param parentZone The optional parent zone parameter restricts a zone query to only return zones within that
     * parent zone. Used together with &#x60;zone-level&#x60;, it allows to explore the response for a large zone query
     * in a hierarchical manner. (optional)
     * @param offset The optional offset parameter indicates the offset within the result set from which the server
     * shall begin presenting results in the response document. The first element has an offset of 0 (default).
     * (optional, default to 0)
     * @param datetime Either a date-time or an interval. Date and time expressions adhere to RFC 3339, section 5.6.
     * Intervals may be bounded or half-bounded (double-dots at start or end). Server implementations may or may not
     * support times expressed using time offsets from UTC, but need to support UTC time with the notation ending with a
     * Z. Examples: * A date-time: \&quot;2018-02-12T23:20:50Z\&quot; * A bounded interval:
     * \&quot;2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\&quot; * Half-bounded intervals:
     * \&quot;2018-02-12T00:00:00Z/..\&quot; or \&quot;../2018-03-18T12:31:12Z\&quot; Only resources that have a
     * temporal property that intersects the value of &#x60;datetime&#x60; are selected. If a feature has multiple
     * temporal properties, it is the decision of the server whether only a single temporal property is used to
     * determine the extent or all relevant temporal properties. (optional)
     * @param subset Retrieve only part of the data by slicing or trimming along one or more axis For trimming:
     * {axisAbbrev}({low}:{high}) (preserves dimensionality) For slicing: {axisAbbrev}({value}) (reduces dimensionality)
     * An asterisk (&#x60;*&#x60;) can be used instead of {low} or {high} to indicate the minimum/maximum value. For a
     * temporal dimension, a single asterisk can be used to indicate the high value. Support for &#x60;*&#x60; is
     * required for time, but optional for spatial and other dimensions. (optional)
     * @param subsetCrs crs for the specified subset (optional)
     * @param crs reproject the output to the given crs (optional)
     * @param geometry For vector output formats, specify how to return the geometry and/or what the features of the
     * response should represent. &#x60;vectorized&#x60;: return features with regular non-rasterized, non-quantized
     * geometry &#x60;zone-centroid&#x60;: rasterize to zone features and use a Point geometry representing that zone
     * centroid &#x60;zone-region&#x60;: rasterize to zone features and use a (Multi)Polygon/Polyhedron geometry
     * representing that zone&#39;s region -- not supported for DGGS-JSON-FG profiles
     * (&#x60;profile&#x3D;jsonfg-dggs*&#x60;) &#x60;none&#x60;: (for zone listing) omit zone geometry -- not supported
     * for DGGS-JSON-FG profiles (&#x60;profile&#x3D;jsonfg-dggs*&#x60;) (optional)
     * @param profile Allows negotiating a particular profile of an output format, such as OGC Feature &amp; Geometry
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
     * @param filter The filter parameter specifies an expression in a query language (e.g. CQL2) for which an entire
     * feature will be returned if the filter predicate is matched. The language of the filter is specified by the
     * &#x60;filter-lang&#x60; query parameter. (optional)
     * @param filterLang The &#x60;filter-lang&#x60; parameter specifies the query language for the &#x60;filter&#x60;
     * query parameter. (optional)
     * @param f The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39;, &#39;html&#39;, &#39;geojson&#39;, &#39;geotiff&#39; or &#39;uint64&#39;.
     * (optional)
     * @return ServiceResponse&lt;DatasetGetDGGRSZones200Response&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsZonesResponse> datasetGetDGGRSZones(
            @jakarta.annotation.Nonnull String dggrsId,
            @jakarta.annotation.Nullable List<String> collections,
            @jakarta.annotation.Nullable List<BigDecimal> bbox,
            @jakarta.annotation.Nullable String bboxCrs,
            @jakarta.annotation.Nullable Integer zoneLevel,
            @jakarta.annotation.Nullable Boolean compactZones,
            @jakarta.annotation.Nullable Integer limit,
            @jakarta.annotation.Nullable String parentZone,
            @jakarta.annotation.Nullable Integer offset,
            @jakarta.annotation.Nullable String datetime,
            @jakarta.annotation.Nullable List<String> subset,
            @jakarta.annotation.Nullable String subsetCrs,
            @jakarta.annotation.Nullable String crs,
            @jakarta.annotation.Nullable String geometry,
            @jakarta.annotation.Nullable String profile,
            @jakarta.annotation.Nullable String filter,
            @jakarta.annotation.Nullable String filterLang,
            @jakarta.annotation.Nullable String f)
            throws ServiceException {

        if (dggrsId == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling datasetGetDGGRSZones");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", collections));
        queryParams.addAll(toPairs("csv", "bbox", bbox));
        queryParams.addAll(toPairs("bbox-crs", bboxCrs));
        queryParams.addAll(toPairs("zone-level", zoneLevel));
        queryParams.addAll(toPairs("compact-zones", compactZones));
        queryParams.addAll(toPairs("limit", limit));
        queryParams.addAll(toPairs("parent-zone", parentZone));
        queryParams.addAll(toPairs("offset", offset));
        queryParams.addAll(toPairs("datetime", datetime));
        queryParams.addAll(toPairs("csv", "subset", subset));
        queryParams.addAll(toPairs("subset-crs", subsetCrs));
        queryParams.addAll(toPairs("crs", crs));
        queryParams.addAll(toPairs("geometry", geometry));
        queryParams.addAll(toPairs("profile", profile));
        queryParams.addAll(toPairs("filter", filter));
        queryParams.addAll(toPairs("filter-lang", filterLang));
        queryParams.addAll(toPairs("f", f));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/dggs/" + urlEncode(dggrsId) +"/zones", queryParams));
        request.header("Accept", "application/json, image/png, application/geo+json, image/tiff; application=geotiff, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        final HttpResponse<InputStream> response = send(request);
        return toSimpleResponse(response, DggrsZonesResponse.class);
    }

}
