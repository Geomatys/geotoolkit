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
package org.geotoolkit.ogcwxs.client.join;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.apache.sis.xml.MarshallerPool;
import org.geotoolkit.client.service.AbstractService;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.client.service.ServiceRequest;
import org.geotoolkit.client.service.UncheckedServiceException;
import org.geotoolkit.ogcwxs.request.join.DescribeData;
import org.geotoolkit.ogcwxs.request.join.DescribeDatasets;
import org.geotoolkit.ogcwxs.request.join.DescribeFrameworks;
import org.geotoolkit.ogcwxs.request.join.DescribeJoinAbilities;
import org.geotoolkit.ogcwxs.request.join.DescribeKey;
import org.geotoolkit.ogcwxs.request.join.GetCapabilities;
import org.geotoolkit.ogcwxs.request.join.GetData;
import org.geotoolkit.ogcwxs.request.join.JoinData;
import org.geotoolkit.tjs.xml.TJSBindingUtilities;
import org.geotoolkit.tjs.xml.TJSMarshallerPool;
import org.geotoolkit.tjs.xml.v100.AttributeDataType;
import org.geotoolkit.tjs.xml.v100.CapabilitiesType;
import org.geotoolkit.tjs.xml.v100.DataDescriptionsType;
import org.geotoolkit.tjs.xml.v100.DatasetDescriptionsType;
import org.geotoolkit.tjs.xml.v100.DescribeDataType;
import org.geotoolkit.tjs.xml.v100.DescribeDatasetsType;
import org.geotoolkit.tjs.xml.v100.DescribeFrameworksType;
import org.geotoolkit.tjs.xml.v100.DescribeJoinAbilitiesType;
import org.geotoolkit.tjs.xml.v100.DescribeKeyType;
import org.geotoolkit.tjs.xml.v100.FrameworkDescriptionsType;
import org.geotoolkit.tjs.xml.v100.FrameworkKeyDescriptionType;
import org.geotoolkit.tjs.xml.v100.GDASType;
import org.geotoolkit.tjs.xml.v100.GetCapabilitiesType;
import org.geotoolkit.tjs.xml.v100.GetDataType;
import org.geotoolkit.tjs.xml.v100.GetDataXMLType;
import org.geotoolkit.tjs.xml.v100.JoinAbilitiesType;
import org.geotoolkit.tjs.xml.v100.JoinDataRequestType;
import org.geotoolkit.tjs.xml.v100.JoinDataResponseType;
import org.geotoolkit.tjs.xml.v100.MapStylingType;

/**
 * OGC Table Join Service (TJS) 1.0 client.
 *
 * <p>Every request is sent as an HTTP POST with an XML body, mirroring the CSW client
 * ({@link org.geotoolkit.ogcwxs.client.catalog.CSWApi}).</p>
 *
 * @author Quentin Bialota (Geomatys)
 * @see <a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2</a>
 */
public final class TJSApi extends AbstractService {

    public TJSApi(ServiceConfiguration config) {
        super(config);
    }

    /**
     * <blockquote><q>
     * Like most OGC services, TJS includes a mandatory GetCapabilities operation. This
     * operation allows clients to retrieve service metadata.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;8.1</a></small></i>
     * </blockquote>
     *
     * @param parameters the request parameters
     * @return a request yielding the server's {@link CapabilitiesType}
     */
    public ServiceRequest<Supplier<CapabilitiesType>> getCapabilities(GetCapabilities parameters) {
        final GetCapabilitiesType request = new GetCapabilitiesType();
        request.setLanguage(parameters.getLanguage());
        request.setUpdateSequence(parameters.getUpdateSequence());
        return post(request, in -> unmarshal(in, CapabilitiesType.class));
    }

    /**
     * <blockquote><q>
     * The DescribeFrameworks operation returns an XML document that describes one or more
     * spatial frameworks for which data is available from the server.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;9.1</a></small></i>
     * </blockquote>
     *
     * @param parameters the request parameters
     * @return a request yielding the server's {@link FrameworkDescriptionsType}
     */
    public ServiceRequest<Supplier<FrameworkDescriptionsType>> describeFrameworks(DescribeFrameworks parameters) {
        final DescribeFrameworksType request = new DescribeFrameworksType();
        request.setLanguage(parameters.getLanguage());
        request.setFrameworkURI(parameters.getFrameworkURI());
        return post(request, in -> unmarshal(in, FrameworkDescriptionsType.class));
    }

    /**
     * <blockquote><q>
     * The DescribeDatasets operation returns an XML document that identifies all of the
     * matching attribute datasets for which data is available from the service instance.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;10.1</a></small></i>
     * </blockquote>
     *
     * @param parameters the request parameters
     * @return a request yielding the server's {@link DatasetDescriptionsType}
     */
    public ServiceRequest<Supplier<DatasetDescriptionsType>> describeDatasets(DescribeDatasets parameters) {
        final DescribeDatasetsType request = new DescribeDatasetsType();
        request.setLanguage(parameters.getLanguage());
        request.setFrameworkURI(parameters.getFrameworkURI());
        request.setDatasetURI(parameters.getDatasetURI());
        return post(request, in -> unmarshal(in, DatasetDescriptionsType.class));
    }

