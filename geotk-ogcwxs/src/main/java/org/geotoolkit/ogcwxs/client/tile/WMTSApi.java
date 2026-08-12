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
package org.geotoolkit.ogcwxs.client.tile;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;
import jakarta.xml.bind.JAXBException;
import org.geotoolkit.client.Request;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.ogcwxs.request.tile.GetCapabilities;
import org.geotoolkit.ogcwxs.request.tile.GetFeatureInfo;
import org.geotoolkit.ogcwxs.request.tile.GetTile;
import org.geotoolkit.wmts.xml.WMTSBindingUtilities;
import org.geotoolkit.wmts.xml.WMTSVersion;
import org.geotoolkit.wmts.xml.v100.Capabilities;

/**
 * OGC Web Map Tile Service (WMTS) client.
 *
 * <p>
 * Covers the core operations defined by the WMTS 1.0.0 specification (OGC 07-057) : {@code
 * GetCapabilities}, {@code GetTile} and {@code GetFeatureInfo}. Only KVP encoding is used to
 * build requests, but {@link #getTileRequest(GetTile, String)} also supports the RESTful
 * resourceURL template style, by substituting {@code {KEY}} placeholders in the template.
 * </p>
 *
 * @see https://www.ogc.org/standards/wmts/
 * @author Quentin Bialota (Geomatys)
 */
public final class WMTSApi extends AbstractService {

    public WMTSApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the service metadata document describing this WMTS instance : supported
     * layers, tile matrix sets, formats, styles, etc.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Capabilities&gt;&gt;
     */
    public ServiceRequest<Supplier<Capabilities>> getCapabilities(GetCapabilities parameters) {
        final Map<String, String> kvp = new LinkedHashMap<>();
        kvp.put("SERVICE", "WMTS");
        kvp.put("VERSION", WMTSVersion.v100.getCode());
        kvp.put("REQUEST", "GetCapabilities");
        if (parameters.getUpdateSequence() != null) {
            kvp.put("UPDATESEQUENCE", parameters.getUpdateSequence());
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", toQueryParams(kvp)));
        request.header("Accept", "text/xml, application/xml");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsXml(request);
    }

    /**
     * Retrieve a single tile image.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getTile(GetTile parameters) {
        final Map<String, String> kvp = toTileKvp(parameters, "GetTile");

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", toQueryParams(kvp)));
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve information about features shown on a tile, at a given pixel location.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getFeatureInfo(GetFeatureInfo parameters) {
        Objects.requireNonNull(parameters.getInfoFormat(), "Info format must be defined");
        Objects.requireNonNull(parameters.getI(), "I pixel coordinate must be defined");
        Objects.requireNonNull(parameters.getJ(), "J pixel coordinate must be defined");

        final Map<String, String> kvp = toTileKvp(parameters, "GetFeatureInfo");
        kvp.put("I", String.valueOf(parameters.getI()));
        kvp.put("J", String.valueOf(parameters.getJ()));
        kvp.put("INFOFORMAT", parameters.getInfoFormat());

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", toQueryParams(kvp)));
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Build a lazy {@link Request} for a tile, meant to be used by the tiling/pyramid model
     * ({@code TileMatrix.getTile(...)}) : the actual HTTP call only happens when the returned
     * request's response stream is read.
     *
     * @param parameters request parameters
     * @param resourceUrlTemplate a RESTful resourceURL template (with {@code {KEY}}
     *                            placeholders), or {@code null} to use KVP encoding
     * @return a lazy tile request
     */
    public Request getTileRequest(GetTile parameters, String resourceUrlTemplate) {
        final Map<String, String> kvp = toTileKvp(parameters, "GetTile");

        final String url;
        if (resourceUrlTemplate != null) {
            //RESTful mode : substitute placeholders with raw (non url-encoded) values
            String template = resourceUrlTemplate;
            for (Map.Entry<String, String> entry : kvp.entrySet()) {
                template = template.replaceAll("(?i)" + Pattern.quote("{" + entry.getKey() + "}"), entry.getValue());
            }
            url = template;
        } else {
            url = toUri("", toQueryParams(kvp)).toString();
        }

        try {
            return new WMTSTileRequest(httpClient, new URL(url));
        } catch (MalformedURLException ex) {
            throw new UncheckedServiceException(ex);
        }
    }

    /**
     * Build the KVP name/raw-value map shared by {@code GetTile} and {@code GetFeatureInfo}
     * requests. Values are NOT url-encoded : encoding, when needed, is done by
     * {@link #toQueryParams(Map)}.
     */
    private Map<String, String> toTileKvp(GetTile parameters, String requestName) {
        Objects.requireNonNull(parameters.getLayer(), "Layer must be defined");
        Objects.requireNonNull(parameters.getTileMatrixSet(), "TileMatrixSet must be defined");
        Objects.requireNonNull(parameters.getTileMatrix(), "TileMatrix must be defined");
        Objects.requireNonNull(parameters.getTileRow(), "TileRow must be defined");
        Objects.requireNonNull(parameters.getTileCol(), "TileCol must be defined");

        final Map<String, String> kvp = new LinkedHashMap<>();
        kvp.put("SERVICE", "WMTS");
        kvp.put("REQUEST", requestName);
        kvp.put("VERSION", WMTSVersion.v100.getCode());
        kvp.put("LAYER", parameters.getLayer());
        kvp.put("STYLE", parameters.getStyle() == null ? "" : parameters.getStyle());
        kvp.put("FORMAT", parameters.getFormat());
        kvp.put("TILEMATRIXSET", parameters.getTileMatrixSet());
        kvp.put("TILEMATRIX", parameters.getTileMatrix());
        kvp.put("TILEROW", String.valueOf(parameters.getTileRow()));
        kvp.put("TILECOL", String.valueOf(parameters.getTileCol()));
        if (parameters.getDimensions() != null) {
            kvp.putAll(parameters.getDimensions());
        }
        return kvp;
    }

    private List<Pair> toQueryParams(Map<String, String> kvp) {
        final List<Pair> pairs = new java.util.ArrayList<>();
        for (Map.Entry<String, String> entry : kvp.entrySet()) {
            pairs.addAll(toPairs(entry.getKey(), entry.getValue()));
        }
        return pairs;
    }

    private ServiceRequest<Supplier<Capabilities>> requestAsXml(HttpRequest.Builder request) {
        return request(request, (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> {
                try {
                    return WMTSBindingUtilities.unmarshall(in, WMTSVersion.v100);
                } catch (JAXBException ex) {
                    throw new UncheckedServiceException(ex);
                }
            });
        });
    }

}
