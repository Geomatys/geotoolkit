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
package org.geotoolkit.ogcapi.client.geovolumes;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import static org.geotoolkit.client.service.AbstractService.toPairs;
import static org.geotoolkit.client.service.AbstractService.urlEncode;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.geovolumes.GeoVolume;
import org.geotoolkit.ogcapi.dto.geovolumes.GeoVolumes;
import org.geotoolkit.ogcapi.request.common.GetCollectionList;
import org.geotoolkit.ogcapi.request.geovolumes.GetCollection;
import org.geotoolkit.ogcapi.request.geovolumes.GetModel;
import org.opengis.geometry.Envelope;

/**
 * OGCAPI 3D GeoVolumes.
 *
 * NOTE : getCollection method overload those in ogcapi commons.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class GeoVolumesApi extends AbstractOpenApi {

    public GeoVolumesApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the list of 3D-Containers (GeoVolumes) available for the dataset.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;GeoVolumes&gt;
     */
    public ServiceRequest<Supplier<GeoVolumes>> getCollections(GetCollectionList parameters) {

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("f", parameters.getFormat()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections", queryParams));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoVolumes.class);
    }

    /**
     * Retrieve a single 3D-Container (GeoVolume).
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;GeoVolume&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<Supplier<GeoVolume>> getCollection(GetCollection parameters) throws IllegalArgumentException {
        if (parameters.getCollectionId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'collectionId' when calling getCollection");
        }

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("f", parameters.getFormat()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(parameters.getCollectionId()), queryParams));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, GeoVolume.class);
    }

    /**
     * Retrieve a shared 3D model referenced by a tiled point feature, from the "Tiled points
     * instantiating 3D models" requirements class.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     * @throws IllegalArgumentException if fails to make API call
     */
    public ServiceRequest<byte[]> getModel(GetModel parameters) throws IllegalArgumentException {
        if (parameters.getModelId() == null) {
            throw new IllegalArgumentException("Missing the required parameter 'modelId' when calling getModel");
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/models/" + urlEncode(parameters.getModelId()), null));
        request.header("Accept", "*/*");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    private static List toList(Envelope env) {
        if (env == null) return null;
        final List lst = new ArrayList();
        lst.addAll(Arrays.stream(env.getLowerCorner().getCoordinates()).boxed().toList());
        lst.addAll(Arrays.stream(env.getUpperCorner().getCoordinates()).boxed().toList());
        return lst;
    }

}
