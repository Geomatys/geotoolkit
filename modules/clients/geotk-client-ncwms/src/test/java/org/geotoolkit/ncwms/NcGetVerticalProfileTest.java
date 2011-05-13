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
 * Testing class for ncWMS GetVerticalProfile requests.
 *
 * @author Olivier Terral (Geomatys)
 */
public class NcGetVerticalProfileTest {
    public NcGetVerticalProfileTest() {}

    /**
     * Ensures the {@link NcGetVerticalProfile#getURL()} method returns a well-built url,
     * with the parameters given.
     */
    @Test
    public void testNcGetVerticalProfile() {
        final NcGetVerticalProfile request = new NcGetVerticalProfile("http://test.com");
        
        // Mandatory
        request.setLayer("test");
        request.setCrs("CRS:84");
        request.setPoint("24%25");        
        request.setFormat("image/png");
        
        // Optional
        request.setTime("01-01-01T01:00:00Z");
        
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        
        final String sUrl = url.toString();
        assertTrue(sUrl.contains("REQUEST=GetVerticalProfile"));
        assertTrue(sUrl.contains("LAYER=test"));
        assertTrue(sUrl.contains("CRS=CRS:84"));
        assertTrue(sUrl.contains("TIME=01-01-01T01:00:00Z"));
        assertTrue(sUrl.contains("POINT=24%25"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
    }

}
