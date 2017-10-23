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
import org.geotoolkit.wms.v111.GetFeatureInfo111;
import org.geotoolkit.wms.v130.GetFeatureInfo130;
import org.junit.Test;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.apache.sis.referencing.CommonCRS;
import org.geotoolkit.wms.v100.GetFeatureInfo100;
import static org.junit.Assert.*;


/**
 * Testing class for GetFeatureInfo requests, in version 1.1.1 and 1.3.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetFeatureInfoTest extends org.geotoolkit.test.TestBase {
    public GetFeatureInfoTest() {}
    /**
     * Test GetFeatureInfo query syntax for 1.0.0.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetFeatureInfo100() throws NoSuchAuthorityCodeException, FactoryException {
        final GetFeatureInfo100 featureInfo100 = new GetFeatureInfo100("http://test.com",null);
        fillParameters(featureInfo100);
        checkBefore130(featureInfo100);
    }

    /**
     * Ensures the {@link GetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetFeatureInfo111() throws NoSuchAuthorityCodeException, FactoryException {
        final GetFeatureInfo111 featureInfo111 = new GetFeatureInfo111("http://test.com",null);
        fillParameters(featureInfo111);
        checkBefore130(featureInfo111);
    }

    /**
     * Prepare a Get Map request with parameters fitting {@link #checkBefore130(org.geotoolkit.wms.GetMapRequest) }
     * and {@link #checkSince130(org.geotoolkit.wms.GetMapRequest) } tests.
     * @param request
     */
    private void fillParameters(final GetFeatureInfoRequest request) {
        final CoordinateReferenceSystem crs = CommonCRS.defaultGeographic();
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        request.setDimension(new Dimension(800, 600));
        request.setFormat("image/png");
        request.setLayers("test");
        request.setStyles("");
        request.setEnvelope(env);
        request.setInfoFormat("gml");
        request.setQueryLayers("test");
        request.setColumnIndex(50);
        request.setRawIndex(40);
    }

    /**
     * A test designed to check request URL compliance with WMS standard 1.3.0
     * and above.
     *
     * @param request The query containing the URL to test.
     */
    private void checkSince130(GetFeatureInfoRequest request) {
        final URL url;
        try {
            url = request.getURL();
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
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("I=50"));
        assertTrue("was:" + sUrl, sUrl.contains("J=40"));
    }

    /**
     * A test designed to check request URL compliance with WMS standard 1.0.0
     * and 1.1.1.
     *
     * @param request The query containing the URL to test.
     */
    private void checkBefore130(GetFeatureInfoRequest request) {
        fillParameters(request);
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue("was:" + sUrl, sUrl.contains("SRS=CRS%3A84"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=800"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=600"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLES="));
        assertTrue("was:" + sUrl, sUrl.contains("INFO_FORMAT=gml"));
        assertTrue("was:" + sUrl, sUrl.contains("QUERY_LAYERS=test"));
        assertTrue("was:" + sUrl, sUrl.contains("X=50"));
        assertTrue("was:" + sUrl, sUrl.contains("Y=40"));
    }

    /**
     * Ensures the {@link GetFeatureInfo130#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetFeatureInfo130() throws NoSuchAuthorityCodeException, FactoryException {
        final GetFeatureInfo130 featureInfo130 = new GetFeatureInfo130("http://test.com",null);
        fillParameters(featureInfo130);
    }
}
