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
package org.geotoolkit.ogcwxs.client.location;

import java.io.ByteArrayOutputStream;
import java.net.http.HttpRequest;
import java.nio.ByteBuffer;
import java.util.concurrent.Flow;
import jakarta.xml.bind.JAXBException;
import org.geotoolkit.client.service.ServiceConfiguration;
import org.geotoolkit.ogcwxs.request.location.DirectoryRequest;
import org.geotoolkit.ogcwxs.request.location.GeocodeRequest;
import org.geotoolkit.ols.xml.OLSBindingUtilities;
import org.geotoolkit.ols.xml.v121.AddressType;
import org.geotoolkit.ols.xml.v121.DirectoryRequestType;
import org.geotoolkit.ols.xml.v121.ObjectFactory;
import org.geotoolkit.ols.xml.v121.POIProperties;
import org.geotoolkit.ols.xml.v121.RequestType;
import org.geotoolkit.ols.xml.v121.XLSType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Verifies that {@link LocationApi} builds correct {@code <XLS>} request envelopes.
 *
 * @author Quentin Bialota (Geomatys)
 */
public class LocationApiTest {

    private static final ObjectFactory FACTORY = new ObjectFactory();

    private final LocationApi api = new LocationApi(ServiceConfiguration.builder()
            .updateBaseUri("http://localhost/ols")
            .build());

    @Test
    public void geocodeBuildsRequestEnvelope() throws JAXBException {
        final AddressType address = new AddressType();
        address.setFreeFormAddress("2 rue Bougainville, Brest");

        final GeocodeRequest parameters = new GeocodeRequest()
                .address(address)
                .clientName("geotk")
                .requestID("req-1");

        final HttpRequest request = api.geocode(parameters).builder().build();
        assertEquals("POST", request.method());
        assertEquals("text/xml", request.headers().firstValue("Content-Type").orElse(null));

        final XLSType xls = OLSBindingUtilities.unmarshall(new java.io.ByteArrayInputStream(readBody(request)));
        assertEquals("geotk", ((org.geotoolkit.ols.xml.v121.RequestHeaderType) xls.getHeader().getValue()).getClientName());

        final RequestType body = (RequestType) xls.getBody().get(0).getValue();
        assertEquals("GeocodeRequest", body.getMethodName());
        assertEquals("req-1", body.getRequestID());
    }

    @Test
    public void directoryRequestBuildsRequestEnvelope() throws JAXBException {
        final POIProperties properties = new POIProperties();
        final DirectoryRequest parameters = new DirectoryRequest()
                .poiSelectionCriteria(FACTORY.createPOIProperties(properties));

        final HttpRequest request = api.directoryRequest(parameters).builder().build();
        final XLSType xls = OLSBindingUtilities.unmarshall(new java.io.ByteArrayInputStream(readBody(request)));
        final RequestType body = (RequestType) xls.getBody().get(0).getValue();
        assertEquals("DirectoryRequest", body.getMethodName());
        assertTrue(body.getRequestParameters().getValue() instanceof DirectoryRequestType);
    }

    /**
     * Drain an {@link HttpRequest.BodyPublisher} into a byte array. The JDK's
     * {@code ofByteArray} publisher completes synchronously on subscription.
     */
    private static byte[] readBody(HttpRequest request) {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        request.bodyPublisher().orElseThrow().subscribe(new Flow.Subscriber<ByteBuffer>() {
            @Override
            public void onSubscribe(Flow.Subscription subscription) {
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(ByteBuffer item) {
                final byte[] bytes = new byte[item.remaining()];
                item.get(bytes);
                out.writeBytes(bytes);
            }

            @Override
            public void onError(Throwable throwable) {
                throw new AssertionError(throwable);
            }

            @Override
            public void onComplete() {}
        });
        return out.toByteArray();
    }
}
