/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2010, Geomatys
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
package org.geotoolkit.wms;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.test.URLComparator;
import org.geotoolkit.wms.v100.GetCapabilities100;
import org.geotoolkit.wms.v111.GetCapabilities111;
import org.geotoolkit.wms.v130.GetCapabilities130;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for GetCapabilities requests, in version 1.1.1 and 1.3.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetCapabilitiesTest extends org.geotoolkit.test.TestBase {
    public GetCapabilitiesTest() {}
    @Test
    public void testGetCapabilities100() {
        final GetCapabilities100 caps100 = new GetCapabilities100("http://test.com",null);
        checkCapabilitiesURL("http://test.com?VERSION=1.0.0&SERVICE=WMS&REQUEST=GetCapabilities", caps100);
    }

    /**
     * Ensures the {@link GetCapabilities111#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetCapabilities111() {
        final GetCapabilities111 caps111 = new GetCapabilities111("http://test.com",null);
        checkCapabilitiesURL("http://test.com?VERSION=1.1.1&SERVICE=WMS&REQUEST=GetCapabilities", caps111);
    }

    /**
     * Ensures the {@link GetCapabilities130#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetCapabilities130() {
        final GetCapabilities130 caps130 = new GetCapabilities130("http://test.com",null);
        checkCapabilitiesURL("http://test.com?VERSION=1.3.0&SERVICE=WMS&REQUEST=GetCapabilities", caps130);
    }

    private static void checkCapabilitiesURL(final String expectedURL, GetCapabilitiesRequest getCapa) {
        final URL url;
        try {
            url = getCapa.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        new URLComparator(expectedURL, url).compare();
    }
}
