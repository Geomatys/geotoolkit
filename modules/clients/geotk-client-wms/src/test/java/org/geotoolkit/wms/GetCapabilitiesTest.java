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
import org.geotoolkit.wms.v111.GetCapabilities111;
import org.geotoolkit.wms.v130.GetCapabilities130;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for GetCapabilities requests, in version 1.1.1 and 1.3.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetCapabilitiesTest {
    public GetCapabilitiesTest() {}

    /**
     * Ensures the {@link GetCapabilities111#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetCapabilities111() {
        final GetCapabilities111 caps111 = new GetCapabilities111("http://test.com",null);
        final URL url;
        try {
            url = caps111.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String expectedURL = "http://test.com?VERSION=1.1.1&SERVICE=WMS&REQUEST=GetCapabilities";
        assertEquals(expectedURL, url.toString());
    }

    /**
     * Ensures the {@link GetCapabilities130#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetCapabilities130() {
        final GetCapabilities130 caps130 = new GetCapabilities130("http://test.com",null);
        final URL url;
        try {
            url = caps130.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String expectedURL = "http://test.com?VERSION=1.3.0&SERVICE=WMS&REQUEST=GetCapabilities";
        assertEquals(expectedURL, url.toString());
    }
}
