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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractService implements AutoCloseable {

    protected final ServiceConfiguration config;
    protected final HttpClient httpClient;

    public AbstractService(ServiceConfiguration apiClient) {
        this.config = apiClient;
        httpClient = apiClient.createHttpClient();
    }

    /**
     * @return parameters used by this service;
     */
    public ServiceConfiguration getConfiguration() {
        return config;
    }

    protected static <T> ServiceResponse<T> emptyResponse(HttpResponse<?> response) {
        return new ServiceResponse<>(
                response.statusCode(),
                response.headers().map(),
                null
        );
    }

    protected HttpResponse<InputStream> send(HttpRequest.Builder requestBuilder) throws ServiceException {
        final HttpRequest request = requestBuilder.build();
        try {
            final HttpResponse<InputStream> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofInputStream());
            if (config.responseInterceptor != null) {
                config.responseInterceptor.accept(response);
            }
            if (response.statusCode() / 100 != 2) {
                throw toServiceException(request.uri().getPath(), response);
            }
            return response;
        } catch (IOException e) {
            throw new ServiceException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ServiceException(e);
        }
    }

    protected URI toUri(String localPath, List<Pair> queryParams) {
        final String fullPath = localPath == null ? config.baseUri : config.baseUri + localPath;

        if (queryParams != null && !queryParams.isEmpty()) {
            final StringJoiner queryJoiner = new StringJoiner("&");
            queryParams.forEach(p -> queryJoiner.add(p.getName() + '=' + p.getValue()));
            return URI.create(fullPath + '?' + queryJoiner.toString());
        } else {
            return URI.create(fullPath);
        }
    }

    /**
     * Set request timeout and interceptor.
     */
    protected HttpRequest.Builder setConfig(HttpRequest.Builder requestBuilder) {
        if (config.readTimeout != null) requestBuilder.timeout(config.readTimeout);
        if (config.requestInterceptor != null) config.requestInterceptor.accept(requestBuilder);
        return requestBuilder;
    }

    protected ServiceException toServiceException(String operationId, HttpResponse<InputStream> response) throws IOException {
        String body = response.body() == null ? null : new String(response.body().readAllBytes());
        String message = formatExceptionMessage(operationId, response.statusCode(), body);
        return new ServiceException(response.statusCode(), message, response.headers(), body);
    }

    private String formatExceptionMessage(String operationId, int statusCode, String body) {
        if (body == null || body.isEmpty()) {
            body = "[no body]";
        }
        return operationId + " call failed with: " + statusCode + " - " + body;
    }

    @Override
    public void close() throws Exception {
        // HTTPClient becomes closeable in JDK 21
        if (httpClient instanceof AutoCloseable) {
            ((AutoCloseable)httpClient).close();
        }
    }

    public static String valueToString(Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof OffsetDateTime) {
            return ((OffsetDateTime) value).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        return value.toString();
    }

    /**
     * URL encode a string in the UTF-8 encoding.
     *
     * @param s String to encode.
     * @return URL-encoded representation of the input string.
     */
    public static String urlEncode(String s) {
        return URLEncoder.encode(s, UTF_8).replaceAll("\\+", "%20");
    }

    /**
     * Convert a URL query name/value parameter to a list of encoded
     * {@link Pair} objects.
     *
     * <p>
     * The value can be null, in which case an empty list is returned.</p>
     *
     * @param name The query name parameter.
     * @param value The query value, which may not be a collection but may be
     * null.
     * @return A singleton list of the {@link Pair} objects representing the
     * input parameters, which is encoded for use in a URL. If the value is
     * null, an empty list is returned.
     */
    public static List<Pair> toPairs(String name, Object value) {
        if (name == null || name.isEmpty() || value == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(new Pair(urlEncode(name), urlEncode(valueToString(value))));
    }

    /**
     * Convert a URL query name/collection parameter to a list of encoded
     * {@link Pair} objects.
     *
     * @param collectionFormat The swagger collectionFormat string (csv, tsv,
     * etc).
     * @param name The query name parameter.
     * @param values A collection of values for the given query name, which may
     * be null.
     * @return A list of {@link Pair} objects representing the input parameters,
     * which is encoded for use in a URL. If the values collection is null, an
     * empty list is returned.
     */
    public static List<Pair> toPairs(String collectionFormat, String name, Collection<?> values) {
        if (name == null || name.isEmpty() || values == null || values.isEmpty()) {
            return Collections.emptyList();
        }

        // get the collection format (default: csv)
        String format = collectionFormat == null || collectionFormat.isEmpty() ? "csv" : collectionFormat;

        // create the params based on the collection format
        if ("multi".equals(format)) {
            return values.stream()
                    .map(value -> new Pair(urlEncode(name), urlEncode(valueToString(value))))
                    .collect(Collectors.toList());
        }

        String delimiter;
        switch (format) {
            case "csv":
                delimiter = urlEncode(",");
                break;
            case "ssv":
                delimiter = urlEncode(" ");
                break;
            case "tsv":
                delimiter = urlEncode("\t");
                break;
            case "pipes":
                delimiter = urlEncode("|");
                break;
            default:
                throw new IllegalArgumentException("Illegal collection format: " + collectionFormat);
        }

        StringJoiner joiner = new StringJoiner(delimiter);
        for (Object value : values) {
            joiner.add(urlEncode(valueToString(value)));
        }

        return Collections.singletonList(new Pair(urlEncode(name), joiner.toString()));
    }
}
