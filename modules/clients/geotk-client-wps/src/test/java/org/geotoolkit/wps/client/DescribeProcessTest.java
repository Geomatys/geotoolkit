/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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
package org.geotoolkit.wps.client;

import org.geotoolkit.wps.client.WPSVersion;
import org.geotoolkit.wps.client.WebProcessingClient;
import org.geotoolkit.wps.client.DescribeProcessRequest;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import javax.xml.bind.Marshaller;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.junit.Test;

import static org.apache.sis.test.Assert.*;
import org.geotoolkit.wps.xml.v200.DescribeProcess;
import static org.apache.sis.test.MetadataAssert.*;


/**
 * Testing class for GetCapabilities requests of WPS client, in version 1.0.0.
 *
 * @author Quentin Boileau
 */
public class DescribeProcessTest extends org.geotoolkit.test.TestBase {
    public DescribeProcessTest() {}

    /**
     * Ensures the {@link DescribeProcess#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testDescribeProcess110() throws MalformedURLException {
        final WebProcessingClient client = new WebProcessingClient(new URL("http://test.com"), null, WPSVersion.v100);

        final DescribeProcessRequest request = client.createDescribeProcess();
        final DescribeProcess content = request.getContent();
        content.setIdentifier(Arrays.asList("identifier1","identifier2","identifier3"));

        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }

        final String strUrl = url.toString();
        //final String expectedURL = "http://test.com?VERSION=1.0.0&SERVICE=WPS&REQUEST=DescribeProcess&IDENTIFIER=identifier1,identifier2,identifier3";
        assertTrue(strUrl.contains("VERSION=1.0.0"));
        assertTrue(strUrl.contains("SERVICE=WPS"));
        assertTrue(strUrl.contains("REQUEST=DescribeProcess"));
        assertTrue(strUrl.contains("IDENTIFIER=identifier1%2Cidentifier2%2Cidentifier3"));
    }

   @Test
   public void testRequestAndMarshall() throws Exception{
        final WebProcessingClient client = new WebProcessingClient(new URL("http://test.com"), null, WPSVersion.v100);

        final DescribeProcessRequest request = client.createDescribeProcess();
        final DescribeProcess content = request.getContent();
        content.setIdentifier(Arrays.asList("identifier1", "identifier2", "identifier3"));

        final StringWriter stringWriter = new StringWriter();
        final Marshaller marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
        marshaller.marshal(content, stringWriter);

        String result = stringWriter.toString();
        final String expectedMarshalledRequest
                = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                + "<wps:DescribeProcess version=\"1.0.0\" service=\"WPS\""
                + " xmlns:wps=\"http://www.opengis.net/wps/1.0.0\""
                + " xmlns:ows=\"http://www.opengis.net/ows/1.1\">\n"
                + "    <ows:Identifier>identifier1</ows:Identifier>\n"
                + "    <ows:Identifier>identifier2</ows:Identifier>\n"
                + "    <ows:Identifier>identifier3</ows:Identifier>\n"
                + "</wps:DescribeProcess>\n";
        assertXmlEquals(expectedMarshalledRequest, result, "xmlns:*");
        WPSMarshallerPool.getInstance().recycle(marshaller);
    }
}
