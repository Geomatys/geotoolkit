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
package org.geotoolkit.ogcwxs.client.coverage;

import java.awt.Dimension;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import org.apache.sis.referencing.CRS;
import org.apache.sis.referencing.IdentifiedObjects;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.ogcwxs.request.coverage.DescribeCoverage;
import org.geotoolkit.ogcwxs.request.coverage.GetCapabilities;
import org.geotoolkit.ogcwxs.request.coverage.GetCoverage;
import org.geotoolkit.wcs.xml.DescribeCoverageResponse;
import org.geotoolkit.wcs.xml.GetCapabilitiesResponse;
import org.geotoolkit.wcs.xml.WCSMarshallerPool;
import org.geotoolkit.wcs.xml.WCSVersion;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

/**
 * OGC Web Coverage Service (WCS) client.
 *
 * <p>
 * Covers the operations defined by the WCS 1.0.0 (OGC 07-067) and 2.0.0/2.0.1 (OGC 09-110r4) core
 * specifications: {@code GetCapabilities}, {@code DescribeCoverage} and {@code GetCoverage}. For 2.x,
 * only the core KVP binding is supported (identifier, spatial subset, format, CRS) — the Scaling and
 * Interpolation extensions (size/resolution, time, interpolation) are not implemented and requesting
 * them throws {@link IllegalArgumentException} rather than silently ignoring them.
 * </p>
 *
 * @see https://www.ogc.org/standards/wcs/
 * @author Quentin Bialota (Geomatys)
 */
public final class WCSApi extends AbstractService {

    /**
     * Service name.
     */
    public static final String SERVICE_NAME = "WCS";

    /**
     * Operation names.
     */
    public static final String OP_DESCRIBE_COVERAGE = "DescribeCoverage";
    public static final String OP_GET_CAPABILITIES  = "GetCapabilities";
    public static final String OP_GET_COVERAGE      = "GetCoverage";

    /**
     * Parameter names.
     */
    public static final String PARAM_BBOX           = "BBOX";
    public static final String PARAM_COVERAGE       = "COVERAGE";
    public static final String PARAM_COVERAGEID     = "COVERAGEID";
    public static final String PARAM_CRS            = "CRS";
    public static final String PARAM_DEPTH          = "DEPTH";
    public static final String PARAM_EXCEPTIONS     = "EXCEPTIONS";
    public static final String PARAM_FORMAT         = "FORMAT";
    public static final String PARAM_HEIGHT         = "HEIGHT";
    public static final String PARAM_OUTPUT_CRS     = "OUTPUTCRS";
    public static final String PARAM_REQUEST        = "REQUEST";
    public static final String PARAM_RESPONSECRS    = "RESPONSECRS";
    public static final String PARAM_RESX           = "RESX";
    public static final String PARAM_RESY           = "RESY";
    public static final String PARAM_RESZ           = "RESZ";
    public static final String PARAM_SERVICE        = "SERVICE";
    public static final String PARAM_SUBSET         = "SUBSET";
    public static final String PARAM_SUBSETTING_CRS = "SUBSETTINGCRS";
    public static final String PARAM_TIME           = "TIME";
    public static final String PARAM_UPDATESEQUENCE = "UPDATESEQUENCE";
    public static final String PARAM_VERSION        = "VERSION";
    public static final String PARAM_WIDTH          = "WIDTH";

