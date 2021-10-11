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
package org.geotoolkit.wmts;

import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.wmts.v100.GetTile100;
import static org.junit.Assert.*;
import org.junit.Test;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.util.FactoryException;


/**
 * Testing class for GetTile requests, in version 1.0.0
 *
 * @author Guilhem Legal (Geomatys)
 */
public class GetTileTest extends org.geotoolkit.test.TestBase {
    public GetTileTest() {}

    /**
     * Ensures the {@link GetMap111#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetTile100() throws NoSuchAuthorityCodeException, FactoryException {
        final GetTile100 map111 = new GetTile100("http://test.com",null, 100);
        map111.setFormat("image/png");
        map111.setLayer("test");
        map111.setStyle("");
        map111.setTileMatrixSet("test");
        map111.setTileMatrix("L1");
        map111.setTileCol(8);
        map111.setTileRow(4);
        map111.dimensions().put("ELEVATION", "11");
        final URL url;
        try {
            url = map111.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("FORMAT=image%2Fpng"));
        assertTrue(sUrl.contains("TILEMATRIX=L1"));
        assertTrue(sUrl.contains("TILEMATRIXSET=test"));
        assertTrue(sUrl.contains("LAYER=test"));
        assertTrue(sUrl.contains("STYLE="));
        assertTrue(sUrl.contains("TILECOL=8"));
        assertTrue(sUrl.contains("TILEROW=4"));
        assertTrue(sUrl.contains("ELEVATION=11"));
    }

}
