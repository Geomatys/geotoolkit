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
package org.geotoolkit.client.service;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

/**
 * A request configured.
 * It can be customized using the builder method before sending it.
 *
 * @author Johann Sorel (Geomatys)
 * @param <T> request response type.
 */
public final class ServiceRequest<T> {

    private final AbstractService service;
    private final HttpRequest.Builder builder;
    private final HttpResponse.BodyHandler<T> bodyHandler;

    public ServiceRequest(AbstractService service, HttpRequest.Builder builder, HttpResponse.BodyHandler<T> bodyHandler) {
        this.service = service;
        this.builder = builder;
        this.bodyHandler = bodyHandler;
    }

    /**
     * Get the request builder, can be modified before sending the request.
     *
     * @return request builder.
     */
    public HttpRequest.Builder builder() {
        return builder;
    }

    /**
     * Send request and block until response is parsed.
     *
     * @return HttpResponse
     * @throws ServiceException if fails to make API call
     */
    public HttpResponse<T> send() throws ServiceException {
        return service.send(builder.build(), bodyHandler);
    }

    /**
     * Send request in an asynchrone way, method does not block the current thread.
     * @return a future HttpResponse
     */
    public CompletableFuture<HttpResponse<T>> sendAsync() {
        return service.sendAsync(builder.build(), bodyHandler);
    }
}
