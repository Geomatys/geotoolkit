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
import javax.xml.bind.JAXBException;

import org.geotoolkit.geometry.GeneralEnvelope;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.wms.map.WMSMapLayer;
import org.geotoolkit.wms.xml.WMSVersion;

import org.junit.Test;

import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;

import static org.junit.Assert.*;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class WMSMapLayerTest {

    private final WebMapServer SERVER_111;
    private final WebMapServer SERVER_130;

    public WMSMapLayerTest() throws MalformedURLException, JAXBException {
        SERVER_111 = new MockWebMapServer(WMSVersion.v111);
        SERVER_130 = new MockWebMapServer(WMSVersion.v130);
    }

    /**
     * This test checks that in the case we use CRS:84 in WMS 1.1.1
     * the URL CRS will be changed by EPSG:4326 but with no coordinate change.
     * since EPSG:4326 in 1.1.1 is actually a CRS:84 .
     */
    @Test
    public void test_v110_GetMap_CRS84() throws TransformException, MalformedURLException, FactoryException{
        final WMSMapLayer layer = new WMSMapLayer(SERVER_111, "BlueMarble");

        final GeneralEnvelope env = new GeneralEnvelope(DefaultGeographicCRS.WGS84);
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue( query.substring(query.indexOf("SRS")).startsWith("SRS=EPSG:4326"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0,-90.0,180.0,90.0"));
    }

    /**
     * This test checks that in the case we use EPSG:4326 in WMS 1.1.1
     * the URL BBOX Coordinate must be inverted .
     */
    @Test
    public void test_v110_GetMap_EPSG4326() throws MalformedURLException, TransformException, FactoryException {

        final WMSMapLayer layer = new WMSMapLayer(SERVER_111, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue( query.substring(query.indexOf("SRS")).startsWith("SRS=EPSG:4326"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0,-90.0,180.0,90.0"));
    }
    
    /**
     * This test checks that in the case we use CRS:84 in WMS 1.3.0
     * the URL BBOX Coordinate are not changed.
     */
    @Test
    public void test_v130_GetMap_CRS84() throws MalformedURLException, TransformException, FactoryException {

        final WMSMapLayer layer = new WMSMapLayer(SERVER_130, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("CRS:84"));
        env.setRange(0, -180, 180);
        env.setRange(1, -90, 90);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue( query.substring(query.indexOf("CRS")).startsWith("CRS=CRS:84"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-180.0,-90.0,180.0,90.0"));
    }
    
    /**
     * This test checks that in the case we use EPSG:4326 in WMS 1.3.0
     * the URL BBOX Coordinate are not changed.
     */
    @Test
    public void test_v130_GetMap_EPSG4326() throws MalformedURLException, TransformException, FactoryException {

        final WMSMapLayer layer = new WMSMapLayer(SERVER_130, "BlueMarble");


        final GeneralEnvelope env = new GeneralEnvelope(CRS.decode("EPSG:4326"));
        env.setRange(0, -90, 90);
        env.setRange(1, -180, 180);

        final String query = layer.query(env, new Dimension(800, 600)).toString();
        assertTrue( query.substring(query.indexOf("CRS")).startsWith("CRS=EPSG:4326"));
        assertTrue( query.substring(query.indexOf("BBOX")).startsWith("BBOX=-90.0,-180.0,90.0,180.0"));
    }
    
}
