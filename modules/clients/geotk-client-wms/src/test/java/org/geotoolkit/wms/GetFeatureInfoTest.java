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
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.wms.v111.GetFeatureInfo111;
import org.geotoolkit.wms.v130.GetFeatureInfo130;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.junit.Assert.*;


/**
 * Testing class for GetFeatureInfo requests, in version 1.1.1 and 1.3.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetFeatureInfoTest {
    public GetFeatureInfoTest() {}

    /**
     * Ensures the {@link GetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetFeatureInfo111() throws NoSuchAuthorityCodeException, FactoryException {
        final CoordinateReferenceSystem crs = CRS.decode("CRS:84");
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final GetFeatureInfo111 featureInfo111 = new GetFeatureInfo111("http://test.com");
        featureInfo111.setDimension(new Dimension(800, 600));
        featureInfo111.setFormat("image/png");
        featureInfo111.setLayers("test");
        featureInfo111.setStyles("");
        featureInfo111.setEnvelope(env);
        featureInfo111.setInfoFormat("gml");
        featureInfo111.setQueryLayers("test");
        featureInfo111.setX(50);
        featureInfo111.setY(40);
        final URL url;
        try {
            url = featureInfo111.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-180.0,-90.0,180.0,90.0"));
        assertTrue(sUrl.contains("SRS=CRS:84"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=800"));
        assertTrue(sUrl.contains("HEIGHT=600"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("X=50"));
        assertTrue(sUrl.contains("Y=40"));
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
        final CoordinateReferenceSystem crs = CRS.decode("CRS:84");
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final GetFeatureInfo130 featureInfo130 = new GetFeatureInfo130("http://test.com");
        featureInfo130.setDimension(new Dimension(800, 600));
        featureInfo130.setFormat("image/png");
        featureInfo130.setLayers("test");
        featureInfo130.setStyles("");
        featureInfo130.setEnvelope(env);
        featureInfo130.setInfoFormat("gml");
        featureInfo130.setQueryLayers("test");
        featureInfo130.setI(50);
        featureInfo130.setJ(40);
        final URL url;
        try {
            url = featureInfo130.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-180.0,-90.0,180.0,90.0"));
        assertTrue(sUrl.contains("CRS=CRS:84"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=800"));
        assertTrue(sUrl.contains("HEIGHT=600"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("QUERY_LAYERS=test"));
        assertTrue(sUrl.contains("I=50"));
        assertTrue(sUrl.contains("J=40"));
    }
}
