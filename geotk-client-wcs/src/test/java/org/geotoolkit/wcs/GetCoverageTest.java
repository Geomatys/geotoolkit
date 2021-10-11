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

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.wcs.v100.GetCoverage100;
import org.junit.Test;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.referencing.CommonCRS;
import static org.junit.Assert.*;


/**
 * Testing class for GetCoverage requests, in version 1.0.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetCoverageTest extends org.geotoolkit.test.TestBase {
    public GetCoverageTest() {}

    /**
     * Ensures the {@link GetCoverage100#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetCoverage100() throws NoSuchAuthorityCodeException, FactoryException {
        final CoordinateReferenceSystem crs = CommonCRS.defaultGeographic();
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final GetCoverage100 coverage100 = new GetCoverage100("http://test.com",null);
        coverage100.setDimension(new Dimension(800, 600));
        coverage100.setFormat("image/png");
        coverage100.setCoverage("test");
        coverage100.setEnvelope(env);
        final URL url;
        try {
            url = coverage100.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue("was:" + sUrl, sUrl.contains("CRS=CRS%3A84"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=800"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=600"));
        assertTrue("was:" + sUrl, sUrl.contains("COVERAGE=test"));
    }
}
