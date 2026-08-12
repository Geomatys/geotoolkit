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
package org.geotoolkit.ogcwxs.client.process;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import org.apache.sis.util.UnconvertibleObjectException;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.Pair;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.ogcwxs.request.process.DescribeProcess;
import org.geotoolkit.ogcwxs.request.process.Dismiss;
import org.geotoolkit.ogcwxs.request.process.Execute;
import org.geotoolkit.ogcwxs.request.process.GetCapabilities;
import org.geotoolkit.ogcwxs.request.process.GetResult;
import org.geotoolkit.ogcwxs.request.process.GetStatus;
import org.geotoolkit.ows.xml.v200.AcceptVersionsType;
import org.geotoolkit.ows.xml.v200.CodeType;
import org.geotoolkit.wps.adaptor.ComplexAdaptor;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v200.Capabilities;
import org.geotoolkit.wps.xml.v200.DataOutput;
import org.geotoolkit.wps.xml.v200.Format;
import org.geotoolkit.wps.xml.v200.ProcessOfferings;
import org.geotoolkit.wps.xml.v200.Result;
import org.geotoolkit.wps.xml.v200.StatusInfo;

/**
 * OGC Web Processing Service (WPS) client.
 *
 * <p>
 * Covers the core operations of WPS 2.0/2.0.1 (OGC 14-065) : {@code GetCapabilities},
 * {@code DescribeProcess}, {@code Execute}, {@code GetStatus}, {@code GetResult} and
 * {@code Dismiss}. Requests are always built as WPS 2.0.0 documents ; wire compatibility with
 * WPS 1.0.0 servers is handled transparently by {@link WPSMarshallerPool}. This client exposes
 * the raw asynchronous operations only : callers wanting synchronous-until-done behaviour must
 * poll {@link #getStatus(GetStatus)} / {@link #getResult(GetResult)} themselves.
 * </p>
 *
 * @see https://www.ogc.org/standards/wps/
 * @author Quentin Bialota (Geomatys)
 */
public final class WPSApi extends AbstractService {

    public WPSApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the service metadata document describing this WPS instance : supported
     * operations, offered processes, formats, etc.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Capabilities&gt;&gt;
     */
    public ServiceRequest<Supplier<Capabilities>> getCapabilities(GetCapabilities parameters) {
        final String version = version(parameters.getVersion());
        final org.geotoolkit.wps.xml.v200.GetCapabilities req = new org.geotoolkit.wps.xml.v200.GetCapabilities(
                new AcceptVersionsType(version), null, null, parameters.getUpdateSequence(), "WPS", null);
        return get(req.toKVP(), Capabilities.class);
    }

    /**
     * Retrieve a full description of one or more processes offered by the server.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;ProcessOfferings&gt;&gt;
     */
    public ServiceRequest<Supplier<ProcessOfferings>> describeProcess(DescribeProcess parameters) {
        if (parameters.getIdentifiers() == null || parameters.getIdentifiers().isEmpty()) {
            throw new IllegalArgumentException("Identifiers must be defined");
        }
        final String version = version(parameters.getVersion());
        final List<CodeType> identifiers = new ArrayList<>();
        for (String identifier : parameters.getIdentifiers()) {
            identifiers.add(new CodeType(identifier));
        }
        final org.geotoolkit.wps.xml.v200.DescribeProcess req =
                new org.geotoolkit.wps.xml.v200.DescribeProcess("WPS", version, null, identifiers);
        return get(req.toKVP(), ProcessOfferings.class);
    }

    /**
     * Poll the status of a job previously accepted for asynchronous execution. Not available in
     * WPS 1.0.0.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;StatusInfo&gt;&gt;
     */
    public ServiceRequest<Supplier<StatusInfo>> getStatus(GetStatus parameters) {
        Objects.requireNonNull(parameters.getJobId(), "Job id must be defined");
        final String version = version(parameters.getVersion());
        final org.geotoolkit.wps.xml.v200.GetStatus req =
                new org.geotoolkit.wps.xml.v200.GetStatus("WPS", version, parameters.getJobId());
        return get(req.toKVP(), StatusInfo.class);
    }

    /**
     * Retrieve the result of a job that has completed asynchronous execution. Not available in
     * WPS 1.0.0.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Result&gt;&gt;
     */
    public ServiceRequest<Supplier<Result>> getResult(GetResult parameters) {
        Objects.requireNonNull(parameters.getJobId(), "Job id must be defined");
        final String version = version(parameters.getVersion());
        final org.geotoolkit.wps.xml.v200.GetResult req =
                new org.geotoolkit.wps.xml.v200.GetResult("WPS", version, parameters.getJobId());
        return get(req.toKVP(), Result.class);
    }

