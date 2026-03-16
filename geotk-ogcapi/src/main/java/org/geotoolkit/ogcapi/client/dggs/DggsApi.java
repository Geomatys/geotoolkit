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
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.sis.referencing.IdentifiedObjects;
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
     * @param parameters request parameters
     * @return ServiceResponse&lt;DatasetGetDGGRSList200Response&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsListResponse> collectionGetDGGRSList(GetDggrsList parameters)
            throws ServiceException {
        if (parameters.getCollectionId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRSList");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/dggs", toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), DggrsListResponse.class);
    }

    /**
     * Retrieve the description of the specified Discrete Global Grid Reference System in the context of a specified
     * collection
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;Dggrs&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<Dggrs> collectionGetDGGRS(GetDggrs parameters)
            throws ServiceException {

        if (parameters.getDggrsId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling collectionGetDGGRS");
        }
        if (parameters.getCollectionId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRS");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/dggs/" + urlEncode(parameters.getDggrsId()), toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), Dggrs.class);
    }

    /**
     * Retrieve information about a DGGRS Zone, such as geometry and data availability, in the context of a specific
     * collection.
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;Void&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<ZoneInfo> collectionGetDGGRSZoneInfo(GetZone parameters)
            throws ServiceException {
        if (parameters.getDggrsId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling collectionGetDGGRSZoneInfo");
        }
        if (parameters.getZoneId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'zoneId' when calling collectionGetDGGRSZoneInfo");
        }
        if (parameters.getCollectionId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRSZoneInfo");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", parameters.getCollections()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/dggs/" + urlEncode(parameters.getDggrsId()) + "/zones/" + urlEncode(parameters.getZoneId()), queryParams));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), ZoneInfo.class);
    }

    /**
     * Retrieve the description of the specified Discrete Global Grid Reference System
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;Dggrs&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<Dggrs> datasetGetDGGRS(GetDggrs parameters) throws ServiceException {
        if (parameters.getDggrsId()== null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling datasetGetDGGRS");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/dggs/" + urlEncode(parameters.getDggrsId()), toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), Dggrs.class);
    }

    /**
     * Retrieve the list of available DGGRSs
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;DatasetGetDGGRSList200Response&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsListResponse> datasetGetDGGRSList(GetDggrsList parameters) throws ServiceException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/dggs", toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), DggrsListResponse.class);
    }

    /**
     * Retrieve information about a DGGRS Zone, such as geometry and data availability.
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;ZoneInfo&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<ZoneInfo> datasetGetDGGRSZoneInfo(GetZone parameters) throws ServiceException {
        if (parameters.getDggrsId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling datasetGetDGGRSZoneInfo");
        }
        if (parameters.getZoneId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'zoneId' when calling datasetGetDGGRSZoneInfo");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", parameters.getCollections()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/dggs/" + urlEncode(parameters.getDggrsId()) + "/zones/" + urlEncode(parameters.getZoneId()), queryParams));
        request.header("Accept", "application/json, application/geo+json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), ZoneInfo.class);
    }

    /**
     * Retrieve data from a DGGRS Zone for a specific collection. For a DGGRS defining a sub-zone order, optimized zone
     * data packets such as DGGS-(UB)JSON for raster data, or DGGS-(UB)JSON-FG for vector data can be used.
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;DggsJson&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsData> collectionGetDGGRSZoneData(GetZoneData parameters) throws ServiceException {

        if (parameters.getDggrsId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling collectionGetDGGRSZoneData");
        }
        if (parameters.getZoneId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'zoneId' when calling collectionGetDGGRSZoneData");
        }
        if (parameters.getCollectionId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRSZoneData");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("f", parameters.getFormat()));
        queryParams.addAll(toPairs("properties", parameters.getProperties()));
        queryParams.addAll(toPairs("exclude-properties", parameters.getExcludeProperties()));
        queryParams.addAll(toPairs("csv", "subset", parameters.getSubset()));
        queryParams.addAll(toPairs("filter", parameters.getFilter()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("geometry", parameters.getGeometry()));
        queryParams.addAll(toPairs("profile", parameters.getProfile()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("zone-depth", parameters.getZoneDepth()));
        queryParams.addAll(toPairs("values-offset", parameters.getValuesOffset()));
        queryParams.addAll(toPairs("values-scale", parameters.getValuesScale()));

        final URI uri = toUri("/collections/" + urlEncode(parameters.getCollectionId().get(0)) + "/dggs/" + urlEncode(parameters.getDggrsId()) + "/zones/" + urlEncode(parameters.getZoneId()) + "/data", queryParams);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(uri);
        request.header("Accept", "application/json, image/png, image/tiff; application=geotiff, application/geo+json, application/cbor, text/html");
        if (parameters.isGzip()) request.setHeader("Accept-Encoding", "gzip");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        final String f = parameters.getFormat();
        if ("json".equals(f)) {
            return toSimpleResponse(send(request), DggrsData.class);
        } else if ("ubjson".equals(f)) {
            return toSimpleResponse(send(request), DggrsData.class, new UBJsonMapper());
        } else if ("cbor".equals(f)) {
            return toSimpleResponse(send(request), DggrsData.class, new CBORMapper());
        } else {
            return toSimpleResponse(send(request), DggrsData.class);
        }

    }

    /**
     * Retrieve data from a DGGRS Zone. For a DGGRS defining a sub-zone order, optimized zone data packets such as
     * DGGS-(UB)JSON for raster data, or DGGS-(UB)JSON-FG for vector data can be used.
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;DggsJson&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsData> datasetGetDGGRSZoneData(GetZoneData parameters) throws ServiceException {

        if (parameters.getDggrsId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling datasetGetDGGRSZoneData");
        }
        if (parameters.getZoneId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'zoneId' when calling datasetGetDGGRSZoneData");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", parameters.getCollectionId()));
        queryParams.addAll(toPairs("f", parameters.getFormat()));
        queryParams.addAll(toPairs("properties", parameters.getProperties()));
        queryParams.addAll(toPairs("exclude-properties", parameters.getExcludeProperties()));
        queryParams.addAll(toPairs("csv", "subset", parameters.getSubset()));
        queryParams.addAll(toPairs("filter", parameters.getFilter()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("geometry", parameters.getGeometry()));
        queryParams.addAll(toPairs("profile", parameters.getProfile()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("zone-depth", parameters.getZoneDepth()));
        queryParams.addAll(toPairs("values-offset", parameters.getValuesOffset()));
        queryParams.addAll(toPairs("values-scale", parameters.getValuesScale()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/dggs/" + urlEncode(parameters.getDggrsId()) + "/zones/" + urlEncode(parameters.getZoneId()) + "/data", queryParams));
        request.header("Accept", "application/json, image/png, image/tiff; application=geotiff, application/geo+json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), DggrsData.class);
    }

    /**
     * Retrieve the list of zones with data for a specific collection, or for a particular query
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;DatasetGetDGGRSZones200Response&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsZonesResponse> collectionGetDGGRSZones(GetZoneList parameters) throws ServiceException {

        if (parameters.getCollectionId().get(0) == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling collectionGetDGGRSZones");
        }
        if (parameters.getDggrsId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling collectionGetDGGRSZones");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", parameters.getCollectionId()));
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("bbox-crs", toCrs(parameters.getBbox())));
        queryParams.addAll(toPairs("compact-zones", parameters.getCompactZones()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("parent-zone", parameters.getParentZone()));
        queryParams.addAll(toPairs("offset", parameters.getOffset()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "subset", parameters.getSubset()));
        queryParams.addAll(toPairs("subset-crs", parameters.getSubsetCrs()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("geometry", parameters.getGeometry()));
        queryParams.addAll(toPairs("profile", parameters.getProfile()));
        queryParams.addAll(toPairs("filter", parameters.getFilter()));
        queryParams.addAll(toPairs("filter-lang", parameters.getFilterLang()));
        queryParams.addAll(toPairs("f", parameters.getFormat()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId().get(0))+ "/dggs/" + urlEncode(parameters.getDggrsId()) + "/zones", queryParams));
        request.header("Accept", "application/json, image/png, application/geo+json, image/tiff; application=geotiff, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return toSimpleResponse(send(request), DggrsZonesResponse.class);
    }

    /**
     * Retrieve the list of zones with data for this dataset, or for a particular query
     *
     * @param parameters request parameters
     * @return ServiceResponse&lt;DatasetGetDGGRSZones200Response&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<DggrsZonesResponse> datasetGetDGGRSZones(GetZoneList parameters) throws ServiceException {

        if (parameters.getDggrsId() == null) {
            throw new ServiceException(400, "Missing the required parameter 'dggrsId' when calling datasetGetDGGRSZones");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "collections", parameters.getCollectionId()));
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("bbox-crs", toCrs(parameters.getBbox())));
        queryParams.addAll(toPairs("zone-level", parameters.getZoneLevel()));
        queryParams.addAll(toPairs("compact-zones", parameters.getCompactZones()));
        queryParams.addAll(toPairs("limit", parameters.getLimit()));
        queryParams.addAll(toPairs("parent-zone", parameters.getParentZone()));
        queryParams.addAll(toPairs("offset", parameters.getOffset()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "subset", parameters.getSubset()));
        queryParams.addAll(toPairs("subset-crs", parameters.getSubsetCrs()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("geometry", parameters.getGeometry()));
        queryParams.addAll(toPairs("profile", parameters.getProfile()));
        queryParams.addAll(toPairs("filter", parameters.getFilter()));
        queryParams.addAll(toPairs("filter-lang", parameters.getFilterLang()));
        queryParams.addAll(toPairs("f", parameters.getFormat()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/dggs/" + urlEncode(parameters.getDggrsId()) +"/zones", queryParams));
        request.header("Accept", "application/json, image/png, application/geo+json, image/tiff; application=geotiff, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        final HttpResponse<InputStream> response = send(request);
        return toSimpleResponse(response, DggrsZonesResponse.class);
    }

    private static List toList(Envelope env) {
        if (env == null) return null;
        final List lst = new ArrayList();
        lst.addAll(Arrays.stream(env.getUpperCorner().getCoordinates()).boxed().toList());
        lst.addAll(Arrays.stream(env.getLowerCorner().getCoordinates()).boxed().toList());
        return lst;
    }

    private static String toCrs(Envelope env) {
        if (env == null) return null;
        CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        return IdentifiedObjects.getIdentifierOrName(crs);
    }
}
