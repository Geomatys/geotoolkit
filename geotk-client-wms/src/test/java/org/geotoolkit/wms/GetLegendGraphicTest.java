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
        final GetLegend111 legend111 = new GetLegend111("http://test.com",null);
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
        assertTrue(sUrl.contains("FORMAT=image%2Fpng"));
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
        final GetLegend130 legend130 = new GetLegend130("http://test.com",null);
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
        assertTrue(sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue(sUrl.contains("WIDTH=140"));
        assertTrue(sUrl.contains("HEIGHT=20"));
        assertTrue(sUrl.contains("LAYER=test"));
    }

    /**
     * Ensures the {@link GetLegend111#getURL()} method returns a well-built url,
     * with the parameters given.
     * Test with all the available parameters specified.
     */
    @Test
    public void testGetLegendGraphic111Complete() {
        final GetLegend111 legend = new GetLegend111("http://test.com",null);
        legend.setDimension(new Dimension(140, 20));
        legend.setFormat("image/png");
        legend.setExceptions("application/test");
        legend.setLayer("test");
        legend.setStyle("test");
        legend.setRule("test");
        legend.setScale(2500.0);
        legend.setSldVersion("3.3.3");
        legend.dimensions().put("TIME", "20-20-20T20:20:20Z");
        legend.dimensions().put("ELEVATION", "500");
        legend.dimensions().put("DIMRANGE", "-50,50");

        final URL url;
        try {
            url = legend.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue("was:" + sUrl, sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("SERVICE=WMS"));
        assertTrue("was:" + sUrl, sUrl.contains("VERSION=1.1.1"));
        assertTrue("was:" + sUrl, sUrl.contains("REQUEST=GetLegendGraphic"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("EXCEPTIONS=application%2Ftest"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYER=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLE=test"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=140"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=20"));
        assertTrue("was:" + sUrl, sUrl.contains("RULE=test"));
        assertTrue("was:" + sUrl, sUrl.contains("SCALE=2500"));
        assertTrue("was:" + sUrl, sUrl.contains("SLD_VERSION=3.3.3"));
        assertTrue("was:" + sUrl, sUrl.contains("TIME=20-20-20T20%3A20%3A20Z"));
        assertTrue("was:" + sUrl, sUrl.contains("ELEVATION=500"));
        assertTrue("was:" + sUrl, sUrl.contains("DIMRANGE=-50%2C50"));
    }

    /**
     * Ensures the {@link GetLegend130#getURL()} method returns a well-built url,
     * with the parameters given.
     * Test with all the available parameters specified.
     */
    @Test
    public void testGetLegendGraphic130Complete() {
        final GetLegend130 legend = new GetLegend130("http://test.com",null);
        legend.setDimension(new Dimension(140, 20));
        legend.setFormat("image/png");
        legend.setExceptions("application/test");
        legend.setLayer("test");
        legend.setStyle("test");
        legend.setRule("test");
        legend.setScale(2500.0);
        legend.setSldVersion("3.3.3");
        legend.dimensions().put("TIME", "20-20-20T20:20:20Z");
        legend.dimensions().put("ELEVATION", "500");
        legend.dimensions().put("DIMRANGE", "-50,50");

        final URL url;
        try {
            url = legend.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue("was:" + sUrl, sUrl.startsWith("http://test.com?"));
        assertTrue("was:" + sUrl, sUrl.contains("SERVICE=WMS"));
        assertTrue("was:" + sUrl, sUrl.contains("VERSION=1.3.0"));
        assertTrue("was:" + sUrl, sUrl.contains("REQUEST=GetLegendGraphic"));
        assertTrue("was:" + sUrl, sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue("was:" + sUrl, sUrl.contains("EXCEPTIONS=application%2Ftest"));
        assertTrue("was:" + sUrl, sUrl.contains("LAYER=test"));
        assertTrue("was:" + sUrl, sUrl.contains("STYLE=test"));
        assertTrue("was:" + sUrl, sUrl.contains("WIDTH=140"));
        assertTrue("was:" + sUrl, sUrl.contains("HEIGHT=20"));
        assertTrue("was:" + sUrl, sUrl.contains("RULE=test"));
        assertTrue("was:" + sUrl, sUrl.contains("SCALE=2500"));
        assertTrue("was:" + sUrl, sUrl.contains("SLD_VERSION=3.3.3"));
        assertTrue("was:" + sUrl, sUrl.contains("TIME=20-20-20T20%3A20%3A20Z"));
        assertTrue("was:" + sUrl, sUrl.contains("ELEVATION=500"));
        assertTrue("was:" + sUrl, sUrl.contains("DIMRANGE=-50%2C50"));
    }
    /**
     * Ensures the {@link GetLegend111#getURL()} method returns a well-built url,
     * with the parameters given.
     * Test the precedence of SLD parameter on STYLE.
     */
    @Test
    public void testGetLegendGraphic111SLDPrecedence() {
        final GetLegend111 legend = new GetLegend111("http://test.com",null);
        legend.setDimension(new Dimension(140, 20));
        legend.setLayer("test");
        legend.setStyle("test");
        legend.setSld("http://test.com/text/xml");

        final URL url;
        try {
            url = legend.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertFalse(sUrl.contains("STYLE=test"));
        assertTrue("was:" + sUrl, sUrl.contains("SLD=http%3A%2F%2Ftest.com%2Ftext%2Fxml"));
    }

    /**
     * Ensures the {@link GetLegend130#getURL()} method returns a well-built url,
     * with the parameters given.
     * Test the precedence of SLD parameter on STYLE.
     */
    @Test
    public void testGetLegendGraphic130SLDPrecedence() {
        final GetLegend130 legend = new GetLegend130("http://test.com",null);
        legend.setDimension(new Dimension(140, 20));
        legend.setLayer("test");
        legend.setStyle("test");
        legend.setSld("http://test.com/text/xml");

        final URL url;
        try {
            url = legend.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertFalse(sUrl.contains("STYLE=test"));
        assertTrue("was:" + sUrl, sUrl.contains("SLD=http%3A%2F%2Ftest.com%2Ftext%2Fxml"));
    }

    /**
     * Ensures the {@link GetLegend111#getURL()} method returns a well-built url,
     * with the parameters given.
     * Test the precedence of SLD_BODY parameter on STYLE and SLD.
     */
    @Test
    public void testGetLegendGraphic111SLDBODYPrecedence() {
        final GetLegend111 legend = new GetLegend111("http://test.com",null);
        legend.setDimension(new Dimension(140, 20));
        legend.setLayer("test");
        legend.setStyle("test");
        legend.setSld("http://test.com/text/xml");
        legend.setSldBody("<xml>test</xml>");

        final URL url;
        try {
            url = legend.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertFalse(sUrl.contains("STYLE=test"));
        assertFalse(sUrl.contains("SLD=http://test.com/text/xml"));
        assertTrue("was:" + sUrl, sUrl.contains("SLD_BODY=%3Cxml%3Etest%3C%2Fxml%3E"));
    }

    /**
     * Ensures the {@link GetLegend130#getURL()} method returns a well-built url,
     * with the parameters given.
     * Test the precedence of SLD_BODY parameter on STYLE and SLD
     */
    @Test
    public void testGetLegendGraphic130SLDBODYPrecedence() {
        final GetLegend130 legend = new GetLegend130("http://test.com",null);
        legend.setDimension(new Dimension(140, 20));
        legend.setLayer("test");
        legend.setStyle("test");
        legend.setSld("http://test.com/text/xml");
        legend.setSldBody("<xml>test</xml>");

        final URL url;
        try {
            url = legend.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertFalse(sUrl.contains("STYLE=test"));
        assertFalse(sUrl.contains("SLD=http://test.com/text/xml"));
        assertTrue("was:" + sUrl, sUrl.contains("SLD_BODY=%3Cxml%3Etest%3C%2Fxml%3E"));
    }


}
