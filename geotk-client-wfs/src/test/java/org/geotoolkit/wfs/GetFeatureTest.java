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
import org.apache.sis.geometry.GeneralEnvelope;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.data.wfs.v110.GetFeature110;
import static org.geotoolkit.filter.FilterUtilities.FF;
import static org.junit.Assert.*;
import org.junit.Test;


/**
 * Testing class for GetFeature requests, in version 1.1.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetFeatureTest {
    public GetFeatureTest() {}

    /**
     * Ensures the {@link GetFeature110#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetFeature110() {
        final GetFeature110 getFeat110 = new GetFeature110("http://test.com",null);
        getFeat110.setTypeName(new QName("http://myqnametest.com", "value", "ut"));
        final GeneralEnvelope env = new GeneralEnvelope(CommonCRS.WGS84.normalizedGeographic());
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        getFeat110.setFilter(FF.bbox(FF.property("propGeom"), env));
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
//      assertTrue("was:" + sUrl, sUrl.contains("%3Cogc%3APropertyName%3EpropGeom%3C%2Fogc%3APropertyName%3E"));
        assertTrue("was:" + sUrl, sUrl.contains("Envelope+srsName%3D%22urn%3Aogc%3Adef%3Acrs%3AOGC%3A1.3%3ACRS84%22%3E"));
        assertTrue("was:" + sUrl, sUrl.contains("lowerCorner%3E-180.0+-90.0"));
        assertTrue("was:" + sUrl, sUrl.contains("upperCorner%3E180.0+90.0"));
    }
}
