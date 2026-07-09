/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2026, Geomatys
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
package org.geotoolkit.openeo.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.common.ConfClasses;
import org.geotoolkit.ogcapi.request.common.GetConformance;
import org.geotoolkit.openeo.dto.capabilities.ApiVersion;
import org.geotoolkit.openeo.dto.capabilities.Capabilities;
import org.geotoolkit.openeo.dto.capabilities.FileFormats;
import org.geotoolkit.openeo.dto.capabilities.OidcProviders;
import org.geotoolkit.openeo.dto.capabilities.UserInfo;
import org.geotoolkit.openeo.request.capabilities.GetCapabilities;
import org.geotoolkit.openeo.request.capabilities.GetCredentialsBasic;
import org.geotoolkit.openeo.request.capabilities.GetCredentialsOidc;
import org.geotoolkit.openeo.request.capabilities.GetFileFormats;
import org.geotoolkit.openeo.request.capabilities.GetMe;
import org.geotoolkit.openeo.request.capabilities.GetServiceTypes;
import org.geotoolkit.openeo.request.capabilities.GetUdfRuntimes;
import org.geotoolkit.openeo.request.capabilities.GetWellKnown;

/**
 * OpenEO Capabilities API client.
 * Covers: landing page, conformance, file formats, service types, credentials and user info.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/Capabilities">OpenEO Doc</a>
 */
public final class CapabilitiesApi extends AbstractOpenApi {

    public CapabilitiesApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Information about the back-end.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Capabilities&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<Capabilities>> getCapabilities(GetCapabilities parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/", toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Capabilities.class);
    }

    /**
     * List of supported conformance classes.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Conformance&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<ConfClasses>> getConformance(GetConformance parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/conformance", toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ConfClasses.class);
    }

    /**
     * Lists supported input and output file formats.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;FileFormats&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<FileFormats>> getFileFormats(GetFileFormats parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/file_formats", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, FileFormats.class);
    }

    /**
     * Lists supported secondary web service types.
     * Returns a dynamic JSON object; use ObjectNode for flexible deserialization.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ObjectNode&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<ObjectNode>> getServiceTypes(GetServiceTypes parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/service_types", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ObjectNode.class);
    }

    /**
     * Information about the authenticated user.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;UserInfo&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<UserInfo>> getMe(GetMe parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/me", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, UserInfo.class);
    }

    /**
     * Authenticate with HTTP Basic and obtain a bearer token.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;String&gt;&gt; the bearer access token
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<String>> getCredentialsBasic(GetCredentialsBasic parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/credentials/basic", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJsonField(request, "access_token", new TypeReference<String>() {});
    }

    /**
     * Lists available OpenID Connect providers.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;OidcProviders&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<OidcProviders>> getCredentialsOidc(GetCredentialsOidc parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/credentials/oidc", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, OidcProviders.class);
    }

    /**
     * Lists all OpenEO API versions supported by the back-end (no auth required).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;List&lt;ApiVersion&gt;&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<List<ApiVersion>>> getWellKnown(GetWellKnown parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/.well-known/openeo", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJsonField(request, "versions", new TypeReference<List<ApiVersion>>() {});
    }

    /**
     * Lists UDF (User-Defined Function) runtimes supported by the back-end.
     * Returns a dynamic map keyed by runtime identifier; use ObjectNode for flexible deserialization.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ObjectNode&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<ObjectNode>> getUdfRuntimes(GetUdfRuntimes parameters) throws IllegalArgumentException {
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/udf_runtimes", List.of()));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, ObjectNode.class);
    }
}
