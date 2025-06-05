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
import java.io.InputStream;
import java.net.http.HttpResponse;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;

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

    protected <T> ServiceResponse<T> toSimpleResponse(HttpResponse<InputStream> response, Class<T> valueClass) throws ServiceException {
        return toSimpleResponse(response, valueClass == null ? null : objectMapper.constructType(valueClass));
    }

    protected <T> ServiceResponse<T> toSimpleResponse(HttpResponse<InputStream> response, TypeReference<T> ref) throws ServiceException {
        return toSimpleResponse(response, ref == null ? null : objectMapper.constructType(ref));
    }

    private <T> ServiceResponse<T> toSimpleResponse(HttpResponse<InputStream> response, JavaType ref) throws ServiceException {
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
            return new ServiceResponse<>(response.statusCode(), response.headers().map(), responseBody);

        } catch (IOException ex) {
            throw new ServiceException(ex);
        }
    }

}
