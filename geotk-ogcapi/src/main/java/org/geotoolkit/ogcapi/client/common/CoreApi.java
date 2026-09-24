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
package org.geotoolkit.ogcapi.client.common;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.common.ConfClasses;
import org.geotoolkit.ogcapi.dto.common.LandingPage;
import org.geotoolkit.ogcapi.request.common.GetApi;
import org.geotoolkit.ogcapi.request.common.GetConformance;
import org.geotoolkit.ogcapi.request.common.GetLandingPage;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CoreApi extends AbstractOpenApi {

    /**
     * Parameter names.
     */
    public static final String PARAM_F = "f";

    /**
     * Path fragments.
     */
    public static final String PATH_API         = "/api";
    public static final String PATH_CONFORMANCE = "/conformance";

    public CoreApi(OpenApiConfiguration apiClient) {
        super(apiClient);
    }

    /**
     * This document This document
     *
     * @param parameters request parameters
     * @return ApiResponse&lt;Void&gt;
     */
    public ServiceRequest<Void> getApi(GetApi parameters) {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_API, toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * API conformance definition A list of all conformance classes specified in
     * a standard that the server conforms to.
     *
     * @param parameters request parameters
     * @return ApiResponse&lt;ConfClasses&gt;
     */
    public ServiceRequest<Supplier<ConfClasses>> getConformance(GetConformance parameters) {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(PATH_CONFORMANCE, toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ConfClasses.class);
    }

    /**
     * Landing page The landing page provides links to the API definition and
     * the conformance statements for this API.
     *
     * @param parameters request parameters
     * @return ApiResponse&lt;LandingPage&gt;
     */
    public ServiceRequest<Supplier<LandingPage>> getLandingPage(GetLandingPage parameters) {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/", toPairs(PARAM_F, parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, LandingPage.class);
    }

}
