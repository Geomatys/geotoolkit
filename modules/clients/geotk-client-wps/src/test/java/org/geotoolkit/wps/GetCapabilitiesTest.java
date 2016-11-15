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
import org.geotoolkit.wps.v100.GetCapabilities100;
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
    public void testGetCapabilities110() {
        final GetCapabilities100 caps100 = new GetCapabilities100("http://test.com",null);
        final URL url;
        try {
            url = caps100.getURL();
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
            final GetCapabilities100 caps100 = new GetCapabilities100("http://test.com",null);
            final GetCapabilities request = caps100.makeRequest();
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
