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
import java.util.Collections;
import javax.xml.namespace.QName;
import org.geotoolkit.data.wfs.v110.DescribeFeatureType110;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for DescribeFeatureType requests, in version 1.1.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class DescribeFeatureTypeTest {
    public DescribeFeatureTypeTest() {}

    /**
     * Ensures the {@link DescribeFeatureType110#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testDescribeFeatureType110() {
        final DescribeFeatureType110 describeFeat110 = new DescribeFeatureType110("http://test.com");
        describeFeat110.setTypeNames(Collections.singletonList(new QName("http://myqnametest.com", "value", "ut")));
        final URL url;
        try {
            url = describeFeat110.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("TYPENAME=ut%3Avalue"));
        assertTrue(sUrl.contains("NAMESPACE={xmlns(ut=http://myqnametest.com)}"));
    }
}
