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
import org.geotoolkit.ogcapi.client.AbstractApi;
import org.geotoolkit.ogcapi.client.OpenApiConfiguration;
import org.geotoolkit.ogcapi.client.OpenApiException;
import org.geotoolkit.ogcapi.client.OpenApiResponse;
import org.geotoolkit.ogcapi.model.common.ConfClasses;
import org.geotoolkit.ogcapi.model.common.LandingPage;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CoreApi extends AbstractApi {

    public CoreApi(OpenApiConfiguration apiClient) {
        super(apiClient);
    }

    /**
     * This document This document
     *
     * @param f The optional f parameter indicates the output format that the
     * server shall provide as part of the response document. The default format
     * is JSON. (optional, default to json)
     * @return ApiResponse&lt;Void&gt;
     * @throws OpenApiException if fails to make API call
     */
    public OpenApiResponse<Void> getApi(@jakarta.annotation.Nullable String f) throws OpenApiException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/api", toPairs("f", f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), (Class)null);
    }

    /**
     * API conformance definition A list of all conformance classes specified in
     * a standard that the server conforms to.
     *
     * @param f The optional f parameter indicates the output format that the
     * server shall provide as part of the response document. The default format
     * is JSON. (optional, default to json)
     * @return ApiResponse&lt;ConfClasses&gt;
     * @throws OpenApiException if fails to make API call
     */
    public OpenApiResponse<ConfClasses> getConformance(@jakarta.annotation.Nullable String f) throws OpenApiException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/conformance", toPairs("f", f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), ConfClasses.class);
    }

    /**
     * Landing page The landing page provides links to the API definition and
     * the conformance statements for this API.
     *
     * @param f The optional f parameter indicates the output format that the
     * server shall provide as part of the response document. The default format
     * is JSON. (optional, default to json)
     * @return ApiResponse&lt;LandingPage&gt;
     * @throws OpenApiException if fails to make API call
     */
    public OpenApiResponse<LandingPage> getLandingPage(@jakarta.annotation.Nullable String f) throws OpenApiException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/", toPairs("f", f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), LandingPage.class);
    }

}
