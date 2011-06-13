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
package org.geotoolkit.csw;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.csw.v202.GetCapabilities202;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for GetCapabilities requests, in version 2.0.2.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetCapabilitiesTest {
    public GetCapabilitiesTest() {}

    /**
     * Ensures the {@link GetCapabilities202#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetCapabilities202() {
        final GetCapabilities202 caps202 = new GetCapabilities202("http://test.com",null);
        final URL url;
        try {
            url = caps202.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String expectedURL = "http://test.com?VERSION=2.0.2&SERVICE=CSW&REQUEST=GetCapabilities";
        assertEquals(expectedURL, url.toString());
    }
}
