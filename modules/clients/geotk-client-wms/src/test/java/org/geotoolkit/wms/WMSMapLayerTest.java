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
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.wms.map.WMSMapLayer;
import org.geotoolkit.wms.xml.WMSVersion;
import org.junit.Test;
import static org.junit.Assert.*;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSMapLayerTest {

    /**
     * This test checks that in the case we use CRS:84 in WMS 1.1.1
     * the URL CRS will be changed by EPSG:4326 but with no coordinate change.
     * since EPSG:4326 in 1.1.1 is actually a CRS:84 .
     */
    @Test
    public void testCRS84WithWMS110() throws MalformedURLException, TransformException, FactoryException {

        final WebMapServer server = new WebMapServer(new URL("http://localhost/constellation/WS/wms?"), WMSVersion.v111);
        final WMSMapLayer layer = new WMSMapLayer(server, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        String query = layer.query(env, new Dimension(800, 600)).toString();

        assertTrue( query.substring(query.indexOf("SRS")).startsWith("SRS=EPSG:4326"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0,-90.0,180.0,90.0"));
        //http://localhost/constellation/WS/wms
        //?TRANSPARENT=TRUE
        //&BBOX=-180.0,-90.0,180.0,90.0
        //&VERSION=1.1.1
        //&FORMAT=image/png
        //&SERVICE=WMS
        //&HEIGHT=600
        //&LAYERS=BlueMarble
        //&REQUEST=GetMap
        //&STYLES=
        //&SRS=EPSG:4326
        //&WIDTH=800


    }

    /**
     * This test checks that in the case we use EPSG:4326 in WMS 1.1.1
     * the URL BBOX Coordinate must be inverted .
     */
    @Test
    public void testEPSG4326WithWMS110() throws MalformedURLException, TransformException, FactoryException {

        final WebMapServer server = new WebMapServer(new URL("http://localhost/constellation/WS/wms?"), WMSVersion.v111);
        final WMSMapLayer layer = new WMSMapLayer(server, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);

        String query = layer.query(env, new Dimension(800, 600)).toString();
        System.out.println(query);

        assertTrue( query.substring(query.indexOf("SRS")).startsWith("SRS=EPSG:4326"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0,-90.0,180.0,90.0"));
        //http://localhost/constellation/WS/wms
        //?TRANSPARENT=TRUE
        //&BBOX=-180.0,-90.0,180.0,90.0
        //&VERSION=1.1.1
        //&FORMAT=image/png
        //&SERVICE=WMS
        //&HEIGHT=600
        //&LAYERS=BlueMarble
        //&REQUEST=GetMap
        //&STYLES=
        //&SRS=EPSG:4326
        //&WIDTH=800

    }
    
}
