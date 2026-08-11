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
 * Covers the operations defined by the WCS 1.0.0 specification (OGC 07-067): {@code GetCapabilities},
 * {@code DescribeCoverage} and {@code GetCoverage}.
 * </p>
 *
 * @see https://www.ogc.org/standards/wcs/
 * @author Quentin Bialota (Geomatys)
 */
public final class WCSApi extends AbstractService {

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
        queryParams.addAll(toPairs("SERVICE", "WCS"));
        queryParams.addAll(toPairs("VERSION", version.getCode()));
        queryParams.addAll(toPairs("REQUEST", "GetCapabilities"));
        queryParams.addAll(toPairs("UPDATESEQUENCE", parameters.getUpdateSequence()));

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
        queryParams.addAll(toPairs("SERVICE", "WCS"));
        queryParams.addAll(toPairs("VERSION", version.getCode()));
        queryParams.addAll(toPairs("REQUEST", "DescribeCoverage"));
        queryParams.addAll(toPairs("csv", "COVERAGE", parameters.getCoverages()));

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
        queryParams.addAll(toPairs("SERVICE", "WCS"));
        queryParams.addAll(toPairs("VERSION", version.getCode()));
        queryParams.addAll(toPairs("REQUEST", "GetCoverage"));
        queryParams.addAll(toPairs("COVERAGE", parameters.getCoverage()));
        queryParams.addAll(toPairs("csv", "BBOX", toList(envelope)));
        queryParams.addAll(toPairs("CRS", toCrs(envelope)));
        queryParams.addAll(toPairs("FORMAT", parameters.getFormat()));
        queryParams.addAll(toPairs("TIME", parameters.getTime()));
        queryParams.addAll(toPairs("EXCEPTIONS", parameters.getExceptions()));

        final Dimension dimension = parameters.getDimension();
        if (dimension != null) {
            queryParams.addAll(toPairs("WIDTH", dimension.width));
            queryParams.addAll(toPairs("HEIGHT", dimension.height));
            queryParams.addAll(toPairs("DEPTH", parameters.getDepth()));
        } else if (parameters.getResX() != null && parameters.getResY() != null) {
            queryParams.addAll(toPairs("RESX", parameters.getResX()));
            queryParams.addAll(toPairs("RESY", parameters.getResY()));
            queryParams.addAll(toPairs("RESZ", parameters.getResZ()));
        }

        final CoordinateReferenceSystem responseCRS = parameters.getResponseCRS();
        if (responseCRS != null) {
            queryParams.addAll(toPairs("RESPONSECRS", IdentifiedObjects.getIdentifierOrName(responseCRS)));
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
