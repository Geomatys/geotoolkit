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

import java.net.MalformedURLException;
import java.net.URL;
import java.awt.Dimension;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Testing class for GetMap requests, in version 1.1.1 and 1.3.0.
 *
 * @author Olivier Terral (Geomatys)
 */
public class AbstractNcGetLegendTest {
    public AbstractNcGetLegendTest() {}
    
    /**
     * Test the getters ans setters methods.
     */
    @Test
    public void testGettersAndSetters() {
        final NcGetLegendImpl legend = new NcGetLegendImpl("http://test.com");
        legend.setOpacity(60);
        legend.setLogScale(true);
        legend.setPalette("test");
        legend.setNumColorBands(125);
        legend.dimensions().put("COLORSCALERANGE","20,30");
        
        assertTrue(legend.getOpacity().equals(60));
        assertTrue(legend.isLogScale().equals(true));
        assertTrue(legend.getNumColorBands().equals(125));
        assertTrue(legend.dimensions().get("COLORSCALERANGE").equals("20,30"));
        assertTrue(legend.getPalette().equals("test"));
    }
    
    /**
     * Test the prepareParameters method.
     */
    @Test
    public void testPrepareParameters() {
        final NcGetLegendImpl legend = new NcGetLegendImpl("http://test.com");
        legend.setLayer("test");
        legend.setFormat("image/png");
        legend.setOpacity(60);
        legend.setLogScale(true);
        legend.setPalette("test");
        legend.setNumColorBands(125);
        legend.dimensions().put("COLORSCALERANGE","20,30");
        
        final URL url;
        try {
            url = legend.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        
        final String sUrl = url.toString();
        assertTrue(sUrl.contains("OPACITY=60"));
        assertTrue(sUrl.contains("LOGSCALE=true"));
        assertTrue(sUrl.contains("PALETTE=test"));
        assertTrue(sUrl.contains("NUMCOLORBANDS=125"));
        assertTrue(sUrl.contains("COLORSCALERANGE=20,30"));
    }
}

class NcGetLegendImpl extends AbstractNcGetLegend {
    public NcGetLegendImpl(final String serverURL){
        super(serverURL,"1.1.1");
    }
}
