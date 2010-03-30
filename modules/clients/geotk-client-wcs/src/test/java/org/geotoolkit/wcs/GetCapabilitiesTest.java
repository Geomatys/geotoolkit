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
package org.geotoolkit.wcs;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.wcs.v100.GetCapabilities100;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for GetCapabilities requests, in version 1.0.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetCapabilitiesTest {
    public GetCapabilitiesTest() {}

    /**
     * Ensures the {@link GetCapabilities100#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetCapabilities100() {
        final GetCapabilities100 caps100 = new GetCapabilities100("http://test.com");
        final URL url;
        try {
            url = caps100.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String expectedURL = "http://test.com?VERSION=1.0.0&SERVICE=WCS&REQUEST=GetCapabilities";
        assertEquals(expectedURL, url.toString());
    }
}
