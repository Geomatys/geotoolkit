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
import org.geotoolkit.wms.v111.GetMap111;
import org.geotoolkit.wms.v130.GetMap130;
import org.junit.Test;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.junit.Assert.*;


/**
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetMapTest {
    public GetMapTest() {}

    @Test
    public void testGetMap111() throws NoSuchAuthorityCodeException, FactoryException {
        final CoordinateReferenceSystem crs = CRS.decode("CRS:84");
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final GetMap111 map111 = new GetMap111("http://test.com");
        map111.setDimension(new Dimension(800, 600));
        map111.setFormat("image/png");
        map111.setLayers("test");
        map111.setStyles("");
        map111.setEnvelope(env);
        final URL url;
        try {
            url = map111.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue(sUrl.contains("SRS=CRS:84"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=800"));
        assertTrue(sUrl.contains("HEIGHT=600"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
    }

    @Test
    public void testGetMap130() throws NoSuchAuthorityCodeException, FactoryException {
        final CoordinateReferenceSystem crs = CRS.decode("CRS:84");
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final GetMap130 map130 = new GetMap130("http://test.com");
        map130.setDimension(new Dimension(800, 600));
        map130.setFormat("image/png");
        map130.setLayers("test");
        map130.setStyles("");
        map130.setEnvelope(env);
        final URL url;
        try {
            url = map130.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("BBOX=-180.0%2C-90.0%2C180.0%2C90.0"));
        assertTrue(sUrl.contains("CRS=CRS:84"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("WIDTH=800"));
        assertTrue(sUrl.contains("HEIGHT=600"));
        assertTrue(sUrl.contains("LAYERS=test"));
        assertTrue(sUrl.contains("STYLES="));
    }
}
