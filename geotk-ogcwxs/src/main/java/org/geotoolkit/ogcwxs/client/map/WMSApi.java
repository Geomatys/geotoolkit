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
package org.geotoolkit.ogcwxs.client.map;

import java.awt.Color;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import jakarta.xml.bind.JAXBException;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.ogcwxs.request.map.DescribeLayer;
import org.geotoolkit.ogcwxs.request.map.GetCapabilities;
import org.geotoolkit.ogcwxs.request.map.GetFeatureInfo;
import org.geotoolkit.ogcwxs.request.map.GetLegendGraphic;
import org.geotoolkit.ogcwxs.request.map.GetMap;
import org.geotoolkit.wms.xml.AbstractWMSCapabilities;
import org.geotoolkit.wms.xml.WMSBindingUtilities;
import org.geotoolkit.wms.xml.WMSVersion;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * OGC Web Map Service (WMS) client.
 *
 * <p>
 * Covers the core operations defined by the WMS 1.3.0 specification (OGC 06-042): {@code GetCapabilities},
 * {@code GetMap} and {@code GetFeatureInfo}. {@code GetLegendGraphic} and {@code DescribeLayer} are also
 * provided for convenience, though they are defined by the Styled Layer Descriptor profile of WMS
 * (OGC 05-078) rather than by the base specification.
 * </p>
 *
 * <p>
 * The axis order of a {@link GetMap#getBbox()} envelope is sent to the server as-is (minx,miny,maxx,maxy
 * in the order the envelope's CRS exposes it). Callers are responsible for supplying coordinates in the
 * order expected by the requested {@link WMSVersion} : always longitude/easting first for versions prior
 * to 1.3.0 ({@link WMSVersion#longitudeFirst}), but following the authority-defined axis order of the CRS
 * for 1.3.0 (for example latitude before longitude for {@code EPSG:4326}).
 * </p>
 *
 * @see https://www.ogc.org/standards/wms/
 * @author Johann Sorel (Geomatys)
 */
public final class WMSApi extends AbstractService {

    public WMSApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the service metadata document describing this WMS instance : supported operations,
     * layers, styles, formats, coordinate reference systems, etc.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;AbstractWMSCapabilities&gt;&gt;
     */
    public ServiceRequest<Supplier<AbstractWMSCapabilities>> getCapabilities(GetCapabilities parameters) {
        final WMSVersion version = version(parameters.getVersion());

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("SERVICE", "WMS"));
        queryParams.addAll(toPairs(version == WMSVersion.v100 ? "WMTVER" : "VERSION", version.getCode()));
        queryParams.addAll(toPairs("REQUEST", "GetCapabilities"));
        queryParams.addAll(toPairs("UPDATESEQUENCE", parameters.getUpdateSequence()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.header("Accept", "text/xml, application/xml");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsXml(request, version);
    }

    /**
     * Retrieve a rendered map image.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getMap(GetMap parameters) {
        final List<Pair> queryParams = toGetMapPairs(parameters, "GetMap");

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve information about features shown on a map, at a given pixel location.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getFeatureInfo(GetFeatureInfo parameters) {
        Objects.requireNonNull(parameters.getInfoFormat(), "Info format must be defined");
        if (parameters.getQueryLayers() == null || parameters.getQueryLayers().isEmpty()) {
            throw new IllegalArgumentException("Query layers must be defined");
        }
        Objects.requireNonNull(parameters.getX(), "X pixel coordinate must be defined");
        Objects.requireNonNull(parameters.getY(), "Y pixel coordinate must be defined");

        final WMSVersion version = version(parameters.getVersion());
        final List<Pair> queryParams = toGetMapPairs(parameters, "GetFeatureInfo");
        queryParams.addAll(toPairs("csv", "QUERY_LAYERS", parameters.getQueryLayers()));
        queryParams.addAll(toPairs("INFO_FORMAT", parameters.getInfoFormat()));
        queryParams.addAll(toPairs("FEATURE_COUNT", parameters.getFeatureCount()));
        queryParams.addAll(toPairs(version == WMSVersion.v130 ? "I" : "X", parameters.getX()));
        queryParams.addAll(toPairs(version == WMSVersion.v130 ? "J" : "Y", parameters.getY()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve a legend graphic image for a layer.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getLegendGraphic(GetLegendGraphic parameters) {
        Objects.requireNonNull(parameters.getLayer(), "Layer must be defined");
        final WMSVersion version = version(parameters.getVersion());

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("SERVICE", "WMS"));
        queryParams.addAll(toPairs("VERSION", version.getCode()));
        queryParams.addAll(toPairs("REQUEST", "GetLegendGraphic"));
        queryParams.addAll(toPairs("LAYER", parameters.getLayer()));
        queryParams.addAll(toPairs("FORMAT", parameters.getFormat()));
        queryParams.addAll(toPairs("WIDTH", parameters.getWidth()));
        queryParams.addAll(toPairs("HEIGHT", parameters.getHeight()));
        queryParams.addAll(toPairs("EXCEPTIONS", parameters.getExceptions()));
        queryParams.addAll(toPairs("RULE", parameters.getRule()));
        queryParams.addAll(toPairs("SCALE", parameters.getScale()));
        if (parameters.getSldBody() != null) {
            queryParams.addAll(toPairs("SLD_BODY", parameters.getSldBody()));
        } else if (parameters.getSld() != null) {
            queryParams.addAll(toPairs("SLD", parameters.getSld()));
        } else {
            queryParams.addAll(toPairs("STYLE", parameters.getStyle()));
        }
        queryParams.addAll(toPairs("SLD_VERSION", parameters.getSldVersion()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Retrieve, for the given layers, a description of the underlying data feature types or coverages.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> describeLayer(DescribeLayer parameters) {
        if (parameters.getLayers() == null || parameters.getLayers().isEmpty()) {
            throw new IllegalArgumentException("Layers must be defined");
        }
        final WMSVersion version = version(parameters.getVersion());

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("SERVICE", "WMS"));
        queryParams.addAll(toPairs("VERSION", version.getCode()));
        queryParams.addAll(toPairs("REQUEST", "DescribeLayer"));
        queryParams.addAll(toPairs("csv", "LAYERS", parameters.getLayers()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.header("Accept", "text/xml, application/xml");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    /**
     * Build the query parameters shared by {@code GetMap} and {@code GetFeatureInfo} requests.
     */
    private List<Pair> toGetMapPairs(GetMap parameters, String requestName) {
        if (parameters.getLayers() == null || parameters.getLayers().isEmpty()) {
            throw new IllegalArgumentException("Layers must be defined");
        }
        Objects.requireNonNull(parameters.getBbox(), "Bbox must be defined");
        Objects.requireNonNull(parameters.getWidth(), "Width must be defined");
        Objects.requireNonNull(parameters.getHeight(), "Height must be defined");
        Objects.requireNonNull(parameters.getFormat(), "Format must be defined");

        final WMSVersion version = version(parameters.getVersion());

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs("SERVICE", "WMS"));
        queryParams.addAll(toPairs("VERSION", version.getCode()));
        queryParams.addAll(toPairs("REQUEST", requestName));
        queryParams.addAll(toPairs("csv", "LAYERS", parameters.getLayers()));
        queryParams.addAll(toPairs(crsParameterName(version), toCrs(parameters.getBbox())));
        queryParams.addAll(toPairs("csv", "BBOX", toList(parameters.getBbox())));
        queryParams.addAll(toPairs("WIDTH", parameters.getWidth()));
        queryParams.addAll(toPairs("HEIGHT", parameters.getHeight()));
        queryParams.addAll(toPairs("FORMAT", parameters.getFormat()));
        if (parameters.getTransparent() != null) {
            queryParams.addAll(toPairs("TRANSPARENT", Boolean.toString(parameters.getTransparent()).toUpperCase()));
        }
        queryParams.addAll(toPairs("BGCOLOR", toHex(parameters.getBgColor())));
        queryParams.addAll(toPairs("EXCEPTIONS", parameters.getExceptions()));
        queryParams.addAll(toPairs("ELEVATION", parameters.getElevation()));
        queryParams.addAll(toPairs("TIME", parameters.getTime()));

        //a single style parameter, in order of precedence
        if (parameters.getSldBody() != null) {
            queryParams.addAll(toPairs("SLD_BODY", parameters.getSldBody()));
        } else if (parameters.getSld() != null) {
            queryParams.addAll(toPairs("SLD", parameters.getSld()));
        } else if (parameters.getStyles() != null && !parameters.getStyles().isEmpty()) {
            queryParams.addAll(toPairs("csv", "STYLES", parameters.getStyles()));
        } else {
            //some servers reject a missing STYLES parameter
            queryParams.addAll(toPairs("STYLES", ""));
        }
        queryParams.addAll(toPairs("SLD_VERSION", parameters.getSldVersion()));

        final Map<String, String> dimensions = parameters.getDimensions();
        if (dimensions != null) {
            for (Map.Entry<String, String> entry : dimensions.entrySet()) {
                queryParams.addAll(toPairs("DIM_" + entry.getKey(), entry.getValue()));
            }
        }

        return queryParams;
    }

    private ServiceRequest<Supplier<AbstractWMSCapabilities>> requestAsXml(HttpRequest.Builder request, WMSVersion version) {
        return request(request, (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> {
                try {
                    return WMSBindingUtilities.unmarshall(in, version, AbstractWMSCapabilities.class);
                } catch (JAXBException | MalformedURLException ex) {
                    throw new UncheckedServiceException(ex);
                }
            });
        });
    }

    private static WMSVersion version(WMSVersion version) {
        //version negotiation ("auto") is not implemented, default to the latest version
        return (version == null || version == WMSVersion.auto) ? WMSVersion.v130 : version;
    }

    private static String crsParameterName(WMSVersion version) {
        return version == WMSVersion.v130 ? "CRS" : "SRS";
    }

    private static String toHex(Color color) {
        if (color == null) return null;
        return String.format("0x%02X%02X%02X", color.getRed(), color.getGreen(), color.getBlue());
    }

    /**
     * BBOX is always exactly {@code minx,miny,maxx,maxy} : only the first two dimensions of the
     * envelope are considered, others (elevation, time) must be set through the dedicated
     * {@link GetMap} fields.
     */
    private static List<Double> toList(Envelope env) {
        if (env == null) return null;
        return List.of(env.getMinimum(0), env.getMinimum(1), env.getMaximum(0), env.getMaximum(1));
    }

    private static String toCrs(Envelope env) {
        if (env == null) return null;
        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        if (crs == null) return null;
        return IdentifiedObjects.getIdentifierOrName(crs);
    }

}
