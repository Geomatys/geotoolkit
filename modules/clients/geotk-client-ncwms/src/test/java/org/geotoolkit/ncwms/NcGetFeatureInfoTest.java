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
package org.geotoolkit.ncwms;

import org.geotoolkit.ncwms.v111.NcGetFeatureInfo111;
import org.geotoolkit.ncwms.v130.NcGetFeatureInfo130;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.wms.v111.GetFeatureInfo111;
import org.geotoolkit.wms.v130.GetFeatureInfo130;
import org.junit.Test;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.junit.Assert.*;


/**
 * Testing class for nc WMSGetFeatureInfo requests, in version 1.1.1 and 1.3.0.
 *
 * @author Olivier Terral (Geomatys)
 */
public class NcGetFeatureInfoTest {
    public NcGetFeatureInfoTest() {}

    /**
     * Ensures the {@link NcGetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testNcGetFeatureInfo111() throws NoSuchAuthorityCodeException, FactoryException {
        
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        
        final NcGetFeatureInfo111 request = new NcGetFeatureInfo111("http://test.com",null);
        request.setDimension(new Dimension(800, 600));
        request.setFormat("image/png");
        request.setLayers("test");
        request.setStyles("");
        request.setEnvelope(env);
        request.setInfoFormat("gml");
        request.setQueryLayers("test");
        request.setColumnIndex(50);
        request.setRawIndex(40);
        
        request.setOpacity(65);
        request.dimensions().put("COLORSCALERANGE","-50,50");
        request.setNumColorBands(150);
        request.setLogScale(true);
        
        final URL url;
        try {
            url = request.getURL();
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
        assertTrue(sUrl.contains("OPACITY=65"));
        assertTrue(sUrl.contains("COLORSCALERANGE=-50,50"));
        assertTrue(sUrl.contains("NUMCOLORBANDS=150"));
        assertTrue(sUrl.contains("LOGSCALE=true"));
    }

    /**
     * Ensures the {@link NcGetFeatureInfo130#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testNcGetFeatureInfo130() throws NoSuchAuthorityCodeException, FactoryException {
        
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        
        final NcGetFeatureInfo130 request = new NcGetFeatureInfo130("http://test.com",null);
        request.setDimension(new Dimension(800, 600));
        request.setFormat("image/png");
        request.setLayers("test");
        request.setStyles("");
        request.setEnvelope(env);
        request.setInfoFormat("gml");
        request.setQueryLayers("test");
        request.setColumnIndex(50);
        request.setRawIndex(40);        
        
        request.setOpacity(65);
        request.dimensions().put("COLORSCALERANGE","-50,50");
        request.setNumColorBands(150);
        request.setLogScale(true);
        
        final URL url;
        try {
            url = request.getURL();
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
        
        assertTrue(sUrl.contains("OPACITY=65"));
        assertTrue(sUrl.contains("COLORSCALERANGE=-50,50"));
        assertTrue(sUrl.contains("NUMCOLORBANDS=150"));
        assertTrue(sUrl.contains("LOGSCALE=true"));
    }
}
