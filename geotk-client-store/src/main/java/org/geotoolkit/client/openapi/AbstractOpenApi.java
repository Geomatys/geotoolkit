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
package org.geotoolkit.client.openapi;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Supplier;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.ServiceRequest;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public abstract class AbstractOpenApi extends AbstractService {

    protected final ObjectMapper objectMapper;

    public AbstractOpenApi(OpenApiConfiguration apiClient) {
        super(apiClient);
        objectMapper = apiClient.getObjectMapper();
    }

    @Override
    public OpenApiConfiguration getConfiguration() {
        return (OpenApiConfiguration) super.getConfiguration();
    }

    protected <T> ServiceRequest<Supplier<T>> requestAsJson(HttpRequest.Builder request, Class<T> valueClass) {
        return super.requestAsJson(request, objectMapper, valueClass);
    }

    protected <T> ServiceRequest<Supplier<T>> requestAsJson(HttpRequest.Builder request, TypeReference<T> valueClass) {
        return super.requestAsJson(request, objectMapper, valueClass);
    }

    protected <T> ServiceRequest<Supplier<T>> requestAsJson(HttpRequest.Builder request, JavaType valueClass) {
        return super.requestAsJson(request, objectMapper, valueClass);
    }

    /**
     * Deserializes a specific named field from the JSON response object.
     * Use when the API returns a thin envelope (e.g. {@code {"versions":[...]}})
     * and the caller needs only the inner value as a standard Java type.
     *
     * @param <T>       target type
     * @param request   prepared request builder
     * @param fieldName JSON field name to extract from the root object
     * @param valueType Jackson TypeReference for the target type
     * @return configured service request
     */
    protected <T> ServiceRequest<Supplier<T>> requestAsJsonField(HttpRequest.Builder request, String fieldName, TypeReference<T> valueType) {
        final HttpResponse.BodyHandler<Supplier<T>> bodyHandler = responseInfo ->
            HttpResponse.BodySubscribers.mapping(
                HttpResponse.BodySubscribers.ofInputStream(),
                is -> {
                    try {
                        final T value = objectMapper.convertValue(
                            objectMapper.readTree(is).get(fieldName),
                            valueType);
                        return () -> value;
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        return request(request, bodyHandler);
    }
}
