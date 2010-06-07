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
import javax.xml.namespace.QName;
import org.geotoolkit.csw.v202.DescribeRecord202;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for DescribeRecord requests, in version 2.0.2.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class DescribeRecordTest {
    public DescribeRecordTest() {}

    /**
     * Ensures the {@link DescribeRecord202#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testDescribeRecord202() {
        final DescribeRecord202 describeRecord202 = new DescribeRecord202("http://test.com");
        describeRecord202.setTypeNames(new QName("ut:value"));
        describeRecord202.setNamespace("xmlns(ut=http://myqnametest.com)");
        final URL url;
        try {
            url = describeRecord202.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("TYPENAME=ut:value"));
        assertTrue(sUrl.contains("NAMESPACE=xmlns(ut=http://myqnametest.com)"));
    }
}
