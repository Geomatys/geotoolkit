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

import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper;
import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.dggs.Dggrs;
import org.geotoolkit.ogcapi.dto.dggs.DggrsData;
import org.geotoolkit.ogcapi.dto.dggs.DggrsListResponse;
import org.geotoolkit.ogcapi.dto.dggs.DggrsZonesResponse;
import org.geotoolkit.ogcapi.dto.dggs.Enumeration;
import org.geotoolkit.ogcapi.dto.dggs.ZoneInfo;
import org.geotoolkit.ogcapi.request.dggs.GetDggrs;
import org.geotoolkit.ogcapi.request.dggs.GetDggrsList;
import org.geotoolkit.ogcapi.request.dggs.GetZoneData;
import org.geotoolkit.ogcapi.request.dggs.GetZone;
import org.geotoolkit.ogcapi.request.dggs.GetZoneList;
import org.geotoolkit.ubjson.UBJsonMapper;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public final class DggsApi extends AbstractOpenApi {

    /**
     * Parameter names.
     */
    public static final String PARAM_BBOX               = "bbox";
    public static final String PARAM_BBOX_CRS           = "bbox-crs";
    public static final String PARAM_COLLECTIONS        = "collections";
    public static final String PARAM_COMPACT_ZONES      = "compact-zones";
    public static final String PARAM_CRS                = "crs";
    public static final String PARAM_DATETIME           = "datetime";
    public static final String PARAM_EXCLUDE_PROPERTIES = "exclude-properties";
    public static final String PARAM_F                  = "f";
    public static final String PARAM_FILTER             = "filter";
    public static final String PARAM_FILTER_LANG        = "filter-lang";
    public static final String PARAM_GEOMETRY           = "geometry";
    public static final String PARAM_LIMIT              = "limit";
    public static final String PARAM_OFFSET             = "offset";
    public static final String PARAM_PARENT_ZONE        = "parent-zone";
    public static final String PARAM_PROFILE            = "profile";
    public static final String PARAM_PROPERTIES         = "properties";
    public static final String PARAM_SUBSET             = "subset";
    public static final String PARAM_SUBSET_CRS         = "subset-crs";
    public static final String PARAM_VALUES_OFFSET      = "values-offset";
    public static final String PARAM_VALUES_SCALE       = "values-scale";
    public static final String PARAM_ZONE_DEPTH         = "zone-depth";
    public static final String PARAM_ZONE_LEVEL         = "zone-level";

    /**
     * Path fragments.
     */
    public static final String PATH_API_ALL_COLLECTIONS = "/api/all-collections";
    public static final String PATH_API_DGGS            = "/api/dggs";
    public static final String PATH_COLLECTIONS         = "/collections";
    public static final String PATH_DATA                = "/data";
    public static final String PATH_DGGS                = "/dggs";
    public static final String PATH_ZONES               = "/zones";

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
     * @return ServiceRequest&lt;Enumeration&gt;
     */
    public ServiceRequest<Supplier<Enumeration>> getAPICollections(String f) {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_API_ALL_COLLECTIONS, toPairs(PARAM_F, f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Enumeration.class);
    }

    /**
     * Retrieve the list of shared Discrete Global Grid Reference Systems
     * available from this API implementation &amp; deployment.
     *
     * @param f The format of the response. If no value is provided, the accept
     * header is used to determine the format. Accepted values are
     * &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ServiceRequest&lt;Enumeration&gt;
     */
    public ServiceRequest<Supplier<Enumeration>> getAPIDGGRS(String f) {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_API_DGGS, toPairs(PARAM_F, f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Enumeration.class);
    }

    /**
     * Retrieve the list of available DGGRS for the specified collection
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;DatasetGetDGGRSList200Response&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<DggrsListResponse>> collectionGetDGGRSList(GetDggrsList parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling collectionGetDGGRSList");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_DGGS, toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, DggrsListResponse.class);
    }

    /**
     * Retrieve the description of the specified Discrete Global Grid Reference System in the context of a specified
     * collection
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Dggrs&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Dggrs>> collectionGetDGGRS(GetDggrs parameters) throws IllegalArgumentException {

        if (parameters.getDggrsId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dggrsId' when calling collectionGetDGGRS");
        }
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling collectionGetDGGRS");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_DGGS + '/' + urlEncode(parameters.getDggrsId()), toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, Dggrs.class);
    }

    /**
     * Retrieve information about a DGGRS Zone, such as geometry and data availability, in the context of a specific
     * collection.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<ZoneInfo>> collectionGetDGGRSZoneInfo(GetZone parameters) throws IllegalArgumentException {
        if (parameters.getDggrsId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dggrsId' when calling collectionGetDGGRSZoneInfo");
        }
        if (parameters.getZoneId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'zoneId' when calling collectionGetDGGRSZoneInfo");
        }
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling collectionGetDGGRSZoneInfo");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_COLLECTIONS, parameters.getCollections()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_DGGS + '/' + urlEncode(parameters.getDggrsId()) + PATH_ZONES + '/' + urlEncode(parameters.getZoneId()), queryParams));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, ZoneInfo.class);
    }

    /**
     * Retrieve the description of the specified Discrete Global Grid Reference System
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Dggrs&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Dggrs>> datasetGetDGGRS(GetDggrs parameters) throws IllegalArgumentException {
        if (parameters.getDggrsId()== null) {
            throw new IllegalArgumentException("Missing the required parameter 'dggrsId' when calling datasetGetDGGRS");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DGGS + '/' + urlEncode(parameters.getDggrsId()), toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Dggrs.class);
    }

    /**
     * Retrieve the list of available DGGRSs
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;DatasetGetDGGRSList200Response&gt;
     */
    public ServiceRequest<Supplier<DggrsListResponse>> datasetGetDGGRSList(GetDggrsList parameters) {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DGGS, toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, DggrsListResponse.class);
    }

    /**
     * Retrieve information about a DGGRS Zone, such as geometry and data availability.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;ZoneInfo&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<ZoneInfo>> datasetGetDGGRSZoneInfo(GetZone parameters) throws IllegalArgumentException {
        if (parameters.getDggrsId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dggrsId' when calling datasetGetDGGRSZoneInfo");
        }
        if (parameters.getZoneId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'zoneId' when calling datasetGetDGGRSZoneInfo");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_COLLECTIONS, parameters.getCollections()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DGGS + '/' + urlEncode(parameters.getDggrsId()) + PATH_ZONES + '/' + urlEncode(parameters.getZoneId()), queryParams));
        request.header("Accept", "application/json, application/geo+json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, ZoneInfo.class);
    }

    /**
     * Retrieve data from a DGGRS Zone for a specific collection. For a DGGRS defining a sub-zone order, optimized zone
     * data packets such as DGGS-(UB)JSON for raster data, or DGGS-(UB)JSON-FG for vector data can be used.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;DggsJson&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<DggrsData>> collectionGetDGGRSZoneData(GetZoneData parameters) throws IllegalArgumentException {

        if (parameters.getDggrsId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dggrsId' when calling collectionGetDGGRSZoneData");
        }
        if (parameters.getZoneId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'zoneId' when calling collectionGetDGGRSZoneData");
        }
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling collectionGetDGGRSZoneData");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_F, parameters.getFormat()));
        queryParams.addAll(toPairs(PARAM_PROPERTIES, parameters.getProperties()));
        queryParams.addAll(toPairs(PARAM_EXCLUDE_PROPERTIES, parameters.getExcludeProperties()));
        queryParams.addAll(toPairs("csv", PARAM_SUBSET, parameters.getSubset()));
        queryParams.addAll(toPairs(PARAM_FILTER, parameters.getFilter()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_GEOMETRY, parameters.getGeometry()));
        queryParams.addAll(toPairs(PARAM_PROFILE, parameters.getProfile()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs(PARAM_ZONE_DEPTH, parameters.getZoneDepth()));
        queryParams.addAll(toPairs(PARAM_VALUES_OFFSET, parameters.getValuesOffset()));
        queryParams.addAll(toPairs(PARAM_VALUES_SCALE, parameters.getValuesScale()));

        final URI uri = toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId().get(0)) + PATH_DGGS + '/' + urlEncode(parameters.getDggrsId()) + PATH_ZONES + '/' + urlEncode(parameters.getZoneId()) + PATH_DATA, queryParams);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(uri);
        request.header("Accept", "application/json, image/png, image/tiff; application=geotiff, application/geo+json, application/cbor, text/html");
        if (parameters.isGzip()) request.setHeader("Accept-Encoding", "gzip");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        final String f = parameters.getFormat();
        if ("json".equals(f)) {
            return requestAsJson(request, DggrsData.class);
        } else if ("ubjson".equals(f)) {
            return requestAsJson(request, new UBJsonMapper(), DggrsData.class);
        } else if ("cbor".equals(f)) {
            return requestAsJson(request, new CBORMapper(), DggrsData.class);
        } else {
            return requestAsJson(request, DggrsData.class);
        }

    }

    /**
     * Retrieve data from a DGGRS Zone. For a DGGRS defining a sub-zone order, optimized zone data packets such as
     * DGGS-(UB)JSON for raster data, or DGGS-(UB)JSON-FG for vector data can be used.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;DggsJson&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<DggrsData>> datasetGetDGGRSZoneData(GetZoneData parameters) throws IllegalArgumentException {

        if (parameters.getDggrsId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dggrsId' when calling datasetGetDGGRSZoneData");
        }
        if (parameters.getZoneId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'zoneId' when calling datasetGetDGGRSZoneData");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_COLLECTIONS, parameters.getCollectionId()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getFormat()));
        queryParams.addAll(toPairs(PARAM_PROPERTIES, parameters.getProperties()));
        queryParams.addAll(toPairs(PARAM_EXCLUDE_PROPERTIES, parameters.getExcludeProperties()));
        queryParams.addAll(toPairs("csv", PARAM_SUBSET, parameters.getSubset()));
        queryParams.addAll(toPairs(PARAM_FILTER, parameters.getFilter()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_GEOMETRY, parameters.getGeometry()));
        queryParams.addAll(toPairs(PARAM_PROFILE, parameters.getProfile()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs(PARAM_ZONE_DEPTH, parameters.getZoneDepth()));
        queryParams.addAll(toPairs(PARAM_VALUES_OFFSET, parameters.getValuesOffset()));
        queryParams.addAll(toPairs(PARAM_VALUES_SCALE, parameters.getValuesScale()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DGGS + '/' + urlEncode(parameters.getDggrsId()) + PATH_ZONES + '/' + urlEncode(parameters.getZoneId()) + PATH_DATA, queryParams));
        request.header("Accept", "application/json, image/png, image/tiff; application=geotiff, application/geo+json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, DggrsData.class);
    }

    /**
     * Retrieve the list of zones with data for a specific collection, or for a particular query
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;DatasetGetDGGRSZones200Response&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<DggrsZonesResponse>> collectionGetDGGRSZones(GetZoneList parameters) throws IllegalArgumentException {

        if (parameters.getCollectionId().get(0) == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling collectionGetDGGRSZones");
        }
        if (parameters.getDggrsId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dggrsId' when calling collectionGetDGGRSZones");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_COLLECTIONS, parameters.getCollectionId()));
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_BBOX_CRS, toCrs(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_COMPACT_ZONES, parameters.getCompactZones()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        queryParams.addAll(toPairs(PARAM_PARENT_ZONE, parameters.getParentZone()));
        queryParams.addAll(toPairs(PARAM_OFFSET, parameters.getOffset()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", PARAM_SUBSET, parameters.getSubset()));
        queryParams.addAll(toPairs(PARAM_SUBSET_CRS, parameters.getSubsetCrs()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_GEOMETRY, parameters.getGeometry()));
        queryParams.addAll(toPairs(PARAM_PROFILE, parameters.getProfile()));
        queryParams.addAll(toPairs(PARAM_FILTER, parameters.getFilter()));
        queryParams.addAll(toPairs(PARAM_FILTER_LANG, parameters.getFilterLang()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getFormat()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId().get(0))+ PATH_DGGS + '/' + urlEncode(parameters.getDggrsId()) + PATH_ZONES, queryParams));
        request.header("Accept", "application/json, image/png, application/geo+json, image/tiff; application=geotiff, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, DggrsZonesResponse.class);
    }

    /**
     * Retrieve the list of zones with data for this dataset, or for a particular query
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;DatasetGetDGGRSZones200Response&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<DggrsZonesResponse>> datasetGetDGGRSZones(GetZoneList parameters) throws IllegalArgumentException {

        if (parameters.getDggrsId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'dggrsId' when calling datasetGetDGGRSZones");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_COLLECTIONS, parameters.getCollectionId()));
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_BBOX_CRS, toCrs(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_ZONE_LEVEL, parameters.getZoneLevel()));
        queryParams.addAll(toPairs(PARAM_COMPACT_ZONES, parameters.getCompactZones()));
        queryParams.addAll(toPairs(PARAM_LIMIT, parameters.getLimit()));
        queryParams.addAll(toPairs(PARAM_PARENT_ZONE, parameters.getParentZone()));
        queryParams.addAll(toPairs(PARAM_OFFSET, parameters.getOffset()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", PARAM_SUBSET, parameters.getSubset()));
        queryParams.addAll(toPairs(PARAM_SUBSET_CRS, parameters.getSubsetCrs()));
        queryParams.addAll(toPairs(PARAM_CRS, parameters.getCrs()));
        queryParams.addAll(toPairs(PARAM_GEOMETRY, parameters.getGeometry()));
        queryParams.addAll(toPairs(PARAM_PROFILE, parameters.getProfile()));
        queryParams.addAll(toPairs(PARAM_FILTER, parameters.getFilter()));
        queryParams.addAll(toPairs(PARAM_FILTER_LANG, parameters.getFilterLang()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getFormat()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_DGGS + '/' + urlEncode(parameters.getDggrsId()) +PATH_ZONES, queryParams));
        request.header("Accept", "application/json, image/png, application/geo+json, image/tiff; application=geotiff, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, DggrsZonesResponse.class);
    }

    private static List toList(Envelope env) {
        if (env == null) return null;
        final List lst = new ArrayList();
        lst.addAll(Arrays.stream(env.getLowerCorner().getCoordinates()).boxed().toList());
        lst.addAll(Arrays.stream(env.getUpperCorner().getCoordinates()).boxed().toList());
        return lst;
    }

    private static String toCrs(Envelope env) {
        if (env == null) return null;
        CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        return IdentifiedObjects.getIdentifierOrName(crs);
    }
}
