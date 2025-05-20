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
package org.geotoolkit.ogcapi.client;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;
import org.openapitools.jackson.nullable.JsonNullableModule;

/**
 * Configuration and utility class for OpenAPI clients.
 *
 * <p>
 * This class can be constructed and modified, then used to instantiate the
 * various API classes. The API classes use the settings in this class to
 * configure themselves, but otherwise do not store a link to this class.</p>
 *
 * <p>
 * This class is mutable and not synchronized, so it is not thread-safe. The API
 * classes generated from this are immutable and thread-safe.</p>
 *
 * <p>
 * The setter methods of this class return the current object to facilitate a
 * fluent style of configuration.</p>
 */
public final class OpenApiConfiguration {

    private static final String DEFAULT_BASE_URI = "http://localhost";

    private HttpClient.Builder builder;
    private ObjectMapper mapper;
    private String scheme;
    private String host;
    private int port;
    private String basePath;
    private Consumer<HttpRequest.Builder> interceptor;
    private Consumer<HttpResponse<InputStream>> responseInterceptor;
    private Consumer<HttpResponse<String>> asyncResponseInterceptor;
    private Duration readTimeout;
    private Duration connectTimeout;

    /**
     * Create an instance of ApiClient.
     */
    public OpenApiConfiguration() {
        this.builder = createDefaultHttpClientBuilder();
        this.mapper = createDefaultObjectMapper();
        updateBaseUri(DEFAULT_BASE_URI);
        interceptor = null;
        readTimeout = null;
        connectTimeout = null;
        responseInterceptor = null;
        asyncResponseInterceptor = null;
    }

    /**
     * Create an instance of ApiClient.
     *
     * @param builder Http client builder.
     * @param mapper Object mapper.
     * @param baseUri Base URI
     */
    public OpenApiConfiguration(HttpClient.Builder builder, ObjectMapper mapper, String baseUri) {
        this.builder = builder;
        this.mapper = mapper;
        updateBaseUri(baseUri != null ? baseUri : DEFAULT_BASE_URI);
        interceptor = null;
        readTimeout = null;
        connectTimeout = null;
        responseInterceptor = null;
        asyncResponseInterceptor = null;
    }

    public final void updateBaseUri(String baseUri) {
        URI uri = URI.create(baseUri);
        scheme = uri.getScheme();
        host = uri.getHost();
        port = uri.getPort();
        basePath = uri.getRawPath();
    }

    /**
     * Set a custom {@link HttpClient.Builder} object to use when creating the
     * {@link HttpClient} that is used by the API client.
     *
     * @param builder Custom client builder.
     * @return This object.
     */
    public OpenApiConfiguration setHttpClientBuilder(HttpClient.Builder builder) {
        this.builder = builder;
        return this;
    }

    /**
     * Get an {@link HttpClient} based on the current
     * {@link HttpClient.Builder}.
     *
     * <p>
     * The returned object is immutable and thread-safe.</p>
     *
     * @return The HTTP client.
     */
    public HttpClient getHttpClient() {
        return builder.build();
    }

    /**
     * Set a custom {@link ObjectMapper} to serialize and deserialize the
     * request and response bodies.
     *
     * @param mapper Custom object mapper.
     * @return This object.
     */
    public OpenApiConfiguration setObjectMapper(ObjectMapper mapper) {
        this.mapper = mapper;
        return this;
    }

    /**
     * Get a copy of the current {@link ObjectMapper}.
     *
     * @return A copy of the current object mapper.
     */
    public ObjectMapper getObjectMapper() {
        return mapper.copy();
    }

    /**
     * Set a custom host name for the target service.
     *
     * @param host The host name of the target service.
     * @return This object.
     */
    public OpenApiConfiguration setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * Set a custom port number for the target service.
     *
     * @param port The port of the target service. Set this to -1 to reset the
     * value to the default for the scheme.
     * @return This object.
     */
    public OpenApiConfiguration setPort(int port) {
        this.port = port;
        return this;
    }

    /**
     * Set a custom base path for the target service, for example '/v2'.
     *
     * @param basePath The base path against which the rest of the path is
     * resolved.
     * @return This object.
     */
    public OpenApiConfiguration setBasePath(String basePath) {
        this.basePath = basePath;
        return this;
    }

    /**
     * Get the base URI to resolve the endpoint paths against.
     *
     * @return The complete base URI that the rest of the API parameters are
     * resolved against.
     */
    public String getBaseUri() {
        final String uriPath = (basePath == null || basePath.isBlank())
                               ? ""
                               : "/" + basePath.replaceFirst("^/+", "");
        return scheme + "://" + host + (port == -1 ? "" : ":" + port) + uriPath;
    }

