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
import org.geotoolkit.csw.v202.Harvest202;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for Harvest requests, in version 2.0.2.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class HarvestTest {
    public HarvestTest() {}

    /**
     * Ensures the {@link Harvest202#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testHarvest202() {
        final Harvest202 harvest202 = new Harvest202("http://test.com",null);
        harvest202.setNamespace("xmlns(ut=http://myqname.com)");
        harvest202.setResourceFormat("xml");
        harvest202.setResourceType("file");
        harvest202.setSource("http://mysourcetoharvest.com");
        final URL url;
        try {
            url = harvest202.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("NAMESPACE=xmlns%28ut%3Dhttp%3A%2F%2Fmyqname.com%29"));
        assertTrue("was:" + sUrl, sUrl.contains("SOURCE=http%3A%2F%2Fmysourcetoharvest.com"));
        assertTrue("was:" + sUrl, sUrl.contains("RESOURCETYPE=file"));
        assertTrue("was:" + sUrl, sUrl.contains("RESOURCEFORMAT=xml"));
    }
}
