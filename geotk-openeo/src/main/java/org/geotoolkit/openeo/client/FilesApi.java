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

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.openeo.dto.file.UserFileList;
import org.geotoolkit.openeo.request.file.DeleteFile;
import org.geotoolkit.openeo.request.file.GetFile;
import org.geotoolkit.openeo.request.file.GetFiles;
import org.geotoolkit.openeo.request.file.UploadFile;

/**
 * OpenEO File Storage API client.
 * Covers: listing, downloading, uploading and deleting files in the user's workspace.
 *
 * @author Quentin BIALOTA (Geomatys)
 * Based on : <a href="https://api.openeo.org/#tag/File-Storage">OpenEO Doc</a>
 */
public final class FilesApi extends AbstractOpenApi {

    public FilesApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * List all files in the authenticated user's workspace.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;UserFileList&gt;&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<UserFileList>> getFiles(GetFiles parameters) throws IllegalArgumentException {
        final List<Pair> queryParams = new ArrayList<>(toPairs("limit", parameters.getLimit()));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/files", queryParams));
        request.header("Accept", "application/json");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, UserFileList.class);
    }

    /**
     * Download a file from the user's workspace.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt; raw file bytes
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<byte[]> getFile(GetFile parameters) throws IllegalArgumentException {
        if (parameters.getPath() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'path' when calling getFile");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/files/" + urlEncode(parameters.getPath()), List.of()));
        request.header("Accept", "*/*");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Upload or replace a file in the user's workspace.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt; (200 OK)
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> uploadFile(UploadFile parameters) throws IllegalArgumentException {
        if (parameters.getPath() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'path' when calling uploadFile");
        }
        if (parameters.getContent() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'content' when calling uploadFile");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/files/" + urlEncode(parameters.getPath()), List.of()));
        request.header("Accept", "application/json");
        request.header("Content-Type", "application/octet-stream");
        request.method("PUT", HttpRequest.BodyPublishers.ofByteArray(parameters.getContent()));
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }

    /**
     * Delete a file from the user's workspace.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Void&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Void> deleteFile(DeleteFile parameters) throws IllegalArgumentException {
        if (parameters.getPath() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'path' when calling deleteFile");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/files/" + urlEncode(parameters.getPath()), List.of()));
        request.header("Accept", "application/json");
        request.method("DELETE", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.discarding());
    }
}
