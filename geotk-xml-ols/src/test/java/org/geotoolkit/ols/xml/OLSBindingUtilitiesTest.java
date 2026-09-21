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
package org.geotoolkit.ols.xml;

import java.io.ByteArrayInputStream;
import jakarta.xml.bind.JAXBException;
import org.geotoolkit.ols.xml.v121.AddressType;
import org.geotoolkit.ols.xml.v121.ErrorCodeType;
import org.geotoolkit.ols.xml.v121.ErrorListType;
import org.geotoolkit.ols.xml.v121.ErrorType;
import org.geotoolkit.ols.xml.v121.GeocodeRequestType;
import org.geotoolkit.ols.xml.v121.GeocodeResponseListType;
import org.geotoolkit.ols.xml.v121.GeocodeResponseType;
import org.geotoolkit.ols.xml.v121.ObjectFactory;
import org.geotoolkit.ols.xml.v121.RequestHeaderType;
import org.geotoolkit.ols.xml.v121.ResponseType;
import org.geotoolkit.ols.xml.v121.XLSType;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Round-trip marshal/unmarshal tests for the OLS {@link XLSType} envelope.
 *
 * @author Quentin Bialota (Geomatys)
 */
public class OLSBindingUtilitiesTest {

    private static final ObjectFactory FACTORY = new ObjectFactory();

    @Test
    public void marshallUnmarshallGeocodeRequest() throws JAXBException {
        final AddressType address = new AddressType();
        address.setFreeFormAddress("2 rue Bougainville, Brest");
        final GeocodeRequestType geocodeRequest = new GeocodeRequestType();
        geocodeRequest.getAddress().add(address);

        final RequestHeaderType header = OLSXmlFactory.buildRequestHeader("geotk", null, null, "EPSG:4326", null);
        final XLSType xls = OLSXmlFactory.buildRequestEnvelope(header, "GeocodeRequest", "req-1", 10,
                FACTORY.createGeocodeRequest(geocodeRequest));

        final byte[] xml = OLSBindingUtilities.marshall(xls);
        assertTrue(new String(xml, java.nio.charset.StandardCharsets.UTF_8).contains("GeocodeRequest"));

        final XLSType unmarshalled = OLSBindingUtilities.unmarshall(new ByteArrayInputStream(xml));
        assertEquals(OLSXmlFactory.VERSION, unmarshalled.getVersion());
        assertNotNull(unmarshalled.getHeader());
        assertEquals("geotk", ((RequestHeaderType) unmarshalled.getHeader().getValue()).getClientName());
    }

    @Test
    public void extractResponseParametersSucceeds() throws JAXBException {
        final GeocodeResponseListType responseList = new GeocodeResponseListType();
        final GeocodeResponseType geocodeResponse = new GeocodeResponseType();
        geocodeResponse.getGeocodeResponseList().add(responseList);

        final ResponseType response = new ResponseType();
        response.setVersion(OLSXmlFactory.VERSION.toPlainString());
        response.setResponseParameters(FACTORY.createGeocodeResponse(geocodeResponse));

        final XLSType xls = new XLSType();
        xls.setVersion(OLSXmlFactory.VERSION);
        xls.getBody().add(FACTORY.createResponse(response));

        final GeocodeResponseType extracted = OLSXmlFactory.extractResponseParameters(xls, GeocodeResponseType.class);
        assertSame(geocodeResponse, extracted);
    }

    @Test
    public void extractResponseParametersThrowsOnError() {
        final ErrorType error = new ErrorType();
        error.setErrorCode(ErrorCodeType.UNKNOWN);
        error.setMessage("unresolvable address");
        final ErrorListType errorList = new ErrorListType();
        errorList.getError().add(error);

        final ResponseType response = new ResponseType();
        response.setErrorList(errorList);

        final XLSType xls = new XLSType();
        xls.setVersion(OLSXmlFactory.VERSION);
        xls.getBody().add(FACTORY.createResponse(response));

        try {
            OLSXmlFactory.extractResponseParameters(xls, GeocodeResponseType.class);
            fail("Expected a JAXBException carrying the OLS error");
        } catch (JAXBException ex) {
            assertTrue(ex.getMessage().contains("unresolvable address"));
        }
    }
}
