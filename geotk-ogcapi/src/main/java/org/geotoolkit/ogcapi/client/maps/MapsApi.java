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
package org.geotoolkit.ogcapi.client.maps;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import static org.geotoolkit.client.service.AbstractService.toPairs;
import static org.geotoolkit.client.service.AbstractService.urlEncode;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcapi.dto.maps.Projections;
import org.geotoolkit.ogcapi.request.maps.GetMap;
import org.geotoolkit.ogcapi.request.maps.GetProjections;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * OGCAPI Maps.
 *
 * @author Johann Sorel (Geomatys)
 */
public final class MapsApi extends AbstractOpenApi {

    public MapsApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve a map. Depending on the parameters set, this covers the dataset map ({@code /map}), the
     * collection map ({@code /collections/{collectionId}/map}), and their styled variants
     * ({@code .../styles/{styleId}/map}).
     *
     * <p>
     * A successful (200) response body contains the map encoded in the negotiated format, and may carry :
     * {@code Content-Crs} header (the CRS used to render the map, omitted if it is [ogc:CRS84])
     * {@code Content-Bbox} header (the actual geospatial boundary of the rendered map)
     * {@code Content-Datetime} header (if the resource has a temporal aspect)
     * {@code Content-Orientation} header (if the {@code orientation} parameter was used)
     * {@code Content-Crs-Method}/{@code Content-Crs-Method-Params}/{@code Content-Crs-Datum} headers (if
     * a custom CRS projection was requested).
     * {@code Content-Attribution} for the attribution
     * These are available on the sent request's {@link java.net.http.HttpResponse#headers()}.
     * A 204 status means there is no data available for the requested extent.
     * </p>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getMap(GetMap parameters) {

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("csv", "bbox", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("datetime", parameters.getDatetime()));
        queryParams.addAll(toPairs("csv", "collections", parameters.getCollections()));
        queryParams.addAll(toPairs("csv", "subset", parameters.getSubset()));
        queryParams.addAll(toPairs("crs", parameters.getCrs()));
        queryParams.addAll(toPairs("bbox-crs", toCrs(parameters.getBbox())));
        queryParams.addAll(toPairs("subset-crs", parameters.getSubsetCrs()));
        queryParams.addAll(toPairs("bgcolor", parameters.getBgColor()));
        queryParams.addAll(toPairs("transparent", parameters.getTransparency()));
        queryParams.addAll(toPairs("void-color", parameters.getVoidColor()));
        queryParams.addAll(toPairs("void-transparent", parameters.getVoidTransparent()));
        queryParams.addAll(toPairs("width", parameters.getWidth()));
        queryParams.addAll(toPairs("height", parameters.getHeight()));
        queryParams.addAll(toPairs("csv", "center", toList(parameters.getCenter())));
        queryParams.addAll(toPairs("center-crs", toCrs(parameters.getCenter())));
        queryParams.addAll(toPairs("scale-denominator", parameters.getScaleDenominator()));
        queryParams.addAll(toPairs("mm-per-pixel", parameters.getMmPerPixel()));
        queryParams.addAll(toPairs("orientation", parameters.getOrientation()));
        queryParams.addAll(toPairs("crs-proj-method", parameters.getCrsProjMethod()));
        queryParams.addAll(toPairs("crs-proj-center", parameters.getCrsProjCenter()));
        queryParams.addAll(toPairs("crs-proj-params", parameters.getCrsProjParams()));
        queryParams.addAll(toPairs("crs-datum", parameters.getCrsDatum()));
        queryParams.addAll(toPairs("f", parameters.getFormat()));

        final StringBuilder pathBuilder = new StringBuilder();
        if (parameters.getCollectionId() != null) pathBuilder.append("/collections/").append(urlEncode(parameters.getCollectionId()));
        if (parameters.getStyleId() != null) pathBuilder.append("/styles/").append(urlEncode(parameters.getStyleId()));
        pathBuilder.append("/map");

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri(pathBuilder.toString(), queryParams));
        request.header("Accept", "image/png, image/jpeg, image/tiff; application=geotiff");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve the list of custom CRS projection operation methods (including their associated
     * parameters) and datums supported by this API.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Projections&gt;
     */
    public ServiceRequest<Supplier<Projections>> getProjections(GetProjections parameters) {

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/projectionsAndDatums", toPairs("f", parameters.getFormat())));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsJson(request, Projections.class);
    }

    private static List toList(Envelope env) {
        if (env == null) return null;
        final List lst = new ArrayList();
        lst.addAll(Arrays.stream(env.getLowerCorner().getCoordinates()).boxed().toList());
        lst.addAll(Arrays.stream(env.getUpperCorner().getCoordinates()).boxed().toList());
        return lst;
    }

    private static String toCrs(Envelope env) {
        if (env == null) return null;
        return toId(env.getCoordinateReferenceSystem());
    }

    private static List toList(DirectPosition position) {
        if (position == null) return null;
        return Arrays.stream(position.getCoordinates()).boxed().toList();
    }

    private static String toCrs(DirectPosition position) {
        if (position == null) return null;
        return toId(position.getCoordinateReferenceSystem());
    }

    private static String toId(CoordinateReferenceSystem crs) {
        if (crs == null) return null;
        return IdentifiedObjects.getIdentifierOrName(crs);
    }

}
