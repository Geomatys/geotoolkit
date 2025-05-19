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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.function.Consumer;
import org.geotoolkit.client.service.ServiceConfiguration;
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
public final class OpenApiConfiguration extends ServiceConfiguration {

    private final ObjectMapper mapper;

    protected OpenApiConfiguration(HttpClient.Builder builder, String scheme,
            String host, int port, String basePath,
            Consumer<HttpRequest.Builder> interceptor,
            Consumer<HttpResponse<InputStream>> responseInterceptor,
            Consumer<HttpResponse<String>> asyncResponseInterceptor,
            Duration readTimeout, Duration connectTimeout, ObjectMapper mapper) {
        super(builder, scheme, host, port, basePath, interceptor,
                responseInterceptor, asyncResponseInterceptor, readTimeout, connectTimeout);
        this.mapper = mapper;
    }

    /**
     * Get a copy of the current {@link ObjectMapper}.
     *
     * @return A copy of the current object mapper.
     */
    public ObjectMapper getObjectMapper() {
        return mapper.copy();
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

    public static OpenApiConfiguration.Builder builder() {
        return new OpenApiConfiguration.Builder();
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
    public static class Builder extends ServiceConfiguration.Builder<Builder> {

        private ObjectMapper mapper = createDefaultObjectMapper();

        /**
         * Set a custom {@link ObjectMapper} to serialize and deserialize the
         * request and response bodies.
         *
         * @param mapper Custom object mapper.
         * @return This object.
         */
        public Builder setObjectMapper(ObjectMapper mapper) {
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

        @Override
        public OpenApiConfiguration build() {
            return new OpenApiConfiguration(builder, scheme, host, port, basePath,
                    interceptor, responseInterceptor, asyncResponseInterceptor, readTimeout, connectTimeout, mapper);
        }

    }
}
