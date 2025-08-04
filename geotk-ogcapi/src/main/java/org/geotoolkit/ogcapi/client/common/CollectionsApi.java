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
package org.geotoolkit.ogcapi.client.common;

import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import org.geotoolkit.client.openapi.AbstractOpenApi;
import org.geotoolkit.client.openapi.OpenApiConfiguration;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceException;
import org.geotoolkit.client.service.ServiceResponse;
import org.geotoolkit.ogcapi.model.common.CollectionDescription;
import org.geotoolkit.ogcapi.model.common.Collections;

/**
 *
 * @author Johann Sorel (Geomatys)
 */
public final class CollectionsApi extends AbstractOpenApi {


    public CollectionsApi(OpenApiConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the description of a collection available from this service.
     *
     * @param collectionId Local identifier of a collection (required)
     * @param f The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ServiceResponse&lt;CollectionDesc&gt;
     * @throws ServiceException if fails to make API call
     */
    public ServiceResponse<CollectionDescription> getCollection(
            @jakarta.annotation.Nonnull String collectionId,
            @jakarta.annotation.Nullable String f) throws ServiceException {
        if (collectionId == null) {
            throw new ServiceException(400, "Missing the required parameter 'collectionId' when calling getCollection");
        }
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections/" + urlEncode(collectionId), toPairs("f", f)));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), CollectionDescription.class);
    }

    /**
     * Retrieve the list of geospatial data collections available from this service.
     *
     * @param datetime Either a date-time or an interval. Date and time expressions adhere to RFC 3339, section 5.6.
     * Intervals may be bounded or half-bounded (double-dots at start or end). Server implementations may or may not
     * support times expressed using time offsets from UTC, but need to support UTC time with the notation ending with a
     * Z. Examples: * A date-time: \&quot;2018-02-12T23:20:50Z\&quot; * A bounded interval:
     * \&quot;2018-02-12T00:00:00Z/2018-03-18T12:31:12Z\&quot; * Half-bounded intervals:
     * \&quot;2018-02-12T00:00:00Z/..\&quot; or \&quot;../2018-03-18T12:31:12Z\&quot; Only resources that have a
     * temporal property that intersects the value of &#x60;datetime&#x60; are selected. If a feature has multiple
     * temporal properties, it is the decision of the server whether only a single temporal property is used to
     * determine the extent or all relevant temporal properties. (optional)
     * @param bbox Only resources that have a geometry that intersects the bounding box are selected. The bounding box
     * is provided as four or six numbers, depending on whether the coordinate reference system includes a vertical axis
     * (elevation or depth): * Lower left corner, coordinate axis 1 * Lower left corner, coordinate axis 2 * Minimum
     * value, coordinate axis 3 (optional) * Upper right corner, coordinate axis 1 * Upper right corner, coordinate axis
     * 2 * Maximum value, coordinate axis 3 (optional) If the value consists of four numbers, the coordinate reference
     * system is WGS84 longitude/latitude (http://www.opengis.net/def/crs/OGC/1.3/CRS84) unless a different coordinate
     * reference system is specified in the parameter &#x60;bbox-crs&#x60;. If the value consists of six numbers, the
     * coordinate reference system is WGS 84 longitude/latitude/ellipsoidal height
     * (http://www.opengis.net/def/crs/OGC/0/CRS84h) unless a different coordinate reference system is specified in a
     * parameter &#x60;bbox-crs&#x60;. For WGS84 longitude/latitude the values are in most cases the sequence of minimum
     * longitude, minimum latitude, maximum longitude and maximum latitude. However, in cases where the box spans the
     * antimeridian the first value (west-most box edge) is larger than the third value (east-most box edge). If the
     * vertical axis is included, the third and the sixth number are the bottom and the top of the 3-dimensional
     * bounding box. If a resource has multiple spatial geometry properties, it is the decision of the server whether
     * only a single spatial geometry property is used to determine the extent or all relevant geometries. (optional)
     * @param limit The optional limit parameter limits the number of collections that are presented in the response
     * document. Only items are counted that are on the first level of the collection in the response document. Nested
     * objects contained within the explicitly requested items shall not be counted. * Minimum &#x3D; 1 * Maximum &#x3D;
     * 10000 * Default &#x3D; 10 (optional, default to 10)
     * @param f The format of the response. If no value is provided, the accept header is used to determine the format.
     * Accepted values are &#39;json&#39; or &#39;html&#39;. (optional)
     * @return ApiResponse&lt;Collections&gt;
     * @throws ApiException if fails to make API call
     */
    public ServiceResponse<Collections> getCollections(
            @jakarta.annotation.Nullable String datetime,
            @jakarta.annotation.Nullable List<Double> bbox,
            @jakarta.annotation.Nullable Integer limit,
            @jakarta.annotation.Nullable String f) throws ServiceException {
        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("datetime", datetime));
        queryParams.addAll(toPairs("csv", "bbox", bbox));
        queryParams.addAll(toPairs("limit", limit));
        queryParams.addAll(toPairs("f", f));
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("/collections", queryParams));
        request.header("Accept", "application/json, text/html");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return toSimpleResponse(send(request), Collections.class);
    }

}