    /**
     * Set a custom scheme for the target service, for example 'https'.
     *
     * @param scheme The scheme of the target service
     * @return This object.
     */
    public OpenApiConfiguration setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    /**
     * Set a custom request interceptor.
     *
     * <p>
     * A request interceptor is a mechanism for altering each request before it
     * is sent. After the request has been fully configured but not yet built,
     * the request builder is passed into this function for further
     * modification, after which it is sent out.</p>
     *
     * <p>
     * This is useful for altering the requests in a custom manner, such as
     * adding headers. It could also be used for logging and monitoring.</p>
     *
     * @param interceptor A function invoked before creating each request. A
     * value of null resets the interceptor to a no-op.
     * @return This object.
     */
    public OpenApiConfiguration setRequestInterceptor(Consumer<HttpRequest.Builder> interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    /**
     * Get the custom interceptor.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpRequest.Builder> getRequestInterceptor() {
        return interceptor;
    }

    /**
     * Set a custom response interceptor.
     *
     * <p>
     * This is useful for logging, monitoring or extraction of header
     * variables</p>
     *
     * @param interceptor A function invoked before creating each request. A
     * value of null resets the interceptor to a no-op.
     * @return This object.
     */
    public OpenApiConfiguration setResponseInterceptor(Consumer<HttpResponse<InputStream>> interceptor) {
        this.responseInterceptor = interceptor;
        return this;
    }

    /**
     * Get the custom response interceptor.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpResponse<InputStream>> getResponseInterceptor() {
        return responseInterceptor;
    }

    /**
     * Set a custom async response interceptor. Use this interceptor when
     * asyncNative is set to 'true'.
     *
     * <p>
     * This is useful for logging, monitoring or extraction of header
     * variables</p>
     *
     * @param interceptor A function invoked before creating each request. A
     * value of null resets the interceptor to a no-op.
     * @return This object.
     */
    public OpenApiConfiguration setAsyncResponseInterceptor(Consumer<HttpResponse<String>> interceptor) {
        this.asyncResponseInterceptor = interceptor;
        return this;
    }

    /**
     * Get the custom async response interceptor. Use this interceptor when
     * asyncNative is set to 'true'.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpResponse<String>> getAsyncResponseInterceptor() {
        return asyncResponseInterceptor;
    }

    /**
     * Set the read timeout for the http client.
     *
     * <p>
     * This is the value used by default for each request, though it can be
     * overridden on a per-request basis with a request interceptor.</p>
     *
     * @param readTimeout The read timeout used by default by the http client.
     * Setting this value to null resets the timeout to an effectively infinite
     * value.
     * @return This object.
     */
    public OpenApiConfiguration setReadTimeout(Duration readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    /**
     * Get the read timeout that was set.
     *
     * @return The read timeout, or null if no timeout was set. Null represents
     * an infinite wait time.
     */
    public Duration getReadTimeout() {
        return readTimeout;
    }

    /**
     * Sets the connect timeout (in milliseconds) for the http client.
     *
     * <p>
     * In the case where a new connection needs to be established, if the
     * connection cannot be established within the given {@code
     * duration}, then {@link HttpClient#send(HttpRequest,BodyHandler)
     * HttpClient::send} throws an {@link HttpConnectTimeoutException}, or      {@link HttpClient#sendAsync(HttpRequest,BodyHandler)
   * HttpClient::sendAsync} completes exceptionally with an
     * {@code HttpConnectTimeoutException}. If a new connection does not need to
     * be established, for example if a connection can be reused from a previous
     * request, then this timeout duration has no effect.
     *
     * @param connectTimeout connection timeout in milliseconds
     *
     * @return This object.
     */
    public OpenApiConfiguration setConnectTimeout(Duration connectTimeout) {
        this.connectTimeout = connectTimeout;
        this.builder.connectTimeout(connectTimeout);
        return this;
    }

    /**
     * Get connection timeout (in milliseconds).
     *
     * @return Timeout in milliseconds
     */
    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public static ObjectMapper createDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.enable(SerializationFeature.WRITE_ENUMS_USING_TO_STRING);
        mapper.enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING);
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new JsonNullableModule());
        mapper.registerModule(new RFC3339JavaTimeModule());
        return mapper;
    }

    public static HttpClient.Builder createDefaultHttpClientBuilder() {
        return HttpClient.newBuilder();
    }
}
