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
package org.geotoolkit.wms;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.sis.geometry.GeneralEnvelope;
import org.geotoolkit.wms.v111.GetMap111;
import org.geotoolkit.wms.v130.GetMap130;
import org.junit.Test;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.wms.v100.GetMap100;
import static org.junit.Assert.*;


/**
 * Testing class for GetMap requests, in version 1.1.1 and 1.3.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetMapTest extends org.geotoolkit.test.TestBase {
    public GetMapTest() {}

    /**
     * Ensures the {@link GetMap111#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetMap100() throws NoSuchAuthorityCodeException, FactoryException {
        final GetMap100 map100 = new GetMap100("http://test.com",null);
        fillGetMap(map100);
        checkBefore130(map100);
    }

    /**
     * Ensures the {@link GetMap111#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetMap111() throws NoSuchAuthorityCodeException, FactoryException {
        final GetMap111 map111 = new GetMap111("http://test.com",null);
        fillGetMap(map111);
        checkBefore130(map111);
    }

    /**
     * Prepare a Get Map request with parameters fitting {@link #checkBefore130(org.geotoolkit.wms.GetMapRequest) }
     * and {@link #checkSince130(org.geotoolkit.wms.GetMapRequest) } tests.
     * @param request
     */
    private void fillGetMap(final GetMapRequest request) {
        final CoordinateReferenceSystem crs = CommonCRS.defaultGeographic();
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        request.setDimension(new Dimension(800, 600));
        request.setFormat("image/png");
        request.setLayers("test");
        request.setStyles("");
        request.setEnvelope(env);
    }

    /**
     * A test designed to check get map URL compliance with WMS standard 1.3.0
     * and above.
     *
     * @param request The query containing the URL to test.
     */
    private void checkSince130(GetMapRequest request) {
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue(sUrl.contains("CRS=CRS%3A84"));
        assertTrue(sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue(sUrl.contains("WIDTH=800"));
        assertTrue(sUrl.contains("HEIGHT=600"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
    }

    /**
     * A test designed to check get map URL compliance with WMS standard 1.0.0
     * and 1.1.1.
     *
     * @param request The query containing the URL to test.
     */
    private void checkBefore130(GetMapRequest request) {
        fillGetMap(request);
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue(sUrl.contains("SRS=CRS%3A84"));
        assertTrue(sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue(sUrl.contains("WIDTH=800"));
        assertTrue(sUrl.contains("HEIGHT=600"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
    }

    /**
     * Ensures the {@link GetMap130#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetMap130() throws NoSuchAuthorityCodeException, FactoryException {
        final GetMap130 map130 = new GetMap130("http://test.com",null);
        fillGetMap(map130);
        checkSince130(map130);
    }
}
