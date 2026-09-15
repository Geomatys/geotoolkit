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
package org.geotoolkit.ogcwxs.client.catalog;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.csw.xml.AbstractCapabilities;
import org.geotoolkit.csw.xml.CSWBindingUtilities;
import org.geotoolkit.csw.xml.CSWMarshallerPool;
import org.geotoolkit.csw.xml.CSWVersion;
import org.geotoolkit.csw.xml.CswXmlFactory;
import org.geotoolkit.csw.xml.ElementSetName;
import org.geotoolkit.csw.xml.GetRecordByIdResponse;
import org.geotoolkit.csw.xml.GetRecordsResponse;
import org.geotoolkit.csw.xml.Query;
import org.geotoolkit.csw.xml.QueryConstraint;
import org.geotoolkit.ogc.xml.FilterMarshallerPool;
import org.geotoolkit.ogc.xml.FilterVersion;
import org.geotoolkit.ogc.xml.XMLFilter;
import org.geotoolkit.ogcwxs.request.catalog.GetCapabilities;
import org.geotoolkit.ogcwxs.request.catalog.GetRecordById;
import org.geotoolkit.ogcwxs.request.catalog.GetRecords;
import org.opengis.filter.Filter;

/**
 * OGC Catalogue Service for the Web (CSW) client.
 *
 * <p>
 * Covers {@code GetCapabilities}, {@code GetRecords} and {@code GetRecordById}. Every request is
 * sent as an HTTP POST with an XML body, following the same transport used by the legacy
 * {@code geotk-client-csw} module.
 * </p>
 *
 * <p>
 * {@code DescribeRecord}, {@code GetDomain}, {@code Harvest} and {@code Transaction} are not
 * covered by this client ({@code DescribeRecord} in particular has no equivalent in CSW 3.0).
 * </p>
 *
 * <blockquote><q>
 * The Discovery class allows clients to discover resources registered in a catalogue, by
 * providing three operations named "query", describeRecordType, and getDomain.
 * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html">OGC 12-168r6, &sect;7.2.4.1</a></small></i>
 * </blockquote>
 *
 * @see https://docs.ogc.org/is/12-168r6/12-168r6.html
 * @author Quentin Bialota (Geomatys)
 */
public final class CSWApi extends AbstractService {

    public CSWApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * Retrieve the service metadata document describing this CSW instance.
     *
     * <blockquote><q>
     * Allows clients to retrieve service metadata describing Catalogue Service instance
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_6">OGC 12-168r6, Table 6</a></small></i>
     * </blockquote>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;AbstractCapabilities&gt;&gt;
     */
    public ServiceRequest<Supplier<AbstractCapabilities>> getCapabilities(GetCapabilities parameters) {
        final CSWVersion version = version(parameters.getVersion());
        final org.geotoolkit.csw.xml.GetCapabilities request = CswXmlFactory.createGetCapabilities(
                version.getCode(), null, null, null, parameters.getUpdateSequence(), "CSW");
        return post(request, in -> unmarshal(in, AbstractCapabilities.class));
    }

    /**
     * Retrieve records (metadata) matching a filter on a set of record types.
     *
     * <blockquote><q>
     * Allows clients to ask a catalogue to execute a query that searches the catalogued metadata
     * and produces a result set containing (zero or more) references to all the registered
     * resources that satisfy the query.
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_8">OGC 12-168r6, Table 8</a></small></i>
     * </blockquote>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;GetRecordsResponse&gt;&gt;
     */
    public ServiceRequest<Supplier<GetRecordsResponse>> getRecords(GetRecords parameters) {
        final CSWVersion version = version(parameters.getVersion());
        return post(buildGetRecords(parameters, version), in -> unmarshal(in, GetRecordsResponse.class));
    }

    private static org.geotoolkit.csw.xml.GetRecordsRequest buildGetRecords(GetRecords parameters, CSWVersion version) {
        final String code = version.getCode();
        final ElementSetName elementSet = CswXmlFactory.createElementSetName(code, parameters.getElementSetName());
        final QueryConstraint constraint = toQueryConstraint(parameters.getFilter(), version);
        final Query query = CswXmlFactory.createQuery(code, parameters.getTypeNames(), elementSet, null, constraint);
        return CswXmlFactory.createGetRecord(code, "CSW", parameters.getResultType(), null, parameters.getOutputFormat(),
                parameters.getOutputSchema(), parameters.getStartPosition(), parameters.getMaxRecords(), query, null);
    }