    /**
     * <blockquote><q>
     * The DescribeData operation returns an XML document that describes all of the
     * attributes for any dataset identified in the request.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;11.1</a></small></i>
     * </blockquote>
     *
     * @param parameters the request parameters
     * @return a request yielding the server's {@link DataDescriptionsType}
     */
    public ServiceRequest<Supplier<DataDescriptionsType>> describeData(DescribeData parameters) {
        final DescribeDataType request = new DescribeDataType();
        request.setLanguage(parameters.getLanguage());
        request.setFrameworkURI(parameters.getFrameworkURI());
        request.setDatasetURI(parameters.getDatasetURI());
        request.setAttributes(parameters.getAttributes());
        return post(request, in -> unmarshal(in, DataDescriptionsType.class));
    }

    /**
     * <blockquote><q>
     * The GetData operation provides a means for a client to retrieve attribute data. The
     * response includes descriptive information about each attribute included in the
     * response, as well as the attribute values for all selected records.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;12.1</a></small></i>
     * </blockquote>
     *
     * @param parameters the request parameters
     * @return a request yielding the server's {@link GDASType}
     */
    public ServiceRequest<Supplier<GDASType>> getData(GetData parameters) {
        final GetDataType request = new GetDataType();
        request.setLanguage(parameters.getLanguage());
        request.setFrameworkURI(parameters.getFrameworkURI());
        request.setDatasetURI(parameters.getDatasetURI());
        request.setAttributes(parameters.getAttributes());
        request.setLinkageKeys(parameters.getLinkageKeys());
        request.setFilterColumn(parameters.getFilterColumn());
        request.setFilterValue(parameters.getFilterValue());
        request.setXsl(parameters.getXsl());
        request.setAid(parameters.getAid());
        return post(request, in -> unmarshal(in, GDASType.class));
    }

    /**
     * <blockquote><q>
     * The DescribeJoinAbilities operation returns an XML document that identifies all of
     * the spatial frameworks to which attribute data can be joined by the service instance.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;13.1</a></small></i>
     * </blockquote>
     *
     * @param parameters the request parameters
     * @return a request yielding the server's {@link JoinAbilitiesType}
     */
    public ServiceRequest<Supplier<JoinAbilitiesType>> describeJoinAbilities(DescribeJoinAbilities parameters) {
        final DescribeJoinAbilitiesType request = new DescribeJoinAbilitiesType();
        request.setLanguage(parameters.getLanguage());
        return post(request, in -> unmarshal(in, JoinAbilitiesType.class));
    }

    /**
     * <blockquote><q>
     * The DescribeKey operation returns an XML document that identifies all of the keys
     * for a spatial framework to which data can be joined by the service instance.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;14.1</a></small></i>
     * </blockquote>
     *
     * @param parameters the request parameters
     * @return a request yielding the server's {@link FrameworkKeyDescriptionType}
     */
    public ServiceRequest<Supplier<FrameworkKeyDescriptionType>> describeKey(DescribeKey parameters) {
        final DescribeKeyType request = new DescribeKeyType();
        request.setLanguage(parameters.getLanguage());
        request.setFrameworkURI(parameters.getFrameworkURI());
        return post(request, in -> unmarshal(in, FrameworkKeyDescriptionType.class));
    }

    /**
     * <blockquote><q>
     * The JoinData operation instructs the server to merge an attribute data table encoded
     * in GDAS format (i.e. obtainable via a GetData request) with its spatial framework.
     * </q><i><small><a href="https://docs.ogc.org/is/10-070r2/10-070r2.pdf">OGC 10-070r2, &sect;15.1</a></small></i>
     * </blockquote>
     *
     * @param parameters the request parameters
     * @return a request yielding the server's {@link JoinDataResponseType}
     */
    public ServiceRequest<Supplier<JoinDataResponseType>> joinData(JoinData parameters) {
        final JoinDataRequestType request = new JoinDataRequestType();
        request.setLanguage(parameters.getLanguage());
        request.setUpdate(parameters.getUpdate());
        request.setClassificationURL(parameters.getClassificationURL());

        final AttributeDataType attributeData = new AttributeDataType();
        if (parameters.getGetDataURL() != null) {
            attributeData.setGetDataURL(parameters.getGetDataURL());
        } else {
            final GetDataXMLType getDataXML = new GetDataXMLType();
            getDataXML.setFrameworkURI(parameters.getGetDataFrameworkURI());
            getDataXML.setDatasetURI(parameters.getGetDataDatasetURI());
            getDataXML.setAttributes(parameters.getGetDataAttributes());
            getDataXML.setLinkageKeys(parameters.getGetDataLinkageKeys());
            getDataXML.setGetDataHost(parameters.getGetDataHost());
            getDataXML.setLanguage(parameters.getGetDataLanguage());
            attributeData.setGetDataXML(getDataXML);
        }
        request.setAttributeData(attributeData);

        if (parameters.getMapStylingIdentifier() != null || parameters.getMapStylingURL() != null) {
            final MapStylingType mapStyling = new MapStylingType();
            mapStyling.setStylingIdentifier(parameters.getMapStylingIdentifier());
            mapStyling.setStylingURL(parameters.getMapStylingURL());
            request.setMapStyling(mapStyling);
        }

        return post(request, in -> unmarshal(in, JoinDataResponseType.class));
    }

    /**
     * Marshal the given TJS request object as the body of a POST request, and turn the response
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
            return TJSBindingUtilities.unmarshall(in, dataType);
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
        final MarshallerPool pool = TJSMarshallerPool.getInstance();
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
}
