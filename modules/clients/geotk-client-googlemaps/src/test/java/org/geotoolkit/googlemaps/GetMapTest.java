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
package org.geotoolkit.googlemaps;

import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import org.geotoolkit.geometry.GeneralDirectPosition;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.junit.Test;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import static org.junit.Assert.*;


/**
 * Testing class for GetMap requests.
 *
 * @author Johann Sorel (Geomatys)
 */
public class GetMapTest {
    
    public GetMapTest() {}
    
    /**
     * Ensures the generate URL contain all parameters.
     */
    @Test
    public void testRequestStructure() throws NoSuchAuthorityCodeException, FactoryException, TransformException {
        
        final StaticGoogleMapsServer gmserver = new StaticGoogleMapsServer();        
        final GetMapRequest request = gmserver.createGetMap();
        
        final GeneralDirectPosition pos = new GeneralDirectPosition(DefaultGeographicCRS.WGS84);
        pos.setOrdinate(0, 45);
        pos.setOrdinate(1, 31);
        
        request.setZoom(5);
        request.setMapType(GetMapRequest.TYPE_HYBRID);
        request.setFormat(GetMapRequest.FORMAT_GIF);
        request.setDimension(new Dimension(500, 400));
        request.setCenter(pos);
        
        final URL url;
        try {
            url = request.getURL();
        } catch (MalformedURLException ex) {
            fail(ex.getLocalizedMessage());
            return;
        }
        final String sUrl = url.toString();
        System.out.println(sUrl);
        assertTrue(sUrl.startsWith("http://maps.google.com/maps/api/staticmap?"));
        assertTrue(sUrl.contains("zoom=5"));
        assertTrue(sUrl.contains("maptype=hybrid"));
        assertTrue(sUrl.contains("format=gif"));
        assertTrue(sUrl.contains("size=500x400"));
        assertTrue(sUrl.contains("center=31.0,45.0")); //lat,lon order
        assertTrue(sUrl.contains("sensor=false"));
    }

}
