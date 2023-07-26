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
import org.geotoolkit.csw.v202.GetDomain202;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for GetDomain requests, in version 2.0.2.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetDomainTest {
    public GetDomainTest() {}

    /**
     * Ensures the {@link GetDomain202#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetDomain202() {
        final GetDomain202 getDomain202 = new GetDomain202("http://test.com",null);
        getDomain202.setPropertyName("prop");
        final URL url;
        try {
            url = getDomain202.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("PROPERTYNAME=prop"));
    }
}
