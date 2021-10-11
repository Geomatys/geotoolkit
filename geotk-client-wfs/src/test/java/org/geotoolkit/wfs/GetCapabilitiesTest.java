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
package org.geotoolkit.wfs;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.data.wfs.AbstractGetCapabilities;
import org.geotoolkit.test.URLComparator;
import org.geotoolkit.wfs.xml.WFSVersion;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for GetCapabilities requests, in version 1.1.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetCapabilitiesTest extends org.geotoolkit.test.TestBase {
    public GetCapabilitiesTest() {}

    /**
     * Ensures the {@link GetCapabilities110#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetCapabilities110() {
        final AbstractGetCapabilities caps110 = new AbstractGetCapabilities("http://test.com", WFSVersion.v110, null);
        final URL url;
        try {
            url = caps110.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }

        final String expectedURL = "http://test.com?VERSION=1.1.0&SERVICE=WFS&REQUEST=GetCapabilities";
        new URLComparator(expectedURL, url).compare();
    }
}