    /**
     * Retrieve one or more records (metadata) by their identifier.
     *
     * <p>
     * CSW 2.x wraps the result(s) in a {@code GetRecordByIdResponse} element; CSW 3.0 dropped
     * that envelope and returns the requested record document directly (only a single identifier
     * is supported in that case, see {@link GetRecordById#getIds()}). Either way this method
     * returns a uniform {@link GetRecordByIdResponse}.
     * </p>
     *
     * <blockquote><q>
     * The getResourceById operation is inherited from OWS Common and supports the request of one
     * or more resources – in this case full, structured metadata records – from the catalogue.
     * </q><i><small><a href="https://docs.ogc.org/is/12-168r6/12-168r6.html#requirement_7">OGC 12-168r6, &sect;7.2.3.3</a></small></i>
     * </blockquote>
     *
     * @param parameters request parameters
     * @return ServiceRequest&lt;Supplier&lt;GetRecordByIdResponse&gt;&gt;
     */
    public ServiceRequest<Supplier<GetRecordByIdResponse>> getRecordById(GetRecordById parameters) {
        Objects.requireNonNull(parameters.getIds(), "Record identifiers must be defined");
        final CSWVersion version = version(parameters.getVersion());
        final String code = version.getCode();
        final ElementSetName elementSet = CswXmlFactory.createElementSetName(code, parameters.getElementSetName());
        final org.geotoolkit.csw.xml.GetRecordById request = CswXmlFactory.createGetRecordById(code, "CSW",
                elementSet, parameters.getOutputFormat(), parameters.getOutputSchema(), parameters.getIds());
        return post(request, in -> {
            if (version == CSWVersion.v300) {
                return CswXmlFactory.createGetRecordByIdResponse(code, List.of(unmarshal(in, Object.class)));
            }
            return unmarshal(in, GetRecordByIdResponse.class);
        });
    }

    private static QueryConstraint toQueryConstraint(Filter filter, CSWVersion version) {
        if (filter == null || Filter.include().equals(filter)) return null;
        final FilterVersion filterVersion = filterVersion(version);
        final XMLFilter xmlFilter = FilterMarshallerPool.transform(filter, filterVersion);
        return CswXmlFactory.createQueryConstraint(version.getCode(), (Filter) xmlFilter, filterVersionCode(version));
    }

    /**
     * CSW 2.x embeds an OGC Filter 1.1.0, CSW 3.0 embeds a Filter Encoding 2.0 (FES) filter.
     */
    private static FilterVersion filterVersion(CSWVersion version) {
        return version == CSWVersion.v300 ? FilterVersion.V200 : FilterVersion.V110;
    }

    private static String filterVersionCode(CSWVersion version) {
        return version == CSWVersion.v300 ? "2.0.0" : "1.1.0";
    }

    /**
     * Marshal the given CSW request object as the body of a POST request, and turn the response
     * stream into the result value using the given reader.
     */
    private <T> ServiceRequest<Supplier<T>> post(Object requestObject, Function<InputStream, T> reader) {
        final HttpRequest.Builder request = toPostRequest(requestObject);
        return request(request, (HttpResponse.ResponseInfo responseInfo) -> {
            final HttpResponse.BodySubscriber<InputStream> upstream = HttpResponse.BodySubscribers.ofInputStream();
            return HttpResponse.BodySubscribers.mapping(upstream, (InputStream in) -> () -> reader.apply(in));
        });
    }

    private static <T> T unmarshal(InputStream in, Class<T> dataType) {
        try {
            return CSWBindingUtilities.unmarshall(in, dataType);
        } catch (JAXBException ex) {
            throw new UncheckedServiceException(ex);
        }
    }

    private HttpRequest.Builder toPostRequest(Object requestObject) {
        final byte[] body = marshal(requestObject);
        final HttpRequest.Builder request = HttpRequest.newBuilder();
        request.uri(toUri("", List.of()));
        request.header("Content-Type", "text/xml");
        request.header("Accept", "text/xml, application/xml");
        request.method("POST", HttpRequest.BodyPublishers.ofByteArray(body));
        setConfig(request);
        return request;
    }

    private static byte[] marshal(Object requestObject) {
        final MarshallerPool pool = CSWMarshallerPool.getInstance();
        try {
            final Marshaller marshaller = pool.acquireMarshaller();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            marshaller.marshal(requestObject, out);
            pool.recycle(marshaller);
            return out.toByteArray();
        } catch (JAXBException ex) {
            throw new UncheckedServiceException(ex);
        }
    }

    private static CSWVersion version(CSWVersion version) {
        //no "auto" version negotiation in CSWVersion, default to the latest version
        return version == null ? CSWVersion.v300 : version;
    }

}
