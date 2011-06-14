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

import org.geotoolkit.ncwms.v130.NcGetLegend130;
import org.geotoolkit.ncwms.v111.NcGetLegend111;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for ncWMS GetMap requests, in version 1.1.1 and 1.3.0.
 *
 * @author Olivier Terral (Geomatys)
 */
public class NcGetLegendGraphicTest {
    public NcGetLegendGraphicTest() {}

    /**
     * Ensures the {@link NcGetLegend111#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testNcGetLegendGraphic111() {
        final NcGetLegend111 request = new NcGetLegend111("http://test.com",null);
        request.setDimension(new Dimension(140, 20));
        request.setFormat("image/png");
        request.setLayer("test");
        
        request.setOpacity(65);
        request.dimensions().put("COLORSCALERANGE","-50,50");
        request.setNumColorBands(150);
        request.setLogScale(true);
        request.setPalette("test");
        
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
        assertTrue(sUrl.contains("PALETTE=test"));
    }

    /**
     * Ensures the {@link NcGetLegend130#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testNcGetLegendGraphic130() {
        final NcGetLegend130 request = new NcGetLegend130("http://test.com",null);
        request.setDimension(new Dimension(140, 20));
        request.setFormat("image/png");
        request.setLayer("test");
        
        request.setOpacity(65);
        request.dimensions().put("COLORSCALERANGE","-50,50");
        request.setNumColorBands(150);
        request.setLogScale(true);
        request.setPalette("test");
        
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
        assertTrue(sUrl.contains("PALETTE=test"));
    }    
    
}
