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
package org.geotoolkit.ogcapi.client.coverages;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.coverage.DataRecord;
import org.geotoolkit.ogcapi.dto.coverage.DomainSet;
import org.geotoolkit.ogcapi.request.coverage.GetCoverage;
import org.geotoolkit.ogcapi.request.coverage.GetCoverageDomainSet;
import org.geotoolkit.ogcapi.request.coverage.GetCoverageRangeType;

/**
 * Client for OGC API - Coverages.
 *
 * @see <a href="https://docs.ogc.org/DRAFTS/19-087.html">OGC API - Coverages</a>
 * @author Quentin Bialota (Geomatys)
 */
public final class CoverageApi extends AbstractOpenApi {

    /**
     * Parameter names.
     */
    public static final String PARAM_BBOX       = "bbox";
    public static final String PARAM_BBOX_CRS   = "bbox-crs";
    public static final String PARAM_DATETIME   = "datetime";
    public static final String PARAM_F          = "f";
    public static final String PARAM_PROPERTIES = "properties";

    /**
     * Path fragments.
     */
    public static final String PATH_COLLECTIONS        = "/collections";
    public static final String PATH_COVERAGE           = "/coverage";
    public static final String PATH_COVERAGE_DOMAINSET = "/coverage/domainset";
    public static final String PATH_COVERAGE_RANGETYPE = "/coverage/rangetype";

    public CoverageApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Fetch the domain set (grid geometry) of a collection's coverage.
     */
    public ServiceRequest<Supplier<DomainSet>> getCoverageDomainSet(GetCoverageDomainSet parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCoverageDomainSet");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_COVERAGE_DOMAINSET,
                toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, DomainSet.class);
    }

    /**
     * Fetch the range type (sample dimensions) of a collection's coverage.
     */
    public ServiceRequest<Supplier<DataRecord>> getCoverageRangeType(GetCoverageRangeType parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCoverageRangeType");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_COVERAGE_RANGETYPE,
                toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return requestAsJson(request, DataRecord.class);
    }

    /**
     * Fetch the raw coverage data (range set), as a binary stream (e.g. GeoTIFF or NetCDF).
     */
    public ServiceRequest<InputStream> getCoverage(GetCoverage parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCoverage");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", PARAM_BBOX, parameters.getBbox()));
        queryParams.addAll(toPairs(PARAM_BBOX_CRS, parameters.getBboxCrs()));
        queryParams.addAll(toPairs("csv", PARAM_PROPERTIES, parameters.getProperties()));
        queryParams.addAll(toPairs(PARAM_DATETIME, parameters.getDatetime()));
        queryParams.addAll(toPairs(PARAM_F, parameters.getFormat()));

        final URI uri = toUri(PATH_COLLECTIONS + '/' + urlEncode(parameters.getCollectionId()) + PATH_COVERAGE, queryParams);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(uri);
        request.header("Accept", "image/tiff; application=geotiff, application/x-netcdf");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return request(request, HttpResponse.BodyHandlers.ofInputStream());
    }
}
