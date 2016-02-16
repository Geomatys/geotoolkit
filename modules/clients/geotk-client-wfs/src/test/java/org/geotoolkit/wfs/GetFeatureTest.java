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
import javax.xml.namespace.QName;
import org.geotoolkit.data.wfs.v110.GetFeature110;
import org.geotoolkit.factory.FactoryFinder;
import org.junit.Test;
import org.opengis.filter.FilterFactory;
import static org.junit.Assert.*;


/**
 * Testing class for GetFeature requests, in version 1.1.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetFeatureTest extends org.geotoolkit.test.TestBase {
    public GetFeatureTest() {}

    /**
     * Ensures the {@link GetFeature110#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetFeature110() {
        final GetFeature110 getFeat110 = new GetFeature110("http://test.com",null);
        getFeat110.setTypeName(new QName("http://myqnametest.com", "value", "ut"));
        final FilterFactory ff = FactoryFinder.getFilterFactory(null);
        getFeat110.setFilter(ff.bbox("propGeom", -180.0, -90.0, 180.0, 90.0, "CRS:84"));
        final URL url;
        try {
            url = getFeat110.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("TYPENAME=ut%3Avalue"));
        assertTrue("was:" + sUrl, sUrl.contains("NAMESPACE=xmlns%28ut%3Dhttp%3A%2F%2Fmyqnametest.com%29"));
        assertTrue("was:" + sUrl, sUrl.contains("%3Cogc%3APropertyName%3EpropGeom%3C%2Fogc%3APropertyName%3E"));
        assertTrue("was:" + sUrl, sUrl.contains("%3Cgml%3AEnvelope+srsName%3D%22CRS%3A84%22%3E"));
        assertTrue("was:" + sUrl, sUrl.contains("%3Cgml%3AlowerCorner%3E-180.0+-90.0%3C%2Fgml%3AlowerCorner%3E"));
        assertTrue("was:" + sUrl, sUrl.contains("%3Cgml%3AupperCorner%3E180.0+90.0%3C%2Fgml%3AupperCorner%3E"));
    }
}
