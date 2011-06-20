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

import org.geotoolkit.wmts.v100.GetFeatureInfo100;
import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.junit.Test;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import static org.junit.Assert.*;


/**
 * Testing class for GetFeatureInfo requests, in version 1.1.1 and 1.3.0.
 *
 * @author Cédric Briançon (Geomatys)
 */
public class GetFeatureInfoTest {
    public GetFeatureInfoTest() {}

    /**
     * Ensures the {@link GetFeatureInfo111#getURL()} method returns a well-built url,
     * with the parameters given.
     *
     * @throws NoSuchAuthorityCodeException
     * @throws FactoryException
     */
    @Test
    public void testGetFeatureInfo100() throws NoSuchAuthorityCodeException, FactoryException {
        final CoordinateReferenceSystem crs = CRS.decode("CRS:84");
        final GeneralEnvelope env = new GeneralEnvelope(crs);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);
        final GetFeatureInfo100 featureInfo111 = new GetFeatureInfo100("http://test.com",null);
        featureInfo111.setFormat("image/png");
        featureInfo111.setLayer("test");
        featureInfo111.setStyle("");
        featureInfo111.setTileMatrixSet("test");
        featureInfo111.setTileMatrix("L1");
        featureInfo111.setTileCol(8);
        featureInfo111.setTileRow(4);

        featureInfo111.setInfoFormat("gml");
        
        featureInfo111.setColumnIndex(50);
        featureInfo111.setRawIndex(40);
        final URL url;
        try {
            url = featureInfo111.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        assertTrue(sUrl.startsWith("http://test.com?"));
        assertTrue(sUrl.contains("FORMAT=image/png"));
        assertTrue(sUrl.contains("TILEMATRIX=L1"));
        assertTrue(sUrl.contains("TILEMATRIXSET=test"));
        assertTrue(sUrl.contains("LAYER=test"));
        assertTrue(sUrl.contains("STYLE="));
        assertTrue(sUrl.contains("TILECOL=8"));
        assertTrue(sUrl.contains("TILEROW=4"));
        assertTrue(sUrl.contains("INFO_FORMAT=gml"));
        assertTrue(sUrl.contains("I=50"));
        assertTrue(sUrl.contains("J=40"));
    }

   
}
