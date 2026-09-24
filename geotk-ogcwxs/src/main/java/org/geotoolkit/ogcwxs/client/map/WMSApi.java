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

    /**
     * Service name.
     */
    public static final String SERVICE_NAME = "WMS";

    /**
     * Operation names.
     */
    public static final String OP_DESCRIBE_LAYER     = "DescribeLayer";
    public static final String OP_GET_CAPABILITIES   = "GetCapabilities";
    public static final String OP_GET_FEATURE_INFO   = "GetFeatureInfo";
    public static final String OP_GET_LEGEND_GRAPHIC = "GetLegendGraphic";
    public static final String OP_GET_MAP            = "GetMap";

    /**
     * Parameter names.
     */
    public static final String PARAM_BBOX           = "BBOX";
    public static final String PARAM_BGCOLOR        = "BGCOLOR";
    public static final String PARAM_CRS            = "CRS";
    public static final String PARAM_DIM_PREFIX     = "DIM_";
    public static final String PARAM_ELEVATION      = "ELEVATION";
    public static final String PARAM_EXCEPTIONS     = "EXCEPTIONS";
    public static final String PARAM_FEATURE_COUNT  = "FEATURE_COUNT";
    public static final String PARAM_FORMAT         = "FORMAT";
    public static final String PARAM_HEIGHT         = "HEIGHT";
    public static final String PARAM_I              = "I";
    public static final String PARAM_INFO_FORMAT    = "INFO_FORMAT";
    public static final String PARAM_J              = "J";
    public static final String PARAM_LAYER          = "LAYER";
    public static final String PARAM_LAYERS         = "LAYERS";
    public static final String PARAM_QUERY_LAYERS   = "QUERY_LAYERS";
    public static final String PARAM_REQUEST        = "REQUEST";
    public static final String PARAM_RULE           = "RULE";
    public static final String PARAM_SCALE          = "SCALE";
    public static final String PARAM_SERVICE        = "SERVICE";
    public static final String PARAM_SLD            = "SLD";
    public static final String PARAM_SLD_BODY       = "SLD_BODY";
    public static final String PARAM_SLD_VERSION    = "SLD_VERSION";
    public static final String PARAM_SRS            = "SRS";
    public static final String PARAM_STYLE          = "STYLE";
    public static final String PARAM_STYLES         = "STYLES";
    public static final String PARAM_TIME           = "TIME";
    public static final String PARAM_TRANSPARENT    = "TRANSPARENT";
    public static final String PARAM_UPDATESEQUENCE = "UPDATESEQUENCE";
    public static final String PARAM_VERSION        = "VERSION";
    public static final String PARAM_WIDTH          = "WIDTH";
    public static final String PARAM_WMTVER         = "WMTVER";
    public static final String PARAM_X              = "X";
    public static final String PARAM_Y              = "Y";

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
        queryParams.addAll(toPairs(PARAM_SERVICE, SERVICE_NAME));
        queryParams.addAll(toPairs(version == WMSVersion.v100 ? PARAM_WMTVER : PARAM_VERSION, version.getCode()));
        queryParams.addAll(toPairs(PARAM_REQUEST, OP_GET_CAPABILITIES));
        queryParams.addAll(toPairs(PARAM_UPDATESEQUENCE, parameters.getUpdateSequence()));

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
        final List<Pair> queryParams = toGetMapPairs(parameters, OP_GET_MAP);

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
        final List<Pair> queryParams = toGetMapPairs(parameters, OP_GET_FEATURE_INFO);
        queryParams.addAll(toPairs("csv", PARAM_QUERY_LAYERS, parameters.getQueryLayers()));
        queryParams.addAll(toPairs(PARAM_INFO_FORMAT, parameters.getInfoFormat()));
        queryParams.addAll(toPairs(PARAM_FEATURE_COUNT, parameters.getFeatureCount()));
        queryParams.addAll(toPairs(version == WMSVersion.v130 ? PARAM_I : PARAM_X, parameters.getX()));
        queryParams.addAll(toPairs(version == WMSVersion.v130 ? PARAM_J : PARAM_Y, parameters.getY()));

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
        queryParams.addAll(toPairs(PARAM_SERVICE, SERVICE_NAME));
        queryParams.addAll(toPairs(PARAM_VERSION, version.getCode()));
        queryParams.addAll(toPairs(PARAM_REQUEST, OP_GET_LEGEND_GRAPHIC));
        queryParams.addAll(toPairs(PARAM_LAYER, parameters.getLayer()));
        queryParams.addAll(toPairs(PARAM_FORMAT, parameters.getFormat()));
        queryParams.addAll(toPairs(PARAM_WIDTH, parameters.getWidth()));
        queryParams.addAll(toPairs(PARAM_HEIGHT, parameters.getHeight()));
        queryParams.addAll(toPairs(PARAM_EXCEPTIONS, parameters.getExceptions()));
        queryParams.addAll(toPairs(PARAM_RULE, parameters.getRule()));
        queryParams.addAll(toPairs(PARAM_SCALE, parameters.getScale()));
        if (parameters.getSldBody() != null) {
            queryParams.addAll(toPairs(PARAM_SLD_BODY, parameters.getSldBody()));
        } else if (parameters.getSld() != null) {
            queryParams.addAll(toPairs(PARAM_SLD, parameters.getSld()));
        } else {
            queryParams.addAll(toPairs(PARAM_STYLE, parameters.getStyle()));
        }
        queryParams.addAll(toPairs(PARAM_SLD_VERSION, parameters.getSldVersion()));

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
        queryParams.addAll(toPairs(PARAM_SERVICE, SERVICE_NAME));
        queryParams.addAll(toPairs(PARAM_VERSION, version.getCode()));
        queryParams.addAll(toPairs(PARAM_REQUEST, OP_DESCRIBE_LAYER));
        queryParams.addAll(toPairs("csv", PARAM_LAYERS, parameters.getLayers()));

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
        queryParams.addAll(toPairs(PARAM_SERVICE, SERVICE_NAME));
        queryParams.addAll(toPairs(PARAM_VERSION, version.getCode()));
        queryParams.addAll(toPairs(PARAM_REQUEST, requestName));
        queryParams.addAll(toPairs("csv", PARAM_LAYERS, parameters.getLayers()));
        queryParams.addAll(toPairs(crsParameterName(version), toCrs(parameters.getBbox())));
        queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(parameters.getBbox())));
        queryParams.addAll(toPairs(PARAM_WIDTH, parameters.getWidth()));
        queryParams.addAll(toPairs(PARAM_HEIGHT, parameters.getHeight()));
        queryParams.addAll(toPairs(PARAM_FORMAT, parameters.getFormat()));
        if (parameters.getTransparent() != null) {
            queryParams.addAll(toPairs(PARAM_TRANSPARENT, Boolean.toString(parameters.getTransparent()).toUpperCase()));
        }
        queryParams.addAll(toPairs(PARAM_BGCOLOR, toHex(parameters.getBgColor())));
        queryParams.addAll(toPairs(PARAM_EXCEPTIONS, parameters.getExceptions()));
        queryParams.addAll(toPairs(PARAM_ELEVATION, parameters.getElevation()));
        queryParams.addAll(toPairs(PARAM_TIME, parameters.getTime()));

        //a single style parameter, in order of precedence
        if (parameters.getSldBody() != null) {
            queryParams.addAll(toPairs(PARAM_SLD_BODY, parameters.getSldBody()));
        } else if (parameters.getSld() != null) {
            queryParams.addAll(toPairs(PARAM_SLD, parameters.getSld()));
        } else if (parameters.getStyles() != null && !parameters.getStyles().isEmpty()) {
            queryParams.addAll(toPairs("csv", PARAM_STYLES, parameters.getStyles()));
        } else {
            //some servers reject a missing STYLES parameter
            queryParams.addAll(toPairs(PARAM_STYLES, ""));
        }
        queryParams.addAll(toPairs(PARAM_SLD_VERSION, parameters.getSldVersion()));

        final Map<String, String> dimensions = parameters.getDimensions();
        if (dimensions != null) {
            for (Map.Entry<String, String> entry : dimensions.entrySet()) {
                queryParams.addAll(toPairs(PARAM_DIM_PREFIX + entry.getKey(), entry.getValue()));
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
        return version == WMSVersion.v130 ? PARAM_CRS : PARAM_SRS;
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
