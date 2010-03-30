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
import org.geotoolkit.wcs.v100.DescribeCoverage100;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for DescribeCoverage requests, in version 1.0.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class DescribeCoverageTest {
    public DescribeCoverageTest() {}

    /**
     * Ensures the {@link DescribeCoverage100#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testDescribeCoverage100() {
        final DescribeCoverage100 describeCoverage100 = new DescribeCoverage100("http://test.com");
        describeCoverage100.setCoverage("test");
        final URL url;
        try {
            url = describeCoverage100.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("COVERAGE=test"));
    }
}
