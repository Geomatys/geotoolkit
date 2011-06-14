/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2011, Geomatys
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

import org.geotoolkit.ncwms.v130.NcGetMap130;
import org.geotoolkit.ncwms.v111.NcGetMap111;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.junit.Test;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import static org.junit.Assert.*;


/**
 * Testing class for ncWMS GetMap requests, in version 1.1.1 and 1.3.0.
 *
 * @author Olivier Terral (Geomatys)
 */
public class NcGetMapTest {
    public NcGetMapTest() {}

    /**
     * Ensures the {@link NcGetMap111#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testNcGetMap111() throws NoSuchAuthorityCodeException, FactoryException {
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final NcGetMap111 request = new NcGetMap111("http://test.com",null);
        request.setDimension(new Dimension(800, 600));
        request.setFormat("image/png");
        request.setLayers("test");
        request.setStyles("");
        request.setEnvelope(env);
        
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
        assertTrue(sUrl.contains("VERSION=1.1.1"));
        assertTrue(sUrl.contains("OPACITY=65"));
        assertTrue(sUrl.contains("COLORSCALERANGE=-50,50"));
        assertTrue(sUrl.contains("NUMCOLORBANDS=150"));
        assertTrue(sUrl.contains("LOGSCALE=true"));
    }

    /**
     * Ensures the {@link NcGetMap130#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetMap130() throws NoSuchAuthorityCodeException, FactoryException {
        
        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        
        final NcGetMap130 request = new NcGetMap130("http://test.com",null);
        request.setDimension(new Dimension(800, 600));
        request.setFormat("image/png");
        request.setLayers("test");
        request.setStyles("");
        request.setEnvelope(env);
        
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
        assertTrue(sUrl.contains("VERSION=1.3.0"));
        assertTrue(sUrl.contains("OPACITY=65"));
        assertTrue(sUrl.contains("COLORSCALERANGE=-50,50"));
        assertTrue(sUrl.contains("NUMCOLORBANDS=150"));
        assertTrue(sUrl.contains("LOGSCALE=true"));
    }
}
