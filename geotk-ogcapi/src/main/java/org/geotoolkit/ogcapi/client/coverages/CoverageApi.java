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
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/coverage/domainset",
                toPairs("f", parameters.getFormat())));
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
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/coverage/rangetype",
                toPairs("f", parameters.getFormat())));
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
        queryParams.addAll(toPairs("csv", "bbox", parameters.getBbox()));
        queryParams.addAll(toPairs("bbox-crs", parameters.getBboxCrs()));
        queryParams.addAll(toPairs("csv", "properties", parameters.getProperties()));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("f", parameters.getFormat()));

        final URI uri = toUri("/collections/" + urlEncode(parameters.getCollectionId()) + "/coverage", queryParams);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(uri);
        request.header("Accept", "image/tiff; application=geotiff, application/x-netcdf");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);

        return request(request, HttpResponse.BodyHandlers.ofInputStream());
    }
}