    public WCSApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the service metadata document describing this WCS instance : supported operations,
     * coverages, formats, coordinate reference systems, etc.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;GetCapabilitiesResponse&gt;&gt;
     */
    public ServiceRequest<Supplier<GetCapabilitiesResponse>> getCapabilities(GetCapabilities parameters) {
        final WCSVersion version = version(parameters.getVersion());

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_SERVICE, SERVICE_NAME));
        queryParams.addAll(toPairs(PARAM_VERSION, version.getCode()));
        queryParams.addAll(toPairs(PARAM_REQUEST, OP_GET_CAPABILITIES));
        queryParams.addAll(toPairs(PARAM_UPDATESEQUENCE, parameters.getUpdateSequence()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.header("Accept", "text/xml, application/xml");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsXml(request, GetCapabilitiesResponse.class);
    }

    /**
     * Retrieve a full description of one or more coverages offered by the server.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;DescribeCoverageResponse&gt;&gt;
     */
    public ServiceRequest<Supplier<DescribeCoverageResponse>> describeCoverage(DescribeCoverage parameters) {
        final WCSVersion version = version(parameters.getVersion());

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_SERVICE, SERVICE_NAME));
        queryParams.addAll(toPairs(PARAM_VERSION, version.getCode()));
        queryParams.addAll(toPairs(PARAM_REQUEST, OP_DESCRIBE_COVERAGE));
        if (isV2(version)) {
            queryParams.addAll(toPairs("csv", PARAM_COVERAGEID, parameters.getCoverages()));
        } else {
            queryParams.addAll(toPairs("csv", PARAM_COVERAGE, parameters.getCoverages()));
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.header("Accept", "text/xml, application/xml");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsXml(request, DescribeCoverageResponse.class);
    }

    /**
     * Retrieve a coverage, encoded in a given format.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     */
    public ServiceRequest<byte[]> getCoverage(GetCoverage parameters) {
        Objects.requireNonNull(parameters.getCoverage(), "Coverage must be defined");
        Objects.requireNonNull(parameters.getEnvelope(), "Envelope must be defined");
        Objects.requireNonNull(parameters.getFormat(), "Format must be defined");

        final WCSVersion version = version(parameters.getVersion());
        final Envelope envelope = parameters.getEnvelope();

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_SERVICE, SERVICE_NAME));
        queryParams.addAll(toPairs(PARAM_VERSION, version.getCode()));
        queryParams.addAll(toPairs(PARAM_REQUEST, OP_GET_COVERAGE));

        if (isV2(version)) {
            if (parameters.getDimension() != null || parameters.getResX() != null || parameters.getResY() != null
                    || parameters.getResZ() != null || parameters.getDepth() != null || parameters.getTime() != null) {
                throw new IllegalArgumentException(
                        "dimension/resX/resY/resZ/depth/time are not supported by the WCS 2.x core KVP binding " +
                        "(Scaling/Interpolation extensions are not implemented)");
            }
            queryParams.addAll(toPairs(PARAM_COVERAGEID, parameters.getCoverage()));
            queryParams.addAll(toPairs("multi", PARAM_SUBSET, toSubsets(envelope)));
            queryParams.addAll(toPairs(PARAM_FORMAT, parameters.getFormat()));
            queryParams.addAll(toPairs(PARAM_EXCEPTIONS, parameters.getExceptions()));
            final String subsettingCrs = toUrn(envelope.getCoordinateReferenceSystem());
            if (subsettingCrs != null) {
                queryParams.addAll(toPairs(PARAM_SUBSETTING_CRS, subsettingCrs));
            }
            final CoordinateReferenceSystem responseCRS = parameters.getResponseCRS();
            if (responseCRS != null) {
                final String outputCrs = toUrn(responseCRS);
                if (outputCrs != null) {
                    queryParams.addAll(toPairs(PARAM_OUTPUT_CRS, outputCrs));
                }
            }
        } else {
            queryParams.addAll(toPairs(PARAM_COVERAGE, parameters.getCoverage()));
            queryParams.addAll(toPairs("csv", PARAM_BBOX, toList(envelope)));
            queryParams.addAll(toPairs(PARAM_CRS, toCrs(envelope)));
            queryParams.addAll(toPairs(PARAM_FORMAT, parameters.getFormat()));
            queryParams.addAll(toPairs(PARAM_TIME, parameters.getTime()));
            queryParams.addAll(toPairs(PARAM_EXCEPTIONS, parameters.getExceptions()));

            final Dimension dimension = parameters.getDimension();
            if (dimension != null) {
                queryParams.addAll(toPairs(PARAM_WIDTH, dimension.width));
                queryParams.addAll(toPairs(PARAM_HEIGHT, dimension.height));
                queryParams.addAll(toPairs(PARAM_DEPTH, parameters.getDepth()));
            } else if (parameters.getResX() != null && parameters.getResY() != null) {
                queryParams.addAll(toPairs(PARAM_RESX, parameters.getResX()));
                queryParams.addAll(toPairs(PARAM_RESY, parameters.getResY()));
                queryParams.addAll(toPairs(PARAM_RESZ, parameters.getResZ()));
            }

            final CoordinateReferenceSystem responseCRS = parameters.getResponseCRS();
            if (responseCRS != null) {
                queryParams.addAll(toPairs(PARAM_RESPONSECRS, IdentifiedObjects.getIdentifierOrName(responseCRS)));
            }
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    private <T> ServiceRequest<Supplier<T>> requestAsXml(HttpRequest.Builder request, Class<T> responseType) {
        return request(request, (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> {
                try {
                    final Unmarshaller unmarshaller = WCSMarshallerPool.getInstance().acquireUnmarshaller();
                    try {
                        return responseType.cast(unmarshaller.unmarshal(in));
                    } finally {
                        WCSMarshallerPool.getInstance().recycle(unmarshaller);
                    }
                } catch (JAXBException ex) {
                    throw new UncheckedServiceException(ex);
                }
            });
        });
    }

    private static WCSVersion version(WCSVersion version) {
        return version == null ? WCSVersion.v100 : version;
    }

    private static boolean isV2(WCSVersion version) {
        return version == WCSVersion.v200 || version == WCSVersion.v201;
    }

    /**
     * WCS 2.x core {@code subset} KVP: one {@code <axisAbbrev>(<min>,<max>)} entry per axis of the
     * envelope, e.g. {@code Long(10,20)}.
     */
    private static List<String> toSubsets(Envelope env) {
        final CoordinateReferenceSystem crs = env.getCoordinateReferenceSystem();
        final List<String> subsets = new ArrayList<>();
        for (int i = 0; i < env.getDimension(); i++) {
            final String axis = (crs != null) ? crs.getCoordinateSystem().getAxis(i).getAbbreviation() : "d" + i;
            subsets.add(axis + "(" + env.getMinimum(i) + "," + env.getMaximum(i) + ")");
        }
        return subsets;
    }

    private static String toUrn(CoordinateReferenceSystem crs) {
        if (crs == null) return null;
        try {
            return IdentifiedObjects.lookupURN(crs, null);
        } catch (org.opengis.util.FactoryException ex) {
            return null;
        }
    }

    /**
     * BBOX is {@code minx,miny,maxx,maxy} (plus {@code minz,maxz} for a 3D envelope) : only the first
     * two or three dimensions of the envelope are considered.
     */
    private static List<Double> toList(Envelope env) {
        final List<Double> list = new ArrayList<>();
        list.add(env.getMinimum(0));
        list.add(env.getMinimum(1));
        list.add(env.getMaximum(0));
        list.add(env.getMaximum(1));
        if (env.getDimension() > 2) {
            list.add(env.getMinimum(2));
            list.add(env.getMaximum(2));
        }
        return list;
    }

    private static String toCrs(Envelope env) {
        final CoordinateReferenceSystem crs = CRS.getHorizontalComponent(env.getCoordinateReferenceSystem());
        if (crs == null) return null;
        return IdentifiedObjects.getIdentifierOrName(crs);
    }

}
