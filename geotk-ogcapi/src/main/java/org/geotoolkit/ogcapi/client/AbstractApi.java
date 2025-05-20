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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import static java.nio.charset.StandardCharsets.UTF_8;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractApi {

    protected final HttpClient httpClient;
    protected final ObjectMapper objectMapper;
    protected final String baseUri;
    protected final Consumer<HttpRequest.Builder> requestInterceptor;
    protected final Duration readTimeout;
    protected final Consumer<HttpResponse<InputStream>> responseInterceptor;
    protected final Consumer<HttpResponse<String>> asyncResponseInterceptor;

    public AbstractApi(OpenApiConfiguration apiClient) {
        httpClient = apiClient.getHttpClient();
        objectMapper = apiClient.getObjectMapper();
        baseUri = apiClient.getBaseUri();
        requestInterceptor = apiClient.getRequestInterceptor();
        readTimeout = apiClient.getReadTimeout();
        responseInterceptor = apiClient.getResponseInterceptor();
        asyncResponseInterceptor = apiClient.getAsyncResponseInterceptor();
    }


    protected static <T> OpenApiResponse<T> emptyResponse(HttpResponse<?> response) {
        return new OpenApiResponse<>(
                response.statusCode(),
                response.headers().map(),
                null
        );
    }

    protected HttpResponse<InputStream> send(HttpRequest.Builder requestBuilder) throws OpenApiException {
        final HttpRequest request = requestBuilder.build();
        try {
            final HttpResponse<InputStream> response = httpClient.send(
                    request,
                    HttpResponse.BodyHandlers.ofInputStream());
            if (responseInterceptor != null) {
                responseInterceptor.accept(response);
            }
            if (response.statusCode() / 100 != 2) {
                throw getOpenApiException(request.uri().getPath(), response);
            }
            return response;
        } catch (IOException e) {
            throw new OpenApiException(e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OpenApiException(e);
        }
    }

    protected <T> OpenApiResponse<T> toSimpleResponse(HttpResponse<InputStream> response, Class<T> valueClass) throws OpenApiException {
        return toSimpleResponse(response, valueClass == null ? null : objectMapper.constructType(valueClass));
    }

    protected <T> OpenApiResponse<T> toSimpleResponse(HttpResponse<InputStream> response, TypeReference<T> ref) throws OpenApiException {
        return toSimpleResponse(response, ref == null ? null : objectMapper.constructType(ref));
    }

    private <T> OpenApiResponse<T> toSimpleResponse(HttpResponse<InputStream> response, JavaType ref) throws OpenApiException {
        if (response.body() == null) {
            return emptyResponse(response);
        }

        try (var in = response.body()){
            final T responseBody;
            if (ref == null) {
                // Drain the InputStream
                while (in.read() != -1) {
                    // Ignore
                }
                responseBody = null;
            } else {
                responseBody = objectMapper.readValue(in, ref);
            }
            return new OpenApiResponse<>(response.statusCode(), response.headers().map(), responseBody);

        } catch (IOException ex) {
            throw new OpenApiException(ex);
        }
    }

    protected URI toUri(String localPath, List<Pair> queryParams) {
        if (!queryParams.isEmpty()) {
            final StringJoiner queryJoiner = new StringJoiner("&");
            queryParams.forEach(p -> queryJoiner.add(p.getName() + '=' + p.getValue()));
            return URI.create(baseUri + localPath + '?' + queryJoiner.toString());
        } else {
            return URI.create(baseUri + localPath);
        }
    }

    /**
     * Set request timeout and interceptor.
     */
    protected HttpRequest.Builder setConfig(HttpRequest.Builder requestBuilder) {
        if (readTimeout != null) requestBuilder.timeout(readTimeout);
        if (requestInterceptor != null) requestInterceptor.accept(requestBuilder);
        return requestBuilder;
    }

    protected OpenApiException getOpenApiException(String operationId, HttpResponse<InputStream> response) throws IOException {
        String body = response.body() == null ? null : new String(response.body().readAllBytes());
        String message = formatExceptionMessage(operationId, response.statusCode(), body);
        return new OpenApiException(response.statusCode(), message, response.headers(), body);
    }

    private String formatExceptionMessage(String operationId, int statusCode, String body) {
        if (body == null || body.isEmpty()) {
            body = "[no body]";
        }
        return operationId + " call failed with: " + statusCode + " - " + body;
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

        String delimiter = switch (format) {
            case "csv" -> urlEncode(",");
            case "ssv" -> urlEncode(" ");
            case "tsv" -> urlEncode("\t");
            case "pipes" -> urlEncode("|");
            default -> throw new IllegalArgumentException("Illegal collection format: " + collectionFormat);
        };

        StringJoiner joiner = new StringJoiner(delimiter);
        for (Object value : values) {
            joiner.add(urlEncode(valueToString(value)));
        }

        return Collections.singletonList(new Pair(urlEncode(name), joiner.toString()));
    }
}
