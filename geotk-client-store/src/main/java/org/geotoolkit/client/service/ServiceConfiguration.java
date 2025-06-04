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
package org.geotoolkit.client.service;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;

/**
 * Configuration elements of an API client.
 *
 * <p>
 * This class is immutable and thread-safe.</p>
 *
 * <p>
 * The setter methods of this class return the current object to facilitate a
 * fluent style of configuration.</p>
 */
public class ServiceConfiguration {

    private static final String DEFAULT_BASE_URI = "http://localhost";

    private final HttpClient.Builder builder;
    final String scheme;
    final String host;
    final int port;
    final String basePath;
    final Consumer<HttpRequest.Builder> requestInterceptor;
    final Consumer<HttpResponse<InputStream>> responseInterceptor;
    final Consumer<HttpResponse<String>> asyncResponseInterceptor;
    final Duration readTimeout;
    final Duration connectTimeout;
    final String baseUri;

    protected ServiceConfiguration(HttpClient.Builder builder, String scheme,
            String host, int port, String basePath,
            Consumer<HttpRequest.Builder> interceptor,
            Consumer<HttpResponse<InputStream>> responseInterceptor,
            Consumer<HttpResponse<String>> asyncResponseInterceptor,
            Duration readTimeout, Duration connectTimeout) {
        this.builder = builder;
        this.scheme = scheme;
        this.host = host;
        this.port = port;
        this.basePath = basePath;
        this.requestInterceptor = interceptor;
        this.responseInterceptor = responseInterceptor;
        this.asyncResponseInterceptor = asyncResponseInterceptor;
        this.readTimeout = readTimeout;
        this.connectTimeout = connectTimeout;
        final String uriPath = (basePath == null || basePath.isBlank())
                       ? ""
                       : "/" + basePath.replaceFirst("^/+", "");
        this.baseUri = scheme + "://" + host + (port == -1 ? "" : ":" + port) + uriPath;
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
    public HttpClient createHttpClient() {
        return builder.build();
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getBasePath() {
        return basePath;
    }

    /**
     * Get the base URI to resolve the endpoint paths against.
     *
     * @return The complete base URI that the rest of the API parameters are
     * resolved against.
     */
    public String getBaseUri() {
        return baseUri;
    }

    /**
     * Get the custom interceptor.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpRequest.Builder> getRequestInterceptor() {
        return requestInterceptor;
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
     * Get the custom async response interceptor. Use this interceptor when
     * asyncNative is set to 'true'.
     *
     * @return The custom interceptor that was set, or null if there isn't any.
     */
    public Consumer<HttpResponse<String>> getAsyncResponseInterceptor() {
        return asyncResponseInterceptor;
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
     * Get connection timeout (in milliseconds).
     *
     * @return Timeout in milliseconds
     */
    public Duration getConnectTimeout() {
        return connectTimeout;
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     *
    * <p>
    * This class can be constructed and modified, then used to instantiate the
    * various API classes. The API classes use the settings in this class to
    * configure themselves, but otherwise do not store a link to this class.</p>
    *
    * <p>
    * This class is mutable and not synchronized, so it is not thread-safe. The API
    * classes generated from this are immutable and thread-safe.</p>
     */
    public static class Builder<T extends Builder<T>> {

        protected HttpClient.Builder builder;
        protected String scheme;
        protected String host;
        protected int port;
        protected String basePath;
        protected Consumer<HttpRequest.Builder> interceptor;
        protected Consumer<HttpResponse<InputStream>> responseInterceptor;
        protected Consumer<HttpResponse<String>> asyncResponseInterceptor;
        protected Duration readTimeout;
        protected Duration connectTimeout;

        /**
         * Create an instance of ApiClient.
         */
        protected Builder() {
            this.builder = HttpClient.newBuilder();
            updateBaseUri(DEFAULT_BASE_URI);
            interceptor = null;
            readTimeout = null;
            connectTimeout = null;
            responseInterceptor = null;
            asyncResponseInterceptor = null;
        }

        public final T updateBaseUri(String baseUri) {
            URI uri = URI.create(baseUri);
            scheme = uri.getScheme();
            host = uri.getHost();
            port = uri.getPort();
            basePath = uri.getRawPath();
            return (T) this;
        }

        /**
         * Set a custom {@link HttpClient.Builder} object to use when creating the
         * {@link HttpClient} that is used by the API client.
         *
         * @param builder Custom client builder.
         * @return This object.
         */
        public T setHttpClientBuilder(HttpClient.Builder builder) {
            this.builder = builder;
            return (T) this;
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
         * Set a custom host name for the target service.
         *
         * @param host The host name of the target service.
         * @return This object.
         */
        public T setHost(String host) {
            this.host = host;
            return (T) this;
        }

        /**
         * Set a custom port number for the target service.
         *
         * @param port The port of the target service. Set this to -1 to reset the
         * value to the default for the scheme.
         * @return This object.
         */
        public T setPort(int port) {
            this.port = port;
            return (T) this;
        }

        /**
         * Set a custom base path for the target service, for example '/v2'.
         *
         * @param basePath The base path against which the rest of the path is
         * resolved.
         * @return This object.
         */
        public T setBasePath(String basePath) {
            this.basePath = basePath;
            return (T) this;
        }

        /**
         * Get the base URI to resolve the endpoint paths against.
         *
         * @return The complete base URI that the rest of the API parameters are
         * resolved against.
         */
        public String getBaseUri() {
            return scheme + "://" + host + (port == -1 ? "" : ":" + port) + basePath;
        }

        /**
         * Set a custom scheme for the target service, for example 'https'.
         *
         * @param scheme The scheme of the target service
         * @return This object.
         */
        public T setScheme(String scheme) {
            this.scheme = scheme;
            return (T) this;
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
        public T setRequestInterceptor(Consumer<HttpRequest.Builder> interceptor) {
            this.interceptor = interceptor;
            return (T) this;
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
        public T setResponseInterceptor(Consumer<HttpResponse<InputStream>> interceptor) {
            this.responseInterceptor = interceptor;
            return (T) this;
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
        public T setAsyncResponseInterceptor(Consumer<HttpResponse<String>> interceptor) {
            this.asyncResponseInterceptor = interceptor;
            return (T) this;
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
        public T setReadTimeout(Duration readTimeout) {
            this.readTimeout = readTimeout;
            return (T) this;
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
        public T setConnectTimeout(Duration connectTimeout) {
            this.connectTimeout = connectTimeout;
            this.builder.connectTimeout(connectTimeout);
            return (T) this;
        }

        /**
         * Get connection timeout (in milliseconds).
         *
         * @return Timeout in milliseconds
         */
        public Duration getConnectTimeout() {
            return connectTimeout;
        }

        public ServiceConfiguration build() {
            return new ServiceConfiguration(builder, scheme, host, port, basePath,
                    interceptor, responseInterceptor, asyncResponseInterceptor, readTimeout, connectTimeout);
        }
    }

}
