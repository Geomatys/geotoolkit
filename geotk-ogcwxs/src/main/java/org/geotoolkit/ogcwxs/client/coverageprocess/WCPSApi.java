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
package org.geotoolkit.ogcwxs.client.coverageprocess;

import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.ogcwxs.request.coverageprocess.ProcessCoverages;
import org.geotoolkit.wcs.xml.WCSVersion;

/**
 * OGC Web Coverage Processing Service (WCPS) client.
 *
 * <p>
 * Covers the {@code ProcessCoverages} operation defined by the WCS Processing Extension
 * (OGC 08-059r4), which carries a versionless WCPS (OGC 08-068) abstract syntax query as
 * the {@code QUERY} parameter of a WCS request. Only the GET/KVP binding is supported.
 * {@code GetCapabilities} and {@code DescribeCoverage} are plain WCS operations, not
 * redefined by WCPS: use {@link org.geotoolkit.ogcwxs.client.coverage.WCSApi} for those
 * against the same endpoint.
 * </p>
 *
 * @author Quentin Bialota (Geomatys)
 * @see <a href="https://docs.ogc.org/is/08-059r4/08-059r4.pdf">OGC 08-059r4</a>
 * @see <a href="https://docs.ogc.org/is/08-068r2/08-068r2.pdf">OGC 08-068r2</a>
 */
public final class WCPSApi extends AbstractService {

    /**
     * Service name.
     */
    public static final String SERVICE_NAME = "WCS";

    /**
     * Operation names.
     */
    public static final String OP_PROCESS_COVERAGES = "ProcessCoverages";

    /**
     * Parameter names.
     */
    public static final String PARAM_QUERY   = "QUERY";
    public static final String PARAM_REQUEST = "REQUEST";
    public static final String PARAM_SERVICE = "SERVICE";
    public static final String PARAM_VERSION = "VERSION";

    public WCPSApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * <blockquote><q>
     * A ProcessCoverages request has one mandatory parameter containing the WCPS query
     * string and zero or more optional parameters (like scalars or features, including the
     * feature subtype coverage) acting as ad-hoc input to a parametrized query.
     * </q><i><small><a href="https://docs.ogc.org/is/08-059r4/08-059r4.pdf">OGC 08-059r4, &sect;6.5.1</a></small></i>
     * </blockquote>
     * <br>
     * <ul>
     * <li>Request parameter is encoded with key {@code REQUEST} and value
     * {@code ProcessCoverages}.</li>
     * <li>Query parameter is encoded with key {@code QUERY} and its value representing a
     * printable string.</li>
     * <li>{@code VERSION} identifies the WCS service version, not the WCPS query language
     * version.</li>
     * </ul>
     * <pre>
     * http://myserver.com/wcs?
     *     SERVICE=WCS&amp;VERSION=2.0&amp;REQUEST=ProcessCoverages&amp;
     *     query=for%20$c%20in%20%28Scene1%29%
     *           return%20encode%28%20$c.red+42,%20%22image%2Fjp2%22%29
     * </pre>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;byte[]&gt;
     * @see <a href="https://docs.ogc.org/is/08-059r4/08-059r4.pdf">OGC 08-059r4, &sect;6.7.1</a>
     */
    public ServiceRequest<byte[]> processCoverages(ProcessCoverages parameters) {
        Objects.requireNonNull(parameters.getQuery(), "Query must be defined");
        final WCSVersion version = version(parameters.getVersion());

        final List<Pair> queryParams = new ArrayList<>();
        queryParams.addAll(toPairs(PARAM_SERVICE, SERVICE_NAME));
        queryParams.addAll(toPairs(PARAM_VERSION, version.getCode()));
        queryParams.addAll(toPairs(PARAM_REQUEST, OP_PROCESS_COVERAGES));
        queryParams.addAll(toPairs(PARAM_QUERY, parameters.getQuery()));

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return request(request, HttpResponse.BodyHandlers.ofByteArray());
    }

    private static WCSVersion version(WCSVersion version) {
        return version == null ? WCSVersion.v201 : version;
    }

}