    /**
     * Cancel a running job, or clean up the stored result of a completed one. Not available in
     * WPS 1.0.0.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;StatusInfo&gt;&gt;
     */
    public ServiceRequest<Supplier<StatusInfo>> dismiss(Dismiss parameters) {
        Objects.requireNonNull(parameters.getJobId(), "Job id must be defined");
        final String version = version(parameters.getVersion());
        final org.geotoolkit.wps.xml.v200.Dismiss req =
                new org.geotoolkit.wps.xml.v200.Dismiss("WPS", version, parameters.getJobId());
        return get(req.toKVP(), StatusInfo.class);
    }

    /**
     * Execute an identified process, synchronously or asynchronously depending on
     * {@link Execute#getMode()}. The returned document is either a {@link StatusInfo} (job
     * accepted for asynchronous execution) or a {@link Result} (execution already completed) :
     * this client does not poll on the caller's behalf.
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;Object&gt;&gt;
     */
    public ServiceRequest<Supplier<Object>> execute(Execute parameters) {
        Objects.requireNonNull(parameters.getIdentifier(), "Identifier must be defined");
        final String version = version(parameters.getVersion());

        final org.geotoolkit.wps.xml.v200.Execute req = new org.geotoolkit.wps.xml.v200.Execute(
                "WPS", version, null, new CodeType(parameters.getIdentifier()),
                parameters.getInputs(), parameters.getOutputs(), parameters.getResponse());
        if (parameters.getMode() != null) {
            req.setMode(parameters.getMode());
        }

        final byte[] body;
        final MarshallerPool pool = WPSMarshallerPool.getInstance();
        try {
            final Marshaller marshaller = pool.acquireMarshaller();
            try {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                marshaller.marshal(req, out);
                body = out.toByteArray();
            } finally {
                pool.recycle(marshaller);
            }
        } catch (JAXBException ex) {
            throw new UncheckedServiceException(ex);
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", List.of()));
        request.header("Content-Type", "text/xml");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(body));
        setConfig(request);
        return requestAsXml(request, Object.class);
    }

    /**
     * Decode a process output holding complex data (or a reference to it) into a typed Java
     * object, using the {@link ComplexAdaptor} registered for its format.
     *
     * @param output the output to decode
     * @return the decoded value
     * @throws UnconvertibleObjectException if no adaptor is registered for the output's format
     */
    public static Object decodeOutput(DataOutput output) throws UnconvertibleObjectException {
        final String mimeType, encoding, schema;
        if (output.getData() != null) {
            mimeType = output.getData().getMimeType();
            encoding = output.getData().getEncoding();
            schema = output.getData().getSchema();
        } else if (output.getReference() != null) {
            mimeType = output.getReference().getMimeType();
            encoding = output.getReference().getEncoding();
            schema = output.getReference().getSchema();
        } else {
            throw new UnconvertibleObjectException("Output has neither data nor reference");
        }

        final ComplexAdaptor adaptor = ComplexAdaptor.getAdaptor(new Format(encoding, mimeType, schema, null));
        if (adaptor == null) {
            throw new UnconvertibleObjectException("No adaptor available for format: " + mimeType);
        }
        return adaptor.fromWPS2Input(output);
    }

    private <T> ServiceRequest<Supplier<T>> get(Map<String, String> kvp, Class<T> type) {
        final List<Pair> queryParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : kvp.entrySet()) {
            queryParams.addAll(toPairs(entry.getKey(), entry.getValue()));
        }

        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", queryParams));
        request.header("Accept", "text/xml, application/xml");
        request.method("GET", HttpRequest.BodyPublishers.noBody());
        setConfig(request);
        return requestAsXml(request, type);
    }

    private <T> ServiceRequest<Supplier<T>> requestAsXml(HttpRequest.Builder request, Class<T> type) {
        return request(request, (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> {
                final MarshallerPool pool = WPSMarshallerPool.getInstance();
                try {
                    final Unmarshaller unmarshaller = pool.acquireUnmarshaller();
                    try {
                        Object result = unmarshaller.unmarshal(in);
                        if (result instanceof JAXBElement) {
                            result = ((JAXBElement) result).getValue();
                        }
                        return type.cast(result);
                    } finally {
                        pool.recycle(unmarshaller);
                    }
                } catch (JAXBException ex) {
                    throw new UncheckedServiceException(ex);
                }
            });
        });
    }

    private static String version(String version) {
        //version negotiation ("auto") is not implemented, default to the latest version
        return (version == null) ? "2.0.0" : version;
    }

}
