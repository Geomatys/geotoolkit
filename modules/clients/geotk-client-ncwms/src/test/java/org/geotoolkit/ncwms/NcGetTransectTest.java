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

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Testing class for ncWMS GetTransect requests.
 *
 * @author Olivier Terral (Geomatys)
 */
public class NcGetTransectTest {
    public NcGetTransectTest() {}

    /**
     * Ensures the {@link NcGetTransect#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testNcGetTransect() {
        final NcGetTransect request = new NcGetTransect("http://test.com");
        
        // Mandatory
        request.setLayer("test");
        request.setCrs("CRS:84");
        request.setLineString("24%25,26%27");       
        request.setFormat("image/png");
        
        // Optional
        request.setTime("01-01-01T01:00:00Z");
        request.setElevation("500");
        
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        
        final String sUrl = url.toString();
        assertTrue(sUrl.contains("REQUEST=GetTransect"));
        assertTrue(sUrl.contains("LAYER=test"));
        assertTrue(sUrl.contains("CRS=CRS:84"));
        assertTrue(sUrl.contains("LINESTRING=24%25,26%27"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("TIME=01-01-01T01:00:00Z"));
        assertTrue(sUrl.contains("ELEVATION=500"));
    }

}
