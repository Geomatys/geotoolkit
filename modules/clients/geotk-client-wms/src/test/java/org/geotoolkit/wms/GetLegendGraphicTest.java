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
import org.geotoolkit.wms.v111.GetLegend111;
import org.geotoolkit.wms.v130.GetLegend130;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for GetMap requests, in version 1.1.1 and 1.3.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetLegendGraphicTest {
    public GetLegendGraphicTest() {}

    /**
     * Ensures the {@link GetLegend111#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetLegendGraphic111() {
        final GetLegend111 legend111 = new GetLegend111("http://test.com");
        legend111.setDimension(new Dimension(140, 20));
        legend111.setFormat("image/png");
        legend111.setLayer("test");
        final URL url;
        try {
            url = legend111.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=140"));
        assertTrue(sUrl.contains("HEIGHT=20"));
        assertTrue(sUrl.contains("LAYER=test"));
    }

    /**
     * Ensures the {@link GetLegend130#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testGetLegendGraphic130() {
        final GetLegend130 legend130 = new GetLegend130("http://test.com");
        legend130.setDimension(new Dimension(140, 20));
        legend130.setFormat("image/png");
        legend130.setLayer("test");

        final URL url;
        try {
            url = legend130.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=140"));
        assertTrue(sUrl.contains("HEIGHT=20"));
        assertTrue(sUrl.contains("LAYER=test"));
    }
}
