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
package org.geotoolkit.wps;

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.bind.Marshaller;
import org.geotoolkit.wps.xml.WPSMarshallerPool;
import org.geotoolkit.wps.xml.v100.GetCapabilities;
import org.junit.Test;

import static org.apache.sis.test.Assert.*;
import org.geotoolkit.test.URLComparator;


/**
 * Testing class for GetCapabilities requests of WPS client, in version 1.0.0.
 *
 * @author Quentin Boileau
 */
public class GetCapabilitiesTest extends org.geotoolkit.test.TestBase {
    public GetCapabilitiesTest() {}

    /**
     * Ensures the {@link GetCapabilities110#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetCapabilities110() throws MalformedURLException {
        final WebProcessingClient client = new WebProcessingClient(new URL("http://test.com"), null, WPSVersion.v100);

        final URL url;
        try {
            url = client.createGetCapabilities().getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }

        final String expectedURL = "http://test.com?SERVICE=WPS&ACCEPTVERSIONS=1.0.0&REQUEST=GetCapabilities";
        new URLComparator(expectedURL, url).compare();
    }

   @Test
   public void testRequestAndMarshall(){
       Marshaller marshaller = null;
        try {
            final WebProcessingClient client = new WebProcessingClient(new URL("http://test.com"), null, WPSVersion.v100);

            final GetCapabilitiesRequest caps100 = client.createGetCapabilities();
            final GetCapabilities request = (GetCapabilities) caps100.getContent();
            assertEquals("WPS", request.getService());
            assertEquals("1.0.0", request.getAcceptVersions().getVersion().get(0));

            final StringWriter stringWriter = new StringWriter();
            marshaller = WPSMarshallerPool.getInstance().acquireMarshaller();
            marshaller.marshal(request,stringWriter);

            final String expectedMarshalledRequest =
                "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n"
                    + "<wps:GetCapabilities service=\"WPS\""
                    + " xmlns:wps=\"http://www.opengis.net/wps/1.0.0\""
                    + " xmlns:ows=\"http://www.opengis.net/ows/1.1\">\n"
                    + "    <wps:AcceptVersions>\n"
                    + "        <ows:Version>1.0.0</ows:Version>\n"
                    + "    </wps:AcceptVersions>\n"
                    + "</wps:GetCapabilities>\n";

            String result = stringWriter.toString();
            assertXmlEquals(expectedMarshalledRequest, result, "xmlns:*");
            WPSMarshallerPool.getInstance().recycle(marshaller);

        } catch (Exception ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
   }
}
